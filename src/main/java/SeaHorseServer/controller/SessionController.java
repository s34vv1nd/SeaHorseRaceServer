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
      User user = new User(username, password, -1, -1);
      thread.setUser(user);
      thread.send("{status: 200, response: {message:'Login Successfully!'}}");
    }
    else {
      thread.send("{status: 401, response: {message: 'Invalid username or password!'}}");
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

      UserRepo.getInstance().addUser(username, password);
      // Send response to client
      thread.send("{status: 200, response: {message: 'Register Successfully!'}}");
    }
    else {
      // Send response to client
      thread.send("{status: 401, response: {message: 'Username already exist!'}}");
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
    //TODO: handle all case of logout
      thread.send("{status: 200, response: 'Logout Successfully!'}");
//      thread.send("{status: 401, response: 'Username already exist!'}");

  }
}
