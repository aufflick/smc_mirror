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
 * Visits the abstract syntax tree, emitting VB.Net code.
 * @see SmcElement
 * @see SmcCodeGenerator
 * @see SmcVisitor
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcVBGenerator
    extends SmcCodeGenerator
{
//---------------------------------------------------------------
// Member methods
//

    public SmcVBGenerator(PrintStream source,
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
        List maps = fsm.getMaps();
        List transitions;
        Iterator it;
        Iterator it2;
        SmcMap map;
        SmcState state;
        SmcTransition trans;
        String transName;
        SmcParameter param;
        String vbState;
        String separator;
        int index;
        List params;
        String indent2;

        // Dump out the raw source code, if any.
        if (rawSource != null && rawSource.length () > 0)
        {
            _source.println(rawSource);
            _source.println();
        }

        // If reflection is on, then import the .Net collections
        // package.
        if (Smc.isReflection() == true)
        {
            _source.println("Imports System.Collections");
        }

        // Do user-specified imports now.
        for (it = fsm.getImports().iterator();
             it.hasNext() == true;
            )
        {
            _source.print("Imports ");
            _source.println(it.next());
        }

        // If serialization is on, then import the .Net
        // serialization package.
        if (Smc.isSerial() == true)
        {
            _source.println(
                "Imports System.Runtime.Serialization");
        }
        _source.println();

        // If a package has been specified, generate the package
        // statement now and set the indent.
        if (packageName != null && packageName.length() > 0)
        {
            _source.print("Namespace ");
            _source.println(packageName);
            _source.println();
            _indent = "    ";
        }

        // If -serial was specified, then prepend the serialize
        // attribute to the class declaration.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.print("<Serializable()> ");
        }

        // Now declare the context class.
        _source.print(_indent);
        _source.print("Public NotInheritable Class ");
        _source.print(context);
        _source.println("Context");
        _source.print(_indent);
        _source.println("    Inherits statemap.FSMContext");

        // If -serial was specified, then we also implement the
        // ISerializable interface.
        if (Smc.isSerial() == true)
        {
            _source.print(_indent);
            _source.println("    Implements ISerializable");
        }

        // Declare the associated application class as a data
        // member.
        _source.println();
        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Member data");
        _source.print(_indent);
        _source.println("    '");
        _source.print(_indent);
        _source.println();
        _source.print(_indent);
        _source.println(
            "    ' The associated application class instance.");
        _source.print(_indent);
        _source.print("    Private _owner As ");
        _source.println(context);
        _source.println();

        // If serialization is on, then the shared state array
        // must be generated.
        if (Smc.isSerial() == true)
        {
            String mapName;

            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Shared data");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    ' State instance array. ");
            _source.println("Used to deserialize.");
            _source.print(_indent);
            _source.print(
                "    Private Shared ReadOnly _States() As ");
            _source.print(context);
            _source.println("State = _");
            _source.print(_indent);
            _source.print("        {");

            // For each map, ...
            for (it = maps.iterator(), separator = " _";
                 it.hasNext() == true;
                )
            {
                map = (SmcMap) it.next();
                mapName = map.getName();

                // and for each map state, ...
                for (it2 = map.getStates().iterator();
                     it2.hasNext() == true;
                     separator = ", _")
                {
                    state = (SmcState) it2.next();

                    // Add its singleton instance to the array.
                    _source.println(separator);
                    _source.print(_indent);
                    _source.print("            ");
                    _source.print(mapName);
                    _source.print(".");
                    _source.print(state.getClassName());
                }
            }

            _source.println(" _");
            _source.print(_indent);
            _source.println("        }");
            _source.println();
        }

        // Now declare the current state and owner properties.
        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Properties");
        _source.print(_indent);
        _source.println("    '");
        _source.println();
        _source.print(_indent);
        _source.print("    Public Property State() As ");
        _source.print(context);
        _source.println("State");
        _source.print(_indent);
        _source.println("        Get");
        _source.print(_indent);
        _source.println("            If _state Is Nothing _");
        _source.print(_indent);
        _source.println("            Then");
        _source.print(_indent);
        _source.print("                Throw ");
        _source.println(
            "New statemap.StateUndefinedException()");
        _source.print(_indent);
        _source.println("            End If");
        _source.println();
        _source.print(_indent);
        _source.println("            Return _state");
        _source.print(_indent);
        _source.println("        End Get");
        _source.println();
        _source.print(_indent);
        _source.print("        Set(ByVal state As ");
        _source.print(context);
        _source.println("State)");
        _source.println();
        _source.print(_indent);
        _source.println("            _state = state");
        _source.print(_indent);
        _source.println("        End Set");
        _source.print(_indent);
        _source.println("    End Property");
        _source.println();
        _source.print(_indent);
        _source.print(
            "    Public Property Owner() As ");
        _source.println(context);
        _source.print(_indent);
        _source.println("        Get");
        _source.print(_indent);
        _source.println("            Return _owner");
        _source.print(_indent);
        _source.println("        End Get");
        _source.print(_indent);
        _source.print("        Set(ByVal owner As ");
        _source.print(context);
        _source.println(")");
        _source.println();
        _source.print(_indent);
        _source.println("            If owner Is Nothing _");
        _source.print(_indent);
        _source.println("            Then");
        _source.print(_indent);
        _source.println(
            "                Throw New NullReferenceException");
        _source.print(_indent);
        _source.println("            End If");
        _source.println();
        _source.print(_indent);
        _source.println("            _owner = owner");
        _source.print(_indent);
        _source.println("        End Set");
        _source.print(_indent);
        _source.println("    End Property");
        _source.println();

        // Generate the class member methods, starting with the
        // constructor.
        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Member methods");
        _source.print(_indent);
        _source.println("    '");
        _source.println();
        _source.print(_indent);
        _source.print("    Public Sub New(ByRef owner As ");
        _source.print(context);
        _source.println(")");
        _source.println();
        _source.print(_indent);
        _source.println("        _owner = owner");

        // The state name "map::state" must be changed to
        // "map.state".
        if ((index = startState.indexOf("::")) >= 0)
        {
            vbState = startState.substring(0, index) +
                      "." +
                      startState.substring(index + 2);
        }
        else
        {
            vbState = startState;
        }

        _source.print(_indent);
        _source.print("        _state = ");
        _source.println(vbState);

        // Execute the start state's entry actions.
        _source.print(_indent);
        _source.print("        ");
        _source.print(vbState);
        _source.println(".Entry(Me)");

        _source.print(_indent);
        _source.println("    End Sub");
        _source.println();

        // Generate the transition methods. These methods are all
        // formatted: set transition name, call the current
        // state's transition method, clear the transition name.
        transitions = fsm.getTransitions();
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();

            // Ignore the default transition.
            if (trans.getName().equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("    Public Sub ");
                _source.print(trans.getName());
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
                _source.println();

                // If the -sync flag was specified, then output
                // "SyncLock Me" to prevent multiple threads from
                // access this state machine simultaneously.
                if (Smc.isSynchronized() == true)
                {
                    _source.print(_indent);
                    _source.println("        SyncLock Me");
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
                _source.print(trans.getName());
                _source.println("\"");

                _source.print(indent2);
                _source.print("State.");
                _source.print(trans.getName());
                _source.print("(Me");

                for (it2 = params.iterator();
                     it2.hasNext() == true;
                    )
                {
                    param = (SmcParameter) it2.next();

                    _source.print(", ");
                    _source.print(param.getName());
                }
                _source.println(")");
                _source.print(indent2);
                _source.println("_transition = \"\"");

                // If the -sync flag was specified, then output
                // the "End SyncLock".
                if (Smc.isSynchronized() == true)
                {
                    _source.print(_indent);
                    _source.println("        End SyncLock");
                }

                _source.print(_indent);
                _source.println("    End Sub");
                _source.println();
            }
        }

        // If serialization is one, then output the GetObjectData
        // and deserialize constructor.
        if (Smc.isSerial() == true)
        {
            // Output the ValueOf method.
            _source.print(_indent);
            _source.print("    Public Function ValueOf(");
            _source.print("ByVal stateId As Integer) As ");
            _source.print(context);
            _source.println("State");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        Return _States(stateId)");
            _source.print(_indent);
            _source.println("    End Function");
            _source.println();

            _source.print(_indent);
            _source.print("    Private Sub GetObjectData(");
            _source.println(
                "ByVal info As SerializationInfo, _");
            _source.print(_indent);
            _source.print("                              ");
            _source.println(
                "ByVal context As StreamingContext) _");
            _source.print(_indent);
            _source.print("            ");
            _source.println(
                "Implements ISerializable.GetObjectData");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        Dim stackSize As Integer = 0");
            _source.print(_indent);
            _source.println("        Dim index As Integer");
            _source.print(_indent);
            _source.println("        Dim it As IEnumerator");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        If Not IsNothing(_stateStack) _");
            _source.print(_indent);
            _source.println("        Then");
            _source.print(_indent);
            _source.println(
                "            stackSize = _stateStack.Count");
            _source.print(_indent);
            _source.println(
                "            it = _stateStack.GetEnumerator()");
            _source.print(_indent);
            _source.println("        End If");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println(
                "info.AddValue(\"stackSize\", stackSize)");
            _source.println();
            _source.print(_indent);
            _source.println("        index = 0");
            _source.print(_indent);
            _source.println("        While index < stackSize");
            _source.print(_indent);
            _source.println("            info.AddValue( _");
            _source.print(_indent);
            _source.print("                ");
            _source.println(
                "String.Concat(\"stackItem\", index), _");
            _source.print(_indent);
            _source.println(
                "                              it.Current.Id)");
            _source.print(_indent);
            _source.println(
                "                it.MoveNext()");
            _source.print(_indent);
            _source.println(
                "                index += 1");
            _source.print(_indent);
            _source.println("        End While");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        info.AddValue(\"state\", _state.Id)");
            _source.print(_indent);
            _source.println("    End Sub");
            _source.println();
            _source.print(_indent);
            _source.print("    Private Sub New(");
            _source.println(
                "ByVal info As SerializationInfo, _");
            _source.print(_indent);
            _source.print("                    ");
            _source.println(
                "ByVal context As StreamingContext)");
            _source.println();
            _source.print(_indent);
            _source.println("        Dim stackSize As Integer");
            _source.print(_indent);
            _source.println("        Dim stateId As Integer");
            _source.println();
            _source.print(_indent);
            _source.print("        stackSize = ");
            _source.println("info.GetInt32(\"stackSize\")");
            _source.print(_indent);
            _source.println("        If stackSize > 0 _");
            _source.print(_indent);
            _source.println("        Then");
            _source.print(_indent);
            _source.println("            Dim index As Integer");
            _source.println();
            _source.print(_indent);
            _source.println(
                "            _stateStack = New Stack()");
            _source.print(_indent);
            _source.println(
                "            index = stackSize - 1");
            _source.print(_indent);
            _source.println(
                "            While index >= 0");
            _source.print(_indent);
            _source.println(
                "                stateId = _");
            _source.print(_indent);
            _source.println(
                "                    info.GetInt32( _");
            _source.print(_indent);
            _source.print("                        ");
            _source.println(
                "String.Concat(\"stackItem\", index))");
            _source.print(_indent);
            _source.print("                    ");
            _source.println(
                "_stateStack.Push(_States(stateId))");
            _source.print(_indent);
            _source.println(
                "                    index -= 1");
            _source.print(_indent);
            _source.println(
                "            End While");
            _source.print(_indent);
            _source.println("        End If");
            _source.println();
            _source.print(_indent);
            _source.println(
                "        stateId = info.GetInt32(\"state\")");
            _source.print(_indent);
            _source.println("        _state = _States(stateId)");
            _source.println();
            _source.print(_indent);
            _source.println("    End Sub");
            _source.println();
        }

        // The context class declaration is complete.
        _source.print(_indent);
        _source.println("End Class");
        _source.println();

        // Declare the root application state class.
        _source.print(_indent);
        _source.print("Public MustInherit Class ");
        _source.print(context);
        _source.println("State");
        _source.print(_indent);
        _source.println("    Inherits statemap.State");
        _source.println();

        // If reflection is on, then generate the abstract
        // Transitions property.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Properties");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    Public MustOverride ReadOnly ");
            _source.println(
                "Property Transitions() As IDictionary");
            _source.println();
        }

        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Member methods");
        _source.print(_indent);
        _source.println("    '");
        _source.println();
        _source.print(_indent);
        _source.print("    Protected Sub New(");
        _source.println(
            "ByVal name As String, ByVal id As Integer)");
        _source.println();
        _source.print(_indent);
        _source.println("        MyBase.New(name, id)");
        _source.print(_indent);
        _source.println("    End Sub");
        _source.println();
        _source.print(_indent);
        _source.print("    Public Overridable Sub Entry(");
        _source.print("ByRef context As ");
        _source.print(context);
        _source.println("Context)");
        _source.print(_indent);
        _source.println("    End Sub");
        _source.println();
        _source.print(_indent);
        _source.print("    Public Overridable Sub Exit_(");
        _source.print("ByRef context As ");
        _source.print(context);
        _source.println("Context)");
        _source.print(_indent);
        _source.println("    End Sub");
        _source.println();

        // Generate the default transition definitions.
        for (it = transitions.iterator(); it.hasNext() == true;)
        {
            trans = (SmcTransition) it.next();

            // Don't generate the Default transition here.
            if (trans.getName().equals("Default") == false)
            {
                _source.print(_indent);
                _source.print("    Public Overridable Sub ");
                _source.print(trans.getName());
                _source.print("(ByRef context As ");
                _source.print(context);
                _source.print("Context");

                for (it2 = trans.getParameters().iterator();
                     it2.hasNext() == true;
                    )
                {
                    _source.print(", ");
                    ((SmcParameter) it2.next()).accept(this);
                }

                _source.println(")");
                _source.println();

                // If this method is reached, that means that
                // this transition was passed to a state which
                // does not define the transition. Call the
                // state's default transition method.
                // Note: "Default" is a VB keyword, so use
                // "Default_" instead.
                _source.print(_indent);
                _source.println("        Default_(context)");

                _source.print(_indent);
                _source.println("    End Sub");
                _source.println();
            }
        }

        // Generate the overall Default transition for all maps.
        // Note: "Default" is a VB keyword, so use "Default_"
        // instead.
        _source.print(_indent);
        _source.print("    Public Overridable Sub Default_(");
        _source.print("ByRef context As ");
        _source.print(context);
        _source.println("Context)");
        _source.println();

        if (Smc.isDebug() == true)
        {
            _source.println("#If TRACE Then");
            _source.print(_indent);
            _source.print("        Trace.WriteLine(");
            _source.println("\"TRANSITION   : Default\")");
            _source.println("#End If");
            _source.println();
        }

        _source.print(_indent);
        _source.print("        Throw ");
        _source.println(
            "New statemap.TransitionUndefinedException( _");
        _source.print(_indent);
        _source.println(
            "            String.Concat(\"State: \", _");
        _source.print(_indent);
        _source.println("               context.State.Name, _");
        _source.print(_indent);
        _source.println("               \", Transition: \", _");
        _source.print(_indent);
        _source.println(
            "               context.GetTransition()))");
        _source.print(_indent);
        _source.println("    End Sub");

        // End of the application class' state class.
        _source.print(_indent);
        _source.println("End Class");

        // Have each map print out its source code now.
        for (it = maps.iterator(); it.hasNext();)
        {
            ((SmcMap) it.next()).accept(this);
        }

        // If a package has been specified, then generate
        // the End tag now.
        if (packageName != null && packageName.length() > 0)
        {
            _source.println();
            _source.println("End Namespace");
        }

        return;
    }

    public void visit(SmcMap map)
    {
        List definedDefaultTransitions;
        SmcState defaultState = map.getDefaultState();
        String context = map.getFSM().getContext();
        String mapName = map.getName();
        List states = map.getStates();
        Iterator it;
        SmcState state;
        String stateName;

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

        // Declare the map class. Declare it as abstract to
        // prevent its instantiation.
        _source.println();
        _source.print(_indent);
        _source.print("Public MustInherit Class ");
        _source.println(mapName);
        _source.println();
        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Shared data");
        _source.print(_indent);
        _source.println("    '");
        _source.println();

        // Declare each of the state class member data.
        for (it = states.iterator(); it.hasNext() == true;)
        {
            state = (SmcState) it.next();
            stateName = state.getClassName();

            _source.print(_indent);
            _source.print("    Public Shared ");
            _source.print(state.getInstanceName());
            _source.print(" As ");
            _source.print(mapName);
            _source.print('_');
            _source.print(stateName);
            _source.println(" = _");
            _source.print(_indent);
            _source.print("        New ");
            _source.print(mapName);
            _source.print('_');
            _source.print(stateName);
            _source.print("(\"");
            _source.print(mapName);
            _source.print('.');
            _source.print(stateName);
            _source.print("\", ");
            _source.print(map.getNextStateId());
            _source.println(")");
        }

        // Create a default state as well.
        _source.print(_indent);
        _source.print("    Private Shared Default_ As ");
        _source.print(mapName);
        _source.println("_Default = _");
        _source.print(_indent);
        _source.print("        New ");
        _source.print(mapName);
        _source.print("_Default(\"");
        _source.print(mapName);
        _source.println(".Default\", -1)");
        _source.println();

        // End of the map class.
        _source.print(_indent);
        _source.println("End Class");
        _source.println();

        // Declare the map default state class.
        _source.print(_indent);
        _source.print("Public Class ");
        _source.print(mapName);
        _source.println("_Default");
        _source.print(_indent);
        _source.print("    Inherits ");
        _source.print(context);
        _source.println("State");
        _source.println();

        // If reflection is on, generate the Transition property.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Properties");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    Public Overrides ReadOnly ");
            _source.println(
                "Property Transitions() As IDictionary");
            _source.print(_indent);
            _source.println("        Get");
            _source.println();
            _source.print(_indent);
            _source.println("            Return _transitions");
            _source.print(_indent);
            _source.println("        End Get");
            _source.print(_indent);
            _source.println("    End Property");
            _source.println();
        }

        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Member methods");
        _source.print(_indent);
        _source.println("    '");
        _source.println();

        // Generate the constructor.
        _source.print(_indent);
        _source.print("    Public Sub New(");
        _source.println(
            "ByVal name As String, ByVal id As Integer)");
        _source.println();
        _source.print(_indent);
        _source.println("        MyBase.New(name, id)");
        _source.print(_indent);
        _source.println("    End Sub");
        _source.println();

        // Declare the user-defined default transitions first.
        for (it = definedDefaultTransitions.iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }

        // If reflection is on, then define the transitions list.
        if (Smc.isReflection() == true)
        {
            List allTransitions = map.getFSM().getTransitions();
            SmcTransition transition;
            String transName;
            int transDefinition;

            _source.println();
            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Shared data");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    ");
            _source.println(
                "Private Shared _transitions As IDictionary");
            _source.println();
            _source.print(_indent);
            _source.println("    Shared Sub New()");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println("_transitions = New Hashtable()");

            // Now place the transition names into the list.
            for (it = allTransitions.iterator();
                 it.hasNext() == true;
                )
            {
                transition = (SmcTransition) it.next();
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

                _source.print(_indent);
                _source.print("        ");
                _source.print("_transitions.Add(\"");
                _source.print(transName);
                _source.print("\", ");
                _source.print(transDefinition);
                _source.println(")");
            }
            _source.println();
            _source.print(_indent);
            _source.println("    End Sub");
        }

        _source.print(_indent);
        _source.println("End Class");

        // Have each state now generate its code. Each state
        // class is an inner class.
        for (it = states.iterator(); it.hasNext() == true;)
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
        List actions;
        Iterator it;

        // Declare the state class.
        _source.println();
        _source.print(_indent);
        _source.print("Public NotInheritable Class ");
        _source.print(mapName);
        _source.print('_');
        _source.println(state.getClassName());
        _source.print(_indent);
        _source.print("    Inherits ");
        _source.print(mapName);
        _source.println("_Default");
        _source.println();

        // Generate the Transitions property if reflection is on.
        if (Smc.isReflection() == true)
        {
            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Properties");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    Public Overrides ReadOnly ");
            _source.println(
                "Property Transitions() As IDictionary");
            _source.print(_indent);
            _source.println("        Get");
            _source.println();
            _source.print(_indent);
            _source.println("            Return _transitions");
            _source.print(_indent);
            _source.println("        End Get");
            _source.print(_indent);
            _source.println("    End Property");
            _source.println();
        }

        _source.print(_indent);
        _source.println(
            "    '------------------------------------------------------------");
        _source.print(_indent);
        _source.println("    ' Member methods");
        _source.print(_indent);
        _source.println("    '");
        _source.println();

        // Add the constructor.
        _source.print(_indent);
        _source.print("    Public Sub New(");
        _source.println(
            "ByVal name As String, ByVal id As Integer)");
        _source.println();
        _source.print(_indent);
        _source.println("        MyBase.New(name, id)");
        _source.print(_indent);
        _source.println("    End Sub");

        // Add the Entry() and Exit() member functions if this
        // state defines them.
        actions = state.getEntryActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print(_indent);
            _source.print("    Public Overrides Sub Entry(");
            _source.print("ByRef context As ");
            _source.print(context);
            _source.println("Context)");
            _source.println();

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("       Dim ctxt As ");
            _source.print(context);
            _source.println(" = context.Owner");
            _source.println();

            // Generate the actions associated with this code.
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            _source.print(_indent);
            _source.println("    End Sub");
        }

        actions = state.getExitActions();
        if (actions != null && actions.size() > 0)
        {
            _source.println();
            _source.print(_indent);
            _source.print("    Public Overrides Sub Exit(");
            _source.print("ByRef context As ");
            _source.print(context);
            _source.println("Context)");
            _source.println();

            // Declare the "ctxt" local variable.
            _source.print(_indent);
            _source.print("        Dim ctxt As ");
            _source.print(context);
            _source.println(" = context.Owner");
            _source.println();

            // Generate the actions associated with this code.
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }

            _source.print(_indent);
            _source.println("    End Sub");
        }

        // Have each transition generate its code.
        for (it = state.getTransitions().iterator();
             it.hasNext() == true;
            )
        {
            ((SmcTransition) it.next()).accept(this);
        }

        // If reflection is on, then generate the transitions
        // list.
        if (Smc.isReflection() == true)
        {
            List allTransitions = map.getFSM().getTransitions();
            List stateTransitions = state.getTransitions();
            SmcState defaultState = map.getDefaultState();
            List defaultTransitions;
            SmcTransition transition;
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
                defaultTransitions = (List) new ArrayList();
            }

            _source.println();
            _source.print(_indent);
            _source.println(
                "    '------------------------------------------------------------");
            _source.print(_indent);
            _source.println("    ' Shared data");
            _source.print(_indent);
            _source.println("    '");
            _source.println();
            _source.print(_indent);
            _source.print("    ");
            _source.println(
                "Private Shared _transitions As IDictionary");
            _source.println();
            _source.print(_indent);
            _source.println("    Shared Sub New()");
            _source.println();
            _source.print(_indent);
            _source.print("        ");
            _source.println("_transitions = New Hashtable()");

            // Now place the transition names into the list.
            for (it = allTransitions.iterator();
                 it.hasNext() == true;
                )
            {
                transition = (SmcTransition) it.next();
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

                _source.print(_indent);
                _source.print("        ");
                _source.print("_transitions.Add(\"");
                _source.print(transName);
                _source.print("\", ");
                _source.print(transDefinition);
                _source.println(")");
            }

            _source.println();
            _source.print(_indent);
            _source.println("    End Sub");
        }

        _source.print(_indent);
        _source.println("End Class");

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
        SmcParameter param;

        _source.println();
        _source.print(_indent);
        _source.print("    Public Overrides Sub ");

        // If this is the Default transition, then change its
        // name to "Default_" because Default is a VB keyword.
        if (transName.equals("Default") == true)
        {
            _source.print("Default_");
        }
        else
        {
            _source.print(transName);
        }

        _source.print("(ByRef context As ");
        _source.print(context);
        _source.print("Context");

        // Add user-defined parameters.
        for (it = parameters.iterator(); it.hasNext() == true;)
        {
            _source.print(", ");
            ((SmcParameter) it.next()).accept(this);
        }
        _source.println(")");
        _source.println();

        // Generate the ctxt local variable if needed.
        if (transition.hasCtxtReference() == true)
        {
            _source.print(_indent);
            _source.print("        Dim ctxt As ");
            _source.print(context);
            _source.println(" = context.Owner");
        }

        // Generate the loopbackFlag for the Default state only
        // and only if at least one of the guards references a
        // non-nil end state.
        if (stateName.equals("Default") == true &&
            transition.hasNonNilEndState() == true)
        {
            _source.print(_indent);
            _source.println(
                "        Dim loopbackFlag As Boolean = False");
            _source.println();
        }

        // Output transition to debug stream.
        if (Smc.isDebug() == true)
        {
            String sep;

            _source.println("#If TRACE Then");
            _source.print(_indent);
            _source.println("        Trace.WriteLine( _");
            _source.print(_indent);
            _source.print("            \"TRANSITION   : ");
            _source.print(mapName);
            _source.print(".");
            _source.print(stateName);
            _source.print(".");
            _source.print(transName);

            _source.print("(");
            for (it = parameters.iterator(), sep = "";
                 it.hasNext() == true;
                 sep = ", ")
            {
                _source.print(sep);
                ((SmcParameter) it.next()).accept(this);
            }
            _source.print(")");

            _source.println("\")");
            _source.println("#End If");
            _source.println();
        }

        // Loop through the guards and print each one.
        for (it = guards.iterator(),
                 _guardIndex = 0,
                 _guardCount = guards.size();
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
            _source.println();
            _source.print(_indent);
            _source.println("        Else");
            _source.print(_indent);
            _source.print("            MyBase.");
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

            _source.println(")");
            _source.print(_indent);
            _source.println("        End If");
        }
        // Need to add a final newline after a multiguard block.
        else if (_guardCount > 1)
        {
            _source.print(_indent);
            _source.println("        End If");
            _source.println();
        }

        _source.print(_indent);
        _source.println("    End Sub");

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
        String indent1;
        String indent2;
        String indent3;
        String indent4;
        String endStateName = guard.getEndState();
        String fqEndStateName = "";
        String pushStateName = guard.getPushState();
        String condition = guard.getCondition();
        List actions = guard.getActions();

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
            indent1 = _indent + "            ";

            // There are multiple guards.
            // Is this the first guard?
            if (_guardIndex == 0 && condition.length() > 0)
            {
                // Yes, this is the first. This means an "if"
                // should be used.
                _source.print(_indent);
                _source.print("        If ");
                _source.print(condition);
                _source.println(" _");
                _source.print(_indent);
                _source.println("        Then");
            }
            else if (condition.length() > 0)
            {
                // No, this is not the first transition but it
                // does have a condition. Use an "else if".
                _source.println();
                _source.print(_indent);
                _source.print("        ElseIf ");
                _source.print(condition);
                _source.println(" _");
                _source.print(_indent);
                _source.println("        Then");
            }
            else
            {
                // This is not the first transition and it has
                // no condition.
                _source.println();
                _source.print(_indent);
                _source.println("        Else");
            }
        }
        else
        {
            // There is only one guard. Does this guard have
            // a condition?
            if (condition.length() == 0)
            {
                // No. This is a plain, old. vanilla transition.
                indent1 = _indent + "        ";
            }
            else
            {
                // Yes there is a condition.
                indent1 = _indent + "            ";
                _source.print(_indent);
                _source.print("        If ");
                _source.print(condition);
                _source.println(" _");
                _source.print(_indent);
                _source.println("        Then");
            }
        }

        // Now that the necessary conditions are in place, it's
        // time to dump out the transition's actions. First, do
        // the proper handling of the state change. If this
        // transition has no actions, then set the end state
        // immediately. Otherwise, unset the current state so
        // that if an action tries to issue a transition, it will
        // fail.
        if (actions.size() == 0)
        {
            fqEndStateName = endStateName;
        }
        // Save away the current state if this is a loopback
        // transition. Storing current state allows the
        // current state to be cleared before any actions are
        // executed. Remember: actions are not allowed to
        // issue transitions and clearing the current state
        // prevents them from doing do.
        else if (loopbackFlag == true)
        {
            fqEndStateName = "endState";
            _source.print(_indent);
            _source.print("Dim ");
            _source.print(fqEndStateName);
            _source.print(" As ");
            _source.print(context);
            _source.println("State = context.State");
            _source.println();
        }
        else
        {
            fqEndStateName = endStateName;
        }

        // Decide if runtime loopback checking must be done.
        if (defaultFlag == true &&
            transType != Smc.TRANS_POP &&
            loopbackFlag == false)
        {
            _source.print(indent1);
            _source.print("If context.State.Name = ");
            _source.print(fqEndStateName);
            _source.println(".Name _");
            _source.print(indent1);
            _source.println("Then");
            _source.print(indent1);
            _source.println("    loopbackFlag = True");
            _source.print(indent1);
            _source.println("End If");
            _source.println();
        }

        // Dump out the exit actions - but only for the first
        // guard.
        // v. 1.0, beta 3: Not any more. The exit actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a pop transition.
        if (transType == Smc.TRANS_POP || loopbackFlag == false)
        {
            indent3 = indent1;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent3 = indent1 + "    ";

                _source.print(indent1);
                _source.println("If loopbackFlag = False _");
                _source.print(indent1);
                _source.println("Then");
            }

            _source.print(indent3);
            _source.println("context.State.Exit_(context)");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent1);
                _source.println("End If");
                _source.println();
            }
        }

        // Dump out this transition's actions.
        if (actions.size() == 0)
        {
            if (condition.length() > 0)
            {
                _source.print(indent1);
                _source.println("' No actions.");
            }

            indent2 = indent1;
        }
        else
        {
            Iterator it;
            String tempIndent;

            // Now that we are in the transition, clear the
            // current state.
            _source.print(indent1);
            _source.println("context.ClearState()");

            // v. 2.0.0: Place the actions inside a try/finally
            // block. This way the state will be set before an
            // exception leaves the transition method.
            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.print(indent1);
                _source.println("Try");

                indent2 = indent1 + "    ";
            }
            else
            {
                indent2 = indent1;
            }

            tempIndent = _indent;
            _indent = indent2;
            for (it = actions.iterator(); it.hasNext() == true;)
            {
                ((SmcAction) it.next()).accept(this);
            }
            _indent = tempIndent;

            // v. 2.2.0: Check if the user has turned off this
            // feature first.
            if (Smc.isNoCatch() == false)
            {
                _source.print(indent1);
                _source.println("Finally");
            }
        }

        // Print the setState() call, if necessary. Do NOT
        // generate the set state it:
        // 1. The transition has no actions AND is a loopback OR
        // 2. This is a push or pop transition.
        if (transType == Smc.TRANS_SET &&
            (actions.size() > 0 || loopbackFlag == false))
        {
            _source.print(indent2);
            _source.print("context.State = ");
            _source.println(fqEndStateName);
        }
        else if (transType == Smc.TRANS_PUSH)
        {
            // Set the next state so this it can be pushed
            // onto the state stack. But only do so if a clear
            // state was done.
            if (loopbackFlag == false || actions.size() > 0)
            {
                _source.print(indent2);
                _source.print("context.State = ");
                _source.println(fqEndStateName);
            }

            // Before doing the push, execute the end state's
            // entry actions (if any) if this is not a loopback.
            if (loopbackFlag == false)
            {
                indent3 = indent2;

                if (defaultFlag == true)
                {
                    indent3 = indent2 + "    ";

                    _source.println();
                    _source.print(indent2);
                    _source.println("If loopbackFlag = False _");
                    _source.print(indent2);
                    _source.println("Then");
                }

                _source.print(indent3);
                _source.println("context.State.Entry(context)");

                if (defaultFlag == true)
                {
                    _source.print(indent3);
                    _source.println("End If");
                }
            }

            _source.print(indent2);
            _source.print("context.PushState(");
            _source.print(pushStateName);
            _source.println(")");
        }
        else if (transType == Smc.TRANS_POP)
        {
            _source.print(indent1);
            _source.println("context.PopState()");
        }

        // Perform the new state's enty actions.
        // v. 1.0, beta 3: Not any more. The entry actions are
        // executed only if 1) this is a standard, non-loopback
        // transition or a push transition.
        if ((transType == Smc.TRANS_SET &&
             loopbackFlag == false) ||
             transType == Smc.TRANS_PUSH)
        {
            indent3 = indent2;

            // If this is a non-loopback, generic transition,
            // do runtime loopback checking.
            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                indent3 = indent2 + "    ";

                _source.println();
                _source.print(indent2);
                _source.println("If loopbackFlag = False _");
                _source.print(indent2);
                _source.println("Then");
            }

            _source.print(indent3);
            _source.println("context.State.Entry(context)");

            if (transType == Smc.TRANS_SET &&
                defaultFlag == true)
            {
                _source.print(indent2);
                _source.println("End If");
                _source.println();
            }
        }

        // If there was a try/finally, then put the closing
        // brace on the finally block.
        // v. 2.2.0: Check if the user has turned off this
        // feature first.
        if (actions.size() > 0 && Smc.isNoCatch() == false)
        {
            _source.print(indent1);
            _source.println("End Try");
        }

        // If there is a transition associated with the pop, then
        // issue that transition here.
        if (transType == Smc.TRANS_POP &&
            endStateName.equals(NIL_STATE) == false &&
            endStateName.length() > 0)
        {
            String popArgs = guard.getPopArgs();

            _source.println();
            _source.print(indent1);
            _source.print("context.");
            _source.print(endStateName);
            _source.print("(");

            // Output any and all pop arguments.
            if (popArgs.length() > 0)
            {
                _source.print(popArgs);
            }
            _source.println(")");
        }

        return;
    }

    public void visit(SmcAction action)
    {
        String name = action.getName();
        List arguments = action.getArguments();
        Iterator it;
        String sep;

        // Need to distinguish between FSMContext actions and
        // application class actions. If the action is
        // "emptyStateStack", then pass it to the context.
        // Otherwise, let the application class handle it.
        if (name.compareTo("EmptyStateStack") == 0)
        {
            _source.print(_indent);
            _source.println("context.EmptyStateStack()");
        }
        // If this is a property assignment, then strip the
        // semicolon from the argument's end.
        else if (action.isProperty() == true)
        {
            String arg = (String) arguments.get(0);

            _source.print(_indent);
            _source.print("ctxt.");
            _source.print(name);
            _source.print(" = ");
            _source.println(arg.substring(0, arg.indexOf(';')));
        }
        else
        {
            _source.print(_indent);
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

            _source.println(")");
        }

        return;
    }

    public void visit(SmcParameter parameter)
    {
        _source.print("ByVal ");
        _source.print(parameter.getName());
        _source.print(" As ");
        _source.print(parameter.getType());

        return;
    }

//---------------------------------------------------------------
// Member data
//
}

//
// CHANGE LOG
// $Log$
// Revision 1.7  2006/09/23 14:28:19  cwrapp
// Final SMC, v. 4.3.3 check-in.
//
// Revision 1.6  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
//
// Revision 1.5  2006/07/11 18:18:37  cwrapp
// Corrected owner property.
//
// Revision 1.4  2006/06/03 19:39:25  cwrapp
// Final v. 4.3.1 check in.
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
// Revision 1.2  2005/08/26 15:21:34  cwrapp
// Final commit for release 4.2.0. See README.txt for more information.
//
// Revision 1.1  2005/05/28 19:28:43  cwrapp
// Moved to visitor pattern.
//
// Revision 1.2  2005/02/21 15:38:51  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.1  2005/02/21 15:23:02  charlesr
// Modified isLoopback() method call to new signature due to moving
// the method from SmcGuard to SmcCodeGenerator.
//
// Revision 1.0  2005/02/03 17:12:57  charlesr
// Initial revision
//
