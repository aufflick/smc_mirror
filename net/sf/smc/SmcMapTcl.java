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
// Revision 1.3  2001/10/12 14:28:04  cwrapp
// SMC v. 1.0.1
//
// Revision 1.2  2001/05/09 23:40:01  cwrapp
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

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public final class SmcMapTcl
    extends SmcMap
{
    public SmcMapTcl(String name, int line_number)
    {
        super(name, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String context,
                             String pkg,
                             String indent)
        throws ParseException
    {
        List definedDefaultTransitions = null;
        List transList;
        ListIterator stateIt;
        ListIterator transIt;
        ListIterator paramIt;
        SmcState state;
        SmcTransition defaultTransition =
                new SmcTransitionTcl("Default",
                                     (List) new LinkedList(),
                                     _line_number);
        SmcTransition trans;
        SmcParameter parameter;

        if (_default_state != null)
        {
            definedDefaultTransitions =
                    _default_state.getTransitions();
        }
        else
        {
            definedDefaultTransitions =
                (List) new LinkedList();
        }

        // Declare the map class.
        source.println(indent + "class " + _name + " {");

        source.println(indent + "# Member data.\n");

        // Print all the static state objects.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            source.println(indent +
                           "    public common " +
                           state.getClassName() +
                           " \"\";");
        }

        // End of map class.
        source.println(indent + "}\n");

        // Declare the map's default state class.
        source.println(indent + "class " + _name + "_Default {");
        source.println(indent +
                       "    inherit " +
                       context +
                       "State;\n");

        source.println(indent + "# Member functions.\n");
        source.println(indent + "    constructor {name} {");
        source.println(indent +
                       "       " +
                       context +
                       "State::constructor $name;");
        source.println(indent + "    } {}");

        // Dump out the user-defined default transitions.
        if (_default_state != null)
        {
            for (transIt = _default_state.getTransitions().listIterator();
                 transIt.hasNext() == true;
                )
            {
                trans = (SmcTransition) transIt.next();
                trans.generateCode(header,
                                   source,
                                   context,
                                   pkg,
                                   _name,
                                   "",
                                   indent);
            }
        }

        // End the map's default state class declaration.
        source.println(indent + "}\n");

        // Have each state now generate itself.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            state.generateCode(header,
                               source,
                               _name,
                               context,
                               pkg,
                               indent);
        }

        // Now create each of the static states.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            source.println(indent +
                           "set " +
                           _name +
                           "::" +
                           state.getClassName() +
                           " " +
                           pkg +
                           "[" +
                           _name +
                           "_" +
                           state.getClassName() +
                           " #auto \"" +
                           _name +
                           "::" +
                           state.getClassName() +
                           "\"];");
        }

        source.println();

        return;
    }
}
