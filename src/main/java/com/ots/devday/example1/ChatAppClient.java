package com.ots.devday.example1;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tasos on 12/12/2016.
 */
public class ChatAppClient {

    private static final Logger logger = Logger.getLogger(ChatAppClient.class.getName());

    private final ManagedChannel channel;
    private final ChatServiceGrpc.ChatServiceBlockingStub blockingStub;
    private final ChatServiceGrpc.ChatServiceStub asyncStub;

    public ChatAppClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public ChatAppClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ChatServiceGrpc.newBlockingStub(channel);
        asyncStub = ChatServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        ChatAppClient client = new ChatAppClient("localhost", 5000);
        try {
            client.chat();
        } finally {
            client.shutdown();
        }
    }

    private void chat() throws InterruptedException {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<ChatMessage> requestObserver =
                asyncStub.chat(new StreamObserver<ChatMessage>() {
                    //@Override
                    public void onNext(ChatMessage chatMessage) {
                        logger.log(Level.INFO, chatMessage.getClientid() + " : " + chatMessage.getContent());
                    }

                    //@Override
                    public void onError(Throwable t) {
                        Status status = Status.fromThrowable(t);
                        logger.log(Level.WARNING, "Chat Failed: {0}", status);
                        finishLatch.countDown();
                    }

                    //@Override
                    public void onCompleted() {
                        logger.log(Level.INFO,"Finished Chat");
                        finishLatch.countDown();
                    }
                });

        ChatMessage msg = ChatMessage.newBuilder().setClientid("client1").setContent("hello").build();
        requestObserver.onNext(msg);

        // Mark the end of requests
        //requestObserver.onCompleted();

        // Receiving happens asynchronously
        finishLatch.await(1, TimeUnit.MINUTES);

    }
}
