/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.svc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 * A socket-based server providing client access to the ShaTu tutor.
 *
 * Protocol msg ::= &lt;cmd> &lt;argData> <br>
 * &lt;cmd> ::= :CreateStudentAccount | :LaunchSession | :SignIn | <br>
 * :RequestHint | :RequestCorrectAnswer | :CompletedStep | :CompletedTask <br>
 * The &lt;argData> for each command is documented in the TutorSvc interface.
 *
 * @author Rickb
 */
public class ShaTuServer implements Runnable {

    /**
     * Port on which this server (ShaTu tutor) is listening for client connections.
     */
    public static final int PORT = 53636;

    /**
     * Handler for logging messages.
     */
    private static final Logger LOGGER
            = Logger.getLogger(ShaTuServer.class.getName());

    /**
     * Hard cap on request size to guard against malformed/oversized payloads.
     */
    private static final int MAX_REQUEST_LENGTH = 8192;

    /**
     * The socket listening for connections from the client
     */
    private ServerSocket server;

    /**
     * A no-op
     */
    public ShaTuServer() {
    }

    /**
     * Create a server socket that waits for connection requests from a client,
     * which are handled by spawning a new ShaTuConnection, with an associated
     * new ShaTu tutor, that handles all subsequent communication between the 
     * client and sever.
     */
    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);

            while (true) {
                new ShaTuConnection(server.accept()).run();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "EncryptionServer.run()", e);
        }
    }

    /**
     * A connection to a client which handles ShaTu tutoring requests to the
     * server (tutor).
     */
    private class ShaTuConnection implements Runnable {

        /**
         * The socket connection with the client
         */
        private final Socket client;

        /**
         * Stream from which messages from the client socket can be read
         */
        private BufferedReader in;

        /**
         * Stream from which messages to the client socket can be written
         */
        private PrintWriter out;

        /**
         * The ShaTu tutor associated with this connection.
         */
        private final TutorSvc tutor;

        /**
         * Initialize this connection by creating a new ShaTu tutor that is
         * communicating with the client associated with the given socket.
         *
         * @param client an established socket connection to a client
         */
        public ShaTuConnection(Socket client) {
            this.client = client;

            tutor = new ShaTuTutor();
        }


        /**
         * Read a JSon encoded request from the client to the ShaTu tutor.
         */
        @Override
        public void run() {
            Gson gson = new Gson();
            try {
                in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);

                String msg = in.readLine();
                if (msg == null || msg.isEmpty()) {
                    LOGGER.warning("Received empty request");
                    return;
                }
                if (msg.length() > MAX_REQUEST_LENGTH) {
                    LOGGER.warning("Request exceeded max length and was rejected");
                    return;
                }
                
                ClientRequest request = gson.fromJson(msg, ClientRequest.class);
                
                TutorReply reply = tutor.request(request);
                
                out.println(gson.toJson(reply));

                out.flush();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "EncryptionConnection.run()", e);
            } finally {
                // About as ugly as it gets, but the following code ensures that
                // we've at least tried to close an open socket and its associated
                // input and output streams in every possible error scenario
                // If we didn't, it's possible that we're leaking memory.
                try {
                    if (out != null) {
                        out.close();
                    }
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Unable to close client socket in", e);
                    } finally {
                        try {
                            if (client != null) {
                                client.close();
                            }
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "Unable to close client socket in", e);
                        }
                    }
                }
            }
        }
    }
}
