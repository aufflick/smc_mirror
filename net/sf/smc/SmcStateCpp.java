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
// Revision 1.4  2002/02/19 19:52:49  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
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

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ListIterator;

public final class SmcStateCpp
    extends SmcState
{
    public SmcStateCpp(String name, int line_number)
    {
        super(name, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String mapName,
                             String context,
                             String pkg,
                             String indent)
        throws ParseException
    {
        ListIterator transIt;
        ListIterator actionIt;
        SmcTransition transition;
        SmcAction action;

        header.println("\n" +
                       indent +
                       "class " +
                       mapName +
                       "_" +
                       _class_name +
                       " : public " +
                       mapName +
                       "_Default");
        header.println(indent + "{");
        header.println(indent + "public:");

        // Add the required getName() method.
        header.println(indent +
                       "    " +
                       mapName +
                       "_" +
                       _class_name +
                       "(const char *name)");
        header.println(indent +
                       "    : " +
                       mapName +
                       "_Default(name)");
        header.println(indent + "    {};\n");

        // Add the Entry() and Exit() methods if this state
        // defines them.
        if (_entryActions.size() > 0)
        {
            header.println(indent +
                           "    void Entry(" +
                           context +
                           "Context&);");

            source.println("\n" +
                           indent +
                           "void " +
                           mapName +
                           "_" +
                           _class_name +
                           "::Entry(" +
                           context +
                           "Context& context)\n{");

            // Declare the "ctxt" local variable.
            source.println(indent +
                           "    " +
                           context +
                           "& ctxt = context.getOwner();\n");

            // Generate the actions associated with this code.
            for (actionIt = _entryActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source, context, "    ");
                source.println(";");
            }

            // End the Entry() method with a return.
            source.println(indent + "    return;");
            source.println(indent + "}");
        }

        if (_exitActions.size() > 0)
        {
            header.println(indent +
                           "    void Exit(" +
                           context +
                           "Context&);");

            source.println("\n" +
                           indent +
                           "void " +
                           mapName +
                           "_" +
                           _class_name +
                           "::Exit(" +
                           context +
                           "Context& context)");
            source.println(indent + "{");

            // Declare the "ctxt" local variable.
            source.println(indent +
                           "    " +
                           context +
                           "& ctxt = context.getOwner();\n");

            // Generate the actions associated with this code.
            for (actionIt = _exitActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source, context, "    ");
                source.println(";");
            }

            // End the Exit() method with a return.
            source.println(indent + "    return;");
            source.println(indent + "\n}");
        }

        // Have the transitions generate their code.
        for (transIt = _transitions.listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();
            transition.generateCode(header,
                                    source,
                                    context,
                                    pkg,
                                    mapName,
                                    _class_name,
                                    indent);
        }

        // End of the state class declaration.
        header.println(indent + "};");

        return;
    }
}
