#ifndef _H_TCPSEGMENT
#define _H_TCPSEGMENT

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
//  TcpSegment.h
//
// Description
//  All TCP transmissions are put into a segment.
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

#if defined(WIN32)
#include <iostream>
#include <winsock2.h>
#else
#include <iostream.h>
#include <sys/socket.h>
#include <netinet/in.h>
#endif

#if defined(WIN32)
using namespace std;
#endif

class TcpSegment
{
// Member functions.
public:

    TcpSegment(const sockaddr_in& source_address,
               const sockaddr_in& destination_address,
               unsigned long sequence_number,
               unsigned long ack_number,
               unsigned short flags,
               const char *data,
               int offset,
               int size);

    // Convert the raw bytes into a segment.
    TcpSegment(const sockaddr_in& source_address,
               const sockaddr_in& dest_address,
               const char *data,
               int size);

    // Destructor.
    ~TcpSegment();

    const sockaddr_in& getSource() const;
    const sockaddr_in& getDestination() const;
    unsigned long getSequenceNumber() const;
    unsigned long getAcknowledgeNumber() const;
    unsigned short getFlags() const;
    int getDataSize() const;
    const char* getData() const;

    // Convert this segment into raw bytes.
    void packetize(char*& data, int& size);

    friend ostream& operator<<(ostream& stream,
                               const TcpSegment& segment);

 protected:
 private:

    static char* flagsToString(unsigned short flags);
    static char* addressToString(const sockaddr_in& address);

// Member data.
 public:

    enum TcpHeaderFlags
    {
        NONE= 0x00,
        FIN = 0x01,
        SYN = 0x02,
        RST = 0x04,
        PSH = 0x08,
        ACK = 0x10,
        URG = 0x20,
        FIN_ACK = 0x11,
        SYN_ACK = 0x12,
        RST_ACK = 0x14,
        PSH_ACK = 0x18,
        FLAG_MASK = 0x3f
    };

    static const int TCP_HEADER_SIZE;

 protected:
 private:

    sockaddr_in _src_address;
    sockaddr_in _dest_address;
    unsigned long _sequence_number;
    unsigned long _ack_number;
    unsigned short _flags;
    char *_data;
    int _data_size;
};

#endif
