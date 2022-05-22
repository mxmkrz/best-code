package ru.itmo.lib;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class SimpleMessage implements Serializable {
    private String sender;
    private String text;
    private LocalDateTime dateTime;
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public SimpleMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }


    public String getText() {
        return text;
    }


    public void setDateTime(){
        dateTime = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "ru.itmo.lib.SimpleMessage{" +
                "sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", dateTime=" + dateTime +
                ", uuid=" + uuid +
                '}';
    }

    public static SimpleMessage getMessage(String sender, String text){
        return new SimpleMessage(sender, text);
    }
}