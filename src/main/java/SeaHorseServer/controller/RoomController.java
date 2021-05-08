package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.service.RoomService;
import SeaHorseServer.service.UserService;

import java.io.IOException;
import java.util.ArrayList;

public class RoomController {

    EchoThreadWriter thread;
    String[] lines;

    public RoomController(EchoThreadWriter thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;

        if (lines[1].equals("create")) {
            this.create();
        }
        else if (lines[1].equals("join")) {
            this.join();
        }
        else if (lines[1].equals("exit")) {
            this.exit();
        } 
        else if (lines[1].equals("fetch")) {
            this.fetch();
        }
        else if (lines[1].equals("fetch_one")) {
            this.fetchOne();
        }
    }

    private synchronized void join() throws IOException {
        String username = thread.getCurrentUser().getUsername();
        int roomId = Integer.parseInt(lines[2]);
        String password = (lines.length == 3) ? "" : lines[3];

        if (UserService.enterRoom(username, roomId, password)) {
            String message = "ROOM join " + roomId + " success";
            for (User user : UserRepo.getInstance().getUsersByRoomId(roomId)) {
                message = message + " " + user.getUsername();
            }
            RoomService.sendToRoom(roomId, message);
        } else {
            thread.send ("ROOM join " + roomId + " fail");
        }
    }

    private synchronized void create() throws IOException {
        String password = (lines.length == 2) ? "" : lines[2];
        int roomId = RoomService.createRoom(password);
        thread.send("ROOM create " + roomId + " " + password);
    }

    private synchronized void exit() throws IOException {
        int roomId = thread.getCurrentUser().getRoomId();
        if (UserService.exitRoom(thread.getCurrentUser().getUsername())) {
            String message = "ROOM exit " + thread.getCurrentUser().getUsername() + " success";
            thread.send(message);
            RoomService.sendToRoom(roomId, message);
        }
        else {
            String message = "ROOM exit " + thread.getCurrentUser().getUsername() + " fail";
            thread.send(message);
        }
    }

    private void fetch() throws IOException {
        ArrayList<Room> roomList = RoomRepo.getInstance().getRoomsList();
        String returnMessage = "ROOM fetch";
        for (Room room : roomList) {
            returnMessage = returnMessage + " " + room.getId();
        }
        thread.send(returnMessage);
    }

    private void fetchOne() throws IOException {
        int roomId = Integer.parseInt(lines[2]);
        Room room = RoomRepo.getInstance().getRoomById(roomId);
        if (room != null) {
            ArrayList<User> userListByRoomId = UserRepo.getInstance().getUsersByRoomId(roomId);
            thread.send("ROOM fetch_one " + room.getId() + " " + userListByRoomId.size() + " " + room.getCurrentTurn());
        } else {
            thread.send("ROOM fetch_one " + roomId + " fail");
        }
    }
}
