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
# Copyright (C) 2005 Charles W. Rapp.
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

CP=		cp -f
CHMOD=		chmod 444
MKDIR=		mkdir -p
RM_F=		rm -f
RM_RF=		rm -rf

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
LUA_LIB_STAGING_DIR=$(LIB_STAGING_DIR)/Lua

#################################################################
# Rules.
#

$(LUA_LIB_STAGING_DIR) : $(LIB_STAGING_DIR)
		$(MKDIR) $(LUA_LIB_STAGING_DIR)

install :	$(LUA_LIB_STAGING_DIR) statemap.lua
		$(CP) statemap.lua $(LUA_LIB_STAGING_DIR)/statemap.lua
		$(CP) README $(LUA_LIB_STAGING_DIR)/README

uninstall :
		-$(RM_F) $(LUA_LIB_STAGING_DIR)/statemap.lua
		-$(RM_F) $(LUA_LIB_STAGING_DIR)/README

clean :

realclean :

#
# CHANGE LOG
# $Log$
# Revision 1.2  2007/10/11 12:48:35  fperrad
# - refactor: Makefile with variables
#
# Revision 1.1  2007/01/03 15:49:23  fperrad
# + Added Lua.
#
#
#
