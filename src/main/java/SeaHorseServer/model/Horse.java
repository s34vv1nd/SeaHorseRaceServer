package SeaHorseServer.model;

import com.opencsv.bean.CsvBindByName;

import SeaHorseServer.utils.Utils;

public class Horse {
    @CsvBindByName(column = "room_id")
    private int roomId;
    @CsvBindByName(column = "color")
    private int color;
    @CsvBindByName(column = "steps")
    private int steps;
    @CsvBindByName(column = "rank")
    private int rank;

    public Horse(int roomId, int color) {
        this.roomId = roomId;
        this.color = color;
        this.steps = 0;
        this.rank = 0;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getColor() {
        return color;
    }

    public int getSteps() {
        return steps;
    }

    public int getPosition() {
        return (Utils.STARTING_POSITIONS[getColor()] + steps) % Utils.NUM_HORSE_POSITIONS;
    }

    public int getRank() {
        return rank;
    }
    
    public boolean isInRank() {
        return rank > 0;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String[] toArray() {
        String[] result = new String[4];
        result[0] = Integer.toString(this.roomId);
        result[1] = Integer.toString(this.color);
        result[2] = Integer.toString(this.steps);
        result[3] = Integer.toString(this.rank);
        return result;
    }
}
