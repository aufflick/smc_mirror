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

INSTALL_DIRS=	lib_install \
		bin_install \
		examples_install \
		misc_install

UNINSTALL_DIRS=	lib_uninstall \
		bin_uninstall \
		examples_uninstall \
		misc_uninstall

CLEAN_DIRS=	lib_clean \
		bin_clean

STAGING_DIR =	../staging

SMC_STAGING_DIR=$(STAGING_DIR)/smc

TARFILE=	$(STAGING_DIR)/releases/smc_1_0b7.tar
GZIPFILE=	$(STAGING_DIR)/releases/smc_1_0b7.tgz

# Copy all products to the staging directory.
install : $(STAGING_DIR) $(SMC_STAGING_DIR) $(INSTALL_DIRS)
		cp -f LICENSE.txt $(SMC_STAGING_DIR)
		cp -f README.txt $(SMC_STAGING_DIR)

$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

$(SMC_STAGING_DIR) :
		mkdir $(SMC_STAGING_DIR)

lib_install :
		$(MAKE) -C lib install

bin_install :
		$(MAKE) -C bin install

examples_install :
		cp -R -f -p ./examples $(SMC_STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)/examples/CVS
		-rm -fr $(SMC_STAGING_DIR)/examples/*/CVS
		-rm -fr $(SMC_STAGING_DIR)/examples/*/*/CVS

misc_install :
		$(MAKE) -C misc install

uninstall : $(UNINSTALL_DIRS)
		-rm -f $(SMC_STAGING_DIR)/LICENSE.txt
		-rm -f $(SMC_STAGING_DIR)/README.txt

lib_uninstall :
		$(MAKE) -C lib uninstall

bin_uninstall :
		$(MAKE) -C bin uninstall

examples_uninstall : 
		-rm -fr $(SMC_STAGING_DIR)/examples

misc_uninstall :
		$(MAKE) -C misc uninstall

clean : $(CLEAN_DIRS)

lib_clean :
		$(MAKE) -C lib clean

bin_clean :
		$(MAKE) -C bin clean

dist : 		install
		cd $(STAGING_DIR); tar cvf $(TARFILE) smc
		gzip $(TARFILE)
		mv $(TARFILE).gz $(GZIPFILE)

distclean :
		-rm -f $(TARFILE) $(GZIPFILE)
