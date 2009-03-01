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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
 * Visits the abstract syntax tree, emitting a Graphviz diagram.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcGraphGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a GraphViz code generator for the given
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
    public SmcGraphGenerator(String srcfileBase,
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
               "dot",
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

        _parameters = null;
    } // end of SmcGraphGenerator(...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits GraphViz code for the finite state machine.
     * @param fsm emit GraphViz code for this finite state machine.
     */
    public void visit(SmcFSM fsm)
    {
        // Create one overall graph and place each map in a
        // subgraph.
        _source.println("digraph ");
        _source.print(_srcfileBase);
        _source.println(" {");
        _source.println();
        _source.println("    node");
        _source.println("        [shape=record];");
        _source.println();

        // Have each map generate its subgraph.
        for (SmcMap map: fsm.getMaps())
        {
            _source.print("    subgraph cluster_");
            _source.print(map.getName());
            _source.println(" {");

            map.accept(this);

            // Output the subgraph's closing brace.
            _source.println("    }");
            _source.println();
        }

        // Output the digraph's closing brace.
        _source.println("}");

        return;
    } // end of visit(SmcFSM)

    /**
     * Emits GraphViz code for the FSM map.
     * @param map emit GraphViz code for this map.
     */
    public void visit(SmcMap map)
    {
        String mapName = map.getName();
        SmcState defaultState = map.getDefaultState();
        List<SmcTransition> transitions = map.getTransitions();
        String popArgs;

        _source.println();
        _source.print("        label=\"");
        _source.print(mapName);
        _source.println("\";");
        _source.println();
        _source.print("        //");
        _source.println(
            "-------------------------------------------------------");
        _source.println("        // States (Nodes)");
        _source.println("        //");
        _source.println();

        // Output the state names first.
        for (SmcState state: map.getStates())
        {
            state.accept(this);
        }

        // Output the default state - if there is one and it
        // has some transitions.
        if (defaultState != null &&
            defaultState.getTransitions().isEmpty() == false)
        {
            defaultState.accept(this);
        }

        // Now output the pop transitions as "nodes".
        for (SmcTransition transition: transitions)
        {
            for (SmcGuard guard: transition.getGuards())
            {
                if (guard.getTransType() == TransType.TRANS_POP)
                {
                    // Graph Level 0, 1: Output the pop
                    // transition.
                    // Graph Level 2: Output the pop arguments.
                    _source.print("        \"");
                    _source.print(mapName);
                    _source.print("::pop(");
                    _source.print(guard.getEndState());

                    if (_graphLevel == GRAPH_LEVEL_2 &&
                        (popArgs = guard.getPopArgs()) != null &&
                        popArgs.length() > 0)
                    {
                        _source.print(", ");
                        _source.print(escape(popArgs));
                    }

                    _source.println(")\"");
                    _source.println(
                        "            [shape=plaintext];");
                    _source.println();
                }
            }
        }

        _source.print("        //");
        _source.println(
            "-------------------------------------------------------");
        _source.println("        // Transitions (Edges)");
        _source.println("        //");

        // For each state, output its transitions.
        for (SmcState state: map.getStates())
        {
            for (SmcTransition transition:
                     state.getTransitions())
            {
                transition.accept(this);
                _source.println();
            }
        }

        // Have the default state output its transitions last.
        if (defaultState != null)
        {
            for (SmcTransition transition:
                     defaultState.getTransitions())
            {
                transition.accept(this);
                _source.println();
            }
        }

        return;
    } // end of visit(SmcMap)

    /**
     * Emits GraphViz code for this FSM state.
     * @param state emits GraphViz code for this state.
     */
    public void visit(SmcState state)
    {
        String mapName = state.getMap().getName();
        String instanceName = state.getInstanceName();

        // The state name must be fully-qualified because
        // Graphviz does not allow subgraphs to share node names.
        // Place the node name in quotes.
        _source.print("        \"");
        _source.print(mapName);
        _source.print("::");

        if (instanceName.equals("DefaultState") == true)
        {
            _source.print("Default");
        }
        else
        {
            _source.print(instanceName);
        }
        _source.println("\"");

        // For graph levels 0 and 1, output just the state name.
        if (_graphLevel < GRAPH_LEVEL_2)
        {
            _source.println("            [label=\"\\N\"];");
        }
        // For graph level 2, output the state name, entry and
        // exit actions.
        else
        {
            List<SmcAction> actions;
            Iterator<SmcAction> it;
            String sep;

            _source.print("            [label=\"{\\N| Entry:");

            // Output the entry actions, one per line.
            actions = state.getEntryActions();
            if (actions != null && actions.isEmpty() == false)
            {
                for (it = actions.iterator(), sep = " ";
                     it.hasNext() == true;
                     sep = "\\l")
                {
                    _source.print(sep);
                    (it.next()).accept(this);
                }
            }

            _source.print("| Exit:");

            // Output the exit actions, one per line.
            actions = state.getExitActions();
            if (actions != null && actions.isEmpty() == false)
            {
                for (it = actions.iterator(), sep = " ";
                     it.hasNext() == true;
                     sep = "\\l")
                {
                    _source.print(sep);
                    (it.next()).accept(this);
                }
            }

            _source.println("}\"];");
        }

        _source.println();

        return;
    } // end of visit(SmcState)

    /**
     * Emits GraphViz code for this FSM state transition.
     * @param transition emits GraphViz code for this state transition.
     */
    public void visit(SmcTransition transition)
    {
        // Graph level 2: add parameters to transition name.
        // Generate the parameters once and pass the string to
        // the guards in the "package" argument.
        if (_graphLevel == GRAPH_LEVEL_2)
        {
            List<SmcParameter> parameters =
                transition.getParameters();
            Iterator<SmcParameter> pit;

            if (parameters.isEmpty() == true)
            {
                _parameters = null;
            }
            else
            {
                ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();
                PrintStream pstream = _source;
                String sep;

                _source = new PrintStream(baos);
                for (pit = parameters.iterator(), sep = "";
                     pit.hasNext() == true;
                     sep = ", ")
                {
                    (pit.next()).accept(this);
                }

                _parameters = baos.toString();
                _source = pstream;
            }
        }

        for (SmcGuard guard: transition.getGuards())
        {
            _source.println();
            guard.accept(this);
        }

        return;
    } // end of visit(SmcTransition)

    /**
     * Emits GraphViz code for this FSM transition guard.
     * @param guard emits GraphViz code for this transition guard.
     */
    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String mapName = map.getName();
        String stateName = state.getInstanceName();
        String transName = transition.getName();
        TransType transType = guard.getTransType();
        String endStateName = guard.getEndState();
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List<SmcAction> actions = guard.getActions();

        _source.print("        \"");
        _source.print(mapName);
        _source.print("::");
        _source.print(stateName);
        _source.print("\" -> ");

        if (transType != TransType.TRANS_POP)
        {
            if (endStateName.equals(
                    SmcElement.NIL_STATE) == true)
            {
                endStateName = mapName + "::" + stateName;
            }
            else if (endStateName.indexOf("::") < 0)
            {
                endStateName = mapName + "::" + endStateName;
            }

            _source.print("\"");
            _source.print(endStateName);
            _source.println("\"");
        }
        else
        {
            String popArgs = guard.getPopArgs();

            _source.print("\"");
            _source.print(mapName);
            _source.print("::pop(");
            _source.print(endStateName);

            if (_graphLevel == GRAPH_LEVEL_2 &&
                popArgs != null &&
                popArgs.length() > 0)
            {
                _source.print(", ");
                _source.print(escape(popArgs));
            }

            _source.println(")\"");
        }

        _source.print("            [label=\"");
        _source.print(transName);

        // Graph Level 2: Output the transition parameters.
        if (_graphLevel == GRAPH_LEVEL_2 &&
            _parameters != null &&
            _parameters.length() > 0)
        {
            _source.print("(");
            _source.print(_parameters);
            _source.print(")");
        }

        // Graph Level 1, 2: Output the guard.
        if (_graphLevel > GRAPH_LEVEL_0 &&
            condition != null &&
            condition.length() > 0)
        {
            String continueLine = "\\\\";

            _source.print("\\l\\[");

            // If the condition contains line separators,
            // then replace them with a "\n" so Graphviz knows
            // about the line separation.
            // 4.3.0: First escape the condition then replace the
            //        line separators.
            _source.print(
                escape(condition).replaceAll(
                    "\\n", "\\\\\\n"));

            _source.print("\\]");
        }

        if (transType == TransType.TRANS_PUSH)
        {
            _source.print("/\\lpush(");
            _source.print(pushStateName);
            _source.print(")");
        }

        // Graph Level 1, 2: output actions.
        if (_graphLevel > GRAPH_LEVEL_0 &&
            actions != null &&
            actions.isEmpty() == false)
        {
            _source.print("/\\l");

            for (SmcAction action: actions)
            {
                action.accept(this);
                _source.print("\\l");
            }
        }

        _source.print("\"];");

        return;
    } // end of visit(SmcGuard)

    /**
     * Emits GraphViz code for this FSM action.
     * @param action emits GraphViz code for this action.
     */
    public void visit(SmcAction action)
    {
        // Actions are only reported for graph levels 1 and 2.
        // Graph level 1: only the action name, no arguments.
        // Graph level 2: action name and arguments.
        //
        // Note: do not output an end-of-line.
        if (_graphLevel >= GRAPH_LEVEL_1)
        {
            _source.print(action.getName());

            if (_graphLevel == GRAPH_LEVEL_2)
            {
                Iterator<String> it;
                String arg;
                String sep;

                _source.print("(");

                // Now output the arguments.
                for (it = action.getArguments().iterator(),
                         sep = "";
                     it.hasNext() == true;
                     sep = ", ")
                {
                    arg = (it.next()).trim();

                    _source.print(sep);

                    // If the argument is a quoted string, then
                    // the quotes must be escaped.
                    // First, replace all backslashes with two
                    // backslashes.
                    arg = arg.replaceAll("\\\\", "\\\\\\\\");

                    // Then replace all double quotes with
                    // a backslash double qoute.
                    _source.print(
                        arg.replaceAll("\"", "\\\\\""));
                }

                _source.print(")");
            }

            _source.print(';');
        }

        return;
    } // end of visit(SmcAction)

    /**
     * Emits GraphViz code for this transition parameter.
     * @param parameter emits GraphViz code for this transition parameter.
     */
    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getName());
        _source.print(": ");
        _source.print(parameter.getType());

        return;
    } // end of visit(SmcParameter)

    //
    // end of SmcVisitor Abstract Method Impelementation.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member data
//

    // Store the serialized parameters here.
    private String _parameters;
} // end of class SmcGraphGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.8  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.7  2007/12/28 12:34:41  cwrapp
// Version 5.0.1 check-in.
//
// Revision 1.6  2007/02/21 13:54:51  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.5  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.4  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.3  2006/07/11 18:13:33  cwrapp
// Changed method getGraphLevel() to graphLevel().
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
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.1  2005/02/21 15:35:19  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.0  2005/02/03 17:10:52  charlesr
// Initial revision
//
