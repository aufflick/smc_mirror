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
// Revision 1.3  2001/06/16 19:52:43  cwrapp
// Changes in release 1.0, beta 7:
// Fixes the minor code generation bugs and introduces a new
// example Java program (found at examples/Java/EX7). This
// example program is also a Java applet and can be seen at
// http://smc.sourceforge.net/SmcDemo.htm.
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
// Revision 1.2  2000/09/01 15:32:22  charlesr
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
import java.util.List;
import java.util.ListIterator;

public final class SmcTransitionCpp
    extends SmcTransition
{
    public SmcTransitionCpp(String name,
                            List parameters,
                            int line_number)
    {
        super(name, parameters, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String context,
                             String pkg,
                             String mapName,
                             String stateName,
                             String indent)
        throws ParseException
    {
        int guardCount;
        int guardIndex;
        int nullConditions;
        ListIterator paramIt;
        SmcParameter parameter;
        ListIterator guardIt;
        SmcGuard guard;
        String virtual;
        String fqStateName;

        // Qualify the state name as well.
        if (stateName.indexOf("::") < 0)
        {
            fqStateName = mapName + "::" + stateName;
        }
        else
        {
            fqStateName = stateName;
        }

        // If this transition is in the default state, then
        // precede the method with "virtual".
        if (stateName.compareTo("Default") == 0)
        {
            virtual = "virtual ";
        }
        else
        {
            virtual = "";
        }

        header.print(indent +
                     "    " +
                     virtual +
                     "void " +
                     _name +
                     "(" +
                     context +
                     "Context& s");

        source.print("\n" +
                     indent +
                     "void " +
                     mapName +
                     "_" +
                     stateName +
                     "::" +
                     _name +
                     "(" +
                     context +
                     "Context& s");

        // Add user-defined parameters.
        for (paramIt = _parameters.listIterator();
             paramIt.hasNext() == true;
            )
        {
            parameter = (SmcParameter) paramIt.next();

            header.print(", ");
            parameter.generateCode(header);

            source.print(", ");
            parameter.generateCode(source);
        }
        header.println(");");
        source.println(")");
        source.println(indent + "{");

        // Print the transition to the verbose log.
        if (Smc.isDebug() == true)
        {
            source.println(indent +
                           "    if (s.getDebugFlag() == true)");
            source.println(indent + "    {");
            source.println(indent +
                           "        ostream& str = s.getDebugStream();");
            source.println();
            source.print(indent +
                         "        str << \"TRANSITION   : " +
                         mapName +
                         " " +
                         _name);
            if (_parameters.size() > 0)
            {
                String separator;

                source.print("(");
                for (paramIt = _parameters.listIterator(),
                         separator = "";
                     paramIt.hasNext() == true;
                     separator = ", ")
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(separator);
                    parameter.generateCode(source);
                }
                source.print(")");
            }
            source.println("\"");
            source.println(indent + "        << endl;");
            source.println(indent + "    }\n");
        }

        // Loop through the guards and print each one.
        for (guardIt = _guards.listIterator(),
                     guardIndex = 0,
                     guardCount = _guards.size(),
                     nullConditions = 0;
             guardIt.hasNext() == true;
             ++guardIndex)
        {
            guard = (SmcGuard) guardIt.next();

            // Count up the number of guards with no condition.
            if (guard.getCondition() == null)
            {
                ++nullConditions;
            }

            guard.generateCode(source,
                               guardIndex,
                               guardCount,
                               context,
                               pkg,
                               mapName,
                               stateName,
                               "    ");
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition.
        if (guardCount > 0 && nullConditions == 0)
        {
            if (guardCount == 1)
            {
                source.println(indent + "    }");
            }

            source.println(indent + "    else");
            source.println(indent + "    {");
            source.print(indent +
                         "         " +
                         mapName +
                         "_Default::" +
                         _name +
                         "(s");

            for (paramIt = _parameters.listIterator();
                 paramIt.hasNext() == true;
                )
            {
                parameter = (SmcParameter) paramIt.next();
                source.print(", " + parameter.getName());
            }
            source.println(");");
            source.println(indent + "    }");
            source.println();
        }
        else if (nullConditions > 1)
        {
            // If there are multiple transitions with the same
            // name, then only one of them may be unguarded.
            ParseException e =
                    new ParseException("State " +
                                       mapName +
                                       "::" +
                                       stateName +
                                       " has multiple transitions with the same name (\"" +
                                       _name +
                                       "\") and guard.",
                                       0);
            throw(e);
        }
        else if (guardCount > 1)
        {
            source.println("\n");
        }
        else if (guardCount == 1 &&
                 ((guard = ((SmcGuard) _guards.get(0))).isLoopback(fqStateName)
                     == false ||
                  guard.getActionCount() > 0))
        {
            // Add a newline to the end of the if/then/else body.
            source.println();
        }

        source.println(indent + "    return;");
        source.println(indent + "}");

        return;
    }
}
