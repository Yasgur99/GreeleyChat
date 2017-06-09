package client;

public Class ClientMain{
    public static void main(String args[]){
	Client myClient = new Client(4444, "localhost");
	myClient.runClient();
    }
}
