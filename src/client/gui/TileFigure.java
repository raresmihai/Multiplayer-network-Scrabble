package client.gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

import game.Tile;

@SuppressWarnings("serial")
public class TileFigure extends JButton{
	private Tile tile;
	
	public TileFigure(Tile tile) {
		super();
		this.tile=tile;
		this.setPreferredSize(new Dimension(60, 60));
		this.setFont(new Font("Times New Roman", 0, 33));
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setText(Character.toString(tile.getLetter()));
	}

	public Tile getTile() {
		return tile;
	}
}
