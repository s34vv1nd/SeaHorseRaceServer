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
        this.ParseCsvToHorse();
    }

    private void ParseCsvToHorse() {
        horsesList = new ArrayList<Horse>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(Utils.HORSE_CSV_URL))
        ) {
            CsvToBean<Horse> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Horse.class)
                    .withIgnoreLeadingWhiteSpace(true)
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

    private void writeHorseListToDB() throws IOException {
        writeToCSV(Utils.USER_CSV_URL, new String[]{"room_id,color,position,rank"});
        // feed in your array (or convert your data to an array)
        for (Horse horse : horsesList){
            AppendToCSVExample(Utils.USER_CSV_URL, horse.toArray());
        }
    }
}
