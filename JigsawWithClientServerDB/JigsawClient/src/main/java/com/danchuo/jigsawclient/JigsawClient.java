package com.danchuo.jigsawclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class JigsawClient implements AutoCloseable {

  private static final int SERVER_PORT = 5000;
  private static final String SERVER_HOST = "localhost";
  private final Socket socket;
  private final BufferedReader bufferedReader;
  private final PrintWriter printWriter;

  private final Runnable endConnectionHandler;

  JigsawClient(Runnable endConnectionHandler) throws Exception {
    socket = new Socket(SERVER_HOST, SERVER_PORT);
    this.endConnectionHandler = endConnectionHandler;
    bufferedReader =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    printWriter = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
  }

  public void sendMessage(String message) {
    printWriter.println(message);
  }

  public String getAnswer() {
    try {
      var answer = bufferedReader.readLine();

      if("x".equals(answer)){
        endConnectionHandler.run();
        return getAnswer();
      }

      return answer;
    } catch (IOException e) {
      endConnectionHandler.run();
      return "";
    }
  }

  public String sendMessageAndGetAnswer(String message) {
    sendMessage(message);
    return getAnswer();
  }

  @Override
  public void close() throws Exception {
    socket.close();
  }
}
