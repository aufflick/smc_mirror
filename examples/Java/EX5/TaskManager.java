//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Map Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//  TaskManager.java
//
// Description
//  This singleton is responsible for scheduling the running
//  task.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.2  2001/05/09 23:40:02  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
//
// Revision 1.1.1.2  2001/03/26 14:41:47  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.1.1.1  2000/08/02 12:51:03  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

public final class TaskManager
    implements TaskEventListener
{
// Member Methods.

    public TaskManager()
    {
        TaskController control = new TaskController();

        _runningTask = null;
        _runnableTaskQueue = new LinkedList();
        _blockedTaskList = new LinkedList();
        _timerTable = new HashMap();
        _exitCode = 0;

        _statemap = new TaskManagerContext(this);

        // Uncomment to see debug output.
        // _statemap.setDebugFlag(true);

        // Register with the controller.
        control.register("Task Manager", this);
    }

    //===========================================================
    // These methods respond to viewer messages.
    //

    // Create a new task.
    public void createTask(String name, int time, int priority)
    {
        if (name == null || name.length() == 0)
        {
            sendMessage(0, "Cannot create task without a name.");
        }
        else if (taskExists(name) == true)
        {
            sendMessage(0,
                        "Cannot create task named \"" +
                        name +
                        "\" - a task with that name already exists.");
        }
        else
        {
            Task newTask = new Task(name, priority, time);
            _runnableTaskQueue.add(newTask);
            sendMessage(1,
                        "Created task " +
                        name +
                        "(priority: " +
                        Integer.toString(priority) +
                        ", time: " +
                        Integer.toString(time) +
                        ").");

            _statemap.TaskCreated();
        }

        return;
    }

    // Suspend the currently running task - if there is one.
    public void suspendTask()
    {
        if (_runningTask != null)
        {
            TaskController control = new TaskController();

            sendMessage(2,
                        "Suspending task " +
                        _runningTask.getName() +
                        ".");

            // Tell the task to suspend.
            control.postMessage(_runningTask.getName(),
                                "suspend");

            // Put the task back on to the runnable queue.
            _runnableTaskQueue.add(_runningTask);
            _runningTask = null;
        }

        return;
    }

    // Block the specified task. If that task is running,
    // then remove it.
    public void blockTask(String taskName)
    {
        Task task;

        if ((task = findTask(taskName)) != null)
        {
            TaskController control = new TaskController();

            sendMessage(2, "Task " + taskName + " is blocked.");

            // Tell the task to block.
            control.postMessage(taskName,
                                "block");

            if (task == _runningTask)
            {
                _runningTask = null;
            }
            else
            {
                // Remove the task from the runnable queue.
                _runnableTaskQueue.remove(task);
            }

            _blockedTaskList.add(task);
        }

        return;
    }

    public void unblockTask(String taskName)
    {
        Task task;
        int taskIndex;

        // Is there a task with this name?
        if ((task = findTask(taskName)) != null)
        {
            // Is this task on the blocked list?
            if ((taskIndex = _blockedTaskList.indexOf(task)) >= 0)
            {
                TaskController control = new TaskController();

                sendMessage(2,
                            "Task " +
                            taskName +
                            " is unblocked.");

                // Tell the task it is now unblocked.
                control.postMessage(task.getName(),
                                    "unblock");

                // Move the task from the blocked queue to the
                // runnable queue.
                _blockedTaskList.remove(taskIndex);
                _runnableTaskQueue.add(task);

                _statemap.TaskUnblocked();
            }
        }

        return;
    }

    public void deleteTask(String taskName)
    {
        Task task;

        if ((task = findTask(taskName)) != null)
        {
            TaskController control = new TaskController();

            // Tell the task to go and die.
            control.postMessage(taskName, "delete");
        }

        return;
    }

    // Shutting down the application.
    public void shutdown()
    {
        _statemap.Shutdown();
        return;
    }

    //===========================================================
    // These methods handle task object messages.
    //

    // The running task has completed its work.
    public void taskDone(String taskName)
    {
        Task task;
        int taskIndex;

        if ((task = findTask(taskName)) != null)
        {
            sendMessage(1,
                        "Task " + taskName + " has completed.");

            // Is this the running task?
            if (task == _runningTask)
            {
                _runningTask = null;
                _statemap.TaskDone();
            }
            else if ((taskIndex =
                      _runnableTaskQueue.indexOf(task)) >= 0)
            {
                // I don't know how a suspended task managed to
                // complete. Remove it from a runnable list.
                _runnableTaskQueue.remove(taskIndex);
            }
            else if ((taskIndex =
                      _blockedTaskList.indexOf(task)) >= 0)
            {
                // I don't know how a blocked task managed to
                // complete. Remove it from the blocked list.
                _blockedTaskList.remove(taskIndex);
            }
        }

        return;
    }

    // A task has stopped and is ready for deletion.
    public void taskStopped(String taskName)
    {
        Task task;
        int taskIndex;

        if ((task = findTask(taskName)) != null &&
            (taskIndex = _blockedTaskList.indexOf(task)) >= 0)
        {
            sendMessage(1, "Task " + taskName + " is stopped.");
            _blockedTaskList.remove(taskIndex);
            _statemap.TaskStopped();
        }
        else
        {
            sendMessage(4,
                        "TaskManager::taskStopped: " +
                        taskName +
                        " not on blocked list.");
        }

        return;
    }

    // A task has stopped and is ready for deletion.
    public void taskDeleted(String taskName)
    {
        Task task;
        int taskIndex;

        if ((task = findTask(taskName)) != null)
        {
            sendMessage(1, "Task " + taskName + " deleted.");

            if (task == _runningTask)
            {
                _runningTask = null;
                _statemap.TaskDeleted();
            }
            else if ((taskIndex = _runnableTaskQueue.indexOf(task))
                         >= 0)
            {
                _runnableTaskQueue.remove(taskIndex);
            }
            else if ((taskIndex = _blockedTaskList.indexOf(task))
                         >= 0)
            {
                _blockedTaskList.remove(taskIndex);
            }
        }

        return;
    }

    //===========================================================
    // State map actions.
    //

    // Create a timer for the specified period. When the timer
    // expires, issue the corresponding state map transition.
    public void setTimer(String name, int period)
    {
        Timer timer;

        // Is there a timer with this name already?
        if (_timerTable.containsKey(name) == true)
        {
            // Yes, there is. Stop the current timer and then
            // start it again.
            stopTimer(name);
        }

        timer = new Timer(period,
                          new TimerListener(name, this));
        timer.setRepeats(false);
        _timerTable.put(name, timer);

        // Start the timer running.
        timer.start();

        return;
    }

    // Stop the named timer if it is running.
    public void stopTimer(String name)
    {
        Timer timer;

        // Remove the timer from the table and stop it.
        if ((timer = (Timer) _timerTable.remove(name)) != null)
        {
            timer.stop();
        }

        return;
    }

    // Send a message to the GUI controller so it can be posted
    // on the message display.
    public void sendMessage(int level, String message)
    {
        TaskController control = new TaskController();
        HashMap args = new HashMap();

        args.put("level", new Integer(level));
        args.put("object", "TaskManager");
        args.put("message", message);
        control.postMessage("Message GUI",
                            "Post Message",
                            args);

        return;
    }

    // Check if there are any tasks to run. If yes, then
    // asynchronously issue a RunTask transition using the
    // setTimer() method.
    public void checkTaskQueue()
    {
        if (_runnableTaskQueue.size() > 0)
        {
            // Create a timer which will expire immediately.
            setTimer("Run Task", 0);
        }

        return;
    }

    // Check if there are any tasks to run.
    public boolean areTasksQueued()
    {
        return (_runnableTaskQueue.size() > 0);
    }

    // Task the highest priority task off the runnable queue
    // and have it start running.
    public void startTask()
    {
        ListIterator taskIt;
        Task task;
        int index;
        int taskIndex;
        int taskPriority;
        int currentMinPriority;

        // Find the task with the lowest priority.
        for (taskIt = _runnableTaskQueue.listIterator(0),
                     currentMinPriority = Integer.MAX_VALUE,
                     index = 0,
                     taskIndex = -1;
             taskIt.hasNext() == true;
             ++index)
        {
            task = (Task) taskIt.next();
            taskPriority = task.getDynamicPriority();

            // Is the new task's priority less than
            // the current task. 
            if (taskPriority < currentMinPriority)
            {
                taskIndex = index;
                currentMinPriority = taskPriority;
            }
        }

        // Was a task found?
        if (taskIndex >= 0)
        {
            TaskController control = new TaskController();

            _runningTask =
                    (Task) _runnableTaskQueue.remove(taskIndex);
             sendMessage(2,
                         "Attempting to run task " +
                         _runningTask.getName() +
                         ".");

             control.postMessage(_runningTask.getName(),
                                 "start");
        }

        return;
    }

    // Cancel all existing timers.
    public void stopAllTimers()
    {
        Iterator entryIt;
        Map.Entry mapEntry;
        Timer timer;

        for (entryIt = _timerTable.entrySet().iterator();
             entryIt.hasNext() == true;
             )
        {
            mapEntry = (Map.Entry) entryIt.next();
            timer = (Timer) mapEntry.getValue();
            timer.stop();
        }

        _timerTable.clear();

        return;
    }

    public void stopAllTasks()
    {
        TaskController control = new TaskController();
        ListIterator listIt;
        Task task;

        // Put all tasks into the blocked list. As they report
        // that they are stopped, remove the tasks.
        //
        // Do the blocked list first.
        for (listIt = _blockedTaskList.listIterator(0);
             listIt.hasNext() == true;
             )
        {
            task = (Task) listIt.next();

            sendMessage(3,
                        "Stopping task " +
                        task.getName() +
                        ".");
            control.postMessage(task.getName(),
                                "stop");
        }

        // Do the runnable tasks next.
        for (listIt = _runnableTaskQueue.listIterator(0);
             listIt.hasNext() == true;
             )
        {
            task = (Task) listIt.next();

            sendMessage(3,
                        "Stopping task " +
                        task.getName() +
                        ".");
            control.postMessage(task.getName(),
                                "stop");
            _blockedTaskList.add(task);
        }
        _runnableTaskQueue.clear();

        // Do the running task last.
        if (_runningTask != null)
        {
            sendMessage(3,
                        "Stopping task " +
                        _runningTask.getName() +
                        ".");
            control.postMessage(_runningTask.getName(),
                                "stop");
            _blockedTaskList.add(_runningTask);
            _runningTask = null;
        }

        return;
    }

    // Check if all tasks has stopped.
    public boolean allTasksStopped()
    {
        boolean retval;

        if (_runningTask == null &&
            _runnableTaskQueue.size() == 0 &&
            _blockedTaskList.size() == 0)
        {
            retval = true;
        }
        else
        {
            retval = false;
        }

        return(retval);
    }

    public void deleteRunningTask()
    {
        if (_runningTask != null)
        {
            _runningTask = null;
        }

        return;
    }

    // Forcibly delete all existing tasks with extreme prejudice.
    public void deleteAllTasks()
    {
        _runningTask = null;
        _runnableTaskQueue.clear();
        _blockedTaskList.clear();

        return;
    }

    public void exitApplication()
    {
        // Wait another 1.5 secs before actually dying.
        setTimer("Exit", 1500);
        return;
    }

    public Task findTask(String taskName)
    {
        ListIterator taskIt;
        Task task;
        Task retval;

        retval = null;

        // Is the running task the one we are looking for?
        if (_runningTask != null &&
            taskName.compareTo(_runningTask.getName()) == 0)
        {
            retval = _runningTask;
        }
        else
        {
            // Is the task in the runnable queue?
            for (taskIt = _runnableTaskQueue.listIterator(0);
                 taskIt.hasNext() == true && retval == null;
                )
            {
                task = (Task) taskIt.next();
                if (taskName.compareTo(task.getName()) == 0)
                {
                    retval = task;
                }
            }

            // Is this task in the blocked list?
            if (retval == null)
            {
                for (taskIt = _blockedTaskList.listIterator(0);
                     taskIt.hasNext() == true && retval == null;
                    )
                {
                    task = (Task) taskIt.next();
                    if (taskName.compareTo(task.getName()) == 0)
                    {
                        retval = task;
                    }
                }
            }
        }

        return(retval);
    }

    // Does a task already exist with this name?
    public boolean taskExists(String name)
    {
        return(findTask(name) == null ? false : true);
    }

    // Issue the state map transition associated with this timer
    // name. Also, remove the now defunct timer from the timer
    // table.
    public void handleEvent(String eventName, HashMap args)
    {
        String taskName;

        if (eventName.compareTo("Create Task") == 0)
        {
            Integer runtime;
            Integer priority;

            taskName = (String) args.get("Task Name");
            runtime = (Integer) args.get("Runtime");
            priority = (Integer) args.get("Priority");
            createTask(taskName,
                       runtime.intValue(),
                       priority.intValue());
        }
        else if (eventName.compareTo("Run Task") == 0)
        {
            _statemap.RunTask();
        }
        else if (eventName.compareTo("Slice Timeout") == 0)
        {
            _statemap.SliceTimeout();
        }
        else if (eventName.compareTo("Reply Timeout") == 0)
        {
            _statemap.ReplyTimeout();
        }
        else if (eventName.compareTo("Suspend Task") == 0)
        {
            suspendTask();
        }
        else if (eventName.compareTo("Block Task") == 0)
        {
            taskName = (String) args.get("Task Name");
            blockTask(taskName);
        }
        else if (eventName.compareTo("Unblock Task") == 0)
        {
            taskName = (String) args.get("Task Name");
            unblockTask(taskName);
        }
        else if (eventName.compareTo("Delete Task") == 0)
        {
            taskName = (String) args.get("Task Name");
            deleteTask(taskName);            
        }
        else if (eventName.compareTo("Task Suspended") == 0)
        {
            taskName = (String) args.get("Task Name");
            sendMessage(2,
                        "Task " +
                        taskName +
                        " has been suspended.");

            _statemap.TaskSuspended();
        }
        else if (eventName.compareTo("Task Done") == 0)
        {
            taskName = (String) args.get("Task Name");
            taskDone(taskName);
        }
        else if (eventName.compareTo("Task Stopped") == 0)
        {
            taskName = (String) args.get("Task Name");
            taskStopped(taskName);
        }
        else if (eventName.compareTo("Task Deleted") == 0)
        {
            taskName = (String) args.get("Task Name");
            taskDeleted(taskName);
        }
        else if (eventName.compareTo("Shutdown") == 0)
        {
            Integer exitCode;

            exitCode = (Integer) args.get("Exit Code");
            _exitCode = exitCode.intValue();

            _statemap.Shutdown();
        }
        else if (eventName.compareTo("Exit") == 0)
        {
            System.exit(_exitCode);
        }
        else if (eventName.compareTo("ShutdownTimeout") == 0)
        {
            _statemap.ShutdownTimeout();
        }

        return;
    }

// Member Data.

    private TaskManagerContext _statemap;

    // Runnable task queue, sorted by priority.
    private LinkedList _runnableTaskQueue;

    // Blocked task list.
    private LinkedList _blockedTaskList;

    // The currently running task.
    private Task _runningTask;

    // Task manager's various timers.
    private HashMap _timerTable;

    // The application's exit code.
    private int _exitCode;

// Inner classes.

    private final class TimerListener
        implements ActionListener
    {
        public TimerListener(String name,
                             TaskManager owner)
        {
            _timerName = name;
            _owner = owner;
        }

        public void actionPerformed(ActionEvent e)
        {
            HashMap args = new HashMap();

            _owner.handleEvent(_timerName, args);
            return;
        }

        private String _timerName;
        private TaskManager _owner;
    }
}
