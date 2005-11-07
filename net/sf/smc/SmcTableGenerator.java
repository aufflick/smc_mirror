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
import java.util.Iterator;
import java.util.List;

/**
 * Visits the abstract syntax tree, emitting an HTML table.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcTableGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcTableGenerator(PrintStream source,
                             String srcfileBase)
    {
        super (source, srcfileBase);
    }

    public void visit(SmcFSM fsm)
    {
        Iterator it;
        String separator;

        // Output the top-of-page HTML.
        _source.println("<html>");
        _source.println("  <head>");
        _source.print("    <title>");
        _source.print(_srcfileBase);
        _source.println("</title>");
        _source.println("  </head>");
        _source.println();
        _source.println("  <body>");

        // Have each map generate its HTML table.
        for (it = fsm.getMaps().iterator(), separator = "";
             it.hasNext();
             separator = "    <p>\n")
        {
            _source.print(separator);
            ((SmcMap) it.next()).accept(this);
        }

        // Output the end-of-page HTML.
        _source.println("  </body>");
        _source.println("</html>");

        return;
    }

    public void visit(SmcMap map)
    {
        String mapName = map.getName();
        List transitions = map.getTransitions();
        List params;
        int transitionCount = transitions.size() + 1;
        Iterator it;
        Iterator it2;
        SmcState state;
        SmcTransition transition;
        SmcTransition defaultTransition = null;
        String transName;
        boolean firstFlag;

        // Output start of this map's table.
        _source.println(
            "    <table align=center border=3 cellspacing=2 cellpadding=2>");
        _source.println("      <caption align=\"top\">");
        _source.print("        ");
        _source.print(mapName);
        _source.println(" Finite State Machine");
        _source.println("      </caption>");

        // Output the table's header.
        _source.println("      <tr>");
        _source.println("        <th rowspan=2>");
        _source.println("          State");
        _source.println("        </th>");
        _source.println("        <th colspan=2>");
        _source.println("          Actions");
        _source.println("        </th>");
        _source.print("        <th colspan=");
        _source.print(transitionCount);
        _source.println(">");
        _source.println("          Transition");
        _source.println("        </th>");
        _source.println("      </tr>");
        _source.println("      <tr>");
        _source.println("        <th>");
        _source.println("          Entry");
        _source.println("        </th>");
        _source.println("        <th>");
        _source.println("         Exit");
        _source.println("        </th>");

        // Place each transition name into the header.
        for (it = transitions.iterator();
             it.hasNext() == true;
            )
        {
            transition = (SmcTransition) it.next();
            transName = transition.getName();
            params = transition.getParameters();

            // Since we are placing the default transition at the
            // right-most column, don't output it here if it
            // should locally defined.
            if (transName.equals("Default") == false)
            {
                _source.println("        <th>");
                _source.print("          ");
                _source.println(transName);

                // If the transition has parameters, output
                // them now.
                if (params.size() > 0)
                {
                    _source.println("          <br>");
                    _source.print("          (");

                    for (it2 = params.iterator(),
                             firstFlag = true;
                         it2.hasNext() == true;
                         firstFlag = false)
                    {
                        if (firstFlag == false)
                        {
                            _source.println(',');
                            _source.println("          <br>");
                            _source.print("          ");
                        }

                        ((SmcParameter) it2.next()).accept(this);
                    }

                    _source.println(")");
                }

                _source.println("        </th>");
            }
        }

        // Also output the default transition.
        _source.println("        <th>");
        _source.println("          <b>Default</b>");
        _source.println("        </th>");
        _source.println("      </tr>");

        // The table header is finished. Now have each state
        // output its row.
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            state = (SmcState) it.next();

            // Output the row start.
            _source.println("      <tr>");

            // Note: the state outputs only its name and
            // entry/exit actions. It does not output its
            // transitions (see below).
            state.accept(this);

            // We need to generate transitions in the exact same
            // order as in the header. But the state object
            // does not store its transitions in any particular
            // order. Therefore we must output the state's
            // transitions for it.
            for (it2 = transitions.iterator();
                 it2.hasNext() == true;
                )
            {
                transition = (SmcTransition) it2.next();
                transName = transition.getName();
                params = transition.getParameters();

                // Since we are placing the default transition at the
                // right-most column, don't output it here if it
                // should locally defined.
                if (transName.equals("Default") == true)
                {
                    // If this state has a false transition,
                    // get it now and store it away for later.
                    defaultTransition =
                        state.findTransition(transName, params);
                }
                else
                {
                    _source.println("        <td>");

                    // We have the default transition definition in
                    // hand. We need the state's transition.
                    transition =
                        state.findTransition(transName, params);
                    if (transition != null)
                    {
                        // Place the transitions in preformatted
                        // sections. Don't add a new line - the
                        // transition will do that.
                        _source.print("          <pre>");
                        transition.accept(this);
                        _source.println("          </pre>");
                    }

                    _source.println("        </td>");
                }
            }

            // Now add the Default transition to the last column.
            _source.println("        <td>");
            if (defaultTransition != null)
            {
                // Place the transitions in preformatted
                // sections. Don't add a new line - the
                // transition will do that.
                _source.print("          <pre>");
                defaultTransition.accept(this);
                _source.println("          </pre>");
            }
            _source.println("        </td>");

            // Output the row end.
            _source.println("      </tr>");
        }

        // Output end of this map's table.
        _source.println("    </table>");

        return;
    }

    public void visit(SmcState state)
    {
        List actions;
        Iterator it;
        SmcTransition transition;

        // Output the row data. This consists of:
        // + the state name.
        // + the state entry actions.
        // + the state exit actions.
        // + Each of the transtions.
        _source.println("        <td>");
        _source.print("          ");
        _source.println(state.getInstanceName());
        _source.println("        </td>");

        _source.println("        <td>");
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println("          <pre>");
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }
            _source.println("          </pre>");
        }
        _source.println("        </td>");

        _source.println("        <td>");
        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println("          <pre>");
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }
            _source.println("          </pre>");
        }
        _source.println("        </td>");

        // Note: SmcMap generates our transitions for us in order
        //       to guarantee correct transition ordering.

        return;
    }

    public void visit(SmcTransition transition)
    {
        Iterator it;

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
        int transType = guard.getTransType();
        String condition = guard.getCondition();
        String endStateName = guard.getEndState();
        Iterator it;
        List actions = guard.getActions();

        // Print out the guard (if there is one).
        if (condition.length() > 0)
        {
            _source.print('[');
            _source.print(condition);
            _source.println(']');
        }

        // If this is a pop transition, then print
        // out the pop transition and any arguments.
        if (transType == Smc.TRANS_POP)
        {
            _source.print("  pop(");

            // Is there a pop transition?
            if (endStateName.equals(NIL_STATE) == false &&
                endStateName.length() > 0)
            {
                String popArgs = guard.getPopArgs();

                _source.print(endStateName);

                // Output any and all pop arguments.
                if (popArgs.length() > 0)
                {
                    _source.print(", ");
                    _source.print(popArgs.trim());
                }
            }

            _source.println(")");
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            _source.print("  push(");

            // If the end state is nil, then replace it with the
            // current map and state.
            if (endStateName.equals(NIL_STATE) == true)
            {
                _source.print(mapName);
                _source.print("::");
                _source.print(stateName);
            }
            else
            {
                _source.print(endStateName);
            }

            _source.println(")");
        }
        // Else this is a plain, old transition.
        else
        {
            // Print out the end state.
            _source.print("  ");

            // If the end state is nil, then replace it with the
            // current state's read name.
            if (endStateName.equals(NIL_STATE) == true)
            {
                _source.println(stateName);
            }
            else
            {
                _source.println(endStateName);
            }
        }

        // Print out the actions (if there are any). Otherwise
        // output empty braces.
        if (actions.size() == 0)
        {
            _source.println("  {}");
        }
        else
        {
            _source.println("  {");

            _indent = "    ";
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            _source.println("  }");
        }

        return;
    }

    public void visit(SmcAction action)
    {
        Iterator it;
        String sep;

        _source.print(_indent);
        _source.print(action.getName());
        _source.print("(");

        for (it = action.getArguments().iterator(), sep = "";
             it.hasNext() == true;
             sep = ", ")
        {
            _source.print(sep);
            _source.print(((String) it.next()).trim());
        }

        _source.println(");");

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        return;
    }

//---------------------------------------------------------------
// Member data
//
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
// Revision 1.1  2005/02/21 15:38:24  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.0  2005/02/03 17:12:24  charlesr
// Initial revision
//
