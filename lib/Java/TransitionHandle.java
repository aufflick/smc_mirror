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
// The Original Code is  State Machine Compiler(SMC).
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
//  This package defines the FSMContext7 class which must be
//  inherited by any Java class wanting to use an smc-generated
//  state machine.
//
// RCS ID
// $Id$
//

package statemap;

import java.lang.invoke.MethodHandle;

/**
 * Encapsulates a {@link MethodHandle transition method handle},
 * tracking whether the transition is a {@code Default} or not.
 * If not a default transition, then calls the transition method
 * handle with the given arguments. Otherwise, calls the default
 * transition method with no arguments.
 *
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class TransitionHandle
{
//---------------------------------------------------------------
// Member methods.
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a transition method handle for the given arguments.
     * @param isDefault {@code true} if this is a default
     * transition method.
     * @param mh the actual transition method handle.
     */
    /* package */ TransitionHandle(final boolean isDefault,
                                   final MethodHandle mh)
    {
        _isDefault = isDefault;
        _method = mh;
    } // end of TransitionHandle(boolean, MethodHandle)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Get Methods.
    //

    /**
     * Returns {@code true} if this is a default transition
     * method and {@code false} otherwise.
     * @return {@code true} if this is a default transition.
     */
    public boolean isDefault()
    {
        return (_isDefault);
    } // end of isDefault()

    /**
     * Returns the transition method handle.
     * @return transition method handle.
     */
    public MethodHandle handle()
    {
        return (_method);
    } // end of handle()

    //
    // end of Get Methods.
    //-----------------------------------------------------------        

//---------------------------------------------------------------
// Member data.
//

    /**
     * Set to {@code true} if the encapsulated method is a
     * default transition, thus having no parameters.
     */
    private final boolean _isDefault;

    /**
     * The actual transition method handle.
     */
    private final MethodHandle _method;
} // end of class TransitionHandle
