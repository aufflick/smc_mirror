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
#       Port to Php by Toni Arnold
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.1  2008/04/22 15:58:41  fperrad
# - add PHP language (patch from Toni Arnold)
#
#
#

SM_SOURCES=     AppClass.sm
SOURCES=        $(SM_SOURCES:%.sm=%_sm.php)

# Uncomment to turn on reflection.
# REFLECT = 	-reflect

SMC=            java -jar ../../../bin/Smc.jar
SMC_FLAGS=      -php -g $(REFLECT)
RM_F=           rm -f

all :           checkstring

%_sm.php : %.sm
		$(SMC) $(SMC_FLAGS) $<

checkstring :   $(SOURCES)

clean :
		-$(RM_F) *_sm.php

test:
		-php -q checkstring.php ""
		-php -q checkstring.php 000
		-php -q checkstring.php 00011
		-php -q checkstring.php 111
		-php -q checkstring.php 000111100
		-php -q checkstring.php 00011a1b10c0
