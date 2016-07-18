package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import client.dictionary.Trie;
import client.gui.ClientInterface;
import client.gui.TileFigure;
import game.Tile;

public class ScrabbleClient {
	private static final int PORT=5400;
	private static final String serverAddress = "127.0.0.1";
	private Socket socket;
	private Socket scoreSocket;
	private int room;
	private String playerName;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private DataInputStream scoreInputStream;
	private ObjectInputStream objectInputStream;
	private List<Tile> currentLetters;
	private ClientInterface clientInterface;
	private Trie dictionary;
	private volatile boolean GameOn;
	private int requestedLetters=7;
	private NewLettersThread newLettersThread;
	private boolean[] visited;
	private String word;
	private StringBuilder wordBuilder;
	private File xmlFile;

	public DataInputStream getScoreInputStream() {
		return scoreInputStream;
	}

	public ClientInterface getClientInterface() {
		return clientInterface;
	}

	public boolean isGameOn() {
		return GameOn;
	}
	
	public void setGameOn(boolean b){
		GameOn=b;
	}
	
	@SuppressWarnings("unchecked")
	private void requestForLetters(){
		clientInterface.getCenterPanelTop().removeAll();
		if (GameOn==true){
			try {
				dataOutputStream.writeInt(requestedLetters);
				List<Tile> lettersToAdd=(List<Tile>)objectInputStream.readObject();
				if (lettersToAdd.size()==requestedLetters){
					currentLetters.addAll(lettersToAdd);
					for(Tile tileIterator:currentLetters){
						clientInterface.getCenterPanelTop().add(new TileFigure(tileIterator));
					}
					clientInterface.getMainPanel().validate();
					clientInterface.getMainPanel().repaint();
				}
				else {
					GameOn=false;
					newLettersThread.stopThread();		
				}
			} catch (IOException e) {
				System.out.println("Eroare la comunicarea clientului cu server-ul.");
				GameOn=false;
			} catch (ClassNotFoundException e) {
				System.out.println("Clasa Tile nu exista.");
			}
		}
	}
	
	private void assignActionsToInterface(){
		clientInterface.getMainFrame().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				try {
					socket.close();
					scoreSocket.close();
					GameOn=false;
					newLettersThread.stopThread();
				} catch (IOException e) {
					System.out.println("Conexiunea cu server-ul nu se poate inchide.");
				}
			}
		});
		clientInterface.getSubmitButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				word=clientInterface.getWordField().getText();
				int score;
				clientInterface.getDefinitionArea().setText("");
				if (word.length()>0){
					if(wordIsValid(word)==true){
						clientInterface.getWordField().setPrompt("Word here...");
						clientInterface.getWordField().setPromptForeground(Color.GRAY);
						clientInterface.getWordField().setText("");
						//getWordDefinition(word);
						score=getScoreFromString(word);
						try {
							dataOutputStream.writeInt(score);
							GameOn=dataInputStream.readBoolean();
						} catch (IOException e1) {
							System.out.println("Eroare la comunicarea clientului cu server-ul.");
						}
						finally {
							requestedLetters=word.length();
							for (int i = 0; i < word.length(); i++) {
	                            for (int j = 0; j < currentLetters.size(); j++) {
	                                if (word.charAt(i) == currentLetters.get(j).getLetter()) {
	                                    currentLetters.remove(j);
	                                    break;
	                                }
	                            }
	                        }
							clientInterface.getWordsTable().addRow(new String[]{word, Integer.toString(score)});
							requestForLetters();
						}
					}
					else{
						clientInterface.getWordField().setText("");
						clientInterface.getWordField().setPrompt("Invalid word!!!");
						clientInterface.getWordField().setPromptForeground(Color.RED);
					}
				}
				else {
					clientInterface.getWordField().setPrompt("Word here...");
					clientInterface.getWordField().setPromptForeground(Color.GRAY);
					clientInterface.getWordField().setText("");
					visited=new boolean[currentLetters.size()];
					wordBuilder=new StringBuilder();
					seachForWord(0);
					if (word.length()>0){
						score=getScoreFromString(word);
						//getWordDefinition(word);
						try {
							dataOutputStream.writeInt(score);
							GameOn=dataInputStream.readBoolean();
						} catch (IOException e1) {
							System.out.println("Eroare la comunicarea clientului cu server-ul.");
						}
						finally {
							requestedLetters=word.length();
							for (int i = 0; i < word.length(); i++) {
	                            for (int j = 0; j < currentLetters.size(); j++) {
	                                if (word.charAt(i) == currentLetters.get(j).getLetter()) {
	                                    currentLetters.remove(j);
	                                    break;
	                                }
	                            }
	                        }
							clientInterface.getWordsTable().addRow(new String[]{word, Integer.toString(score)});
							requestForLetters();
						}
					}
					else {
						score=0;
						try {
							dataOutputStream.writeInt(score);
							GameOn=dataInputStream.readBoolean();
						} catch (IOException e1) {
							System.out.println("Eroare la comunicarea clientului cu server-ul.");
						}
						finally {
							currentLetters.clear();
							requestedLetters=7;
							requestForLetters();
						}
					}
				}
			}
		});
		clientInterface.getNewLettersButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int score=0;
				clientInterface.getDefinitionArea().setText("");
				try {
					dataOutputStream.writeInt(score);
					GameOn=dataInputStream.readBoolean();
				} catch (IOException e1) {
					System.out.println("Eroare la comunicarea clientului cu server-ul.");
				}
				finally {
					clientInterface.getWordField().setPrompt("Word here...");
					clientInterface.getWordField().setPromptForeground(Color.GRAY);
					clientInterface.getWordField().setText("");
					currentLetters.clear();
					requestedLetters=7;
					requestForLetters();
					newLettersThread.startCountDown();
				}
			}
		});
	}

	public ScrabbleClient() throws UnknownHostException, IOException{
		socket = new Socket(serverAddress, PORT);
		scoreSocket=new Socket(serverAddress, PORT+1);
		dictionary=new Trie();
		clientInterface=new ClientInterface();
		clientInterface.getMainFrame().setVisible(true);
		assignActionsToInterface();
		dataInputStream=new DataInputStream(socket.getInputStream());
		dataOutputStream=new DataOutputStream(socket.getOutputStream());
		objectInputStream=new ObjectInputStream(socket.getInputStream());
		scoreInputStream=new DataInputStream(scoreSocket.getInputStream());
		currentLetters=new ArrayList<>();
	}
	
	public void connect() throws IOException, ClassNotFoundException{
		GameOn=dataInputStream.readBoolean();
		if (GameOn==true){
			readGameConfiguration();
			new ScoreThread(this).start();
			newLettersThread=new NewLettersThread(clientInterface.getNewLettersButton());
			newLettersThread.start();
			clientInterface.getRoomLabel().setText(clientInterface.getRoomLabel().getText()+room);
			clientInterface.getPlayerNameLabel().setText(clientInterface.getPlayerNameLabel().getText()+playerName);
			clientInterface.getCardLayout().show(clientInterface.getMainPanel(),"Game");
			play();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readGameConfiguration() throws IOException, ClassNotFoundException{
		room=dataInputStream.readInt();
		playerName=dataInputStream.readUTF();
		xmlFile=new File("word Definition "+playerName+" .txt");
		if (xmlFile.exists()==false){
			xmlFile.createNewFile();
		}
        clientInterface.getScoreTable().addRow(new String[]{playerName,Integer.toString(0)});
        clientInterface.getScoreTable().getCellRenderer(0, 0).getTableCellRendererComponent(clientInterface.getScoreTable(),
        		playerName, false, true, 0, 0).setForeground(Color.BLUE);
        List<String> playerNames=(List<String>) objectInputStream.readObject();
		for (String name:playerNames){
			if (!name.equals(playerName)){
				clientInterface.getScoreTable().addRow(new String[]{name,Integer.toString(0)});
			}
		}
		clientInterface.getScoreTable().adjustColumns();
	}
	
	private void play(){
		requestForLetters();
		while (GameOn==true){
		}
		//the game is now finished
		System.out.println("Game finished!");
		newLettersThread.stopThread();
		try {
			System.out.println(dataInputStream.readUTF());
		} catch (IOException e) {
			System.out.println("Eroare la primirea rankingLink-ului de la server.");
		}
		clientInterface.getSubmitButton().setEnabled(false);
		clientInterface.getNewLettersButton().setEnabled(false);
	}
	
	private boolean wordIsValid(String word) {
		List<Character> availableLetters = new ArrayList<>();
	    for (int i = 0; i < currentLetters.size(); ++i) {
	         availableLetters.add(currentLetters.get(i).getLetter());
	    }
	    for (int i = 0; i < word.length(); ++i) {
	    	if (!availableLetters.contains(word.charAt(i))) {
	                return false;
	        }
	    	else{
	    		for (int j = 0; j < availableLetters.size(); ++j) {
	    			if (availableLetters.get(j) == word.charAt(i)) {
	    				availableLetters.remove(j);
	    				break;
	                }
	            }
	        }
	    }
	    if (dictionary.search(word)) {
	    	return true;
	    }
	    return false;
	}
	
	private int getScoreFromString(String word) {
        int wordScore = 0;
        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < currentLetters.size(); j++) {
                if (currentLetters.get(j).getLetter() == word.charAt(i)) {
                    wordScore += currentLetters.get(j).getValue();
                    break;
                }
            }
        }
        return wordScore;
    }
	
	private void seachForWord(int p) {
        if (p < currentLetters.size()) {
            for (int i = 0; i < currentLetters.size(); ++i) {
                if (!visited[i]) {
                    visited[i] = true;
                    wordBuilder.append(currentLetters.get(i).getLetter());
                    String currentWord = wordBuilder.toString();
                    if (dictionary.startsWith(currentWord)) { //valid prefix
                        if (dictionary.search(currentWord)) { //solution
                            word = currentWord;
                        }
                        seachForWord(p + 1);
                    }
                    wordBuilder=wordBuilder.deleteCharAt(wordBuilder.length()-1);
                    visited[i] = false;
                }
            }
        }
    }
	
	@SuppressWarnings("resource")
	private void getXMLDocumentFromOnlineDictionary(String word){
		String uri ="http://www.dictionaryapi.com/api/v1/references/collegiate/xml/"+word+"?key=1c98855e-2bb7-4763-8572-3bedc9d2c1d7";
		URL url;
		String output;
		try {
			url = new URL(uri);
			FileWriter fileWriter=new FileWriter(xmlFile);
			HttpURLConnection connection =(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			while ((output = br.readLine()) != null) {
				fileWriter.write(output);
				fileWriter.write("\n");
			}
			fileWriter.flush();
			connection.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void getWordDefinition(String word){
		getXMLDocumentFromOnlineDictionary(word);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			NodeList nList = doc.getElementsByTagName("entry");
            Element entryElement=(Element) nList.item(0);
            clientInterface.getDefinitionArea().append(entryElement.getElementsByTagName("ew").item(0).getTextContent()+"\n");
            Element defElement=(Element)entryElement.getElementsByTagName("def").item(0);
            if (defElement!=null){
            	Element dateElement=(Element) defElement.getElementsByTagName("date").item(0);
            	if (dateElement!=null){
                	clientInterface.getDefinitionArea().append("\n----Date----\n");
                	clientInterface.getDefinitionArea().append("Date: "+dateElement.getTextContent()+"\n");
                }
                clientInterface.getDefinitionArea().append("\n----Definitions----\n");
                NodeList dtList=defElement.getElementsByTagName("dt");
                if (dtList!=null){
                	for (int i=0;i<dtList.getLength();i++){
                    	Element dfElement=(Element) dtList.item(i);
                    	clientInterface.getDefinitionArea().append("Definiton "+(i+1)+dfElement.getTextContent()+"\n");
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
