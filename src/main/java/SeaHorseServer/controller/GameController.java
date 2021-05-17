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
import SeaHorseServer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class GameController {

    EchoThreadWriter thread;
    String[] words;

    public GameController(EchoThreadWriter thread, String[] words) throws IOException {
        this.thread = thread;
        this.words = words;
        
        switch (words[1]) {
            case "roll":
                this.roll();
                break;
            case "launch":
                this.launch();
                break;
            case "move":
                this.move();
                break;
            case "uprank":
                this.uprank();
                break;
            case "exit":
                this.exit();
                break;
            case "end": //for hack
                this.end();
                break;
            default:
                System.err.println("Cannot dispatch " + words[1]);
        }
    }

    private boolean checkBasicConditions() {
        if (thread.getCurrentUser() == null) {
            System.err.println("Not login");
            return false;
        }
        if (thread.getCurrentUser().getRoomId() == -1) {
            System.err.println("Not in room");
            return false;
        }
        Room room = RoomRepo.getInstance().getRoomById(thread.getCurrentUser().getRoomId());
        if (room == null || room.getCurrentTurn() == -1) {
            System.err.println("Not in game");
            return false;
        }
        return true;
    }

    private void roll() throws IOException {
        if (!checkBasicConditions()) {
            thread.send("GAME roll fail");
        }
        else {
            User user = thread.getCurrentUser();
            int hackValue = -1;
            if (words.length > 2) {
                hackValue = Integer.parseInt(words[2]);
            }
            System.out.println(user.getUsername() + " " + hackValue);
            int dice = GameService.roll(user, hackValue);
            if (dice == -1) {
                thread.send("GAME roll fail");
            } else {
                RoomService.sendToRoom(user.getRoomId(), "GAME roll success " + user.getColor() + " " + dice);
            }
        }
    }

    private void launch() throws IOException {
        if (checkBasicConditions() && GameService.launch(thread.getCurrentUser())) {
            RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), "GAME launch success " + thread.getCurrentUser().getColor());
        } else {
            thread.getCurrentUser().send("GAME launch fail");
        }
    }

    private void move() throws IOException {
        if (checkBasicConditions()) {
            int startPos = Integer.parseInt(words[2]);
            int result = GameService.move(thread.getCurrentUser(), startPos);
            if (result != -1) {
                RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), "GAME move success " + thread.getCurrentUser().getColor() + " " + startPos + " " + result);
            } else {
                thread.getCurrentUser().send("GAME move fail");
            }
        } else {
            thread.getCurrentUser().send("GAME move fail");
        }
    }

    private void uprank() throws IOException {
        if (checkBasicConditions()) {
            int curRank = Integer.parseInt(words[2]);
            int newRank = GameService.uprank(thread.getCurrentUser(), curRank);
            if (newRank == -1) {
                thread.send("GAME uprank fail");
            } else {
                RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), 
                    "GAME uprank success " + thread.getCurrentUser().getColor() + " " + curRank + " " + newRank);
                if (GameService.canEnd(thread.getCurrentUser())) {
                    GameService.endGame(thread.getCurrentUser());
                    RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), "GAME end " + thread.getCurrentUser().getColor());
                }
            }
        } else {
            thread.send("GAME uprank fail");
        }
    }

    private void exit() throws IOException {
        if (checkBasicConditions() && GameService.exit(thread.getCurrentUser())) {
            RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), "GAME exit success " + thread.getCurrentUser().getUsername());
        } else {
            thread.send("GAME exit fail");
        }
    }

    private void end() throws IOException {
        if (checkBasicConditions()) {
            GameService.endGame(thread.getCurrentUser());
            RoomService.sendToRoom(thread.getCurrentUser().getRoomId(), "GAME end " + thread.getCurrentUser().getColor());
        }
    }

}
