package SeaHorseServer.model;

import SeaHorseServer.EchoThreadWriter;
import com.opencsv.bean.CsvBindByName;

import java.io.IOException;

public class User {
    @CsvBindByName (column = "username")
    private String username;
    @CsvBindByName (column = "password")
    private String password;
    @CsvBindByName (column = "room_id")
    private int roomId;
    @CsvBindByName (column = "color")
    private int color;
    @CsvBindByName (column = "status")
    private int status;

    private EchoThreadWriter writer;

    public User () { }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roomId = -1;
        this.color = -1;
        this.status = 0;
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

    public int getStatus() { return status; }

    public void setRoomId(int roomId) { this.roomId = roomId; }

    public void setColor(int color) { this. color = color; }

    public void setStatus(int status) { this.status = status; }

    public String[] toArray() {
        String[] result = new String[5];
        result[0] = this.username;
        result[1] = this.password;
        result[2] = Integer.toString(this.roomId);
        result[3] = Integer.toString(this.color);
        result[4] = Integer.toString(this.status);
        return result;
    }

    public boolean isWriterExist() {
        return writer != null;
    }

    public void setWriter(EchoThreadWriter writer) {
        this.writer = writer;
    }

    public boolean send(String line) throws IOException {
        if (writer != null) {
            writer.send(line);
            return true;
        }
        return false;
    }
}
