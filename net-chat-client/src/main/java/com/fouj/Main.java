package com.fouj;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if(username != null && !username.trim().equals(""))
            new TCPClient("127.0.0.1", 6666, username);
    }
}