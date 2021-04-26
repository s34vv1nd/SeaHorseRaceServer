package SeaHorseServer;

import java.io.*;
import java.net.*;

import SeaHorseServer.model.User;

public class EchoThread extends Thread {
  protected Socket socket;
  private InputStream inp;
  private BufferedReader brinp;
  private DataOutputStream out;

  private User currentUser;

  public EchoThread(Socket clientSocket) {
    this.socket = clientSocket;
    this.inp = null;
    this.brinp = null;
    this.out = null;
    this.currentUser = null;
  }

  public void setUser(User user) {
    this.currentUser = user;
  }
  public User getCurrentUser() { return this.currentUser; }

  public void run() {
    try {
      inp = socket.getInputStream();
      brinp = new BufferedReader(new InputStreamReader(inp));
      out = new DataOutputStream(socket.getOutputStream());
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
          Dispatcher.getInstance().dispatch(this, line);
        }
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
  }

  public void send(String line) throws IOException {
    this.out.writeBytes(line + "\r");
    this.out.flush();
  }
}