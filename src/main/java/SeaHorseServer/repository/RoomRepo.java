package SeaHorseServer.repository;
import SeaHorseServer.model.Room;
import SeaHorseServer.utils.Utils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class RoomRepo extends BaseRepo{
    public static RoomRepo instance;
    private ArrayList<Room> roomsList;

    private RoomRepo() {}

    public static RoomRepo getInstance() {
        if (instance == null) {
            instance = new RoomRepo();
            instance.init();
        }
        return instance;
    }

    protected void init() {
        this.ParseCsvToRoom();
    }

    private void ParseCsvToRoom() {
        roomsList = new ArrayList<>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(Utils.ROOM_CSV_URL))
        ) {
            CsvToBean<Room> csvToBean = new CsvToBeanBuilder<Room>(reader)
                    .withType(Room.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            Iterator<Room> csvIterator = csvToBean.iterator();

            while (csvIterator.hasNext()) {
                Room csvRoom = csvIterator.next();
                roomsList.add(csvRoom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeRoomListToDB() throws IOException {
        writeToCSV(Utils.ROOM_CSV_URL, new String[]{"id,password,currentTurn"});
        // feed in your array (or convert your data to an array)
        for (Room room : roomsList){
            appendToCSV(Utils.ROOM_CSV_URL, room.toArray());
        }
    }

    public void removeRoom(int roomId) throws IOException {
        Room room = getRoomById(roomId);
        roomsList.remove(room);
        writeRoomListToDB();
    }

    public ArrayList<Room> getRoomsList() {
        return roomsList;
    }

    public synchronized void addRoom(int id, String password) throws IOException {
        roomsList.add(new Room (id, password, -1));
        //Add new room to database
        String[] stringRoom = new String[1];
        stringRoom[0] = Integer.toString(id) + "," + password + ",-1";
        appendToCSV(Utils.ROOM_CSV_URL, stringRoom);
    }

    public synchronized Room getRoomById(int id) {
        for (Room room : roomsList) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    public synchronized int getNewId() {
        for (int id = 1; id < Utils.MAX_ROOM_NUMBER; ++id) {
            if (this.getRoomById(id) == null) {
                return id;
            }
        }
        return -1;
    }

}