package com.fouj;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPServer {

    private ServerSocket serverSocket;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private CopyOnWriteArrayList<ClientHandler> clients;

    public TCPServer(String ipAddress, int port) {

        JFrame frame = new JFrame("Server chat");

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        clients = new CopyOnWriteArrayList<>();

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            serverSocket = new ServerSocket(port, 5, inetAddress);
            addMessage("<span style=\"color: green;\">System</span>", "Server is ready...");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listenForMessages(String data) {
        clients.forEach(client -> client.sendMessage(data));
        String arr[] = data.split(":", 3);
        String sender = arr[1];
        String message = arr[2];
        addMessage(sender, message);
    }

    public void addMessage(String sender, String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("<html><div style=\"width: 400px; word-wrap: break-word; word-break: break-word;\"><b>" + sender + ":</b> " + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);

        messagePanel.add(messageLabel);
        messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

}
