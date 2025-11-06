package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  // cache loginId
  private String loginId;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String loginId) 
  {
    try 
    {
      client = new ChatClient(host, port, this, loginId);
      this.loginId = loginId;

      // Connect immediately so 2003/2004 behave as expected.
      try {
        client.openConnection(); // triggers auto "#login <id>"
      } catch (Exception e) {
        System.out.println("ERROR - Can't setup connection! Terminating client.");
        System.exit(1);
      }
    } 
    catch(Exception exception) 
    {
      System.out.println("ERROR - Can't setup connection! Terminating client.");
      System.exit(1);
    }
    
    fromConsole = new Scanner(System.in); 
  }

  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;
      while (true) 
      {
        System.out.print("> ");
        message = fromConsole.nextLine();

        if (handleClientCommand(message)) continue;

        if (!client.isConnectedSafe()) {
          display("Not connected. Use #login to connect first.");
          continue;
        }
        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  private boolean handleClientCommand(String line) {
    if (line == null) return false;
    line = line.trim();
    if (!line.startsWith("#")) return false;

    String[] parts = line.split("\\s+");
    String cmd = parts[0].toLowerCase();

    try {
      switch (cmd) {
        case "#quit":
          try { if (client.isConnectedSafe()) client.closeConnection(); } catch (Exception ignore) {}
          System.exit(0);
          return true;

        case "#logoff":
          try { client.closeConnection(); display("Connection closed."); }
          catch (Exception e) { display("Connection closed."); }
          return true;

        case "#sethost":
          if (client.isConnectedSafe()) { display("Error: must be logged off."); return true; }
          if (parts.length < 2) { display("Usage: #sethost <host>"); return true; }
          client.setHost(parts[1]);
          display("Host set to " + parts[1]);
          return true;

        case "#setport":
          if (client.isConnectedSafe()) { display("Error: must be logged off."); return true; }
          if (parts.length < 2) { display("Usage: #setport <port>"); return true; }
          int p = Integer.parseInt(parts[1]);
          client.setPort(p);
          display("Port set to " + p);
          return true;

        case "#login":
          if (client.isConnectedSafe()) { display("Error: already connected."); return true; }
          client.openConnection(); // auto-sends "#login <id>"
          display("Connected.");
          return true;

        case "#gethost":
          display("Current host: " + client.getHost());
          return true;

        case "#getport":
          display("Current port: " + client.getPort());
          return true;

        default:
          display("Unknown command: " + cmd);
          return true;
      }
    } catch (Exception e) {
      display("Command error: " + e.getMessage());
      return true;
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host;
    int port = DEFAULT_PORT;
    String loginId;

    // Exact wording for 2002:
    if (args.length < 1) {
      System.out.println("ERROR - No login ID specified.  Connection aborted.");
      System.exit(1);
      return;
    }
    loginId = args[0];
    host = (args.length >= 2) ? args[1] : "localhost";
    try { if (args.length >= 3) port = Integer.parseInt(args[2]); } catch (Throwable t) { port = DEFAULT_PORT; }

    ClientConsole chat= new ClientConsole(host, port, loginId);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
