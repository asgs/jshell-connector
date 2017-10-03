package org.asgs.jshellconnector.websocket;

import org.asgs.jshellconnector.process.ProcessInstance;

import javax.websocket.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static javax.websocket.CloseReason.CloseCodes.NORMAL_CLOSURE;

/** Created by asgs on 06-01-2017. */
public class JShellWebSocketEndpoint extends Endpoint {

  private static final int PROCESS_TIME = 1000;
  private static final int BYTES_TO_READ = 1 << 20; // An MB.
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private static final List<String> DISALLOWED_COMMANDS = List.of("/edit");
  private static final List<String> SESSION_CLOSURE_COMMANDS = List.of("/ex", "/exi", "/exit");

  @Override
  public void onOpen(final Session session, EndpointConfig config) {
    final ProcessInstance instance = ProcessInstance.getInstance();
    OutputStream outputStream = instance.getOutputStream();
    InputStream inputStream = instance.getInputStream();

    // Converting the below Anonymous class to Lambda throws a ClassCastException in java.base.
    // Could be Jigsaw?
    RemoteEndpoint.Async remoteEndpoint = session.getAsyncRemote();
    session.setMaxIdleTimeout(120000); // 2 min timeout.
    session.addMessageHandler(
        String.class,
        (MessageHandler.Whole<String>)
            message -> {
              try {

                for (String disallowedCommand : DISALLOWED_COMMANDS) {
                  if (message.contains(disallowedCommand)) {
                    remoteEndpoint.sendText("No! Can't execute that." + LINE_SEPARATOR);
                    return;
                  }
                }

                if (SESSION_CLOSURE_COMMANDS.contains(message)) {
                  session.close(new CloseReason(NORMAL_CLOSURE, "Goodbye!" + LINE_SEPARATOR));
                  return;
                }

                outputStream.write((message + LINE_SEPARATOR).getBytes("UTF-8"));
                System.out.println("Sent command to jshell: " + message);
                outputStream.flush();

                // Give a chance to JShell to process the command.
                try {
                  Thread.sleep(PROCESS_TIME);
                } catch (Exception e) {
                  e.printStackTrace();
                }

                byte[] data = new byte[BYTES_TO_READ];
                // Attempting to read as much as possible in one go, so as not to stall.
                int bytesRead = inputStream.read(data, 0, data.length);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(data, 0, bytesRead);
                String commandOutput = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                remoteEndpoint.sendText(commandOutput);
                System.out.println("Sent command output: " + commandOutput);
              } catch (IOException e) {
                e.printStackTrace();
              }
            });

    remoteEndpoint.sendText(
        "JVM Version is " + System.getProperty("java.vm.version") + LINE_SEPARATOR);
  }

  public void onError(final Session session, Throwable t) {
    System.out.println("Encountered error with request from session - " + session);
    t.printStackTrace();
  }
}
