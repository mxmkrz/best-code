package ru.itmo.clientapp;

import ru.itmo.lib.Connection;
import ru.itmo.lib.SimpleMessage;


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Scanner scanner;
    private final Connection connection;
    private SimpleMessage message;


    public Client(int port, String ip) throws IOException {
        this.scanner = new Scanner(System.in);
        connection = new Connection(new Socket(ip, port));

        ClientSender sender = new ClientSender();
        sender.setName("Client sender");
        sender.start();
        ClientReceiver receiver = new ClientReceiver();
        receiver.setName("Client receiver");
        receiver.start();

    }

    class ClientSender extends Thread {


        @Override
        public void run() {
            try {
                System.out.println("Enter name");
                String name = scanner.nextLine();
                String messageText;
                while (true) {
                    System.out.println("Enter message");
                    messageText = scanner.nextLine();
                    message = SimpleMessage.getMessage(name, messageText);
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


        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    message = connection.readMessage();
                    System.out.println("New message: " + message.getText() + " from: " + message.getSender());
                    System.out.println(Thread.currentThread());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }


}


