package SeaHorseServer.controller;

import java.io.IOException;

import SeaHorseServer.EchoThread;

public class SessionController {
  public void login(EchoThread thread, String request) throws IOException {
    String[] lines = request.split(" ");
    String username = lines[2];
    String password = lines[3];
    if (validateLogin(username, password)) {
      // thread.setUser();
      thread.send("{status: 200, response: 'Login Successfully!'}");
    }
    else {
      thread.send("{status: 401, response: 'Invalid username or password!'}");
    }
  }

  private boolean validateLogin(String username, String password) {

    return true;
  }
}
