package com.trigg.fault_injection;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class TerminalProcess {
    //https://beknazarsuranchiyev.medium.com/run-terminal-commands-from-java-da4be2b1dc09
    /*public static void main(String[] args) throws Exception {
        // Where we want to execute
        File location = new File("/Users/ciaratrigg/Desktop/SE 598/spring project/target systems/postgres");
        String command1 = "curl --unix-socket /var/run/docker.sock -X POST http://localhost/containers/63e49b030a5b/stop";
        String command2 = "curl --unix-socket /var/run/docker.sock -X POST http://localhost/containers/63e49b030a5b/start";
        runCommand(location, command1); // for Mac(Linux based OS) users list files

        // runCommand(location, "dir"); // For Windows users list files
    }*/

    public static void runCommand(File whereToRun, String command) throws Exception {
        System.out.println("Running in: " + whereToRun);
        System.out.println("Command: " + command);

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(whereToRun);


        builder.command("sh", "-c", command);


        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        printStream(inputStream);
        printStream(errorStream);

        boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished) {
            process.destroyForcibly();
        }
    }
    private static void printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

        }
    }
}
