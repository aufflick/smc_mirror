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
// Revision 1.2  2000/09/01 15:32:13  charlesr
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

public final class SmcMapJava
    extends SmcMap
{
    public SmcMapJava(String name, int line_number)
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
        List definedDefaultTransitions;
        List transList;
        ListIterator stateIt;
        ListIterator transIt;
        ListIterator paramIt;
        SmcState state;
        SmcTransition defaultTransition =
                new SmcTransitionJava("Default",
                                      (List) new LinkedList(),
                                      _line_number);
        SmcTransition transition;
        SmcParameter parameter;
        String separator;

        // Initialize the default transition list to all the
        // default state's transitions.
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

        // Get map's entire list of transitions (the returned
        // list is already sorted).
        transList = getTransitions();

        // Declare the map class. Declare it abstract to prevent
        // its instantiation.
        source.println("\n    protected static abstract class " +
                       _name);
        source.println("    {");

        // Declare each of the state class member data.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            source.println("        private static " +
                           _name +
                           "_Default." +
                           _name +
                           "_" +
                           state.getClassName() +
                           " " +
                           state.getInstanceName() +
                           ";");
        }

        // Create a default state as well.
        source.println("        private static " +
                       _name +
                       "_Default Default;");
        source.println("");

        // Declare the static block.
        source.println("        static");
        source.println("        {");

        // Initialize the static state objects.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();

            // TODO
            // Move this check to check() method?
            // Transitions and states cannot have the same name.
//            if (Collections.binarySearch(transList,
//                                           state.getInstanceName(),
//                                           new Comparator() {l
//                                               public int compare(Object o1,
//                                                                  Object o2) {
//                                                   return(((SmcTransition) o1).getName().compareTo((SmcTransition) o2));
//                                               }
//                                            }) >= 0)
//              {
//                  ParseException e =
//                          new ParseException("A state and transition have the same name: " +
//                                             state.getInstanceName(),
//                                             0);
//                  throw(e);
//              }

            source.println("            " +
                           state.getInstanceName() +
                           " = new " +
                           _name +
                           "_Default." +
                           _name +
                           "_" +
                           state.getClassName() +
                           "(\"" +
                           _name +
                           "." +
                           state.getClassName() +
                           "\");");
        }

        // Instantiate a default state as well.
        source.println("            Default = new " +
                       _name +
                       "_Default(\"" +
                       _name +
                       ".Default\");");

        // End of static block.
        source.println("        }\n");

        // End of the map class.
        source.println("    }\n");

        // Declare the map default state class.
        source.println("    protected static class " +
                       _name +
                       "_Default");
        source.println("        extends " +
                       context +
                       "State");
        source.println("    {");

        // Generate the constructor.
        source.println("        protected " +
                       _name +
                       "_Default(String name)");
        source.println("        {");
        source.println("            super(name);");
        source.println("        }");

        // Declare the user-defined default transitions first.
        for (transIt = _default_state.getTransitions().listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();

            // Since this is a default transition definition,
            // pass in the empty string as the state class name.
            transition.generateCode(header,
                                    source,
                                    context,
                                    pkg,
                                    _name,
                                    "Default",
                                    "        ");
        }

        // Declare the undefined default transitions.
        //
        // SF Bug 469289: This code has been move into
        //                SmcParseTreeJava.java.
        //  for (transIt = getUndefinedDefaultTransitions().listIterator();
//               transIt.hasNext() == true;
//              )
//          {
//              transition = (SmcTransition) transIt.next();
//              source.print("\n        protected void " +
//                           transition.getName() +
//                           "(" +
//                           context +
//                           "Context s");
//              for (paramIt = transition.getParameters().listIterator();
//                   paramIt.hasNext() == true;
//                  )
//              {
//                  parameter = (SmcParameter) paramIt.next();
//                  source.print(", ");
//                  parameter.generateCode(source);
//              }
//              source.println(")");
//              source.println("        {");
//              source.println("            Default(s);");
//              source.println("            return;");
//              source.println("        }");
//          }

        // Add the special transition "Default" to the list if
        // not already defined.
        //
        // SF Bug 469289: This code has been move into
        //                SmcParseTreeJava.java.
//          if (Collections.binarySearch(definedDefaultTransitions,
//                                       defaultTransition,
//                                       new Comparator() {
//                                           public int compare(Object o1,
//                                                              Object o2) {
//                                               return(((SmcTransition) o1).compareTo((SmcTransition) o2));
//                                           }
//                                        }) < 0)
//          {
//              source.println("\n        protected void Default(" +
//                             context +
//                             "Context s)");
//              source.println("        {");

//              // Output transition out to debug stream.
//              if (Smc.isDebug() == true)
//              {
//                  source.println("            if (s.getDebugFlag() == true)");
//                  source.println("            {");
//                  source.println("                System.err.println(\"TRANSITION   : " +
//                                 _name +
//                                 ".Default\");");
//                  source.println("            }\n");
//              }

//              // A transition has been issued which has no
//              // definition in the current state and there
//              // is no default to cover for it. Throw an
//              // exception.
//              source.println("            throw (new statemap.TransitionUndefinedException(\"State: \" +");
//              source.println("                                                             s.getState().getName() +");
//              source.println("                                                             \", Transition: \" +");
//              source.println("                                                             s.getTransition()));");

//              source.println("        }");
//          }

        // Have each state now generate its code. Each state
        // class is an inner class.
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

        // The map class has been defined.
        source.println("    }");

        return;
    }
}
