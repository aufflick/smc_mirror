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
# AppClass --
#
#  This class contains the 0*1* state machine and decides whether a
#  a particular string fits the pattern or not.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.3  2002/05/07 00:29:50  cwrapp
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
# Revision 1.1.1.1  2000/08/02 12:51:04  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

package require statemap;

source ./AppClass_sm.tcl;

class AppClass {
# Member data.
    private variable _is_acceptable;
    private variable _fsm;

# Member functions.
    constructor {} {
        set _fsm [AppClassContext #auto $this];
        set _is_acceptable 0;

        # Uncomment to see debug output;
        # $_fsm setDebugFlag 1;
    }

    public method checkString {astring} {
		# If the string is {}, then it is an empty string.
		# In that case, issue the EOS transition now.
		if {[string compare astring "{}"] != 0} {
			set string_length [string length $astring];
			for {set i 0} {$i < $string_length} {incr i} {
				switch -exact -- [string index $astring $i] {
					0 { set Transition Zero; }
					1 { set Transition One; }
					default { set Transition Unknown; }
				}

				# puts "TRANSITION: $Transition";
				$_fsm $Transition;
			}
		}

		# puts "TRANSITION: EOS";
		$_fsm EOS;

		return -code ok $_is_acceptable;
    }

    public method Acceptable {} {
		set _is_acceptable 1;
    }

    public method Unacceptable {} {
		set _is_acceptable 0;
    }
}
