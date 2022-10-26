package org.academiadecodigo.notorbios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMsgManager implements Runnable {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String userInput;
    private String username;

    public ClientMsgManager(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;


        this.server = server;
    }

    private void setupIOstreams() {

        //System.out.println("Setting up IO Streams ...");

        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Receives Input and sends that Input to every client connected to the Server
     */
    private void generalChatStream() {

        try {

            userInput = in.readLine(); // 1. Collects User Input

            logger.log(Level.INFO, username + " : " + userInput); // 2. Logs every User Input regardless

            // Checks if is NOT Command, null or empty
            if (userInput != null && !userInput.equals("/who") && !userInput.equals("/q") && !userInput.isEmpty()) {

                System.out.print(Thread.currentThread().getName() + " : "); // Client side name pointer

                // Echoes User Input to every user connected to Server
                for (int i = 0; i < server.getClientList().size(); i++) {

                    server.getClientList().get(i).out.println(Thread.currentThread().getName() + " : " + userInput);
                }
            }

            // If null disconnects user from chat for safety
            if (userInput == null) {

                logger.log(Level.INFO, Thread.currentThread().getName() + " has disconnected.");

                closeStreamsAndSockets();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Closes client streams and sockets
     */
    private void closeStreamsAndSockets() {

        //System.out.println("Closing Streams and Sockets...");

        logger.log(Level.INFO, Thread.currentThread().getName() + " has disconnected.");

        try {

            server.getClientList().remove(this);

            out.close();

            in.close();

            clientSocket.close();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Will perform different actions depending on command requested
     */
    private void userCommands() {

        switch (userInput) {
            case "/who":

                for (int i = 0; i < server.getClientList().size(); i++) {

                    out.println(server.getClientList().get(i).username);
                }

            case "/q":

                out.println("You have been disconnected. Bye!");

                closeStreamsAndSockets();
        }
    }

    @Override
    public void run() {

        logger.log(Level.INFO, "Your chat session is open\n");

        this.username = Thread.currentThread().getName();

        setupIOstreams();

        while (!clientSocket.isClosed()) {

            generalChatStream();
            userCommands();
        }
    }
}

