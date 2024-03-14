package ru.asocial.games.core;

public interface IMessageService {

    void writeMessage(String tag, String message);

    void close();

}
