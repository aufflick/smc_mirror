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
# $Log$
# Revision 1.5  2002/02/19 19:52:45  cwrapp
# Changes in release 1.3.0:
# Add the following features:
# + 479555: Added subroutine/method calls as argument types.
# + 508878: Added %import keyword.
#
# Revision 1.3  2001/11/30 15:17:22  cwrapp
# Changes in release 1.0.2:
# + Placed the class files in Smc.jar in the net.sf.smc package.
# + Moved Java source files from smc/bin to net/sf/smc.
# + Corrected a C++ generation bug wherein arguments were written
#   to the .h file rather than the .cpp file.
#
# Revision 1.2  2001/06/16 19:52:43  cwrapp
# Changes in release 1.0, beta 7:
# Fixes the minor code generation bugs and introduces a new
# example Java program (found at examples/Java/EX7). This
# example program is also a Java applet and can be seen at
# http://smc.sourceforge.net/SmcDemo.htm.
#
# Revision 1.1  2001/05/13 16:18:49  cwrapp
# Initial add to repository.
#

VERSION=	1_3_0

STAGING_DIR=	../staging
SMC_STAGING_DIR=$(STAGING_DIR)/smc
SMC_RELEASE_DIR=$(STAGING_DIR)/smc_$(VERSION)
RELEASE_DIR=	$(STAGING_DIR)/releases

TARFILE=	$(RELEASE_DIR)/smc_$(VERSION).tar
GZIPFILE=	$(RELEASE_DIR)/smc_$(VERSION).tgz

# Create the staging directories if needed.
$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

$(SMC_STAGING_DIR) :	$(STAGING_DIR)
		mkdir $(SMC_STAGING_DIR)

$(RELEASE_DIR) :	$(STAGING_DIR)
		mkdir $(RELEASE_DIR)

# Copy all products to the staging directory.
install :	$(SMC_STAGING_DIR)
		$(MAKE) -C lib install
		$(MAKE) -C net/sf/smc install
		cp -R -f -p ./examples $(SMC_STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)/examples/CVS
		-rm -fr $(SMC_STAGING_DIR)/examples/*/CVS
		-rm -fr $(SMC_STAGING_DIR)/examples/*/*/CVS
		$(MAKE) -C misc install
		cp -f LICENSE.txt $(SMC_STAGING_DIR)
		cp -f README.txt $(SMC_STAGING_DIR)

uninstall :
		$(MAKE) -C lib uninstall
		$(MAKE) -C net/sf/smc uninstall
		$(MAKE) -C misc uninstall
		-rm -fr $(SMC_STAGING_DIR)/examples
		-rm -f $(SMC_STAGING_DIR)/LICENSE.txt
		-rm -f $(SMC_STAGING_DIR)/README.txt

clean :
		$(MAKE) -C lib clean
		$(MAKE) -C ./net/sf/smc clean

dist : 		install $(RELEASE_DIR)
		rm -fr $(SMC_RELEASE_DIR)
		cd $(STAGING_DIR); \
			mv $(SMC_STAGING_DIR) $(SMC_RELEASE_DIR); \
			tar cvf $(TARFILE) smc_$(VERSION)
		gzip $(TARFILE)
		mv $(TARFILE).gz $(GZIPFILE)

distclean :
		-rm -f $(TARFILE) $(GZIPFILE)

realclean :
		$(MAKE) -C lib realclean
		$(MAKE) -C ./net/sf/smc realclean
