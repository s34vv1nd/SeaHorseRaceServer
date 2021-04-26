package SeaHorseServer.repository;
import SeaHorseServer.model.User;
import SeaHorseServer.utils.Utils;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class UserRepo extends BaseRepo{
    public static UserRepo instance;
    private ArrayList<User> usersList;

    private UserRepo() {}

    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
            instance.init();
        }
        return instance;
    }

    protected void init() {
        this.ParseCsvToUser();
    }

    private void ParseCsvToUser() {
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
    }

    private void writeUserListToDB() throws IOException {
        writeToCSV(Utils.USER_CSV_URL, new String[]{"username,password,room_id,color,status"});
        // feed in your array (or convert your data to an array)
        for (User user : usersList){
            AppendToCSVExample(Utils.USER_CSV_URL, user.toArray());
        }
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public void addUser(String username, String password) {
        usersList.add(new User (username, password, -1, -1));
    }

    public User getUserByUserName(String username) {
        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getUsersByRoomId(int roomId) {
        ArrayList<User> usersListByRoomId = new ArrayList<>();
        for (User user : usersList) {
            if (user.getRoomId() == roomId) {
                usersListByRoomId.add(user);
            }
        }
        return usersListByRoomId;
    }

    public void setRoomId (String username, int roomId) throws IOException {
        usersList.forEach(user -> {
            if (user.getUsername().equals(username)) {
                user.setRoomId(roomId);
            }
        });
        this.writeUserListToDB();
    }

    public void setColor (String username, int color) throws IOException {
        usersList.forEach(user -> {
            if (user.getUsername().equals(username)) {
                user.setColor(color);
            }
        });
        this.writeUserListToDB();
    }

    public void setAllStatus (int roomId, int status) throws IOException {
        usersList.forEach(user -> {
            if (user.getRoomId() == roomId) {
                user.setStatus(status);
            }
        });
        this.writeUserListToDB();
    }
}