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
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.5  2002/05/07 00:10:20  cwrapp
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
// Revision 1.2  2001/12/14 20:10:37  cwrapp
// Changes in release 1.1.0:
// Add the following features:
// + 486786: Added the %package keyword which specifies the
//           Java package/C++ namespace/Tcl namespace
//           the SMC-generated classes will be placed.
// + 486471: The %class keyword accepts fully qualified
//           class names.
// + 491135: Add FSMContext methods getDebugStream and
//           setDebugStream.
// + 492165: Added -sync command line option which causes
//           the transition methods to be synchronized
//           (this option may only be used with -java).
//
// Revision 1.1  2001/12/03 14:14:03  cwrapp
// Changes in release 1.0.2:
// + Placed the class files in Smc.jar in the net.sf.smc package.
// + Moved Java source files from smc/bin to net/sf/smc.
// + Corrected a C++ generation bug wherein arguments were written
//   to the .h file rather than the .cpp file.
//
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:13:59  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.2  2000/09/01 15:32:07  charlesr
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
// Revision 1.1.1.1  2000/08/02 12:50:56  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class SmcGuard
{
// Member Methods

    public SmcGuard(SmcAction condition, int line_number)
    {
        _condition = condition;
        _line_number = line_number;
        _end_state = "";
        _push_state = "";
        _actions = (List) new LinkedList();
        _pop_args = (List) new LinkedList();
    }

    public SmcAction getCondition()
    {
        return(_condition);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public int getTransType()
    {
        return(_trans_type);
    }

    public void setTransType(int trans_type)
    {
        _trans_type = trans_type;
        return;
    }

    public String getEndState()
    {
        return(_end_state);
    }

    public void setEndState(String end_state)
    {
        _end_state = end_state;
        return;
    }

    public void appendEndState(String token)
    {
        _end_state += token;
        return;
    }

    public String getPushState()
    {
        return (_push_state);
    }

    public void setPushState(String state)
    {
        _push_state = state;
        return;
    }

    public void appendPushState(String token)
    {
        _push_state += token;
        return;
    }

    public void setPopArgs(List argList)
    {
        _pop_args = (List) ((LinkedList) argList).clone();
        return;
    }

    public void addAction(SmcAction action)
    {
        _actions.add(action);
        return;
    }

    // Return true if this transition is a loopback.
    public boolean isLoopback(String stateName)
    {
        return ((_trans_type == Smc.TRANS_SET ||
                 _trans_type == Smc.TRANS_PUSH) &&
                (_end_state.compareTo("nil") == 0 ||
                 _end_state.compareTo(stateName) == 0));
    }

    // Return the number of actions.
    public int getActionCount()
    {
        return (_actions.size());
    }

    public String toString()
    {
        String retval;
        String separator;
        ListIterator action_it;
        SmcAction action;

        if (_condition == null)
        {
            retval = "";
        }
        else
        {
            retval = "[" + _condition + "]";
        }

        switch(_trans_type)
        {
            case Smc.TRANS_NOT_SET:
                retval += " not set";
                break;

            case Smc.TRANS_SET:
            case Smc.TRANS_PUSH:
                retval += " set";
                break;

            case Smc.TRANS_POP:
                retval += " pop";
                break;
        }

        retval += " " + _end_state;

        if (_trans_type == Smc.TRANS_PUSH)
        {
            retval += "/";
            retval += " push(";
            retval += _push_state;
            retval += ")";
        }

        retval += " {";
        for (action_it = _actions.listIterator(),
                 separator = "";
             action_it.hasNext() == true;
             separator = " ")
        {
            action = (SmcAction) action_it.next();
            retval += separator + action;
        }
        retval += "}";

        return(retval);
    }

    public abstract void generateCode(PrintStream source,
                                      int guardIndex,
                                      int guardCount,
                                      String context,
                                      String pkg,
                                      String mapName,
                                      String stateName,
                                      String indent)
        throws ParseException;

// Member Data

    protected SmcAction _condition;
    protected int _line_number;
    protected int _trans_type;
    protected String _end_state;
    protected String _push_state;
    protected List _pop_args;
    protected List _actions;
}
