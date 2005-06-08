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
// Copyright (C) 2005. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Visits the abstract syntax tree emitting a C++ header file.
 * @see SmcElement
 * @see SmcVisitor
 * @see SmcCppGenerator
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcHeaderGenerator
    extends SmcCodeGenerator
{
//-----------------------------------------------------------------
// Member methods
//

    public SmcHeaderGenerator(PrintStream source,
                              String srcfileBase)
    {
        super (source, srcfileBase);

        _indent = "";
    }

    public void visit(SmcFSM fsm)
    {
        String srcfileCaps;
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String mapName;
        List transList = (List) new ArrayList();
        String separator;
        List params;
        Iterator it;
        Iterator mapIt;
        Iterator transIt;
        Iterator pit;
        String declaration;
        int packageDepth = 0;
        SmcMap map;
        SmcState state;
        SmcTransition trans;
        SmcParameter param;
        int index;

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

        // Make the file name upper case and replace
        // slashes with underscores.
        srcfileCaps =
            _srcfileBase.toUpperCase().replace('\\', '_');
        _source.print("#ifndef _H_");
        _source.print(srcfileCaps);
        _source.println("_SM");
        _source.print("#define _H_");
        _source.print(srcfileCaps);
        _source.println("_SM");

        // If this application *is* using iostreams to output
        // debug messages, then define SMC_USES_IOSTREAMS.
        // Otherwise the user is responsible for providing a
        // TRACE macro to output the debug messages.
        if (Smc.isNoStreams() == false)
        {
            _source.println();
            _source.println("#define SMC_USES_IOSTREAMS");
        }

        // If this application is *not* using exceptions, then
        // define SMC_NO_EXCEPTIONS.
        if (Smc.isNoExceptions() == true)
        {
            _source.println();
            _source.println("#define SMC_NO_EXCEPTIONS");
        }

        // Include required standard .h files.
        _source.println();
        _source.println("#include <statemap.h>");

        _source.println();

        // If a namespace was specified, then output that
        // namespace now. If the package name is "a::b::c", then
        // this must be converted to:
        // namespace a {
        //   namespace b {
        //     namespace c {
        //       ...
        //     }
        //   }
        // }
        _indent = "";
        if (packageName != null && packageName.length() > 0)
        {
            StringTokenizer tokenizer =
                new StringTokenizer(packageName, "::");
            String token;

            while (tokenizer.hasMoreTokens() == true)
            {
                token = tokenizer.nextToken();
                ++packageDepth;

                _source.print(_indent);
                _source.print("namespace ");
                _source.println(token);
                _source.print(_indent);
                _source.println("{");
                _indent += "    ";
            }
        }

        // Forward declare all the state classes in all the maps.
        _source.print(_indent);
        _source.println("// Forward declarations.");
        for (mapIt = fsm.getMaps().iterator();
             mapIt.hasNext() == true;
            )
        {
            map = ((SmcMap) mapIt.next());
            mapName = map.getName();

            // class <map name>;
            _source.print(_indent);
            _source.print("class ");
            _source.print(mapName);
            _source.println(";");

            // Iterate over the map's states.
            for (it = map.getStates().iterator();
                 it.hasNext() == true;
                )
            {
                state = (SmcState) it.next();

                _source.print(_indent);
                _source.print("class ");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.println(";");
            }

            // Forward declare the default state as well.
            _source.print(_indent);
            _source.print("class ");
            _source.print(mapName);
            _source.println("_Default;");
        }

        // Forward declare the state class and its
        // context as well.
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("State;");
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("Context;");

        // Forward declare the application class.
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println(";");

        // Do user-specified forward declarations now.
        for (it = fsm.getDeclarations().iterator();
             it.hasNext() == true;
            )
        {
            declaration = (String) it.next();
            _source.print(_indent);
            _source.print(declaration);

            // Add a semicolon if the user did not use one.
            if (declaration.endsWith(";") == false)
            {
                _source.print(";");
            }

            _source.println();
        }
        _source.println();

        // Declare user's base state class.
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("State :");
        _source.print(_indent);
        _source.println("    public statemap::State");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("public:");
        _source.println();

        // Constructor.
        _source.print(_indent);
        _source.print("    ");
        _source.print(context);
        _source.println("State(const char *name, int stateId)");
        _source.print(_indent);
        _source.println("    : statemap::State(name, stateId)");
        _source.print(_indent);
        _source.println("    {};");
        _source.println();

        // Add the default Entry() and Exit() definitions.
        _source.print(_indent);
        _source.print("    virtual void Entry(");
        _source.print(context);
        _source.println("Context&) {};");
        _source.print(_indent);
        _source.print("    virtual void Exit(");
        _source.print(context);
        _source.println("Context&) {};");
        _source.println();

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        for (mapIt = fsm.getMaps().iterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Merge the new transitions into the current set.
            transList =
                    Smc.merge(
                        map.getTransitions(),
                        transList,
                        new Comparator() {
                            public int compare(Object o1,
                                               Object o2)
                            {
                                return (
                                    ((SmcTransition)
                                     o1).compareTo(
                                         (SmcTransition) o2));
                            }
                        });
        }

        // Output the global transition declarations.
        for (transIt = transList.iterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            // Don't output the default state here.
            if (trans.getName().equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("    virtual void ");
                _source.print(trans.getName());
                _source.print("(");
                _source.print(context);
                _source.print("Context& context");

                params = trans.getParameters();
                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    param = (SmcParameter) pit.next();

                    _source.print(", ");
                    param.accept(this);
                }

                _source.println(");");
            }
        }

        // Declare the global Default transition.
        _source.println();
        _source.print(_indent);
        _source.println("protected:");
        _source.println();
        _source.print(_indent);
        _source.print("    virtual void Default(");
        _source.print(context);
        _source.println("Context& context);");

        // The base class has been defined.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        // Generate the map classes. The maps will, in turn,
        // generate the state classes.
        for (mapIt = fsm.getMaps().iterator();
             mapIt.hasNext() == true;
            )
        {
            ((SmcMap) mapIt.next()).accept(this);
        }

        // Generate the FSM context class.
        // class FooContext :
        //     public statemap::FSMContext
        // {
        // public:
        //     FOOContext(FOO& owner)
        //     
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("Context :");
        _source.print(_indent);
        _source.println("    public statemap::FSMContext");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("public:");
        _source.println();
        _source.print(_indent);
        _source.print("    ");
        _source.print(context);
        _source.print("Context(");
        _source.print(context);
        _source.println("& owner)");
        _source.print(_indent);
        _source.println("    : _owner(owner)");
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.print("        setState(");
        _source.print(fsm.getStartState());
        _source.println(");");
        _source.print(_indent);
        _source.print("        ");
        _source.print(fsm.getStartState());
        _source.println(".Entry(*this);");
        _source.print(_indent);
        _source.println("    };");
        _source.println();
        _source.print(_indent);
        _source.print("    ");
        _source.print(context);
        _source.println("& getOwner() const");
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.println("        return (_owner);");
        _source.print(_indent);
        _source.println("    };");
        _source.println();
        _source.print(_indent);
        _source.print("    ");
        _source.print(context);
        _source.println("State& getState() const");
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.println("        if (_state == NULL)");
        _source.print(_indent);
        _source.println("        {");

        // v. 1.3.1: If -noex was specified, then don't throw
        // exceptions.
        if (Smc.isNoExceptions() == false)
        {
            _source.print(_indent);
            _source.print("            throw ");
            _source.println(
                "statemap::StateUndefinedException();");
        }
        else
        {
            _source.print(_indent);
            _source.println("            assert(1 == 0);");
        }

        _source.print(_indent);
        _source.println("        }");
        _source.println();
        _source.print(_indent);
        _source.print("        return (");
        _source.print(Smc.getCastType());
        _source.print("<");
        _source.print(context);
        _source.println("State&>(*_state));");
        _source.print(_indent);
        _source.println("    };");

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (transIt = transList.iterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print(_indent);
                _source.print("    void ");
                _source.print(trans.getName());
                _source.print("(");

                params = trans.getParameters();
                for (pit = params.iterator(),
                       separator = "";
                     pit.hasNext() == true;
                     separator = ", ")
                {
                    param = (SmcParameter) pit.next();

                    _source.print(separator);
                    param.accept(this);
                }
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");

                // If -g was specified, then set the transition
                // name so it can be printed out.
                if (Smc.isDebug() == true)
                {
                    _source.print(_indent);
                    _source.print("        setTransition(\"");
                    _source.print(trans.getName());
                    _source.println("\");");
                }

                _source.print(_indent);
                _source.print("        (getState()).");
                _source.print(trans.getName());
                _source.print("(*this");
                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    param = (SmcParameter) pit.next();

                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(");");

                if (Smc.isDebug() == true)
                {
                    _source.print(_indent);
                    _source.println(
                        "        setTransition(NULL);");
                }

                _source.print(_indent);
                _source.println("    };");
            }
        }

        // v. 2.2.0: If we are supporting serialization, then
        // declare the valueOf static method.
        if (Smc.isSerial() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.print("    static ");
            _source.print(context);
            _source.println("State& valueOf(int stateId);");
        }

        // Member data.
        _source.println();
        _source.print(_indent);
        _source.println("private:");
        _source.println();
        _source.print(_indent);
        _source.print("    ");
        _source.print(context);
        _source.println("& _owner;");

        // v. 2.2.0: If we are supporting serialization, then
        // declare the min and max indices.
        if (Smc.isSerial() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println("private:");
            _source.println();
            _source.print(_indent);
            _source.println("    const static int MIN_INDEX;");
            _source.print(_indent);
            _source.println("    const static int MAX_INDEX;");
            _source.print(_indent);
            _source.print("    static ");
            _source.print(context);
            _source.println("State* _States[];");
        }

        // Put the closing brace on the context class.
        _source.print(_indent);
        _source.println("};");

        // If necessary, place an end brace for the namespace.
        if (packageName != null && packageName.length() > 0)
        {
            int i;
            int j;

            for (i = (packageDepth - 1); i >= 0; --i)
            {
                // Output the proper indent.
                for (j = 0; j < i; ++j)
                {
                    _source.print("    ");
                }

                _source.println("};");
                _source.println();
            }
        }
        else
        {
            _source.println();
        }

        _source.println("#endif");

        return;
    }

    // Generate the map class declaration and then the state
    // classes:
    //
    // class <map name>
    // {
    // public:
    //
    //     static <map name>_<state name> <state name>;
    // };
    public void visit(SmcMap map)
    {
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        Iterator it;
        SmcState state;
        String stateName;

        // Forward declare the map.
        _source.print(_indent);
        _source.print("class ");
        _source.println(mapName);
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("public:");
        _source.println();

        // Iterate over the map's states and declare the static,
        // singleton state instances
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            state = (SmcState) it.next();
            stateName = state.getClassName();

            _source.print(_indent);
            _source.print("    static ");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print(" ");
            _source.print(stateName);
            _source.println(";");
        }

        // The map class is now defined.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        // Declare the map's default state class.
        //
        // class <map name>_Default :
        //     public <context>State
        // {
        // public:
        //
        //     <map name>_Default(const char *name, int stateId)
        //     : <context>State(name, stateId)
        //     {};
        //
        //     (user-defined Default state transitions.)
        // };
        _source.print(_indent);
        _source.print("class ");
        _source.print(mapName);
        _source.println("_Default :");
        _source.print(_indent);
        _source.print("    public ");
        _source.print(context);
        _source.println("State");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("public:");
        _source.println();

        // Default state's constructor.
        _source.print(_indent);
        _source.print("    ");
        _source.print(mapName);
        _source.println("_Default(const char *name, int stateId)");
        _source.print(_indent);
        _source.print("    : ");
        _source.print(context);
        _source.println("State(name, stateId)");
        _source.print(_indent);
        _source.println("    {};");
        _source.println();

        // Declare the user-defined default transitions first.
        if (map.hasDefaultState() == true)
        {
            SmcState defaultState = map.getDefaultState();

            for (it = defaultState.getTransitions().iterator();
                 it.hasNext() == true;
                )
            {
                ((SmcTransition) it.next()).accept(this);
            }
        }

        // The map's default state class is now defined.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        // Now output the state class declarations.
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcState) it.next()).accept(this);
        }

        return;
    }

    // Generate the state's class declaration.
    //
    // class <map name>_<state name> :
    //     public <map name>_Default
    // {
    // public:
    //
    //     <map name>_<state name>(const char *name, int stateId)
    //     : <map name>_Default(name, stateId)
    //     {};
    //
    //     (declare the transition methods.)
    //     void <transition name>(<context>& context, <args>);
    // };
    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        List actions;
        Iterator it;

        _source.print(_indent);
        _source.print("class ");
        _source.print(mapName);
        _source.print('_');
        _source.print(stateName);
        _source.println(" :");
        _source.print(_indent);
        _source.print("    public ");
        _source.print(mapName);
        _source.println("_Default");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.print("public:");
        _source.println();
        _source.print(_indent);
        _source.print("    ");
        _source.print(mapName);
        _source.print('_');
        _source.print(stateName);
        _source.println("(const char *name, int stateId)");
        _source.print(_indent);
        _source.print("    : ");
        _source.print(mapName);
        _source.println("_Default(name, stateId)");
        _source.print(_indent);
        _source.println("    {};");
        _source.println();

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.print(_indent);
            _source.print("    void Entry(");
            _source.print(context);
            _source.println("Context&);");
        }

        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.print(_indent);
            _source.print("    void Exit(");
            _source.print(context);
            _source.println("Context&);");
        }

        // Now generate the transition methods.
        for (it = state.getTransitions().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }

        // End of the state class declaration.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        return;
    }

    // Generate the transition method declaration.
    //
    // void <transition name>(<context>Context& context, <args>);
    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        String stateName = state.getClassName();
        String virtual = "";
        Iterator pit;

        // If this transition is in the default state, then
        // precede the method with "virtual".
        if (stateName.equals("Default") == true)
        {
            virtual = "virtual ";
        }

        _source.print(_indent);
        _source.print("    ");
        _source.print(virtual);
        _source.print("void ");
        _source.print(transition.getName());
        _source.print("(");
        _source.print(
            state.getMap().getFSM().getContext());
        _source.print("Context& context");

        // Add user-defined parameters.
        for (pit = transition.getParameters().iterator();
             pit.hasNext() == true;
            )
        {
            _source.print(", ");
            ((SmcParameter) pit.next()).accept(this);
        }

        // End of transition method declaration.
        _source.println(");");

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(" ");
        _source.print(parameter.getName());

        return;
    }

//-----------------------------------------------------------------
// Member data
//
}

//
// CHANGE LOG
// $Log$
// Revision 1.2  2005/06/08 11:09:15  cwrapp
// + Updated Python code generator to place "pass" in methods with empty
//   bodies.
// + Corrected FSM errors in Python example 7.
// + Removed unnecessary includes from C++ examples.
// + Corrected errors in top-level makefile's distribution build.
//
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.1  2005/02/21 15:35:32  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.0  2005/02/03 17:11:12  charlesr
// Initial revision
//
