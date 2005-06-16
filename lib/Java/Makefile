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
# Copyright (C) 2000 - 2005 Charles W. Rapp.
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

LIB_SOURCES=	FSMContext.java \
		State.java \
		TransitionUndefinedException.java \
		StateUndefinedException.java

PACKAGE_NAME=	statemap

JAVAC=		javac
JAVAC_FLAGS=	-d . -g
JAR=		jar
JAR_FLAGS=	cvf
JAR_FILE=	$(PACKAGE_NAME).jar

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
CLASS_DIR=	$(LIB_STAGING_DIR)/$(PACKAGE_NAME)

#################################################################
# Rules.
#

all:		$(JAR_FILE)

$(JAR_FILE) :	$(LIB_SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(LIB_SOURCES)
		$(JAR) $(JAR_FLAGS) $(JAR_FILE) $(PACKAGE_NAME)

$(CLASS_DIR) :
		mkdir $(CLASS_DIR)

install :	$(JAR_FILE) $(CLASS_DIR)
		cp -f $(JAR_FILE) ..
		cp -f $(JAR_FILE) $(LIB_STAGING_DIR)
		-chmod 444 $(LIB_STAGING_DIR)/$(JAR_FILE)
		cp -f $(PACKAGE_NAME)/*.class $(CLASS_DIR)

uninstall :
		-rm ../$(JAR_FILE)
		-rm $(LIB_STAGING_DIR)/$(JAR_FILE)

clean :
		-rm -f ./$(JAR_FILE) ./$(PACKAGE_NAME)/*

realclean :	clean
		-rm -fr ./statemap

#
# CHANGE LOG
# $Log$
# Revision 1.10  2005/06/16 18:26:35  fperrad
# Sorry, reverse to revision 1.8
#
# Revision 1.8  2005/06/08 11:09:14  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.7  2005/05/28 18:44:13  cwrapp
# Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
#
# Revision 1.3  2005/02/21 19:04:40  charlesr
# Make installed jar file read-only.
#
# Revision 1.2  2005/02/21 19:02:50  charlesr
# Including standard SMC makefile macros.
#
# Revision 1.1  2004/10/30 15:55:25  charlesr
# Added -f option to install copy.
#
# Revision 1.0  2003/12/14 20:38:57  charlesr
# Initial revision
#
