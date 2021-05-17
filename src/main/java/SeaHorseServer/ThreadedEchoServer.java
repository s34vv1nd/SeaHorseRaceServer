package SeaHorseServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadedEchoServer {

    static final int PORT = 5000;

    public static ArrayList<EchoThreadWriter> clientWriterThreads;
    public static ArrayList<EchoThreadReader> clientReaderThreads;
    private ReentrantLock mutex = new ReentrantLock();


    private ThreadedEchoServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientReaderThreads = new ArrayList<EchoThreadReader>();
        clientWriterThreads = new ArrayList<EchoThreadWriter>();

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mutex.lock();   // only one pair of threads registered at a time

            // Remove stopped threads
            for (int i = 0; i < clientReaderThreads.size(); ++i) {
                if (!clientReaderThreads.get(i).isAlive()) {
                    clientReaderThreads.remove(i);
                    clientWriterThreads.remove(i);
                    System.out.println("Thread " + i + " is removed.");
                }
            }

            // start new writer and reader thread for a client
            System.out.println("A new user connected. Current number of user: " + (clientReaderThreads.size() + 1));
            EchoThreadWriter threadWriter = new EchoThreadWriter(socket);
            threadWriter.start();
            clientWriterThreads.add(threadWriter);

            EchoThreadReader threadReader = new EchoThreadReader(socket, threadWriter);
            threadReader.start();
            clientReaderThreads.add(threadReader);

            mutex.unlock();
        }
    }

    public static void main(String args[]) {
        new ThreadedEchoServer();
    }
}