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
#       Port to Python by Francois Perrad, francois.perrad@gadz.org
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.4  2008/01/22 08:46:39  fperrad
# - add a target 'graph'
#
# Revision 1.3  2007/10/12 08:55:02  fperrad
# - refactor: Makefile with variables
#
# Revision 1.2  2007/01/03 14:40:12  fperrad
# + Added -reflect option for Perl, Python and Ruby code generation
#
# Revision 1.1  2005/05/28 17:48:30  cwrapp
# Added Python examples 1 - 4 and 7.
#
#

#################################################################
# Macros.
#

SM_SOURCES=     Telephone.sm
SOURCES=        $(SM_SOURCES:%.sm=%_sm.py)

# Uncomment to turn on reflection.
# REFLECT = 	-reflect

SMC=            java -jar ../../../bin/Smc.jar
SMC_FLAGS=      -python -g $(REFLECT)
RM_F=           rm -f

#################################################################
# Rules.
#

%_sm.py : %.sm
		$(SMC) $(SMC_FLAGS) $<

%_sm.dot : %.sm
		$(SMC) -graph -glevel 1 $<

all :           telephone

telephone :   $(SOURCES)

graph :		$(SM_SOURCES:%.sm=%_sm.dot)

clean :
		-$(RM_F) *_sm.py
		-$(RM_F) *.pyc
		-$(RM_F) *_sm.dot
