


                              SMC -
                     The State Map Compiler
                      Version 1.0.1


+ Bug Fixes - Default transitions
---------------------------------

Modified generated code so that all default transitions are now
defined in the <appclass>State class and not in each of the
individual map classes. The resulting generated code is
equivalent to the previous code *except* it now throws a
TransitionUndefinedException when a transition is issued in a
map which does not define that exception. Also, the generated
code has been reduced and is easier to read.

Also updated generate [incr Tcl] code so that it is now
up-to-date with the C++ and Java class structure.


+ Bug Fixes - Generated Java code thread safe
---------------------------------------------

The generated Java code is now thread safe. Now the same state
machine can be accessed from multiple threads safely.




                              SMC -
                     The State Map Compiler
                      Version 1.0.0, Beta 4


+ Bug Fixes - Entry/Exit Actions
--------------------------------

SMC previously executed the current state's Exit actions and the
destination state's Entry actions on every transition.Experience
now shows that this is not correct behavior for loopback, push
and pop transitions. Entry and Exit actions are now executed
as follows, depending on the transition type:

+ Simple, non-loopback transition:
    1. Execute current state's Exit actions.
    2. Clear current state.
    3. Execute transition actions.
    4. Set current state to destination state.
    5. Execute destination state's Entry actions.

+ Loopback transition:
  Note: Entry and Exit actions *are* not executed.
    1. Save current state.
    2. Clear current state.
    3. Execute transition actions.
    4. Restore current state.

+ Push transition:
  Note: Only the Entry actions are executed.
    1. Push current state on top of state stack.
    2. Clear current state.
    3. Execute transition actions.
    4. Set current state to destination state.
    5. Execute destination state's Entry actions.

+ Pop transition:
  Note: Only the Exit actions are executed.
    1. Execute current state's Exit acions.
    2. Clear current state.
    3. Execute transition actions.
    4. Pop state off the top of the state stack and make it the
       current state.
    5. If the pop included a transition, issue that transition
       from the current state.




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
