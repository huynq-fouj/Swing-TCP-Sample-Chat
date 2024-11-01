package com.fouj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private BufferedReader reader;
    private PrintWriter printWriter;
    private Socket socket;
    private TCPServer server;

    public ClientHandler(Socket socket, TCPServer server) {
        this.socket = socket;
        this.server = server;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String data) {
        printWriter.println(data);
    }

    @Override
    public void run() {
        String data;
        try {
            while((data = reader.readLine()) != null) {
                server.listenForMessages(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if(reader != null) reader.close();
            if(printWriter != null) printWriter.close();
            if(socket != null) socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
