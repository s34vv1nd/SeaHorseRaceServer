package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadReader;
import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;

import java.io.IOException;

public class UserController {
    public UserController (EchoThreadWriter thread, String[] lines) throws IOException {
        if (lines[1].equals("fetch")) {
            this.fetch(thread, lines);
        }
    }

    private void fetch(EchoThreadWriter thread, String[] lines) throws IOException {
        String username = lines[2];
        User user = UserRepo.getInstance().getUserByUserName(username);
        thread.send("USER fetch " + username + " " + user.getColor() + " " + user.getStatus());
    }
}
