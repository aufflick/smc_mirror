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
// Copyright (C) 2000 - 2005. Charles W. Rapp.
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

import java.io.PrintStream;

public final class SmcParameter
    extends SmcElement
    implements Comparable
{
//---------------------------------------------------------------
// Member methods
//

    public SmcParameter(String name, int lineNumber)
    {
        super (name, lineNumber);

        // While Tcl is weakly typed, it still differentiates
        // between call-by-value and call-by-name. By default,
        // SMC generates call-by-value.
        if (Smc._targetLanguage == Smc.TCL)
        {
            _type = "value";
        }
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcParameter parameter = (SmcParameter) obj;

            retval = _name.equals(parameter.getName()) == true &&
                     _type.equals(parameter.getType()) == true;
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return (retval);
    }

    public int compareTo(Object obj)
        throws ClassCastException
    {
        SmcParameter param = (SmcParameter) obj;
        int retval;

        if ((retval = _name.compareTo(param.getName())) == 0)
        {
            retval = _type.compareTo(param.getType());
        }

        return (retval);
    }

    public String getType()
    {
        return (_type);
    }

    public void setType(String typeName)
    {
        // Trim away whitespace since the type was
        // read in verbatim.
        _type = typeName.trim();

        return;
    }

    public String toString()
    {
        return(_name + ": " + _type);
    }

    //-----------------------------------------------------------
    // SmcElement Abstract Methods.
    //

    public void accept(SmcVisitor visitor)
    {
        visitor.visit(this);
        return;
    }

    //
    // end of SmcElement Abstract Methods.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member data
//

    // A parameter has a name, a type and the line number where it
    // appears in the .sm file.
    private String _type;

    //-----------------------------------------------------------
    // Constants.
    //
    public static final String TCL_VALUE_TYPE = "value";
    public static final String TCL_REFERENCE_TYPE = "reference";
}

//
// CHANGE LOG
// $Log$
// Revision 1.5  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.4  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.5  2005/02/21 15:37:35  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.4  2005/02/03 16:48:51  charlesr
// In implementing the Visitor pattern, the generateCode()
// methods have been moved to the appropriate Visitor
// subclasses (e.g. SmcJavaGenerator). This class now extends
// SmcElement.
//
// Revision 1.3  2004/10/30 16:06:38  charlesr
// Added Graphviz DOT file generation.
//
// Revision 1.2  2004/09/06 16:40:41  charlesr
// Added C# support.
//
// Revision 1.1  2004/05/31 13:55:36  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:04:32  charlesr
// Initial revision
//
