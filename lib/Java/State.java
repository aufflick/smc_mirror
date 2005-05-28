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
// Copyright (C) 2000 - 2003 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// statemap.java --
//
//  This package defines the fsmContext class which must be inherited by
//  any Java class wanting to use an smc-generated state machine.
//
// RCS ID
// $Id$
//
// Change Log
// $Log$
// Revision 1.5  2005/05/28 18:44:13  cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
//
// Revision 1.0  2003/12/14 20:39:41  charlesr
// Initial revision
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

    protected State(String name, int id)
    {
        _name = name;
        _id = id;
    }

    public String getName()
    {
        return (_name);
    }

    public int getId()
    {
        return (_id);
    }

    public String toString()
    {
        return (_name);
    }

// Member data

    private String _name;
    private int _id;
}
