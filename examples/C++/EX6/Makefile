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
# Revision 1.1  2001/01/03 03:14:00  cwrapp
# Initial revision
#
# Revision 1.1.1.1  2000/08/02 12:51:01  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

CXX       = CC
# CPPFLAGS  = -D_DEBUG -g -I. -I../../../lib/C++
CPPFLAGS  = -g -I. -I../../lib/C++
SMC       = java -jar ../../bin/Smc.jar
SMCFLAGS  = -c++ -g
LIBS      = -lxnet

STATEMAPS      = 	TcpConnection.sm

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
	$(CXX) -g -o $@ $(COMMON_OBJECTS) $(SERVER_OBJECTS) $(LIBS)

client : common $(CLIENT_OBJECTS)
	$(CXX) -g -o $@ $(COMMON_OBJECTS) $(CLIENT_OBJECTS) $(LIBS)

install :
	cp *.h *.cpp *.sm README.txt Makefile ../../../../Smc/examples/C++/EX6

clean :
	-rm -f server \
                client \
		$(COMMON_OBJECTS) \
		$(SERVER_OBJECTS) \
                $(CLIENT_OBJECTS) \
		*_sm.h \
		*_sm.cpp \
		core
