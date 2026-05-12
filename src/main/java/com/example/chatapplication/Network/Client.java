package com.example.chatapplication.Network;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Client {

    private final String host;
    private final int port;
    private final String username;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenerThread;
    private volatile boolean running = false;

    // Callback for UI message updates
    private Consumer<String> messageListener;

    public Client(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    // Connect to the server
    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        // Send username first to register with server
        out.println(username);
        out.flush();

        running = true;
        listenerThread = new Thread(this::listenLoop, "Client-Listener-" + username);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    // Listen for messages from the server
    private void listenLoop() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                if (messageListener != null) {
                    messageListener.accept(line);
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Client listener error: " + e.getMessage());
            }
        } finally {
            closeConnection();
        }
    }

    // Set callback for UI
    public void setMessageListener(Consumer<String> listener) {
        this.messageListener = listener;
    }

    // Send a message
    public void sendMessage(String msg) {
        if (out != null && msg != null && !msg.trim().isEmpty()) {
            out.println(msg);
            out.flush();
        }
    }

    // Graceful close
    public void closeConnection() {
        running = false;
        try {
            if (out != null) {
                out.println("/quit");
                out.flush();
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 5000, "TestUser");
            client.setMessageListener(System.out::println);
            client.connect();

            // Send a test message
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String input;
            System.out.println("Connected! Type messages below:");
            while ((input = console.readLine()) != null) {
                client.sendMessage(input);
                if (input.equalsIgnoreCase("/quit")) break;
            }

            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

