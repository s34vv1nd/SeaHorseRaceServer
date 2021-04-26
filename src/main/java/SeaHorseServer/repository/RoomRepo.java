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
            CsvToBean<Room> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Room.class)
                    .withIgnoreLeadingWhiteSpace(true)
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

    public ArrayList<Room> getRoomsList() {
        return roomsList;
    }

    public void addRoom(int id, String password, int status) {
        roomsList.add(new Room (id, password, 0));
    }

    public Room getRoomById(int id) {
        for (Room room : roomsList) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    public int getNewId() {
        int maxId = -1;
        for (Room room : roomsList) {
            if (room.getId() > maxId) {
                maxId = room.getId();
            }
        }
        return maxId + 1;
    }
}