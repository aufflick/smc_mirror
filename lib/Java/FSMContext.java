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
// The Original Code is  State Machine Compiler(SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 - 2003 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// statemap.java --
//
//  This package defines the FSMContext class which must be inherited by
//  any Java class wanting to use an smc-generated state machine.
//
// RCS ID
// $Id$
//
// Change Log
// $Log$
// Revision 1.7  2005/05/28 18:44:13  cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
//
// Revision 1.1  2005/02/21 19:03:38  charlesr
// Variable name clean up.
//
// Revision 1.0  2003/12/14 20:38:40  charlesr
// Initial revision
//

package statemap;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.EmptyStackException;

// statemap.FSMContext --
//
//  All Java classes wanting to use an SMC-generated state machine
//  must extend this class. Since FSMContext provides
//  functionality, it was not possible to implement it as an
//  interface. See the SMC FAQ for how a class can use a state
//  machine when that class is already extending another class.

public abstract class FSMContext
    implements Serializable
{
// Member functions

    public FSMContext()
    {
        // There is no state until the application explicitly
        // sets the initial state.
        _state = null;
        _transition = "";
        _previousState = null;
        _stateStack = null;
        _debugFlag = false;
        _debugStream = System.err;
    }

    // When debug is set to true, the state machine
    // will print messages to the console.
    public boolean getDebugFlag()
    {
        return(_debugFlag && _debugStream != null);
    }

    public void setDebugFlag(boolean flag)
    {
        _debugFlag = flag;
        return;
    }

    // Write the debug output to this stream.
    public PrintStream getDebugStream()
    {
        return (_debugStream == null ? System.err : _debugStream);
    }

    public void setDebugStream(PrintStream stream)
    {
        _debugStream = stream;
        return;
    }

    // Is this state machine in a transition? If state is null,
    // then true; otherwise, false.
    public boolean isInTransition()
    {
        return(_state == null ? true : false);
    }

    public void setState(State state)
    {
        if (getDebugFlag() == true)
        {
            getDebugStream().println("NEW STATE    : " +
                                     state.getName());
        }

        _state = state;

        return;
    }

    public void clearState()
    {
        _previousState = _state;
        _state = null;

        return;
    }

    public State getPreviousState()
        throws NullPointerException
    {
        if (_previousState == null)
        {
            throw (new NullPointerException());
        }
        else
        {
            return(_previousState);
        }
    }

    public void pushState(State state)
    {
        if (_state == null)
        {
            throw (new NullPointerException());
        }

        if (getDebugFlag() == true)
        {
            getDebugStream().println("PUSH TO STATE: " +
                                     state.getName());
        }

        if (_stateStack == null)
        {
            _stateStack = new java.util.Stack();
        }

        _stateStack.push(_state);
        _state = state;

        return;
    }

    public void popState()
        throws EmptyStackException
    {
        if (_stateStack == null ||
            _stateStack.isEmpty() == true)
        {
            if (getDebugFlag() == true)
            {
                getDebugStream().println("POPPING ON EMPTY STATE STACK.");
            }

            throw (new EmptyStackException());
        }
        else
        {
            // The pop method removes the top element
            // from the stack and returns it.
            _state = (State) _stateStack.pop();

            if (_stateStack.isEmpty() == true)
            {
                _stateStack = null;
            }

            if (getDebugFlag() == true)
            {
                getDebugStream().println("POP TO STATE : " +
                                      _state.getName());
            }
        }

        return;
    }

    public void emptyStateStack()
    {
        _stateStack.clear();
        _stateStack = null;

        return;
    }

    public String getTransition()
    {
        return(_transition);
    }

// Member data

    // The current state.
    transient protected State _state;

    // The current transition *name*. Used for debugging
    // purposes.
    transient protected String _transition;

    // Remember what state a transition left.
    // Do no persist the previous state because an FSM should be
    // serialized while in transition.
    transient protected State _previousState;

    // This stack is used when a push transition is taken.
    transient protected java.util.Stack _stateStack;

    // When this flag is set to true, this class will print
    // out debug messages.
    transient protected boolean _debugFlag;

    // Write debug output to this stream.
    transient protected PrintStream _debugStream;
}
