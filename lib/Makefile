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
# Revision 1.4  2002/05/07 00:19:09  cwrapp
# Changes in release 1.3.2:
# Add the following feature:
# + 528321: Modified push transition syntax to be:
#
# 	  <transname> <state1>/push(<state2>)  {<actions>}
#
# 	  which means "transition to <state1> and then
# 	  immediately push to <state2>". The current
# 	  syntax:
#
# 	  <transname> push(<state2>)  {<actions>}
#
#           is still valid and <state1> is assumed to be "nil".
#
# No bug fixes.
#
# Revision 1.2  2001/11/30 15:17:22  cwrapp
# Changes in release 1.0.2:
# + Placed the class files in Smc.jar in the net.sf.smc package.
# + Moved Java source files from smc/bin to net/sf/smc.
# + Corrected a C++ generation bug wherein arguments were written
#   to the .h file rather than the .cpp file.
#
# Revision 1.1  2001/05/13 16:19:34  cwrapp
# Initial add to repository.
#

STAGING_DIR=	../../staging/smc/lib

# Copy all products to the staging directory.
install : $(STAGING_DIR)
		$(MAKE) -C C++ install
		$(MAKE) -C Java install
		$(MAKE) -C Tcl install

$(STAGING_DIR) :
		mkdir $(STAGING_DIR)

uninstall :
		$(MAKE) -C C++ uninstall
		$(MAKE) -C Java uninstall
		$(MAKE) -C Tcl uninstall

clean :
		$(MAKE) -C C++ clean
		$(MAKE) -C Java clean
		$(MAKE) -C Tcl clean

realclean :
		$(MAKE) -C C++ realclean
		$(MAKE) -C Java realclean
		$(MAKE) -C Tcl realclean
