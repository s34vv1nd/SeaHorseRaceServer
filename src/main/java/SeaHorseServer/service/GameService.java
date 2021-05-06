package SeaHorseServer.service;

import java.util.Random;

import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;

public class GameService {
  private static Random rand = new Random();

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
