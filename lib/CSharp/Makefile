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
# Copyright (C) 2004, 2005. Charles W. Rapp.
# All Rights Reserved.
# 
# Contributor(s):
#   Eitan Suez contributed examples/Ant.
#   (Name withheld) contributed the C# code generation and
#   examples/C#.
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

# Include the official macros.
include ../../smc.mk

LIB_SOURCES=	FSMContext.cs \
		State.cs \
		TransitionUndefinedException.cs \
		StateUndefinedException.cs

PACKAGE_NAME=	statemap

JAVAC=		javac
JAVAC_FLAGS=	-d . -g

JAR=		jar
JAR_FLAGS=	cvf

DLL_FILE=	$(PACKAGE_NAME).dll
PDB_FILE=	$(PACKAGE_NAME).pdb

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib/DotNet

#################################################################
# Rules.
#

all:		$(DLL_FILE)

# .dll can only be built withing Dev Studio.

$(LIB_STAGING_DIR) :
		mkdir $(LIB_STAGING_DIR)

$(LIB_STAGING_DIR)/Debug : $(LIB_STAGING_DIR)
		mkdir $(LIB_STAGING_DIR)/Debug

$(LIB_STAGING_DIR)/Debug/Trace : $(LIB_STAGING_DIR)/Debug
		mkdir $(LIB_STAGING_DIR)/Debug/Trace

$(LIB_STAGING_DIR)/Debug/NoTrace : $(LIB_STAGING_DIR)/Debug
		mkdir $(LIB_STAGING_DIR)/Debug/NoTrace

$(LIB_STAGING_DIR)/Release : $(LIB_STAGING_DIR)
		mkdir $(LIB_STAGING_DIR)/Release

$(LIB_STAGING_DIR)/Release/Trace : $(LIB_STAGING_DIR)/Release
		mkdir $(LIB_STAGING_DIR)/Release/Trace

$(LIB_STAGING_DIR)/Release/NoTrace : $(LIB_STAGING_DIR)/Release
		mkdir $(LIB_STAGING_DIR)/Release/NoTrace

install :	install_debug install_release

install_debug : install_debug_w_trace install_debug_wo_trace

install_debug_w_trace : Debug/$(DLL_FILE) $(LIB_STAGING_DIR)/Debug/Trace
		cp -f Debug/$(DLL_FILE) $(LIB_STAGING_DIR)/Debug/Trace
		cp -f Debug/$(PDB_FILE) $(LIB_STAGING_DIR)/Debug/Trace

install_debug_wo_trace : Debug/$(DLL_FILE) $(LIB_STAGING_DIR)/Debug/NoTrace
		cp -f DebugNoTrace/$(DLL_FILE) $(LIB_STAGING_DIR)/Debug/NoTrace
		cp -f DebugNoTrace/$(PDB_FILE) $(LIB_STAGING_DIR)/Debug/NoTrace

install_release : install_release_w_trace install_release_wo_trace

install_release_w_trace : Release/$(DLL_FILE) $(LIB_STAGING_DIR)/Release/Trace
		cp Release/$(DLL_FILE) $(LIB_STAGING_DIR)/Release/Trace

install_release_wo_trace : Release/$(DLL_FILE) $(LIB_STAGING_DIR)/Release/NoTrace
		cp ReleaseNoTrace/$(DLL_FILE) $(LIB_STAGING_DIR)/Release/NoTrace

uninstall :
		-rm -f $(LIB_STAGING_DIR)/Debug/Trace/$(DLL_FILE)
		-rm -f $(LIB_STAGING_DIR)/Debug/Trace/$(PDB_FILE)
		-rm -f $(LIB_STAGING_DIR)/Debug/NoTrace/$(DLL_FILE)
		-rm -f $(LIB_STAGING_DIR)/Debug/NoTrace/$(PDB_FILE)
		-rm -f $(LIB_STAGING_DIR)/Release/Trace/$(DLL_FILE)
		-rm -f $(LIB_STAGING_DIR)/Release/NoTrace/$(DLL_FILE)

clean :

realclean :	clean
		-rm -f ./Debug/$(DLL_FILE) \
			./Debug/$(PDB_FILE) \
			./Release/$(DLL_FILE)

#
# CHANGE LOG
# $Log$
# Revision 1.3  2006/09/16 15:04:28  cwrapp
# Initial v. 4.3.3 check-in.
#
# Revision 1.2  2005/06/08 11:09:14  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.1  2005/05/28 18:44:13  cwrapp
# Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
#
# Revision 1.2  2005/02/21 19:02:37  charlesr
# Including standard SMC makefile macros.
#
# Revision 1.1  2004/10/30 15:55:01  charlesr
# Added -f option to install copy.
#
# Revision 1.0  2004/09/06 16:32:18  charlesr
# Initial revision
#
