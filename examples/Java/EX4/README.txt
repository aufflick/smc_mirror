


                         Java Example 4


Simplistic, graphical simulation of a stoplight. Demonstrates
how to use state maps to handle external events (in this case
timeouts).


+ Building
----------

NOTE: Smc.jar must be built and installed.

Unix & Windows:
    $ make Traffic


+ Executing
-----------

Unix:

    $ java -classpath ${CLASSPATH}:../../../lib/statemap.jar Traffic

Windows:

    $ java -classpath "${CLASSPATH};../../../lib/statemap.jar" Traffic

Click on the "Configure..." button and modify the demo's
settings. Increase the vehicle speed and appearance
rate. Decrease the stoplight times.

Also, click on "Pause" and "Continue". "Stop" halts the
demonstration but does not terminate the demo.
