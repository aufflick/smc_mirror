


                              SMC -
                     The State Map Compiler


+ Building & Installing Smc.jar
-------------------------------

1. Make certain that the most recent smc/lib/Java/statemap.jar
   is built and that ../lib/Java/statemap directory contains
   FSMContext.class and State.class. (See smc/lib/Java/README.txt
   regarding how to build statemap.jar).

2. If you have build Smc.jar previously and want to perform a
   complete build, then do:

   $ make clean

3. Build Smc.jar:

   $ make Smc.jar

4. To install Smc.jar in the Smc distribution directory tree, do:

   $ make install

   The distribution directory is named staging/smc.



+ Executing Smc.jar
-------------------

Run SMC with Java 1.2 or later.

$ java -jar Smc.jar <options> <.sm source file>

SMC's command line options are:

    -suffix  : Add this suffix to output file.
    -g       : Add debugging to generated code.
    -version : Print SMC version to standard out and exit.
    -help    : Print this message to standard out and exit.
    -sync    : Synchronize generated Java code (use with -java only).
    -c++     : Generate C++ code.
    -java    : Generate Java code.
    -tcl     : Generate [incr Tcl] code.

    NOTE: Source file *must* end in ".sm".
          Only one of "-c++", "-java" and "-tcl" may specified.
