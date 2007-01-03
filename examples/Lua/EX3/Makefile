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
#       Port to Lua by Francois Perrad, francois.perrad@gadz.org
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.1  2007/01/03 15:42:33  fperrad
# + Added Lua examples 1 - 3.
#
#
#

SM_SOURCES=     AppClass.sm
SOURCES=        $(SM_SOURCES:%.sm=%_sm.lua)

# Uncomment to turn on reflection.
# REFLECT = 	-reflect

SMC=            java -jar ../../../bin/Smc.jar
SMC_FLAGS=      -lua -g $(REFLECT)

export LUA_PATH=./?.lua;../../../lib/Lua/?.lua

all :           checkstring

%_sm.lua : %.sm
		$(SMC) $(SMC_FLAGS) $<

checkstring :   $(SOURCES)

clean :
		-rm *_sm.lua

test:
		-lua checkstring.lua ""
		-lua checkstring.lua 00
		-lua checkstring.lua 1c
		-lua checkstring.lua c0
		-lua checkstring.lua abcba
		-lua checkstring.lua 110010c010011
		-lua checkstring.lua 110010c110010
