//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy
// of the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an
// "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// The Original Code is State Machine Compiler (SMC).
//
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2005, 2008. Charles W. Rapp.
// All Rights Reserved.
//
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python.
//   Chris Liscio contributed the Objective-C code generation
//   and examples/ObjC.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc.generator;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import net.sf.smc.model.SmcAction;
import net.sf.smc.model.SmcElement;
import net.sf.smc.model.SmcFSM;
import net.sf.smc.model.SmcGuard;
import net.sf.smc.model.SmcMap;
import net.sf.smc.model.SmcParameter;
import net.sf.smc.model.SmcState;
import net.sf.smc.model.SmcTransition;
import net.sf.smc.model.SmcVisitor;

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
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a C++ header code generator for the given
     * parameters.
     * @param srcfileBase write the emitted code to this target
     * source file name sans the suffix.
     * @param srcDirectory place the target source file in this
     * directory.
     * @param headerDirectory place the target header file in
     * this directory. Ignored if there is no generated header
     * file.
     * @param castType use this type cast (C++ code generation
     * only).
     * @param graphLevel amount of detail in the generated
     * GraphViz graph (graph code generation only).
     * @param serialFlag if {@code true}, generate unique
     * identifiers for persisting the FSM.
     * @param debugFlag if {@code true} add debug output messages
     * to code.
     * @param noExceptionFlag if {@code true} then use asserts
     * rather than exceptions (C++ only).
     * @param noCatchFlag if {@code true} then do <i>not</i>
     * generate try/catch/rethrow code.
     * @param noStreamsFlag if {@code true} then use TRACE macro
     * for debug output.
     * @param reflectFlag if {@code true} then generate
     * reflection code.
     * @param syncFlag if {@code true} then generate
     * synchronization code.
     * @param genericFlag if {@code true} then use generic
     * collections.
     */
    public SmcHeaderGenerator(String srcfileBase,
                              String srcDirectory,
                              String headerDirectory,
                              String castType,
                              int graphLevel,
                              boolean serialFlag,
                              boolean debugFlag,
                              boolean noExceptionFlag,
                              boolean noCatchFlag,
                              boolean noStreamsFlag,
                              boolean reflectFlag,
                              boolean syncFlag,
                              boolean genericFlag)
    {
        super (srcfileBase,
               "{0}{1}_sm.{2}",
               "h",
               srcDirectory,
               headerDirectory,
               castType,
               graphLevel,
               serialFlag,
               debugFlag,
               noExceptionFlag,
               noCatchFlag,
               noStreamsFlag,
               reflectFlag,
               syncFlag,
               genericFlag);
    } // end of SmcHeaderGenerator(...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits C++ header code for the finite state machine.
     * @param fsm emit C=+ header code for this finite state
     * machine.
     */
    public void visit(SmcFSM fsm)
    {
        String srcfileCaps;
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String mapName;
        List<SmcTransition> transList;
        String separator;
        List<SmcParameter> params;
        Iterator<SmcParameter> pit;
        int packageDepth = 0;

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
        srcfileCaps = _srcfileBase.replace('\\', '_');
        srcfileCaps = srcfileCaps.replace('/', '_');
        srcfileCaps = srcfileCaps.toUpperCase();
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
        if (_noStreamsFlag == false)
        {
            _source.println();
            _source.println("#define SMC_USES_IOSTREAMS");
        }

        // If this application is *not* using exceptions, then
        // define SMC_NO_EXCEPTIONS.
        if (_noExceptionFlag == true)
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
        for (SmcMap map: fsm.getMaps())
        {
            mapName = map.getName();

            // class <map name>;
            _source.print(_indent);
            _source.print("class ");
            _source.print(mapName);
            _source.println(";");

            // Iterate over the map's states.
            for (SmcState state: map.getStates())
            {
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
        for (String declaration: fsm.getDeclarations())
        {
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
        transList = fsm.getTransitions();

        // Output the global transition declarations.
        for (SmcTransition trans: transList)
        {
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
                for (SmcParameter param: params)
                {
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
        for (SmcMap map: fsm.getMaps())
        {
            map.accept(this);
        }

        // Generate the FSM context class.
        // class FooContext :
        //     public statemap::FSMContext
        // {
        // public:
        //     FOOContext(FOO& owner)
        //     virtual void enterStartState()
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
        _source.print("    : FSMContext(");
        _source.print(fsm.getStartState());
        _source.println("),");
        _source.print(_indent);
        _source.println("      _owner(owner)");
        _source.print(_indent);
        _source.println("    {};");
        _source.println();
        _source.print(_indent);
        _source.println("    virtual void enterStartState()");
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.println("        getState().Entry(*this);");
        _source.print(_indent);
        _source.println("        return;");
        _source.println("    }");
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
        if (_noExceptionFlag == false)
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
        _source.print(_castType);
        _source.print("<");
        _source.print(context);
        _source.println("State&>(*_state));");
        _source.print(_indent);
        _source.println("    };");

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (SmcTransition trans: transList)
        {
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
                    _source.print(separator);
                    (pit.next()).accept(this);
                }
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");

                // If -g was specified, then set the transition
                // name so it can be printed out.
                if (_debugFlag == true)
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
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(");");

                if (_debugFlag == true)
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
        if (_serialFlag == true)
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
        if (_serialFlag == true)
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

                _source.println("}");
                _source.println();
            }
        }
        else
        {
            _source.println();
        }

        _source.print("#endif // _H_");
        _source.print(srcfileCaps);
        _source.println("_SM");

        return;
    } // end of visit(SmcFSM)

    /**
     * Generates the map class declaration and then the state
     * classes:
     * <code>
     *   <pre>
     * class <i>map name</i>
     * {
     * public:
     *
     *     static <i>map name</i>_<i>state name</i> <i>state name</i>;
     * };
     *   </pre>
     * </code>
     * @param map emit C++ header code for this map.
     */
    // 
    //
    public void visit(SmcMap map)
    {
        String context = map.getFSM().getContext();
        String mapName = map.getName();
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
        for (SmcState state: map.getStates())
        {
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
        _source.println(
            "_Default(const char *name, int stateId)");
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

            for (SmcTransition transition:
                     defaultState.getTransitions())
            {
                transition.accept(this);
            }
        }

        // The map's default state class is now defined.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        // Now output the state class declarations.
        for (SmcState state: map.getStates())
        {
            state.accept(this);
        }

        return;
    } // end of visit(SmcMap)

    /**
     * Generates the state's class declaration:
     * <code>
     *   <pre>
     * class <i>map name</i>_<i>state name</i> :
     *     public <i>map name</i>_Default
     * {
     * public:
     *
     *     <i>map name</i>_<i>state name</i>(const char *name, int stateId)
     *     : <i>map name</i>_Default(name, stateId)
     *     {};
     *
     *     (declare the transition methods.)
     *     void <i>transition name</i>(<i>context</i>& context, <i>args</i>);
     * };
     *   </pre>
     * </code>
     * @param state emits C++ header code for this state.
     */
    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        List<SmcAction> actions;

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
        for (SmcTransition transition: state.getTransitions())
        {
            transition.accept(this);
        }

        // End of the state class declaration.
        _source.print(_indent);
        _source.println("};");
        _source.println();

        return;
    } // end of visit(SmcState)

    /**
     * Generates the transition method declaration:
     * <code>
     *   <pre>
     * void <i>transition name</i>(<i>context</i>Context&amp; context, <i>args</i>);
     *   </pre>
     * </code>
     * @param transition emits C++ header code for this state
     * transition.
     */
    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        String stateName = state.getClassName();
        String virtual = "";

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
        for (SmcParameter param: transition.getParameters())
        {
            _source.print(", ");
            param.accept(this);
        }

        // End of transition method declaration.
        _source.println(");");

        return;
    } // end of visit(SmcTransition)

    /**
     * Emits C++ header code for this transition parameter.
     * @param parameter emits C++ header code for this transition
     * parameter.
     */
    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(" ");
        _source.print(parameter.getName());

        return;
    } // end of visit(SmcParameter)

    //
    // end of SmcVisitor Abstract Method Impelementation.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member data
//
} // end of class SmcHeaderGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.9  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.8  2007/02/21 13:55:20  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.7  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.6  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.5  2006/07/11 18:14:38  cwrapp
// Changed method getCastType() to castType().
//
// Revision 1.4  2005/11/07 19:34:54  cwrapp
// Changes in release 4.3.0:
// New features:
//
// + Added -reflect option for Java, C#, VB.Net and Tcl code
//   generation. When used, allows applications to query a state
//   about its supported transitions. Returns a list of transition
//   names. This feature is useful to GUI developers who want to
//   enable/disable features based on the current state. See
//   Programmer's Manual section 11: On Reflection for more
//   information.
//
// + Updated LICENSE.txt with a missing final paragraph which allows
//   MPL 1.1 covered code to work with the GNU GPL.
//
// + Added a Maven plug-in and an ant task to a new tools directory.
//   Added Eiten Suez's SMC tutorial (in PDF) to a new docs
//   directory.
//
// Fixed the following bugs:
//
// + (GraphViz) DOT file generation did not properly escape
//   double quotes appearing in transition guards. This has been
//   corrected.
//
// + A note: the SMC FAQ incorrectly stated that C/C++ generated
//   code is thread safe. This is wrong. C/C++ generated is
//   certainly *not* thread safe. Multi-threaded C/C++ applications
//   are required to synchronize access to the FSM to allow for
//   correct performance.
//
// + (Java) The generated getState() method is now public.
//
// Revision 1.3  2005/06/18 18:28:42  cwrapp
// SMC v. 4.0.1
//
// New Features:
//
// (No new features.)
//
// Bug Fixes:
//
// + (C++) When the .sm is in a subdirectory the forward- or
//   backslashes in the file name are kept in the "#ifndef" in the
//   generated header file. This is syntactically wrong. SMC now
//   replaces the slashes with underscores.
//
// + (Java) If %package is specified in the .sm file, then the
//   generated *Context.java class will have package-level access.
//
// + The Programmer's Manual had incorrect HTML which prevented the
//   pages from rendering correctly on Internet Explorer.
//
// + Rewrote the Programmer's Manual section 1 to make it more
//   useful.
//
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
