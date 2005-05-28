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
# Copyright (C) 2004. Charles W. Rapp.
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
# $Log$
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

STAGING_DIR=	../../../staging/smc/lib

#################################################################
# Rules.
#

all:		$(DLL_FILE)

# .dll can only be built withing Dev Studio.

$(STAGING_DIR)/CSharp :
		mkdir $(STAGING_DIR)/CSharp

$(STAGING_DIR)/CSharp/Debug : $(STAGING_DIR)/CSharp
		mkdir $(STAGING_DIR)/CSharp/Debug

$(STAGING_DIR)/CSharp/Release : $(STAGING_DIR)/CSharp
		mkdir $(STAGING_DIR)/CSharp/Release

install :	install_debug install_release

install_debug : Debug/$(DLL_FILE) $(STAGING_DIR)/CSharp/Debug
		cp -f Debug/$(DLL_FILE) $(STAGING_DIR)/CSharp/Debug
		cp -f Debug/$(PDB_FILE) $(STAGING_DIR)/CSharp/Debug

install_release : Release/$(DLL_FILE) $(STAGING_DIR)/CSharp/Release
		cp Release/$(DLL_FILE) $(STAGING_DIR)/CSharp/Release

uninstall :
		-rm -f $(STAGING_DIR)/CSharp/Debug/$(DLL_FILE)
		-rm -f $(STAGING_DIR)/CSharp/Debug/$(PDB_FILE)
		-rm -f $(STAGING_DIR)/CSharp/Release/$(DLL_FILE)

clean :
		-rm -f ./Debug/$(DLL_FILE) \
			./Debug/$(PDB_FILE) \
			./Release/$(DLL_FILE)

realclean :	clean
