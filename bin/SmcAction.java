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
// SmcAction --
//
//  Stores a state map action. May be associated with a
//  transition, a state's entry or exit.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:04  charlesr
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
// Revision 1.1.1.1  2000/08/02 12:50:55  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class SmcAction
{
// Member Methods

    public SmcAction(String name,
                     boolean negation_flag,
                     int line_number)
    {
        _name = name;
        _line_number = line_number;
        _default = false;

        if (negation_flag == true)
        {
            _negation = "!";
        }
        else
        {
            _negation = "";
        }

        _arguments = new LinkedList();
    }

    public String getName()
    {
        return(_name);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public boolean getDefault()
    {
        return(_default);
    }

    public void setDefault(boolean flag)
    {
        _default = flag;
        return;
    }

    public LinkedList getArguments()
    {
        return(_arguments);
    }

    public void addArgument(String argument)
    {
        _arguments.add(argument);
        return;
    }

    public int compareTo(SmcAction action)
    {
        int retval;

        if ((retval = _name.compareTo(action.getName())) == 0)
        {
            retval = _arguments.size() -
                         action.getArguments().size();
            if (retval == 0 &&
                _arguments.size() > 0)
            {
                ListIterator it1;
                ListIterator it2;
                String arg1;
                String arg2;

                for (it1 = _arguments.listIterator(),
                         it2 = action.getArguments().listIterator();
                     it1.hasNext() == true && retval == 0;
                    )
                {
                    arg1 = (String) it1.next();
                    arg2 = (String) it2.next();
                    retval = arg1.compareTo(arg2);
                }
            }
        }

        return(retval);
    }

    public String toString()
    {
        String retval;
        ListIterator argue_it;
        String argument;
        String separator;

        retval = _name +
                "(";

        for (argue_it = _arguments.listIterator(),
                     separator = "";
             argue_it.hasNext() == true;
             separator = ", ")
        {
            argument = (String) argue_it.next();
            retval += separator + argument;
        }

        retval += ")";

        return(retval);
    }

    // This language-specific method must be implemented by
    // the derived classes.
    public abstract void generateCode(PrintStream source,
                                      String context,
                                      String indent);

// Member Data

    protected String _name;
    protected int _line_number;

    // The action's argument list.
    protected LinkedList _arguments;

    // true if the action's result is to be negated.
    protected String _negation;

    // Is this action for a default transition or not?
    protected boolean _default;
}
