package org.academiadecodigo.notorbios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final LinkedList<ClientMsgManager> clientList = new LinkedList<>();
    private static final int DEFAULT_PORT = 8085;
    private ServerSocket bindServerSocket = null;
    private Socket clientSocket = null;

    public Server() {

        setupServerSocket();
    }

    /**
     * Starts the server, server will be listening to new requests
     */
    public void start() {

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        while (bindServerSocket.isBound()) {

            try {

                clientSocket = bindServerSocket.accept(); // blocking method, holds for connection

                clientList.offer(new ClientMsgManager(clientSocket, this));

                cachedThreadPool.submit(clientList.getLast()); // creates new thread for a message manager on request and gives it to a new client

            } catch (NumberFormatException e) {

                System.err.println("Usage: WebServer [PORT]\n");

                System.exit(1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        System.out.println("bye");
    }

    /**
     * Sets up server socket to DEFAULT_PORT
     */
    private void setupServerSocket() {

        try {

            bindServerSocket = new ServerSocket(DEFAULT_PORT);

            logger.log(Level.INFO, "server bind to " + getAddress(bindServerSocket) + "\n");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private String getAddress(ServerSocket socket) {

        if (socket == null) {

            return null;
        }

        return socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort();
    }

    public LinkedList<ClientMsgManager> getClientList() {
        return clientList;
    }

    /**
     * Server runs here
     */
    public static void main(String[] args) {

        Server server = new Server();

        server.start();
    }
}