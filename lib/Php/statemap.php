<?php

/*
 The contents of this file are subject to the Mozilla Public
 License Version 1.1 (the "License"); you may not use this file
 except in compliance with the License. You may obtain a copy of
 the License at http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS
 IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 implied. See the License for the specific language governing
 rights and limitations under the License.

 The Original Code is State Machine Compiler (SMC).

 The Initial Developer of the Original Code is Charles W. Rapp.
 Portions created by Charles W. Rapp are
 Copyright (C) 2005. Charles W. Rapp.
 All Rights Reserved.

 Port (from the Python port) to PHP5 by Toni Arnold

 Copyright 2004, Francois Perrad.
 All Rights Reserved.

 Contributor(s):

 See: http://smc.sourceforge.net/

 RCS ID
 $Id$

 CHANGE LOG
 $Log$
 Revision 1.1  2008/04/22 16:00:39  fperrad
 - add PHP language (patch from Toni Arnold)


*/

/*
A StateUndefinedException is thrown by
an SMC-generated state machine whenever a transition is taken
and there is no state currently set. This occurs when a
transition is issued from within a transition action."""
*/
class StateUndefinedException extends Exception
{
}

/*
A TransitionUndefinedException is thrown by
an SMC-generated state machine whenever a transition is taken
which:

 - Is not explicitly defined in the current state.
 - Is not explicitly defined in the current FSM's default state.
 - There is no Default transition in the current state."""
*/
class TransitionUndefinedException extends Exception
{
}

/*
Base State class
*/
class State
{
    protected $_name;
    protected $_id;

    public function __construct($name, $id) {
        $this->_name = $name;
        $this->_id = $id;
    }

    // Returns the state's printable name.
    public function getName() {
        return $this->_name;
    }

    // Returns the state's unique identifier.
    public function getId() {
        return $this->_id;
    }
}

/*
The user can derive FSM contexts from this class and interface
to them with the methods of this class.

The finite state machine needs to be initialized to the starting
state of the FSM.  This must be done manually in the constructor
of the derived class.
*/
class FSMContext {

    protected $_state;
    protected $_previous_state;
    protected $_state_stack;
    protected $_transition;
    protected $_debug_flag;

    public function __construct() {
        $this->_state = NULL;
        $this->_previous_state = NULL;
        $this->_state_stack = array();
        $this->_transition = NULL;
        $this->_debug_flag = FALSE;
        // using STDERR works only on cli, but explicitly opening
        // stderr writes to /var/log/apache2/error_log
        $this->_debug_stream = fopen("php://stderr","w");
    }

    // Returns the debug flag's current setting.
    public function getDebugFlag() {
        return $this->_debug_flag;
    }

    // Sets the debug flag.
    // A true value means debugging is on and false means off.
    public function setDebugFlag($flag) {
        $this->_debug_flag = $flag;
    }

    // Returns the stream to which debug output is written.
    public function getDebugStream() {
        return $this->_debug_stream;
    }

    // Sets the debug output stream.
    public function setDebugStream($stream) {
        $this->_debug_stream = $stream;
    }

    // Is this state machine already inside a transition?
    // True if state is undefined.
    public function isInTransition() {
        if ($this->_state = NULL)
            return TRUE;
        else
            return FALSE;
    }

    // Returns the current transition's name.
    // Used only for debugging purposes.
    public function getTransition() {
        return $this->_transition;
    }

    // Clears the current state.
    public function clearState() {
        $this->_previous_state = $this->_state;
        $this->_state = NULL;
    }

    // Returns the state which a transition left.
    // May be Null
    public function getPreviousState() {
        return $this->_previous_state;
     }

    // Sets the current state to the specified state.
    public function setState($state) {
        if (! $state instanceof State)
            throw new Exception('$state should be of class State');
        $this->_state = $state;
        if ($this->_debug_flag)
            fwrite($this->_debug_stream, "NEW STATE    : {$this->_state->getName()}\n");
    }

    // Returns True if the state stack is empty and False otherwise.
    public function isStateStackEmpty() {
        return count($this->_state_stack) == 0;
    }

    // Returns the state stack's depth.
    public function getStateStackDepth() {
        return count($this->_state_stack);
    }

    // Push the current state on top of the state stack
    // and make the specified state the current state.
    public function pushState($state) {
        if (! $state instanceof State)
            throw new Exception('$state should be of class State');
        if ($this->_state != NULL)
            array_push($this->_state_stack, $this->_state);
        $this->_state = $state;
        if ($this->_debug_flag)
            fwrite($this->_debug_stream, "PUSH TO STATE: {$this->_state->getName()}\n");
    }

    // Make the state on top of the state stack the current state.
    public function popState() {
        if (count($this->_state_stack) == 0) {
            if ($this->_debug_flag)
                fwrite($this->_debug_stream, "POPPING ON EMPTY STATE STACK.\n");
            throw new Exception('empty state stack');
        } else {
            $this->_state = array_pop($this->_state_stack);
            if ($this->_debug_flag)
                fwrite($this->_debug_stream, "POP TO STATE : {$this->_state->getName()}\n");
        }
    }

    // Remove all states from the state stack.
    public function emptyStateStack() {
        $this->_state_stack = array();
    }
}

?>