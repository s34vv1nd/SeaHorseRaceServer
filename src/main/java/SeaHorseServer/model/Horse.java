package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

public class Horse {
    @CsvBindByName(column = "room_id")
    private int roomId;
    @CsvBindByName(column = "color")
    private int color;
    @CsvBindByName(column = "position")
    private int position;
    @CsvBindByName(column = "rank")
    private int rank;

    public int getRoomId() {
        return roomId;
    }

    public int getColor() {
        return color;
    }

    public int getPosition() {
        return position;
    }

    public int getRank() {
        return rank;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String[] toArray() {
        String[] result = new String[5];
        result[0] = Integer.toString(this.roomId);
        result[1] = Integer.toString(this.color);
        result[2] = Integer.toString(this.position);
        result[3] = Integer.toString(this.rank);
        return result;
    }
}
