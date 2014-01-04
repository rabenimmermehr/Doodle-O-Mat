Doodle-O-Mat
============

A script, that opens up a given doodle(www.dooodle.com) - link and selects the workdays of the next weeks, adds timeslots and saves everything


1. Setup
-----------

* Clone the repository into your workspace
* Install maven from here http://maven.apache.org/download.cgi
  - Follow the instructions further down
  - Watch out with the variables, you can't add "%M2%" to the path in Win7, you need to paste the whole path
* Open Eclipse, import the project ("import existing project")
* Install m2Eclipse frome here http://www.eclipse.org/m2e/download/
* Right-click project, select "configure->convert to maven project"
  - Ignore the error
* Follow these steps: http://www.scandio.de/2012/10/eclipse-enabling-maven-dependency-management-has-encountered-a-problem/
* Download and copy this file into C:/Windows/Sys32/ https://sites.google.com/a/chromium.org/chromedriver/downloads (Or put it somewhere else, where it can be found via the PATH variable)

Now you are ready

2. Usage
----------
Right now the script has no options, it just tries to update a Doodle with given times for the next week. Any 
changes have to happen in the code. It takes the URL to the Doodle as a command-line-paramter
