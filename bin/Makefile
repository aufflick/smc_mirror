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
# Revision 1.1  2001/01/03 03:13:59  cwrapp
# Initial revision
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

STATEMAP_LIB_DIR=	..\lib\Java
APP_CLASSPATH=	"$(CLASSPATH);$(STATEMAP_LIB_DIR)/statemap.jar"

JAVAC=		javac
JAVAC_FLAGS=	-d . -g -classpath $(APP_CLASSPATH)
SMC=		java -jar ../../Smc/bin/Smc.jar
SMC_FLAGS=	-java -g
JAR=		jar
JAR_CREATE_FLAGS=	cmf manifest.txt
JAR_UPDATE_FLAGS=	uf
JAR_FILE=	Smc.jar

all :		$(JAR_FILE)

%Context.java :	%.sm
		$(SMC) $(SMC_FLAGS) $<

$(JAR_FILE) :	$(APP_SOURCES) $(STATEMAP_LIB_DIR)/statemap/FSMContext.class $(STATEMAP_LIB_DIR)/statemap/State.class
		$(JAVAC) $(JAVAC_FLAGS) $(APP_SOURCES)
		$(JAR) $(JAR_CREATE_FLAGS) $(JAR_FILE) ./*.class
		$(JAR) $(JAR_UPDATE_FLAGS) $(JAR_FILE) -C $(STATEMAP_LIB_DIR) statemap

install :	$(JAR_FILE)
		cp ../../Smc/bin/$(JAR_FILE) ../../Smc/bin/$(JAR_FILE).BAK
		cp $(JAR_FILE) ../../Smc/bin

clean :
		-rm -f *.class *Context.java $(JAR_FILE)
