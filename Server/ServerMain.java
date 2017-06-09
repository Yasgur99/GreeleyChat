package Server;

public Class ServerMain{
    public static void main(String args[]){
	Server myServer = new Server(4444);
	myServer.runServer();
    }
}
