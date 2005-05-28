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

LIB_SOURCES=	FSMContext.vb \
		State.vb \
		TransitionUndefinedException.vb \
		StateUndefinedException.vb

PACKAGE_NAME=	statemap

JAVAC=		javac
JAVAC_FLAGS=	-d . -g
JAR=		jar
JAR_FLAGS=	cvf
DLL_FILE=	$(PACKAGE_NAME).dll

STAGING_DIR=	../../../staging/smc/lib

#################################################################
# Rules.
#

all:		$(DLL_FILE)

# .dll can only be built withing Dev Studio.

$(STAGING_DIR)/VB :
		mkdir $(STAGING_DIR)/VB

install :	$(DLL_FILE) $(STAGING_DIR)/VB
		cp -f $(DLL_FILE) $(STAGING_DIR)/VB

uninstall :
		-rm -f $(STAGING_DIR)/VB/$(DLL_FILE)

clean :
		-rm -f ./$(DLL_FILE) ./$(PACKAGE_NAME)/*

realclean :	clean

#
# CHANGE LOG
# $Log$
# Revision 1.1  2005/05/28 18:47:13  cwrapp
# Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
#
# Revision 1.3  2005/02/21 19:06:40  charlesr
# Including standard SMC macros.
#
# Revision 1.2  2004/10/30 15:57:07  charlesr
# Added -f option to the install copy.
#
# Revision 1.1  2004/09/06 16:35:55  charlesr
# Updated for SMC v. 3.1.0.
#
# Revision 1.0  2004/05/31 13:46:35  charlesr
# Initial revision
#
