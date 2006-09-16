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
// The Original Code is  State Machine Compiler(SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2004. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//
// RCS ID
// $Id$
//
// statemap.java --
//
//  This package defines the FSMContext class which is inherited
//  by the smc-generated application FSM context class.
//
// CHANGE LOG
// (See the bottom of this file.)
//

using System;
using System.IO;
using System.Diagnostics;

namespace statemap
{
    // statemap.FSMContext --
    //
    //  Base class for the smc-generated application FSM
    //  context class.

    [Serializable]
    public abstract class FSMContext
    {
    //-----------------------------------------------------------
    // Member functions.
    //

        public FSMContext()
        {
            // There is no state until the application explicitly
            // sets the initial state.
            _state = null;
            _transition = "";
            _previousState = null;
            _stateStack = null;
            _debugFlag = false;
            _debugStream = null;
        }

        // DEPRECATED
        // As of v. 4.3.3, System.Diagnostics.Trace is
        // used instead of the _debugFlag, _debugStream
        // pair.
        // Used to enable debugging output
        public bool Debug
        {
            get
            {
                return _debugFlag;
            }
            set
            {
                _debugFlag = value;
            }
        }

        // DEPRECATED
        // As of v. 4.3.3, System.Diagnostics.Trace is
        // used instead of the _debugFlag, _debugStream
        // pair. _debugStream will always be null.
        // Used to set the output text writer.
        public TextWriter DebugStream
        {
            get
            {
                return _debugStream;
            }
            set
            {
                // DEPRECATED.
                // _debugStream = value;
            }
        }

        // Is this state machine in a transition? If state is
        // null, then true; otherwise, false.
        public bool InTransition
        {
            get 
            {
                return(_state == null ? true : false);
            }
        }

        public void SetState(State state)
        {
#if TRACE
            Trace.WriteLine("NEW STATE    : " +    state.Name);
#endif

            _state = state;
            return;
        }

        public void ClearState()
        {
            _previousState = _state;
            _state = null;

            return;
        }

        public State PreviousState
        {
            get
            {
                if (_previousState != null)
                {
                    return(_previousState);
                }

                throw
                    new System.NullReferenceException(
                        "Previous state not set.");
            }
        }

        public void PushState(State state)
        {
#if TRACE
            Trace.WriteLine("PUSH TO STATE: " +    state.Name);
#endif

            if (_state != null)
            {
                if (_stateStack == null)
                {
                    _stateStack = new System.Collections.Stack();
                }

                _stateStack.Push(_state);
            }

            _state = state;

            return;
        }

        public void PopState()
        {
            if (_stateStack.Count == 0)
            {
#if TRACE
                Trace.WriteLine("POPPING ON EMPTY STATE STACK.");
#endif

                throw new
                    System.InvalidOperationException(
                        "popping an empty state stack");
            }
            else
            {
                // The pop method removes the top element
                // from the stack and returns it.
                _state = (State) _stateStack.Pop();

#if TRACE
                Trace.WriteLine("POP TO STATE : " + _state.Name);
#endif
            }

            return;
        }

        public void EmptyStateStack()
        {
            _stateStack.Clear();
        }

        public string GetTransition()
        {
            return _transition;
        }

        // Release all acquired resources.
        ~FSMContext()  //TODO: Add disposable
        {
            _state = null;
            _transition = null;
            _previousState = null;
            _stateStack = null;
        }

    //-----------------------------------------------------------
    // Member data
    //

        // The current state.
        [NonSerialized]
        protected State _state;

        // The current transition *name*. Used for debugging
        // purposes.
        [NonSerialized]
        protected string _transition;

        // Remember what state a transition left.
        // Do no persist the previous state because an FSM should
        // be serialized while in transition.
        [NonSerialized]
        protected State _previousState;

        // This stack is used when a push transition is taken.
        [NonSerialized]
        protected System.Collections.Stack _stateStack;

        // DEPRECATED
        // As of v. 4.3.3, System.Diagnostics.Trace is
        // used instead of the _debugFlag, _debugStream
        // pair. _debugStream will always be null.
        // When this flag is set to true, this class will print
        // out debug messages.
        [NonSerialized]
        protected bool _debugFlag;

        // Write debug output to this stream.
        [NonSerialized]
        protected TextWriter _debugStream;
    } // end of class FSMContext
} // end of namespace statemap

//
// CHANGE LOG
// $Log$
// Revision 1.4  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.3  2006/06/03 19:39:25  cwrapp
// Final v. 4.3.1 check in.
//
// Revision 1.2  2006/04/22 12:45:25  cwrapp
// Version 4.3.1
//
// Revision 1.1  2005/05/28 18:44:13  cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
//
// Revision 1.0  2004/09/06 16:32:15  charlesr
// Initial revision
//
