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
// SmcMethodArgTcl --
//
//  Generates a Tcl method call.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2002/05/07 00:10:20  cwrapp
// Changes in release 1.3.2:
// Add the following feature:
// + 528321: Modified push transition syntax to be:
//
// 	  <transname> <state1>/push(<state2>)  {<actions>}
//
// 	  which means "transition to <state1> and then
// 	  immediately push to <state2>". The current
// 	  syntax:
//
// 	  <transname> push(<state2>)  {<actions>}
//
//           is still valid and <state1> is assumed to be "nil".
//
// No bug fixes.
//

package net.sf.smc;

import java.io.PrintStream;
import java.util.List;
import java.util.ListIterator;

public final class SmcMethodArgTcl
    extends SmcMethodArg
{
    public SmcMethodArgTcl(String name, List argList, int line_number)
    {
        super (name, argList, line_number);
    }

    public void generateCode(PrintStream source)
    {
        int index;
        ListIterator ait;

        source.print('[');

        // If the "name" is of the form "var.method", then separate
        // the var and the method and output "$var method".
        if ((index = _name.indexOf(".")) < 0)
        {
            // This name contains just a routine.
            source.print(_name);
        }
        else
        {
            String variable = _name.substring(0, index);
            String method = _name.substring((index + 1), _name.length());

            source.print("$");
            source.print(variable);
            source.print(" ");
            source.print(method);
        }

        for (ait = _argList.listIterator(); ait.hasNext() == true;)
        {
            source.print(' ');
            ((SmcArgument) ait.next()).generateCode(source);
        }

        source.print(']');

        return;
    }
    

// Member data.
}
