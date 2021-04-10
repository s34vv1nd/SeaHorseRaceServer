package SeaHorseServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ThreadedEchoServer {

    static final int PORT = 5000;

    static private ThreadedEchoServer instance;

    public ArrayList<EchoThread> clientThreads;

    static public ThreadedEchoServer getInstance() {
        if (instance == null) {
            instance = new ThreadedEchoServer();
        }
        return instance;
    }

    public ThreadedEchoServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            EchoThread thread = new EchoThread(socket);
            clientThreads.add(thread);
            thread.start();
        }
    }

    public static void main(String args[]) {
        new ThreadedEchoServer();
    }
}