#
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy
# of the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an
# "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is State Machine Compiler (SMC).
#
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2009. Charles W. Rapp.
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

# Include the official macros.
include ../../../../smc.mk

SOURCES= \
	SmcOptions.java \
	SmcCGenerator.java \
	SmcCodeGenerator.java \
	SmcCppGenerator.java \
	SmcCSharpGenerator.java \
	SmcGraphGenerator.java \
	SmcGroovyGenerator.java \
	SmcHeaderGenerator.java \
	SmcHeaderCGenerator.java \
	SmcHeaderObjCGenerator.java \
	SmcJavaGenerator.java \
	SmcJava7Generator.java \
	SmcLuaGenerator.java \
	SmcObjCGenerator.java \
	SmcPerlGenerator.java \
	SmcPhpGenerator.java \
	SmcPythonGenerator.java \
	SmcRubyGenerator.java \
	SmcScalaGenerator.java \
	SmcTableGenerator.java \
	SmcTclGenerator.java \
	SmcVBGenerator.java \
	SmcJSGenerator.java

PACKAGE_NAME=	net.sf.smc.generator
PACKAGE_DIR=	$(subst .,/,$(PACKAGE_NAME))

JAVAC_FLAGS=	-d . -g -source 1.7 -target 1.7 -Xlint:unchecked -classpath ../model/SmcModel.jar

JAR_CREATE_FLAGS=	cf
JAR_FILE=	SmcGenerator.jar

INSTALL_DIR=	../../../../../staging/smc/lib/Java

CP=			cp
MKDIR=		mkdir -p
MV=		mv
RM_F=		rm -f
RM_RF=		rm -rf

#################################################################
# Rules
#

all :		$(JAR_FILE)

$(JAR_FILE) :	$(SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)
		$(JAR) $(JAR_CREATE_FLAGS) $(JAR_FILE) \
			$(PACKAGE_DIR)/*.class

install :	$(INSTALL_DIR) $(JAR_FILE)
		-$(RM_F) $(INSTALL_DIR)/$(JAR_FILE)
		$(CP) $(JAR_FILE) $(INSTALL_DIR)

uninstall :
		-$(RM_F) $(INSTALL_DIR)/$(JAR_FILE)

clean :
		-$(RM_F) $(JAR_FILE)
		-$(RM_F) $(PACKAGE_DIR)/*.class

realclean :	clean
		-$(RM_F) *.flc *~
		-$(RM_RF) ./net

#
# CHANGE LOG
# $Log$
# Revision 1.5  2015/02/16 21:43:09  cwrapp
# SMC v. 6.5.0
#
# SMC - The State Machine Compiler v. 6.5.0
#
# Major changes:
#
# (Java)
#     Added a new "-java7" target language. This version represents
#     the FSM as a transition table. The transition table maps the
#     current state and the transition to a
#     java.lang.invoke.MethodHandle. The transition is executed by
#     calling MethodHandle.invokeExact, which is only slightly
#     slower than a compiled method call.
#
#     The -java7 generated code is compatible with -java generated
#     code. This allows developers to switch between the two
#     without changing application code.
#
#     NOTE: -java7 requires Java 1.7 or latter to run.
#
#
# Minor changes:
#
# (None.)
#
#
# Bug Fixes:
#
# (Objective-C)
#     Incorrect initWithOwner body generated. Same fundamental
#     problem as SF bug 200. See below.
#     (SF bug 198)
#
# (Website)
#     Corrected broken link in FAQ page.
#     (SF bug 199)
#
# (C++)
#     Corrected the invalid generated FSM class name.
#     (SF bug 200)
#
# (C)
#     EXIT_STATE() #define macro not generated.
#     (SF bug 201)
#
# (Manual)
#     Corrected examples which showed %fsmclass and %map set to the
#     same name. This is invalid for most target languages since
#     that would mean the nested map class would have the same name
#     as the containing FSM class.
#
#
#
# ++++++++++++++++++++++++++++++++++++++++
#
# If you have any questions or bugs, please surf
# over to http://smc.sourceforge.net and check out
# the discussion and bug forums. Note: you must be
# a SourceForge member to add articles or bugs. You
# do not have to be a member to read posted
# articles or bugs.
#
# Revision 1.4  2011/02/14 21:29:56  nitin-nizhawan
# corrected some build errors
#
# Revision 1.2  2009/11/24 20:42:39  cwrapp
# v. 6.0.1 update
#
# Revision 1.1  2009/03/01 18:20:42  cwrapp
# Preliminary v. 6.0.0 commit.
#
