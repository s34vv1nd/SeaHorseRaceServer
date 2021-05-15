package SeaHorseServer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;

public class GameService {
  private static Random rand = new Random();

  public synchronized static void startGame(int roomId) throws IOException {    
    RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
  }

  private synchronized static int getNextTurn(int roomId) {
    ArrayList<User> users = UserRepo.getInstance().getUsersByRoomId(roomId);
    int result = 4;
    for (User user : users) {
      if (user.getStatus() == 1) {
        result = Math.min(result, user.getColor());
      }
    }
    return result;
  }

  public synchronized static int roll(String username) {
    User user = UserRepo.getInstance().getUserByUserName(username);
    int roomId = user.getRoomId();
    if (roomId == -1) return -1;
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    if (user.getColor() == room.getCurrentTurn()) {
      int dice = rand.nextInt(6) + 1;

      return dice;
    }
    return -1;
  }
}
