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
// The Original Code is State Map Compiler (SMC).
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
import java.util.ListIterator;

public final class SmcParseTreeTcl
    extends SmcParseTree
{
    public SmcParseTreeTcl()
    {
        super();
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String srcfileBase)
        throws ParseException
    {
        ListIterator mapIt;
        ListIterator transIt;
        ListIterator paramIt;
        SmcMap map;
        SmcTransition trans;
        SmcParameter parameter;
        String separator;

        // Now dump out the raw source code, if any.
        if (_source != null && _source.length() > 0)
        {
            source.println(_source + "\n");
        }

        // Generate the context.
        source.println("class " +
                       _context +
                       "Context {");
        source.println("    inherit ::statemap::FSMContext;\n");
        source.println("# Member functions.\n");
        source.println("    constructor {owner} {");
        source.println("        ::statemap::FSMContext::constructor;");
        source.println("    } {");
        source.println("        set _owner $owner;");
        source.println("        setState ${" +
                       _start_state +
                       "};");
        source.println("        ${" +
                       _start_state +
                       "} Entry $this;");

        // If transition queuing is being done, then initialize
        // the queue here.
//          if (Smc.isTransQueue() == true)
//          {
//              source.println("        set _trans_queue {};");
//          }

        source.println("    }");

        // For every possible transition in every state map,
        // create a method.
        // First, get the transitions list.
        LinkedList transList = new LinkedList();
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

            // Don't do the Default transition.
            if (trans.getName().compareTo("Default") != 0)
            {
                source.print("\n    public method " +
                             trans.getName() +
                             " {");
                for (paramIt = trans.getParameters().listIterator(),
                   separator = "";
                     paramIt.hasNext() == true;
                     separator = " ")
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(separator);
                    parameter.generateCode(source);
                }
                source.println("} {");

            // If transition queuing, then queue the transition
            // and its arguments and execute them in from the
            // dispatch transitions method.
//              if (Smc.isTransQueue() == true)
//              {
//                  source.print("        lappend _trans_queue [list " +
//                               trans.getName() +
//                               " [list");
//                  for (paramIt = trans.getParameters().listIterator();
//                       paramIt.hasNext() == true;
//                      )
//                  {
//                      parameter = (SmcParameter) paramIt.next();
//                      source.print(" $" + parameter.getName());
//                  }
//                  source.println("];\n");
//                  source.println("        if {[string compare $_state \"\"] == 0} {");
//                  source.println("            dispatchTransitions;");
//                  source.println("        }");
//              }
//              else
//              {
                source.println("        set _transition \"" +
                               trans.getName() +
                               "\";");
                source.print("        [getState] " +
                             trans.getName() +
                             " $this");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(" $" + parameter.getName());
                }
                source.println(";");
                source.println("        set _transition \"\";");
                source.println("        return -code ok;");
                source.println("    }");
//              }
            }
        }

//          if (Smc.isTransQueue() == true)
//          {
//              source.println("\n    private method dispatchTransitions {} {");
//              source.println("        while {[llength $_trans_queue] > 0} {");
//              source.println("            set transition [lindex $_trans_queue 0];");
//              source.println("            set _trans_queue [lreplace $_trans_queue 0 0];");
//              source.println("            eval $_state [lindex $transition 0] [lindex $transition 1];");
//              source.println("        }");
//              source.println("    }");
//          }

        source.println("\n    public method getOwner {} {");
        source.println("        return -code ok $_owner;");
        source.println("    }");

        source.println("\n# Member data.\n");
        source.println("    private variable _owner;");

        // If transition queuing, declare the necessary data.
//          if (Smc.isTransQueue() == true)
//          {
//              source.println("    private variable _trans_queue;");
//          }

        // Put the closing brace on the context class.
        source.println("}\n");

        //
        // Now output the application's state class.
        source.println("class " +
                       _context +
                       "State {");
        source.println("    inherit ::statemap::State;\n");
        source.println("# Member functions.\n");
        source.println("    constructor {name} {");
        source.println("        ::statemap::State::constructor $name;");
        source.println("    } {}\n");

        // Define the default Entry() and Exit() methods.
        source.println("    public method Entry {context} {};");
        source.println("    public method Exit {context} {};");

        // Declare the undefined default transitions.
        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();

            // The Default transition is handled separately.
            if (trans.getName().compareTo("Default") != 0)
            {
                source.print("\n    public method " +
                             trans.getName() +
                             " {context");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(" ");
                    parameter.generateCode(source);
                }
                source.println("} {");
                source.println("        Default $context;");
                source.println("        return -code ok;");
                source.println("    }");
            }
        }

        // Define the default Default transition.
        source.println("\n    public method Default {context} {");
        source.println("        set transtion [$context getTransition];");
        source.println("        return -code error \"Transition \\\"$transition\\\" fell through to a non-existent default definition.\";");
        source.println("    }");

        // End of the application state class.
        source.println("}\n");

        // Have each map print out its source code in turn.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            map.generateCode(header, source, _context);
        }

        return;
    }
}
