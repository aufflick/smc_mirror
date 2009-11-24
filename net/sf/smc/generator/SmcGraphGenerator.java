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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * @see SmcOptions
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
     * Creates a GraphViz code generator for the given options.
     * @param options The target code generator options.
     */
    public SmcGraphGenerator(final SmcOptions options)
    {
        super (options, "{0}{1}_sm.{2}", "dot");

        _indent_action = "&nbsp;&nbsp;&nbsp;";
    } // end of SmcGraphGenerator(SmcOptions)

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
        _source.print("digraph ");
        _source.print(_srcfileBase);
        _source.println(" {");
        _source.println();
        _source.println("    node");
        _source.println("        [shape=Mrecord width=1.5];");
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

    /**
     * Emits GraphViz code for the FSM map.
     * @param map emit GraphViz code for this map.
     */
    public void visit(SmcMap map)
    {
        String mapName = map.getName();
        SmcState defaultState = map.getDefaultState();
        String startStateName = map.getFSM().getStartState();
        Map<String, String> pushEntryMap = new HashMap<String, String>();
        Map<String, String> popTransMap = new HashMap<String, String>();
        Map<String, String> pushStateMap = new HashMap<String, String>();
        boolean needEnd = false;

        _source.println("        //");
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

        for (SmcState state: map.getAllStates())
        {
            for (SmcTransition transition: state.getTransitions())
            {
                for (SmcGuard guard: transition.getGuards())
                {
                    String endStateName = guard.getEndState();
                    TransType transType = guard.getTransType();

                    if (transType == TransType.TRANS_PUSH)
                    {
                        String pushStateName = guard.getPushState();
                        String pushMapName;
                        int index;

                        if (endStateName.equals(SmcElement.NIL_STATE) == true)
                        {
                             endStateName = state.getInstanceName();
                        }

                        if ((index = pushStateName.indexOf("::")) >= 0)
                        {
                            pushMapName = pushStateName.substring(0, index);
                        }
                        else
                        {
                            pushMapName = mapName;
                        }

                        pushStateMap.put(mapName + "::" + endStateName + "::" + pushMapName, pushMapName);
                    }
                    else if (transType == TransType.TRANS_POP)
                    {
                        String popKey = endStateName;
                        String popVal = endStateName;
                        String popArgs;

                        if (_graphLevel == GRAPH_LEVEL_2 &&
                            (popArgs = guard.getPopArgs()) != null &&
                            popArgs.length() > 0)
                        {
                            popKey += ", ";
                            popVal += ", ";
                            popKey += _escape(_normalize(popArgs));
                            // If the argument contains line separators,
                            // then replace them with a "\n" so Graphviz knows
                            // about the line separation.
                            popVal += _escape(popArgs).replaceAll(
                                "\\n", "\\\\\\l");
                        }
                        popTransMap.put(popKey, popVal);
                        needEnd = true;
                    }
                }
            }
        }
        // Now output the pop transitions as "nodes".
        for (String pname: popTransMap.keySet())
        {
            _source.print("        \"");
            _source.print(mapName);
            _source.print("::pop(");
            _source.print(pname);
            _source.println(")\"");
            _source.println("            [label=\"\" width=1]");
            _source.println();
        }

        if (needEnd == true)
        {
            // Output the end node
            _source.print("        \"");
            _source.print(mapName);
            _source.println("::%end\"");
            _source.println(
                "            [label=\"\" shape=doublecircle style=filled fillcolor=black width=0.15];");
            _source.println();
        }

        // Now output the push composite state.
        for (String pname: pushStateMap.keySet())
        {
            _source.print("        \"");
            _source.print(pname);
            _source.println("\"");
            _source.print("            [label=\"{");
            _source.print(pushStateMap.get(pname));
            _source.println("|O-O\\r}\"]");
            _source.println();
        }

        if (startStateName.indexOf(mapName) == 0)
        {
            // Output the start node only in the right map
            _source.println("        \"%start\"");
            _source.println("            [label=\"\" shape=circle style=filled fillcolor=black width=0.25];");
            _source.println();
        }

        // Now output the push actions as "nodes".
        for (SmcMap map2: map.getFSM().getMaps())
        {
            for (SmcState state: map2.getAllStates())
            {
                for (SmcTransition transition: state.getTransitions())
                {
                    for (SmcGuard guard: transition.getGuards())
                    {
                        if (guard.getTransType() == TransType.TRANS_PUSH)
                        {
                            String pushStateName = guard.getPushState();

                            if (pushStateName.indexOf(mapName) == 0)
                            {
                                pushEntryMap.put(pushStateName, "");
                            }
                        }
                    }
                }
            }
        }
        for (String pname: pushEntryMap.keySet())
        {
            // Output the push action.
            _source.print("        \"push(");
            _source.print(pname);
            _source.println(")\"");
            _source.println("            [label=\"\" shape=plaintext];");
            _source.println();
        }

        _source.println("        //");
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

        // Now output the pop transitions.
        for (String pname: popTransMap.keySet())
        {
            _source.println();
            _source.print("        \"");
            _source.print(mapName);
            _source.print("::pop(");
            _source.print(pname);
            _source.print(")\" -> \"");
            _source.print(mapName);
            _source.println("::%end\"");
            _source.print("            [label=\"pop(");
            _source.print(popTransMap.get(pname));
            _source.println(");\\l\"];");
        }

        // Now output the composite state transition.
        for (String pname: pushStateMap.keySet())
        {
            _source.println();
            _source.print("        \"");
            _source.print(pname);
            _source.print("\" -> \"");
            _source.print(pname.substring(0, pname.lastIndexOf("::")));
            _source.println("\"");
            _source.println("            [label=\"pop/\"]");
        }

        if (startStateName.indexOf(mapName) == 0)
        {
            // Output the start transition only in the right map
            _source.println();
            _source.print("        \"%start\" -> \"");
            _source.print(startStateName);
            _source.println("\"");
        }

        // Now output the push actions as entry "transition".
        for (String pname: pushEntryMap.keySet())
        {
            _source.println();
            _source.print("        \"push(");
            _source.print(pname);
            _source.print(")\" -> \"");
            _source.print(pname);
            _source.println("\"");
            _source.println("            [arrowtail=odot];");
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
        _source.print(instanceName);
        _source.println("\"");

        _source.print("            [label=\"{");
        // Output the state name.
        if (instanceName.equals("DefaultState") == true)
        {
            _source.print("&laquo; Default &raquo;");
        }
        else
        {
            _source.print(instanceName);
        }

        // For graph level 1 & 2, output entry and exit actions.
        if (_graphLevel >= GRAPH_LEVEL_1)
        {
            List<SmcAction> actions;
            Iterator<SmcAction> it;
            boolean empty = true;

            actions = state.getEntryActions();
            if (actions != null)
            {
                if (empty == true)
                {
                    _source.print("|");
                    empty = false;
                }
                _source.print("Entry/\\l");

                // Output the entry actions, one per line.
                for (SmcAction action: actions)
                {
                    _source.print(_indent_action);
                    action.accept(this);
                }
            }

            actions = state.getExitActions();
            if (actions != null)
            {
                if (empty == true)
                {
                    _source.print("|");
                    empty = false;
                }
                _source.print("Exit/\\l");

                // Output the exit actions, one per line.
                for (SmcAction action: actions)
                {
                    _source.print(_indent_action);
                    action.accept(this);
                }
            }

            // Starts a new compartment for internal events
            empty = true;
            for (SmcTransition transition: state.getTransitions())
            {
                for (SmcGuard guard: transition.getGuards())
                {
                    String endStateName = guard.getEndState();
                    TransType transType = guard.getTransType();

                    if (isLoopback(transType, endStateName) &&
                        transType != TransType.TRANS_PUSH)
                    {
                        String transName = transition.getName();
                        String condition = guard.getCondition();
                        String pushStateName = guard.getPushState();
                        actions = guard.getActions();

                        if (empty == true)
                        {
                            _source.print("|");
                            empty = false;
                        }
                        _source.print(transName);

                        // Graph Level 2: Output the transition parameters.
                        if (_graphLevel == GRAPH_LEVEL_2)
                        {
                            List<SmcParameter> parameters = transition.getParameters();
                            Iterator<SmcParameter> pit;
                            String sep;

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
                            String tmp = _escape(condition);

                            // If the condition contains line separators,
                            // then replace them with a "\n" so Graphviz knows
                            // about the line separation.
                            tmp = tmp.replaceAll("\\n", "\\\\\\l");

                            // Not needed when label in edge !!
                            tmp = tmp.replaceAll(">", "\\\\>");
                            tmp = tmp.replaceAll("<", "\\\\<");
                            tmp = tmp.replaceAll("\\|", "\\\\|");

                            _source.print("\\l\\[");
                            _source.print(tmp);
                            _source.print("\\]");
                        }

                        _source.print("/\\l");

                        if (actions != null)
                        {
                            // Output the actions, one per line.
                            for (SmcAction action: actions)
                            {
                                _source.print(_indent_action);
                                action.accept(this);
                            }
                        }

                        if (transType == TransType.TRANS_PUSH)
                        {
                            _source.print(_indent_action);
                            _source.print("push(");
                            _source.print(pushStateName);
                            _source.print(")\\l");
                        }
                    }
                }
            }
        }

        _source.println("}\"];");
        _source.println();

        return;
    } // end of visit(SmcState)

    /**
     * Emits GraphViz code for this FSM transition.
     * @param transition emits GraphViz code for this transition.
     */
    public void visit(SmcTransition transition)
    {
        for (SmcGuard guard: transition.getGuards())
        {
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

        // Loopback are added in the state
        if (isLoopback(transType, endStateName) &&
            transType != TransType.TRANS_PUSH)
        {
            return;
        }

        _source.println();
        _source.print("        \"");
        _source.print(mapName);
        _source.print("::");
        _source.print(stateName);
        _source.print("\" -> ");

        if (transType != TransType.TRANS_POP)
        {
            if (endStateName.equals(SmcElement.NIL_STATE) == true)
            {
                endStateName = stateName;
            }

            if (endStateName.indexOf("::") < 0)
            {
                endStateName = mapName + "::" + endStateName;
            }

            _source.print("\"");
            _source.print(endStateName);

            if (transType == TransType.TRANS_PUSH)
            {
                int index = pushStateName.indexOf("::");

                _source.print("::");

                if (index < 0)
                {
                    _source.print(mapName);
                }
                else
                {
                    _source.print(pushStateName.substring(0, pushStateName.indexOf("::")));
                }
            }

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
                _source.print(_escape(_normalize(popArgs)));
            }

            _source.println(")\"");
        }

        _source.print("            [label=\"");
        _source.print(transName);

        // Graph Level 2: Output the transition parameters.
        if (_graphLevel == GRAPH_LEVEL_2)
        {
            List<SmcParameter> parameters =
                transition.getParameters();
            Iterator<SmcParameter> pit;
            String sep;

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
                _escape(condition).replaceAll(
                    "\\n", "\\\\\\l"));

            _source.print("\\]");
        }
        _source.print("/\\l");

        // Graph Level 1, 2: output actions.
        if (_graphLevel > GRAPH_LEVEL_0 &&
            actions != null)
        {
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
        }

        if (transType == TransType.TRANS_PUSH)
        {
            _source.print("push(");
            _source.print(pushStateName);
            _source.print(")\\l");
        }

        _source.println("\"];");

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

            _source.print(";\\l");
        }

        return;
    } // end of visit(SmcAction)

    /**
     * Emits GraphViz code for this transition parameter.
     * @param parameter emits GraphViz code for this transition parameter.
     */
    public void visit(SmcParameter parameter)
    {
        // Graph Level 2
        _source.print(parameter.getName());
        if (parameter.getType().equals("") == false)
        {
            _source.print(": ");
            _source.print(parameter.getType());
        }

        return;
    } // end of visit(SmcParameter)

    // Place a backslash escape character in front of backslashes
    // and doublequotes.
    private static String _escape(String s)
    {
        String retval;

        if (s.indexOf('\\') < 0 && s.indexOf('"') < 0)
        {
            retval = s;
        }
        else
        {
            StringBuffer buffer =
                new StringBuffer(s.length() * 2);
            int index;
            int length = s.length();
            char c;

            for (index = 0; index < length; ++index)
            {
                c = s.charAt(index);
                if (c == '\\' || c == '"')
                {
                    buffer.append('\\');
                }

                buffer.append(c);
            }

            retval = buffer.toString();
        }

        return (retval);
    }

    private static String _normalize(String s)
    {
        int index;
        int length = s.length();
        char c;
        boolean space = false;
        StringBuffer buffer =
            new StringBuffer(length);

        for (index = 0; index < length; ++index)
        {
            c = s.charAt(index);
            if (space)
            {
                if (c != ' ' && c != '\t' && c != '\n')
                {
                    buffer.append(c);
                    space = false;
                }
            }
            else
            {
                if (c == ' ' || c == '\t' || c == '\n')
                {
                    buffer.append(' ');
                    space = true;
                }
                else
                {
                    buffer.append(c);
                }
            }
        }

        return (buffer.toString().trim());
    }

    // Outputs a list of warning and error messages.
//---------------------------------------------------------------
// Member data
//

    String _indent_action;
} // end of class SmcGraphGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.4  2009/11/24 20:42:39  cwrapp
// v. 6.0.1 update
//
// Revision 1.3  2009/09/05 15:39:20  cwrapp
// Checking in fixes for 1944542, 1983929, 2731415, 2803547 and feature 2797126.
//
// Revision 1.2  2009/03/27 09:41:47  cwrapp
// Added F. Perrad changes back in.
//
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.23  2008/08/21 11:21:56  fperrad
// + draw a composite state when 'push' loopback
//
// Revision 1.22  2008/08/20 08:18:21  fperrad
// + draw a composite state when 'push' transition
//
// Revision 1.21  2008/08/19 08:59:24  fperrad
// + draw 'pop' transition with UML artifacts
//
// Revision 1.20  2008/08/15 23:33:02  fperrad
// + fix : unique push entry transition
//
// Revision 1.19  2008/08/15 22:24:38  fperrad
// + draw push entry transition
// + don't draw namespace
//
// Revision 1.18  2008/08/14 09:16:18  fperrad
// + internal actions : more indentation
// + split entry/exit & internal events in two compartments
//
// Revision 1.17  2008/08/06 07:45:16  fperrad
// + refactor : don't need optimization for speed
//
// Revision 1.16  2008/08/02 09:28:49  fperrad
// + fix : push when internal event
// + drawing close to UML
//   - state with Mrecord
//   - pop with invhouse
//   - initial state with filled circle
//
// Revision 1.15  2008/07/31 12:03:17  fperrad
// + draw the start transition
// + add emphasis to Default state
//
// Revision 1.14  2008/07/31 06:20:59  fperrad
// + fix : default state name in edge
// + fix : escape newline in pop argument (level 2)
//
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
