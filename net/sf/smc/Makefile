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
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.5  2002/05/07 00:10:20  cwrapp
# Changes in release 1.3.2:
# Add the following feature:
# + 528321: Modified push transition syntax to be:
#
# 	  <transname> <state1>/push(<state2>)  {<actions>}
#
# 	  which means "transition to <state1> and then
# 	  immediately push to <state2>". The current
# 	  syntax:
#
# 	  <transname> push(<state2>)  {<actions>}
#
#           is still valid and <state1> is assumed to be "nil".
#
# No bug fixes.
#
# Revision 1.2  2001/12/14 20:10:37  cwrapp
# Changes in release 1.1.0:
# Add the following features:
# + 486786: Added the %package keyword which specifies the
#           Java package/C++ namespace/Tcl namespace
#           the SMC-generated classes will be placed.
# + 486471: The %class keyword accepts fully qualified
#           class names.
# + 491135: Add FSMContext methods getDebugStream and
#           setDebugStream.
# + 492165: Added -sync command line option which causes
#           the transition methods to be synchronized
#           (this option may only be used with -java).
#
# Revision 1.1  2001/12/03 14:14:03  cwrapp
# Changes in release 1.0.2:
# + Placed the class files in Smc.jar in the net.sf.smc package.
# + Moved Java source files from smc/bin to net/sf/smc.
# + Corrected a C++ generation bug wherein arguments were written
#   to the .h file rather than the .cpp file.
#
# Revision 1.2  2001/05/09 23:40:01  cwrapp
# Changes in release 1.0, beta 6:
# Fixes the four following bugs:
# + 416011: SMC does not properly handle pop transitions which
#           have no argument.
# + 416013: SMC generated code does not throw a
#           "Transition Undefined" exception as per Programmer's
#           Manual.
# + 416014: The initial state's Entry actions are not being
#           executed.
# + 416015: When a transition has both a guarded and an unguarded
#           definition, the Exit actions are only called when the
#           guard evaluates to true.
# + 422795: SMC -tcl abnormally terminates.
#
# Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
# Corrected Entry/Exit action semantics. Exit actions are now
# executed only by simple transitions and pop transitions.
# Entry actions are executed by simple transitions and push
# transitions. Loopback transitions do not execute either Exit
# actions or entry actions. See SMC Programmer's manual for
# more information.
#
# Revision 1.1.1.1  2001/01/03 03:13:59  cwrapp
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
# Revision 1.2  2000/09/01 15:32:03  charlesr
# Changes for v. 1.0, Beta 2:
#
# + Removed order dependency on "%start", "%class" and "%header"
#   appearance. These three tokens may now appear in any order but
#   still must appear before the first map definition.
#
# + Modified SMC parser so that it will continue after finding an
#   error. Also improved the error message quality.
#
# + Made error messages so emacs is able to parse them.
#
# Revision 1.1.1.1  2000/08/02 12:50:55  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

SM_SOURCES=	SmcLexer.sm \
		SmcParser.sm

APP_SOURCES=	SmcLexerContext.java \
		SmcParserContext.java \
		SmcArgument.java \
		SmcSimpleArg.java \
		SmcMethodArg.java \
		SmcMethodArgCpp.java \
		SmcMethodArgJava.java \
		SmcMethodArgTcl.java \
		SmcAction.java \
		SmcActionCpp.java \
		SmcActionJava.java \
		SmcActionTcl.java \
		SmcParameter.java \
		SmcParameterCpp.java \
		SmcParameterJava.java \
		SmcParameterTcl.java \
		SmcGuard.java \
		SmcGuardCpp.java \
		SmcGuardJava.java \
		SmcGuardTcl.java \
		SmcTransition.java \
		SmcTransitionCpp.java \
		SmcTransitionJava.java \
		SmcTransitionTcl.java \
		SmcState.java \
		SmcStateCpp.java \
		SmcStateJava.java \
		SmcStateTcl.java \
		SmcMap.java \
		SmcMapCpp.java \
		SmcMapJava.java \
		SmcMapTcl.java \
		SmcParseTree.java \
		SmcParseTreeCpp.java \
		SmcParseTreeJava.java \
		SmcParseTreeTcl.java \
		SmcLexer.java \
		SmcParser.java \
		Smc.java

PACKAGE_NAME=	net.sf.smc
PACKAGE_DIR=	net/sf/smc

STATEMAP_LIB_DIR=	../../../lib/Java
APP_CLASSPATH=	"$(CLASSPATH):$(STATEMAP_LIB_DIR)/statemap.jar"

INSTALL_DIR=	../../../../staging/smc/bin

JAVAC=		javac
JAVAC_FLAGS=	-d . -g -classpath $(APP_CLASSPATH)
SMC=		java -jar /Applications/smc/bin/Smc.jar
# SMC_FLAGS=	-java
SMC_FLAGS=	-java -g
JAR=		jar
JAR_CREATE_FLAGS=	cmf manifest.txt
JAR_UPDATE_FLAGS=	uf
JAR_FILE=	Smc.jar

TAGS=		jtags
TAG_FILE=	TAGS
TAG_FLAGS=	-a -o $(TAG_FILE) --members

all :		$(JAR_FILE) tags

%Context.java :	%.sm
		$(SMC) $(SMC_FLAGS) $<

$(JAR_FILE) :	$(APP_SOURCES) $(STATEMAP_LIB_DIR)/statemap/FSMContext.class $(STATEMAP_LIB_DIR)/statemap/State.class
		$(JAVAC) $(JAVAC_FLAGS) $(APP_SOURCES)
		$(JAR) $(JAR_CREATE_FLAGS) $(JAR_FILE) \
			$(PACKAGE_DIR)/*.class
		$(JAR) $(JAR_UPDATE_FLAGS) $(JAR_FILE) -C $(STATEMAP_LIB_DIR) statemap

install :	$(INSTALL_DIR) $(JAR_FILE)
		cp $(JAR_FILE) $(INSTALL_DIR)

$(INSTALL_DIR) :
		mkdir $(INSTALL_DIR)

tags :
		$(TAGS) $(TAG_FLAGS)

uninstall :
		-rm $(INSTALL_DIR)/$(JAR_FILE)

clean :
		-rm -f $(PACKAGE_DIR)/*.class \
			*Context.java \
			$(JAR_FILE)

realclean :	clean
		-rm -fr ./net $(TAG_FILE) *.flc *~
