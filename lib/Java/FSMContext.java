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
//  any Java class wanting to use an smc-generated state machine.
//
// Change Log
// $Log$
// Revision 1.5  2002/02/19 19:52:48  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
// Revision 1.3  2001/06/16 19:52:43  cwrapp
// Changes in release 1.0, beta 7:
// Fixes the minor code generation bugs and introduces a new
// example Java program (found at examples/Java/EX7). This
// example program is also a Java applet and can be seen at
// http://smc.sourceforge.net/SmcDemo.htm.
//
// Revision 1.2  2001/05/09 23:40:02  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
//
// Revision 1.1.1.2  2001/03/26 14:41:47  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package statemap;

import java.io.PrintStream;
import java.io.Serializable;

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
        _previous_state = null;
        _state_stack = new java.util.Stack();
        _debug_flag = false;
        _debug_stream = System.err;
    }

    // When debug is set to true, the state machine
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

    // Write the debug output to this stream.
    public PrintStream getDebugStream()
    {
        return (_debug_stream);
    }

    public void setDebugStream(PrintStream stream)
    {
        _debug_stream = stream;
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
        if (_debug_flag == true)
        {
            _debug_stream.println("NEW STATE    : " +
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
            _debug_stream.println("PUSH TO STATE: " +
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
                _debug_stream.println("POPPING ON EMPTY STATE STACK.");
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
                _debug_stream.println("POP TO STATE : " +
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

    public String getTransition()
    {
        return(_transition);
    }

    // Release all acquired resources.
    protected void finalize() throws java.lang.Throwable
    {
        _state = null;
        _transition = null;
        _previous_state = null;
        _state_stack = null;

        super.finalize();
    }

// Member data

    // The current state.
    protected State _state;

    // The current transition *name*. Used for debugging
    // purposes.
    protected String _transition;

    // Remember what state a transition left.
    protected State _previous_state;

    // This stack is used when a push transition is taken.
    protected java.util.Stack _state_stack;

    // When this flag is set to true, this class will print
    // out debug messages.
    protected boolean _debug_flag;

    // Write debug output to this stream.
    protected PrintStream _debug_stream;

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
