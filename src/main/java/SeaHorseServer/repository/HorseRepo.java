package SeaHorseServer.repository;

import SeaHorseServer.model.Horse;
import SeaHorseServer.utils.Utils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class HorseRepo extends BaseRepo {
    public static HorseRepo instance;
    private ArrayList<Horse> horsesList;

    private HorseRepo() {}

    public static HorseRepo getInstance() {
        if (instance == null) {
            instance = new HorseRepo();
            instance.init();
        }
        return instance;
    }

    protected void init() {
        ParseCsvToHorse();
    }

    private void ParseCsvToHorse() {
        horsesList = new ArrayList<Horse>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(Utils.HORSE_CSV_URL))
        ) {
            CsvToBean<Horse> csvToBean = new CsvToBeanBuilder<Horse>(reader)
                    .withType(Horse.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            Iterator<Horse> csvHorseIterator = csvToBean.iterator();

            while (csvHorseIterator.hasNext()) {
                Horse csvHorse = csvHorseIterator.next();
                horsesList.add(csvHorse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeHorseListToDB() throws IOException {
        writeToCSV(Utils.HORSE_CSV_URL, new String[]{"room_id,color,steps,rank"});
        // feed in your array (or convert your data to an array)
        for (Horse horse : horsesList){
            appendToCSV(Utils.HORSE_CSV_URL, horse.toArray());
        }
    }

    public ArrayList<Horse> getAllHorseList() {
        return horsesList;
    }

    public ArrayList<Horse> getHorsesListByRoomId(int roomId) {
        if (horsesList.isEmpty()) {
            return horsesList;
        }
        ArrayList<Horse> horseArrayList = new ArrayList<>();
        for (Horse horse : horsesList)
        if (horse.getRoomId() == roomId){
            horseArrayList.add(horse);
        }
        return horseArrayList;
    }

    public Horse getHorseByPosition(int roomId, int position) {
        for (Horse horse : horsesList)
        if (horse.getRoomId() == roomId && horse.getPosition() == position && !horse.isInRank()){
            return horse;
        }
        return null;
    }

    public ArrayList<Horse> getHorsesByColor(int roomId, int color) {
        ArrayList<Horse> horseArrayList = new ArrayList<Horse>();
        for (Horse horse : horsesList)
        if (horse.getRoomId() == roomId && horse.getColor() == color){
            horseArrayList.add(horse);
        }
        return horseArrayList;
    }

    public Horse getHorseByRank(int roomId, int color, int rank) {
        for (Horse horse : horsesList)
        if (horse.getRoomId() == roomId && horse.getColor() == color && horse.getSteps() == Utils.NUM_HORSE_POSITIONS - 1 && horse.getRank() == rank) {
            return horse;
        }
        return null;
    }

    public synchronized void addNewHorse (Horse horse) throws IOException {
        horsesList.add(horse);
        appendToCSV(Utils.HORSE_CSV_URL, horse.toArray());
    }

    public synchronized void removeHorse(int roomId, int position) throws IOException {
        Horse horse = getHorseByPosition(roomId, position);
        horsesList.remove(horse);
        writeHorseListToDB();
    }

    public synchronized void removeHorsesByRoomId(int roomId) throws IOException {
        ArrayList<Horse> horses = getHorsesListByRoomId(roomId);
        for (Horse horse : horses) {
            horsesList.remove(horse);
        }
        writeHorseListToDB();
    }

    public synchronized void removeHorsesByColor(int roomId, int color) throws IOException {
        ArrayList<Horse> horses = getHorsesListByRoomId(roomId);
        for (Horse horse : horses)
        if (horse.getColor() == color) {
            horsesList.remove(horse);
        }
        writeHorseListToDB();
    }

    public synchronized void setSteps(Horse horse, int steps) throws IOException {
        horse.setSteps(steps);
        writeHorseListToDB();
    }

    public synchronized void setRank(Horse horse, int rank) throws IOException {
        horse.setRank(rank);
        writeHorseListToDB();
    }
}
