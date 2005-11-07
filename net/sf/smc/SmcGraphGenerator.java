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

    public SmcGraphGenerator(PrintStream source,
                             String srcfileBase)
    {
        super (source, srcfileBase);

        _parameters = null;
    }

    public void visit(SmcFSM fsm)
    {
        Iterator it;
        SmcMap map;

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
        for (it = fsm.getMaps().iterator(); it.hasNext();)
        {
            map = (SmcMap) it.next();

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
    }

    public void visit(SmcMap map)
    {
        String mapName = map.getName();
        int graphLevel = Smc.getGraphLevel();
        Iterator it;
        Iterator it2;
        SmcState defaultState = map.getDefaultState();
        List transitions = map.getTransitions();
        SmcState state;
        SmcTransition transition;
        Iterator guardIt;
        SmcGuard guard;
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
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcState) it.next()).accept(this);
        }

        // Output the default state - if there is one and it
        // has some transitions.
        if (defaultState != null &&
            defaultState.getTransitions().isEmpty() == false)
        {
            defaultState.accept(this);
        }

        // Now output the pop transitions as "nodes".
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            transition = (SmcTransition) it.next();

            for (it2 = transition.getGuards().iterator();
                 it2.hasNext() == true;
                )
            {
                guard = (SmcGuard) it2.next();

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
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            state = (SmcState) it.next();

            for (it2 = state.getTransitions().iterator();
                 it2.hasNext() == true;
                )
            {
                ((SmcTransition) it2.next()).accept(this);
                _source.println();
            }
        }

        // Have the default state output its transitions last.
        if (defaultState != null)
        {
            for (it =
                     defaultState.getTransitions().iterator();
                 it.hasNext() == true;
                )
            {
                ((SmcTransition) it.next()).accept(this);
                _source.println();
            }
        }

        return;
    }

    public void visit(SmcState state)
    {
        int graphLevel = Smc.getGraphLevel();
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
        if (graphLevel < Smc.GRAPH_LEVEL_2)
        {
            _source.println("            [label=\"\\N\"];");
        }
        // For graph level 2, output the state name, entry and exit
        // actions.
        else
        {
            List actions;
            Iterator it;
            String sep;

            _source.print("            [label=\"{\\N| Entry:");

            // Output the entry actions, one per line.
            actions = state.getEntryActions();
            if (actions != null && actions.size() > 0)
            {
                for (it = actions.iterator(), sep = " ";
                     it.hasNext() == true;
                     sep = "\\l")
                {
                    _source.print(sep);
                    ((SmcAction) it.next()).accept(this);
                }
            }

            _source.print("| Exit:");

            // Output the exit actions, one per line.
            actions = state.getExitActions();
            if (actions != null && actions.size() > 0)
            {
                for (it = actions.iterator(), sep = " ";
                     it.hasNext() == true;
                     sep = "\\l")
                {
                    _source.print(sep);
                    ((SmcAction) it.next()).accept(this);
                }
            }

            _source.println("}\"];");
        }

        _source.println();

        return;
    }

    public void visit(SmcTransition transition)
    {
        List parameters = transition.getParameters();
        Iterator it;

        // Graph level 2: add parameters to transition name.
        // Generate the parameters once and pass the string to
        // the guards in the "package" argument.
        if (Smc.getGraphLevel() == Smc.GRAPH_LEVEL_2 &&
            parameters.isEmpty() == false)
        {
            ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
            PrintStream pstream = _source;
            String sep;

            _source = new PrintStream(baos);
            for (it = parameters.iterator(), sep = "";
                 it.hasNext() == true;
                 sep = ", ")
            {
                ((SmcParameter) it.next()).accept(this);
            }

            _parameters = baos.toString();
            _source = pstream;
        }

        for (it = transition.getGuards().iterator();
             it.hasNext() == true;)
        {
            _source.println();
            ((SmcGuard) it.next()).accept(this);
        }

        return;
    }

    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String mapName = map.getName();
        String stateName = state.getClassName();
        String transName = transition.getName();
        int transType = guard.getTransType();
        String endStateName = guard.getEndState();
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        int graphLevel = Smc.getGraphLevel();
        List actions = guard.getActions();
        Iterator it;

        _source.print("        \"");
        _source.print(mapName);
        _source.print("::");
        _source.print(stateName);
        _source.print("\" -> ");

        if (transType != Smc.TRANS_POP)
        {
            if (endStateName.equals(NIL_STATE) == true)
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

        if (transType == Smc.TRANS_PUSH)
        {
            _source.print("/\\lpush(");
            _source.print(pushStateName);
            _source.print(")");
        }

        // Graph Level 1, 2: output actions.
        if (graphLevel > Smc.GRAPH_LEVEL_0 &&
            actions != null &&
            actions.isEmpty() == false)
        {
            _source.print("/\\l");

            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
                _source.print("\\l");
            }
        }

        _source.print("\"];");

        return;
    }

    public void visit(SmcAction action)
    {
        int graphLevel = Smc.getGraphLevel();

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
                Iterator it;
                String arg;
                String sep;

                _source.print("(");

                // Now output the arguments.
                for (it = action.getArguments().iterator(),
                         sep = "";
                     it.hasNext() == true;
                     sep = ", ")
                {
                    arg = ((String) it.next()).trim();

                    _source.print(sep);

                    // If the argument is a quoted string, then
                    // the quotes must be escaped.
                    // First, replace all backslashes with two
                    // backslashes.
                    arg = arg.replaceAll("\\\\", "\\\\\\\\");

                    // Then replace all double quotes with
                    // a backslash double qoute.
                    _source.print(arg.replaceAll("\"", "\\\\\""));
                }

                _source.print(")");
            }

            _source.print(';');
        }

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getName());
        _source.print(": ");
        _source.print(parameter.getType());

        return;
    }

//---------------------------------------------------------------
// Member data
//

    // Store the serialized parameters here.
    private String _parameters;
}

//
// CHANGE LOG
// $Log$
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
