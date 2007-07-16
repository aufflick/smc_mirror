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
# Copyright (C) 2000 - 2006. Charles W. Rapp.
# All Rights Reserved.
#
# Contributor(s):
#       Port to Groovy by Francois Perrad, francois.perrad@gadz.org
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

SM_SOURCES=     AppClass.sm
SOURCES=	$(SM_SOURCES:%.sm=%Context.groovy)

# Uncomment to turn on reflection.
REFLECT = 	-reflect

SMC=            java -jar ../../../bin/Smc.jar
SMC_FLAGS=	-groovy -g $(REFLECT)

GROOVY=		groovy
GROOVY_FLAGS=	-classpath ../../../lib/Groovy/statemap.jar

RM_F=		rm -f

#################################################################
# Rules.
#

all :		checkstring test

%Context.groovy : %.sm
		$(SMC) $(SMC_FLAGS) $<

checkstring :   $(SOURCES)

test :		$(OBJECTS)
		-$(GROOVY) $(GROOVY_FLAGS) checkstring ""
		-$(GROOVY) $(GROOVY_FLAGS) checkstring 00
		-$(GROOVY) $(GROOVY_FLAGS) checkstring 1c
		-$(GROOVY) $(GROOVY_FLAGS) checkstring 0c
		-$(GROOVY) $(GROOVY_FLAGS) checkstring abcba
		-$(GROOVY) $(GROOVY_FLAGS) checkstring 110010c010011
		-$(GROOVY) $(GROOVY_FLAGS) checkstring 110010c110010

clean :
		-$(RM_F) ./*Context.groovy

#
# CHANGE LOG
# $Log$
# Revision 1.1  2007/07/16 06:31:31  fperrad
# + Added Groovy examples 1 - 3.
#
#
