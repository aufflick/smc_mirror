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
# Copyright (C) 2000 - 2004. Charles W. Rapp.
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

SM_SOURCES=     AppClass.sm
SOURCES=        $(SM_SOURCES:%.sm=%Context.java) \
                AppClass.java \
                checkstring.java

JAVAC=          javac
JAVAC_FLAGS=    -g -classpath ../../../lib/statemap.jar

SMC=            java -jar ../../../bin/Smc.jar
SMC_FLAGS=      -java -g

#################################################################
# Rules.
#

%Context.java : %.sm
		$(SMC) $(SMC_FLAGS) $<

%_sm.dot :	%.sm
		$(SMC) -graph -glevel 1 $<

all :           checkstring

checkstring :   $(SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)

graph :		$(SM_SOURCES:%.sm=%_sm.dot)

clean :
		-rm -f *.class *Context.java *_sm.dot

#
# CHANGE LOG
# $Log$
# Revision 1.4  2005/05/28 13:51:24  cwrapp
# Update Java examples 1 - 7.
#
# Revision 1.1  2004/10/30 12:01:21  charlesr
# Added Graphviz rules.
#
# Revision 1.0  2003/12/14 19:58:50  charlesr
# Initial revision
#
# Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
#
# ----------------------------------------------------------------------
# SMC - The State Map Compiler
# Version: 1.0, Beta 3
#
# SMC compiles state map descriptions into a target object oriented
# language. Currently supported languages are: C++, Java and [incr Tcl].
# SMC finite state machines have such features as:
# + Entry/Exit actions for states.
# + Transition guards
# + Transition arguments
# + Push and Pop transitions.
# + Default transitions. 
# ----------------------------------------------------------------------
#
# Revision 1.1.1.1  2000/08/02 12:51:02  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#
