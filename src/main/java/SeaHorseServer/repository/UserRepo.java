package SeaHorseServer.repository;
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

public class UserRepo extends BaseRepo{
    private ArrayList<User> usersList;

    public ArrayList<User> ParseCsvToUser() {
        usersList = new ArrayList<>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(Utils.USER_CSV_URL))
        ) {
            CsvToBean<User> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(User.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<User> csvUserIterator = csvToBean.iterator();

            while (csvUserIterator.hasNext()) {
                User csvUser = csvUserIterator.next();
                usersList.add(csvUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usersList;
    }
}