package com.fouj;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TCPClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter printWriter;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField textField;
    private String userName;
    private String userId;

    public TCPClient(String serverAddress, int port, String userName) {
        this.userName = userName;
        this.userId = UUID.randomUUID().toString();

        JFrame frame = new JFrame("Client chat - " + userName);
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textField = new JTextField(40);
        textField.setPreferredSize(new Dimension(500, 30));
        textField.addActionListener(e -> submitMessage());
        textField.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(100, 30));
        sendButton.addActionListener(e -> submitMessage());
        sendButton.setFont(new Font("Arial", Font.PLAIN, 13));
        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(textField);
        inputPanel.add(sendButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            socket = new Socket(serverAddress, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> listenForMessages()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void submitMessage() {
        String message = textField.getText();
        if(!message.trim().isEmpty()) {
            sendMessage(message);
            textField.setText("");
        }
    }

    private void listenForMessages() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                String[] parts = message.split(":", 3);
                String senderId = parts[0];
                String sender = parts[1];
                String msgContent = parts[2];
                addMessage(msgContent, senderId, sender);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private void addMessage(String message, String senderId, String sender) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(new EmptyBorder(5, 10, 5, 10));


        JLabel senderLabel = new JLabel(sender + " - You");
        JLabel messageLabel = new JLabel("<html><div style=\"width: 350px; word-break: break-all;\">" + message + "</div></html>");
        JLabel timeLabel = new JLabel(new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date()));

        senderLabel.setFont(new Font("Arial", Font.BOLD, 13));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        timeLabel.setForeground(Color.GRAY);

        messagePanel.add(senderLabel);
        messagePanel.add(messageLabel);
        messagePanel.add(timeLabel);
        messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if(senderId.equals(userId)) {
            messageLabel.setForeground(Color.BLUE);
            senderLabel.setForeground(Color.BLUE);
        }

        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    private void closeConnections() {
        try {
            if (reader != null) reader.close();
            if (printWriter != null) printWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        printWriter.println(userId + ":" + userName + ":" + message);
    }

}
