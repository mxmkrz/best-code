package ru.itmo.serverapp;

import java.io.IOException;

import static ru.itmo.lib.ConstValue.PORT;

public class ServerApp {
    public static void main(String[] args) {
        try {
            new EchoServer(PORT).start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
