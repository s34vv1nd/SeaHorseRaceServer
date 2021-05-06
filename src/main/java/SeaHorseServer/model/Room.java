package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

public class Room {
    @CsvBindByName(column = "id")
    private int id;
    @CsvBindByName (column = "password")
    private String password;
    @CsvBindByName (column = "currentTurn")
    private int currentTurn;

    public Room() { }

    public Room(int id, String password, int currentTurn) {
        this.id = id;
        this.password = password;
        this.currentTurn = currentTurn;
    }

    public String[] toArray() {
        String[] result = new String[3];
        result[0] = Integer.toString(id);
        result[1] = password;
        result[2] = Integer.toString(currentTurn);
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
}
