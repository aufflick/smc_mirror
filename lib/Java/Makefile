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
# CHANGE LOG
# $Log$
# Revision 1.3  2001/11/30 15:17:22  cwrapp
# Changes in release 1.0.2:
# + Placed the class files in Smc.jar in the net.sf.smc package.
# + Moved Java source files from smc/bin to net/sf/smc.
# + Corrected a C++ generation bug wherein arguments were written
#   to the .h file rather than the .cpp file.
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

STAGING_DIR=	../../../staging/smc/lib

all:		$(JAR_FILE)

$(JAR_FILE) :	$(LIB_SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(LIB_SOURCES)
		$(JAR) $(JAR_FLAGS) $(JAR_FILE) $(PACKAGE_NAME)

install :	$(JAR_FILE)
		cp $(JAR_FILE) $(STAGING_DIR)

uninstall :
		-rm $(STAGING_DIR)/$(JAR_FILE)

clean :
		-rm -f $(JAR_FILE) $(PACKAGE_NAME)/*

realclean :	clean
		-rm -fr ./statemap
