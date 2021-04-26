package SeaHorseServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ThreadedEchoServer {

    static final int PORT = 5000;

    public static ArrayList<EchoThreadWriter> clientThreads;


    private ThreadedEchoServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientThreads = new ArrayList<EchoThreadWriter>();

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }

            // new thread for a client
            EchoThreadWriter threadWriter = new EchoThreadWriter(socket);
            threadWriter.start();
            clientThreads.add(threadWriter);

            EchoThreadReader threadReader = new EchoThreadReader(socket, threadWriter);
            threadReader.start();
        }
    }

    public static void main(String args[]) {
        new ThreadedEchoServer();
    }
}