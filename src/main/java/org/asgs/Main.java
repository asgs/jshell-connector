package org.asgs;

/*import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.PumpStreamHandler;*/

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import org.glassfish.tyrus.server.Server;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerApplicationConfig;
import java.util.*;
import com.google.common.collect.ImmutableList;

/**
 * Main program!
 */
public class Main {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final List DISALLOWED_COMMANDS = ImmutableList.of("/ex", "/exi", "/exit", "/edit");

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        new Thread() {
		public void run() {
			ProcessInstance.getInstance().initProcess();
		}
	}.start();
	Server server = new Server("localhost", 8080, "/jshell-ws", null, MyCustomConfig.class);
        try {
		server.start();
	} catch (Exception e) {
		e.printStackTrace();
	}
Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown the child process.");
                ProcessInstance.getInstance().getProcess().destroy();
        }
        });
	System.out.println("Type quit to stop.");
        Scanner scanner = new Scanner(System.in);
	while (scanner.hasNextLine()) {
		if (scanner.nextLine().equalsIgnoreCase("quit")) {
			break;
		}
	}
	//doItManually();
        //doItUsingZtExecLib();

    }

   public static class JShellEndpoint1 extends Endpoint { 
    	@Override
	public void onOpen(final Session session, EndpointConfig config) {
		final ProcessInstance instance = ProcessInstance.getInstance();
        	session.addMessageHandler(new MessageHandler.Whole<String>() {
			public void onMessage(String message) {
                		try {
				    //System.out.println("Received message " + message + " from session " + session);
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
        	        	}
            			catch (IOException e) {
                			e.printStackTrace();
	            		}
			}		   
            	});
	}
		public void onError(final Session session, Throwable t) {
			System.out.println("Encountered error with request from session - " + session);
			t.printStackTrace();
		}
    	}

public static class MyCustomConfig  implements ServerApplicationConfig {
@Override
  public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set) {
System.out.println("Inside getEndpointConfigs. Set is " + set);    
Set<ServerEndpointConfig> set1 = new HashSet<ServerEndpointConfig>();
 set1.add(ServerEndpointConfig.Builder
            .create(JShellEndpoint1.class, "/")
            .build());
return set1;
     }
  

  @Override
  public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
	System.out.println("Returning empty set.");
    return new HashSet();
  }
}

private static class ProcessInstance {
Process process;
static ProcessInstance instance = new ProcessInstance();
private ProcessInstance() {
// No.
}

public static ProcessInstance getInstance() {
	return instance;
}

public void initProcess() {
	ProcessBuilder command = new ProcessBuilder("/home/asgs/jdk-9/bin/jshell");
        command.redirectErrorStream(true);
        System.out.println("Bootstrapping JShell. Please wait...");
        try {
		process = command.start();
		System.out.println("JShell up and running; pid=" + process.getPid());
	} catch (Exception e) {
		e.printStackTrace();
	}

        InputStream inputStream = process.getInputStream();
	try {
		Thread.sleep(500);
	} catch (Exception e) {
		e.printStackTrace();
	}
	try {
		byte[] data = new byte[1000000];
	        // Clear the initial JShell banner displayed.
		inputStream.read(data, 0, data.length);
	} catch (IOException e) {
		System.err.println("Error consuming JShell banner text.");
		e.printStackTrace();
	}
	try {
		process.waitFor();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public OutputStream getOutputStream() {
	return process.getOutputStream();
}

public InputStream getInputStream() {
	return process.getInputStream();
}

public Process getProcess() {
	return process;
}

}

    private static void doItManually() throws IOException, InterruptedException {
        ProcessBuilder command = new ProcessBuilder("/home/asgs/jdk-9/bin/jshell");
        //ProcessBuilder command = new ProcessBuilder("/usr/local/google/home/gowrisankar/Downloads/jdk-9/bin/jshell");
        command.redirectErrorStream(true);
        //command.inheritIO();
        System.out.println("Bootstrapping JShell. Please wait...");
        Process process = command.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown the child process.");
                process.destroy();
            }
        });
        //System.out.println(process.getPid());

        InputStream inputStream = process.getInputStream();

        Main app = new Main();

        InputReaderJob inputReaderJob = app.new InputReaderJob(inputStream);
        Thread thread1 = new Thread(inputReaderJob);
        thread1.setName("JShellInputReadingThread.");
        //thread1.setDaemon(true);
        thread1.start();

        OutputStream outputStream = process.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        Runnable scannerRunnable = () -> {
            while (true) {
                String nextLine = scanner.nextLine();
                //System.out.println(nextLine);
                nextLine += LINE_SEPARATOR;
                try {
                    outputStream.write(nextLine.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    scanner.reset();
                    //System.out.println("Processing next line.");
                    //scanner.next();
                }


            }
        };

        Thread thread2 = new Thread(scannerRunnable);
        thread2.setName("UserInputReadingThread.");
        //thread2.setDaemon(true);
        thread2.start();
        process.waitFor();

    }

    private class InputReaderJob implements Runnable {
        InputStream inputStream;

        InputReaderJob(InputStream inputStream) {
            this.inputStream = new BufferedInputStream(inputStream);
        }

        @Override
        public void run() {
            int bytesRead = -1;
            byte[] data = new byte[8192];
            try {
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(data, 0, bytesRead);
                    System.out.print(new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

