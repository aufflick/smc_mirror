public final class TelephoneContext
    extends statemap.FSMContext
{
// Member methods.

    public TelephoneContext(Telephone owner)
    {
        super();

        _owner = owner;
        setState(CallMap.Initialized);

        owner.writeState("CallMap.Initialized");

        CallMap.Initialized.Entry(this);
    }

    public TelephoneState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(new statemap.StateUndefinedException());
        }

        return((TelephoneState) _state);
    }

    public Telephone getOwner()
    {
        return(_owner);
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

    public void Looptimer()
    {
        _transition = "Looptimer";
        getState().Looptimer(this);
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

// Member data.

    private Telephone _owner;

    protected static abstract class TelephoneState
        extends statemap.State
    {
        protected TelephoneState(String name)
        {
            super(name);
        }

        protected void Entry(TelephoneContext s) {}
        protected void Exit(TelephoneContext s) {}

        protected void ClockTimer(TelephoneContext s) {}
        protected void Default(TelephoneContext s) {}
        protected void DepositMoney(TelephoneContext s) {}
        protected void DialingDone(TelephoneContext s) {}
        protected void Digit(TelephoneContext s, String n) {}
        protected void Emergency(TelephoneContext s) {}
        protected void InvalidDigit(TelephoneContext s) {}
        protected void InvalidNumber(TelephoneContext s) {}
        protected void LeftOffHook(TelephoneContext s) {}
        protected void LineBusy(TelephoneContext s) {}
        protected void LoopTimer(TelephoneContext s) {}
        protected void Looptimer(TelephoneContext s) {}
        protected void NYCTemp(TelephoneContext s) {}
        protected void OffHook(TelephoneContext s) {}
        protected void OffHookTimer(TelephoneContext s) {}
        protected void OnHook(TelephoneContext s) {}
        protected void PlaybackDone(TelephoneContext s) {}
        protected void RingTimer(TelephoneContext s) {}
        protected void Start(TelephoneContext s) {}
        protected void Stop(TelephoneContext s) {}
        protected void Time(TelephoneContext s) {}
    }

    protected static abstract class CallMap
    {
        private static CallMap_Default.CallMap_Initialized Initialized;
        private static CallMap_Default.CallMap_OnHook OnHook;
        private static CallMap_Default.CallMap_Routing Routing;
        private static CallMap_Default.CallMap_NYCTemp NYCTemp;
        private static CallMap_Default.CallMap_Time Time;
        private static CallMap_Default.CallMap_DepositMoney DepositMoney;
        private static CallMap_Default.CallMap_BusySignal BusySignal;
        private static CallMap_Default.CallMap_PlayingMessage PlayingMessage;
        private static CallMap_Default.CallMap_MessagePlayed MessagePlayed;
        private static CallMap_Default.CallMap_LeftOffHook LeftOffHook;
        private static CallMap_Default.CallMap_InvalidDigit InvalidDigit;
        private static CallMap_Default.CallMap_WaitForOnHook WaitForOnHook;
        private static CallMap_Default Default;

        static
        {
            Initialized = new CallMap_Default.CallMap_Initialized("CallMap.Initialized");
            OnHook = new CallMap_Default.CallMap_OnHook("CallMap.OnHook");
            Routing = new CallMap_Default.CallMap_Routing("CallMap.Routing");
            NYCTemp = new CallMap_Default.CallMap_NYCTemp("CallMap.NYCTemp");
            Time = new CallMap_Default.CallMap_Time("CallMap.Time");
            DepositMoney = new CallMap_Default.CallMap_DepositMoney("CallMap.DepositMoney");
            BusySignal = new CallMap_Default.CallMap_BusySignal("CallMap.BusySignal");
            PlayingMessage = new CallMap_Default.CallMap_PlayingMessage("CallMap.PlayingMessage");
            MessagePlayed = new CallMap_Default.CallMap_MessagePlayed("CallMap.MessagePlayed");
            LeftOffHook = new CallMap_Default.CallMap_LeftOffHook("CallMap.LeftOffHook");
            InvalidDigit = new CallMap_Default.CallMap_InvalidDigit("CallMap.InvalidDigit");
            WaitForOnHook = new CallMap_Default.CallMap_WaitForOnHook("CallMap.WaitForOnHook");
            Default = new CallMap_Default("CallMap.Default");
        }

    }

    protected static class CallMap_Default
        extends TelephoneState
    {
        protected CallMap_Default(String name)
        {
            super(name);
        }

        protected void Digit(TelephoneContext s, String n)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : CallMap.Default.Digit(String n)");
            }

            (s.getOwner()).writeTransition("Default.Digit(" +
                                          n +
                                          ")");

            return;
        }

        protected void OnHook(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : CallMap.Default.OnHook");
            }

            (s.getState()).Exit(s);
            s.clearState();

            (s.getOwner()).writeTransition("Default.OnHook");

            (s.getOwner()).writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\");");
            (s.getOwner()).setReceiver("off hook", "Pick up receiver");

            (s.getOwner()).writeTransAction("clearDisplay();");
            (s.getOwner()).clearDisplay();

            s.setState(CallMap.OnHook);
            (s.getOwner()).writeState("CallMap.OnHook");

            (s.getState()).Entry(s);
            return;
        }

        protected void Stop(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : CallMap.Default.Stop");
            }

            (s.getState()).Exit(s);
            s.clearState();

            (s.getOwner()).writeTransition("Default.Stop");

            (s.getOwner()).writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\");");
            (s.getOwner()).setReceiver("off hook", "Pick up receiver");

            (s.getOwner()).writeTransAction("clearDisplay();");
            (s.getOwner()).clearDisplay();

            s.setState(CallMap.Initialized);
            (s.getOwner()).writeState("CallMap.OnHook");

            (s.getState()).Entry(s);
            return;
        }

        protected void ClockTimer(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : CallMap.Default.ClockTimer");

            (s.getOwner()).writeTransition("Default.ClockTimer");

            }

            return;
        }

        protected void Start(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void DialingDone(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void InvalidDigit(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void LeftOffHook(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void OffHook(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void DepositMoney(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void Emergency(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void InvalidNumber(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void LineBusy(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void NYCTemp(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void Time(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void RingTimer(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void PlaybackDone(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void OffHookTimer(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void LoopTimer(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void Looptimer(TelephoneContext s)
        {
            Default(s);
            return;
        }

        protected void Default(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : CallMap.Default");
            }

            throw (new statemap.TransitionUndefinedException("State: " +
                                                             s.getState().getName() +
                                                             ", Transition: " +
                                                             s.getTransition()));
        }

        private static final class CallMap_Initialized
            extends CallMap_Default
        {
            private CallMap_Initialized(String name)
            {
                super(name);
            }

            protected void Default(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Initialized.Default");
                }

                (s.getOwner()).writeTransition("Default");

                return;
            }

            protected void Start(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Initialized.Start");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("Start");

                s.setState(CallMap.OnHook);
                (s.getOwner()).writeState("CallMap.OnHook");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_OnHook
            extends CallMap_Default
        {
            private CallMap_OnHook(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "updateClock();");
                (s.getOwner()).updateClock();

                (s.getOwner()).writeStateAction("Entry",
                                                "startClockTimer();");
                (s.getOwner()).startClockTimer();
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"ClockTimer\");");
                (s.getOwner()).stopTimer("ClockTimer");
                return;
            }

            protected void ClockTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.OnHook.ClockTimer");
                }


                TelephoneState endState = s.getState();

                s.clearState();

                (s.getOwner()).writeTransition("ClockTimer");

                (s.getOwner()).writeTransAction("updateClock();");
                (s.getOwner()).updateClock();

                (s.getOwner()).writeTransAction("startClockTimer();");
                (s.getOwner()).startClockTimer();

                s.setState(endState);
                return;
            }

            protected void DialingDone(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.OnHook.DialingDone");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("DialingDone");

                (s.getOwner()).writeTransAction("routeCall();");
                (s.getOwner()).routeCall();

                s.setState(CallMap.Routing);
                (s.getOwner()).writeState("CallMap.Routing");

                (s.getState()).Entry(s);
                return;
            }

            protected void InvalidDigit(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.OnHook.InvalidDigit");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("InvalidDigit");

                s.setState(CallMap.InvalidDigit);
                (s.getOwner()).writeState("CallMap.InvalidDigit");

                (s.getState()).Entry(s);
                return;
            }

            protected void LeftOffHook(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.OnHook.LeftOffHook");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("LeftOffHook");

                s.setState(CallMap.LeftOffHook);
                (s.getOwner()).writeState("CallMap.LeftOffHook");

                (s.getState()).Entry(s);
                return;
            }

            protected void OffHook(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.OnHook.OffHook");
                }


                TelephoneState currentState = s.getState();

                s.clearState();

                (s.getOwner()).writeTransition("OffHook");

                (s.getOwner()).writeTransAction("clearDisplay();");
                (s.getOwner()).clearDisplay();

                (s.getOwner()).writeTransAction("setReceiver(\"on hook\", \"Put down receiver\");");
                (s.getOwner()).setReceiver("on hook", "Put down receiver");

                s.setState(currentState);
                s.pushState(PhoneNumber.DialTone);
                (s.getOwner()).writeState("PhoneNumber.DialTone");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_Routing
            extends CallMap_Default
        {
            private CallMap_Routing(String name)
            {
                super(name);
            }

            protected void DepositMoney(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.DepositMoney");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("DepositMoney");

                s.setState(CallMap.DepositMoney);
                (s.getOwner()).writeState("CallMap.DepositMoney");

                (s.getState()).Entry(s);
                return;
            }

            protected void Emergency(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.Emergency");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("Emergency");

                (s.getOwner()).writeTransAction("playEmergency();");
                (s.getOwner()).playEmergency();

                s.setState(CallMap.PlayingMessage);
                (s.getOwner()).writeState("CallMap.PlayingMessage");

                (s.getState()).Entry(s);
                return;
            }

            protected void InvalidNumber(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.InvalidNumber");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("InvalidNumber");

                (s.getOwner()).writeTransAction("playInvalidNumber();");
                (s.getOwner()).playInvalidNumber();

                s.setState(CallMap.PlayingMessage);
                (s.getOwner()).writeState("CallMap.PlayingMessage");

                (s.getState()).Entry(s);
                return;
            }

            protected void LineBusy(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.LineBusy");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("LineBusy");

                s.setState(CallMap.BusySignal);
                (s.getOwner()).writeState("CallMap.BusySignal");

                (s.getState()).Entry(s);
                return;
            }

            protected void NYCTemp(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.NYCTemp");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("NYCTemp");
                
                s.setState(CallMap.NYCTemp);
                (s.getOwner()).writeState("CallMap.NYCTemp");

                (s.getState()).Entry(s);
                return;
            }

            protected void Time(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Routing.Time");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("Time");

                s.setState(CallMap.Time);
                (s.getOwner()).writeState("CallMap.Time");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_NYCTemp
            extends CallMap_Default
        {
            private CallMap_NYCTemp(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"ringing\");");
                (s.getOwner()).loop("ringing");

                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"RingTimer\", 10000);");
                (s.getOwner()).startTimer("RingTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"ringing\");");
                (s.getOwner()).stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.NYCTemp.RingTimer");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("RingTimer");

                (s.getOwner()).writeTransAction("playNYCTemp();");
                (s.getOwner()).playNYCTemp();

                s.setState(CallMap.PlayingMessage);
                (s.getOwner()).writeState("CallMap.PlayingMessage");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_Time
            extends CallMap_Default
        {
            private CallMap_Time(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"ringing\");");
                (s.getOwner()).loop("ringing");

                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"RingTimer\", 10000);");
                (s.getOwner()).startTimer("RingTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"ringing\");");
                (s.getOwner()).stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.Time.RingTimer");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("RingTimer");

                (s.getOwner()).writeTransAction("playTime();");
                (s.getOwner()).playTime();

                s.setState(CallMap.PlayingMessage);
                (s.getOwner()).writeState("CallMap.PlayingMessage");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_DepositMoney
            extends CallMap_Default
        {
            private CallMap_DepositMoney(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"ringing\");");
                (s.getOwner()).loop("ringing");

                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"RingTimer\", 5000);");
                (s.getOwner()).startTimer("RingTimer", 5000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"ringing\");");
                (s.getOwner()).stopLoop("ringing");
                return;
            }

            protected void RingTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.DepositMoney.RingTimer");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("RingTimer");

                (s.getOwner()).writeTransAction("playDepositMoney();");
                (s.getOwner()).playDepositMoney();

                s.setState(CallMap.PlayingMessage);
                (s.getOwner()).writeState("CallMap.PlayingMessage");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_BusySignal
            extends CallMap_Default
        {
            private CallMap_BusySignal(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"busy\");");
                (s.getOwner()).loop("busy");
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"busy\");");
                (s.getOwner()).stopLoop("busy");
                return;
            }
        }

        private static final class CallMap_PlayingMessage
            extends CallMap_Default
        {
            private CallMap_PlayingMessage(String name)
            {
                super(name);
            }

            protected void OnHook(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.PlayingMessage.OnHook");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("OnHook");

                (s.getOwner()).writeTransAction("stopPlayback();");
                (s.getOwner()).stopPlayback();

                (s.getOwner()).writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\");");
                (s.getOwner()).setReceiver("off hook", "Pick up receiver");

                (s.getOwner()).writeTransAction("clearDisplay();");
                (s.getOwner()).clearDisplay();

                s.setState(CallMap.OnHook);
                (s.getOwner()).writeState("CallMap.OnHook");

                (s.getState()).Entry(s);
                return;
            }

            protected void PlaybackDone(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.PlayingMessage.PlaybackDone");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("PlaybackDone");

                s.setState(CallMap.MessagePlayed);
                (s.getOwner()).writeState("CallMap.MessagePlayed");

                (s.getState()).Entry(s);
                return;
            }

            protected void Stop(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.PlayingMessage.Stop");
                }

                (s.getState()).Exit(s);
                s.clearState();

                (s.getOwner()).writeTransition("Stop");

                (s.getOwner()).writeTransAction("stopPlayback();");
                (s.getOwner()).stopPlayback();

                (s.getOwner()).writeTransAction("setReceiver(\"off hook\", \"Pick up receiver\");");
                (s.getOwner()).setReceiver("off hook", "Pick up receiver");

                (s.getOwner()).writeTransAction("clearDisplay();");
                (s.getOwner()).clearDisplay();

                s.setState(CallMap.Initialized);
                (s.getOwner()).writeState("CallMap.Initialized");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_MessagePlayed
            extends CallMap_Default
        {
            private CallMap_MessagePlayed(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void OffHookTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.MessagePlayed.OffHookTimer");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("OffHookTimer");

                s.setState(CallMap.LeftOffHook);
                (s.getOwner()).writeState("CallMap.LeftOffHook");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_LeftOffHook
            extends CallMap_Default
        {
            private CallMap_LeftOffHook(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"LoopTimer\", 10000);");
                (s.getOwner()).startTimer("LoopTimer", 10000);

                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"phone_off_hook\");");
                (s.getOwner()).loop("phone_off_hook");
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"LoopTimer\");");
                (s.getOwner()).stopTimer("LoopTimer");

                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"phone_off_hook\");");
                (s.getOwner()).stopLoop("phone_off_hook");
                return;
            }

            protected void Default(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.LeftOffHook.Default");
                }

                (s.getOwner()).writeTransition("Default");

                return;
            }

            protected void LoopTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.LeftOffHook.LoopTimer");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("LoopTimer");

                s.setState(CallMap.WaitForOnHook);
                (s.getOwner()).writeState("CallMap.WaitForOnHook");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_InvalidDigit
            extends CallMap_Default
        {
            private CallMap_InvalidDigit(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"LoopTimer\", 10000);");
                (s.getOwner()).startTimer("LoopTimer", 10000);

                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"fast_busy\");");
                (s.getOwner()).loop("fast_busy");
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"LoopTimer\");");
                (s.getOwner()).stopTimer("LoopTimer");

                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"fast_busy\");");
                (s.getOwner()).stopLoop("fast_busy");
                return;
            }

            protected void Default(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.InvalidDigit.Default");
                }

                (s.getOwner()).writeTransition("Default");

                return;
            }

            protected void LoopTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.InvalidDigit.LoopTimer");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("LoopTimer");

                s.setState(CallMap.WaitForOnHook);
                (s.getOwner()).writeState("CallMap.WaitForOnHook");

                (s.getState()).Entry(s);
                return;
            }
        }

        private static final class CallMap_WaitForOnHook
            extends CallMap_Default
        {
            private CallMap_WaitForOnHook(String name)
            {
                super(name);
            }

            protected void Default(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : CallMap.WaitForOnHook.Default");
                }

                (s.getOwner()).writeTransition("Default");

                return;
            }
        }
    }

    protected static abstract class PhoneNumber
    {
        private static PhoneNumber_Default.PhoneNumber_DialTone DialTone;
        private static PhoneNumber_Default.PhoneNumber_LongDistance LongDistance;
        private static PhoneNumber_Default.PhoneNumber_OneOneStart OneOneStart;
        private static PhoneNumber_Default.PhoneNumber_NineOne NineOne;
        private static PhoneNumber_Default.PhoneNumber_Exchange Exchange;
        private static PhoneNumber_Default.PhoneNumber_LocalCall LocalCall;
        private static PhoneNumber_Default Default;

        static
        {
            DialTone = new PhoneNumber_Default.PhoneNumber_DialTone("PhoneNumber.DialTone");
            LongDistance = new PhoneNumber_Default.PhoneNumber_LongDistance("PhoneNumber.LongDistance");
            OneOneStart = new PhoneNumber_Default.PhoneNumber_OneOneStart("PhoneNumber.OneOneStart");
            NineOne = new PhoneNumber_Default.PhoneNumber_NineOne("PhoneNumber.NineOne");
            Exchange = new PhoneNumber_Default.PhoneNumber_Exchange("PhoneNumber.Exchange");
            LocalCall = new PhoneNumber_Default.PhoneNumber_LocalCall("PhoneNumber.LocalCall");
            Default = new PhoneNumber_Default("PhoneNumber.Default");
        }

    }

    protected static class PhoneNumber_Default
        extends TelephoneState
    {
        protected PhoneNumber_Default(String name)
        {
            super(name);
        }

        protected void OffHookTimer(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : PhoneNumber.Default.OffHookTimer");
            }

            (s.getState()).Exit(s);
            s.clearState();

            (s.getOwner()).writeTransition("Default.OffHookTimer");

            (s.getOwner()).writeTransAction("clearDisplay();");
            (s.getOwner()).clearDisplay();

            s.popState();
            (s.getOwner()).writeState((s.getState()).getName());

            (s.getState()).LeftOffHook(s);
            return;
        }

        protected void OnHook(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : PhoneNumber.Default.OnHook");
            }

            (s.getState()).Exit(s);
            s.clearState();

            (s.getOwner()).writeTransition("Default.OnHook");

            (s.getOwner()).writeTransAction("clearDisplay();");
            (s.getOwner()).clearDisplay();

            s.popState();
            (s.getOwner()).writeState((s.getState()).getName());

            (s.getState()).OnHook(s);
            return;
        }

        protected void Stop(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : PhoneNumber.Default.Stop");
            }

            (s.getState()).Exit(s);
            s.clearState();

            (s.getOwner()).writeTransition("Default.Stop");

            (s.getOwner()).writeTransAction("clearDisplay();");
            (s.getOwner()).clearDisplay();

            s.popState();
            (s.getOwner()).writeState((s.getState()).getName());

            (s.getState()).Stop(s);
            return;
        }

        protected void ClockTimer(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : PhoneNumber.Default.ClockTimer");
            }

            (s.getOwner()).writeTransition("Default.ClockTimer");

            return;
        }

        protected void Digit(TelephoneContext s, String n)
        {
            Default(s);
            return;
        }

        protected void Default(TelephoneContext s)
        {
            if (s.getDebugFlag() == true)
            {
                System.err.println("TRANSITION   : PhoneNumber.Default");
            }

            throw (new statemap.TransitionUndefinedException("State: " +
                                                             s.getState().getName() +
                                                             ", Transition: " +
                                                             s.getTransition()));
        }

        private static final class PhoneNumber_DialTone
            extends PhoneNumber_Default
        {
            private PhoneNumber_DialTone(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "loop(\"dialtone\");");
                (s.getOwner()).loop("dialtone");

                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");

                (s.getOwner()).writeStateAction("Exit",
                                                "stopLoop(\"dialtone\");");
                (s.getOwner()).stopLoop("dialtone");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.DialTone.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidDigit(s);
                }
                else if ((s.getOwner()).equal(n, 1))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("setType(Telephone.LONG_DISTANCE);");
                    (s.getOwner()).setType(Telephone.LONG_DISTANCE);

                    (s.getOwner()).writeTransAction("saveAreaCode(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveAreaCode(n);

                    (s.getOwner()).writeTransAction("addDisplay(\"-\");");
                    (s.getOwner()).addDisplay("-");

                    s.setState(PhoneNumber.LongDistance);
                    (s.getOwner()).writeState("PhoneNumber.LongDistance");

                    (s.getState()).Entry(s);
                }
                else if ((s.getOwner()).equal(n, 9))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveLocal(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    s.setState(PhoneNumber.OneOneStart);
                    (s.getOwner()).writeState("PhoneNumber.OneOneStart");

                    (s.getState()).Entry(s);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("setType(Telephone.LOCAL);");
                    (s.getOwner()).setType(Telephone.LOCAL);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    s.setState(PhoneNumber.Exchange);
                    (s.getOwner()).writeState("PhoneNumber.Exchange");

                    (s.getState()).Entry(s);
                }

                return;
            }

            protected void OffHookTimer(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.DialTone.OffHookTimer");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("OffHookTimer");

                s.popState();
                (s.getOwner()).writeState((s.getState()).getName());

                (s.getState()).LeftOffHook(s);
                return;
            }

            protected void OnHook(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.DialTone.OnHook");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("OnHook");

                s.popState();
                (s.getOwner()).writeState((s.getState()).getName());

                (s.getState()).OnHook(s);
                return;
            }

            protected void Stop(TelephoneContext s)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.DialTone.Stop");
                }

                (s.getState()).Exit(s);

                (s.getOwner()).writeTransition("Stop");

                s.popState();
                (s.getOwner()).writeState((s.getState()).getName());

                (s.getState()).Stop(s);
                return;
            }
        }

        private static final class PhoneNumber_LongDistance
            extends PhoneNumber_Default
        {
            private PhoneNumber_LongDistance(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.LongDistance.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidDigit(s);
                }
                else if (!(s.getOwner()).isCodeComplete())
                {

                    TelephoneState endState = s.getState();

                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveAreaCode(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveAreaCode(n);

                    (s.getOwner()).writeTransAction("resetTimer(\"OffHookTimer\");");
                    (s.getOwner()).resetTimer("OffHookTimer");

                    s.setState(endState);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveAreaCode(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveAreaCode(n);

                    (s.getOwner()).writeTransAction("addDisplay(\"-\");");
                    (s.getOwner()).addDisplay("-");

                    s.setState(PhoneNumber.Exchange);
                    (s.getOwner()).writeState("PhoneNumber.Exchange");

                    (s.getState()).Entry(s);
                }

                return;
            }
        }

        private static final class PhoneNumber_OneOneStart
            extends PhoneNumber_Default
        {
            private PhoneNumber_OneOneStart(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.OneOneStart.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidDigit(s);
                }
                else if ((s.getOwner()).equal(n, 1))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    s.setState(PhoneNumber.NineOne);
                    (s.getOwner()).writeState("PhoneNumber.NineOne");

                    (s.getState()).Entry(s);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("setType(Telephone.Local);");
                    (s.getOwner()).setType(Telephone.LOCAL);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    s.setState(PhoneNumber.Exchange);
                    (s.getOwner()).writeState("PhoneNumber.Exchange");

                    (s.getState()).Entry(s);
                }

                return;
            }
        }

        private static final class PhoneNumber_NineOne
            extends PhoneNumber_Default
        {
            private PhoneNumber_NineOne(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.NineOne.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidNumber(s);
                }
                else if ((s.getOwner()).equal(n, 1))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("setType(Telephone.EMERGENCY);");
                    (s.getOwner()).setType(Telephone.EMERGENCY);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).DialingDone(s);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("setType(Telephone.LOCAL);");
                    (s.getOwner()).setType(Telephone.LOCAL);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    (s.getOwner()).writeTransAction("addDisplay(\"-\");");
                    (s.getOwner()).addDisplay("-");

                    s.setState(PhoneNumber.LocalCall);
                    (s.getOwner()).writeState("PhoneNumber.LocalCall");

                    (s.getState()).Entry(s);
                }

                return;
            }
        }

        private static final class PhoneNumber_Exchange
            extends PhoneNumber_Default
        {
            private PhoneNumber_Exchange(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.Exchange.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidDigit(s);
                }
                else if (!(s.getOwner()).isExchangeComplete())
                {

                    TelephoneState endState = s.getState();

                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    (s.getOwner()).writeTransAction("resetTimer(\"OffHookTimer\");");
                    (s.getOwner()).resetTimer("OffHookTimer");

                    s.setState(endState);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveExchange(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveExchange(n);

                    (s.getOwner()).writeTransAction("addDisplay(\"-\");");
                    (s.getOwner()).addDisplay("-");

                    s.setState(PhoneNumber.LocalCall);
                    (s.getOwner()).writeState("PhoneNumber.LocalCall");

                    (s.getState()).Entry(s);
                }

                return;
            }
        }

        private static final class PhoneNumber_LocalCall
            extends PhoneNumber_Default
        {
            private PhoneNumber_LocalCall(String name)
            {
                super(name);
            }

            protected void Entry(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Entry",
                                                "startTimer(\"OffHookTimer\", 10000);");
                (s.getOwner()).startTimer("OffHookTimer", 10000);
                return;
            }

            protected void Exit(TelephoneContext s)
            {
                (s.getOwner()).writeStateAction("Exit",
                                                "stopTimer(\"OffHookTimer\");");
                (s.getOwner()).stopTimer("OffHookTimer");
                return;
            }

            protected void Digit(TelephoneContext s, String n)
            {
                if (s.getDebugFlag() == true)
                {
                    System.err.println("TRANSITION   : PhoneNumber.LocalCall.Digit(String n)");
                }

                (s.getOwner()).writeTransition("Digit(" +
                                               n +
                                               ")");

                if (!(s.getOwner()).isDigitValid(n))
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("clearDisplay();");
                    (s.getOwner()).clearDisplay();

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).InvalidDigit(s);
                }
                else if (!(s.getOwner()).isLocalComplete())
                {

                    TelephoneState endState = s.getState();

                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveLocal(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveLocal(n);

                    (s.getOwner()).writeTransAction("resetTimer(\"OffHookTimer\");");
                    (s.getOwner()).resetTimer("OffHookTimer");

                    s.setState(endState);
                }
                else
                {
                    (s.getState()).Exit(s);
                    s.clearState();

                    (s.getOwner()).writeTransAction("playTT(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).playTT(n);

                    (s.getOwner()).writeTransAction("saveLocal(" +
                                                    n +
                                                    ");");
                    (s.getOwner()).saveLocal(n);

                    s.popState();
                    (s.getOwner()).writeState((s.getState()).getName());

                    (s.getState()).DialingDone(s);
                }

                return;
            }
        }
    }
}
