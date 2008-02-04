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
# Copyright (C) 2000 - 2005. Charles W. Rapp.
# All Rights Reserved.
#
# Contributor(s):
#   Eitan Suez contributed examples/Ant.
#   (Name withheld) contributed the C# code generation and
#   examples/C#.
#   Francois Perrad contributed the Python code generation and
#   examples/Python, Perl code generation and examples/Perl,
#   Ruby code generation and examples/Ruby, Lua code generation
#   and examples/Lua, Groovy code generation and examples/Groovy,
#   Scala code generation and examples/Scala.
#   Chris Liscio contributed the Objective-C code generation
#   and examples/ObjC.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# (See the bottom of this file.)
#

#################################################################
# Macros
#

SM_SOURCES= \
		SmcLexer.sm \
		SmcParser.sm

CORE_SOURCES= \
		$(SM_SOURCES:%.sm=%Context.java) \
		SmcMessage.java \
		SmcElement.java \
		SmcParameter.java \
		SmcAction.java \
		SmcGuard.java \
		SmcTransition.java \
		SmcState.java \
		SmcMap.java \
		SmcFSM.java \
		SmcLexer.java \
		SmcParser.java \
		SmcVisitor.java \
		SmcSyntaxChecker.java \
		SmcCodeGenerator.java \
		Smc.java

LANG_SOURCES= \
		SmcJavaGenerator.java \
		SmcHeaderGenerator.java \
		SmcCppGenerator.java \
		SmcTclGenerator.java \
		SmcVBGenerator.java \
		SmcCSharpGenerator.java \
		SmcPythonGenerator.java \
		SmcTableGenerator.java \
		SmcGraphGenerator.java \
		SmcHeaderCGenerator.java \
		SmcCGenerator.java \
		SmcLuaGenerator.java \
		SmcPerlGenerator.java \
		SmcRubyGenerator.java \
		SmcHeaderObjCGenerator.java \
		SmcObjCGenerator.java \
		SmcGroovyGenerator.java \
		SmcScalaGenerator.java

PACKAGE_NAME=	net.sf.smc
PACKAGE_DIR=	net/sf/smc

STATEMAP_LIB_DIR=	../../../lib/Java
APP_CLASSPATH=	"$(STATEMAP_LIB_DIR)/statemap.jar"

BIN_DIR=	../../../bin
INSTALL_DIR=	../../../../staging/smc/bin

JAVAC=		javac
JAVAC_FLAGS=	-d . -g -Xlint:unchecked -classpath $(APP_CLASSPATH)

SMC=		java -jar $(BIN_DIR)/Smc.jar
SMC_FLAGS=	-java -g

JAR=		jar
JAR_CREATE_FLAGS=	cmf manifest.txt
JAR_UPDATE_FLAGS=	uf
JAR_FILE=	Smc.jar

TAGS=		jtags
TAG_FILE=	TAGS
TAG_FLAGS=	-a -o $(TAG_FILE) --members

CP=			cp
MKDIR=		mkdir -p
MV=		mv
RM_F=		rm -f
RM_RF=		rm -rf
# Alternate version based on Perl (compatible Windows / *nix)
#CP=		perl -MExtUtils::Command -e cp
#MKDIR=		perl -MExtUtils::Command -e mkpath
#MV=		perl -MExtUtils::Command -e mv
#RM_F=		perl -MExtUtils::Command -e rm_f
#RM_RF=		perl -MExtUtils::Command -e rm_rf

#################################################################
# Rules
#

all :		$(JAR_FILE) tags

%Context.java :	%.sm
		$(SMC) $(SMC_FLAGS) $<

$(JAR_FILE) :	$(CORE_SOURCES) $(LANG_SOURCES) $(STATEMAP_LIB_DIR)/statemap/FSMContext.class $(STATEMAP_LIB_DIR)/statemap/State.class
		$(JAVAC) $(JAVAC_FLAGS) $(CORE_SOURCES) $(LANG_SOURCES)
		$(JAR) $(JAR_CREATE_FLAGS) $(JAR_FILE) \
			$(PACKAGE_DIR)/*.class
		$(JAR) $(JAR_UPDATE_FLAGS) $(JAR_FILE) -C $(STATEMAP_LIB_DIR) statemap

install :	$(INSTALL_DIR) $(JAR_FILE)
		-$(RM) -f $(BIN_DIR)/$(JAR_FILE).FBK
		-$(MV) $(BIN_DIR)/$(JAR_FILE) $(BIN_DIR)/$(JAR_FILE).FBK
		$(CP) $(JAR_FILE) $(BIN_DIR)
		-$(RM_F) $(INSTALL_DIR)/$(JAR_FILE)
		$(CP) $(JAR_FILE) $(INSTALL_DIR)

$(INSTALL_DIR) :
		$(MKDIR) $(INSTALL_DIR)

tags :
		$(TAGS) $(TAG_FLAGS)

uninstall :
		-$(RM_F) $(INSTALL_DIR)/$(JAR_FILE)

clean :
		-$(RM_F) $(JAR_FILE)
		-$(RM_F) *Context.java
		-$(RM_F) $(PACKAGE_DIR)/*.class

realclean :	clean
		-$(RM_F) $(TAG_FILE)
		-$(RM_F) *.flc *~
		-$(RM_RF) ./net

#
# CHANGE LOG
# $Log$
# Revision 1.16  2008/02/04 10:32:49  fperrad
# + Added Scala language generation.
#
# Revision 1.15  2007/10/16 10:22:10  fperrad
# - refactor: Makefile with variables
#
# Revision 1.14  2007/10/10 12:19:32  fperrad
# - fix Makefile (mkdir)
#
# Revision 1.13  2007/07/16 06:28:06  fperrad
# + Added Groovy generator.
#
# Revision 1.12  2007/03/31 13:27:22  cwrapp
# Version 5.0.0 check-in.
#
# Revision 1.11  2007/02/21 13:53:25  cwrapp
# Moved Java code to release 1.5.0
#
# Revision 1.10  2007/01/15 00:23:50  cwrapp
# Release 4.4.0 initial commit.
#
# Revision 1.9  2007/01/03 15:35:57  fperrad
# + Added Lua generator.
#
# Revision 1.8  2005/08/26 15:21:34  cwrapp
# Final commit for release 4.2.0. See README.txt for more information.
#
# Revision 1.7  2005/06/08 11:09:15  cwrapp
# + Updated Python code generator to place "pass" in methods with empty
#   bodies.
# + Corrected FSM errors in Python example 7.
# + Removed unnecessary includes from C++ examples.
# + Corrected errors in top-level makefile's distribution build.
#
# Revision 1.6  2005/05/28 19:28:42  cwrapp
# Moved to visitor pattern.
#
# Revision 1.4  2005/02/21 15:33:44  charlesr
# Added Francois Perrad to Contributors section for Python work.
#
# Revision 1.3  2005/02/21 14:47:04  charlesr
# Added SmcPythonGenerator.java to source files.
#
# Revision 1.2  2005/02/03 16:19:18  charlesr
# Moved SMC to the Visitor pattern. This change resulted in
# the following source files being added:
#
# + SmcMessage.java
# + SmcElement.java
# + SmcVisitor.java
# + SmcSyntaxChecker.java
# + SmcCodeGenerator.java
# + SmcHeaderGenerator.java
# + SmcCppGenerator.java
# + SmcJavaGenerator.java
# + SmcTclGenerator.java
# + SmcVBGenerator.java
# + SmcCSharpGenerator.java
# + SmcTableGenerator.java
# + SmcGraphGenerator.java
#
# Also change SmcParseTree.java to SmcFSM.java.
#
# Revision 1.1  2004/09/06 16:38:30  charlesr
# Updated for SMC v. 3.1.0.
#
# Revision 1.0  2003/12/14 20:46:25  charlesr
# Initial revision
#
