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
// Copyright (C) 2000 - 2005 Charles W. Rapp.
// All Rights Reserved.
//
// Port to Scala by Francois Perrad, francois.perrad@gadz.org
// Copyright 2008, Francois Perrad.
// All Rights Reserved.
//
// Contributor(s):
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package statemap

import scala.collection.mutable.Stack
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.io.PrintStream

class StateUndefinedException() extends RuntimeException() {
}

class TransitionUndefinedException(reason: String) extends RuntimeException(reason) {
}

[ serializable ] // deprecated since 2.4 in favor of Java's convention @serializable
abstract class FSMContext[State] {
    private var _state: State = null
    private var _stateStack: Stack[State] = new Stack[State]
    private var _previousState: State = null
    protected var _transition: String = ""
    private var _debugFlag: Boolean = false
    private var _debugStream: PrintStream = System.err
    private var _listeners: PropertyChangeSupport = new PropertyChangeSupport(this)

    def getDebugFlag(): Boolean = _debugFlag

    def setDebugFlag(flag: Boolean): Unit = {
        _debugFlag = flag
    }

    def getDebugStream(): PrintStream = _debugStream

    def setDebugStream(stream: PrintStream): Unit = {
        _debugStream = stream
    }

    def getTransition(): String = _transition

    // Is this state machine in a transition? If state is null,
    // then true; otherwise, false.
    def isInTransition(): Boolean = (_state == null)

    def setState(state: State): Unit = {
        val previousState = _state
        if (! state.isInstanceOf[State])
            throw new IllegalArgumentException("state should be a statemap.State")
        if (_debugFlag)
            _debugStream.println("NEW STATE    : " + state)
        _state = state
        // Inform all listeners about this state change
        _listeners.firePropertyChange("State", previousState, _state)
    }

    def getState(): State = {
        if (_state == null)
            throw new StateUndefinedException()
        return _state
    }

    def clearState(): Unit = {
        _previousState = _state
        _state = null
    }

    def pushState(state: State): Unit = {
        val previousState = _state
        if (! state.isInstanceOf[State])
            throw new IllegalArgumentException("state should be a statemap.State")
        if (_state == null)
            throw new NullPointerException("uninitialized state")
        if (_debugFlag)
            _debugStream.println("PUSH TO STATE: " + state)
        _stateStack.push(_state)
        _state = state
        // Inform all listeners about this state change
        _listeners.firePropertyChange("State", previousState, _state)
    }

    def popState(): Unit = {
        if (_stateStack.length == 0) {
            if (_debugFlag)
                _debugStream.println("POPPING ON EMPTY STATE STACK.")
            throw new NoSuchElementException("empty state stack")
        }
        val previousState = _state
        _state = _stateStack.pop
        if (_debugFlag)
            _debugStream.println("POP TO STATE : " + _state)
        // Inform all listeners about this state change
        _listeners.firePropertyChange("State", previousState, _state)
    }

    def emptyStateStack(): Unit = {
        _stateStack = new Stack[State]
    }

    def addStateChangeListener(listener: PropertyChangeListener): Unit = {
        _listeners.addPropertyChangeListener("State", listener)
    }

    def removeStateChangeListener(listener: PropertyChangeListener): Unit = {
        _listeners.removePropertyChangeListener("State", listener)
    }
}

//
// CHANGE LOG
// $Log$
// Revision 1.1  2008/02/04 10:48:47  fperrad
// + Added Scala library
//
//
