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
// Port to Groovy by Francois Perrad, francois.perrad@gadz.org
// Copyright 2007, Francois Perrad.
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

class State implements Serializable {
    String name
    int id

    def String toString () {
        return name
    }
}

class StateUndefinedException extends RuntimeException {
}

class TransitionUndefinedException extends RuntimeException {
}

class FSMContext implements Serializable {
    private _state = null
    private _stateStack = []
    def previousState = null
    String transition = ''
    boolean debugFlag = false
    def debugStream = System.err

    // Is this state machine in a transition? If state is null,
    // then true; otherwise, false.
    boolean isInTransition () {
        return (_state == null) ? true : false
    }

    def setState (state) {
        if (! (state instanceof State))
            throw new IllegalArgumentException('state should be a statemap.State')
        if (debugFlag)
            debugStream.println('NEW STATE    : ' + state.name)
        _state = state
    }

    def getState () {
        if (_state == null)
            throw new StateUndefinedException()
        return _state
    }

    def clearState () {
        previousState = _state
        _state = null
    }

    def pushState (state) {
        if (! (state instanceof State))
            throw new IllegalArgumentException('state should be a statemap.State')
        if (_state == null)
            throw new NullPointerException('uninitialized state')
        if (debugFlag)
            debugStream.println('PUSH TO STATE: ' + state.name)
        _stateStack << _state   // push
        _state = state
    }

    def popState () {
        if (!_stateStack) {
            if (debugFlag)
                debugStream.println('POPPING ON EMPTY STATE STACK.')
            throw new EmptyStackException('empty state stack')
        }
        else {
            _state = _stateStack.pop()
            if (debugFlag)
                debugStream.println('POP TO STATE : ' + _state.name)
        }
    }

    def emptyStateStack () {
        _stateStack = []
    }

}

//
// CHANGE LOG
// $Log$
// Revision 1.2  2008/01/14 19:59:23  cwrapp
// Release 5.0.2 check-in.
//
// Revision 1.1  2007/07/16 06:29:37  fperrad
// + Added Groovy.
//
//
