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

SOURCES	= Makefile.PL MANIFEST README Changes Statemap.pm test.pl

LIB_STAGING_DIR=../../$(SMC_STAGING_DIR)/lib
PERL_LIB_STAGING_DIR=$(LIB_STAGING_DIR)/Perl

#################################################################
# Rules.
#

$(PERL_LIB_STAGING_DIR) :
		mkdir -p $(PERL_LIB_STAGING_DIR)

install :	$(PERL_LIB_STAGING_DIR) $(SOURCES)
		cp -f Makefile.PL $(PERL_LIB_STAGING_DIR)/Makefile.PL
		cp -f MANIFEST $(PERL_LIB_STAGING_DIR)/MANIFEST
		cp -f README $(PERL_LIB_STAGING_DIR)/README
		cp -f Statemap.pm $(PERL_LIB_STAGING_DIR)/Statemap.pm
		cp -f test.pl $(PERL_LIB_STAGING_DIR)/test.pl

uninstall :	$(PERL_LIB_STAGING_DIR)
		-rm $(PERL_LIB_STAGING_DIR)/Makefile.PL
		-rm $(PERL_LIB_STAGING_DIR)/MANIFEST
		-rm $(PERL_LIB_STAGING_DIR)/README
		-rm $(PERL_LIB_STAGING_DIR)/Statemap.pm
		-rm $(PERL_LIB_STAGING_DIR)/test.pl

clean :

realclean :

#
# CHANGE LOG
# $Log$
# Revision 1.2  2007/10/10 12:29:06  fperrad
# - fix Makefile (mkdir)
#
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
