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
// Revision 1.4  2002/05/07 00:10:20  cwrapp
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
// Revision 1.2  2000/09/01 15:32:21  charlesr
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
// Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class SmcTransition
{
// Member Methods

    public SmcTransition(String name,
                         List parameters,
                         int line_number)
    {
        _name = name;
        _parameters = parameters;
        _line_number = line_number;
        _guards = (List) new LinkedList();
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcTransition trans = (SmcTransition) obj;

            if (_name.compareTo(trans.getName()) != 0 ||
                _parameters.size() != trans.getParameters().size())
            {
                retval = false;
            }
            else
            {
                ListIterator it1;
                ListIterator it2;
                SmcParameter param1;
                SmcParameter param2;

                // The two parameter lists are equal until proven
                // otherwise.
                for (it1 = _parameters.listIterator(),
                         it2 = trans.getParameters().listIterator(),
                         retval = true;
                     it1.hasNext() == true && retval == true;
                    )
                {
                    param1 = (SmcParameter) it1.next();
                    param2 = (SmcParameter) it2.next();
                    retval = param1.equals(param2);
                }
            }
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return(retval);
    }

    public int compareTo(SmcTransition trans)
    {
        int retval;

        if ((retval = _name.compareTo(trans.getName())) == 0)
        {
            retval = _parameters.size() - trans.getParameters().size();
            if (retval == 0)
            {
                ListIterator it1;
                ListIterator it2;
                SmcParameter param1;
                SmcParameter param2;

                // Compare each parameter's name ONLY.
                for (it1 = _parameters.listIterator(),
                         it2 = trans.getParameters().listIterator();
                     it1.hasNext() == true && retval == 0;
                    )
                {
                    param1 = (SmcParameter) it1.next();
                    param2 = (SmcParameter) it2.next();
                    retval = param1.compareTo(param2);
                }
            }
        }

        return(retval);
    }

    public String getName()
    {
        return(_name);
    }

    public List getParameters()
    {
        return(_parameters);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public void addGuard(SmcGuard guard)
    {
        _guards.add(guard);
        return;
    }

    public List getGuards()
    {
        return(_guards);
    }

    public String toString()
    {
        String retval;
        ListIterator it;

        retval = _name;

        if (_parameters.size() > 0)
        {
            String separator;
            SmcParameter param;

            retval += "(";
            for (it = _parameters.listIterator(),
                     separator = "";
                 it.hasNext() == true;
                 separator = ", ")
            {
                param = (SmcParameter) it.next();
                retval += separator + param;
            }
            retval += ")";
        }

        if (_guards.size() > 0)
        {
            SmcGuard guard;

            for (it = _guards.listIterator();
                 it.hasNext() == true;
                )
            {
                guard = (SmcGuard) it.next();
                retval += "\n" + guard;
            }
        }

        return(retval);
    }

    public abstract void generateCode(PrintStream header,
                                      PrintStream source,
                                      String context,
                                      String pkg,
                                      String mapName,
                                      String stateName,
                                      String indent)
        throws ParseException;

// Member Data

    protected String _name;
    protected int _line_number;
    protected List _parameters;
    protected List _guards;
}
