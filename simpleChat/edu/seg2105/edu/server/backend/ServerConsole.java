package edu.seg2105.edu.server.backend;

import java.io.*;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {
    
    private EchoServer server;
    
    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    @Override
    public void display(String message) {
        
        System.out.println("Server Status: " + message);
    }

    public void accept() {

        try {

            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;
            System.out.println("Server Console: Running. Type Messages or Commands.");

            while (true) {
                
                message = fromConsole.readLine();
                handleMessageFromServerUI(message);

            }

        } 

        catch (Exception e) {

            System.out.println("FATAL ERROR: Server Console Input Failed.");

        }
    }

    public void handleMessageFromServerUI(String message) {
        
        System.out.println(message); 
        
        try {
            
            String fullMessage = "Server MSG> " + message;
            server.sendToAllClients(fullMessage);
            
        } catch (Exception e) {

            System.out.println("Warning: Cannot Send Message. Server May Be Closed or Stopped.");

        }
    }
}