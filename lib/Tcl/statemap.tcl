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
# The Original Code is State Map Compiler (SMC).
# 
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2000 Charles W. Rapp.
# All Rights Reserved.
# 
# Contributor(s):
# 
# RCS ID
# $Id$
#
# statemap.tcl --
#
#  This package defines the fsmContext class which must be inherited by
#  any [incr Tcl] class wanting to use an smc generated state map.
#
# Change Log
# $Log$
# Revision 1.3  2001/10/12 14:28:04  cwrapp
# SMC v. 1.0.1
#
# Revision 1.2  2001/05/09 23:40:02  cwrapp
# Changes in release 1.0, beta 6:
# Fixes the four following bugs:
# + 416011: SMC does not properly handle pop transitions which
#           have no argument.
# + 416013: SMC generated code does not throw a
#           "Transition Undefined" exception as per Programmer's
#           Manual.
# + 416014: The initial state's Entry actions are not being
#           executed.
# + 416015: When a transition has both a guarded and an unguarded
#           definition, the Exit actions are only called when the
#           guard evaluates to true.
# + 422795: SMC -tcl abnormally terminates.
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
# Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

package provide statemap 0.1;

package require Itcl;

namespace eval ::statemap:: {
    namespace export FSMContext;
}

::itcl::class ::statemap::State {
# Member data.
    private variable _name "NAME NOT SET";

# Member functions.

    constructor {name} {
        set _name $name;
    }

    public method getName {} {
        return -code ok $_name;
    }
}

::itcl::class ::statemap::FSMContext {
# Member data.

    protected variable _state "";
    private variable _previous_state "";
    private variable _state_stack {};
    private variable _transition;
    private variable _debug_flag;

# Member functions.

    constructor {} {
        set _state "";
        set _previous_state "";
        set _state_stack {};
        set _transition "";
        set _debug_flag 0;
    }

    public method getDebugFlag {} {
        return -code ok $_debug_flag;
    }

    public method setDebugFlag {flag} {
        if {$flag != 0} {
            set _debug_flag 1;
        } else {
            set _debug_flag 0;
        }

        return -code ok;
    }

    public method isInTransition {} {
        if {[string compare $_state ""] == 0} {
            set retval 1;
        } else {
            set retval 0;
        }

        return -code ok $retval;
    }

    public method getTransition {} {
        return -code ok $_transition;
    }

    public method getState {} {
        if {[string compare $_state ""] == 0} {
            return -code error "The start state has not been set.";
        } else {
            return -code ok $_state;
        }
    }

    public method setState {state_name} {
        if {$_debug_flag == 1} {
            puts stderr "NEW STATE     : [$state_name getName]";
        }

        set _state $state_name;

        return -code ok;
    }

    public method clearState {} {
        set _previous_state $_state;
        set _state "";

        return -code ok;
    }

    public method getPreviousState {} {
        if {[string compare $_previous_state ""] == 0} {
            return -code error "The previous state has not been set.";
        } else {
            return -code ok $_previous_state;
        }
    }

    public method pushState {state_name} {
        if {$_debug_flag == 1} {
            puts stderr "PUSH TO STATE : [$state_name getName]";
        }

        if {[string compare $_state ""] != 0} {
            lappend _state_stack $_state;
        }

        set _state $state_name;

        return -code ok;
    }

    public method popState {} {
        if {[llength $_state_stack] > 0} {
            set _state [lindex $_state_stack end];
            set _state_stack [lrange $_state_stack 0 [expr [llength $_state_stack] - 2]];

            set Retcode ok;
            set Retval "";

            if {$_debug_flag == 1} {
                puts stderr "POP TO STATE  : [$_state getName]";
            }
        } else {
            # Tried to pop on an empty state stack.
            if {$_debug_flag == 1} {
                puts stderr "POPPING ON EMPTY STATE STACK.";
            }

            set Retcode error;
            set Retval "Pop transition does not have a matching push transition (State: [$_state getName]).";
        }

        return -code $Retcode $Retval;
    }

    public method emptyStateStack {} {
        if {[llength $_state_stack] > 0} {
            set _state_stack {};
        }

        return -code ok;
    }

    # Save away the transition name only if debugging is
    # turned on.
    protected method setTransition {transition} {
        if {$_debug_flag == 1} {
            set _transition $transition;
        }
    }
}
