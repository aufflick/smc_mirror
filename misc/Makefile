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
# Revision 1.3  2002/02/19 19:52:49  cwrapp
# Changes in release 1.3.0:
# Add the following features:
# + 479555: Added subroutine/method calls as argument types.
# + 508878: Added %import keyword.
#
# Revision 1.2  2001/11/30 15:17:22  cwrapp
# Changes in release 1.0.2:
# + Placed the class files in Smc.jar in the net.sf.smc package.
# + Moved Java source files from smc/bin to net/sf/smc.
# + Corrected a C++ generation bug wherein arguments were written
#   to the .h file rather than the .cpp file.
#
# Revision 1.1  2001/05/13 16:20:47  cwrapp
# Initial add to repository.
#

STAGING_DIR =	../../staging/smc/misc

# Copy all products to the staging directory.
install : $(STAGING_DIR)
		cp -f ./smc.ico $(STAGING_DIR)

$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

uninstall : 
		-rm -f $(STAGING_DIR)/smc.ico

clean :

realclean :
