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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.ListIterator;

public final class SmcTransitionCpp
    extends SmcTransition
{
    public SmcTransitionCpp(String name,
                            LinkedList parameters,
                            int line_number)
    {
        super(name, parameters, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String context,
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

        header.print("    " +
                     virtual +
                     "void " +
                     _name +
                     "(" +
                     context +
                     "Context& s");

        source.print("\nvoid " +
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
        source.println(")\n{");

        // Print the transition to the verbose log.
        if (Smc.isDebug() == true)
        {
            source.println("    if (s.getDebugFlag() == true)");
            source.println("    {");
            source.print("        fprintf(stderr, \"TRANSITION   : " +
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
            source.println("\\n\");");
            source.println("    }\n");
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
                               mapName,
                               stateName,
                               "    ");
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition.
        if (guardCount > 0 && nullConditions == 0)
        {
            source.println("    }");
            source.println("    else");
            source.println("    {");
            source.print("         " +
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
            source.println("    }");
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
        else if (guardCount > 0)
        {
            // Add a newline to the end of the if/then/else body.
            source.println();
        }

        source.println("\n    return;");
        source.println("}");

        return;
    }
}
