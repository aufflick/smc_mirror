# 
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy
# of the License at http://www.mozilla.org/MPL/
# 
# Software distributed under the License is distributed on an
# "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
# 
# The Original Code is State Machine Compiler (SMC).
# 
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2007. Charles W. Rapp.
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

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
OBJC_LIB_STAGING_DIR=$(LIB_STAGING_DIR)/ObjC

#################################################################
# Rules.
#

$(OBJC_LIB_STAGING_DIR) : $(LIB_STAGING_DIR)
		mkdir $(OBJC_LIB_STAGING_DIR)

install :	statemap.h statemap.m $(OBJC_LIB_STAGING_DIR)
		cp -f statemap.h $(OBJC_LIB_STAGING_DIR)/README.txt
		cp -f statemap.h $(OBJC_LIB_STAGING_DIR)/statemap.h
		cp -f statemap.m $(OBJC_LIB_STAGING_DIR)/statemap.m

uninstall :
		-rm $(OBJC_LIB_STAGING_DIR)/README.txt \
		    $(OBJC_LIB_STAGING_DIR)/statemap.h \
		    $(OBJC_LIB_STAGING_DIR)/statemap.m

clean :

realclean :

#
# CHANGE LOG
# $Log$
# Revision 1.1  2007/01/15 00:23:50  cwrapp
# Release 4.4.0 initial commit.
#
