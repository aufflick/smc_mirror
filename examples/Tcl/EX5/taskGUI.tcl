# 
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
# 
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
# 
# The Original Code is State Machine Compiler (SMC).
# 
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2000 Charles W. Rapp.
# All Rights Reserved.
# 
# Contributor(s):
#
# GUIController --
#
#  This object simply passes messages between the view objects to
#  the model objects.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.3  2002/05/07 00:29:51  cwrapp
# Changes in release 1.3.2:
# Add the following feature:
# + 528321: Modified push transition syntax to be:
#
# 	  <transname> <state1>/push(<state2>)  {<actions>}
#
# 	  which means "transition to <state1> and then
# 	  immediately push to <state2>". The current
# 	  syntax:
#
# 	  <transname> push(<state2>)  {<actions>}
#
#           is still valid and <state1> is assumed to be "nil".
#
# No bug fixes.
#
# Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
#
# ----------------------------------------------------------------------
# SMC - The State Map Compiler
# Version: 1.0, Beta 3
#
# SMC compiles state map descriptions into a target object oriented
# language. Currently supported languages are: C++, Java and [incr Tcl].
# SMC finite state machines have such features as:
# + Entry/Exit actions for states.
# + Transition guards
# + Transition arguments
# + Push and Pop transitions.
# + Default transitions. 
# ----------------------------------------------------------------------
#
# Revision 1.1.1.1  2000/08/02 12:51:06  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

class GUIController {
# Member data.
    # Put messages on this queue for later delivery.
    # Each message is a list consisting of: the target object,
    # the target method and the method's arguments.
    private variable _messageQueue;

    # Store the message transmission timer ID here.
    private variable _timerID;

# Member methods.
    constructor {} {
        set _messageQueue {};
        set _timerID -1;
    }

    destructor {
        set _messageQueue {};
        if {_timerID >= 0} {
            after cancel $_timerID;
            set _timerID -1;
        }
    }

    # setLevel --
    #
    #   Set the message filter level to this new value.
    #
    # Arguments:
    #   level   The new message filter level.
    #
    # Results:
    #   None.

    public method setLevel {level} {
        postMessage messageGUI setLevel [list $level];
        return -code ok;
    }

    # postMessage --
    #
    #   Asynchronously call the specified object's method with
    #   the specified arguments.
    #
    # Arguments:
    #   object   Send message to this itcl object.
    #   method   Call object's method.
    #   argList  Pass these arguments to the method.
    #
    # Results:
    #   None.

    public method postMessage {object method args} {
        set Message [concat [list $object $method] $args];
        lappend _messageQueue $Message;
                

        if {$_timerID < 0} {
            set _timerID [after idle [list $this sendMessage]];
        }

        return -code ok;
    }

    # sendMessage --
    #
    #   Send all the messages currently on the queue but not
    #   any messages that are added after this method is
    #   entered. If there are more messages to send, then reset
    #   the transmission timer.
    #
    # Arguments:
    #   None.
    #
    # Results:
    #   None.

    public method sendMessage {} {
        # The transmission timer has expired, so the
        # current timer ID is no longer valid.
        set _timerID -1;

        while {[llength $_messageQueue] > 0} {
            set Message [lindex $_messageQueue 0];
            set _messageQueue [lrange $_messageQueue 1 end];

            # Use eval so the arguments get properly sent.
            eval $Message;
        }

        return -code ok;
    }
}
