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
// Class
//	AppClass
//
// Inline Member Functions
//	AppClass()				   - Default constructor.
//	CheckString(const char *)  - Is this string acceptable?
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:50:58  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

#include "AppClass.h"

const static char _rcs_id[] = "$Id$";

AppClass::AppClass()
: _state_map(*this),
  isAcceptable(false)
{
    // Uncomment to see debug output.
    // _state_map.setDebugFlag(true);
}

bool AppClass::CheckString(const char *theString)
{
	while(*theString)
	{
		switch(*theString)
		{
		case '0':
			_state_map.Zero();
			break;

		case '1':
			_state_map.One();
			break;

		default:
			_state_map.Unknown();
			break;
		}
		++theString;
	}

	// end of string has been reached - send the EOS transition.
	_state_map.EOS();

	return(isAcceptable);
}
