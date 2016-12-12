package com.ots.devday.example1;

import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tasos on 12/12/2016.
 */
public class ChatAppServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = Logger.getLogger(ChatAppServiceImpl.class.getName());

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<ChatMessage>() {

            public void onNext(ChatMessage chatMessage) {
                logger.log(Level.INFO, chatMessage.getClientid() + " : " + chatMessage.getContent());
            }

            public void onError(Throwable throwable) {
                logger.log(Level.WARNING, throwable.getMessage());
            }

            public void onCompleted() {

            }
        };
    }
}
