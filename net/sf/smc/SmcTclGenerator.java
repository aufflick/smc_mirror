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
import java.util.Iterator;
import java.util.List;

/**
 * Visits the abstract syntax tree, emitting [incr Tcl] code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcTclGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcTclGenerator(PrintStream source,
                           String srcfileBase)
    {
        super (source, srcfileBase);
    }

    public void visit(SmcFSM fsm)
    {
        String context = fsm.getContext();
        String rawSource = fsm.getSource();
        String packageName = fsm.getPackage();
        String startState = fsm.getStartState();
        List maps = fsm.getMaps();
        List transitions;
        List params;
        SmcMap map;
        String mapName;
        SmcState state;
        SmcTransition trans;
        String transName;
        String separator;
        int index;
        Iterator it;
        Iterator it2;

        // Now dump out the raw source code, if any.
        if (rawSource != null && rawSource.length() > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // Do user-specified imports now.
        for (it = fsm.getImports().iterator();
             it.hasNext() == true;
            )
        {
            _source.print("package require ");
            _source.print(it.next());
            _source.println(";");
        }
        _source.println();

        // If a namespace was specified, then output that
        // namespace now.
        if (packageName != null && packageName.length() > 0)
        {
            _source.print("namespace eval ");
            _source.print(packageName);
            _source.println(" {");
            _source.println();
            _indent = "    ";

            _pkgScope = "::" + packageName + "::";
        }
        else
        {
            _indent = "";
            _pkgScope = "";
        }

        // Generate the context.
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("Context {");
        _source.print(_indent);
        _source.println("    inherit ::statemap::FSMContext;");
        _source.println();
        _source.print(_indent);
        _source.println("# Member functions.");
        _source.println();
        _source.print(_indent);
        _source.println("    constructor {owner} {");
        _source.print(_indent);
        _source.println(
            "        ::statemap::FSMContext::constructor;");
        _source.print(_indent);
        _source.println("    } {");
        _source.print(_indent);
        _source.println("        set _owner $owner;");
        _source.print(_indent);
        _source.print("        setState ${");
        _source.print(_pkgScope);
        _source.print(startState);
        _source.println("};");
        _source.print(_indent);
        _source.print("        ${");
        _source.print(_pkgScope);
        _source.print(startState);
        _source.println("} Entry $this;");

        _source.print(_indent);
        _source.println("    }");

        // For every possible transition in every state map,
        // create a method.
        // First, get the transitions list.
        transitions = fsm.getTransitions();
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();
            params = trans.getParameters();

            // Don't do the Default transition.
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print(_indent);
                _source.print("    public method ");
                _source.print(trans.getName());
                _source.print(" {");

                for (it2 = params.iterator(), separator = "";
                     it2.hasNext() == true;
                     separator = " ")
                {
                    _source.print(separator);
                    ((SmcParameter) it2.next()).accept(this);
                }
                _source.println("} {");

                _source.print(_indent);
                _source.print("        [getState] ");
                _source.print(trans.getName());
                _source.print(" $this");
                for (it2 = params.iterator();
                     it2.hasNext() == true;
                    )
                {
                    _source.print(" $");
                    _source.print(
                        ((SmcParameter) it2.next()).getName());
                }
                _source.println(";");
                _source.print(_indent);
                _source.println("        return -code ok;");
                _source.print(_indent);
                _source.println("    }");
            }
        }

        _source.println();
        _source.print(_indent);
        _source.println("    public method getOwner {} {");
        _source.print(_indent);
        _source.println("        return -code ok $_owner;");
        _source.print(_indent);
        _source.println("    }");

        // v. 2.2.0: If we are supporting serialization, then
        // declare the min and max indices.
        if (Smc.isSerial() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println("    public method valueOf {id} {");
            _source.print(_indent);
            _source.println(
                "        if {$id < $MIN_ID || $id > $MAX_ID} {");
            _source.print(_indent);
            _source.println("            set retcode error;");
            _source.print(_indent);
            _source.print("            set retval ");
            _source.println("\"$id is out of bounds\";");
            _source.print(_indent);
            _source.println("        } else {");
            _source.print(_indent);
            _source.println("            set retcode ok;");
            _source.print(_indent);
            _source.println(
                "            set retval $_States($id);");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        return -code $retcode $retval;");
            _source.print(_indent);
            _source.println("    }");
        }

        _source.println();
        _source.print(_indent);
        _source.println("# Member data.");
        _source.println();
        _source.print(_indent);
        _source.println("    private variable _owner;");

        // v. 2.2.0: If we are supporting serialization, then
        // declare the min and max indices.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.println("    private common MIN_ID;");
            _source.print(_indent);
            _source.println("    private common MAX_ID;");
            _source.print(_indent);
            _source.println("    private common _States;");
        }

        // Put the closing brace on the context class.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        // Now output the application's state class.
        _source.print(_indent);
        _source.print("class ");
        _source.print(context);
        _source.println("State {");
        _source.print(_indent);
        _source.println("    inherit ::statemap::State;");
        _source.println();
        _source.print(_indent);
        _source.println("# Member functions.");
        _source.println();
        _source.print(_indent);
        _source.println("    constructor {name id} {");
        _source.print(_indent);
        _source.println(
            "        ::statemap::State::constructor $name $id;");
        _source.print(_indent);
        _source.println("    } {}");
        _source.println();

        // Define the default Entry() and Exit() methods.
        _source.print(_indent);
        _source.println("    public method Entry {context} {};");
        _source.print(_indent);
        _source.println("    public method Exit {context} {};");

        // Declare the undefined default transitions.
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();
            transName = trans.getName();

            // The Default transition is handled separately.
            if (transName.equals("Default") == false)
            {
                _source.println();
                _source.print(_indent);
                _source.print("    public method ");
                _source.print(transName);
                _source.print(" {context");
                for (it2 = trans.getParameters().iterator();
                     it2.hasNext() == true;
                    )
                {
                    _source.print(" ");
                    ((SmcParameter) it2.next()).accept(this);
                }
                _source.println("} {");
                _source.print(_indent);
                _source.println("        Default $context;");
                _source.print(_indent);
                _source.println("        return -code ok;");
                _source.print(_indent);
                _source.println("    }");
            }
        }

        // Define the default Default transition.
        _source.println();
        _source.print(_indent);
        _source.println("    public method Default {context} {");
        _source.print(_indent);
        _source.println(
            "        set transition [$context getTransition];");
        _source.print(_indent);
        _source.print("        return -code error ");
        _source.print("\"Transition \\\"$transition\\\" ");
        _source.print("fell through to a ");
        _source.println(
            "non-existent default definition.\";");
        _source.print(_indent);
        _source.println("    }");

        // End of the application state class.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        // Have each map print out its source code in turn.
        for (it = maps.iterator(); it.hasNext() == true;)
        {
            ((SmcMap) it.next()).accept(this);
        }

        // Output the static state initialization.
        _source.print(_indent);
        _source.println("# Static state declarations.");
        for (it = maps.iterator(), index = 0;
             it.hasNext() == true;
            )
        {
            map = (SmcMap) it.next();
            mapName = map.getName();

            for (it2 = map.getStates().iterator();
                 it2.hasNext() == true;
                 ++index)
            {
                state = (SmcState) it2.next();

                _source.print(_indent);
                _source.print("set ");
                _source.print(mapName);
                _source.print("::");
                _source.print(state.getInstanceName());
                _source.print(" ");

                if (packageName != null && packageName.length() > 0)
                {
                    _source.print(packageName);
                    _source.print("::");
                }

                _source.print("[");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print(" #auto \"");
                _source.print(mapName);
                _source.print("::");
                _source.print(state.getClassName());
                _source.print("\" ");
                _source.print(index);
                _source.println("];");
            }
        }

        // If -reflect specified, then generate the transitions
        // class-level data member for each state - but only if
        // the state has transitions.
        if (Smc.isReflection() == true)
        {
            SmcState defaultState;
            List defaultTransitions;
            List stateTransitions;

            _source.println();
            _source.print(_indent);
            _source.println("# Static state transitions.");

            for (it = maps.iterator(), index = 0;
                 it.hasNext() == true;
                )
            {
                map = (SmcMap) it.next();
                defaultState = map.getDefaultState();
                defaultTransitions =
                    defaultState.getTransitions();
                stateTransitions = (List) new ArrayList();

                // Generate the default state's transitions
                // first.
                _reflectTransitions(defaultState,
                                    stateTransitions,
                                    defaultTransitions,
                                    transitions);

                for (it2 = map.getStates().iterator();
                     it2.hasNext() == true;
                     ++index)
                {
                    state = (SmcState) it2.next();
                    stateTransitions = state.getTransitions();
                    _reflectTransitions(state,
                                        stateTransitions,
                                        defaultTransitions,
                                        transitions);
                }
            }
        }

        // v. 2.2.0: If we are supporting serialization, then
        // declare the min and max indices.
        if (Smc.isSerial() == true)
        {
            // Output the state deserialization static data.
            _source.print(_indent);
            _source.print("set ");
            _source.print(context);
            _source.println("Context::MIN_ID 0;");
            _source.print(_indent);
            _source.print("set ");
            _source.print(context);
            _source.print("Context::MAX_ID ");
            _source.print(index - 1);
            _source.println(";");
            _source.print(_indent);
            _source.print("array set ");
            _source.print(context);
            _source.print("Context::_States [list");

            for (it = maps.iterator(), index = 0;
                 it.hasNext() == true;
                )
            {
                map = (SmcMap) it.next();
                mapName = map.getName();

                for (it2 = map.getStates().iterator();
                     it2.hasNext() == true;
                     ++index)
                {
                    state = (SmcState) it2.next();

                    _source.print(" ");
                    _source.print(index);
                    _source.print(" ${");
                    _source.print(mapName);
                    _source.print("::");
                    _source.print(state.getInstanceName());
                    _source.print("}");
                }
            }
            _source.println("];");
        }

        // If necessary, place an end brace for the namespace.
        if (packageName != null && packageName.length() > 0)
        {
            _source.println("}");
        }

        return;
    }

    public void visit(SmcMap map)
    {
        List definedDefaultTransitions;
        SmcState defaultState = map.getDefaultState();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        List states = map.getStates();
        Iterator it;
        SmcState state;

        if (defaultState != null)
        {
            definedDefaultTransitions =
                    defaultState.getTransitions();
        }
        else
        {
            definedDefaultTransitions = (List) new ArrayList();
        }

        // Declare the map class.
        _source.print(_indent);
        _source.print("class ");
        _source.print(mapName);
        _source.println(" {");
        _source.print(_indent);
        _source.println("# Member data.");
        _source.println();

        // Print all the static state objects.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            state = (SmcState) it.next();

            _source.print(_indent);
            _source.print("    public common ");
            _source.print(state.getClassName());
            _source.println(" \"\";");
        }

        // End of map class.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        // Declare the map's default state class.
        _source.print(_indent);
        _source.print("class ");
        _source.print(mapName);
        _source.println("_Default {");
        _source.print(_indent);
        _source.print("    inherit ");
        _source.print(context);
        _source.println("State;");
        _source.println();

        _source.print(_indent);
        _source.println("# Member functions.");
        _source.println();
        _source.print(_indent);
        _source.println("    constructor {name id} {");
        _source.print(_indent);
        _source.print("       ");
        _source.print(context);
        _source.println("State::constructor $name $id;");
        _source.print(_indent);
        _source.println("    } {}");

        // If -reflection was specified, then generate the
        // getTransitions method.
        if (Smc.isReflection() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "    public method getTransitions {} {");
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "return -code ok [array get _transitions];");
            _source.println("    }");
        }

        // Dump out the user-defined default transitions.
        if (defaultState != null)
        {
            for (it = definedDefaultTransitions.iterator();
                 it.hasNext() == true;
                )
            {
                ((SmcTransition) it.next()).accept(this);
            }
        }

        // If -reflect specified, then generate the transitions
        // class data.
        if (Smc.isReflection() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println("# Member data.");
            _source.println();
            _source.print(_indent);
            _source.println("    public common _transitions;");
        }

        // End the map's default state class declaration.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        // Have each state now generate itself.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            ((SmcState) it.next()).accept(this);
        }

        // v. 1.4.0: This functionality moved to
        // SmcFSM.java.
        // Now create each of the static states.
//          for (stateIt = _states.iterator();
//               stateIt.hasNext() == true;
//              )
//          {
//              state = (SmcState) stateIt.next();
//              source.println(indent +
//                             "set " +
//                             _name +
//                             "::" +
//                             state.getClassName() +
//                             " " +
//                             pkg +
//                             "[" +
//                             _name +
//                             "_" +
//                             state.getClassName() +
//                             " #auto \"" +
//                             _name +
//                             "::" +
//                             state.getClassName() +
//                             "\"];");
//          }

        _source.println();

        return;
    }

    public void visit(SmcState state)
    {
        String mapName = state.getMap().getName();
        String stateName = state.getClassName();
        List actions;
        Iterator it;

        _source.print(_indent);
        _source.print("class ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.println(" {");
        _source.print(_indent);
        _source.print("    inherit ");
        _source.print(mapName);
        _source.println("_Default;");
        _source.println();
        _source.print(_indent);
        _source.println("    constructor {name id} {");
        _source.print(_indent);
        _source.print("        ");
        _source.print(mapName);
        _source.println("_Default::constructor $name $id;");
        _source.print(_indent);
        _source.println("    } {}");

        // If -reflection was specified, then generate the
        // getTransitions method.
        if (Smc.isReflection() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "    public method getTransitions {} {");
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "return -code ok [array get _transitions];");
            _source.println("    }");
        }

        // Add the Entry() and Exit() member functions if this
        // state defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "    public method Entry {context} {");

            // Declare the ctxt local variable.
            _source.println(
                "        set ctxt [$context getOwner];");
            _source.println();

            // Generate the actions associated with this code.
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            //` End the Entry() method with a return.
            _source.println();
            _source.print(_indent);
            _source.println("        return -code ok;");
            _source.print(_indent);
            _source.println("    }");
        }

        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "    public method Exit {context} {");

            // Declare the ctxt local variable.
            _source.println(
                "        set ctxt [$context getOwner];");
            _source.println();

            // Generate the actions associated with this code.
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            // End the Exit() method with a return.
            _source.print(_indent);
            _source.println("        return -code ok;");
            _source.print(_indent);
            _source.println("    }");
        }

        // Have the transitions generate their code.
        for (it = state.getTransitions().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }

        // If -reflect specified, then generate the transitions
        // class data.
        if (Smc.isReflection() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println("# Member data.");
            _source.println();
            _source.print(_indent);
            _source.println("    public common _transitions;");
        }

        // End of the state class declaration.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        return;
    }

    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        String transName = transition.getName();
        List parameters = transition.getParameters();
        List guards = transition.getGuards();
        boolean nullCondition = false;
        Iterator it;
        SmcGuard guard;
        SmcParameter param;

        _source.println();
        _source.print(_indent);
        _source.print("    public method ");
        _source.print(transName);
        _source.print(" {context");

        // Add user-defined parameters.
        for (it = parameters.iterator(); it.hasNext() == true;)
        {
            _source.print(" ");
            ((SmcParameter) it.next()).accept(this);
        }

        _source.println("} {");

        // All transitions have a "ctxt" local variable.
        // 8/14/2003:
        // Do this only if there are any transition actions or
        // guard conditions which reference it.
        if (transition.hasCtxtReference() == true)
        {
            _source.print(_indent);
            _source.println(
                "        set ctxt [$context getOwner];");
        }

        // If this is a default transition, create the loopback flag.
        if (stateName.equals("Default") == true)
        {
            _source.print(_indent);
            _source.println("        set loopbackFlag 0;");
            _source.println();

            // Remember this transition's name.
            _source.print(_indent);
            _source.print("        set _transition \"");
            _source.print(transName);
            _source.println("\";");
        }
        else
        {
            _source.println();
        }

        // If verbose is turned on, then put the logging code in.
        if (Smc.isDebug() == true)
        {
            String sep;

            _source.print(_indent);
            _source.println(
                "        if {[$context getDebugFlag] != 0} {");
            _source.print(_indent);
            _source.print(
                "            puts [$context getDebugStream] ");
            _source.print("\"TRANSITION    : ");
            _source.print(mapName);
            _source.print("::");
            _source.print(stateName);
            _source.print(" ");
            _source.print(transName);
            _source.print("(");
            for (it = parameters.iterator(), sep = "";
                 it.hasNext() == true;
                 sep = ", ")
            {
                _source.print(sep);
                ((SmcParameter) it.next()).accept(this);
            }
            _source.print(")");

            _source.println("\";");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }

        for (it = guards.iterator(),
                 _guardIndex = 0,
                 _guardCount = guards.size();
             it.hasNext() == true;
             ++_guardIndex)
        {
            guard = (SmcGuard) it.next();

            // Track if there is a guard with no condition.
            if (guard.getCondition().length() == 0)
            {
                nullCondition = true;
            }

            guard.accept(this);
        }

        // What if all the guards have a condition? There will be
        // no "else" clause. This condition will fall through and
        // do nothing? Is that right? No. If that is the case,
        // then add the "else" clause and have it call this
        // transition's default.
        if (_guardIndex > 0 && nullCondition == false)
        {
            if (_guardCount == 1)
            {
                _source.print(_indent);
                _source.print("        }");
            }

            _source.println(" else {");
            _source.print(_indent);
            _source.print("            ");
            _source.print(mapName);
            _source.print("_Default ");
            _source.print(transName);
            _source.print(" $context");

            for (it = parameters.iterator();
                 it.hasNext() == true;
                )
            {
                _source.print(" ");
                _source.print(
                    ((SmcParameter) it.next()).getName());
            }

            _source.println(";");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }
        else
        {
            _source.println();
        }

        _source.print(_indent);
        _source.println("        return -code ok;");
        _source.print(_indent);
        _source.println("    }");

        return;
    }

    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        int transType = guard.getTransType();
        boolean defaultFlag =
            stateName.equalsIgnoreCase("Default");
        boolean loopbackFlag = false;
        String indent2;
        String indent3;
        String indent4;
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List actions = guard.getActions();

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the
        // state name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state is actually a transition name.
        if (transType != Smc.TRANS_POP &&
            endStateName.length () > 0 &&
            endStateName.equals(NIL_STATE) == false &&
            endStateName.indexOf("::") < 0)
        {
            endStateName = mapName + "::" + endStateName;
        }

        if (stateName.indexOf("::") < 0)
        {
            stateName = mapName + "::" + stateName;
        }

        // v. 2.0.2: If the push state is not fully-qualified,
        // then prepend the current map's name and make if
        // fully-qualified.
        if (pushStateName != null &&
            pushStateName.length() > 0 &&
            pushStateName.indexOf("::") < 0)
        {
            pushStateName = mapName + "::" + pushStateName;
        }

        loopbackFlag =
            isLoopback(transType, stateName, endStateName);

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            // Ok, there are multiple guard statements. Now is
            // this the first guard among many?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an "if"
                // should be used for this condition.
                _source.print(_indent);
                _source.print("        if {");
                _source.print(condition);
                _source.println("} {");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if" for
                // the condition.
                _source.print(" elseif {");
                _source.print(condition);
                _source.println("} {");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println(" else {");
            }

            indent2 = _indent + "            ";
        }
        // There is only one guard. Does this guard have a
        // condition.
        else if (condition.length() == 0)
        {
            // Actually, this is a plain, old, vaniila
            // transition.
            indent2 = _indent + "        ";
        }
        else
        {
            indent2 = _indent + "            ";

            // Yes, there is a condition.
            _source.print(_indent);
            _source.print("        if {");
            _source.print(condition);
            _source.println("} {");
        }

        // If this is a pop transition, do not change the
        // "end state" name because it is really a transition.
        if (transType == Smc.TRANS_POP)
        {
            // no-op.
        }
        // Now that the necessary conditions are in place, it's
        // time to dump out the transitions actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        else if (actions.size() == 0 &&
                 endStateName.length() != 0)
        {
            endStateName = "${" + _pkgScope + endStateName + "}";
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
                endStateName = "${EndStateName}";
                _source.print(indent2);
                _source.println(
                    "set EndStateName [$context getState];");
            }
            else
            {
                endStateName =
                    "${" + _pkgScope + endStateName + "}";
            }
        }

        // Decide if runtime loopback checking must be done.
        if (defaultFlag == true &&
            transType == Smc.TRANS_SET &&
            loopbackFlag == false)
        {
            _source.print(_indent);
            _source.print("        if {[string compare ");
            _source.print("[[$context getState] getName] [");
            _source.print(endStateName);
            _source.println(" getName]] == 0} {");
            _source.print(_indent);
            _source.println("            set loopbackFlag 1;");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }

        // Perform the current state's exit action.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (transType == Smc.TRANS_POP || loopbackFlag == false)
        {
            indent4 = indent2;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent4 = indent2 + "    ";
                _source.print(indent2);
                _source.println("if {$loopbackFlag == 0} {");
            }

            _source.print(indent4);
            _source.println(
                "[$context getState] Exit $context;");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("}");
                _source.println();
            }
        }

        // Dump out this transition's actions.
        if (actions.size() == 0)
        {
            indent3 = indent2;

            if (condition.length() > 0)
            {
                _source.print(indent2);
                _source.println("# No actions.");
            }
        }
        else
        {
            Iterator it;

            // Now that we are in the transition, clear the
            // current state since we are no longer in a state.
            _source.print(indent2);
            _source.println("$context clearState;");

            // v. 2.0.2: Place the actions inside a catch block.
            // If one of the actions raises an error, the catch
            // block will make sure the state is set before
            // re-raising the error.
            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                indent3 = indent2 + "    ";

                _source.print(indent2);
                _source.println("if [catch {");
            }
            else
            {
                indent3 = indent2;
            }

            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.print(indent2);
                _source.println("} result] {");

                // v. 2.0.2: Generate the set state, push or pop code
                // for the if's then body. Note: the catch body was
                // generated only if there were actions.
                if (transType == Smc.TRANS_SET)
                {
                    _source.print(indent3);
                    _source.print("$context setState ");
                    _source.print(endStateName);
                    _source.println(";");
                }
                else if (transType == Smc.TRANS_PUSH)
                {
                    _source.print(indent3);
                    _source.print("$context setState ");
                    _source.print(endStateName);
                    _source.println(";");

                    // Before doing the push, execute the end state's
                    // entry actions (if any) if this is not a
                    // loopback.
                    if (loopbackFlag == false)
                    {
                        indent4 = indent3;

                        if (defaultFlag == true)
                        {
                            indent4 = indent3 + "    ";

                            _source.println();
                            _source.print(indent3);
                            _source.println(
                                "if {$loopbackFlag == 0} {");
                        }
                        else
                        {
                            _source.println();
                        }

                        _source.print(indent4);
                        _source.println(
                            "[$context getState] Entry $context;");

                        if (defaultFlag == true)
                        {
                            _source.print(indent3);
                            _source.println("}");
                        }
                        else
                        {
                            _source.println();
                        }
                    }

                    _source.print(indent3);
                    _source.print("$context pushState ${");
                    _source.print(_pkgScope);
                    _source.print(pushStateName);
                    _source.println("};");
                }
                else if (transType == Smc.TRANS_POP)
                {
                    _source.print(indent3);
                    _source.println("$context popState;");
                }

                // Re-throw the caught Tcl error so the
                // application may now see it.
                _source.print(indent3);
                _source.println("error $result;");

                // Close off the then body and start the else body.
                _source.print(indent2);
                _source.println("} else {");
            }
        }

        // Print the setState() call, if necessary. Do NOT
        // generate the set state if:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        //
        // v. 2.0.2: The following code must be generated twice -
        // once for the catch body and again in the if's then
        // body.
        if (transType == Smc.TRANS_SET &&
            (actions.size() > 0 ||
             loopbackFlag == false))
        {
            _source.print(indent3);
            _source.print("$context setState ");
            _source.print(endStateName);
            _source.println(";");
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            // Set the next state so that it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || actions.size() > 0)
            {
                _source.print(indent3);
                _source.print("$context setState ");
                _source.print(endStateName);
                _source.println(";");
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                indent4 = indent3;

                _source.println();

                if (defaultFlag == true)
                {
                    indent4 = indent3 + "    ";

                    _source.print(indent3);
                    _source.println(
                        "if {$loopbackFlag == 0} {");
                }

                _source.print(indent4);
                _source.println(
                    "[$context getState] Entry $context;");

                if (defaultFlag == true)
                {
                    _source.print(indent3);
                    _source.println("}");
                }
                else
                {
                    _source.println();
                }
            }

            _source.print(indent3);
            _source.print("$context pushState ${");
            _source.print(_pkgScope);
            _source.print(pushStateName);
            _source.println("};");
        }
        else if (transType == Smc.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("$context popState;");
        }

        // Close off the else body if there is one.
        // v. 2.2.0: There won't be one if the user turned off
        // guards.
        if (actions.size() > 0 && Smc.isNoCatch() == false)
        {
            _source.print(indent2);
            _source.println("}");
        }

        // Perform the new state's entry actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((transType == Smc.TRANS_SET &&
             loopbackFlag == false) ||
             transType == Smc.TRANS_PUSH)
        {
            indent4 = indent2;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent4 = indent2 + "    ";

                _source.println();
                _source.print(indent2);
                _source.println("if {$loopbackFlag == 0} {");
            }

            _source.print(indent4);
            _source.println(
                "[$context getState] Entry $context;");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("}");
            }
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == Smc.TRANS_POP &&
            endStateName.equals(NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.println();
            _source.print(indent2);
            _source.print("$context ");
            _source.print(endStateName);

            // Output any and all pop arguments.
            if (popArgs.length() > 0)
            {
                _source.print(" ");
                _source.print(popArgs);
            }

            _source.println(";");
        }

        // If this is a guarded transition, it will be necessary
        // to close off the if body. DON'T PRINT A NEW LINE. Why?
        // Because an else or elseif may follow and we won't know
        // until we go back to the transition source generator
        // whether all clauses have been done.
        if (_guardCount > 1)
        {
            _source.print(_indent);
            _source.print("        }");
        }

        return;
    }

    public void visit(SmcAction action)
    {
        String name = action.getName();
        Iterator it;

        if (action.getDefault() == true)
        {
            _source.print(_indent);
            _source.print(name);
        }
        else
        {
            _source.print(_indent);

            // Need to distinguish between FSMContext actions and
            // application class actions. If the action is
            // "emptyStateStack", then pass it to the context.
            // Otherwise, let the application class handle it.
            if (name.equals("emptyStateStack") == true)
            {
                _source.print("$context ");
            }
            else
            {
                _source.print("$ctxt ");
            }

            _source.print(name);
        }

        for (it = action.getArguments().iterator(); 
             it.hasNext() == true;
            )
        {
            _source.print(" ");
            _source.print((String) it.next());
        }

        _source.println(";");

        return;
    }

    public void visit(SmcParameter parameter)
    {
        // v. 2.0.2: Tcl differentiates between
        // call-by-value and call-by-name. If this is
        // call-by-value, prepend the name with a "$".
        if (parameter.getType().equals("value") == true)
        {
            _source.print("$");
        }

        // Types? Types?!!! We don't need no stinkin' types!
        // I'm Tcl dammit!
        _source.print(parameter.getName());

        return;
    }

    // Returns the _transition initializations for reflection.
    private void _reflectTransitions(SmcState state,
                                     List stateTransitions,
                                     List defaultTransitions,
                                     List allTransitions)
    {
        Iterator it;
        SmcTransition transition;
        String transName;
        int transDefinition;
        String sep;

        _source.print(_indent);
        _source.print("array set ");
        _source.print(state.getMap().getName());
        _source.print("_");
        _source.print(state.getClassName());
        _source.print("::_transitions {");

        for (it = allTransitions.iterator(), sep = "";
             it.hasNext() == true;
             sep = " ")
        {
            transition = (SmcTransition) it.next();
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

            _source.print(sep);
            _source.print("\"");
            _source.print(transName);
            _source.print("\" ");
            _source.print(transDefinition);
        }

        _source.println("};");

        return;
    }

//---------------------------------------------------------------
// Member data
//

    // Use this string to fully-qualify names.
    private String _pkgScope;
}

//
// CHANGE LOG
// $Log$
// Revision 1.3  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
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
// Revision 1.2  2005/02/21 15:38:32  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:22:14  charlesr
// Modified isLoopback method call to new signature due to moving
// method from SmcGuard to SmcCodeGenerator.
//
// Revision 1.0  2005/02/03 17:12:44  charlesr
// Initial revision
//
