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
//  TcpSegment.cpp
//
// Description
//  Converts between raw bytes and segment objects.
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

#include "TcpSegment.h"
#include <memory.h>
#include <string.h>
#include <stdio.h>
#if !defined(WIN32)
#include <netdb.h>
#include <arpa/inet.h>
#endif

// Static member data declarations.
const int TcpSegment::TCP_HEADER_SIZE = 16;

//---------------------------------------------------------------
// TcpSegment(const sockaddr_in&, ...) (Public)
// Create an outbound segment.
//
TcpSegment::TcpSegment(const sockaddr_in& source_address,
                       const sockaddr_in& destination_address,
                       unsigned long sequence_number,
                       unsigned long ack_number,
                       unsigned short flags,
                       const char *data,
                       int offset,
                       int size)
{
    (void) memcpy(&_src_address,
                  &source_address,
                  sizeof(source_address));
    (void) memcpy(&_dest_address,
                  &destination_address,
                  sizeof(destination_address));
    _sequence_number = sequence_number;
    _ack_number = ack_number;
    _flags = flags;

    // Copy the data.
    _data = new char[size];
    _data_size = size;
    (void) memcpy(_data, (data + offset), size);

    return;
} // end of TcpSegment::TcpSegment(const sockaddr_in&, ...)

//---------------------------------------------------------------
// TcpSegment(const sockaddr_in&, ...) (Public)
// Create an inbound segment.
//
TcpSegment::TcpSegment(const sockaddr_in& source_address,
                       const sockaddr_in& dest_address,
                       const char *data,
                       int size)
{
    unsigned short srcPort;
    unsigned short destPort;
    unsigned long  sequence;
    unsigned long  ack;
    unsigned short flags;
    unsigned short data_size;

    srcPort     = ((((unsigned short) data[ 0]) & 0x00ff) <<  8) |
                   (((unsigned short) data[ 1]) & 0x00ff);
    destPort    = ((((unsigned short) data[ 2]) & 0x00ff) <<  8) |
                   (((unsigned short) data[ 3]) & 0x00ff);
    sequence    = ((((unsigned long)  data[ 4]) & 0x000000ff) << 24) |
                  ((((unsigned long)  data[ 5]) & 0x000000ff) << 16) |
                  ((((unsigned long)  data[ 6]) & 0x000000ff) <<  8) |
                   (((unsigned long)  data[ 7]) & 0x000000ff);
    ack         = ((((unsigned long)  data[ 8]) & 0x000000ff) << 24) |
                  ((((unsigned long)  data[ 9]) & 0x000000ff) << 16) |
                  ((((unsigned long)  data[10]) & 0x000000ff) <<  8) |
                   (((unsigned long)  data[11]) & 0x000000ff);
    flags       = ((((unsigned short) data[12]) & 0x00ff) <<  8) |
                   (((unsigned short) data[13]) & 0x00ff);
    data_size   = ((((unsigned short) data[14]) & 0x00ff) <<  8) |
                   (((unsigned short) data[15]) & 0x00ff);

    // Set the source address.
    _src_address.sin_family = AF_INET;
    _src_address.sin_port = ntohs(srcPort);
    _src_address.sin_addr.s_addr = source_address.sin_addr.s_addr;

    // Set the destination address.
    _dest_address.sin_family = AF_INET;
    _dest_address.sin_port = ntohs(destPort);
    _dest_address.sin_addr.s_addr = dest_address.sin_addr.s_addr;

    _sequence_number = sequence;
    _ack_number = ack;
    _flags = flags;
    _data_size = data_size;
    

    if (_data_size == 0)
    {
        _data = NULL;
    }
    else
    {
        _data = new char[_data_size];
        (void) memcpy(_data,
                      &(data[TCP_HEADER_SIZE]),
                      _data_size);
    }

    return;
} // end of TcpSegment::TcpSegment(const char*, int)

//---------------------------------------------------------------
// ~TcpSegment() (Public)
// Destructor.
//
TcpSegment::~TcpSegment()
{
    if (_data != NULL)
    {
        delete[] _data;
        _data = NULL;
        _data_size = 0;
    }
} // end of TcpSegment::~TcpSegment()

//---------------------------------------------------------------
// getSource() const (Public)
// Return segment's source address.
//
const sockaddr_in& TcpSegment::getSource() const
{
    return(_src_address);
} // end of TcpSegment::getSource() const

//---------------------------------------------------------------
// getDestination() const (Public)
// Return segment's destination address.
//
const sockaddr_in& TcpSegment::getDestination() const
{
    return(_dest_address);
} // end of TcpSegment::getDestination() const

//---------------------------------------------------------------
// getSequenceNumber() const (Public)
// Return segment's sequence number.
//
unsigned long TcpSegment::getSequenceNumber() const
{
    return(_sequence_number);
} // end of TcpSegment::getSequenceNumber() const

//---------------------------------------------------------------
// getAcknowledgeNumber() const (Public)
// Return segment's acknowledge number.
//
unsigned long TcpSegment::getAcknowledgeNumber() const
{
    return(_ack_number);
} // end of TcpSegment::getAcknowledgeNumber() const

//---------------------------------------------------------------
// getFlags() const (Public)
// Return segment's flags.
//
unsigned short TcpSegment::getFlags() const
{
    return(_flags);
} // end of TcpSegment::getFlags() const

//---------------------------------------------------------------
// getDataSize() const (Public)
// Return segment's data size.
//
int TcpSegment::getDataSize() const
{
    return(_data_size);
} // end of TcpSegment::getDataSize() const

//---------------------------------------------------------------
// getData() const (Public)
// Return segment's data.
//
const char* TcpSegment::getData() const
{
    return(_data);
} // end of TcpSegment::getData() const

//---------------------------------------------------------------
// packetize(char*&, int&) (Public)
// Convert this TCP segment into raw bytes.
//
void TcpSegment::packetize(char*& data, int& size)
{
    unsigned short srcPort;
    unsigned short destPort;
    unsigned long sequence;
    unsigned long ack;
    unsigned short flags;
    unsigned short data_size;
    
    // The segment's size is the TCP header size + data size.
    // (TCP options are *not* supported).
    size = TCP_HEADER_SIZE + _data_size;

    data = new char[size];

    // Get data in network byte order.
    srcPort = htons(_src_address.sin_port);
    destPort = htons(_dest_address.sin_port);
    sequence = _sequence_number;
    ack = _ack_number;
    flags = _flags;
    data_size = _data_size;

    // Fill in the TCP header.
    data[ 0] = (char) ((  srcPort & 0xff00)     >>  8);
    data[ 1] = (char)  (  srcPort & 0x00ff);
    data[ 2] = (char) (( destPort & 0xff00)     >>  8);
    data[ 3] = (char)  ( destPort & 0x00ff);
    data[ 4] = (char) (( sequence & 0xff000000) >> 24);
    data[ 5] = (char) (( sequence & 0x00ff0000) >> 16);
    data[ 6] = (char) (( sequence & 0x0000ff00) >>  8);
    data[ 7] = (char)  ( sequence & 0x000000ff);
    data[ 8] = (char) ((      ack & 0xff000000) >> 24);
    data[ 9] = (char) ((      ack & 0x00ff0000) >> 16);
    data[10] = (char) ((      ack & 0x0000ff00) >>  8);
    data[11] = (char)  (      ack & 0x000000ff);
    data[12] = (char) ((    flags & 0xff00)     >>  8);
    data[13] = (char)  (    flags & 0x00ff);
    data[14] = (char) ((data_size & 0xff00)     >>  8);
    data[15] = (char)  (data_size & 0x00ff);

    // Copy in the data.
    if (_data_size > 0)
    {
        (void) memcpy((data + TCP_HEADER_SIZE),
                      _data,
                      _data_size);
    }

    return;
} // end of TcpSegment::packetize(char&*, int&)

//---------------------------------------------------------------
// flagsToString(unsigned short) (Static Private)
// Make a string representation of the TCP header flags.
//
char* TcpSegment::flagsToString(unsigned short flags)
{
    char separator[3] = "{";
    char *retval;
    char *cptr;

    (void) strcpy(separator, "{");
    retval = new char[31];
    (void) memset(retval, 0, 31);
    cptr = retval;
    if ((flags & FIN) == FIN)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "FIN");
        cptr += 3;
        (void) strcpy(separator, ", ");
    }

    if ((flags & SYN) == SYN)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "SYN");
        cptr += 3;
        (void) strcpy(separator, ", ");
    }

    if ((flags & RST) == RST)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "RST");
        cptr += 3;
        (void) strcpy(separator, ", ");
    }

    if ((flags & PSH) == PSH)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "PSH");
        cptr += 3;
        (void) strcpy(separator, ", ");
    }

    if ((flags & ACK) == ACK)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "ACK");
        cptr += 3;
        (void) strcpy(separator, ", ");
    }

    if ((flags & URG) == URG)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "URG");
        cptr += 3;
    }

    if ((flags & FLAG_MASK) == 0)
    {
        (void) strcpy(cptr, separator);
        cptr += strlen(separator);
        (void) strcpy(cptr, "UNK");
        cptr += 3;
    }

    *cptr = '}';

    return(retval);
} // end of TcpSegment::flagsToString(unsigned short)

//---------------------------------------------------------------
// addressToString(const sockaddr_in&) (Static Private)
// Generate a string representation of an IP address.
//
char* TcpSegment::addressToString(const sockaddr_in& address)
{
    struct hostent *hostEntry;
    char *dotted_addr;
    char port[6];
    char *retval;
    char *cptr;
    unsigned short udp_port;

    retval = new char[512];
    (void) memset(retval, 0, 512);
    cptr = retval;
    hostEntry = gethostbyaddr((char *) &(address.sin_addr),
                              sizeof(address.sin_addr),
                              AF_INET);
    (void) strcpy(cptr,
                  (hostEntry == NULL ?
                   "<no hostname>" :
                   hostEntry->h_name));
    cptr += strlen(cptr);
    *cptr++ = '(';
    dotted_addr = inet_ntoa(address.sin_addr);
    (void) strcpy(cptr, dotted_addr);
    cptr += strlen(dotted_addr);
    (void) strcpy(cptr, "):");
    cptr += 2;
    udp_port = ntohs(address.sin_port);
    sprintf(port, "%d", udp_port);
    (void) strcpy(cptr, port);

    return(retval);
} // end of TcpSegment::addressToString(const sockaddr_in&)

//---------------------------------------------------------------
// operator<<(ostream&, const TcpSegment&) (Routine)
// Print this segment out to the stream.
//
ostream& operator<<(ostream& stream, const TcpSegment& segment)
{
    char *srcAddrStr,
         *destAddrStr,
         *flagStr;

    srcAddrStr = TcpSegment::addressToString(segment._src_address);
    destAddrStr = TcpSegment::addressToString(segment._dest_address);
    flagStr = TcpSegment::flagsToString(segment._flags);

    stream << "\t       Source: "
           << srcAddrStr
           << "\n\t  Destination: "
           << destAddrStr
           << "\n\t   Sequence #: "
           << segment._sequence_number
           << "\n\tAcknowledge #: "
           << segment._ack_number
           << "\n\t        Flags: "
           << flagStr
           << " (0x" << hex << segment._flags << dec << ")"
           << "\n\t    Data size: "
           << segment._data_size;

    delete[] srcAddrStr;
    delete[] destAddrStr;
    delete[] flagStr;

    return(stream);
} // end of operator<<(ostream&, const TcpSegment&)
