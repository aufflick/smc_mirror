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
//  TcpConnection.java
//
// Description
//  Encapsulates "TCP" server connection, accepting new client
//  connections.
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
// Revision 1.1  2000/10/16 21:00:46  charlesr
// Initial version.
//

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

public final class server
    implements TcpConnectionListener
{
// Member methods

    public static void main(String[] args)
    {
        int port = -1;

        if (args.length != 1)
        {
            System.err.println("usage: server port");
            System.exit(1);
        }

        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Invalid port number - \"" +
                               args[0] +
                               "\".");
            System.exit(2);
        }

        try
        {
            server server;
            
            server = new server();

            System.out.println("(Starting execution. Hit Enter to stop.)");

            try
            {
                server.run(port);
            }
            catch (Exception jex)
            {
                jex.printStackTrace();
            }

            System.out.println("(Stopping execution.)");

            System.exit(0);
        }
        catch (Exception ex)
        {
            System.err.println(ex);
            System.exit(5);
        }
    }

    public server()
    {
        _isRunning = false;
        _opened = false;
        _my_thread = null;
        _reason = null;
        _client_list = new LinkedList();

        return;
    }

    public synchronized void clientClosed(client tcp_client)
    {
        // Remove client from list.
        _client_list.remove(tcp_client);
        
        return;
    }

    public void run(int port)
    {
        TcpServer server_socket = new TcpServer(this);
        StopThread thread = new StopThread(this);

        // Remember this thread for latter.
        _my_thread = Thread.currentThread();

        // Create a thread to watch for a keystroke.
        thread.start();

        // Open the server connection.
        System.out.print("Opening server on " +
                         Integer.toString(port) +
                         " ... ");
        _opened = false;
        server_socket.open(port);

        // Wait for open to complete.
        try
        {
            _isRunning = true;
            while (_isRunning == true)
            {
                Thread.sleep(1000);
            }
        }
        catch (InterruptedException interrupt) {}

        if (_opened == false)
        {
            System.out.println("Open failed - " + _reason + ".");
        }
        else
        {
            System.out.println("Open successful.");
            System.out.println("Listening for new connections.");

            _isRunning = true;
            while (_isRunning == true)
            {
                try
                {
                    Thread.sleep(MAX_SLEEP);
                }
                catch (InterruptedException interrupt)
                {
                    System.out.println("(Server: Interrupt caught.)");
                }
            }

            // Now that we are no longer running, close the
            // connection.
            System.out.print("Closing connection ... ");
            server_socket.close();

            // Stop all remaining accepted clients.
            client client;
            while (_client_list.size() > 0)
            {
                client = (client) _client_list.removeFirst();
                client.halt();
            }
        }

        return;
    }

    // Stop the app.
    public synchronized void halt()
    {
        _isRunning = false;

        // Wake me up in case I am sleeping.
        _my_thread.interrupt();

        return;
    }

    public void opened(TcpConnection server)
    {
        _opened = true;
        _my_thread.interrupt();
        return;
    }

    public void openFailed(String reason, TcpConnection server)
    {
        _opened = false;
        _reason = reason;
        _my_thread.interrupt();
        return;
    }

    public void halfClosed(TcpConnection client) {}

    public void closed(String reason, TcpConnection server)
    {
        System.out.println("Closed.");
        return;
    }

    public void accepted(TcpClient client, TcpServer server)
    {
        client new_client;

        System.out.println("Accepted new connection from " +
                           client.getAddress() +
                           ":" +
                           Integer.toString(client.getPort()) +
                           ".");
        new_client = new client(client, this);
        _client_list.add(new_client);

        // Start the client running in a separate thread.
        new_client.start();

        return;
    }

    public void transmitted(TcpConnection client) {}
    public void transmitFailed(String reason, TcpConnection client) {}
    public void receive(byte[] data, TcpConnection client) {}

// Member data

    private boolean _isRunning;
    private boolean _opened;
    private Thread _my_thread;
    private String _reason;

    // Keep list of accepted connections.
    private LinkedList _client_list;

    public static final long MAX_SLEEP = 0x7fffffff;

// Inner classes

    private final class StopThread
        extends Thread
    {
        private StopThread(server server)
        {
            _server = server;
        }

        public void run()
        {
            // As soon as any key is hit, stop.
            try
            {
                System.in.read();
            }
            catch (IOException io_exception)
            {}

            _server.halt();

            return;
        }

        private server _server;
    }
}
