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
# Copyright (C) 2000 - 2005. Charles W. Rapp.
# All Rights Reserved.
#
# Port to Python by Francois Perrad, francois.perrad@gadz.org
# Copyright 2004, Francois Perrad.
# All Rights Reserved.
# 
# Contributor(s):
#   Eitan Suez contributed examples/Ant.
#   (Name withheld) contributed the C# code generation and
#   examples/C#.
#   Francois Perrord contributed the Python code generator and
#   examples/Python.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# (See the bottom of this file.)
#

#################################################################
# Macros.
#

VERSION=	4_3_2

STAGING_DIR=	../staging
SMC_STAGING_DIR=$(STAGING_DIR)/smc
SMC_RELEASE_DIR=$(STAGING_DIR)/smc_$(VERSION)
RELEASE_DIR=	$(STAGING_DIR)/releases

#################################################################
# Rules.
#

# Create the staging directories if needed.
$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

$(SMC_STAGING_DIR) :	$(STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)
		mkdir $(SMC_STAGING_DIR)

#
# CHANGE LOG
# $Log$
# Revision 1.10  2006/07/11 18:33:20  cwrapp
# Updated version.
#
# Revision 1.9  2006/04/22 12:45:22  cwrapp
# Version 4.3.1
#
# Revision 1.8  2005/11/07 19:34:53  cwrapp
# Changes in release 4.3.0:
# New features:
#
# + Added -reflect option for Java, C#, VB.Net and Tcl code
#   generation. When used, allows applications to query a state
#   about its supported transitions. Returns a list of transition
#   names. This feature is useful to GUI developers who want to
#   enable/disable features based on the current state. See
#   Programmer's Manual section 11: On Reflection for more
#   information.
#
# + Updated LICENSE.txt with a missing final paragraph which allows
#   MPL 1.1 covered code to work with the GNU GPL.
#
# + Added a Maven plug-in and an ant task to a new tools directory.
#   Added Eiten Suez's SMC tutorial (in PDF) to a new docs
#   directory.
#
# Fixed the following bugs:
#
# + (GraphViz) DOT file generation did not properly escape
#   double quotes appearing in transition guards. This has been
#   corrected.
#
# + A note: the SMC FAQ incorrectly stated that C/C++ generated
#   code is thread safe. This is wrong. C/C++ generated is
#   certainly *not* thread safe. Multi-threaded C/C++ applications
#   are required to synchronize access to the FSM to allow for
#   correct performance.
#
# + (Java) The generated getState() method is now public.
#
# Revision 1.7  2005/09/19 15:20:01  cwrapp
# Changes in release 4.2.2:
# New features:
#
# None.
#
# Fixed the following bugs:
#
# + (C#) -csharp not generating finally block closing brace.
#
# Revision 1.6  2005/09/14 01:51:33  cwrapp
# Changes in release 4.2.0:
# New features:
#
# None.
#
# Fixed the following bugs:
#
# + (Java) -java broken due to an untested minor change.
#
# Revision 1.5  2005/08/26 15:21:33  cwrapp
# Final commit for release 4.2.0. See README.txt for more information.
#
# Revision 1.4  2005/06/30 10:44:02  cwrapp
# Added %access keyword which allows developers to set the generate Context
# class' accessibility level in Java and C#.
#
# Revision 1.3  2005/06/18 18:28:36  cwrapp
# SMC v. 4.0.1
#
# New Features:
#
# (No new features.)
#
# Bug Fixes:
#
# + (C++) When the .sm is in a subdirectory the forward- or
#   backslashes in the file name are kept in the "#ifndef" in the
#   generated header file. This is syntactically wrong. SMC now
#   replaces the slashes with underscores.
#
# + (Java) If %package is specified in the .sm file, then the
#   generated *Context.java class will have package-level access.
#
# + The Programmer's Manual had incorrect HTML which prevented the
#   pages from rendering correctly on Internet Explorer.
#
# + Rewrote the Programmer's Manual section 1 to make it more
#   useful.
#
# Revision 1.2  2005/06/08 11:08:58  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.1  2005/05/28 19:41:44  cwrapp
# Update for SMC v. 4.0.0.
#
