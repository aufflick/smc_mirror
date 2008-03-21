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
// Copyright (C) 2005. Charles W. Rapp.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Visits the abstract syntax tree, emitting C# code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcCSharpGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcCSharpGenerator(String srcfileBase)
    {
        super (srcfileBase, "{0}{1}_sm.{2}", "cs");
    } // end of SmcCSharpGenerator(String)

    public void visit(SmcFSM fsm)
    {
        String rawSource = fsm.getSource();
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String startState = fsm.getStartState();
        String accessLevel = fsm.getAccessLevel();
        List<SmcMap> maps = fsm.getMaps();
        List<SmcTransition> transitions;
        Iterator<SmcParameter> pit;
        String transName;
        String csState;
        String separator;
        int index;
        List<SmcParameter> params;
        String indent2;

        // If the access level has not been set, then the
        // default is "public".
        if (accessLevel == null || accessLevel.length() == 0)
        {
            accessLevel = "public";
        }

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length () > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // Always include the system package.
        _source.println("using System;");

        // If debugging code is being generated, then import
        // system diagnostics package as well.
        if (Smc.isDebug() == true)
        {
            _source.println("using System.Diagnostics;");
        }

        // If serialization is on, then import the .Net
        // serialization package.
        if (Smc.isSerial() == true)
        {
            _source.println(
                "using System.Runtime.Serialization;");
            _source.println("using System.Security;");
            _source.println(
                "using System.Security.Permissions;");
        }

        // If reflection is on, then import the .Net collections
        // package.
        if (Smc.isReflection() == true)
        {
            _source.println("using System.Collections;");
        }
        _source.println();

        // Do user-specified imports now.
        for (String imp: fsm.getImports())
        {
            _source.print("using ");
            _source.print(imp);
            _source.println(";");
        }

        // If a package has been specified, generate the package
        // statement now and set the indent.
        if (packageName != null && packageName.length() > 0)
        {
            _source.print("namespace ");
            _source.println(packageName);
            _source.println("{");
            _indent = "    ";
        }

        // Does the user want to serialize this FSM?
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.println("[Serializable]");
        }

        // Now declare the FSM context class.
        _source.print(_indent);
        _source.print(accessLevel);
        _source.print(" sealed class ");
        _source.print(context);
        _source.println("Context :");
        _source.print(_indent);
        _source.print("    statemap.FSMContext");
        if (Smc.isSerial() == false)
        {
            _source.println();
        }
        else
        {
            _source.println(',');
            _source.print(_indent);
            _source.println("    ISerializable");
        }
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println(
            "//---------------------------------------------------------------");
        _source.print(_indent);
        _source.println("// Properties.");
        _source.print(_indent);
        _source.println("//");
        _source.println();

        // State property.
        _source.print(_indent);
        _source.print("    public ");
        _source.print(context);
        _source.println("State State");
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.println("        get");
        _source.print(_indent);
        _source.println("        {");

        // Again, if synchronization is on, then protect access
        // to this FSM.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            lock (this)");
            _source.print(_indent);
            _source.println("            {");

            indent2 = _indent + "                ";
        }
        else
        {
            indent2 = _indent + "            ";
        }

        _source.print(indent2);
        _source.println("if (_state == null)");
        _source.print(indent2);
        _source.println("{");
        _source.print(indent2);
        _source.println("    throw(");
        _source.print(indent2);
        _source.println(
            "        new statemap.StateUndefinedException());");
        _source.print(indent2);
        _source.println("}");
        _source.println();
        _source.print(indent2);
        _source.print("return ((");
        _source.print(context);
        _source.println("State) _state);");

        // If we are in a lock block, close it.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            }");
        }

        // Close the State get.
        _source.print(_indent);
        _source.println("        }");

        // Now generate the State set.
        _source.print(_indent);
        _source.println("        set");
        _source.print(_indent);
        _source.println("        {");

        // Again, if synchronization is on, then protect access
        // to this FSM.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            lock(this)");
            _source.print(_indent);
            _source.println("            {");

            indent2 = _indent + "                ";
        }
        else
        {
            indent2 = _indent + "            ";
        }

        _source.print(indent2);
        _source.println("SetState(value);");

        // If we are in a lock block, close it.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            }");
        }

        // Close the State set.
        _source.print(_indent);
        _source.println("        }");

        // Close the state property.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        // Generate the Owner property.
        _source.print(_indent);
        _source.print("    public ");
        _source.print(context);
        _source.println(" Owner");
        _source.print(_indent);
        _source.println("    {");

        // Generate the property get method.
        _source.print(_indent);
        _source.println("        get");
        _source.print(_indent);
        _source.println("        {");
        _source.print(_indent);
        _source.println("            return (_owner);");
        _source.print(_indent);
        _source.println("        }");

        // Generate the property set method.
        _source.print(_indent);
        _source.println("        set");
        _source.print(_indent);
        _source.println("        {");

        // Again, if synchronization is on, then protect access
        // to this FSM.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            lock(this)");
            _source.print(_indent);
            _source.println("            {");

            indent2 = _indent + "                ";
        }
        else
        {
            indent2 = _indent + "            ";
        }

        _source.print(indent2);
        _source.println("_owner = value;");

        // If we are in a lock block, close it.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            }");
        }

        // Close the Onwer set.
        _source.print(_indent);
        _source.println("        }");

        // Close the Owner property.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        _source.print(_indent);
        _source.println(
            "//---------------------------------------------------------------");
        _source.print(_indent);
        _source.println("// Member methods.");
        _source.print(_indent);
        _source.println("//");
        _source.println();

        // Generate the context class' constructor.
        _source.print(_indent);
        _source.print("    public ");
        _source.print(context);
        _source.print("Context(");
        _source.print(context);
        _source.println(" owner) :");
        _source.print(_indent);
        _source.println("        base ()");
        _source.print(_indent);
        _source.println("    {");
        _source.println("        _owner = owner;");

        // The state name "map::state" must be changed to
        // "map.state".
        if ((index = startState.indexOf("::")) >= 0)
        {
            csState = startState.substring(0, index) +
                      "." +
                      startState.substring(index + 2);
        }
        else
        {
            csState = startState;
        }

        _source.print(_indent);
        _source.print("        _state = ");
        _source.print(csState);
        _source.println(";");

        // Execute the start state's entry actions.
        _source.print(_indent);
        _source.print("        ");
        _source.print(csState);
        _source.println(".Entry(this);");

        // Context class' constructor end.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        // If -serial was specified, then generate the
        // deserialize constructor.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.print("    public ");
            _source.print(context);
            _source.print("Context(SerializationInfo info, ");
            _source.println("StreamingContext context) :");
            _source.print(_indent);
            _source.println("        base ()");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println("        int stackSize;");
            _source.print(_indent);
            _source.println("        int stateId;");
            _source.println();
            _source.print(_indent);
            _source.print(
                "        stackSize = ");
            _source.println("info.GetInt32(\"stackSize\");");
            _source.print(_indent);
            _source.println("        if (stackSize > 0)");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println("            int index;");
            _source.print(_indent);
            _source.println("            String name;");
            _source.println();
            _source.print(_indent);
            _source.print(
                "            for (index = (stackSize - 1); ");
            _source.println("index >= 0; --index)");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.print("                ");
            _source.println("name = \"stackIndex\" + index;");
            _source.print(_indent);
            _source.print("                ");
            _source.println("stateId = info.GetInt32(name);");
            _source.print(_indent);
            _source.print("                ");
            _source.println("PushState(_States[stateId]);");
            _source.print(_indent);
            _source.println("            }");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        stateId = info.GetInt32(\"state\");");
            _source.print(_indent);
            _source.println(
                "        PushState(_States[stateId]);");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Generate the default transition methods.
        // First get the transition list.
        transitions = fsm.getTransitions();
        for (SmcTransition trans: transitions)
        {
            transName = trans.getName();

            // Ignore the default transition.
            if (transName.equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("    public void ");
                _source.print(transName);
                _source.print("(");

                // Now output the transition's parameters.
                params = trans.getParameters();
                for (pit = params.iterator(), separator = "";
                     pit.hasNext() == true;
                     separator = ", ")
                {
                    _source.print(separator);
                    (pit.next()).accept(this);
                }
                _source.println(")");
                _source.print(_indent);
                _source.println("    {");

                // If the -sync flag was specified, then output
                // "lock(this)" to prevent multiple threads from
                // access this state machine simultaneously.
                if (Smc.isSynchronized() == true)
                {
                    _source.print(_indent);
                    _source.println("        lock (this)");
                    _source.print(_indent);
                    _source.println("        {");

                    indent2 = _indent + "            ";
                }
                else
                {
                    indent2 = _indent + "        ";
                }

                // Save away the transition name in case it is
                // need in an UndefinedTransitionException.
                _source.print(indent2);
                _source.print("_transition = \"");
                _source.print(transName);
                _source.println("\";");

                _source.print(indent2);
                _source.print("State.");
                _source.print(transName);
                _source.print("(this");

                for (SmcParameter param: params)
                {
                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(");");
                _source.print(indent2);
                _source.println("_transition = \"\";");

                // If the -sync flag was specified, then output
                // the "End SyncLock".
                if (Smc.isSynchronized() == true)
                {
                    _source.print(_indent);
                    _source.println("        }");
                    _source.println();
                }

                _source.print(_indent);
                _source.println("        return;");
                _source.print(_indent);
                _source.println("    }");
                _source.println();
            }
        }

        // If -serial specified, then output the valueOf(int)
        // method.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.print("    public ");
            _source.print(context);
            _source.println("State valueOf(int stateId)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println("        return(_States[stateId]);");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // If serialization is turned on, then output the
        // GetObjectData method.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.print("    [SecurityPermissionAttribute(");
            _source.print("SecurityAction.Demand, ");
            _source.println("SerializationFormatter=true)]");
            _source.print(_indent);
            _source.print("    public void GetObjectData(");
            _source.println("SerializationInfo info,");
            _source.print(_indent);
            _source.print("                              ");
            _source.println("StreamingContext context)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println("        int stackSize = 0;");
            _source.println();
            _source.print(_indent);
            _source.println("        if (_stateStack != null)");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println(
                "            stackSize = _stateStack.Count;");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "info.AddValue(\"stackSize\", stackSize);");
            _source.println();
            _source.print(_indent);
            _source.println("        if (stackSize > 0)");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println("            int index = 0;");
            _source.print(_indent);
            _source.println("            String name;");
            _source.println();
            _source.print(_indent);
            _source.print("            foreach (");
            _source.print(context);
            _source.println("State state in _stateStack)");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.print("                ");
            _source.println("name = \"stackIndex\" + index;");
            _source.print(_indent);
            _source.print("                info.AddValue(");
            _source.println("name, state.Id);");
            _source.print(_indent);
            _source.println("                ++index;");
            _source.print(_indent);
            _source.println("            }");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        info.AddValue(\"state\", _state.Id);");
            _source.println();
            _source.print(_indent);
            _source.println("        return;");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Declare member data.
        _source.print(_indent);
        _source.println(
            "//---------------------------------------------------------------");
        _source.print(_indent);
        _source.println("// Member data.");
        _source.print(_indent);
        _source.println("//");
        _source.println();
        _source.print(_indent);
        _source.println("    [NonSerialized]");
        _source.print(_indent);
        _source.print("    private ");
        _source.print(context);
        _source.println(" _owner;");
        _source.println();

        // If serialization support is on, then create the state
        // array.
        if (Smc.isSerial() == true)
        {
            Iterator<SmcMap> mit;
            SmcMap map;
            String mapName;
            Iterator<SmcState> sit;
            SmcState state;

            _source.print(_indent);
            _source.println(
                "    // Map state IDs to state objects.");
            _source.print(_indent);
            _source.println(
                "    // Used to deserialize an FSM.");
            _source.print(_indent);
            _source.println("    [NonSerialized]");
            _source.print(_indent);
            _source.print("    private static ");
            _source.print(context);
            _source.println("State[] _States =");
            _source.print(_indent);
            _source.print("    {");

            for (mit = maps.iterator(), separator = "";
                 mit.hasNext() == true;
                )
            {
                map = mit.next();
                mapName = map.getName();

                for (sit = map.getStates().iterator();
                     sit.hasNext() == true;
                     separator = ",")
                {
                    state = sit.next();
                    _source.println(separator);
                    _source.print(_indent);
                    _source.print("        ");
                    _source.print(mapName);
                    _source.print(".");
                    _source.print(state.getClassName());
                }
            }

            _source.println();
            _source.print(_indent);
            _source.println("    };");
            _source.println();
        }

        // Declare the inner state class.
        _source.print(_indent);
        _source.println(
            "//---------------------------------------------------------------");
        _source.print(_indent);
        _source.println("// Inner classes.");
        _source.print(_indent);
        _source.println("//");
        _source.println();
        _source.print(_indent);
        _source.print("    public abstract class ");
        _source.print(context);
        _source.println("State :");
        _source.print(_indent);
        _source.println("        statemap.State");
        _source.print(_indent);
        _source.println("    {");

        // The abstract Transitions property - if reflection was
        // is specified.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println("    //-----------------------------------------------------------");
            _source.print(_indent);
            _source.println("    // Properties.");
            _source.print(_indent);
            _source.println("    //");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "public abstract IDictionary Transitions");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println("            get;");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }

        _source.print(_indent);
        _source.println("    //-----------------------------------------------------------");
        _source.print(_indent);
        _source.println("    // Member methods.");
        _source.print(_indent);
        _source.println("    //");
        _source.println();

        // State constructor.
        _source.print(_indent);
        _source.print("        internal ");
        _source.print(context);
        _source.println("State(string name, int id) :");
        _source.print(_indent);
        _source.println("            base (name, id)");
        _source.print(_indent);
        _source.println("        {}");
        _source.println();

        // Entry/Exit methods.
        _source.print(_indent);
        _source.print(
            "        protected internal virtual void Entry(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {}");
        _source.println();
        _source.print(_indent);
        _source.print(
            "        protected internal virtual void Exit(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {}");
        _source.println();

        // Transition methods (except default).
        for (SmcTransition trans: transitions)
        {
            transName = trans.getName();

            if (transName.equals("Default") == false)
            {
                _source.print(_indent);
                _source.print(
                    "        protected internal virtual void ");
                _source.print(transName);
                _source.print("(");
                _source.print(context);
                _source.print("Context context");

                for (SmcParameter param: trans.getParameters())
                {
                    _source.print(", ");
                    param.accept(this);
                }

                _source.println(")");
                _source.print(_indent);
                _source.println("        {");

                // If this method is reached, that means this
                // transition was passed to a state which does
                // not define the transition. Call the state's
                // default transition method.
                _source.print(_indent);
                _source.println("            Default(context);");

                _source.print(_indent);
                _source.println("        }");
                _source.println();
            }
        }

        // Generate the overall Default transition for all maps.
        _source.print(_indent);
        _source.print(
            "        protected internal virtual void Default(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {");

        // If generating debug code, then write this trace
        // message.
        if (Smc.isDebug() == true)
        {
            _source.println("#if TRACE");
            _source.print(_indent);
            _source.println("            Trace.WriteLine(");
            _source.print(_indent);
            _source.print(
                "                \"TRANSITION : Default\"");
            _source.println(");");
            _source.println("#endif");
        }

        // The default transition action is to throw a
        // TransitionUndefinedException.
        _source.print(_indent);
        _source.println("            throw (");
        _source.print(_indent);
        _source.print("                ");
        _source.println(
            "new statemap.TransitionUndefinedException(");
        _source.print(_indent);
        _source.println(
            "                    \"State: \" +");
        _source.print(_indent);
        _source.println(
            "                    context.State.Name +");
        _source.print(_indent);
        _source.println(
            "                    \", Transition: \" +");
        _source.print(_indent);
        _source.println(
            "                    context.GetTransition()));");

        // Close the Default transition method.
        _source.print(_indent);
        _source.println("        }");

        // Close the inner state class declaration.
        _source.print(_indent);
        _source.println("    }");

        // Have each map print out its source code now.
        for (SmcMap map: maps)
        {
            map.accept(this);
        }

        // Close the context class.
        _source.print(_indent);
        _source.println("}");
        _source.println();

        // If a package has been specified, then generate
        // the closing brace now.
        if (packageName != null && packageName.length() > 0)
        {
            _source.println("}");
        }

        return;
    } // end of visit(SmcFSM)

    public void visit(SmcMap map)
    {
        List<SmcTransition> definedDefaultTransitions;
        SmcState defaultState = map.getDefaultState();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String indent2;
        List<SmcState> states = map.getStates();

        // Initialize the default transition list to all the
        // default state's transitions.
        if (defaultState != null)
        {
            definedDefaultTransitions =
                defaultState.getTransitions();
        }
        else
        {
            definedDefaultTransitions =
                new ArrayList<SmcTransition>();
        }

        // Declare the map class and make it abstract to prevent
        // its instantiation.
        _source.println();
        _source.print(_indent);
        _source.print("    internal abstract class ");
        _source.println(mapName);
        _source.print(_indent);
        _source.println("    {");
        _source.print(_indent);
        _source.println(
            "    //-----------------------------------------------------------");
        _source.print(_indent);
        _source.println("    // Member methods.");
        _source.print(_indent);
        _source.println("    //");
        _source.println();
        _source.print(_indent);
        _source.println(
            "    //-----------------------------------------------------------");
        _source.print(_indent);
        _source.println("    // Member data.");
        _source.print(_indent);
        _source.println("    //");
        _source.println();
        _source.print(_indent);
        _source.println(
            "        //-------------------------------------------------------");
        _source.print(_indent);
        _source.println("        // Statics.");
        _source.print(_indent);
        _source.println("        //");

        // Declare each of the state class member data.
        for (SmcState state: states)
        {
            _source.print(_indent);
            _source.println("        [NonSerialized]");
            _source.print(_indent);
            _source.print(
                "        internal static readonly ");
            _source.print(mapName);
            _source.print("_Default.");
            _source.print(mapName);
            _source.print('_');
            _source.print(state.getClassName());
            _source.print(' ');
            _source.print(state.getInstanceName());
            _source.println(" =");
            _source.print(_indent);
            _source.print("            new ");
            _source.print(mapName);
            _source.print("_Default.");
            _source.print(mapName);
            _source.print("_");
            _source.print(state.getClassName());
            _source.print("(\"");
            _source.print(mapName);
            _source.print(".");
            _source.print(state.getClassName());
            _source.print("\", ");
            _source.print(map.getNextStateId());
            _source.println(");");
        }

        // Create the default state as well.
        _source.print(_indent);
        _source.println("        [NonSerialized]");
        _source.print(_indent);
        _source.print("        private static readonly ");
        _source.print(mapName);
        _source.println("_Default Default =");
        _source.print(_indent);
        _source.print("            new ");
        _source.print(mapName);
        _source.print("_Default(\"");
        _source.print(mapName);
        _source.println(".Default\", -1);");
        _source.println();

        // End of map class.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        // Declare the map default state class.
        _source.print(_indent);
        _source.print("    internal class ");
        _source.print(mapName);
        _source.println("_Default :");
        _source.print(_indent);
        _source.print("        ");
        _source.print(context);
        _source.println("State");
        _source.print(_indent);
        _source.println("    {");

        // If reflection is on, generate the Transition property.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println(
                "    //-----------------------------------------------------------");
            _source.print(_indent);
            _source.println("    // Properties.");
            _source.print(_indent);
            _source.println("    //");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "public override IDictionary Transitions");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println("            get");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.println(
                "                return (_transitions);");
            _source.print(_indent);
            _source.println("            }");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }

        // Generate the constructor.
        _source.print(_indent);
        _source.println(
            "    //-----------------------------------------------------------");
        _source.print(_indent);
        _source.println("    // Member methods.");
        _source.print(_indent);
        _source.println("    //");
        _source.println();
        _source.print(_indent);
        _source.print("        internal ");
        _source.print(mapName);
        _source.println(
            "_Default(string name, int id) :");
        _source.print(_indent);
        _source.println("            base (name, id)");
        _source.print(_indent);
        _source.println("        {}");

        // Declare the user-defined transitions first.
        indent2 = _indent;
        _indent = _indent + "        ";
        for (SmcTransition trans: definedDefaultTransitions)
        {
            trans.accept(this);
        }
        _indent = indent2;

        // Have each state now generate its code. Each state
        // class is an inner class.
        _source.println();
        _source.print(_indent);
        _source.println(
            "    //-----------------------------------------------------------");
        _source.print(_indent);
        _source.println("    // Inner classes.");
        _source.print(_indent);
        _source.println("    //");
        for (SmcState state: states)
        {
            state.accept(this);
        }

        // If reflection is on, then define the transitions list.
        if (Smc.isReflection() == true)
        {
            List<SmcTransition> allTransitions =
                map.getFSM().getTransitions();
            String transName;
            int transDefinition;

            _source.println();
            _source.print(_indent);
            _source.println(
                "    //-----------------------------------------------------------");
            _source.print(_indent);
            _source.println("    // Member data.");
            _source.print(_indent);
            _source.println("    //");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        //-------------------------------------------------------");
            _source.print(_indent);
            _source.println("        // Statics.");
            _source.print(_indent);
            _source.println("        //");
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "private static IDictionary _transitions;");
            _source.println();
            _source.print(_indent);
            _source.print("        static ");
            _source.print(mapName);
            _source.println("_Default()");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.print("            ");
            _source.println("_transitions = new Hashtable();");

            // Now place the transition names into the list.
            for (SmcTransition transition: allTransitions)
            {
                transName = transition.getName();

                // If the transition is defined in this map's
                // default state, then the value is 2.
                if (definedDefaultTransitions.contains(
                        transition) == true)
                {
                    transDefinition = 2;
                }
                // Otherwise the value is 0 - undefined.
                else
                {
                    transDefinition = 0;
                }

                _source.print("            ");
                _source.print("_transitions.Add(\"");
                _source.print(transName);
                _source.print("\", ");
                _source.print(transDefinition);
                _source.println(");");
            }
            _source.print(_indent);
            _source.println("        }");
        }

        // End of the map default state class.
        _source.print(_indent);
        _source.println("    }");

        return;
    } // end of visit(SmcMap)

    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        List<SmcAction> actions;
        String indent2;

        // Declare the inner state class.
        _source.println();
        _source.print(_indent);
        _source.print("        internal class ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.println(" :");
        _source.print(_indent);
        _source.print("            ");
        _source.print(mapName);
        _source.println("_Default");
        _source.print(_indent);
        _source.println("        {");

        // Generate the Transitions property if reflection is on.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println(
                "        //-------------------------------------------------------");
            _source.print(_indent);
            _source.println("        // Properties.");
            _source.print(_indent);
            _source.println("        //");
            _source.println();
            _source.print(_indent);
            _source.print("            ");
            _source.println(
                "public override IDictionary Transitions");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.println("                get");
            _source.print(_indent);
            _source.println("                {");
            _source.print(_indent);
            _source.println(
                "                    return (_transitions);");
            _source.print(_indent);
            _source.println("                }");
            _source.print(_indent);
            _source.println("            }");
            _source.println();
        }

        // Add the constructor.
        _source.print(_indent);
        _source.println(
            "        //-------------------------------------------------------");
        _source.print(_indent);
        _source.println("        // Member methods.");
        _source.print(_indent);
        _source.println("        //");
        _source.println();
        _source.print(_indent);
        _source.print("            internal ");
        _source.print(mapName);
        _source.print("_");
        _source.print(stateName);
        _source.println("(string name, int id) :");
        _source.print(_indent);
        _source.println("                base (name, id)");
        _source.print(_indent);
        _source.println("            {}");

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print(_indent);
            _source.print("            ");
            _source.print(
                "protected internal override void Entry(");
            _source.print(context);
            _source.println("Context context)");
            _source.print(_indent);
            _source.println("            {");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("                ");
            _source.print(context);
            _source.println(" ctxt = context.Owner;");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "                ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }
            _indent = indent2;

            // End of the Entry() method.
            _source.print(_indent);
            _source.println("                return;");
            _source.print(_indent);
            _source.println("            }");
        }

        actions = state.getExitActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.println();
            _source.print(_indent);
            _source.print("            ");
            _source.print(
                "protected internal override void Exit(");
            _source.print(context);
            _source.println("Context context)");
            _source.print(_indent);
            _source.println("            {");

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("                ");
            _source.print(context);
            _source.println(" ctxt = context.Owner;");
            _source.println();

            // Generate the actions associated with this code.
            indent2 = _indent;
            _indent = _indent + "                ";
            for (SmcAction action: actions)
            {
                action.accept(this);
            }

            // End of the Exit() method.
            _source.print(_indent);
            _source.println("                return;");
            _source.print(_indent);
            _source.println("            }");
        }

        // Have each transition generate its code.
        indent2 = _indent;
        _indent = _indent + "            ";
        for (SmcTransition trans: state.getTransitions())
        {
            trans.accept(this);
        }
        _indent = indent2;

        // If reflection is on, then generate the transitions
        // map.
        if (Smc.isReflection() == true)
        {
            List<SmcTransition> allTransitions =
                map.getFSM().getTransitions();
            List<SmcTransition> stateTransitions =
                state.getTransitions();
            SmcState defaultState = map.getDefaultState();
            List<SmcTransition> defaultTransitions;
            String transName;
            int transDefinition;

            // Initialize the default transition list to all the
            // default state's transitions.
            if (defaultState != null)
            {
                defaultTransitions =
                    defaultState.getTransitions();
            }
            else
            {
                defaultTransitions =
                    new ArrayList<SmcTransition>();
            }

            _source.println();
            _source.print(_indent);
            _source.println(
                "        //-------------------------------------------------------");
            _source.print(_indent);
            _source.println("        // Member data.");
            _source.print(_indent);
            _source.println("        //");
            _source.println();
            _source.print(_indent);
            _source.println(
                "            //---------------------------------------------------");
            _source.print(_indent);
            _source.println("            // Statics.");
            _source.print(_indent);
            _source.println("            //");
            _source.print(_indent);
            _source.print("            ");
            _source.println(
                "new private static IDictionary _transitions;");
            _source.println();
            _source.print(_indent);
            _source.print("            static ");
            _source.print(mapName);
            _source.print("_");
            _source.print(stateName);
            _source.println("()");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.print("                ");
            _source.println("_transitions = new Hashtable();");

            // Now place the transition names into the list.
            for (SmcTransition transition: allTransitions)
            {
                transName = transition.getName();

                // If the transition is in this state, then its
                // value is 1.
                if (stateTransitions.contains(
                        transition) == true)
                {
                    transDefinition = 1;
                }
                // If the transition is defined in this map's
                // default state, then the value is 2.
                else if (defaultTransitions.contains(
                             transition) == true)
                {
                    transDefinition = 2;
                }
                // Otherwise the value is 0 - undefined.
                else
                {
                    transDefinition = 0;
                }

                _source.print("                ");
                _source.print("_transitions.Add(\"");
                _source.print(transName);
                _source.print("\", ");
                _source.print(transDefinition);
                _source.println(");");
            }

            _source.print(_indent);
            _source.println("            }");
        }

        // End of state declaration.
        _source.print(_indent);
        _source.println("        }");

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
        List<SmcParameter> parameters =
            transition.getParameters();
        List<SmcGuard> guards = transition.getGuards();
        boolean nullCondition = false;
        Iterator<SmcGuard> git;
        SmcGuard guard;

        _source.println();
        _source.print(_indent);
        _source.print("protected internal override void ");
        _source.print(transName);
        _source.print("(");
        _source.print(context);
        _source.print("Context context");

        // Add user-defined parameters.
        for (SmcParameter param: parameters)
        {
            _source.print(", ");
            param.accept(this);
        }
        _source.println(")");

        _source.print(_indent);
        _source.println("{");

        // Almost all transitions have a "ctxt" local variable.
        if (transition.hasCtxtReference() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.print("    ");
            _source.print(context);
            _source.println(" ctxt = context.Owner;");
            _source.println();
        }

        // Output transition to debug stream.
        if (Smc.isDebug() == true)
        {
            String sep;

            _source.println();
            _source.println("#if TRACE");
            _source.print(_indent);
            _source.println("    Trace.WriteLine(");
            _source.print(_indent);
            _source.print(
                "        \"TRANSITION   : ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.print(".");
            _source.print(transName);

            // Output the transition parameters.
            _source.print("(");
            for (SmcParameter param: parameters)
            {
                _source.print(", ");
                param.accept(this);
            }
            _source.print(")");
            _source.println("\");");
            _source.println("#endif");
            _source.println();
        }

        // Loop through the guards and print each one.
        _guardIndex = 0;
        _guardCount = guards.size();
        for (git = guards.iterator();
             git.hasNext() == true;
             ++_guardIndex)
        {
            guard = git.next();

            // Count up the guards with no condition.
            if (guard.getCondition().length() == 0)
            {
                nullCondition = true;
            }

            guard.accept(this);
        }

        // If all guards have a condition, then create a final
        // "else" clause which passes control to the default
        // transition. Pass all arguments into the default
        // transition.
        if (_guardIndex > 0 && nullCondition == false)
        {
            // If there was only one guard, then we need to close
            // of its body.
            if (_guardCount == 1)
            {
                _source.print(_indent);
                _source.println("}");
            }

            _source.print(_indent);
            _source.print("    else");
            _source.print(_indent);
            _source.println("    {");

            // Call the super class' transition method using
            // the "base" keyword and not the class name.
            _source.print(_indent);
            _source.print("        base.");
            _source.print(transName);
            _source.print("(context");
            for (SmcParameter param: parameters)
            {
                _source.print(", ");
                _source.print(param.getName());
            }
            _source.println(");");

            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }
        // Need to add a final newline after a multiguard block.
        else if (_guardCount > 1)
        {
            _source.println();
            _source.println();
        }

        // End of transition.
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
        String indent4;
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List<SmcAction> actions = guard.getActions();
        boolean hasActions = !(actions.isEmpty());

        // If this guard's end state is not of the form
        // "map::state", then prepend the map name to the
        // state name.
        // DON'T DO THIS IF THIS IS A POP TRANSITION!
        // The "state" is actually a transition name.
        if (transType != Smc.TRANS_POP &&
            endStateName.length () > 0 &&
            endStateName.equals(NIL_STATE) == false)
        {
            endStateName = scopeStateName(endStateName, mapName);
        }

        // Qualify the state and push state names as well.
        stateName = scopeStateName(stateName, mapName);
        pushStateName = scopeStateName(pushStateName, mapName);

        loopbackFlag =
            isLoopback(transType, stateName, endStateName);

        // The guard code generation is a bit tricky. The first
        // question is how many guards are there? If there are
        // more than one, then we will need to generate the
        // proper "if-then-else" code.
        if (_guardCount > 1)
        {
            indent2 = _indent + "        ";

            // There are multiple guards.
            // Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an "if"
                // should be used.
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
                // does have a condition. Use an "else if".
                _source.println();
                _source.print(_indent);
                _source.print("    else if (");
                _source.print(condition);
                _source.println(")");
                _source.print(_indent);
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
        // There is only one guard. Does this guard have
        // a condition?
        else if (condition.length() == 0)
        {
            // No. This is a plain, old. vanilla transition.
            indent2 = _indent + "    ";
        }
        else
        {
            // Yes there is a condition.
            indent2 = _indent + "        ";

            _source.print(_indent);
            _source.print("    if (");
            _source.print(condition);
            _source.println(")");
            _source.print(_indent);
            _source.println("    {");
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transition's actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (hasActions == false && endStateName.length() != 0)
        {
            fqEndStateName = endStateName;
        }
        else if (hasActions == true)
        {
            // Save away the current state if this is a loopback
            // transition. Storing current state allows the
            // current state to be cleared before any actions are
            // executed. Remember: actions are not allowed to
            // issue transitions and clearing the current state
            // prevents them from doing do.
            if (loopbackFlag == true)
            {
                fqEndStateName = "endState";

                _source.print(indent2);
                _source.print(context);
                _source.print("State ");
                _source.print(fqEndStateName);
                _source.println(" = context.State;");
            }
            else
            {
                fqEndStateName = endStateName;
            }
        }

        // Decide if runtime loopback checking must be done.
        if (defaultFlag == true &&
            transType != Smc.TRANS_POP &&
            loopbackFlag == false)
        {
            _source.print(indent2);
            _source.println("bool loopbackFlag =");
            _source.print(indent2);
            _source.print(
                "    (context.State.Name == ");
            _source.print(fqEndStateName);
            _source.println(".Name);");
        }

        _source.println();

        // Dump out the exit actions if
        // 1) this is a standard, non-loopback transition or
        // 2) a pop transition.
        if (transType == Smc.TRANS_POP || loopbackFlag == false)
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
            _source.println("context.State.Exit(context);");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("}");
                _source.println();
            }
        }

        // Dump out this transition's actions.
        if (hasActions == false)
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
            // Now that we are in the transition, clear the
            // current state.
            _source.print(indent2);
            _source.println("context.ClearState();");

            // v. 2.0.0: Place the actions inside a try/finally
            // block. This way the state will be set before an
            // exception leaves the transition method.
            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.println();
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

            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.print(indent2);
                _source.println("}");
                _source.print(indent2);
                _source.println("finally");
                _source.print(indent2);
                _source.println("{");
            }
        }

        // Print the state assignment if necessary. Do NOT
        // generate the state assignment if:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        if (transType == Smc.TRANS_SET &&
            (hasActions == true || loopbackFlag == false))
        {
            _source.print(indent3);
            _source.print("context.State = ");
            _source.print(fqEndStateName);
            _source.println(";");
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            // Set the next state so this it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            // v. 4.3.0: If the full-qualified end state is
            // "nil", then don't need to do anything.
            if ((loopbackFlag == false || hasActions == true) &&
                fqEndStateName.equals(NIL_STATE) == false)
            {
                _source.print(indent3);
                _source.print("context.State = ");
                _source.print(fqEndStateName);
                _source.println(";");
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
                }

                _source.print(indent4);
                _source.println("context.State.Entry(context);");

                if (defaultFlag == true)
                {
                    _source.print(indent3);
                    _source.println("}");
                }
            }

            _source.print(indent3);
            _source.print("context.PushState(");
            _source.print(pushStateName);
            _source.println(");");
        }
        else if (transType == Smc.TRANS_POP)
        {
            _source.print(indent3);
            _source.println("context.PopState();");
        }

        // Perform the new state's enty actions if:
        // 1) this is a standard, non-loopback transition or
        // 2) a push transition.
        if ((transType == Smc.TRANS_SET &&
              endStateName.equals(NIL_STATE) == false &&
              endStateName.equals(stateName) == false) ||
             transType == Smc.TRANS_PUSH)
        {
            indent4 = indent3;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent4 = indent3 + "    ";

                _source.println();
                _source.print(indent3);
                _source.println("if (loopbackFlag == false)");
                _source.print(indent3);
                _source.println("{");
            }

            _source.print(indent4);
            _source.println("context.State.Entry(context);");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent3);
                _source.println("}");
                _source.println();
            }
        }

        // If there was a try/finally, then put the closing
        // brace on the finally block.
        // v. 2.2.0: Check if the user has turned off this
        // feature first.
        if (hasActions == true && Smc.isNoCatch() == false)
        {
            _source.print(indent2);
            _source.println("}");
            _source.println();
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == Smc.TRANS_POP &&
            endStateName.equals(NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.println();
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
        List<String> arguments = action.getArguments();
        Iterator<String> it;
        String sep;

        _source.print(_indent);

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        if (name.equals("EmptyStateStack") == true)
        {
            _source.println("context.EmptyStateStack();");
        }
        else if (action.isProperty() == true)
        {
            String arg = arguments.get(0);

            _source.print("ctxt.");
            _source.print(name);
            _source.print(" = ");
            _source.print(arg);
            _source.println(";");
        }
        else
        {
            _source.print("ctxt.");
            _source.print(name);
            _source.print("(");

            for (it = arguments.iterator(), sep = "";
                 it.hasNext() == true;
                 sep = ", ")
            {
                _source.print(sep);
                _source.print(it.next());
            }

            _source.println(");");
        }

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
} // end of class SmcCSharpGenerator

//
// CHANGE LOG
// $Log$
// Revision 1.12  2008/03/21 14:03:16  fperrad
// refactor : move from the main file Smc.java to each language generator the following data :
//  - the default file name suffix,
//  - the file name format for the generated SMC files
//
// Revision 1.11  2008/01/14 19:59:23  cwrapp
// Release 5.0.2 check-in.
//
// Revision 1.10  2007/02/21 13:54:15  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.9  2007/01/15 00:23:50  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.8  2006/09/16 15:04:28  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.7  2006/06/03 19:39:25  cwrapp
// Final v. 4.3.1 check in.
//
// Revision 1.6  2005/11/07 19:34:54  cwrapp
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
// Revision 1.5  2005/09/19 15:20:03  cwrapp
// Changes in release 4.2.2:
// New features:
//
// None.
//
// Fixed the following bugs:
//
// + (C#) -csharp not generating finally block closing brace.
//
// Revision 1.4  2005/09/14 01:51:33  cwrapp
// Changes in release 4.2.0:
// New features:
//
// None.
//
// Fixed the following bugs:
//
// + (Java) -java broken due to an untested minor change.
//
// Revision 1.3  2005/08/26 15:21:34  cwrapp
// Final commit for release 4.2.0. See README.txt for more information.
//
// Revision 1.2  2005/06/30 10:44:23  cwrapp
// Added %access keyword which allows developers to set the generate Context
// class' accessibility level in Java and C#.
//
// Revision 1.1  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.2  2005/02/21 15:34:38  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:10:36  charlesr
// Modified isLoopback() to new signature.
//
// Revision 1.0  2005/02/03 17:10:08  charlesr
// Initial revision
//
