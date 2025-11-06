package edu.seg2105.edu.server.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.seg2105.client.common.ChatIF;

/**
 * Simple server-side console that can issue #commands and broadcast messages.
 * Warning: Some of the code here is cloned from ClientConsole.
 */
public class ServerConsole implements ChatIF {
  private final EchoServer server;

  public ServerConsole(EchoServer server) {
    this.server = server;
  }

  public void accept() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      for (;;) {
        System.out.print("SERVER> ");
        String line = br.readLine();
        if (line == null) break;

        if (line.startsWith("#")) {
          handleServerCommand(line.trim());
        } else {
          // EXACT prefix required by tests:
          server.sendToAllClients("SERVER MESSAGE> " + line);
          display("SERVER MESSAGE> " + line);
        }
      }
    } catch (Exception e) {
      display("Server console error: " + e.getMessage());
    }
  }

  private void handleServerCommand(String line) {
    String[] parts = line.split("\\s+");
    String cmd = parts[0].toLowerCase();

    try {
      switch (cmd) {
        case "#quit":
          try { server.close(); } catch (Exception ignore) {}
          System.out.println("Server quitting...");
          System.exit(0);
          break;

        case "#stop":
          server.stopListening();
          // serverStopped() will print the exact line.
          break;

        case "#close":
          server.stopListening();
          try { server.close(); } catch (Exception ignore) {}
          // EchoServer.clientDisconnected will print "<loginID> has disconnected."
          break;

        case "#setport":
          if (parts.length < 2) { display("Usage: #setport <port>"); break; }
          if (server.isListening()) { display("Error: server must be closed to set port."); break; }
          int p = Integer.parseInt(parts[1]);
          server.setPort(p);
          display("Port set to " + p);
          break;

        case "#start":
          server.listen(); // serverStarted() prints the exact line.
          break;

        case "#getport":
          display("Current port: " + server.getPort());
          break;

        default:
          display("Unknown server command: " + cmd);
      }
    } catch (Exception e) {
      display("Command failed: " + e.getMessage());
    }
  }

  @Override
  public void display(String message) {
    System.out.println("SERVER MSG> " + message);
  }

  public static void main(String[] args) throws Exception {
    int port = EchoServer.DEFAULT_PORT;
    try { if (args.length > 0) port = Integer.parseInt(args[0]); } catch (Throwable ignore) {}

    EchoServer server = new EchoServer(port);
    server.listen(); // EchoServer prints "Server listening for connections on port ..."
    new ServerConsole(server).accept();
  }
}
