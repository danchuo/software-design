package com.danchuo.jigsawclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class JigsawClient {

    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;

    JigsawClient(String serverHost, int serverPort) throws Exception {
        socket = new Socket(serverHost, serverPort);
        bufferedReader =
                new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        printWriter = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public String getAnswer() throws IOException {
        return bufferedReader.readLine();
    }

    public String sendMessageAndGetAnswer(String message) throws IOException {
        sendMessage(message);
        //    if (serverString == null) {
        //      clientThread.interrupt();
        //    }
        return getAnswer();
    }
}
