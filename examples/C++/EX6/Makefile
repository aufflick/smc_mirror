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
# Revision 1.3  2002/02/19 19:52:46  cwrapp
# Changes in release 1.3.0:
# Add the following features:
# + 479555: Added subroutine/method calls as argument types.
# + 508878: Added %import keyword.
#
# Revision 1.2  2001/05/09 23:40:02  cwrapp
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
# Revision 1.1.1.1  2000/08/02 12:51:01  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

CXX       = c++
# CPPFLAGS  = -D_DEBUG -g -I. -I../../../lib/C++
CPPFLAGS  = -g -I. -I../../../lib
SMC       = java -jar ../../../bin/Smc.jar
SMCFLAGS  = -c++ -g

FSMS           = 	TcpConnection.sm

COMMON_SOURCES = 	TcpConnection_sm.cpp \
			Eventloop.cpp \
			TcpSegment.cpp \
			TcpConnection.cpp \
			TcpClient.cpp \
			TcpServer.cpp

SERVER_SOURCES = 	AppServer.cpp \
			AppClient.cpp \
			server.cpp

CLIENT_SOURCES =	AppClient.cpp \
			AppServer.cpp \
			client.cpp

COMMON_OBJECTS =	$(COMMON_SOURCES:.cpp=.o)
SERVER_OBJECTS =	$(SERVER_SOURCES:.cpp=.o)
CLIENT_OBJECTS =	$(CLIENT_SOURCES:.cpp=.o)

##############

%_sm.h %_sm.cpp : %.sm
	$(SMC) $(SMCFLAGS) $<

%.o :	%.cpp
	$(CXX) $(CPPFLAGS) -c $<

all : server client

common : $(COMMON_OBJECTS)

server : common $(SERVER_OBJECTS)
	$(CXX) -g -o $@ $(COMMON_OBJECTS) $(SERVER_OBJECTS)

client : common $(CLIENT_OBJECTS)
	$(CXX) -g -o $@ $(COMMON_OBJECTS) $(CLIENT_OBJECTS)

clean :
	-rm -f server \
                client \
		$(COMMON_OBJECTS) \
		$(SERVER_OBJECTS) \
                $(CLIENT_OBJECTS) \
		*_sm.h \
		*_sm.cpp \
		core
