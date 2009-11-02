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
// Copyright (C) 2005, 2008. Charles W. Rapp.
// All Rights Reserved.
//
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python.
//   Chris Liscio contributed the Objective-C code generation
//   and examples/ObjC.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc.generator;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import net.sf.smc.model.SmcAction;
import net.sf.smc.model.SmcElement;
import net.sf.smc.model.SmcFSM;
import net.sf.smc.model.SmcGuard;
import net.sf.smc.model.SmcMap;
import net.sf.smc.model.SmcParameter;
import net.sf.smc.model.SmcState;
import net.sf.smc.model.SmcTransition;
import net.sf.smc.model.SmcVisitor;

/**
 * Visits the abstract syntax tree emitting a C++ header file.
 * @see SmcElement
 * @see SmcVisitor
 * @see SmcCGenerator
 *
 * @author Francois Perrad
 */

public final class SmcHeaderCGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a C header code generator for the given
     * parameters.
     * @param srcfileBase write the emitted code to this target
     * source file name sans the suffix.
     * @param srcDirectory place the target source file in this
     * directory.
     * @param headerDirectory place the target header file in
     * this directory. Ignored if there is no generated header
     * file.
     * @param castType use this type cast (C++ code generation
     * only).
     * @param graphLevel amount of detail in the generated
     * GraphViz graph (graph code generation only).
     * @param serialFlag if {@code true}, generate unique
     * identifiers for persisting the FSM.
     * @param debugFlag if {@code true} add debug output messages
     * to code.
     * @param noExceptionFlag if {@code true} then use asserts
     * rather than exceptions (C++ only).
     * @param noCatchFlag if {@code true} then do <i>not</i>
     * generate try/catch/rethrow code.
     * @param noStreamsFlag if {@code true} then use TRACE macro
     * for debug output.
     * @param reflectFlag if {@code true} then generate
     * reflection code.
     * @param syncFlag if {@code true} then generate
     * synchronization code.
     * @param genericFlag if {@code true} then use generic
     * collections.
     * @param accessLevel use this access keyword for the
     * generated classes.
     */
    public SmcHeaderCGenerator(final String srcfileBase,
                               final String srcDirectory,
                               final String headerDirectory,
                               final String castType,
                               final int graphLevel,
                               final boolean serialFlag,
                               final boolean debugFlag,
                               final boolean noExceptionFlag,
                               final boolean noCatchFlag,
                               final boolean noStreamsFlag,
                               final boolean reflectFlag,
                               final boolean syncFlag,
                               final boolean genericFlag,
                               final String accessLevel)
    {
        super (srcfileBase,
               "{0}{1}_sm.{2}",
               "h",
               srcDirectory,
               headerDirectory,
               castType,
               graphLevel,
               serialFlag,
               debugFlag,
               noExceptionFlag,
               noCatchFlag,
               noStreamsFlag,
               reflectFlag,
               syncFlag,
               genericFlag,
               accessLevel);
    } // end of SmcHeaderCGenerator(...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcVisitor Abstract Method Impelementation.
    //

    /**
     * Emits C header code for the finite state machine.
     * @param fsm emit Groovy code for this finite state machine.
     */
    public void visit(SmcFSM fsm)
    {
        String srcfileCaps;
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String fsmClassName = fsm.getFsmClassName();
        List<SmcTransition> transList;
        List<SmcParameter> params;

        _source.println("/*");
        _source.println(" * ex: set ro:");
        _source.println(" * DO NOT EDIT.");
        _source.println(" * generated by smc (http://smc.sourceforge.net/)");
        _source.print(" * from file : ");
        _source.print(_srcfileBase);
        _source.println(".sm");
        _source.println(" */");
        _source.println();

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
              context = packageName + "_" + context;
        }

        // The first two lines in the header file should be:
        //
        //    #ifndef _H_<source file name>_SM
        //    #define _H_<source file name>_SM
        //
        // where the source file name is all in caps.
        // The last line is:
        //
        //    #endif
        //

        // Make the file name upper case and replace
        // slashes with underscores.
        srcfileCaps = _srcfileBase.replace('\\', '_');
        srcfileCaps = srcfileCaps.replace('/', '_');
        srcfileCaps = srcfileCaps.toUpperCase();
        _source.print("#ifndef _H_");
        _source.print(srcfileCaps);
        _source.println("_SM");
        _source.print("#define _H_");
        _source.print(srcfileCaps);
        _source.println("_SM");

        // Include required standard .h files.
        _source.println();
        _source.println("#include <statemap.h>");

        _source.println();

        // Do user-specified forward declarations now.
        for (String declaration: fsm.getDeclarations())
        {
            _source.print(declaration);

            // Add a semicolon if the user did not use one.
            if (declaration.endsWith(";") == false)
            {
                _source.print(";");
            }

            _source.println();
        }

        // Forward declare the application class.
        _source.println();
        _source.print("struct ");
        _source.print(context);
        _source.println(";");
        _source.print("struct ");
        _source.print(fsmClassName);
        _source.println(";");

        // Declare user's base state class.
        _source.println();
        _source.print("struct ");
        _source.print(context);
        _source.println("State");
        _source.println("{");

        // Add the default Entry() and Exit() definitions.
        if (fsm.hasEntryActions() == true)
        {
            _source.print("    void(*Entry)(struct ");
            _source.print(fsmClassName);
            _source.println("*);");
        }
        if (fsm.hasExitActions() == true)
        {
            _source.print("    void(*Exit)(struct ");
            _source.print(fsmClassName);
            _source.println("*);");
        }
        _source.println();

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        transList = fsm.getTransitions();

        // Output the global transition declarations.
        for (SmcTransition trans: transList)
        {
            // Don't output the default state here.
            if (trans.getName().equals("Default") == false)
            {
                _source.print("    void(*");
                _source.print(trans.getName());
                _source.print(")(struct ");
                _source.print(fsmClassName);
                _source.print("*");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getType());
                }

                _source.println(");");
            }
        }
        _source.println();
        _source.print("    void(*Default)(struct ");
        _source.print(fsmClassName);
        _source.println("*);");

        _source.println();
        _source.println("    STATE_MEMBERS");

        // The base class has been defined.
        _source.println("};");
        _source.println();

        // Generate the map classes. The maps will, in turn,
        // generate the state classes.
        for (SmcMap map: fsm.getMaps())
        {
            map.accept(this);
        }

        // Generate the FSM context class.
        _source.println();
        _source.print("struct ");
        _source.print(fsmClassName);
        _source.println("");
        _source.println("{");
        _source.print("    FSM_MEMBERS(");
        _source.print(context);
        _source.println(")");
        _source.print("    struct ");
        _source.print(context);
        _source.println(" *_owner;");

        // Put the closing brace on the context class.
        _source.println("};");
        _source.println();

        // Constructor
        _source.print("extern void ");
        _source.print(fsmClassName);
        _source.print("_Init");
        _source.print("(struct ");
        _source.print(fsmClassName);
        _source.print("*, struct ");
        _source.print(context);
        _source.println("*);");

        // EnterStartState method.
        if (fsm.hasEntryActions() == true)
        {
            _source.print("extern void ");
            _source.print(fsmClassName);
            _source.print("_EnterStartState(struct ");
            _source.print(fsmClassName);
            _source.println("*);");
        }

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.print("extern void ");
                _source.print(fsmClassName);
                _source.print("_");
                _source.print(trans.getName());
                _source.print("(struct ");
                _source.print(fsmClassName);
                _source.print("*");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getType());
                }
                _source.println(");");
            }
        }

        _source.println();
        _source.println("#endif");

        _source.println();
        _source.println("/*");
        _source.println(" * Local variables:");
        _source.println(" *  buffer-read-only: t");
        _source.println(" * End:");
        _source.println(" */");

        return;
    } // end of visit(SmcFSM)

    /**
     * Emits C header code for the FSM map.
     * @param map emit Groovy code for this map.
     */
    public void visit(SmcMap map)
    {
        String packageName = map.getFSM().getPackage();
        String context = map.getFSM().getContext();
        String mapName = map.getName();

        // If a package has been specified,
        if (packageName != null && packageName.length() > 0)
        {
              context = packageName + "_" + context;
        }

        for (SmcState state: map.getStates())
        {
            _source.print("extern const struct ");
            _source.print(context);
            _source.print("State ");
            if (packageName != null && packageName.length() > 0)
            {
                _source.print(packageName);
                _source.print("_");
            }
            _source.print(mapName);
            _source.print("_");
            _source.print(state.getInstanceName());
            _source.println(";");
        }

        return;
    } // end of visit(SmcMap)

    //
    // end of SmcVisitor Abstract Method Impelementation.
    //-----------------------------------------------------------

//---------------------------------------------------------------
// Member data
//
} // end of class SmcHeaderCGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.6  2009/11/02 09:57:43  fperrad
// fix C generation
//
// Revision 1.5  2009/09/12 21:44:49  kgreg99
// Implemented feature req. #2718941 - user defined generated class name.
// A new statement was added to the syntax: %fsmclass class_name
// It is optional. If not used, generated class is called as before "XxxContext" where Xxx is context class name as entered via %class statement.
// If used, generated class is called asrequested.
// Following language generators are touched:
// c, c++, java, c#, objc, lua, groovy, scala, tcl, VB
// This feature is not tested yet !
// Maybe it will be necessary to modify also the output file name.
//
// Revision 1.4  2009/09/05 15:39:20  cwrapp
// Checking in fixes for 1944542, 1983929, 2731415, 2803547 and feature 2797126.
//
// Revision 1.3  2009/03/27 15:26:55  fperrad
// C : the function Context_EnterStartState is generated only if FSM hasEntryActions
//
// Revision 1.2  2009/03/27 09:41:47  cwrapp
// Added F. Perrad changes back in.
//
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.12  2008/07/14 09:31:24  fperrad
// + Added the generation of read-only macro for Vi & Emacs
//
// Revision 1.11  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.10  2008/02/08 08:46:02  fperrad
// C : optimize footprint when no Entry action or no Exit action
//
// Revision 1.9  2007/08/05 14:50:54  cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more information.
//
// Revision 1.8  2007/08/05 14:29:44  cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more informaiton.
//
// Revision 1.7  2007/07/16 06:13:21  fperrad
// + Added the generation of a DO NOT EDIT comment
//
// Revision 1.6  2007/02/21 13:55:02  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.5  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.4  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.3  2005/11/07 19:34:54  cwrapp
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
// Revision 1.2  2005/07/07 12:07:28  fperrad
// When the .sm is in a subdirectory the forward- or backslashes in the file name are kept in the "#ifndef" in the generated header file. This is syntactically wrong. SMC nowreplaces the slashes with underscores.
//
// Revision 1.1  2005/06/16 18:11:01  fperrad
// Added C, Perl & Ruby generators.
//
//
