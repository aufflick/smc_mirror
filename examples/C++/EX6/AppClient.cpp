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
// The Original Code is State Map Compiler (SMC).
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
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
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

#if defined(WIN32)
using namespace std;
#endif

// Externally defined global variables.
extern Eventloop *Gevent_loop;

// Class variables.
int AppClient::_initFlag = 0;
const long AppClient::MIN_SLEEP_TIME = 100;
const long AppClient::MAX_SLEEP_TIME = 300000;

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
void AppClient::receive(const char *data, int, TcpConnection&)
{
    const sockaddr_in& address = _client_socket->getServerAddress();

    cout << "Received data from "
         << inet_ntoa(address.sin_addr)
         << ":"
         << ntohs(address.sin_port)
         << ": \""
         << data
         << "\""
         << endl;

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

        _client_socket->transmit(message, 0, (strlen(message) + 1));
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
