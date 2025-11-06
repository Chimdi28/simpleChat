// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  /**
   * Login id for this client (required by tests).
   */
  private String loginId;

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  /**
   * Overloaded constructor used when we want to login after connecting.
   * (ClientConsole passes loginId.)
   */
  public ChatClient(String host, int port, ChatIF clientUI, String loginId)
    throws IOException
  {
    super(host, port);
    this.clientUI = clientUI;
    this.loginId = loginId;
  }

  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
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

  /* ===== Test-case aligned behavior ===== */

  @Override
  protected void connectionEstablished() {
    // Auto-login so server can print "<loginID> has logged on."
    if (loginId != null && !loginId.isEmpty()) {
      try { sendToServer("#login " + loginId); }
      catch (IOException e) { clientUI.display("Could not login: " + e.getMessage()); }
    }
  }

  @Override
  protected void connectionClosed() {
    // Keep quiet here; client console prints "Connection closed." for #logoff.
  }

  @Override
  protected void connectionException(Exception exception) {
    // When server closes while connected, show exact message expected by tests.
    clientUI.display("The server has shut down.");
    System.exit(0);
  }

  // Helpers:
  public boolean isConnectedSafe() { try { return isConnected(); } catch (Exception e) { return false; } }
  public void setLoginId(String id) { this.loginId = id; }
  public String getLoginId() { return this.loginId; }
}
//End of ChatClient class
