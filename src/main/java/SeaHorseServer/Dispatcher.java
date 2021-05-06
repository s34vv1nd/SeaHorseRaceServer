package SeaHorseServer;

import SeaHorseServer.controller.GameController;
import SeaHorseServer.controller.RoomController;
import SeaHorseServer.controller.SessionController;
import SeaHorseServer.controller.UserController;

import java.io.IOException;

public class Dispatcher {

  static private Dispatcher instance;

  public static Dispatcher getInstance() {
    if (instance == null){
      instance = new Dispatcher();
    }
    return instance;
  }

  void dispatch(EchoThreadWriter thread, String request) throws IOException {

    String[] lines = request.split(" ");
    if (lines[0].equals("SESSION")) {
      new SessionController(thread, lines);
    }
    else if (lines[0].equals("ROOM")) {
      new RoomController(thread, lines);
    }
    else if (lines[0].equals("GAME")) {
      new GameController(thread, lines);
    }
    else if (lines[0].equals("USER")) {
      new UserController(thread, lines);
    }
    else {
      thread.send("UNDEFINED");
    }
  }
}
