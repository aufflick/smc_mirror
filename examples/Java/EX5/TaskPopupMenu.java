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
//  TaskPopupMenu.java
//
// Description
//  Clicking on a TaskTable row causes this pop-up menu to
//  appear. The menu allows the user to suspend, block, unblock
//  or delete a task.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/05/07 00:29:50  cwrapp
// Changes in release 1.3.2:
// Add the following feature:
// + 528321: Modified push transition syntax to be:
//
// 	  <transname> <state1>/push(<state2>)  {<actions>}
//
// 	  which means "transition to <state1> and then
// 	  immediately push to <state2>". The current
// 	  syntax:
//
// 	  <transname> push(<state2>)  {<actions>}
//
//           is still valid and <state1> is assumed to be "nil".
//
// No bug fixes.
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
// Revision 1.1.1.1  2000/08/02 12:51:04  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class TaskPopupMenu
    implements ActionListener
{
// Member Methods

    public TaskPopupMenu()
    {
        _taskName = null;

        _menu = new JPopupMenu();

        // Add items to the task pop-up menu.
        _suspendItem = new JMenuItem("Suspend");
        _suspendItem.addActionListener(this);
        _suspendItem.setEnabled(false);
        _menu.add(_suspendItem);

        _blockItem = new JMenuItem("Block");
        _blockItem.addActionListener(this);
        _blockItem.setEnabled(false);
        _menu.add(_blockItem);

        _unblockItem = new JMenuItem("Unblock");
        _unblockItem.addActionListener(this);
        _unblockItem.setEnabled(false);
        _menu.add(_unblockItem);

        _deleteItem = new JMenuItem("Delete");
        _deleteItem.addActionListener(this);
        _deleteItem.setEnabled(true);
        _menu.add(_deleteItem);

        _menu.setVisible(false);
    }

    public void activate(String taskName,
                         String status,
                         MouseEvent e)
    {
        _taskName = taskName;

        // Which buttons are enabled or disabled depends on the
        // task's current state.
        // NOTE: the delete item is always enabled.
        if (status.compareTo("Running") == 0)
        {
            // If the task is running, enable suspend
            // and block, disable unblock.
            _suspendItem.setEnabled(true);
            _blockItem.setEnabled(true);
            _unblockItem.setEnabled(false);
        }
        else if (status.compareTo("Suspended") == 0)
        {
            // If the task is suspended, enable block
            // and disabled suspend and unblock.
            _suspendItem.setEnabled(false);
            _blockItem.setEnabled(true);
            _unblockItem.setEnabled(false);
        }
        else if (status.compareTo("Blocked") == 0)
        {
            // If the task is suspended, enable unblock
            // and disabled suspend and block.
            _suspendItem.setEnabled(false);
            _blockItem.setEnabled(false);
            _unblockItem.setEnabled(true);
        }
        // Else if the status is Done, we shouldn't even
        // get here.

        // All is ready. Make the menu visible where the mouse
        // was clicked.
        _menu.show(e.getComponent(), e.getX(), e.getY());
        _menu.setVisible(true);

        return;
    }

    public void deactivate()
    {
        _taskName = null;
        _menu.setVisible(false);
        return;
    }

    // Generate the task message associated with the selected
    // menu item.
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem) (e.getSource());
        TaskController controller = new TaskController();
        Map args = new HashMap();

        args.put("Task Name", _taskName);
        if (source.getText().compareTo(_suspendItem.getText()) == 0)
        {
            controller.postMessage("Task Manager",
                                   "Suspend Task",
                                   args);
        }
        else if (source.getText().compareTo(_blockItem.getText()) == 0)
        {
            controller.postMessage("Task Manager",
                                   "Block Task",
                                   args);
        }
        else if (source.getText().compareTo(_unblockItem.getText()) == 0)
        {
            controller.postMessage("Task Manager",
                                   "Unblock Task",
                                   args);
        }
        else if (source.getText().compareTo(_deleteItem.getText()) == 0)
        {
            controller.postMessage("Task Manager",
                                   "Delete Task",
                                   args);
        }

        return;
    }

// Member Data

    // This pop-up menu was clicked over this task.
    String _taskName;

    // GUI components.
    JPopupMenu _menu;
    JMenuItem _suspendItem;
    JMenuItem _blockItem;
    JMenuItem _unblockItem;
    JMenuItem _deleteItem;
}
