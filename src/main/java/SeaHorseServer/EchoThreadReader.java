package SeaHorseServer;

import java.io.*;
import java.net.*;

import SeaHorseServer.model.User;

public class EchoThreadReader extends Thread {
  protected Socket socket;
  private InputStream inp;
  private BufferedReader brinp;
  private EchoThreadWriter echoThreadWriter;

  public EchoThreadReader(Socket clientSocket, EchoThreadWriter threadWriter) {
    this.socket = clientSocket;
    this.inp = null;
    this.echoThreadWriter = threadWriter;
  }

  public void run() {
    try {
      inp = socket.getInputStream();
      brinp = new BufferedReader(new InputStreamReader(inp));
    } catch (IOException e) {
      return;
    }
    String line;
    while (true) {
      try {
        line = brinp.readLine();
        System.out.println(line);
        if ((line == null) || line.equalsIgnoreCase("QUIT")) {
          socket.close();
          return;
        } else {
          Dispatcher.getInstance().dispatch(echoThreadWriter, line);
        }
      }
      catch (java.net.SocketException e) {
        try {
          socket.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        if (echoThreadWriter.getCurrentUser() == null) {
          System.err.println("An user has disconnected.");
        }
        else {
          System.err.println("User " + echoThreadWriter.getCurrentUser().getUsername() + " has disconnected.");
        }
        return;
      }
      catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
  }
}