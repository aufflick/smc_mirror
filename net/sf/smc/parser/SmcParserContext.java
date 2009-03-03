
/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is State Machine Compiler (SMC).
 *
 * The Initial Developer of the Original Code is Charles W. Rapp.
 * Portions created by Charles W. Rapp are
 * Copyright (C) 2000 - 2005, 2008. Charles W. Rapp.
 * All Rights Reserved.
 *
 * Contributor(s):
 *   Eitan Suez contributed examples/Ant.
 *   (Name withheld) contributed the C# code generation and
 *   examples/C#.
 *   Francois Perrad contributed the Python code generation and
 *   examples/Python, Perl code generation and examples/Perl,
 *   Ruby code generation and examples/Ruby, Lua code generation
 *   and examples/Lua, Groovy code generation and examples/Groovy.
 *   Chris Liscio contributed the Objective-C code generation
 *   and examples/ObjC.
 *
 * smcParser --
 *
 *  This state machine defines the  state machine language.
 *
 * RCS ID
 * $Id$
 *
 * CHANGE LOG
 * $Log$
 * Revision 1.2  2009/03/03 17:28:53  kgreg99
 * 1. Bugs resolved:
 * #2657779 - modified SmcParser.sm and SmcParserContext.java
 * #2648516 - modified SmcCSharpGenerator.java
 * #2648472 - modified SmcSyntaxChecker.java
 * #2648469 - modified SmcMap.java
 *
 * Revision 1.1  2009/03/01 18:20:42  cwrapp
 * Preliminary v. 6.0.0 commit.
 *
 * Revision 1.20  2008/04/22 16:05:24  fperrad
 * - add PHP language (patch from Toni Arnold)
 *
 * Revision 1.19  2008/01/22 08:56:55  fperrad
 * - fix : allows Perl parameter with graph generation
 *
 * Revision 1.18  2007/11/19 18:53:21  fperrad
 * + add : jump syntax
 *   jump uses the same syntax as push,
 *   allows transition between states of different maps but without stacking a return context.
 *
 * Revision 1.17  2007/07/16 06:28:06  fperrad
 * + Added Groovy generator.
 *
 * Revision 1.16  2007/02/21 13:56:16  cwrapp
 * Moved Java code to release 1.5.0
 *
 * Revision 1.15  2007/01/15 00:23:51  cwrapp
 * Release 4.4.0 initial commit.
 *
 * Revision 1.14  2007/01/03 15:23:05  fperrad
 * + Added Lua generator.
 *
 * Revision 1.13  2006/09/16 15:04:29  cwrapp
 * Initial v. 4.3.3 check-in.
 *
 * Revision 1.12  2006/07/11 18:17:01  cwrapp
 * Removed errors regarding percent keywords.
 *
 * Revision 1.11  2006/04/22 12:45:26  cwrapp
 * Version 4.3.1
 *
 * Revision 1.10  2005/09/14 01:51:33  cwrapp
 * Changes in release 4.2.0:
 * New features:
 *
 * None.
 *
 * Fixed the following bugs:
 *
 * + (Java) -java broken due to an untested minor change.
 *
 * Revision 1.9  2005/08/26 15:21:34  cwrapp
 * Final commit for release 4.2.0. See README.txt for more information.
 *
 * Revision 1.8  2005/07/07 12:11:56  fperrad
 * Add a new token '$' for Perl language.
 * The type of parameter is optional for Python, Per & Ruby.
 *
 * Revision 1.7  2005/06/30 10:44:23  cwrapp
 * Added %access keyword which allows developers to set the generate Context
 * class' accessibility level in Java and C#.
 *
 * Revision 1.6  2005/05/28 19:28:42  cwrapp
 * Moved to visitor pattern.
 *
 * Revision 1.5  2005/02/21 15:37:52  charlesr
 * Added Francois Perrad to Contributors section for Python work.
 *
 * Revision 1.4  2005/02/21 15:21:21  charlesr
 * Added -graph target as allowing %include, %declare and %import.
 *
 * Revision 1.3  2005/02/03 17:06:21  charlesr
 * SmcParser.warning() and .error() methods now take a line
 * number argument. All calls to these method within the
 * parser's FSM have been modified.
 *
 * Revision 1.2  2004/09/06 16:41:32  charlesr
 * Added "property = value" syntax. Added C# support.
 *
 * Revision 1.1  2004/05/31 13:56:34  charlesr
 * Added support for VB.net code generation.
 *
 * Revision 1.0  2003/12/14 21:06:00  charlesr
 * Initial revision
 *
 */


package net.sf.smc.parser;

import java.util.List;
import net.sf.smc.model.SmcAction;
import net.sf.smc.model.SmcElement.TransType;
import net.sf.smc.model.SmcFSM;
import net.sf.smc.model.SmcGuard;
import net.sf.smc.model.SmcMap;
import net.sf.smc.model.SmcParameter;
import net.sf.smc.model.SmcTransition;
import net.sf.smc.parser.SmcParser.TargetLanguage;
import java.io.PrintStream;

public final class SmcParserContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public SmcParserContext(SmcParser owner)
    {
        super (ParserMap.Start);

        _owner = owner;
    }

    public SmcParserContext(SmcParser owner, SmcParserState initState)
    {
        super (initState);

        _owner = owner;
    }

    public void enterStartState()
    {
        getState().Entry(this);
        return;
    }

    public void ACCESS(SmcLexer.Token token)
    {
        _transition = "ACCESS";
        getState().ACCESS(this, token);
        _transition = "";
        return;
    }

    public void CLASS_NAME(SmcLexer.Token token)
    {
        _transition = "CLASS_NAME";
        getState().CLASS_NAME(this, token);
        _transition = "";
        return;
    }

    public void COLON(SmcLexer.Token token)
    {
        _transition = "COLON";
        getState().COLON(this, token);
        _transition = "";
        return;
    }

    public void COMMA(SmcLexer.Token token)
    {
        _transition = "COMMA";
        getState().COMMA(this, token);
        _transition = "";
        return;
    }

    public void DECLARE(SmcLexer.Token token)
    {
        _transition = "DECLARE";
        getState().DECLARE(this, token);
        _transition = "";
        return;
    }

    public void DOLLAR(SmcLexer.Token token)
    {
        _transition = "DOLLAR";
        getState().DOLLAR(this, token);
        _transition = "";
        return;
    }

    public void ENTRY(SmcLexer.Token token)
    {
        _transition = "ENTRY";
        getState().ENTRY(this, token);
        _transition = "";
        return;
    }

    public void EOD(SmcLexer.Token token)
    {
        _transition = "EOD";
        getState().EOD(this, token);
        _transition = "";
        return;
    }

    public void EQUAL(SmcLexer.Token token)
    {
        _transition = "EQUAL";
        getState().EQUAL(this, token);
        _transition = "";
        return;
    }

    public void EXIT(SmcLexer.Token token)
    {
        _transition = "EXIT";
        getState().EXIT(this, token);
        _transition = "";
        return;
    }

    public void HEADER_FILE(SmcLexer.Token token)
    {
        _transition = "HEADER_FILE";
        getState().HEADER_FILE(this, token);
        _transition = "";
        return;
    }

    public void IMPORT(SmcLexer.Token token)
    {
        _transition = "IMPORT";
        getState().IMPORT(this, token);
        _transition = "";
        return;
    }

    public void INCLUDE_FILE(SmcLexer.Token token)
    {
        _transition = "INCLUDE_FILE";
        getState().INCLUDE_FILE(this, token);
        _transition = "";
        return;
    }

    public void JUMP(SmcLexer.Token token)
    {
        _transition = "JUMP";
        getState().JUMP(this, token);
        _transition = "";
        return;
    }

    public void LEFT_BRACE(SmcLexer.Token token)
    {
        _transition = "LEFT_BRACE";
        getState().LEFT_BRACE(this, token);
        _transition = "";
        return;
    }

    public void LEFT_BRACKET(SmcLexer.Token token)
    {
        _transition = "LEFT_BRACKET";
        getState().LEFT_BRACKET(this, token);
        _transition = "";
        return;
    }

    public void LEFT_PAREN(SmcLexer.Token token)
    {
        _transition = "LEFT_PAREN";
        getState().LEFT_PAREN(this, token);
        _transition = "";
        return;
    }

    public void MAP_NAME(SmcLexer.Token token)
    {
        _transition = "MAP_NAME";
        getState().MAP_NAME(this, token);
        _transition = "";
        return;
    }

    public void PACKAGE_NAME(SmcLexer.Token token)
    {
        _transition = "PACKAGE_NAME";
        getState().PACKAGE_NAME(this, token);
        _transition = "";
        return;
    }

    public void POP(SmcLexer.Token token)
    {
        _transition = "POP";
        getState().POP(this, token);
        _transition = "";
        return;
    }

    public void PUSH(SmcLexer.Token token)
    {
        _transition = "PUSH";
        getState().PUSH(this, token);
        _transition = "";
        return;
    }

    public void RIGHT_BRACE(SmcLexer.Token token)
    {
        _transition = "RIGHT_BRACE";
        getState().RIGHT_BRACE(this, token);
        _transition = "";
        return;
    }

    public void RIGHT_PAREN(SmcLexer.Token token)
    {
        _transition = "RIGHT_PAREN";
        getState().RIGHT_PAREN(this, token);
        _transition = "";
        return;
    }

    public void SEMICOLON(SmcLexer.Token token)
    {
        _transition = "SEMICOLON";
        getState().SEMICOLON(this, token);
        _transition = "";
        return;
    }

    public void SLASH(SmcLexer.Token token)
    {
        _transition = "SLASH";
        getState().SLASH(this, token);
        _transition = "";
        return;
    }

    public void SOURCE(SmcLexer.Token token)
    {
        _transition = "SOURCE";
        getState().SOURCE(this, token);
        _transition = "";
        return;
    }

    public void START_STATE(SmcLexer.Token token)
    {
        _transition = "START_STATE";
        getState().START_STATE(this, token);
        _transition = "";
        return;
    }

    public void WORD(SmcLexer.Token token)
    {
        _transition = "WORD";
        getState().WORD(this, token);
        _transition = "";
        return;
    }

    public void actionsDone(List<SmcAction> actions, int lineNumber)
    {
        _transition = "actionsDone";
        getState().actionsDone(this, actions, lineNumber);
        _transition = "";
        return;
    }

    public void actionsError()
    {
        _transition = "actionsError";
        getState().actionsError(this);
        _transition = "";
        return;
    }

    public void argsDone(List<String> args)
    {
        _transition = "argsDone";
        getState().argsDone(this, args);
        _transition = "";
        return;
    }

    public void argsError()
    {
        _transition = "argsError";
        getState().argsError(this);
        _transition = "";
        return;
    }

    public void paramsDone(List<SmcParameter> params, int lineNumber)
    {
        _transition = "paramsDone";
        getState().paramsDone(this, params, lineNumber);
        _transition = "";
        return;
    }

    public void paramsError(List<SmcParameter> params, int lineNumber)
    {
        _transition = "paramsError";
        getState().paramsError(this, params, lineNumber);
        _transition = "";
        return;
    }

    public SmcParserState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((SmcParserState) _state);
    }

    protected SmcParser getOwner()
    {
        return (_owner);
    }

    public void setOwner(SmcParser owner)
    {
        if (owner == null)
        {
            throw (
                new NullPointerException(
                    "null owner"));
        }
        else
        {
            _owner = owner;
        }

        return;
    }

//---------------------------------------------------------------
// Member data.
//

    transient private SmcParser _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class SmcParserState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SmcParserState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(SmcParserContext context) {}
        protected void Exit(SmcParserContext context) {}

        protected void ACCESS(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void CLASS_NAME(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void COLON(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void COMMA(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void DECLARE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void DOLLAR(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void ENTRY(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void EOD(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void EQUAL(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void EXIT(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void HEADER_FILE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void IMPORT(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void INCLUDE_FILE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void JUMP(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void LEFT_BRACKET(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void PACKAGE_NAME(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void POP(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void PUSH(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void SEMICOLON(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void SLASH(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void START_STATE(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void WORD(SmcParserContext context, SmcLexer.Token token)
        {
            Default(context);
        }

        protected void actionsDone(SmcParserContext context, List<SmcAction> actions, int lineNumber)
        {
            Default(context);
        }

        protected void actionsError(SmcParserContext context)
        {
            Default(context);
        }

        protected void argsDone(SmcParserContext context, List<String> args)
        {
            Default(context);
        }

        protected void argsError(SmcParserContext context)
        {
            Default(context);
        }

        protected void paramsDone(SmcParserContext context, List<SmcParameter> params, int lineNumber)
        {
            Default(context);
        }

        protected void paramsError(SmcParserContext context, List<SmcParameter> params, int lineNumber)
        {
            Default(context);
        }

        protected void Default(SmcParserContext context)
        {
            if (context.getDebugFlag() == true)
            {
                PrintStream str = 
                    context.getDebugStream();

                str.println(
                    "TRANSITION   : Default");
            }

            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class ParserMap
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final ParserMap_Default.ParserMap_Start Start =
            new ParserMap_Default.ParserMap_Start("ParserMap.Start", 0);
        public static final ParserMap_Default.ParserMap_Context Context =
            new ParserMap_Default.ParserMap_Context("ParserMap.Context", 1);
        public static final ParserMap_Default.ParserMap_StartState StartState =
            new ParserMap_Default.ParserMap_StartState("ParserMap.StartState", 2);
        public static final ParserMap_Default.ParserMap_HeaderFile HeaderFile =
            new ParserMap_Default.ParserMap_HeaderFile("ParserMap.HeaderFile", 3);
        public static final ParserMap_Default.ParserMap_IncludeFile IncludeFile =
            new ParserMap_Default.ParserMap_IncludeFile("ParserMap.IncludeFile", 4);
        public static final ParserMap_Default.ParserMap_Package Package =
            new ParserMap_Default.ParserMap_Package("ParserMap.Package", 5);
        public static final ParserMap_Default.ParserMap_Import Import =
            new ParserMap_Default.ParserMap_Import("ParserMap.Import", 6);
        public static final ParserMap_Default.ParserMap_Declare Declare =
            new ParserMap_Default.ParserMap_Declare("ParserMap.Declare", 7);
        public static final ParserMap_Default.ParserMap_Access Access =
            new ParserMap_Default.ParserMap_Access("ParserMap.Access", 8);
        public static final ParserMap_Default.ParserMap_StartError StartError =
            new ParserMap_Default.ParserMap_StartError("ParserMap.StartError", 9);
        public static final ParserMap_Default.ParserMap_MapStart MapStart =
            new ParserMap_Default.ParserMap_MapStart("ParserMap.MapStart", 10);
        public static final ParserMap_Default.ParserMap_MapStartError MapStartError =
            new ParserMap_Default.ParserMap_MapStartError("ParserMap.MapStartError", 11);
        public static final ParserMap_Default.ParserMap_MapName MapName =
            new ParserMap_Default.ParserMap_MapName("ParserMap.MapName", 12);
        public static final ParserMap_Default.ParserMap_MapStates MapStates =
            new ParserMap_Default.ParserMap_MapStates("ParserMap.MapStates", 13);
        public static final ParserMap_Default.ParserMap_MapStatesError MapStatesError =
            new ParserMap_Default.ParserMap_MapStatesError("ParserMap.MapStatesError", 14);
        public static final ParserMap_Default.ParserMap_States States =
            new ParserMap_Default.ParserMap_States("ParserMap.States", 15);
        public static final ParserMap_Default.ParserMap_StateStart StateStart =
            new ParserMap_Default.ParserMap_StateStart("ParserMap.StateStart", 16);
        public static final ParserMap_Default.ParserMap_StateStartError StateStartError =
            new ParserMap_Default.ParserMap_StateStartError("ParserMap.StateStartError", 17);
        public static final ParserMap_Default.ParserMap_EntryStart EntryStart =
            new ParserMap_Default.ParserMap_EntryStart("ParserMap.EntryStart", 18);
        public static final ParserMap_Default.ParserMap_EntryEnd EntryEnd =
            new ParserMap_Default.ParserMap_EntryEnd("ParserMap.EntryEnd", 19);
        public static final ParserMap_Default.ParserMap_ExitStart ExitStart =
            new ParserMap_Default.ParserMap_ExitStart("ParserMap.ExitStart", 20);
        public static final ParserMap_Default.ParserMap_ExitEnd ExitEnd =
            new ParserMap_Default.ParserMap_ExitEnd("ParserMap.ExitEnd", 21);
        public static final ParserMap_Default.ParserMap_Transitions Transitions =
            new ParserMap_Default.ParserMap_Transitions("ParserMap.Transitions", 22);
        public static final ParserMap_Default.ParserMap_TransError TransError =
            new ParserMap_Default.ParserMap_TransError("ParserMap.TransError", 23);
        public static final ParserMap_Default.ParserMap_TransStart TransStart =
            new ParserMap_Default.ParserMap_TransStart("ParserMap.TransStart", 24);
        public static final ParserMap_Default.ParserMap_TransStartError TransStartError =
            new ParserMap_Default.ParserMap_TransStartError("ParserMap.TransStartError", 25);
        public static final ParserMap_Default.ParserMap_TransParams TransParams =
            new ParserMap_Default.ParserMap_TransParams("ParserMap.TransParams", 26);
        public static final ParserMap_Default.ParserMap_TransNext TransNext =
            new ParserMap_Default.ParserMap_TransNext("ParserMap.TransNext", 27);
        public static final ParserMap_Default.ParserMap_TransNextError TransNextError =
            new ParserMap_Default.ParserMap_TransNextError("ParserMap.TransNextError", 28);
        public static final ParserMap_Default.ParserMap_TransGuard TransGuard =
            new ParserMap_Default.ParserMap_TransGuard("ParserMap.TransGuard", 29);
        public static final ParserMap_Default.ParserMap_EndState EndState =
            new ParserMap_Default.ParserMap_EndState("ParserMap.EndState", 30);
        public static final ParserMap_Default.ParserMap_EndStateError EndStateError =
            new ParserMap_Default.ParserMap_EndStateError("ParserMap.EndStateError", 31);
        public static final ParserMap_Default.ParserMap_SimpleTrans SimpleTrans =
            new ParserMap_Default.ParserMap_SimpleTrans("ParserMap.SimpleTrans", 32);
        public static final ParserMap_Default.ParserMap_PushTransition PushTransition =
            new ParserMap_Default.ParserMap_PushTransition("ParserMap.PushTransition", 33);
        public static final ParserMap_Default.ParserMap_PushStart PushStart =
            new ParserMap_Default.ParserMap_PushStart("ParserMap.PushStart", 34);
        public static final ParserMap_Default.ParserMap_PushError PushError =
            new ParserMap_Default.ParserMap_PushError("ParserMap.PushError", 35);
        public static final ParserMap_Default.ParserMap_PushMap PushMap =
            new ParserMap_Default.ParserMap_PushMap("ParserMap.PushMap", 36);
        public static final ParserMap_Default.ParserMap_PushEnd PushEnd =
            new ParserMap_Default.ParserMap_PushEnd("ParserMap.PushEnd", 37);
        public static final ParserMap_Default.ParserMap_JumpStart JumpStart =
            new ParserMap_Default.ParserMap_JumpStart("ParserMap.JumpStart", 38);
        public static final ParserMap_Default.ParserMap_JumpError JumpError =
            new ParserMap_Default.ParserMap_JumpError("ParserMap.JumpError", 39);
        public static final ParserMap_Default.ParserMap_JumpMap JumpMap =
            new ParserMap_Default.ParserMap_JumpMap("ParserMap.JumpMap", 40);
        public static final ParserMap_Default.ParserMap_JumpEnd JumpEnd =
            new ParserMap_Default.ParserMap_JumpEnd("ParserMap.JumpEnd", 41);
        public static final ParserMap_Default.ParserMap_PopStart PopStart =
            new ParserMap_Default.ParserMap_PopStart("ParserMap.PopStart", 42);
        public static final ParserMap_Default.ParserMap_PopError PopError =
            new ParserMap_Default.ParserMap_PopError("ParserMap.PopError", 43);
        public static final ParserMap_Default.ParserMap_PopAction PopAction =
            new ParserMap_Default.ParserMap_PopAction("ParserMap.PopAction", 44);
        public static final ParserMap_Default.ParserMap_PopArgs PopArgs =
            new ParserMap_Default.ParserMap_PopArgs("ParserMap.PopArgs", 45);
        public static final ParserMap_Default.ParserMap_PopArgsEnd PopArgsEnd =
            new ParserMap_Default.ParserMap_PopArgsEnd("ParserMap.PopArgsEnd", 46);
        public static final ParserMap_Default.ParserMap_ActionStart ActionStart =
            new ParserMap_Default.ParserMap_ActionStart("ParserMap.ActionStart", 47);
        public static final ParserMap_Default.ParserMap_ActionEnd ActionEnd =
            new ParserMap_Default.ParserMap_ActionEnd("ParserMap.ActionEnd", 48);
        public static final ParserMap_Default.ParserMap_ActionStartError ActionStartError =
            new ParserMap_Default.ParserMap_ActionStartError("ParserMap.ActionStartError", 49);
        private static final ParserMap_Default Default =
            new ParserMap_Default("ParserMap.Default", -1);

    }

    protected static class ParserMap_Default
        extends SmcParserState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ParserMap_Default(String name, int id)
        {
            super (name, id);
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class ParserMap_Start
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void ACCESS(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.ACCESS(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Access);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void CLASS_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.CLASS_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Context);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void DECLARE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.DECLARE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Declare);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting \"%{ source %}\", %start, or %class.", ctxt.getLineNumber());
                    ctxt.setHeaderLine(ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void HEADER_FILE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.HEADER_FILE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.HeaderFile);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void IMPORT(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.IMPORT(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Import);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void INCLUDE_FILE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.INCLUDE_FILE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.IncludeFile);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.MAP_NAME(SmcLexer.Token token)");
                }

                if (ctxt.isValidHeader() == true)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setHeaderLine(token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.MapName);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("%map not preceded by %start and %class.", token.getLineNumber());
                        ctxt.setHeaderLine(token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.StartError);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

            protected void PACKAGE_NAME(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.PACKAGE_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Package);
                (context.getState()).Entry(context);
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.SOURCE(SmcLexer.Token token)");
                }

                SmcParserState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.setSource(token.getValue());
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void START_STATE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Start.START_STATE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartState);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Context
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Context(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Context.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing name after %class.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Context.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setContext(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_StartState
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_StartState(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartState.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing state after %start.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartState.WORD(SmcLexer.Token token)");
                }

                if (ctxt.isValidStartState(token.getValue()) == true)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setStartState(token.getValue());
                    }
                    finally
                    {
                        context.setState(ParserMap.Start);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Start state must be of the form \"map::state\".", token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.StartError);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_HeaderFile
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_HeaderFile(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("\n\r\f");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.HeaderFile.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing header file after %header.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.HeaderFile.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeader(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_IncludeFile
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_IncludeFile(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("\n\r\f");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.IncludeFile.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing include file after %include.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.IncludeFile.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addInclude(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Package
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Package(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Package.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing name after %package.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Package.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setPackageName(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Import
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Import(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("\n\r\f");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Import.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing name after %import.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Import.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addImport(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Declare
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Declare(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("\n\r");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Declare.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing name after %declare.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Declare.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addDeclare(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Access
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Access(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("\n\r\f");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Access.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing access level after %access.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Access.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setAccessLevel(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_StartError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_StartError(String name, int id)
            {
                super (name, id);
            }

            protected void ACCESS(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.ACCESS(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Access);
                (context.getState()).Entry(context);
                return;
            }

            protected void CLASS_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.CLASS_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Context);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void DECLARE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.DECLARE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Declare);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.Default()");
                }


                return;
            }

            protected void HEADER_FILE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.HEADER_FILE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.HeaderFile);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void IMPORT(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.IMPORT(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Import);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.MAP_NAME(SmcLexer.Token token)");
                }

                if (ctxt.isValidHeader() == true)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setHeaderLine(token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.MapName);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    SmcParserState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.error("%map not preceded by %start and %class.", token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }

                return;
            }

            protected void PACKAGE_NAME(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.PACKAGE_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Package);
                (context.getState()).Entry(context);
                return;
            }

            protected void START_STATE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StartError.START_STATE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setHeaderLine(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StartState);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_MapStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_MapStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting %map.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.MapStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStart.MAP_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.MapName);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_MapStartError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_MapStartError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStartError.Default()");
                }


                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStartError.MAP_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.MapName);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_MapName
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_MapName(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapName.Default()");
                }

                SmcParserState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.error("Name expected after \"%map\".", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void EOD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapName.EOD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Name expected after \"%map\".", token.getLineNumber());
                    ctxt.createMap(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.States);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapName.WORD(SmcLexer.Token token)");
                }

                if (ctxt.isDuplicateMap(token.getValue()) == true)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Duplicate map name.", token.getLineNumber());
                        ctxt.createMap(token.getValue(), token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.MapStates);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.createMap(token.getValue(), token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.MapStates);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_MapStates
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_MapStates(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStates.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting %% after \"%map mapname\".", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.MapStatesError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EOD(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStates.EOD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.States);
                (context.getState()).Entry(context);
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStates.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting %% after \"%map mapname\".", token.getLineNumber());
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StateStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_MapStatesError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_MapStatesError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStatesError.Default()");
                }


                return;
            }

            protected void EOD(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStatesError.EOD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.States);
                (context.getState()).Entry(context);
                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStatesError.MAP_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addMap();
                }
                finally
                {
                    context.setState(ParserMap.MapName);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.MapStatesError.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StateStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_States
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_States(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.Default()");
                }

                SmcParserState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new state definition or end of map (%%).", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void ENTRY(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.ENTRY(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new state definition or end of map (%%).", token.getLineNumber());
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.EntryStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EOD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.EOD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addMap();
                }
                finally
                {
                    context.setState(ParserMap.MapStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EQUAL(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.EQUAL(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new state definition or end of map (%%).", token.getLineNumber());
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Transitions);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EXIT(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.EXIT(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new state definition or end of map (%%).", token.getLineNumber());
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.ExitStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new state definition or end of map (%%).", token.getLineNumber());
                    ctxt.createState(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.Transitions);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void MAP_NAME(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.MAP_NAME(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting \"%%\" before another \"%map\".", token.getLineNumber());
                    ctxt.addMap();
                }
                finally
                {
                    context.setState(ParserMap.MapName);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.States.WORD(SmcLexer.Token token)");
                }

                if (ctxt.isDuplicateState(token.getValue()) == true)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Duplicate state name.", token.getLineNumber());
                        ctxt.createState(token.getValue(), token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.StateStart);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.createState(token.getValue(), token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParserMap.StateStart);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_StateStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_StateStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("After the state name is given, then either an entry action, exit action or '{' is expected.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StateStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void ENTRY(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStart.ENTRY(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.EntryStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void EXIT(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStart.EXIT(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ExitStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStart.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Transitions);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_StateStartError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_StateStartError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStartError.Default()");
                }


                return;
            }

            protected void ENTRY(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStartError.ENTRY(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.EntryStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void EXIT(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStartError.EXIT(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ExitStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.StateStartError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Transitions);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_EntryStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_EntryStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EntryStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("A '{' is expected after Entry.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StateStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EntryStart.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.EntryEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_EntryEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_EntryEnd(String name, int id)
            {
                super (name, id);
            }

            protected void actionsDone(SmcParserContext context, List<SmcAction> actions, int lineNumber)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EntryEnd.actionsDone(List<SmcAction> actions, int lineNumber)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setEntryAction(actions);
                }
                finally
                {
                    context.setState(ParserMap.StateStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void actionsError(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EntryEnd.actionsError()");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.StartState);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_ExitStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_ExitStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ExitStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("A '{' is expected after Exit.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.StateStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ExitStart.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ExitEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_ExitEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_ExitEnd(String name, int id)
            {
                super (name, id);
            }

            protected void actionsDone(SmcParserContext context, List<SmcAction> actions, int lineNumber)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ExitEnd.actionsDone(List<SmcAction> actions, int lineNumber)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setExitAction(actions);
                }
                finally
                {
                    context.setState(ParserMap.StateStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void actionsError(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ExitEnd.actionsError()");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.StateStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_Transitions
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_Transitions(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Transitions.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a new transition or a '}'.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.TransError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Transitions.RIGHT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addState();
                }
                finally
                {
                    context.setState(ParserMap.States);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.Transitions.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.storeTransitionName(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.TransStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransError.Default()");
                }


                return;
            }

            protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransError.RIGHT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addState();
                }
                finally
                {
                    context.setState(ParserMap.States);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransError.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.storeTransitionName(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.TransStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a guard, \"push\", \"pop\", \"jump\" or end state.", ctxt.getLineNumber());
                    ctxt.createTransition(ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.TransStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void JUMP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.JUMP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(token.getLineNumber());
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                }
                finally
                {
                    context.setState(ParserMap.JumpStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACKET(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.LEFT_BRACKET(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.TransGuard);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.TransParams);
                (context.getState()).Entry(context);
                context.pushState(ParamMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void POP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.POP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(token.getLineNumber());
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_POP);
                }
                finally
                {
                    context.setState(ParserMap.PopStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(token.getLineNumber());
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_PUSH);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.PushStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStart.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(token.getLineNumber());
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.SimpleTrans);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransStartError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransStartError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.Default()");
                }


                return;
            }

            protected void JUMP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.JUMP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.JumpStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState("End state missing");
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACKET(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.LEFT_BRACKET(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.TransGuard);
                (context.getState()).Entry(context);
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.TransParams);
                (context.getState()).Entry(context);
                context.pushState(ParamMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void POP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.POP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_POP);
                }
                finally
                {
                    context.setState(ParserMap.PopStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_PUSH);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.PushStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransStartError.RIGHT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addGuard();
                    ctxt.addTransition();
                }
                finally
                {
                    context.setState(ParserMap.Transitions);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransParams
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransParams(String name, int id)
            {
                super (name, id);
            }

            protected void paramsDone(SmcParserContext context, List<SmcParameter> params, int lineNumber)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransParams.paramsDone(List<SmcParameter> params, int lineNumber)");
                }

                if (ctxt.getTransitionName().equalsIgnoreCase(
           "Default") == true &&
       params.isEmpty() == false)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Default transitions may not have parameters.", lineNumber);
                        ctxt.createTransition(params, lineNumber);
                    }
                    finally
                    {
                        context.setState(ParserMap.TransNext);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.createTransition(params, lineNumber);
                    }
                    finally
                    {
                        context.setState(ParserMap.TransNext);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

            protected void paramsError(SmcParserContext context, List<SmcParameter> params, int lineNumber)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransParams.paramsError(List<SmcParameter> params, int lineNumber)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createTransition(params, lineNumber);
                }
                finally
                {
                    context.setState(ParserMap.TransNext);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransNext
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransNext(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a guard, \"push\", \"pop\", \"jump\" or end state.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.TransNextError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void JUMP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.JUMP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.JumpStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACKET(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.LEFT_BRACKET(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.TransGuard);
                (context.getState()).Entry(context);
                return;
            }

            protected void POP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.POP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_POP);
                }
                finally
                {
                    context.setState(ParserMap.PopStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_PUSH);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.PushStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNext.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.SimpleTrans);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransNextError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransNextError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.Default()");
                }


                return;
            }

            protected void JUMP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.JUMP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.JumpStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACKET(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.LEFT_BRACKET(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.TransGuard);
                (context.getState()).Entry(context);
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.LEFT_PAREN(SmcLexer.Token token)");
                }


                return;
            }

            protected void POP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.POP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_POP);
                }
                finally
                {
                    context.setState(ParserMap.PopStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_PUSH);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.PushStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransNextError.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), "", token.getLineNumber());
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.SimpleTrans);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_TransGuard
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_TransGuard(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("[", "]");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.TransGuard.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createGuard(ctxt.getTransitionName(), token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.EndState);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_EndState
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_EndState(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndState.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either \"push\", \"pop\", \"jump\" or end state.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.EndStateError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void JUMP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndState.JUMP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.JumpStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void POP(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndState.POP(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setTransType(TransType.TRANS_POP);
                }
                finally
                {
                    context.setState(ParserMap.PopStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndState.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setTransType(TransType.TRANS_PUSH);
                    ctxt.setEndState("nil");
                }
                finally
                {
                    context.setState(ParserMap.PushStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndState.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setTransType(TransType.TRANS_SET);
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.SimpleTrans);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_EndStateError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_EndStateError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndStateError.Default()");
                }


                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.EndStateError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_SimpleTrans
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_SimpleTrans(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.SimpleTrans.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("A '{' must proceed any action definitions.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.ActionStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.SimpleTrans.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SLASH(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.SimpleTrans.SLASH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setTransType(TransType.TRANS_PUSH);
                }
                finally
                {
                    context.setState(ParserMap.PushTransition);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PushTransition
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PushTransition(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushTransition.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("\"push\" must follow a '/'.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PushError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PUSH(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushTransition.PUSH(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.PushStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PushStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PushStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("\"push\" must be followed by a '/'.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PushError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushStart.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.PushMap);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PushError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PushError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushError.Default()");
                }


                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushError.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PushMap
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PushMap(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushMap.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting a state name.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PushError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushMap.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setPushState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.PushEnd);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PushEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PushEnd(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushEnd.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("push transition missing closing paren.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PushError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PushEnd.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_JumpStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_JumpStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("\"jump\" must be followed by a '/'.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.JumpError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpStart.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.JumpMap);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_JumpError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_JumpError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpError.Default()");
                }


                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpError.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_JumpMap
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_JumpMap(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpMap.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting a state name.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.JumpError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpMap.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.JumpEnd);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_JumpEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_JumpEnd(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpEnd.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("jump transition missing closing paren.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.JumpError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.JumpEnd.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PopStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PopStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting '(trans)' or '{' after pop.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PopError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopStart.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopStart.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.PopAction);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PopError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PopError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopError.Default()");
                }


                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopError.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PopAction
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PopAction(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopAction.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting either a pop transition or closing paren.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PopError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopAction.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopAction.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setEndState(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.PopArgs);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PopArgs
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PopArgs(String name, int id)
            {
                super (name, id);
            }

            protected void COMMA(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopArgs.COMMA(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.PopArgsEnd);
                (context.getState()).Entry(context);
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopArgs.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Pop transition missing closing paren.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.PopError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopArgs.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.ActionStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_PopArgsEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_PopArgsEnd(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("(", ")");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.PopArgsEnd.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setPopArgs(token.getValue());
                }
                finally
                {
                    context.setState(ParserMap.ActionStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_ActionStart
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_ActionStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("A '{' must proceed any action definitions.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParserMap.ActionStartError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionStart.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_ActionEnd
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_ActionEnd(String name, int id)
            {
                super (name, id);
            }

            protected void actionsDone(SmcParserContext context, List<SmcAction> actions, int lineNumber)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionEnd.actionsDone(List<SmcAction> actions, int lineNumber)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setActions(actions);
                    ctxt.addGuard();
                    ctxt.addTransition();
                }
                finally
                {
                    context.setState(ParserMap.Transitions);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void actionsError(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionEnd.actionsError()");
                }


                (context.getState()).Exit(context);
                context.setState(ParserMap.Transitions);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParserMap_ActionStartError
            extends ParserMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParserMap_ActionStartError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionStartError.Default()");
                }


                return;
            }

            protected void LEFT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParserMap.ActionStartError.LEFT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createActionList();
                }
                finally
                {
                    context.setState(ParserMap.ActionEnd);
                    (context.getState()).Entry(context);
                    context.pushState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class ParamMap
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final ParamMap_Default.ParamMap_Start Start =
            new ParamMap_Default.ParamMap_Start("ParamMap.Start", 50);
        public static final ParamMap_Default.ParamMap_Dollar Dollar =
            new ParamMap_Default.ParamMap_Dollar("ParamMap.Dollar", 51);
        public static final ParamMap_Default.ParamMap_ParamSeparator ParamSeparator =
            new ParamMap_Default.ParamMap_ParamSeparator("ParamMap.ParamSeparator", 52);
        public static final ParamMap_Default.ParamMap_ParamType ParamType =
            new ParamMap_Default.ParamMap_ParamType("ParamMap.ParamType", 53);
        public static final ParamMap_Default.ParamMap_NextParam NextParam =
            new ParamMap_Default.ParamMap_NextParam("ParamMap.NextParam", 54);
        public static final ParamMap_Default.ParamMap_Error Error =
            new ParamMap_Default.ParamMap_Error("ParamMap.Error", 55);
        private static final ParamMap_Default Default =
            new ParamMap_Default("ParamMap.Default", -1);

    }

    protected static class ParamMap_Default
        extends SmcParserState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ParamMap_Default(String name, int id)
        {
            super (name, id);
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class ParamMap_Start
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.createParamList();
                return;
            }

            protected void DOLLAR(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Start.DOLLAR(SmcLexer.Token token)");
                }

                if (ctxt.getTargetLanguage() == TargetLanguage.PERL ||
       ctxt.getTargetLanguage() == TargetLanguage.PHP ||
       ctxt.getTargetLanguage() == TargetLanguage.GRAPH ||
       ctxt.getTargetLanguage() == TargetLanguage.TABLE)
                {

                    (context.getState()).Exit(context);
                    // No actions.
                    context.setState(ParamMap.Dollar);
                    (context.getState()).Entry(context);
                }
                else
                {
                    super.DOLLAR(context, token);
                }

                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Start.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Invalid parameter syntax.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.Error);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Start.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.paramsDone(
            ctxt.getParamList(),
            token.getLineNumber());
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Start.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createParameter(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.ParamSeparator);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParamMap_Dollar
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_Dollar(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Dollar.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Invalid parameter syntax.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.Error);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Dollar.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createParameter("$" + token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.ParamSeparator);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParamMap_ParamSeparator
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_ParamSeparator(String name, int id)
            {
                super (name, id);
            }

            protected void COLON(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.ParamSeparator.COLON(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.setState(ParamMap.ParamType);
                (context.getState()).Entry(context);
                return;
            }

            protected void COMMA(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.ParamSeparator.COMMA(SmcLexer.Token token)");
                }

                if (ctxt.getTargetLanguage() == TargetLanguage.TCL ||
       ctxt.getTargetLanguage() == TargetLanguage.GROOVY ||
       ctxt.getTargetLanguage() == TargetLanguage.LUA ||
       ctxt.getTargetLanguage() == TargetLanguage.PERL ||
       ctxt.getTargetLanguage() == TargetLanguage.PHP ||
       ctxt.getTargetLanguage() == TargetLanguage.PYTHON ||
       ctxt.getTargetLanguage() == TargetLanguage.RUBY ||
       ctxt.getTargetLanguage() == TargetLanguage.GRAPH ||
       ctxt.getTargetLanguage() == TargetLanguage.TABLE)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addParameter();
                    }
                    finally
                    {
                        context.setState(ParamMap.Start);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Parameter type missing.", token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ParamMap.Error);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.ParamSeparator.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Invalid parameter syntax.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.Error);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.ParamSeparator.RIGHT_PAREN(SmcLexer.Token token)");
                }

                if (ctxt.getTargetLanguage() == TargetLanguage.TCL ||
       ctxt.getTargetLanguage() == TargetLanguage.GROOVY ||
       ctxt.getTargetLanguage() == TargetLanguage.LUA ||
       ctxt.getTargetLanguage() == TargetLanguage.PERL ||
       ctxt.getTargetLanguage() == TargetLanguage.PHP ||
       ctxt.getTargetLanguage() == TargetLanguage.PYTHON ||
       ctxt.getTargetLanguage() == TargetLanguage.RUBY ||
       ctxt.getTargetLanguage() == TargetLanguage.GRAPH ||
       ctxt.getTargetLanguage() == TargetLanguage.TABLE)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.addParameter();
                    }
                    finally
                    {
                        context.popState();
                    }

                    context.paramsDone(
            ctxt.getParamList(),
            token.getLineNumber());
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.error("Parameter type missing.", token.getLineNumber());
                    }
                    finally
                    {
                        context.popState();
                    }

                    context.paramsError(
            ctxt.getParamList(),
            token.getLineNumber());
                }

                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParamMap_ParamType
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_ParamType(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("(", ")", ", ");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.ParamType.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setParamType(token.getValue());
                }
                finally
                {
                    context.setState(ParamMap.NextParam);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParamMap_NextParam
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_NextParam(String name, int id)
            {
                super (name, id);
            }

            protected void COMMA(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.NextParam.COMMA(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addParameter();
                }
                finally
                {
                    context.setState(ParamMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.NextParam.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Invalid parameter syntax.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ParamMap.Error);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.NextParam.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addParameter();
                }
                finally
                {
                    context.popState();
                }

                context.paramsDone(
            ctxt.getParamList(),
            token.getLineNumber());
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ParamMap_Error
            extends ParamMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ParamMap_Error(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("(", ")");
                ctxt.clearParameter();
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ParamMap.Error.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.paramsError(
            ctxt.getParamList(),
            token.getLineNumber());
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class ActionMap
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final ActionMap_Default.ActionMap_Start Start =
            new ActionMap_Default.ActionMap_Start("ActionMap.Start", 56);
        public static final ActionMap_Default.ActionMap_ActionName ActionName =
            new ActionMap_Default.ActionMap_ActionName("ActionMap.ActionName", 57);
        public static final ActionMap_Default.ActionMap_ActionArgs ActionArgs =
            new ActionMap_Default.ActionMap_ActionArgs("ActionMap.ActionArgs", 58);
        public static final ActionMap_Default.ActionMap_ActionEnd ActionEnd =
            new ActionMap_Default.ActionMap_ActionEnd("ActionMap.ActionEnd", 59);
        public static final ActionMap_Default.ActionMap_PropertyAssignment PropertyAssignment =
            new ActionMap_Default.ActionMap_PropertyAssignment("ActionMap.PropertyAssignment", 60);
        public static final ActionMap_Default.ActionMap_ActionError ActionError =
            new ActionMap_Default.ActionMap_ActionError("ActionMap.ActionError", 61);
        private static final ActionMap_Default Default =
            new ActionMap_Default("ActionMap.Default", -1);

    }

    protected static class ActionMap_Default
        extends SmcParserState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ActionMap_Default(String name, int id)
        {
            super (name, id);
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class ActionMap_Start
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.Start.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.clearActions();
                    ctxt.error("Expecting either a method name or a '}'", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ActionMap.ActionError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.Start.RIGHT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.actionsDone(
			ctxt.getActionList(),
			token.getLineNumber());
                return;
            }

            protected void WORD(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.Start.WORD(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createAction(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ActionMap.ActionName);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ActionMap_ActionName
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_ActionName(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionName.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.clearActions();
                    ctxt.error("Expecting an open paren after the method name", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ActionMap.ActionError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EQUAL(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionName.EQUAL(SmcLexer.Token token)");
                }

                if (ctxt.getTargetLanguage() != TargetLanguage.VB &&
       ctxt.getTargetLanguage() != TargetLanguage.GRAPH &&
       ctxt.getTargetLanguage() != TargetLanguage.C_SHARP)
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.clearActions();
                        ctxt.error("'=' property assignment may only be used with -vb, -csharp or -graph.", token.getLineNumber());
                    }
                    finally
                    {
                        context.setState(ActionMap.ActionError);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {

                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.setProperty(true);
                        ctxt.createArgList();
                    }
                    finally
                    {
                        context.setState(ActionMap.PropertyAssignment);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }

            protected void LEFT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionName.LEFT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createArgList();
                }
                finally
                {
                    context.setState(ActionMap.ActionArgs);
                    (context.getState()).Entry(context);
                    context.pushState(ArgsMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ActionMap_ActionArgs
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_ActionArgs(String name, int id)
            {
                super (name, id);
            }

            protected void argsDone(SmcParserContext context, List<String> args)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionArgs.argsDone(List<String> args)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setActionArgs(args);
                }
                finally
                {
                    context.setState(ActionMap.ActionEnd);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void argsError(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionArgs.argsError()");
                }


                (context.getState()).Exit(context);
                context.setState(ActionMap.ActionError);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ActionMap_ActionEnd
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_ActionEnd(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionEnd.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Expecting a ';' after closing paren", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ActionMap.ActionError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SEMICOLON(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionEnd.SEMICOLON(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addAction();
                }
                finally
                {
                    context.setState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ActionMap_PropertyAssignment
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_PropertyAssignment(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode(";");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.PropertyAssignment.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing ';' at end of property assignment", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ActionMap.ActionError);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.PropertyAssignment.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createArgument(token.getValue(), token.getLineNumber());
                    ctxt.addArgument();
                    ctxt.setActionArgs(ctxt.getArgsList());
                    ctxt.addAction();
                }
                finally
                {
                    context.setState(ActionMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ActionMap_ActionError
            extends ActionMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ActionMap_ActionError(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcParserContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionError.Default()");
                }


                return;
            }

            protected void RIGHT_BRACE(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ActionMap.ActionError.RIGHT_BRACE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.actionsError();
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class ArgsMap
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final ArgsMap_Default.ArgsMap_Start Start =
            new ArgsMap_Default.ArgsMap_Start("ArgsMap.Start", 62);
        public static final ArgsMap_Default.ArgsMap_NextArg NextArg =
            new ArgsMap_Default.ArgsMap_NextArg("ArgsMap.NextArg", 63);
        public static final ArgsMap_Default.ArgsMap_Error Error =
            new ArgsMap_Default.ArgsMap_Error("ArgsMap.Error", 64);
        private static final ArgsMap_Default Default =
            new ArgsMap_Default("ArgsMap.Default", -1);

    }

    protected static class ArgsMap_Default
        extends SmcParserState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected ArgsMap_Default(String name, int id)
        {
            super (name, id);
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class ArgsMap_Start
            extends ArgsMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ArgsMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("(", ")", ", ");
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ArgsMap.Start.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.createArgument(token.getValue(), token.getLineNumber());
                }
                finally
                {
                    context.setState(ArgsMap.NextArg);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ArgsMap_NextArg
            extends ArgsMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ArgsMap_NextArg(String name, int id)
            {
                super (name, id);
            }

            protected void COMMA(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ArgsMap.NextArg.COMMA(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addArgument();
                }
                finally
                {
                    context.setState(ArgsMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Default(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ArgsMap.NextArg.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.error("Missing ', ' or closing paren after argument.", ctxt.getLineNumber());
                }
                finally
                {
                    context.setState(ArgsMap.Error);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void RIGHT_PAREN(SmcParserContext context, SmcLexer.Token token)
            {
                SmcParser ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ArgsMap.NextArg.RIGHT_PAREN(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addArgument();
                }
                finally
                {
                    context.popState();
                }

                context.argsDone( ctxt.getArgsList());
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class ArgsMap_Error
            extends ArgsMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private ArgsMap_Error(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setRawMode("{", "}");
                ctxt.clearArguments();
                return;
            }

            protected void Exit(SmcParserContext context)
            {
                SmcParser ctxt = context.getOwner();

                ctxt.setCookedMode();
                return;
            }

            protected void SOURCE(SmcParserContext context, SmcLexer.Token token)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : ArgsMap.Error.SOURCE(SmcLexer.Token token)");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.argsError();
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
