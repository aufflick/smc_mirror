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
// Revision 1.2  2000/09/01 15:32:15  charlesr
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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public final class SmcParseTreeCpp
    extends SmcParseTree
{
    public SmcParseTreeCpp()
    {
        super();
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String srcfileBase)
        throws ParseException
    {
        String srcfileCaps;
        String mapName;
        String separator;
        String indent;
        ListIterator iIt;
        ListIterator mapIt;
        ListIterator stateIt;
        ListIterator transIt;
        ListIterator paramIt;
        String packageName;
        SmcMap map;
        SmcState state;
        SmcTransition trans;
        SmcParameter param;

        // The first two lines in the header file should be:
        //
        //    #ifndef _H_<source file name>_SM
        //    #define _H_<source file name>_SM
        //
        // where the source file name is all in caps.
        // The last line is:
        //
        //    #endif
        //
        srcfileCaps = srcfileBase.toUpperCase();
        header.println("#ifndef _H_" +
                       srcfileCaps +
                       "_SM");
        header.println("#define _H_" +
                       srcfileCaps +
                       "_SM");

        // If transition queuing is turned on, then #define
        // SMC_TRANS_Q to turn it on in statemap.h.
        /*
         * Transition queuing not supported.
         *
        if (Smc.isTransQueue() == true)
        {
            header.println("\n// Turn on transition queuing before including statemap.h.");
            header.println("#define SMC_TRANS_Q\n");
        }
         */

        // Include required standard .h files.
        header.println("#include <statemap.h>\n");

        // Dump out the raw source code, if any.
        if (_source != null && _source.length() > 0)
        {
            source.println (_source + "\n");
        }
                
        // Import the statemap namespace symbols into the main
        // namespace.
        header.println("using namespace statemap;");

        // Do user-specified imports now.
        for (iIt = _importList.listIterator();
             iIt.hasNext() == true;
            )
        {
            packageName = (String) iIt.next();
            header.println("using namespace " + packageName + ";");
        }

        header.println();

        // If a namespace was specified, then output that
        // namespace now.
        if (_package != null && _package.length() > 0)
        {
            header.println("namespace " + _package);
            header.println("{");
            indent = "    ";
        }
        else
        {
            indent = "";
        }

        // Forward declare all the state classes in all the maps.
        header.println(indent + "// Forward declarations.");

        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Forward declare the map.
            header.println(indent +
                           "class " +
                           map.getName() +
                           ";");

            // Iterate over the map's states.
            for (stateIt = map.getStates().listIterator();
                 stateIt.hasNext() == true;
                )
            {
                state = (SmcState) stateIt.next();
                header.println(indent +
                               "class " +
                               map.getName() +
                               "_" +
                               state.getClassName() +
                               ";");
            }

            // Forward declare the default state as well.
            header.println(indent +
                           "class " +
                           map.getName() +
                           "_Default;");
        }

        // Forward declare the state class and its
        // context as well.
        header.println(indent + "class " + _context + "State;");
        header.println(indent +
                       "class " +
                       _context +
                       "Context;");

        // Forward declare the application class.
        header.println(indent + "class " + _context + ";\n");

        // Declare user's base state class.
        header.println(indent +
                       "class " +
                       _context +
                       "State : public State");
        header.println(indent + "{");
        header.println(indent + "public:");

        // Constructor.
        header.println(indent +
                       "    " +
                       _context +
                       "State(const char *name)");
        header.println(indent + "    : State(name)");
        header.println(indent + "    {};\n");


        // Add the default Entry() and Exit() definitions.
        header.println(indent + "    virtual void Entry(" +
                       _context +
                       "Context&) {};");
        header.println(indent + "    virtual void Exit(" +
                       _context +
                       "Context&) {};\n");

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        List transList = (List) new LinkedList();
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Merge the new transitions into the current set.
            transList =
                    Smc.merge(map.getTransitions(),
                              transList,
                              new Comparator() {
                                   public int compare(Object o1,
                                                      Object o2) {
                                       return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                   }
                               });
        }

        // Output the global transition declarations.
        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            if (trans.getName().compareTo("Default") != 0)
            {
                header.print(indent +
                             "    virtual void " +
                             trans.getName() +
                             "(" +
                             _context +
                             "Context& context");

                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    param = (SmcParameter) paramIt.next();
                    header.print(", ");
                    param.generateCode(header);
                }

                header.println(");");
            }
        }

        // Declare the global Default transition.
        header.println("\n" + indent + "protected:");
        header.println(indent +
                       "    virtual void Default(" +
                       _context +
                       "Context& context);");

        // The base class has been defined.
        header.println(indent + "};\n");

        // Begin the source file with the appropriate headers.
        source.println("#include \"" +
                       srcfileBase +
                       "_sm.h\"");

        if (_header == null || _header.length() == 0)
        {
            ParseException e =
                    new ParseException("No header file specified.", 0);
            throw(e);
        }

        source.println("#include \"" +
                       _header +
                       "\"\n");

        // Import the user-defined namespace into the main
        // namespace.
        if (_package != null && _package.length() > 0)
        {
            source.println("namespace " + _package);
            source.println("{");
            indent = "    ";
        }
        else
        {
            indent = "";
        }

        // Statically declare all derive state classes.
        source.println(indent +
                       "// Static class declarations.");
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            mapName = map.getName();
            for (stateIt = map.getStates().listIterator();
                 stateIt.hasNext() == true;
                )
            {
                state = (SmcState) stateIt.next();
                source.println(indent +
                               mapName +
                               "_" +
                               state.getClassName() +
                               "    " +
                               mapName +
                               "::" +
                               state.getInstanceName() +
                               "(\"" +
                               mapName +
                               "::" +
                               state.getClassName() +
                               "\");");
            }
        }

        // Output the default transition definitions.
        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            if (trans.getName().compareTo("Default") != 0)
            {
                source.print("\n" +
                             indent +
                             "void " +
                             _context +
                             "State::" +
                             trans.getName() +
                             "(" +
                             _context +
                             "Context& context");

                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    param = (SmcParameter) paramIt.next();
                    source.print(", ");
                    param.generateCode(source);
                }

                source.println(")");
                source.println(indent + "{");
                source.println(indent + "    Default(context);");
                source.println(indent + "    return;");
                source.println(indent + "}");
            }
        }

        // Output the Default transition method ... almost.
        // If -g is being used, then add the "s" argname.
        source.println("\n" +
                       indent +
                       "void " +
                       _context +
                       "State::Default(" +
                       _context +
                       "Context& context)");
        source.println(indent + "{");

        // Print the transition out to the verbose log.
        if (Smc.isDebug() == true)
        {
            source.println(indent +
                           "    if (context.getDebugFlag() == true)");
            source.println(indent + "    {");
            source.println(indent +
                           "        ostream& str = context.getDebugStream();");
            source.println();
            source.println(indent +
                           "        str << \"TRANSITION   : Default\"");
            source.println(indent + "            << endl;");
            source.println(indent + "    }");
            source.println();
        }

        // A transition has been issued which has no
        // definition in the current state and there
        // is no default to cover for it. Throw an
        // exception.
        source.println(indent + "    throw (");
        source.println(indent +
                       "        TransitionUndefinedException(");
        source.println(indent +
                       "            context.getState().getName(),");
        source.println(indent +
                       "            context.getTransition()));");
        source.println();

        source.println(indent + "    return;");
        source.println(indent + "}");

        // Have each map print out its source code now.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            map.generateCode(header,
                             source,
                             _context,
                             _package,
                             indent);
        }

        // If a namespace was specified, then put an
        // ending brace on the namespace now.
        if (_package != null && _package.length() > 0)
        {
            source.println("}");
        }

        // Generate the context class.
        header.println(indent +
                       "class " +
                       _context +
                       "Context : public FSMContext");
        header.println(indent + "{");
        header.println(indent + "public:");
        header.println(indent +
                       "    " +
                       _context +
                       "Context(" +
                       _context +
                       "& owner)");
        header.println(indent + "    : _owner(owner)");
        header.println(indent + "    {");
        header.println(indent +
                       "        setState(" +
                       _start_state +
                       ");");
        header.println(indent +
                       "        " +
                       _start_state +
                       ".Entry(*this);");
        header.println(indent + "    };\n");
        header.println(indent +
                       "    " +
                       _context +
                       "& getOwner() const");
        header.println(indent + "    {");
        header.println(indent + "        return(_owner);");
        header.println(indent + "    };\n");
        header.println(indent +
                       "    " +
                       _context +
                       "State& getState() const");
        header.println(indent + "    {");
        header.println(indent + "        if (_state == NULL)");
        header.println(indent + "        {");
        header.println(indent +
                       "            throw StateUndefinedException();");
        header.println(indent + "        }\n");
        header.println(indent +
                       "        return(dynamic_cast<" +
                       _context +
                       "State&>(*_state));");
        header.println(indent + "    };");

        // If transition queuing is being done, generate a
        // dispatchTransitions() method.
        /*
         * Transition queuing not being done.
        if (Smc.isTransQueue() == true)
        {
            header.println("\n    void dispatchTransitions();\n");
        }
         */

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            if (trans.getName().compareTo("Default") != 0)
            {
                header.print("\n" +
                             indent +
                             "    void " +
                             trans.getName() +
                             "(");
                for (paramIt = trans.getParameters().listIterator(),
                         separator = "";
                     paramIt.hasNext() == true;
                     separator = ", ")
                {
                    param = (SmcParameter) paramIt.next();
                    header.print(separator);
                    param.generateCode(header);
                }
                header.print(")");

                // If doing transition queuing, define this
                // method in the source file.
                /*
                 * Transition queuing not being done.
                if (Smc.isTransQueue() == true)
                {
                    header.println(";");
                }
                else
                {
                */
                header.println("\n" +
                               indent +
                               "    {");
                header.println(indent + "        setTransition(\"" +
                               trans.getName() +
                               "\");");
                header.print(indent +
                             "        (getState())." +
                             trans.getName() +
                             "(*this");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    param = (SmcParameter) paramIt.next();
                    header.print(", " + param.getName());
                }
                header.println(");");
                header.println(indent +
                               "        setTransition(NULL);");
                header.println(indent + "    };");
                /*
                 * Transition queuing not supported.
                }
                 */
            }
        }

        // Member data.
        header.println("\n" + indent + "private:");
        header.println(indent +
                       "    " +
                       _context +
                       "& _owner;");

        // For transition queuing, generate an enum listing all
        // possible transitions.
        /*
         * Transition queuing not supported.
         *
        if (Smc.isTransQueue() == true)
        {
            List paramList;
            int i;
            int j;

            header.println("    enum TransIndex");
            header.println("    {");

            for (transIt = transList.listIterator(),
                           i = 1,
                           separator = "";
                 transIt.hasNext() == true;
                 ++i, separator = ",\n")
            {
                trans = (SmcTransition) transIt.next();
                if (trans.getName().compareTo("Default") != 0)
                {
                    header.print(separator +
                                 "        " +
                                 trans.getName() +
                                 "_" +
                                 Integer.toString(i) +
                                 " = " +
                                 Integer.toString(i));
                }
            }
            header.println("\n    };");

            // Now generate the source file code.
            // First, generate the transition methods and then
            // the dispatchTransitions() method.
            for (transIt = transList.listIterator(),
                           i = 1,
                           separator = "";
                 transIt.hasNext() == true;
                 ++i, separator = ",\n")
            {
                trans = (SmcTransition) transIt.next();
                if (trans.getName().compareTo("Default") != 0)
                {
                    source.print("\nvoid " +
                                 _context +
                                 "Context::" +
                                 trans.getName() +
                                 "(");
                    paramList = trans.getParameters();
                    for (paramIt = paramList.listIterator(),
                             separator = "";
                         paramIt.hasNext() == true;
                         separator = ", ")
                    {
                        param = (SmcParameter) paramIt.next();
                        source.print(separator);
                        param.generateCode(source);
                    }
                    source.println(")");
                    source.println("{");

                    // Are there any arguments to store away?
                    if (paramList.size() == 0)
                    {
                        // No.
                        source.println("    enqueue_transition(new TransEntry(" +
                                       _context +
                                       "Context::" +
                                       trans.getName() +
                                       "_" +
                                       Integer.toString(i) +
                                       ", NULL, 0));\n");
                    }
                    else
                    {
                        source.println("    unsigned char **argv = new unsigned char*[" +
                                       Integer.toString(paramList.size()) +
                                       "];\n");

                        for (paramIt = paramList.listIterator(),
                                   j = 0;
                             paramIt.hasNext() == true;
                             ++j)
                        {
                            param = (SmcParameter) paramIt.next();
                            source.println("    argv[" +
                                           Integer.toString(j) +
                                           "] = new unsigned char[sizeof(" +
                                           param.getName() +
                                           ")];");
                            source.println("    (void) memcpy(argv[" +
                                           Integer.toString(j) +
                                           "], &" +
                                           param.getName() +
                                           ", sizeof(" +
                                           param.getName() +
                                           "));");
                        }
                        source.println();

                        source.println("    enqueue_transition(new TransEntry(" +
                                       _context +
                                       "Context::" +
                                       trans.getName() +
                                       "_" +
                                       Integer.toString(i) +
                                       ", argv, " +
                                       Integer.toString(paramList.size()) +
                                       "));\n");
                    }

                    source.println("    if (_state != NULL)");
                    source.println("    {");
                    source.println("        dispatchTransitions();");
                    source.println("    }\n");
                    source.println("    return;");
                    source.println("}");
                }
            }

            // Generate the dispatchTransitions() method.
            source.println("\nvoid " +
                           _context +
                           "Context::dispatchTransitions()");
            source.println("{");
            source.println("    TransEntry *transition;");
            source.println("    int trans_index;");
            source.println("    unsigned char **argv;\n");
            source.println("    while ((transition = dequeue_transition()) != NULL)");
            source.println("    {");
            source.println("        trans_index = transition->getIndex();");
            source.println("        argv = transition->getArgv();\n");
            source.println("        switch(trans_index)");
            source.print("        {");

            // Translate the TransEntry back into a transition
            // method call.
            for (transIt = transList.listIterator(),
                           i = 1,
                           separator = "";
                 transIt.hasNext() == true;
                 ++i, separator = ",\n")
            {
                trans = (SmcTransition) transIt.next();
                if (trans.getName().compareTo("Default") != 0)
                {
                    source.println("\n            case " +
                                   trans.getName() +
                                   "_" +
                                   Integer.toString(i) +
                                   ":");

                    // If this transition takes arguments,
                    // then translate the argv back into
                    // the destination arguments.
                    paramList = trans.getParameters();
                    if (paramList.size() == 0)
                    {
                        source.println("                getState()." +
                                       trans.getName() +
                                       "(*this);");
                    }
                    else
                    {
                        source.println("            {");

                        // Declare each of the parameter variables.
                        for (paramIt = paramList.listIterator();
                             paramIt.hasNext() == true;
                            )
                        {
                            param = (SmcParameter) paramIt.next();
                            source.print("                ");
                            param.generateCode(source);
                            source.println(";");
                        }
                        source.println();

                        // Initialize the arguments.
                        for (paramIt = paramList.listIterator(),
                                   j = 0;
                             paramIt.hasNext() == true;
                             ++j)
                        {
                            param = (SmcParameter) paramIt.next();
                            source.println("                (void) memcpy(&" +
                                           param.getName() +
                                           ", argv[" +
                                           Integer.toString(j) +
                                           "], sizeof(" +
                                           param.getName() +
                                           "));");
                        }
                        source.println();

                        // Generate the transition method call.
                        source.print("                getState()." +
                                     trans.getName() +
                                     "(*this, ");
                        for (paramIt = paramList.listIterator(),
                                 separator = "";
                             paramIt.hasNext() == true;
                             separator = ", ")
                        {
                            param = (SmcParameter) paramIt.next();
                            source.print(separator +
                                         param.getName());
                        }
                        source.println(");");
                        source.println("            }");
                    }
                    source.println("                break;");
                }
            }

            source.println("        }\n");
            source.println("        delete transition;");
            source.println("    }\n");
            source.println("    return;");
            source.println("}");
        }
         * No transition queuing.
         */

        // Put the closing brace on the context class.
        header.println(indent + "};");

        // If necessary, place an end brace for the namespace.
        if (_package != null && _package.length() > 0)
        {
            header.println("};\n");
        }
        else
        {
            header.println();
        }

        header.println("#endif");

        return;
    }
}
