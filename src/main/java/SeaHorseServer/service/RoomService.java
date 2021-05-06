package SeaHorseServer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.utils.Utils;

public class RoomService {
  public synchronized static void sendToRoom(int roomId, String message) throws IOException {
    ArrayList<User> users = UserRepo.getInstance().getUsersByRoomId(roomId);
    for (User user : users) {
      user.send(message);
    }
  }

  public synchronized static ArrayList<Integer> emptySlotsInRoom(int roomId) {
    Integer arr[] = {0, 1, 2, 3};
    ArrayList<Integer> result = new ArrayList<Integer>(Arrays.asList(arr));
    ArrayList<User> userList = UserRepo.getInstance().getUsersByRoomId(roomId);
    for (User user : userList) {
      result.remove(Integer.valueOf(user.getColor()));
    }
    return result;
  }

  public synchronized static boolean isEveryoneReady(int roomId) {
    ArrayList<User> users = UserRepo.getInstance().getUsersByRoomId(roomId);
    for (User user : users) {
      if (user.getStatus() == 0) {
        return false;
      }
    }
    return true;
  }

  public synchronized static int createRoom(String password) throws IOException {
    int currentNumberOfRooms = RoomRepo.getInstance().getRoomsList().size();
    if (currentNumberOfRooms < Utils.MAX_ROOM_NUMBER) {
      int roomId = RoomRepo.getInstance().getNewId();
      RoomRepo.getInstance().addRoom(roomId, password);
      return roomId;
    }
    return -1;
  }
}
