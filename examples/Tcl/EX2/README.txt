


                          Tcl Example 2


This state map "recognizes" the string 0*1*. Example 2 differs
from example 1 in that example 2 uses default transitions.


+ Building
----------

NOTE: Smc.jar must be built and installed.

Unix & Windows:
    $ java -jar ../../../bin/Smc.jar AppClass.sm


+ Executing
-----------

Unix & Windows

    $ tclsh checkstring <string>

Try several different strings, such as:

    $ tclsh checkstring ""
      -> acceptable
      
    $ tclsh checkstring 000
      -> acceptable
      
    $ tclsh checkstring 00011
      -> acceptable
      
    $ tclsh checkstring 111
      -> acceptable
      
    $ tclsh checkstring 000111100
      -> unacceptable
      
    $ tclsh checkstring 00011a1b10c0
      -> unacceptable
