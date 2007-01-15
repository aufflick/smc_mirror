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
# Copyright (C) 2007. Charles W. Rapp.
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

TARGET=		Traffic

SM_SOURCES=	VEHICLE.SM \
		stoplight.sm

SOURCES=	$(SM_SOURCES:%.sm=%_sm.tcl) \
		$(SM_SOURCES:%.SM=%_sm.tcl)

# Uncomment to turn on reflection.
# REFLECT = 	-reflect

SMC=		java -jar ../../../bin/Smc.jar
SMC_FLAGS=	-tcl -g $(REFLECT)

#################################################################
# Rules.
#

%_sm.tcl : %.sm
		$(SMC) $(SMC_FLAGS) $<

%_sm.tcl : %.SM
		$(SMC) $(SMC_FLAGS) $<

all :		$(TARGET) test

$(TARGET) :	$(SOURCES)

test :		$(SOURCES)
		wish TRAFFIC.TCL

clean :
		-rm *_sm.tcl

#
# CHANGE LOG
# $Log$
# Revision 1.1  2007/01/15 16:58:59  cwrapp
# Release 4.4.0 second commit.
#
