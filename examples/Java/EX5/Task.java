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
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//  Task.java
//
// Description
//  Each instance is a task to be run to completion.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/02/19 19:52:47  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public final class Task
    implements TaskEventListener
{
// Member Methods

    public Task(String name, int priority, int time)
    {
        TaskController control = new TaskController();

        _name = name;
        _priority = priority;
        _runtime = time * 1000;
        _timeLeft = _runtime;
        _runStartTime = null;
        _timerTable = new HashMap();

        _fsm = new TaskContext(this);

        // Uncomment to see debug output.
        // _fsm.setDebugFlag(true);

        // Register with the task controller.
        control.register(name, this);

        // Since the task starts suspended, timestamp the suspend
        // start time.
        _suspendStartTime = new Date();

        // Have this task placed on the task display.
        Map args = new HashMap();
        args.put(new String("name"), name);
        args.put(new String("status"), new String("Suspended"));
        args.put(new String("priority"), new Integer(priority));
        args.put(new String("runtime"), new Integer(time));

        control.postMessage("Task GUI",
                            "Task Created",
                            args);
    }

    public String getName()
    {
        return(_name);
    }

    // Return this task's *static* priority.
    public int getPriority()
    {
        return(_priority);
    }

    // Return the task's total run time in milliseconds.
    public int getTime()
    {
        return(_runtime);
    }

    // Return task's remaining run time.
    public int getTimeLeft()
    {
        return(_timeLeft);
    }

    public int getDynamicPriority()
    {
        int suspendTime;
        int retval;

        suspendTime = getSuspendTime();
        retval = _priority - (suspendTime / 1000);

        return(retval);
    }

    // Return how long this task has been suspended in
    // milliseconds.
    public int getSuspendTime()
    {
        Date currTime = new Date();
        long retval;

        retval = currTime.getTime() - _suspendStartTime.getTime();
        return((int) retval);
    }

    public int getPercentComplete()
    {
        int retval;

        retval = (int) ((((float) (_runtime - _timeLeft)) /
                         ((float) _runtime)) * 100.0);
        return(retval);
    }

    public void handleEvent(String eventName, Map args)
    {
        if (eventName.compareTo("start") == 0)
        {
            _fsm.Start();
        }
        else if (eventName.compareTo("suspend") == 0)
        {
            _fsm.Suspend();
        }
        else if (eventName.compareTo("block") == 0)
        {
            _fsm.Block();
        }
        else if (eventName.compareTo("unblock") == 0)
        {
            _fsm.Unblock();
        }
        else if (eventName.compareTo("stop") == 0)
        {
            _fsm.Stop();
        }
        else if (eventName.compareTo("delete") == 0)
        {
            _fsm.Delete();
        }
        else if (eventName.compareTo("Done") == 0)
        {
            _fsm.Done();
        }
        else if (eventName.compareTo("Update Time") == 0)
        {
            timeUpdate();
        }
        else if (eventName.compareTo("Stopped") == 0)
        {
            _fsm.Stopped();
        }

        return;
    }

    //===========================================================
    // State Machine Actions.
    //

    // Create a timer for the specified period. When the timer
    // expires, issue the associated state machine transition.
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
                          new TaskTimerListener(name, this));
        timer.setRepeats(false);
        _timerTable.put(name, timer);

        // Start the timer running.
        timer.start();

        return;
    }

    // Stop the specified timer if it is running.
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
        Map args = new HashMap();

        args.put(new String("level"), new Integer(level));
        args.put(new String("object"), _name);
        args.put(new String("message"), message);
        control.postMessage("Message GUI",
                            "Post Message",
                            args);

        return;
    }

    // Tell the task manager about this tasks's change in status.
    public void stateUpdate(String change)
    {
        TaskController controller = new TaskController();
        Map args = new HashMap();

        args.put(new String("name"), _name);
        args.put(new String("status"), change);
        controller.postMessage("Task GUI",
                               "Task State Update",
                               args);
        return;
    }

    // Update the task's percent complete display.
    public void timeUpdate()
    {
        TaskController control = new TaskController();
        int timeLeft;
        int percentComplete;
        Date currTime = new Date();
        Map args = new HashMap();

        timeLeft =
                _timeLeft -
                    ((int) (currTime.getTime() -
                           _runStartTime.getTime()));

        if (timeLeft < 0)
        {
            timeLeft = 0;
        }

        percentComplete = 
                (int) (((((float) (_runtime - timeLeft)) /
                         ((float) _runtime))) * 100.0);

        args.put(new String("name"), _name);
        args.put(new String("percentComplete"),
                 new Integer(percentComplete));
        control.postMessage("Task GUI",
                            "Task % Update",
                            args);

        if (timeLeft > 1000)
        {
            setTimer("Update Time", 1000);
        }
    }

    public void setRunTimer()
    {
        if (_timeLeft > 0)
        {
            setTimer("Done", _timeLeft);
        }
        else
        {
            setTimer("Done", 0);
        }

        return;
    }

    public void setStartTime()
    {
        if (_timeLeft > 1000)
        {
            setTimer("Update Time", 1000);
        }

        _runStartTime = new Date();

        return;
    }

    public void setStopTime()
    {
        TaskController control = new TaskController();
        Date currTime = new Date();
        Map args = new HashMap();
        int percentComplete;

        _timeLeft =
                _timeLeft -
                ((int) (currTime.getTime() - _runStartTime.getTime()));
        if (_timeLeft < 0)
        {
            _timeLeft = 0;
        }

        percentComplete = getPercentComplete();

        args.put(new String("name"), _name);
        args.put(new String("percentComplete"),
                 new Integer(percentComplete));
        control.postMessage("Task GUI",
                            "Task % Update",
                            args);

        stopTimer("Update Time");
        return;
    }

    public void setSuspendTime()
    {
        _suspendStartTime = new Date();
        return;
    }

    public void updateTaskMan(String state)
    {
        TaskController control = new TaskController();
        Map args = new HashMap();

        args.put(new String("Task Name"), _name);
        control.postMessage("Task Manager",
                            state,
                            args);

        return;
    }

    // Deregister from the task controller.
    public void deregister()
    {
        TaskController control = new TaskController();
        control.deregister(_name);
        return;
    }

// Member Data

    private TaskContext _fsm;

    // The task's human readable name.
    private String _name;

    // The task's fixed priority.
    private int _priority;

    // The tasks's total run time in milliseconds.
    private int _runtime;

    // How much run time the task has left.
    private int _timeLeft;

    // When the task is told to run, remember at what time it
    // started. Then when the task stops running, it can figure
    // out for how long it was running and how much longer it
    // needs to run.
    private Date _runStartTime;

    // When the task is suspended, remember at what time the
    // suspension started. This is necessary in order to
    // calculate the dynamic priority.
    private Date _suspendStartTime;

    // Put internal timers here.
    private Map _timerTable;

// Inner Classes

    private final class TaskTimerListener
        implements ActionListener
    {
        private TaskTimerListener(String name, Task owner)
        {
            super();

            _name = name;
            _owner = owner;
        }

        public void actionPerformed(ActionEvent e)
        {
            Map args = new HashMap();

            _owner.handleEvent(_name, args);
            return;
        }

        private String _name;
        private Task _owner;
    }
}
