


                              SMC -
                     The State Map Compiler
                      Version 1.0, Beta 3


+ New Feature - Transition Parameters
-------------------------------------

You can now pass parameters into a transtion. You specify a
transition's parameters using the following syntax:

    OnLogin(name: const char*, passwd: const char*, grp_id: int)
    [isLoginValid(name, passwd);]
        {startLoginShell(name, passwd, grp_id);}

    OnLogin(name: const char*, passwd: const char*, grp_id: int)
    [maxLoginAttemptsReached(name);]
        {disconnect();}

    OnLogin(name: const char*, passwd: const char*, grp_id: int)
        {displayLoginPrompt();}

The format is: <transition>[(var: type[, var: type]+)]
If the transition takes no parameters, you may omit the parens
"()". Empty parens work as well.

NOTE: When a transition has multiple definitions (as in the above
example), then each transition definition *must* have exactly the
same parameter list. Transitions with the same name but different
parameter lists are *different* transitions.



+ Almost Feature - Transition Queuing
-------------------------------------

I put a "transition queuing" feature into SMC wherein, if an
object issued a transition from within a transition, the new
transition is queued until the current transition completes.

While it seemed a code idea, it spawned evil code containing
insidous bugs that are difficult to find:

+ The overarching problem is that SMC leads you to believe that a
  transition has completly executed after the transition call
  returns. With transition queuing, this is simply not true. Code
  which depends on this "transition completed upon return" will
  perform incorrectly.

+ Transition queuing + transition parameters = Big Problems.
  Passing anything other than simple, built-in types to a
  queued transition is a recipe for disastor. I couldn't figure
  out a way to deep-copy the transition's arguments (because I am
  determined that SMC be target-language neutral), so I saved
  only the argument reference. But now the application developer
  must be careful not to delete the objects until after the
  transition has been taken. This means adding transition actions
  which delete the transition's arguments plus locating the
  arguments in persistent memory. Overall, very ugly.

So, I have decided to punt on transition queuing and leave it up
to the developer to handle this problem. Objects can now query
their state map context to see if a transition is in progress:

    bool AppClassContext::isInTransition();

If the state map is in transition, then true is returned. The
application must not issue a transition now and should queue up
that transition for later. If isInTransition() returns false,
then a transition may be issued.

I have added a lengthy discussion to the SMC Programmer's Manual
discussing how to develop state maps which avoid this
transition-within-transition problem. In fact, I describe how to
design state maps in general.
