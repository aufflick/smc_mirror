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
//  AppClient.cpp
//
// Description
//  TcpClient listener object.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.4  2002/05/07 00:29:49  cwrapp
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
// Revision 1.2  2001/05/09 23:40:02  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
//
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// $Log$
// Revision 1.4  2002/05/07 00:29:49  cwrapp
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
// Revision 1.2  2001/05/09 23:40:02  cwrapp
// Changes in release 1.0, beta 6:
// Fixes the four following bugs:
// + 416011: SMC does not properly handle pop transitions which
//           have no argument.
// + 416013: SMC generated code does not throw a
//           "Transition Undefined" exception as per Programmer's
//           Manual.
// + 416014: The initial state's Entry actions are not being
//           executed.
// + 416015: When a transition has both a guarded and an unguarded
//           definition, the Exit actions are only called when the
//           guard evaluates to true.
// + 422795: SMC -tcl abnormally terminates.
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

#include "AppClient.h"
#include "AppServer.h"
#include "Eventloop.h"
#if defined(WIN32)
#include <winsock2.h>
#include <iostream>
#include <time.h>
#else
#include <arpa/inet.h>
#endif
#include <stdlib.h>
#include <memory.h>

#if defined(WIN32)
using namespace std;
#endif

// Externally defined global variables.
extern Eventloop *Gevent_loop;

// Class variables.
int AppClient::_initFlag = 0;
const long AppClient::MIN_SLEEP_TIME = 100;
const long AppClient::MAX_SLEEP_TIME = 30000;

//---------------------------------------------------------------
// AppClient() (Public)
// Default constructor.
//
AppClient::AppClient()
: _client_socket(NULL),
  _owner(NULL),
  _messageCount(0),
  _host(NULL)
{}

//---------------------------------------------------------------
// AppClient(const char*, TcpClient&, AppServer&) (Public)
// An "accept" construct.
//
AppClient::AppClient(const char *host,
                     TcpClient& tcp_client,
                     AppServer& owner)
: _client_socket(&tcp_client),
  _owner(&owner),
  _messageCount(0),
  _host(NULL)
{
    // Store away the host name.
    _host = new char[strlen(host) + 1];
    (void) strcpy(_host, host);

    // Tell the client that this object is now listening to it.
    tcp_client.setListener(*this);

    // Since this client is already connected, set the
    // random transmit timer.
    setTransmitTimer();
    return;
} // end of AppClient::AppClient(TcpClient&, AppServer&)

//---------------------------------------------------------------
// ~AppClient() (Public)
// Destructor.
//
AppClient::~AppClient()
{
    if (_client_socket != NULL)
    {
        delete _client_socket;
        _client_socket = NULL;
    }

    if (_host != NULL)
    {
        delete[] _host;
        _host = NULL;
    }

    return;
} // end of AppClient::~AppClient()

//---------------------------------------------------------------
// getHost() const (Public)
// Return the host name.
//
const char* AppClient::getHost() const
{
    return(_host);
} // end of AppClient::getHost() const

//---------------------------------------------------------------
// open(const char*, const sockaddr_in&) (Public)
// Open a connection to the named TCP service.
//
void AppClient::open(const char *host, const sockaddr_in& address)
{
    if (_client_socket == NULL)
    {
        // Store away the host name.
        _host = new char[strlen(host) + 1];
        (void) strcpy(_host, host);

        cout << "Opening connection to "
             << host
             << ":"
             << ntohs(address.sin_port)
             << " ... ";

        // Create the client object and open it.
        _client_socket = new TcpClient(*this);
        _client_socket->open(address);
    }

    return;
} // end of AppClient::open(const char*, const sockaddr_in&)

//---------------------------------------------------------------
// close() (Public)
// Close the client connection.
//
void AppClient::close()
{
    if (_client_socket != NULL)
    {
        cout << "Closing connection to "
             << _host
             << ":"
             << ntohs((_client_socket->getServerAddress()).sin_port)
             << " ... ";

        _client_socket->doClose();
    }

    return;
} // end of AppClient::close()

//---------------------------------------------------------------
// opened(TcpConnection&) (Public)
// TCP client connection successful.
//
void AppClient::opened(TcpConnection&)
{
    const sockaddr_in& address = _client_socket->getServerAddress();

    cout << "open successful." << endl;

    // Set the transmit timer.
    setTransmitTimer();

    return;
} // end of AppClient::opened(TcpConnection&)

//---------------------------------------------------------------
// openFailed(const char*, TcpConnection&) (Public)
// TCP client connection failed.
//
void AppClient::openFailed(const char *reason, TcpConnection&)
{
    const sockaddr_in& address = _client_socket->getServerAddress();

    void socketClosed();

    cout << "open failed";
    if (reason == NULL)
    {
        cout << "." << endl;
    }
    else
    {
        cout << " - "
             << reason
             << endl;
    }

    // Let the application know that the socket is closed.
    socketClosed();

    return;
} // end of AppClient::openFailed(const char*, TcpConnection&)

//---------------------------------------------------------------
// transmitted(TcpConnection&) (Public)
// Data successfully transmitted.
//
void AppClient::transmitted(TcpConnection&)
{
    // Set the timer for the next transmission.
    setTransmitTimer();

    cout << "transmit successful." << endl;
    return;
} // end of AppClient::transmitted(TcpConnection&)

//---------------------------------------------------------------
// transmitFailed(const char*, TcpConnection&) (Public)
// Failed to send data to far-end.
//
void AppClient::transmitFailed(const char *reason, TcpConnection&)
{
    cout << "transmit failed - " << reason << "." << endl;
    return;
} // end of AppClient::transmitFailed(const char*, TcpConnection&)

//---------------------------------------------------------------
// receive(const char*, int, TcpConnection&) (Public)
// Received data from far end.
//
void AppClient::receive(const char *data,
                        int size,
                        TcpConnection&)
{
    const sockaddr_in& address = _client_socket->getServerAddress();
    char *string = new char[size + 1];

    // Turn the data into a string.
    (void) memcpy(string, data, size);
    string[size] = '\0';

    cout << "Received data from "
         << inet_ntoa(address.sin_addr)
         << ":"
         << ntohs(address.sin_port)
         << ": \""
         << string
         << "\""
         << endl;

    delete[] string;

    return;
} // end of AppClient::receive(const char*, int, TcpConnection&)

//---------------------------------------------------------------
// halfClosed(TcpConnection&) (Public)
// This far end has closed its connection. Close down this side.
//
void AppClient::halfClosed(TcpConnection&)
{
    const sockaddr_in& address = _client_socket->getServerAddress();

    cout << "Connection from "
         << _host
         << ":"
         << address.sin_port
         << " has closed its side.\nClosing connection to "
         << _host
         << ":"
         << ntohs(address.sin_port)
         << " ... ";

    // Stop the transmit timer if running.
    Gevent_loop->stopTimer("TRANSMIT_TIMER", *this);

    // When this timer expires, close this end of the connection,
    // after waiting one millisecond.
    Gevent_loop->startTimer("CLOSE_TIMER", 1, *this);

    return;
} // end of AppClient::halfClosed(TcpConnection&)

//---------------------------------------------------------------
// closed(const char*, TcpConnection&) (Public)
// This client connection is closed.
//
void AppClient::closed(const char *reason, TcpConnection&)
{
    const sockaddr_in& address =
        _client_socket->getServerAddress();

    void socketClosed();

    cout << "closed." << endl;

    // Stop the transmit timer if running.
    Gevent_loop->stopTimer("TRANSMIT_TIMER", *this);

    // If this is an accepted connection, tell the service owner
    // that this connection is gone.
    if (_owner != NULL)
    {
        _owner->clientClosed(*this);
    }
    else
    {
        // Tell the client application that this connection is
        // closed.
        socketClosed();
    }

    return;
} // end of AppClient::closed(const char*, TcpConnection&)

//---------------------------------------------------------------
// handleTimeout(const char*) (Public)
// Time to send another message.
//
void AppClient::handleTimeout(const char *name)
{
    if (strcmp(name, "TRANSMIT_TIMER") == 0)
    {
        char message[30];
        const sockaddr_in& address = _client_socket->getServerAddress();

        (void) sprintf(message,
                       "This is message #%d.",
                       ++_messageCount);

        cout << "Transmitting to "
             << _host
             << ":"
             << ntohs(address.sin_port)
             << ": \""
             << message
             << "\" ... ";

        _client_socket->transmit(message, 0, strlen(message));
    }
    else if (strcmp(name, "CLOSE_TIMER") == 0)
    {
        _client_socket->doClose();
    }

    return;
} // end of AppClient::handleTimeout(const char*)

//---------------------------------------------------------------
// setTransmitTimer() (Private)
// Set the transmit timer to expire some random time in the
// future.
//
void AppClient::setTransmitTimer()
{
    long duration;

    // Initialize the random number generator, if necessary.
    if (_initFlag == 0)
    {
        srand(time(NULL));
        _initFlag = 1;
    }

    // Set the next tranmission to some random time.
    duration = ((long) rand()) % MAX_SLEEP_TIME;
    if (duration < MIN_SLEEP_TIME)
    {
        duration = MIN_SLEEP_TIME;
    }

    Gevent_loop->startTimer("TRANSMIT_TIMER", duration, *this);

    return;
} // end of AppClient::setTransmitTimer()
