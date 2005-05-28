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
// Revision 1.3  2001/06/16 19:52:43  cwrapp
// Changes in release 1.0, beta 7:
// Fixes the minor code generation bugs and introduces a new
// example Java program (found at examples/Java/EX7). This
// example program is also a Java applet and can be seen at
// http://smc.sourceforge.net/SmcDemo.htm.
//
// Revision 1.2  2001/05/09 23:40:01  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
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
// Revision 1.2  2000/09/01 15:32:17  charlesr
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
// Revision 1.1  2000/08/23 21:25:40  charlesr
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:50:56  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public final class SmcParser
{
// Member Methods

    public SmcParser(SmcLexer lexer, int language)
    {
        _lexer = lexer;
        _language = language;
        _parser_map = new SmcParserContext(this);

        _start_state = "";
        _className = "";
        _context = "";
        _header = "";

        _action_negation = false;
        
    }

    public SmcParseTree parse()
        throws IOException,
               IllegalAccessException,
               InvocationTargetException
    {
        SmcLexer.Token token = null;
        int token_type;
        Object[] params = new Object[1];

        _name_in_progress = "";
        _map_in_progress = null;
        _state_in_progress = null;
        _transition_name = null;
        _transition_parameters = null;
        _parameter_in_progress = null;
        _transition_in_progress = null;
        _guard_in_progress = null;
        _action_in_progress = null;
        _variable_in_progress = null;

        _argList = null;
        _argsStack = new Stack();

        // DEBUG
        // _parser_map.setDebugFlag(true);

        _parse_status = true;
        _quit_flag = false;

        // Read all the tokens into a list.
        _tokenList = (List) new LinkedList();
        token_type = SmcLexer.TOKEN_NOT_SET;
        while (_quit_flag == false &&
               (token = _lexer.nextToken()) != null &&
               (token_type = token.getType()) != SmcLexer.DONE_SUCCESS &&
               token_type != SmcLexer.DONE_FAILED &&
               token_type != SmcLexer.TOKEN_NOT_SET)
        {
            // Is the token type valid?
            if (token_type > SmcLexer.TOKEN_NOT_SET &&
                token_type < SmcLexer.TOKEN_COUNT)
            {
                // Yes. Add it to the list.
                _tokenList.add(token.copy());
            }
            else
            {
                error("Undefined token type (" +
                      Integer.toString(token_type) +
                      ")",
                      true);

                _quit_flag = true;
                _parse_status = false;
            }
        }

        // If the last token as a failure, don't go on.
        if (token_type == SmcLexer.DONE_FAILED ||
            token_type == SmcLexer.TOKEN_NOT_SET)
        {
            error(token.getValue(), true);
        }
        else
        {
            _parse_tree = createTargetTree();

            // If the token collection was successful,
            // start parsing.
            while (_quit_flag == false &&
                   _tokenList.isEmpty() == false)
            {
                token = (SmcLexer.Token) _tokenList.remove(0);
                token_type = token.getType();

                params[0] = token;
                _TransMethod[token_type].invoke(_parser_map,
                                                params);
            }

            if (_parse_status == false)
            {
                _parse_tree = null;
            }
        }

        return (_parse_tree);
    }

    //----------------------------------------
    // State Map Actions
    //

    /* package */ void warning(String error_msg)
    {
        System.err.println(Smc.getSourceFileName() +
                           ":" +
                           Integer.toString(_lexer.getLineNumber()) +
                           ": warning - " +
                           error_msg);
        return;
    }

    /* package */ void error(String error_msg, boolean quitFlag)
    {
        System.err.println(Smc.getSourceFileName() +
                           ":" +
                           Integer.toString(_lexer.getLineNumber()) +
                           ": error - " +
                           error_msg);
        _parse_status = false;
        if (_quit_flag == true)
        {
            _quit_flag = quitFlag;
        }

        return;
    }

    /* package */ boolean isStartStateSet()
    {
        return (_parse_tree.getStartState().length() == 0 ? false : true);
    }

    /* package */ boolean isClassSet()
    {
        return (_parse_tree.getContext().length() == 0 ? false : true);
    }

    /* package */ boolean isHeaderSet()
    {
        return (_parse_tree.getHeader().length() == 0 ? false : true);
    }

    /* package */ boolean isPackageSet()
    {
        String pkg = _parse_tree.getPackage();

        return (pkg != null && pkg.length() > 0);
    }

    /* package */ boolean isDuplicateMap(SmcLexer.Token token)
    {
        return (_parse_tree.findMap(token.getValue()) == null ? false : true);
    }

    /* package */ boolean isDuplicateState(SmcLexer.Token token)
    {
        return (_map_in_progress.findState(token.getValue()));
    }

    /* package */ boolean isEntrySet()
    {
        return (_state_in_progress.getEntryActions().size() == 0 ? false : true);
    }

    /* package */ boolean isExitSet()
    {
        return (_state_in_progress.getExitActions().size() == 0 ? false : true);
    }

    /* package */ boolean isDuplicateGuard()
    {
        ListIterator guardIt;
        SmcGuard guard;
        SmcAction condition;
        boolean retval;

        if (_transition_in_progress == null)
        {
            retval = false;
        }
        else
        {
            for (guardIt = _transition_in_progress.getGuards().listIterator(),
                     retval = false;
                 guardIt.hasNext() == true && retval == false;
                )
            {
                guard = (SmcGuard) guardIt.next();
                condition = guard.getCondition();

                // If the condition and the current action are
                // equal, then the guards are equal.
                if (condition == null && _action_in_progress == null ||
                    (condition != null &&
                     _action_in_progress != null &&
                     condition.compareTo(_action_in_progress) == 0))
                {
                    retval = true;
                }
            }
        }

        return (retval);
    }

    /* package */ boolean isCpp()
    {
        return (_language == Smc.C_PLUS_PLUS);
    }

    /* package */ boolean isJava()
    {
        return (_language == Smc.JAVA);
    }

    /* package */ boolean isTcl()
    {
        return (_language == Smc.TCL);
    }

    /* package */ boolean isTransParametersEmpty()
    {
        return ((_transition_parameters == null ||
                 _transition_parameters.size() == 0)
                ? true : false);
    }

    // Push a token back on the front of the token list.
    /* package */ void pushBack(SmcLexer.Token token)
    {
        _tokenList.add(0, token);
        return;
    }

    /* package */ void setHeaderLine()
    {
        _parse_tree.setHeaderLine(_lexer.getLineNumber());
        return;
    }

    /* package */ void addSource(SmcLexer.Token token)
    {
        _parse_tree.setSource(token.getValue());
        return;
    }

    /* package */ void appendStartState(SmcLexer.Token token)
    {
        _start_state += token.getValue();
        return;
    }

    /* package */ void setStartState()
    {
        _parse_tree.setStartState(_start_state);
        return;
    }

    // If true, then the class name is for %class.
    // If false, then the class name is for %package.
    /* package */ void setNameType(int type)
    {
        _nameType = type;
        return;
    }

    /* package */ String getFQName()
    {
        return (_className);
    }

    /* package */ void appendClassName(SmcLexer.Token token)
    {
        _className += token.getValue();
        return;
    }

    /* package */ void setClassName(String name)
    {
        switch (_nameType)
        {
            case SmcLexer.CLASS_NAME:
                _parse_tree.setContext(name);
                break;

            case SmcLexer.PACKAGE_NAME:
                _parse_tree.setPackage(name);
                break;

            case SmcLexer.IMPORT:
                _parse_tree.addImport(name);
                break;
        }

        _className = "";

        return;
    }

    /* package */ void setHeader(SmcLexer.Token token)
    {
        _parse_tree.setHeader(token.getValue());
        return;
    }

    /* package */ void addMap()
    {
        if (_map_in_progress == null)
        {
            error("There is no in-progress map to add", false);
        }
        else
        {
            // If this map does not have a default state, then
            // create one now.
            if (_map_in_progress.hasDefaultState() == false)
            {
                SmcState DefaultState =
                        createTargetState("Default");

                _map_in_progress.addState(DefaultState);
            }

            _parse_tree.addMap(_map_in_progress);
            _map_in_progress = null;
        }

        return;
    }

    /* package */ void createMap(SmcLexer.Token token)
    {
        if (_map_in_progress != null)
        {
            error("Cannot create new map while still filling in previous map (" +
                  _map_in_progress.getName() +
                  ").",
                  false);
        }
        else
        {
            _map_in_progress = createTargetMap(token.getValue());
        }

        return;
    }

    /* package */ void addState()
    {
        if (_map_in_progress == null)
        {
            error("There is no in-progress map to which the state may be added.",
                  false);
        }
        else if (_state_in_progress == null)
        {
            error("There is no in-progrss state to add to the map.",
                  false);
        }
        else
        {
            _map_in_progress.addState(_state_in_progress);
            _state_in_progress = null;
        }

        return;
    }

    /* package */ void createState(SmcLexer.Token token)
    {
        SmcState retval;

        if (_state_in_progress != null)
        {
            error("Cannot create new state while still filling in previous state (" +
                  _state_in_progress.getName() +
                  ").",
                  false);
        }
        else
        {
            _state_in_progress =
                createTargetState(token.getValue());
        }

        return;
    }

    /* package */ void addEntryAction()
    {
        // First verify there is an in-progress action.
        if (_action_in_progress == null)
        {
            error("There is no in-progress action to use as the entry action.",
                  false);
        }
        // Second verify there is an in-progress state.
        else if (_state_in_progress == null)
        {
            error("There is no in-progress state to receive the entry action.",
                  false);
        }
        else
        {
            _state_in_progress.addEntryAction(_action_in_progress);
            _action_in_progress = null;
        }

        return;
    }

    /* package */ void addExitAction()
    {
        // First verify there is an in-progress action.
        if (_action_in_progress == null)
        {
            error("There is no in-progress action to use as the exit action.",
                  false);
        }
        // Second verify there is an in-progress state.
        else if (_state_in_progress == null)
        {
            error("There is no in-progress state to receive the exit action.",
                  false);
        }
        else
        {
            _state_in_progress.addExitAction(_action_in_progress);
            _action_in_progress = null;
        }

        return;
    }

    // Append the in-progress transition to the in-progress
    // state's transition list.
    /* package */ void addTransition()
    {
        if (_state_in_progress == null)
        {
            error("There is no in-progress state to which the transition may be added.",
                  false);
        }
        else if (_transition_in_progress == null)
        {
            error("There is no in-progress transition to add to the state.",
                  false);
        }
        else
        {
            _state_in_progress.addTransition(_transition_in_progress);
            _transition_in_progress = null;
        }

        return;
    }

    // Store away the transition's name for later use in
    // creating the transition.
    /* package */ void storeTransitionName(SmcLexer.Token token)
    {
        if (_transition_name != null)
        {
            error("There already is a previously stored transition name - \"" +
                  _transition_name +
                  "\".",
                  false);
        }
        else
        {
            _transition_name = token.getValue();
        }

        return;
    }

    // Create a transition parameter list.
    /* package */ void createParameterList()
    {
        if (_transition_parameters != null)
        {
            error("Parameter list in-progress.", false);
        }
        else
        {
            _transition_parameters = (List) new LinkedList();
        }

        return;
    }

    // Create a parameter object with the current token as its
    // name.
    /* package */ void createParameter(SmcLexer.Token token)
    {
        if (_parameter_in_progress != null)
        {
            error("There is a parameter already in-progress (" +
                  _parameter_in_progress.getName() +
                  ")",
                  false);
        }
        else
        {
            _parameter_in_progress =
                    createTargetParameter(token.getValue());
        }

        return;
    }

    /* package */ void appendParameterType(SmcLexer.Token token,
                                    String separator)
    {
        if (_parameter_in_progress == null)
        {
            error("There is no in-progress parameter.", false);
        }
        else
        {
            _parameter_in_progress.appendType(token.getValue(),
                                              separator);
        }

        return;
    }

    /* package */ void addTransitionParameter()
    {
        if (_parameter_in_progress == null)
        {
            error("There is no in-progress parameter.", false);
        }
        else if (_transition_parameters == null)
        {
            error("There is no in-progress parameter list.", false);
        }
        else
        {
            _transition_parameters.add(_parameter_in_progress);
            _parameter_in_progress = null;
        }

        return;
    }

    // Create a transition object with the current token as its
    // name.
    /* package */ void createTransition()
    {
        if (_transition_in_progress != null)
        {
            error("Cannot create new transition while still filling in previous transition (" +
                  _transition_in_progress.getName() +
                  ").",
                  false);
        }
        else if (_state_in_progress == null)
        {
            error("There is no in-progress state to which the transition may be added.",
                  false);
        }
        else if (_transition_name == null)
        {
            error("There is no stored transition name.", false);
        }
        else
        {
            SmcTransition transition;

            if (_transition_parameters == null)
            {
                _transition_parameters = (List) new LinkedList();
            }

            // Check if this state already has a transition with
            // this name. If so, then reuse that object.
            // Otherwise, create a new transition object.
            if ((_transition_in_progress = _state_in_progress.findTransition(_transition_name, _transition_parameters)) == null)
            {
                _transition_in_progress =
                        createTargetTransition(_transition_name,
                                               _transition_parameters);
            }

            _transition_name = null;
            _transition_parameters = null;
        }

        return;
    }

    /* package */ void addGuard()
    {
        if (_transition_in_progress == null)
        {
            error("There is no in-progress transition to which the guard may be added.",
                  false);
        }
        else if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to add to the " +
                  _transition_in_progress.getName() +
                  " transition.",
                  false);
        }
        else
        {
            _transition_in_progress.addGuard(_guard_in_progress);
            _guard_in_progress = null;
        }

        return;
    }

    // Create a guard object with the in-progress action as its
    // condition.
    /* package */ void createGuard()
    {
        if (_guard_in_progress != null)
        {
            error("Cannot create new guard while still filling in previous guard.",
                  false);
        }
        else
        {
            _guard_in_progress =
                    createTargetGuard(_action_in_progress);
            _action_in_progress = null;
        }

        return;
    }

    // Set the in-progress guard's transtion type (set, push or
    // pop).
    /* package */ void setTransType(int  trans_type)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to set the transition type.",
                  false);
        }
        else
        {
            switch (trans_type)
            {
                case Smc.TRANS_SET:
                case Smc.TRANS_PUSH:
                case Smc.TRANS_POP:
                    _guard_in_progress.setTransType(trans_type);
                    break;

                default:
                    error("Transition type must be either \"TRANS_SET\", \"TRANS_PUSH\" or \"TRANS_POP\".",
                          false);
                    break;
            }
        }

        return;
    }

    // Set the in-progress guard's end state.
    /* package */ void setEndState(SmcLexer.Token token)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the end state.",
                  false);
        }
        else
        {
            _guard_in_progress.setEndState(token.getValue());
        }

        return;
    }

    // Set the in-progress guard's end state.
    /* package */ void setEndState(String state)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the end state.",
                  false);
        }
        else
        {
            _guard_in_progress.setEndState(state);
        }

        return;
    }

    // Append the current token to the end state.
    /* package */ void appendEndState(SmcLexer.Token token)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to append to the end state.",
                  false);
        }
        else
        {
            _guard_in_progress.appendEndState(token.getValue());
        }

        return;
    }

    // Set the in-progress guard's push state.
    /* package */ void setPushState(SmcLexer.Token token)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the end state.",
                  false);
        }
        else if (_guard_in_progress.getTransType() != Smc.TRANS_PUSH)
        {
            error("Cannot set push state on a non-push transition.",
                  false);
        }
        else if (token.getValue().equals("nil") == true)
        {
            error("Cannot push to \"nil\" state.", false);
        }
        else
        {
            _guard_in_progress.setPushState(token.getValue());
        }

        return;
    }

    // Append the current token to the push state.
    /* package */ void appendPushState(SmcLexer.Token token)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to append to the end state.",
                  false);
        }
        else if (_guard_in_progress.getTransType() != Smc.TRANS_PUSH)
        {
            error("Cannot append push state on a non-push transition.",
                  false);
        }
        else
        {
            _guard_in_progress.appendPushState(token.getValue());
        }

        return;
    }

    // Append the in-progress action to the guard's list.
    /* package */ void addAction()
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the action.",
                  false);
        }
        else if (_action_in_progress == null)
        {
            error("There is no in-progress action to which to add to the guard.",
                  false);
        }
        else
        {
            _guard_in_progress.addAction(_action_in_progress);
            _action_in_progress = null;
        }

        return;
    }

    /* package */ void setActionNegation()
    {
        _action_negation = true;
        return;
    }

    /* package */ void createAction(SmcLexer.Token token)
    {
        if (_action_in_progress != null)
        {
            error("May not create new action while still filling in previous action.",
                  false);
        }
        else
        {
            _action_in_progress =
                    createTargetAction(token.getValue(),
                                       _action_negation);
            _action_negation = false;
        }

        return;
    }

    /* package */ void createVariable(SmcLexer.Token token)
    {
        if (_variable_in_progress != null)
        {
            error("May not create new variable while still filling in previous variable.",
                  false);
        }
        else
        {
            _variable_in_progress = new String(token.getValue());
        }

        return;
    }

    // Add an argument to the current action.
    /* package */ void addSimpleArgument(SmcLexer.Token token)
    {
        _argList.add(
            new SmcSimpleArg(
                token.getValue(),
                token.getLineNumber()));
        return;
    }

    /* package */ void appendVariable(SmcLexer.Token token)
    {
        if (_variable_in_progress == null)
        {
            error("There is no in-progress action to which to append.",
                  false);
        }
        else
        {
            _variable_in_progress += token.getValue();
        }

        return;
    }

    /* package */ void addVariableArgument()
    {
        if (_variable_in_progress == null)
        {
            error("There is no in-progress variable to add.", false);
        }
        else
        {
            _argList.add(
                new SmcSimpleArg(
                    _variable_in_progress,
                    _lexer.getLineNumber()));
            _variable_in_progress = null;
        }

        return;
    }

    // Add a method argument to the argument list.
    /* package */ void addMethodArgument(List argList)
    {
        if (_variable_in_progress == null)
        {
            error("There is no method name to add.", false);
        }
        else
        {
            _argList.add(
                createTargetMethodArg(
                    _variable_in_progress,
                    argList));
            _variable_in_progress = null;
        }

        return;
    }

    // Place the current argument data into a map and push the
    // map on top of the argument stack.
    /* package */ void pushArgStack()
    {
        Object[] argMap = new Object[ARG_ITEMS];

        argMap[ARG_LIST_INDEX] = _argList;
        argMap[VAR_IN_PROG_INDEX] = _variable_in_progress;

        _argsStack.push(argMap);

        _argList = (List) new LinkedList();
        _variable_in_progress = null;

        return;
    }

    // Return the result argument list.
    /* package */ List getArgsResult()
    {
        return (_argsResult);
    }

    // Place the current argument list into the result so that
    // it may be popped back to the caller.
    /* package */ void setArgsResult()
    {
        _argsResult = (List) ((LinkedList) _argList).clone();
        return;
    }

    // Pop the previous argument data from the stack and reset
    // the argument data.
    /* package */ void popArgStack()
    {
        Object[] argMap = (Object[]) _argsStack.pop();

        _argList = (List) argMap[ARG_LIST_INDEX];
        _variable_in_progress = (String) argMap[VAR_IN_PROG_INDEX];

        return;
    }
    

    /* package */ void setPopArgs(List argList)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the action.",
                  false);
        }
        else
        {
            _guard_in_progress.setPopArgs(argList);

            // Clear out the argument list in preparation of
            // the next action/pop.
            argList.clear();
        }

        return;
    }

    /* package */ void setActionArgs(List argList)
    {
        if (_action_in_progress == null)
        {
            error("There is no in-progress action to which to add the argument.",
                  false);
        }
        else
        {
            _action_in_progress.setArguments(argList);

            // Clear out the argument list in preparation for
            // the next pop/action.
            argList.clear();
        }

        return;
    }

    private SmcParseTree createTargetTree()
    {
        SmcParseTree retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcParseTreeCpp();
                break;

            case Smc.JAVA:
                retval = new SmcParseTreeJava();
                break;

            case Smc.TCL:
                retval = new SmcParseTreeTcl();
                break;
        }

        return (retval);
    }

    // Create a map object that is language-specific.
    private SmcMap createTargetMap(String name)
    {
        SmcMap retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcMapCpp(name,
                                       _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcMapJava(name,
                                       _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcMapTcl(name,
                                       _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    // Create a state object that is language-specific.
    private SmcState createTargetState(String name)
    {
        SmcState retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcStateCpp(name,
                                         _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcStateJava(name,
                                          _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcStateTcl(name,
                                         _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    private SmcTransition createTargetTransition(String name,
                                                 List parameters)
    {
        SmcTransition retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcTransitionCpp(name,
                                              parameters,
                                              _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcTransitionJava(name,
                                              parameters,
                                               _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcTransitionTcl(name,
                                              parameters,
                                              _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    private SmcParameter createTargetParameter(String name)
    {
        SmcParameter retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcParameterCpp(name,
                                             _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcParameterJava(name,
                                              _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcParameterTcl(name,
                                             _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    private SmcGuard createTargetGuard(SmcAction condition)
    {
        SmcGuard retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcGuardCpp(condition,
                                         _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcGuardJava(condition,
                                          _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcGuardTcl(condition,
                                         _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    private SmcAction createTargetAction(String name,
                                         boolean negation_flag)
    {
        SmcAction retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcActionCpp(name,
                                          negation_flag,
                                          _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcActionJava(name,
                                           negation_flag,
                                           _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcActionTcl(name,
                                          negation_flag,
                                          _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

    // Create the appropriate method argument object depending
    // on the target programming language.
    private SmcArgument createTargetMethodArg(String name, List argList)
    {
        SmcArgument retval = null;

        switch(Smc._target_language)
        {
            case Smc.C_PLUS_PLUS:
                retval = new SmcMethodArgCpp(name,
                                             argList,
                                             _lexer.getLineNumber());
                break;

            case Smc.JAVA:
                retval = new SmcMethodArgJava(name,
                                             argList,
                                              _lexer.getLineNumber());
                break;

            case Smc.TCL:
                retval = new SmcMethodArgTcl(name,
                                             argList,
                                             _lexer.getLineNumber());
                break;
        }

        return (retval);
    }

// Member Data

    // The parse state map.
    private SmcParserContext _parser_map;

    // Get tokens from the lexer.
    private SmcLexer _lexer;

    // Read all the tokens into this list first and then
    // pass them to the parser map.
    private List _tokenList;

    // Store arguments in this list until they can be associated
    // with a pop or an action.
    private List _argList;

    // The argument list is stored here so it can be returned.
    private List _argsResult;

    // Because a function can now be an
    private Stack _argsStack;

    // Will be generating code for this language.
    private int _language;

    // Keep track of errors.
    private String _error_message;
    private boolean _parse_status;
    private boolean _quit_flag;

    // Keep track whether we are parsing a class name, package or
    // import.
    private int _nameType;

    private SmcParseTree _parse_tree;
    private String _start_state;
    private String _className;
    private String _context;
    private String _header;
    private boolean _action_negation;

    private String _name_in_progress;
    private SmcMap _map_in_progress;
    private SmcState _state_in_progress;
    private String _transition_name;
    private List _transition_parameters;
    private SmcParameter _parameter_in_progress;
    private SmcTransition _transition_in_progress;
    private SmcGuard _guard_in_progress;
    private SmcAction _action_in_progress;
    private String _variable_in_progress;

    // Constants
    private static final int ARG_LIST_INDEX = 0;
    private static final int VAR_IN_PROG_INDEX = 1;
    private static final int ARG_ITEMS = 2;

    // Statics.

    // Create a hashmap which associates token names with
    // parser transitions. When a token is received, use this
    // table to get the appropriate transition method and
    // invoke that method.
    private static Method[] _TransMethod;

    static
    {
        _TransMethod = new Method[SmcLexer.TOKEN_COUNT];

        try
        {
            Class _map_class = SmcParserContext.class;
            Class[] param_types = new Class[1];

            param_types[0] = SmcLexer.Token.class;

            _TransMethod[SmcLexer.ENTRY] =
                _map_class.getDeclaredMethod("ENTRY",
                                             param_types);
            _TransMethod[SmcLexer.EXIT] =
                _map_class.getDeclaredMethod("EXIT",
                                             param_types);
            _TransMethod[SmcLexer.POP] =
                _map_class.getDeclaredMethod("POP",
                                             param_types);
            _TransMethod[SmcLexer.PUSH] =
                _map_class.getDeclaredMethod("PUSH",
                                             param_types);
            _TransMethod[SmcLexer.WORD] =
                _map_class.getDeclaredMethod("WORD",
                                             param_types);
            _TransMethod[SmcLexer.START_STATE] =
                _map_class.getDeclaredMethod("START_STATE",
                                             param_types);
            _TransMethod[SmcLexer.MAP_NAME] =
                _map_class.getDeclaredMethod("MAP_NAME",
                                             param_types);
            _TransMethod[SmcLexer.CLASS_NAME] =
                _map_class.getDeclaredMethod("CLASS_NAME",
                                             param_types);
            _TransMethod[SmcLexer.HEADER_FILE] =
                _map_class.getDeclaredMethod("HEADER_FILE",
                                             param_types);
            _TransMethod[SmcLexer.PACKAGE_NAME] =
                _map_class.getDeclaredMethod("PACKAGE_NAME",
                                             param_types);
            _TransMethod[SmcLexer.IMPORT] =
                _map_class.getDeclaredMethod("IMPORT",
                                             param_types);
            _TransMethod[SmcLexer.LEFT_BRACE] =
                _map_class.getDeclaredMethod("LEFT_BRACE",
                                             param_types);
            _TransMethod[SmcLexer.RIGHT_BRACE] =
                _map_class.getDeclaredMethod("RIGHT_BRACE",
                                             param_types);
            _TransMethod[SmcLexer.LEFT_BRACKET] =
                _map_class.getDeclaredMethod("LEFT_BRACKET",
                                             param_types);
            _TransMethod[SmcLexer.RIGHT_BRACKET] =
                _map_class.getDeclaredMethod("RIGHT_BRACKET",
                                             param_types);
            _TransMethod[SmcLexer.LEFT_PAREN] =
                _map_class.getDeclaredMethod("LEFT_PAREN",
                                             param_types);
            _TransMethod[SmcLexer.RIGHT_PAREN] =
                _map_class.getDeclaredMethod("RIGHT_PAREN",
                                             param_types);
            _TransMethod[SmcLexer.SEMICOLON] =
                _map_class.getDeclaredMethod("SEMICOLON",
                                             param_types);
            _TransMethod[SmcLexer.COLON] =
                _map_class.getDeclaredMethod("COLON",
                                             param_types);
            _TransMethod[SmcLexer.COMMA] =
                _map_class.getDeclaredMethod("COMMA",
                                             param_types);
            _TransMethod[SmcLexer.EXCLAMATION] =
                _map_class.getDeclaredMethod("EXCLAMATION",
                                             param_types);
            _TransMethod[SmcLexer.SOURCE] =
                _map_class.getDeclaredMethod("SOURCE",
                                             param_types);
            _TransMethod[SmcLexer.EOD] =
                _map_class.getDeclaredMethod("EOD",
                                             param_types);
            _TransMethod[SmcLexer.VARIABLE] =
                _map_class.getDeclaredMethod("VARIABLE",
                                             param_types);
            _TransMethod[SmcLexer.INTEGER] =
                _map_class.getDeclaredMethod("INTEGER",
                                             param_types);
            _TransMethod[SmcLexer.FLOAT] =
                _map_class.getDeclaredMethod("FLOAT",
                                             param_types);
            _TransMethod[SmcLexer.STRING] =
                _map_class.getDeclaredMethod("STRING",
                                             param_types);
            _TransMethod[SmcLexer.ASTERISK] =
                _map_class.getDeclaredMethod("ASTERISK",
                                             param_types);
            _TransMethod[SmcLexer.AMPERSAND] =
                _map_class.getDeclaredMethod("AMPERSAND",
                                             param_types);
            _TransMethod[SmcLexer.SLASH] =
                _map_class.getDeclaredMethod("SLASH",
                                             param_types);
        }
        catch (NoSuchMethodException ex1)
        {
            System.err.println("INITIALIZATION ERROR! " + ex1);
            System.exit(1);
        }
        catch (SecurityException ex2)
        {
            System.err.println("INITIALIZATION ERROR! " + ex2);
            System.exit(1);
        }
    }
}