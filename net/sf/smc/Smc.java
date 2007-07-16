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
// Copyright (C) 2000 - 2007. Charles W. Rapp.
// All Rights Reserved.
//
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python, Perl code generation and examples/Perl,
//   Ruby code generation and examples/Ruby, Lua code generation
//   and examples/Lua, Groovy code generation and examples/Groovy.
//   Chris Liscio contributed the Objective-C code generation
//   and examples/ObjC.
//
// SMC --
//
//  State Map Compiler
//
// This class parses a state map exception, checks the code
// for semantic consistency and then generates object-oriented
// code in the user specified target language.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See bottom of file.)
//

package net.sf.smc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Smc
{
//---------------------------------------------------------------
// Member Methods
//

    public static void main(String[] args)
    {
        int retcode = 0;

        _errorMsg = new String();

        // The default smc output level is 1.
        _targetLanguage = null;
        _version = VERSION;
        _debug = false;
        _nostreams = false;
        _sync = false;
        _noex = false;
        _nocatch = false;
        _serial = false;
        _castType = "dynamic_cast";
        _graphLevel = GRAPH_LEVEL_0;
        _sourceFileList = new ArrayList<String>();
        _verbose = false;
        _fsmVerbose = false;
        _return = false;
        _reflection = false;
        _outputDirectory = null;
        _headerDirectory = null;

        // Process the command line.
        if (parseArgs(args) == false)
        {
            retcode = 1;
            System.err.println(APP_NAME + ": " + _errorMsg);
            // _usage(System.err);
        }
        // Arguments check out - start compiling..
        else
        {
            SmcLexer lexer;
            SmcParser parser;
            SmcFSM fsm;
            Iterator<String> sit;
            boolean checkFlag;
            long startTime = 0;
            long finishTime;
            long totalStartTime = 0;
            long totalFinishTime;

            if (_verbose == true)
            {
                totalStartTime = System.currentTimeMillis();
            }

            try
            {
                for (sit = _sourceFileList.iterator();
                     sit.hasNext() == true;
                    )
                {
                    _sourceFileName = (String) sit.next();

                    if (_verbose == true)
                    {
                        System.out.print("[parsing started ");
                        System.out.print(_sourceFileName);
                        System.out.println("]");

                        startTime = System.currentTimeMillis();
                    }

                    parser =
                        new SmcParser(
                            _getFileName(_sourceFileName),
                            new FileInputStream(_sourceFileName),
                            _fsmVerbose);

                    // First - do the parsing
                    fsm = parser.parse();

                    if (_verbose == true)
                    {
                        finishTime = System.currentTimeMillis();

                        System.out.print("[parsing completed ");
                        System.out.print(finishTime - startTime);
                        System.out.println("ms]");
                    }

                    if (fsm == null)
                    {
                        retcode = 1;

                        // Output the parser's messages.
                        outputMessages(_sourceFileName,
                                       System.err,
                                       parser.getMessages());
                    }
                    else
                    {
                        SmcSyntaxChecker checker =
                            new SmcSyntaxChecker(
                                _sourceFileName,
                                _targetLanguage.index());

                        if (_verbose == true)
                        {
                            System.out.print("[checking ");
                            System.out.print(_sourceFileName);
                            System.out.println("]");
                        }

                        // Second - do the semantic check.
                        fsm.accept(checker);
                        if (checker.isValid() == false)
                        {
                            retcode = 1;

                            // Output the syntax checker's
                            // messages.
                            outputMessages(
                                _sourceFileName,
                                System.err,
                                checker.getMessages());
                        }
                        else
                        {
                            // Third - do the code generation.
                            _generateCode(fsm);
                        }
                    }
                }
            }
            // Report an unknown file exception.
            catch (FileNotFoundException filex)
            {
                System.err.print(_sourceFileName);
                System.err.print(": error - ");
                System.err.println(filex.getMessage());
            }
            // A parse exception may be thrown by generateCode().
            // This is not a problem.
            catch (ParseException parsex)
            {
                System.err.print(_sourceFileName);
                System.err.print(":");
                System.err.print(parsex.getErrorOffset());
                System.err.print(": error - ");
                System.err.println(parsex.getMessage());
            }
            catch (Exception e)
            {
                retcode = 1;

                System.err.println(
                    "SMC has experienced a fatal error. Please e-mail the following error output to rapp@acm.org. Thank you.\n");
                System.err.println(
                    "--------------------------------------------------------------------------------");
                System.err.println("SMC version: " + _version);
                System.err.println(
                    "JRE version: v. " +
                    System.getProperty("java.version"));
                System.err.println(
                    "JRE vender: " +
                    System.getProperty("java.vendor") +
                    " (" +
                    System.getProperty("java.vendor.url") +
                    ")");
                System.err.println(
                    "JVM: " +
                    System.getProperty("java.vm.name") +
                    ", v. " +
                    System.getProperty("java.vm.version"));
                System.err.println(
                    "JVM vender: " +
                    System.getProperty("java.vm.vendor"));
                System.err.println("Exception:\n");
                e.printStackTrace();
                System.err.println(
                    "--------------------------------------------------------------------------------");
            }

            if (_verbose == true)
            {
                totalFinishTime = System.currentTimeMillis();

                System.out.print("[total ");
                System.out.print(
                    totalFinishTime - totalStartTime);
                System.out.println("ms]");
            }
        }

        // Need to return the appropriate exit code in case SMC
        // is called by make. Just doing a return always results
        // in a zero return code.
        // v. 4.0.0: But calling exit when SMC is an ANT task is
        // problematic. ANT is a Java program and calls Smc.main
        // directly and not as a forked process. So when Smc.main
        // exits, it exits the JVM for everyone including ANT.
        if (_return == false)
        {
            System.exit(retcode);
        }
        else
        {
            return;
        }
    } // end of main(String[])

    public static String sourceFileName()
    {
        return (_sourceFileName);
    }

    public static boolean isDebug()
    {
        return (_debug);
    }

    public static boolean isNoStreams()
    {
        return (_nostreams);
    }

    public static boolean isSynchronized()
    {
        return (_sync);
    }

    public static boolean isNoExceptions()
    {
        return (_noex);
    }

    public static boolean isNoCatch()
    {
        return (_nocatch);
    }

    public static boolean isSerial()
    {
        return (_serial);
    }

    public static boolean isReflection()
    {
        return (_reflection);
    }

    public static String castType()
    {
        return (_castType);
    }

    public static int graphLevel()
    {
        return (_graphLevel);
    }

    public static String outputDirectory()
    {
        return (_outputDirectory);
    }

    public static String headerDirectory()
    {
        return (_headerDirectory);
    }

    // Merge two lists together, returning an ordered list with
    // no multiple entries.
    public static List<SmcTransition>
        merge(List<SmcTransition> l1,
              List<SmcTransition> l2,
              Comparator<SmcTransition> c)
    {
        int result;
        Iterator<SmcTransition> it1;
        Iterator<SmcTransition> it2;
        SmcTransition e1;
        SmcTransition e2;
        List<SmcTransition> retval =
            new ArrayList<SmcTransition>();

        // First, make certain that both lists are sorted.
        Collections.sort(l1, c);
        Collections.sort(l2, c);

        // Now merge the two lists together.
        // Continue until the end of either list is reached.
        for (it1 = l1.iterator(),
                     it2 = l2.iterator(),
                     e1 = null,
                     e2 = null;
             (it1.hasNext() == true || e1 != null) &&
                     (it2.hasNext() == true || e2 != null);
            )
        {
            if (e1 == null)
            {
                e1 = it1.next();
            }
            if (e2 == null)
            {
                e2 = it2.next();
            }

            if ((result = c.compare(e1, e2)) < 0)
            {
                retval.add(e1);
                e1 = null;
            }
            else if (result > 0)
            {
                retval.add(e2);
                e2 = null;
            }
            else
            {
                retval.add(e1);
                e1 = null;
                e2 = null;
            }
        }

        // Is there any more to add?
        if (it1.hasNext() == true || e1 != null)
        {
            if (e1 != null)
            {
                retval.add(e1);
            }

            for (; it1.hasNext() == true;)
            {
                retval.add(it1.next());
            }
        }
        else if (it2.hasNext() == true || e2 != null)
        {
            if (e2 != null)
            {
                retval.add(e2);
            }

            for (; it2.hasNext() == true;)
            {
                retval.add(it2.next());
            }
        }

        return (retval);
    }

    // Place a backslash escape character in front of backslashes
    // and doublequotes.
    public static String escape(String s)
    {
        String retval;

        if (s.indexOf('\\') < 0 && s.indexOf('"') < 0)
        {
            retval = s;
        }
        else
        {
            StringBuffer buffer =
                new StringBuffer(s.length() * 2);
            int index;
            int length = s.length();
            char c;

            for (index = 0; index < length; ++index)
            {
                c = s.charAt(index);
                if (c == '\\' || c == '"')
                {
                    buffer.append('\\');
                }

                buffer.append(c);
            }

            retval = buffer.toString();
        }

        return (retval);
    }

    // Outputs a list of warning and error messages.
    public static void outputMessages(String srcFileName,
                                      PrintStream stream,
                                      List<SmcMessage> messages)
    {
        for (SmcMessage message: messages)
        {
            stream.print(srcFileName);
            stream.print(':');
            stream.print(message.getLineNumber());

            if (message.getLevel() == SmcMessage.WARNING)
            {
                stream.print(": warning - ");
            }
            else
            {
                stream.print(": error - ");
            }

            stream.println(message.getText());
        }

        return;
    }

    private static boolean parseArgs(String[] args)
    {
        int i;
        int argsConsumed;
        boolean helpFlag = false;
        boolean retcode= true;

        // Look for either -help or -verson first. If specified,
        // then output the necessary info and return.
        helpFlag = _needHelp(args);
        if (helpFlag == false)
        {
            // Look for the target language second. Verify that
            // exactly one target language is specifed.
            try
            {
                _targetLanguage = _findTargetLanguage(args);
            }
            catch (IllegalArgumentException argex)
            {
                retcode = false;
                _errorMsg = argex.getMessage();
            }

            if (retcode == true && _targetLanguage == null)
            {
                retcode = false;
                _errorMsg = "Target language was not specified.";
            }
        }

        // Parse all options first. Keep going until an error is
        // encountered or there are no more options left.
        for (i = 0, argsConsumed = 0;
             i < args.length &&
                 helpFlag == false &&
                 retcode == true &&
                 args[i].startsWith("-") == true;
             i += argsConsumed, argsConsumed = 0)
        {
            // Ignore the target language flags - they have
            // been processed.
            if (_findLanguage(args[i]) != null)
            {
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-sy") == true)
            {
                if (_supportsOption(SYNC_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        SYNC_FLAG +
                        ".";
                }
                else
                {
                    _sync = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-su") == true)
            {
                // -suffix should be followed by a suffix.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        SUFFIX_FLAG + " not followed by a value";
                }
                else if (_supportsOption(SUFFIX_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        SUFFIX_FLAG +
                        ".";
                }
                else
                {
                    _suffix = args[i+1];
                    argsConsumed = 2;
                }
            }
            else if (args[i].startsWith("-ca") == true)
            {
                // -cast should be followed by a cast type.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        CAST_FLAG +
                        " not followed by a value";
                }
                else if (_supportsOption(CAST_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        CAST_FLAG +
                        ".";
                }
                else if (_isValidCast(args[i+1]) == false)
                {
                    retcode = false;
                    _errorMsg =
                        "\"" +
                        args[i+1] +
                        "\" is an invalid C++ cast type.";
                }
                else
                {
                    _castType = args[i+1];
                    argsConsumed = 2;
                }
            }
            else if (args[i].equals("-d") == true)
            {
                // -d should be followed by a directory.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        DIRECTORY_FLAG +
                        " not followed by directory";
                }
                else if (
                    _supportsOption(DIRECTORY_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        DIRECTORY_FLAG +
                        ".";
                }
                else
                {
                    _outputDirectory = args[i+1];
                    argsConsumed = 2;

                    // If the output directory does not end with
                    // file path separator, then add one.
                    if (_outputDirectory.endsWith(
                            File.separator) == false)
                    {
                        _outputDirectory =
                            _outputDirectory + File.separator;
                    }

                    retcode =
                        _isValidDirectory(_outputDirectory);
                }
            }
            else if (args[i].startsWith("-hea") == true)
            {
                // -headerd should be followed by a directory.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg = HEADER_FLAG +
                                " not followed by directory";
                }
                else if (
                    _supportsOption(HEADER_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        HEADER_FLAG +
                        ".";
                }
                else
                {
                    _headerDirectory = args[i+1];
                    argsConsumed = 2;

                    // If the output directory does not end with
                    // file path separator, then add one.
                    if (_headerDirectory.endsWith(
                            File.separator) == false)
                    {
                        _headerDirectory =
                            _headerDirectory + File.separator;
                    }

                    retcode =
                        _isValidDirectory(_headerDirectory);
                }
            }
            else if (args[i].startsWith("-gl") == true)
            {
                // -glevel should be followed by an integer.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        GLEVEL_FLAG +
                        " not followed by integer";
                }
                else if (_supportsOption(GLEVEL_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        GLEVEL_FLAG +
                        ".";
                }
                else
                {
                    try
                    {
                        _graphLevel =
                            Integer.parseInt(args[i+1]);

                        if (_graphLevel < GRAPH_LEVEL_0 ||
                            _graphLevel > GRAPH_LEVEL_2)
                        {
                            retcode = false;
                            _errorMsg =
                                GLEVEL_FLAG +
                                " must be 0, 1 or 2";
                        }
                        else
                        {
                            argsConsumed = 2;
                        }
                    }
                    catch (NumberFormatException numberex)
                    {
                        retcode = false;

                        _errorMsg =
                            GLEVEL_FLAG +
                            " not followed by valid integer";
                    }
                }
            }
            else if (args[i].equals("-g") == true)
            {
                if (_supportsOption(DEBUG_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        DEBUG_FLAG +
                        ".";
                }
                else
                {
                    _debug = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-nos") == true)
            {
                if (_supportsOption(NO_STREAMS_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        NO_STREAMS_FLAG +
                        ".";
                }
                else
                {
                    _nostreams = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-noe") == true)
            {
                if (_supportsOption(NO_EXCEPTIONS_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        NO_EXCEPTIONS_FLAG +
                        ".";
                }
                else
                {
                    _noex = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-noc") == true)
            {
                if (_supportsOption(NO_CATCH_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        NO_CATCH_FLAG +
                        ".";
                }
                else
                {
                    _nocatch = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-ret") == true)
            {
                if (_supportsOption(RETURN_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        RETURN_FLAG +
                        ".";
                }
                else
                {
                    _return = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-ref") == true)
            {
                if (_supportsOption(REFLECT_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        REFLECT_FLAG +
                        ".";
                }
                else
                {
                    _reflection = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-se") == true)
            {
                if (_supportsOption(SERIAL_FLAG) == false)
                {
                    retcode = false;
                    _errorMsg =
                        _targetLanguage.name() +
                        " does not support " +
                        SERIAL_FLAG +
                        ".";
                }
                else
                {
                    _serial = true;
                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-verb") == true)
            {
                _verbose = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-vverb") == true)
            {
                _fsmVerbose = true;
                argsConsumed = 1;
            }
            else
            {
                retcode = false;
                _errorMsg = "Unknown option (" +
                             args[i] +
                             ")";
            }
        }

        // Was a state map source file given? It must be the
        // last argument in the list.
        if (helpFlag == false && retcode == true)
        {
            if (i == args.length)
            {
                retcode = false;
                _errorMsg = "Missing source file";
            }
            else
            {
                File sourceFile;

                for (; i < args.length && retcode == true; ++i)
                {
                    // The file name must end in ".sm".
                    if (args[i].toLowerCase().endsWith(".sm") ==
                            false)
                    {
                        retcode = false;
                        _errorMsg =
                            "Source file name must end in " +
                            "\".sm\" (" +
                            args[i] +
                            ")";
                    }
                    else
                    {
                        sourceFile = new File(args[i]);
                        if (sourceFile.exists() == false)
                        {
                            retcode = false;
                            _errorMsg = "No such file named \"" +
                                         args[i] +
                                         "\"";
                        }
                        else if (sourceFile.canRead() == false)
                        {
                            retcode = false;
                            _errorMsg = "Source file \"" +
                                         args[i] +
                                         "\" is not readable";
                        }
                        else
                        {
                            _sourceFileList.add(args[i]);
                        }
                    }
                }
            }
        }

        return (retcode);
    }

    // Process the -help and -version flags separately.
    private static boolean _needHelp(String[] args)
    {
        int i;
        boolean retval = false;

        for (i = 0; i < args.length && retval == false; ++i)
        {
            if (args[i].startsWith("-hel") == true)
            {
                retval = true;
                _usage(System.out);
            }
            else if (args[i].startsWith("-vers") == true)
            {
                retval = true;
                System.out.println(APP_NAME + " " + _version);
            }
        }

        return (retval);
    }

    // Returns the target language found in the command line
    // arguments. Throws an IllegalArgumentException if more than
    // one target language is specified.
    // As a side effect sets the default suffix.
    private static Language _findTargetLanguage(String[] args)
    {
        int i;
        Language lang;
        Language retval = null;

        for (i = 0; i < args.length; ++i)
        {
            // Is this argument a language name?
            if ((lang = _findLanguage(args[i])) != null)
            {
                // Only one target langugage can be specified.
                if (retval != null && retval != lang)
                {
                    throw (
                        new IllegalArgumentException(
                            "Only one target language " +
                            "may be specified"));
                }
                else
                {
                    retval = lang;
                    _suffix = lang.suffix();
                }
            }
        }

        return (retval);
    }

    // Returns the langugage record associated with the given
    // command line option.
    private static Language _findLanguage(String option)
    {
        int index;
        Language retval = null;

        for (index = 1;
             index < _languages.length && retval == null;
             ++index)
        {
            if (option.equals(
                    _languages[index].optionFlag()) == true)
            {
                retval = _languages[index];
            }
        }

        return (retval);
    }

    // Returns true if the target language supports the specified
    // option.
    private static boolean _supportsOption(String option)
    {
        List<Language> languages = _optionMap.get(option);

        return (
            languages != null &&
            languages.contains(_targetLanguage));
    }

    // Returns true if the string is a valid C++ cast.
    private static boolean _isValidCast(String castType)
    {
        return (castType.equals("dynamic_cast") == true ||
                castType.equals("static_cast") == true ||
                castType.equals("reinterpret_cast") == true);
    }

    // Returns true if the path is a valid destination directory.
    private static boolean _isValidDirectory(String path)
    {
        boolean retcode = false;

        try
        {
            File pathObj = new File(path);

            if (pathObj.isDirectory() == false)
            {
                _errorMsg =
                    "\"" + path + "\" is not a directory";
            }
            else if (pathObj.canWrite() == false)
            {
                _errorMsg =
                    "\"" + path + "\" is not writeable";
            }
            else
            {
                retcode = true;
            }
        }
        catch (SecurityException securex)
        {
            _errorMsg = "Unable to access \"" + path + "\"";
        }

        return (retcode);
    }

    private static void _usage(PrintStream stream)
    {
        stream.print("usage: ");
        stream.print(APP_NAME);
        stream.print(" [-suffix suffix]");
        stream.print(" [-g]");
        stream.print(" [-nostreams]");
        stream.print(" [-version]");
        stream.print(" [-verbose]");
        stream.print(" [-help]");
        stream.print(" [-sync]");
        stream.print(" [-noex]");
        stream.print(" [-nocatch]");
        stream.print(" [-serial]");
        stream.print(" [-return]");
        stream.print(" [-reflect]");
        stream.print(" [-cast cast_type]");
        stream.print(" [-d directory]");
        stream.print(" [-headerd directory]");
        stream.print(" [-glevel int]");
        stream.print(
            " {-c | -c++ | -csharp | -graph | -groovy | -java | ");
        stream.print(
            "-lua | -objc | -perl | -python | -ruby | -table | ");
        stream.print("-tcl | -vb}");
        stream.println(" statemap_file");
        stream.println("    where:");
        stream.println(
            "\t-suffix   Add this suffix to output file");
        stream.println(
            "\t-g        Add debugging to generated code");
        stream.println("\t-nostreams Do not use C++ iostreams ");
        stream.print("\t          ");
        stream.println("(use with -c++ only)");
        stream.print("\t-version  Print smc version ");
        stream.println("information to standard out and exit");
        stream.print("\t-verbose  ");
        stream.println("Output more compiler messages.");
        stream.print("\t-help     Print this message to ");
        stream.println("standard out and exit");
        stream.println(
            "\t-sync     Synchronize generated Java code ");
        stream.print("\t          ");
        stream.println("(use with -java, -groovy, -vb and -csharp only)");
        stream.println(
            "\t-noex     Do not generate C++ exception throws ");
        stream.print("\t          ");
        stream.println("(use with -c++ only)");
        stream.print(
            "\t-nocatch  Do not generate try/catch/rethrow ");
        stream.println("code (not recommended)");
        stream.println(
            "\t-serial   Generate serialization code");
        stream.print("\t-return   ");
        stream.println("Smc.main() returns, not exits");
        stream.print("\t          ");
        stream.println("(use this option with ANT)");
        stream.println("\t-reflect  Generate reflection code");
        stream.print("\t          ");
        stream.print("(use with -java, -tcl, -vb, -csharp, ");
        stream.println("-groovy, -lua, -perl, -python and -ruby only)");
        stream.println("\t-cast     Use this C++ cast type ");
        stream.print("\t          ");
        stream.println("(use with -c++ only)");
        stream.println(
            "\t-d        Place generated files in directory");
        stream.print(
            "\t-headerd  Place generated header files in ");
        stream.println("directory");
        stream.print("\t          ");
        stream.println("(use with -c, -c++ only)");
        stream.print(
            "\t-glevel   Detail level from 0 (least) to 2 ");
        stream.println("(greatest)");
        stream.print("\t          ");
        stream.println("(use with -graph only)");
        stream.println("\t-c        Generate C code");
        stream.println("\t-c++      Generate C++ code");
        stream.println("\t-csharp   Generate C# code");
        stream.println("\t-graph    Generate GraphViz DOT file");
        stream.println("\t-grooyv   Generate Groovy code");
        stream.println("\t-java     Generate Java code");
        stream.println("\t-lua      Generate Lua code");
        stream.println("\t-objc     Generate Objective-C code");
        stream.println("\t-perl     Generate Perl code");
        stream.println("\t-python   Generate Python code");
        stream.println("\t-ruby     Generate Ruby code");
        stream.println("\t-table    Generate HTML table code");
        stream.println("\t-tcl      Generate [incr Tcl] code");
        stream.println("\t-vb       Generate VB.Net code");
        stream.println();
        stream.println(
            "    Note: statemap_file must end in \".sm\"");
        stream.print(
            "    Note: must select one of -c, -c++, -csharp, ");
        stream.print("-graph, -groovy, -java, -lua, -objc, -perl, ");
        stream.println(
            "-python, -ruby, -table, -tcl or -vb.");

        return;
    }

    // Returns the <name> portion from <path>/<name>.sm.
    private static String _getFileName(String fullName)
    {
        File file = new File(fullName);
        String fileName = file.getName();

        // Note: this works because the file name's form
        // has already been validated as ending in .sm.
        return (
            fileName.substring(
                0, fileName.toLowerCase().indexOf(".sm")));
    }

    // Generate the State pattern in the target language.
    private static void _generateCode(SmcFSM fsm)
        throws FileNotFoundException,
               IOException,
               ParseException
    {
        int endIndex =
            _sourceFileName.length() - 3;
        String srcFilePath =
            "." + System.getProperty("file.separator");
        String srcFileBase =
            _sourceFileName.substring(0, endIndex);
        String headerFileName = "";
        FileOutputStream headerFileStream = null;
        PrintStream headerStream = null;
        SmcCodeGenerator headerGenerator = null;
        String srcFileName = "";
        FileOutputStream sourceFileStream = null;
        PrintStream sourceStream = null;
        SmcCodeGenerator generator = null;

        // For some strange reason I get the wrong
        // line separator character when I use Java
        // on Windows. Set the line separator to "\n"
        // and all is well.
        System.setProperty("line.separator", "\n");

        // Strip away any preceding directories from
        // the source file name.
        endIndex = srcFileBase.lastIndexOf(File.separatorChar);
        if (endIndex >= 0)
        {
            srcFilePath =
                srcFileBase.substring(
                    0, (endIndex + 1));

            // Strip the ".sm" from the source file's name.
            srcFileBase =
                srcFileBase.substring(
                    endIndex + 1);
        }

        // If -d was specified, then use place generated file
        // there.
        if (_outputDirectory != null)
        {
            srcFilePath = _outputDirectory;
        }

        // Create the header file name and generator -
        // if the language uses a header file.
        if (_targetLanguage.hasHeaderFile() == true)
        {
            String headerPath = srcFilePath;

            // If -headerd was specified, then place the file
            // there. -headerd takes precedence over -d.
            if (_headerDirectory != null)
            {
                headerPath = _headerDirectory;
            }

            headerFileName =
                _targetLanguage.sourceFile(
                    headerPath, srcFileBase, "h");
            headerFileStream =
                new FileOutputStream(headerFileName);
            headerStream =
                new PrintStream(headerFileStream);
            headerGenerator =
                _targetLanguage.headerGenerator(
                    headerStream, srcFileBase);
        }

        // Create the language-specific source code generator.
        srcFileName =
            _targetLanguage.sourceFile(
                srcFilePath, srcFileBase, _suffix);
        sourceFileStream =
            new FileOutputStream(srcFileName);
        sourceStream =
            new PrintStream(sourceFileStream);
        generator =
            _targetLanguage.generator(sourceStream, srcFileBase);

        // Generate the header file first.
        if (headerGenerator != null)
        {
            fsm.accept(headerGenerator);
            headerFileStream.flush();
            headerFileStream.close();

            if (_verbose == true)
            {
                System.out.print("[wrote ");
                System.out.print(headerFileName);
                System.out.println("]");
            }
        }

        // Now output the FSM in the target language.
        if (generator != null)
        {
            fsm.accept(generator);
            sourceFileStream.flush();
            sourceFileStream.close();

            if (_verbose == true)
            {
                System.out.print("[wrote ");
                System.out.print(srcFileName);
                System.out.println("]");
            }
        }

        return;
    }

//---------------------------------------------------------------
// Inner classes
//

    // The Language class explicitly stores each target
    // language's properties:
    // + The *start* of the command line option.
    // + The language's full name.
    // + The default file name suffix.
    // + The file name format for the generated SMC files.
    // + The language's SmcCodeGenerator subclass.
    // + Whether the language also generates a header file and
    //   that header file SmcCodeGenerator subclass.

    /* package */ static final class Language
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        // Constructor.
        public Language(int index,
                        String optionFlag,
                        String name,
                        String suffix,
                        String sourceNameFormat,
                        Class generator,
                        boolean headerFlag,
                        Class headerGenerator)
        {
            Class[] params = new Class[2];
            Constructor sourceCtor = null;
            Constructor headerCtor = null;

            _index = index;
            _optionFlag = optionFlag;
            _name = name;
            _suffix = suffix;
            _sourceNameFormat = sourceNameFormat;
            _headerFlag = headerFlag;

            params[0] = PrintStream.class;
            params[1] = String.class;
            if (generator != null)
            {
                try
                {
                    sourceCtor =
                        generator.getDeclaredConstructor(params);
                }
                catch (NoSuchMethodException methoex)
                {}
            }

            if (headerGenerator != null)
            {
                try
                {
                    headerCtor =
                        headerGenerator.getDeclaredConstructor(
                            params);
                }
                catch (NoSuchMethodException methoex)
                {}
            }

            _generator = sourceCtor;
            _headerGenerator = headerCtor;
        }

        //-------------------------------------------------------
        // Get methods.
        //

        public int index()
        {
            return (_index);
        }

        public String optionFlag()
        {
            return (_optionFlag);
        }

        public String name()
        {
            return (_name);
        }

        public String suffix()
        {
            return (_suffix);
        }

        public String sourceFile(String path,
                                 String basename,
                                 String suffix)
        {
            MessageFormat formatter =
                new MessageFormat(_sourceNameFormat);
            Object[] args = new Object[3];

            args[0] = path;
            args[1] = basename;
            args[2] = suffix;

            return (formatter.format(args));
        }

        public SmcCodeGenerator generator(PrintStream stream,
                                          String basename)
        {
            SmcCodeGenerator retval = null;

            try
            {
                Object[] args = new Object[2];

                args[0] = stream;
                args[1] = basename;

                retval =
                    (SmcCodeGenerator)
                        _generator.newInstance(args);
            }
            catch (Exception jex)
            {
                // Ignore. Return null.
            }

            return (retval);
        }

        public boolean hasHeaderFile()
        {
            return (_headerFlag);
        }

        public SmcCodeGenerator
            headerGenerator(PrintStream stream,
                            String basename)
        {
            SmcCodeGenerator retval = null;

            try
            {
                Object[] args = new Object[2];

                args[0] = stream;
                args[1] = basename;

                retval =
                    (SmcCodeGenerator)
                        _headerGenerator.newInstance(args);
            }
            catch (Exception jex)
            {
                // Ignore. Return null.
            }

            return (retval);
        }

        public String toString()
        {
            return (_name);
        }

        //
        // end of Get methods.
        //-------------------------------------------------------

    //-----------------------------------------------------------
    // Member data.
    //

        private final int _index;
        private final String _optionFlag;
        private final String _name;
        private final String _suffix;
        private final String _sourceNameFormat;
        private final Constructor _generator;
        private final boolean _headerFlag;
        private final Constructor _headerGenerator;
    }

//---------------------------------------------------------------
// Member Data
//

    //-----------------------------------------------------------
    // Statics.
    //

    // The source file currently being compiled.
    private static String _sourceFileName;

    // The state map source code to be compiled.
    private static List<String> _sourceFileList;

    // Append this suffix to the end of the output file.
    private static String _suffix;

    // Place the output files in this directory. May be null.
    private static String _outputDirectory;

    // Place header files in this directory. May be null.
    private static String _headerDirectory;

    // If true, then generate verbose information.
    private static boolean _debug;

    // If true, then do not use C++ iostreams for debugging.
    // Application code must provide a TRACE macro to output
    // the debug messages.
    private static boolean _nostreams;

    // If true, then generate thread-safe Java code.
    private static boolean _sync;

    // If true, then do *not* generate C++ exception throws.
    private static boolean _noex;

    // If true, then do *not* generate try/catch/rethrow code.
    private static boolean _nocatch;

    // If true, then generate unique integer IDs for each state.
    private static boolean _serial;

    // If true, then generate getTransitions() method for each
    // state.
    private static boolean _reflection;

    // If true, then generate compiler verbose messages.
    private static boolean _verbose;

    // If true, then generate FSM messages.
    private static boolean _fsmVerbose;

    // The details placed into the GraphViz DOT file.
    private static int _graphLevel;

    // When generating C++ code, use this cast type.
    private static String _castType;

    // Have Smc.main() return rather than exit.
    private static boolean _return;

    // Store command line error messages here.
    private static String _errorMsg;

    // The app's version ID.
    private static String _version;

    // The list of all supported languages.
    private static Language[] _languages;

    // Map each command line option flag to the target languages
    // supporting the flag.
    // private static Map<String, List<Language>> _optionMap;
    private static Map<String, List<Language>> _optionMap;

    //-----------------------------------------------------------
    // Constants.
    //

    // Specifies target programming language.
    /* package */ static Language _targetLanguage;
    /* package */ static final int LANG_NOT_SET = 0;
    /* package */ static final int C_PLUS_PLUS = 1;
    /* package */ static final int JAVA = 2;
    /* package */ static final int TCL = 3;
    /* package */ static final int VB = 4;
    /* package */ static final int C_SHARP = 5;
    /* package */ static final int PYTHON = 6;
    /* package */ static final int TABLE = 7;
    /* package */ static final int GRAPH = 8;
    /* package */ static final int PERL = 9;
    /* package */ static final int RUBY = 10;
    /* package */ static final int C = 11;
    /* package */ static final int OBJECTIVE_C = 12;
    /* package */ static final int LUA = 13;
    /* package */ static final int GROOVY = 14;
    /* package */ static final int LANGUAGE_COUNT = 15;

    // GraphViz detail level.
    /* package */ static final int NO_GRAPH_LEVEL = -1;
    /* package */ static final int GRAPH_LEVEL_0 = 0;
    /* package */ static final int GRAPH_LEVEL_1 = 1;
    /* package */ static final int GRAPH_LEVEL_2 = 2;

    // Specifies the transition's type.
    /* package */ static final int TRANS_NOT_SET = 0;
    /* package */ static final int TRANS_SET = 1;
    /* package */ static final int TRANS_PUSH = 2;
    /* package */ static final int TRANS_POP = 3;

    private static final String APP_NAME = "smc";
    private static final String VERSION = "v. 5.0.0";

    // Command line option flags.
    private static final String CAST_FLAG = "-cast";
    private static final String DIRECTORY_FLAG = "-d";
    private static final String DEBUG_FLAG = "-g";
    private static final String GLEVEL_FLAG = "-glevel";
    private static final String HEADER_FLAG = "-headerd";
    private static final String HELP_FLAG = "-help";
    private static final String NO_CATCH_FLAG = "-nocatch";
    private static final String NO_EXCEPTIONS_FLAG = "-noex";
    private static final String NO_STREAMS_FLAG = "-nostreams";
    private static final String REFLECT_FLAG = "-reflect";
    private static final String RETURN_FLAG = "-return";
    private static final String SERIAL_FLAG = "-serial";
    private static final String SUFFIX_FLAG = "-suffix";
    private static final String SYNC_FLAG = "-sync";
    private static final String VERBOSE_FLAG = "-verbose";
    private static final String VERSION_FLAG = "-version";
    private static final String VVERBOSE_FLAG = "-vverbose";

    static
    {
        // Find in the static languages array.
        _languages = new Language[LANGUAGE_COUNT];
        _languages[LANG_NOT_SET] =
            new Language(LANG_NOT_SET,
                         "",
                         "(not set)",
                         "",
                         null,
                         null,
                         false,
                         null);
        _languages[C] =
            new Language(
                C,
                "-c",
                "C",
                "c",
                "{0}{1}_sm.{2}",
                SmcCGenerator.class,
                true,
                SmcHeaderCGenerator.class);
        _languages[C_PLUS_PLUS] =
            new Language(
                C_PLUS_PLUS,
                "-c++",
                "C++",
                "cpp",
                "{0}{1}_sm.{2}",
                SmcCppGenerator.class,
                true,
                SmcHeaderGenerator.class);
        _languages[C_SHARP] =
            new Language(
                C_SHARP,
                "-csharp",
                "C#",
                "cs",
                "{0}{1}_sm.{2}",
                SmcCSharpGenerator.class,
                false,
                null);
        _languages[JAVA] =
            new Language(
                JAVA,
                "-java",
                "Java",
                "java",
                "{0}{1}Context.{2}",
                SmcJavaGenerator.class,
                false,
                null);
        _languages[GRAPH] =
            new Language(
                GRAPH,
                "-graph",
                "-graph",
                "dot",
                "{0}{1}_sm.{2}",
                SmcGraphGenerator.class,
                false,
                null);
        _languages[GROOVY] =
            new Language(
                GROOVY,
                "-groovy",
                "Groovy",
                "groovy",
                "{0}{1}Context.{2}",
                SmcGroovyGenerator.class,
                false,
                null);
        _languages[LUA] =
            new Language(
                LUA,
                "-lua",
                "Lua",
                "lua",
                "{0}{1}_sm.{2}",
                SmcLuaGenerator.class,
                false,
                null);
        _languages[OBJECTIVE_C] =
            new Language(
                OBJECTIVE_C,
                "-objc",
                "Objective-C",
                "m",
                "{0}{1}_sm.{2}",
                SmcObjCGenerator.class,
                true,
                SmcHeaderObjCGenerator.class);
        _languages[PERL] =
            new Language(
                PERL,
                "-perl",
                "Perl",
                "pm",
                "{0}{1}_sm.{2}",
                SmcPerlGenerator.class,
                false,
                null);
        _languages[PYTHON] =
            new Language(
                PYTHON,
                "-python",
                "Python",
                "py",
                "{0}{1}_sm.{2}",
                SmcPythonGenerator.class,
                false,
                null);
        _languages[RUBY] =
            new Language(
                RUBY,
                "-ruby",
                "Ruby",
                "rb",
                "{0}{1}_sm.{2}",
                SmcRubyGenerator.class,
                false,
                null);
        _languages[TABLE] =
            new Language(
                TABLE,
                "-table",
                "-table",
                "html",
                "{0}{1}_sm.{2}",
                SmcTableGenerator.class,
                false,
                null);
        _languages[TCL] =
            new Language(
                TCL,
                "-tcl",
                "[incr Tcl]",
                "tcl",
                "{0}{1}_sm.{2}",
                SmcTclGenerator.class,
                false,
                null);
        _languages[VB] =
            new Language(
                VB,
                "-vb",
                "VB.net",
                "vb",
                "{0}{1}_sm.{2}",
                SmcVBGenerator.class,
                false,
                null);

        List<Language> languages = new ArrayList<Language>();
        int target;

        _optionMap = new HashMap<String, List<Language>>();

        // Languages supporting each option:
        // +      -cast: C++
        // +         -d: all
        // +         -g: all
        // +    -glevel: graph
        // +    -header: C, C++, Objective C
        // +      -help: all
        // +   -nocatch: all
        // +      -noex: C++
        // + -nostreams: C++
        // +   -reflect: C#, Java, TCL, VB, Lua, Perl, Python,
        //               Ruby, Groovy
        // +    -return: all
        // +    -serial: C#, C++, Java, Tcl, VB, Groovy
        // +    -suffix: all
        // +      -sync: C#, Java, VB, Groovy
        // +   -verbose: all
        // +   -version: all
        // +  -vverbose: all

        // Set the options supporting all languages first.
        for (target = (LANG_NOT_SET + 1);
             target < LANGUAGE_COUNT;
             ++target)
        {
            languages.add(_languages[target]);
        }

        _optionMap.put(DIRECTORY_FLAG, languages);
        _optionMap.put(DEBUG_FLAG, languages);
        _optionMap.put(HELP_FLAG, languages);
        _optionMap.put(NO_CATCH_FLAG, languages);
        _optionMap.put(RETURN_FLAG, languages);
        _optionMap.put(SUFFIX_FLAG, languages);
        _optionMap.put(VERBOSE_FLAG, languages);
        _optionMap.put(VERSION_FLAG, languages);
        _optionMap.put(VVERBOSE_FLAG, languages);

        // Set the options supported by less than all langugages.
        languages = new ArrayList<Language>();
        languages.add(_languages[C_PLUS_PLUS]);
        _optionMap.put(CAST_FLAG, languages);
        _optionMap.put(NO_EXCEPTIONS_FLAG, languages);
        _optionMap.put(NO_STREAMS_FLAG, languages);

        languages = new ArrayList<Language>();
        languages.add(_languages[C_PLUS_PLUS]);
        languages.add(_languages[C]);
        languages.add(_languages[OBJECTIVE_C]);
        _optionMap.put(HEADER_FLAG, languages);

        languages = new ArrayList<Language>();
        languages.add(_languages[C_SHARP]);
        languages.add(_languages[JAVA]);
        languages.add(_languages[VB]);
        languages.add(_languages[GROOVY]);
        _optionMap.put(SYNC_FLAG, languages);

        languages = new ArrayList<Language>();
        languages.add(_languages[C_SHARP]);
        languages.add(_languages[JAVA]);
        languages.add(_languages[VB]);
        languages.add(_languages[TCL]);
        languages.add(_languages[LUA]);
        languages.add(_languages[PERL]);
        languages.add(_languages[PYTHON]);
        languages.add(_languages[RUBY]);
        languages.add(_languages[GROOVY]);
        _optionMap.put(REFLECT_FLAG, languages);

        languages = new ArrayList<Language>();
        languages.add(_languages[C_SHARP]);
        languages.add(_languages[JAVA]);
        languages.add(_languages[VB]);
        languages.add(_languages[TCL]);
        languages.add(_languages[C_PLUS_PLUS]);
        languages.add(_languages[GROOVY]);
        _optionMap.put(SERIAL_FLAG, languages);

        languages = new ArrayList<Language>();
        languages.add(_languages[GRAPH]);
        _optionMap.put(GLEVEL_FLAG, languages);
    }
}

//
// CHANGE LOG
// $Log$
// Revision 1.22  2007/07/16 06:28:06  fperrad
// + Added Groovy generator.
//
// Revision 1.21  2007/02/21 13:53:38  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.20  2007/02/13 18:43:19  cwrapp
// Reflect options fix.
//
// Revision 1.19  2007/01/15 00:23:50  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.18  2007/01/03 15:37:38  fperrad
// + Added Lua generator.
// + Added -reflect option for Lua, Perl, Python and Ruby code generation
//
// Revision 1.17  2006/09/23 14:28:18  cwrapp
// Final SMC, v. 4.3.3 check-in.
//
// Revision 1.16  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.15  2006/07/11 18:20:00  cwrapp
// Added -headerd option. Improved command line processing.
//
// Revision 1.14  2006/04/22 12:45:26  cwrapp
// Version 4.3.1
//
// Revision 1.13  2005/11/07 19:34:54  cwrapp
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
// Revision 1.12  2005/09/19 15:20:03  cwrapp
// Changes in release 4.2.2:
// New features:
//
// None.
//
// Fixed the following bugs:
//
// + (C#) -csharp not generating finally block closing brace.
//
// Revision 1.11  2005/09/14 01:51:33  cwrapp
// Changes in release 4.2.0:
// New features:
//
// None.
//
// Fixed the following bugs:
//
// + (Java) -java broken due to an untested minor change.
//
// Revision 1.10  2005/08/26 15:21:34  cwrapp
// Final commit for release 4.2.0. See README.txt for more information.
//
// Revision 1.9  2005/07/07 12:08:44  fperrad
// Added C, Perl & Ruby generators.
//
// Revision 1.8  2005/06/30 10:44:23  cwrapp
// Added %access keyword which allows developers to set the generate Context
// class' accessibility level in Java and C#.
//
// Revision 1.7  2005/06/18 18:28:42  cwrapp
// SMC v. 4.0.1
//
// New Features:
//
// (No new features.)
//
// Bug Fixes:
//
// + (C++) When the .sm is in a subdirectory the forward- or
//   backslashes in the file name are kept in the "#ifndef" in the
//   generated header file. This is syntactically wrong. SMC now
//   replaces the slashes with underscores.
//
// + (Java) If %package is specified in the .sm file, then the
//   generated *Context.java class will have package-level access.
//
// + The Programmer's Manual had incorrect HTML which prevented the
//   pages from rendering correctly on Internet Explorer.
//
// + Rewrote the Programmer's Manual section 1 to make it more
//   useful.
//
// Revision 1.6  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.8  2005/02/21 15:34:25  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.7  2005/02/21 15:09:07  charlesr
// Added -python and -return command line options. Also added an
// undocuments option -vverbose which causes the SmcParser and
// SmcLexer FSMs to enter verbose mode.
//
// Revision 1.6  2005/02/03 16:26:44  charlesr
// SMC now implements the Visitor pattern. The parser returns
// an SmcFSM object which is an SmcElement subclass. SMC then
// creates the appropriate visitor object based on the target
// language and passes the visitor to SmcElement.accept().
// This starts the code generation process.
//
// One minor point: the lexer and parser objects no longer
// write warning and error messages directly to System.err.
// Instead, these messages are collected as SmcMessage objects.
// It is then up to the application calling the parser to
// decide how to display this information. Now the SMC
// application writes these messages to System.err as before.
// This change allows the parser to be used in different
// applications.
//
// Revision 1.5  2004/10/30 16:02:24  charlesr
// Added Graphviz DOT file generation.
// Changed version to 3.2.0.
//
// Revision 1.4  2004/10/08 18:56:07  charlesr
// Update version to 3.1.2.
//
// Revision 1.3  2004/10/02 19:50:24  charlesr
// Updated version string.
//
// Revision 1.2  2004/09/06 16:39:16  charlesr
// Added -verbose and -d options. Added C# support.
//
// Revision 1.1  2004/05/31 13:52:56  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:02:45  charlesr
// Initial revision
//
