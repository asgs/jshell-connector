package org.asgs.jshellconnector.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by asgs on 05-01-2017.
 */
public class ProcessInstance {
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