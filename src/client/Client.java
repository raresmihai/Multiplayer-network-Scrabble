package client;

import java.io.IOException;

class Client {
	public static void main(String args[])
	{
		try {
			ScrabbleClient scrabbleClient=new ScrabbleClient();
			scrabbleClient.connect();
		} catch (IOException e) {
			System.out.println("Eroare la conectarea la server");
		} catch (ClassNotFoundException e) {
			System.out.println("Clasa Tile nu exista.");
		}
	}
}
