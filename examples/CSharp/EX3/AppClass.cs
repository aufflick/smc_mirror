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
// Copyright (C) 2004. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//
// Class
//    AppClass
//
// Description
//    When a state machine executes an action, it is really calling a
//  member function in the context class.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2005/05/28 13:05:17  cwrapp
// Added CSharp examples 1 - 3.
//
// Revision 1.0  2004/09/01 17:34:50  charlesr
// Initial revision
//

using System;

public class AppClass
{
// Member methods.

    public AppClass()
    {
        _fsm = new AppClassContext(this);
        _is_acceptable = false;

        // Uncomment to see debug output.
        // _fsm.setDebugFlag(true);
    }

    public bool CheckString(string str)
    {
        int i,
            Length;
      

        for (i = 0, Length = str.Length;
             i < Length;
             ++i)
        {
            switch (str[i])
            {
                case '0':
                    _fsm.Zero();
                    break;

                case '1':
                    _fsm.One();
                    break;

                case 'c':
                case 'C':
                    _fsm.C();
                    break;

                default:
                    _fsm.Unknown();
                    break;
            }
        }

        _fsm.EOS();

        return(_is_acceptable);
    }

    public void Acceptable()
    {
        _is_acceptable = true;
    }

    public void Unacceptable()
    {
        _is_acceptable = false;
    }

// Member data.

    private AppClassContext _fsm;
    private bool _is_acceptable;
}
