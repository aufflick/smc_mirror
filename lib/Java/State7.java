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
// Copyright (C) 2014. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// statemap.java --
//
//  This package defines the fsmContext class which must be inherited by
//  any Java class wanting to use an smc-generated state machine.
//
// RCS ID
// $Id$
//

package statemap;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;

/**
 * A single state class is used for generated Java code which
 * implements the FSM as a method handle table. The class
 * contains the state name, unique state identifier, the entry
 * and exit action method handles, and the transition method
 * handle table.
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class State7
    implements Serializable
{
//---------------------------------------------------------------
// Member functions
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a state instance with the given name and unique
     * integer identifier.
     * @param name The state name.
     * @param id The state unique identifier.
     * @param entry state entry action method handle. Will be
     * {@code null} if the state has no entry action.
     * @param exit state exit action method handle. Will be
     * {@code null} if the state has no exit action.
     * @param transitions transition action method handles.
     * @param transNames the transition names.
     */
    public State7(final String name,
                  final int id,
                  final MethodHandle entry,
                  final MethodHandle exit,
                  final MethodHandle[] transitions,
                  final String[] transNames)
    {
        _name = name;
        _id = id;
        _entryAction = entry;
        _exitAction = exit;
        _transitions = transitions;
        _transitionNames = transNames;
    } // end of State7(String, int, ...)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Object Method Overrides.
    //

    /**
     * Returns the state name.
     * @return the state name.
     */
    @Override
    public String toString()
    {
        return (_name);
    } // end of toString()

    //
    // end of Object Method Overrides.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Get methods.
    //

    /**
     * Returns the state name.
     * @return the state name.
     */
    public String getName()
    {
        return (_name);
    } // end of getName()

    /**
     * Returns the unique integer identifier.
     * @return the unique integer identifier.
     */
    public int getId()
    {
        return (_id);
    } // end of getId()

    /**
     * Returns the state transition names.
     * @return state transition names.
     */
    public String[] getTransitions()
    {
        return (_transitionNames);
    } // end of getTransitions()

    //
    // end of Get methods.
    //-----------------------------------------------------------

    /**
     * Returns the transition method handle for the given
     * unique transition identifier.
     * @param transitionId the unique transition identifier.
     * @return the transition method handle.
     */
    public MethodHandle transition(final int transitionId)
    {
        return (_transitions[transitionId]);
    } // end of transition(int)

    /**
     * Returns the state entry action. May return {@code null} if
     * the state has no entry action.
     * @return state entry action method handle.
     */
    public MethodHandle enterState()
    {
        return (_entryAction);
    } // end of enterState(FSMContext7)

    /**
     * Returns the state exit action. May return {@code null} if
     * the state has not exit action.
     * @return state exit action method handle.
     */
    public MethodHandle exitState()
    {
        return (_exitAction);
    } // end of exitState(FSMContext7)

//---------------------------------------------------------------
// Member data
//

    /**
     * The state name. Used for human-readability.
     */
    private final String _name;

    /**
     * Unique state identifier. Used for serialization.
     */
    private final int _id;

    /**
     * The entry action method handle. Will be {@code null} if
     * this state has no entry action.
     */
    private final MethodHandle _entryAction;

    /**
     * The exit action method handle. Will be {@code null} if
     * this state has no exit action.
     */
    private final MethodHandle _exitAction;

    /**
     * Transition action method handles.
     */
    private final MethodHandle[] _transitions;

    /**
     * The transition names implemented in this class.
     */
    private final String[] _transitionNames;

    //-----------------------------------------------------------
    // Constants.
    //

    // Transition reflection values.

    /**
     * Zero (0) means the transition is undefined.
     */
    public static final Integer TRANSITION_UNDEFINED = 0;

    /**
     * One (1) means the transition is defined in the current
     * state.
     */
    public static final Integer TRANSITION_DEFINED_LOCALLY = 1;

    /**
     * Two (2) means the transition is defined in the default
     * state.
     */
    public static final Integer TRANSITION_DEFINED_DEFAULT = 2;

    /**
     * The SMC version for Java serialization purposes.
     */
    private static final long serialVersionUID = 0x070000L;
} // end of class State7
