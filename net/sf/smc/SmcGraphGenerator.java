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
// Copyright (C) 2005. Charles W. Rapp.
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

package net.sf.smc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

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

    public SmcGraphGenerator(String srcfileBase)
    {
        super (srcfileBase, "{0}{1}_sm.{2}", "dot");

        _parameters = null;
    } // end of SmcGraphGenerator(String)

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
            String mapName = map.getName();

            _source.print("    subgraph cluster_");
            _source.print(mapName);
            _source.println(" {");
            _source.println();
            _source.print("        label=\"");
            _source.print(mapName);
            _source.println("\";");
            _source.println();

            map.accept(this);

            // Output the subgraph's closing brace.
            _source.println("    }");
            _source.println();
        }

        // Output the digraph's closing brace.
        _source.println("}");

        return;
    } // end of visit(SmcFSM)

    public void visit(SmcMap map)
    {
        String mapName = map.getName();
        int graphLevel = Smc.graphLevel();
        SmcState defaultState = map.getDefaultState();
        List<SmcTransition> transitions = map.getTransitions();
        String popArgs;

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
                if (guard.getTransType() == Smc.TRANS_POP)
                {
                    // Graph Level 0, 1: Output the pop
                    // transition.
                    // Graph Level 2: Output the pop arguments.
                    _source.print("        \"");
                    _source.print(mapName);
                    _source.print("::pop(");
                    _source.print(guard.getEndState());

                    if (graphLevel == Smc.GRAPH_LEVEL_2 &&
                        (popArgs = guard.getPopArgs()) != null &&
                        popArgs.length() > 0)
                    {
                        _source.print(", ");
                        _source.print(Smc.escape(popArgs));
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
            }
        }

        // Have the default state output its transitions last.
        if (defaultState != null)
        {
            for (SmcTransition transition:
                     defaultState.getTransitions())
            {
                transition.accept(this);
            }
        }

        return;
    } // end of visit(SmcMap)

    public void visit(SmcState state)
    {
        int graphLevel = Smc.graphLevel();
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

        // Output the state name.
        _source.print("            [label=\"{\\N");

        // For graph level 1 & 2, output entry and exit actions.
        if (graphLevel >= Smc.GRAPH_LEVEL_1)
        {
            List<SmcAction> actions;
            Iterator<SmcAction> it;
            String sep;

            actions = state.getEntryActions();
            if (actions != null)
            {
                _source.print("| Entry/");

                // Output the entry actions, one per line.
                for (SmcAction action: actions)
                {
                    _source.print("\\l");
                    action.accept(this);
                }
                _source.print("\\l");
            }

            actions = state.getExitActions();
            if (actions != null)
            {
                _source.print("| Exit/");

                // Output the exit actions, one per line.
                for (SmcAction action: actions)
                {
                    _source.print("\\l");
                    action.accept(this);
                }
                _source.print("\\l");
            }

            for (SmcTransition transition: state.getTransitions())
            {
                for (SmcGuard guard: transition.getGuards())
                {
                    String endStateName = guard.getEndState();
                    int transType = guard.getTransType();

                    if (isLoopback(transType, instanceName, endStateName))
                    {
                        String transName = transition.getName();
                        String condition = guard.getCondition();
                        actions = guard.getActions();

                        _source.print("| ");
                        _source.print(transName);

                        // Graph Level 2: Output the transition parameters.
                        if (graphLevel == Smc.GRAPH_LEVEL_2)
                        {
                            List<SmcParameter> parameters = transition.getParameters();
                            Iterator<SmcParameter> pit;

                            _source.print("(");
                            for (pit = parameters.iterator(), sep = "";
                                 pit.hasNext() == true;
                                 sep = ", ")
                            {
                                _source.print(sep);
                                (pit.next()).accept(this);
                            }
                            _source.print(")");
                        }

                        // Output the guard.
                        if (condition != null && condition.length() > 0)
                        {
                            String tmp = Smc.escape(condition);

                            // If the condition contains line separators,
                            // then replace them with a "\n" so Graphviz knows
                            // about the line separation.
                            tmp = tmp.replaceAll("\\n", "\\\\\\n");

                            // Not needed when label in edge !!
                            tmp = tmp.replaceAll(">", "\\\\>");
                            tmp = tmp.replaceAll("<", "\\\\<");

                            _source.print("\\l\\[");
                            _source.print(tmp);
                            _source.print("\\]");
                        }

                        _source.print("/");

                        if (actions != null)
                        {
                            // Output the actions, one per line.
                            for (SmcAction action: actions)
                            {
                                _source.print("\\l");
                                action.accept(this);
                            }
                            _source.print("\\l");
                        }
                    }
                }
            }
        }

        _source.println("}\"];");
        _source.println();

        return;
    } // end of visit(SmcState)

    public void visit(SmcTransition transition)
    {
        // Graph level 2: add parameters to transition name.
        // Generate the parameters once and pass the string to
        // the guards in the "package" argument.
        if (Smc.graphLevel() == Smc.GRAPH_LEVEL_2)
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
            guard.accept(this);
        }

        return;
    } // end of visit(SmcTransition)

    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String mapName = map.getName();
        String stateName = state.getInstanceName();
        String transName = transition.getName();
        int transType = guard.getTransType();
        String endStateName = guard.getEndState();
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        int graphLevel = Smc.graphLevel();
        List<SmcAction> actions = guard.getActions();

        // Loopback are added in the state
        if (isLoopback(transType, stateName, endStateName))
        {
            return;
        }

        _source.println();
        _source.print("        \"");
        _source.print(mapName);
        _source.print("::");
        _source.print(stateName);
        _source.print("\" -> ");

        if (transType != Smc.TRANS_POP)
        {
            if (endStateName.indexOf("::") < 0)
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

            if (graphLevel == Smc.GRAPH_LEVEL_2 &&
                popArgs != null &&
                popArgs.length() > 0)
            {
                _source.print(", ");
                _source.print(Smc.escape(popArgs));
            }

            _source.println(")\"");
        }

        _source.print("            [label=\"");
        _source.print(transName);

        // Graph Level 2: Output the transition parameters.
        if (graphLevel == Smc.GRAPH_LEVEL_2 &&
            _parameters != null &&
            _parameters.length() > 0)
        {
            _source.print("(");
            _source.print(_parameters);
            _source.print(")");
        }

        // Graph Level 1, 2: Output the guard.
        if (graphLevel > Smc.GRAPH_LEVEL_0 &&
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
                Smc.escape(condition).replaceAll(
                    "\\n", "\\\\\\n"));

            _source.print("\\]");
        }

        _source.print("/");

        if (transType == Smc.TRANS_PUSH)
        {
            _source.print("\\lpush(");
            _source.print(pushStateName);
            _source.print(")");
        }

        // Graph Level 1, 2: output actions.
        if (graphLevel > Smc.GRAPH_LEVEL_0 &&
            actions != null)
        {
            for (SmcAction action: actions)
            {
                _source.print("\\l");
                action.accept(this);
            }
            _source.print("\\l");
        }

        _source.println("\"];");

        return;
    } // end of visit(SmcGuard)

    public void visit(SmcAction action)
    {
        int graphLevel = Smc.graphLevel();

        // Actions are only reported for graph levels 1 and 2.
        // Graph level 1: only the action name, no arguments.
        // Graph level 2: action name and arguments.
        //
        // Note: do not output an end-of-line.
        if (graphLevel >= Smc.GRAPH_LEVEL_1)
        {
            _source.print(action.getName());

            if (graphLevel == Smc.GRAPH_LEVEL_2)
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

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getName());
        _source.print(": ");
        _source.print(parameter.getType());

        return;
    } // end of visit(SmcParameter)

//---------------------------------------------------------------
// Member data
//

    // Store the serialized parameters here.
    private String _parameters;
} // end of class SmcGraphGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.13  2008/07/30 07:07:23  fperrad
// + fix : escape condition guard in loopback
//
// Revision 1.12  2008/07/27 15:54:37  fperrad
// + refactor with isLoopback()
//
// Revision 1.11  2008/07/26 07:42:35  fperrad
// + draw loopback (internal event) in state instead as transition
//
// Revision 1.10  2008/07/25 11:22:20  fperrad
// + in level 2, don't draw entry/exit when no action
// + in level 1, draw entry & exit
//
// Revision 1.9  2008/07/24 06:24:31  fperrad
// + don't draw loopback (internal event) in level 0
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
