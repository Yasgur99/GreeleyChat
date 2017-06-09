/**
 *
 * @author michaelmaitland
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private int port;
    private String host;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket socket;
    private Scanner sc;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
        this.sc = new Scanner(System.in);
        System.out.println("Client");
        System.out.println("------");
    }

    public void runClient() {
        try {
            getConnection();
            setupStreams();
            chat();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void getConnection() throws IOException {
        System.out.println("Trying to connect...");
        this.socket = new Socket(InetAddress.getByName(this.host), this.port);
    }

    private void setupStreams() throws IOException {
        this.output = new ObjectOutputStream(this.socket.getOutputStream());
        this.output.flush();
        this.input = new ObjectInputStream(this.socket.getInputStream());
        System.out.println("The Streams are now setup");
    }
    
    private void chat() {
       System.out.println("You are now connected with the server...");
       startOutputThread();
       startInputThread();
    }
    
    private void startOutputThread(){
        Thread outputThread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    do {
                        System.out.print("Me: ");
                        message = sc.nextLine();
                        sendMessage(message);
                    } while(!message.equals("QUIT\n"));
                    closeConnection();
                }
               
            }
        );
    outputThread.start();
    }
    
    private void startInputThread(){
        Thread inputThread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    do{
                        try {
                            message = (String)input.readObject();
                            System.out.print("\b\b\bServer: " + message + "\nMe: ");
                        } catch (ClassNotFoundException classNotFoundException) {
                            System.out.println("Server tried to send an object other than a string");
                        } catch(EOFException eofException){
                            break;
                        } catch (IOException ioException){
                            ioException.printStackTrace();
                        } 
                    }while (!message.equals("QUIT"));
                    closeConnection();
                }
            }
        );
        inputThread.start();
    }
     
    private void sendMessage(String message){
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException ioException) {
            System.err.println("Error writting the message");
        }
    }
    
    private void closeConnection() {
        try {
            this.output.close();
            this.input.close();
            this.socket.close();
            System.out.println("Connection terminated...");
            System.exit(0);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}

