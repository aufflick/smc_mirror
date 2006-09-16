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
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2005. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc;

/**
 * The super class for all abstract syntax tree visitors.
 * Visitors perform either global syntax checking on the tree
 * or generate code based on the tree.
 * See the Visitor pattern in GoF (p. 331).
 * @see SmcElement
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public abstract class SmcVisitor
{
//---------------------------------------------------------------
// Member methods
//

    // Explicitly visit all the concrete abstract syntax tree
    // concrete elements. Not all visitors visit all element
    // types, so these methods are not abstract but have empty
    // bodies. Derived visitor classes should override at their
    // discretion.
    public void visit(SmcFSM fsm)
    {}

    public void visit(SmcMap map)
    {}

    public void visit(SmcState state)
    {}

    public void visit(SmcTransition transition)
    {}

    public void visit(SmcGuard guard)
    {}

    public void visit(SmcAction action)
    {}

    public void visit(SmcParameter parameter)
    {}

    // Default constructor.
    protected SmcVisitor()
    {}

//---------------------------------------------------------------
// Member data
//
}

//
// CHANGE LOG
// $Log$
// Revision 1.2  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.1  2005/05/28 19:28:43  cwrapp
// Moved to visitor pattern.
//
// Revision 1.1  2005/02/21 15:38:58  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.0  2005/02/03 17:09:19  charlesr
// Initial revision
//
