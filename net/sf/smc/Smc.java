//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy
// of the License at http://www.mozilla.org/MPL/
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
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class Smc
{
//---------------------------------------------------------------
// Member Methods
//

    public static void main(String[] args)
    {
        int retcode = 0;

        _appName = new String("smc");
        _errorMsg = new String();

        // The default smc output level is 1.
        _targetLanguage = LANG_NOT_SET;
        _version = VERSION;
        _debug = false;
        _nostreams = false;
        _sync = false;
        _noex = false;
        _nocatch = false;
        _serial = false;
        _castType = "dynamic_cast";
        _graphLevel = NO_GRAPH_LEVEL;
        _sourceFileList = (List) new LinkedList();
        _verbose = false;
        _fsmVerbose = false;
        _return = false;

        // Process the command line.
        if (parseArgs(args) == false)
        {
            retcode = 1;
            System.err.println(_appName + ": " + _errorMsg);
            _usage(System.err);
        }
        // Arguments check out - start compiling..
        else
        {
            SmcLexer lexer;
            SmcParser parser;
            SmcFSM fsm;
            Iterator sit;
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
                                _targetLanguage);

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
    }

    public static String getSourceFileName()
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

    public static String getCastType()
    {
        return (_castType);
    }

    public static int getGraphLevel()
    {
        return (_graphLevel);
    }

    // Merge two lists together, returning an ordered list with
    // no multiple entries.
    public static List merge(List l1, List l2, Comparator c)
    {
        int result;
        Iterator it1;
        Iterator it2;
        Object e1;
        Object e2;
        List retval = (List) new LinkedList();

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
            byte[] s2 = s.getBytes();
            int index;

            for (index = 0; index < s2.length; ++index)
            {
                if (s2[index] == '\\' || s2[index] == '"')
                {
                    buffer.append('\\');
                }

                buffer.append(s2[index]);
            }

            retval = buffer.toString();
        }

        return (retval);
    }

    // Outputs a list of warning and error messages.
    public static void outputMessages(String srcFileName,
                                      PrintStream stream,
                                      List messages)
    {
        Iterator mit;
        SmcMessage message;

        for (mit = messages.iterator(); mit.hasNext() == true;)
        {
            message = (SmcMessage) mit.next();

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
        boolean castFlag = false;
        boolean retcode;

        // Parse all options first. Keep going until an error is
        // encountered or there are no more options left.
        for (i = 0, retcode = true, argsConsumed = 0;
             i < args.length &&
                     retcode == true &&
                     args[i].startsWith("-") == true;
             i += argsConsumed, argsConsumed = 0)
        {
            if (args[i].startsWith("-sy") == true)
            {
                _sync = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-su") == true)
            {
                // -suffix should be followed by a suffix.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        "-suffix not followed by a value";
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
                    _errorMsg = "-cast not followed by a value";
                }
                else
                {
                    _castType = args[i+1];
                    castFlag = true;
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
                    _errorMsg = "-d not followed by directory";
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
            else if (args[i].startsWith("-gl") == true)
            {
                // -glevel should be followed by an integer.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _errorMsg =
                        "-glevel not followed by integer";
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
                                "-glevel must be 0, 1 or 2";
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
                            "-glevel not followed by valid integer";
                    }
                }
            }
            else if (args[i].equals("-g") == true)
            {
                _debug = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-nos") == true)
            {
                _nostreams = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-noe") == true)
            {
                // -noex is a flag.
                _noex = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-noc") == true)
            {
                // -nocatch is a flag.
                _nocatch = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-return") == true)
            {
                // -return is a flag.
                _return = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-se") == true)
            {
                // -serial is a flag.
                _serial = true;
                argsConsumed = 1;
            }
            else if (args[i].startsWith("-c+") == true)
            {
                // Only one target language can be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != C_PLUS_PLUS)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = C_PLUS_PLUS;

                    if (_suffix == null)
                    {
                        _suffix = "cpp";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-j") == true)
            {
                // Only one target language can be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != JAVA)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = JAVA;

                    if (_suffix == null)
                    {
                        _suffix = "java";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-tc") == true)
            {
                // Only one target language can be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != TCL)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = TCL;

                    if (_suffix == null)
                    {
                        _suffix = "tcl";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-ta") == true)
            {
                // Only one target language can be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != TABLE)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = TABLE;

                    if (_suffix == null)
                    {
                        _suffix = "html";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].equals("-vb") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != VB)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = VB;

                    if (_suffix == null)
                    {
                        _suffix = "vb";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-cs") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != C_SHARP)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = C_SHARP;

                    if (_suffix == null)
                    {
                        _suffix = "cs";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-py") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != PYTHON)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = PYTHON;

                    if (_suffix == null)
                    {
                        _suffix = "py";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-pe") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != PERL)
                {
                    retcode = false;
                    _errorMsg = "Only one target language can be specified";
                }
                else
                {
                    _targetLanguage = PERL;

                    if (_suffix == null)
                    {
                        _suffix = "pm";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-ru") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != RUBY)
                {
                    retcode = false;
                    _errorMsg = "Only one target language can be specified";
                }
                else
                {
                    _targetLanguage = RUBY;

                    if (_suffix == null)
                    {
                        _suffix = "rb";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].equals("-c") == true)
            {
                // Only one target language can be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != C)
                {
                    retcode = false;
                    _errorMsg = "Only one target language can be specified";
                }
                else
                {
                    _targetLanguage = C;

                    if (_suffix == null)
                    {
                        _suffix = "c";
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-gr") == true)
            {
                // Only one target language may be specified.
                if (_targetLanguage != LANG_NOT_SET &&
                    _targetLanguage != GRAPH)
                {
                    retcode = false;
                    _errorMsg =
                        "Only one target language may be specified";
                }
                else
                {
                    _targetLanguage = GRAPH;

                    if (_suffix == null)
                    {
                        _suffix = "dot";
                    }

                    // If the graph level is no specified, then
                    // set it to level 0.
                    if (_graphLevel == NO_GRAPH_LEVEL)
                    {
                        _graphLevel = GRAPH_LEVEL_0;
                    }

                    argsConsumed = 1;
                }
            }
            else if (args[i].startsWith("-vers") == true)
            {
                System.out.println(_appName + " " + _version);
                System.exit(0);
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
            else if (args[i].startsWith("-h") == true)
            {
                _usage(System.out);
                System.exit(0);
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
        if (retcode == true)
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
                    if (args[i].endsWith(".sm") == false &&
                        args[i].endsWith(".SM") == false)
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

        // Before returning, verify that a target programming
        // language was selected.
        if (retcode == true && _targetLanguage == LANG_NOT_SET)
        {
            retcode = false;
            _errorMsg = "Target language was not specified.";
        }
        // Also verify that if the sync flag was given, then
        // the target language is Java.
        else if (retcode == true &&
                 _sync == true &&
                 _targetLanguage != JAVA &&
                 _targetLanguage != VB &&
                 _targetLanguage != C_SHARP)
        {
            retcode = false;
            _errorMsg =
                "-sync can only be used with -java, -vb and -csharp.";
        }
        // Verify that -nostreams flag is used only with -c++.
        else if (retcode == true &&
                 _nostreams == true &&
                 _targetLanguage != C_PLUS_PLUS)
        {
            retcode = false;
            _errorMsg =
                "-nostreams can only be used with -c++.";
        }
        // Verify that the -noex flag is used only with -c++.
        else if (retcode == true &&
                 _noex == true &&
                 _targetLanguage != C_PLUS_PLUS)
        {
            retcode = false;
            _errorMsg = "-noex can only be used with -c++.";
        }
        // Verify that the -cast flag is used only with -c++.
        else if (retcode == true &&
                 castFlag == true &&
                 _targetLanguage != C_PLUS_PLUS)
        {
            retcode = false;
            _errorMsg = "-cast can only be used with -c++.";
        }
        // Verify that the cast type is valid.
        else if (retcode == true &&
                 castFlag == true &&
                 _castType.equals("dynamic_cast") == false &&
                 _castType.equals("static_cast") == false &&
                 _castType.equals("reinterpret_cast") == false)
        {
            retcode = false;
            _errorMsg = "\"" +
                         _castType +
                         "\" is an invalid C++ cast type.";
        }
        // Verify that -glevel is used only with -graph.
        else if (retcode == true &&
                 _graphLevel >= GRAPH_LEVEL_0 &&
                 _targetLanguage != GRAPH)
        {
            retcode = false;
            _errorMsg = "-glevel can only be used with -graph.";
        }

        return (retcode);
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
        stream.print(_appName);
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
        stream.print(" [-cast cast_type]");
        stream.print(" [-d directory]");
        stream.print(" [-glevel int]");
        stream.print(" {-c | -c++ | -java | -tcl | -vb | -csharp | ");
        stream.print("-python | -perl | -ruby | -table | -graph}");
        stream.println(" statemap_file");
        stream.println("    where:");
        stream.println(
            "\t-suffix   Add this suffix to output file");
        stream.println(
            "\t-g        Add debugging to generated code");
        stream.print("\t-nostreams Do not use C++ iostreams ");
        stream.println("(use with -c++ only)");
        stream.print("\t-version  Print smc version ");
        stream.println("information to standard out and exit");
        stream.print("\t-verbose ");
        stream.println("Output more compiler messages.");
        stream.print("\t-help     Print this message to ");
        stream.println("standard out and exit");
        stream.print(
            "\t-sync     Synchronize generated Java code ");
        stream.println("(use with -java, -vb and -csharp only)");
        stream.print(
            "\t-noex     Do not generate C++ exception throws ");
        stream.println("(use with -c++ only)");
        stream.print(
            "\t-nocatch  Do not generate try/catch/rethrow ");
        stream.println("code (not recommended)");
        stream.println(
            "\t-serial   Generate serialization code");
        stream.println("\t-return   Smc.main() returns");
        stream.println("            Use this option with ANT");
        stream.print("\t-cast     Use this C++ cast type ");
        stream.println("(use with -c++ only)");
        stream.println(
            "\t-d        Place generated files in directory");
        stream.println(
            "\t-glevel   Detail level from 0 (least) to 2 (greatest)");
        stream.println("\t-c        Generate C code");
        stream.println("\t-c++      Generate C++ code");
        stream.println("\t-java     Generate Java code");
        stream.println("\t-tcl      Generate [incr Tcl] code");
        stream.println("\t-vb       Generate VB.Net code");
        stream.println("\t-csharp   Generate C# code");
        stream.println("\t-python   Generate Python code");
        stream.println("\t-perl     Generate Perl code");
        stream.println("\t-ruby     Generate Ruby code");
        stream.println("\t-table    Generate HTML table code");
        stream.println("\t-graph    Generate GraphViz DOT file");
        stream.println();
        stream.println(
            "    Note: statemap_file must end in \".sm\"");
        stream.print(
            "    Note: must select one of -c++, -java, -tcl, ");
        stream.print("-vb, -csharp, -perl, -python, -ruby or ");
        stream.println("-table or -graph.");

        return;
    }

    // Returns the <name> portion from <path>/<name>.sm.
    private static String _getFileName(String fullName)
    {
        File file = new File(fullName);
        String fileName = file.getName();

        // Note: this works because the file name's form
        // has already been validated as ending in .sm.
        return (fileName.substring(0, fileName.indexOf(".sm")));
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
        String srcFileName = "";
        FileOutputStream sourceFileStream = null;
        PrintStream sourceStream;
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

        // Open the output files. The file names
        // are based on the input file name.
        switch (_targetLanguage)
        {
            // For C++ two files are generated: the .h and the
            // .cpp.
            case C_PLUS_PLUS:
                // Generate the header file first.
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm.h";
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                fsm.accept(
                    new SmcHeaderGenerator(
                        sourceStream, srcFileBase));
                sourceFileStream.flush();
                sourceFileStream.close();

                if (_verbose == true)
                {
                    System.out.print("[wrote ");
                    System.out.print(srcFileName);
                    System.out.println("]");
                }

                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcCppGenerator(
                        sourceStream, srcFileBase);
                break;

            case C:
                // Generate the header file first.
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm.h";
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                fsm.accept(
                    new SmcHeaderCGenerator(
                        sourceStream, srcFileBase));
                sourceFileStream.flush();
                sourceFileStream.close();

                if (_verbose == true)
                {
                    System.out.print("[wrote ");
                    System.out.print(srcFileName);
                    System.out.println("]");
                }

                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcCGenerator(
                        sourceStream, srcFileBase);
                break;

            case JAVA:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "Context." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcJavaGenerator(
                        sourceStream, srcFileBase);
                break;

            case TCL:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcTclGenerator(
                        sourceStream, srcFileBase);
                break;

            case VB:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcVBGenerator(
                        sourceStream, srcFileBase);
                break;

            case C_SHARP:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcCSharpGenerator(
                        sourceStream, srcFileBase);
                break;

            case PYTHON:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcPythonGenerator(
                        sourceStream, srcFileBase);
                break;

            case PERL:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcPerlGenerator(
                        sourceStream, srcFileBase);
                break;

            case RUBY:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcRubyGenerator(
                        sourceStream, srcFileBase);
                break;

            case TABLE:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcTableGenerator(
                        sourceStream, srcFileBase);
                break;

            case GRAPH:
                srcFileName =
                    srcFilePath +
                    srcFileBase +
                    "_sm." +
                    _suffix;
                sourceFileStream =
                    new FileOutputStream(srcFileName);
                sourceStream =
                    new PrintStream(sourceFileStream);
                generator =
                    new SmcGraphGenerator(
                        sourceStream, srcFileBase);
                break;
        }

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
// Member Data
//

    //-----------------------------------------------------------
    // Statics.
    //

    // This applications print name.
    private static String _appName;

    // The source file currently being compiled.
    private static String _sourceFileName;

    // The state map source code to be compiled.
    private static List _sourceFileList;

    // Append this suffix to the end of the output file.
    private static String _suffix;

    // Place the output files in this directory. May be null.
    private static String _outputDirectory;

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

    //-----------------------------------------------------------
    // Constants.
    //

    // Specifies target programming language.
    /* package */ static int _targetLanguage;
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

    private static final String VERSION = "v. 4.0.2";
}

//
// CHANGE LOG
// $Log$
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
