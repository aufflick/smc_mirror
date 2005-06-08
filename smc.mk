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
# Copyright (C) 2000 - 2004. Charles W. Rapp.
# All Rights Reserved.
#
# Port to Python by Francois Perrad, francois.perrad@gadz.org
# Copyright 2004, Francois Perrad.
# All Rights Reserved.
# 
# Contributor(s):
#   Eitan Suez contributed examples/Ant.
#   (Name withheld) contributed the C# code generation and
#   examples/C#.
#   Francois Perrord contributed the Python code generator and
#   examples/Python.
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

VERSION=	4_0_0

STAGING_DIR=	../staging
SMC_STAGING_DIR=$(STAGING_DIR)/smc
SMC_RELEASE_DIR=$(STAGING_DIR)/smc_$(VERSION)
RELEASE_DIR=	$(STAGING_DIR)/releases

#################################################################
# Rules.
#

# Create the staging directories if needed.
$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

$(SMC_STAGING_DIR) :	$(STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)
		mkdir $(SMC_STAGING_DIR)

#
# CHANGE LOG
# $Log$
# Revision 1.2  2005/06/08 11:08:58  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.1  2005/05/28 19:41:44  cwrapp
# Update for SMC v. 4.0.0.
#
