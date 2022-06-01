package jigsawserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ConnectionHandler extends Thread {

  private final ServerSocket serverSocket;
  private final int maxServers;

  ConnectionHandler(ServerSocket serverSocket, int maxServers) {
    super("ConnectionHandler");
    this.maxServers = maxServers;
    this.serverSocket = serverSocket;
  }

  public int getMaxServers() {
    return maxServers;
  }

  public String sendMessageAndGetAnswer(String message, Socket connected) throws IOException {
    OutputStream outputStream = connected.getOutputStream();
    PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
    InputStream inputStream = connected.getInputStream();
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    printWriter.println(message);

    return bufferedReader.readLine();
  }

  @Override
  public void run() {
    try {
      while (true) {
        Socket connected = serverSocket.accept();

        System.out.println("ConnectionHandler: " + connected);
        CompletableFuture<String> completableFuture =
            CompletableFuture.supplyAsync(
                    () -> {
                      try {
                        return sendMessageAndGetAnswer(
                            "hello from server, please introduce yourself", connected);
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    })
                .whenCompleteAsync(
                    (result, exeption) -> {
                      try {
                        addClient(result, connected);
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    });
      }
    } catch (Exception ex) {
      System.out.println(Thread.currentThread().getName() + ": got exception: " + ex);
    }
    System.out.println("ConnectionHandler finishing...");
  }

  private void addClient(String name, Socket connected) throws IOException {
    if (JigsawServer.getAmountOfServers() < maxServers) {
      var client = new JigsawServer(connected, name);
      client.start();
      if (JigsawServer.getAmountOfServers() == maxServers) {
        client.startGame();
      }
    } else {
      System.out.println("extra ConnectionHandler: " + connected);
      OutputStream outputStream = connected.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
      printWriter.println("all players already connected, sorry");
      printWriter.close();
    }
  }
}
