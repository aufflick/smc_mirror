#ifndef _H_APPCLIENT
#define _H_APPCLIENT

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
//  AppClient.h
//
// Description
//  Encapsulates a TcpClient object.
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

#include "TcpClient.h"
#include "TcpServer.h"
#include "TcpConnectionListener.h"

// Foward declarations.
class AppServer;

class AppClient :
    /* implements */ public TcpConnectionListener,
    /* implements */ public TimerListener
{
// Member functions.
public:

    // Default constructor.
    AppClient();

    // Create a client around an accepted connection.
    AppClient(const char *host,
              TcpClient& client,
              AppServer& owner);

    ~AppClient();

    const char* getHost() const;

    // Create a TCP client object and open a connection to the
    // specified service.
    void open(const char *host,
              const sockaddr_in& address);

    // Close the TCP service.
    void close();

    // TCP connection listener callback methods.
    void opened(TcpConnection& connection);
    void openFailed(const char *reason,
                    TcpConnection& connection);
    void transmitted(TcpConnection& connection);
    void transmitFailed(const char *reason,
                        TcpConnection& connection);
    void receive(const char *data,
                 int size,
                 TcpConnection& connection);
    void halfClosed(TcpConnection& connection);
    void closed(const char *reason,
                TcpConnection& connection);

    // Clients never receive this event.
    void accepted(TcpClient&, TcpServer&) {};

    // Timer listener callback method.
    void handleTimeout(const char *name);

protected:
private:

    // Randomly set the transmit time.
    void setTransmitTimer();

// Member data.
public:
protected:
private:

    // The TCP service itself.
    TcpClient *_client_socket;

    // If this was an accepted client connection, _owner points
    // to the parent TCP service. This will be NULL otherwise.
    AppServer *_owner;

    // The host to which we are connected.
    char *_host;

    // Every time a message is sent, increment this counter.
    int _messageCount;

    // Initialize the random number generator once per process.
    static int _initFlag;

    // Class constants. Minimum and Maximum transmit timeout.
    const static long MIN_SLEEP_TIME;
    const static long MAX_SLEEP_TIME;
};

#endif
