import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.*;


public class EchoServer {
    private int port;
    private Connection connection;
    private LinkedTransferQueue<SimpleMessage> messages;
    private HashSet<Connection> connections; //CopyOnWriteArraySet ? нельзя использовать так как нельзя использовать метод remove??
    private ArrayList<Future<SimpleMessage>> results;

    public EchoServer(int port) {
        this.port = port;
        this.messages = new LinkedTransferQueue<>();
        this.connections = new HashSet<>();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public void start() throws IOException, ClassNotFoundException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started...");
            new SendMessageThread().start();
            while (true) {
                Socket socket = serverSocket.accept(); // после этой строчки меняется код
                //создается потоки на одного клиента один поток, сколько клиентов столько потоков (нефиксированное кол-во потоков)
                //пул потоко сендмесадж
                ExecutorService pool = Executors.newFixedThreadPool(10);
                // ReceiveMessage receiveMessage1 = new ReceiveMessage(socket);
                Callable<SimpleMessage> receiveMessage = new ReceiveMessage(socket);

                ArrayList<Future<SimpleMessage>> results = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    Future<SimpleMessage> resultContainer = pool.submit(receiveMessage);
                    results.add(resultContainer);
                    System.out.println("Сообщение из контейнера: " + results.get(i));
                }
                //  for (Future<SimpleMessage> simpleMessageFuture: results) {
                //      System.out.println(simpleMessageFuture);
                // }
                // connection = new Connection(socket); //у потока должны быть ссылка на подключение
                // ссылку на подключение нужно передать в поток получатель в потокобезопасную коллекцию
                // printMessage(connection.readMessage());
                // connection.sendMessage(SimpleMessage.getMessage("server", "сообщение получено"));

            }
        }
    }

    private void printMessage(SimpleMessage message) {
        System.out.println("получено сообщение: " + message);
    }

    class ReceiveMessage implements Callable<SimpleMessage> {
        private Socket socket;
        private Connection connection;

        public ReceiveMessage(Socket socket) throws IOException {
            this.socket = socket;
            this.connection = new Connection(socket);

        }

        @Override
        public SimpleMessage call() throws Exception {
            Thread.sleep(3000);
            SimpleMessage message = connection.readMessage();
            messages.put(message);
            connections.add(connection);
            messages.transfer(message);
            printMessage(message);
            if (socket.getInputStream().read() == -1) {
                connections.remove(connection);
            }
            while (true) {
                SimpleMessage nextMessage = connection.readMessage();
                messages.put(nextMessage);
                messages.transfer(nextMessage);
                System.out.println("Сообщение отправлено: " + nextMessage);
                return nextMessage;
            }
        }
    }


    class SendMessageThread extends Thread {
        @Override
        public void run() {
            try {
                SimpleMessage simpleMessage = messages.take();
                for (Connection connection : connections) {
                    if (!Objects.equals(connection.getClient(), simpleMessage.getSender())) {
                        connection.sendMessage(simpleMessage);
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 8090;
        EchoServer messageServer = new EchoServer(port);
        try {
            messageServer.start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}