#ifndef _H_TCPCONNECTION
#define _H_TCPCONNECTION

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
//  TcpConnection.h
//
// Description
//  TCP connection class header.
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

#include <sys/types.h>
#if defined(WIN32)
// #include <winbase.h>
#include <winsock2.h>
#else
#include <netinet/in.h>
#endif
#include "TcpSegment.h"
#include "TcpConnection_sm.h"
#include "TcpConnectionListener.h"
#include "InputListener.h"
#include "TimerListener.h"

class TcpConnection :
    /* implements */ public InputListener,
    /* implements */ public TimerListener
{
// Member functions.
 public:

    // Send the raw bytes to the far end client socket.
    void transmit(const char *data,
                  int offset,
                  int size);

    // Start closing this connection.
    void doClose();

    // Set an accepted client socket's listener.
    void setListener(TcpConnectionListener& listener);

    // Socket event handling methods.
    void handleReceive(int fd);
    void handleTimeout(const char *name);
    
    //-----------------------------------------------------------
    // State Machine Guards
    //

    // Is this segment from the expected source? If not, then
    // it is from an interloper.
    int isInterloper(const TcpSegment *segment) const;

    // The segment is from the expected source but does it have
    // the correct acknowledgement number? The correct number is
    // the current sequence number.
    int isValidAck(const TcpSegment *segment) const;

    //-----------------------------------------------------------
    // State Machine Actions
    //
    void openServerSocket(unsigned short port);
    void openClientSocket(const sockaddr_in *address);
    void openSuccess();
    void openFailed(const char *reason);
    void closeSocket();
    void halfClosed();
    void closed(const char *reason);
    void clearListener();
    void transmitted();
    void transmitFailed(const char *reason);
    void receive(const TcpSegment *segment);
    void sendOpenSyn(const sockaddr_in *address);
    void accept(const TcpSegment *segment);
    void accepted();
    void sendSynAck(const TcpSegment *segment);
    void sendSynAckAck(const TcpSegment *segment);
    void doSend(unsigned short flags,
                const char *data,
                int offset,
                int size,
                const TcpSegment *recv_segment);
    void startTimer(const char *name, time_t time);
    void stopTimer(const char *name);
    void setNearAddress();
    void setFarAddress(const TcpSegment *segment);
    void deleteSegment(const TcpSegment *segment);

 protected:

    // Server socket constructor.
    TcpConnection(TcpConnectionListener& listener);

    // "Accepted" socket constructor.
    TcpConnection(const sockaddr_in& near_address,
                  const sockaddr_in& far_address,
#if defined(WIN32)
                  unsigned short actual_port,
                  SOCKET udp_socket,
                  HANDLE udp_handle,
#else
                  int udp_socket,
#endif
                  int sequence_number,
                  TcpServer& server,
                  TcpConnectionListener& listener);

    virtual ~TcpConnection();

    // Open a server socket.
    void passiveOpen(unsigned short port);

    // Open a client socket.
    void activeOpen(const sockaddr_in& address);

    // An accepted client socket.
    void acceptOpen(const TcpSegment& segment);
                  
 private:

    // Increase the buffer's size.
    void expandBuffer();

#if defined(WIN32)
    // Windows doesn't bind UDP sockets to random ports
    // correctly. So this app has to do it itself.
    int doBind(int handle) const;
#else
    // Return the UDP socket's port.
    unsigned short getLocalPort(int fd) const;
#endif

    // Return this machine's address.
    unsigned long getLocalAddress() const;

    // Return the expected ack number for the given segment.
    int getAck(const TcpSegment& segment);

// Member data.
 public:

    // Constants.
    // Initial Sequence Number.
    const static unsigned long ISN;

    // Minimum timeout is 1 millisecond.
    const static int MIN_TIMEOUT;

    // Wait only so long for an ACK (in milliseconds).
    const static int ACK_TIMEOUT;

    // Wait a while before reusing this port (in milliseconds).
    const static int CLOSE_TIMEOUT;

    // Allocate the input buffer using this block size.
    const static int BUFFER_BLOCK_SIZE;

 protected:

    // Send TCP events to this listener.
    TcpConnectionListener *_listener;

    // Connected to this address.
    sockaddr_in _farAddress;

    // This socket's address.
    sockaddr_in _nearAddress;

#if defined(WIN32)
    // This socket's actual port number.
    unsigned short _actualPort;
#endif

 private:

    TcpConnectionContext _fsm;
#if defined(WIN32)
    SOCKET _udp_win_socket;
#endif
    int _udp_socket;
    unsigned long _sequence_number;

    // Read data into the following buffer.
    char *_buffer;
    int _bufferSize;

    // The server which accepted this connection. Will only be
    // set if this is an "accepted" client connection.
    TcpServer *_server;

    // Store failure messages here.
    char *_errorMessage;
};

#endif
