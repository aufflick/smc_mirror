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

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
RUBY_LIB_STAGING_DIR=$(LIB_STAGING_DIR)/Ruby

#################################################################
# Rules.
#

$(RUBY_LIB_STAGING_DIR) : $(LIB_STAGING_DIR)
		mkdir $(RUBY_LIB_STAGING_DIR)

install :	$(RUBY_LIB_STAGING_DIR) statemap.rb
		cp -f statemap.rb $(RUBY_LIB_STAGING_DIR)/statemap.rb
		cp -f README $(RUBY_LIB_STAGING_DIR)/README

uninstall :
		-rm $(RUBY_LIB_STAGING_DIR)/statemap.rb
		-rm $(RUBY_LIB_STAGING_DIR)/README

clean :

realclean :

#
# CHANGE LOG
# $Log$
# Revision 1.1  2005/09/19 15:30:56  cwrapp
# Changes in release 4.2.2:
# New features:
#
# None.
#
# Fixed the following bugs:
#
# + (C#) -csharp not generating finally block closing brace.
#
