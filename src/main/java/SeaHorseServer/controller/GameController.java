package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.Horse;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.HorseRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.utils.Utils;

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
        } else if (lines[1].equals("launch")) {
            this.launch(thread, lines);
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
        int dice = Integer.parseInt(lines[3]);

        if (canMove(startPos, startPos + dice)) {
            int roomId = thread.getCurrentUser().getRoomId();
            ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);

            for (User user : userArrayList) {
                user.send("GAME move " + startPos + " " + (startPos + dice));
            }
        } else {
            thread.getCurrentUser().send("GAME move fail");
        }
    }

    private boolean canMove(int startPos, int endPos) {
        int roomId = thread.getCurrentUser().getRoomId();
        ArrayList<Horse> horseArrayList = HorseRepo.getInstance().getHorsesListByRoomId(roomId);

        for (Horse horse : horseArrayList) {
            if (horse.getPosition() > startPos && horse.getPosition() < endPos) {
                return false;
            }
        }
        return true;
    }

    private void launch(EchoThreadWriter thread, String[] lines) throws IOException {
        if (canLaunch()) {
            // Create a horse to DB
            int roomId = thread.getCurrentUser().getRoomId();
            int color = thread.getCurrentUser().getColor();
            int position = Utils.STARTING_POSITIONS[color];
            int rank = 0;
            Horse horse = new Horse(roomId, color, position, rank);
            HorseRepo.getInstance().addNewHorse(horse);

            // Send feedback to user
            ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);

            for (User user : userArrayList) {
                user.send("GAME launch success");
            }
        } else {
            thread.getCurrentUser().send("GAME launch fail");
        }
    }

    private boolean canLaunch() {
        int roomId = thread.getCurrentUser().getRoomId();
        ArrayList<Horse> horseArrayList = HorseRepo.getInstance().getHorsesListByRoomId(roomId);

        for (Horse horse : horseArrayList) {
            if (horse.getPosition() == Utils.STARTING_POSITIONS[thread.getCurrentUser().getColor()]) {
                return false;
            }
        }
        return true;
    }
}
