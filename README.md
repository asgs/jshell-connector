# jshell-connector
Connects to jshell executable process and let the user interact with it

Creates a Java Process out of JShell binary included with JDK9+ and exposes the OutputStream and InputStream to the external world using Websockets API.

No JavaEE or Servlet Container is required as this is intended to be as simple as a Java Main console application. Grizzly provides the Server functionality exposing the application as a Websocket Server.

# **Refactoring**

Code is ugly as fuck as it stands at the moment, as it was written in a hurry without any sort of Code Quality in mind. It just works. More commits to come to make it clean and unit-testable.

# Test Instance

If you want to quickly try this, go to http://137.74.64.141/jshell-frontend/

# Build and Deploy

Uses mvn, so it's just a matter of `mvn clean package && java -jar \<jarname\>`
It assumes there's a jdk directory named jdk-9 under your user directory. Feel free to change it before building.
