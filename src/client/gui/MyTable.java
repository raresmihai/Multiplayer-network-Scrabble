package client.gui;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
abstract class MyTable extends JTable{
	protected String[] columnNames;
	protected DefaultTableModel tableModel;
	protected Integer maxs[];
	
	public MyTable() {
		super();
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER );
		this.setDefaultRenderer(Object.class, centerRenderer);
	}

	public boolean isCellEditable(int row,int column){
	    return false;
	  }
	
	protected void setAllCellsUnresizeable(){
		for (int i=0;i<this.getColumnCount();i++){
			this.getColumnModel().getColumn(i).setResizable(false);
		}
	}
	
	public void addRow(Object[] data){
		tableModel.addRow(data);
		int i=0;
		for (Object iterator:data){
			maxs[i]=Math.max(maxs[i].intValue(), iterator.toString().length());
			i++;
		}
	}
	
	public void adjustColumns(){
		for (int i=0;i<maxs.length;i++){
			this.getColumnModel().getColumn(i).setPreferredWidth(maxs[i]*6);
		}
	}
}
