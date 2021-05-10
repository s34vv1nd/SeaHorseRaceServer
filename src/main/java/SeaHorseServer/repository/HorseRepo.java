package SeaHorseServer.repository;

import SeaHorseServer.model.Horse;
import SeaHorseServer.model.User;
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
        writeToCSV(Utils.HORSE_CSV_URL, new String[]{"room_id,color,position,rank"});
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

    public synchronized void addNewHorse (Horse horse) throws IOException {
        horsesList.add(horse);
        appendToCSV(Utils.HORSE_CSV_URL, horse.toArray());
    }

    public void updateHorsePosition (int startPos, int endPos) throws IOException {
        // Kick the horse
        for (Horse horse : horsesList)
        if (horse.getPosition() == endPos){
            horsesList.remove(horse);
            break;
        }
        //Move the horse
        for (Horse horse : horsesList)
        if (horse.getPosition() == startPos){
            horse.setPosition(endPos);
            break;
        }

        writeHorseListToDB();
    }

    public void updateHorseRank (int color, int curRank, int newRank) throws IOException {
        if (curRank == 0) {
            boolean uprank = true;
            //Check if horse can up rank
            for (Horse horse : horsesList) {
                if (horse.getColor() == color && horse.getRank() != 0 && horse.getRank() <= newRank) {
                    uprank = false;
                    break;
                }
            }
            if (uprank) {
                for (Horse horse : horsesList)
                    if (horse.getColor() == color && Utils.STABLE_POSITIONS[color] == horse.getPosition()) {
                        horse.setRank(newRank);
                        break;
                    }
            }
        } else if (newRank - curRank == 1) {
            boolean uprank = true;
            //Check if horse can up rank
            for (Horse horse : horsesList) {
                if (horse.getColor() == color && horse.getRank() == newRank) {
                    uprank = false;
                    break;
                }
            }
            if (uprank) {
                for (Horse horse : horsesList)
                    if (horse.getColor() == color && horse.getRank() == curRank) {
                        horse.setRank(newRank);
                        break;
                    }
            }
        }
        writeHorseListToDB();
    }
}
