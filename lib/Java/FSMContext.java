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
// The Original Code is  State Map Compiler(SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// RCS ID
// $Id$
//
// statemap.java --
//
//  This package defines the FSMContext class which must be inherited by
//  any Java class wanting to use an smc-generated state map.
//
// Change Log
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package statemap;

import java.lang.reflect.Method;
import java.io.Serializable;

// statemap.FSMContext --
//
//  All Java classes wanting to use an SMC-generated state map
//  must extend this class. Since FSMContext provides
//  functionality, it was not possible to implement it as an
//  interface. See the SMC FAQ for how a class can use a state
//  map when that class is already extending another class.

public abstract class FSMContext
    implements Serializable
{
// Member functions

    public FSMContext()
    {
        // There is no state until the application explicitly
        // sets the initial state.
        _state = null;
        _previous_state = null;
        _state_stack = new java.util.Stack();
        _debug_flag = false;
    }

    // When debug is set to true, the state map
    // will print messages to the console.
    public boolean getDebugFlag()
    {
        return(_debug_flag);
    }

    public void setDebugFlag(boolean flag)
    {
        _debug_flag = flag;
        return;
    }

    // Is this state map in a transition? If state is null, then
    // true; otherwise, false.
    public boolean isInTransition()
    {
        return(_state == null ? true : false);
    }

    public void setState(State state)
    {
        if (_debug_flag == true)
        {
            System.err.println("NEW STATE    : " +
                               state.getName());
        }

        // Should this be done?
        // _previous_state = _state;
        _state = state;

        return;
    }

    public void clearState()
    {
        _previous_state = _state;
        _state = null;

        return;
    }

    public State getPreviousState() throws java.lang.NullPointerException
    {
        if (_previous_state == null)
        {
            throw new java.lang.NullPointerException();
        }
        else
        {
            return(_previous_state);
        }
    }

    public void pushState(State state)
    {
        if (_debug_flag == true)
        {
            System.out.println("PUSH TO STATE: " +
                               state.getName());
        }

        if (_state != null)
        {
            _state_stack.push(_state);
        }

        _state = state;

        return;
    }

    public void popState() throws java.util.EmptyStackException
    {
        if (_state_stack.empty() == true)
        {
            if (_debug_flag == true)
            {
                System.out.println("POPPING ON EMPTY STATE STACK.");
            }

            throw new java.util.EmptyStackException();
        }
        else
        {
            // The pop method removes the top element
            // from the stack and returns it.
            _state = (State) _state_stack.pop();

            if (_debug_flag == true)
            {
                System.out.println("POP TO STATE : " +
                                   _state.getName());
            }
        }

        return;
    }

    public void emptyStateStack()
    {
        while (_state_stack.empty() == false)
        {
            _state_stack.pop();
        }

        return;
    }

    // Release all acquired resources.
    protected void finalize() throws java.lang.Throwable
    {
        _state = null;
        _previous_state = null;
        _state_stack = null;

        super.finalize();
    }

// Member data

    // The current state.
    protected State _state;

    // Remember what state a transition left.
    protected State _previous_state;

    // This stack is used when a push transition is taken.
    protected java.util.Stack _state_stack;

    // When this flag is set to true, this class will print
    // out debug messages.
    protected boolean _debug_flag;

// Inner classes

    /* Transition queuing will not be released. This code will be
     * left in place but commented out.
     *
    // This entry is used to store transitions on the transition
    // queue.
    public final class TransEntry
    {
        public TransEntry(String method_name,
                          Object[] args,
                          Class[] parameters)
        {
            _method_name = method_name;
            _args = args;
            _parameters = parameters;
            return;
        }

        public String getMethodName()
        {
            return(_method_name);
        }

        public Object[] getArgs()
        {
            return(_args);
        }

        public Class[] getParameters()
        {
            return(_parameters);
        }

        public void free()
        {
            int i;

            _method_name = null;
            for (i = 0; i < _args.length; ++i)
            {
                _args[i] = null;
                _parameters[i] = null;
            }
            _args = null;
            _parameters = null;

            return;
        }

        private String   _method_name;
        private Object[] _args;
        private Class[]  _parameters;
    }
     */
}
