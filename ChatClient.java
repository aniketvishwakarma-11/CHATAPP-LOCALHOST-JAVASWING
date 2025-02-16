import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static String clientName;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);

        String serverAddress = "localhost";
        int port = 12345;

        try {
            Socket socket = new Socket(serverAddress, port);
            chatArea.append("Connected to server!\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask for client's name
            clientName = JOptionPane.showInputDialog(frame, "Enter your name:");
            chatArea.append("You are " + clientName + "\n");
            out.println(clientName);

            // **Thread to listen for server messages**
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        chatArea.append("Server :- " + serverMessage + "\n");

                        // If server sends "bye", exit
                        if (serverMessage.startsWith("Bye")) {
                            chatArea.append("Chat ended.\n");
                            socket.close();
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    chatArea.append("Connection lost!\n");
                }
            }).start();

            // **Send message when button is clicked**
            sendButton.addActionListener(e -> sendMessage());

        } catch (IOException e) {
            chatArea.append("Error connecting to server: " + e.getMessage() + "\n");
        }
    }

    private static void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.append(clientName + ":-"  + message + "\n");
            out.println(message);
            messageField.setText("");

            // Respond to "hii"
            if (message.equalsIgnoreCase("hii")) {
                out.println("Hii " + clientName);
            }
            // Respond to "bye" and close connection
            else if (message.equalsIgnoreCase("bye")) {
                out.println("Bye " + clientName);
                chatArea.append("Chat ended.\n");
                System.exit(0);
            }
        }
    }
}
