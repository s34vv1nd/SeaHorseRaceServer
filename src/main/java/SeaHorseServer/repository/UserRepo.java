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

public class UserRepo extends BaseRepo {
    private static UserRepo instance;
    private ArrayList<User> usersList;

    private UserRepo() {
    }

    public synchronized static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
            instance.init();
        }
        return instance;
    }

    protected synchronized void init() {
        this.ParseCsvToUser();
    }

    private synchronized void ParseCsvToUser() {
        usersList = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(Utils.USER_CSV_URL))) {
            CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(reader)
                    .withType(User.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
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

    private synchronized void writeUserListToDB() throws IOException {
        writeToCSV(Utils.USER_CSV_URL, new String[] { "username,password,room_id,color,status" });
        // feed in your array (or convert your data to an array)
        for (User user : usersList) {
            appendToCSV(Utils.USER_CSV_URL, user.toArray());
        }
    }

    public synchronized ArrayList<User> getUsersList() {
        return usersList;
    }

    public synchronized void addUser(String username, String password) throws IOException {
        usersList.add(new User(username, password));
        // Create string array user and add to database
        String[] stringUser = new String[1];
        stringUser[0] = username + "," + password + "," + "-1,-1,0";
        appendToCSV(Utils.USER_CSV_URL, stringUser);
    }

    public synchronized User getUserByUserName(String username) {
        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public synchronized ArrayList<User> getUsersByRoomId(int roomId) {
        ArrayList<User> usersListByRoomId = new ArrayList<>();
        for (User user : usersList) {
            if (user.getRoomId() == roomId) {
                usersListByRoomId.add(user);
            }
        }
        return usersListByRoomId;
    }

    public synchronized User getUserByColor(int roomId, int color) {
        for (User user : usersList) {
            if (user.getRoomId() == roomId && user.getColor() == color) {
                return user;
            }
        }
        return null;
    }

    public synchronized void setRoomId(String username, int roomId) throws IOException {
        User user = getUserByUserName(username);
        user.setRoomId(roomId);
        this.writeUserListToDB();
    }
    
    public synchronized void setColor(String username, int color) throws IOException {
        User user = getUserByUserName(username);
        user.setColor(color);
        this.writeUserListToDB();
    }

    public synchronized void setAllStatus(int roomId, int status) throws IOException {
        usersList.forEach(user -> {
            if (user.getRoomId() == roomId) {
                user.setStatus(status);
            }
        });
        this.writeUserListToDB();
    }

    public synchronized void setStatus(String username, int status) throws IOException {
        usersList.forEach(user -> {
            if (user.getUsername().equals(username)) {
                user.setStatus(status);
            }
        });
        this.writeUserListToDB();
    }
}