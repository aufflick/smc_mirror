import java.io.PrintStream;

public final class TelephoneContext
    extends statemap.FSMContext
{
// Member methods.

    public TelephoneContext(Telephone owner)
    {
        super();

        _owner = owner;
        setState(CallMap.Initialized);
        CallMap.Initialized.Entry(this);
    }

    public void ClockTimer()
    {
        _transition = "ClockTimer";
        getState().ClockTimer(this);
        _transition = "";
        return;
    }

    public void DepositMoney()
    {
        _transition = "DepositMoney";
        getState().DepositMoney(this);
        _transition = "";
        return;
    }

    public void DialingDone()
    {
        _transition = "DialingDone";
        getState().DialingDone(this);
        _transition = "";
        return;
    }

    public void Digit(String n)
    {
        _transition = "Digit";
        getState().Digit(this, n);
        _transition = "";
        return;
    }

    public void Emergency()
    {
        _transition = "Emergency";
        getState().Emergency(this);
        _transition = "";
        return;
    }

    public void InvalidDigit()
    {
        _transition = "InvalidDigit";
        getState().InvalidDigit(this);
        _transition = "";
        return;
    }

    public void InvalidNumber()
    {
        _transition = "InvalidNumber";
        getState().InvalidNumber(this);
        _transition = "";
        return;
    }

    public void LeftOffHook()
    {
        _transition = "LeftOffHook";
        getState().LeftOffHook(this);
        _transition = "";
        return;
    }

    public void LineBusy()
    {
        _transition = "LineBusy";
        getState().LineBusy(this);
        _transition = "";
        return;
    }

    public void LoopTimer()
    {
        _transition = "LoopTimer";
        getState().LoopTimer(this);
        _transition = "";
        return;
    }

    public void NYCTemp()
    {
        _transition = "NYCTemp";
        getState().NYCTemp(this);
        _transition = "";
        return;
    }

    public void OffHook()
    {
        _transition = "OffHook";
        getState().OffHook(this);
        _transition = "";
        return;
    }

    public void OffHookTimer()
    {
        _transition = "OffHookTimer";
        getState().OffHookTimer(this);
        _transition = "";
        return;
    }

    public void OnHook()
    {
        _transition = "OnHook";
        getState().OnHook(this);
        _transition = "";
        return;
    }

    public void PlaybackDone()
    {
        _transition = "PlaybackDone";
        getState().PlaybackDone(this);
        _transition = "";
        return;
    }

    public void RingTimer()
    {
        _transition = "RingTimer";
        getState().RingTimer(this);
        _transition = "";
        return;
    }

    public void Start()
    {
        _transition = "Start";
        getState().Start(this);
        _transition = "";
        return;
    }

    public void Stop()
    {
        _transition = "Stop";
        getState().Stop(this);
        _transition = "";
        return;
    }

    public void Time()
    {
        _transition = "Time";
        getState().Time(this);
        _transition = "";
        return;
    }

    protected TelephoneState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((TelephoneState) _state);
    }

    protected Telephone getOwner()
    {
        return (_owner);
    }

// Member data.

    transient private Telephone _owner;

    protected static abstract class TelephoneState
        extends statemap.State
    {
        protected TelephoneState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(TelephoneContext context) {}
        protected void Exit(TelephoneContext context) {}

        protected void ClockTimer(TelephoneContext context)
        {
            Default(context);
        }

        protected void DepositMoney(TelephoneContext context)
        {
            Default(context);
        }

        protected void DialingDone(TelephoneContext context)
        {
            Default(context);
        }

        protected void Digit(TelephoneContext context, String n)
        {
            Default(context);
        }

        protected void Emergency(TelephoneContext context)
        {
            Default(context);
        }

        protected void InvalidDigit(TelephoneContext context)
        {
            Default(context);
        }

        protected void InvalidNumber(TelephoneContext context)
        {
            Default(context);
        }

        protected void LeftOffHook(TelephoneContext context)
        {
            Default(context);
        }

        protected void LineBusy(TelephoneContext context)
        {
            Default(context);
        }

        protected void LoopTimer(TelephoneContext context)
        {
            Default(context);
        }

        protected void NYCTemp(TelephoneContext context)
        {
            Default(context);
        }

        protected void OffHook(TelephoneContext context)
        {
            Default(context);
        }

        protected void OffHookTimer(TelephoneContext context)
        {
            Default(context);
        }

        protected void OnHook(TelephoneContext context)
        {
            Default(context);
        }

        protected void PlaybackDone(TelephoneContext context)
        {
            Default(context);
        }

        protected void RingTimer(TelephoneContext context)
        {
            Default(context);
        }

        protected void Start(TelephoneContext context)
        {
            Default(context);
        }

        protected void Stop(TelephoneContext context)
        {
            Default(context);
        }

        protected void Time(TelephoneContext context)
        {
            Default(context);
        }

        protected void Default(TelephoneContext context)
        {
            if (context.getDebugFlag() == true)
            {
                PrintStream str = 
                    context.getDebugStream();

                str.println(
                    "TRANSITION   : Default");
            }

            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }
    }

    /* package */ static abstract class CallMap
    {
        /* package */ static CallMap_Default.CallMap_Initialized Initialized;
        /* package */ static CallMap_Default.CallMap_OnHook OnHook;
        /* package */ static CallMap_Default.CallMap_Routing Routing;
        /* package */ static CallMap_Default.CallMap_NYCTemp NYCTemp;
        /* package */ static CallMap_Default.CallMap_Time Time;
        /* package */ static CallMap_Default.CallMap_DepositMoney DepositMoney;
        /* package */ static CallMap_Default.CallMap_BusySignal BusySignal;
        /* package */ static CallMap_Default.CallMap_PlayingMessage PlayingMessage;
        /* package */ static CallMap_Default.CallMap_MessagePlayed MessagePlayed;
        /* package */ static CallMap_Default.CallMap_LeftOffHook LeftOffHook;
        /* package */ static CallMap_Default.CallMap_InvalidDigit InvalidDigit;
        /* package */ static CallMap_Default.CallMap_WaitForOnHook WaitForOnHook;
        private static CallMap_Default Default;

        static
        {
            Initialized = new CallMap_Default.CallMap_Initialized("CallMap.Initialized", 0);
            OnHook = new CallMap_Default.CallMap_OnHook("CallMap.OnHook", 1);
            Routing = new CallMap_Default.CallMap_Routing("CallMap.Routing", 2);
            NYCTemp = new CallMap_Default.CallMap_NYCTemp("CallMap.NYCTemp", 3);
            Time = new CallMap_Default.CallMap_Time("CallMap.Time", 4);
            DepositMoney = new CallMap_Default.CallMap_DepositMoney("CallMap.DepositMoney", 5);
            BusySignal = new CallMap_Default.CallMap_BusySignal("CallMap.BusySignal", 6);
            PlayingMessage = new CallMap_Default.CallMap_PlayingMessage("CallMap.PlayingMessage", 7);
            MessagePlayed = new CallMap_Default.CallMap_MessagePlayed("CallMap.MessagePlayed", 8);
            LeftOffHook = new CallMap_Default.CallMap_LeftOffHook("CallMap.LeftOffHook", 9);
            InvalidDigit = new CallMap_Default.CallMap_InvalidDigit("CallMap.InvalidDigit", 10);
            WaitForOnHook = new CallMap_Default.CallMap_WaitForOnHook("CallMap.WaitForOnHook", 11);
            Default = new CallMap_Default("CallMap.Default", -1);
        }

    }

    protected static class CallMap_Default
        extends TelephoneState
    {
        protected CallMap_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Digit(TelephoneContext context, String n)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : CallMap.Default.Digit(String n)");
            }

            TelephoneState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.writeTransition("CallMap::Default::Digit(" + n + ")");
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }

        protected void OnHook(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : CallMap.Default.OnHook()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    CallMap.OnHook.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.writeTransition("CallMap::Default::OnHook");
                ctxt.writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\")");
                ctxt.setReceiver("off hook", "Pick up receiver");
                ctxt.writeTransAction("clearDisplay()");
                ctxt.clearDisplay();
            }
            finally
            {
                context.setState(CallMap.OnHook);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void Stop(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : CallMap.Default.Stop()");
            }

            boolean loopbackFlag =
                context.getState().getName().equals(
                    CallMap.Initialized.getName());

            if (loopbackFlag == false)
            {
                (context.getState()).Exit(context);
            }

            context.clearState();
            try
            {
                ctxt.writeTransition("CallMap::Default::Stop");
                ctxt.writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\")");
                ctxt.setReceiver("off hook", "Pick up receiver");
                ctxt.writeTransAction("clearDisplay()");
                ctxt.clearDisplay();
            }
            finally
            {
                context.setState(CallMap.Initialized);

                if (loopbackFlag == false)
                {
                    (context.getState()).Entry(context);
                }

            }
            return;
        }

        protected void ClockTimer(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : CallMap.Default.ClockTimer()");
            }

            TelephoneState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.writeTransition("CallMap::Default::ClockTimer");
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }

        private static final class CallMap_Initialized
            extends CallMap_Default
        {
            private CallMap_Initialized(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::Initialized");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::Initialized");
                return;
            }

            protected void Default(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Initialized.Default()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("Default");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void Start(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Initialized.Start()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("OnHook");
                }
                finally
                {
                    context.setState(CallMap.OnHook);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_OnHook
            extends CallMap_Default
        {
            private CallMap_OnHook(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::OnHook");
                ctxt.writeStateAction("Entry", "updateClock()");
                ctxt.updateClock();
                ctxt.writeStateAction("Entry", "startClockTimer()");
                ctxt.startClockTimer();
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::OnHook");
                ctxt.writeStateAction("Exit", "stopTimer(\"ClockTimer\")");
                ctxt.stopTimer("ClockTimer");
                return;
            }

            protected void ClockTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.OnHook.ClockTimer()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("ClockTimer");
                    ctxt.writeTransAction("updateClock()");
                    ctxt.updateClock();
                    ctxt.writeTransAction("startClockTimer()");
                    ctxt.startClockTimer();
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void DialingDone(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.OnHook.DialingDone()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("DialingDone");
                    ctxt.writeTransAction("routeCall()");
                    ctxt.routeCall();
                }
                finally
                {
                    context.setState(CallMap.Routing);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void InvalidDigit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.OnHook.InvalidDigit()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("InvalidDigit");
                }
                finally
                {
                    context.setState(CallMap.InvalidDigit);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LeftOffHook(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.OnHook.LeftOffHook()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("LeftOffHook");
                }
                finally
                {
                    context.setState(CallMap.LeftOffHook);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void OffHook(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.OnHook.OffHook()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("push(PhoneNumber::DialTone)");
                    ctxt.writeTransAction("clearDisplay()");
                    ctxt.clearDisplay();
                    ctxt.writeTransAction("setReceiver(\"on hook\", \"Put down receiver\")");
                    ctxt.setReceiver("on hook", "Put down receiver");
                }
                finally
                {
                    context.setState(endState);
                    context.pushState(PhoneNumber.DialTone);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_Routing
            extends CallMap_Default
        {
            private CallMap_Routing(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::Routing");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::Routing");
                return;
            }

            protected void DepositMoney(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.DepositMoney()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("DepositMoney");
                }
                finally
                {
                    context.setState(CallMap.DepositMoney);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Emergency(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.Emergency()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("Emergency");
                    ctxt.writeTransAction("playEmergency()");
                    ctxt.playEmergency();
                }
                finally
                {
                    context.setState(CallMap.PlayingMessage);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void InvalidNumber(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.InvalidNumber()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("InvalidNumber");
                    ctxt.writeTransAction("playInvalidNumber()");
                    ctxt.playInvalidNumber();
                }
                finally
                {
                    context.setState(CallMap.PlayingMessage);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void LineBusy(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.LineBusy()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("LineBusy");
                }
                finally
                {
                    context.setState(CallMap.BusySignal);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void NYCTemp(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.NYCTemp()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("NYCTemp");
                }
                finally
                {
                    context.setState(CallMap.NYCTemp);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Time(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Routing.Time()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("Time");
                }
                finally
                {
                    context.setState(CallMap.Time);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_NYCTemp
            extends CallMap_Default
        {
            private CallMap_NYCTemp(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::NYCTemp");
                ctxt.writeStateAction("Entry", "loop(\"ringing\")");
                ctxt.loop("ringing");
                ctxt.writeStateAction("Entry", "startTimer(\"RingTimer\", 10000)");
                ctxt.startTimer("RingTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::NYCTemp");
                ctxt.writeStateAction("Entry", "stopLoop(\"ringing\")");
                ctxt.stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.NYCTemp.RingTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("RingTimer");
                    ctxt.writeTransAction("playNYCTemp()");
                    ctxt.playNYCTemp();
                }
                finally
                {
                    context.setState(CallMap.PlayingMessage);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_Time
            extends CallMap_Default
        {
            private CallMap_Time(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::Time");
                ctxt.writeStateAction("Entry", "loop(\"ringing\")");
                ctxt.loop("ringing");
                ctxt.writeStateAction("Entry", "startTimer(\"RingTimer\", 10000)");
                ctxt.startTimer("RingTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::Time");
                ctxt.writeStateAction("Entry", "stopLoop(\"ringing\")");
                ctxt.stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.Time.RingTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("RingTimer");
                    ctxt.writeTransAction("playTime()");
                    ctxt.playTime();
                }
                finally
                {
                    context.setState(CallMap.PlayingMessage);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_DepositMoney
            extends CallMap_Default
        {
            private CallMap_DepositMoney(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::DepositMoney");
                ctxt.writeStateAction("Entry", "loop(\"ringing\")");
                ctxt.loop("ringing");
                ctxt.writeStateAction("Entry", "startTimer(\"RingTimer\", 5000)");
                ctxt.startTimer("RingTimer", 5000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::DepositMoney");
                ctxt.writeStateAction("Entry", "stopLoop(\"ringing\")");
                ctxt.stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.DepositMoney.RingTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.playDepositMoney();
                }
                finally
                {
                    context.setState(CallMap.PlayingMessage);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_BusySignal
            extends CallMap_Default
        {
            private CallMap_BusySignal(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::BusySignal");
                ctxt.writeStateAction("Entry", "loop(\"busy\")");
                ctxt.loop("busy");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::BusySignal");
                ctxt.writeStateAction("Entry", "stopLoop(\"busy\")");
                ctxt.stopLoop("busy");
                return;
            }
        }

        private static final class CallMap_PlayingMessage
            extends CallMap_Default
        {
            private CallMap_PlayingMessage(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::PlayingMessage");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::PlayingMessage");
                return;
            }

            protected void OnHook(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.PlayingMessage.OnHook()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("OnHook");
                    ctxt.writeTransAction("stopPlayback()");
                    ctxt.stopPlayback();
                    ctxt.writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\")");
                    ctxt.setReceiver("off hook", "Pick up receiver");
                    ctxt.writeTransAction("clearDisplay()");
                    ctxt.clearDisplay();
                }
                finally
                {
                    context.setState(CallMap.OnHook);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void PlaybackDone(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.PlayingMessage.PlaybackDone()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("PlaybackDone");
                }
                finally
                {
                    context.setState(CallMap.MessagePlayed);
                    (context.getState()).Entry(context);
                }
                return;
            }

            protected void Stop(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.PlayingMessage.Stop()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("Stop");
                    ctxt.writeTransAction("stopPlayback()");
                    ctxt.stopPlayback();
                    ctxt.writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\")");
                    ctxt.setReceiver("off hook", "Pick up receiver");
                    ctxt.writeTransAction("clearDisplay()");
                    ctxt.clearDisplay();
                }
                finally
                {
                    context.setState(CallMap.Initialized);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_MessagePlayed
            extends CallMap_Default
        {
            private CallMap_MessagePlayed(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::MessagePlayed");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::MessagePlayed");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void OffHookTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.MessagePlayed.OffHookTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("OffHookTimer");
                }
                finally
                {
                    context.setState(CallMap.LeftOffHook);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_LeftOffHook
            extends CallMap_Default
        {
            private CallMap_LeftOffHook(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::LeftOffHook");
                ctxt.writeStateAction("Entry", "startTimer(\"LoopTimer\", 10000)");
                ctxt.startTimer("LoopTimer", 10000);
                ctxt.writeStateAction("Entry", "loop(\"phone_off_hook\")");
                ctxt.loop("phone_off_hook");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::MessagePlayed");
                ctxt.writeStateAction("Exit", "stopTimer(\"LoopTimer\")");
                ctxt.stopTimer("LoopTimer");
                ctxt.writeStateAction("Exit", "stopLoop(\"phone_off_hook\")");
                ctxt.stopLoop("phone_off_hook");
                return;
            }

            protected void Default(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.LeftOffHook.Default()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("Default");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void LoopTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.LeftOffHook.LoopTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("WaitForOnHook");
                }
                finally
                {
                    context.setState(CallMap.WaitForOnHook);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_InvalidDigit
            extends CallMap_Default
        {
            private CallMap_InvalidDigit(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::InvalidDigit");
                ctxt.writeStateAction("Entry", "startTimer(\"LoopTimer\", 10000)");
                ctxt.startTimer("LoopTimer", 10000);
                ctxt.writeStateAction("Entry", "loop(\"fast_busy\")");
                ctxt.loop("fast_busy");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::MessagePlayed");
                ctxt.writeStateAction("Exit", "stopTimer(\"LoopTimer\")");
                ctxt.stopTimer("LoopTimer");
                ctxt.writeStateAction("Exit", "stopLoop(\"phone_off_hook\")");
                ctxt.stopLoop("fast_busy");
                return;
            }

            protected void Default(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.InvalidDigit.Default()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("Default");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }

            protected void LoopTimer(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.InvalidDigit.LoopTimer()");
                }

                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("LoopTimer");
                }
                finally
                {
                    context.setState(CallMap.WaitForOnHook);
                    (context.getState()).Entry(context);
                }
                return;
            }
        }

        private static final class CallMap_WaitForOnHook
            extends CallMap_Default
        {
            private CallMap_WaitForOnHook(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::WaitForOnHook");
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::WaitForOnHook");
                return;
            }

            protected void Default(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : CallMap.WaitForOnHook.Default()");
                }

                TelephoneState endState = context.getState();

                context.clearState();
                try
                {
                    ctxt.writeTransition("Default");
                }
                finally
                {
                    context.setState(endState);
                }
                return;
            }
        }
    }

    /* package */ static abstract class PhoneNumber
    {
        /* package */ static PhoneNumber_Default.PhoneNumber_DialTone DialTone;
        /* package */ static PhoneNumber_Default.PhoneNumber_LongDistance LongDistance;
        /* package */ static PhoneNumber_Default.PhoneNumber_NineOneOneStart NineOneOneStart;
        /* package */ static PhoneNumber_Default.PhoneNumber_NineOne NineOne;
        /* package */ static PhoneNumber_Default.PhoneNumber_Exchange Exchange;
        /* package */ static PhoneNumber_Default.PhoneNumber_LocalCall LocalCall;
        private static PhoneNumber_Default Default;

        static
        {
            DialTone = new PhoneNumber_Default.PhoneNumber_DialTone("PhoneNumber.DialTone", 12);
            LongDistance = new PhoneNumber_Default.PhoneNumber_LongDistance("PhoneNumber.LongDistance", 13);
            NineOneOneStart = new PhoneNumber_Default.PhoneNumber_NineOneOneStart("PhoneNumber.NineOneOneStart", 14);
            NineOne = new PhoneNumber_Default.PhoneNumber_NineOne("PhoneNumber.NineOne", 15);
            Exchange = new PhoneNumber_Default.PhoneNumber_Exchange("PhoneNumber.Exchange", 16);
            LocalCall = new PhoneNumber_Default.PhoneNumber_LocalCall("PhoneNumber.LocalCall", 17);
            Default = new PhoneNumber_Default("PhoneNumber.Default", -1);
        }

    }

    protected static class PhoneNumber_Default
        extends TelephoneState
    {
        protected PhoneNumber_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Digit(TelephoneContext context, String n)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : PhoneNumber.Default.Digit(String n)");
            }

            if (ctxt.isDigitValid(n) == false)
            {
                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.writeTransition("PhoneNumber::Default::Digit(" + n + ")");
                    ctxt.writeTransAction("clearDisplay()");
                    ctxt.clearDisplay();
                    ctxt.writeTransition("pop(InvalidDigit)");
                }
                finally
                {
                    context.popState();
                }

                context.InvalidDigit();
            }
            else
            {
                super.Digit(context, n);
            }

            return;
        }

        protected void OffHookTimer(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : PhoneNumber.Default.OffHookTimer()");
            }

            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.writeTransition("PhoneNumber::Default::OffHookTimer");
                ctxt.writeTransAction("clearDisplay()");
                ctxt.clearDisplay();
                ctxt.writeTransition("pop(LeftOffHook)");
            }
            finally
            {
                context.popState();
            }

            context.LeftOffHook();
            return;
        }

        protected void OnHook(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : PhoneNumber.Default.OnHook()");
            }

            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.writeTransition("PhoneNumber::Default::OnHook");
                ctxt.writeTransAction("clearDisplay()");
                ctxt.clearDisplay();
                ctxt.writeTransition("pop(OnHook)");
            }
            finally
            {
                context.popState();
            }

            context.OnHook();
            return;
        }

        protected void Stop(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : PhoneNumber.Default.Stop()");
            }

            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.writeTransition("PhoneNumber::Default::Stop");
                ctxt.writeTransAction("clearDisplay()");
                ctxt.clearDisplay();
                ctxt.writeTransition("pop(Stop)");
            }
            finally
            {
                context.popState();
            }

            context.Stop();
            return;
        }

        protected void ClockTimer(TelephoneContext context)
        {
            Telephone ctxt = context.getOwner();

            if (context.getDebugFlag() == true)
            {
                PrintStream str = context.getDebugStream();

                str.println("TRANSITION   : PhoneNumber.Default.ClockTimer()");
            }

            TelephoneState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.writeTransition("PhoneNumber::Default::ClockTimer");
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }

        private static final class PhoneNumber_DialTone
            extends PhoneNumber_Default
        {
            private PhoneNumber_DialTone(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering PhoneNumber::DialTone");
                ctxt.writeStateAction("Entry", "loop(\"dialtone\")");
                ctxt.loop("dialtone");
                ctxt.writeStateAction("Exit", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting PhoneNumber::DialTone");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                ctxt.writeStateAction("Exit", "stopLoop(\"dialtone\")");
                ctxt.stopLoop("dialtone");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.DialTone.Digit(String n)");
                }

                if (ctxt.equal(n, 1) == true)
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("setType(Telephone.LONG_DISTANCE)");
                        ctxt.setType(Telephone.LONG_DISTANCE);
                        ctxt.writeTransAction("saveAreaCode(" + n + ")");
                        ctxt.saveAreaCode(n);
                        ctxt.writeTransAction("addDisplay(\"-\")");
                        ctxt.addDisplay("-");
                    }
                    finally
                    {
                        context.setState(PhoneNumber.LongDistance);
                        (context.getState()).Entry(context);
                    }
                }
                else if (ctxt.equal(n, 9) == true)
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                    }
                    finally
                    {
                        context.setState(PhoneNumber.NineOneOneStart);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("setType(Telephone.LOCAL)");
                        ctxt.setType(Telephone.LOCAL);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                    }
                    finally
                    {
                        context.setState(PhoneNumber.Exchange);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }
        }

        private static final class PhoneNumber_LongDistance
            extends PhoneNumber_Default
        {
            private PhoneNumber_LongDistance(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering PhoneNumber::LongDistance");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting PhoneNumber::LongDistance");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.LongDistance.Digit(String n)");
                }

                if (ctxt.isCodeComplete() == false)
                {
                    TelephoneState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveAreaCode(" + n + ")");
                        ctxt.saveAreaCode(n);
                        ctxt.writeTransAction("resetTimer(\"OffHookTimer\")");
                        ctxt.resetTimer("OffHookTimer");
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveAreaCode(" + n + ")");
                        ctxt.saveAreaCode(n);
                        ctxt.writeTransAction("addDisplay(\"-\")");
                        ctxt.addDisplay("-");
                    }
                    finally
                    {
                        context.setState(PhoneNumber.Exchange);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }
        }

        private static final class PhoneNumber_NineOneOneStart
            extends PhoneNumber_Default
        {
            private PhoneNumber_NineOneOneStart(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering PhoneNumber::NineOneOneStart");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting PhoneNumber::NineOneOneStart");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.NineOneOneStart.Digit(String n)");
                }

                if (ctxt.equal(n, 1) == true)
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveAreaCode(" + n + ")");
                        ctxt.saveExchange(n);
                    }
                    finally
                    {
                        context.setState(PhoneNumber.NineOne);
                        (context.getState()).Entry(context);
                    }
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("setType(Telephone.LOCAL)");
                        ctxt.setType(Telephone.LOCAL);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                    }
                    finally
                    {
                        context.setState(PhoneNumber.Exchange);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }
        }

        private static final class PhoneNumber_NineOne
            extends PhoneNumber_Default
        {
            private PhoneNumber_NineOne(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering PhoneNumber::NineOne");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting PhoneNumber::NineOne");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.NineOne.Digit(String n)");
                }

                if (ctxt.equal(n, 1) == true)
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("setType(Telephone.EMERGENCY)");
                        ctxt.setType(Telephone.EMERGENCY);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                        ctxt.writeTransition("pop(DialingDone)");
                    }
                    finally
                    {
                        context.popState();
                    }

                    context.DialingDone();
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("setType(Telephone.LOCAL)");
                        ctxt.setType(Telephone.LOCAL);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                        ctxt.writeTransAction("addDisplay(\"-\")");
                        ctxt.addDisplay("-");
                    }
                    finally
                    {
                        context.setState(PhoneNumber.LocalCall);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }
        }

        private static final class PhoneNumber_Exchange
            extends PhoneNumber_Default
        {
            private PhoneNumber_Exchange(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering PhoneNumber::Exchange");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting PhoneNumber::Exchange");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.Exchange.Digit(String n)");
                }

                if (ctxt.isExchangeComplete() == false)
                {
                    TelephoneState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                        ctxt.writeTransition("resetTimer(\"OffHookTimer\")");
                        ctxt.resetTimer("OffHookTimer");
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveExchange(" + n + ")");
                        ctxt.saveExchange(n);
                        ctxt.writeTransAction("addDisplay(\"-\")");
                        ctxt.addDisplay("-");
                    }
                    finally
                    {
                        context.setState(PhoneNumber.LocalCall);
                        (context.getState()).Entry(context);
                    }
                }

                return;
            }
        }

        private static final class PhoneNumber_LocalCall
            extends PhoneNumber_Default
        {
            private PhoneNumber_LocalCall(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Entering CallMap::LocalCall");
                ctxt.writeStateAction("Entry", "startTimer(\"OffHookTimer\", 10000)");
                ctxt.startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext context)
            {
                Telephone ctxt = context.getOwner();

                ctxt.writeState("Exiting CallMap::LocalCall");
                ctxt.writeStateAction("Exit", "stopTimer(\"OffHookTimer\")");
                ctxt.stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext context, String n)
            {
                Telephone ctxt = context.getOwner();

                if (context.getDebugFlag() == true)
                {
                    PrintStream str = context.getDebugStream();

                    str.println("TRANSITION   : PhoneNumber.LocalCall.Digit(String n)");
                }

                if (ctxt.isLocalComplete() == false)
                {
                    TelephoneState endState = context.getState();

                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveLocal(" + n + ")");
                        ctxt.saveLocal(n);
                        ctxt.writeTransAction("resetTimer(\"OffHookTimer\")");
                        ctxt.resetTimer("OffHookTimer");
                    }
                    finally
                    {
                        context.setState(endState);
                    }
                }
                else
                {
                    (context.getState()).Exit(context);
                    context.clearState();
                    try
                    {
                        ctxt.writeTransition("Digit(" + n + ")");
                        ctxt.writeTransAction("playTT(" + n + ")");
                        ctxt.playTT(n);
                        ctxt.writeTransAction("saveLocal(" + n + ")");
                        ctxt.saveLocal(n);
                        ctxt.writeTransition("pop(DialingDone)");
                    }
                    finally
                    {
                        context.popState();
                    }

                    context.DialingDone();
                }

                return;
            }
        }
    }
}
