


                              SMC -
                   The State Machine Compiler


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

Run SMC with Java 1.4.1 or later.

$ java -jar Smc.jar <options> <.sm source files>

SMC's command line options are:

    -suffix <suffix> 
             : Use this suffix on the output file.
    -g       : Add debugging to generated code.
    -version : Print SMC version to standard out and exit.
    -help    : Print this message to standard out and exit.
    -sync    : Synchronize generated Java code
               (use with -java and -vb only).
    -noex    : Do not generate C++ exception throws
               (use with -c++ only).
    -nocatch : Do not generate try/catch/rethrow code
               (not recommended).
    -cast <cast type>
             : use this C++ cast type (use with -c++ only).
    -serial  : Generate serialization code (use with -c++, -java,
               -tcl, -vb and -csharp only).
    -d <directory>
             : Placed generated files in directory.
    -nostreams
             : Do not use C++ iostream (use with -c++ only).
    -return  : Smc.main() does not exit but simply returns.
               Use this option with ANT.
               Do *not* use this option with make.
    -glevel <0 - 2>
             : Graph detail level from 0 (least) to 2 (greatest)
               (use with -graph only).
    -c++     : Generate C++ code.
    -java    : Generate Java code.
    -tcl     : Generate [incr Tcl] code.
    -vb      : Generate VB.Net code.
    -csharp  : Generate C# code.
    -python  : Generate Python code.
    -table   : Generate an HTML table.
    -graph   : Generate a Graphviz DOT file.

    NOTE: Source files *must* end in ".sm" or ".SM".
          Exactly one of the following must be specified:
          + -c++
          + -java
          + -tcl
          + -vb
          + -csharp
          + -python
          + -table
          + -graph
