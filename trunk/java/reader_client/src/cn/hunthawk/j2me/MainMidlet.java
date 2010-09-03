package cn.hunthawk.j2me;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;


import cn.hunthawk.j2me.ui.screen.SplashScreen;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;


public class MainMidlet extends MIDlet {
	
	
	public static MainMidlet mainMidlet = null;
	public Display display=null;
	public static boolean iswap=false;

	public MainMidlet()
	{
		mainMidlet = this;
	}

	public static MainMidlet getInstance()
	{
		if(mainMidlet==null){
			mainMidlet = new MainMidlet();
		}
		return mainMidlet;
	}
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		
	}

	
	protected void pauseApp() {
		

	}

	
	protected void startApp() throws MIDletStateChangeException {
		if(display==null)
    	{
    		display = Display.getDisplay(this);
    	}
		
		SplashScreen.getInstance().start();
		
		//¼ÇÂ¼µÇÂ¼Ê±¼ä
		tempData.startTime = System.currentTimeMillis();
		display.setCurrent(SplashScreen.getInstance());
	}
	
	public void quit()
	{
		try {
			RecordStore.deleteRecordStore("imageCache");
		} catch (RecordStoreNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RecordStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			destroyApp(false);
			notifyDestroyed();
		}catch(Exception e){}
	}

//	class LightThread extends Thread{
//		public void run(){
//			while(true){
//				Tools.sleep(10000);
//				DeviceControl.setLights(0, 90);
//			}
//		}
//	}
	
	
}
