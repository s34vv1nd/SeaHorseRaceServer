package SeaHorseServer.controller;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.model.Horse;
import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.HorseRepo;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.service.GameService;
import SeaHorseServer.service.RoomService;
import SeaHorseServer.service.UserService;
import SeaHorseServer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameController {
    private static int dice;

    EchoThreadWriter thread;
    String[] lines;

    public GameController(EchoThreadWriter thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;
        if (lines[1].equals("roll")) {
            this.roll();
        } else if (lines[1].equals("move")) {
            this.move();
        } else if (lines[1].equals("uprank")) {
            this.uprank();
        } else if (lines[1].equals("launch")) {
            this.launch();
        }
    }

    private void roll() throws IOException {
        User user = thread.getCurrentUser();
        if (user == null) {
            thread.send("GAME roll fail");
        }
        else {
            dice = GameService.roll(user.getUsername());
            if (dice == -1) {
                thread.send("GAME roll fail");
            } else {
                RoomService.sendToRoom(user.getRoomId(), "Game roll success " + user.getColor() + " " + dice);
            }
        }
    }

    private void move() throws IOException {
        int startPos = Integer.parseInt(lines[2]);
        int endPos = startPos + dice;

        if (canMove(startPos, endPos)) {
            // Update this horse
            HorseRepo.getInstance().updateHorsePosition(startPos, endPos);

            // Send feed back to all user
            int roomId = thread.getCurrentUser().getRoomId();
            ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);

            for (User user : userArrayList) {
                user.send("GAME move success " + startPos + " " + (startPos + dice));
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

    private boolean gameEnd(int color) throws IOException {
        boolean[] rank = new boolean[7];

        ArrayList<Horse> horseArrayList = HorseRepo.getInstance().getAllHorseList();

        for (Horse horse : horseArrayList)
            if (horse.getColor() == color) {
                rank[horse.getColor()] = true;
            }

        for (int i = 3; i <= 6; i++)
            if (!rank[i]) {
                return false;
            }

        return true;

    }

    private void uprank() throws IOException {
        int color = Integer.parseInt(lines[2]);
        int curRank = Integer.parseInt(lines[3]);
        int roomId = thread.getCurrentUser().getRoomId();

        HorseRepo.getInstance().updateHorseRank(color, curRank, dice);

        if (gameEnd(color)) {
            // Send feedback to user
            ArrayList<User> userArrayList = UserRepo.getInstance().getUsersByRoomId(roomId);

            for (User user : userArrayList) {
                user.send("GAME launch " + color + " success");
            }
        }
    }

    private void launch() throws IOException {
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
                user.send("GAME launch " + color + " success");
            }
        } else {
            thread.getCurrentUser().send("GAME launch fail");
        }
    }

    private boolean canLaunch() {
        int roomId = thread.getCurrentUser().getRoomId();
        ArrayList<Horse> horseArrayList = HorseRepo.getInstance().getHorsesListByRoomId(roomId);
        if (horseArrayList.isEmpty()) {
            return true;
        }
        for (Horse horse : horseArrayList) {
            if (horse.getPosition() == Utils.STARTING_POSITIONS[thread.getCurrentUser().getColor()]) {
                return false;
            }
        }
        return true;
    }
}
