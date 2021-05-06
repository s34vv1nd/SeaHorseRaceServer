package SeaHorseServer.controller;

import java.io.IOException;

import SeaHorseServer.EchoThreadReader;
import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.service.UserService;
import SeaHorseServer.utils.Utils;

public class SessionController {
  EchoThreadWriter thread;
  String[] lines;

  public SessionController(EchoThreadWriter thread, String[] lines) throws IOException {
    this.thread = thread;
    this.lines = lines;

    if (lines[1].equals("login")) {
      this.login(thread, lines);
    } else if (lines[1].equals("register")) {
      this.register(thread, lines);
    } else if (lines[1].equals("logout")) {
      this.logout(thread, lines);
    }
  }

  public void login(EchoThreadWriter thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (UserService.login(thread, username, password)) {
      thread.send("SESSION login " + username + " success");
    } else {
      thread.send("SESSION login " + username + " fail");
    }
  }

  public void register(EchoThreadWriter thread, String[] lines) throws IOException {
    String username = lines[2];
    String password = lines[3];
    if (UserService.register(username, password)) {
      thread.send("SESSION register success");
    } else {
      thread.send("SESSION register fail");
    }
  }

  public void logout(EchoThreadWriter thread, String[] lines) throws IOException {
    if (UserService.logout(thread)) {
      thread.send("SESSION logout");
    }
  }
}
