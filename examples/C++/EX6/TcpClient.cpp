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
//  TcpClient.cpp
//
// Description
//  TCP client connection class implementation.
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
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
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

#include "TcpClient.h"
#if defined (WIN32)
#include <winsock2.h>
#else
#  if defined(__sun)
#include <sys/systeminfo.h>
#  else
#include <unistd.h>
#  endif
#include <netdb.h>
#endif

// Const declarations.
const static long MAX_HOSTNAME_LEN = 257;

//---------------------------------------------------------------
// TcpClient(TcpConnectionListener&) (Public)
// Create an unconnected TCP client.
//
TcpClient::TcpClient(TcpConnectionListener& listener)
: TcpConnection(listener)
{}

//---------------------------------------------------------------
// ~TcpClient() (Public)
// Destructor.
//
TcpClient::~TcpClient()
{}

//---------------------------------------------------------------
// getServerAddress() const (Public)
// This client is connected to this TCP service.
//
const sockaddr_in& TcpClient::getServerAddress() const
{
    return(_farAddress);
} // end of TcpClient::getServerAddress() const

//---------------------------------------------------------------
// getClientPort() const (Public)
// This client's TCP port.
//
unsigned short TcpClient::getClientPort() const
{
    return(_nearAddress.sin_port);
} // end of TcpClient::getClientPort() const

//---------------------------------------------------------------
// open(unsigned short) (Public)
// Open a client connection to a local TCP service.
//
void TcpClient::open(unsigned short port)
{
    sockaddr_in address;
    char hostname[MAX_HOSTNAME_LEN];
    hostent *hostentry;

    // Get the local address.
#if defined(__sun)
    (void) sysinfo(SI_HOSTNAME, hostname, MAX_HOSTNAME_LEN);
#else
    (void) gethostname(hostname, MAX_HOSTNAME_LEN);
#endif
    hostentry = gethostbyname(hostname);

    // Fill in the destination address.
    address.sin_port = port;
    (void) memcpy(&(address.sin_addr.s_addr),
                  hostentry->h_addr_list[0],
                  4);

    activeOpen(address);

    return;
} // end of TcpClient::open(unsigned short)

//---------------------------------------------------------------
// open(const sockaddr_in&) (Public)
// Open a client connection to a remove TCP service.
//
void TcpClient::open(const sockaddr_in& address)
{
    activeOpen(address);
    return;
} // end of TcpClient::open(const sockaddr_in&)

//---------------------------------------------------------------
// TcpClient(const sockaddr_in&, ...) (Private)
// Create an "accepted" client connection.
//
TcpClient::TcpClient(const sockaddr_in& far_address,
                     const sockaddr_in& near_address,
#if defined(WIN32)
                     unsigned short actual_port,
                     SOCKET udp_socket,
                     HANDLE udp_handle,
#else
                     int udp_socket,
#endif
                     unsigned long sequence_number,
                     TcpServer& server,
                     TcpConnectionListener& listener)
: TcpConnection(far_address,
                near_address,
#if defined(WIN32)
                actual_port,
                udp_socket,
                udp_handle,
#else
                udp_socket,
#endif
                sequence_number,
                server,
                listener)
{}
