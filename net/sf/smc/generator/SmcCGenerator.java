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
// Copyright (C) 2005, 2008 - 2009. Charles W. Rapp.
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
import java.io.StringWriter;
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
 * Visits the abstract syntax tree emitting C code to an output
 * stream.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author Francois Perrad
 */

public final class SmcCGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a C code generator for the given parameters.
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
    public SmcCGenerator(String srcfileBase,
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
               "c",
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
    } // end of SmcCGenerator(...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits the following C code for the FSM:
     * <code>
     *   <pre>
     * %{ %} raw source code - if any
     *
     * #include &lt;%include header file&gt;
     * #include "<i>context</i>_sm.h"
     *   </pre>
     * </code>
     * If the -headerd option is used, then this is generated:
     * <code>
     *   <pre>
     * #include "<i>header dir</i>/<i>context</i>_sm.h")
     *   </pre>
     * </code>
     * @param fsm Emit code for this finite state machine.
     */
    public void visit(SmcFSM fsm)
    {
        String packageName = fsm.getPackage();
        String rawSource = fsm.getSource();
        String context = fsm.getContext();
        String mapName;
        String startStateName = fsm.getStartState();
        List<SmcTransition> transList;
        String separator;
        List<SmcParameter> params;
        String declaration;
        String cState;
        int packageDepth = 0;
        int index;

        _source.println("/*");
        _source.println(" * DO NOT EDIT.");
        _source.println(" * generated by smc (http://smc.sourceforge.net/)");
        _source.print(" * from file : ");
        _source.print(_srcfileBase);
        _source.println(".sm");
        _source.println(" */");
        _source.println();

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
            context = packageName + "_" + context;
            startStateName = packageName + "_" + startStateName;
        }

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length() > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        _source.println("#include <assert.h>");

        // Generate #includes.
        for (String include: fsm.getIncludes())
        {
            _source.print("#include ");
            _source.println(include);
        }

        // Include the context file last.
        // Is the header file included in a different directory
        // than the source file?
        _source.print("#include \"");
        if ((_srcDirectory == null &&
             _headerDirectory != null) ||
            (_srcDirectory != null &&
             _headerDirectory != null &&
             _srcDirectory.equals(_headerDirectory) == false))
        {
            // They are in different directories. Prepend the
            // header directory to the file name.
            _source.print(_headerDirectory);
        }
        // Else they are in the same directory.
        else if (_srcDirectory != null)
        {
            _source.print(_srcDirectory);
        }
        _source.print(_srcfileBase);
        _source.println("_sm.h\"");

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        transList = fsm.getTransitions();

        _source.println();
        _source.println("#define getOwner(fsm) \\");
        _source.println("    (fsm)->_owner");
        _source.println();

        _source.println("#define POPULATE_STATE(state) \\");
        if (fsm.hasEntryActions() == true)
        {
            _source.println("    state##_Entry, \\");
        }
        if (fsm.hasExitActions() == true)
        {
            _source.println("    state##_Exit, \\");
        }
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.print("    state##_");
                _source.print(trans.getName());
                _source.println(", \\");
            }
        }
        _source.println("    state##_Default");


        _source.println();
        if (fsm.hasEntryActions() == true)
        {
            _source.println("#define ENTRY_STATE(state) \\");
            _source.println("    if ((state)->Entry != NULL) { \\");
            _source.println("        (state)->Entry(fsm); \\");
            _source.println("    }");
        }
        else
        {
            _source.println("#define ENTRY_STATE(state)");
        }
        _source.println();
        if (fsm.hasExitActions() == true)
        {
            _source.println("#define EXIT_STATE(state) \\");
            _source.println("    if ((state)->Exit != NULL) { \\");
            _source.println("        (state)->Exit(fsm); \\");
            _source.println("    }");
        }
        else
        {
            _source.println("#define EXIT_STATE(state)");
        }

        // Output the default transition definitions.
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print("static void ");
                _source.print(context);
                _source.print("State_");
                _source.print(trans.getName());
                _source.print("(struct ");
                _source.print(context);
                _source.print("Context *fsm");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    param.accept(this);
                }

                _source.println(")");
                _source.println("{");
                _source.println(
                    "    getState(fsm)->Default(fsm);");
                _source.println("}");
            }
        }

        _source.println();
        _source.print("static void ");
        _source.print(context);
        _source.print("State_Default(struct ");
        _source.print(context);
        _source.println("Context *fsm)");
        _source.println("{");

        // Print the transition out to the verbose log.
        if (_debugFlag == true)
        {
            _source.println("    if (getDebugFlag(fsm) != 0) {");

            // The TRACE macro.
            _source.print("        TRACE(");
            _source.print("\"TRANSITION   : %s.%s\\n\\r\", ");
            _source.println(
                "getName(getState(fsm)), getTransition(fsm));");

            _source.println("    }");
        }
        _source.println("    State_Default(fsm);");
        _source.println("}");

        // Have each map print out its source code now.
        for (SmcMap map: fsm.getMaps())
        {
            mapName = map.getName();
            if (packageName != null && packageName.length() > 0)
            {
                mapName = packageName + "_" + mapName;
            }
            _source.println();

            for (SmcState state: map.getStates())
            {
                for (SmcTransition trans: transList)
                {
                    if (trans.getName().equals(
                            "Default") == false)
                    {
                        _source.print("#define ");
                        _source.print(mapName);
                        _source.print("_");
                        _source.print(state.getInstanceName());
                        _source.print("_");
                        _source.print(trans.getName());
                        _source.print(" ");
                        _source.print(context);
                        _source.print("State_");
                        _source.println(trans.getName());
                    }
                }

                _source.print("#define ");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getInstanceName());
                _source.print("_Default ");
                _source.print(context);
                _source.println("State_Default");
                _source.print("#define ");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getInstanceName());
                _source.println("_Entry NULL");
                _source.print("#define ");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getInstanceName());
                _source.println("_Exit NULL");
            }

            for (SmcTransition trans: transList)
            {
                if (trans.getName().equals("Default") == false)
                {
                    _source.print("#define ");
                    _source.print(mapName);
                    _source.print("_DefaultState_");
                    _source.print(trans.getName());
                    _source.print(" ");
                    _source.print(context);
                    _source.print("State_");
                    _source.println(trans.getName());
                }
            }

            map.accept(this);
        }

        // The state name "map::state" must be changed to
        // "map_state".
        if ((index = startStateName.indexOf("::")) >= 0)
        {
            cState =
                    startStateName.substring(0, index) +
                    "_" +
                startStateName.substring(index + 2);
        }
        else
        {
            cState = startStateName;
        }

        // Constructor.
        _source.println();
        _source.print("void ");
        _source.print(context);
        _source.print("Context_Init");
        _source.print("(struct ");
        _source.print(context);
        _source.print("Context* fsm, struct ");
        _source.print(context);
        _source.println("* owner)");
        _source.println("{");
        _source.print("    FSM_INIT(fsm, &");
        _source.print(cState);
        _source.println(");");
        _source.println("    fsm->_owner = owner;");
        _source.println("}");

        // EnterStartState method.
        _source.println();
        _source.print("void ");
        _source.print(context);
        _source.print("Context_EnterStartState(struct ");
        _source.print(context);
        _source.println("Context* fsm)");
        _source.println("{");
        _source.println("    ENTRY_STATE(getState(fsm));");
        _source.println("}");

        // Generate the context class.
        // Generate a method for every transition in every map
        // *except* the default transition.
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print("void ");
                _source.print(context);
                _source.print("Context_");
                _source.print(trans.getName());
                _source.print("(struct ");
                _source.print(context);
                _source.print("Context* fsm");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getType());
                    _source.print(" ");
                    _source.print(param.getName());
                }
                _source.println(")");
                _source.println("{");

                _source.print("    const struct ");
                _source.print(context);
                _source.println("State* state = getState(fsm);");
                _source.println();

                _source.println("    assert(state != NULL);");
                _source.print("    setTransition(fsm, \"");
                _source.print(trans.getName());
                _source.println("\");");
                _source.print("    state->");
                _source.print(trans.getName());
                _source.print("(fsm");
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(");");
                _source.println("    setTransition(fsm, NULL);");

                _source.println("}");
            }
        }

        return;
    } // end of visit(SmcFSM)

    /**
     * Emits C code for the FSM map. Visits the map default
     * state first (if there is one) and then each of the map's
     * states.
     * @param map emit code for this FSM map.
     */
    public void visit(SmcMap map)
    {
        String packageName = map.getFSM().getPackage();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String transName;
        String stateName;

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
            context = packageName + "_" + context;
            mapName = packageName + "_" + mapName;
        }

        // Declare the user-defined default transitions first.
        if (map.hasDefaultState() == true)
        {
            SmcState defaultState = map.getDefaultState();

            for (SmcTransition trans:
                     defaultState.getTransitions())
            {
                transName = trans.getName();

                _source.println();

                for (SmcState state: map.getStates())
                {
                    stateName = state.getInstanceName();

                    // Firstly, make sure the transition name
                    // is not defined.
                    _source.print("#undef ");
                    _source.print(mapName);
                    _source.print("_");
                    _source.print(stateName);
                    _source.print("_");
                    _source.println(transName);

                    // Secondly, define the transition name
                    // to be a default transition alias.
                    _source.print("#define ");
                    _source.print(mapName);
                    _source.print("_");
                    _source.print(stateName);
                    _source.print("_");
                    _source.print(transName);
                    _source.print(" ");
                    _source.print(mapName);
                    _source.print("_DefaultState_");
                    _source.println(transName);
                }

                _source.print("#undef ");
                _source.print(mapName);
                _source.print("_DefaultState_");
                _source.println(transName);

                trans.accept(this);
            }
        }

        // Have each state now generate its code.
        for (SmcState state: map.getStates())
        {
            stateName = state.getInstanceName();

            state.accept(this);

            _source.println();
            _source.print("const struct ");
            _source.print(context);
            _source.print("State ");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print(" = { POPULATE_STATE(");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print("), \"");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print("\", ");
            _source.print(
                Integer.toString(map.getNextStateId()));
            _source.println(" };");
        }

        return;
    } // end of visit(SmcMap)

    /**
     * Emits C code for this FSM state. Generates entry and exit
     * action routines first (if any) and then the transition
     * routines.
     * @param state emit C code for this FSM state.
     */
    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String packageName = map.getFSM().getPackage();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String instanceName = state.getInstanceName();
        String indent2;
        List<SmcAction> actions;

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
            context = packageName + "_" + context;
            mapName = packageName + "_" + mapName;
        }

        _context = context;

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print("#undef ");
            _source.print(mapName);
            _source.print("_");
            _source.print(instanceName);
            _source.println("_Entry");
            _source.print("void ");
            _source.print(mapName);
            _source.print("_");
            _source.print(instanceName);
            _source.print("_Entry(struct ");
            _source.print(context);
            _source.println("Context *fsm)");
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print("    struct ");
            _source.print(context);
            _source.println(" *ctxt = getOwner(fsm);");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent2;

            // End the Entry() method.
            _source.println("}");
        }

        actions = state.getExitActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print("#undef ");
            _source.print(mapName);
            _source.print("_");
            _source.print(instanceName);
            _source.println("_Exit");
            _source.print("void ");
            _source.print(mapName);
            _source.print("_");
            _source.print(instanceName);
            _source.print("_Exit(struct ");
            _source.print(context);
            _source.println("Context *fsm)");
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print("    struct ");
            _source.print(context);
            _source.println(" *ctxt = getOwner(fsm);");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent2;

            // End the Entry() method.
            _source.println("}");
        }

        // Have the transitions generate their code.
        for (SmcTransition transition: state.getTransitions())
        {
            transition.accept(this);
        }

        return;
    } // end of visit(SmcState)

    /**
     * Emits C code for this FSM transition. Generates the
     * transition subroutine and then each of the guards within
     * that routine.
     * @param transition emit C code for this FSM transition.
     */
    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String packageName = map.getFSM().getPackage();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getInstanceName();
        String transName = transition.getName();
        boolean nullCondition = false;
        List<SmcGuard> guards = transition.getGuards();
        Iterator<SmcGuard> git;
        SmcGuard guard;

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
            context = packageName + "_" + context;
            mapName = packageName + "_" + mapName;
        }

        if (stateName.equals("DefaultState") == false)
        {
            _source.println();
            _source.print("#undef ");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print("_");
            _source.println(transName);
        }

        _source.print("static void ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.print("_");
        _source.print(transName);
        _source.print("(struct ");
        _source.print(context);
        _source.print("Context *fsm");

        // Add user-defined parameters.
        for (SmcParameter parameter: transition.getParameters())
        {
            _source.print(", ");
            parameter.accept(this);
        }

        _source.println(")");
        _source.println("{");

        // All transitions have a "ctxt" local variable.
        // 8/14/2003:
        // Do this only if there are any transition actions or
        // guard conditions which reference it.
        if (transition.hasCtxtReference() == true)
        {
            _source.print("    struct ");
            _source.print(context);
            _source.println("* ctxt = getOwner(fsm);");
        }

        // ANSI C requires all local variables be declared
        // at the code block's start before any control
        // statements. If this transition appears only once
        // in the state, has at least one action and it is a
        // loopback and debugging is on, then visit(SmcGuard)
        // will generate a local variable declaration after the
        // debug if clause - an ANSI syntax error.
        // So we need to check if this transition meets that
        // condition and generate the local variable declaration
        // here rather than in visit(SmcGuard).
        //
        // Note: when guard count is > 1, then the guard code
        // is placed into an if or else block - and so the
        // end state variable will appear at the start of that
        // block, nullifying the debug if clauses affect.
        _guardCount = guards.size();
        if (_guardCount == 1)
        {
            guard = (SmcGuard) guards.get(0);

            if (guard.getActions().isEmpty() == false &&
                isLoopback(
                    guard.getTransType(), guard.getEndState())
                        == true)
            {
                _source.print("    const struct ");
                _source.print(context);
                _source.println(
                    "State* EndStateName = getState(fsm);");
            }
        }

        _source.println();

        // Print the transition to the verbose log.
        if (_debugFlag == true)
        {
            Iterator<SmcParameter> pit;
            SmcParameter param;
            String sep;

            _source.println("    if (getDebugFlag(fsm) != 0) {");
            _source.print("        TRACE(\"TRANSITION   : ");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.print(".");
            _source.print(transName);
            _source.print("(");

            for (pit = transition.getParameters().iterator(),
                     sep = "";
                 pit.hasNext() == true;
                 sep = ", ")
            {
                param = pit.next();
                _source.print(sep);
                _source.print(param.getName());
            }

            _source.println(")\\n\\r\");");

            _source.println("    }");
        }

        // Loop through the guards and print each one.
        for (git = guards.iterator(), _guardIndex = 0;
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = git.next();

            // Count up the number of guards with no condition.
            if (guard.getCondition().length() == 0)
            {
                nullCondition = true;
            }

            guard.accept(this);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition.
        if (_guardIndex > 0 && nullCondition == false)
        {
            // If there is only one transition definition, then
            // close off the guard.
            if (_guardCount == 1)
            {
                _source.println("    }");
            }

            _source.println("    else {");
            _source.print("        ");
            _source.print(mapName);
            _source.print("_DefaultState_");
            _source.print(transName);
            _source.print("(fsm");

            // Output user-defined parameters.
            for (SmcParameter param: transition.getParameters())
            {
                _source.print(", ");
                _source.print(param.getName());
            }
            _source.println(");");
            _source.println("    }");
        }
        else if (_guardCount > 1)
        {
            _source.println();
        }

        _source.println("}");

        return;
    } // end of visit(SmcTransition)

    /**
     * Emits C code for this FSM guard. If the guard has a
     * condition, then the condition is placed inside an
     * {@code [else] if} statement. The exit actions, transition
     * actions, setting the next state and entry actions are
     * called from within the condition body. If the condition
     * has no condition but there are other guards, then the
     * guard actions are placed within an {@code else} body.
     * If the guard has no condition and there are no other
     * guards, then the guard action code is emitted within the
     * transition routine block.
     * @param guard emit C code for this transition guard.
     */
    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String packageName = map.getFSM().getPackage();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getInstanceName();
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

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
            context = packageName + "_" + context;
            mapName = packageName + "_" + mapName;
        }

        _context = context;

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the state
        // name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state" is actually a transition name.
        if (transType != TransType.TRANS_POP &&
            endStateName.length () > 0 &&
            endStateName.equalsIgnoreCase(
                SmcElement.NIL_STATE) == false)
        {
            endStateName =
                "&" + scopeStateName(endStateName, mapName);
        }

        // Qualify the state and push state names as well.
        stateName = "&" + scopeStateName(stateName, mapName);
        pushStateName = scopeStateName(pushStateName, mapName);
        if (packageName != null && packageName.length() > 0)
        {
            pushStateName =
                "&" + packageName + "_" + pushStateName;
        }
        else
        {
            pushStateName = "&" + pushStateName;
        }

        loopbackFlag = isLoopback(transType, endStateName);

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            indent2 = _indent + "        ";

            // More than one guard. Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an
                // "if" should be used for this condition.
                _source.print(_indent);
                _source.print("    if (");
                _source.print(condition);
                _source.println(") {");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if" for
                // the condition.
                _source.println();
                _source.print(_indent);
                _source.print("    else if (");
                _source.print(condition);
                _source.println(") {");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println();
                _source.print(_indent);
                _source.println("    else {");
            }
        }
        else
        {
            // There is only one guard. Does this guard have a
            // condition.
            if (condition.length() == 0)
            {
                // Actually, this is a plain, old, vaniila
                // transition.
                indent2 = _indent + "    ";
            }
            else
            {
                // Yes, there is a condition.
                _source.print(_indent);
                _source.print("    if (");
                _source.print(condition);
                _source.println(") {");
                indent2 = _indent + "        ";
            }
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transitions actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (actions.size() == 0 && endStateName.length() > 0)
        {
            fqEndStateName = endStateName;
        }
        else if (actions.size() > 0)
        {
            // Save away the current state if this is a loopback
            // transition. Storing current state allows the
            // current to be cleared before any actions are
            // executed. Remember: actions are not allowed to
            // issue transitions and clearing the current state
            // prevents them from doing so.
            if (loopbackFlag == true)
            {
                fqEndStateName = "EndStateName";

                // Generate this declaration only if this
                // transition has multiple guards. If this
                // is the only guard, then this local variable
                // declaration will appear after the first
                // control statement - which is an ANSI C
                // syntax error.
                // If there is only one guard, then this code
                // is generated by visit(SmcTransition) before
                // the debug code is generated.
                // If there are multiple guards, then this code
                // appears at the start of an if, else if or else
                // code block which is acceptable ANSI C.
                if (_guardCount > 1)
                {
                    _source.print(indent2);
                    _source.print("const struct ");
                    _source.print(context);
                    _source.print("State* ");
                    _source.print(fqEndStateName);
                    _source.println(" = getState(fsm);");
                    _source.println();
                }
            }
            else
            {
                fqEndStateName = endStateName;
            }
        }

        // Before doing anything else, perform the current
        // state's exit actions.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (transType == TransType.TRANS_POP ||
            loopbackFlag == false)
        {
            _source.print(indent2);
            _source.println("EXIT_STATE(getState(fsm));");
        }

        if (actions.isEmpty() == false)
        {
            // Now that we are in the transition, clear the
            // current state.
            _source.print(indent2);
            _source.println("clearState(fsm);");
        }

        // Dump out this transition's actions.
        if (actions.isEmpty() == true)
        {
            if (condition.length() > 0)
            {
                _source.print(indent2);
                _source.println("/* No actions. */");
            }
        }
        else
        {
            indent4 = _indent;
            _indent = indent2;
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent4;
        }
        indent3 = indent2;

        // Print the setState() call, if necessary. Do NOT
        // generate the set state if:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        if (transType == TransType.TRANS_SET &&
            (actions.isEmpty() == false ||
             loopbackFlag == false))
        {
            _source.print(indent3);
            _source.print("setState(fsm, ");
            _source.print(fqEndStateName);
            _source.println(");");
        }
        else if (transType == TransType.TRANS_PUSH)
        {
            // Set the end state so that it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false ||
                actions.isEmpty() == false)
            {
                _source.print(indent3);
                _source.print("setState(fsm, ");
                _source.print(fqEndStateName);
                _source.println(");");
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                _source.print(indent3);
                _source.println("ENTRY_STATE(getState(fsm));");
            }

            _source.print(indent3);
            _source.print("pushState(fsm, ");
            _source.print(pushStateName);
            _source.println(");");
        }
        else if (transType == TransType.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("popState(fsm);");
        }

        // Perform the new state's entry actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((transType == TransType.TRANS_SET &&
             loopbackFlag == false) ||
             transType == TransType.TRANS_PUSH)
        {
            _source.print(indent2);
            _source.println("ENTRY_STATE(getState(fsm));");
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == TransType.TRANS_POP &&
            endStateName.equalsIgnoreCase(
                SmcElement.NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.print(indent2);
            _source.print(context);
            _source.print("Context_");
            _source.print(endStateName);
            _source.print("(fsm");

            // Output any and all pop arguments.
            if (popArgs.length() > 0)
            {
                _source.print(", ");
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
     * Emits C code for this FSM action. The emitted C code has
     * the format:
     * <code>
     *   <pre>
     * <i>action</i>(ctxt[, <i>arg0</i>, ... , <i>argn</i>]);
     *   </pre>
     * </code>
     * @param action emit C code for this action.
     */
    public void visit(SmcAction action)
    {
        String name = action.getName();

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        _source.print(_indent);
        if (name.equals("emptyStateStack") == true)
        {
            _source.print(name);
            _source.print("(fsm");
        }
        else
        {
            _source.print(_context);
            _source.print("_");
            _source.print(name);
            _source.print("(ctxt");
        }

        for (String arg: action.getArguments())
        {
            if (arg.length() > 0)
            {
                _source.print(", ");
                _source.print(arg);
            }
        }

        _source.println(");");

        return;
    } // end of visit(SmcAction)

    /**
     * Emits C code for this transition parameter. The emitted
     * C code has the format:
     * <code>
     *   <pre>
     * <i>type</i> <i>name</i>
     *   </pre>
     * </code>
     * @param parameter emit C code for this transition
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

    /**
     * Returns the scoped state name. If the state is
     * unscoped, then returns
     * {@code <i>mapName</i>.<i>stateName</i>}. Replaces
     * any "::" with "_" in the state name.
     * @param stateName the state name.
     * @param mapName the map name.
     * @return the scoped state name.
     */
    @Override
    protected String scopeStateName(String stateName,
                                    String mapName)
    {
        int index;
        StringWriter retval = new StringWriter();

        index = stateName.indexOf("::");
        if (index < 0)
        {
            retval.write(mapName);
            retval.write("_");
            retval.write(stateName);
        }
        else
        {
            retval.write(stateName.substring(0, index));
            retval.write('_');
            retval.write(stateName.substring(index + 2));
        }

        return (retval.toString());
    } // end of scopeStateName(String, String)

//---------------------------------------------------------------
// Member data
//

    // This FSM belongs to this "class".
    private String _context;
} // end of class SmcCGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.15  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language
// generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.14  2008/02/08 08:46:02  fperrad
// C : optimize footprint when no Entry action or no Exit action
//
// Revision 1.13  2008/02/04 10:26:51  fperrad
// Don't generate cuddled else
//
// Revision 1.12  2007/12/28 12:34:41  cwrapp
// Version 5.0.1 check-in.
//
// Revision 1.11  2007/11/19 18:48:54  fperrad
// fix C generation :
// default state must be always 'DefaultState' (previously 'Default')
//
// Revision 1.10  2007/08/05 14:50:54  cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more information.
//
// Revision 1.9  2007/08/05 14:26:25  cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more informaiton.
//
// Revision 1.8  2007/07/16 06:13:21  fperrad
// + Added the generation of a DO NOT EDIT comment
//
// Revision 1.7  2007/02/21 13:53:57  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.6  2007/01/15 00:23:50  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.5  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.4  2006/07/11 18:11:41  cwrapp
// Added support for new -headerd command line option.
//
// Revision 1.3  2006/04/22 12:45:26  cwrapp
// Version 4.3.1
//
// Revision 1.2  2005/11/07 19:34:54  cwrapp
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
// Revision 1.1  2005/06/16 18:11:01  fperrad
// Added C, Perl & Ruby generators.
//
//
