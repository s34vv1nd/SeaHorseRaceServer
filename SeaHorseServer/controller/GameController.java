package SeaHorseServer.controller;

import SeaHorseServer.EchoThread;
import java.io.IOException;
import java.util.Random;

public class GameController {
    EchoThread thread;
    String[] lines;
    public GameController(EchoThread thread, String[] lines) throws IOException {
        this.thread = thread;
        this.lines = lines;

        if (lines[1].equals("start")) {
            this.start(thread, lines);
        }
        else if (lines[1].equals("roll")) {
            this.roll(thread, lines);
        }
        else if (lines[1].equals("move")) {
            this.move(thread, lines);
        }
    }

    private void start(EchoThread thread, String[] lines) throws IOException {
        thread.send ("{status: 200, response: {message: 'Start Successfully'}}");
    }

    private void roll(EchoThread thread, String[] lines) throws IOException {
        Random rand = new Random();
        int rollAgain = 0;
        int dice = rand.nextInt(6) + 1;
        if (dice == 6) {
            rollAgain = 1;
        }

        thread.send ("{status: 200, response: {dice:" + dice + ", rollAgain:" + rollAgain +  "}}");
    }

    private void move(EchoThread thread, String[] lines) throws IOException {
        int startPos = Integer.parseInt(lines[2]);
        int endPos = Integer.parseInt(lines[3]);

        if (startPos == -1 && endPos == -1) {
            if (canLaunch()) {
                thread.send("{status: 200, response: {message: 'Launch Successfully'}}");
            } else {
                thread.send("{status: 404, response: {message: 'Launch Fail'}");
            }
        } else {
            if (canMove()) {
                thread.send("{status: 200, response: {message: 'Move Successfully'}}");
            } else {
                thread.send("{status: 404, response: {message: 'Move Fail'}");
            }
        }
    }

    private boolean canMove() {
        //TODO: read the state of the board to decide can move or not
        return true;
    }

    private boolean canLaunch() {
        //TODO: read the state of the board to decide can launch or not
        return true;
    }
}
