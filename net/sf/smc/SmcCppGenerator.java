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
// Copyright (C) 2005, 2006. Charles W. Rapp.
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

package net.sf.smc;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Visits the abstract syntax tree emitting C++ code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcCppGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcCppGenerator(String srcfileBase)
    {
        super (srcfileBase, "{0}{1}_sm.{2}", "cpp");
    } // end of SmcCppGenerator(String)

    // This method generates the following code:
    //
    // %{ %} raw source code - if any
    //
    // #include <%include header file>
    // #include "<context>_sm.h"
    // (If the -headerd option is used, then this is generated:
    // #include "<header dir>/<context>_sm.h")
    //
    // using namespace statemap;
    // using namespace <%import namespace>
    //
    // (put namespace a { namespace b ... if %package defined.)
    //
    // Static class declarations.
    // <map name>_<state name> <map name>::<state>("<map name>::<state name>", <index>)
    public void visit(SmcFSM fsm)
    {
        String srcDirectory = Smc.outputDirectory();
        String headerDirectory = Smc.headerDirectory();
        String packageName = fsm.getPackage();
        String rawSource = fsm.getSource();
        String context = fsm.getContext();
        String mapName;
        List<SmcTransition> transList;
        String separator;
        List<SmcParameter> params;
        Iterator<SmcMap> mapIt;
        Iterator<SmcState> stateIt;
        String declaration;
        int packageDepth = 0;
        SmcMap map;
        SmcState state;
        int index;

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length() > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // Generate #includes.
        for (String include: fsm.getIncludes())
        {
            _source.print("#include ");
            _source.println(include);
        }

        // Include the context file last.
        // Is the header file included in a different directory
        // than the source file?
        _source.print("#include \"");
        if ((srcDirectory == null && headerDirectory != null) ||
            (srcDirectory != null &&
             headerDirectory != null &&
             srcDirectory.equals(headerDirectory) == false))
        {
            // They are in different directories. Prepend the
            // header directory to the file name.
            _source.print(headerDirectory);
        }
        // Else they are in the same directory.
        else if (srcDirectory != null)
        {
            _source.print(srcDirectory);
        }
        _source.print(_srcfileBase);
        _source.println("_sm.h\"");

        // Import the statemap namespace symbols into the main
        // namespace.
        _source.println();
        _source.println("using namespace statemap;");

        // Do user-specified imports now.
        for (String imp: fsm.getImports())
        {
            _source.print("using namespace ");
            _source.print(imp);
            _source.println(";");
        }

        // End of namespace imports.
        _source.println();

        // Import the user-defined namespace into the main
        // namespace. If the package name is "a::b::c", then
        // this must be converted to:
        // namespace a {
        //   namespace b {
        //     namespace c {
        //       ...
        //     }
        //   }
        // }
        if (packageName != null && packageName.length() > 0)
        {
            _indent = "";
            StringTokenizer tokenizer =
                new StringTokenizer(packageName, "::");
            String token;

            while (tokenizer.hasMoreTokens() == true)
            {
                token = tokenizer.nextToken();
                ++packageDepth;

                _source.print(_indent);
                _source.print("namespace ");
                _source.println(token);
                _source.print(_indent);
                _source.println("{");
                _indent += "    ";
            }
        }

        // Statically declare all derived state classes.
        _source.print(_indent);
        _source.println("// Static class declarations.");
        for (mapIt = fsm.getMaps().iterator(), index = 0;
             mapIt.hasNext() == true;
            )
        {
            map = mapIt.next();
            mapName = map.getName();
            for (stateIt = map.getStates().iterator();
                 stateIt.hasNext() == true;
                 ++index)
            {
                state = stateIt.next();
                _source.print(_indent);
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print(" ");
                _source.print(mapName);
                _source.print("::");
                _source.print(state.getInstanceName());
                _source.print("(\"");
                _source.print(mapName);
                _source.print("::");
                _source.print(state.getClassName());
                _source.print("\", ");
                _source.print(index);
                _source.println(");");
            }
        }

        // v. 2.2.0: If supporting serialization, then set the
        // min and max indices and the state array.
        if (Smc.isSerial() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.print("const int ");
            _source.print(context);
            _source.println("Context::MIN_INDEX = 0;");
            _source.print(_indent);
            _source.print("const int ");
            _source.print(context);
            _source.print("Context::MAX_INDEX = ");
            _source.print(--index);
            _source.println(";");
            _source.print(_indent);
            _source.print(context);
            _source.print("State* ");
            _source.print(context);
            _source.println("Context::_States[] = ");
            _source.print("{");

            for (mapIt = fsm.getMaps().iterator(),
                     separator = "";
                 mapIt.hasNext() == true;
                )
            {
                map = mapIt.next();
                mapName = map.getName();
                for (stateIt = map.getStates().iterator();
                     stateIt.hasNext() == true;
                     separator = ",")
                {
                    state = stateIt.next();
                    _source.print(separator);
                    _source.println();
                    _source.print(_indent);
                    _source.print("    &");
                    _source.print(mapName);
                    _source.print("::");
                    _source.print(state.getClassName());
                }
            }

            _source.println();
            _source.print(_indent);
            _source.println("};");
        }

        // If serialization is supported, then output the valueOf
        // method now.
        if (Smc.isSerial() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.print(context);
            _source.print("State& ");
            _source.print(context);
            _source.println("Context::valueOf(int stateId)");
            _source.print(_indent);
            _source.println("{");
            _source.print(_indent);
            _source.print("    if (stateId < MIN_INDEX || ");
            _source.println("stateId > MAX_INDEX)");
            _source.print(_indent);
            _source.println("    {");

            // If exceptions are not being thrown, then assert
            // instead.
            if (Smc.isNoExceptions() == true)
            {
                _source.print(_indent);
                _source.println("        assert(1==0);");
            }
            else
            {
                _source.print(_indent);
                _source.println("        throw (");
                _source.print(_indent);
                _source.println(
                    "            IndexOutOfBoundsException(");
                _source.print(_indent);
                _source.print(
                    "                stateId, MIN_INDEX, ");
                _source.println("MAX_INDEX));");
            }

            _source.print(_indent);
            _source.println("    }");
            _source.println();
            _source.print(_indent);
            _source.print("    return (static_cast<");
            _source.print(context);
            _source.print("State&>(*(");
            _source.println("_States[stateId])));");
            _source.print(_indent);
            _source.println("}");
        }

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        transList = fsm.getTransitions();

        // Output the default transition definitions.
        for (SmcTransition trans: transList)
        {
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print(_indent);
                _source.print("void ");
                _source.print(context);
                _source.print("State::");
                _source.print(trans.getName());
                _source.print("(");
                _source.print(context);
                _source.print("Context& context");

                params = trans.getParameters();
                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    param.accept(this);
                }

                _source.println(")");
                _source.print(_indent);
                _source.println("{");
                _source.print(_indent);
                _source.println("    Default(context);");
                _source.print(_indent);
                _source.println("    return;");
                _source.print(_indent);
                _source.println("}");
            }
        }

        // Output the Default transition method ... almost.
        // If -g is being used, then add the "s" argname.
        _source.println();
        _source.print(_indent);
        _source.print("void ");
        _source.print(context);
        _source.print("State::Default(");
        _source.print(context);
        _source.println("Context& context)");
        _source.print(_indent);
        _source.println("{");

        // Print the transition out to the verbose log.
        if (Smc.isDebug() == true)
        {
            _source.print(_indent);
            _source.println(
                "    if (context.getDebugFlag() == true)");
            _source.print(_indent);
            _source.println("    {");

            // Are we using C++ iostreams or the app's TRACE
            // macro?
            if (Smc.isNoStreams() == true)
            {
                // The TRACE macro.
                _source.print(_indent);
                _source.print("        TRACE(");
                _source.println(
                    "\"TRANSITION   : Default\\n\\r\");");
            }
            else
            {
                _source.print(_indent);
                _source.print("        std::ostream& str = ");
                _source.println("context.getDebugStream();");
                _source.println();
                _source.print(_indent);
                _source.println(
                    "        str << \"TRANSITION   : Default\"");
                _source.print(_indent);
                _source.println("            << std::endl;");
            }

            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // A transition has been issued which has no
        // definition in the current state and there
        // is no default to cover for it. Throw an
        // exception.
        // v. 1.3.1: But only if -noex was not specified.
        if (Smc.isNoExceptions() == false)
        {
            _source.print(_indent);
            _source.println("    throw (");
            _source.print(_indent);
            _source.println(
                "        TransitionUndefinedException(");
            _source.print(_indent);
            _source.println(
                "            context.getState().getName(),");
            _source.print(_indent);
            _source.println(
                "            context.getTransition()));");
            _source.println();
        }
        else
        {
            // Otherwise, generate an assert.
            _source.print(_indent);
            _source.println("    assert(1==0);");
            _source.println();
        }

        _source.print(_indent);
        _source.println("    return;");
        _source.print(_indent);
        _source.println("}");

        // Have each map print out its source code now.
        for (mapIt = fsm.getMaps().iterator();
             mapIt.hasNext() == true;
            )
        {
            (mapIt.next()).accept(this);
        }

        // If a namespace was specified, then put the
        // ending braces on the namespace now.
        if (packageName != null && packageName.length() > 0)
        {
            int i;
            int j;

            for (i = (packageDepth - 1); i >= 0; --i)
            {
                // Output the proper indent.
                for (j = 0; j < i; ++j)
                {
                    _source.print("    ");
                }

                _source.println("}");
            }
        }

        return;
    } // end of visit(SmcFSM)

    public void visit(SmcMap map)
    {
        // Declare the user-defined default transitions first.
        if (map.hasDefaultState() == true)
        {
            SmcState defaultState = map.getDefaultState();

            for (SmcTransition trans:
                     defaultState.getTransitions())
            {
                trans.accept(this);
            }
        }

        // Have each state now generate its code.
        for (SmcState state: map.getStates())
        {
            state.accept(this);
        }

        return;
    } // end of visit(SmcMap)

    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String className = state.getClassName();
        String indent2;
        List<SmcAction> actions;

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print(_indent);
            _source.print("void ");
            _source.print(mapName);
            _source.print("_");
            _source.print(className);
            _source.print("::Entry(");
            _source.print(context);
            _source.println("Context& context)");
            _source.println();
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println("& ctxt(context.getOwner());");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent2;

            // End the Entry() method with a return.
            _source.print(_indent);
            _source.println("    return;");
            _source.print(_indent);
            _source.println("}");
        }

        actions = state.getExitActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print(_indent);
            _source.print("void ");
            _source.print(mapName);
            _source.print("_");
            _source.print(className);
            _source.print("::Exit(");
            _source.print(context);
            _source.println("Context& context)");
            _source.println();
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println("& ctxt(context.getOwner());");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent2;

            // End the Entry() method with a return.
            _source.print(_indent);
            _source.println("    return;");
            _source.print(_indent);
            _source.println("}");
        }

        // Have the transitions generate their code.
        for (SmcTransition trans: state.getTransitions())
        {
            trans.accept(this);
        }

        return;
    } // end of visit(SmcState)

    public void visit(SmcTransition transition)
    {
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        String transName = transition.getName();
        boolean defaultFlag = false;
        boolean nullCondition = false;
        List<SmcGuard> guards = transition.getGuards();
        Iterator<SmcGuard> git;
        SmcGuard guard;
        Iterator<SmcParameter> pit;
        String fqStateName;

        // Set a flag to denote if this is a Default state
        // transition.
        if (stateName.equals("Default") == true)
        {
            defaultFlag = true;
        }

        // Qualify the state name as well.
        if (stateName.indexOf("::") < 0)
        {
            fqStateName = mapName + "::" + stateName;
        }
        else
        {
            fqStateName = stateName;
        }

        _source.println();
        _source.print(_indent);
        _source.print("void ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.print("::");
        _source.print(transName);
        _source.print("(");
        _source.print(context);
        _source.print("Context& context");

        // Add user-defined parameters.
        for (SmcParameter param: transition.getParameters())
        {
            _source.print(", ");
            param.accept(this);
        }

        _source.println(")");
        _source.print(_indent);
        _source.println("{");

        // All transitions have a "ctxt" local variable.
        // 8/14/2003:
        // Do this only if there are any transition actions or
        // guard conditions which reference it.
        if (transition.hasCtxtReference() == true)
        {
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println("& ctxt(context.getOwner());");
        }

        // The loopbackFlag local variable is needed if there
        // is one guard with a non-nil end state. Conversely,
        // if all the transition guards use a nil end state,
        // then we don't use the loopbackFlag local variable.
        if (defaultFlag == true &&
            allNilEndStates(guards) == false)
        {
            _source.print(_indent);
            _source.println("    bool loopbackFlag = false;");
            _source.println();
        }
        else
        {
            _source.println();
        }

        // Print the transition to the verbose log.
        if (Smc.isDebug() == true)
        {
            String sep;

            _source.print(_indent);
            _source.println(
                "    if (context.getDebugFlag() == true)");
            _source.print(_indent);
            _source.println("    {");

            if (Smc.isNoStreams() == true)
            {
                _source.print(_indent);
                _source.print("        TRACE(\"TRANSITION   : ");
                _source.print(mapName);
                _source.print(" ");
                _source.print(transName);
                _source.print("(");

                for (pit = transition.getParameters().iterator(),
                         sep = "";
                     pit.hasNext() == true;
                     sep = ", ")
                {
                    _source.print(sep);
                    (pit.next()).accept(this);
                }

                _source.println(")\\n\\r\");");
            }
            else
            {
                _source.print(_indent);
                _source.print("        std::ostream& str = ");
                _source.println("context.getDebugStream();");
                _source.println();
                _source.print(_indent);
                _source.print(
                    "        str << \"TRANSITION   : ");
                _source.print(mapName);
                _source.print(" ");
                _source.print(transName);
                _source.print("(");

                for (pit = transition.getParameters().iterator(),
                         sep = "";
                     pit.hasNext() == true;
                     sep = ", ")
                {
                    _source.print(sep);
                    (pit.next()).accept(this);
                }

                _source.println(")\"");
                _source.print(_indent);
                _source.println("            << std::endl;");
            }

            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Loop through the guards and print each one.
        for (git = guards.iterator(),
                     _guardIndex = 0,
                     _guardCount = guards.size();
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = git.next();

            // Count up the number of guards with no condition.
            if (guard.getCondition().length() == 0)
            {
                nullCondition = true;
            }

            guard.accept(this);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition.
        if (_guardIndex > 0 && nullCondition == false)
        {
            // If there is only one transition definition, then
            // close off the guard.
            if (_guardCount == 1)
            {
                _source.print(_indent);
                _source.println("    }");
            }

            _source.print(_indent);
            _source.println("    else");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.print("         ");
            _source.print(mapName);
            _source.print("_Default::");
            _source.print(transName);
            _source.print("(context");

            // Output user-defined parameters.
            for (SmcParameter param: transition.getParameters())
            {
                _source.print(", ");
                _source.print(param.getName());
            }
            _source.println(");");
            _source.print(_indent);
            _source.println("    }");
        }
        else if (_guardCount > 1)
        {
            _source.println();
        }

        _source.println();
        _source.print(_indent);
        _source.println("    return;");
        _source.print(_indent);
        _source.println("}");

        return;
    } // end of visit(SmcTransition)

    public void visit(SmcGuard guard)
    {
        SmcTransition transition = guard.getTransition();
        SmcState state = transition.getState();
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        int transType = guard.getTransType();
        boolean defaultFlag =
            stateName.equalsIgnoreCase("Default");
        boolean loopbackFlag = false;
        String indent2;
        String indent3;
        String indent4 = "";
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List<SmcAction> actions = guard.getActions();

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the state
        // name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state" is actually a transition name.
        if (transType != Smc.TRANS_POP &&
            endStateName.length () > 0 &&
            endStateName.equals("nil") == false &&
            endStateName.indexOf("::") < 0)
        {
            endStateName = mapName + "::" + endStateName;
        }

        // Qualify the state name as well.
        if (stateName.indexOf("::") < 0)
        {
            stateName = mapName + "::" + stateName;
        }

        loopbackFlag =
            isLoopback(transType, stateName, endStateName);

        // v. 2.0.2: If the push state is not fully-qualified,
        // then prepend the current map's name and make if
        // fully-qualified.
        if (pushStateName != null &&
            pushStateName.length() > 0 &&
            pushStateName.indexOf("::") < 0)
        {
            pushStateName = mapName + "::" + pushStateName;
        }

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            indent2 = _indent + "        ";

            // More than one guard. Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an
                // "if" should be used for this condition.
                _source.print(_indent);
                _source.print("    if (");
                _source.print(condition);
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if" for
                // the condition.
                _source.println();
                _source.print(_indent);
                _source.print("    else if (");
                _source.print(condition);
                _source.println(")");
                _source.println(_indent);
                _source.println("    {");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println();
                _source.print(_indent);
                _source.println("    else");
                _source.print(_indent);
                _source.println("    {");
            }
        }
        else
        {
            // There is only one guard. Does this guard have a
            // condition.
            if (condition.length() == 0)
            {
                // Actually, this is a plain, old, vaniila
                // transition.
                indent2 = _indent + "    ";
            }
            else
            {
                // Yes, there is a condition.
                _source.print(_indent);
                _source.print("    if (");
                _source.print(condition);
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");
                indent2 = _indent + "        ";
            }
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transitions actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (actions.size() == 0 && endStateName.length() > 0)
        {
            fqEndStateName = endStateName;
        }
        else if (actions.size() > 0)
        {
            // Save away the current state if this is a loopback
            // transition. Storing current state allows the
            // current to be cleared before any actions are
            // executed. Remember: actions are not allowed to
            // issue transitions and clearing the current state
            // prevents them from doing so.
            if (loopbackFlag == true)
            {
                fqEndStateName = "EndStateName";

                _source.print(indent2);
                _source.print(context);
                _source.print("State& ");
                _source.print(fqEndStateName);
                _source.println(" = context.getState();");
                _source.println();
            }
            else
            {
                fqEndStateName = endStateName;
            }
        }

        // Decide if runtime loopback checking must be done.
        if (defaultFlag == true &&
            transType == Smc.TRANS_SET &&
            loopbackFlag == false)
        {
            _source.print(indent2);
            _source.println(
                "if (strcmp(context.getState().getName(), ");
            _source.print(indent2);
            _source.print("           ");
            _source.print(fqEndStateName);
            _source.println(".getName())");
            _source.print(indent2);
            _source.println("       == 0)");
            _source.print(indent2);
            _source.println("{");
            _source.print(indent2);
            _source.println("    loopbackFlag = true;");
            _source.print(indent2);
            _source.println("}");
            _source.println();
        }

        // Before doing anything else, perform the current
        // state's exit actions.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (transType == Smc.TRANS_POP ||
            loopbackFlag == false)
        {
            indent4 = indent2;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent4 = indent2 + "    ";
                _source.print(indent2);
                _source.println("if (loopbackFlag == false)");
                _source.print(indent2);
                _source.println("{");
            }

            _source.print(indent4);
            _source.println(
                "(context.getState()).Exit(context);");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("}");
                _source.println();
            }
        }

        if (actions.size() > 0)
        {
            // Now that we are in the transition, clear the
            // current state.
            _source.print(indent2);
            _source.println("context.clearState();");
        }

        // Dump out this transition's actions.
        if (actions.size() == 0)
        {
            if (condition.length() > 0)
            {
                _source.print(indent2);
                _source.println("// No actions.");
            }

            indent3 = indent2;
        }
        else
        {
            // v. 2.0.2: Place the actions inside a try/catch
            // block. This way the state will be set before an
            // exception leaves the transition method.
            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.print(indent2);
                _source.println("try");
                _source.print(indent2);
                _source.println("{");

                indent3 = indent2 + "    ";
            }
            else
            {
                indent3 = indent2;
            }

            indent4 = _indent;
            _indent = indent3;

            for (SmcAction action: actions)
            {
                action.accept(this);
            }

            _indent = indent4;
        }

        // Print the setState() call, if necessary. Do NOT
        // generate the set state if:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        //
        // v. 2.0.2: The following code must be generated twice -
        // once for the try body and again for the catch body.
        // Unlike Java, C++ does not have a finally clause.
        if (transType == Smc.TRANS_SET &&
            (actions.size() > 0 || loopbackFlag == false))
        {
            _source.print(indent3);
            _source.print("context.setState(");
            _source.print(fqEndStateName);
            _source.println(");");
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            // Set the end state so that it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || actions.size() > 0)
            {
                _source.print(indent3);
                _source.print("context.setState(");
                _source.print(fqEndStateName);
                _source.println(");");
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                if (defaultFlag == true)
                {
                    indent4 = indent3 + "    ";

                    _source.println();
                    _source.print(indent3);
                    _source.println(
                        "if (loopbackFlag == false)");
                    _source.print(indent3);
                    _source.println("{");
                }
                else
                {
                    indent4 = indent3;

                    _source.println();
                }

                _source.print(indent4);
                _source.println(
                    "(context.getState()).Entry(context);");

                if (defaultFlag == true)
                {
                    _source.print(indent3);
                    _source.println("}");
                }
            }

            _source.print(indent3);
            _source.print("context.pushState(");
            _source.print(pushStateName);
            _source.println(");");
        }
        else if (transType == Smc.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("context.popState();");
        }

        // v. 2.0.2: Generate the set state, push or pop
        // code for the catch body. Note: The try body was
        // generated only if there were actions.
        // Place the try block's closing brace and open the
        // catch-all block.
        // v. 2.2.0: Check if the user has turned off this
        // feature first.
        if (actions.isEmpty() == false &&
            Smc.isNoCatch() == false)
        {
            _source.print(indent2);
            _source.println("}");
            _source.print(indent2);
            _source.println("catch (...)");
            _source.print(indent2);
            _source.println("{");

            if (transType == Smc.TRANS_SET)
            {
                _source.print(indent3);
                _source.print("context.setState(");
                _source.print(fqEndStateName);
                _source.println(");");
            }
            else if (transType == Smc.TRANS_PUSH)
            {
                // Set the end state so that it can be pushed
                // onto the state stack.
                _source.print(indent3);
                _source.print("context.setState(");
                _source.print(fqEndStateName);
                _source.println(");");

                // Before doing the push, execute the end state's
                // entry actions (if any) if this is not a
                // loopback.
                if (loopbackFlag == false)
                {
                    indent4 = indent3;

                    if (defaultFlag == true)
                    {
                        indent4 = indent3 + "    ";
                        _source.println();
                        _source.print(indent3);
                        _source.println(
                            "if (loopbackFlag == false)");
                        _source.print(indent3);
                        _source.println("{");
                    }
                    else
                    {
                        _source.println();
                    }

                    _source.print(indent4);
                    _source.println(
                        "(context.getState()).Entry(context);");

                    if (defaultFlag == true)
                    {
                        _source.print(indent3);
                        _source.println("}");
                    }
                }

                _source.print(indent3);
                _source.print("context.pushState(");
                _source.print(pushStateName);
                _source.println(");");
            }
            else if (transType == Smc.TRANS_POP)
            {
                _source.print(indent3);
                _source.println("context.popState();");
            }

            // Rethrow the exception so the application can
            // handle it (or not). This is the end of the
            // catch block.
            _source.print(indent3);
            _source.println("throw;");
            _source.print(indent2);
            _source.println("}");
        }

        // Perform the new state's entry actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((transType == Smc.TRANS_SET &&
             loopbackFlag == false) ||
             transType == Smc.TRANS_PUSH)
        {
            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent4 = indent2 + "    ";

                _source.println();
                _source.print(indent2);
                _source.println("if (loopbackFlag == false)");
                _source.print(indent2);
                _source.println("{");
            }
            else
            {
                indent4 = indent2;
            }

            _source.print(indent4);
            _source.println(
                "(context.getState()).Entry(context);");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("}");
            }
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == Smc.TRANS_POP &&
            endStateName.equals(NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.print(indent2);
            _source.print("context.");
            _source.print(endStateName);
            _source.print("(");

            // Output any and all pop arguments.
            if (popArgs.length() > 0)
            {
                _source.print(popArgs);
            }
            _source.println(");");
        }

        // If this is a guarded transition, it will be necessary
        // to close off the "if" body. DON'T PRINT A NEW LINE!
        // Why? Because an "else" or "else if" may follow and we
        // won't know until we go back to the transition source
        // generator whether all clauses have been done.
        if (_guardCount > 1)
        {
            _source.print(_indent);
            _source.print("    }");
        }

        return;
    } // end of visit(SmcGuard)

    public void visit(SmcAction action)
    {
        String name = action.getName();
        Iterator<String> it;
        String sep;

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        _source.print(_indent);
        if (name.equals("emptyStateStack") == true)
        {
            _source.print("context.");
        }
        else
        {
            _source.print("ctxt.");
        }
        _source.print(name);
        _source.print('(');

        for (it = action.getArguments().iterator(), sep = "";
             it.hasNext() == true;
             sep = ", ")
        {
            _source.print(sep);
            _source.print(it.next());
        }

        _source.println(");");

        return;
    } // end of visit(SmcAction)

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(" ");
        _source.print(parameter.getName());

        return;
    } // end of visit(SmcParameter)

//---------------------------------------------------------------
// Member data
//
} // end of class SmcCppGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.10  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.9  2007/12/28 12:34:41  cwrapp
// Version 5.0.1 check-in.
//
// Revision 1.8  2007/08/05 14:36:11  cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more informaiton.
//
// Revision 1.7  2007/02/21 13:54:27  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.6  2007/01/15 00:23:50  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.5  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.4  2006/07/11 18:12:16  cwrapp
// Added support for new -headerd command line option.
//
// Revision 1.3  2006/04/22 12:45:26  cwrapp
// Version 4.3.1
//
// Revision 1.2  2005/11/07 19:34:54  cwrapp
// Changes in release 4.3.0:
// New features:
//
// + Added -reflect option for Java, C#, VB.Net and Tcl code
//   generation. When used, allows applications to query a state
//   about its supported transitions. Returns a list of
//   transition names. This feature is useful to GUI developers
//   who want to enable/disable features based on the current
//   state. See Programmer's Manual section 11: On Reflection
//   for more information.
//
// + Updated LICENSE.txt with a missing final paragraph which
//   allows MPL 1.1 covered code to work with the GNU GPL.
//
// + Added a Maven plug-in and an ant task to a new tools
//   directory.
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
//   certainly *not* thread safe. Multi-threaded C/C++
//   applications are required to synchronize access to the FSM
//   to allow for correct performance.
//
// + (Java) The generated getState() method is now public.
//
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.2  2005/02/21 15:34:59  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:13:03  charlesr
// Modified isLoopback() to new signature due to moving method
// from SmcGuard to SmcCodeGenerator.
//
// Revision 1.0  2005/02/03 17:10:26  charlesr
// Initial revision
//
