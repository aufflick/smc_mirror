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
// Revision 1.2  2000/09/01 15:32:21  charlesr
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

public abstract class SmcTransition
{
// Member Methods

    public SmcTransition(String name,
                         LinkedList parameters,
                         int line_number)
    {
        _name = name;
        _parameters = parameters;
        _line_number = line_number;
        _guards = new LinkedList();
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcTransition trans = (SmcTransition) obj;

            if (_name.compareTo(trans.getName()) != 0 ||
                _parameters.size() != trans.getParameters().size())
            {
                retval = false;
            }
            else
            {
                ListIterator it1;
                ListIterator it2;
                SmcParameter param1;
                SmcParameter param2;

                // The two parameter lists are equal until proven
                // otherwise.
                for (it1 = _parameters.listIterator(),
                         it2 = trans.getParameters().listIterator(),
                         retval = true;
                     it1.hasNext() == true && retval == true;
                    )
                {
                    param1 = (SmcParameter) it1.next();
                    param2 = (SmcParameter) it2.next();
                    retval = param1.equals(param2);
                }
            }
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return(retval);
    }

    public int compareTo(SmcTransition trans)
    {
        int retval;

        if ((retval = _name.compareTo(trans.getName())) == 0)
        {
            retval = _parameters.size() - trans.getParameters().size();
            if (retval == 0)
            {
                ListIterator it1;
                ListIterator it2;
                SmcParameter param1;
                SmcParameter param2;

                // Compare each parameter's name ONLY.
                for (it1 = _parameters.listIterator(),
                         it2 = trans.getParameters().listIterator();
                     it1.hasNext() == true && retval == 0;
                    )
                {
                    param1 = (SmcParameter) it1.next();
                    param2 = (SmcParameter) it2.next();
                    retval = param1.compareTo(param2);
                }
            }
        }

        return(retval);
    }

    public String getName()
    {
        return(_name);
    }

    public LinkedList getParameters()
    {
        return(_parameters);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public void addGuard(SmcGuard guard)
    {
        _guards.add(guard);
        return;
    }

    public LinkedList getGuards()
    {
        return(_guards);
    }

    public String toString()
    {
        String retval;
        ListIterator it;

        retval = _name;

        if (_parameters.size() > 0)
        {
            String separator;
            SmcParameter param;

            retval += "(";
            for (it = _parameters.listIterator(),
                     separator = "";
                 it.hasNext() == true;
                 separator = ", ")
            {
                param = (SmcParameter) it.next();
                retval += separator + param;
            }
            retval += ")";
        }

        if (_guards.size() > 0)
        {
            SmcGuard guard;

            for (it = _guards.listIterator();
                 it.hasNext() == true;
                )
            {
                guard = (SmcGuard) it.next();
                retval += "\n" + guard;
            }
        }

        return(retval);
    }

    public abstract void generateCode(PrintStream header,
                                      PrintStream source,
                                      String context,
                                      String mapName,
                                      String stateName,
                                      String indent)
        throws ParseException;

// Member Data

    protected String _name;
    protected int _line_number;
    protected LinkedList _parameters;
    protected LinkedList _guards;
}
