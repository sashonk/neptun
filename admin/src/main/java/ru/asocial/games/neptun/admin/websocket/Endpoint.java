package ru.asocial.games.neptun.admin.websocket;


import ru.asocial.games.core.IMessageDeliveryService;
import ru.asocial.games.core.Message;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

@ServerEndpoint(value = "/service", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class Endpoint {

    static IMessageDeliveryService deliveryService;

    static {
        ServiceLoader<IMessageDeliveryService> serviceLoader = ServiceLoader.load(IMessageDeliveryService.class);
        Iterator<IMessageDeliveryService> iterator = serviceLoader.iterator();

        if (iterator.hasNext()) {
            Logger.getLogger("ws endpoint").info("use kafka delivery service");
        }
        else {
            Logger.getLogger("ws endpoint").info("use stub delivery service");
        }
        deliveryService = iterator.hasNext() ? iterator.next() : new IMessageDeliveryService() {
            @Override
            public void addListener(Listener listener) {
                //NOOP
            }

            @Override
            public void startDeliveryAsync() {
                //NOOP
            }
        };

        Logger.getLogger("ws endpoint").info("start delivery async");
        deliveryService.startDeliveryAsync();
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // Get session and WebSocket connection
        System.out.println(">>> open");

        deliveryService.addListener(msg -> {
            Logger.getLogger("listener: " + msg.getContent());
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg.getContent());
            }
        } );
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
        // Handle new messages
        System.out.println(">>> msg = " + message);

        session.getBasicRemote().sendText("Hello");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
        System.out.println(">>> close");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        throwable.printStackTrace();
    }

}
