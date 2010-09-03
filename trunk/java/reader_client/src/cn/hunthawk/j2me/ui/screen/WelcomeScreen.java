package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.Tools;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;

public class WelcomeScreen extends Form implements CommandListener,Runnable{
	public static WelcomeScreen welScreen = null;
	private Command cmdClose = new Command("关闭",Command.BACK,1);
	private Command cmdSet = new Command("设置",Command.ITEM,1);
	private HtmlBrowser htmlBrowser = null;
	private StringItem strItem = null;
	private boolean lock = false;
	public WelcomeScreen(){
		//#style welScreen
		super(null);
		
		this.addCommand(cmdSet);
		this.addCommand(cmdClose);
		this.setCommandListener(this);
		//#style browser
		htmlBrowser = new HtmlBrowser();
		
//		htmlBrowser.setDifintionKey(false);
//		this.append(htmlBrowser);
		//#style progessItem4
		strItem = new StringItem(null,"请稍后,正在加载....");
		this.append(strItem);
		new Thread(this).start();
	}
	public static WelcomeScreen getInstance(){
		if(welScreen == null){
			welScreen = new WelcomeScreen();
		}
		return welScreen;
	}
	public void run(){
		while(true){
			Tools.sleep(500);
			if(htmlBrowser.getItems().length != 0){
				this.deleteAll();
				this.append(htmlBrowser);
				lock = true;
				break;
			}
		}
	}
	public void init(){
		Vector v = (Vector)RmsOpt.readRMS("homecontrol");
		HomeControler home = (HomeControler)v.elementAt(0);
		String url = home.getLink();
		
		htmlBrowser.go(url);
		
	}
	protected boolean handleKeyPressed(int keyCode, int gameAction){
		if(gameAction == Canvas.FIRE){
			Item anchor= htmlBrowser.getFocusedItem();
			System.out.println("current:"+htmlBrowser.getCurrentUrl());
			String href = (String)anchor.getAttribute("href");
			if(href != null){
				if(href.indexOf("pg=d") != -1){
					href = htmlBrowser.makeAbsoluteURL(href);
					
					NovelsReaderScreen.getInstance().setEntry(3);
					NovelsReaderScreen.getInstance().initCommand();
					MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
					NovelsReaderScreen.getInstance().setAddress(href,1);
				}else if(href.indexOf("pg=r") != -1){
					href = htmlBrowser.makeAbsoluteURL(href);
					System.out.println("href1:"+href);
					
					ReaderControlScreen.getInstance().setEntry(5);
					MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
					ReaderControlScreen.getInstance().init(0, href, null);
				}else{
					return htmlBrowser.handleKeyPressed(keyCode, gameAction);
				}
			}
		}else if(gameAction == Canvas.DOWN){
			if(lock){
				boolean isDown = true;
				  int index = htmlBrowser.getFocusedIndex();
				  if(index == -1) return false;
				  do{
					  index++;
					  if(index > htmlBrowser.getItemSize()){
						  int height =-(htmlBrowser.getContentHeight() - this.getAvailableHeight()+50);
						  System.out.println("height:"+height);
						  this.setScrollYOffset(height, false);
						  isDown = false;
						  break;
					  }
				  }while(!htmlBrowser.focus(index));
				  if(isDown){
					  htmlBrowser.scroll(0, htmlBrowser.getFocusedItem());
				  }
			}
			 
			  
			  
		}else if(gameAction == Canvas.UP){
			if(lock){
				boolean top = true;
				  int index = htmlBrowser.getFocusedIndex();
				  if(index == -1) return false;
				  do{
					  index--;
					  if(index < 0){
						  htmlBrowser.setScrollYOffset(0, false);
						  top = false;
						  break;
					  }
				  }while(!htmlBrowser.focus(index));
				  if(top){
					  htmlBrowser.scroll(0, htmlBrowser.getFocusedItem());
				  }
			}
			
		}
		else{
			return htmlBrowser.handleKeyPressed(keyCode, gameAction);
		}
		return true;
	}
	protected boolean handleKeyRepeated(int keyCode,
	          int action){
		  
		  return this.handleKeyPressed(keyCode, action);

	 }
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdClose){
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdSet){
			
			MainMidlet.getInstance().display.setCurrent(WelcomePageSetScreen.getInstance());
		}
		
	}
	
	
}
