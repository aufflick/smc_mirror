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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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
                             String context)
        throws ParseException
    {
        LinkedList definedDefaultTransitions = null;
        LinkedList transList;
        ListIterator stateIt;
        ListIterator transIt;
        ListIterator paramIt;
        SmcState state;
        SmcTransition defaultTransition =
                new SmcTransitionTcl("Default",
                                     new LinkedList(),
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
            definedDefaultTransitions = new LinkedList();
        }

        // Declare the map class.
        source.println("class " + _name + " {");

        source.println("\n# Member data.\n");

        // Print all the static state objects.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            source.println("    public common " +
                           state.getClassName() +
                           " \"\";");
        }

        // End of map class.
        source.println("}\n");

        // Declare the map's default state class.
        source.println("class " + _name + "_Default {");
        source.println("    inherit ::statemap::State;\n");

        source.println("# Member functions.\n");
        source.println("    constructor {name} {");
        source.println("       ::statemap::State::constructor $name;");
        source.println("    } {}\n");

        // Define the default Entry() and Exit() methods.
        source.println("    public method Entry {context} {};");
        source.println("    public method Exit {context} {};");

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
                                   _name,
                                   "",
                                   null);
            }
        }

        // Declare the undefined default transitions.
        for (transIt = getUndefinedDefaultTransitions().listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            source.print("\n    public method " +
                         trans.getName() +
                         " {context");
            for (paramIt = trans.getParameters().listIterator();
                 paramIt.hasNext() == true;
                )
            {
                parameter = (SmcParameter) paramIt.next();
                source.print(" ");
                parameter.generateCode(source);
            }
            source.println("} {");
            source.println("        set _transition \"" +
                           trans.getName() +
                           "\";");
            source.println("        Default $context;");
            source.println("        return -code ok;");
            source.println("    }");
        }

        // Define the default Default transition if it has not
        // already been defined.
        if (Collections.binarySearch(definedDefaultTransitions,
                                     defaultTransition,
                                     new Comparator() {
                                         public int compare(Object o1,
                                                            Object o2) {
                                             return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                         }
                                      }) < 0)
        {
            source.println("\n    public method Default {context} {");
            source.println("        return -code error \"Transition \\\"$_transition\\\" fell through to a non-existent default definition.\"");
            source.println("    }");
        }

        source.println("\n# Member data.\n");

        // This private member data is for storing the
        // transition's name when a default transition is taken.
        source.println("    protected common _transition \"\";");

        // End the map's default state class declaration.
        source.println("}\n");

        // Have each state now generate itself.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            state.generateCode(header, source, _name, context);
        }

        // Now create each of the static states.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            source.println("set " +
                           _name +
                           "::" +
                           state.getClassName() +
                           " [" +
                           _name +
                           "_" +
                           state.getClassName() +
                           " #auto \"" +
                           _name +
                           "::" +
                           state.getClassName() +
                           "\"];");
        }

        source.println("");

        return;
    }
}
