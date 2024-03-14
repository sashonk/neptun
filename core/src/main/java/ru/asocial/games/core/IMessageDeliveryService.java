package ru.asocial.games.core;

public interface IMessageDeliveryService {

    interface Listener {
        void onMessage(Message msg) throws Exception;
    }

    void addListener(Listener listener);

    void startDeliveryAsync();

}
