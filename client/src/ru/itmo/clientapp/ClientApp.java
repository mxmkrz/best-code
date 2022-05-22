package ru.itmo.clientapp;

import static ru.itmo.lib.ConstValue.IP;
import static ru.itmo.lib.ConstValue.PORT;

public class ClientApp {
    public static void main(String[] args) {
        try {
            new Client(PORT, IP);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
