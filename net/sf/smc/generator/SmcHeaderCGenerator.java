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
 * @see SmcOptions
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
     * Creates a C header code generator for the given options.
     * @param options The target code generator options.
     */
    public SmcHeaderCGenerator(final SmcOptions options)
    {
        super (options, "h");
    } // end of SmcHeaderCGenerator(SmcOptions)

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
        String targetfileCaps;
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String fsmClassName = fsm.getFsmClassName();
        String startStateName = fsm.getStartState();
        String cState;
        List<SmcTransition> transList;
        List<SmcParameter> params;
        int index;

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
            fsmClassName = packageName + "_" + fsmClassName;
            startStateName = packageName + "_" + startStateName;
        }

        // The first two lines in the header file should be:
        //
        //    #ifndef _<source file name>_H
        //    #define _<source file name>_H
        //
        // where the source file name is all in caps.
        // The last line is:
        //
        //    #endif
        //

        // Make the file name upper case and replace
        // slashes with underscores.
        targetfileCaps = _targetfileBase.replace('\\', '_');
        targetfileCaps = targetfileCaps.replace('/', '_');
        targetfileCaps = targetfileCaps.toUpperCase();
        _source.print("#ifndef _");
        _source.print(targetfileCaps);
        _source.println("_H");
        _source.print("#define _");
        _source.print(targetfileCaps);
        _source.println("_H");

        // Include required standard .h files.
        _source.println();
        _source.println("#include <assert.h>");

        if (_debugLevel >= DEBUG_LEVEL_0)
        {
            _source.println("#define STATEMAP_DEBUG 1");
        }
        _source.println("#include <statemap.h>");

        _source.println();

        // Do user-specified forward declarations now.
        for (String declaration: fsm.getDeclarations())
        {
            _source.print(declaration);
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
        _source.println("State {");

        // Add the default Entry() and Exit() definitions.
        if (fsm.hasEntryActions() == true)
        {
            _source.print("    void(*Entry)(struct ");
            _source.print(fsmClassName);
            _source.println(" *const fsm);");
        }
        if (fsm.hasExitActions() == true)
        {
            _source.print("    void(*Exit)(struct ");
            _source.print(fsmClassName);
            _source.println(" *const fsm);");
        }

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
                _source.print(" *const fsm");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getType());
                    _source.print(" ");
                    _source.print(param.getName());
                }

                _source.println(");");
            }
        }
        _source.print("    void(*Default)(struct ");
        _source.print(fsmClassName);
        _source.println(" *const fsm);");

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
        _source.println(" {");
        _source.print("    struct ");
        _source.print(context);
        _source.println(" *_owner;");
        _source.print("    FSM_MEMBERS(");
        _source.print(context);
        _source.println(")");

        // Put the closing brace on the context class.
        _source.println("};");
        _source.println();

        // The state name "map::state" must be changed to
        // "map_state".
        if ((index = startStateName.indexOf("::")) >= 0)
        {
            cState =
                    startStateName.substring(0, index) +
                    "_" +
                startStateName.substring(index + 2);
        }
        else
        {
            cState = startStateName;
        }

        _source.print("#ifdef NO_");
        _source.print(targetfileCaps);
        _source.println("_MACRO");

        // Constructor
        _source.print("extern void ");
        _source.print(fsmClassName);
        _source.print("_Init");
        _source.print("(struct ");
        _source.print(fsmClassName);
        _source.print(" *const fsm, struct ");
        _source.print(context);
        _source.println(" *const owner);");

        // EnterStartState method.
        if (fsm.hasEntryActions() == true)
        {
            _source.print("extern void ");
            _source.print(fsmClassName);
            _source.print("_EnterStartState(struct ");
            _source.print(fsmClassName);
            _source.println(" *const fsm);");
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
                _source.print(" *const fsm");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getType());
                    _source.print(" ");
                    _source.print(param.getName());
                }
                _source.println(");");
            }
        }

        _source.println("#else");

        // Constructor
        _source.print("#define ");
        _source.print(fsmClassName);
        _source.println("_Init(fsm, owner) \\");
        _source.print("    FSM_INIT((fsm), &");
        _source.print(cState);
        _source.println("); \\");
        _source.println("    (fsm)->_owner = (owner)");

        // EnterStartState method.
        if (fsm.hasEntryActions() == true)
        {
            _source.println();
            _source.print("#define ");
            _source.print(fsmClassName);
            _source.println("_EnterStartState(fsm) \\");
            _source.println("    ENTRY_STATE(getState(fsm))");
        }

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print("#define ");
                _source.print(fsmClassName);
                _source.print("_");
                _source.print(trans.getName());
                _source.print("(fsm");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(") \\");

                _source.println("    assert(getState(fsm) != NULL); \\");
                _source.print("    setTransition((fsm), \"");
                _source.print(trans.getName());
                _source.println("\"); \\");
                _source.print("    getState(fsm)->");
                _source.print(trans.getName());
                _source.print("((fsm)");
                for (SmcParameter param: params)
                {
                    _source.print(", (");
                    _source.print(param.getName());
                    _source.print(")");
                }
                _source.println("); \\");
                _source.println("    setTransition((fsm), NULL)");
            }
        }

        _source.println("#endif");
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
// Revision 1.14  2014/08/30 07:12:41  fperrad
// refactor C generation
//
// Revision 1.13  2012/04/21 10:04:06  fperrad
// fix 3518773 : remove additional ';' with '%declare'
//
// Revision 1.12  2012/01/28 18:03:02  fperrad
// fix 3476060 : generate both C functions and macros
//
// Revision 1.11  2010/12/01 15:29:09  fperrad
// C: refactor when package
//
// Revision 1.10  2010/09/21 08:16:00  fperrad
// refactor C generation
//
// Revision 1.9  2010/02/15 18:05:43  fperrad
// fix 2950619 : make distinction between source filename (*.sm) and target filename.
//
// Revision 1.8  2009/11/25 22:30:19  cwrapp
// Fixed problem between %fsmclass and sm file names.
//
// Revision 1.7  2009/11/24 20:42:39  cwrapp
// v. 6.0.1 update
//
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
//
