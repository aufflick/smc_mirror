


                          Tcl Example 4


Simplistic, graphical simulation of a stoplight. Demonstrates
how to use state maps to handle external events (in this case
timeouts).


+ Building
----------

NOTE: Smc.jar must be built and installed.

Unix & Windows:
    $ java -jar ../../../bin/Smc.jar Stoplight.sm
    $ java -jar ../../../bin/Smc.jar Vehicle.sm


+ Executing
-----------

Unix & Windows

    $ wish traffic.tcl

    Click on the "Configure..." button and modify the demo's
    settings. Increase the vehicle speed and appearance
    rate. Decrease the stoplight times.

    Also, click on "Pause" and "Continue". "Stop" halts the
    demonstration but does not terminate the demo.
