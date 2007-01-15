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
//   Chris Liscio contributed the Objective-C code generation
//   and examples/ObjC.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc;

/**
 * The super class for all abstract syntax tree elements.
 * Provides the interface for the visitor classes.
 * See the Visitor pattern in GoF (p. 331).
 * @see SmcFSM
 * @see SmcMap
 * @see SmcState
 * @see SmcTransition
 * @see SmcGuard
 * @see SmcAction
 * @see SmcParameter
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public abstract class SmcElement
{
//---------------------------------------------------------------
// Member methods
//

    /**
     * Returns the element's unique name.
     * @return the element's unique name.
     */
    public String getName()
    {
        return (_name);
    }

    /**
     * Returns the element's position in the .sm file.
     * @return the element's position in the .sm file.
     */
    public int getLineNumber()
    {
        return(_lineNumber);
    }

    /**
     * Accepts a new visitor which performs some action upon this
     * abstract syntax tree element.
     * @param visitor a parser visitor.
     */
    public abstract void accept(SmcVisitor visitor);

    /**
     * Default constructor.
     * @param name the elements unique name.
     * @param lineNumber where this element appears in the .sm
     * file.
     */
    protected SmcElement(String name, int lineNumber)
    {
        _name = name;
        _lineNumber = lineNumber;
    }

//---------------------------------------------------------------
// Member data
//

    /**
     * An element has a unique name.
     */
    protected final String _name;

    /**
     * An element starts on a particular line number.
     */
    public final int _lineNumber;
}

//
// CHANGE LOG
// $Log$
// Revision 1.3  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.2  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.1  2005/02/21 15:35:05  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.0  2005/02/03 17:08:39  charlesr
// Initial revision
//
