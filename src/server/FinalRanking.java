package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

class FinalRanking {
	private RoomThread room;
	private File roomRanking;
	private String rankingLink;
	
	public String getRankingLink() {
		return rankingLink;
	}

	public void setRankingLink(String rankingLink) {
		this.rankingLink = rankingLink;
	}

	private void makeHTMLPage(){
		List<ClientThread> roomPlayers=room.getRoomPlayers();
		try {
			FileWriter fileWriter=new FileWriter(roomRanking);
			fileWriter.write("<html><head><title>Room "+room.getRoomId()+"</title></head>"
					+ "<body><h1>Final Ranking of Room "+room.getRoomId()+"</h1></body></hmtl>");
			int index;
			for (index=0;index<roomPlayers.size();index++){
				switch (index) {
				case 0:{
					fileWriter.write("<h2>1st place: "+roomPlayers.get(index).getPlayerName()+" with the score: "
							+roomPlayers.get(index).getPlayerScore()+".</h2>\n");
					break;
				}
				case 1:{
					fileWriter.write("<h2>2nd place: "+roomPlayers.get(index).getPlayerName()+" with the score: "
							+roomPlayers.get(index).getPlayerScore()+".</h2>\n");
					break;
				}
				case 2:{
					fileWriter.write("<h2>3rd place: "+roomPlayers.get(index).getPlayerName()+" with the score: "
							+roomPlayers.get(index).getPlayerScore()+".</h2>\n");
					break;
				}
				default:
					fileWriter.write("<h2>"+(index+1)+"th place: "+roomPlayers.get(index).getPlayerName()+" with the score: "
							+roomPlayers.get(index).getPlayerScore()+".</h2>\n");
					break;
				}
			}
			fileWriter.close();
		} catch (IOException e) {
		}		
	}
	
	FinalRanking(RoomThread roomThread) {
		this.room=roomThread;
		roomRanking=new File("room"+room.getRoomId()+"FinalRanking.html");
		if (roomRanking.exists()==false){
			try {
				roomRanking.createNewFile();
			} catch (IOException e) {
				System.out.println("Fisier de ranking nefacut pentru camera: "+room.getRoomId());
			}
		}
		setRankingLink("http://students.info.uaic.ro/~gheorghe.gripca/"+roomRanking.getName());
	}
	
	void displayOnWeb(){
		makeHTMLPage();
	    FileInputStream fis=null;
	    try{
	      String user="gheorghe.gripca";
	    
	      String host="fenrir.info.uaic.ro";
	      String rfile="/fenrir/studs/gheorghe.gripca/public_html";
	
	      JSch jsch=new JSch();
	      Session session=jsch.getSession(user, host, 22);
	      session.setPassword("pass");
	      
	      Properties config = new Properties();
	      config.put("StrictHostKeyChecking","no");
	      session.setConfig(config);
	      session.connect();
	
	      boolean ptimestamp = true;
	
	      // exec 'scp -t rfile' remotely
	      String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
	      Channel channel=session.openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);
	
	      // get I/O streams for remote scp
	      OutputStream out=channel.getOutputStream();
	      InputStream in=channel.getInputStream();
	
	      channel.connect();
	
	      if(checkAck(in)!=0){
	    	  System.exit(0);
	      }
	      System.out.println("trecut de 1");
	      if(ptimestamp){
	        command="T "+(roomRanking.lastModified()/1000)+" 0";
	        // The access time should be sent here,
	        // but it is not accessible with JavaAPI ;-<
	        command+=(" "+(roomRanking.lastModified()/1000)+" 0\n");
	        out.write(command.getBytes()); out.flush();
	        if(checkAck(in)!=0){
	        	System.exit(0);
	        }
	        System.out.println("trecut de 2");
	      }
	      
	      // send "C0644 filesize filename", where filename should not include '/'
	      long filesize=roomRanking.getName().length();
	      command="C0644 "+filesize+" ";
	      if(roomRanking.getName().lastIndexOf('/')>0){
	        command+=roomRanking.getName().substring(roomRanking.getName().lastIndexOf('/')+1);
	      }
	      else{
	        command+=roomRanking.getName();
	      }
	      command+="\n";
	      out.write(command.getBytes()); out.flush();
	      if(checkAck(in)!=0){
	    	  System.exit(0);
	      }
	      System.out.println("trecut de 3");
	
	      // send a content of lfile
	      fis=new FileInputStream(roomRanking);
	      byte[] buf=new byte[1024];
	      while(true){
	        int len=fis.read(buf, 0, buf.length);
	        if(len<=0) break;
	        out.write(buf, 0, len); //out.flush();
	      }
	      fis.close();
	      fis=null;
	      // send '\0'
	      buf[0]=0; out.write(buf, 0, 1); out.flush();
	      if(checkAck(in)!=0){
	    	  System.exit(0);
	    	  System.out.println("trecut de 4");
	      }
	      out.close();
	
	      channel.disconnect();
	      session.disconnect();
	    }
	    catch(Exception e){
	      System.out.println(e);
	      try{if(fis!=null)fis.close();}catch(Exception ee){}
	    }
	  }
	
	  static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;
	
	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
	    	  c=in.read();
	    	  sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
	    	  System.out.print(sb.toString());
	      }
	      if(b==2){ // fatal error
	    	  System.out.print(sb.toString());
	      }
	    }
	    return b;
	  }
}
