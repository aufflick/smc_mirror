#!/bin/sh
# -*- tab-width: 4; -*-
# \
exec Tclsh80 "$0" "$@"

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
# checkstring --
#
#  This test program uses the state map language to determine
#  is a string is of the form 0*1*.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.1  2001/01/03 03:14:00  cwrapp
# Initial revision
#
# Revision 1.1.1.1  2000/08/02 12:51:05  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

package require Itcl;
package require statemap;

namespace import ::itcl::*;
namespace import ::statemap::*;

source ./AppClass.tcl;

# Check if a string has been passed in.
if {[llength $argv] < 1} {
    puts stderr "No string to check.";
    set ErrorCode error;
} else {
    AppClass mycontext;

    set ErrorCode 0;

    set InputString [lindex $argv 0];
    puts -nonewline stdout "The string \"$InputString\" is ";
    flush stdout;

    if {! [mycontext CheckString $InputString]} {
	puts -nonewline stdout "not ";
    }

    puts stdout "acceptable.";
}

exit $ErrorCode;
