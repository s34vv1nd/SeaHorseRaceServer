package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

public class Room {
    @CsvBindByName(column = "id")
    private int id;
    @CsvBindByName (column = "password")
    private String password;
    @CsvBindByName (column = "currentTurn")
    private int currentTurn;
    @CsvBindByName (column = "currentDice")
    private int currentDice;

    public Room() { }

    public Room(int id, String password) {
        this.id = id;
        this.password = password;
        this.currentTurn = -1;
        this.currentDice = 0;
    }

    public String[] toArray() {
        String[] result = new String[4];
        result[0] = Integer.toString(id);
        result[1] = password;
        result[2] = Integer.toString(currentTurn);
        result[3] = Integer.toString(currentDice);
        return result;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getCurrentDice() {
        return currentDice;
    }

    public void setCurrentDice(int currentDice) {
        this.currentDice = currentDice;
    }
}