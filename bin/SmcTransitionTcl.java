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
// Revision 1.1  2001/01/03 03:13:59  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:23  charlesr
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
// Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import java.io.PrintStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.ListIterator;

public final class SmcTransitionTcl
    extends SmcTransition
{
    public SmcTransitionTcl(String name,
                            LinkedList parameters,
                            int line_number)
    {
        super(name, parameters, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String context,
                             String mapName,
                             String stateName,
                             String indent)
        throws ParseException
    {
        int guardCount;
        int guardIndex;
        int nullConditions;
        ListIterator paramIt;
        SmcParameter parameter;
        ListIterator guardIt;
        SmcGuard guard;

        source.print("\n    public method " +
                     _name +
                     " {context");

        // Add user-defined parameters.
        for (paramIt = _parameters.listIterator();
             paramIt.hasNext() == true;
            )
        {
            parameter = (SmcParameter) paramIt.next();

            source.print(" ");
            parameter.generateCode(source);
        }
        source.println("} {");

        // First things first. Remember this transition's name
        // but not if it is the default transition.
        if (_name.compareTo("Default") != 0)
        {
            source.println("        set _transition \"" +
                           _name +
                           "\";");
        }

        // If verbose is turned on, then put the logging code in.
        if (Smc.isDebug() == true)
        {
            source.println("\n        if {[$context getDebugFlag] != 0} {");
            source.print("            puts stderr \"TRANSITION    : " +
                         mapName +
                         "::" +
                         stateName +
                         " " +
                         _name);
            if (_parameters.size() > 0)
            {
                String separator;

                source.print(" {");
                for (paramIt = _parameters.listIterator(),
                         separator = "";
                     paramIt.hasNext() == true;
                     separator = " ")
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(separator);
                    parameter.generateCode(source);
                }
                source.print("}");
            }
            source.println("\";");
            source.println("        }\n");
        }

        guardIndex = 0;
        guardCount = _guards.size();
        nullConditions = 0;
        for (guardIt = _guards.listIterator();
             guardIt.hasNext() == true;
             ++guardIndex)
        {
            guard = (SmcGuard) guardIt.next();
            if (guard.getCondition() == null)
            {
                ++nullConditions;
            }

            guard.generateCode(source,
                               guardIndex,
                               guardCount,
                               context,
                               mapName,
                               stateName,
                               "");
        }

        // What if all the guards have a condition? There will be
        // no "else" clause. This condition will fall through and
        // do nothing? Is that right? No. If that is the case,
        // then add the "else" clause and have it call this
        // transition's default.
        if (guardCount > 0 && nullConditions == 0)
        {
            source.println(" else {");
            source.println("            " +
                           mapName +
                           "_Default " +
                           _name +
                           " $context;");
            source.println("        }\n");
        }
        else if (nullConditions > 1)
        {
            // If there are multiple transitions with the same
            // name, then only one of them may be unguarded.
            ParseException e =
                    new ParseException("State " +
                                       mapName +
                                       "::" +
                                       stateName +
                                       " has multiple transitions with the same name (\"" +
                                       _name +
                                       "\") and guard.",
                                       0);
            throw(e);
        }
        else
        {
            source.println("");
        }

        source.println("        return -code ok;");
        source.println("    }");

        return;
    }
}
