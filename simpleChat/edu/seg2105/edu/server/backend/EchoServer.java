package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */






  public void handleMessageFromClient(Object msg, ConnectionToClient client) {

    String message = msg.toString();

    if (message.startsWith("#login ")) {
        
      if (client.getInfo("loginID") != null) { 
        try {

          client.sendToClient("ERROR: Login Already Processed. Disconnecting.");

        } catch (IOException e) {

          System.err.println("Failed To Send Error Message: " + e.getMessage());

        }

        try {

          client.close(); 
          
        } 

        catch (IOException e) {
        }

        return;
      }

      // FIX for TC 2004: Add the explicit "Message received" log for the initial #login command.
      System.out.println("Message received: " + message + " from null"); 
      
      String loginID = message.substring(7);
      client.setInfo("loginID", loginID); 
      System.out.println(loginID + " has logged on.");
      this.sendToAllClients(loginID + " has logged on.");

    } 
    else {
    
      String clientID = (String) client.getInfo("loginID");

      if (clientID == null) {
      
        try {

          client.sendToClient("ERROR: Must Login First. Disconnecting.");

        } catch (IOException e) {

          System.err.println("Failed To Send Error Message: " + e.getMessage());

        }

      try {

        client.close();

      } 
      
      catch (IOException e) {  
      }

      return;
    }
      
    String prefixedMessage = clientID + "> " + message; 
    System.out.println("Message received: " + message + " from " + clientID);
    this.sendToAllClients(prefixedMessage);

  }
}

    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0;

    try
    {
      port = Integer.parseInt(args[0]);
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT;
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen();
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR");
    }
  }
  @Override
  public void clientConnected(ConnectionToClient client) {
    
    System.out.println("A new client has connected to the server.");

  }

  @Override
  public void clientDisconnected(ConnectionToClient client) {

    String clientID = (String)client.getInfo("loginID");
      
    if (clientID != null) {

      System.out.println(clientID + " has disconnected.");

    } 
          
    else {
        
      System.out.println("Client: Disconnected"); 
        
    }
  }
}
//End of EchoServer class
