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
    String[] words;

    public RoomController(EchoThreadWriter thread, String[] words) throws IOException {
        this.thread = thread;
        this.words = words;
        if (thread.getCurrentUser() == null) {
            System.err.println("Not login");
            return;
        }
        switch (words[1]) {
            case "create":
                this.create();
                break;
            case "join":
                this.join();
                break;
            case "exit":
                this.exit();
                break;
            case "fetch":
                this.fetch();
                break;
            case "fetch_one":
                this.fetchOne();
                break;
            default:
                System.err.println("Cannot dispatch " + words[1]);
        }
    }

    private synchronized boolean checkBasicConditions() {
        if (thread.getCurrentUser() == null) return false;
        return true;
    }

    private synchronized void join() throws IOException {
        String username = thread.getCurrentUser().getUsername();
        int roomId = Integer.parseInt(words[2]);
        String password = (words.length == 3) ? "" : words[3];

        if (checkBasicConditions() && UserService.enterRoom(username, roomId, password)) {
            String message = "ROOM join success " + roomId;
            for (User user : UserRepo.getInstance().getUsersByRoomId(roomId)) {
                message = message + " " + user.getUsername();
            }
            RoomService.sendToRoom(roomId, message);
        } else {
            thread.send ("ROOM join fail " + roomId);
        }
    }

    private synchronized void create() throws IOException {
        String password = (words.length == 2) ? "" : words[2];
        int roomId = RoomService.createRoom(password);
        if (checkBasicConditions() && roomId != -1) {
            thread.send("ROOM create success " + roomId + " " + password);
        }
        else {
            thread.send("ROOM create fail");
        }
    }

    private synchronized void exit() throws IOException {
        int roomId = thread.getCurrentUser().getRoomId();
        if (checkBasicConditions() && UserService.exitRoom(thread.getCurrentUser().getUsername())) {
            String message = "ROOM exit success " + thread.getCurrentUser().getUsername();
            thread.send(message);
            RoomService.sendToRoom(roomId, message);
        }
        else {
            thread.send("ROOM exit fail");
        }
    }

    private void fetch() throws IOException {
        if (checkBasicConditions()) {
            ArrayList<Room> roomList = RoomRepo.getInstance().getRoomsList();
            String returnMessage = "ROOM fetch success";
            for (Room room : roomList) {
                returnMessage = returnMessage + " " + room.getId();
            }
            thread.send(returnMessage);
        }
        else {
            thread.send("ROOM fetch fail");
        }
    }

    private void fetchOne() throws IOException {
        int roomId = Integer.parseInt(words[2]);
        Room room = RoomRepo.getInstance().getRoomById(roomId);
        if (checkBasicConditions() && room != null) {
            ArrayList<User> userListByRoomId = UserRepo.getInstance().getUsersByRoomId(roomId);
            thread.send("ROOM fetch_one success " + room.getId() + " " + userListByRoomId.size() + " " + room.getCurrentTurn());
        } else {
            thread.send("ROOM fetch_one fail " + roomId);
        }
    }
}
