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
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:19  charlesr
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

public abstract class SmcState
{
// Member Methods

    public SmcState(String name, int line_number)
    {
        _line_number = line_number;

        if (name.compareTo("Default") == 0)
        {
            _instance_name = "DefaultState";
        }
        else
        {
            _instance_name = name;
        }

        // Make sure the first character in the class name is
        // upper case.
        String first_letter = name.substring(0, 1);
        String remainder = name.substring(1);

        _class_name = first_letter.toUpperCase() + remainder;

        _entryActions = new LinkedList();
        _exitActions = new LinkedList();
        _transitions = new LinkedList();
    }

    public String getName()
    {
        return(_class_name + "." + _instance_name);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public String getClassName()
    {
        return(_class_name);
    }

    public String getInstanceName()
    {
        return(_instance_name);
    }

    public LinkedList getEntryActions()
    {
        return(_entryActions);
    }

    public LinkedList getExitActions()
    {
        return(_exitActions);
    }

    public void addEntryAction(SmcAction action)
    {
        _entryActions.add(action);
        return;
    }

    public void addExitAction(SmcAction action)
    {
        _exitActions.add(action);
        return;
    }

    public LinkedList getTransitions()
    {
        return(_transitions);
    }

    public SmcTransition findTransition(String name,
                                        LinkedList parameters)
    {
        SmcTransition retval;
        boolean match;
        ListIterator transIt;
        ListIterator paramIt1;
        ListIterator paramIt2;
        SmcTransition transition;
        SmcParameter param1;
        SmcParameter param2;

        for (transIt = _transitions.listIterator(),
                     retval = null;
             transIt.hasNext() == true && retval == null;
            )
        {
            transition = (SmcTransition) transIt.next();
            if (name.compareTo(transition.getName()) == 0 &&
                parameters.size() == transition.getParameters().size())
            {
                
                for (paramIt1 = parameters.listIterator(),
                         paramIt2 = transition.getParameters().listIterator(),
                         match = true;
                     paramIt1.hasNext() == true && match == true;
                    )
                {
                    param1 = (SmcParameter) paramIt1.next();
                    param2 = (SmcParameter) paramIt2.next();
                    match = param1.equals(param2);
                }

                if (match == true)
                {
                    retval = transition;
                }
            }
        }

        return(retval);
    }

    public void addTransition(SmcTransition transition)
    {
        // Add the transition only if it is not already in the
        // list.
        if (_transitions.contains(transition) == false)
        {
            _transitions.add(transition);
        }

        return;
    }

    public String toString()
    {
        String retval;
        String separator;
        ListIterator it;
        SmcAction action;
        SmcTransition transition;

        retval = _instance_name;

        if (_entryActions.size() > 0)
        {
            retval += "\n\tEntry {";
            for (it = _entryActions.listIterator(),
                     separator = "";
                 it.hasNext() == true;
                 separator = " ")
            {
                action = (SmcAction) it.next();
                retval += separator + action;
            }
            retval += "}";
        }

        if (_exitActions.size() > 0)
        {
            retval += "\n\tExit {";
            for (it = _exitActions.listIterator(),
                     separator = "";
                 it.hasNext() == true;
                 separator = " ")
            {
                action = (SmcAction) it.next();
                retval += separator + action;
            }
            retval += "}";
        }

        for (it = _transitions.listIterator();
             it.hasNext() == true;
            )
        {
            transition = (SmcTransition) it.next();
            retval += "\n" + transition;
        }

        return(retval);
    }

    public abstract void generateCode(PrintStream header,
                                      PrintStream stream,
                                      String mapName,
                                      String context)
        throws ParseException;;

// Member Data

    protected int _line_number;
    protected String _class_name;
    protected String _instance_name;
    protected LinkedList _entryActions;
    protected LinkedList _exitActions;
    protected LinkedList _transitions;
}
