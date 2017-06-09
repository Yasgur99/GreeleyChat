/**
 *
 * @author michaelmaitland
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    private int port;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket socket;
    private Scanner sc;

    public Server(int port) {
        System.out.println("Server");
        System.out.println("------");
        this.port = port;
        this.sc = new Scanner(System.in);
    }

    public void runServer() {
        try {
            this.server = new ServerSocket(this.port);
            try {
                getConnection();
                setupStreams();
                chat();
            } catch (EOFException eofException) {
                System.out.println("Connection terminated...");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void getConnection() throws IOException {
        System.out.println("Waiting for client...");
        this.socket = this.server.accept();
        System.out.println("You are now connected with the client...");
    }

    private void setupStreams() throws IOException {
        this.output = new ObjectOutputStream(this.socket.getOutputStream());
        this.output.flush();
        this.input = new ObjectInputStream(this.socket.getInputStream());
        System.out.println("The streams are now setup");
    }
    
     private void chat() throws IOException {
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
                            System.out.print("\b\b\bClient: " + message + "\nMe: ");
                        } catch (ClassNotFoundException classNotFoundException) {
                            System.out.println("Client tried to send an object other than a string");
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

    private void sendMessage(String message) {
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



