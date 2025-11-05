package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient
{
  // Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  // New constant for Exercise 1b
  public static final int DEFAULT_PORT = 5555; // Use the actual default port for your app

  
  // Constructors ****************************************************
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    // Note: The original SimpleChat may open the connection here, but
    // the commands now allow the user to control connection status.
    // openConnection(); // Remove if you want #login to be the first action
  }

  
  // Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI (user input/commands).           
   *
   * This method implements all client commands for Exercise 2a.
   */
  public void handleMessageFromClientUI(String message)
  {
    
    if (message.startsWith("#")) {
        
        String command = message.split(" ")[0].toLowerCase();
        
        if (command.equals("#quit")) {
            
            quit(); 
        }
        
        else if (command.equals("#logoff")) {

            if (isConnected()) {
                
                try {

                    closeConnection();
                    System.out.println("Client: Logged Off");

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
                    clientUI.display("ERROR: Command requires an argument.");
                    return;
                }
                String argument = parts[1];
                
                if (command.equals("#sethost")) {
                    setHost(argument);
                    
                    System.out.println("Host Set To: " + argument);
                } else {

                    try {
                        int newPort = Integer.parseInt(argument);
                        setPort(newPort);
                        
                        System.out.println("Port Set: " + newPort);
                    } catch (NumberFormatException e) {
                        clientUI.display("ERROR: Invalid Port Number Format.");
                    }
                }
            } else {
                
                clientUI.display("ERROR: Must be logged off to change connection settings.");
            }
        }
        
        
        else {
            clientUI.display("ERROR: Unknown Command.");
        }
        
    } 
    
    else {
        try {
            sendToServer(message);
        } catch (IOException e) {
            clientUI.display("Could not send message to server. Terminating client.");
            quit();
        }
    }
}
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  // --- Exercise 1a Implementations (Client Shutdown Response) ---

  /**
   * Hook method called after the connection has been closed gracefully.
   */
  @Override
  public void connectionClosed() {
    // Modify the client so it responds to the shutdown of the server by printing a message... (Exercise 1a)
    System.out.println("--- Server has shut down. ---");
    
    // ... and quitting. (Exercise 1a)
    System.exit(0);
  }


  /**
   * Hook method called when an exception is raised by the client's thread.
   */
  @Override
  public void connectionException(Exception exception) {
    // Handle abnormal termination the same way.
    System.out.println("--- Connection terminated unexpectedly. ---");

    System.exit(0);
  }
}
// End of ChatClient class