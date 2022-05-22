import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private int port;
    private String ip;
    private Scanner scanner;
    private Connection connection;

    public Client(int port, String ip) throws IOException {
        this.port = port;
        this.ip = ip;
        scanner = new Scanner(System.in);
       // this.connection = new Connection(new Socket(ip, port));
        this.connection = new Connection(getSocket());
        new ClientSenderThread(this.connection).start();
        new ClientReceiverThread(this.connection).start();
    }

/*
    public void start() throws Exception {
        System.out.println("Введите имя");
        String name = scanner.nextLine();
        String messageText = null;

        while (true) {
            System.out.println("Введите сообщение");
            messageText = scanner.nextLine();
            sendAndPrintMessage(SimpleMessage.getMessage(name, messageText));
            if (messageText.equalsIgnoreCase("break")) {
                System.out.println("выход из чата");
                break;
            }
        }
    }

    private void sendAndPrintMessage(SimpleMessage message) throws Exception {
        Connection connection = new Connection(getSocket()); // getSocket Только для того, чтобы посмотреть методы сокета
        connection.sendMessage(message);
        SimpleMessage formServer = connection.readMessage();
        if (message.getText().equalsIgnoreCase("break")) {
            connection.close();
        }
        System.out.println("ответ от сервера: " + formServer);
    }

    //метод получения сообщения клиента от клиента (здесь надо получить connection из метода sendAndPrintMessage)
    private void getMessage(SimpleMessage simpleMessage) {

    }

 */

    private Socket getSocket() throws IOException {
        Socket socket = new Socket(ip, port);
        return socket;
    }


    public static void main(String[] args) {
        int port = 8090;
        String ip = "127.0.0.1";

        try {
            // new Client(port, ip).start();
            new Client(port, ip);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class ClientSenderThread extends Thread {
        private Connection connectionFromSender;

        public ClientSenderThread(Connection connection) {
            this.connectionFromSender = connection;
        }

        @Override
        public void run() {
            try {
                System.out.println("Введите имя");
                String name = scanner.nextLine();
                String messageText = null;
                while (true) {
                    System.out.println("Введите сообщение");
                    messageText = scanner.nextLine();
                    SimpleMessage message = SimpleMessage.getMessage(name, messageText);
                    connectionFromSender.sendMessage(message);
                    if (message.getText().equalsIgnoreCase("break")) {
                        connectionFromSender.close();
                        System.out.println("выход из чата");
                        break;
                    }
                  //  SimpleMessage formServer = connectionFromSender.readMessage();
                 //   System.out.println("ответ от сервера: " + formServer);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    class ClientReceiverThread extends Thread {
        private Connection connectionFromSender;

        public ClientReceiverThread(Connection connection) {
            this.connectionFromSender = connection;
        }

        @Override
        public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()){
                    SimpleMessage formServer = connectionFromSender.readMessage();
                    System.out.println("Новое сообщение: " + formServer.getText() + " от: " + formServer.getSender());}
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


