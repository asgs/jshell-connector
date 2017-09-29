package org.asgs.jshellconnector.process;

import org.asgs.jshellconnector.CommonConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Created by asgs on 05-01-2017. */
public class ProcessInstance {
  public static final String JDK_HOME_ENV_VAR = "JDK_HOME";
  public static final String JSHELL_BIN_REL_PATH = "/bin/jshell";
  private static final int PROCESS_TIME = 1000;
  private static final int BYTES_TO_READ = 1 << 20; // An MB.
  private static ProcessInstance instance = new ProcessInstance();
  private Process process;

  private ProcessInstance() {
    // No. We want only one JShell process running, and not worried about concurrency issues arising
    // because of multiple users interacting with only one such process.
  }

  public static ProcessInstance getInstance() {
    return instance;
  }

  public void initProcess() throws InterruptedException, IOException {
    ProcessBuilder command =
        new ProcessBuilder(CommonConfig.getValue(JDK_HOME_ENV_VAR) + JSHELL_BIN_REL_PATH);
    command.redirectErrorStream(true);
    System.out.println("Bootstrapping JShell. Please wait...");
    process = command.start();
    System.out.println("JShell up and running; pid=" + process.pid());

    InputStream inputStream = process.getInputStream();
    Thread.sleep(PROCESS_TIME);

    byte[] data = new byte[BYTES_TO_READ];
    // Clear the initial JShell banner displayed.
    inputStream.read(data, 0, data.length);

    process.waitFor();
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
