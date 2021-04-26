package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.HorseRepo;
import SeaHorseServer.repository.UserRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {
    EchoThreadWriter thread;
    String[] lines;
    public GameController(EchoThreadWriter thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;

        if (lines[1].equals("ready")) {
            this.ready(thread, lines);
        }
        else if (lines[1].equals("roll")) {
            this.roll(thread, lines);
        }
        else if (lines[1].equals("move")) {
            this.move(thread, lines);
        }
        else if (lines[1].equals("uprank")) {
            this.uprank(thread, lines);
        }
    }

    private void uprank(EchoThreadWriter thread, String[] lines) {

    }

    private void ready(EchoThreadWriter thread, String[] lines) throws IOException {
        int roomId = thread.getCurrentUser().getRoomId();
        boolean allReady = true;

        // Change status of current user
        thread.getCurrentUser().setStatus(1);

        // Change status in DB
        UserRepo.getInstance().setStatus(thread.getCurrentUser().getUsername(), 1);

        ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);
        for (User user : userArrayList) {
            try {
                if (user == null) System.out.println("user is null");
                user.send("GAME ready " + thread.getCurrentUser().getUsername());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (user.getStatus() == 0) {
                allReady = false;
            }
        }
        if (allReady) {
            for (User user : userArrayList) {
                try {
                    user.send("GAME start");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void roll(EchoThreadWriter thread, String[] lines) throws IOException {
        Random rand = new Random();
        int dice = rand.nextInt(6) + 1;

        int roomId = thread.getCurrentUser().getRoomId();
        ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);

        for (User user : userArrayList) {
            user.send("GAME roll " + dice);
        }
    }

    private void move(EchoThreadWriter thread, String[] lines) throws IOException {
        int startPos = Integer.parseInt(lines[2]);

    }

    private boolean canMove(int startPos) {
        //HorseRepo.getInstance().getHorseList();
        return true;
    }

    private boolean canLaunch() {
        //TODO: read the state of the board to decide can launch or not
        return true;
    }
}
