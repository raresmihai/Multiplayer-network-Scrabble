package client;

import java.util.concurrent.TimeUnit;

import javax.swing.JButton;

class NewLettersThread extends Thread{
	private volatile boolean start=false;
	private JButton buttonToManage;
	private volatile boolean GameOn;
	
	NewLettersThread(JButton buttonToManage) {
		this.buttonToManage=buttonToManage;
	}
	
	public void run(){
		GameOn=true;
		long startTime;
		long estimatedTime;
		long time;
		while(GameOn==true){
			startTime = System.nanoTime();
			while(start==true){
				  estimatedTime= System.nanoTime() - startTime;
		          time=TimeUnit.NANOSECONDS.toSeconds(estimatedTime);
		          buttonToManage.setText("Wait "+(9-time)+" sec.");
		          if (time==9){
		        	  start=false;
		        	  buttonToManage.setEnabled(true);
		        	  buttonToManage.setText("New Letters");
		          }
			}
		}
	}
	
	public void startCountDown(){
		start=true;
		buttonToManage.setEnabled(false);
	}
	
	public void stopThread(){
		GameOn=false;
	}
}
