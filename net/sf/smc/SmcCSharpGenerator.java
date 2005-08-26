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
// Copyright (C) 2005. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//   Eitan Suez contributed examples/Ant.
//   (Name withheld) contributed the C# code generation and
//   examples/C#.
//   Francois Perrad contributed the Python code generation and
//   examples/Python.
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
import java.util.Comparator;
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

    public SmcCSharpGenerator(PrintStream source,
                              String srcfileBase)
    {
        super (source, srcfileBase);
    }

    public void visit(SmcFSM fsm)
    {
        String rawSource = fsm.getSource();
        String packageName = fsm.getPackage();
        String context = fsm.getContext();
        String startState = fsm.getStartState();
        String accessLevel = fsm.getAccessLevel();
        List maps = fsm.getMaps();
        List transitions;
        Iterator it;
        Iterator it2;
        SmcMap map;
        SmcTransition trans;
        String transName;
        String csState;
        String separator;
        int index;
        List params;
        String indent2;

        // If the access level has not been set, then the
        // default is "public".
        // TODO
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
            _source.println("using System.IO;");
        }
        _source.println();

        // Do user-specified imports now.
        for (it = fsm.getImports().iterator();
             it.hasNext() == true;
            )
        {
            _source.print("using ");
            _source.print(it.next());
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
            _source.println("[Serializable]");
        }

        // Now declare the FSM context class.
        _source.print(_indent);
        _source.print(accessLevel);
        _source.print(" sealed class ");
        _source.print(context);
        _source.println("Context :");
        _source.print(_indent);
        _source.println("    statemap.FSMContext");
        _source.print(_indent);
        _source.println("{");
        _source.print(_indent);
        _source.println("// Member methods.");
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

        // If debugging code is being generated, then direct
        // the debug trace to the console.
        if (Smc.isDebug() == true)
        {
            _source.println();
            _source.print(_indent);
            _source.println(
                "        // Register the console as a trace listener");
            _source.print(_indent);
            _source.println(
                "        TextWriterTraceListener myWriter =");
            _source.print(_indent);
            _source.print(
                "            new TextWriterTraceListener(");
            _source.println("System.Console.Out);");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        Trace.Listeners.Add(myWriter);");
            _source.println();
            _source.print(_indent);
        }

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

        // Generate the default transition methods.
        // First get the transition list.
        transitions = (List) new ArrayList();
        for (it = maps.iterator(); it.hasNext() == true;)
        {
            map = (SmcMap) it.next();

            // Merge the new transitions into the current set.
            transitions =
                Smc.merge(
                    map.getTransitions(),
                    transitions,
                    new Comparator()
                    {
                        public int compare(Object o1,
                                           Object o2)
                        {
                            return (
                                ((SmcTransition) o1).compareTo(
                                    (SmcTransition) o2));
                        }
                    });
        }

        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();
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
                for (it2 = params.iterator(), separator = "";
                     it2.hasNext() == true;
                     separator = ", ")
                {
                    _source.print(separator);
                    ((SmcParameter) it2.next()).accept(this);
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

                for (it2 = params.iterator();
                     it2.hasNext() == true;
                    )
                {
                    _source.print(", ");
                    _source.print(
                        ((SmcParameter) it2.next()).getName());
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

        // getState() method.
        _source.print(_indent);
        _source.print("    internal ");
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

        // Close the state get.
        _source.print(_indent);
        _source.println("        }");
        _source.println();

        // Now generate the state set.
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
        _source.println("_state = value;");

        // If we are in a lock block, close it.
        if (Smc.isSynchronized() == true)
        {
            _source.print(_indent);
            _source.println("            }");
        }

        // Close the state set.
        _source.print(_indent);
        _source.println("        }");

        // Close the state property.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        // Generate the owner property.
        _source.print(_indent);
        _source.print("    internal ");
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

        // Close the owner property.
        _source.print(_indent);
        _source.println("    }");
        _source.println();

        // If serialization is turned on, then output the
        // WriteObject and ReadObject methods.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.print("    private void WriteObject(");
            _source.println("BinaryWriter writer)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println(
                "        int size = _state_stack.Count;");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        writer.Write((int) (size + 1));");
            _source.println();
            _source.print(_indent);
            _source.print(
                "        foreach (");
            _source.print(context);
            _source.println("State state in _state_stack)");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.println(
                "            writer.Write(state.Id);");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        writer.Write(base._state.Id);");
            _source.print(_indent);
            _source.println("        return;");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
            _source.print(_indent);
            _source.print("    private void ReadObject(");
            _source.println("BinaryReader reader)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println("        int size;");
            _source.print(_indent);
            _source.println("        int i;");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        size = reader.ReadInt32();");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        for (i = 0; i < size; ++i)");
            _source.print(_indent);
            _source.println("        {");
            _source.print(_indent);
            _source.print("            PushState(_States[");
            _source.println("reader.ReadInt32()]);");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
            _source.print(_indent);
            _source.println("        return;");
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Declare member data.
        _source.print(_indent);
        _source.println("// Member data.");
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
            String mapName;
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

            for (it = maps.iterator(), separator = "";
                 it.hasNext() == true;
                )
            {
                map = (SmcMap) it.next();
                mapName = map.getName();

                for (it2 = map.getStates().iterator();
                     it2.hasNext() == true;
                     separator = ",")
                {
                    state = (SmcState) it2.next();
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
        _source.println("// Inner classes.");
        _source.println();
        _source.print(_indent);
        _source.print("    public abstract class ");
        _source.print(context);
        _source.println("State :");
        _source.print(_indent);
        _source.println("        statemap.State");
        _source.print(_indent);
        _source.println("    {");

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
        _source.print("        internal virtual void Entry(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {}");
        _source.println();
        _source.print(_indent);
        _source.print("        internal virtual void Exit(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {}");
        _source.println();

        // Transition methods (except default).
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();
            transName = trans.getName();

            if (transName.equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("        internal virtual void ");
                _source.print(transName);
                _source.print("(");
                _source.print(context);
                _source.print("Context context");

                for (it2 = trans.getParameters().iterator();
                     it2.hasNext() == true;
                    )
                {
                    _source.print(", ");
                    ((SmcParameter) it2.next()).accept(this);
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
        _source.print("        internal virtual void Default(");
        _source.print(context);
        _source.println("Context context)");
        _source.print(_indent);
        _source.println("        {");

        // If generating debug code, then write this trace
        // message.
        if (Smc.isDebug() == true)
        {
            _source.print(_indent);
            _source.println(
                "            if (context._debug_flag == true)");
            _source.print(_indent);
            _source.println("            {");
            _source.print(_indent);
            _source.println(
                "                Trace.WriteLine(");
            _source.print(_indent);
            _source.print(
                "                    \"TRANSITION : Default\"");
            _source.println(");");
            _source.println();
            _source.print(_indent);
            _source.println("            }");
            _source.println();
        }

        // The default transition action is to throw a
        // TransitionUndefinedException.
        _source.print(_indent);
        _source.println("            throw (");
        _source.print(_indent);
        _source.println(
            "                new statemap.TransitionUndefinedException(");
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
        for (it = maps.iterator(); it.hasNext() == true;)
        {
            ((SmcMap) it.next()).accept(this);
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
    }

    public void visit(SmcMap map)
    {
        List definedDefaultTransitions;
        SmcState defaultState = map.getDefaultState();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String indent2;
        List states = map.getStates();
        Iterator it;
        SmcState state;

        // Initialize the default transition list to all the
        // default state's transitions.
        if (defaultState != null)
        {
            definedDefaultTransitions =
                defaultState.getTransitions();
        }
        else
        {
            definedDefaultTransitions = (List) new ArrayList();
        }

        // Declare the map class and make it abstract to prevent
        // its instantiation.
        _source.println();
        _source.print(_indent);
        _source.print("    /* package */ abstract class ");
        _source.println(mapName);
        _source.print(_indent);
        _source.println("    {");

        // Declare each of the state class member data.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            state = (SmcState) it.next();

            _source.print(_indent);
            _source.print(
                "        /* package */ internal static ");
            _source.print(mapName);
            _source.print("_Default.");
            _source.print(mapName);
            _source.print('_');
            _source.print(state.getClassName());
            _source.print(' ');
            _source.print(state.getInstanceName());
            _source.println(';');
        }

        // Create the default state as well.
        _source.print(_indent);
        _source.print("        private static ");
        _source.print(mapName);
        _source.println("_Default Default;");
        _source.println();

        // Declare the static block.
        _source.print(_indent);
        _source.print("        static ");
        _source.print(mapName);
        _source.println("()");
        _source.print(_indent);
        _source.println("        {");

        // Initialize the static state objects.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            state = (SmcState) it.next();

            _source.print(_indent);
            _source.print("            ");
            _source.print(state.getInstanceName());
            _source.print(" = new ");
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

        // Instantiate a default state as well.
        _source.print(_indent);
        _source.print("            Default = new ");
        _source.print(mapName);
        _source.print("_Default(\"");
        _source.print(mapName);
        _source.println(".Default\", -1);");

        // End of static block.
        _source.print(_indent);
        _source.println("        }");

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

        // Generate the constructor.
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
        for (it = definedDefaultTransitions.iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }
        _indent = indent2;

        // Have each state now generate its code. Each state
        // class is an inner class.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            ((SmcState) it.next()).accept(this);
        }

        // End of the map default state class.
        _source.print(_indent);
        _source.println("    }");

        return;
    }

    public void visit(SmcState state)
    {
        SmcMap map = state.getMap();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        String stateName = state.getClassName();
        List actions;
        String indent2;
        Iterator it;

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

        // Add the constructor.
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
        _source.println();

        // Add the Entry() and Exit() methods if this state
        // defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.isEmpty() == false)
        {
            _source.print(_indent);
            _source.print(
                "            internal override void Entry(");
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
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
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
            _source.print(_indent);
            _source.print(
                "            internal override void Exit(");
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
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
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
        for (it = state.getTransitions().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }
        _indent = indent2;

        // End of state declaration.
        _source.print(_indent);
        _source.println("        }");

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
        List parameters = transition.getParameters();
        List guards = transition.getGuards();
        boolean nullCondition = false;
        Iterator it;
        SmcGuard guard;

        _source.println();
        _source.print(_indent);
        _source.print("internal override void ");
        _source.print(transName);
        _source.print("(");
        _source.print(context);
        _source.print("Context context");

        // Add user-defined parameters.
        for (it = parameters.iterator(); it.hasNext() == true;)
        {
            _source.print(", ");
            ((SmcParameter) it.next()).accept(this);
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
            _source.print(_indent);
            _source.println(
                "    if (context._debug_flag == true)");
            _source.print(_indent);
            _source.println("    {");
            _source.print(_indent);
            _source.println(
                "        Trace.WriteLine(");
            _source.print(_indent);
            _source.print(
                "            \"TRANSITION   : ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.print(".");
            _source.print(transName);

            // Output the transition parameters.
            _source.print("(");
            for (it = parameters.iterator();
                 it.hasNext() == true;
                )
            {
                _source.print(", ");
                ((SmcParameter) it.next()).accept(this);
            }
            _source.print(")");
            _source.println("\");");

            // End of debug output.
            _source.print(_indent);
            _source.println("    }");
            _source.println();
        }

        // Loop through the guards and print each one.
        _guardIndex = 0;
        _guardCount = guards.size();
        for (it = guards.iterator();
             it.hasNext() == true;
             ++_guardIndex)
        {
            guard = (SmcGuard) it.next();

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
            for (it = parameters.iterator();
                 it.hasNext() == true;
                )
            {
                _source.print(", ");
                _source.print(
                    ((SmcParameter) it.next()).getName());
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
        String indent4;
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List actions = guard.getActions();
        boolean hasActions = actions.isEmpty();

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

            // There are multiple guards. Is this the first guard?
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
        if (hasActions == true && endStateName.length() != 0)
        {
            fqEndStateName = endStateName;
        }
        else if (hasActions == false)
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
            _source.println(
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
            if (transType == Smc.TRANS_SET && defaultFlag == true)
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
        if (hasActions == true)
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
            Iterator it;

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
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
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
            (hasActions == false || loopbackFlag == false))
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
            if (loopbackFlag == false || hasActions == true)
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
        if (hasActions == false && Smc.isNoCatch() == false)
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
    }

    public void visit(SmcAction action)
    {
        String name = action.getName();
        List arguments = action.getArguments();
        Iterator it;
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
            String arg = (String) arguments.get(0);

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
                _source.print((String) it.next());
            }

            _source.println(");");
        }

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print(parameter.getType());
        _source.print(" ");
        _source.print(parameter.getName());

        return;
    }

//---------------------------------------------------------------
// Member data
//
}

//
// CHANGE LOG
// $Log$
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
