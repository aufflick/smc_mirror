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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SmcTransition
    extends SmcElement
    implements Comparable
{
//---------------------------------------------------------------
// Member methods
//

    public SmcTransition(String name,
                         List parameters,
                         int lineNumber,
                         SmcState state)
    {
        super (name, lineNumber);

        _state = state;
        _parameters = parameters;
        _guards = (List) new ArrayList();
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcTransition trans = (SmcTransition) obj;

            retval =
                (_name.equals(trans.getName()) == true &&
                 _compareParams(trans.getParameters()) == 0);
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return(retval);
    }

    public int compareTo(Object o)
        throws ClassCastException
    {
        SmcTransition trans = (SmcTransition) o;
        int retval;

        if ((retval = _name.compareTo(trans.getName())) == 0)
        {
            retval = _compareParams(trans.getParameters());
        }

        return(retval);
    }

    public int compareTo(String name, List parameters)
    {
        int retval;

        if ((retval = _name.compareTo(name)) == 0)
        {
            retval = _compareParams(parameters);
        }

        return (retval);
    }

    public SmcState getState()
    {
        return (_state);
    }

    public List getParameters()
    {
        return(_parameters);
    }

    public void addGuard(SmcGuard guard)
    {
        _guards.add(guard);
        return;
    }

    public List getGuards()
    {
        return(_guards);
    }

    // Determine if this transition references the ctxt local
    // variable.
    public boolean hasCtxtReference()
    {
        Iterator guardIt;
        boolean retcode;

        // Stop as soon as we know that ctxt is referenced.
        for (guardIt = _guards.iterator(), retcode = false;
             guardIt.hasNext() == true && retcode == false;
            )
        {
            retcode =
                ((SmcGuard) guardIt.next()).hasCtxtReference();
        }

        return (retcode);
    }

    // Determine if this transition references a non-nil
    // end state.
    public boolean hasNonNilEndState()
    {
        Iterator guardIt;
        SmcGuard guard;
        boolean retcode;

        // Stop as soon as we know that a non-nill end state
        // is referenced.
        for (guardIt = _guards.iterator(), retcode = false;
             guardIt.hasNext() == true && retcode == false;
            )
        {
            guard = (SmcGuard) guardIt.next();
            retcode =
                !guard.getEndState().equals(SmcGuard.NIL_STATE);
        }

        return (retcode);
    }

    public String toString()
    {
        StringBuffer retval = new StringBuffer(512);
        String sep;
        Iterator it;

        retval.append(_name);
        retval.append("(");

        for (it = _parameters.iterator(), sep = "";
             it.hasNext() == true;
             sep = ", ")
        {
            retval.append(sep);
            retval.append((SmcParameter) it.next());
        }

        retval.append(")");

        if (_guards.size() > 0)
        {
            SmcGuard guard;

            for (it = _guards.iterator();
                 it.hasNext() == true;
                )
            {
                guard = (SmcGuard) it.next();
                retval.append("\n");
                retval.append(guard);
            }
        }

        return(retval.toString());
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

    // Compare this transition's parameters with the given list.
    private int _compareParams(List params)
    {
        Iterator pit1;
        Iterator pit2;
        SmcParameter param1;
        SmcParameter param2;
        int retval;

        retval = _parameters.size() - params.size();
        if (retval == 0)
        {
            for (pit1 = _parameters.iterator(),
                     pit2 = params.iterator(),
                     retval = 0;
                 pit1.hasNext() == true &&
                     pit2.hasNext() == true &&
                     retval == 0;
                )
            {
                param1 = (SmcParameter) pit1.next();
                param2 = (SmcParameter) pit2.next();
                retval = param1.compareTo(param2);
            }
        }
        

        return (retval);
    }

//---------------------------------------------------------------
// Member data
//

    private SmcState _state;
    private List _parameters;
    private List _guards;
}

//
// CHANGE LOG
// $Log$
// Revision 1.7  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.6  2005/11/07 19:34:54  cwrapp
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
// Revision 1.5  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.6  2005/02/21 15:38:43  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.5  2005/02/03 16:52:21  charlesr
// In implementing the Visitor pattern, the generateCode()
// methods have been moved to the appropriate Visitor
// subclasses (e.g. SmcJavaGenerator). This class now extends
// SmcElement.
//
// Revision 1.4  2004/10/30 16:08:54  charlesr
// Added Graphviz DOT file generation.
//
// Revision 1.3  2004/10/02 19:54:57  charlesr
// Full-qualified all std namespace references with "std::".
//
// Revision 1.2  2004/09/06 16:41:48  charlesr
// Added C# support.
//
// Revision 1.1  2004/05/31 13:57:17  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:07:01  charlesr
// Initial revision
//
