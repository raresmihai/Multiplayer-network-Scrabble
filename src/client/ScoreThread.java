package client;

import java.io.IOException;

class ScoreThread extends Thread {
	private ScrabbleClient scrabbleClient;
	private volatile boolean GameOn;
	
	ScoreThread(ScrabbleClient scrabbleClient) {
		this.scrabbleClient=scrabbleClient;
	}
	
	public void run(){
		String playerName;
		int score;
		int rowsNumber=scrabbleClient.getClientInterface().getScoreTable().getRowCount();
		GameOn=true;
		while (GameOn==true){
			try {
				playerName=scrabbleClient.getScoreInputStream().readUTF();
				score=scrabbleClient.getScoreInputStream().readInt();
				GameOn=scrabbleClient.getScoreInputStream().readBoolean();
				for (int i=0;i<rowsNumber;i++){
					if (((String) scrabbleClient.getClientInterface().getScoreTable().getModel()
							.getValueAt(i, 0)).equals(playerName)){
						scrabbleClient.getClientInterface().getScoreTable().getModel()
						.setValueAt(Integer.toString(score), i, 1);
						i=rowsNumber;
					}
				}
			} catch (IOException e) {
				System.out.println("Eroare la citirea scorurilor de la server.");
				GameOn=false;
			}
		}
	}
}
