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
// Name
//  TimerEvent.java
//
// Description
//  Because I am using non-Swing, pre-JDK 1.4 Java, I have to
//  use my own timer class.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.2  2002/05/07 00:46:22  cwrapp
// Changes in release 1.3.2:
// Add the following feature:
// + 528321: Modified push transition syntax to be:
//
// 	  <transname> <state1>/push(<state2>)  {<actions>}
//
// 	  which means "transition to <state1> and then
// 	  immediately push to <state2>". The current
// 	  syntax:
//
// 	  <transname> push(<state2>)  {<actions>}
//
//           is still valid and <state1> is assumed to be "nil".
//
// No bug fixes.
//
// Revision 1.1  2001/06/26 22:16:23  cwrapp
// Changes in release 1.0.0:
// Checking in code for first production release.
// If SMC should crash, critical information are printed out along
// with instructions explaining where to send that information.
//

public final class TimerEvent
{
// Member functions.

    /**
     * Return the timer's name.
     * @return Timer name.
     */
    public String toString()
    {
        return(_timerName);
    }

    /**
     * Return the expired timer's name.
     * @return Timer name.
     */
    /* package */ String getTimerName()
    {
        return(_timerName);
    }

    // Only Gas is allowed to create timer events.
    /* package */ TimerEvent(String timerName)
    {
        _timerName = timerName;
    }

// Member data.

    // The timer generating this event.
    private String _timerName;
}
    
