package cn.hunthawk.j2me.ui.screen;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.MainMidlet;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.StringItem;

public class LoadPageScreen extends Form implements Runnable, CommandListener
{

	private static LoadPageScreen loadscreen = null;
	protected Gauge loadingIndicator;
	private Command cancelCmd = new Command("»°œ˚", Command.CANCEL, 10);
	private static Displayable dis;
	private StringItem item = null;
	public LoadPageScreen()
	{
		//#style LoadPageScreen	
		super(null);

	    //#style loadIndicator1
	    this.loadingIndicator = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
	    //#style loadIndicator1label
	    item = new StringItem(null,"«Î…‘∫Ú...");
	    
	    this.append(this.loadingIndicator);
	    this.append(item);
	    this.addCommand(cancelCmd);
	    this.setCommandListener(this);
	}

	public static LoadPageScreen getInstance()
	{
		if(loadscreen==null){
			loadscreen = new LoadPageScreen();
		}
		dis = MainMidlet.getInstance().display.getCurrent();
		return loadscreen;
	}
	
	public void run() {
		try {
			Thread.sleep(500);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void commandAction(Command cmd, Displayable screen) {
		if(cmd == this.cancelCmd)
			MainMidlet.getInstance().display.setCurrent(dis);		
	}
	
	public Displayable getPrevScreen(){
		return this.dis;
	}
}

