

package require statemap;
source ./TcpConnection_sm.tcl

class TcpConnection {
# Member methods

    constructor {} {

    }

    destructor {

    }

    public method close {} {
        $_statemap Close;
        return -code ok;
    }

    public method handleReceive {packet async_socket} {
        set flags ;
        set address ;
        set segment ;

        _state $_trans_map($flags) $address $segment;

        return -code ok;
    }

    public method handleError {error async_socket} {
        # TODO
    }

    public method handleTimeout {transition} {
        $_statemap $transition;
        return -code ok;
    }

    protected method passiveOpen {port} {
        $_statemap PassiveOpen $port;
        return -code ok;
    }

    protected method activeOpen {address} {
        $_statemap ActiveOpen $address;
        return -code ok;
    }

    protected method acceptOpen {segment} {
        $_statemap AcceptOpen $segment;
        return -code ok;
    }

    protected method setListener {listener} {
        if {[string compare $_listener ""] != 0} {
            set retcode error;
            set retval "Socket listener already set";
        } else {
            set _listener $listener;
            set recode ok;
            set retval "";
        }

        return -code $retcode $retval;
    }

    protected method transmit {data} {
        $_statemap Transmit $data;
        return -code ok;
    }

# Member data

    protected variable _listener;
    private variable _statemap;
    private variable _sequence_number;
    private variable _ack_number;

    # The address and port to which this socket is connected.
    protected variable _address;

    # The server which accepted this connection.
    protected variable _server;

    # The Initial Sequence Number;
    private common ISN 1415531521;

    # Wait only so long for an ACK (in milliseconds).
    private common ACK_TIMEOUT 500;

    # Wait a while before reusing this port (in milliseconds).
    private common CLOSE_TIMEOUT 120000;

    # This array maps TCP flags to transitions.
    private common _trans_map;
}