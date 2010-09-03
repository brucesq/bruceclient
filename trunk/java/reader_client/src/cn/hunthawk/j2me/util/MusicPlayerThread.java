package cn.hunthawk.j2me.util;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class MusicPlayerThread {
	
	public static MusicPlayerThread music = null;
	private playThread musicPlay = null;
	private Player player = null;
	private InputStream is = null;
	public MusicPlayerThread(){
		
	}
	public static MusicPlayerThread getInstance(){
		if(music == null){
			music = new MusicPlayerThread();
		}
		return music;
	}
	
	public void play(){
		musicPlay = new playThread();
		musicPlay.start();
	}
	public void stop(){
		try {
			if(player != null){
				player.stop();
				player = null;
			}
			if(musicPlay != null){
				musicPlay = null;
			}
			
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class playThread extends Thread{
		public void run(){
			try {
				try {
					is=getClass().getResourceAsStream("/test.mid");
					player = Manager.createPlayer(is,"audio/mid");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.realize(); 
				player.setLoopCount(-1);
				player.start();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
