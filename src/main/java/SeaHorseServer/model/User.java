package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

public class User {
    @CsvBindByName (column = "username")
    private String username;
    @CsvBindByName (column = "password")
    private String password;
    @CsvBindByName (column = "room_id")
    private int roomId;
    @CsvBindByName (column = "color")
    private int color;

    public User () { }

    public User(String username, String password, int roomId, int color) {
        this.username = username;
        this.password = password;
        this.roomId = roomId;
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getColor() {
        return color;
    }
}
