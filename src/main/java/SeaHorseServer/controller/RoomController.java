package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.ThreadedEchoServer;
import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class RoomController {
    EchoThreadWriter thread;
    String[] lines;
    public RoomController(EchoThreadWriter thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;

        if (lines[1].equals("create")) {
            this.create(thread, lines);
        }
        else if (lines[1].equals("join")) {
            this.join(thread, lines);
        }
        else if (lines[1].equals("exit")) {
            this.exit(thread, lines);
        } 
        else if (lines[1].equals("fetch")) {
            this.fetch(thread, lines);
        }
        else if (lines[1].equals("fetch_one")) {
            this.fetchOne(thread, lines);
        }
    }

    private void join(EchoThreadWriter thread, String[] lines) throws IOException {
        int roomId = Integer.parseInt(lines[2]);
        int[] colored = new int[4];
        int color = -1;
        String password = (lines.length == 3) ? "" : lines[3];

        if (validateRoom(roomId, password)) {
            ArrayList<User> userListByRoomId = UserRepo.getInstance().getUsersByRoomId(roomId);
            //If number of player < 4
            if (userListByRoomId.size() < 4) {
                String returnMessage = "ROOM join " + roomId + " success " + thread.getCurrentUser().getUsername();
                for (User user : userListByRoomId) {
                    returnMessage = returnMessage + " " + user.getUsername();
                    if (user.getColor() != -1)
                        colored[user.getColor()] = 1;
                }

                thread.send(returnMessage);
                for (EchoThreadWriter otherThread : ThreadedEchoServer.clientWriterThreads) {
                    if (otherThread.getCurrentUser() != null && otherThread.getCurrentUser().getRoomId() == roomId) {
                        otherThread.send(returnMessage);
                    }
                }
                // Get color for user
                for (int i = 0; i <= 3; i++)
                if (colored[i] == 0){
                    color = i;
                    break;
                }

                // Update this current user roomId
                thread.getCurrentUser().setRoomId(roomId);
                thread.getCurrentUser().setColor(color);

                // Update this current user roomId in DB
                UserRepo.getInstance().setRoomId(thread.getCurrentUser().getUsername(), roomId);
                UserRepo.getInstance().setColor(thread.getCurrentUser().getUsername(), color);
            } else {
                thread.send ("ROOM join " + roomId + " fail");
            }
        } else {
            thread.send ("ROOM join " + roomId + " fail");
        }
    }

    private boolean validateRoom(int roomId, String password) {
        Room room = RoomRepo.getInstance().getRoomById(roomId);

        if (room != null && room.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    private void create(EchoThreadWriter thread, String[] lines) throws IOException {
        String password = (lines.length == 2) ? "" : lines[2];
        int roomId = RoomRepo.getInstance().getNewId();

        //Add new room to database
        String[] stringRoom = new String[1];
        stringRoom[0] = Integer.toString(roomId) + "," + password + "," + "0";
        RoomRepo.getInstance().AppendToCSV(Utils.ROOM_CSV_URL, stringRoom);

        //Add new room to room list
        RoomRepo.getInstance().addRoom(roomId, password, 0);
        thread.send("ROOM create " + roomId + " " + password);
    }

    private void exit(EchoThreadWriter thread, String[] lines) throws IOException {
        // Update this all user status to not ready
        int roomId = thread.getCurrentUser().getRoomId();
        UserRepo.getInstance().setAllStatus(roomId, 0);

        //Update this current user to not ready and roomId to -1
        thread.getCurrentUser().setRoomId(-1);
        thread.getCurrentUser().setStatus(0);

        //Send result to all user
        String returnMessage = "ROOM exit " + thread.getCurrentUser().getUsername();
        thread.send(returnMessage);
        for (EchoThreadWriter otherThread : ThreadedEchoServer.clientWriterThreads)
            if (otherThread.getCurrentUser().getRoomId() == roomId) {
                otherThread.send(returnMessage);
            }

    }

    private void fetch(EchoThreadWriter thread, String[] lines) throws IOException {
        ArrayList<Room> roomList = RoomRepo.getInstance().getRoomsList();
        String returnMessage = "ROOM fetch";
        for (Room room : roomList) {
            returnMessage = returnMessage + " " + room.getId();
        }
        thread.send(returnMessage);
    }

    private void fetchOne(EchoThreadWriter thread, String[] lines) throws IOException {
        int roomId = Integer.parseInt(lines[2]);
        Room room = RoomRepo.getInstance().getRoomById(roomId);
        if (room != null) {
            ArrayList<User> userListByRoomId = UserRepo.getInstance().getUsersByRoomId(roomId);
            thread.send("ROOM fetch_one " + room.getId() + " " + userListByRoomId.size() + " " + room.getStatus());
        } else {
            thread.send("ROOM fetch_one " + roomId + " fail");
        }
    }
}
