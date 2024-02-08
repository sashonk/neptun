package ru.asocial.games.core;

public interface IMessagingService {

    void writeMessage(String tag, String message);

    void close();

}
