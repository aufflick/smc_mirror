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
// Copyright (C) 2000 - 2005. Charles W. Rapp.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Performs a global syntax check on the various elements of the
 * abstract syntax tree.
 * @see SmcElement
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcSyntaxChecker
    extends SmcVisitor
{
//---------------------------------------------------------------
// Member methods
//

    /**
     * Creates a syntax checker for the named FSM and target
     * programming language.
     * @param fsm the finite state machine's name.
     * @param targetLanguage the target programming language.
     */
    public SmcSyntaxChecker(String fsm, int targetLanguage)
    {
        super ();

        _fsmName = fsm;
        _targetLanguage = targetLanguage;
        _messages = new ArrayList<SmcMessage>();
        _checkFlag = true;
    } // end of SmcSyntaxCheck(String, int)

    /**
     * Returns <code>true</code> if no errors were found and
     * <code>false</code> if there are syntax errors.
     * @return <code>true</code> if no errors were found and
     * <code>false</code> if there are syntax errors.
     */
    public boolean isValid()
    {
        return (_checkFlag);
    } // end of isValid()

    /**
     * Returns a list of warning and error messages.
     * @return a list of warning and error messages.
     */
    public List<SmcMessage> getMessages()
    {
        return (_messages);
    } // end of getMessages()

    //-----------------------------------------------------------
    // SmcVisitor Methods.
    //

    // Verify that the context class and source files exist.
    public void visit(SmcFSM fsm)
    {
        String startState = fsm.getStartState();
        String context = fsm.getContext();
        String header = fsm.getHeader();
        int headerLine = fsm.getHeaderLine();

        // Check if the start state and class has been
        // specified (and header file for C++ generation).
        if (startState == null ||
            startState.length() == 0)
        {
            _messages.add(
                new SmcMessage(_fsmName,
                               0,
                               SmcMessage.ERROR,
                               "\"%start\" missing."));

            _checkFlag = false;
        }

        if (context.length() == 0)
        {
            _messages.add(
                new SmcMessage(_fsmName,
                               0,
                               SmcMessage.ERROR,
                               "\"%class\" missing."));

            _checkFlag = false;
        }

        if (_targetLanguage == Smc.C_PLUS_PLUS &&
            (header == null ||
             header.length() == 0))
        {
            _messages.add(
                new SmcMessage(_fsmName,
                               0,
                               SmcMessage.ERROR,
                               "\"%header\" missing."));

            _checkFlag = false;
        }

        // If this is Java, then <name> in <name>.sm must match
        // %class <name>.
        // That is foo.sm is the FSM for foo.java.
        if (_targetLanguage == Smc.JAVA &&
            fsm.getName().equals(context) == false)
        {
            _messages.add(
                new SmcMessage(
                    _fsmName,
                    0,
                    SmcMessage.ERROR,
                    ".sm file name \"" +
                    fsm.getName() +
                    "\" does not match context class name \"" +
                    context +
                    "\"."));

            _checkFlag = false;
        }

        // Check if all the end states are valid.
        // Check each map in turn. But don't stop when an error
        // is found - check all the transitions.
        for (SmcMap map: fsm.getMaps())
        {
            map.accept(this);
        }

        return;
    } // end of visit(SmcFSM)

    // Check the map's states.
    public void visit(SmcMap map)
    {
        // Check the real states first.
        for (SmcState state: map.getStates())
        {
            state.accept(this);
        }

        // Now check the default state.
        if (map.hasDefaultState() == true)
        {
            map.getDefaultState().accept(this);
        }

        return;
    } // end of visit(SmcMap)

    // Check if the state's transitions contain valid end states.
    public void visit(SmcState state)
    {
        for (SmcTransition transition: state.getTransitions())
        {
            transition.accept(this);
        }

        return;
    } // end of visit(SmcState)

    // Check the transition's guards.
    public void visit(SmcTransition transition)
    {
        List<SmcGuard> guards = transition.getGuards();
        int guardCount = guards.size();

        // If this is Tcl, then make sure the parameter types
        // are either value or reference.
        if (_targetLanguage == Smc.TCL)
        {
            for (SmcParameter parameter:
                     transition.getParameters())
            {
                parameter.accept(this);
            }
        }

        // If this transition has multiple definitions in this
        // state, then each transition must have a unique guard.
        if (guardCount > 1)
        {
            SmcState state = transition.getState();
            String mapName = state.getMap().getName();
            String stateName = state.getClassName();
            String transName = transition.getName();
            List<String> conditions =
                new ArrayList<String>(guardCount);
            String condition;

            for (SmcGuard guard: guards)
            {
                condition = guard.getCondition();

                // Each guard must have a unique condition.
                if (conditions.contains(condition) == true)
                {
                    StringBuffer text = new StringBuffer(500);

                    text.append("State ");
                    text.append(mapName);
                    text.append("::");
                    text.append(stateName);
                    text.append(
                        " has multiple transitions with ");
                    text.append("same name (\"");
                    text.append(transName);
                    text.append("\") and guard (\"");
                    text.append(condition);
                    text.append("\").");

                    _messages.add(
                        new SmcMessage(
                            _fsmName,
                            guard.getLineNumber(),
                            SmcMessage.ERROR,
                            text.toString()));

                    _checkFlag = false;
                }
                else
                {
                    conditions.add(condition);
                }
            }
        }

        for (SmcGuard guard: guards)
        {
            guard.accept(this);
        }

        return;
    } // end of visit(SmcTransition)

    // Now check if the guard has a valid end state.
    public void visit(SmcGuard guard)
    {
        String endState = guard.getEndState();
        String condition = guard.getCondition();

        // Ignore pop transitions.
        if (guard.getTransType() == Smc.TRANS_POP)
        {
            // Do nothing.
        }
        else if (endState.compareToIgnoreCase("default") == 0)
        {
            _messages.add(
                new SmcMessage(
                    _fsmName,
                    guard.getLineNumber(),
                    SmcMessage.ERROR,
                    "may not transition to the default state."));

            _checkFlag = false;
        }
        // "nil" is always a valid end state.
        else if (endState.compareTo("nil") != 0 &&
                 _findState(endState, guard) == false)
        {
            _messages.add(
                new SmcMessage(
                    _fsmName,
                    guard.getLineNumber(),
                    SmcMessage.ERROR,
                    "no such state as \"" + endState + "\"."));

            _checkFlag = false;
        }

        return;
    } // end of visit(SmcGuard)

    // Check if the parameter types are acceptable.
    public void visit(SmcParameter parameter)
    {
        String typeName = parameter.getType();

        // v. 2.0.2: Tcl has two artificial types: value and
        // reference. Value means that this parameter is passed
        // call-by-value (a $ is prepended to the parameter).
        // Reference means that this parameters name is passed
        // (no $ is prepended). Verify that the type name is one
        // of these two.
        if (_targetLanguage == Smc.TCL &&
            typeName.equals(
                SmcParameter.TCL_VALUE_TYPE) == false &&
            typeName.equals(
                SmcParameter.TCL_REFERENCE_TYPE) == false)
        {
            _messages.add(
                new SmcMessage(
                    _fsmName,
                    parameter.getLineNumber(),
                    SmcMessage.ERROR,
                    "Tcl parameter type not \"" +
                    SmcParameter.TCL_VALUE_TYPE +
                    "\" or \"" +
                    SmcParameter.TCL_REFERENCE_TYPE +
                    "\" but \"" +
                    typeName +
                    "\"."));

            _checkFlag = false;
        }

        return;
    } // end of visit(SmcParameter)

    //
    // end of SmcVisitor Methods.
    //-----------------------------------------------------------

    // Find if this named state appears in the FSM.
    private boolean _findState(String endState, SmcGuard guard)
    {
        int index = endState.indexOf("::");
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        boolean retval = false;

        // Is end state of the form "map::state"?
        if (index < 0)
        {
            // No. This state must then appear in this map.
            retval = map.isKnownState(endState);
        }
        else
        {
            // Yes, the end state is of the form "map::state".
            String mapName = endState.substring(0, index);
            String stateName = endState.substring(index + 2);

            // Is the map name actually this map name?
            // In other words, it the end state fully qualified?
            if (mapName.equals(map.getName()) == true)
            {
                // Yes, it is this map.
                // Is the end state the same as this state?
                if (stateName.equals(state.getName()) == true)
                {
                    // Yes, then this is a valid end state.
                    retval = true;
                }
                else
                {
                    // No. Have the map check if this state is
                    // valid.
                    retval = map.isKnownState(stateName);
                }
            }
            else
            {
                // This name is for a state in another map.
                // Have the parse tree return the map and then
                // check that map for the state.
                map = map.getFSM().findMap(mapName);
                if (map != null)
                {
                    retval = map.isKnownState(stateName);
                }
            }
        }

        return (retval);
    } // end of _findState(String, SmcGuard)

//---------------------------------------------------------------
// Member data
//

    // The FSM's name.
    private String _fsmName;

    // The target programming language.
    private int _targetLanguage;

    // Store warning and error messages in this list. Do not
    // output them. Let the application do that.
    private List<SmcMessage> _messages;

    // Set this flag to false if the check fails.
    private boolean _checkFlag;
} // end of SmcSyntaxCheck

//
// CHANGE LOG
// $Log$
// Revision 1.6  2007/02/21 13:56:47  cwrapp
// Moved Java code to release 1.5.0
//
