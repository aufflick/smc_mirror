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
# Copyright (C) 2000 Charles W. Rapp.
# All Rights Reserved.
#
# Contributor(s):
#       Port to C by Francois Perrad, francois.perrad@gadz.org
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.4  2008/01/22 08:45:03  fperrad
# - add a target 'graph'
#
# Revision 1.3  2007/10/12 08:55:00  fperrad
# - refactor: Makefile with variables
#
# Revision 1.2  2007/08/05 13:39:13  cwrapp
# Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more information.
#
# Revision 1.1  2005/06/16 18:00:15  fperrad
# Added C examples 1 - 4
#
#

#################################################################
# Macros.
#

TARGET    = ./checkstring

CXX       = gcc
CPPFLAGS  = -g -I../../../lib/C
SMC       = java -jar ../../../bin/Smc.jar
SMCFLAGS  = -c -g
RM_F      = rm -f

SM_SOURCES= AppClass.sm
SOURCES   = $(SM_SOURCES:.sm=_sm.c) \
            AppClass.c main.c
HEADERS   = $(SM_SOURCES:.sm=_sm.h) \
            AppClass.h
OBJECTS   = $(SOURCES:.c=.o)

#################################################################
# Rules.
#

%_sm.h %_sm.c : %.sm
		$(SMC) $(SMCFLAGS) $<

%_sm.dot : %.sm
		$(SMC) -graph -glevel 1 $<

all :		$(TARGET) test

$(TARGET) :	$(HEADERS) $(SOURCES)
		$(CXX) $(CPPFLAGS) -o $@ $(SOURCES)

test :		$(TARGET)
		-$(TARGET) ""
		-$(TARGET) 000
		-$(TARGET) 00011
		-$(TARGET) 111
		-$(TARGET) 000111100
		-$(TARGET) 00011a1b10c0

graph :		$(SM_SOURCES:%.sm=%_sm.dot)

clean :
		-$(RM_F) $(TARGET)
		-$(RM_F) $(OBJECTS)
		-$(RM_F) *_sm.h
		-$(RM_F) *_sm.c
		-$(RM_F) *_sm.dot
