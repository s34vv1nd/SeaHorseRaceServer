package SeaHorseServer.service;

import java.io.IOException;
import java.util.ArrayList;

import SeaHorseServer.EchoThreadWriter;
import SeaHorseServer.ThreadedEchoServer;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;

public class UserService {
  public synchronized static boolean register(String username, String password) throws IOException {
    User user = UserRepo.getInstance().getUserByUserName(username);
    if (user == null) {
      UserRepo.getInstance().addUser(username, password);
      return true;
    }
    return false;
  }

  public synchronized static boolean login(EchoThreadWriter thread, String username, String password) {
    User user = UserRepo.getInstance().getUserByUserName(username);
    if (user != null && user.getPassword().equals(password) && !user.isWriterExist()) {
      thread.setUser(user);
      user.setWriter(thread);
      return true;
    }
    return false;
  }

  public synchronized static boolean enterRoom(String username, int roomId, String password) throws IOException {
    if (RoomRepo.getInstance().getRoomById(roomId) == null) return false;
    if (RoomRepo.getInstance().getRoomById(roomId).getCurrentTurn() != -1) return false;
    if (!RoomRepo.getInstance().getRoomById(roomId).getPassword().equals(password)) 
      return false;
    ArrayList<Integer> colors = RoomService.emptySlotsInRoom(roomId);
    if (colors.size() > 0) {
      UserRepo.getInstance().setRoomId(username, roomId);
      UserRepo.getInstance().setColor(username, colors.get(0));
      UserRepo.getInstance().setStatus(username, 0);
      return true;
    }
    return false;
  }

  public synchronized static boolean ready(String username, int status) throws IOException {
    int roomId = UserRepo.getInstance().getUserByUserName(username).getRoomId();
    if (roomId == -1) return false;
    UserRepo.getInstance().setStatus(username, status);
    return true;
  }

  public synchronized static boolean exitRoom(String username) throws IOException {
    int roomId = UserRepo.getInstance().getUserByUserName(username).getRoomId();
    if (roomId != -1) {
      UserRepo.getInstance().setAllStatus(roomId, 0);
      UserRepo.getInstance().setRoomId(username, -1);
      UserRepo.getInstance().setColor(username, -1);
      RoomService.removeRoom(roomId);
      return true;
    }
    return false;
  }

  public synchronized static boolean logout(EchoThreadWriter thread) throws IOException {
    if (thread.getCurrentUser() != null) {
      String username = thread.getCurrentUser().getUsername();
      exitRoom(username);
      User user = UserRepo.getInstance().getUserByUserName(username);
      thread.setUser(null);
      user.setWriter(null);
      return true;
    }
    return false;
  }
}
