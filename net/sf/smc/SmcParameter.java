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
// Revision 1.3  2002/05/07 00:10:20  cwrapp
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

package net.sf.smc;

import java.io.PrintStream;

public abstract class SmcParameter
{
// Member Methods

    public SmcParameter(String name, int line_number)
    {
        _name = name;
        _line_number = line_number;
        _type = "";
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcParameter parameter = (SmcParameter) obj;

            if (_name.compareTo(parameter.getName()) == 0 &&
                _type.compareTo(parameter.getType()) == 0)
            {
                retval = true;
            }
            else
            {
                retval = false;
            }
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return(retval);
    }

    public int compareTo(SmcParameter param)
    {
        return(_type.compareTo(param.getType()));
    }

    public String getName()
    {
        return(_name);
    }

    public String getType()
    {
        return(_type);
    }

    public void appendType(String type_name, String separator)
    {
        _type += separator + type_name;
        return;
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public String toString()
    {
        return(_name + ": " + _type);
    }

    public abstract void generateCode(PrintStream source);

// Member Data.

    String _name;
    String _type;
    int _line_number;
}
