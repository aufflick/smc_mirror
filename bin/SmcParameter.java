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

import java.io.PrintStream;

public abstract class SmcParameter
{
// Member Methods

    public SmcParameter(String name, int line_number)
    {
        _name = name;
        _line_number = line_number;
        _type = "";
    }

    public boolean equals(Object obj)
    {
        boolean retval;

        try
        {
            SmcParameter parameter = (SmcParameter) obj;

            if (_name.compareTo(parameter.getName()) == 0 &&
                _type.compareTo(parameter.getType()) == 0)
            {
                retval = true;
            }
            else
            {
                retval = false;
            }
        }
        catch (Exception jex)
        {
            retval = false;
        }

        return(retval);
    }

    public int compareTo(SmcParameter param)
    {
        return(_type.compareTo(param.getType()));
    }

    public String getName()
    {
        return(_name);
    }

    public String getType()
    {
        return(_type);
    }

    public void appendType(String type_name, String separator)
    {
        _type += separator + type_name;
        return;
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public String toString()
    {
        return(_name + ": " + _type);
    }

    public abstract void generateCode(PrintStream source);

// Member Data.

    String _name;
    String _type;
    int _line_number;
}
