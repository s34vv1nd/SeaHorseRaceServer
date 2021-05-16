package SeaHorseServer.controller;

import java.io.IOException;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.service.UserService;

public class SessionController {

  EchoThreadWriter thread;
  String[] words;

  public SessionController(EchoThreadWriter thread, String[] words) throws IOException {
    this.thread = thread;
    this.words = words;

    switch (words[1]) {
      case "login":
        this.login();
        break;
      case "register":
        this.register();
        break;
      case "logout":
        this.logout();
        break;
      default:
        System.err.println("Cannot dispatch " + words[1]);
    }
  }

  public void login() throws IOException {
    String username = words[2];
    String password = words[3];
    if (UserService.login(thread, username, password)) {
      thread.send("SESSION login success " + username);
    } else {
      thread.send("SESSION login fail " + username);
    }
  }

  public void register() throws IOException {
    String username = words[2];
    String password = words[3];
    if (UserService.register(username, password)) {
      thread.send("SESSION register success");
    } else {
      thread.send("SESSION register fail");
    }
  }

  public void logout() throws IOException {
    if (UserService.logout(thread)) {
      thread.send("SESSION logout success");
    } else {
      thread.send("SESSION logout fail");
    }
  }
}
