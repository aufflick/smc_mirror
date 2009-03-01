
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
 * Copyright (C) 2000 - 2006, 2008. Charles W. Rapp.
 * All Rights Reserved.
 * 
 * Contributor(s):
 *   Eitan Suez contributed examples/Ant.
 *   (Name withheld) contributed the C# code generation and
 *   examples/C#.
 *   Francois Perrad contributed the Python code generation and
 *   examples/Python.
 *   Chris Liscio contributed the Objective-C code generation
 *   and examples/ObjC.
 *
 * smcLexer --
 *
 *  This statemap finds tokens in a statemap source file.
 *
 * RCS ID
 * $Id$
 *
 * CHANGE LOG
 * $Log$
 * Revision 1.1  2009/03/01 18:20:42  cwrapp
 * Preliminary v. 6.0.0 commit.
 *
 * Revision 1.9  2007/12/28 12:34:41  cwrapp
 * Version 5.0.1 check-in.
 *
 * Revision 1.8  2007/01/15 00:23:51  cwrapp
 * Release 4.4.0 initial commit.
 *
 * Revision 1.7  2006/09/16 15:04:29  cwrapp
 * Initial v. 4.3.3 check-in.
 *
 * Revision 1.6  2005/07/07 12:10:41  fperrad
 * Add a new token '$' for Perl language.
 *
 * Revision 1.5  2005/05/28 19:28:42  cwrapp
 * Moved to visitor pattern.
 *
 * Revision 1.5  2005/02/21 18:14:01  charlesr
 * Removed unknown FSM transition, using unicode transition
 * instead.
 *
 * Revision 1.4  2005/02/21 15:36:08  charlesr
 * Added Francois Perrad to Contributors section for Python work.
 *
 * Revision 1.3  2004/10/30 16:05:34  charlesr
 * Added unicode support.
 *
 * Revision 1.2  2004/09/06 16:40:22  charlesr
 * Added C# support.
 *
 * Revision 1.1  2004/01/29 02:14:49  charlesr
 * Add Default state containing single transition which
 * handles an unexpected asterisk.
 *
 * Revision 1.0  2003/12/14 21:04:00  charlesr
 * Initial revision
 *
 */


package net.sf.smc.parser;

import java.io.PrintStream;

public final class SmcLexerContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public SmcLexerContext(SmcLexer owner)
    {
        super (TokenMap.Start);

        _owner = owner;
    }

    public SmcLexerContext(SmcLexer owner, SmcLexerState initState)
    {
        super (initState);

        _owner = owner;
    }

    public void enterStartState()
    {
        getState().Entry(this);
        return;
    }

    public void EOL()
    {
        _transition = "EOL";
        getState().EOL(this);
        _transition = "";
        return;
    }

    public void alpha()
    {
        _transition = "alpha";
        getState().alpha(this);
        _transition = "";
        return;
    }

    public void asterisk()
    {
        _transition = "asterisk";
        getState().asterisk(this);
        _transition = "";
        return;
    }

    public void colon()
    {
        _transition = "colon";
        getState().colon(this);
        _transition = "";
        return;
    }

    public void comma()
    {
        _transition = "comma";
        getState().comma(this);
        _transition = "";
        return;
    }

    public void commentDone()
    {
        _transition = "commentDone";
        getState().commentDone(this);
        _transition = "";
        return;
    }

    public void digit()
    {
        _transition = "digit";
        getState().digit(this);
        _transition = "";
        return;
    }

    public void dollar()
    {
        _transition = "dollar";
        getState().dollar(this);
        _transition = "";
        return;
    }

    public void equal()
    {
        _transition = "equal";
        getState().equal(this);
        _transition = "";
        return;
    }

    public void left_brace()
    {
        _transition = "left_brace";
        getState().left_brace(this);
        _transition = "";
        return;
    }

    public void left_bracket()
    {
        _transition = "left_bracket";
        getState().left_bracket(this);
        _transition = "";
        return;
    }

    public void left_paren()
    {
        _transition = "left_paren";
        getState().left_paren(this);
        _transition = "";
        return;
    }

    public void percent()
    {
        _transition = "percent";
        getState().percent(this);
        _transition = "";
        return;
    }

    public void period()
    {
        _transition = "period";
        getState().period(this);
        _transition = "";
        return;
    }

    public void right_brace()
    {
        _transition = "right_brace";
        getState().right_brace(this);
        _transition = "";
        return;
    }

    public void right_paren()
    {
        _transition = "right_paren";
        getState().right_paren(this);
        _transition = "";
        return;
    }

    public void semicolon()
    {
        _transition = "semicolon";
        getState().semicolon(this);
        _transition = "";
        return;
    }

    public void slash()
    {
        _transition = "slash";
        getState().slash(this);
        _transition = "";
        return;
    }

    public void sourceDone()
    {
        _transition = "sourceDone";
        getState().sourceDone(this);
        _transition = "";
        return;
    }

    public void underscore()
    {
        _transition = "underscore";
        getState().underscore(this);
        _transition = "";
        return;
    }

    public void unicode()
    {
        _transition = "unicode";
        getState().unicode(this);
        _transition = "";
        return;
    }

    public void whitespace()
    {
        _transition = "whitespace";
        getState().whitespace(this);
        _transition = "";
        return;
    }

    public SmcLexerState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((SmcLexerState) _state);
    }

    protected SmcLexer getOwner()
    {
        return (_owner);
    }

    public void setOwner(SmcLexer owner)
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

    transient private SmcLexer _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class SmcLexerState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SmcLexerState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(SmcLexerContext context) {}
        protected void Exit(SmcLexerContext context) {}

        protected void EOL(SmcLexerContext context)
        {
            Default(context);
        }

        protected void alpha(SmcLexerContext context)
        {
            Default(context);
        }

        protected void asterisk(SmcLexerContext context)
        {
            Default(context);
        }

        protected void colon(SmcLexerContext context)
        {
            Default(context);
        }

        protected void comma(SmcLexerContext context)
        {
            Default(context);
        }

        protected void commentDone(SmcLexerContext context)
        {
            Default(context);
        }

        protected void digit(SmcLexerContext context)
        {
            Default(context);
        }

        protected void dollar(SmcLexerContext context)
        {
            Default(context);
        }

        protected void equal(SmcLexerContext context)
        {
            Default(context);
        }

        protected void left_brace(SmcLexerContext context)
        {
            Default(context);
        }

        protected void left_bracket(SmcLexerContext context)
        {
            Default(context);
        }

        protected void left_paren(SmcLexerContext context)
        {
            Default(context);
        }

        protected void percent(SmcLexerContext context)
        {
            Default(context);
        }

        protected void period(SmcLexerContext context)
        {
            Default(context);
        }

        protected void right_brace(SmcLexerContext context)
        {
            Default(context);
        }

        protected void right_paren(SmcLexerContext context)
        {
            Default(context);
        }

        protected void semicolon(SmcLexerContext context)
        {
            Default(context);
        }

        protected void slash(SmcLexerContext context)
        {
            Default(context);
        }

        protected void sourceDone(SmcLexerContext context)
        {
            Default(context);
        }

        protected void underscore(SmcLexerContext context)
        {
            Default(context);
        }

        protected void unicode(SmcLexerContext context)
        {
            Default(context);
        }

        protected void whitespace(SmcLexerContext context)
        {
            Default(context);
        }

        protected void Default(SmcLexerContext context)
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

    /* package */ static abstract class TokenMap
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
        public static final TokenMap_Default.TokenMap_Start Start =
            new TokenMap_Default.TokenMap_Start("TokenMap.Start", 0);
        public static final TokenMap_Default.TokenMap_CommentStart CommentStart =
            new TokenMap_Default.TokenMap_CommentStart("TokenMap.CommentStart", 1);
        public static final TokenMap_Default.TokenMap_PercentStart PercentStart =
            new TokenMap_Default.TokenMap_PercentStart("TokenMap.PercentStart", 2);
        public static final TokenMap_Default.TokenMap_PercentKeyword PercentKeyword =
            new TokenMap_Default.TokenMap_PercentKeyword("TokenMap.PercentKeyword", 3);
        public static final TokenMap_Default.TokenMap_Word Word =
            new TokenMap_Default.TokenMap_Word("TokenMap.Word", 4);
        public static final TokenMap_Default.TokenMap_Scope Scope =
            new TokenMap_Default.TokenMap_Scope("TokenMap.Scope", 5);
        public static final TokenMap_Default.TokenMap_Colon Colon =
            new TokenMap_Default.TokenMap_Colon("TokenMap.Colon", 6);
        private static final TokenMap_Default Default =
            new TokenMap_Default("TokenMap.Default", -1);

    }

    protected static class TokenMap_Default
        extends SmcLexerState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected TokenMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void asterisk(SmcLexerContext context)
        {
            SmcLexer ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : TokenMap.Default.asterisk()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    TokenMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.badToken("Unknown token");
            }
            finally
            {
                context.setState(TokenMap.Start);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class TokenMap_Start
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void EOL(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.EOL()");
                }


                return;
            }

            protected void alpha(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.alpha()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(TokenMap.Word);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void colon(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.colon()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(TokenMap.Colon);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void comma(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.comma()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.COMMA);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void dollar(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.dollar()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.DOLLAR);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void equal(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.equal()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.EQUAL);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void left_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.left_brace()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.LEFT_BRACE);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void left_bracket(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.left_bracket()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.LEFT_BRACKET);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void left_paren(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.left_paren()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.LEFT_PAREN);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void percent(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.percent()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(TokenMap.PercentStart);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void right_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.right_brace()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.RIGHT_BRACE);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void right_paren(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.right_paren()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.RIGHT_PAREN);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void semicolon(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.semicolon()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.SEMICOLON);
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.slash()");
                }


                (context.getState()).Exit(context);
                context.setState(TokenMap.CommentStart);
                (context.getState()).Entry(context);
                return;
            }

            protected void underscore(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.underscore()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(TokenMap.Word);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void unicode(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.unicode()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                    ctxt.addToToken();
                    ctxt.outputChar();
                    ctxt.badToken("Unknown character");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void whitespace(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Start.whitespace()");
                }


                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_CommentStart
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_CommentStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.CommentStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.endToken(SmcLexer.SLASH);
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void asterisk(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.CommentStart.asterisk()");
                }


                context.pushState(OldCommentMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void commentDone(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.CommentStart.commentDone()");
                }


                (context.getState()).Exit(context);
                context.setState(TokenMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.CommentStart.slash()");
                }


                context.pushState(NewCommentMap.Start);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_PercentStart
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_PercentStart(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken();
                    ctxt.badToken("Unknown % directive");
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void alpha(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.alpha()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(TokenMap.PercentKeyword);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void left_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.left_brace()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.startToken();
                }
                finally
                {
                    context.setState(endState);
                    context.pushState(SourceMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void percent(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.percent()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken();
                    ctxt.endToken(SmcLexer.EOD);
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void right_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.right_brace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken();
                    ctxt.badToken("End-of-source appears without matching start-of-source");
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void sourceDone(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentStart.sourceDone()");
                }


                (context.getState()).Exit(context);
                context.setState(TokenMap.Start);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_PercentKeyword
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_PercentKeyword(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentKeyword.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken();
                    ctxt.badToken("Unknown % directive");
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EOL(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentKeyword.EOL()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.checkPercentKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void alpha(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentKeyword.alpha()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void whitespace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.PercentKeyword.whitespace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.checkPercentKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_Word
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_Word(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.badToken("Unknown token");
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void EOL(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.EOL()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void alpha(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.alpha()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void asterisk(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.asterisk()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void colon(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.colon()");
                }


                (context.getState()).Exit(context);
                context.setState(TokenMap.Scope);
                (context.getState()).Entry(context);
                return;
            }

            protected void comma(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.comma()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void digit(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.digit()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void dollar(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.dollar()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void equal(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.equal()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void left_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.left_brace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void left_bracket(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.left_bracket()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void left_paren(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.left_paren()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void period(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.period()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void right_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.right_brace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void right_paren(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.right_paren()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void semicolon(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.semicolon()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void slash(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.slash()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void underscore(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.underscore()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void whitespace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Word.whitespace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_Scope
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_Scope(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Scope.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.ungetChar();
                    ctxt.checkKeyword();
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void colon(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Scope.colon()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken("::");
                }
                finally
                {
                    context.setState(TokenMap.Word);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class TokenMap_Colon
            extends TokenMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private TokenMap_Colon(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Colon.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.ungetChar();
                    ctxt.addToToken(":");
                    ctxt.endToken(SmcLexer.COLON);
                }
                finally
                {
                    context.setState(TokenMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void colon(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : TokenMap.Colon.colon()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken("::");
                }
                finally
                {
                    context.setState(TokenMap.Word);
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

    /* package */ static abstract class OldCommentMap
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
        public static final OldCommentMap_Default.OldCommentMap_Start Start =
            new OldCommentMap_Default.OldCommentMap_Start("OldCommentMap.Start", 7);
        public static final OldCommentMap_Default.OldCommentMap_CommentStart CommentStart =
            new OldCommentMap_Default.OldCommentMap_CommentStart("OldCommentMap.CommentStart", 8);
        public static final OldCommentMap_Default.OldCommentMap_CommentEnd CommentEnd =
            new OldCommentMap_Default.OldCommentMap_CommentEnd("OldCommentMap.CommentEnd", 9);
        private static final OldCommentMap_Default Default =
            new OldCommentMap_Default("OldCommentMap.Default", -1);

    }

    protected static class OldCommentMap_Default
        extends SmcLexerState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected OldCommentMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void alpha(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.alpha()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void digit(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.digit()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void percent(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.percent()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void slash(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.slash()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void asterisk(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.asterisk()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_brace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.left_brace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void right_brace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.right_brace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_bracket(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.left_bracket()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_paren(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.left_paren()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void right_paren(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.right_paren()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void period(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.period()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void colon(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.colon()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void comma(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.comma()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void semicolon(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.semicolon()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void underscore(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.underscore()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void equal(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.equal()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void dollar(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.dollar()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void whitespace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.whitespace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void EOL(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.EOL()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void unicode(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : OldCommentMap.Default.unicode()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    OldCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(OldCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class OldCommentMap_Start
            extends OldCommentMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private OldCommentMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void asterisk(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.Start.asterisk()");
                }


                (context.getState()).Exit(context);
                context.setState(OldCommentMap.CommentEnd);
                (context.getState()).Entry(context);
                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.Start.slash()");
                }


                (context.getState()).Exit(context);
                context.setState(OldCommentMap.CommentStart);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class OldCommentMap_CommentStart
            extends OldCommentMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private OldCommentMap_CommentStart(String name, int id)
            {
                super (name, id);
            }

            protected void asterisk(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.CommentStart.asterisk()");
                }


                context.pushState(OldCommentMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void commentDone(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.CommentStart.commentDone()");
                }


                (context.getState()).Exit(context);
                context.setState(OldCommentMap.Start);
                (context.getState()).Entry(context);
                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.CommentStart.slash()");
                }


                context.pushState(NewCommentMap.Start);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class OldCommentMap_CommentEnd
            extends OldCommentMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private OldCommentMap_CommentEnd(String name, int id)
            {
                super (name, id);
            }

            protected void asterisk(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.CommentEnd.asterisk()");
                }


                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : OldCommentMap.CommentEnd.slash()");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.commentDone();
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

    /* package */ static abstract class NewCommentMap
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
        public static final NewCommentMap_Default.NewCommentMap_Start Start =
            new NewCommentMap_Default.NewCommentMap_Start("NewCommentMap.Start", 10);
        private static final NewCommentMap_Default Default =
            new NewCommentMap_Default("NewCommentMap.Default", -1);

    }

    protected static class NewCommentMap_Default
        extends SmcLexerState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected NewCommentMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void alpha(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.alpha()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void digit(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.digit()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void percent(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.percent()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void slash(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.slash()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void asterisk(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.asterisk()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_brace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.left_brace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void right_brace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.right_brace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_bracket(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.left_bracket()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void left_paren(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.left_paren()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void right_paren(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.right_paren()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void period(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.period()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void colon(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.colon()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void comma(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.comma()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void semicolon(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.semicolon()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void underscore(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.underscore()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void equal(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.equal()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void dollar(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.dollar()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void whitespace(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.whitespace()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

        protected void unicode(SmcLexerContext context)
        {

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : NewCommentMap.Default.unicode()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    NewCommentMap.Start.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.setState(NewCommentMap.Start);

            if (loopbackFlag == false)
            {
                (context.getState()).Entry(context);
            }

            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class NewCommentMap_Start
            extends NewCommentMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private NewCommentMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void EOL(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : NewCommentMap.Start.EOL()");
                }


                (context.getState()).Exit(context);
                context.popState();

                context.commentDone();
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

    /* package */ static abstract class SourceMap
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
        public static final SourceMap_Default.SourceMap_Start Start =
            new SourceMap_Default.SourceMap_Start("SourceMap.Start", 11);
        public static final SourceMap_Default.SourceMap_SourceEnd SourceEnd =
            new SourceMap_Default.SourceMap_SourceEnd("SourceMap.SourceEnd", 12);
        public static final SourceMap_Default.SourceMap_NeverUsed NeverUsed =
            new SourceMap_Default.SourceMap_NeverUsed("SourceMap.NeverUsed", 13);
        private static final SourceMap_Default Default =
            new SourceMap_Default("SourceMap.Default", -1);

    }

    protected static class SourceMap_Default
        extends SmcLexerState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SourceMap_Default(String name, int id)
        {
            super (name, id);
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class SourceMap_Start
            extends SourceMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private SourceMap_Start(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.Start.Default()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void percent(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.Start.percent()");
                }


                (context.getState()).Exit(context);
                context.setState(SourceMap.SourceEnd);
                (context.getState()).Entry(context);
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class SourceMap_SourceEnd
            extends SourceMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private SourceMap_SourceEnd(String name, int id)
            {
                super (name, id);
            }

            protected void Default(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.SourceEnd.Default()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.addToToken("%");
                    ctxt.addToToken();
                }
                finally
                {
                    context.setState(SourceMap.Start);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void percent(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.SourceEnd.percent()");
                }

                SmcLexerState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.addToToken("%");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void right_brace(SmcLexerContext context)
            {
                SmcLexer ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.SourceEnd.right_brace()");
                }


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.endToken(SmcLexer.SOURCE);
                }
                finally
                {
                    context.popState();
                }

                context.sourceDone();
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class SourceMap_NeverUsed
            extends SourceMap_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private SourceMap_NeverUsed(String name, int id)
            {
                super (name, id);
            }

            protected void EOL(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.EOL()");
                }


                return;
            }

            protected void alpha(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.alpha()");
                }


                return;
            }

            protected void asterisk(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.asterisk()");
                }


                return;
            }

            protected void colon(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.colon()");
                }


                return;
            }

            protected void comma(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.comma()");
                }


                return;
            }

            protected void digit(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.digit()");
                }


                return;
            }

            protected void dollar(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.dollar()");
                }


                return;
            }

            protected void equal(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.equal()");
                }


                return;
            }

            protected void left_brace(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.left_brace()");
                }


                return;
            }

            protected void left_bracket(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.left_bracket()");
                }


                return;
            }

            protected void left_paren(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.left_paren()");
                }


                return;
            }

            protected void period(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.period()");
                }


                return;
            }

            protected void right_brace(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.right_brace()");
                }


                return;
            }

            protected void right_paren(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.right_paren()");
                }


                return;
            }

            protected void semicolon(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.semicolon()");
                }


                return;
            }

            protected void slash(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.slash()");
                }


                return;
            }

            protected void underscore(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.underscore()");
                }


                return;
            }

            protected void unicode(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.unicode()");
                }


                return;
            }

            protected void whitespace(SmcLexerContext context)
            {

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : SourceMap.NeverUsed.whitespace()");
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
}
