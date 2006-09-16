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
// SmcAction --
//
//  Stores a state machine action. May be associated with a
//  transition, a state's entry or exit.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SmcAction
    extends SmcElement
{
//---------------------------------------------------------------
// Member Methods
//

    public SmcAction(String name,
                     int lineNumber)
    {
        super (name, lineNumber);

        _propertyFlag = false;
        _default = false;
    }

    public boolean isProperty()
    {
        return (_propertyFlag);
    }

    public void setProperty(boolean flag)
    {
        _propertyFlag = flag;
        return;
    }

    public boolean getDefault()
    {
        return (_default);
    }

    public void setDefault(boolean flag)
    {
        _default = flag;
        return;
    }

    public List getArguments()
    {
        return (_arguments);
    }

    public void setArguments(List args)
    {
        _arguments = (List) new ArrayList(args);
        return;
    }

    public int compareTo(SmcAction action)
    {
        int retval;

        if ((retval = _name.compareTo(action.getName())) == 0)
        {
            Iterator ait1;
            Iterator ait2;
            String s1;
            String s2;

            for (ait1 = _arguments.iterator(),
                     ait2 = action._arguments.iterator();   
                 ait1.hasNext() == true && retval == 0;
                )
            {
                s1 = (String) ait1.next();
                s2 = (String) ait2.next();

                retval = s1.compareTo(s2);
            }
        }

        return (retval);
    }

    public String toString()
    {
        Iterator ait;
        String sep;
        StringBuffer retval = new StringBuffer(40);

        retval.append(_name);
        retval.append('(');

        for (ait = _arguments.iterator(), sep = "";
             ait.hasNext() == true;
             sep = ", ")
        {
            retval.append(sep);
            retval.append((String) ait.next());
        }

        retval.append(')');

        return (retval.toString());
    }

    //-----------------------------------------------------------
    // SmcElement Abstract Methods.
    //

    public void accept(SmcVisitor visitor)
    {
        visitor.visit(this);
    }

    //
    // end of SmcElement Abstract Methods.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member Data
//

    // The action's argument list.
    private List _arguments;

    // Is this action a .Net property assignment?
    private boolean _propertyFlag;

    // Is this action for a default transition or not?
    private boolean _default;
}

//
// CHANGE LOG
// $Log$
// Revision 1.8  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.7  2005/11/07 19:34:54  cwrapp
// Changes in release 4.3.0:
// New features:
//
// + Added -reflect option for Java, C#, VB.Net and Tcl code
//   generation. When used, allows applications to query a state
//   about its supported transitions. Returns a list of transition
//   names. This feature is useful to GUI developers who want to
//   enable/disable features based on the current state. See
//   Programmer's Manual section 11: On Reflection for more
//   information.
//
// + Updated LICENSE.txt with a missing final paragraph which allows
//   MPL 1.1 covered code to work with the GNU GPL.
//
// + Added a Maven plug-in and an ant task to a new tools directory.
//   Added Eiten Suez's SMC tutorial (in PDF) to a new docs
//   directory.
//
// Fixed the following bugs:
//
// + (GraphViz) DOT file generation did not properly escape
//   double quotes appearing in transition guards. This has been
//   corrected.
//
// + A note: the SMC FAQ incorrectly stated that C/C++ generated
//   code is thread safe. This is wrong. C/C++ generated is
//   certainly *not* thread safe. Multi-threaded C/C++ applications
//   are required to synchronize access to the FSM to allow for
//   correct performance.
//
// + (Java) The generated getState() method is now public.
//
// Revision 1.6  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.5  2005/02/21 15:34:32  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.4  2005/02/03 16:43:05  charlesr
// In implementing the Visitor pattern, the generateCode()
// methods have been moved to the appropriate Visitor
// subclasses (e.g. SmcJavaGenerator).
//
// Revision 1.3  2004/10/30 16:04:01  charlesr
// Added Graphviz DOT file generation.
//
// Revision 1.2  2004/09/06 16:39:31  charlesr
// Added C# support.
//
// Revision 1.1  2004/05/31 13:53:34  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:03:05  charlesr
// Initial revision
//
