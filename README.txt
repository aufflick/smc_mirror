

                               SMC
                     The State Machine Compiler
                         (Version: 4.3.0)

                     http://smc.sourceforge.net



0. What's New?
--------------

New Features:

+ Added -reflect option for Java, C#, VB.Net and Tcl code
  generation. When used, allows applications to query a state
  about its supported transitions. Returns a list of transition
  names. This feature is useful to GUI developers who want to
  enable/disable features based on the current state. See
  Programmer's Manual section 11: On Reflection for more
  information.

+ Updated LICENSE.txt with a missing final paragraph which allows
  MPL 1.1 covered code to work with the GNU GPL.

+ Added a Maven plug-in and an ant task to a new tools directory.
  Added Eiten Suez's SMC tutorial (in PDF) to a new docs
  directory.


Bug Fixes:

+ (GraphViz) DOT file generation did not properly escape
  double quotes appearing in transition guards. This has been
  corrected.

+ A note: the SMC FAQ incorrectly stated that C/C++ generated
  code is thread safe. This is wrong. C/C++ generated is
  certainly *not* thread safe. Multi-threaded C/C++ applications
  are required to synchronize access to the FSM to allow for
  correct performance.

+ (Java) The generated getState() method is now public.



1. System Requirements
----------------------

+ JRE (Standard Edition) 1.4.1 or better.
+ Whatever JRE's requirements are (see http://java.sun.com/j2se/
  for more information).


2. Introduction
---------------

If you use state machines to define your objects behavior and are
tired of the time-consuming, error-prone work of implementing
those state machines as state transition matrices or widely
scattered switch statements, then SMC is what you're looking for.

SMC takes a state machine definition and generates State pattern
classes implementing that state machine. The only code you need
to add to your object is 1) create the state machine object and
2) issue transitions. ITS THAT EASY.

+ NO, you don't have to inherit any state machine class.
+ NO, you don't have to implement any state machine interface.

YES, you add to your class constructor:

        _myFSM = new MyClassContext(this);

YES, you issue state transitions:

        _myFSM.HandleMessage(msg);

Congratulations! You've integrated a state machine into your object.

SMC is written in Java and is truly "Write once, run anywhere".
If you have at least the Java Standard Edition v. 1.4.1 loaded,
then you can run SMC (if you have the Java Enterpise Edition, so
much the better!)

Java Standard Edition can be downloaded for FREE from

                    http://java.sun.com/j2se/

SMC currently supports nine programming languages:
  1. C,
  2. C++,
  3. C#,
  4. Java,
  5. Perl,
  6. Python,
  7. Ruby,
  8. [incr Tcl] and
  9. VB.Net.

SMC is also able to generate an HTML table representation of your
FSM and a GraphViz DOT file representation
(http://www.graphviz.org).


3. Download
-----------

Surf over to http://smc.sourceforge.net and check out
"File Releases". The latest SMC version is 3.0.0.
SMC downloads come in two flavors: tar/gzip (for Unix)
and self-extracting zip file (for Windows).

The download package contains the executable Smc.jar and
supporting library: statemap.h (for C++), statemap.jar
(for Java), statemap.tcl & pkgIndex.tcl (for Tcl),
statemap.dll (for VB.Net) and statemap.dll (for C#).

NOTE: Only the SMC-generated code uses these libraries. Your code
doesn't even know they exist. However, when compiling your
application, you will need to add a
    -I<path to statemap.h directory>
or
    -classpath ...:<path to statemap.jar>
to your compile command (when running you Java application, you
also need to add statemap.jar to your classpath).

The download package's directory layout is:

    Smc -+-LICENSE.txt
         |
         +-README.txt
         |
         +-bin---Smc.jar 
         |
         +-docs--SMC_Tutorial.pdf
         |
         +-lib-+-statemap.h
         |     |
         |     +-statemap.jar
         |     |
         |     +-setup.py
         |     |
         |     +-statemap.py
         |     |
         |     +-C---statemap.h
         |     |
         |     +-CSharp-+-Debug-+-statemap.dll
         |     |        |       |
         |     |        |       +-statemap.pdb
         |     |        |
         |     |        +-Release-+-statemap.dll
         |     |
         |     +-Perl-+-MANIFEST
         |     |      |
         |     |      +-Makefile.pl
         |     |      |
         |     |      +-README
         |     |      |
         |     |      +-Statemap.pm
         |     |      |
         |     |      +-test.pl
         |     |
         |     +-Ruby-+-README
         |     |      |
         |     |      +-statemap.rb
         |     |
         |     +-VB-+-Debug-+-statemap.dll
         |     |    |       |
         |     |    |       +-statemap.pdb
         |     |    |
         |     |    +-Release---statemap.dll
         |     |
         |     +-statemap-+-FSMContext.class
         |     |          |
         |     |          +-State.class
         |     |          |
         |     |          +-StateUndefinedException.class
         |     |          |
         |     |          +-TransitionUndefinedException.class
         |     |
         |     +-statemap1.0-+-statemap.tcl
         |                   |
         |                   +-pkgIndex.tcl
         |
         +-misc-+-smc.ico (smc Windows icon)
         |
         +-examples-+-C++--+-EX1 (C++ source code and build files)
         |          |      |
         |          |      +-EX2
         |          |      |
         |          |      +-EX3
         |          |      |
         |          |      +-EX4
         |          |      |
         |          |      +-EX5
         |          |      |
         |          |      +-EX6
         |          |
         |          +-Java-+-EX1 (Java source code, Makefiles)
         |          |      |
         |          |      +-EX2
         |          |      |
         |          |      +-EX3
         |          |      |
         |          |      +-EX4
         |          |      |
         |          |      +-EX5
         |          |      |
         |          |      +-EX6
         |          |      |
         |          |      +-EX7
         |          |
         |          +-Tcl--+-EX1 (Tcl source code)
         |          |      |
         |          |      +-EX2
         |          |      |
         |          |      +-EX3
         |          |      |
         |          |      +-EX4
         |          |      |
         |          |      +-EX5
         |          |
         |          +-VB---+-EX1 (VB.Net source code)
         |          |      |
         |          |      +-EX2
         |          |      |
         |          |      +-EX3
         |          |      |
         |          |      +-EX4
         |          |
         |          +-CSharp-+-EX1 (C# source code)
         |          |        |
         |          |        +-EX3
         |          |
         |          +-Ant--+-EX1 (Java source code, Ant built)
         |          |      |
         |          |      +-EX2
         |          |      |
         |          |      +-EX3
         |          |      |
         |          |      +-EX4
         |          |      |
         |          |      +-EX5
         |          |      |
         |          |      +-EX6
         |          |      |
         |          |      +-EX7
         |          |
         |          +-Python-+-EX1 (Python source code)
         |                   |
         |                   +-EX2
         |                   |
         |                   +-EX3
         |                   |
         |                   +-EX4
         |                   |
         |                   +-EX7
         |
         +-tools-+-maven-+-plugin.jelly
                 |       |
                 |       +-plugin.properties
                 |       |
                 |       +-project.xml
                 |
                 +-smc-anttask-+-.classpath
                               |
                               +-.project
                               |
                               +-build.xml
                               |
                               +-smc-anttask.iml
                               |
                               +-smc-anttask.ipr
                               |
                               +-smc-anttask.iws
                               |
                               +-build---classes---...
                               |
                               +-dist---smc-ant.jar
                               |
                               +-lib---ant.jar
                               |
                               +-src---net---sf---smc---ant---SmcJarWrapper.java


4. Installation
---------------

After downloading SMC (either tar/gzip or self-extracting zip
file), you install SMC as follows:

1. Figure out where you can to load the Smc directory and place
   the SMC package there.
2. If you already have an "smc" directory/folder, change its name
   to something like "smc_old" or "smc_1_2_0". This will prevent
   its contents from being overwritten in case you want to back
   out of the new version. Once you are satisfied with the new
   version, you may delete the old SMC.
3. Load the SMC package:
    (Unix) $ tar xvfz Smc_4_0_0.tgz
    (Windows) running Smc_4_0_0.zip

You're done! There really is nothing more that needs to be done.
You may want to take the following steps.

+ Add the full path to .../Smc/bin to your PATH environment
  variable.
+ Add the full path to statemap.jar to your CLASSPATH environment
  variable.
+ Add the full path to .../Smc/lib to your TCLLIBPATH environment
  variable.

The tools directory includes a Maven plug-in (http://www.maven.org)
and an ant task to help integrate SMC into other development
environments.

An Eclipse plug-in is not yet available.


5. Examples
-----------

The examples directory contains example SMC-based applications.
The examples range from trivial (EX1) to sophisticated (EX5).
Use these examples together with the SMC Programmer's Guide to
learn how to use SMC.

The C++ examples provide Makefiles, Microsoft DevStudio 6.0
workspace and DevStudio 7.0 solution.

The Java examples in examples/Java use "make" for building.
The same examples also appear in examples/Ant and use "ant".

The [incr Tcl] examples are not built and require you to
execute "java -jar Smc.jar" by hand.

The VB.Net and C# examples use DevStudio 7.0.

To learn more about each example and how to build & run each one,
read the example's README.txt.


6. FAQ/Documentation/Reporting Bugs/Latest News
-----------------------------------------------

Surf over to http://smc.sourceforge.net to:

+ Read the SMC Frequently Asked Questions (FAQ).
+ Download documentation - including the SMC Programmer's Guide.
+ Talk with other SMC users in Public Forums.
+ Report bugs.
+ Get the latest news about SMC.
+ Access SMC source code via a CVS web interface.
+ Check out docs/SMC_Tutorial.pdf.


7. Notices
----------

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
