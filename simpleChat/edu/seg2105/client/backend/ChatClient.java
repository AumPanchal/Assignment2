package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient {

    ChatIF clientUI; 
    public static final int DEFAULT_PORT = 5555; 
    private String loginID; 

    public ChatClient(String loginID, String host, int port, ChatIF clientUI) throws IOException {

        super(host, port);
        this.clientUI = clientUI;
        this.loginID = loginID;
        openConnection();

    }

    public String getLoginID() {

        return loginID;

    }
  
    public void handleMessageFromServer(Object msg) {

        clientUI.display(msg.toString());

    }

        /**
     * This method handles all data coming from the UI (user input/commands).           
     *
     * This method implements all client commands for Exercise 2a.
     */
    public void handleMessageFromClientUI(String message) {
    

    if (message.startsWith("#")) {
        
        String command = message.split(" ")[0].toLowerCase();
        
        if (command.equals("#quit")) {

            System.out.println("Client: Quitting");
            quit(); 
        }
        
        else if (command.equals("#logoff")) {

            if (isConnected()) {

                try {

                    closeConnection();
                    clientUI.display("Connection closed.");

                } 
                catch (IOException e) {

                    System.out.println("Client: Error Logging Off");

                }
            } 

            else {

                clientUI.display("Client: Already Logged Off");

            }
        }
        
        else if (command.equals("#gethost")) {

            System.out.println("Current Host: " + getHost());

        }
        
        else if (command.equals("#getport")) {

            System.out.println("Current Port: " + getPort());

        }
        
        else if (command.equals("#login")) {

            if (!isConnected()) {

                try {

                    openConnection();
                    System.out.println("Attempting To Connect...");

                } 
                catch (IOException e) {

                    clientUI.display("ERROR: Cannot Open Connection: " + e.getMessage());

                }
            } 
            else {

                clientUI.display("ERROR: Already Connected To Server.");

            }
        }
        
        else if (command.equals("#sethost") || command.equals("#setport")) {
            
            if (!isConnected()) {
                
                String[] parts = message.split(" ", 2);

                if (parts.length < 2) {

                    clientUI.display("ERROR: Command Requires Argument.");
                    return;

                }

                String argument = parts[1];
                
                if (command.equals("#sethost")) {

                    setHost(argument);
                    System.out.println("Host Set To: " + argument);

                } 

                else {

                    try {

                        int newPort = Integer.parseInt(argument);
                        setPort(newPort);
                        System.out.println("Port Set: " + newPort);

                    } 
                    catch (NumberFormatException e) {

                        clientUI.display("ERROR: Invalid Port Number Format.");

                    }
                }
            } 

            else {

                clientUI.display("ERROR: Must Be Logged Off To Change Connection Settings.");

            }
        }
        else {

            clientUI.display("ERROR: Unknown Command.");

        }
        
    } 
    else {

        try {

            sendToServer(message);

        }
        catch (IOException e) {
            clientUI.display("Couldn't Send Message To Server. Terminating Client.");
            quit();
        }
    }
}
  
    public void quit() {
    
        try {

            closeConnection();

        }
        catch(IOException e) {
        }

        System.exit(0);
    }

    @Override
    protected void connectionEstablished() {
        try {
        
            sendToServer("#login " + loginID);

        }
      
        catch (IOException e) {

            clientUI.display("ERROR: Failed To Send Automatic Login Command. Terminating.");
            quit();
        
        }
    }

    @Override
    public void connectionClosed() {    
    }

    @Override
    public void connectionException(Exception exception) {

        System.out.println("Server: Shut Down");
        System.exit(0);

    }
}
// End of ChatClient class