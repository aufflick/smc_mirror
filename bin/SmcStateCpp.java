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
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:19  charlesr
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

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ListIterator;

public final class SmcStateCpp
    extends SmcState
{
    public SmcStateCpp(String name, int line_number)
    {
        super(name, line_number);
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String mapName,
                             String context)
        throws ParseException
    {
        ListIterator transIt;
        ListIterator actionIt;
        SmcTransition transition;
        SmcAction action;

        header.println("\nclass " +
                       mapName +
                       "_" +
                       _class_name +
                       " : public " +
                       mapName +
                       "_Default\n{");
        header.println("public:");

        // Add the required getName() method.
        header.println("    " +
                       mapName +
                       "_" +
                       _class_name +
                       "(const char *name)");
        header.println("    : " +
                       mapName +
                       "_Default(name)");
        header.println("    {};\n");

        // Add the Entry() and Exit() methods if this state
        // defines them.
        if (_entryActions.size() > 0)
        {
            header.println("    void Entry(" +
                           context +
                           "Context&);");

            source.println("\nvoid " +
                           mapName +
                           "_" +
                           _class_name +
                           "::Entry(" +
                           context +
                           "Context& s)\n{");

            // Generate the actions associated with this code.
            for (actionIt = _entryActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source, context, "    ");
                source.println(";");
            }

            // End the Entry() method with a return.
            source.println("    return;\n}");
        }

        if (_exitActions.size() > 0)
        {
            header.println("    void Exit(" +
                           context +
                           "Context&);");

            source.println("\nvoid " +
                           mapName +
                           "_" +
                           _class_name +
                           "::Exit(" +
                           context +
                           "Context& s)\n{");

            // Generate the actions associated with this code.
            for (actionIt = _exitActions.listIterator();
                 actionIt.hasNext() == true;
                )
            {
                action = (SmcAction) actionIt.next();
                action.generateCode(source, context, "    ");
                source.println(";");
            }

            // End the Exit() method with a return.
            source.println("    return;\n}");
        }

        // Have the transitions generate their code.
        for (transIt = _transitions.listIterator();
             transIt.hasNext() == true;
            )
        {
            transition = (SmcTransition) transIt.next();
            transition.generateCode(header,
                                    source,
                                    context,
                                    mapName,
                                    _class_name,
                                    null);
        }

        // End of the state class declaration.
        header.println("};");

        return;
    }
}
