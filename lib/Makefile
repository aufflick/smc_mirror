# 
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy
# of the License at http://www.mozilla.org/MPL/
# 
# Software distributed under the License is distributed on an
# "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
# 
# The Original Code is State Machine Compiler (SMC).
# 
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2000 - 2005. Charles W. Rapp.
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
include ../smc.mk

LIB_STAGING_DIR=../$(SMC_STAGING_DIR)/lib

#################################################################
# Rules.
#

$(LIB_STAGING_DIR) :
		$(MKDIR) $(LIB_STAGING_DIR)

# Copy all products to the staging directory.
install :       $(LIB_STAGING_DIR)
		$(MAKE) -C C install
		$(MAKE) -C C++ install
		$(MAKE) -C CSharp install
		$(MAKE) -C Groovy install
		$(MAKE) -C Java install
		$(MAKE) -C Lua install
		$(MAKE) -C ObjC install
		$(MAKE) -C Perl install
		$(MAKE) -C Python install
		$(MAKE) -C Ruby install
		$(MAKE) -C Scala install
		$(MAKE) -C Tcl install
		-$(MAKE) -C VB install

uninstall :
		$(MAKE) -C C uninstall
		$(MAKE) -C C++ uninstall
		$(MAKE) -C CSharp uninstall
		$(MAKE) -C Groovy uninstall
		$(MAKE) -C Java uninstall
		$(MAKE) -C Lua uninstall
		$(MAKE) -C ObjC uninstall
		$(MAKE) -C Perl uninstall
		$(MAKE) -C Python uninstall
		$(MAKE) -C Ruby uninstall
		$(MAKE) -C Scala uninstall
		$(MAKE) -C Tcl uninstall
		$(MAKE) -C VB uninstall

clean :
		$(MAKE) -C C clean
		$(MAKE) -C C++ clean
		$(MAKE) -C CSharp clean
		$(MAKE) -C Groovy clean
		$(MAKE) -C Java clean
		$(MAKE) -C Lua clean
		$(MAKE) -C ObjC clean
		$(MAKE) -C Perl clean
		$(MAKE) -C Python clean
		$(MAKE) -C Ruby clean
		$(MAKE) -C Scala clean
		$(MAKE) -C Tcl clean
		$(MAKE) -C VB clean

realclean :
		$(MAKE) -C C realclean
		$(MAKE) -C C++ realclean
		$(MAKE) -C CSharp realclean
		$(MAKE) -C Groovy realclean
		$(MAKE) -C Java realclean
		$(MAKE) -C Lua realclean
		$(MAKE) -C ObjC realclean
		$(MAKE) -C Perl realclean
		$(MAKE) -C Python realclean
		$(MAKE) -C Ruby realclean
		$(MAKE) -C Scala realclean
		$(MAKE) -C Tcl realclean
		$(MAKE) -C VB realclean

#
# CHANGE LOG
# $Log$
# Revision 1.12  2008/02/04 10:50:56  fperrad
# + Added Scala library
#
# Revision 1.11  2008/01/14 19:59:23  cwrapp
# Release 5.0.2 check-in.
#
# Revision 1.10  2007/01/15 17:19:11  cwrapp
# Release 4.4.0 third commit.
#
# Revision 1.9  2007/01/15 00:23:50  cwrapp
# Release 4.4.0 initial commit.
#
# Revision 1.8  2006/09/16 15:04:28  cwrapp
# Initial v. 4.3.3 check-in.
#
# Revision 1.7  2005/08/26 15:21:33  cwrapp
# Final commit for release 4.2.0. See README.txt for more information.
#
# Revision 1.6  2005/06/08 11:09:14  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.5  2005/05/28 18:44:10  cwrapp
# Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
#
# Revision 1.3  2005/02/21 19:07:03  charlesr
# Including standard SMC macros.
#
# Revision 1.2  2004/09/06 16:35:06  charlesr
# Updated for SMC v. 3.1.0.
#
# Revision 1.1  2004/05/31 13:44:03  charlesr
# Updated macros and rules for VB.net.
#
# Revision 1.0  2003/12/14 20:41:37  charlesr
# Initial revision
#
