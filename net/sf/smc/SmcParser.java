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
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;

public final class SmcParser
{
// Member Methods

    public SmcParser(SmcLexer lexer, int language)
    {
        _lexer = lexer;
        _language = language;
        _parser_map = new SmcParserContext(this);

        _token = null;
        _start_state = "";
        _context = "";
        _header = "";

        _action_negation = false;

        // Create a hashmap which associates token names with
        // parser transitions. When a token is received, use this
        // hashmap to get the appropriate transition method and
        // invoke that method.
        _transMethod = new Method[SmcLexer.TOKEN_COUNT];
        try
        {
            Class _map_class = _parser_map.getClass();
            Class[] param_types = new Class[0];

            _transMethod[SmcLexer.ENTRY] =
                _map_class.getDeclaredMethod("ENTRY",
                                             param_types);
            _transMethod[SmcLexer.EXIT] =
                _map_class.getDeclaredMethod("EXIT",
                                             param_types);
            _transMethod[SmcLexer.POP] =
                _map_class.getDeclaredMethod("POP",
                                             param_types);
            _transMethod[SmcLexer.PUSH] =
                _map_class.getDeclaredMethod("PUSH",
                                             param_types);
            _transMethod[SmcLexer.WORD] =
                _map_class.getDeclaredMethod("WORD",
                                             param_types);
            _transMethod[SmcLexer.START_STATE] =
                _map_class.getDeclaredMethod("START_STATE",
                                             param_types);
            _transMethod[SmcLexer.MAP_NAME] =
                _map_class.getDeclaredMethod("MAP_NAME",
                                             param_types);
            _transMethod[SmcLexer.CLASS_NAME] =
                _map_class.getDeclaredMethod("CLASS_NAME",
                                             param_types);
            _transMethod[SmcLexer.HEADER_FILE] =
                _map_class.getDeclaredMethod("HEADER_FILE",
                                             param_types);
            _transMethod[SmcLexer.LEFT_BRACE] =
                _map_class.getDeclaredMethod("LEFT_BRACE",
                                             param_types);
            _transMethod[SmcLexer.RIGHT_BRACE] =
                _map_class.getDeclaredMethod("RIGHT_BRACE",
                                             param_types);
            _transMethod[SmcLexer.LEFT_BRACKET] =
                _map_class.getDeclaredMethod("LEFT_BRACKET",
                                             param_types);
            _transMethod[SmcLexer.RIGHT_BRACKET] =
                _map_class.getDeclaredMethod("RIGHT_BRACKET",
                                             param_types);
            _transMethod[SmcLexer.LEFT_PAREN] =
                _map_class.getDeclaredMethod("LEFT_PAREN",
                                             param_types);
            _transMethod[SmcLexer.RIGHT_PAREN] =
                _map_class.getDeclaredMethod("RIGHT_PAREN",
                                             param_types);
            _transMethod[SmcLexer.SEMICOLON] =
                _map_class.getDeclaredMethod("SEMICOLON",
                                             param_types);
            _transMethod[SmcLexer.COLON] =
                _map_class.getDeclaredMethod("COLON",
                                             param_types);
            _transMethod[SmcLexer.COMMA] =
                _map_class.getDeclaredMethod("COMMA",
                                             param_types);
            _transMethod[SmcLexer.EXCLAMATION] =
                _map_class.getDeclaredMethod("EXCLAMATION",
                                             param_types);
            _transMethod[SmcLexer.SOURCE] =
                _map_class.getDeclaredMethod("SOURCE",
                                             param_types);
            _transMethod[SmcLexer.EOD] =
                _map_class.getDeclaredMethod("EOD",
                                             param_types);
            _transMethod[SmcLexer.VARIABLE] =
                _map_class.getDeclaredMethod("VARIABLE",
                                             param_types);
            _transMethod[SmcLexer.INTEGER] =
                _map_class.getDeclaredMethod("INTEGER",
                                             param_types);
            _transMethod[SmcLexer.FLOAT] =
                _map_class.getDeclaredMethod("FLOAT",
                                             param_types);
            _transMethod[SmcLexer.STRING] =
                _map_class.getDeclaredMethod("STRING",
                                             param_types);
            _transMethod[SmcLexer.ASTERISK] =
                _map_class.getDeclaredMethod("ASTERISK",
                                             param_types);
            _transMethod[SmcLexer.AMPERSAND] =
                _map_class.getDeclaredMethod("AMPERSAND",
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

    public SmcParseTree parse()
        throws IOException
    {
        int token_type;
        Object[] params = new Object[0];

        _parse_tree = createTargetTree();

        _map_in_progress = null;
        _state_in_progress = null;
        _transition_name = null;
        _transition_parameters = null;
        _parameter_in_progress = null;
        _transition_in_progress = null;
        _guard_in_progress = null;
        _action_in_progress = null;
        _variable_in_progress = null;

        // _parser_map.setDebugFlag(true);

        _parse_status = true;
        _quit_flag = false;
        token_type = -1;
        while (_quit_flag == false &&
               (_token = _lexer.nextToken()) != null &&
               (token_type = _token.getType()) != SmcLexer.DONE_SUCCESS &&
               token_type != SmcLexer.DONE_FAILED &&
               token_type != SmcLexer.TOKEN_NOT_SET)
        {
            // Is the token type valid?
            if (token_type > SmcLexer.TOKEN_NOT_SET &&
                token_type < SmcLexer.TOKEN_COUNT)
            {
                try
                {
                    _transMethod[token_type].invoke(_parser_map,
                                                    params);
                }
                catch (Exception ex)
                {
                    System.err.println("TRANSITION EXCEPTION - " +
                                       ex +
                                       (ex.getMessage() == null ?
                                        ", token " :
                                        "\"" + ex.getMessage() + "\", token ") +
                                       _token);
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            else
            {
                error("Undefined token type (" +
                      Integer.toString(token_type) +
                      ")",
                      true);
            }
        }

        if (token_type == SmcLexer.DONE_FAILED)
        {
            error(_token.getValue(), true);
        }
        else if (_parse_status == false)
        {
            _parse_tree = null;
        }

        return(_parse_tree);
    }

    //----------------------------------------
    // State Map Actions
    //

    public void warning(String error_msg)
    {
        System.err.println(Smc.getSourceFileName() +
                           ":" +
                           Integer.toString(_lexer.getLineNumber()) +
                           ": warning - " +
                           error_msg);
        return;
    }

    public void error(String error_msg, boolean quitFlag)
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

    public boolean isStartStateSet()
    {
        return(_parse_tree.getStartState().length() == 0 ? false : true);
    }

    public boolean isClassSet()
    {
        return(_parse_tree.getContext().length() == 0 ? false : true);
    }

    public boolean isHeaderSet()
    {
        return(_parse_tree.getHeader().length() == 0 ? false : true);
    }

    public boolean isDuplicateMap()
    {
        return(_parse_tree.findMap(_token.getValue()) == null ? false : true);
    }

    public boolean isDuplicateState()
    {
        return(_map_in_progress.findState(_token.getValue()));
    }

    public boolean isEntrySet()
    {
        return(_state_in_progress.getEntryActions().size() == 0 ? false : true);
    }

    public boolean isExitSet()
    {
        return(_state_in_progress.getExitActions().size() == 0 ? false : true);
    }

    public boolean isDuplicateGuard()
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

        return(retval);
    }

    public boolean isCpp()
    {
        return (_language == Smc.C_PLUS_PLUS);
    }

    public boolean isJava()
    {
        return (_language == Smc.JAVA);
    }

    public boolean isTcl()
    {
        return (_language == Smc.TCL);
    }

    public void setHeaderLine()
    {
        _parse_tree.setHeaderLine(_lexer.getLineNumber());
        return;
    }

    public void addSource()
    {
        _parse_tree.setSource(_token.getValue());
        return;
    }

    public void appendStartState()
    {
        _start_state += _token.getValue();
        return;
    }

    public void setStartState()
    {
        _parse_tree.setStartState(_start_state);
        return;
    }

    public void setClassName()
    {
        _parse_tree.setContext(_token.getValue());
        return;
    }

    public void setHeader()
    {
        _parse_tree.setHeader(_token.getValue());
        return;
    }

    public void addMap()
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

    public void createMap()
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
            _map_in_progress = createTargetMap(_token.getValue());
        }

        return;
    }

    public void addState()
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

    public void createState()
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
            _state_in_progress = createTargetState(_token.getValue());
        }

        return;
    }

    public void addEntryAction()
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

    public void addExitAction()
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
    public void addTransition()
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
    public void storeTransitionName()
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
            _transition_name = _token.getValue();
        }

        return;
    }

    // Create a transition parameter list.
    public void createParameterList()
    {
        if (_transition_parameters != null)
        {
            error("Parameter list in-progress.", false);
        }
        else
        {
            _transition_parameters = new LinkedList();
        }

        return;
    }

    public boolean isTransParametersEmpty()
    {
        return((_transition_parameters == null ||
                _transition_parameters.size() == 0)
               ? true : false);
    }

    // Create a parameter object with the current token as its
    // name.
    public void createParameter()
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
                    createTargetParameter(_token.getValue());
        }

        return;
    }

    public void appendParameterType(String separator)
    {
        if (_parameter_in_progress == null)
        {
            error("There is no in-progress parameter.", false);
        }
        else
        {
            _parameter_in_progress.appendType(_token.getValue(),
                                              separator);
        }

        return;
    }

    public void addTransitionParameter()
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
    public void createTransition()
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
                _transition_parameters = new LinkedList();
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

    public void addGuard()
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
    public void createGuard()
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
    public void setTransType(String trans_type)
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to set the transition type.",
                  false);
        }
        else if (trans_type.compareTo("TRANS_SET") == 0)
        {
            _guard_in_progress.setTransType(Smc.TRANS_SET);
        }
        else if (trans_type.compareTo("TRANS_PUSH") == 0)
        {
            _guard_in_progress.setTransType(Smc.TRANS_PUSH);
        }
        else if (trans_type.compareTo("TRANS_POP") == 0)
        {
            _guard_in_progress.setTransType(Smc.TRANS_POP);
        }
        else
        {
            error("Transition type must be either \"TRANS_SET\", \"TRANS_PUSH\" or \"TRANS_POP\".",
                  false);
        }

        return;
    }

    // Set the in-progress guard's end state.
    public void setEndState()
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to add the end state.",
                  false);
        }
        else
        {
            _guard_in_progress.setEndState(_token.getValue());
        }

        return;
    }

    // Append the current token to the end state.
    public void appendEndState()
    {
        if (_guard_in_progress == null)
        {
            error("There is no in-progress guard to which to append to the end state.",
                  false);
        }
        else
        {
            _guard_in_progress.appendEndState(_token.getValue());
        }

        return;
    }

    // Append the in-progress action to the guard's list.
    public void addAction()
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

    public void setActionNegation()
    {
        _action_negation = true;
        return;
    }

    public void createAction()
    {
        if (_action_in_progress != null)
        {
            error("May not create new action while still filling in previous action.",
                  false);
        }
        else
        {
            _action_in_progress =
                    createTargetAction(_token.getValue(),
                                       _action_negation);
            _action_negation = false;
        }

        return;
    }

    public void createVariable()
    {
        if (_variable_in_progress != null)
        {
            error("May not create new variable while still filling in previous variable.",
                  false);
        }
        else
        {
            _variable_in_progress = new String(_token.getValue());
        }

        return;
    }

    // Add an argument to the current action.
    public void addArgument()
    {
        if (_action_in_progress == null)
        {
            error("There is no in-progress action to which to add the argument.",
                  false);
        }
        else
        {
            _action_in_progress.addArgument(_token.getValue());
        }

        return;
    }

    public void appendVariable()
    {
        if (_variable_in_progress == null)
        {
            error("There is no in-progress action to which to append.",
                  false);
        }
        else
        {
            _variable_in_progress += _token.getValue();
        }

        return;
    }

    public void addVariable()
    {
        if (_variable_in_progress == null)
        {
            error("There is no in-progress variable to add.", false);
        }
        else
        {
            _action_in_progress.addArgument(_variable_in_progress);
            _variable_in_progress = null;
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

        return(retval);
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

        return(retval);
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

        return(retval);
    }

    private SmcTransition createTargetTransition(String name,
                                                 LinkedList parameters)
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

        return(retval);
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

        return(retval);
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

        return(retval);
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

        return(retval);
    }

// Member Data

    // The parse state map.
    private SmcParserContext _parser_map;

    // Legal token transitions.
    private Method[] _transMethod;

    // Get tokens from the lexer.
    private SmcLexer _lexer;

    // Will be generating code for this language.
    private int _language;

    // The current token.
    SmcLexer.Token _token;

    // Keep track of errors.
    String _error_message;
    boolean _parse_status;
    boolean _quit_flag;

    private SmcParseTree _parse_tree;
    private String _start_state;
    private String _context;
    private String _header;
    private boolean _action_negation;

    private SmcMap _map_in_progress;
    private SmcState _state_in_progress;
    private String _transition_name;
    private LinkedList _transition_parameters;
    private SmcParameter _parameter_in_progress;
    private SmcTransition _transition_in_progress;
    private SmcGuard _guard_in_progress;
    private SmcAction _action_in_progress;
    private String _variable_in_progress;
}
