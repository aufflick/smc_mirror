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
// Revision 1.2  2000/09/01 15:32:20  charlesr
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
import java.util.ListIterator;

public final class SmcStateJava
    extends SmcState
{
    public SmcStateJava(String name, int line_number)
    {
        super(name, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String mapName,
                             String context)
        throws ParseException
    {
        ListIterator actionIt;
        ListIterator transIt;
        SmcAction action;
        SmcTransition transition;

        // Declare the inner state class.
        source.println("\n        private static final class " +
                       mapName +
                       "_" +
                       _class_name);
        source.println("            extends " +
                       mapName +
                       "_Default");
        source.println("        {");

        // Add the constructor.
        source.println("            private " +
                       mapName +
                       "_" +
                       _class_name +
                       "(String name)");
        source.println("            {");
        source.println("                super(name);");
        source.println("            }");

        // Add the Entry() and Exit() member functions if this
        // state defines them.
        if (_entryActions.size() > 0)
        {
            source.println("\n            protected void Entry(" +
                           context +
                           "Context s)");
            source.println("            {");

            // Generate the actions associated with this code.
            for (actionIt = _entryActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source,
                                    context,
                                    "                ");
                source.println(";");
            }

            // End the Entry() member function with a return.
            source.println("                return;");
            source.println("            }");
        }

        if (_exitActions.size() > 0)
        {
            source.println("\n            protected void Exit(" +
                           context +
                           "Context s)");
            source.println("            {");

            // Generate the actions associated with this code.
            for (actionIt = _exitActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source,
                                    context,
                                    "                ");
                source.println(";");
            }

            // End the Exit() member function with a return.
            source.println("                return;");
            source.println("            }");
        }

        // Have each transition generate its code.
        for (transIt = _transitions.listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();
            transition.generateCode(header,
                                    source,
                                    context,
                                    mapName,
                                    _class_name,
                                    "            ");
        }

        // End of this state class declaration.
        source.println("        }");

        return;
    }
}
