package client.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jdesktop.xswingx.JXTextArea;
import org.jdesktop.xswingx.JXTextField;
import org.jdesktop.xswingx.PromptSupport.FocusBehavior;

public class ClientInterface {
	private JFrame mainFrame;
	private JPanel mainPanel;
	private CardLayout cardLayout;
	private ScoreTable scoreTable;
	private WordsTable wordsTable;
	private JPanel centerPanelTop;
	private JLabel playerNameLabel;
	private JLabel roomLabel;
	private JButton submitButton;
	private JButton newLettersButton;
	private JXTextField wordField;
	private JXTextArea definitionArea;
	
	public JFrame getMainFrame() {
		return mainFrame;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getCenterPanelTop() {
		return centerPanelTop;
	}

	public CardLayout getCardLayout() {
		return cardLayout;
	}
	
	public JLabel getPlayerNameLabel() {
		return playerNameLabel;
	}

	public JLabel getRoomLabel() {
		return roomLabel;
	}

	public WordsTable getWordsTable() {
		return wordsTable;
	}
	
	public JButton getSubmitButton(){
		return submitButton;
	}
	
	public JButton getNewLettersButton(){
		return newLettersButton;
	}
	
	public JXTextField getWordField(){
		return wordField;
	}
	
	public ScoreTable getScoreTable(){
		return scoreTable;
	}
	
	public JXTextArea getDefinitionArea(){
		return definitionArea;
	}

	public ClientInterface() {
		JPanel searchPanel=new JPanel(new BorderLayout());
		searchPanel.add(new Label("Searching for players...",Label.CENTER),BorderLayout.CENTER);
		JPanel gamePanel=new JPanel(new BorderLayout());
		gamePanel.add(makeSplitPane(),BorderLayout.CENTER);
        cardLayout=new CardLayout();
		mainPanel=new JPanel(cardLayout);
		mainPanel.add(searchPanel, "Search");
		mainPanel.add(gamePanel, "Game");
		mainFrame=new JFrame("Scrabble Game");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		mainFrame.setSize(new Dimension(800, 600));
		mainFrame.setLocationRelativeTo(null);
		mainFrame.add(mainPanel);
		mainFrame.setContentPane(mainPanel);
		cardLayout.show(mainPanel, "Search");
	}
	
	private JPanel makeTopPanel(){
		FlowLayout flowLayout=new FlowLayout(FlowLayout.CENTER,100,5);
		JPanel topPanel=new JPanel(flowLayout);
		playerNameLabel=new JLabel("You: ");
		playerNameLabel.setForeground(Color.RED);
		roomLabel=new JLabel("Camera: ");
		roomLabel.setForeground(Color.MAGENTA);
		topPanel.add(playerNameLabel);
		topPanel.add(roomLabel);
		topPanel.setBorder(new LineBorder(Color.BLACK, 1));
		return topPanel;
	}
	
	private JPanel makeBottomPanel(){
		JPanel bottomPanel=new JPanel(new BorderLayout());
		JLabel definitionLabel=new JLabel("Word definition:");
		definitionLabel.setBorder(new EmptyBorder(0, 20, 10, 0));
		definitionArea=new JXTextArea();
		definitionArea.setRows(4);
		definitionArea.setEditable(false);
		definitionArea.setLineWrap(true);
		definitionArea.setWrapStyleWord(true);
		definitionArea.setPrompt("No definition...");
		definitionArea.setPromptForeground(Color.GRAY);
		definitionArea.setFocusBehavior(FocusBehavior.SHOW_PROMPT);
		definitionArea.setToolTipText("");
		JScrollPane scrollPane=new JScrollPane(definitionArea);
		bottomPanel.add(definitionLabel,BorderLayout.NORTH);
		bottomPanel.add(scrollPane,BorderLayout.CENTER);
		return bottomPanel;
	}
	
	private JScrollPane makeWordsTableScrollPanel(){
		wordsTable=new WordsTable();
		wordsTable.getTableHeader().setReorderingAllowed(false);
		wordsTable.setCellSelectionEnabled(false);
		JScrollPane wordsTablePanel=new JScrollPane(wordsTable);
		wordsTablePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 10, 10, 10),
				BorderFactory.createLineBorder(Color.BLACK)));
		return wordsTablePanel;
	}
	
	private JPanel makeWordsPanel(){
		JPanel wordsPanel=new JPanel();
		BoxLayout boxLayout=new BoxLayout(wordsPanel, BoxLayout.LINE_AXIS);
		wordsPanel.setLayout(boxLayout);
		JScrollPane wordsTabelPanel=makeWordsTableScrollPanel();
		JLabel wordsLabel=new JLabel("Your words: ");
		wordsLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
		wordsLabel.setLabelFor(wordsTabelPanel);
		wordsPanel.add(wordsLabel);
		wordsPanel.add(wordsTabelPanel);
		return wordsPanel;
	}
	
	private JPanel makeCenterPanelBottom(){
		JPanel centerPanelBottom=new JPanel(new FlowLayout(FlowLayout.CENTER));
		wordField=new JXTextField();
		wordField.setPrompt("Word here...");
		wordField.setPromptForeground(Color.GRAY);
		wordField.setFocusBehavior(FocusBehavior.SHOW_PROMPT);
		wordField.setToolTipText("");
		wordField.setPreferredSize(new Dimension(300, 30));
		submitButton=new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(100,30));
		newLettersButton=new JButton("New Letters");
		newLettersButton.setPreferredSize(new Dimension(110,30));
		centerPanelBottom.add(wordField);
		centerPanelBottom.add(submitButton);
		centerPanelBottom.add(newLettersButton);
		centerPanelBottom.setBorder(new EmptyBorder(20, 0, 20, 0));
		return centerPanelBottom;
	}
	
	private JPanel makeCenterPanelTop(){
		centerPanelTop=new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerPanelTop.setBorder(new EmptyBorder(20, 0, 40, 0));
		return centerPanelTop;
	}
	
	private JPanel makeCenterPanel(){
		JPanel centerPanel=new JPanel(new BorderLayout());
		centerPanel.add(makeCenterPanelTop(),BorderLayout.NORTH);
		centerPanel.add(makeCenterPanelBottom(),BorderLayout.SOUTH);
		centerPanel.add(makeWordsPanel(),BorderLayout.CENTER);
		return centerPanel;
	}
	
	private JPanel makeLeftComponentForSplit(){
		JPanel leftComponent=new JPanel(new BorderLayout());
		leftComponent.add(makeCenterPanel(),BorderLayout.CENTER);
		leftComponent.add(makeTopPanel(),BorderLayout.NORTH);
		leftComponent.add(makeBottomPanel(),BorderLayout.SOUTH);
		return leftComponent;
	}
	
	private ScoreTable makeScoreTable(){;
        scoreTable = new ScoreTable();
        scoreTable.setCellSelectionEnabled(false);
        scoreTable.getTableHeader().setReorderingAllowed(false);
        return scoreTable;
	}
	
	private JScrollPane makeRightComponentForSplit(){
		JScrollPane rightComponent=new JScrollPane(makeScoreTable());
		return rightComponent;
	}
	
	private JSplitPane makeSplitPane(){
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setLeftComponent(makeLeftComponentForSplit());
		mainSplitPane.setRightComponent(makeRightComponentForSplit());
        mainSplitPane.setDividerLocation(550);
        mainSplitPane.setEnabled(false);
        return mainSplitPane;
	}
}
