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
# Revision 1.1  2001/01/03 03:14:00  cwrapp
# Initial revision
#
# Revision 1.1.1.1  2000/08/02 12:51:00  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

TARGET    = sleeper

CXX       = CC
CPPFLAGS  = -g -I../../../lib
SMC       = java -jar ../../../bin/Smc.jar
SMCFLAGS  = -c++ -g

STATEMAPS = AppClass.sm
SOURCES   = $(STATEMAPS:.sm=_sm.cpp) AppClass.cpp Main.cpp
HEADERS   = $(STATEMAPS:.sm=_sm.h) AppClass.h
OBJECTS   = $(SOURCES:.cpp=.o)

##############

$(TARGET) : $(HEADERS) $(SOURCES)
	$(CXX) $(CPPFLAGS) -o $@ $(SOURCES)

%_sm.h %_sm.cpp : %.sm
	$(SMC) $(SMCFLAGS) $<

clean :
	rm -f $(TARGET) $(OBJECTS) *_sm.h *_sm.cpp
