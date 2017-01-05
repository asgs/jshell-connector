package org.asgs.jshellconnector.websocket;

import com.google.common.collect.ImmutableList;
import org.asgs.jshellconnector.process.ProcessInstance;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by asgs on 06-01-2017.
 */
public class JShellWebSocketEndpoint extends Endpoint {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final List DISALLOWED_COMMANDS = ImmutableList.of("/ex", "/exi", "/exit", "/edit");

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        final ProcessInstance instance = ProcessInstance.getInstance();
        session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            try {
                if (DISALLOWED_COMMANDS.contains(message)) {
                    session.getBasicRemote().sendText("No! Can't execute that." + LINE_SEPARATOR);
                    return;
                }
                OutputStream outputStream = instance.getOutputStream();
                outputStream.write((message + LINE_SEPARATOR).getBytes("UTF-8"));
                System.out.println("Sent command to jshell " + message);
                outputStream.flush();
                InputStream inputStream = instance.getInputStream();
                // Give a chance for the process to process the command.
                try {
                    Thread.sleep(1000);
                    System.out.println("Slept for a second.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] data = new byte[1000000];
                // Attempting to read with a bigger-than-usual buffer, so as not to stall.
                int bytesRead = inputStream.read(data, 0, data.length);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(data, 0, bytesRead);
                String commandOutput = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                session.getBasicRemote().sendText(commandOutput);
                System.out.println("Sent command output " + commandOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onError(final Session session, Throwable t) {
        System.out.println("Encountered error with request from session - " + session);
        t.printStackTrace();
    }
}