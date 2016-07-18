package server;

import java.util.concurrent.TimeUnit;

class Timer extends Thread{
	private volatile boolean shouldRun = true;
	private long time;
	private ScrabbleServer server;
	private int count=0;

    public Timer(ScrabbleServer scrabbleServer) {
    	this.server=scrabbleServer;
    }
    @Override
    public void run() {
        long startTime = System.nanoTime();
        while (shouldRun) {
            long estimatedTime = System.nanoTime() - startTime;
            time=TimeUnit.NANOSECONDS.toSeconds(estimatedTime);
            if (time==10){
            	System.out.println(time);
            	startTime=System.nanoTime();
            	if (server.getSockets().size()>1){
            		RoomThread roomThread=new RoomThread(server.copySockets(), server.copyScoreSockets(),++count);
            		roomThread.start();
            	}
            }
        }
    }
    
    public void stopTimer(){
    	shouldRun=false;
    }
    
    public long getTime()
    {
    	return time;
    }
}
