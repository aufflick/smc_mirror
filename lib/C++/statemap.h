#ifndef _H_STATEMAP
#define _H_STATEMAP

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
// The Original Code is State Map Compiler (SMC).
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
// Namespace
//	statemap
//
// Description
//  This namespace contains the finite state machine context class.
//  The user can derive FSM contexts from this class and interface
//  to them with the methods of this class.
//
// Notes
//	The finite state machine needs to be initialized to the starting
//	state of the FSM.  This must be done manually in the constructor
//	of the derived class. 
//
// Author
//	C. W. Rapp
//
// Change Log
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//
#ifdef WIN32
#include <iostream>
#include <windows.h>
#else
#include <iostream.h>
#endif
#include <assert.h>
#include <stdio.h>

#ifdef SMC_TRANS_Q
#include <memory.h>
#endif

#ifdef WIN32
using namespace std;
#endif

namespace statemap
{
    class State
    {
    // Member functions.
    public:
        const char* getName()
        {
            return(_name);
        };

    protected:
        State(const char *name)
        : _name(NULL)
        {
            if (name != NULL)
            {
                _name = new char[strlen(name) + 1];
                strcpy(_name, name);
            }
            else
            {
                _name = new char[13];
                strcpy(_name, "NAME NOT SET");
            }
        };

        virtual ~State()
        {
            if (_name != NULL)
            {
                delete[] _name;
                _name = NULL;
            }
        };

    private:
        // Make the default and copy constructors private to
        // prevent their use.
        State() {};
        State(const State&) {};

    // Member data.
    public:
    protected:
        // This state's printable name.
        char *_name;

    private:
    };

    class FSMContext
    {
    // Nested classes.
    public:

#ifdef SMC_TRANS_Q
        class TransEntry
        {
        // Member functions
        public:
            TransEntry(int trans_index,
                       unsigned char **argv,
                       int argc)
            : _trans_index(trans_index),
              _argv(argv),
              _argc(argc),
              _next(NULL)
            {};

            ~TransEntry()
            {
                if (_argv != NULL)
                {
                    int i;

                    for (i = 0; i < _argc; ++i)
                    {
                        if (_argv[i] != NULL)
                        {
                            delete[] _argv[i];
                            _argv[i] = NULL;
                        }
                    }

                    delete[] _argv;
                    _argv = NULL;
                }

                _next = NULL;

                return;
            };

            int getIndex() const
            {
                return(_trans_index);
            };

            int getArgc() const
            {
                return(_argc);
            };

            unsigned char** getArgv() const
            {
                return(_argv);
            };

        protected:
        private:

        // Member data.
        public:
        protected:
        private:
            // The transition routine's ordinal number.
            int _trans_index;

            // The transition's arguments.
            unsigned char **_argv;
            int _argc;

            // The next enqueued transition.
            TransEntry *_next;

        // Friends
            friend class FSMContext;
        }; // end of class TransEntry
#endif

    protected:
    private:

        class StateEntry
        {
        // Member functions.
        public:
            StateEntry(State *state, StateEntry *next)
            : _state(state),
              _next(next)
            {};

            ~StateEntry()
            {
                _state = NULL;
                _next = NULL;
            };

            State* getState()
            {
                return(_state);
            };

            StateEntry* getNext()
            { return(_next); };

        protected:
        private:

        // Member data.
        public:
        protected:
        private:
            State *_state;
            StateEntry *_next;

        // Friends
            friend class FSMContext;
        }; // end of class StateEntry

    // Member functions
    public:
        // Destructor.
        virtual ~FSMContext()
        {
            StateEntry *state;
#ifdef SMC_TRANS_Q
            TransEntry *transition;
#endif

            while (_state_stack != NULL)
            {
                state = _state_stack;
                _state_stack = _state_stack->_next;
                delete state;
            }

#ifdef SMC_TRANS_Q
            while (_trans_queue_head != NULL)
            {
                transition = _trans_queue_head;
                _trans_queue_head = _trans_queue_head->_next;
                delete transition;
            }
            _trans_queue_tail = NULL;
#endif
        };

        // Comparison and assignment operators
        // Assignment operator
        FSMContext& operator=(const FSMContext& fsm)
        {
            // Don't do the assignment if the left and right
            // hand sides are the same object.
            if (this != &fsm)
            {
                _state = fsm._state;
            }

            return(*this);
        };

        // Exact same object (is it me?)
        int same(const FSMContext& fsm) const
        {
            return(this == &fsm);
        };

        bool getDebugFlag()
        {
            return(_debug_flag);
        };

        void setDebugFlag(bool flag)
        {
            _debug_flag = flag;
            return;
        };

        // Is this state map already inside a transition?
        // Yes if state is null.
        bool isInTransition() const
        {
            return(_state == NULL ? true : false);
        };

        // Clear the current state.
        void clearState()
        { _state = NULL; };

        // Set the current state to the specified state.
        void setState(const State& state)
        {
            _state = const_cast<State *>(&state);

            if (_debug_flag == true)
            {
                cerr << "NEW STATE    : "
                     << _state->getName()
                     << endl;
            }
        };

        // Push the current state on top of the state stack
        // and make the specified state the current state.
        void pushState(const State& state)
        {
            StateEntry *new_entry;

            // Do the push only if there is a state to be pushed
            // on the stack.
            if (_state != NULL)
            {
                new_entry = new StateEntry(_state, _state_stack);
                _state_stack = new_entry;
            }

            _state = const_cast<State *>(&state);

            if (_debug_flag == true)
            {
                cerr << "PUSH TO STATE: "
                     << _state->getName()
                     << endl;
            }
        };

        // Make the state on top of the state stack the
        // current state.
        void popState()
        {
            StateEntry *entry;

            // Popping when there was no previous push is an error.
            assert(_state_stack != NULL);

            _state = _state_stack->getState();
            entry = _state_stack;
            _state_stack = _state_stack->getNext();
            delete entry;

            if (_debug_flag == true)
            {
                cerr << "POP TO STATE : "
                     << _state->getName()
                     << endl;
            }
        };

        // Remove all states from the state stack.
        void emptyStateStack()
        {
            StateEntry *state_ptr,
                       *next_ptr;

            for (state_ptr = _state_stack;
                 state_ptr != NULL;
                 state_ptr = next_ptr)
            {
                next_ptr = state_ptr->getNext();
                delete state_ptr;
            }

            _state_stack = NULL;
        };

#ifdef SMC_TRANS_Q
        // Enqueue and dequeue a transition.
        void enqueue_transition(TransEntry *transition)
        {
            if (_trans_queue_head == NULL)
            {
                _trans_queue_head = transition;
            }
            else
            {
                _trans_queue_tail->_next = transition;
            }

            _trans_queue_tail = transition;
            return;
        };

        TransEntry* dequeue_transition()
        {
            TransEntry *retval;

            retval = _trans_queue_head;
            if (_trans_queue_head != NULL)
            {
                _trans_queue_head = _trans_queue_head->_next;
                if (_trans_queue_head == NULL)
                {
                    _trans_queue_tail = NULL;
                }
            }

            return(retval);
        };
#endif

    protected:
        // Default constructor.
        FSMContext()
        : _state(NULL),
          _state_stack(NULL),
#ifdef SMC_TRANS_Q
          _trans_queue_head(NULL),
          _trans_queue_tail(NULL),
#endif
          _debug_flag(false)
        {};

    private:
        // I don't believe that it makes sense to copy a
        // context. It may make sense to copy the application
        // class but the new object is *not* in the same
        // state as the old - the new object must start in
        // the map's initial state. Therefore, the copy
        // constructor is private in order to prevent it
        // being used.
        FSMContext(const FSMContext& fsm) {};

    // Member data
    public:
    protected:
        // The current state of the finite state machine.
        State *_state;

        // The stack of pushed states.
        StateEntry *_state_stack;

#ifdef SMC_TRANS_Q
        // Queued transitions.
        TransEntry *_trans_queue_head;
        TransEntry *_trans_queue_tail;
#endif

    private:

        // When this flag is set to true, this class will print
        // out debug messages.
        bool _debug_flag;
    }; // end of class FSMContext
}

#endif
