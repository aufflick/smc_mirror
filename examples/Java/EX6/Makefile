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

TARGET=		tcpdemo
SM_SOURCES=	TcpConnection.sm

SOURCES=	$(SM_SOURCES:%.sm=%Context.java) \
		AsyncTimer.java \
		TimerListener.java \
		AsyncDatagramSocket.java \
		DatagramSocketListener.java \
		TcpSegment.java \
		TcpConnection.java \
		TcpConnectionListener.java \
		TcpClient.java \
		TcpServer.java \
		client.java \
		server.java

CLIENT_JAR=	client.jar
SERVER_JAR=	server.jar

PACKAGE_DIR=	smc_ex6

FSM_LIB_DIR=	../../../lib

JAVAC=		javac
JAVAC_FLAGS=	-d . -g -classpath $(FSM_LIB_DIR)

SMC=		java -jar ../../../bin/Smc.jar
SMC_FLAGS=	-java -g -sync

JAR=		jar
JAR_CREATE_FLAGS=	cmf
JAR_UPDATE_FLAGS=	uf

#################################################################
# Rules.
#

%Context.java : %.sm
		$(SMC) $(SMC_FLAGS) $<

%_sm.dot :	%.sm
		$(SMC) -graph -glevel 1 $<

all :		$(TARGET)

$(TARGET)     :	$(SOURCES)
		$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)
		$(JAR) $(JAR_CREATE_FLAGS) client_manifest.txt \
			$(CLIENT_JAR) $(PACKAGE_DIR)/*.class
		$(JAR) $(JAR_UPDATE_FLAGS) $(CLIENT_JAR) \
			-C $(FSM_LIB_DIR) statemap
		$(JAR) $(JAR_CREATE_FLAGS) server_manifest.txt \
			$(SERVER_JAR) $(PACKAGE_DIR)/*.class
		$(JAR) $(JAR_UPDATE_FLAGS) $(SERVER_JAR) \
			-C $(FSM_LIB_DIR) statemap

graph :		$(SM_SOURCES:%.sm=%_sm.dot)

clean :
		-rm -f *Context.java \
			$(PACKAGE_DIR)/*.class \
			$(CLIENT_JAR) \
			$(SERVER_JAR) \
			*_sm.dot
		-rm -fr $(PACKAGE_DIR)

#
# CHANGE LOG
# $Log$
# Revision 1.5  2005/05/28 13:51:24  cwrapp
# Update Java examples 1 - 7.
#
# Revision 1.1  2004/10/30 12:01:52  charlesr
# Added Graphviz rules.
#
# Revision 1.0  2003/12/14 20:17:42  charlesr
# Initial revision
#
