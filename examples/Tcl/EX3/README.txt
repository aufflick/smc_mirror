


                          Tcl Example 3


This state map "recognizes" the palindromes (words that read the
same backwards as forwards). The words consist of the alphabet
{0, 1, c} where the letter 'c' may appear only once and marks the
words center.


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
      -> unacceptable
      
    $ tclsh checkstring 00
      -> unacceptable
      
    $ tclsh checkstring 1c
      -> unacceptable
      
    $ tclsh checkstring c0
      -> unacceptable
      
    $ tclsh checkstring abcba
      -> unacceptable

    $ tclsh checkstring 110010c010011
      -> acceptable

    $ tclsh checkstring 110010c110010
      -> unacceptable
