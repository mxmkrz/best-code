package ru.itmo.clientapp;

import ru.itmo.lib.Connection;
import ru.itmo.lib.SimpleMessage;


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Scanner scanner;
    private Connection connection;


    public Client(int port, String ip) throws IOException {
        this.scanner = new Scanner(System.in);
        this.connection = new Connection(new Socket(ip, port));

        Thread sender = new Thread(new ClientSender(connection));
        sender.setName("ru.itmo.clientapp.Client sender");
        sender.start();
        Thread receiver = new Thread(new ClientReceiver(connection));
        receiver.setName("ru.itmo.clientapp.Client receiver");
        receiver.start();

    }

    class ClientSender extends Thread {

        private Connection connection;

        public ClientSender(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                System.out.println("Enter name");
                String name = scanner.nextLine();
                String messageText;
                while (true) {
                    System.out.println("Enter message");
                    messageText = scanner.nextLine();
                    SimpleMessage message = SimpleMessage.getMessage(name, messageText);
                    connection.sendMessage(message);
                    if (message.getText().equalsIgnoreCase("break")) {
                        connection.close();
                        System.out.println("exit");
                        break;
                    }
                    System.out.println(Thread.currentThread());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    class ClientReceiver extends Thread {
        private Connection connectionFromSender;

        public ClientReceiver(Connection connection) {
            this.connectionFromSender = connection;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SimpleMessage formServer = connectionFromSender.readMessage();
                    System.out.println("New message: " + formServer.getText() + " from: " + formServer.getSender());
                    System.out.println(Thread.currentThread());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }


}


