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
// Member Functions
//	AppClass() 					 - Default constructor.
//	Run()						 - Start the system running.
//	ReceiveRequest(const char *) - Process a request.
//	CheckForRequest()			 - See if a request has arrived.
//	DoRequest()					 - Do the actual request processing here.
//	ProcessingCompleted()		 - All done.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.1.1.1  2000/08/02 12:51:00  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

#ifdef WIN32
#include <iostream>
#include <windows.h>
#else
#include <iostream.h>
#include <unistd.h>
#include <sys/time.h>
#endif
#include "AppClass.h"

#ifdef WIN32
using namespace std;
#endif

const static char _rcs_id[] = "$Id$";

AppClass::AppClass()
: _state_map(*this),
  _number_of_requests(0),
  _continue_running(1)
{
    // Uncomment following line to see debug output.
    // _state_map.setDebugFlag(true);
}

void AppClass::Run()
{
#ifdef WIN32
    DWORD SleepTime;
#endif

	while (_continue_running == 1)
	{
#ifdef WIN32
        // Sleep for half a second at a time.
        // This will allow for timely receipt of
        // SIGINTs.
        for (SleepTime = 5000; SleepTime > 0; SleepTime -= 500)
        {
            Sleep(500);
        }

        ProcessingCompleted();
#else
	    pause();
#endif
	}

	return;
} // end of AppClass::Run()

void AppClass::ReceiveRequest(const char *message)
{
	if (strcmp(message, "stop") == 0)
	{
		// Stop processing messages.
		_continue_running = 0;
	}
	else
	{
		// Increment the request count.
		++_number_of_requests;

		// Process this message.
		_state_map.RequestReceived();
	}

	return;
} // end of AppClass::ReceiveRequest(const char*)

void AppClass::CheckForRequest()
{
	if (_number_of_requests > 0)
	{
		_state_map.ProcessRequest();
	}
	else if (_number_of_requests < 0)
	{
		cout << "The number of outstanding requests is less than zero (";
		cout << _number_of_requests << "); resetting to zero." << endl;
		_number_of_requests = 0;
	}

	return;
} // end of AppClass::CheckForRequest()

void AppClass::DoRequest()
{
	// Decrement the request count.
	--_number_of_requests;

#ifdef WIN32
    cout << "Processing request ..." << endl;
#else
	// Local variable decalarations.
	itimerval nextTimeout;

	// Sleep on this request.
	(void) memset((char *) &nextTimeout, 0, sizeof(nextTimeout));
	nextTimeout.it_value.tv_sec = 5;
	if (setitimer(ITIMER_REAL, &nextTimeout, (itimerval *) NULL) < 0)
	{
	    // Failed to start process timer - quit.
		_continue_running = 0;
	}
	else
	{
	    cout << "Processing request ..." << endl;
	}
#endif

	return;
} // end of AppClass::DoRequest()

void AppClass::ProcessingCompleted()
{
    cout << "... Processing completed." << endl;

    _state_map.ProcessingDone();
} // end of AppClass::ProcessingCompleted()
