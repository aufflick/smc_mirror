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
// Copyright (C) 2000 - 2005. Charles W. Rapp.
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
// Id: SmcTransition.java,v 1.1 2009/03/01 18:20:42 cwrapp Exp
//
// CHANGE LOG
// (See the bottom of this file.)
//

package net.sf.smc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SMC breaks transitions into two components: this class and
 * {@link net.sf.smc.model.SmcGuard guard}. This is done because
 * a transition may appear multiple times in a state with
 * different conditional guards. The only requirement is that
 * the transition use the same name and parameters. Two
 * transitions with the name name but different parameters is
 * considered to be different transitions.
 *
 * @see net.sf.smc.model.SmcState
 * @see net.sf.smc.model.SmcGuard
 * @author <a href="mailto:rapp@acm.org">Charles Rapp</a>
 */

public final class SmcTransition
    extends SmcElement
    implements Comparable<SmcTransition>
{
//---------------------------------------------------------------
// Member methods
//

    //-----------------------------------------------------------
    // Constructors.
    //

    /**
     * Creates a transitions instance with the given name,
     * parameters, line number and owning state.
     * @param name transition name.
     * @param parameters transition parameters.
     * @param transId the assigned transition identifier.
     * @param lineNumber where this transition appears in the .sm
     * file.
     * @param state transition appears in this state.
     */
    public SmcTransition(final String name,
                         final List<SmcParameter> parameters,
                         final int transId,
                         final int lineNumber,
                         final SmcState state)
    {
        super (name, lineNumber);

        _state = state;
        _parameters = parameters;
        _identifier = transId;
        _guards = new ArrayList<SmcGuard>();
    } // end of SmcTransition(String, List<>, int, SmcState)

    //
    // end of Constructors.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // SmcElement Abstract Methods.
    //

    /**
     * Calls the visitor's visit method for this finite state
     * machine element.
     * @param visitor The visitor instance.
     * @see SmcVisitor
     */
    @Override
    public void accept(SmcVisitor visitor)
    {
        visitor.visit(this);
    } // end of accept(SmcVisitor)

    //
    // end of SmcElement Abstract Methods.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Comparable Interface Implemenation.
    //

    /**
     * Returns an interger value &lt;, equal to or &gt; than
     * zero if {@code this} transition is &lt;, = or &gt; than
     * {@code trans}. This compararison is based on the
     * transition name first and, if equal, then on the
     * parameters.
     * @param trans the compared transition instance.
     * @return an interger value &lt;, equal to or &gt; than
     * zero if {@code this} transition is &lt;, = or &gt; than
     * {@code trans}.
     */
    @Override
    public int compareTo(final SmcTransition trans)
    {
        int retval = _name.compareTo(trans._name);

        if (retval == 0)
        {
            retval = _compareParams(trans.getParameters());
        }

        return(retval);
    } // end of compareTo(SmcTransition)

    // end of Comparable Interface Implemenation.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Get methods.
    //

    /**
     * Returns the unique transition identifier.
     * @return transition identifier.
     */
    public int getIdentifier()
    {
        return (_identifier);
    } // end of getIdentifier()

    /**
     * Returns the transitions owning state.
     * @return the transitions owning state.
     */
    public SmcState getState()
    {
        return (_state);
    } // end of getState()

    /**
     * Returns the transition parameters.
     * @return the transition parameters.
     */
    public List<SmcParameter> getParameters()
    {
        return(_parameters);
    } // end of getParameters()

    /**
     * Returns the guard list.
     * @return the guard list.
     */
    public List<SmcGuard> getGuards()
    {
        return(_guards);
    } // end of getGuards()

    /**
     * Returns {@code true} if this transition references the
     * {@code ctxt} local variable and {@code false} otherwise.
     * @return {@code true} if this transition references the
     * {@code ctxt} local variable and {@code false} otherwise.
     */
    public boolean hasCtxtReference()
    {
        Iterator<SmcGuard> guardIt;
        boolean retcode;

        // Stop as soon as we know that ctxt is referenced.
        for (guardIt = _guards.iterator(), retcode = false;
             guardIt.hasNext() == true && retcode == false;
            )
        {
            retcode =
                (guardIt.next()).hasCtxtReference();
        }

        return (retcode);
    } // end of hasCtxtReference()

    /**
     * Returns {@code true} if this transition has a guard with a
     * non-{@code nil} end state and {@code false} otherwise.
     * @return {@code true} if this transition has a guard with a
     * non-{@code nil} end state and {@code false} otherwise.
     */
    public boolean hasNonNilEndState()
    {
        Iterator<SmcGuard> guardIt;
        SmcGuard guard;
        boolean retcode;

        // Stop as soon as we know that a non-nill end state
        // is referenced.
        for (guardIt = _guards.iterator(), retcode = false;
             guardIt.hasNext() == true && retcode == false;
            )
        {
            guard = guardIt.next();
            retcode =
                !guard.getEndState().equals(NIL_STATE);
        }

        return (retcode);
    } // end of hasNonNilEndState()

    //
    // end of Get methods.
    //-----------------------------------------------------------

    //-----------------------------------------------------------
    // Set methods.
    //

    /**
     * Adds a guard to the list.
     * @param guard add this guard.
     */
    public void addGuard(SmcGuard guard)
    {
        _guards.add(guard);
        return;
    } // end of addGuard(SmcGuard)

    //
    // end of Set methods.
    //-----------------------------------------------------------

    /**
     * Returns {@code true} if {@code obj} is a
     * non-{@code null SmcTransition} instance with the same name
     * and parameters; {@code false} otherwise.
     * @param obj the compared object.
     * @return {@code true} if {@code obj} is a
     * non-{@code null SmcTransition} instance with the same name
     * and parameters.
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean retcode = (this == obj);

        if (retcode == false && obj instanceof SmcTransition)
        {
            final SmcTransition trans = (SmcTransition) obj;

            retcode =
                (_name.equals(trans._name) == true &&
                 _compareParams(trans._parameters) == 0);
        }

        return(retcode);
    } // end of equals(Object)

    /**
     * Returns the unique transition identifier.
     * @return transition identifier as the transition hash code.
     */
    @Override
    public int hashCode()
    {
        return (_identifier);
    } // end of hashCode()

    /**
     * Returns an interger value &lt;, equal to or &gt; than
     * zero if {@code this} transition's name and parameters are
     * &lt;, = or &gt; than {@code name} and {@code parameters}.
     * @param name transition name.
     * @param parameters transition parameters.
     * @return an interger value &lt;, equal to or &gt; than
     * zero if {@code this} transition's name and parameters are
     * &lt;, = or &gt; than {@code name} and {@code parameters}.
     */
    public int compareTo(String name,
                         List<SmcParameter> parameters)
    {
        int retval;

        if ((retval = _name.compareTo(name)) == 0)
        {
            retval = _compareParams(parameters);
        }

        return (retval);
    } // end of compareTo(String, List<SmcParameeter>)

    /**
     * Returns the transition text representation.
     * @return the transition text representation.
     */
    @Override
    public String toString()
    {
        final StringBuffer retval = new StringBuffer(512);
        String sep;
        Iterator<SmcParameter> pit;

        retval.append(_name);
        retval.append("(");

        for (pit = _parameters.iterator(), sep = "";
             pit.hasNext() == true;
             sep = ", ")
        {
            retval.append(sep);
            retval.append(pit.next());
        }

        retval.append(")");

        if (_guards.isEmpty() == false)
        {
            for (SmcGuard guard: _guards)
            {
                retval.append("\n");
                retval.append(guard);
            }
        }

        return (retval.toString());
    } // end of toString()

    /**
     * Returns an integer value &lt;, equal to, or &gt; zero
     * based on whether {@code p1} is &lt;, equal to, or &gt;
     * {@code p2}. The comparison is based on {@link List#size}
     * first and, if the list sizes are equal, on comparing each
     * {@link SmcParameter} in turn.
     * @param p1 the first parameter list.
     * @param p2 the second parameter list.
     * @return an integer value &lt;, equal to, or &gt; zero.
     */
    public static int compareParams(final List<SmcParameter> p1,
                                    final List<SmcParameter> p2)
    {
        int retval = (p1.size() - p2.size());

        if (retval == 0)
        {
            Iterator<SmcParameter> pit1 = p1.iterator();
            Iterator<SmcParameter> pit2 = p2.iterator();
            SmcParameter param1;
            SmcParameter param2;

            while (pit1.hasNext() == true &&
                   pit2.hasNext() == true &&
                   retval == 0)
            {
                param1 = pit1.next();
                param2 = pit2.next();

                retval = param1.compareTo(param2);
            }
        }

        return (retval);
    } // end of compareParams(List<>, List<>)

    // Compare this transition's parameters with the given list.
    private int _compareParams(final List<SmcParameter> params)
    {
        return (compareParams(_parameters, params));
    } // end of _compareParams(List<>)

//---------------------------------------------------------------
// Member data
//

    /**
     * This transition appears in this state.
     */
    private SmcState _state;

    /**
     * Each unique transition (transition name plus parameters)
     * is assigned a unique integer identifier. This is used
     * in the -java7 transition table generation.
     */
    private final int _identifier;

    /**
     * The transition name plus its parameters uniquely defines
     * the transition.
     */
    private final List<SmcParameter> _parameters;

    /**
     * The transition guards. There must be at least one
     * guard.
     */
    private final List<SmcGuard> _guards;
} // end of class SmcTransition

//
// CHANGE LOG
// Log: SmcTransition.java,v
// Revision 1.1  2009/03/01 18:20:42  cwrapp
// Preliminary v. 6.0.0 commit.
//
// Revision 1.10  2007/12/28 12:34:41  cwrapp
// Version 5.0.1 check-in.
//
// Revision 1.9  2007/02/21 13:56:58  cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.8  2007/01/15 00:23:52  cwrapp
// Release 4.4.0 initial commit.
//
// Revision 1.7  2006/09/16 15:04:29  cwrapp
// Initial v. 4.3.3 check-in.
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
// Revision 1.5  2005/05/28 19:28:42  cwrapp
// Moved to visitor pattern.
//
// Revision 1.6  2005/02/21 15:38:43  charlesr
// Added Francois Perrad to Contributors section for Python work.
//
// Revision 1.5  2005/02/03 16:52:21  charlesr
// In implementing the Visitor pattern, the generateCode()
// methods have been moved to the appropriate Visitor
// subclasses (e.g. SmcJavaGenerator). This class now extends
// SmcElement.
//
// Revision 1.4  2004/10/30 16:08:54  charlesr
// Added Graphviz DOT file generation.
//
// Revision 1.3  2004/10/02 19:54:57  charlesr
// Full-qualified all std namespace references with "std::".
//
// Revision 1.2  2004/09/06 16:41:48  charlesr
// Added C# support.
//
// Revision 1.1  2004/05/31 13:57:17  charlesr
// Added support for VB.net code generation.
//
// Revision 1.0  2003/12/14 21:07:01  charlesr
// Initial revision
//
