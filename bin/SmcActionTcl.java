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
// The Original Code is State Map Compiler (SMC).
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
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:13:59  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:06  charlesr
// Changes for v. 1.0, Beta 2:
//
// + Removed order dependency on "%start", "%class" and "%header"
//   appearance. These three tokens may now appear in any order but
//   still must appear before the first map definition.
//
// + Modified SMC parser so that it will continue after finding an
//   error. Also improved the error message quality.
//
// + Made error messages so emacs is able to parse them.
//
// Revision 1.1.1.1  2000/08/02 12:50:55  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import java.io.PrintStream;
import java.util.ListIterator;

public final class SmcActionTcl
    extends SmcAction
{
    public SmcActionTcl(String name,
                        boolean negation_flag,
                        int line_number)
    {
        super(name, negation_flag, line_number);
    }

    public void generateCode(PrintStream source,
                             String context,
                             String indent)
    {
        ListIterator argueIt;
        String argument;

        if (_default == true)
        {
            source.print(indent + _negation + _name);
        }
        else
        {
            // Need to distinguish between FSMContext actions and
            // application class actions. If the action is
            // "emptyStateStack", then pass it to the context.
            // Otherwise, let the application class handle it.
            if (_name.compareTo("emptyStateStack") == 0)
            {
                source.print(indent +
                             _negation +
                             "$context " +
                             _name);
            }
            else
            {
                source.print(indent +
                             _negation +
                             "[$context getOwner] " +
                             _name);
            }
        }

        for (argueIt = _arguments.listIterator();
             argueIt.hasNext() == true;
            )
        {
            argument = (String) argueIt.next();

            // If this is not the first argument, then put ", "
            // before the argument.
            source.print(" " + argument);
        }

        return;
    }
}
