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
// Copyright (C) 2005 - 2009. Charles W. Rapp.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.smc.model.SmcAction;
import net.sf.smc.model.SmcElement;
import net.sf.smc.model.SmcElement.TransType;
import net.sf.smc.model.SmcFSM;
import net.sf.smc.model.SmcGuard;
import net.sf.smc.model.SmcMap;
import net.sf.smc.model.SmcParameter;
import net.sf.smc.model.SmcState;
import net.sf.smc.model.SmcTransition;
import net.sf.smc.model.SmcVisitor;

/**
 * Visits the abstract syntax tree, emitting Java code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcJavaGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a Java code generator for the given parameters.
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
    public SmcJavaGenerator(String srcfileBase,
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
               "{0}{1}Context.{2}",
               "java",
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
    } // end of SmcJavaGenerator(...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits Java code for the finite state machine.
     * @param fsm emit Java code for this finite state machine.
     */
    public void visit(SmcFSM fsm)
    {
        String rawSource = fsm.getSource();
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String startState = fsm.getStartState();
        String accessLevel = fsm.getAccessLevel();
        List<SmcMap> maps = fsm.getMaps();
        List<SmcTransition> transitions;
        Iterator<SmcParameter> pit;
        String transName;
        String javaState;
        String separator;
        int index;
        List<SmcParameter> params;

        // If the access level has not been set, then the default
        // is "public".
        if (accessLevel == null || accessLevel.length() == 0)
        {
            accessLevel = "public";
        }
        // If the access level is package, change it to
        // /* package */
        else if (accessLevel.equals("package") == true)
        {
            accessLevel = "/* package */";
        }

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length() > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // If a package has been specified, generate the
        // package statement now.
        if (packageName != null && packageName.length() > 0)
        {
            _source.print("package ");
            _source.print(packageName);
            _source.println(";");
            _source.println();
        }

        // Do user-specified imports now.
        for (String imp: fsm.getImports())
        {
            _source.print("import ");
            _source.print(imp);
            _source.println(";");
        }

        // If the -g option was specified, then import the
        // PrintStream class.
        if (_debugFlag == true)
        {
            _source.println("import java.io.PrintStream;");
        }

        if (_reflectFlag == true)
        {
            _source.println("import java.util.HashMap;");
            _source.println("import java.util.Map;");
        }

        _source.println();

        // The context clas contains all the state classes as
        // inner classes, so generate the context first rather
        // than last.
        _source.print(accessLevel);
//         _source.print(" final class ");
        _source.print(" class ");
        _source.print(context);
        _source.println("Context");
        _source.println("    extends statemap.FSMContext");

        if (_serialFlag == true)
        {
            _source.println(
                "    implements java.io.Serializable");
        }

        _source.println("{");
        _source.println(
            "//---------------------------------------------------------------");
        _source.println("// Member methods.");
        _source.println("//");
        _source.println();

        // The state name "map::state" must be changed to
        // "map.state".
        if ((index = startState.indexOf("::")) >= 0)
        {
            javaState =
                startState.substring(0, index) +
                "." +
                startState.substring(index + 2);
        }
        else
        {
            javaState = startState;
        }

        // Generate the context class' constructor.
        _source.print("    public ");
        _source.print(context);
        _source.print("Context(");
        _source.print(context);
        _source.println(" owner)");
        _source.println("    {");
        _source.print("        super (");
        _source.print(javaState);
        _source.println(");");
        _source.println();
        _source.println("        _owner = owner;");
        _source.println("    }");
        _source.println();

        // Generate the second constructor which allows the
        // initial state to be dynamically set. Overrides the
        // %start specifier.
        _source.print("    public ");
        _source.print(context);
        _source.print("Context(");
        _source.print(context);
        _source.print(" owner, ");
        _source.print(context);
        _source.println("State initState)");
        _source.println("    {");
        _source.println("        super (initState);");
        _source.println();
        _source.println("        _owner = owner;");
        _source.println("    }");
        _source.println();

        // Generate the start abstract method which requires
        // executes the initial state's entry actions.
        _source.println("    public void enterStartState()");
        _source.println("    {");
        _source.println("        getState().Entry(this);");
        _source.println("        return;");
        _source.println("    }");
        _source.println();

        // Generate the default transition methods.
        // Get the transition list for the entire FSM.
        transitions = fsm.getTransitions();

        // Generate the transition methods.
        for (SmcTransition trans: transitions)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.print("    public ");

                // If the -sync flag was specified, then output
                // the "synchronized" keyword.
                if (_syncFlag == true)
                {
                    _source.print("synchronized ");
                }

                _source.print("void ");
                _source.print(trans.getName());
                _source.print("(");

                params = trans.getParameters();
                for (pit = params.iterator(), separator = "";
                     pit.hasNext() == true;
                     separator = ", ")
                {
                    _source.print(separator);
                    (pit.next()).accept(this);
                }
                _source.println(")");
                _source.println("    {");

                // Save away the transition name in case it is
                // need in an UndefinedTransitionException.
                _source.print("        _transition = \"");
                _source.print(trans.getName());
                _source.println("\";");

                _source.print("        getState().");
                _source.print(trans.getName());
                _source.print("(this");

                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    _source.print(", ");
                    _source.print((pit.next()).getName());
                }
                _source.println(");");
                _source.println("        _transition = \"\";");

                _source.println("        return;");
                _source.println("    }");
                _source.println();
            }
        }

        // If serialization is turned on, then generate a
        // setOwner method which allows the application class
        // to restore its ownership of the FSM.
        if (_serialFlag == true)
        {
            // Also output the valueOf method in case developers
            // want to serialize manually.
            _source.print("    public ");
            _source.print(context);
            _source.println("State valueOf(int stateId)");
            _source.println(
                "        throws ArrayIndexOutOfBoundsException");
            _source.println("    {");
            _source.println(
                "        return (_States[stateId]);");
            _source.println("    }");
            _source.println();
        }

        // getState() method.
        _source.print("    public ");
        _source.print(context);
        _source.println("State getState()");
        _source.println(
            "        throws statemap.StateUndefinedException");
        _source.println("    {");
        _source.println("        if (_state == null)");
        _source.println("        {");
        _source.println(
            "            throw(");
        _source.println(
            "                new statemap.StateUndefinedException());");
        _source.println("        }");
        _source.println();
        _source.print("        return ((");
        _source.print(context);
        _source.println("State) _state);");
        _source.println("    }");
        _source.println();

        // getOwner() method.
        _source.print("    protected ");
        _source.print(context);
        _source.println(" getOwner()");
        _source.println("    {");
        _source.println("        return (_owner);");
        _source.println("    }");
        _source.println();

        // setOwner() method.
        _source.print("    public void setOwner(");
        _source.print(context);
        _source.println(" owner)");
        _source.println("    {");
        _source.println("        if (owner == null)");
        _source.println("        {");
        _source.println("            throw (");
        _source.println("                new NullPointerException(");
        _source.println("                    \"null owner\"));");
        _source.println("        }");
        _source.println("        else");
        _source.println("        {");
        _source.println("            _owner = owner;");
        _source.println("        }");
        _source.println();
        _source.println("        return;");
        _source.println("    }");
        _source.println();

        // If serialization is turned on, then output the
        // writeObject and readObject methods.
        if (_serialFlag == true)
        {
            _source.print(
                "    private void writeObject(");
            _source.println(
                "java.io.ObjectOutputStream ostream)");
            _source.println(
                "        throws java.io.IOException");
            _source.println("    {");
            _source.println(
                "        int size =");
            _source.print("            ");
            _source.println(
                "(_stateStack == null ? 0 : _stateStack.size());");
            _source.println("        int i;");
            _source.println();
            _source.println(
                "        ostream.writeInt(size);");
            _source.println();
            _source.println(
                "        for (i = 0; i < size; ++i)");
            _source.println("        {");
            _source.println("            ostream.writeInt(");
            _source.print("                ((");
            _source.print(context);
            _source.println(
                "State) _stateStack.get(i)).getId());");
            _source.println("        }");
            _source.println();
            _source.println(
                "        ostream.writeInt(_state.getId());");
            _source.println();
            _source.println("        return;");
            _source.println("    }");
            _source.println();
            _source.print("    private void readObject(");
            _source.println(
                "java.io.ObjectInputStream istream)");
            _source.println(
                "        throws java.io.IOException");
            _source.println("    {");
            _source.println("        int size;");
            _source.println();
            _source.println("        size = istream.readInt();");
            _source.println();
            _source.println("        if (size == 0)");
            _source.println("        {");
            _source.println("            _stateStack = null;");
            _source.println("        }");
            _source.println("        else");
            _source.println("        {");
            _source.println("            int i;");
            _source.println();
            _source.println(
                "            _stateStack = new java.util.Stack();");
            _source.println();
            _source.println(
                "            for (i = 0; i < size; ++i)");
            _source.println("            {");
            _source.print(
                "                _stateStack.add(i, _States[");
            _source.println("istream.readInt()]);");
            _source.println("            }");
            _source.println("        }");
            _source.println();
            _source.println(
                "        _state = _States[istream.readInt()];");
            _source.println();
            _source.println("        return;");
            _source.println("    }");
            _source.println();
        }

        // Declare member data.
        _source.println(
            "//---------------------------------------------------------------");
        _source.println("// Member data.");
        _source.println("//");
        _source.println();
        _source.print("    transient private ");
        _source.print(context);
        _source.println(" _owner;");

        // If serialization support is on, then create the state
        // array.
        if (_serialFlag == true)
        {
            Iterator<SmcMap> mit;
            SmcMap map;
            String mapName;
            Iterator<SmcState> stateIt;
            SmcState state;

            _source.print("    transient private static ");
            _source.print(context);
            _source.println("State[] _States =");
            _source.println("    {");

            for (mit = maps.iterator(), separator = "";
                 mit.hasNext() == true;
                )
            {
                map = mit.next();
                mapName = map.getName();

                for (stateIt = map.getStates().iterator();
                     stateIt.hasNext() == true;
                     separator = ",\n")
                {
                    state = stateIt.next();
                    _source.print(separator);
                    _source.print("        ");
                    _source.print(mapName);
                    _source.print(".");
                    _source.print(state.getClassName());
                }
            }

            _source.println();
            _source.println("    };");
        }

        // Declare the inner state class.
        _source.println();
        _source.println(
            "//---------------------------------------------------------------");
        _source.println("// Inner classes.");
        _source.println("//");
        _source.println();
        _source.print("    public static abstract class ");
        _source.print(context);
        _source.println("State");
        _source.println("        extends statemap.State");
        _source.println("    {");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member methods.");
        _source.println("    //");
        _source.println();

        // Abstract method to obtain the a state's supported
        // transitions.
        if (_reflectFlag == true)
        {
            _source.print("        ");
            _source.print("public abstract Map");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println(" getTransitions();");
            _source.println();
        }

        // Constructor.
        _source.print("        protected ");
        _source.print(context);
        _source.println("State(String name, int id)");
        _source.println("        {");
        _source.println("            super (name, id);");
        _source.println("        }");
        _source.println();
        _source.print("        protected void Entry(");
        _source.print(context);
        _source.println("Context context) {}");
        _source.print("        protected void Exit(");
        _source.print(context);
        _source.println("Context context) {}");
        _source.println();

        // Generate the default transition definitions.
        for (SmcTransition trans: transitions)
        {
            transName = trans.getName();

            // Don't generate the Default transition here.
            if (transName.equals("Default") == false)
            {
                _source.print("        protected void ");
                _source.print(transName);
                _source.print("(");
                _source.print(context);
                _source.print("Context context");

                for (SmcParameter param: trans.getParameters())
                {
                    _source.print(", ");
                    param.accept(this);
                }

                _source.println(")");
                _source.println("        {");

                // If this method is reached, that means that
                // this transition was passed to a state which
                // does not define the transition. Call the
                // state's default transition method.
                _source.println("            Default(context);");

                _source.println("        }");
                _source.println();
            }
        }

        // Generate the overall Default transition for all maps.
        _source.print("        protected void Default(");
        _source.print(context);
        _source.println("Context context)");
        _source.println("        {");

        if (_debugFlag == true)
        {
            _source.println(
                "            if (context.getDebugFlag() == true)");
            _source.println("            {");
            _source.println(
                "                PrintStream str = ");
            _source.println(
                "                    context.getDebugStream();");
            _source.println();
            _source.println(
                "                str.println(");
            _source.println(
                "                    \"TRANSITION   : Default\");");
            _source.println("            }");
            _source.println();
        }

        _source.println("            throw (");
        _source.println(
            "                new statemap.TransitionUndefinedException(");
        _source.println(
            "                    \"State: \" +");
        _source.println(
            "                    context.getState().getName() +");
        _source.println(
            "                    \", Transition: \" +");
        _source.println(
            "                    context.getTransition()));");
        _source.println("        }");

        // End of state class.
        _source.println();
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member data.");
        _source.println("    //");
        _source.println("    }");

        // Have each map print out its source code now.
        for (SmcMap map: maps)
        {
            map.accept(this);
        }

        // End of context class.
        _source.println("}");

        return;
    } // end of visit(SmcFSM)

    /**
     * Emits Java code for the FSM map.
     * @param map emit Java code for this map.
     */
    public void visit(SmcMap map)
    {
        List<SmcTransition> definedDefaultTransitions;
        SmcState defaultState = map.getDefaultState();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        List<SmcState> states = map.getStates();

        // Initialize the default transition list to all the
        // default state's transitions.
        if (defaultState != null)
        {
            definedDefaultTransitions =
                defaultState.getTransitions();
        }
        else
        {
            definedDefaultTransitions =
                new ArrayList<SmcTransition>();
        }

        // Declare the map class. Declare it abstract to prevent
        // its instantiation.
        _source.println();
        _source.print(
            "    /* package */ static abstract class ");
        _source.println(mapName);
        _source.println("    {");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member methods.");
        _source.println("    //");
        _source.println();
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member data.");
        _source.println("    //");
        _source.println();
        _source.println(
            "        //-------------------------------------------------------");
        _source.println("        // Constants.");
        _source.println("        //");

        // Declare each of the state class member data.
        for (SmcState state: states)
        {
            _source.print("        public static final ");
            _source.print(mapName);
            _source.print("_Default.");
            _source.print(mapName);
            _source.print('_');
            _source.print(state.getClassName());
            _source.print(' ');
            _source.print(state.getInstanceName());
            _source.println(" =");
            _source.print("            new ");
            _source.print(mapName);
            _source.print("_Default.");
            _source.print(mapName);
            _source.print('_');
            _source.print(state.getClassName());
            _source.print("(\"");
            _source.print(mapName);
            _source.print('.');
            _source.print(state.getClassName());
            _source.print("\", ");
            _source.print(map.getNextStateId());
            _source.println(");");
        }

        // Create a default state as well.
        _source.print("        private static final ");
        _source.print(mapName);
        _source.println("_Default Default =");
        _source.print("            new ");
        _source.print(mapName);
        _source.print("_Default(\"");
        _source.print(mapName);
        _source.println(".Default\", -1);");
        _source.println();

        // End of the map class.
        _source.println("    }");
        _source.println();

        // Declare the map default state class.
        _source.print("    protected static class ");
        _source.print(mapName);
        _source.println("_Default");
        _source.print("        extends ");
        _source.print(context);
        _source.println("State");
        _source.println("    {");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member methods.");
        _source.println("    //");
        _source.println();

        // If -reflect was specified, then output the
        // getTransitions() abstract method.
        if (_reflectFlag == true)
        {
            _source.print("        ");
            _source.print("public Map");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println(" getTransitions()");
            _source.println("        {");
            _source.println(
                "            return (_transitions);");
            _source.println("        }");
            _source.println();
        }

        // Generate the constructor.
        _source.print("        protected ");
        _source.print(mapName);
        _source.println("_Default(String name, int id)");
        _source.println("        {");
        _source.println("            super (name, id);");
        _source.println("        }");

        // Declare the user-defined default transitions first.
        _indent = "        ";
        for (SmcTransition trans: definedDefaultTransitions)
        {
            trans.accept(this);
        }

        _source.println();
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Inner classse.");
        _source.println("    //");
        _source.println();

        // Have each state now generate its code. Each state
        // class is an inner class.
        for (SmcState state: states)
        {
            state.accept(this);
        }

        _source.println();
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Member data.");
        _source.println("    //");

        // If -reflect was specified, then generate the
        // _transitions map.
        if (_reflectFlag == true)
        {
            List<SmcTransition> allTransitions =
                map.getFSM().getTransitions();
            String transName;
            int transDefinition;

            _source.println();
            _source.println(
                "        //---------------------------------------------------");
            _source.println("        // Statics.");
            _source.println("        //");
            _source.print("        ");
            _source.print("private static Map");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println(" _transitions;");
            _source.println();

            // Now output the transition collection's
            // initialization.
            _source.println("        static");
            _source.println("        {");
            _source.print("            ");
            _source.print("_transitions = new HashMap");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println("();");

            // Now place all transition names and states into the
            // map.
            for (SmcTransition transition: allTransitions)
            {
                transName = transition.getName();

                // If the transition is defined in this map's
                // default state, then the value is 2.
                if (definedDefaultTransitions.contains(
                        transition) == true)
                {
                    transDefinition = 2;
                }
                // Otherwise the value is 0 - undefined.
                else
                {
                    transDefinition = 0;
                }

                _source.print("            ");
                _source.print("_transitions.put(\"");
                _source.print(transName);
                _source.print("\", new Integer(");
                _source.print(transDefinition);
                _source.println("));");
            }
            _source.println("        }");
        }

        // The map class has been defined.
        _source.println("    }");

        return;
    } // end of visit(SmcMap)

    /**
     * Emits Java code for this FSM state.
     * @param state emits Java code for this state.
     */
    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        List<SmcAction> actions;
        String indent2;

        // Declare the inner state class.
        _source.println();
        _source.print("        private static final class ");
        _source.print(mapName);
        _source.print('_');
        _source.println(stateName);
        _source.print("            extends ");
        _source.print(mapName);
        _source.println("_Default");
        _source.println("        {");
        _source.println(
            "        //-------------------------------------------------------");
        _source.println("        // Member methods.");
        _source.println("        //");
        _source.println();

        // If -reflect was specified, then generate the
        // getTransitions() methods.
        if (_reflectFlag == true)
        {
            _source.print("            ");
            _source.print("public Map");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println(" getTransitions()");
            _source.println("            {");
            _source.print("                ");
            _source.println("return (_transitions);");
            _source.println("            }");
            _source.println();
        }

        // Add the constructor.
        _source.print("            private ");
        _source.print(mapName);
        _source.print('_');
        _source.print(stateName);
        _source.println("(String name, int id)");
        _source.println("            {");
        _source.println("                super (name, id);");
        _source.println("            }");

        // Add the Entry() and Exit() member functions if this
        // state defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print("            protected void Entry(");
            _source.print(context);
            _source.println("Context context)");
            _source.println("            {");

            // Declare the "ctxt" local variable.
            _source.print("                ");
            _source.print(context);
            _source.println(" ctxt = context.getOwner();");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = "                ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }

            _indent = indent2;

            // End the Entry() member function with a return.
            _source.println("                return;");
            _source.println("            }");
        }

        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print("            protected void Exit(");
            _source.print(context);
            _source.println("Context context)");
            _source.println("            {");

            // Declare the "ctxt" local variable.
            _source.print("                ");
            _source.print(context);
            _source.println(" ctxt = context.getOwner();");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = "                ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }

            // End the Exit() member function with a return.
            _source.println("                return;");
            _source.println("            }");
        }

        // Have each transition generate its code.
        _indent = "            ";
        for (SmcTransition transition: state.getTransitions())
        {
            transition.accept(this);
        }

        _source.println();
        _source.println(
            "        //-------------------------------------------------------");
        _source.println("        // Member data.");
        _source.println("        //");

        // If -reflect was specified, then generate the
        // _transitions map.
        if (_reflectFlag == true)
        {
            List<SmcTransition> allTransitions =
                map.getFSM().getTransitions();
            List<SmcTransition> stateTransitions =
                state.getTransitions();
            SmcState defaultState = map.getDefaultState();
            List<SmcTransition> defaultTransitions;
            String transName;
            int transDefinition;

            // Initialize the default transition list to all the
            // default state's transitions.
            if (defaultState != null)
            {
                defaultTransitions =
                    defaultState.getTransitions();
            }
            else
            {
                defaultTransitions =
                    new ArrayList<SmcTransition>();
            }

            _source.println();
            _source.println(
                "            //---------------------------------------------------");
            _source.println("            // Statics.");
            _source.println("            //");
            _source.print("            ");
            _source.print("private static Map");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println(" _transitions;");
            _source.println();

            // Now output the transition collection's
            // initialization.
            _source.println("            static");
            _source.println("            {");
            _source.print("                ");
            _source.print("_transitions = new HashMap");
            if (_genericFlag == true)
            {
                _source.print("<String, Integer>");
            }
            _source.println("();");

            // Now place all transition names and states into the
            // map.
            for (SmcTransition transition: allTransitions)
            {
                transName = transition.getName();

                // If the transition is in this state, then its
                // value is 1.
                if (stateTransitions.contains(
                        transition) == true)
                {
                    transDefinition = 1;
                }
                // If the transition is defined in this map's
                // default state, then the value is 2.
                else if (defaultTransitions.contains(
                             transition) == true)
                {
                    transDefinition = 2;
                }
                // Otherwise the value is 0 - undefined.
                else
                {
                    transDefinition = 0;
                }

                _source.print("                ");
                _source.print("_transitions.put(\"");
                _source.print(transName);
                _source.print("\", new Integer(");
                _source.print(transDefinition);
                _source.println("));");
            }
            _source.println("            }");
        }

        // End of this state class declaration.
        _source.println("        }");

        return;
    } // end of visit(SmcState)

    /**
     * Emits Java code for this FSM state transition.
     * @param transition emits Java code for this state transition.
     */
    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        String transName = transition.getName();
        List<SmcParameter> parameters =
            transition.getParameters();
        List<SmcGuard> guards = transition.getGuards();
        boolean nullCondition = false;
        Iterator<SmcParameter> pit;
        Iterator<SmcGuard> git;
        SmcGuard guard;

        _source.println();
        _source.print(_indent);
        _source.print("protected void ");
        _source.print(transName);
        _source.print("(");
        _source.print(context);
        _source.print("Context context");

        // Add user-defined parameters.
        for (SmcParameter parameter: parameters)
        {
            _source.print(", ");
            parameter.accept(this);
        }
        _source.println(")");

        _source.print(_indent);
        _source.println("{");

        // All transitions have a "ctxt" local variable.
        // 8/14/2003:
        // Do this only if there are any transition actions or
        // guard conditions which reference it.
        if (transition.hasCtxtReference() == true)
        {
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println(" ctxt = context.getOwner();");
        }

        _source.println();

        // Output transition to debug stream.
        if (_debugFlag == true)
        {
            String sep;

            _source.print(_indent);
            _source.println(
                "    if (context.getDebugFlag() == true)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.print("        PrintStream str = ");
            _source.println("context.getDebugStream();");
            _source.println();
            _source.print(_indent);
            _source.print(
                "        str.println(\"TRANSITION   : ");
            _source.print(mapName);
            _source.print('.');
            _source.print(stateName);
            _source.print('.');
            _source.print(transName);

            _source.print('(');
            for (pit = parameters.iterator(), sep = "";
                 pit.hasNext() == true;
                 sep = ", ")
            {
                _source.print(sep);
                (pit.next()).accept(this);
            }
            _source.print(')');

            _source.println("\");");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Loop through the guards and print each one.
        for (git = guards.iterator(),
                 _guardIndex = 0,
                 _guardCount = guards.size();
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = git.next();

            // Count up the guards with no condition.
            if (guard.getCondition().length() == 0)
            {
                nullCondition = true;
            }

            guard.accept(this);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition. Pass all arguments into the default
        // transition.
        if (_guardIndex > 0 && nullCondition == false)
        {
            if (_guardCount == 1)
            {
                _source.print(_indent);
                _source.println("    }");
            }

            _source.print(_indent);
            _source.println("    else");
            _source.print(_indent);
            _source.println("    {");

            // Call the super class' transition method using
            // the "super" keyword and not the class name.
            _source.print(_indent);
            _source.print("        super.");
            _source.print(transName);
            _source.print("(context");

            for (SmcParameter param: parameters)
            {
                _source.print(", ");
                _source.print(param.getName());
            }

            _source.println(");");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }
        // Need to add a final newline after a multiguard block.
        else if (_guardCount > 1)
        {
            _source.println();
            _source.println();
        }

        _source.print(_indent);
        _source.println("    return;");
        _source.print(_indent);
        _source.println("}");

        return;
    } // end of visit(SmcTransition)

    /**
     * Emits Java code for this FSM transition guard.
     * @param guard emits Java code for this transition guard.
     */
    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        TransType transType = guard.getTransType();
        boolean loopbackFlag = false;
        String indent2;
        String indent3;
        String indent4;
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List<SmcAction> actions = guard.getActions();
        boolean hasActions = !(actions.isEmpty());

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the
        // state name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state" is actually a transition name.
        if (transType != TransType.TRANS_POP &&
            endStateName.length () > 0 &&
            endStateName.equals(SmcElement.NIL_STATE) == false)
        {
            endStateName = scopeStateName(endStateName, mapName);
        }

        // Qualify the state and push state names as well.
        stateName = scopeStateName(stateName, mapName);
        pushStateName = scopeStateName(pushStateName, mapName);

        loopbackFlag = isLoopback(transType, endStateName);

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            indent2 = _indent + "        ";

            // There are multiple guards.
            // Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an "if"
                // should be used.
                _source.print(_indent);
                _source.print("    if (");
                _source.print(condition);
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if".
                _source.println();
                _source.print(_indent);
                _source.print("    else if (");
                _source.print(condition);
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println();
                _source.print(_indent);
                _source.println("    else");
                _source.print(_indent);
                _source.println("    {");
            }
        }
        // There is only one guard. Does this guard have a
        // condition?
        else if (condition.length() == 0)
        {
            // No. This is a plain, old. vanilla transition.
            indent2 = _indent + "    ";
        }
        else
        {
            // Yes there is a condition.
            indent2 = _indent + "        ";

            _source.print(_indent);
            _source.print("    if (");
            _source.print(condition);
            _source.println(")");
            _source.print(_indent);
            _source.println("    {");
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transition's actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (hasActions == false || loopbackFlag == false)
        {
            fqEndStateName = endStateName;
        }
        // Save away the current state if this is a loopback
        // transition. Storing current state allows the
        // current state to be cleared before any actions are
        // executed. Remember: actions are not allowed to
        // issue transitions and clearing the current state
        // prevents them from doing do.
        else
        {
            fqEndStateName = "endState";

            _source.print(indent2);
            _source.print(context);
            _source.print("State ");
            _source.print(fqEndStateName);
            _source.println(" = context.getState();");
        }

        _source.println();

        // Dump out the exit actions - but only for the first
        // guard.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (transType == TransType.TRANS_POP ||
            loopbackFlag == false)
        {
            _source.print(indent2);
            _source.println(
                "(context.getState()).Exit(context);");
        }

        // Dump out this transition's actions.
        if (hasActions == false)
        {
            if (condition.length() > 0)
            {
                _source.print(indent2);
                _source.println("// No actions.");
            }

            indent3 = indent2;
        }
        else
        {
            // Now that we are in the transition, clear the
            // current state.
            _source.print(indent2);
            _source.println("context.clearState();");

            // v. 2.0.0: Place the actions inside a try/finally
            // block. This way the state will be set before an
            // exception leaves the transition method.
            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (_noCatchFlag == false)
            {
                _source.print(indent2);
                _source.println("try");
                _source.print(indent2);
                _source.println('{');

                indent3 = indent2 + "    ";
            }
            else
            {
                indent3 = indent2;
            }

            indent4 = _indent;
            _indent = indent3;

            for (SmcAction action: actions)
            {
                action.accept(this);
            }

            _indent = indent4;

            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (_noCatchFlag == false)
            {
                _source.print(indent2);
                _source.println('}');
                _source.print(indent2);
                _source.println("finally");
                _source.print(indent2);
                _source.println('{');
            }
        }

        // Print the setState() call, if necessary. Do NOT
        // generate the set state it:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        if (transType == TransType.TRANS_SET &&
            (hasActions == true || loopbackFlag == false))
        {
            _source.print(indent3);
            _source.print("context.setState(");
            _source.print(fqEndStateName);
            _source.println(");");
        }
        else if (transType == TransType.TRANS_PUSH)
        {
            // Set the next state so this it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || hasActions == true)
            {
                _source.print(indent3);
                _source.print("context.setState(");
                _source.print(fqEndStateName);
                _source.println(");");
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                _source.print(indent3);
                _source.println(
                    "(context.getState()).Entry(context);");
            }

            _source.print(indent3);
            _source.print("context.pushState(");
            _source.print(pushStateName);
            _source.println(");");
        }
        else if (transType == TransType.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("context.popState();");
        }

        // Perform the new state's enty actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((transType == TransType.TRANS_SET &&
             loopbackFlag == false) ||
             transType == TransType.TRANS_PUSH)
        {
            _source.print(indent3);
            _source.println(
                "(context.getState()).Entry(context);");
        }

        // If there was a try/finally, then put the closing
        // brace on the finally block.
        // v. 2.2.0: Check if the user has turned off this
        // feature first.
        if (hasActions == true && _noCatchFlag == false)
        {
            _source.print(indent2);
            _source.println('}');
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == TransType.TRANS_POP &&
            endStateName.equals(SmcElement.NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.println();
            _source.print(indent2);
            _source.print("context.");
            _source.print(endStateName);
            _source.print("(");

            // Output any and all pop arguments.
            if (popArgs.length() > 0)
            {
                _source.print(popArgs);
            }
            _source.println(");");
        }

        // If this is a guarded transition, it will be necessary
        // to close off the "if" body. DON'T PRINT A NEW LINE!
        // Why? Because an "else" or "else if" may follow and we
        // won't know until we go back to the transition source
        // generator whether all clauses have been done.
        if (_guardCount > 1)
        {
            _source.print(_indent);
            _source.print("    }");
        }

        return;
    } // end of visit(SmcGuard)

    /**
     * Emits Java code for this FSM action.
     * @param action emits Java code for this action.
     */
    public void visit(SmcAction action)
    {
        String name = action.getName();
        Iterator<String> it;
        String sep;

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        _source.print(_indent);
        if (name.equals("emptyStateStack") == true)
        {
            _source.print("context.");
        }
        else
        {
            _source.print("ctxt.");
        }
        _source.print(name);
        _source.print("(");

        for (it = action.getArguments().iterator(), sep = "";
             it.hasNext() == true;
             sep = ", ")
        {
            _source.print(sep);
            _source.print(it.next());
        }

        _source.println(");");

        return;
    } // end of visit(SmcAction)

    /**
     * Emits Java code for this transition parameter.
     * @param parameter emits Java code for this transition
     * parameter.
     */
    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(' ');
        _source.print(parameter.getName());

        return;
    } // end of visit(SmcParameter)

    //
    // end of SmcVisitor Abstract Method Impelementation.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member data
//
} // end of class SmcJavaGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.2  2009/03/16 19:46:25  cwrapp
// Removed final from context class. Cleaned up push transition code.
//
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.11  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.10  2007/02/21 13:55:32  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.9  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.8  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.7  2006/06/03 19:39:25  cwrapp
// Final v. 4.3.1 check in.
//
// Revision 1.6  2005/11/07 19:34:54  cwrapp
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
// Revision 1.5  2005/09/14 01:51:33  cwrapp
// Changes in release 4.2.0:
// New features:
//
// None.
//
// Fixed the following bugs:
//
// + (Java) -java broken due to an untested minor change.
//
// Revision 1.4  2005/08/26 15:21:34  cwrapp
// Final commit for release 4.2.0. See README.txt for more information.
//
// Revision 1.3  2005/06/30 10:44:23  cwrapp
// Added %access keyword which allows developers to set the generate Context
// class' accessibility level in Java and C#.
//
// Revision 1.2  2005/06/18 18:28:42  cwrapp
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
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.2  2005/02/21 15:35:45  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:18:32  charlesr
// Modified isLoopback() to new signature due to moving method from
// SmcGuard to SmcCodeGenerator.
// Corrected indentation for "loopbackFlag =" statement.
// Declaring "boolean loopbackFlag" only if and where it is needed.
//
// Revision 1.0  2005/02/03 17:11:27  charlesr
// Initial revision
//
