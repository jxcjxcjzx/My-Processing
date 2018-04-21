http://sourceforge.net/projects/speakright/

This is the SpeakRight Framework, an open-source Java framework for writing VoiceXML
applications.

Installation instructions are in INSTALL.txt.

SpeakRight comes without any warrenty. Read COPYING for more details.
Licenses of used libraries can be optained from the related web site.

If you have any questions have a look at our web site.

Release Summary
---------------
v0.1.4 is the first release in several months.  Transfer and Record have been implemented.
A number of new flow object classes have been added.  A Content-logging feature has been added 
that outputs all generated-VoiceXMl to a file.


SpeakRight libraries
--------------------

SpeakRight contains a single jar file:

- speakright-v0-1-4.jar contains compiled code and source for
   - core  main framework classes
   - test  unit tests
   - itest an console-based interactive tester for running SpeakRight apps.
   - tools SpeakRight uses code-generation for the model (MGen) and SROs (SROGen)
   - sro   re-usable speech objects
   - SampleServlet a sample servlet containing a simple SpeakRight app

   - javadocs are included (and availabe at http://speakrightframework.net/javadocs/)


Library depencencies
--------------------

All libraries can be found in the lib folder.  These are

 - antlr 2.7.7
 - stringtemplate 
 - xmlunit 1.0
 
You will also need:
 - log4j-1.2.8 (or higher).  This is included with Eclipse
 

You can also download the third party libraries from their web sites.


