package org.asgs.jshellconnector;

import org.asgs.jshellconnector.process.ProcessInstance;
import org.asgs.jshellconnector.websocket.config.JShellWebSocketApplicationConfig;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Main program! */
public class Main {

  public static void main(String[] args)
      throws IOException, InterruptedException, ExecutionException, DeploymentException {
    final ProcessInstance instance = ProcessInstance.getInstance();
    Runnable job =
        () -> {
          try {
            instance.initProcess();
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
          }
        };

    new Thread(job).start();

    int port;
    try {
      port = Integer.parseInt(CommonConfig.getValue("WS_PORT"));
    } catch (NumberFormatException e) {
      port = 8080;
    }

    Server server =
        new Server(
            "localhost",
            port,
            CommonConfig.getValue("WS_CONTEXT"),
            null,
            JShellWebSocketApplicationConfig.class);

    server.start();

    Runnable cleanUpJob =
        () -> {
          if (instance.getProcess() != null) {
            instance.getProcess().destroy();
            System.out.println("Shutting down the child process.");
          }
        };

    Runtime.getRuntime().addShutdownHook(new Thread(cleanUpJob));
  }
}
