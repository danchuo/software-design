package jigsawserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class JigsawServer extends Thread {

    public static final char DELIMITER = '%';
    public static final int MAX_GAME_DURATION = 600;
    public static final int SECONDS = 60;
    public static final int MIN_GAME_DURATION = 30;
    private static final Set<JigsawServer> TEST_SERVERS = new HashSet<>();
    private static final char[] FIGURES = {'1','2','3','4','5','6','7','8','9'};
    private static final ArrayList<String> CREATED_FIGURES = new ArrayList<>();
    private static int maxSeconds = 0;
    private static JigsawDB dataBase = null;
    private final Socket connectedSocket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private JigsawGameResult jigsawGameResult;
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
            var server = iterator.next();
            iterator.remove();
            stopJigsawServer(server);
        }
    }

    private static synchronized void stopJigsawServer(JigsawServer jigsawServer) {
        try {
            jigsawServer.interrupt();
            jigsawServer.connectedSocket.close();
        } catch (IOException ioException) {
            System.out.println(Thread.currentThread().getName() + ": get exception: " + ioException);
        }
    }

    public static void main(String... args) {
            int maxClients = askNumber("maximum number of clients", 1, 2);
             maxSeconds = askNumber("maximum game duration in seconds", MIN_GAME_DURATION, MAX_GAME_DURATION);
        try (ServerSocket serverSocket = new ServerSocket(5000); JigsawDB db = new JigsawDB()) {
            dataBase = db;
            System.out.println("TCP/IP JigsawServer waiting for clients on port 5000...)");
            System.out.println("Print exit to exit...");
            var winners = db.getTop10Games();

            ConnectionHandler connectionHandler = new ConnectionHandler(serverSocket, maxClients);
            connectionHandler.start();

            Scanner scanner = new Scanner(System.in);
            while (!"exit".equalsIgnoreCase(scanner.nextLine())) {}

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
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            var randomFigure= FIGURES[new SecureRandom().nextInt(FIGURES.length)];
            var randomStanding= new SecureRandom().nextInt(4);
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
                    + DELIMITER
                    + generateFigure(0)
                    + DELIMITER
                    + opponentNames
                    + DELIMITER
                    + maxSeconds);

        }
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                String clientString = bufferedReader.readLine();
                if (clientString == null) {
                    System.out.println(
                            Thread.currentThread().getName()
                                    + ": communication with a client closed by client...");
                    break;
                }
                System.out.println(Thread.currentThread().getName() + ": from Client: " + clientString);
                printWriter.println(handleCommand(clientString));
                checkForEndOfGame();
            }

            printWriter.close();
            bufferedReader.close();
            stopJigsawServer(this);
        } catch (IOException ioException) {
            System.out.println(Thread.currentThread().getName() + ": got exception: " + ioException);
        }
    }

    private String handleCommand(String command){
        var info=command.split(String.valueOf(DELIMITER));
        return switch (command.charAt(0)) {
            case 'r' -> registerClient(command);
            case 'f' -> generateFigure(Integer.parseInt(info[1]));
            case 'e' ->
                generateReport(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
            case 't' -> getTop10Table();
            default -> "unknown command";
        };
    }


    private String getTop10Table(){
        Iterable<JigsawGameResult> winners;
        try {
            winners = dataBase.getTop10Games();
        } catch (SQLException e) {
            return "problems with db";
        }
        var string = new StringBuilder(10);

        for (var winner : winners) {
            string.append(winner.login());
            string.append(DELIMITER);
            string.append(winner.endGameTime());
            string.append(DELIMITER);
            string.append(winner.amountOfTurns());
            string.append(DELIMITER);
            string.append(winner.amountOfSeconds());
            string.append(DELIMITER);
        }

        return string.toString();
    }

    private String generateReport(int amount, int time){
        jigsawGameResult = new JigsawGameResult(clientName, LocalDateTime.now(ZoneOffset.UTC),amount,time);
        var minutes = time / SECONDS;
        var seconds = time % SECONDS;
        return "you spent " + amount + " figures and " + minutes + ':' + seconds + " time";
    }

    private void checkForEndOfGame(){
        JigsawGameResult winner = null;
        for (var server : TEST_SERVERS) {
            if (server.jigsawGameResult == null){
                return;
            } else {
                if (winner == null){
                    winner = server.jigsawGameResult;
                } else {
                   if (server.jigsawGameResult.amountOfTurns() > winner.amountOfTurns()){
                       winner = server.jigsawGameResult;
                   } else {
                       if (server.jigsawGameResult.amountOfTurns() == winner.amountOfTurns() &&
                               server.jigsawGameResult.amountOfSeconds() < winner.amountOfSeconds()){
                           winner = server.jigsawGameResult;
                       }
                   }
                }
            }
        }

        try {
            dataBase.addGameResult(winner);
        } catch (SQLException e) {
            System.out.println(e.getSQLState());
        }

        var result = new StringBuilder(10);
        result.append("congratulatios to the winner ");
        result.append(winner.login());
        result.append(", with the most placed figures: ");
        result.append(winner.amountOfTurns());
        result.append(", and seconds spent: ");
        result.append(winner.amountOfSeconds());

        for (var server : TEST_SERVERS) {
            server.sendMessage(result.toString());
        }

    }

    private String registerClient(String command){
        clientName = command.substring(2);
        return "client " + clientName + " registered, please wait until all clients connect";
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
                if (servers == maxServers){
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
