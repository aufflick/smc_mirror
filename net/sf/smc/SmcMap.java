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
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/02/19 19:52:49  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
// Revision 1.2  2001/12/14 20:10:37  cwrapp
// Changes in release 1.1.0:
// Add the following features:
// + 486786: Added the %package keyword which specifies the
//           Java package/C++ namespace/Tcl namespace
//           the SMC-generated classes will be placed.
// + 486471: The %class keyword accepts fully qualified
//           class names.
// + 491135: Add FSMContext methods getDebugStream and
//           setDebugStream.
// + 492165: Added -sync command line option which causes
//           the transition methods to be synchronized
//           (this option may only be used with -java).
//
// Revision 1.1  2001/12/03 14:14:03  cwrapp
// Changes in release 1.0.2:
// + Placed the class files in Smc.jar in the net.sf.smc package.
// + Moved Java source files from smc/bin to net/sf/smc.
// + Corrected a C++ generation bug wherein arguments were written
//   to the .h file rather than the .cpp file.
//
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:13:59  cwrapp
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
// Revision 1.2  2000/09/01 15:32:11  charlesr
// Changes for v. 1.0, Beta 2:
//
// + Removed order dependency on "%start", "%class" and "%header"
//   appearance. These three tokens may now appear in any order but
//   still must appear before the first map definition.
//
// + Modified SMC parser so that it will continue after finding an
//   error. Also improved the error message quality.
//
// + Made error messages so emacs is able to parse them.
//
// Revision 1.1.1.1  2000/08/02 12:50:56  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class SmcMap
{
// Member Methods

    public SmcMap(String name, int line_number)
    {
        _name = name;
        _line_number = line_number;
        _default_state = null;
        _states = (List) new LinkedList();
    }

    public String getName()
    {
        return(_name);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public List getStates()
    {
        return(_states);
    }

    public void addState(SmcState state)
    {
        if (state.getInstanceName().compareTo("DefaultState") == 0)
        {
            _default_state = state;
        }
        else
        {
            _states.add(state);
        }

        return;
    }

    public boolean findState(SmcState state)
    {
        SmcState state2;
        ListIterator it;
        boolean retval;

        for (it = _states.listIterator(), retval = false;
             it.hasNext() == true && retval == false;
            )
        {
            state2 = (SmcState) it.next();
            if (state.getInstanceName().compareTo(state2.getInstanceName()) == 0)
            {
                retval = true;
            }
        }

        return(retval);
    }

    public boolean findState(String state)
    {
        SmcState state2;
        ListIterator it;
        boolean retval;

        for (it = _states.listIterator(), retval = false;
             it.hasNext() == true && retval == false;
            )
        {
            state2 = (SmcState) it.next();
            if (state.compareTo(state2.getInstanceName()) == 0)
            {
                retval = true;
            }
        }

        return(retval);
    }

    public boolean hasDefaultState()
    {
        return(_default_state == null ? false : true);
    }

    public SmcState getDefaultState()
    {
        return(_default_state);
    }

    // Return all transitions appearing in this map.
    public List getTransitions()
    {
        SmcState state;
        ListIterator stateIt;
        List trans_list;
        List retval;

        // If this map has a default state, then initialize the
        // transition list to the default state's transitions.
        // Otherwise, set it to the empty list.
        if (_default_state != null)
        {
            retval =
                (List) new LinkedList(_default_state.getTransitions());
        }
        else
        {
            retval = (List) new LinkedList();
        }

        // Get each state's transition list and merge it into the
        // results.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            trans_list = state.getTransitions();
            retval = Smc.merge(trans_list,
                               retval,
                               new Comparator() {
                                   public int compare(Object o1,
                                                      Object o2) {
                                       return(((SmcTransition) o1).compareTo(((SmcTransition) o2)));
                                   }
                               });
        }

        return(retval);
    }

    public List getUndefinedDefaultTransitions()
    {
        List retval = (List) new LinkedList();
        List definedDefaultTransitions;
        ListIterator stateIt;
        ListIterator transIt;
        SmcTransition transition;
        SmcState state;

        if (_default_state == null)
        {
            definedDefaultTransitions = (List) new LinkedList();
        }
        else
        {
            definedDefaultTransitions =
                    _default_state.getTransitions();
            Collections.sort(definedDefaultTransitions,
                             new Comparator() {
                                 public int compare(Object o1,
                                                    Object o2) {
                                     return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                 }
                             });
        }

        // Make a transitions list in all the states.
        // For each transition that is *not* defined in the
        // default state, create a default definition for that
        // transition.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            for (transIt = state.getTransitions().listIterator();
                 transIt.hasNext() == true;
                )
            {
                // Create the default transition only if it is
                // not already in the default transition list.
                // DO NOT ADD TRANSITIONS NAMED "DEFAULT".
                transition = (SmcTransition) transIt.next();
                if (transition.getName().compareTo("Default") != 0 &&
                    definedDefaultTransitions.contains(transition) == false &&
                    retval.contains(transition) == false)
                {
                    retval.add(transition);
                }
            }
        }

        return(retval);
    }

    public String toString()
    {
        String retval;
        ListIterator state_it;
        SmcState state;

        retval = "%map " + _name;
        if (_default_state != null)
        {
            retval += "\n" + _default_state;
        }

        for (state_it = _states.listIterator();
             state_it.hasNext() == true;
            )
        {
            state = (SmcState) state_it.next();
            retval += "\n" + state;
        }

        return(retval);
    }

    public abstract void generateCode(PrintStream header,
                                      PrintStream source,
                                      String context,
                                      String pkg,
                                      String indent)
        throws ParseException;

// Member Data

    protected String _name;
    protected int _line_number;
    protected List _states;
    protected SmcState _default_state;
}
