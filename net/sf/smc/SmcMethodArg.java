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
// SmcMethodArg --
//
//  Stores a method argument.
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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class SmcMethodArg
    extends SmcArgument
    implements Comparable
{
// Member methods.

    public SmcMethodArg(String name, List argList, int line_number)
    {
        super (name, line_number);

        _argList = (List) ((LinkedList) argList).clone();
    }

    public int compareTo(Object obj)
    {
        SmcMethodArg arg = (SmcMethodArg) obj;
        int retcode;

        retcode = _name.compareTo(arg._name);
        if (retcode == 0)
        {
            retcode = _argList.size() - arg._argList.size();
            if (retcode == 0)
            {
                ListIterator it1;
                ListIterator it2;
                SmcArgument arg1;
                SmcArgument arg2;

                for (it1 = _argList.listIterator(),
                     it2 = arg._argList.listIterator();
                     it1.hasNext() == true && retcode == 0;
                    )
                {
                    arg1 = (SmcArgument) it1.next();
                    arg2 = (SmcArgument) it2.next();

                    retcode = arg1.compareTo(arg2);
                }
            }
        }

        return (retcode);
    }

    public abstract void generateCode(PrintStream source);

// Member data.

    // Besides a name, a method argument also includes an
    // argument list.
    protected List _argList;
}
