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
// Revision 1.1  2001/01/03 03:13:59  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:12  charlesr
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

public final class SmcMapCpp
    extends SmcMap
{
    public SmcMapCpp(String name, int line_number)
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
                new SmcTransitionCpp("Default",
                                     new LinkedList(),
                                     _line_number);
        SmcTransition transition;
        SmcParameter parameter;
        String separator;

        // Initialize the default transition list to all the
        // default state's transitions from the default state.
        if (_default_state != null)
        {
            definedDefaultTransitions =
                    _default_state.getTransitions();
        }
        else
        {
            definedDefaultTransitions = new LinkedList();
        }

        // Get map's entire list of transitions (the returned
        // list is already sorted).
        transList = getTransitions();

        // Declare the map class.
        header.println("class " +
                       _name);
        header.println("{");
        header.println("public:");

        //  Declare the derived state classes as static.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            header.println("    static " +
                           _name +
                           "_" +
                           state.getClassName() +
                           "    " +
                           state.getInstanceName() +
                           ";");

            // TODO
            // Move this check to check() method?
            // Transitions and states cannot have the same name.
//              if (Collections.binarySearch(transList,
//                                           state.getInstanceName(),
//                                           new Comparator() {
//                                               public int compare(Object o1,
//                                                                  Object o2) {
//                                                   return(((SmcTransition) o1).getName().compareTo((String) o2));
//                                               }
//                                            }) >= 0)
//              {
//                  ParseException e =
//                          new ParseException("A state and transition have the same name: " +
//                                             state.getInstanceName(),
//                                             0);
//                  throw(e);
//              }
        }

        // Declare the default constructor as private to prevent
        // its instantiation.
        header.println("\nprivate:");
        header.println("    " + _name + "() {};");
        header.println("};\n");

        // Declare the map default state class.
        header.println("class " +
                       _name +
                       "_Default : public " +
                       context +
                       "State");
        header.println("{");
        header.println("public:");

        // Default state's constructor.
        header.println("    " +
                       _name +
                       "_Default(const char *name)");
        header.println("    : " +
                       context +
                       "State(name)");
        header.println("    {};\n");

        // Declare the user-defined default transitions first.
        if (_default_state != null)
        {
            for (transIt = _default_state.getTransitions().listIterator();
                 transIt.hasNext() == true;
                )
            {
                transition = (SmcTransition) transIt.next();

                // Since this is a default transition definition,
                // pass in the empty string as the state class
                // name.
                transition.generateCode(header,
                                        source,
                                        context,
                                        _name,
                                        "Default",
                                        null);
            }
        }

        // Declare the undefined default transitions.
        for (transIt = getUndefinedDefaultTransitions().listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();
            header.print("    virtual void " +
                         transition.getName() +
                         "(" +
                         context +
                         "Context& s");
            for (paramIt = transition.getParameters().listIterator();
                 paramIt.hasNext() == true;
                )
            {
                parameter = (SmcParameter) paramIt.next();
                header.print(", ");
                parameter.generateCode(header);
            }
            header.println(");");

            source.print("\nvoid " +
                         _name +
                         "_Default::" +
                         transition.getName() +
                         "(" +
                         context +
                         "Context& s");
            for (paramIt = transition.getParameters().listIterator();
                 paramIt.hasNext() == true;
                )
            {
                parameter = (SmcParameter) paramIt.next();
                source.print(", ");
                parameter.generateCode(source);
            }
            source.println(")\n{");
            source.println("    Default(s);");
            source.println("    return;\n}");
        }

        // Add the special transition "Default" to the list if
        // not already defined.
        if (Collections.binarySearch(definedDefaultTransitions,
                                     defaultTransition,
                                     new Comparator() {
                                         public int compare(Object o1,
                                                            Object o2) {
                                             return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                         }
                                      }) < 0)
        {
            header.println("    virtual void Default(" +
                           context +
                           "Context&);");

            // Output the Default transition method ... almost.
            // If -g is being used, then add the "s" argname.
            source.print("\nvoid " +
                         _name +
                         "_Default::Default(" +
                         context +
                         "Context&");

            // Print the transition out to the verbose log.
            if (Smc.isDebug() == true)
            {
                source.println(" s)\n{");
                source.println("    if (s.getDebugFlag() == true)");
                source.println("    {");
                source.println("        fprintf(stderr, \"TRANSITION   : " +
                               _name +
                               " Default\\n\");");
                source.println("    }\n");
            }
            else
            {
                source.println(")\n{");
            }

            source.println("    return;\n}\n");
        }

        // The map class has been defined.
        header.println("};");

        // Have each state now generate its code.
        for (stateIt = _states.listIterator();
             stateIt.hasNext() == true;
            )
        {
            state = (SmcState) stateIt.next();
            state.generateCode(header, source, _name, context);
        }

        header.println("");

        return;
    }
}
