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

import java.io.PrintStream;
import java.io.StringWriter;

/**
 * Base class for all target language code generators.
 * @see SmcElement
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public abstract class SmcCodeGenerator
    extends SmcVisitor
{
//-----------------------------------------------------------------
// Member methods
//

    // Constructs the target code generator.
    protected SmcCodeGenerator(PrintStream source,
                               String srcfileBase)
    {
        super ();

        _source = source;
        _srcfileBase = srcfileBase;
        _indent = "";
        _guardCount = 0;
        _guardIndex = 0;
    }

    // Scope the state name. If the state is unscoped, then
    // return "<mapName>.<stateName>". If the state named
    // contains the scope string "::", replace that with a ".".
    protected String scopeStateName(String stateName,
                                    String mapName)
    {
        int index;
        StringWriter retval = new StringWriter();

        index = stateName.indexOf("::");
        if (index < 0)
        {
            retval.write(mapName);
            retval.write(".");
            retval.write(stateName);
        }
        else
        {
            retval.write(stateName.substring(0, index));
            retval.write('.');
            retval.write(stateName.substring(index + 2));
        }

        return (retval.toString());
    }

    // Returns true if this transition is a loopback and false
    // otherwise. If this is a loopback, then do not perform the
    // exit and entry actions.
    protected boolean isLoopback(int transType,
                                 String currentState,
                                 String endState)
    {
        return ((transType == Smc.TRANS_SET ||
                 transType == Smc.TRANS_PUSH) &&
                (endState.equals(NIL_STATE) == true ||
                 endState.equals(currentState) == true));
    }

//-----------------------------------------------------------------
// Member data
//

    // Emit the target source code to this file.
    protected PrintStream _source;

    // The .sm file's base name.
    protected final String _srcfileBase;

    // Output this indent before generating a line of code.
    protected String _indent;

    // This information is common between the transition and
    // guard visitor methods.
    protected int _guardCount;
    protected int _guardIndex;

    //-----------------------------------------------------------
    // Constants.
    //
    protected static final String NIL_STATE = "nil";
}

//
// CHANGE LOG
// $Log$
// Revision 1.2  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.2  2005/02/21 15:34:54  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:11:47  charlesr
// Moved isLoopback() method from SmcGuard to this class.
// Added additional parameters.
//
// Revision 1.0  2005/02/03 17:09:49  charlesr
// Initial revision
//
