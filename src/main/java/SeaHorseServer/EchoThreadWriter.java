package SeaHorseServer;

import java.io.*;
import java.net.*;

import SeaHorseServer.model.User;

public class EchoThreadWriter extends Thread{
    protected Socket socket;
    private DataOutputStream out;

    private User currentUser;

    public EchoThreadWriter(Socket clientSocket) {
        this.socket = clientSocket;
        this.out = null;
        this.currentUser = null;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }
    public User getCurrentUser() { return this.currentUser; }

    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void send(String line) throws IOException {
        this.out.writeBytes(line + "\r");
        System.out.println(currentUser.getUsername() + " " + line);
        this.out.flush();
    }


}