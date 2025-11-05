package edu.seg2105.edu.server.backend; 

import java.io.*;
import edu.seg2105.client.common.ChatIF; 

public class ServerConsole implements ChatIF, Runnable {
    
    private EchoServer server;
    
    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    @Override
    public void display(String message) {
        
        System.out.println("SERVER STATUS: " + message);

    }

    /**
     * Replaces the old accept() method. Runs in a separate thread to handle 
     * console input without blocking the main server thread.
     */
    @Override
    public void run() { 
        
        try {
            
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;
            System.out.println("Server Console Running. Type messages or commands (#).");

            while (true) {
                
                message = fromConsole.readLine(); 
                handleMessageFromServerUI(message);

            }
        } catch (Exception e) {

            System.out.println("FATAL ERROR: Server console input thread failed.");
            
        }
    }

    public void handleMessageFromServerUI(String message) {
        
        if (message.startsWith("#")) {
                
            String command = message.split(" ", 2)[0].trim().toLowerCase(); 
            
            if (command.equals("#quit")) {

                System.out.println("Server: Quitting...");

                try {

                    server.close();

                } 
                catch (IOException e) {
                    
                    System.out.println("Warning: Error during server shutdown.");

                }

                System.exit(0);

            }
            
            else if (command.equals("#stop")) {

                if (server.isListening()) {

                    server.stopListening();
                    System.out.println("Server stopped listening for new clients.");

                } 

                else {

                    System.out.println("ERROR: Server is already stopped.");

                }
            }
            
            else if (command.equals("#close")) {

                System.out.println("Processing #close command..."); 

                try {

                    server.close(); 
                    System.out.println("Server closed. All clients disconnected."); 

                } 
                catch (IOException e) {

                    System.out.println("FATAL ERROR: Server closure failed. Check stack trace below:");
                    e.printStackTrace(); 
                    
                }
            }
            
            else if (command.equals("#start")) {

                if (!server.isListening()) {

                    try {

                        server.listen();
                        System.out.println("Server listening for new clients on port " + server.getPort());

                    } 
                    
                    catch (IOException e) {

                        System.out.println("ERROR starting server: " + e.getMessage());

                    }
                } 
                
                else {

                    System.out.println("ERROR: Server already listening.");

                }
            }
            
            else if (command.equals("#getport")) {

                System.out.println("Current Port: " + server.getPort());

            }
            
            else if (command.equals("#setport")) {
                
                if (!server.isListening()) { 
                    
                    String[] parts = message.split(" ", 2);

                    if (parts.length < 2) {
                        System.out.println("ERROR: #setport requires a port number!");
                        return;
                    }
                    
                    String portText = parts[1];

                    try {

                        int newPort = Integer.parseInt(portText);
                        server.setPort(newPort);
                        System.out.println("Port set to: " + newPort);

                    } 
                    catch (NumberFormatException e) {

                        System.out.println("ERROR: INVALID port num format.");

                    }
                } 
                else {

                    System.out.println("ERROR: Must #stop or #close server before changing port!");

                }
            }
            
            else {

                System.out.println("ERROR: Unknown Command.");

            }
            
        } 
        
        else {
            
            String fullMessage = "SERVER> " + message;
            System.out.println(fullMessage);
                        
            try {

                server.sendToAllClients(fullMessage);
                
            }
            catch (Exception e) {

                System.out.println("WARNING: Cannot send message to clients.");

            }
        }
    }
}