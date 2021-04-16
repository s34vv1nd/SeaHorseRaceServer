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
      thread.send("{status: 200, response: {message:'Login Successfully!'}}");
    }
    else {
      thread.send("{status: 401, response: {message: 'Invalid username or password!'}}");
    }
  }

  private boolean validateLogin(String username, String password) {
    UserRepo userRepo = new UserRepo();
    ArrayList<User> users = userRepo.ParseCsvToUser();

    for (User user : users) {
      if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
        return true;
      }
    }

    return false;
  }

  public void register (EchoThread thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (validateUsername(username)) {
      // thread.setUser();
      // Create string array user and add to database
      String[] stringUser = new String[4];
      stringUser[0] = username;
      stringUser[1] = password;
      stringUser[2] = "-1";
      stringUser[3] = "-1";
      UserRepo userRepo = new UserRepo();
      userRepo.AppendToCSVExample(Utils.USER_CSV_URL, stringUser);

      // Send response to client
      thread.send("{status: 200, response: {message: 'Register Successfully!'}}");
    }
    else {
      // Send response to client
      thread.send("{status: 401, response: {message: 'Username already exist!'}}");
    }
  }

  private boolean validateUsername (String username) {
    UserRepo userRepo = new UserRepo();
    ArrayList<User> users = userRepo.ParseCsvToUser();

    for (User user : users) {
      if (user.getUsername().equals(username)) {
        return false;
      }
    }

    return true;
  }

  public void logout (EchoThread thread, String[] lines) throws IOException {
    //TODO: handle all case of logout
      thread.send("{status: 200, response: 'Logout Successfully!'}");
//      thread.send("{status: 401, response: 'Username already exist!'}");

  }
}
