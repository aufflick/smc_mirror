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
// Revision 1.3  2002/02/13 02:45:23  cwrapp
// Changes in release 1.2.0:
// Added the following features:
// + 484889: "pop" transitions can now return arguments
//           along with a transition name.
// + 496625: Multiple .sm files may be specified in the
//           compile command.
//
// Fixed the following bugs:
// + 496692: Fixed the %package C++ code generation.
// + 501157: Transition debug output was still hardcoded
//           to System.err. This has been corrected so
//           that FSMContext._debug_stream is used.
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

package net.sf.smc;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
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

    public List getArguments()
    {
        return(_arguments);
    }

    public void setArguments(List argList)
    {
        // Make a copy of the list since argList will
        // be cleared upon return.
        _arguments = (List) ((LinkedList) argList).clone();

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
    protected List _arguments;

    // true if the action's result is to be negated.
    protected String _negation;

    // Is this action for a default transition or not?
    protected boolean _default;
}
