package SeaHorseServer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import SeaHorseServer.EchoThread;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.utils.Utils;

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
      // Create user and set current user to this user
      User user = new User(username, password, -1, -1);
      thread.setUser(user);

      // Send response to client
      thread.send("SESSION login success");
    }
    else {
      thread.send("SESSION login fail");
    }
  }

  private boolean validateLogin(String username, String password) {
    User user = UserRepo.getInstance().getUserByUserName(username);

    if (user != null && user.getPassword().equals(password)) {
      return true;
    }
    return false;
  }

  public void register (EchoThread thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (validateUsername(username)) {
      // Create string array user and add to database
      String[] stringUser = new String[1];
      stringUser[0] = "\n" + username + "," + password + "," + "-1,-1";
      UserRepo.getInstance().AppendToCSVExample(Utils.USER_CSV_URL, stringUser);

      // Add new user to user list
      UserRepo.getInstance().addUser(username, password);
      // Send response to client
      thread.send("SESSION register success");
    }
    else {
      // Send response to client
      thread.send("SESSION register fail");
    }
  }

  private boolean validateUsername (String username) {
    User user = UserRepo.getInstance().getUserByUserName(username);

    if (user != null) {
      return false;
    }
    return true;
  }

  public void logout (EchoThread thread, String[] lines) throws IOException {
      thread.send("SESSION logout");
  }
}
