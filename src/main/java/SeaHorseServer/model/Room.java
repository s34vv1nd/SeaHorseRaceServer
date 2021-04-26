package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

public class Room {
    @CsvBindByName(column = "id")
    private int id;
    @CsvBindByName (column = "password")
    private String password;
    @CsvBindByName (column = "status")
    private int status;

    public Room() { }
    public Room(int id, String password, int status) {
        this.id = id;
        this.password = password;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public int getStatus() {
        return status;
    }
}
