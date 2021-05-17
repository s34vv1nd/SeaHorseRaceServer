package SeaHorseServer.service;

import java.io.IOException;
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

  private synchronized static int getNextTurn(int roomId) throws IOException {
    int result = -1;
    if (winner(roomId) != null) {
      RoomService.sendToRoom(roomId, "GAME turn -1 roll");
    } else {
      Room room = RoomRepo.getInstance().getRoomById(roomId);
      int turn = room.getCurrentTurn();
      int dice = room.getCurrentDice();
      if (turn == -1) {
        result = 0;
      }
      else if (turn % 4 == 1) {
        if (dice == 6) result = turn + 1; else result = turn + 3;
      }
      else {
        result = turn + 1;
      }
      result %= 16;

      for (int i = 0; i < 4; ++i) 
      if (UserRepo.getInstance().getUserByColor(roomId, result / 4) == null || (result % 2 == 1 && !canAct(roomId, result / 4))) {
        result = (result / 4 + 1) * 4;
        result %= 16;
      }
      RoomService.sendToRoom(roomId, "GAME turn " + (result / 4) + " " + (result % 2 == 0 ? "roll" : "move"));
    }
    return result;
  }

  private synchronized static boolean canAct(int roomId, int color) {
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    User user = UserRepo.getInstance().getUserByColor(roomId, color);
    // System.out.println(room.getCurrentDice() + " " + user.getColor() + " " + canLaunch(user));
    if (canLaunch(user, room.getCurrentDice())) return true;
    for (Horse horse : HorseRepo.getInstance().getHorsesByColor(roomId, color)) {
      if (canMove(roomId, horse, room.getCurrentDice()) != -1 || canUprank(horse, room.getCurrentDice())) {
        return true;
      }
    }
    return false;
  }

  public synchronized static int roll(User user, int hackValue) throws IOException {
    int roomId = user.getRoomId();
    if (roomId == -1) return -1;
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    if (user.getColor() == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.ROLL) {
      int dice = (hackValue == -1) ? rand.nextInt(6) + 1 : hackValue;
      // int dice = 6;
      RoomRepo.getInstance().updateRoomDice(roomId, dice);
      RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
      return dice;
    }
    return -1;
  }

  public synchronized static boolean launch(User user) throws IOException {
    if (canLaunch(user, RoomRepo.getInstance().getRoomById(user.getRoomId()).getCurrentDice())) {
      HorseRepo.getInstance().addNewHorse(new Horse(user.getRoomId(), user.getColor()));
      RoomRepo.getInstance().updateRoomTurn(user.getRoomId(), getNextTurn(user.getRoomId()));
      return true;
    }
    return false;
  }

  private static boolean canLaunch(User user, int dice) {
    if (dice != 6) return false;
    Horse horse = HorseRepo.getInstance().getHorseByPosition(user.getRoomId(), Utils.STARTING_POSITIONS[user.getColor()]);
    // System.out.println((horse == null) + " " + HorseRepo.getInstance().getHorsesByColor(user.getRoomId(), user.getColor()).size());
    if (horse == null && HorseRepo.getInstance().getHorsesByColor(user.getRoomId(), user.getColor()).size() < 4) {
      return true;
    }
    return false;
  }

  public synchronized static int move(User user, int startPos) throws IOException {
    int roomId = user.getRoomId();
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    if (user.getColor() == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.MOVE) {
        System.out.println(roomId + " " + startPos);
        Horse horse = HorseRepo.getInstance().getHorseByPosition(roomId, startPos);
        int steps = canMove(roomId, horse, room.getCurrentDice());
        System.out.println(steps);
        if (steps == -1) return -1;
        int endPos = (Utils.STARTING_POSITIONS[horse.getColor()] + steps) % Utils.NUM_HORSE_POSITIONS;
        HorseRepo.getInstance().removeHorse(roomId, endPos);
        HorseRepo.getInstance().setSteps(horse, steps);
        RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
        return horse.getPosition();
    } else {
        return -1;
    }
  }

  private static int canMove(int roomId, Horse horse, int dice) {
    // System.out.println((horse == null));
    if (horse == null) return -1;
    // System.out.println(horse.isInRank() + " " + (horse.getSteps() + dice) + " " + roomId);
    if (horse.isInRank()) return -1;
    if (horse.getSteps() + dice >= Utils.NUM_HORSE_POSITIONS) return -1;
    int plus = 0;
    for (int i = horse.getPosition() + 1; i <= horse.getPosition() + dice + plus; ++i) {
      if (i % 14 == 0) {
        plus = 1;
      }
      else if (i < horse.getPosition() + dice + plus && HorseRepo.getInstance().getHorseByPosition(roomId, i % Utils.NUM_HORSE_POSITIONS) != null) {
        return -1;
      }
    }
    int steps = horse.getSteps() + dice + plus;
    int endPos = (Utils.STARTING_POSITIONS[horse.getColor()] + steps) % Utils.NUM_HORSE_POSITIONS;
    Horse horse2 = HorseRepo.getInstance().getHorseByPosition(roomId, endPos);
    if (horse2 != null && horse.getColor() == horse2.getColor()) return -1;
    return steps;
  }

  public synchronized static int uprank(User user, int curRank) throws IOException {
    int roomId = user.getRoomId();
    Room room = RoomRepo.getInstance().getRoomById(roomId);
    int color = user.getColor();
    if (color == room.getCurrentPlayer() && room.getCurrentAction() == GameAction.MOVE) {
      Horse horse = HorseRepo.getInstance().getHorseByRank(roomId, color, curRank);
      if (!canUprank(horse, room.getCurrentDice())) return -1;
      HorseRepo.getInstance().setRank(horse, room.getCurrentDice());
      RoomRepo.getInstance().updateRoomTurn(roomId, getNextTurn(roomId));
      return room.getCurrentDice();
    }
    return -1;
  }

  public synchronized static boolean canUprank(Horse horse, int dice) {
    if (horse == null) return false;
    if (!horse.isInRank() && horse.getSteps() < Utils.NUM_HORSE_POSITIONS - 1) return false;
    Room room = RoomRepo.getInstance().getRoomById(horse.getRoomId());
    if (horse.getRank() != 0 && room.getCurrentDice() != horse.getRank() + 1) return false;
    for (int i = horse.getRank() + 1; i <= room.getCurrentDice(); ++i) {
      if (HorseRepo.getInstance().getHorseByRank(horse.getRoomId(), horse.getColor(), i) != null)
        return false;
    }
    return true;
  }

  public synchronized static User winner(int roomId) {
    for (User user : UserRepo.getInstance().getUsersByRoomId(roomId)) {
      if (canEnd(user)) {
        return user;
      }
    }
    return null;
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
    RoomService.sendToRoom(roomId, "GAME turn -1 roll");
  }

  public synchronized static boolean exit(User user) {
    try {
      UserService.exitRoom(user.getUsername());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
