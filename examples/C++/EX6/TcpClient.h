#ifndef _H_TCPCLIENT
#define _H_TCPCLIENT

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
//  TcpClient.h
//
// Description
//  TCP client connection class header.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.2  2002/02/19 19:52:46  cwrapp
// Changes in release 1.3.0:
// Add the following features:
// + 479555: Added subroutine/method calls as argument types.
// + 508878: Added %import keyword.
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

#include "TcpConnection.h"

// Forward declarations.
class TcpServer;

class TcpClient: public TcpConnection
{
// Member functions.
public:

    // Create an unconnected client.
    TcpClient(TcpConnectionListener& listener);

    // Destructor.
    ~TcpClient();

    // This client is connected to this TCP service.
    const sockaddr_in& getServerAddress() const;

    // This client's TCP port.
    unsigned short getClientPort() const;

    // Open a client connection to a local TCP service.
    void open(unsigned short port);

    // Open a client connection to a remote TCP service.
    void open(const sockaddr_in& address);

protected:
private:

    // Create an "accepted" client connection. This constructor
    // may only be called by TcpConnection.
    TcpClient(const sockaddr_in& far_address,
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
              TcpConnectionListener& listener);

    // Don't allow default or copy constructors.
    TcpClient();
    TcpClient(const TcpClient&);

// Member data.
public:
protected:
private:

// Friends.
    friend class TcpConnection;
};

#endif
