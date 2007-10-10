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
# Port to Groovy by Francois Perrad, francois.perrad@gadz.org
# Copyright 2007, Francois Perrad.
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

LIB_SOURCES=	statemap.groovy

PACKAGE_NAME=	statemap

GROOVYC=	groovyc
JAR=		jar
JAR_FLAGS=	cvf
JAR_FILE=	$(PACKAGE_NAME).jar
CP=		cp -f
CHMOD=		chmod 444
MKDIR=		mkdir -p
RM_F=		rm -f
RM_RF=		rm -rf

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
GROOVY_LIB_STAGING_DIR=$(LIB_STAGING_DIR)/Groovy

#################################################################
# Rules.
#

all:		$(JAR_FILE)

$(JAR_FILE) :	$(LIB_SOURCES)
		$(GROOVYC) $(LIB_SOURCES)
		$(JAR) $(JAR_FLAGS) $(JAR_FILE) $(PACKAGE_NAME)

$(GROOVY_LIB_STAGING_DIR) :
		$(MKDIR) $(GROOVY_LIB_STAGING_DIR)

install :	$(GROOVY_LIB_STAGING_DIR) $(JAR_FILE)
		$(CP) $(JAR_FILE) ..
		$(CP) $(JAR_FILE) $(GROOVY_LIB_STAGING_DIR)
		-$(CHMOD) $(GROOVY_LIB_STAGING_DIR)/$(JAR_FILE)

uninstall :
		-$(RM_F) ../$(JAR_FILE)
		-$(RM_F) $(GROOVY_LIB_STAGING_DIR)/$(JAR_FILE)

clean :
		-$(RM_F) ./$(JAR_FILE) ./$(PACKAGE_NAME)/*

realclean :	clean
		-$(RM_RF) ./statemap

#
# CHANGE LOG
# $Log$
# Revision 1.2  2007/10/10 12:29:06  fperrad
# - fix Makefile (mkdir)
#
# Revision 1.1  2007/07/16 06:29:37  fperrad
# + Added Groovy.
#
#
