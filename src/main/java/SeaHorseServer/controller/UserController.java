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
    
    private void fetch() throws IOException {
        String username = lines[2];
        User user = UserRepo.getInstance().getUserByUserName(username);
        if (user == null) {
            thread.send("USER fetch fail " + username);
        }
        else 
            thread.send("USER fetch success " + username + " " + user.getRoomId() + " " + user.getColor() + " " + user.getStatus());
    }

    private void ready() throws IOException {
        if (thread.getCurrentUser() != null) {
            String username = thread.getCurrentUser().getUsername();
            int roomId = thread.getCurrentUser().getRoomId();
            if (UserService.ready(username)) {
                RoomService.sendToRoom(roomId, "USER ready " + thread.getCurrentUser().getUsername());
                if (RoomService.isEveryoneReady(roomId)) {
                    RoomService.sendToRoom(roomId, "USER start");
                }
            } else {
                thread.send("USER ready " + thread.getCurrentUser().getUsername() + " fail");
            }
        } else {
            thread.send("USER ready " + thread.getCurrentUser().getUsername() + " fail");
        }
    }
}
