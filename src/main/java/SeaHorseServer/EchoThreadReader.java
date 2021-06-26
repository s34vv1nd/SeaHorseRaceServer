package SeaHorseServer;

import java.io.*;
import java.net.*;

import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.service.RoomService;
import SeaHorseServer.service.UserService;

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
        System.out.println("Server received: " + line);
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
        User user = echoThreadWriter.getCurrentUser();
        if (user == null) {
          System.err.println("An user has disconnected.");
        }
        else {
          System.err.println("User " + user.getUsername() + " has disconnected.");
          int roomId = user.getRoomId();
          try {
            UserService.logout(echoThreadWriter);
            if (roomId != -1) {
              RoomService.sendToRoom(roomId, "ROOM exit success " + user.getUsername());
            }
          } catch (IOException e1) {
            e1.printStackTrace();
          }
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