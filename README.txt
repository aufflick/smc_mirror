

                               SMC
                     The State Map Compiler
                     (Version: 1.0.0)



0. System Requirements
----------------------

+ JRE (Standard Edition) 1.2.2 or better.
+ Whatever JRE's requirements are (see http://java.sun.com/j2se/
  for more information).


1. Introduction
---------------

If you use state maps to define your objects behavior and are
tired of the time-consuming, error-prone work of implementing
those state maps as state transition matrices or widely scattered
switch statements, then SMC is what you're looking for.

SMC takes a state map definition and generates State pattern
classes implementing that state map. The only code you need
to add to your object is 1) create the state map object and
2) issue transitions. ITS THAT EASY.

+ No, your object doesn't have to inherit any state map class.
+ No, your object doesn't have to implement any state map methods.

YES, your object's constructor does:

        _my_state_map = new MyClassContext(this);

Congratulations! You've integrated a statemap into your object.

SMC is written in Java and is truly "Write once, run anywhere".
If you have at least the Java Standard Edition v. 1.2.2 loaded,
then you can run SMC (if you have the Java Enterpise Edition, so
much the better!)

Java Standard Edition can be downloaded for FREE from

                    http://java.sun.com/j2se/

SMC currently supports three object-oriented languages: C++,
Java and [incr Tcl].


2. Download
-----------

Surf over to http://smc.sourceforge.net and check out
"File Releases". The latest SMC version is 1.0.0.
SMC downloads come in two flavors: tar/gzip (for Unix)
and self-extracting zip file (for Windows).

The download package contains the executable Smc.jar and
supporting library: statemap.h (for C++), statemap.jar (for Java)
and statemap.tcl & pkgIndex.tcl (for Tcl).

NOTE: Only the SMC-generated code uses these libraries. Your code
doesn't even know they exists. However, when compiling your
application, you will need to add a
    -I<path to statemap.h directory>
or
    -classpath ...:<path to statemap.jar>
to your compile command (when running you Java application, you
also need to add statemap.jar to your classpath).

The download package's directory layout is:

    Smc -+-README.txt
         |
         +-LICENSE.txt
         |
         +-bin---Smc.jar 
         |
         +-lib-+-statemap.h
         |     |
         |     +-statemap.jar
         |     |
         |     +-statemap1.0-+-statemap.tcl
         |                   |
         |                   +-pkgIndex.tcl
         |
         +-misc---smc.ico (smc Windows icon)
         |
         +-examples-+-C++--+-EX1 (C++ source code and build files)
                    |      |
                    |      +-EX2
                    |      |
                    |      +-EX3
                    |      |
                    |      +-EX4
                    |      |
                    |      +-EX5
                    |      |
                    |      +-EX6
                    |
                    +-Java-+-EX1 (Java source code)
                    |      |
                    |      +-EX2
                    |      |
                    |      +-EX3
                    |      |
                    |      +-EX4
                    |      |
                    |      +-EX5
                    |      |
                    |      +-EX6
                    |      |
                    |      +-EX7
                    |
                    +-Tcl--+-EX1 (Tcl source code)
                           |
                           +-EX2
                           |
                           +-EX3
                           |
                           +-EX4
                           |
                           +-EX5


3. Installation
---------------

After downloading SMC (either tar/gzip or self-extracting zip
file), you install SMC as follows:

1. Figure out where you can to load the Smc directory and place
   the SMC package there.
2. If you already have an "smc" directory/folder, change its name
   to something like "smc_old" or "smc_1_0_b4". This will prevent
   its contents from being overwritten in case you want to back
   out of the new version. Once you are satisfied with the new
   version, you may delete the old SMC.
3. Load the SMC package:
    (Unix) $ tar xvfz Smc_1_0_0.tgz
    (Windows) running Smc_1_0_0.zip

You're done! There really is nothing more that needs to be done.
You may want to take the following steps.

+ Add the full path to .../Smc/bin to your PATH environment
  variable.
+ Add the full path to statemap.jar to your CLASSPATH environment
  variable.
+ Add the full path to .../Smc/lib to your TCLLIBPATH environment
  variable.


4. Examples
-----------

The examples directory contains example SMC-based applications.
The examples range from trivial (EX1) to sophisticated (EX5).
Use these examples together with the SMC Programmer's Guide to
learn how to use SMC.

To learn more about each example and how to build & run each one,
read the example's README.txt.


5. FAQ/Documentation/Reporting Bugs/Latest News
-----------------------------------------------

Surf over to http://smc.sourceforge.net to:

+ Read the SMC Frequently Asked Questions (FAQ).
+ Download documentation - including the SMC Programmer's Guide.
+ Talk with other SMC users in Public Forums.
+ Report bugs.
+ Get the latest news about SMC.
+ Access SMC source code via a CVS web interface.
