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
// Function
//	Main
//
// Description
//  This routine starts the finite state machine running.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:50:59  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

#ifdef WIN32
#include <iostream>
#include <windows.h>
#else
#include <iostream.h>
#endif
#include "AppClass.h"

const static char _rcs_id[] = "$Id$";

#ifdef WIN32
using namespace std;
#endif

int main(int argc, char *argv[])
{
	AppClass thisContext;

	if (argc < 2)
	{
		cerr << "No string to check." << endl;
		return(1);
	}
	else
	{
		cout << "The string \"" << argv[1] << "\" is ";

		if (thisContext.CheckString(argv[1]) == false)
		{
			cout << "not acceptable." << endl;
		}
		else
		{
			cout << "acceptable." << endl;
		}
	}

	return(0);
}
