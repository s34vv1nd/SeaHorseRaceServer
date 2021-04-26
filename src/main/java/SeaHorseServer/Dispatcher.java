package SeaHorseServer;

import SeaHorseServer.controller.GameController;
import SeaHorseServer.controller.RoomController;
import SeaHorseServer.controller.SessionController;
import SeaHorseServer.controller.UserController;

import java.io.IOException;

public class Dispatcher {

  static private Dispatcher instance;
  private SessionController sessionController;
  private RoomController roomController;
  private GameController gameController;
  private UserController userController;

  public static Dispatcher getInstance() {
    if (instance == null){
      instance = new Dispatcher();
    }
    return instance;
  }

  void dispatch(EchoThreadWriter thread, String request) throws IOException {

    String[] lines = request.split(" ");
    if (lines[0].equals("SESSION")) {
      sessionController = new SessionController(thread, lines);
    }
    else if (lines[0].equals("ROOM")) {
      roomController = new RoomController(thread, lines);
    }
    else if (lines[0].equals("GAME")) {
      gameController = new GameController(thread, lines);
    }
    else if (lines[0].equals("USER")) {
      userController = new UserController(thread, lines);
    }
    else {
      thread.send("{status: 404, response: {error: Not found!}");
    }
  }
}
