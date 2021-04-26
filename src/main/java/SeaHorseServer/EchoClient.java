package SeaHorseServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class EchoClient {

    static final int PORT = 5000;
    static InetAddress address = null;
    static Socket socket = null;
    static String requestMessage = null;
    static BufferedReader messageBuffer = null;
    static BufferedReader responseBuffer = null;
    static PrintWriter outputStream = null;

    public static void main(String args[]) throws IOException {
        address = InetAddress.getLocalHost();
        socket = new Socket(address, PORT);
        messageBuffer = new BufferedReader(new InputStreamReader(System.in));
        responseBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new PrintWriter(socket.getOutputStream());
        System.out.println("Client Address : " + address);

        Thread readerThread = new Thread(() -> {
            while (true) {
                String response = null;
                try {
                    response = responseBuffer.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Server Response : " + response);
            }
        });
        readerThread.start();

        Thread writerThread = new Thread(() -> {
            while (true) {
                try {
                    requestMessage = messageBuffer.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (requestMessage.equals("QUIT")) {
                    try {
                        responseBuffer.close();
                        outputStream.close();
                        messageBuffer.close();
                        socket.close();
                    }
                    catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Connection Closed");
                    break;
                }
                outputStream.println(requestMessage);
                outputStream.flush();
            }
        });
        writerThread.start();
    }

}