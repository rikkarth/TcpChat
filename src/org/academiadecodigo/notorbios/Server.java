package org.academiadecodigo.notorbios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final LinkedList<ClientMsgManager> clientList = new LinkedList<>();
    private final List<ClientMsgManager> synchronizedclientList = Collections.synchronizedList(clientList);
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private ServerSocket bindServerSocket = null;

    public Server() {

        setupServerSocket();

        logger.log(Level.INFO, "server bind to " + getAddress(bindServerSocket) + "\n");
    }

    /**
     * Starts the server, server will be listening to new requests
     */
    public void start() {

        awaitClientConnection();

        if (bindServerSocket.isBound())
            start();

        System.out.println("bye");
    }


    /**
     * Sets up server socket to DEFAULT_PORT
     */
    private void setupServerSocket() {

        try {

            final int DEFAULT_PORT = 8085;

            bindServerSocket = new ServerSocket(DEFAULT_PORT);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void awaitClientConnection() {

        try {

            Socket clientSocket = bindServerSocket.accept(); // blocking method, holds for connection

            clientList.offer(new ClientMsgManager(clientSocket, this)); // adds client to LinkedList

            cachedThreadPool.submit(clientList.getLast()); // creates new thread for a message manager on request and gives it to a new client

        } catch (NumberFormatException e) {

            System.err.println("Usage: WebServer [PORT]\n");

            System.exit(1);

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