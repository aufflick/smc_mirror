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
# Revision 1.1  2001/05/13 16:19:34  cwrapp
# Initial add to repository.
#

INSTALL_DIRS=	C++_install \
		Java_install \
		Tcl_install

UNINSTALL_DIRS=	C++_uninstall \
		Java_uninstall \
		Tcl_uninstall

CLEAN_DIRS=	C++_clean \
		Java_clean \
		Tcl_clean

STAGING_DIR=	../../staging/smc/lib

# Copy all products to the staging directory.
install : $(STAGING_DIR) $(INSTALL_DIRS)

$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

C++_install :
		$(MAKE) -C C++ install

Java_install :
		$(MAKE) -C Java install

Tcl_install :
		$(MAKE) -C Tcl install

uninstall : $(UNINSTALL_DIRS)

C++_uninstall :
		$(MAKE) -C C++ uninstall

Java_uninstall :
		$(MAKE) -C Java uninstall

Tcl_uninstall :
		$(MAKE) -C Tcl uninstall

clean : $(CLEAN_DIRS)

C++_clean :
		$(MAKE) -C C++ clean

Java_clean :
		$(MAKE) -C Java clean

Tcl_clean :
		$(MAKE) -C Tcl clean
