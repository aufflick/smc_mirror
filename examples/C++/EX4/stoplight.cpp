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
//	Stoplight
//
// Member Functions
//	Stoplight()						   - Default constructor.
//	Stoplight(Directions)			   - Set initial direction.
//	TurnLight(StopLights, LightColors) - Change directions.
//	SetTimer(int)					   - Start a timer.
//	Initialize(Directions)			   - Set start state and timer.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/02/13 02:45:22  cwrapp
// Changes in release 1.2.0:
// Added the following features:
// + 484889: "pop" transitions can now return arguments
//           along with a transition name.
// + 496625: Multiple .sm files may be specified in the
//           compile command.
//
// Fixed the following bugs:
// + 496692: Fixed the %package C++ code generation.
// + 501157: Transition debug output was still hardcoded
//           to System.err. This has been corrected so
//           that FSMContext._debug_stream is used.
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
// Revision 1.1.1.1  2000/08/02 12:51:00  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

#ifdef WIN32
#include <windows.h>
#include <winbase.h>
#else
#include <sys/time.h>
#endif
#include <stdlib.h>
#include "stoplight.h"

const static char _rcs_id[] = "$Id$";

extern int NSGreenTimer;
extern int EWGreenTimer;
#ifdef WIN32
extern DWORD Gtimeout;
#endif

namespace cpp_ex4
{
    Stoplight::Stoplight()
        : _state_map(*this)
    {
        Initialize(NORTH_SOUTH);

        // Uncomment to see debug messages.
        // _state_map.setDebugFlag(true);
    }

    Stoplight::Stoplight(Directions direction)
        : _state_map(*this)
    {
        Initialize(direction);
    }

    void Stoplight::TurnLight(StopLights light, LightColors color)
    {
        cout << "Turning the ";

        switch(light)
        {
            case EWLIGHT:
                cout << "east-west lights ";
                break;

            case NSLIGHT:
                cout << "north-south lights ";
                break;
        }

        switch(color)
        {
            case GREEN:
                cout << "green." << endl;
                break;

            case YELLOW:
                cout << "yellow." << endl;
                break;

            case RED:
                cout << "red." << endl;
                break;
        }

        return;
    }

    void Stoplight::SetTimer(int seconds)
    {
#ifdef WIN32
        Gtimeout = seconds * 1000;
#else
        itimerval timeout;

        timeout.it_value.tv_sec = seconds;
        timeout.it_value.tv_usec = 0;
        timeout.it_interval.tv_sec = 0;
        timeout.it_interval.tv_usec = 0;

        if (setitimer(ITIMER_REAL, &timeout, (itimerval *) NULL) < 0)
        {
            cerr << "Failed to set timer. Quitting application." << endl;
            exit(1);
        }
#endif

        return;
    }

    void Stoplight::Initialize(Directions direction)
    {
        switch(direction)
        {
            case NORTH_SOUTH:
                cout << "Turning the north-south lights green." << endl;
                _state_map.setState(StopMap::NorthSouthGreen);
                SetTimer(NSGreenTimer);
                break;

            case EAST_WEST:
                cout << "Turning the east-west lights green." << endl;
                _state_map.setState(StopMap::EastWestGreen);
                SetTimer(EWGreenTimer);
                break;
        }
    }
}
