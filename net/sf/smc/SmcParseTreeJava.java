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
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/02/19 19:52:49  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
// Revision 1.2  2001/12/14 20:10:37  cwrapp
// Changes in release 1.1.0:
// Add the following features:
// + 486786: Added the %package keyword which specifies the
//           Java package/C++ namespace/Tcl namespace
//           the SMC-generated classes will be placed.
// + 486471: The %class keyword accepts fully qualified
//           class names.
// + 491135: Add FSMContext methods getDebugStream and
//           setDebugStream.
// + 492165: Added -sync command line option which causes
//           the transition methods to be synchronized
//           (this option may only be used with -java).
//
// Revision 1.1  2001/12/03 14:14:03  cwrapp
// Changes in release 1.0.2:
// + Placed the class files in Smc.jar in the net.sf.smc package.
// + Moved Java source files from smc/bin to net/sf/smc.
// + Corrected a C++ generation bug wherein arguments were written
//   to the .h file rather than the .cpp file.
//
// Revision 1.3  2001/10/12 14:28:04  cwrapp
// SMC v. 1.0.1
//
// Revision 1.2  2001/05/09 23:40:01  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
//
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.2  2000/09/01 15:32:16  charlesr
// Changes for v. 1.0, Beta 2:
//
// + Removed order dependency on "%start", "%class" and "%header"
//   appearance. These three tokens may now appear in any order but
//   still must appear before the first map definition.
//
// + Modified SMC parser so that it will continue after finding an
//   error. Also improved the error message quality.
//
// + Made error messages so emacs is able to parse them.
//
// Revision 1.1.1.1  2000/08/02 12:50:56  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public final class SmcParseTreeJava
    extends SmcParseTree
{
    public SmcParseTreeJava()
    {
        super();
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String srcfileBase)
        throws ParseException
    {
        ListIterator iIt;
        ListIterator mapIt;
        ListIterator transIt;
        ListIterator paramIt;
        String packageName;
        SmcMap map;
        SmcTransition trans;
        SmcParameter parameter;
        String javaState;
        String separator;
        int index;
        List params;
        List prev_params;

        // Dump out the raw source code, if any.
        if (_source != null && _source.length () > 0)
        {
            source.println(_source + "\n");
        }

        // If a package has been specified, generate the
        // package statement now.
        if (_package != null && _package.length() > 0)
        {
            source.println("package " + _package + ";");
            source.println();
        }

        // Do user-specified imports now.
        for (iIt = _importList.listIterator();
             iIt.hasNext() == true;
            )
        {
            packageName = (String) iIt.next();
            source.println("import " + packageName + ";");
        }

        // If the -g option was specified, then import the
        // PrintStream class.
        if (Smc.isDebug() == true)
        {
            source.println("import java.io.PrintStream;");
            source.println();
        }
        else if (_importList.size() != 0)
        {
            source.println();
        }

        // The context clas contains all the state classes as
        // inner classes, so generate the context first rather
        // than last.
        source.println("public final class " +
                       _context +
                       "Context");
        source.println("    extends statemap.FSMContext");
        source.println("{");
        source.println("// Member methods.\n");

        // Generate the context class' constructor.
        source.println("    public " +
                       _context +
                       "Context(" +
                       _context +
                       " owner)");
        source.println("    {");
        source.println("        super();\n");
        source.println("        _owner = owner;");

        // If transition queuing is being done, then allocate
        // the queue here.
        /*
         * Not supported as of v. 1.0, beta 3.
        if (Smc.isTransQueue() == true)
        {
            source.println("        _trans_queue = new java.util.LinkedList();");
        }
        */

        // The state name "map::state" must be changed to
        // "map.state".
        if ((index = _start_state.indexOf("::")) >= 0)
        {
            javaState =
                    _start_state.substring(0, index) +
                    "." +
                    _start_state.substring(index + 2);
            source.println("        setState(" +
                           javaState +
                           ");");

            // Execute the start state's entry actions.
            source.println("        " +
                           javaState +
                           ".Entry(this);");
        }

        source.println("    }\n");

        // Generate the default transition methods.
        // Get the transition list.
        List transList = (List) new LinkedList();
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Merge the new transitions into the current set.
            transList =
                    Smc.merge(map.getTransitions(),
                              transList,
                              new Comparator() {
                                   public int compare(Object o1,
                                                      Object o2) {
                                       return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                   }
                               });
        }

        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            if (trans.getName().compareTo("Default") != 0)
            {
                source.print("    public ");

                // If the -sync flag was specified, then output
                // the "synchronized" keyword.
                if (Smc.isSynchronized() == true)
                {
                    source.print("synchronized ");
                }

                source.print("void " +
                             trans.getName() +
                             "(");
                for (paramIt = trans.getParameters().listIterator(),
                         separator = "";
                     paramIt.hasNext() == true;
                     separator = ", ")
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(separator);
                    parameter.generateCode(source);
                }
                source.println(")");
                source.println("    {");

                // Save away the transition name in case it is
                // need in an UndefinedTransitionException.
                source.println("        _transition = \"" +
                               trans.getName() +
                               "\";");

                /*
                 * Transition queuing not supported.
                 *
                // If transition queuing, then check if _state
                // is null. If so, enqueue the transition and its
                // arguments for later.
                if (Smc.isTransQueue() == true)
                {
                    String param_type;

                    source.println("        statemap.FSMContext.TransEntry entry;");
                    source.println("        String method_name;");
                    source.println("        Class[] params;");
                    source.println("        Object[] args;");
                    source.println("        java.lang.reflect.Method method;\n");

                    // Add debug message re: this transition being
                    // queued.
                    if (Smc.isDebug() == true)
                    {
                        source.println("        if (_debug_flag == true)");
                        source.println("        {");
                        source.println("            System.err.println(\"QUEUED       : " +
                                       trans.getName() +
                                       "\");");
                        source.println("        }\n");
                    }

                    source.println("        args = new Object[" +
                                   Integer.toString(trans.getParameters().size() + 1) +
                                   "];");
                    source.println("        params = new Class[" +
                                   Integer.toString(trans.getParameters().size() + 1) +
                                   "];\n");
                    source.println("        args[0] = this;");
                    source.println("        params[0] = this.getClass();");


                    for (paramIt = trans.getParameters().listIterator(),
                             index = 1;
                         paramIt.hasNext() == true;
                         ++index)
                    {
                        parameter = (SmcParameter) paramIt.next();

                        source.print("        args[" +
                                     Integer.toString(index) +
                                     "] = ");

                        // Is the argument a Java built-in? If yes,
                        // allocate the corresponding class.
                        // Otherwise, clone the object.
                        param_type = parameter.getType();
                        if (param_type.compareTo("byte") == 0)
                        {
                            source.println("new Byte(" +
                                           parameter.getName() +
                                           ")");
                        }
                        else if (param_type.compareTo("short") == 0)
                        {
                            source.println("new Short(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("int") == 0)
                        {
                            source.println("new Integer(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("long") == 0)
                        {
                            source.println("new Long(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("float") == 0)
                        {
                            source.println("new Float(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("double") == 0)
                        {
                            source.println("new Double(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("char") == 0)
                        {
                            source.println("new Character(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else if (param_type.compareTo("boolean") == 0)
                        {
                            source.println("new Boolean(" +
                                           parameter.getName() +
                                           ");");
                        }
                        else
                        {
                            // This is an object. Save its reference.
                            source.println(parameter.getName() +
                                           ";");
                        }

                        source.println("        params[" +
                                       Integer.toString(index) +
                                       "] = " +
                                       param_type +
                                       ".class;");
                    }

                    // Store away the method name and args.
                    source.println("\n        entry = new statemap.FSMContext.TransEntry(\"" +
                                   trans.getName() +
                                   "\", args, params);");
                    source.println("        _trans_queue.addLast(entry);\n");
                    source.println("        if (_state != null)");
                    source.println("        {");

                    // Now issue any queued up transitions.
                    source.println("            dispatchTransitions();");
                    source.println("        }\n");
                }
                else
                {
                 * transition queuing.
                 */
                source.print("        getState()." +
                             trans.getName() +
                             "(this");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(", " + parameter.getName());
                }
                source.println(");");
                source.println("        _transition = \"\";");

                source.println("        return;");
                source.println("    }\n");
            }
            /*
             * End of transition queue check.
            }
             */
        }

        // getState() method.
        source.println("    protected " +
                       _context +
                       "State getState()");
        source.println("        throws statemap.StateUndefinedException");
        source.println("    {");
        source.println("        if (_state == null)");
        source.println("        {");
        source.println("            throw(new statemap.StateUndefinedException());");
        source.println("        }\n");
        source.println("        return((" +
                       _context +
                       "State) _state);");
        source.println("    }\n");

        // getOwner() method.
        source.println("    protected " +
                       _context +
                       " getOwner()");
        source.println("    {");
        source.println("        return(_owner);");
        source.println("    }\n");

        /*
         * Transition queuing not supported.
         *
        if (Smc.isTransQueue() == true)
        {
            // The following method invokes queued transitions.
            source.println("    private void dispatchTransitions()");
            source.println("    {");
            source.println("        statemap.FSMContext.TransEntry entry;");
            source.println("        String method_name;");
            source.println("        Class[] params;");
            source.println("        Object[] args;");
            source.println("        java.lang.reflect.Method method;\n");
            source.println("        while(_trans_queue.size() > 0)");
            source.println("        {");
            source.println("            entry = (statemap.FSMContext.TransEntry) _trans_queue.removeFirst();");
            source.println("            method_name = entry.getMethodName();");
            source.println("            args = entry.getArgs();");
            source.println("            params = entry.getParameters();");
            source.println("            try");
            source.println("            {");

            source.println("                try");
            source.println("                {");
            source.println("                    method =");
            source.println("                        _state.getClass().getDeclaredMethod(method_name,");
            source.println("                                                            params);");
            source.println("                }");
            source.println("                catch (NoSuchMethodException method_ex)");
            source.println("                {");
            source.println("                    method =");
            source.println("                        _state.getClass().getSuperclass().getDeclaredMethod(method_name,");
            source.println("                                                            params);");
            source.println("                }\n");
            source.println("                method.invoke(_state, args);");
            source.println("                entry.free();");
            source.println("            }");

            if (Smc.isDebug() == false)
            {
                source.println("            catch (Exception jex) {}");
            }
            else
            {
                source.println("            catch (Exception jex)");
                source.println("            {");
                source.println("                if (_debug_flag == true)");
                source.println("                {");
                source.println("                    System.err.println(\"INVOKE ERROR : \" + jex);");
                source.println("                }");
                source.println("            }");
            }

            source.println("        }");
            source.println("    }\n");
        }
        * End of transition queuing code.
        */

        // Declare member data.
        source.println("// Member data.\n");
        source.println("    private " +
                       _context +
                       " _owner;");

        // If transition queuing, declare the necessary
        // data.
        if (Smc.isTransQueue() == true)
        {
            source.println("    private java.util.List _trans_queue;");
        }

        // Declare the inner state class.
        source.println("\n    protected static abstract class " +
                       _context +
                       "State");
        source.println("        extends statemap.State");
        source.println("    {");

        // Constructor.
        source.println("        protected " +
                       _context +
                       "State(String name)");
        source.println("        {");
        source.println("            super(name);");
        source.println("        }\n");
        source.println("        protected void Entry(" +
                       _context +
                       "Context context) {}");
        source.println("        protected void Exit(" +
                       _context +
                       "Context context) {}\n");

        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            // Don't generate the Default transition here.
            if (trans.getName().compareTo("Default") != 0)
            {
                source.print("        protected void " +
                             trans.getName() +
                             "(" +
                             _context +
                             "Context context");

                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(", ");
                    parameter.generateCode(source);
                }

                source.println(")");
                source.println("        {");

                // If this method is reached, that means that this
                // transition was passed to a state which does not
                // define the transition. Call the state's default
                // transition method.
                source.println("            Default(context);");

                source.println("        }");
                source.println();
            }
        }

        // Generate the overall Default transition for all maps.
        source.println("        protected void Default(" +
                       _context +
                       "Context context)");
        source.println("        {");

        if (Smc.isDebug() == true)
        {
            source.println("            if (context.getDebugFlag() == true)");
            source.println("            {");
            source.println("                PrintStream str = context.getDebugStream();");
            source.println();
            source.println("                str.println(\"TRANSITION   : Default\");");
            source.println("            }");
            source.println();
        }

        source.println("            throw (new statemap.TransitionUndefinedException(\"State: \" +");
        source.println("                                                             context.getState().getName() +");
        source.println("                                                             \", Transition: \" +");
        source.println("                                                             context.getTransition()));");
        source.println("        }");

        // End of state class.
        source.println("    }");

        // Have each map print out its source code now.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext();
            )
        {
            map = (SmcMap) mapIt.next();
            map.generateCode(header,
                             source,
                             _context,
                             _package,
                             null);
        }

        // End of context class.
        source.println("}\n");

        return;
    }
}
