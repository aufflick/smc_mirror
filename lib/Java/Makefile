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
# Revision 1.1  2001/01/03 03:14:00  cwrapp
# Initial revision
#
# Revision 1.1.1.1  2000/08/02 12:50:57  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

LIB_SOURCES=	FSMContext.java \
		State.java

PACKAGE_NAME=	statemap

JAVAC=		javac
JAVAC_FLAGS=	-d . -g
JAR=		jar
JAR_FLAGS=	cvf
JAR_FILE=	$(PACKAGE_NAME).jar

all:		$(JAR_FILE)

$(JAR_FILE) :	$(LIB_SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(LIB_SOURCES)
		$(JAR) $(JAR_FLAGS) $(JAR_FILE) $(PACKAGE_NAME)

install :	$(JAR_FILE)
		cp $(JAR_FILE) ../../../Smc/lib

clean :
		-rm -f $(JAR_FILE) $(PACKAGE_NAME)/*
