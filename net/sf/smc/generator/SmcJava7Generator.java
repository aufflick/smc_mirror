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

package net.sf.smc.generator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
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
 * Visits the abstract syntax tree, emitting Java code. Generates
 * a transition table which maps the current state to a method
 * handle. Each state map has a separate transition table but
 * all maps use a union of all transitions.
 *
 * @see SmcJavaGenerator
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 * @see SmcOptions
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcJava7Generator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a Java 7 code generator for the given options.
     * @param options The target code generator options.
     */
    public SmcJava7Generator(final SmcOptions options)
    {
        super (options, "java");
    } // end of SmcJava7Generator(SmcOptions)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits Java code for the finite state machine context
     * class.
     * @param fsm emit Java code for this finite state machine.
     */
    @Override
    public void visit(final SmcFSM fsm)
    {
        // 1. Output the read-only opening comments and user raw
        //    source code.
        outputHeader(fsm);

        // 2. Output the class import statements.
        outputImports(fsm);

        // 3. Output the class declaration and opening brace.
        outputClassDeclaration(fsm);

        // 4. Output the class constructors, owner get and set
        //    methods, and the transition methods.
        outputMethods(fsm);

        // 5. Output the class data members.
        outputData(fsm);

        // 6. Output the class closing brace and the read-only
        //    ending comments.
        outputFooter();

        return;
    } // end of visit(SmcFSM)

    /**
     * Emits Java code for the FSM map.
     * @param map emit Java code for this map.
     */
    @Override
    public void visit(final SmcMap map)
    {
        final SmcState defaultState = map.getDefaultState();

        // 1. Output the map default state transitions - if the
        //    map has a default state.
        if (defaultState != null)
        {
            defaultState.accept(this);
        }

        // 2. Output the map state transitions.        
        for (SmcState state: map.getStates())
        {
            state.accept(this);
        }

        return;
    } // end of visit(SmcMap)

    /**
     * Emits Java code for this FSM state.
     * @param state emits Java code for this state.
     */
    public void visit(final SmcState state)
    {
        final String mapName = (state.getMap()).getName();
        final String stateName = state.getClassName();
        final List<SmcAction> entryActions =
            state.getEntryActions();
        final List<SmcAction> exitActions =
            state.getExitActions();
        final List<SmcTransition> transitions =
            state.getTransitions();

        // Reset the default indentation to 4 spaces in case
        // there are state entry or exit actions.
        _indent = "    ";

        if ((entryActions != null &&
             entryActions.isEmpty() == false) ||
            (exitActions != null &&
             exitActions.isEmpty() == false))
        {
            _source.println(
                "    //-----------------------------------------------------------");
            _source.print("    // ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.println(" State Entry/Exit Actions.");
            _source.println("    //");

            if (entryActions != null &&
                entryActions.isEmpty() == false)
            {
                outputStateActions(mapName,
                                   stateName,
                                   ENTRY_NAME,
                                   entryActions);
            }

            if (exitActions != null &&
                exitActions.isEmpty() == false)
            {
                outputStateActions(mapName,
                                   stateName,
                                   EXIT_NAME,
                                   exitActions);
            }

            _source.println();
            _source.println("    //");
            _source.print("    // end of ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.println(" State Entry/Exit Actions.");
            _source.println(
                "    //-----------------------------------------------------------");
            _source.println();
        }

        if (transitions.isEmpty() == false)
        {
            _source.println(
                "    //-----------------------------------------------------------");
            _source.print("    // ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.println(" State Transitions.");
            _source.println("    //");

            // 1. Output each transition implementation.
            for (SmcTransition transition: transitions)
            {
                transition.accept(this);
            }

            _source.println();
            _source.println("    //");
            _source.print("    // end of ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.println(" State Transitions.");
            _source.println(
                "    //-----------------------------------------------------------");
            _source.println();
        }

        return;
    } // end of visit(SmcState)

    /**
     * Emits Java code for this FSM state transition.
     * @param transition emits Java code for this state transition.
     */
    public void visit(final SmcTransition transition)
    {
        final SmcState state = transition.getState();
        final SmcMap map = state.getMap();
        final String mapName = map.getName();
        final String stateName = state.getClassName();
        final String transName = transition.getName();
        final String context = (map.getFSM()).getContext();
        String sep = "";

        // 1. Output transtion method declaration and opening
        //    brace.

        _source.println();
        _source.print("    private void ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.print("_");
        _source.print(transName);
        _source.print("(");

        // 1.1. Output user-defined parameters.
        for (SmcParameter parameter: transition.getParameters())
        {
            _source.print(sep);
            parameter.accept(this);

            sep = ", ";
        }
        _source.println(")");

        // 1.2. Output opening brace.
        _source.println("    {");

        // 2. Output the transition guards.
        outputTransitionGuards(transition, mapName, stateName);

        // 3. Output the return and closing brace.
        _source.println();
        _source.println("        return;");
        _source.println("    }");
        _source.println();

        return;
    } // end of visit(SmcTransition)

    /**
     * Emits Java code for this FSM transition guard.
     * @param guard emits Java code for this transition guard.
     */
    public void visit(final SmcGuard guard)
    {
        final TransType transType = guard.getTransType();
        final boolean hasActions =
            !(guard.getActions().isEmpty());
        final SmcTransition transition = guard.getTransition();
        final SmcState state = transition.getState();
        final SmcMap map = state.getMap();
        final String mapName = map.getName();
        final String stateName = state.getClassName();
        final String transName = transition.getName();
        final String endStateName = guard.getEndState();
        String endStateId = "";
        String pushStateId = "";
        String pushStateName = "";
        boolean loopbackFlag =
            isLoopback(transType, endStateName);

        // 1. Set the end state identifier used in the setState
        //    or push state call.
        if (transType == TransType.TRANS_SET)
        {
            if (loopbackFlag == true)
            {
                endStateId = "stateId";
            }
            else
            {
                endStateId =
                    scopeStateName(endStateName, mapName, "_") +
                    STATE_ID_SUFFIX;
            }
        }
        // If this is a push transition, then generate the push
        // state identifier.
        // Note: a push transition has the option of
        // transitioning to another state and doing the push
        // from there. So a push transition has two end states:
        // the plain transition state and the push state.
        else if (transType == TransType.TRANS_PUSH)
        {
            if (endStateName.endsWith(DEFAULT_NAME) == true ||
                endStateName.endsWith(NIL_STATE) == true)
            {
                endStateId = "stateId";
            }
            else
            {
                endStateId =
                    scopeStateName(endStateName, mapName, "_") +
                    STATE_ID_SUFFIX;
            }

            pushStateName = guard.getPushState();
            pushStateId =
                scopeStateName(pushStateName,
                               mapName,
                               "_") +
                STATE_ID_SUFFIX;
        }

        // 2. Output either "if", "else if", "else", or nothing
        // if this state's has only one unguarded transition
        // definition.
        outputGuardCondition(guard.getCondition());

        // 3. Output state exit - if this is *not* a push
        //    transition or an internal loopback.
        //
        //    Generate the next state identifier. This is done
        //    for plain transition and push transitions. This is
        //    not done for internal loopback transitions or pop
        //    transitions. It is not done for internal loopback
        //    transitions because the transition does not leave
        //    its original state. It is not done for pop
        //    transitions because the next state is extracted
        //    from the state stack.
        if (transType != TransType.TRANS_PUSH &&
            loopbackFlag == false)
        {
            outputStateExit(mapName, stateName);
        }

        // 4. Output the guard body inside a try/finally block.
        _source.print(_indent);
        _source.println("try");
        _source.print(_indent);
        _source.println('{');

        outputGuardBody(guard, transition, mapName, stateName);

        _source.print(_indent);
        _source.println('}');
        _source.print(_indent);
        _source.println("finally");
        _source.print(_indent);
        _source.println('{');

        // 5. Output setting the next state. How this is done
        //    depends on whether this is plain transition,
        //    internal loopback, push, or pop.
        //    Note: this is done in a finally block.
        if (transType == TransType.TRANS_SET &&
            (hasActions == true || loopbackFlag == false))
        {
            _source.print(_indent);
            _source.print("    setState(_States[");
            _source.print(endStateId);
            _source.println("]);");
        }
        else if (transType == TransType.TRANS_PUSH)
        {
            // Set the next state so this it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || hasActions == true)
            {
                _source.print(_indent);
                _source.print("    setState(_States[");
                _source.print(endStateId);
                _source.println("]);");
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                _source.print(_indent);
                _source.println("    enterState();");
            }

            _source.print(_indent);
            _source.print("    pushState(_States[");
            _source.print(pushStateId);
            _source.println("]);");
        }
        else if (transType == TransType.TRANS_POP)
        {
            _source.print(_indent);
            _source.println("    popState();");
        }

        // 6. Output the finally block closing brace.
        _source.print(_indent);
        _source.println('}');
        _source.println();

        // 7. Output state entry - if this is *not* a pop
        //    transition or an internal loopback.
        if (transType == TransType.TRANS_SET &&
            loopbackFlag == false)
        {
            outputStateEnter(mapName, endStateName);
        }
        // If this a push transition, then use the push state
        // name.
        else if (transType == TransType.TRANS_PUSH)
        {
            outputStateEnter(mapName, pushStateName);
        }

        // 8. Output the pop transition.
        if (transType == TransType.TRANS_POP &&
            endStateName.isEmpty() == false &&
            endStateName.equals(SmcElement.NIL_STATE) == false)
        {
            _source.println();
            _source.print(_indent);
            _source.print(endStateName);
            _source.print("(");
            _source.print(guard.getPopArgs());
            _source.println(");");
        }

        // 9. If this is a guarded transition, it is necessary
        //    to close off the "if" body. DON'T PRINT A NEW LINE!
        //    Why? Because an "else" or "else if" may follow and
        //    we won't know until we go back to the transition
        //    source generator whether all clauses have been
        //    done.
        if (_guardCount > 1 ||
            (guard.getCondition()).isEmpty() == false)
        {
            _source.print("        }");
        }

        return;
    } // end of visit(SmcGuard)

    /**
     * Emits Java code for this FSM action.
     * @param action emits Java code for this action.
     */
    public void visit(final SmcAction action)
    {
        String name = action.getName();
        Iterator<String> it;
        String sep;

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        _source.print(_indent);
        if (action.isEmptyStateStack() == true)
        {
            _source.println("    emptyStateStack();");
        }
        else
        {
        	if (action.isStatic( ) == false)
        	{
	            _source.print("    ctxt.");
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
        }

        return;
    } // end of visit(SmcAction)

    /**
     * Emits Java code for this transition parameter.
     * @param parameter emits Java code for this transition
     * parameter.
     */
    public void visit(final SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(' ');
        _source.print(parameter.getName());

        return;
    } // end of visit(SmcParameter)

    //
    // end of SmcVisitor Abstract Method Impelementation.
    //-----------------------------------------------------------

    /**
     * Writes the unmodifiable comment, optional user raw source,
     * and the package name.
     * @param fsm the parsed FSM model.
     */
    private void outputHeader(final SmcFSM fsm)
    {
        String rawSource = fsm.getSource();
        String packageName = fsm.getPackage();

        _source.println("/*");
        _source.println(" * ex: set ro:");
        _source.println(" * DO NOT EDIT.");
        _source.println(" * generated by smc (http://smc.sourceforge.net/)");
        _source.print(" * from file : ");
        _source.print(_srcfileBase);
        _source.println(".sm");
        _source.println(" */");
        _source.println();

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

        return;
    } // end of outputHeader(SmcFSM)

    /**
     * Writes the user-defined and required import statements.
     * @param smc FSM model.
     */
    private void outputImports(final SmcFSM fsm)
    {
        // Do user-specified imports now.
        for (String imp: fsm.getImports())
        {
            _source.print("import ");
            _source.print(imp);
            _source.println(";");
        }

        // If serialization is on, then import the necessary
        // java.io classes.
        if (_serialFlag == true)
        {
            _source.println("import java.io.IOException;");
            _source.println("import java.io.ObjectInputStream;");
            _source.println("import java.io.ObjectOutputStream;");
            _source.println("import java.io.Serializable;");
        }

        _source.println("import java.lang.invoke.MethodHandle;");
        _source.println("import java.lang.invoke.MethodHandles;");
        _source.println("import java.lang.invoke.MethodHandles.Lookup;");
        _source.println("import java.lang.invoke.MethodType;");
        _source.println("import java.util.ArrayDeque;");
        _source.println("import java.util.Arrays;");
        _source.println("import java.util.Iterator;");

        // Import the required FSMContext7 and State7 classes.
        _source.println("import statemap.FSMContext7;");
        _source.println("import statemap.State7;");

        _source.println();

        return;
    } // end of outputImports(SmcFSM)

    /**
     * Writes the class declaration and opening brace.
     * @param smc FSM model.
     */
    private void outputClassDeclaration(final SmcFSM fsm)
    {
        String fsmClassName = fsm.getFsmClassName();

        // The context clas contains all the state classes as
        // inner classes, so generate the context first rather
        // than last.
        _source.print(_accessLevel);
        _source.print(" final class ");
        _source.print(fsmClassName);
        _source.println("");
        _source.println("    extends FSMContext7");

        if (_serialFlag == true)
        {
            _source.println(
                "    implements Serializable");
        }

        _source.println("{");

        return;
    } // end of outputClassDeclaration(SmcFSM)

    /**
     * Writes the constructors and various FSM methods.
     * @param smc FSM model.
     */
    private void outputMethods(final SmcFSM fsm)
    {
        _source.println(
            "//---------------------------------------------------------------");
        _source.println("// Member methods.");
        _source.println("//");
        _source.println();

        // 4.1. Output the class constructors.
        outputConstructors(fsm);

        // 4.2. Output get/set methods.
        if (_serialFlag == true || _reflectFlag == true)
        {
            outputGet();
        }
        outputSet(fsm);

        // 4.3. Output the required executeAction(MethodHandle)
        //      method override.
        outputExecuteAction();

        // Note: the enterStartState() method is now defined in FSMContext7
        // since State7 is now final.

        // 4.4. Output the transition interface methods.
        //      Note: these are not the method which implement a
        //            transition. The context class calls these
        //            methods to issue a transition.
        outputTransitionApi(fsm);

        // 4.5. Output the Java serialization writeObject and
        //      readObject methods, if serialization is on.
        if (_serialFlag == true)
        {
            outputSerializeMethods(fsm);
        }

        // 4.6. Output the transition implementation methods
        //      for each map, state, and transition.
        for (SmcMap map: fsm.getMaps())
        {
            map.accept(this);
        }

        return;
    } // end of outputMethods(SmcFSM)

    /**
     * Outputs the two FSM class constructors: one which uses the
     * default start state and one which allows the start state
     * to to dynamically set. The second constructor is necessary
     * for de-serializing an FSM context.
     * @param smc the FSM model.
     */
    private void outputConstructors(final SmcFSM fsm)
    {
        final String context = fsm.getContext();
        final String fsmClassName = fsm.getFsmClassName();
        final String startState = fsm.getStartState();
        final int index = startState.indexOf("::");
        String javaState;

        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Constructors.");
        _source.println("    //");
        _source.println();

        // Generate the context class' constructor using the
        // configured start state.

        // The state name "map::state" must be changed to
        // a constant integer state identifier:
        // "map_state_STATE_ID".
        javaState =
            startState.substring(0, index) + // The map name.
            "_" +
            startState.substring(index + 2) + // The state name.
            STATE_ID_SUFFIX;

        _source.print("    ");
        _source.print(_accessLevel);
        _source.print(" ");
        _source.print(fsmClassName);
        _source.print("(final ");
        _source.print(context);
        _source.println(" owner)");
        _source.println("    {");
        _source.print("        this (owner, _States[");
        _source.print(javaState);
        _source.println("]);");
        _source.println("    }");
        _source.println();

        // Generate the second constructor which allows the
        // initial state to be dynamically set. Overrides the
        // %start specifier.
        _source.print("    ");
        _source.print(_accessLevel);
        _source.print(" ");
        _source.print(fsmClassName);
        _source.print("(final ");
        _source.print(context);
        _source.println(" owner, final State7 initState)");
        _source.println("    {");
        _source.println("        super (initState);");
        _source.println();
        _source.println("        ctxt = owner;");

        _source.println("    }");
        _source.println();

        _source.println("    //");
        _source.println("    // end of Constructors.");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println();

        return;
    } // end of outputConstructors(SmcFSM)

    /**
     * Writes the get emthods requested by the -serial and/or
     * -reflect flags.
     */
    private void outputGet()
    {
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Get Methods.");
        _source.println("    //");
        _source.println();

        // NOTE: The getState() method is now implemented in
        // FSMContext7 since State7 is now final (i.e., has no
        // state sub-classes).

        // If serialization is turned on, then generate a
        // setOwner method which allows the application class
        // to restore its ownership of the FSM.
        if (_serialFlag == true)
        {
            _source.println(
                "    public State7 valueOf(int stateId)");
            _source.println(
                "        throws ArrayIndexOutOfBoundsException");
            _source.println("    {");
            _source.println(
                "        return (_States[stateId]);");
            _source.println("    }");
            _source.println();
        }

        if (_reflectFlag == true)
        {
            // getStates() method.
            _source.println("    public State7[] getStates()");
            _source.println("    {");
            _source.println("        return (_States);");
            _source.println("    }");
            _source.println();

            // getTransitions() method.
            _source.println("    public String[] getTransitions()");
            _source.println("    {");
            _source.println(
                "        return (TRANSITION_NAMES);");
            _source.println("    }");
            _source.println();
        }

        _source.println("    //");
        _source.println("    // end of Get Methods.");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println();

        return;
    } // end of outputGet()

    /**
     * Writes the owner set methods requested by the -serial
     * flag.
     * @param fsm the FSM model.
     */
    private void outputSet(final SmcFSM fsm)
    {
        if (_serialFlag == true)
        {
            _source.println(
                "    //-----------------------------------------------------------");
            _source.println("    // Set Methods.");
            _source.println("    //");
            _source.println();

            // setOwner() method.
            _source.print("    public void setOwner(");
            _source.print(fsm.getContext());
            _source.println(" owner)");
            _source.println("    {");
            _source.println("        if (owner == null)");
            _source.println("        {");
            _source.println("            throw (new NullPointerException(\"null owner\"));");
            _source.println("        }");
            _source.println("        else");
            _source.println("        {");
            _source.println("            ctxt = owner;");
            _source.println("        }");
            _source.println();
            _source.println("        return;");
            _source.println("    }");
            _source.println();

            _source.println("    //");
            _source.println("    // end of Set Methods.");
            _source.println(
                "    //-----------------------------------------------------------");
            _source.println();
        }

        return;
    } // end of outputSet(SmcFSM)

    /**
     * Writes the required abstract method override
     * {@code executeAction}.
     */
    private void outputExecuteAction()
    {
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // FSMContext7 Abstract Method Override.");
        _source.println("    //");
        _source.println();

        _source.println("    @Override");
        _source.println("    protected void executeAction(final MethodHandle mh)");
        _source.println("    {");
        _source.println("        try");
        _source.println("        {");
        _source.println("            mh.invokeExact(this);");
        _source.println("        }");
        _source.println("        catch (Throwable tex)");
        _source.println("        {");
        _source.println("            if (_debugFlag == true)");
        _source.println("            {");
        _source.println("                tex.printStackTrace(_debugStream);");
        _source.println("            }");
        _source.println("        }");
        _source.println();
        _source.println("        return;");
        _source.println("    }");
        _source.println();

        _source.println("    //");
        _source.println("    // end of FSMContext7 Abstract Method Override.");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println();

        return;
    } // end of outputExecuteAction()

    /**
     * Writes the transition API methods. The owner context
     * instance calls these methods to issue transitions.
     * @param fsm the FSM model.
     */
    private void outputTransitionApi(final SmcFSM fsm)
    {
        String transName;
        List<SmcParameter> params;
        Iterator<SmcParameter> pit;
        String separator;

        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Transitions.");
        _source.println("    //");
        _source.println();

        // Generate the default transition methods.
        for (SmcTransition trans: fsm.getTransitions())
        {
            transName = trans.getName();

            // Handle the default transition separately.
            if (transName.equals(DEFAULT_NAME) == false)
            {
                _source.print("    public ");

                // If the -sync flag was specified, then output
                // the "synchronized" keyword.
                if (_syncFlag == true)
                {
                    _source.print("synchronized ");
                }

                _source.print("void ");
                _source.print(transName);
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
                _source.print(transName);
                _source.println("\";");

                _source.println("        try");
                _source.println("        {");
                _source.print(
                    "            (getState().transition(");
                _source.print(transName);
                _source.print(trans.getIdentifier());
                _source.print(TRANSITION_ID_SUFFIX);
                _source.print(")).invokeExact(this");

                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    _source.print(", ");
                    _source.print((pit.next()).getName());
                }
                _source.println(");");
                _source.println("        }");
                _source.println("        catch (Throwable tex)");
                _source.println("        {");
                _source.println(
                    "            if (_debugFlag == true)");
                _source.println("            {");
                _source.println(
                    "                tex.printStackTrace(_debugStream);");
                _source.println("            }");
                _source.println("        }");

                // Clear the in-progress transition name before
                // returning.
                _source.println("        _transition = \"\";");

                _source.println("        return;");
                _source.println("    }");
                _source.println();
            }
        }

        _source.println("    //");
        _source.println("    // end of Transitions.");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println();

        return;
    } // end of outputTransitionApi(SmcFSM)

    /**
     * Writes the writeObject() and readObject() methods.
     * @param fsm the FSM model.
     */
    private void outputSerializeMethods(final SmcFSM fsm)
    {
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Serialization Methods.");
        _source.println("    //");
        _source.println();

        _source.println(
            "    private void writeObject(final ObjectOutputStream ostream)");
        _source.println("        throws IOException");
        _source.println("    {");
        _source.println(
            "        final int size =");
        _source.println(
            "            (_stateStack == null ? 0 : _stateStack.size());");
        _source.println("        int i;");
        _source.println();
        _source.println(
            "        ostream.writeInt(size);");
        _source.println();
        _source.println("        if (size > 0)");
        _source.println("        {");
        _source.println(
            "            final Iterator<State7> sit =");
        _source.println(
            "                _stateStack.iterator();");
        _source.println();
        _source.println(
            "            while (sit.hasNext())");
        _source.println("            {");
        _source.println(
            "                ostream.writeInt((sit.next()).getId());");
        _source.println("            }");
        _source.println("        }");
        _source.println();
        _source.println(
            "        ostream.writeInt(_state.getId());");
        _source.println();
        _source.println("        return;");
        _source.println("    }");
        _source.println();
        _source.println("    private void readObject(final ObjectInputStream istream)");
        _source.println(
            "        throws IOException");
        _source.println("    {");
        _source.println(
            "        final int size = istream.readInt();");
        _source.println();
        _source.println("        if (size == 0)");
        _source.println("        {");
        _source.println("            _stateStack = null;");
        _source.println("        }");
        _source.println("        else");
        _source.println("        {");
        _source.println("            int i;");
        _source.println();
        _source.println("            _stateStack = new ArrayDeque<>();");
        _source.println();
        _source.println(
            "            for (i = 0; i < size; ++i)");
        _source.println("            {");
        _source.println(
            "                _stateStack.addLast(_States[istream.readInt()]);");
        _source.println("            }");
        _source.println("        }");
        _source.println();
        _source.println(
            "        _state = _States[istream.readInt()];");
        _source.println();
        _source.println("        return;");
        _source.println("    }");
        _source.println();

        _source.println("    //");
        _source.println("    // end of Serialization Methods.");
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println();

        return;
    } // end of outputSerializeMethods(SmcFSM)

    /**
     * Outputs the instance and class data members.
     * @param fsm the FSM model.
     */
    private void outputData(final SmcFSM fsm)
    {
        final String context = fsm.getContext();

        // 5.1. Declare member data.
        _source.println(
            "//---------------------------------------------------------------");
        _source.println("// Member data.");
        _source.println("//");
        _source.println();
        _source.print("    transient private ");
        _source.print(context);
        _source.println(" ctxt;");

        // 5.2. Declare the class constants.
        _source.println();
        _source.println(
            "    //-----------------------------------------------------------");
        _source.println("    // Constants.");
        _source.println("    //");
        _source.println();
        _source.println(
            "    private static final long serialVersionUID = 1L;");
        _source.println();

        // 5.2.1. Output the state identifiers.
        outputStateIds(fsm);

        // 5.2.2. Output the transition identifiers.
        outputTransitionIds(fsm);

        // 5.2.3. Output the transition method signatures.
        outputTransitionSignatures(fsm);

        // 5.2.4. Output the map, state and transition name arrays.
        outputNames(fsm);

        // 5.2.5. Output the states array.
        //        Note: the array is filled in in the class init.
        _source.println(
            "    private static final State7[] _States = new State7[STATE_COUNT];");
        _source.println();

        // 5.2.6. Output the class static initialization block.
        outputClassInit(fsm);

        return;
    } // end of outputData(SmcFSM)

    /**
     * Writes the state identifiers as constant integer values.
     * @param fsm the FSM model.
     */
    private void outputStateIds(final SmcFSM fsm)
    {
        final List<SmcMap> maps = fsm.getMaps();
        String mapName;
        String stateIdName;
        int stateId = 0;

        for (SmcMap map : maps)
        {
            mapName = map.getName();

            for (SmcState state : map.getStates())
            {
                stateIdName =
                    String.format(
                        "%s_%s%s",
                        mapName,
                        state.getClassName(),
                        STATE_ID_SUFFIX);

                _source.print("    private static final int ");
                _source.print(stateIdName);
                _source.print(" = ");
                _source.print(stateId);
                _source.println(";");

                ++stateId;
            }

            _source.println();
        }

        // Now output the total number of states.
        _source.print(
            "    private static final int STATE_COUNT = ");
        _source.print(stateId);
        _source.println(";");
        _source.println();

        return;
    } // end of outputStateIds(SmcFSM)

    /**
     * Writes the transition identifier constants.
     * @param fsm the FSM model.
     */
    private void outputTransitionIds(final SmcFSM fsm)
    {
        final List<SmcTransition> transitions =
            fsm.getTransitions();
        String transName;
        String transIdName;
        int transId = 1;

        for (SmcTransition trans : transitions)
        {
            transName = trans.getName();

            // The default transition ID is already set to zero
            // in FSMContext7.
            if (transName.equals(DEFAULT_NAME) == false)
            {
                transIdName =
                    String.format(
                        "%s%d%s",
                        transName,
                        trans.getIdentifier(),
                        TRANSITION_ID_SUFFIX);

                _source.print("    private static final int ");
                _source.print(transIdName);
                _source.print(" = ");
                _source.print(transId);
                _source.println(";");

                ++transId;
            }
        }

        // Now output the total number of transitions.
        _source.println();
        _source.print(
            "    private static final int TRANSITION_COUNT = ");
        _source.print(transId);
        _source.println(";");
        _source.println();

        return;
    } // end of outputTransitionIds(SmcFSM)

    private void outputTransitionSignatures(final SmcFSM fsm)
    {
        final List<SmcTransition> transitions =
            fsm.getTransitions();
        String transName;
        List<SmcParameter> params;

        _source.println(
            "    private static final MethodType[] TRANSITION_TYPES =");
        _source.println("    {");

        // The first transition (index 0) is the Default
        // transition - which returns void and has no
        // parameters.
        _source.print("        NO_ARGS_TYPE");

        for (SmcTransition trans : transitions)
        {
            transName = trans.getName();

            if (transName.equals(DEFAULT_NAME) == false)
            {
                // Output the separator between the previous
                // signature and this one.
                _source.println(",");

                params = trans.getParameters();

                // If the transition has no parameters, then
                // output NO_ARGS_TYPE.
                if (params.isEmpty() == true)
                {
                    _source.print("        NO_ARGS_TYPE");
                }
                else
                {
                    // The first argument is the method return
                    // type.
                    _source.print(
                        "        MethodType.methodType(void.class");

                    // Output the method parameters.
                    for (SmcParameter param : params)
                    {
                        _source.print(", ");
                        _source.print(getJavaType(param.getType()));
                        _source.print(".class");
                    }

                    _source.print(")");
                }
            }
        }

        _source.println();
        _source.println("    };");
        _source.println();

        return;
    } // end of outputTransitionSignatures(SmcFSM)

    /**
     * Output the map, state, and transition name arrays.
     * @param fsm the FSM model.
     */
    private void outputNames(final SmcFSM fsm)
    {
        // 5.2.4.1. Output the map names.
        outputMapNames(fsm);

        // 5.2.4.2. Output the state names.
        outputStateNames(fsm);

        // 5.24.3. Output the state transition names.
        outputStateTransitions(fsm);

        // 5.2.4.4. Output the transition names.
        outputTransitionNames(fsm);

        return;
    } // end of outputNames(SmcFSM)

    /**
     * Writes the {@code MAP_NAMES} string array.
     * @param fsm the FSM model.
     */
    private void outputMapNames(final SmcFSM fsm)
    {
        String sep = "";

        _source.println(
            "    private static final String[] MAP_NAMES =");
        _source.print("    {");

        for (SmcMap map : fsm.getMaps())
        {
            _source.println(sep);
            _source.print("        \"");
            _source.print(map.getName());
            _source.print("\"");

            sep = ",";
        }

        _source.println();
        _source.println("    };");
        _source.println();

        return;
    } // end of outputMapNames(SmcFSM)

    /**
     * Writes a two-dimesional array containing the state names
     * for each map.
     * @param fsm the FSM model.
     */
    private void outputStateNames(final SmcFSM fsm)
    {
        String sep0 = "";
        String sep1 = "";

        _source.println(
            "    private static final String[][] STATE_NAMES =");
        _source.print("    {");

        for (SmcMap map : fsm.getMaps())
        {
            _source.println(sep0);
            _source.println("        new String[]");
            _source.print("        {");

            for (SmcState state : map.getStates())
            {
                _source.println(sep1);
                _source.print("            \"");
                _source.print(state.getInstanceName());
                _source.print("\"");

                sep1 = ",";
            }

            _source.println();
            _source.print("        }");

            sep0 = ",\n";
            sep1 = "";
        }

        _source.println();
        _source.println("    };");
        _source.println();

        return;
    } // end of outputStateNames(SmcFSM)

    /**
     * Writes a two-dimensional array containing the transition
     * names used by each state.
     * @param fsm the FSM model.
     */
    private void outputStateTransitions(final SmcFSM fsm)
    {
        String sep0 = "";
        String sep1 = "";

        _source.println(
            "    private static String[][] STATE_TRANSITIONS =");
        _source.print("    {");

        for (SmcMap map : fsm.getMaps())
        {
            for (SmcState state : map.getStates())
            {
                _source.println(sep0);
                _source.println("        new String[]");
                _source.print("        {");

                for (SmcTransition trans :
                         state.getTransitions())
                {
                    _source.println(sep1);
                    _source.print("            \"");
                    _source.print(trans.getName());
                    _source.print("\"");

                    sep1 = ",";
                }

                _source.println();
                _source.print("        }");

                sep0 = ",\n";
                sep1 = "";
            }
        }

        _source.println();
        _source.println("    };");
        _source.println();

        return;
    } // end of outputStateTransitions(SmcFSM)

    /**
     * Writes an array containing all transition names,
     * independent of map and state.
     * @param fsm the FSM model.
     */
    private void outputTransitionNames(final SmcFSM fsm)
    {
        final String sep = ",";
        String transName;

        _source.println(
            "    private static final String[] TRANSITION_NAMES =");
        _source.println("    {");

        // Output the default name separately and first.
        _source.print("        \"");
        _source.print(DEFAULT_NAME);
        _source.print("\"");

        for (SmcTransition trans : fsm.getTransitions())
        {
            transName = trans.getName();
            if (transName.equals(DEFAULT_NAME) == false)
            {
                _source.println(sep);
                _source.print("        \"");
                _source.print(transName);
                _source.print("\"");
            }
        }

        _source.println();
        _source.println("    };");
        _source.println();

        return;
    } // end of outputTransitionNames(SmcFSM)

    /**
     * Writes the class initialization block which fills in the
     * States array with State instances.
     * @param fsm the FSM model.
     */
    private void outputClassInit(final SmcFSM fsm)
    {
        _source.println("    static");
        _source.println("    {");

        // 5.2.6.1. Output the local variables used in generating
        //         the State7 instances.
        outputLocalVars(fsm);

        // 5.2.6.2. Output the map for-loop.
        _source.println(
            "        for (mapIndex = 0; mapIndex < mapSize; ++mapIndex)");
        _source.println("        {");
        _source.println(
            "            mapName = MAP_NAMES[mapIndex];");
        _source.println(
            "            stateSize = STATE_NAMES[mapIndex].length;");
        _source.println();

        // 5.2.6.3. Output the state for-loop.
        outputClassInitStateLoop();

        // 5.2.6.4. Output the map for-loop and class init block
        //          closing braces.
        _source.println("        }");
        _source.println("    }");

        return;
    } // end of outputClassInit(SmcFSM)

    /**
     * Writes the local variable declarations used in the class
     * initialization block.
     * @param fsm the FSM model.
     */
    private void outputLocalVars(final SmcFSM fsm)
    {
        _source.println(
            "        final Lookup lookup = MethodHandles.lookup();");
        _source.print("        final Class<?> clazz = ");
        _source.print(fsm.getFsmClassName());
        _source.println(".class;");
        _source.println(
            "        final int mapSize = MAP_NAMES.length;");
        _source.println("        int stateSize;");
        _source.println("        int mapIndex;");
        _source.println("        int stateIndex;");
        _source.println("        int transIndex;");
        _source.println("        int stateId = 0;");
        _source.println("        String mapName;");
        _source.println("        String stateName;");
        _source.println("        String transName;");
        _source.println("        String methodName;");
        _source.println("        MethodType transType;");
        _source.println("        MethodHandle mh;");
        _source.println("        MethodHandle entryHandle;");
        _source.println("        MethodHandle exitHandle;");
        _source.println("        MethodHandle[] transitions;");
        _source.println();

        return;
    } // end of outputLocalVars(SmcFSM)

    /**
     * Writes the state instance creation loop.
     */
    private void outputClassInitStateLoop()
    {
        // 5.2.6.3.1. Output the state for-loop.
        _source.println(
            "            for (stateIndex = 0; stateIndex < stateSize; ++stateIndex, ++stateId)");
        _source.println("            {");
        _source.println(
            "                stateName = STATE_NAMES[mapIndex][stateIndex];");
        _source.println(
            "                mh = findDefault(lookup, clazz, mapName, stateName);");
        _source.println(
            "                transitions = new MethodHandle[TRANSITION_COUNT];");
        _source.println(
            "                Arrays.fill(transitions, mh);");
        _source.println(
            "                methodName = String.format(ENTRY_NAME, mapName, stateName);");
        _source.println(
            "                entryHandle = lookupMethod(lookup, clazz, methodName, NO_ARGS_TYPE);");
        _source.println(
            "                methodName = String.format(EXIT_NAME, mapName, stateName);");
        _source.println(
            "                exitHandle = lookupMethod(lookup, clazz, methodName, NO_ARGS_TYPE);");
        _source.println();

        // 5.2.6.3.2. Output the transition table initialization.
        outputClassInitTransitions();

        // 5.2.6.3.3. Output the state instantiation.
        _source.println("                _States[stateId] =");
        _source.println("                    new State7(");
        _source.println(
            "                        String.format(STATE_NAME_FORMAT, mapName, stateName),");
        _source.println("                        stateId,");
        _source.println("                        entryHandle,");
        _source.println("                        exitHandle,");
        _source.println("                        transitions,");
        _source.println("                        STATE_TRANSITIONS[stateId]);");

        _source.println();
        _source.println("            }");

        return;
    } // end of outputClassInitStateLoop()

    /**
     * Writes the transition table initialization.
     */
    private void outputClassInitTransitions()
    {
        _source.println(
            "                for (transIndex = 1; transIndex < TRANSITION_COUNT; ++transIndex)");
        _source.println("                {");
        _source.println(
            "                    transName = TRANSITION_NAMES[transIndex];");
        _source.println(
            "                    transType = TRANSITION_TYPES[transIndex];");
        _source.println("                    methodName =");
        _source.println(
            "                        String.format(");
        _source.println(
            "                            TRANSITION_NAME_FORMAT, mapName, stateName, transName);");
        _source.println(
            "                    mh = lookupMethod(lookup, clazz, methodName, transType);");
        _source.println();
        _source.println(
            "                    if (mh == null)");
        _source.println("                    {");
        _source.println("                        methodName =");
        _source.println(
            "                            String.format(");
        _source.println(
            "                                TRANSITION_NAME_FORMAT, mapName, DEFAULT_NAME, transName);");
        _source.println(
            "                        mh = lookupMethod(lookup, clazz, methodName, transType);");
        _source.println("                    }");
        _source.println();
        _source.println("                    if (mh != null)");
        _source.println("                    {");
        _source.println("                        transitions[transIndex] = mh;");
        _source.println("                    }");
        _source.println("                }");
        _source.println();

        return;
    } // end of outputClassInitTransitions()

    /**
     * Writes the class closing brace and ending read-only
     * comment block.
     */
    private void outputFooter()
    {
        _source.println("}");

        _source.println();
        _source.println("/*");
        _source.println(" * Local variables:");
        _source.println(" *  buffer-read-only: t");
        _source.println(" * End:");
        _source.println(" */");

        return;
    } // end of outputFooter()

    /**
     * Writes either the state entry or exit actions.
     * @param mapName the state resides in this map.
     * @param stateName the actions belong to this state.
     * @param nameFormat the method name format.
     * @param actions either entry or exit actions.
     */
    private void outputStateActions(final String mapName,
                                    final String stateName,
                                    final String nameFormat,
                                    final List<SmcAction> actions)
    {
        final String methodName =
            String.format(nameFormat, mapName, stateName);

        _source.println();
        _source.print("    private void ");
        _source.print(methodName);
        _source.println("()");
        _source.println("    {");

        for (SmcAction action : actions)
        {
            action.accept(this);
        }

        _source.println("    }");

        return;
    } // end of outputStateActions(String, String, String, List<>)

    /**
     * Writes the transitions guarded implementations. Sets the
     * code indentation appropriately based on the number of
     * guards and conditions.
     * @param transition output this transitions guarded
     * implementations.
     * @param mapName the transition resides in this map.
     * @param stateName the transtion resides in this state.
     */
    private void outputTransitionGuards(
        final SmcTransition transition,
        final String mapName,
        final String stateName)
    {
        final List<SmcGuard> guards = transition.getGuards();
        final Iterator<SmcGuard> git = guards.iterator();
        SmcGuard guard;
        boolean nullCondition = false;

        _guardCount = guards.size();

        // If there is either more than one guard or one guard
        // with a condition, then indent the guard code one more
        // time because the guard code is inside an if-then body.
        _indent = "        ";
        if (_guardCount > 1 ||
            (_guardCount == 1 &&
             ((guards.get(0)).getCondition()).isEmpty() == false))
        {
            _indent += "    ";
        }
        // Else there are either no guards or one guard with no
        // condition.

        // 4.1. Output the "stateId" local variable in case it is
        //      needed.
        _source.println(
            "        final int stateId = _state.getId();");
        _source.println();

        // 4.2. Output each guard, tracking if there are any with
        //      no condition.
        for (_guardIndex = 0;
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = git.next();

            // Track if there is a "no condition" guard.
            nullCondition = (guard.getCondition()).isEmpty();

            guard.accept(this);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // state implementation of this transition. Pass all
        // arguments into the default transition.
        if (_guardIndex > 0 && nullCondition == false)
        {
            outputElseGuard(transition, mapName);
        }
        // Need to add a final newline after a multiguard block.
        else if (_guardCount > 1)
        {
            _source.println();
            _source.println();
        }

        return;
    } // end of outputTransitionGuards(SmcTransition, ...)

    /**
     * If a state defines a transition using guard conditions
     * only, then SMC must generate the final "else" (unguarded)
     * transition for the programmer. This else body calls the
     * default state's definition of this transition.
     * @param transition default unguarded "else" clause is for
     * this transition.
     * @param mapName the transition resides in this map.
     */
    private void outputElseGuard(final SmcTransition transition,
                                 final String mapName)
    {
        String sep = "";

        _source.println();
        _source.println("        else");
        _source.println("        {");
        _source.print("            ");
        _source.print(mapName);
        _source.print("_");
        _source.print(DEFAULT_NAME);
        _source.print("_");
        _source.print(transition.getName());
        _source.print("(");

        for (SmcParameter param : transition.getParameters())
        {
            _source.print(sep);
            _source.print(param.getName());

            sep = ", ";
        }

        _source.println(");");
        _source.println("        }");
        _source.println();

        return;
    } // end of outputElseGuard(SmcTransition, String)

    /**
     * Writes the guard condition, using an "if", "else if",
     * "else", or nothing depending on the number of guards,
     * this guard's index, and whether this guard has a condition
     * or not.
     * @param condition guard condition code.
     */
    private void outputGuardCondition(final String condition)
    {
        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            // Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an "if"
                // should be used.
                _source.print("        if (");
                _source.print(condition);
                _source.println(")");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if".
                _source.println();
                _source.print("        else if (");
                _source.print(condition);
                _source.println(")");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println();
                _source.println("        else");
            }

            _source.println("        {");
        }
        // There is only one guard. Does this guard have a
        // condition?
        else if (condition.length() > 0)
        {
            // Yes there is a condition.
            _source.print("        if (");
            _source.print(condition);
            _source.println(")");
            _source.println("        {");
        }

        return;
    } // end of outputGuardCondition(String)

    /**
     * Outputs the state exit code, surrounded by option debug
     * logging statements.
     * @param stateName exiting this state.
     */
    private void outputStateExit(final String mapName,
                                 final String stateName)
    {
        if (_debugLevel >= DEBUG_LEVEL_0)
        {
            _source.print(_indent);
            _source.println("if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print(
                "    _debugStream.println(\"LEAVING STATE   : ");
            _source.print(mapName);
            _source.print('.');
            _source.print(stateName);
            _source.println("\");");
            _source.print(_indent);
            _source.println("}");
            _source.println();
        }

        if (_debugLevel >= DEBUG_LEVEL_1)
        {
            String sep;

            _source.print(_indent);
            _source.println("if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print(
                "    _debugStream.println(\"BEFORE EXIT     : ");
            _source.print(stateName);
            _source.println(".exit()\");");
            _source.print(_indent);
            _source.println("}");
            _source.println();
        }

        _source.print(_indent);
        _source.println("exitState();");
        _source.println();

        if (_debugLevel >= DEBUG_LEVEL_1)
        {
            _source.print(_indent);
            _source.println("if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print(
                "    _debugStream.println(\"AFTER EXIT      : ");
            _source.print(stateName);
            _source.println(".exit()\");");
            _source.print(_indent);
            _source.println("}");
            _source.println();
        }

        return;
    } // end of outputStateExit(String, String)

    /**
     * Writes the guard actions, adding the transition enter and
     * exit debug logging.
     * @param guard output this guard's body.
     * @param transition the guard is part of this transition.
     * @param mapName the transition is in this map.
     * @param stateName the transition is in this state.
     */
    private void outputGuardBody(final SmcGuard guard,
                                 final SmcTransition transition,
                                 final String mapName,
                                 final String stateName)
    {
        // 3.1. Output the transition enter logging.
        outputTransitionEnter(transition, mapName, stateName);

        // 3.2. Output the guard actions.
        outputGuardActions(guard);

        // 3.3. Output the transition exit logging.
        outputTransitionExit(transition, mapName, stateName);

        return;
    } // end of outputGuardBody()

    /**
     * Writes the transition enter logging, if debug logging is
     * enabled.
     * @param trans entering this transition.
     * @param mapName the transition is in this map.
     * @param stateName the transition is in this state.
     */
    private void outputTransitionEnter(final SmcTransition trans,
                                       final String mapName,
                                       final String stateName)
    {
        // Output transition to debug stream.
        if (_debugLevel >= DEBUG_LEVEL_0)
        {
            final List<SmcParameter> parameters =
                trans.getParameters();
            final Iterator<SmcParameter> pit =
                parameters.iterator();
            String sep = "";

            _source.print(_indent);
            _source.println(
                "    if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.print(
                "        _debugStream.println(\"ENTER TRANSITION: ");
            _source.print(mapName);
            _source.print('.');
            _source.print(stateName);
            _source.print('.');
            _source.print(trans.getName());

            _source.print('(');
            while (pit.hasNext() == true)
            {
                _source.print(sep);
                (pit.next()).accept(this);

                sep = ", ";
            }
            _source.print(')');

            _source.println("\");");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        return;
    } // end of outputTransitionEnter(SmcTransition,String,String)

    /**
     * Writes the transition guard actions. If the guard has no
     * actions, then writes "// No actions.".
     */
    private void outputGuardActions(final SmcGuard guard)
    {
        final List<SmcAction> actions = guard.getActions();
        final boolean hasActions = !(actions.isEmpty());

        if (hasActions == false)
        {
            if ((guard.getCondition()).isEmpty() == false)
            {
                _source.print(_indent);
                _source.println("    // No actions.");
            }
        }
        else
        {
            // Now that we are in the transition, clear the
            // current state.
            _source.print(_indent);
            _source.println("    clearState();");

            for (SmcAction action: actions)
            {
                action.accept(this);
            }
        }

        return;
    } // end of outputGuardActions()

    /**
     * Writes the transition enter logging, if debug logging is
     * enabled.
     * @param trans entering this transition.
     * @param mapName the transition is in this map.
     * @param stateName the transition is in this state.
     */
    private void outputTransitionExit(final SmcTransition trans,
                                      final String mapName,
                                      final String stateName)
    {
        // Output transition to debug stream.
        if (_debugLevel >= DEBUG_LEVEL_0)
        {
            final List<SmcParameter> parameters =
                trans.getParameters();
            final Iterator<SmcParameter> pit =
                parameters.iterator();
            String sep = "";

            _source.println();
            _source.print(_indent);
            _source.println(
                "    if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.print(
                "        _debugStream.println(\"EXIT TRANSITION : ");
            _source.print(mapName);
            _source.print('.');
            _source.print(stateName);
            _source.print('.');
            _source.print(trans.getName());

            _source.print('(');
            while (pit.hasNext() == true)
            {
                _source.print(sep);
                (pit.next()).accept(this);

                sep = ", ";
            }
            _source.print(')');

            _source.println("\");");
            _source.print(_indent);
            _source.println("    }");
        }

        return;
    } // end of outputTransitionExit(SmcTransition,String,String)

    /**
     * Outputs the state exit code, surrounded by option debug
     * logging statements.
     * @param mapName the state resides in this map.
     * @param stateName exiting this state.
     */
    private void outputStateEnter(final String mapName,
                                  final String stateName)
    {
        if (_debugLevel >= DEBUG_LEVEL_1)
        {
            _source.println();
            _source.print(_indent);
            _source.println("if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print("    _debugStream.println(\"BEFORE ENTRY    : ");
            _source.print(stateName);
            _source.println(".entry()\");");
            _source.print(_indent);
            _source.println("}");
            _source.println();
        }

        _source.print(_indent);
        _source.println("enterState();");

        if (_debugLevel >= DEBUG_LEVEL_1)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "if (_debugFlag == true)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print(
                "    _debugStream.println(\"AFTER ENTRY     : ");
            _source.print(stateName);
            _source.println(".entry()\");");
            _source.print(_indent);
            _source.println("}");
        }

        return;
    } // end of outputStateEnter()

    /**
     * Returns the actual Java type name in the given string.
     * {@code s} may contain addition attributes besides the
     * Java type name (e.g. {@code final}). The Java type name
     * must be the last name in the string and the names are
     * separated by a space, search for the last blank in the
     * string and use the substring in front of the blank.
     * @param s extract the Java type name from this string.
     * @return a Java type name.
     */
    private String getJavaType(final String s)
    {
        final int index = s.lastIndexOf(' ');
        String retval = s;

        if (index >= 0)
        {
            retval = s.substring(index + 1);
        }

        return (retval);
    } // end of getJavaType(String)

//---------------------------------------------------------------
// Member data
//

    //-----------------------------------------------------------
    // Constants.
    //

    /**
     * The default state and transition name is "Default".
     */
    private static final String DEFAULT_NAME = "Default";

    /**
     * Loop back transitions use a "nil" end state.
     */
    private static final String NIL_STATE = "nil";

    /**
     * The constant integer state identifier suffix is
     * "_STATE_ID".
     */
    private static final String STATE_ID_SUFFIX = "_STATE_ID";

    /**
     * The constant integer transition identifier suffix is
     * "_TRANSITION_ID".
     */
    private static final String TRANSITION_ID_SUFFIX =
        "_TRANSITION_ID";
    /**
     * The state entry method name is:
     * "&lt;map&gt;_&lt;state&gt;__Entry_".
     */
    private static final String ENTRY_NAME =
        "%s_%s__Entry_";

    /**
     * The state exit method name is:
     * "&lt;map&gt;_&lt;state&gt;__Exit_".
     */
    private static final String EXIT_NAME =
        "%s_%s__Exit_";
} // end of class SmcJava7Generator
