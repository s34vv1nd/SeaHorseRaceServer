package SeaHorseServer.controller;

import SeaHorseServer.EchoThread;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;

import java.io.IOException;

public class UserController {
    EchoThread thread;
    String[] lines;
    public UserController (EchoThread thread, String[] lines) throws IOException {
        if (lines[1].equals("fetch")) {
            this.fetch(thread, lines);
        }
    }

    private void fetch(EchoThread thread, String[] lines) throws IOException {
        String username = lines[2];
        User user = UserRepo.getInstance().getUserByUserName(username);
        thread.send("USER fetch " + username + " " + user.getColor());
    }
}
