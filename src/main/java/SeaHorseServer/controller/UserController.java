package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadReader;
import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.service.RoomService;
import SeaHorseServer.service.UserService;

import java.io.IOException;

public class UserController {

    EchoThreadWriter thread;
    String[] lines;

    public UserController (EchoThreadWriter thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;
        
        switch (lines[1]) {
            case "fetch":
                this.fetch();
                break;
            case "ready":
                this.ready();
                break;
            default:
                System.err.println("Cannot dispatch " + lines[1]);
        }
    }

    private synchronized boolean checkBasicConditions() {
        if (thread.getCurrentUser() == null) return false;
        if (thread.getCurrentUser().getRoomId() == -1) return false;
        return true;
    }
    
    private void fetch() throws IOException {
        String username = lines[2];
        User user = UserRepo.getInstance().getUserByUserName(username);
        if (checkBasicConditions() && user != null) {
            thread.send("USER fetch success " + username + " " + user.getRoomId() + " " + user.getColor() + " " + user.getStatus());
        }
        else {
            thread.send("USER fetch fail " + username);
        }
    }

    private void ready() throws IOException {
        if (checkBasicConditions()) {
            String username = thread.getCurrentUser().getUsername();
            if (UserService.ready(username)) {
                int roomId = thread.getCurrentUser().getRoomId();
                RoomService.sendToRoom(roomId, "USER ready success" + thread.getCurrentUser().getUsername());
                if (RoomService.isEveryoneReady(roomId)) {
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
