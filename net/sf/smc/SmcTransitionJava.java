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
// Revision 1.2  2000/09/01 15:32:23  charlesr
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

public final class SmcTransitionJava
    extends SmcTransition
{
    public SmcTransitionJava(String name,
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

        source.print("\n" +
                     indent +
                     "protected void " +
                     _name +
                     "(" +
                     context +
                     "Context context");

        // Add user-defined parameters.
        for (paramIt = _parameters.listIterator();
             paramIt.hasNext() == true;
            )
        {
            parameter = (SmcParameter) paramIt.next();

            source.print(", ");
            parameter.generateCode(source);
        }
        source.println(")");

        source.println(indent + "{");

        // All transitions have a "ctxt" local variable.
        source.println(indent +
                       "    " +
                       context +
                       " ctxt = context.getOwner();");

        if (stateName.compareTo("Default") == 0)
        {
            source.println(indent +
                           "    boolean loopbackFlag = false;\n");
        }
        else
        {
            source.println();
        }

        // Output transition to debug stream.
        if (Smc.isDebug() == true)
        {
            source.println(indent +
                           "    if (context.getDebugFlag() == true)");
            source.println(indent + "    {");
            source.println(indent + "        PrintStream str = context.getDebugStream();");
            source.println();
            source.print(indent +
                         "        str.println(\"TRANSITION   : " +
                         mapName +
                         "." +
                         stateName +
                         "." +
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
            source.println("\");");
            source.println(indent + "    }");
            source.println();
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

            // Count up the guards with no condition.
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
                               indent);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition. Pass all arguments into the default
        // transition.
        if (guardCount > 0 && nullConditions == 0)
        {
            if (guardCount == 1)
            {
                source.println("}");
            }

            source.println("\n" +
                           indent +
                           "    else");
            source.println(indent + "    {");
            source.print(indent +
                         "        " +
                         mapName +
                         ".Default." +
                         _name +
                         "(context");

            for (paramIt = _parameters.listIterator();
                 paramIt.hasNext() == true;
                )
            {
                parameter = (SmcParameter) paramIt.next();
                source.print(", " + parameter.getName());
            }
            
            source.println(");");
            source.println(indent + "    }\n");
        }
        else if (nullConditions > 1)
        {
            // If there are multiple transition with the same
            // name, then only one of them can be unguarded.
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
        // Need to add a final newline after a multiguard block.
        else if (guardCount > 1)
        {
            source.println("\n");
        }

        source.println(indent + "    return;");
        source.println(indent + "}");

        return;
    }
}
