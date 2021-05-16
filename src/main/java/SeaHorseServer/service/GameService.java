package SeaHorseServer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import SeaHorseServer.model.Horse;
import SeaHorseServer.model.Room;
import SeaHorseServer.model.User;
import SeaHorseServer.model.Room.GameAction;
import SeaHorseServer.repository.HorseRepo;
import SeaHorseServer.repository.RoomRepo;
import SeaHorseServer.repository.UserRepo;
import SeaHorseServer.utils.Utils;

public class GameService {
  private static Random rand = new Random();

  public synchronized static void startGame(int roomId) throws IOException {
    RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
  }

  private synchronized static int getNextTurn(int roomId) {
    ArrayList<User> users = UserRepo.getInstance().getUsersByRoomId(roomId);
    HashMap<Integer, User> userMap = new HashMap<Integer, User>();
    for (User user : users) {
      userMap.put(user.getColor(), user);
    }
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    int turn = room.getCurrentTurn();
    int dice = room.getCurrentDice();
    int result = turn;
    if (turn >= 0 && turn % 4 == 1) {
      if (dice == 6) result = turn + 1; else result = turn + 3;
    } 
    else {
      result = turn + 1;
    }
    for (int i = 0; i < 4; ++i) 
    if (!userMap.containsKey(result / 4)) {
      result = (result / 4 + 1) * 4;
    }
    return result;
  }

  public synchronized static int roll(User user) throws IOException {
    int roomId = user.getRoomId();
    if (roomId == -1) return -1;
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    if (user.getColor() == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.ROLL) {
      int dice = rand.nextInt(6) + 1;
      RoomRepo.getInstance().updateRoomDice(roomId, dice);
      RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
      return dice;
    }
    return -1;
  }

  public synchronized static boolean launch(User user) throws IOException {
    Horse horse = HorseRepo.getInstance().getHorseByPosition(user.getRoomId(), Utils.STARTING_POSITIONS[user.getColor()]);
    if (horse == null && HorseRepo.getInstance().getHorsesByColor(user.getRoomId(), user.getColor()).size() < 4) {
      HorseRepo.getInstance().addNewHorse(new Horse(user.getRoomId(), user.getColor()));
      return true;
    }
    return false;
  }

  public synchronized static int move(User user, int startPos) throws IOException {
    int roomId = user.getRoomId();
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    if (user.getColor() == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.MOVE) {
        Horse horse = HorseRepo.getInstance().getHorseByPosition(roomId, startPos);
        int steps = canMove(roomId, horse, room.getCurrentDice());
        int endPos = steps % Utils.NUM_HORSE_POSITIONS;
        if (endPos == -1) return -1;
        HorseRepo.getInstance().removeHorse(roomId, endPos);
        HorseRepo.getInstance().setSteps(horse, steps);
        return horse.getPosition();
    } else {
        return -1;
    }
  }

  private static int canMove(int roomId, Horse horse, int dice) {
    if (horse == null) return -1;
    if (horse.getSteps() + dice >= Utils.NUM_HORSE_POSITIONS) return -1;
    int plus = 0;
    for (int i = horse.getPosition() + 1; i < horse.getPosition() + dice + plus; ++i) {
      if (i % 14 == 0) {
        plus = 1;
      }
      else if (HorseRepo.getInstance().getHorseByPosition(roomId, i % Utils.NUM_HORSE_POSITIONS) != null) {
        return -1;
      }
    }
    return horse.getSteps() + dice + plus;
  }

  public synchronized static int uprank(User user, int curRank) throws IOException {
    int roomId = user.getRoomId();
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    int color = user.getColor();
    if (color == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.MOVE) {
      Horse horse = HorseRepo.getInstance().getHorseByRank(roomId, color, curRank);
      if (horse == null) return -1;
      if (curRank != 0 && room.getCurrentDice() != curRank + 1) return -1;
      for (int i = curRank + 1; i <= room.getCurrentDice(); ++i) {
        if (HorseRepo.getInstance().getHorseByRank(roomId, color, i) != null)
          return -1;
      }
      HorseRepo.getInstance().setRank(horse, room.getCurrentDice());
      return room.getCurrentDice();
    }
    return -1;
  }

  public synchronized static boolean canEnd(User user) {
    for (int i = 3; i <= 6; ++i) {
      if (HorseRepo.getInstance().getHorseByRank(user.getRoomId(), user.getColor(), i) == null)
          return false;
    }
    return true;
  }

  public synchronized static void endGame(User user) throws IOException {
    int roomId = user.getRoomId();
    HorseRepo.getInstance().removeHorsesByRoomId(roomId);
    RoomRepo.getInstance().updateRoomTurn(roomId, -1);
    RoomRepo.getInstance().updateRoomDice(roomId, 0);
    UserRepo.getInstance().setAllStatus(roomId, 0);
  }
}
