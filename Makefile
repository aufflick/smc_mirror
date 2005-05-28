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

# Include the official macros.
include ./smc.mk

STAGING_DIR=	../staging
SMC_STAGING_DIR=$(STAGING_DIR)/smc
SMC_RELEASE_DIR=$(STAGING_DIR)/smc_$(VERSION)
RELEASE_DIR=	$(STAGING_DIR)/releases

TAR_FILE=	$(RELEASE_DIR)/smc_$(VERSION).tar
TAR_GZ_FILE=	$(TAR_FILE:.tar=.tar.gz)
GZIP_FILE=	$(TAR_FILE:.tar=.tgz)

SRC_TAR_FILE=	staging/releases/SmcSrc_$(VERSION).tar
SRC_TAR_GZ_FILE=$(SRC_TAR_FILE:.tar=.tar.gz)
SRC_GZIP_FILE=	$(SRC_TAR_FILE:.tar=.tgz)
SRC_TAR_LIST=	./smc/tar_list.txt

#################################################################
# Rules.
#

# Create the staging directories if needed.
$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

$(SMC_STAGING_DIR) :	$(STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)
		mkdir $(SMC_STAGING_DIR)

# Copy all products to the staging directory.
install :	$(SMC_STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)/*
		$(MAKE) -C lib install
		$(MAKE) -C net/sf/smc install
		cp -R -f -p ./examples $(SMC_STAGING_DIR)
		-rm -fr $(SMC_STAGING_DIR)/examples/*/*/RCS
		-rm -fr $(SMC_STAGING_DIR)/examples/Ant/*/*/RCS
		-rm -fr $(SMC_STAGING_DIR)/examples/.DS_Store
		-rm -fr $(SMC_STAGING_DIR)/examples/*/.DS_Store
		-rm -fr $(SMC_STAGING_DIR)/examples/*/*/.DS_Store
		$(MAKE) -C misc install
		cp -f LICENSE.txt $(SMC_STAGING_DIR)
		cp -f README.txt $(SMC_STAGING_DIR)
		chmod 444 $(SMC_STAGING_DIR)/bin/Smc.jar

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

smc_dist :	$(SMC_STAGING_DIR)
		-rm -f $(TAR_FILE) $(TAR_GZ_FILE) $(GZIP_FILE)
		(cd $(STAGING_DIR)/..; \
		    mv $(SMC_STAGING_DIR) $(SMC_RELEASE_DIR); \
		    tar cvf $(TAR_FILE) smc_$(VERSION))
		gzip $(TAR_FILE)
		mv $(TAR_GZ_FILE) $(GZIP_FILE)

src_dist :	$(SMC_RELEASE_DIR)
		(cd ..; \
		    rm -f $(SRC_TAR_FILE) \
			$(SRC_TAR_GZ_FILE) \
			$(SRC_GZIP_FILE); \
		    tar cvmpfT $(SRC_TAR_FILE) $(SRC_TAR_LIST); \
		    gzip $(SRC_TAR_FILE); \
		    mv $(SRC_TAR_GZ_FILE) $(SRC_GZIP_FILE))

dist : 		install smc_dist src_dist

distclean :
		-rm -f $(TAR_FILE) $(TAR_GZ_FILE) $(GZIP_FILE)
		(cd ..; \
		    rm -f $(SRC_TAR_FILE) \
			  $(SRC_TAR_GZ_FILE) \
			  $(SRC_GZIP_FILE))

realclean :
		$(MAKE) -C lib realclean
		$(MAKE) -C ./net/sf/smc realclean

#
# CHANGE LOG
# $Log$
# Revision 1.6  2005/05/28 19:41:44  cwrapp
# Update for SMC v. 4.0.0.
#
# Revision 1.6  2004/10/30 15:34:17  charlesr
# Corrected dist rule and associated macros.
#
# Revision 1.5  2004/10/08 18:44:07  charlesr
# Changed version to 3.1.2.
#
# Revision 1.4  2004/10/02 19:59:18  charlesr
# Changed version to 3.1.1.
#
# Revision 1.3  2004/09/06 15:03:12  charlesr
# Updated for SMC v. 3.1.0.
#
# Revision 1.2  2004/05/31 15:18:29  charlesr
# Added rule for generating source distribution.
#
# Revision 1.1  2004/05/30 21:37:06  charlesr
# Changed version to 3.0.0.
#
# Revision 1.0  2003/12/14 21:07:53  charlesr
# Initial revision
#
