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
// Revision 1.2  2000/09/01 15:32:14  charlesr
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
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class SmcParseTree
{
// Member Methods

    protected SmcParseTree()
    {
        _start_state = "";
        _source = "";
        _context = "";
        _header = "";
        _header_line = 1;
        _maps = new LinkedList();
    }

    public String getSource()
    {
        return(_source);
    }

    public void setSource(String source)
    {
        _source = source;
        return;
    }

    public void setHeaderLine(int line_number)
    {
        _header_line = line_number;
        return;
    }

    public String getStartState()
    {
        return(_start_state);
    }

    public void setStartState(String state)
    {
        _start_state = state;
        return;
    }

    public String getContext()
    {
        return(_context);
    }

    public void setContext(String context)
    {
        _context = context;
        return;
    }

    public String getHeader()
    {
        return(_header);
    }

    public void setHeader(String header)
    {
        _header = header;
        return;
    }

    public SmcMap findMap(String name)
    {
        ListIterator mapIt;
        SmcMap map;
        SmcMap retval;

        for (mapIt = _maps.listIterator(), retval = null;
             mapIt.hasNext() == true && retval == null;
            )
        {
            map = (SmcMap) mapIt.next();
            if (map.getName().compareTo(name) == 0)
            {
                retval = map;
            }
        }

        return(retval);
    }

    public void addMap(SmcMap map)
    {
        _maps.add(map);
        return;
    }

    // Perform semantic checks on parsed state map.
    public boolean check()
    {
        boolean retval = true;
        ListIterator mapIt;
        SmcMap map;
        ListIterator stateIt;
        SmcState state;

        // Check if the start state and class has been
        // specified (and header file for C++ generation).
        if (_start_state.length() == 0)
        {
            System.err.println(Smc.getSourceFileName() +
                               ":" +
                               Integer.toString(_header_line) +
                               ": error - \"%start\" missing.");

            retval = false;
        }

        if (_context.length() == 0)
        {
            System.err.println(Smc.getSourceFileName() +
                               ":" +
                               Integer.toString(_header_line) +
                               ": error - \"%class\" missing.");

            retval = false;
        }

        if (Smc._target_language == Smc.C_PLUS_PLUS &&
            _header.length() == 0)
        {
            System.err.println(Smc.getSourceFileName() +
                               ":" +
                               Integer.toString(_header_line) +
                               ": error - \"%header\" missing.");

            retval = false;
        }

        // Check if all the end states are valid.
        // Check each map in turn. But don't stop when an error
        // is found - check all the transitions.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Check the real states first.
            for (stateIt = map.getStates().listIterator();
                 stateIt.hasNext() == true;
                )
            {
                state = (SmcState) stateIt.next();

                if (checkState(state, map.getName()) == false)
                {
                    retval = false;
                }

            }

            // Now check the default state.
            if (map.hasDefaultState() == true)
            {
                if (checkState(map.getDefaultState(),
                               map.getName()) == false)
                {
                    retval = false;
                }
            }
        }

        return(retval);
    }

    public void dump(PrintStream stream)
    {
        ListIterator map_it;
        SmcMap map;

        stream.println("Start State: " + _start_state);
        stream.print("     Source:");
        if (_source.length() == 0)
        {
            stream.println(" none.");
        }
        else
        {
            stream.println("\n" + _source);
        }
        stream.println("    Context: " + _context);
        stream.println("     Header: " + _header);

        stream.println("       Maps:\n");

        for (map_it = _maps.listIterator();
             map_it.hasNext() == true;
            )
        {
            map = (SmcMap) map_it.next();
            stream.println(map);
        }

        return;
    }

    public abstract void generateCode(PrintStream header,
                                      PrintStream source,
                                      String srcfileBase)
        throws ParseException;

    // Check if two parameter lists are equal.
    protected boolean compareParameters(LinkedList param_list1,
                                        LinkedList param_list2)
    {
        boolean retval;

        if (param_list1 == null && param_list2 == null)
        {
            retval = true;
        }
        else if (param_list1 == null ||
                 param_list2 == null ||
                 param_list1.size() != param_list2.size())
        {
            retval = false;
        }
        else if (param_list1.size() == 0)
        {
            retval = true;
        }
        else
        {
            ListIterator pit1;
            ListIterator pit2;
            SmcParameter param1;
            SmcParameter param2;

            for (pit1 = param_list1.listIterator(),
                     pit2 = param_list2.listIterator(),
                     retval = true;
                 pit1.hasNext() == true && retval == true;
                )
            {
                param1 = (SmcParameter) pit1.next();
                param2 = (SmcParameter) pit2.next();
                retval = (param1.compareTo(param2) == 0 ? true :
                                                          false);
            }
        }

        return(retval);
    }

    // Check if the state's transitions contain valid end states.
    private boolean checkState(SmcState state, String mapName)
    {
        boolean retval;
        ListIterator transIt;
        SmcTransition transition;
        ListIterator guardIt;
        SmcGuard guard;
        String endState;
        SmcAction condition;

        retval = true;
        for (transIt = state.getTransitions().listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();

            for (guardIt = transition.getGuards().listIterator();
                 guardIt.hasNext() == true;
                )
            {
                guard = (SmcGuard) guardIt.next();
                endState = guard.getEndState();
                condition = guard.getCondition();

                // Ignore pop transitions.
                if (guard.getTransType() == Smc.TRANS_POP)
                {
                    // Do nothing.
                }
                else if (endState.compareToIgnoreCase("default") == 0)
                {
                    System.err.println(Smc.getSourceFileName() +
                                       ":" +
                                       Integer.toString(guard.getLineNumber()) +
                                       ": error - may not transition to the default state.");

                    retval = false;
                }
                // "nil" is always a valid end state.
                else if (endState.compareTo("nil") != 0 &&
                         findState(endState,
                                   mapName,
                                   state.getClassName()) == false)
                {
                    System.err.println(Smc.getSourceFileName() +
                                       ":" +
                                       Integer.toString(guard.getLineNumber()) +
                                       ": error - no such state as \"" +
                                       endState +
                                       "\".");

                    retval = false;
                }
            }
        }

        return(retval);
    }

    // Does the state exist?
    private boolean findState(String endState,
                              String mapName,
                              String stateName)
    {
        boolean retval;
        int index;
        String mapName2;
        String stateName2;
        ListIterator mapIt;
        SmcMap map;
        ListIterator stateIt;
        SmcState state;

        // Is end state of the form "map::state"?
        if ((index = endState.indexOf("::")) < 0)
        {
            mapName2 = mapName;
            stateName2 = endState;
        }
        else
        {
            mapName2 = endState.substring(0, index);
            stateName2 = endState.substring(index + 2);
        }

        // Is the end state the same as the current state?
        if (mapName2.compareTo(mapName) == 0 &&
            stateName2.compareTo(stateName) == 0)
        {
            retval = true;
        }
        // Can't traverse to the default state.
        else if (stateName2.compareToIgnoreCase("default") == 0)
        {
            retval = false;
        }
        else
        {
            // Go through each map and look for the named state.
            for (mapIt = _maps.listIterator(), retval = false;
                 mapIt.hasNext() == true && retval == false;
                )
            {
                map = (SmcMap) mapIt.next();
                if (map.getName().compareTo(mapName2) == 0)
                {
                    for (stateIt = map.getStates().listIterator();
                         stateIt.hasNext() == true;
                        )
                    {
                        state = (SmcState) stateIt.next();
                        if (state.getClassName().compareTo(stateName2) == 0)
                        {
                            retval = true;
                        }
                    }
                }
            }
        }

        return(retval);
    }

// Member Data

    // The state map's initial state.
    protected String _start_state;

    // Raw source code appearing at the beginning of the state
    // map source file.
    protected String _source;

    // This state map is associated with this class.
    protected String _context;

    // Where the associated class is defined.
    protected String _header;

    // The line where %start, etc. should appear.
    // Used in error messages.
    private int _header_line;

    // The state maps.
    protected LinkedList _maps;
}
