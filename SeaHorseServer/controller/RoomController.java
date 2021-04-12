package SeaHorseServer.controller;

import SeaHorseServer.EchoThread;

import java.io.IOException;

public class RoomController {
    EchoThread thread;
    String[] lines;
    public RoomController(EchoThread thread, String[] lines) throws IOException {
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
    }

    private void join(EchoThread thread, String[] lines) throws IOException {
        String roomId =lines[2];
        String password = lines[3];

        if (validateRoom(roomId, password)) {
            thread.send("{status: 200, response: 'Join Successfully!'}");
        } else {
            thread.send ("{status: 401, response: 'Invalid room or password!'}");
        }
    }

    private boolean validateRoom(String roomId, String password) {
        //TODO: check exist in database
        return true;
    }

    private void create(EchoThread thread, String[] lines) throws IOException {
        String password = lines[2];
        //TODO Get room id from database
        String roomId = "1";
        thread.send("{status: 200, response: '" + roomId +  "'}");
    }

    private void exit(EchoThread thread, String[] lines) throws IOException {
        thread.send("{status: 200, response: 'Exit Successfully!'}");
    }
}
