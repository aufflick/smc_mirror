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
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:13:59  cwrapp
// Initial revision
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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class SmcMap
{
// Member Methods

    public SmcMap(String name, int line_number)
    {
        _name = name;
        _line_number = line_number;
        _default_state = null;
        _states = new LinkedList();
    }

    public String getName()
    {
        return(_name);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public LinkedList getStates()
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
            _states.addLast(state);
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
    public LinkedList getTransitions()
    {
        SmcState state;
        ListIterator stateIt;
        LinkedList trans_list;
        LinkedList retval;

        // If this map has a default state, then initialize the
        // transition list to the default state's transitions.
        // Otherwise, set it to the empty list.
        if (_default_state != null)
        {
            retval = new LinkedList(_default_state.getTransitions());
        }
        else
        {
            retval = new LinkedList();
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

    public LinkedList getUndefinedDefaultTransitions()
    {
        LinkedList retval = new LinkedList();
        LinkedList definedDefaultTransitions;
        ListIterator stateIt;
        ListIterator transIt;
        SmcTransition transition;
        SmcState state;

        if (_default_state == null)
        {
            definedDefaultTransitions = new LinkedList();
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
                                      String context)
        throws ParseException;

// Member Data

    protected String _name;
    protected int _line_number;
    protected LinkedList _states;
    protected SmcState _default_state;
}
