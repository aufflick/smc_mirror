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
// Revision 1.2  2001/04/06 19:52:32  cwrapp
// Checking in release 1.0, beta 5: Fixed bug 412265 (see http://sourceforge.net/projects/smc).
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
// Revision 1.2  2000/09/01 15:32:09  charlesr
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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ListIterator;

public final class SmcGuardTcl
    extends SmcGuard
{
    public SmcGuardTcl(SmcAction condition, int line_number)
    {
        super(condition, line_number);
    }

    public void generateCode(PrintStream source,
                             int guardIndex,
                             int guardCount,
                             String context,
                             String mapName,
                             String stateName,
                             String indent)
        throws ParseException
    {
        ListIterator actionIt;
        SmcAction action;
        String indent2 = null;
        String endStateName = "";

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the
        // state name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state is actually a transition name.
        if (_trans_type != Smc.TRANS_POP &&
            _end_state.length () > 0 &&
            _end_state.compareTo("nil") != 0 &&
            _end_state.indexOf("::") < 0)
        {
            _end_state = mapName + "::" + _end_state;
        }

        if (stateName.indexOf("::") < 0)
        {
            stateName = mapName + "::" + stateName;
        }

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (guardCount > 1)
        {
            // Ok, there are multiple guard statements. Now is
            // this the first guard among many?
            if (guardIndex == 0 && _condition != null)
            {
                // Yes, this is the first. This means an "if"
                // should be used for this condition.
                source.print("        if {[");
                _condition.generateCode(source, context, "");
                source.println("]} {");
            }
            else if (_condition != null)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if" for
                // the condition.
                source.print(" elseif {[");
                _condition.generateCode(source, context, "");
                source.println("]} {");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                source.println(" else {");
            }

            indent2 = "            ";
        }
        else
        {
            // There is only one guard. Does this guard have a
            // condition.
            if (_condition == null)
            {
                // Actually, this is a plain, old, vaniila
                // transition.
                indent2 = "        ";
            }
            else
            {
                indent2 = "            ";

                // Yes, there is a condition.
                source.print("        if (");
                _condition.generateCode(source, context, "");
                source.println(") {");
            }
        }

        // Perform the current state's exit action.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (guardIndex == 0 &&
            ((_trans_type == Smc.TRANS_SET &&
              _end_state.compareTo("nil") != 0 &&
              _end_state.compareTo(stateName) != 0) ||
             _trans_type == Smc.TRANS_POP))
        {
            source.println("        [$context getState] Exit $context;");
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transitions actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (_actions.size() == 0 && _end_state.length() != 0)
        {
            endStateName = "${" + _end_state + "}";
        }
        else if (_actions.size() > 0)
        {
            // Save away the current state if this is a loopback
            // transition. Storing current state allows the
            // current to be cleared before any actions are
            // executed. Remember: actions are not allowed to
            // issue transitions and clearing the current state
            // prevents them from doing so.
            if (_trans_type == Smc.TRANS_SET &&
                (_end_state.compareTo("nil") == 0 ||
                 _end_state.compareTo(stateName) == 0))
            {
                endStateName = "${EndStateName}";
                source.println(indent2 +
                               "set EndStateName [$context getState];");
            }
            else if (_trans_type == Smc.TRANS_PUSH)
            {
                // If this is a push transition, then remember
                // the current state as well. This will have to
                // be reset before the push is done otherwise the
                // push will not work because it won't know what
                // state to put on the stack.
                if (_end_state.compareTo("nil") == 0)
                {
                    endStateName = _end_state;
                }
                else
                {
                    endStateName = "${" + _end_state + "}";
                }
                source.println(indent2 +
                               "set CurrentState [$context getState];");
            }
            else
            {
                endStateName = "${" + _end_state + "}";
            }

            // Now that we are in the transition, clear the
            // current state since we are no longer in a state.
            source.println(indent2 + "$context clearState;");
        }

        // Dump out this transition's actions.
        for (actionIt = _actions.listIterator();
             actionIt.hasNext() == true;
            )
        {
            action = (SmcAction) actionIt.next();
            action.generateCode(source, context, indent2);
            source.println(";");
        }

        // Print the setState() call, if necessary. Do NOT
        // generate the set state if:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        if (_trans_type == Smc.TRANS_SET &&
            (_actions.size() > 0 ||
             (_end_state.compareTo("nil") != 0 &&
              _end_state.compareTo(stateName) != 0)))
        {
            source.println(indent2 +
                           "$context setState " +
                           endStateName +
                           ";");
        }
        else if (_trans_type == Smc.TRANS_PUSH)
        {
            // Reset the current state so that it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (_actions.size() > 0)
            {
                source.println(indent2 +
                               "$context setState $CurrentState;");
            }

            // If pushing to the "nil" state, then use the
            // current state.
            if (endStateName.compareTo("nil") == 0)
            {
                source.println(indent2 +
                               "$context pushState $CurrentState;");
            }
            else
            {
                source.println(indent2 +
                               "$context pushState " +
                               endStateName +
                               ";");
            }
        }
        else if (_trans_type == Smc.TRANS_POP)
        {
            source.println(indent2 + "$context popState;");
        }

        // Perform the new state's entry actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((_trans_type == Smc.TRANS_SET &&
              _end_state.compareTo("nil") != 0 &&
              _end_state.compareTo(stateName) != 0) ||
             _trans_type == Smc.TRANS_PUSH)
        {
            source.println(indent2 +
                           "[$context getState] Entry $context;");
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (_trans_type == Smc.TRANS_POP &&
            _end_state.compareTo("nil") != 0)
        {
            source.println(indent2 +
                           "[$context getState] " +
                           _end_state +
                           " $context;");
        }

        // If this is a guarded transition, it will be necessary
        // to close off the if body. DON'T PRINT A NEW LINE. Why?
        // Because an else or elseif may follow and we won't know
        // until we go back to the transition source generator
        // whether all clauses have been done.
        if (guardCount > 1)
        {
            source.print("        }");
        }

        return;
    }
}
