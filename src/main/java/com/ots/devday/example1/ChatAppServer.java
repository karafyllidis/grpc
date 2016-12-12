package com.ots.devday.example1;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by tasos on 12/12/2016.
 */
public class ChatAppServer {

    private static final Logger logger = Logger.getLogger(ChatAppServer.class.getName());
    private Server server;
    private int port = 5000;

    public ChatAppServer(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ChatAppServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ChatAppServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[0]);
        final ChatAppServer server = new ChatAppServer(port);
        server.start();
        server.blockUntilShutdown();
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
