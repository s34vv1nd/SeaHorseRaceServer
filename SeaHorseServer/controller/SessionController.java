package SeaHorseServer.controller;

import java.io.IOException;

import SeaHorseServer.EchoThread;

public class SessionController {
  EchoThread thread;
  String[] lines;
  public SessionController(EchoThread thread, String[] lines) throws IOException {
    this.thread = thread;
    this.lines = lines;

    if (lines[1].equals("login")) {
      this.login(thread, lines);
    }
    else if (lines[1].equals("register")) {
      this.register(thread, lines);
    }
    else if (lines[1].equals("logout")) {
      this.logout(thread, lines);
    }
  }

  public void login(EchoThread thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (validateLogin(username, password)) {
      // thread.setUser();
      System.out.println(username + " " + password);
      thread.send("{status: 200, response: 'Login Successfully!'}");
    }
    else {
      thread.send("{status: 401, response: 'Invalid username or password!'}");
    }
  }

  private boolean validateLogin(String username, String password) {
    //TODO: validate login from database
    return true;
  }

  public void register (EchoThread thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (validateUsername(username)) {
      // thread.setUser();
      thread.send("{status: 200, response: 'Register Successfully!'}");
    }
    else {
      thread.send("{status: 401, response: 'Username already exist!'}");
    }
  }

  private boolean validateUsername (String username) {
    //TODO: validate user name from database
    return true;
  }

  public void logout (EchoThread thread, String[] lines) throws IOException {
    //TODO: handle all case of logout
      thread.send("{status: 200, response: 'Logout Successfully!'}");
//      thread.send("{status: 401, response: 'Username already exist!'}");

  }
}
