import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatServer {
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static String serverName;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Server");
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

        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            chatArea.append("Server started. Waiting for client...\n");

            Socket socket = serverSocket.accept();
            chatArea.append("Client connected!\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask for server's name
            serverName = JOptionPane.showInputDialog(frame, "Enter your name:");
            chatArea.append("You are " + serverName + "\n");
            out.println("Enter your name:");
            String clientName = in.readLine();
            chatArea.append("Client name: " + clientName + "\n");

            // **Thread to listen for client messages**
            new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = in.readLine()) != null) {
                        chatArea.append(clientName + ": " + clientMessage + "\n");

                        // Respond to "hii"
                        if (clientMessage.equalsIgnoreCase("hii")) {
                            out.println("Hii " + clientName);
                        } 
                        // Respond to "bye" and close connection
                        else if (clientMessage.equalsIgnoreCase("bye")) {
                            out.println("Bye " + clientName);
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
            sendButton.addActionListener(e -> sendMessage(clientName));

        } catch (IOException e) {
            chatArea.append("Server error: " + e.getMessage() + "\n");
        }
    }

    private static void sendMessage(String clientName) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.append(serverName +":-"+ message + "\n");
            out.println(message);
            messageField.setText("");

            // Respond to "hii"
            if (message.equalsIgnoreCase("hii")) {
                out.println("Hii " + serverName);
            }
            // Respond to "bye" and close connection
            else if (message.equalsIgnoreCase("bye")) {
                out.println("Bye " + serverName);
                chatArea.append("Chat ended.\n");
                System.exit(0);
            }
        }
    }
}
