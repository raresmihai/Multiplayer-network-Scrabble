package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ScrabbleServer {
	private static final int PORT=5400;
	private static ServerSocket serverSocket;
	private static ServerSocket scoreServerSocket;
	private List<Socket> sockets;
	private List<Socket> scoreSockets;
	
	public ScrabbleServer(){
		try {
			serverSocket=new ServerSocket(PORT);
			scoreServerSocket=new ServerSocket(PORT+1);
			sockets=new ArrayList<>();
			scoreSockets=new ArrayList<>();
		} catch (IOException e) {
			System.out.println("Esuare la deschiderea ServerSocket-ului.");
			try {
				if (serverSocket!=null) serverSocket.close();
			} catch (IOException e1) {
				System.out.println("Esuare la inchiderea ServerSocket-ului.");
			}
		}
	}
	
	public List<Socket> getSockets() {
		return sockets;
	}
	
	public List<Socket> getScoreSockets(){
		return scoreSockets;
	}
	
	public List<Socket> copySockets(){
		List<Socket> copySockets=new ArrayList<>();
		for (Socket socketIterator:sockets){
			copySockets.add(socketIterator);
		}
		sockets.clear();
		return copySockets;
	}
	
	public List<Socket> copyScoreSockets(){
		List<Socket> copyScoreSockets=new ArrayList<>();
		for (Socket socketIterator:scoreSockets){
			copyScoreSockets.add(socketIterator);
		}
		scoreSockets.clear();
		return copyScoreSockets;
	}

	public void start(){
		Timer timer=new Timer(this);
		timer.start();
		while (true) {
			System.out.println ("Waiting for a client ...");
			try {
				Socket socket = serverSocket.accept();
				sockets.add(socket);
				Socket scoreSocket= scoreServerSocket.accept();
				scoreSockets.add(scoreSocket);
				System.out.println("S-a conectat clientul cu id-ul "+socket);
			} catch (IOException e) {
				System.out.println("Esuare la crearea socketului pentru client.");
			}
		}
	}
}
