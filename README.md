# jshell-connector
Connects to jshell executable process and let the user interact with it

Creates a Java Process out of JShell binary included with JDK9+ and exposes the OutputStream and InputStream to the external world using Websockets API.

No JavaEE or Servlet Container is required as this is intended to be as simple as a Java Main console application. Grizzly provides the Server functionality exposing the application as a Websocket Server.

# **Refactoring**

Code is ugly as fuck as it stands at the moment, as it was written in a hurry without any sort of Code Quality in mind. It just works. More commits to come to make it clean and unit-testable.

# Test Instance

If you want to quickly try this, go to http://asgs.tech/jshell-frontend/. Not sure who, but someone has already put-up an aptly named website which is embedding this test instance as an iFrame :-) You can check that at http://jshell.tk

# Build and Deploy

This project uses mvn, so it's just a matter of `mvn clean package && java -jar <jarname>`
Change the jdk home under `src/main/resources` to match your installation before building.
