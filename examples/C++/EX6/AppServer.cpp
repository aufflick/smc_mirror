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
//  AppServer.cpp
//
// Description
//  Encapsulates a TcpServer object.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//

#include "AppServer.h"
#include "AppClient.h"
#if defined(WIN32)
#include <winsock2.h>
#else
#include <arpa/inet.h>
#endif

//---------------------------------------------------------------
// AppServer() (Public)
// Default constructor.
//
AppServer::AppServer()
: _server(NULL),
  _clientList(NULL)
{}

//---------------------------------------------------------------
// ~AppServer() (Public)
// Destructor. Close and delete all accepted client connections.
//
AppServer::~AppServer()
{
    ClientEntry *client,
                *nextClient;

    // Close and delete the server if necessary.
    if (_server != NULL)
    {
        _server->doClose();
        delete _server;
        _server = NULL;
    }

    for (client = _clientList;
         client != NULL;
         client = nextClient)
    {
        nextClient = client->getNext();
        (client->getClient())->close();
        delete client;
    }
    _clientList = NULL;

    return;
} // end of AppServer::~AppServer()

//---------------------------------------------------------------
// open(unsigned short) (Public)
// Create a TCP service object and have it start opening.
//
void AppServer::open(unsigned short port)
{
    if (_server == NULL)
    {
        _server = new TcpServer(*this);
        _server->open(port);
    }

    return;
} // end of AppServer::open(unsigned short)

//---------------------------------------------------------------
// close() (Public)
// Have the TCP service start the close process.
//
void AppServer::close()
{
    if (_server != NULL)
    {
        _server->doClose();
    }

    return;
} // end of AppServer::close()

//---------------------------------------------------------------
// clientClosed(const AppClient&) (Public)
// One of the accepted client connections is now closed. Remove
// it from the client list.
//
void AppServer::clientClosed(const AppClient& client)
{
    ClientEntry **entry,
                 *remove_entry;

    for (entry = &_clientList;
         *entry != NULL;
         entry = &((*entry)->_next))
    {
        if ((*entry)->getClient() == &client)
        {
            remove_entry = *entry;
            *entry = (*entry)->getNext();
            remove_entry->setNext(NULL);

            delete remove_entry->getClient();
            delete remove_entry;

            break;
        }
    }

    return;
} // end of AppServer::clientClosed(const AppClient&)

//---------------------------------------------------------------
// opened(TcpConnection&) (Public)
// TCP service successfully opened.
//
void AppServer::opened(TcpConnection&)
{
    sockaddr_in address;

    address.sin_family = AF_INET;
    address.sin_port = _server->getPort();
    address.sin_addr.s_addr = _server->getAddress();

    cout << "Service opened on "
         << inet_ntoa(address.sin_addr)
         << ":"
         << address.sin_port
         << "."
         << endl;

    return;
} // end of AppServer::opened(TcpConnection&)

//---------------------------------------------------------------
// openFailed(const char*, TcpConnection&) (Public)
// TCP service failed to open.
//
void AppServer::openFailed(const char *reason, TcpConnection&)
{
    void socketClosed();

    cout << "Service failed to open on port "
         << _server->getPort()
         << ", reason: "
         << reason
         << endl;

    // Tell the application that the TCP service is closed.
    socketClosed();

    return;
} // end of AppServer::openFailed(const char*, TcpConnection&)

//---------------------------------------------------------------
// closed(const char*, TcpConnection&) (Public)
// TCP service is now closed.
//
void AppServer::closed(const char *reason, TcpConnection&)
{
    void socketClosed();

    cout << "Service closed: " 
         << reason
         << endl;

    // Tell the application that the TCP service is closed.
    socketClosed();

    return;
} // end of AppServer::closed(TcpConnection&)

//---------------------------------------------------------------
// accepteded(TcpClient&, TcpServer&) (Public)
// TCP service has accepted a new connection.
//
void AppServer::accepted(TcpClient& client, TcpServer& server)
{
    AppClient *new_client;
    ClientEntry *new_entry;
    const sockaddr_in& address = client.getServerAddress();
    char *host;

    host = inet_ntoa(address.sin_addr);

    cout << "Accepted new connection from "
         << host
         << ":"
         << address.sin_port
         << "."
         << endl;

    // Create a new client handler and put in the client list.
    new_client = new AppClient(host, client, *this);
    new_entry = new ClientEntry(*new_client);
    new_entry->setNext(_clientList);
    _clientList = new_entry;

    return;
} // end of AppServer::accepted(TcpClient&, TcpServer&)
