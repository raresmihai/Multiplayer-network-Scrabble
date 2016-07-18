package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientThread extends Thread {
	private Socket clientSocket;
	private Socket scoreSocket;
	private RoomThread room;
	private String playerName;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private DataOutputStream scoreOutputStream;
	private ObjectOutputStream objectOutputStream;
	private volatile int playerScore=0;
	 
	public Socket getScoreSocket() {
		return scoreSocket;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getPlayerScore() {
		return playerScore;
	}
	
	public DataOutputStream getScoreOuputStream(){
		return scoreOutputStream;
	}

	public ClientThread (Socket socket, Socket scoreSocket, RoomThread room, String playerName) {
		 this.clientSocket=socket;
		 this.scoreSocket=scoreSocket;
		 this.room=room;
		 this.playerName=playerName;
		 try {
			dataOutputStream=new DataOutputStream(clientSocket.getOutputStream());
			objectOutputStream=new ObjectOutputStream(clientSocket.getOutputStream());
			dataInputStream=new DataInputStream(clientSocket.getInputStream());
			scoreOutputStream=new DataOutputStream(this.scoreSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Socket fara stream");
		}
	 }
	 
	 public void run () {
		 try {
			 dataOutputStream.writeBoolean(room.isGameOn()); //notify the players that a room was created and start the game
			 dataOutputStream.flush();
			 dataOutputStream.writeInt(room.getRoomId());
			 dataOutputStream.flush();
			 dataOutputStream.writeUTF(playerName);
			 dataOutputStream.flush();
			 objectOutputStream.writeObject(room.getPlayerNames());
			 objectOutputStream.flush();
			 int requestedLetters;
			 while(room.isGameOn()==true){
				 requestedLetters=dataInputStream.readInt();
				 objectOutputStream.writeObject(room.getBag().getTilesFromBag(requestedLetters));
				 objectOutputStream.flush();
				 playerScore+=dataInputStream.readInt();
				 dataOutputStream.writeBoolean(room.isGameOn());
				 dataOutputStream.flush();
			 }
		 } catch (IOException e) {
			 System.err.println("Eroare la trimiterea mesajului catre "+playerName+".");
		 } finally {
			 try {
				 dataOutputStream.writeUTF(room.getRankingLink());
				 clientSocket.close();
			 } catch (IOException e) { 
				 System.out.println ("Eroare la inchiderea socketului catre client."); 
			}
		 }
	 }
}
