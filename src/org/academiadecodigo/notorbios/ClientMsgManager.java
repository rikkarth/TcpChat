package org.academiadecodigo.notorbios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMsgManager implements Runnable {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String userInput;

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

    private void receiveUserInput() {

        try {

            userInput = in.readLine();

            if (userInput != null) {

                System.out.print(Thread.currentThread().getName() + " : ");

                System.out.println(userInput);

                for(int i = 0; i < server.getClientList().size(); i++){

                    server.getClientList().get(i).getOut().println(userInput);
                }
            }

            if (userInput == null) {

                logger.log(Level.INFO, Thread.currentThread().getName() + " has disconnected.");

                closeStreamsAndSockets();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void closeStreamsAndSockets() {

        //System.out.println("Closing Streams and Sockets...");

        try {

            out.close();

            in.close();

            clientSocket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getUserInput() {
        return userInput;
    }

    @Override
    public void run() {

        logger.log(Level.INFO, "Your chat session is open\n");

        setupIOstreams();

        while (!clientSocket.isClosed()) {

            receiveUserInput();
        }
    }
}

