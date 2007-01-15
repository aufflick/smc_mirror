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
// Copyright (C) 2006, 2007. Charles W. Rapp.
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
 * Visits the abstract syntax tree emitting Objective-C code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcObjCGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcObjCGenerator(PrintStream source,
                            String srcfileBase)
    {
        super (source, srcfileBase);

        _indent = "";
    }

    // This method generates the following code:
    //
    // %{ %} raw source code - if any
    //
    // #import <%include header file>
    // #import "<context>_sm.h"
    public void visit(SmcFSM fsm)
    {
        String packageName = fsm.getPackage();
        String rawSource = fsm.getSource();
        String context = fsm.getContext();
        String fqStateName = fsm.getStartState();
        String mapName;
        List transList;
        String separator;
        List params;
        Iterator it;
        Iterator mapIt;
        Iterator stateIt;
        Iterator transIt;
        Iterator pit;
        String declaration;
        int packageDepth = 0;
        SmcMap map;
        SmcState state;
        SmcTransition trans;
        SmcParameter param;
        int index;

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length() > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // Generate #includes.
        for (it = fsm.getIncludes().iterator();
             it.hasNext() == true;
            )
        {
            _source.print("#import ");
            _source.println(((String) it.next()));
        }

        // Include the context file last.
        _source.print("#import \"");
        _source.print(_srcfileBase);
        _source.println("_sm.h\"");

        // Statically declare all derive state classes.
        _source.print(_indent);
        _source.println("// Class declarations.");
        for (mapIt = fsm.getMaps().iterator(), index = 0;
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            mapName = map.getName();
            
            _source.print(_indent);
            _source.print("@implementation ");
            _source.println(mapName);
            
            for (stateIt = map.getStates().iterator();
                 stateIt.hasNext() == true;
                 ++index)
            {
                state = (SmcState) stateIt.next();
                _source.print(_indent);
                _source.print("+ (");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print("*)");
                _source.print(state.getInstanceName());
                _source.println(";");
                
                _source.print(_indent);
                _source.println("{");
                
                _source.print(_indent);
                _source.print("    ");
                _source.print("static ");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print(" *g");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.println(" = nil;");
                
                _source.print(_indent);
                _source.print("    if (!g");                
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.println(")");
                _source.print(_indent);
                _source.println("{");

                _source.print(_indent);
                _source.print("        g");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print(" = [[");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.print(" alloc] initWithName:@\"");
                _source.print(mapName);
                _source.print("::");
                _source.print(state.getClassName());
                _source.print("\" stateId:");
                _source.print(index);
                _source.println("];");

                _source.print(_indent);
                _source.println("    }");
                
                _source.print(_indent);
                _source.print("    ");
                _source.print("return g");
                _source.print(mapName);
                _source.print("_");
                _source.print(state.getClassName());
                _source.println(";");

                _source.print(_indent);                
                _source.println("}");
            }
            
            _source.println("@end");
        }

        // Print out the default definitions for all the
        // transitions. First, get the transitions list.
        transList = fsm.getTransitions();
        
        _source.println();
        _source.print(_indent);
        _source.print("@implementation ");
        _source.print(context);
        _source.println("State");
        
        // TODO: Need to fill in Entry/Exit code if they're
        // defined
        _source.print(_indent);
        _source.print("- (void)Entry:(");
        _source.print(context);
        _source.println("Context*)context");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("}");
        
        _source.print(_indent);
        _source.print("- (void)Exit:(");
        _source.print(context);
        _source.println("Context*)context");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("}");
        
        // Output the default transition definitions.
        for (transIt = transList.iterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            if (trans.getName().equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("- (void)");
                _source.print(trans.getName());
                _source.print(":(");
                _source.print(context);
                _source.print("Context*)context");

                params = trans.getParameters();
                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    _source.print(" :");
                    ((SmcParameter) pit.next()).accept(this);
                }

                _source.println(";");
                _source.print(_indent);
                _source.println("{");
                _source.print(_indent);
                _source.println("    [self Default:context];");
                _source.print(_indent);
                _source.println("}");
            }
        }

        // Output the Default transition method ... almost.
        // If -g is being used, then add the "s" argname.
        _source.println();
        _source.print(_indent);
        _source.print("- (void)Default:(");
        _source.print(context);
        _source.println("Context*)context;");
        _source.print(_indent);
        _source.println("{");

        // Print the transition out to the verbose log.
        if (Smc.isDebug() == true)
        {
            _source.print(_indent);
            _source.println("    if ( [context debugFlag] )");
            _source.print(_indent);
            _source.print("{");

            // The TRACE macro.
            _source.print(_indent);
            _source.print("        TRACE(@");
            _source.println(
                "\"TRANSITION   : Default\\n\\r\");");

            _source.print(_indent);
            _source.println("    }");
        }

        // A transition has been issued which has no
        // definition in the current state and there
        // is no default to cover for it. Throw an
        // exception.
        // v. 1.3.1: But only if -noex was not specified.
        _source.print(_indent);
        _source.println(
            "    NSAssert( NO, @\"Default transition\" );");
        _source.print(_indent);
        _source.println("}");
        
        // End the state class
        _source.println("@end");
        _source.println();
        
        // Have each map print out its source code now.
        for (mapIt = fsm.getMaps().iterator();
             mapIt.hasNext() == true;
            )
        {
            ((SmcMap) mapIt.next()).accept(this);
        }
        
        // Dump the context class
        // @implementation FooContext
        // - (id)initWithOwner:(FOO*)owner;
        // ...
        //     
        _source.print(_indent);
        _source.print("@implementation ");
        _source.print(context);
        _source.println("Context");

        // Convert SMC scope syntax to Objective-C syntax.
        if (fqStateName.indexOf("::") >= 0)
        {
            fqStateName = convertScope(fqStateName);
        }

        // Dump the initWithOwner method
        // - (id)initWithOwner:(Foo*)owner;
        // {
        //     [self setOwner:owner];
        //     [self setState:startState]   
        // }
        _source.print(_indent);
        _source.print("- (id)initWithOwner:(");
        _source.print(context);
        _source.print("*)");
        _source.println("owner;");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.print("    ");
        _source.println("self = [super init];");
        _source.print(_indent);
        _source.print("    ");
        _source.println("if (!self)");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.print("        ");
        _source.println("return nil;");
        _source.print(_indent);
        _source.println("    }");
        _source.print(_indent);
        _source.print("    ");
        _source.println("_owner = owner;");
        _source.print(_indent);
        _source.print("    ");
        _source.print("[self setState:");
        _source.print(fqStateName);
        _source.println("];");
        
        _source.print(_indent);
        _source.print("    [");
        _source.print(fqStateName);
        _source.println(" Entry:self];");
        
        _source.print(_indent);
        _source.print("    ");
        _source.println("return self;");
        _source.print(_indent);
        _source.println("}");
        
        
        _source.print(_indent);
        _source.print("- (" );
        _source.print(context);
        _source.println("State*)state;");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.print("    ");
        _source.print("return (");
        _source.print(context);        
        _source.println("State*)_state;");
        _source.print(_indent);
        _source.println("}");
                        
        _source.print(_indent);
        _source.print("- (");
        _source.print(context);
        _source.println("*)owner;");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.print("    ");
        _source.println("return _owner;");
        _source.print(_indent);
        _source.println("}");

        // Generate a method for every transition in every map
        // *except* the default transition.
        for (transIt = transList.iterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            if (trans.getName().equals("Default") == false)
            {
                _source.println();
                _source.print(_indent);
                _source.print("- (void)");
                _source.print(trans.getName());

                params = trans.getParameters();
                for (pit = params.iterator(),
                       separator = ":";
                     pit.hasNext() == true;
                     separator = " :")
                {
                    param = (SmcParameter) pit.next();

                    _source.print(separator);
                    param.accept(this);
                }
                _source.println(";");
                
                _source.print(_indent);
                _source.println("{");

                // If -g was specified, then set the transition
                // name so it can be printed out.
                if (Smc.isDebug() == true)
                {
                    _source.print(_indent);
                    _source.print("    [self setTransition:@\"");
                    _source.print(trans.getName());
                    _source.println("\"];");
                }

                _source.print(_indent);
                _source.print("    [[self state] ");
                _source.print(trans.getName());
                _source.print(":self");
                for (pit = params.iterator();
                     pit.hasNext() == true;
                    )
                {
                    param = (SmcParameter) pit.next();

                    _source.print(" :");
                    _source.print(param.getName());
                }
                _source.println("];");

                if (Smc.isDebug() == true)
                {
                    _source.print(_indent);
                    _source.println(
                        "    [self setTransition:nil];");
                }

                _source.print(_indent);
                _source.println("}");
            }
        }

        // End the class implementation
        _source.print(_indent);
        _source.println("@end");

        return;
    }

    public void visit(SmcMap map)
    {
        Iterator it;
        
        // Print out the default state class
        _source.println();
        _source.print(_indent);
        _source.print("@implementation ");
        _source.print(map.getName());
        _source.println("_Default");

        // Declare the user-defined default transitions first.
        if (map.hasDefaultState() == true)
        {
            SmcState defaultState = map.getDefaultState();

            for (it = defaultState.getTransitions().iterator();
                 it.hasNext() == true;
                )
            {
                ((SmcTransition) it.next()).accept(this);
            }
        }
        
        _source.println("@end");

        // Have each state now generate its code.
        for (it = map.getStates().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcState) it.next()).accept(this);
        }

        return;
    }

    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String className = state.getClassName();
        String indent2;
        List actions;
        Iterator it;

        _source.print(_indent);
        _source.print("@implementation ");
        _source.print(mapName);
        _source.print("_");
        _source.println(className);

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.print(_indent);
            _source.print("- (void)Entry:(");
            _source.print(context);
            _source.println("Context*)context;");
            _source.println();
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println(" *ctxt = [context owner];");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }
            _indent = indent2;

            // End the Entry() method with a return.
            _source.print(_indent);
            _source.println("}");
        }

        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.print(_indent);
            _source.print("- (void)Exit:(");
            _source.print(context);
            _source.println("Context*)context;");
            _source.println();
            _source.println("{");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println(" *ctxt = [context owner];");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "    ";
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }
            _indent = indent2;

            _source.print(_indent);
            _source.println("}");
        }

        // Have the transitions generate their code.
        for (it = state.getTransitions().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }
        
        _source.print(_indent);
        _source.println("@end");
        _source.println();

        return;
    }

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
        List guards = transition.getGuards();
        Iterator git;
        SmcGuard guard;
        Iterator pit;
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
            fqStateName = "[" + mapName + " " + stateName + "]";
        }
        else
        {
            fqStateName = stateName;
        }

        _source.println();
        _source.print(_indent);
        _source.print("- (void)");
        _source.print(transName);
        _source.print(":(");
        _source.print(context);
        _source.print("Context*)context");

        // Add user-defined parameters.
        for (pit = transition.getParameters().iterator();
             pit.hasNext() == true;
            )
        {
            _source.print(" :");
            ((SmcParameter) pit.next()).accept(this);
        }

        _source.println(";");
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
            _source.println(" *ctxt = [context owner];");
        }

        if (defaultFlag == true)
        {
            _source.print(_indent);
            _source.println("    BOOL loopbackFlag = NO;");
        }

        // Print the transition to the verbose log.
        if (Smc.isDebug() == true)
        {
            String sep;

            _source.print(_indent);
            _source.println("    if ( [context debugFlag] )");
            _source.print(_indent);
            _source.println("    {");

            _source.print(_indent);
            _source.print("        TRACE(@\"TRANSITION   : ");
            _source.print(mapName);
            _source.print(" ");
            _source.print(transName);
            _source.print("(");

            for (pit = transition.getParameters().iterator(),
                     sep = "";
                 pit.hasNext() == true;
                 sep = " ")
            {
                _source.print(sep);
                ((SmcParameter) pit.next()).accept(this);
            }

            _source.println(")\\n\\r\");");
            
            _source.print(_indent);
            _source.println("    }");
        }


        // Loop through the guards and print each one.
        for (git = guards.iterator(),
                     _guardIndex = 0,
                     _guardCount = guards.size();
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = (SmcGuard) git.next();

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
            for (pit = transition.getParameters().iterator();
                 pit.hasNext() == true;
                )
            {
                _source.print(", ");
                _source.print(
                    ((SmcParameter) pit.next()).getName());
            }
            _source.println(");");
            _source.print(_indent);
            _source.println("    }");
        }
        else if (_guardCount > 1)
        {
            _source.println();
        }

        _source.print(_indent);
        _source.println("}");

        return;
    }

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
        List actions = guard.getActions();

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

        // Convert the SMC scope syntex to the Objective-C
        // syntax.
        if (endStateName.indexOf("::") >= 0)
        {
            endStateName = convertScope(endStateName);
        }

        // Qualify the state name as well.
        if (stateName.indexOf("::") >= 0)
        {
            stateName = convertScope(stateName);
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
            pushStateName =
                "[" + mapName + " " + pushStateName + "]";
        }
        
        if (pushStateName.indexOf("::") >= 0)
        {
            pushStateName = convertScope(pushStateName);
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
                _source.print("State* ");
                _source.print(fqEndStateName);
                _source.println(" = [context state];");
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
            _source.print("if ( [[[context state] name] ");
            _source.print("isEqualToString:[");
            _source.print(fqEndStateName);
            _source.println(" name]] )");
            _source.print(indent2);
            _source.println("{");
            _source.print(indent2);
            _source.println("    loopbackFlag = YES;");
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
                _source.println("if (!loopbackFlag)");
                _source.print(indent2);
                _source.println("{");
            }

            _source.print(indent4);
            _source.println(
                "[[context state] Exit:context];");

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
            _source.println("[context clearState];");
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
            Iterator ait;
            indent3 = indent2;
            indent4 = _indent;
            _indent = indent3;

            for (ait = actions.iterator();
                 ait.hasNext() == true;
                )
            {
                ((SmcAction) ait.next()).accept(this);
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
            _source.print("[context setState:");
            _source.print(fqEndStateName);
            _source.println("];");
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            // Set the end state so that it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || actions.size() > 0)
            {
                _source.print(indent3);
                _source.print("[context setState:");
                _source.print(fqEndStateName);
                _source.println("];");
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
                    _source.println("if (!loopbackFlag)");
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
                    "[[context state] Entry:context];");

                if (defaultFlag == true)
                {
                    _source.print(indent3);
                    _source.println("}");
                }
            }

            _source.print(indent3);
            _source.print("[context pushState:");
            _source.print(pushStateName);
            _source.println("];");
        }
        else if (transType == Smc.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("[context popState];");
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
                _source.println("if (!loopbackFlag)");
                _source.print(indent2);
                _source.println("{");
            }
            else
            {
                indent4 = indent2;
            }

            _source.print(indent4);
            _source.println(
                "[[context state] Entry:context];");

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
            int popLength = popArgs.length();

            _source.print(indent2);
            _source.print("[context ");
            _source.print(endStateName);

            // Output any and all pop arguments.
            if (popLength > 0)
            {
                int cur;
                int comma;

                for (cur = 0, comma = popArgs.indexOf(',');
                     comma >= 0;
                     cur = (comma + 1),
                         comma = popArgs.indexOf(',', cur))
                {
                    _source.print(':');
                    _source.print(popArgs.substring(cur, comma));
                }

                if (cur < popLength)
                {
                    _source.print(':');
                    _source.print(popArgs.substring(cur));
                }
            }
            _source.println("];");
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
    }

    public void visit(SmcAction action)
    {
        String name = action.getName();
        Iterator it;
        String sep;

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        _source.print(_indent);
        if (name.equals("emptyStateStack") == true)
        {
            _source.print("[context ");
        }
        else
        {
            _source.print("[ctxt ");
        }
        _source.print(name);

        for (it = action.getArguments().iterator(), sep = ":";
             it.hasNext() == true;
             )
        {
            String arg = (String) it.next();

            if (arg.trim().length() > 0)
            {
                _source.print(sep);
                _source.print(arg);                
            }
        }

        _source.println("];");

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print("(");
        _source.print(parameter.getType());
        _source.print(")");
        _source.print(parameter.getName());

        return;
    }

    // Converts the SMC scope syntax to Objective-C syntax.
    private String convertScope(String s)
    {
        int index = s.indexOf("::");

        return ("[" +
                s.substring(0, index) +
                " " +
                s.substring((index + 2)) +
                "]");
    }

//---------------------------------------------------------------
// Member data
//
}

//
// CHANGE LOG
// $Log$
// Revision 1.1  2007/01/15 00:23:51  cwrapp
// Release 4.4.0 initial commit.
//
//
