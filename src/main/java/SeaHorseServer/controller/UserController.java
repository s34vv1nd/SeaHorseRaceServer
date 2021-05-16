package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadReader;
import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.service.GameService;
import SeaHorseServer.service.RoomService;
import SeaHorseServer.service.UserService;

import java.io.IOException;

public class UserController {

    EchoThreadWriter thread;
    String[] words;

    public UserController(EchoThreadWriter thread, String[] words) throws IOException {
        this.thread = thread;
        this.words = words;
        if (thread.getCurrentUser() == null) {
            System.err.println("Not login");
            return;
        }
        if (thread.getCurrentUser().getRoomId() == -1) {
            System.err.println("Not in room");
            return;
        }
        switch (words[1]) {
            case "fetch":
                this.fetch();
                break;
            case "ready":
                this.ready();
                break;
            default:
                System.err.println("Cannot dispatch " + words[1]);
        }
    }

    private synchronized boolean checkBasicConditions() {
        if (thread.getCurrentUser() == null)
            return false;
        if (thread.getCurrentUser().getRoomId() == -1)
            return false;
        return true;
    }

    private void fetch() throws IOException {
        String username = words[2];
        User user = UserRepo.getInstance().getUserByUserName(username);
        if (checkBasicConditions() && user != null) {
            thread.send("USER fetch success " + username + " " + user.getRoomId() + " " + user.getColor() + " "
                    + user.getStatus());
        } else {
            thread.send("USER fetch fail " + username);
        }
    }

    private void ready() throws IOException {
        if (checkBasicConditions()) {
            String username = thread.getCurrentUser().getUsername();
            int status = Integer.parseInt(words[3]);
            if (UserService.ready(username, status)) {
                int roomId = thread.getCurrentUser().getRoomId();
                RoomService.sendToRoom(roomId, "USER ready success " + thread.getCurrentUser().getUsername() + " " + status);
                if (RoomService.isEveryoneReady(roomId)) {
                    GameService.startGame(roomId);
                    RoomService.sendToRoom(roomId, "USER start");
                }
            } else {
                thread.send("USER ready fail " + thread.getCurrentUser().getUsername());
            }
        } else {
            thread.send("USER ready fail " + thread.getCurrentUser().getUsername());
        }
    }
}
