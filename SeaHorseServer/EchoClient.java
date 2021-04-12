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

        while (true) {
            requestMessage = messageBuffer.readLine();
            if (requestMessage.equals("QUIT")) {
                break;
            }
            outputStream.println(requestMessage);
            outputStream.flush();
            String response = responseBuffer.readLine();
            System.out.println("Server Response : " + response);
        }
        responseBuffer.close();
        outputStream.close();
        messageBuffer.close();
        socket.close();
        System.out.println("Connection Closed");
    }
}