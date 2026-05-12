package com.example.chatapplication.Network;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final int port;
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server started on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Send a message to all connected clients except the sender
    private void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler client : clients) {
            if (client != exclude) {
                client.send(message);
            }
        }
    }

    // Remove a disconnected client
    private void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client removed: " + client.getUsername());
    }

    // Inner handler class
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;
        private volatile boolean running = true;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        String getUsername() {
            return username;
        }

        void send(String msg) {
            if (out != null) {
                out.println(msg);
                out.flush();
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Receive username first
                username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    username = "Unknown";
                }

                System.out.println(username + " joined.");
                broadcast("[Server] " + username + " joined the chat.", this);

                String line;
                while (running && (line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("/quit")) {
                        break;
                    }

                    // avoid double username since client already adds it
                    System.out.println("Broadcasting: " + line);
                    broadcast(line, this);
                }
            } catch (IOException e) {
                System.err.println("Connection error with " + username + ": " + e.getMessage());
            } finally {
                running = false;
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                } catch (IOException ignored) {}

                removeClient(this);
                broadcast("[Server] " + username + " left the chat.", this);
                System.out.println(username + " disconnected.");
            }
        }
    }

    // Launch server manually
    public static void main(String[] args) {
        int port = 5000; // ✅ Match client port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        new Server(port).start();
    }
}
