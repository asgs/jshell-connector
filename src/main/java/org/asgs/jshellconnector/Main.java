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
        int port;
        try {
            port = Integer.parseInt(CommonConfig.getValue("WS_PORT"));
        } catch (NumberFormatException e) {
            port = 8080;
        }
        Server server = new Server("localhost", port, CommonConfig.getValue("WS_CONTEXT"), null, JShellWebSocketApplicationConfig.class);
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
