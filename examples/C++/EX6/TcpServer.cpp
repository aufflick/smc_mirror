//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//  TcpServer.cpp
//
// Description
//  TCP server connection class implementation.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.3  2002/05/07 00:29:50  cwrapp
// Changes in release 1.3.2:
// Add the following feature:
// + 528321: Modified push transition syntax to be:
//
// 	  <transname> <state1>/push(<state2>)  {<actions>}
//
// 	  which means "transition to <state1> and then
// 	  immediately push to <state2>". The current
// 	  syntax:
//
// 	  <transname> push(<state2>)  {<actions>}
//
//           is still valid and <state1> is assumed to be "nil".
//
// No bug fixes.
//
// Revision 1.1.1.2  2001/03/26 14:41:47  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//

#include "TcpServer.h"

//---------------------------------------------------------------
// TcpServer(TcpConnectionListener&) (Public)
// Create a TCP service object.
//
TcpServer::TcpServer(TcpConnectionListener& listener)
: TcpConnection(listener)
{}

//---------------------------------------------------------------
// ~TcpServer() (Public)
// Destructor.
//
TcpServer::~TcpServer()
{}

//---------------------------------------------------------------
// getPort() const (Public)
// Return this service's TCP port.
//
unsigned short TcpServer::getPort() const
{
    return(_nearAddress.sin_port);
} // end of TcpServer::getPort() const

//---------------------------------------------------------------
// getAddress() const (Public)
// Return this service's IP address.
//
unsigned long TcpServer::getAddress() const
{
    return(_nearAddress.sin_addr.s_addr);
} // end of TcpServer::getAddress() const

//---------------------------------------------------------------
// open(unsigned short) (Public)
// Open this TCP service.
//
void TcpServer::open(unsigned short port)
{
    passiveOpen(port);
    return;
} // end of TcpServer::open(unsigned short)
