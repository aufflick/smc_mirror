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
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedList;
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
        ListIterator mapIt;
        ListIterator stateIt;
        ListIterator transIt;
        ListIterator paramIt;
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
        if (Smc.isTransQueue() == true)
        {
            header.println("\n// Turn on transition queuing before including statemap.h.");
            header.println("#define SMC_TRANS_Q\n");
        }

        // Include required standard .h files.
        header.println("#include <statemap.h>\n");

        // Dump out the raw source code, if any.
        if (_source != null && _source.length () > 0)
        {
            source.println (_source + "\n");
        }

        // Import the statemap namespace symbols into the main
        // namespace.
        header.println("using namespace statemap;\n");

        // Forward declare all the state classes in all the maps.
        header.println("// Forward declarations.");
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Iterate over the map's states.
            for (stateIt = map.getStates().listIterator();
                 stateIt.hasNext() == true;
                )
            {
                state = (SmcState) stateIt.next();
                header.println("class " +
                               map.getName() +
                               "_" +
                               state.getClassName() +
                               ";");
            }
        }

        // Forward declare the application class and its
        // context as well.
        header.println("class " + _context + ";");
        header.println("class " + _context + "Context;\n");

        // Declare user's base state class.
        header.println("class " +
                       _context +
                       "State : public State\n{");
        header.println("public:");

        // Constructor.
        header.println("    " +
                       _context +
                       "State(const char *name)");
        header.println("    : State(name)");
        header.println("    {};\n");


        // Add the default Entry() and Exit() definitions.
        header.println("    virtual void Entry(" +
                       _context +
                       "Context&) {};");
        header.println("    virtual void Exit(" +
                       _context +
                       "Context&) {};\n");

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        LinkedList transList = new LinkedList();
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

        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            header.print("    virtual void " +
                         trans.getName() +
                         "(" +
                         _context +
                         "Context& s");

            for (paramIt = trans.getParameters().listIterator();
                 paramIt.hasNext() == true;
                )
            {
                param = (SmcParameter) paramIt.next();
                header.print(", ");
                param.generateCode(header);
            }

            header.println(") {};");
        }

        // The base class has been defined.
        header.println("};\n");

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

        // Statically declare all derive state classes.
        source.println("// Static class declarations.");
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
                source.println(mapName +
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

        // Have each map print out its source code now.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            map.generateCode(header, source, _context);
        }

        // Generate the context class.
        header.println("class " +
                       _context +
                       "Context : public FSMContext");
        header.println("{");
        header.println("public:");
        header.println("    " +
                       _context +
                       "Context(" +
                       _context +
                       "& owner)");
        header.println("    : _owner(owner)");
        header.println("    {");
        header.println("        setState(" +
                       _start_state +
                       ");");
        header.println("    };\n");
        header.println("    " +
                       _context +
                       "& getOwner() const");
        header.println("    {");
        header.println("        return(_owner);");
        header.println("    };\n");
        header.println("    " +
                       _context +
                       "State& getState() const");
        header.println("    {");
        header.println("        assert(_state != NULL);\n");
        header.println("        return(dynamic_cast<" +
                       _context +
                       "State&>(*_state));");
        header.println("    };");

        // If transition queuing is being done, generate a
        // dispatchTransitions() method.
        if (Smc.isTransQueue() == true)
        {
            header.println("\n    void dispatchTransitions();\n");
        }

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            if (trans.getName().compareTo("Default") != 0)
            {
                header.print("    void " +
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
                if (Smc.isTransQueue() == true)
                {
                    header.println(";");
                }
                else
                {
                    header.println("\n    {");
                    header.print("        (getState())." +
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
                    header.println("    };");
                }
            }
        }

        // Member data.
        header.println("\nprivate:");
        header.println("    " +
                       _context +
                       "& _owner;\n");

        // For transition queuing, generate an enum listing all
        // possible transitions.
        if (Smc.isTransQueue() == true)
        {
            LinkedList paramList;
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

        // Put the closing brace on the context class.
        header.println("};\n");

        header.println("#endif");

        return;
    }
}
