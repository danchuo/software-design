package jigsawserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class JigsawServer extends Thread {

    public static final char DELIMITER ='%';
    private static final Set<JigsawServer> TEST_SERVERS = new HashSet<>();
    private static final char[] FIGURES={'1','2','3','4','5','6','7','8','9'};
    private static final ArrayList<String> CREATED_FIGURES = new ArrayList<>();
    private final Socket connectedSocket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private String clientName;

    JigsawServer(Socket connected,String clientName) throws IOException {
        super("JigsawServer: thread(" + connected.getPort() + ")");
        connectedSocket = connected;
        bufferedReader =
                new BufferedReader(new InputStreamReader(connected.getInputStream(), StandardCharsets.UTF_8));
        printWriter = new PrintWriter(connected.getOutputStream(), true, StandardCharsets.UTF_8);

        sendMessage(handleCommand(clientName));

        registerJigsawServer(this);
        System.out.println("name = "+this.clientName+"; "+
                "port = " + connected.getPort() + "; localPort = " + connected.getLocalPort());
        System.out.println("registered connections number = " + TEST_SERVERS.size());
    }

    private static synchronized void registerJigsawServer(JigsawServer JigsawServer) {
        TEST_SERVERS.add(JigsawServer);
    }

    private static synchronized void shutdownJigsawServers() {
        Iterator<JigsawServer> iterator = TEST_SERVERS.iterator();
        while (iterator.hasNext()) {
            JigsawServer JigsawServer = iterator.next();
            iterator.remove();
            stopJigsawServer(JigsawServer);
        }
    }

    private static synchronized void stopJigsawServer(JigsawServer JigsawServer) {
        try {
            JigsawServer.interrupt();
            JigsawServer.connectedSocket.close();
        } catch (IOException ioException) {
            System.out.println(Thread.currentThread().getName() + ": get exception: " + ioException);
        }
    }

    public static void main(String... args) {
            int maxClients = askNumber("max number of clients", 1, 2);
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("TCP/IP JigsawServer waiting for clients on port 5000...)");
            System.out.println("Print exit to exit...");

            ConnectionHandler connectionHandler = new ConnectionHandler(serverSocket, maxClients);
            connectionHandler.start();

            Scanner scanner = new Scanner(System.in);
            while (!scanner.nextLine().equalsIgnoreCase("exit")) {}

            serverSocket.close();
            shutdownJigsawServers();

            try {
                connectionHandler.join();
                System.out.println("connectionHandler finished.");
            } catch (InterruptedException interruptedException) {
                System.out.println(
                        Thread.currentThread().getName() + ": main() got exception: " + interruptedException);
            }
            System.out.println(Thread.currentThread().getName() + ": JigsawServer finished.");

        } catch (IOException ioException) {
            System.out.println(
                    Thread.currentThread().getName() + ": main() got exception: " + ioException);
        }
    }

    private static int askNumber(String phrase, int min, int max) {
        Integer result;
        do {
            System.out.println(
                    "Please enter the " + phrase + " that belongs to the range [" + min + ";" + max + "]...");

            result = parseIntOrNull(new Scanner(System.in, StandardCharsets.UTF_8).nextLine());
        } while (result == null || result < min || result > max);

        return result;
    }

    private static Integer parseIntOrNull(String value) {
        Integer returnValue = null;
        try {
            returnValue = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }
        return returnValue;
    }

    private static String generateFigure(int numberOfFigure){
        if (CREATED_FIGURES.size() < numberOfFigure+1){
            var randomFigure=FIGURES[new SecureRandom().nextInt(FIGURES.length)];
            var randomStanding=new SecureRandom().nextInt(4);
            CREATED_FIGURES.add(randomFigure + " " + randomStanding);
        }

        return CREATED_FIGURES.get(numberOfFigure);
    }

    public  void startGame(){
        for (var server : TEST_SERVERS) {
            var opponentNames = new StringBuilder(10);
            for (var innerServer : TEST_SERVERS) {
                if (server!=innerServer){
                    opponentNames.append(innerServer.clientName);
                    opponentNames.append(' ');
                }
            }
            server.sendMessage("game started"
                    +DELIMITER
                    + generateFigure(0)
                    +DELIMITER
                    +opponentNames);

        }
    }

    @Override
    public void run() {
        try {

            InputStream inputStream = connectedSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            OutputStream outputStream = connectedSocket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);

            while (!isInterrupted()) {
                String clientString = bufferedReader.readLine();
                if (clientString == null) {
                    System.out.println(
                            Thread.currentThread().getName()
                                    + ": communication with a client closed by client...");
                    break;
                }
                System.out.println(Thread.currentThread().getName() + ": from Client: " + clientString);
                printWriter.println(handleCommand(clientString)); // just echo...
            }

            printWriter.close();
            bufferedReader.close();
            stopJigsawServer(this);
        } catch (IOException ioException) {
            System.out.println(Thread.currentThread().getName() + ": got exception: " + ioException);
        }
    }

    private String handleCommand(String command){
        return switch (command.charAt(0)) {
            case 'r' -> registerClient(command);
            case 'f' -> generateFigure(Integer.parseInt(command.split(String.valueOf(DELIMITER))[1]));
            case 'e' -> generateReport(Integer.parseInt(command.split(String.valueOf(DELIMITER))[1]));
            default -> "unknown command";
        };
    }


    private String generateReport(int amount){
        var builder=new StringBuilder(10);
        builder.append("you spent ").append(amount).append(" figures.");

        return builder.toString();
    }

    private String registerClient(String command){
        clientName = command.substring(2);
        return "client "+clientName+" registered, please wait until all clients connect";
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public String getAnswer() throws IOException {
        return bufferedReader.readLine();
    }

    public String sendMessageAndGetAnswer(String message) throws IOException {
        sendMessage(message);

        return getAnswer();
    }

    static class ConnectionHandler extends Thread {

        private final ServerSocket serverSocket;
        private final int maxServers;
        private int servers;

        ConnectionHandler(ServerSocket serverSocket, int maxServers) {
            super("ConnectionHandler");
            this.maxServers = maxServers;
            servers = 0;
            this.serverSocket = serverSocket;
        }

        public String sendMessageAndGetAnswer(String message, Socket connected ) throws IOException {
            OutputStream outputStream = connected.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
            InputStream inputStream = connected.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8));

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
                                            return sendMessageAndGetAnswer("hello from server, please introduce yourself", connected);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }).whenCompleteAsync((result,exeption)->{
                                try {
                                    addClient(result,connected);
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


        private void addClient(String name,Socket connected) throws IOException {
            if (servers < maxServers) {

                var client= new JigsawServer(connected,name);
                client.start();
                ++servers;
                if(servers== maxServers){
                    client.startGame();
                }
            } else {
                System.out.println("extra ConnectionHandler: " + connected);
                OutputStream outputStream = connected.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
                printWriter.println("all players already connected, sorry");
            }
        }

    }
}