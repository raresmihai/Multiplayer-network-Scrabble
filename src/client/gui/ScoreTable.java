package client.gui;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ScoreTable extends MyTable {
	
	public ScoreTable() {
		super();
		columnNames=new String[]{"Player","Score"};
		tableModel=new DefaultTableModel(columnNames, 0);
		maxs=new Integer[columnNames.length];
		for (int i=0;i<maxs.length;i++){
			maxs[i]=new Integer(0);
		}
		this.setModel(tableModel);
		this.setAllCellsUnresizeable();
	}
}
