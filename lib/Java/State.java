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
//  This package defines the fsmContext class which must be inherited by
//  any Java class wanting to use an smc-generated state machine.
//
// Change Log
// $Log$
// Revision 1.3  2002/02/19 19:52:48  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
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

import java.io.Serializable;

// statemap.State --
//
//  The reason why Java has an abstract State class while C++ and
//  Tcl do not is because:
//  1) in C++, FSMContext is a template class and so the
//     app-specific state class name can be used to generate an
//     app-specific fsmContext class,
//  2) Tcl is weakly typed so all it needs is a state name.
//  Since Java does not have templates and is strongly typed,
//  an abstract State class is needed for FSMContext's _state
//  and _state_stack declarations.
//
//  Another reason why I don't have a state class in C++ and Tcl
//  when I could have one is because there is nothing to the class.

public abstract class State
    implements Serializable
{
// Member functions

    protected State(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return (_name);
    }

    public String toString()
    {
        return (_name);
    }

// Member data

    private String _name;
}
