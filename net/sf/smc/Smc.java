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
// $Log$
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
// Revision 1.5  2001/10/12 14:28:04  cwrapp
// SMC v. 1.0.1
//
// Revision 1.4  2001/06/26 22:00:17  cwrapp
// Changes in release 1.0.0:
// Checking in code for first production release.
// If SMC should crash, critical information are printed out along
// with instructions explaining where to send that information.
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
// Revision 1.2  2000/09/01 15:32:03  charlesr
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
// Revision 1.1.1.1  2000/08/02 12:50:55  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public final class Smc
{
// Member Methods

    public static void main(String[] args)
    {
        int retcode = 0;

        _app_name = new String("smc");
        _error_msg = new String();
        _usage =
                new String("usage: " +
                           _app_name +
                           " [-suffix suffix]" +
                           " [-g]" +
                           " [-version]" +
                           " [-help]" +
                           " [-sync]" +
                           " {-c++ | -java | -tcl}" +
                           " statemap_file\n" +
                           "    where:\n" +
                           "\t-suffix   Add this suffix to output file\n" +
                           "\t-g        Add debugging to generated code\n" +
                           "\t-tq       Add transition queue support\n" +
                           "\t-version  Print smc version information to standard out and exit\n" +
                           "\t-help     Print this message to standard out and exit\n" +
                           "\t-sync     Synchronize generated Java code (use with -java only)\n" +
                           "\t-c++      Generate C++ code\n" +
                           "\t-java     Generate Java code\n" +
                           "\t-tcl      Generate [incr Tcl] code\n" +
                           "\n    Note: statemap_file must end in \".sm\"");

        // The default smc output level is 1.
        _target_language = LANG_NOT_SET;
        _version = "v. 1.1.0";
        _source_file_name = null;
        _debug = false;
        _sync = false;
        _trans_queue = false;

        // Process the command line.
        if (parseArgs(args) == false)
        {
            retcode = 1;
            System.out.println(_app_name + ": " + _error_msg);
        }
        // Arguments check out - start compiling..
        else
        {
            SmcLexer lexer = new SmcLexer(_source_file_stream);
            SmcParser parser = new SmcParser(lexer, _target_language);
            SmcParseTree parse_tree;

            try
            {
                // First - do the parsing
                if ((parse_tree = parser.parse()) == null ||
                    parse_tree.check() == false)
                {
                    retcode = 1;
                }
                else
                {
                    // Second - do the code generation.
                    // Open the output files. The file names
                    // are based on the input file name.
                    // Strip the ".sm" from the source file's name.
                    int endIndex = _source_file_name.length() - 3;
                    String srcFileBase =
                            _source_file_name.substring(0, endIndex);
                    String headerFileName;
                    String srcFileName = "";
                    FileOutputStream headerFileStream;
                    FileOutputStream sourceFileStream;
                    PrintStream headerStream = null;
                    PrintStream sourceStream;

                    // For some strange reason I get the wrong
                    // line separator character when I use Java
                    // on Windows. Set the line separator to "\n"
                    // and all is well.
                    System.setProperty("line.separator", "\n");

                    // Strip away any preceding directories from
                    // the source file name.
                    srcFileBase =
                            srcFileBase.substring(srcFileBase.lastIndexOf(File.separatorChar) + 1);

                    switch (_target_language)
                    {
                        case C_PLUS_PLUS:
                            headerFileName = srcFileBase + "_sm.h";
                            srcFileName = srcFileBase +
                                    "_sm." +
                                    _suffix;

                            // Create the header output streams.
                            headerFileStream =
                                    new FileOutputStream(headerFileName);
                            headerStream =
                                    new PrintStream(headerFileStream);
                            break;

                        case JAVA:
                            srcFileName = srcFileBase +
                                          "Context." +
                                          _suffix;
                            break;

                        case TCL:
                            srcFileName = srcFileBase +
                                          "_sm." +
                                          _suffix;
                            break;
                    }

                    // Open the source output stream.
                    sourceFileStream = new FileOutputStream(srcFileName);
                    sourceStream = new PrintStream(sourceFileStream);

                    parse_tree.generateCode(headerStream,
                                            sourceStream,
                                            srcFileBase);

                    sourceFileStream.flush();
                    sourceFileStream.close();
                }
            }
            catch (Exception e)
            {
                retcode = 1;

                System.err.println("SMC has exprienced a fatal error. Please e-mail the following error output to rapp@acm.org. Thank you.\n");
                System.err.println("--------------------------------------------------------------------------------");
                System.err.println("SMC version: " + _version);
                System.err.println("JRE version: v. " +
                                   System.getProperty("java.version"));
                System.err.println("JRE vender: " +
                                   System.getProperty("java.vendor") +
                                   " (" +
                                   System.getProperty("java.vendor.url") +
                                   ")");
                System.err.println("JVM: " +
                                   System.getProperty("java.vm.name") +
                                   ", v. " +
                                   System.getProperty("java.vm.version"));
                System.err.println("JVM vender: " +
                                   System.getProperty("java.vm.vendor"));
                System.err.println("Exception:\n");
                e.printStackTrace();
                System.err.println("--------------------------------------------------------------------------------");
            }
        }

        // Need to return the appropriate exit code in case SMC
        // is called by make. Just doing a return always results
        // in a zero return code.
        System.exit(retcode);
    }

    public static boolean isDebug()
    {
        return (_debug);
    }

    public static boolean isSynchronized()
    {
        return (_sync);
    }

    public static boolean isTransQueue()
    {
        /* Transition queuing is now turned off. This method
         * will always return false.
        return (_trans_queue);
         */
        return (false);
    }

    // Merge two lists together, returning an ordered list with
    // no multiple entries.
    public static List merge(List l1, List l2, Comparator c)
    {
        int result;
        ListIterator it1;
        ListIterator it2;
        Object e1;
        Object e2;
        List retval = (List) new LinkedList();

        // First, make certain that both lists are sorted.
        Collections.sort(l1, c);
        Collections.sort(l2, c);

        // Now merge the two lists together.
        // Continue until the end of either list is reached.
        for (it1 = l1.listIterator(),
                     it2 = l2.listIterator(),
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

    private static boolean parseArgs(String[] args)
    {
        int i;
        int args_consumed;
        boolean retcode;

        // Parse all options first. Keep going until an error is
        // encountered or there are no more options left.
        for (i = 0, retcode = true, args_consumed = 0;
             i < args.length &&
                     retcode == true &&
                     args[i].startsWith("-") == true;
             i += args_consumed, args_consumed = 0)
        {
            if (args[i].startsWith("-sy") == true)
            {
                _sync = true;
                args_consumed = 1;
            }
            else if (args[i].startsWith("-s") == true)
            {
                // -suffix should be followed by a suffix.
                if ((i + 1) == args.length ||
                    args[i+1].startsWith("-") == true)
                {
                    retcode = false;
                    _error_msg = "-suffix not followed by a value";
                }
                else
                {
                    _suffix = args[i+1];
                    args_consumed = 2;
                }
            }
            else if (args[i].startsWith("-g") == true)
            {
                _debug = true;
                args_consumed = 1;
            }
            /* Transition queuing (particularly when transition
             * parameters are being used) are VERY difficult
             * to use properly. I believe that application
             * objects must be cognizant of when it is within
             * a transition and take the proper steps. That is
             * why I have added a public "isInTransition()"
             * method to FSMContext.
             *
             * I am leaving the code in place.
             *
            else if (args[i].startsWith("-tq") == true)
            {
                _trans_queue = true;
                args_consumed = 1;
            }
             */
            else if (args[i].startsWith("-c") == true)
            {
                // Only one target language can be specified.
                if (_target_language != LANG_NOT_SET &&
                    _target_language != C_PLUS_PLUS)
                {
                    retcode = false;
                    _error_msg = "Only one target language can be specified";
                }
                else
                {
                    _target_language = C_PLUS_PLUS;

                    if (_suffix == null)
                    {
                        _suffix = "cpp";
                    }

                    args_consumed = 1;
                }
            }
            else if (args[i].startsWith("-j") == true)
            {
                // Only one target language can be specified.
                if (_target_language != LANG_NOT_SET &&
                    _target_language != JAVA)
                {
                    retcode = false;
                    _error_msg = "Only one target language can be specified";
                }
                else
                {
                    _target_language = JAVA;

                    if (_suffix == null)
                    {
                        _suffix = "java";
                    }

                    args_consumed = 1;
                }
            }
            else if (args[i].startsWith("-t") == true)
            {
                // Only one target language can be specified.
                if (_target_language != LANG_NOT_SET &&
                    _target_language != TCL)
                {
                    retcode = false;
                    _error_msg = "Only one target language can be specified";
                }
                else
                {
                    _target_language = TCL;

                    if (_suffix == null)
                    {
                        _suffix = "tcl";
                    }

                    args_consumed = 1;
                }
            }
            else if (args[i].startsWith("-v") == true)
            {
                System.out.println(_app_name + " " + _version);
                System.exit(0);
            }
            else if (args[i].startsWith("-h") == true)
            {
                System.out.println(_usage);
                System.exit(0);
            }
            else
            {
                retcode = false;
                _error_msg = "Unknown option (" +
                             args[i] +
                             ")";
            }
        }

        // Was a state map source file given? It must be the
        // last argument in the list.
        if (retcode == true)
        {
            if ((i + 1) < args.length)
            {
                retcode = false;
                _error_msg = "Extra arguments beyond source file";
            }
            else if (i == args.length)
            {
                retcode = false;
                _error_msg = "Missing source file";
            }
            // The file name must end in ".sm".
            else if (args[i].endsWith(".sm") == false)
            {
                retcode = false;
                _error_msg = "Source file name must end in \".sm\" (" +
                             args[i] +
                             ")";
            }
            else
            {
                _source_file_name = args[i];
                File source_file = new File(args[i]);
                if (source_file.exists() == false)
                {
                    retcode = false;
                    _error_msg = "No such file named \"" +
                                 args[i] +
                                 "\"";
                }
                else if (source_file.canRead() == false)
                {
                    retcode = false;
                    _error_msg = "Source file \"" +
                                 args[i] +
                                 "\" is not readable";
                }
                else
                {
                    try
                    {
                        _source_file_stream =
                                new FileInputStream(source_file);
                    }
                    catch (FileNotFoundException e)
                    {
                        // Ignore.
                    }
                }
            }
        }

        // Before returning, verify that a target programming
        // language was selected.
        if (retcode == true && _target_language == LANG_NOT_SET)
        {
            retcode = false;
            _error_msg = "Target language was not specified.";
        }
        // Also verify that the if the sync flag was given, then
        // the target language is Java.
        else if (retcode == true &&
                 _sync == true &&
                 _target_language != JAVA)
        {
            retcode = false;
            _error_msg = "-sync can only be used with -java.";
        }

        return (retcode);
    }

    public static String getSourceFileName()
    {
        return (_source_file_name);
    }

// Member Data

    private static String _app_name;

    // The state map source code to be compiled.
    private static String _source_file_name;
    private static FileInputStream _source_file_stream;

    // Append this suffix to the end of the output file.
    private static String _suffix;

    // If true, then generate verbose information.
    private static boolean _debug;

    // If true, then generate thread-safe Java code.
    private static boolean _sync;

    // If true, then add transition queue support to
    // generated code.
    // Transition queuing will not be released!!!!
    // This member data will always be false.
    private static boolean _trans_queue;

    private static String _error_msg;

    // Specifies target programming language.
    /* package */ static int _target_language;
    /* package */ static final int LANG_NOT_SET = 0;
    /* package */ static final int C_PLUS_PLUS = 1;
    /* package */ static final int JAVA = 2;
    /* package */ static final int TCL = 3;

    // Specifies the transition's type.
    /* package */ static final int TRANS_NOT_SET = 0;
    /* package */ static final int TRANS_SET = 1;
    /* package */ static final int TRANS_PUSH = 2;
    /* package */ static final int TRANS_POP = 3;

    // The app's version ID and command line syntax.
    private static String _version;
    private static String _usage;
}
