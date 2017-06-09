package org.asgs.jshellconnector.process;

import org.asgs.jshellconnector.CommonConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by asgs on 05-01-2017.
 */
public class ProcessInstance {
    private static final int PROCESS_TIME = 1000;
    private static final int BYTES_TO_READ = 1 << 20; // Close to an MB.
    private static ProcessInstance instance = new ProcessInstance();
    private Process process;

    private ProcessInstance() {
        // No.
    }

    public static ProcessInstance getInstance() {
        return instance;
    }

    public void initProcess() {
        ProcessBuilder command = new ProcessBuilder(CommonConfig.getValue("JDK_HOME") + "/bin/jshell");
        command.redirectErrorStream(true);
        System.out.println("Bootstrapping JShell. Please wait...");
        try {
            process = command.start();
            System.out.println("JShell up and running; pid=" + process.pid());
        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStream inputStream = process.getInputStream();
        try {
            Thread.sleep(PROCESS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            byte[] data = new byte[BYTES_TO_READ];
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
