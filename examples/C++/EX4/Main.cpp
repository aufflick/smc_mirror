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
// Revision 1.3  2002/02/19 19:52:45  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
//
// Revision 1.2  2001/12/14 20:10:36  cwrapp
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
// Revision 1.1.1.1  2000/08/02 12:50:59  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

#ifdef WIN32
#include <iostream>
#include <windows.h>
#include <winbase.h>
#endif
#include <stdlib.h>
#include <signal.h>
#include "stoplight.h"

#ifdef WIN32
using namespace std;
#else
#ifndef SA_NOMASK
#define SA_NOMASK 0
#endif
#endif

const static char _rcs_id[] = "$Id$";

using namespace cpp_ex4;

Stoplight *TheLight;
int KeepGoing = 1;

int YellowTimer = 2;
	// Yellow lights last 2 seconds.

int NSGreenTimer = 8;
	// North-south green lasts 8 seconds.

int EWGreenTimer = 5;
	// East-west green lasts 5 seconds.

#ifdef WIN32
// Number of milliseconds until the next timeout.
DWORD Gtimeout;
#endif

int main()
{
	void SigintHandler(int);

#ifdef WIN32
    // Windows kinda supports signals.
    (void) signal(SIGINT, SigintHandler);
#else
	struct sigaction signalAction;

	void SigalrmHandler(int);

	signalAction.sa_handler = SigintHandler;
#if defined(__hpux) || defined (__linux__)
	sigemptyset(&signalAction.sa_mask);
#if defined(__linux__)
	signalAction.sa_restorer = NULL;
#endif
#endif
	signalAction.sa_flags = SA_NOMASK;
	if (sigaction(SIGINT, &signalAction, (struct sigaction *) NULL) != 0)
	{
		cerr << "Unable to set SIGINT handling function." << endl;
		exit(1);
	}

	signalAction.sa_handler = SigalrmHandler;
#if defined(__hpux) || defined(__linux__)
	sigemptyset(&signalAction.sa_mask);
#if defined(__linux__)
	signalAction.sa_restorer = NULL;
#endif
#endif
	signalAction.sa_flags = SA_NOMASK;
	if (sigaction(SIGALRM, &signalAction, (struct sigaction *) NULL) != 0)
	{
		cerr << "Unable to set SIGALRM handling function." << endl;
		exit(1);
	}
#endif

	TheLight = new Stoplight(EAST_WEST);
	if (TheLight == (Stoplight *) NULL)
	{
		cerr << "Failed to create stoplight object." << endl;
		exit(1);
	}

#ifdef WIN32
    // Wait for either the app to end or for timers to expire.
    while (KeepGoing)
    {
        // Because this demo only has one timer, just
        // sleep for that specified time period but for
        // only a half a second at a time to allow SIGINTs
        // to be received.
        while (Gtimeout > 0)
        {
            Gtimeout -= 500;
            Sleep(500);
        }

        TheLight->Timeout();
    }
#else
	while (KeepGoing)
        ;
#endif

	cout << "Terminating application." << endl;
	return(0);
}

void SigintHandler(int)
{
	KeepGoing = 0;
	return;
}

#ifndef WIN32
void SigalrmHandler(int)
{
	TheLight->Timeout();
	return;
}
#endif
