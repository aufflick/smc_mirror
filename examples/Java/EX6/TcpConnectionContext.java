
import java.net.InetAddress;


public final class TcpConnectionContext
    extends statemap.FSMContext
{
    public TcpConnectionContext(TcpConnection owner)
    {
        super();

        _owner = owner;
        _trans_queue = new java.util.LinkedList();
        setState(MainMap.CLOSED);
    }

    public TcpConnectionState getState()
        throws java.lang.NullPointerException
    {
        if (_state == null)
        {
            throw(new java.lang.NullPointerException("Current state not set"));
        }

        return((TcpConnectionState) _state);
    }

    public TcpConnection getOwner()
    {
        return(_owner);
    }

    public void ACK(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("ACK", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void AcceptOpen(TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[2];
        params = new Class[2];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = segment;
        params[1] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("AcceptOpen", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void AckTimeout()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("AckTimeout", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void ActiveOpen(InetAddress address, int port)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[3];
        params = new Class[3];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;

        entry = new statemap.FSMContext.TransEntry("ActiveOpen", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void Close()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("Close", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void CloseAckTimeout()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("CloseAckTimeout", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void CloseTimeout()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("CloseTimeout", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void ConnAckTimeout()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("ConnAckTimeout", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void FIN(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("FIN", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void FIN_ACK(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("FIN_ACK", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void OpenFailed(String reason)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[2];
        params = new Class[2];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = reason;
        params[1] = String.class;

        entry = new statemap.FSMContext.TransEntry("OpenFailed", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void Opened()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("Opened", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void Opened(InetAddress address, int port)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[3];
        params = new Class[3];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;

        entry = new statemap.FSMContext.TransEntry("Opened", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void PSH(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("PSH", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void PSH_ACK(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("PSH_ACK", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void PassiveOpen(int port)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[2];
        params = new Class[2];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = new Integer(port);
        params[1] = int.class;

        entry = new statemap.FSMContext.TransEntry("PassiveOpen", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void RST(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("RST", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void RST_ACK(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("RST_ACK", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void SYN(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("SYN", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void SYN_ACK(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("SYN_ACK", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void TransAckTimeout()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[1];
        params = new Class[1];

        args[0] = this;
        params[0] = this.getClass();

        entry = new statemap.FSMContext.TransEntry("TransAckTimeout", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void Transmit(byte[] data, int offset, int size)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = data;
        params[1] = byte[].class;
        args[2] = new Integer(offset);
        params[2] = int.class;
        args[3] = new Integer(size);
        params[3] = int.class;

        entry = new statemap.FSMContext.TransEntry("Transmit", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void UNDEF(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("UNDEF", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    public void URG(InetAddress address, int port, TcpSegment segment)
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        args = new Object[4];
        params = new Class[4];

        args[0] = this;
        params[0] = this.getClass();
        args[1] = address;
        params[1] = InetAddress.class;
        args[2] = new Integer(port);
        params[2] = int.class;
        args[3] = segment;
        params[3] = TcpSegment.class;

        entry = new statemap.FSMContext.TransEntry("URG", args, params);
        _trans_queue.addLast(entry);

        if (_state != null)
        {
            dispatchTransitions();
        }

        return;
    }

    private void dispatchTransitions()
    {
        statemap.FSMContext.TransEntry entry;
        String method_name;
        Class[] params;
        Object[] args;
        java.lang.reflect.Method method;

        while(_trans_queue.size() > 0)
        {
            entry = (statemap.FSMContext.TransEntry) _trans_queue.removeFirst();
            method_name = entry.getMethodName();
            args = entry.getArgs();
            params = entry.getParameters();
            try
            {
                method =
                    _state.getClass().getDeclaredMethod(method_name,
                                                        params);
                method.invoke(_state, args);
                entry.free();
            }
            catch (Exception jex) {}
        }
    }

// Member data.

    private TcpConnection _owner;
    private java.util.LinkedList _trans_queue;

    protected static abstract class TcpConnectionState
        extends statemap.State
    {
        protected TcpConnectionState(String name)
        {
            super(name);
        }

        protected void Entry(TcpConnectionContext s) {}
        protected void Exit(TcpConnectionContext s) {}

        protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void AcceptOpen(TcpConnectionContext s, TcpSegment segment) {}
        protected void AckTimeout(TcpConnectionContext s) {}
        protected void ActiveOpen(TcpConnectionContext s, InetAddress address, int port) {}
        protected void Close(TcpConnectionContext s) {}
        protected void CloseAckTimeout(TcpConnectionContext s) {}
        protected void CloseTimeout(TcpConnectionContext s) {}
        protected void ConnAckTimeout(TcpConnectionContext s) {}
        protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void FIN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void OpenFailed(TcpConnectionContext s, String reason) {}
        protected void Opened(TcpConnectionContext s) {}
        protected void Opened(TcpConnectionContext s, InetAddress address, int port) {}
        protected void PSH(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void PSH_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void PassiveOpen(TcpConnectionContext s, int port) {}
        protected void RST(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void RST_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void SYN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void SYN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void TransAckTimeout(TcpConnectionContext s) {}
        protected void Transmit(TcpConnectionContext s, byte[] data, int offset, int size) {}
        protected void UNDEF(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
        protected void URG(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment) {}
    }

    protected static abstract class MainMap
    {
        private static MainMap_Default.MainMap_CLOSED CLOSED;
        private static MainMap_Default.MainMap_ServiceOpening ServiceOpening;
        private static MainMap_Default.MainMap_ClientOpening ClientOpening;
        private static MainMap_Default.MainMap_LISTEN LISTEN;
        private static MainMap_Default.MainMap_SYN_RCVD SYN_RCVD;
        private static MainMap_Default.MainMap_SYN_SENT SYN_SENT;
        private static MainMap_Default.MainMap_ESTABLISHED ESTABLISHED;
        private static MainMap_Default.MainMap_Transmitting Transmitting;
        private static MainMap_Default.MainMap_CLOSE_WAIT CLOSE_WAIT;
        private static MainMap_Default.MainMap_LAST_ACK LAST_ACK;
        private static MainMap_Default.MainMap_FIN_WAIT_1 FIN_WAIT_1;
        private static MainMap_Default.MainMap_FIN_WAIT_2 FIN_WAIT_2;
        private static MainMap_Default.MainMap_CLOSING CLOSING;
        private static MainMap_Default.MainMap_TIME_WAIT TIME_WAIT;
        private static MainMap_Default Default;

        static
        {
            CLOSED = new MainMap_Default.MainMap_CLOSED("MainMap.CLOSED");
            ServiceOpening = new MainMap_Default.MainMap_ServiceOpening("MainMap.ServiceOpening");
            ClientOpening = new MainMap_Default.MainMap_ClientOpening("MainMap.ClientOpening");
            LISTEN = new MainMap_Default.MainMap_LISTEN("MainMap.LISTEN");
            SYN_RCVD = new MainMap_Default.MainMap_SYN_RCVD("MainMap.SYN_RCVD");
            SYN_SENT = new MainMap_Default.MainMap_SYN_SENT("MainMap.SYN_SENT");
            ESTABLISHED = new MainMap_Default.MainMap_ESTABLISHED("MainMap.ESTABLISHED");
            Transmitting = new MainMap_Default.MainMap_Transmitting("MainMap.Transmitting");
            CLOSE_WAIT = new MainMap_Default.MainMap_CLOSE_WAIT("MainMap.CLOSE_WAIT");
            LAST_ACK = new MainMap_Default.MainMap_LAST_ACK("MainMap.LAST_ACK");
            FIN_WAIT_1 = new MainMap_Default.MainMap_FIN_WAIT_1("MainMap.FIN_WAIT_1");
            FIN_WAIT_2 = new MainMap_Default.MainMap_FIN_WAIT_2("MainMap.FIN_WAIT_2");
            CLOSING = new MainMap_Default.MainMap_CLOSING("MainMap.CLOSING");
            TIME_WAIT = new MainMap_Default.MainMap_TIME_WAIT("MainMap.TIME_WAIT");
            Default = new MainMap_Default("MainMap.Default");
        }

    }

    protected static class MainMap_Default
        extends TcpConnectionState
    {
        protected MainMap_Default(String name)
        {
            super(name);

        }
        protected void PassiveOpen(TcpConnectionContext s, int port)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.PassiveOpen(int port)");
            }

            (s.getState()).Exit(s);

            TcpConnectionState endState = s.getState();

            s.clearState();
            (s.getOwner()).openFailed("already open");
            s.setState(endState);
            (s.getState()).Entry(s);

            return;
        }

        protected void ActiveOpen(TcpConnectionContext s, InetAddress address, int port)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.ActiveOpen(InetAddress address, int port)");
            }

            (s.getState()).Exit(s);

            TcpConnectionState endState = s.getState();

            s.clearState();
            (s.getOwner()).openFailed("already open");
            s.setState(endState);
            (s.getState()).Entry(s);

            return;
        }

        protected void Transmit(TcpConnectionContext s, byte[] data, int offset, int size)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.Transmit(byte[] data, int offset, int size)");
            }

            (s.getState()).Exit(s);

            TcpConnectionState endState = s.getState();

            s.clearState();
            (s.getOwner()).transmitFailed("connection not established");
            s.setState(endState);
            (s.getState()).Entry(s);

            return;
        }

        protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.FIN(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void SYN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.SYN(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void PSH(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.PSH(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.ACK(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void URG(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.URG(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void FIN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.FIN_ACK(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void SYN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.SYN_ACK(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void PSH_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.PSH_ACK(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {
                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void UNDEF(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.UNDEF(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            if ((s.getOwner()).isInterloper(address, port))
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            else
            {

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).send(TcpSegment.RST, null, 0, 0, address, port, segment);
                (s.getOwner()).closed("connection reset");
                s.setState(endState);
                (s.getState()).Entry(s);
            }
            return;
        }

        protected void RST(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.RST(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            s.clearState();
            (s.getOwner()).closed("connection reset by peer");
            s.setState(MainMap.CLOSED);
            (s.getState()).Entry(s);

            return;
        }

        protected void RST_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.RST_ACK(InetAddress address, int port, TcpSegment segment)");
            }

            (s.getState()).Exit(s);
            s.clearState();
            (s.getOwner()).closed("connection reset by peer");
            s.setState(MainMap.CLOSED);
            (s.getState()).Entry(s);

            return;
        }

        protected void ConnAckTimeout(TcpConnectionContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.ConnAckTimeout");
            }

            (s.getState()).Exit(s);
            (s.getState()).Entry(s);

            return;
        }

        protected void TransAckTimeout(TcpConnectionContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.TransAckTimeout");
            }

            (s.getState()).Exit(s);
            (s.getState()).Entry(s);

            return;
        }

        protected void CloseAckTimeout(TcpConnectionContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.CloseAckTimeout");
            }

            (s.getState()).Exit(s);
            (s.getState()).Entry(s);

            return;
        }

        protected void CloseTimeout(TcpConnectionContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default.CloseTimeout");
            }

            (s.getState()).Exit(s);
            (s.getState()).Entry(s);

            return;
        }


        protected void AcceptOpen(TcpConnectionContext s, TcpSegment segment)
        {
            Default(s);
            return;
        }

        protected void OpenFailed(TcpConnectionContext s, String reason)
        {
            Default(s);
            return;
        }

        protected void Opened(TcpConnectionContext s)
        {
            Default(s);
            return;
        }

        protected void Opened(TcpConnectionContext s, InetAddress address, int port)
        {
            Default(s);
            return;
        }

        protected void Close(TcpConnectionContext s)
        {
            Default(s);
            return;
        }

        protected void AckTimeout(TcpConnectionContext s)
        {
            Default(s);
            return;
        }
        protected void Default(TcpConnectionContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : MainMap.Default");
            }

            return;
        }

        private static final class MainMap_CLOSED
            extends MainMap_Default
        {
            private MainMap_CLOSED(String name)
            {
                super(name);
            }

            protected void AcceptOpen(TcpConnectionContext s, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSED.AcceptOpen(TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).sendAcceptSynAck(segment);
                s.setState(MainMap.SYN_RCVD);
                (s.getState()).Entry(s);

                return;
            }

            protected void ActiveOpen(TcpConnectionContext s, InetAddress address, int port)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSED.ActiveOpen(InetAddress address, int port)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).openClientSocket(address, port);
                s.setState(MainMap.ClientOpening);
                (s.getState()).Entry(s);

                return;
            }

            protected void PassiveOpen(TcpConnectionContext s, int port)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSED.PassiveOpen(int port)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).openServerSocket(port);
                s.setState(MainMap.ServiceOpening);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_ServiceOpening
            extends MainMap_Default
        {
            private MainMap_ServiceOpening(String name)
            {
                super(name);
            }

            protected void OpenFailed(TcpConnectionContext s, String reason)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ServiceOpening.OpenFailed(String reason)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).openFailed(reason);
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void Opened(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ServiceOpening.Opened");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).openSuccess();
                s.setState(MainMap.LISTEN);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_ClientOpening
            extends MainMap_Default
        {
            private MainMap_ClientOpening(String name)
            {
                super(name);
            }

            protected void OpenFailed(TcpConnectionContext s, String reason)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ClientOpening.OpenFailed(String reason)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).openFailed(reason);
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void Opened(TcpConnectionContext s, InetAddress address, int port)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ClientOpening.Opened(InetAddress address, int port)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.SYN, null, 0, 0, address, port, null);
                s.setState(MainMap.SYN_SENT);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_LISTEN
            extends MainMap_Default
        {
            private MainMap_LISTEN(String name)
            {
                super(name);
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.LISTEN.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void RST(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.LISTEN.RST(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                (s.getState()).Entry(s);

                return;
            }

            protected void SYN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.LISTEN.SYN(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);

                TcpConnectionState endState = s.getState();

                s.clearState();
                (s.getOwner()).accept(address, port, segment);
                s.setState(endState);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_SYN_RCVD
            extends MainMap_Default
        {
            private MainMap_SYN_RCVD(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("ACK_TIMER");
                return;
            }

            protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_RCVD.ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).accepted();
                    s.setState(MainMap.ESTABLISHED);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.ACK(s, address, port, segment);
                }

                return;
            }

            protected void AckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_RCVD.AckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).openFailed("acknowledge timeout");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_RCVD.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.FIN, null, 0, 0, null, -1, null);
                s.setState(MainMap.FIN_WAIT_1);
                (s.getState()).Entry(s);

                return;
            }

            protected void RST(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_RCVD.RST(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).clearListener();
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_SYN_SENT
            extends MainMap_Default
        {
            private MainMap_SYN_SENT(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("CONN_ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("CONN_ACK_TIMER");
                return;
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_SENT.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void ConnAckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_SENT.ConnAckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).openFailed("acknowledge timeout");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

            protected void SYN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.SYN_SENT.SYN_ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).setDestinationPort(segment);
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    (s.getOwner()).openSuccess();
                    s.setState(MainMap.ESTABLISHED);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.SYN_ACK(s, address, port, segment);
                }

                return;
            }

        }

        private static final class MainMap_ESTABLISHED
            extends MainMap_Default
        {
            private MainMap_ESTABLISHED(String name)
            {
                super(name);
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ESTABLISHED.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.FIN, null, 0, 0, null, 0, null);
                s.setState(MainMap.FIN_WAIT_1);
                (s.getState()).Entry(s);

                return;
            }

            protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ESTABLISHED.FIN(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if (!(s.getOwner()).isInterloper(address, port))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    (s.getOwner()).halfClosed();
                    s.setState(MainMap.CLOSE_WAIT);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.FIN(s, address, port, segment);
                }

                return;
            }

            protected void PSH(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ESTABLISHED.PSH(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if (!(s.getOwner()).isInterloper(address, port))
                {

                    TcpConnectionState endState = s.getState();

                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    (s.getOwner()).receive(segment);
                    s.setState(endState);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.PSH(s, address, port, segment);
                }

                return;
            }

            protected void Transmit(TcpConnectionContext s, byte[] data, int offset, int size)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.ESTABLISHED.Transmit(byte[] data, int offset, int size)");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.PSH, data, offset, size, null, 0, null);
                s.setState(MainMap.Transmitting);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_Transmitting
            extends MainMap_Default
        {
            private MainMap_Transmitting(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("TRANS_ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("TRANS_ACK_TIMER");
                return;
            }

            protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.Transmitting.ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).transmitted();
                    s.setState(MainMap.ESTABLISHED);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.ACK(s, address, port, segment);
                }

                return;
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.Transmitting.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.FIN, null, 0, 0, null, 0, null);
                s.setState(MainMap.FIN_WAIT_1);
                (s.getState()).Entry(s);

                return;
            }

            protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.Transmitting.FIN(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if (!(s.getOwner()).isInterloper(address, port))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    (s.getOwner()).halfClosed();
                    s.setState(MainMap.CLOSE_WAIT);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.FIN(s, address, port, segment);
                }

                return;
            }

            protected void PSH_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.Transmitting.PSH_ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    (s.getOwner()).transmitted();
                    (s.getOwner()).receive(segment);
                    s.setState(MainMap.ESTABLISHED);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.PSH_ACK(s, address, port, segment);
                }

                return;
            }

            protected void TransAckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.Transmitting.TransAckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).transmitFailed("peer did not acknowledge");
                (s.getOwner()).closed("connection lost");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_CLOSE_WAIT
            extends MainMap_Default
        {
            private MainMap_CLOSE_WAIT(String name)
            {
                super(name);
            }

            protected void Close(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSE_WAIT.Close");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).send(TcpSegment.FIN, null, 0, 0, null, 0, null);
                s.setState(MainMap.LAST_ACK);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_LAST_ACK
            extends MainMap_Default
        {
            private MainMap_LAST_ACK(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("CLOSE_ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("CLOSE_ACK_TIMER");
                return;
            }

            protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.LAST_ACK.ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).closeSocket();
                    (s.getOwner()).closed("");
                    s.setState(MainMap.CLOSED);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.ACK(s, address, port, segment);
                }

                return;
            }

            protected void CloseAckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.LAST_ACK.CloseAckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_FIN_WAIT_1
            extends MainMap_Default
        {
            private MainMap_FIN_WAIT_1(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("CLOSE_ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("CLOSE_ACK_TIMER");
                return;
            }

            protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.FIN_WAIT_1.ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.setState(MainMap.FIN_WAIT_2);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.ACK(s, address, port, segment);
                }

                return;
            }

            protected void CloseAckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.FIN_WAIT_1.CloseAckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.FIN_WAIT_2);
                (s.getState()).Entry(s);

                return;
            }

            protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.FIN_WAIT_1.FIN(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if (!(s.getOwner()).isInterloper(address, port))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    s.setState(MainMap.CLOSING);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.FIN(s, address, port, segment);
                }

                return;
            }

            protected void FIN_ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.FIN_WAIT_1.FIN_ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    s.setState(MainMap.TIME_WAIT);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.FIN_ACK(s, address, port, segment);
                }

                return;
            }

        }

        private static final class MainMap_FIN_WAIT_2
            extends MainMap_Default
        {
            private MainMap_FIN_WAIT_2(String name)
            {
                super(name);
            }

            protected void FIN(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.FIN_WAIT_2.FIN(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if (!(s.getOwner()).isInterloper(address, port))
                {
                    s.clearState();
                    (s.getOwner()).send(TcpSegment.ACK, null, 0, 0, address, port, segment);
                    s.setState(MainMap.TIME_WAIT);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.FIN(s, address, port, segment);
                }

                return;
            }

        }

        private static final class MainMap_CLOSING
            extends MainMap_Default
        {
            private MainMap_CLOSING(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("CLOSE_ACK_TIMER", TcpConnection.ACK_TIMEOUT);
                return;
            }

            protected void Exit(TcpConnectionContext s)
            {
                (s.getOwner()).stopTimer("CLOSE_ACK_TIMER");
                return;
            }

            protected void ACK(TcpConnectionContext s, InetAddress address, int port, TcpSegment segment)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSING.ACK(InetAddress address, int port, TcpSegment segment)");
                }

                (s.getState()).Exit(s);
                if ((s.getOwner()).isValidAck(address, port, segment))
                {
                    s.setState(MainMap.TIME_WAIT);
                    (s.getState()).Entry(s);
}
                else
                {
                    MainMap.Default.ACK(s, address, port, segment);
                }

                return;
            }

            protected void CloseAckTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.CLOSING.CloseAckTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.TIME_WAIT);
                (s.getState()).Entry(s);

                return;
            }

        }

        private static final class MainMap_TIME_WAIT
            extends MainMap_Default
        {
            private MainMap_TIME_WAIT(String name)
            {
                super(name);
            }

            protected void Entry(TcpConnectionContext s)
            {
                (s.getOwner()).startTimer("CLOSE_TIMER", TcpConnection.CLOSE_TIMEOUT);
                return;
            }

            protected void CloseTimeout(TcpConnectionContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : MainMap.TIME_WAIT.CloseTimeout");
                }

                (s.getState()).Exit(s);
                s.clearState();
                (s.getOwner()).closeSocket();
                (s.getOwner()).closed("");
                s.setState(MainMap.CLOSED);
                (s.getState()).Entry(s);

                return;
            }

        }
    }
}

