package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import game.TilesBag;

public class RoomThread extends Thread{
	private List<ClientThread> roomPlayers;
	private List<Socket> roomSockets;
	private List<Socket> roomScoreSockets;
	private List<String> playerNames;
	private String[] possiblePlayerNames;
	private boolean[] marked;
	private int id;
	private volatile boolean GameOn=true;
	private TilesBag bag;
	private FinalRanking roomFinalRanking;
	private String rankingLink;
	
	public String getRankingLink(){
		return rankingLink;
	}
	
	
	public List<ClientThread> getRoomPlayers() {
		return roomPlayers;
	}

	public List<String> getPlayerNames() {
		return playerNames;
	}

	public boolean isGameOn() {
		return GameOn;
	}

	public void setGameOn(boolean gameOn) {
		GameOn = gameOn;
	}
	
	public int getRoomId(){
		return this.id;
	}
	
	public TilesBag getBag(){
		return this.bag;
	}
	
	RoomThread(List<Socket> sockets,List <Socket> roomScoreSockets,int roomId) {
		this.roomSockets=sockets;
		this.roomScoreSockets=roomScoreSockets;
		this.id=roomId;
		possiblePlayerNames=new String[]{"Pitonul anonim","Broasca anonima","Rata anonima","Lemurul anonim"};
		playerNames=new ArrayList<>();
		roomPlayers=new ArrayList<>();
		marked=new boolean[possiblePlayerNames.length];
		bag=new TilesBag(this);
		roomFinalRanking=new FinalRanking(this);
		rankingLink=roomFinalRanking.getRankingLink();
	}
	
	public void run()
	{
		System.out.println("Camera "+id+" este lansata.");
		assignNameForEachPlayer();
		int index=0;
		for (Socket socketIterator:roomSockets){
			roomPlayers.add(new ClientThread(socketIterator,roomScoreSockets.get(index),this,playerNames.get(index)));
			roomPlayers.get(roomPlayers.size()-1).start();
			index++;
		}
		sendScoresToPlayers();
	}
	
	private void assignNameForEachPlayer(){
		int randomNumber,i;
		Random random=new Random();
		for (i=0;i<roomSockets.size();i++){
			randomNumber=random.nextInt(marked.length)%4;
			while (marked[randomNumber]){
				randomNumber=random.nextInt(marked.length);
			}
			marked[randomNumber]=true;
			playerNames.add(possiblePlayerNames[randomNumber]);
		}
	}
	
	private void sendScoresToPlayers(){
		String playerName;
		int score,i,j;
		while (GameOn==true){
			for (i=0;i<roomPlayers.size();i++){
				playerName=roomPlayers.get(i).getPlayerName();
				score=roomPlayers.get(i).getPlayerScore();
				for (j=0;j<roomPlayers.size();j++){
					try {
						roomPlayers.get(j).getScoreOuputStream().writeUTF(playerName);
						roomPlayers.get(j).getScoreOuputStream().flush();
						roomPlayers.get(j).getScoreOuputStream().writeInt(score);
						roomPlayers.get(j).getScoreOuputStream().flush();
						roomPlayers.get(j).getScoreOuputStream().writeBoolean(GameOn);
						roomPlayers.get(j).getScoreOuputStream().flush();
					} catch (IOException e) {
						GameOn=false;
					}
				}
			}
		}
		roomPlayers.sort(new Comparator<ClientThread>() {
			@Override
			public int compare(ClientThread o1, ClientThread o2) {
				if (o1.getPlayerScore()>o2.getPlayerScore()){
					return -1;
				}
				else if(o2.getPlayerScore()>o1.getPlayerScore()){
					return 1;
				}
				return 0;
			}
		});
		roomFinalRanking.displayOnWeb();
		for (ClientThread clientThreadIterator:roomPlayers){
			try {
				clientThreadIterator.getScoreSocket().close();
			} catch (IOException e) {
				System.out.println("Eroare la inchiderea socket-ului de scor pt un client.");
			}
		}
	}
}

