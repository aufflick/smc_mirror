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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SmcState
    extends SmcElement
{
//---------------------------------------------------------------
// Member methods
//

    public SmcState(String name, int lineNumber, SmcMap map)
    {
        super (name, lineNumber);

        _map = map;

        if (name.compareTo("Default") == 0)
        {
            _instanceName = "DefaultState";
        }
        else
        {
            _instanceName = name;
        }

        // Make sure the first character in the class name is
        // upper case.
        String firstLetter = name.substring(0, 1);
        String remainder = name.substring(1);

        _className = firstLetter.toUpperCase() + remainder;

        _entryActions = null;
        _exitActions = null;
        _transitions = (List) new ArrayList();
    }

    public SmcMap getMap()
    {
        return (_map);
    }

    public String getName()
    {
        return(_className + "." + _instanceName);
    }

    public String getClassName()
    {
        return(_className);
    }

    public String getInstanceName()
    {
        return(_instanceName);
    }

    public List getEntryActions()
    {
        return(_entryActions);
    }

    public void setEntryActions(List actions)
    {
        _entryActions = (List) new ArrayList(actions);
        return;
    }

    public List getExitActions()
    {
        return(_exitActions);
    }

    public void setExitActions(List actions)
    {
        _exitActions = (List) new ArrayList(actions);
        return;
    }

    public List getTransitions()
    {
        return(_transitions);
    }

    public SmcTransition findTransition(String name,
                                        List parameters)
    {
        Iterator transIt;
        SmcTransition transition;
        Iterator pit;
        SmcParameter parameter;
        SmcTransition retval;

        for (transIt = _transitions.iterator(),
                     retval = null;
             transIt.hasNext() == true && retval == null;
            )
        {
            transition = (SmcTransition) transIt.next();
            if (name.equals(transition.getName()) == true &&
                transition.compareTo(name, parameters) == 0)
            {
                retval = transition;
            }
        }

        return (retval);
    }

    public void addTransition(SmcTransition transition)
    {
        // Add the transition only if it is not already in the
        // list.
        if (_transitions.contains(transition) == false)
        {
            _transitions.add(transition);
        }

        return;
    }

    public String toString()
    {
        Iterator it;
        StringBuffer retval = new StringBuffer(512);

        retval.append(_instanceName);

        if (_entryActions != null && _entryActions.size() > 0)
        {
            retval.append("\n\tEntry {");
            for (it = _entryActions.iterator();
                 it.hasNext() == true;
                )
            {
                retval.append((SmcAction) it.next());
                retval.append('\n');
            }
            retval.append("}");
        }

        if (_exitActions != null && _exitActions.size() > 0)
        {
            retval.append("\n\tExit {");
            for (it = _exitActions.iterator();
                 it.hasNext() == true;
                )
            {
                retval.append((SmcAction) it.next());
                retval.append('\n');
            }
            retval.append("}");
        }

        for (it = _transitions.iterator();
             it.hasNext() == true;
            )
        {
            retval.append("\n");
            retval.append((SmcTransition) it.next());
        }

        return (retval.toString());
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

    private SmcMap _map;
    private String _className;
    private String _instanceName;
    private List _entryActions;
    private List _exitActions;
    private List _transitions;
}

//
// CHANGE LOG
// $Log$
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
// Revision 1.6  2005/02/21 15:38:04  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.5  2005/02/03 16:49:43  charlesr
// In implementing the Visitor pattern, the generateCode()
// methods have been moved to the appropriate Visitor
// subclasses (e.g. SmcJavaGenerator). This class now extends
// SmcElement.
//
// Revision 1.4  2004/10/30 16:07:55  charlesr
// Added Graphviz DOT file generation.
//
// Revision 1.3  2004/10/08 18:55:41  charlesr
// Fixed C# exit action generation.
//
// Revision 1.2  2004/09/06 16:41:40  charlesr
// Added C# support.
//
// Revision 1.1  2004/05/31 13:57:04  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:06:45  charlesr
// Initial revision
//
