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
# Revision 1.2  2002/02/13 02:45:23  cwrapp
# Changes in release 1.2.0:
# Added the following features:
# + 484889: "pop" transitions can now return arguments
#           along with a transition name.
# + 496625: Multiple .sm files may be specified in the
#           compile command.
#
# Fixed the following bugs:
# + 496692: Fixed the %package C++ code generation.
# + 501157: Transition debug output was still hardcoded
#           to System.err. This has been corrected so
#           that FSMContext._debug_stream is used.
#
# Revision 1.1  2001/06/26 22:16:24  cwrapp
# Changes in release 1.0.0:
# Checking in code for first production release.
# If SMC should crash, critical information are printed out along
# with instructions explaining where to send that information.
#

TARGET=		telephone

SM_SOURCES=	Telephone.sm

SOURCES=	$(SM_SOURCES:%.sm=%Context.java) \
		Telephone.java

JAVAC=		javac
JAVAC_FLAGS=	-g -classpath ../../../lib/statemap.jar
SMC=		java -jar ../../../bin/Smc.jar
SMC_FLAGS=	-java -g

all :		telephone

%Context.java : %.sm
		$(SMC) $(SMC_FLAGS) $<

$(TARGET) :	$(SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)

clean :
		-rm -f *.class *Context.java *.flc
