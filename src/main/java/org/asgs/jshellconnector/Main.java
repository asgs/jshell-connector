package org.asgs.jshellconnector;

import org.asgs.jshellconnector.process.ProcessInstance;
import org.asgs.jshellconnector.websocket.config.JShellWebSocketApplicationConfig;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Main program!
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final ProcessInstance instance = ProcessInstance.getInstance();
        new Thread(() -> instance.initProcess()).start();
        Server server = new Server("localhost", 8080, "/jshell-ws", null, JShellWebSocketApplicationConfig.class);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the child process.");
            instance.getProcess().destroy();
        }));
    }
}
