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
// SmcLexer --
//
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.4  2002/05/07 00:10:20  cwrapp
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
// Revision 1.2  2001/06/16 19:52:43  cwrapp
// Changes in release 1.0, beta 7:
// Fixes the minor code generation bugs and introduces a new
// example Java program (found at examples/Java/EX7). This
// example program is also a Java applet and can be seen at
// http://smc.sourceforge.net/SmcDemo.htm.
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
// Revision 1.2  2000/09/01 15:32:10  charlesr
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.HashMap;

public final class SmcLexer
{
// Member Methods

    public SmcLexer(FileInputStream stream)
    {
        _stream = stream;
        _token = new Token();
        _token_buffer = new StringBuffer(50);
        _line_number = 1;
        _read_buffer = new byte[READ_BUFFER_LEN];
        _buffer_size = 0;
        _read_index = 0;

        _lexer_map = new SmcLexerContext(this);
    }

    // Return the current line being parsed.
    public int getLineNumber()
    {
        return(_line_number);
    }

    // Return the next token and its token name.
    public Token nextToken()
        throws IOException
    {
        // Keep reading until told to stop or the
        // end-of-file is reached.
        try
        {
            _stop_flag = false;
            while (_stop_flag == false)
            {
                 _current_char = readChar();

                // Translate this character into a transition.
                if (Character.isLetter(_current_char) == true)
                {
                    _lexer_map.alpha();
                }
                else if (Character.isDigit(_current_char) == true)
                {
                    _lexer_map.digit();
                }
                else if (Character.isWhitespace(_current_char) == true)
                {
                    // If this character is a newline, then increment
                    // the current line number.
                    if (_current_char == '\n')
                    {
                        ++_line_number;
                        _lexer_map.EOL();
                    }
                    else
                    {
                        _lexer_map.whitespace();
                    }
                }
                else if (_current_char == ';')
                {
                    _lexer_map.semicolon();
                }
                else if (_current_char == '/')
                {
                    _lexer_map.slash();
                }
                else if (_current_char == '\\')
                {
                    _lexer_map.backslash();
                }
                else if (_current_char == '*')
                {
                    _lexer_map.asterisk();
                }
                else if (_current_char == '{')
                {
                    _lexer_map.left_brace();
                }
                else if (_current_char == '}')
                {
                    _lexer_map.right_brace();
                }
                else if (_current_char == '[')
                {
                    _lexer_map.left_bracket();
                }
                else if (_current_char == ']')
                {
                    _lexer_map.right_bracket();
                }
                else if (_current_char == '(')
                {
                    _lexer_map.left_paren();
                }
                else if (_current_char == ')')
                {
                    _lexer_map.right_paren();
                }
                else if (_current_char == '%')
                {
                    _lexer_map.percent();
                }
                else if (_current_char == '"')
                {
                    _lexer_map.doublequote();
                }
                else if (_current_char == ',')
                {
                    _lexer_map.comma();
                }
                else if (_current_char == ':')
                {
                    _lexer_map.colon();
                }
                else if (_current_char == '.')
                {
                    _lexer_map.period();
                }
                else if (_current_char == '!')
                {
                    _lexer_map.exclamation();
                }
                else if (_current_char == '_')
                {
                    _lexer_map.underscore();
                }
                else if (_current_char == '$')
                {
                    _lexer_map.dollarsign();
                }
                else if (_current_char == '&')
                {
                    _lexer_map.ampersand();
                }
                else if (_current_char == '-')
                {
                    _lexer_map.dash();
                }
                else
                {
                    _lexer_map.unknown();
                }
            }
        }
        catch (EOFException e)
        {
            // If this is the end of the source file, let
            // the parser know.
            _token.setType(DONE_SUCCESS);
            _token.setValue("");
        }

        return(_token);
    }

    //-----------------------------------------------------------
    // State Map Actions
    // The following methods are actions in the state map.
    //
    /* package */ void startToken()
    {
        _token.reset();
        _token_buffer.delete(0, _token_buffer.length());

        // The token's line number is the line on which the
        // token begins.
        _token.setLineNumber(_line_number);

        return;
    }

    /* package */ void addCurrentCharToToken()
    {
        _token_buffer.append(_current_char);
        return;
    }

    /* package */ void addCharToToken(String character)
    {
        _token_buffer.append(character);
        return;
    }

    /* package */ void endToken(int type)
    {
        _token.setType(type);
        _token.setValue(_token_buffer.toString());
        _stop_flag = true;
        return;
    }

    // A malformed token has been detected.
    /* package */ void badToken(String error_msg)
    {
        _token.setType(DONE_FAILED);
        _token.setValue(error_msg +
                        " (token: " +
                        _token_buffer.toString() +
                        ")");
        _stop_flag = true;
        return;
    }

    // Check if the token is a keyword. Otherwise, set the token
    // type to WORD.
    /* package */ void checkKeyword()
    {
        Integer tokenType;

        _token.setValue(_token_buffer.toString());
        _stop_flag = true;

        tokenType = (Integer) _keywordMap.get(_token.getValue());
        if (tokenType != null)
        {
            _token.setType(tokenType.intValue());
        }
        else if (_token.getValue().length() > 0)
        {
            _token.setType(SmcLexer.WORD);
        }
        else
        {
            badToken("Zero-length word");
        }

        return;
    }

    /* package */ void checkPercentKeyword()
    {
        String tokenStr = _token_buffer.toString();

        _token.setValue(tokenStr);

        if (tokenStr.compareTo("%start") == 0)
        {
            _token.setType(SmcLexer.START_STATE);
            _stop_flag = true;
        }
        else if (tokenStr.compareTo("%map") == 0)
        {
            _token.setType(SmcLexer.MAP_NAME);
            _stop_flag = true;
        }
        else if (tokenStr.compareTo("%class") == 0)
        {
            _token.setType(SmcLexer.CLASS_NAME);
            _stop_flag = true;
        }
        else if (tokenStr.compareTo("%header") == 0)
        {
            _token.setType(SmcLexer.HEADER_FILE);
            _stop_flag = true;
        }
        else if (tokenStr.compareTo("%package") == 0)
        {
            _token.setType(SmcLexer.PACKAGE_NAME);
            _stop_flag = true;
        }
        else if (tokenStr.compareTo("%import") == 0)
        {
            _token.setType(SmcLexer.IMPORT);
            _stop_flag = true;
        }
        else
        {
            badToken("Unknown % directive");
        }

        return;
    }

    // Back up one character in the file so that the character
    // will be read again when nextToken() is called. This is
    // usually done when one token is terminated by another.
    /* package */ void ungetChar()
    {
        --_read_index;
        return;
    }

    // Read the next character. Actually, this routine reads in
    // a large buffer and data returns the next character from
    // there. The idea is to do a few large, efficient reads and
    // make single character reads to be array retrievals.
    private char readChar()
        throws IOException,
               EOFException
    {
        char retval;

        // If we are at the end of the buffer, read the
        // next buffer-full.
        if (_read_index == _buffer_size)
        {
            _read_index = 0;
            _buffer_size = _stream.read(_read_buffer);
        }

        // End of file has been reached.
        if (_buffer_size < 0)
        {
            EOFException e = new EOFException();

            _buffer_size = 0;
            throw e;
            
        }
        else
        {
            retval = (char) _read_buffer[_read_index];
            ++_read_index;
        }

        return(retval);
    }

    // Returns true if the entire source file has been read and
    // false otherwise.
    private boolean endOfFile()
    {
        boolean retval;

        // We are at the end-of-file if 1) the buffer's actual
        // size is less than the buffer's total size and the
        // buffer index is at the end of the actual size.
        if (_buffer_size < _read_buffer.length &&
            _read_index == _buffer_size)
        {
            retval = true;
        }
        else
        {
            retval = false;
        }

        return(retval);
    }

// Member Data

    // The lexer's state map.
    private SmcLexerContext _lexer_map;

    // The file being parsed.
    private FileInputStream _stream;

    // Store the event loop - a token has been found.
    private boolean _stop_flag;

    // The character currently being processed.
    private char _current_char;

    // Token name is an int. Token value is a string.
    // Place the token's name and value into the array's first
    // and second position, respectively.
    private Token _token;

    // Collect the token in a string buffer before making a
    // string out of it.
    private StringBuffer _token_buffer;

    // Keep track of the source line being parsed. This is needed
    // for error messages.
    private int _line_number;

    // Read in a buffer-full of data rather than one character
    // at a time.
    private byte[] _read_buffer;

    // The actual number of read characters in the buffer.
    // May be less than the buffer's size.
    private int _buffer_size;

    // The next character to be read from the _read_buffer.
    private int _read_index;

    // The read buffer's allocated size in bytes.
    private static final int READ_BUFFER_LEN = 4096;

    // Each token type has an integer value. These token type
    // values are package-wide scope so the parser can access
    // them.
    /* package */ static final int TOKEN_NOT_SET = 0;
    /* package */ static final int DONE_FAILED = 1;
    /* package */ static final int DONE_SUCCESS = 2;
    /* package */ static final int ENTRY = 3;
    /* package */ static final int EXIT = 4;
    /* package */ static final int POP = 5;
    /* package */ static final int PUSH = 6;
    /* package */ static final int WORD = 7;
    /* package */ static final int START_STATE= 8;
    /* package */ static final int MAP_NAME = 9;
    /* package */ static final int CLASS_NAME = 10;
    /* package */ static final int HEADER_FILE = 11;
    /* package */ static final int PACKAGE_NAME = 12;
    /* package */ static final int IMPORT = 13;
    /* package */ static final int LEFT_BRACE = 14;
    /* package */ static final int RIGHT_BRACE = 15;
    /* package */ static final int LEFT_BRACKET = 16;
    /* package */ static final int RIGHT_BRACKET = 17;
    /* package */ static final int LEFT_PAREN = 18;
    /* package */ static final int RIGHT_PAREN = 19;
    /* package */ static final int SEMICOLON = 20;
    /* package */ static final int COLON = 21;
    /* package */ static final int COMMA = 22;
    /* package */ static final int EXCLAMATION = 23;
    /* package */ static final int SOURCE = 24;
    /* package */ static final int EOD = 25;
    /* package */ static final int VARIABLE = 26;
    /* package */ static final int INTEGER = 27;
    /* package */ static final int FLOAT = 28;
    /* package */ static final int STRING = 29;
    /* package */ static final int ASTERISK = 30;
    /* package */ static final int AMPERSAND = 31;
    /* package */ static final int SLASH = 32;
    /* package */ static final int TOKEN_COUNT = SLASH + 1;
    private static final int KEYWORD_COUNT = 4;

    private static String[] _typeName = null;
    private static HashMap _keywordMap = null;

    static
    {
        _typeName = new String[TOKEN_COUNT];
        _typeName[SmcLexer.TOKEN_NOT_SET] = "TOKEN_NOT_SET";
        _typeName[SmcLexer.DONE_FAILED] = "DONE_FAILED";
        _typeName[SmcLexer.DONE_SUCCESS] = "DONE_SUCCESS";
        _typeName[SmcLexer.ENTRY] = "ENTRY";
        _typeName[SmcLexer.EXIT] = "EXIT";
        _typeName[SmcLexer.POP] = "POP";
        _typeName[SmcLexer.PUSH] = "PUSH";
        _typeName[SmcLexer.WORD] = "WORD";
        _typeName[SmcLexer.START_STATE] = "START_STATE";
        _typeName[SmcLexer.MAP_NAME] = "MAP_NAME";
        _typeName[SmcLexer.CLASS_NAME] = "CLASS_NAME";
        _typeName[SmcLexer.HEADER_FILE] = "HEADER_FILE";
        _typeName[SmcLexer.PACKAGE_NAME] = "PACKAGE_NAME";
        _typeName[SmcLexer.IMPORT] = "IMPORT";
        _typeName[SmcLexer.LEFT_BRACE] = "LEFT_BRACE";
        _typeName[SmcLexer.RIGHT_BRACE] = "RIGHT_BRACE";
        _typeName[SmcLexer.LEFT_BRACKET] = "LEFT_BRACKET";
        _typeName[SmcLexer.RIGHT_BRACKET] = "RIGHT_BRACKET";
        _typeName[SmcLexer.LEFT_PAREN] = "LEFT_PAREN";
        _typeName[SmcLexer.RIGHT_PAREN] = "RIGHT_PAREN";
        _typeName[SmcLexer.SEMICOLON] = "SEMICOLON";
        _typeName[SmcLexer.COLON] = "COLON";
        _typeName[SmcLexer.COMMA] = "COMMA";
        _typeName[SmcLexer.EXCLAMATION] = "EXCLAMATION";
        _typeName[SmcLexer.SOURCE] = "SOURCE";
        _typeName[SmcLexer.EOD] = "EOD";
        _typeName[SmcLexer.VARIABLE] = "VARIABLE";
        _typeName[SmcLexer.INTEGER] = "INTEGER";
        _typeName[SmcLexer.FLOAT] = "FLOAT";
        _typeName[SmcLexer.STRING] = "STRING";
        _typeName[SmcLexer.ASTERISK] = "ASTERISK";
        _typeName[SmcLexer.AMPERSAND] = "AMPERSAND";
        _typeName[SmcLexer.SLASH] = "SLASH";

        _keywordMap = new HashMap(KEYWORD_COUNT);
        _keywordMap.put("Entry", new Integer(SmcLexer.ENTRY));
        _keywordMap.put("Exit", new Integer(SmcLexer.EXIT));
        _keywordMap.put("pop", new Integer(SmcLexer.POP));
        _keywordMap.put("push", new Integer(SmcLexer.PUSH));
    }

// INNER CLASSES

    /* package */ final class Token
    {
        /* package */ Token()
        {
            _type = TOKEN_NOT_SET;
            _value = null;
            _line_number = -1;
        }

        /* package */ int getType()
        {
            return(_type);
        }

        /* package */ void setType(int type)
        {
            _type = type;
            return;
        }

        /* package */ String getValue()
        {
            return(_value);
        }

        /* package */ void setValue(String value)
        {
            _value = value;
        }

        /* package */ int getLineNumber()
        {
            return (_line_number);
        }

        /* package */ void setLineNumber(int line_number)
        {
            _line_number = line_number;
            return;
        }

        /* package */ Token copy()
        {
            Token retval = new Token();

            retval.setType(_type);
            retval.setValue(_value);

            return (retval);
        }

        public String toString()
        {
            String output = new String("{");

            output += SmcLexer._typeName[_type];
            output += ", " + _value + "}";

            return(output);
        }

        private void reset()
        {
            _type = TOKEN_NOT_SET;
            _value = null;
            _line_number = -1;
        }

        private int _type;
        private String _value;
        private int _line_number;
    }
}
