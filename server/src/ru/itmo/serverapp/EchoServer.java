package ru.itmo.serverapp;

import ru.itmo.lib.Connection;
import ru.itmo.lib.SimpleMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class EchoServer {
    private int port;
    private Connection connection;
    private CopyOnWriteArrayList<Connection> connections;
    private LinkedBlockingQueue<SimpleMessage> messages;

    public EchoServer(int port) {
        this.port = port;
        this.connections = new CopyOnWriteArrayList<>();
        this.messages = new LinkedBlockingQueue<>();
    }

    public void start() throws IOException, ClassNotFoundException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started...");

            Thread senred = new Thread(new SendMessage());
            senred.setName("Server sender");
            senred.start();

            while (true) {
                Socket socket = serverSocket.accept();
                connection = new Connection(socket);
                connections.add(connection);

                Thread receiver = new Thread(new ReceiveMessage(connection));
                receiver.setName("Server receiver");
                receiver.start();


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    class ReceiveMessage extends Thread {

        private Connection connection;

        public ReceiveMessage(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimpleMessage message = connection.readMessage();
                    if (message.getText().equals("break")) {
                        connections.remove(connection);
                        Thread.currentThread().interrupt();
                    }
                    messages.put(message);
                    message.setUuid(connection.getId());
                    System.out.println(Thread.currentThread() + " client name:" + message.getSender());
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

    }


    class SendMessage extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimpleMessage simpleMessage = messages.take();
                    for (Connection connection : connections) {
                        if (!simpleMessage.getUuid().equals(connection.getId())) {
                            connection.sendMessage(simpleMessage);
                        }
                    }
                    System.out.println(Thread.currentThread());
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }



}