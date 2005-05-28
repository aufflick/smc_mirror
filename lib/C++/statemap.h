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
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 - 2004 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
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
// RCS ID
// $Id$
//
// CHANGE LOG
// (See bottom of file)
//

#if (__GNUC__ >= 3 && __GNUC_MINOR__ >= 1)
#include <iostream>
#if defined(SMC_NO_EXCEPTIONS)
#include <cassert>
#endif // SMC_NO_EXCEPTIONS
#include <cstdio>
#elif defined(WIN32)
#include <iostream>
#include <windows.h>
#if defined(SMC_NO_EXCEPTIONS)
#include <cassert>
#endif // SMC_NO_EXCEPTIONS
#else
#include <iostream.h>
#if defined(SMC_NO_EXCEPTIONS)
#include <assert.h>
#endif // SMC_NO_EXCEPTIONS
#include <stdio.h>
#endif

namespace statemap
{
#ifndef SMC_NO_EXCEPTIONS
    // This class is thrown when a pop is issued on an empty
    // state stack.
    class PopOnEmptyStateStackException
    {
    public:

        // Default constructor.
        PopOnEmptyStateStackException() {};

        // Destructor.
        ~PopOnEmptyStateStackException() {};
    };
#endif // !SMC_NO_EXCEPTIONS

    class State
    {
    // Member functions.
    public:

        const char* getName() const
        {
            return (_name);
        };

        int getId() const
        {
            return (_stateId);
        }

    protected:

        State(const char *name, int stateId)
        : _name(NULL),
          _stateId(stateId)
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

        // This state's unique identifier.
        int _stateId;

    private:
    };

    class FSMContext
    {
    // Nested classes.
    public:
    protected:
    private:

        // Implements the state stack.
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

            if (_transition != NULL)
            {
                delete[] _transition;
                _transition = NULL;
            }

            while (_state_stack != NULL)
            {
                state = _state_stack;
                _state_stack = _state_stack->_next;
                delete state;
            }
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

        // Returns the debug flag's current setting.
        bool getDebugFlag()
        {
            return(_debug_flag);
        };

        // Sets the debug flag. A true value means debugging
        // is on and false means off.
        void setDebugFlag(bool flag)
        {
            _debug_flag = flag;

            // If debugging is now off and the transition name
            // has been stored, then delete the transition.
            if (_debug_flag == false && _transition != NULL)
            {
                delete[] _transition;
                _transition = NULL;
            }

            return;
        };

#ifdef SMC_USES_IOSTREAMS
        // Returns the stream to which debug output is written.
        std::ostream& getDebugStream()
        {
            return (*_debug_stream);
        };

        // Sets the debug output stream.
        void setDebugStream(std::ostream& debug_stream)
        {
            _debug_stream = &debug_stream;
            return;
        }
#endif // SMC_USES_IOSTREAMS

        // Is this state machine already inside a transition?
        // Yes if state is null.
        bool isInTransition() const
        {
            return(_state == NULL ? true : false);
        };

        // Returns the current transition's name.
        // Used only for debugging purposes.
        char* getTransition()
        {
            return (_transition);
        };

        // Saves away the transition name only if debugging
        // is turned on.
        void setTransition(char *transition)
        {
            if (_debug_flag == true)
            {
                _transition = transition;
            }

            return;
        }

        // Clears the current state.
        void clearState()
        {
            _previous_state = _state;
            _state = NULL;
        };

        // Returns the state which a transition left.
        // May be NULL.
        State* getPreviousState()
        {
            return (_previous_state);
        }

        // Sets the current state to the specified state.
        void setState(const State& state)
        {
            _state = const_cast<State *>(&state);

            if (_debug_flag == true)
            {
#ifdef SMC_USES_IOSTREAMS
                *_debug_stream << "NEW STATE    : "
                               << _state->getName()
                               << std::endl;
#else
                TRACE("NEW STATE    : %s\n\r",
                      _state->getName());
#endif // SMC_USES_IOSTREAMS
            }
        };

        // Returns true if the state stack is empty and false
        // otherwise.
        bool isStateStackEmpty() const
        {
            return (_state_stack == NULL);
        }

        // Returns the state stack's depth.
        int getStateStackDepth() const
        {
            StateEntry *state_ptr;
            int retval;

            for (state_ptr = _state_stack, retval = 0;
                 state_ptr != NULL;
                 state_ptr = state_ptr->getNext(), ++retval)
                ;

            return (retval);
        }

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
#ifdef SMC_USES_IOSTREAMS
                *_debug_stream << "PUSH TO STATE: "
                               << _state->getName()
                               << std::endl;
#else
                TRACE("PUSH TO STATE: %s\n\r",
                      _state->getName());
#endif // SMC_USES_IOSTREAMS
            }
        };

        // Make the state on top of the state stack the
        // current state.
        void popState()
        {
            StateEntry *entry;

            // Popping when there was no previous push is an error.
#ifdef SMC_NO_EXCEPTIONS
            assert(_state_stack != NULL);
#else
            if (_state_stack == NULL)
            {
                throw PopOnEmptyStateStackException();
            }
#endif // SMC_NO_EXCEPTIONS

            _state = _state_stack->getState();
            entry = _state_stack;
            _state_stack = _state_stack->getNext();
            delete entry;

            if (_debug_flag == true)
            {
#ifdef SMC_USES_IOSTREAMS
                *_debug_stream << "POP TO STATE : "
                               << _state->getName()
                               << std::endl;
#else
                TRACE("POP TO STATE : %s\n\r",
                      _state->getName());
#endif // SMC_USES_IOSTREAMS
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

    protected:

        // Default constructor.
        FSMContext()
        : _state(NULL),
          _previous_state(NULL),
          _state_stack(NULL),
          _transition(NULL),
#ifdef SMC_USES_IOSTREAMS
          _debug_flag(false),
          _debug_stream(&std::cerr)
#else
          _debug_flag(false)
#endif // SMC_USES_IOSTREAMS
        {};

    private:

        // I don't believe that it makes sense to copy a
        // context. It may make sense to copy the application
        // class but the new object is *not* in the same
        // state as the old - the new object must start in
        // the FSM's initial state. Therefore, the copy
        // constructor is private in order to prevent it
        // being used.
        FSMContext(const FSMContext& fsm) {};

    // Member data
    public:
    protected:

        // The current state of the finite state machine.
        State *_state;

        // Remember which state a transition left.
        State *_previous_state;

        // The stack of pushed states.
        StateEntry *_state_stack;

        // The current transition *name*. Use for debugging
        // purposes.
        char *_transition;

    private:

        // When this flag is set to true, this class will print
        // out debug messages.
        bool _debug_flag;

// Include the following only if C++ iostreams are being used.
#ifdef SMC_USES_IOSTREAMS
        // When FSM debugging is on, debug messages will be
        // written to this output stream. This stream is set to
        // standard error by default.
        std::ostream *_debug_stream;
#endif // SMC_USES_IOSTREAMS

    }; // end of class FSMContext

    // This class is thrown when a transition is issued
    // but there is no current state. This happens when
    // a transition is issued from within a transition
    // action.
    class StateUndefinedException
    {
    public:

        // Default constructor.
        StateUndefinedException() {};

        // Destructor.
        ~StateUndefinedException() {};
    };

    // This class is thrown when a transition is issued
    // but there is no code to handle it.
    class TransitionUndefinedException
    {
    // Member methods.
    public:

        // Default constructor.
        TransitionUndefinedException()
        : _state(NULL),
          _transition(NULL)
        {};

        // Construct an exception using the specified state
        // and transition.
        TransitionUndefinedException(const char *state,
                                     const char *transition)
        : _state(_CopyString(state)),
          _transition(_CopyString(transition))
        {};

        // Copy constructor.
        TransitionUndefinedException(
            const TransitionUndefinedException& ex)
        : _state(_CopyString(ex._state)),
          _transition(_CopyString(ex._transition))
        {};

        // Destructor.
        ~TransitionUndefinedException()
        {
            if (_state != NULL)
            {
                delete[] _state;
                _state = NULL;
            }

            if (_transition != NULL)
            {
                delete[] _transition;
                _transition = NULL;
            }
        };

        // Assignment operator.
        const TransitionUndefinedException&
            operator=(const TransitionUndefinedException& ex)
        {
            // Don't do self assignment.
            if (this != &ex)
            {
                if (_state != NULL)
                {
                    delete[] _state;
                    _state = NULL;
                }

                if (_transition != NULL)
                {
                    delete[] _transition;
                    _transition = NULL;
                }

                _state = _CopyString(ex._state);
                _transition = _CopyString(ex._transition);
            }

            return (*this);
        };

        // Returns the state. May be NULL.
        char* getState() const
        {
            return(_state);
        };

        // Returns the transition. May be NULL.
        char* getTransition() const
        {
            return (_transition);
        };

    protected:
    private:

        // Copies a non-null string and returns the copy.
        static char* _CopyString(const char *s)
        {
            char *retval = NULL;

            if (s != NULL)
            {
                retval = new char[strlen(s) + 1];
                (void) strcpy(retval, s);
            }

            return (retval);
        };

    // Member data.
    public:
    protected:
    private:

		char *_state;
		char *_transition;
    };

    // This class is thrown when a state ID is either less than
    // the minimal value or greater than the maximal value.
    class IndexOutOfBoundsException
    {
    // Member methods.
    public:

        // Default constructor.
        IndexOutOfBoundsException()
        : _index(0),
          _minIndex(0),
          _maxIndex(0)
        {};

        // Constructs an exception using the specified index,
        // minimum index and maximum index.
        IndexOutOfBoundsException(int index,
                                  int minIndex,
                                  int maxIndex)
        : _index(index),
          _minIndex(minIndex),
          _maxIndex(maxIndex)
        {};

        // Copy constructor.
        IndexOutOfBoundsException(
            const IndexOutOfBoundsException& ex)
        : _index(ex._index),
          _minIndex(ex._minIndex),
          _maxIndex(ex._maxIndex)
        {};

        // Destructor.
        ~IndexOutOfBoundsException()
        {};

        // Assignment operator.
        const IndexOutOfBoundsException&
            operator=(const IndexOutOfBoundsException& ex)
        {
            // Don't do self assignment.
            if (this != &ex)
            {
                _index = ex._index;
                _minIndex = ex._minIndex;
                _maxIndex = ex._maxIndex;
            }

            return (*this);
        };

        // Returns the out-of-bounds index.
        int getIndex() const
        {
            return(_index);
        };

        // Returns the minimum allowed index value.
        int getMinIndex() const
        {
            return (_minIndex);
        };

        // Returns the maximum allowed index value.
        int getMaxIndex() const
        {
            return (_maxIndex);
        };

    protected:
    private:

    // Member data.
    public:
    protected:
    private:

		int _index;
		int _minIndex;
        int _maxIndex;
    };
};

//
// CHANGE LOG
// $Log$
// Revision 1.6  2005/05/28 18:44:13  cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
//
// Revision 1.2  2005/02/21 19:01:42  charlesr
// Changed State::_id to State::_stateId because of Object-C++
// reserved word conflict.
//
// Revision 1.1  2004/05/31 13:44:41  charlesr
// Added support for non-iostreams output.
//
// Revision 1.0  2003/12/14 20:37:49  charlesr
// Initial revision

#endif
