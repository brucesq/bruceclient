package cn.hunthawk.j2me.ui.screen;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.ChannelItem;
import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.bo.ItemControler;
import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.List;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;


public class MainScreen extends List 
implements CommandListener,Runnable{
	public static MainScreen mainScreen = null;
	public static BrowserScreen2 bookMainScreen = null;
	public static BrowserScreen2 myBookScreen = null;

	private Command cmdOk = new Command("是",Command.ITEM,1);
	private Command cmdNo = new Command("否",Command.BACK,1);

	private Command cmdSelect = new Command(Locale.get("cmd.selectitem"),Command.ITEM,6);
	private Command cmdSet = new Command(Locale.get("cmd.set"),Command.ITEM,7);
	private Command cmdInvitation = new Command(Locale.get("cmd.invitation"),Command.ITEM,8);
	private Command cmdBookStore = new Command(Locale.get("cmd.bookstore"),Command.ITEM,9);
	private Command cmdHelp = new Command(Locale.get("cmd.help"),Command.ITEM,10);
	private Command cmdExit = new Command(Locale.get("cmd.exit"),Command.ITEM,13);
	private Command cmdDownload = new Command(Locale.get("cmd.download"),Command.BACK,1);
	
	
	//#ifdef polish.debugEnabled
	Command showLogCmd = new Command( Locale.get("cmd.showLog"), Command.ITEM, 9 );
	Command cmdExit1 = new Command(Locale.get("cmd.exit"), Command.EXIT, 10 );
	//#endif
	
	private Vector vItem = null;
	private Alert alert = null;
	private int size = 9;
	
	private Font font=null;
	
	private Image []icon = new Image[9];
	private Image []focusedIcon = new Image[9];
	public MainScreen(){
		//#style mainScreen2
		super("精彩阅读  精彩在沃",List.IMPLICIT);
		//#ifdef polish.debugEnabled
		this.addCommand( this.showLogCmd );
		this.addCommand( this.cmdExit );
		this.setCommandListener(this);
		//#endif
		//#style messageAlert
		alert = new Alert( null);
		alert.addCommand(cmdNo);
		alert.addCommand(cmdOk);
		alert.setCommandListener(this);
		Initialize();
		this.addCommand(cmdSet);
		this.addCommand(cmdDownload);
		this.addCommand(cmdBookStore);
		this.addCommand(cmdSelect);
		this.addCommand(cmdInvitation);
		this.addCommand(cmdHelp);
		this.addCommand(cmdExit);
		
		switchItem(0);
		
		this.setCommandListener(this);
		SplashScreen.splashscreen.releaseResources();
		SplashScreen.splashscreen = null;
		
		screenWidth = this.getScreenFullWidth();
		screenHeight = this.getScreenFullHeight();
		
		tempData.setScreen(screenWidth, screenHeight);
		
		new Thread(this).start();
	}
	public static MainScreen getInstance(){
		if(mainScreen == null){
			mainScreen = new MainScreen();
		}
		return mainScreen;
	}
	public void Initialize(){

		Vector v = (Vector)RmsOpt.readRMS("homecontrol");
		HomeControler home = (HomeControler)v.elementAt(0);
		vItem = home.getVItem();
		int length = vItem.size();
		for(int i=0;i<length;i++){
			ItemControler item = (ItemControler)vItem.elementAt(i);
			try {
				icon[i] = Image.createImage(item.getFocusedIcon().substring(11));
				focusedIcon[i] = Image.createImage(item.getIcon().substring(11));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//#style mainCommand
			this.append(null,icon[i]);
		}
	}
	
	public void switchItem(int i){
		int index = this.getSelectedIndex();
		
		this.set(index, null, icon[index]);
		
		this.focus(i);
		
		this.set(i, null, focusedIcon[i]);
		
	}
	protected boolean handleKeyPressed(int keyCode,int gameAction){
		int index = this.getSelectedIndex();
		if(keyCode == Canvas.KEY_NUM1){
//			System.out.println("keyCode:1");
			if(index == 0){
				commandAction(cmdSelect,this);
			}else{
				switchItem(0);
			}
			
		}else if(keyCode == Canvas.KEY_NUM2){
//			System.out.println("keyCode:2");
			if(index == 1){
				commandAction(cmdSelect,this);
			}else{
				switchItem(1);
			}
		}else if(keyCode == Canvas.KEY_NUM3){
//			System.out.println("keyCode:3");
			if(index == 2){
				commandAction(cmdSelect,this);
			}else{
				switchItem(2);
			}
		}else if(keyCode == Canvas.KEY_NUM4){
//			System.out.println("keyCode:4");
			if(index == 3){
				commandAction(cmdSelect,this);
			}else{
				switchItem(3);
			}
		}else if(keyCode == Canvas.KEY_NUM5){
//			System.out.println("keyCode:5");
			if(index == 4){
				commandAction(cmdSelect,this);
			}else{
				switchItem(4);
			}
		}else if(keyCode == Canvas.KEY_NUM6){
//			System.out.println("keyCode:6");
			if(index == 5){
				commandAction(cmdSelect,this);
			}else{
				switchItem(5);
			}
		}else if(keyCode == Canvas.KEY_NUM7){
//			System.out.println("keyCode:7");
			if(index == 6){
				commandAction(cmdSelect,this);
			}else{
				switchItem(6);
			}
		}else if(keyCode == Canvas.KEY_NUM8){
//			System.out.println("keyCode:8");
			if(index == 7){
				commandAction(cmdSelect,this);
			}else{
				switchItem(7);
			}
		}else if(keyCode == Canvas.KEY_NUM9){
//			System.out.println("keyCode:9");
			if(index == 8){
				commandAction(cmdSelect,this);
			}else{
				switchItem(8);
			}
		}else if(keyCode == Canvas.KEY_STAR){
//			System.out.println("keyCode:*");
		    UiAccess.handleKeyPressed(this, -6, gameAction);
		}
		else if(gameAction == Canvas.LEFT){
			if(index == 0){
				switchItem(size-1);
			}else{
				index--;
				switchItem(index);
			}
		}else if(gameAction == Canvas.RIGHT){
			if(index == size-1){
				switchItem(0);
			}else{
				index++;
				switchItem(index);
			}
		}else if(gameAction == Canvas.UP){
			if(index < 3){
				index+=(size-3);
				switchItem(index);
			}else{
				index-=3;
				switchItem(index);
			}
		}else if(gameAction == Canvas.DOWN){
			if(index > (size-4)){
				index-=(size-3);
				switchItem(index);
			}else{
				index+=3;
				switchItem(index);
			}
		}
		else{
			super.handleKeyPressed(keyCode, gameAction);
			
		}
		
		return true;
	}
	protected boolean handleKeyRepeated(int keyCode, int gameAction){
		int index = this.getSelectedIndex();
		//#debug
		System.out.println("key code:"+keyCode);
		//#debug
		System.out.println("key gameAction:"+gameAction);
		if(gameAction == Canvas.LEFT){
			if(index == 0){
				switchItem(size-1);
			}else{
				index--;
				switchItem(index);
			}
		}else if(gameAction == Canvas.RIGHT){
			if(index == size-1){
				switchItem(0);
			}else{
				index++;
				switchItem(index);
			}
		}else if(gameAction == Canvas.UP){
			if(index < 3){
				index+=(size-3);
				switchItem(index);
			}else{
				index-=3;
				switchItem(index);
			}
		}else if(gameAction == Canvas.DOWN){
			if(index > (size-4)){
				index-=(size-3);
				switchItem(index);
			}else{
				index+=3;
				switchItem(index);
			}
		}
		else{
			super.handleKeyRepeated(keyCode, gameAction);
			
		}
		return true;
	}
	
	public void commandAction(Command cmd, Displayable d) {
		//#ifdef polish.debugEnabled
		if (cmd == this.showLogCmd ) 
		{
			Debug.showLog(MainMidlet.getInstance().display);
			return;
		}
		if(cmd == this.cmdExit1)
		{
			MainMidlet.getInstance().quit();
		}
		//#endif
		if(cmd == List.SELECT_COMMAND || cmd == this.cmdSelect){
			int index = this.getSelectedIndex();
			System.out.println("select:"+index);
			if(index == 0 || index == 1 || index == 2 || index == 4){
				if(bookMainScreen != null){
					bookMainScreen.setFocusedTab(true);
					bookMainScreen.setEntry(0);
					MainMidlet.getInstance().display.setCurrent(bookMainScreen);
//					mainScreen.releaseResources();
					
					switch(index){
					
					case 0:
						
						bookMainScreen.setTitle("图书-首页");
						bookMainScreen.switchTab(0);
						break;
					case 1:
						
						bookMainScreen.setTitle("图书-新书");
						bookMainScreen.switchTab(2);
						break;
					case 2:
						
						bookMainScreen.setTitle("图书-分类");
						bookMainScreen.switchTab(5);
						break;
					case 4:
						
						bookMainScreen.setTitle("图书-作者");
						bookMainScreen.switchTab(4);
						break;
					}
					
				}else{
					
					ItemControler ic = (ItemControler)vItem.elementAt(index);
					String titles[] = null;
					String channels[] = null;
					Vector items = (Vector)ic.getChannels();
					titles = new String[items.size()];
					channels = new String[items.size()];
					for(int i=0;i<items.size();i++){
						ChannelItem item = (ChannelItem)items.elementAt(i);
						titles[i] = item.getName();
						channels[i] = item.getLink();
					}
					
					bookMainScreen = new BrowserScreen2(titles,channels);
										
					
					bookMainScreen.setScreenType(0);
					bookMainScreen.setEntry(0);
//					bookMainScreen.switchTabs(0);
					switch(index){
					case 0:
						bookMainScreen.switchTab(0);
						bookMainScreen.setTitle("图书-首页");
						break;
					case 1:
						bookMainScreen.switchTab(2);
						bookMainScreen.setTitle("图书-新书");
						break;
					case 2:
						
						bookMainScreen.switchTab(5);
						bookMainScreen.setTitle("图书-分类");
						break;
					case 4:
						bookMainScreen.switchTab(4);
						bookMainScreen.setTitle("图书-作者");
						break;
					}
					bookMainScreen.setFocusedTab(false);
					MainMidlet.getInstance().display.setCurrent(bookMainScreen);
					
				}
			}else if(index == 6 || index == 7 || index == 8){
				if(myBookScreen != null){
					
					switch(index){
					case 6:
						myBookScreen.switchTab(0);
						myBookScreen.refreshTab(0);
						myBookScreen.setTitle("书架-历史");
						break;
					case 7:
						myBookScreen.switchTab(1);
						myBookScreen.refreshTab(1);
						myBookScreen.setTitle("书架-书签");
						break;
					case 8:
						myBookScreen.switchTab(2);
						myBookScreen.refreshTab(2);
						myBookScreen.setTitle("书架-收藏");
						break;
					}
					myBookScreen.setEntry(0);
					MainMidlet.getInstance().display.setCurrent(myBookScreen);
				}else{
					ItemControler ic = (ItemControler)vItem.elementAt(index);
					String titles[] = null;
					String channels[] = null;
					Vector items = (Vector)ic.getChannels();
					titles = new String[items.size()];
					channels = new String[items.size()];
					for(int i=0;i<items.size();i++){
						ChannelItem item = (ChannelItem)items.elementAt(i);
						titles[i] = item.getName();
						channels[i] = item.getLink();
					}
					
					myBookScreen = new BrowserScreen2(titles,channels);
					myBookScreen.setEntry(0);
					
					myBookScreen.setScreenType(1);
					switch(index){
					case 6:
						myBookScreen.switchTab(0);
						myBookScreen.setTitle("书架-历史");
						break;
					case 7:
						myBookScreen.switchTab(1);
						myBookScreen.setTitle("书架-书签");
						break;
					case 8:
						myBookScreen.switchTab(2);
						myBookScreen.setTitle("书架-收藏");
						break;
					}
					MainMidlet.getInstance().display.setCurrent(myBookScreen);
				}
			}else if(index == 3){
				if(TopScreen.topScreen != null){
					MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
				}else{
					ItemControler ic = (ItemControler)vItem.elementAt(index);
					
					TopScreen.getInstance().initTab(ic.getLink());
					TopScreen.getInstance().setTitle(ic.getItemName());
					
					MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
				}
			}else if(index == 5){
				ItemControler ic = (ItemControler)vItem.elementAt(index);
				
				BrowserScreen.getInstance().setTitle("搜索");
				
				BrowserScreen.getInstance().setEntry(1);
				MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
				BrowserScreen.getInstance().go(ic.getLink());
			}
			
			mainScreen.releaseResources();
			System.gc();
		}else if(cmd == cmdHelp){
			BrowserScreen.getInstance().setTitle("帮助");
			String url = "resource://help.html";
			BrowserScreen.getInstance().setEntry(1);
			MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
			
			BrowserScreen.getInstance().go(url);
			
		}else if(cmd == cmdInvitation){
			InviteScreen1.getInstance().setEntry(0);
			MainMidlet.getInstance().display.setCurrent(InviteScreen1.getInstance());
		}else if(cmd == cmdSet){
			SystemSettingScreen.getInstance().setEntry(0);
			SystemSettingScreen.getInstance().initSetting();
			MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
			
		}else if(cmd == cmdExit){
			showAlert("您真的要退出吗?");
		}else if(cmd == cmdOk){
			
			String xmlURL1=null;
			//#= xmlURL1 = "${ homeurl }";
			
			long runningtime = System.currentTimeMillis() - tempData.startTime;
			runningtime = runningtime / 1000;
			
			Vector vtime = (Vector)RmsOpt.readRMS("runningtime");
			  if(vtime.isEmpty()){
				  vtime = new Vector();
				  vtime.addElement(String.valueOf(runningtime));
				  RmsOpt.saveRMS(vtime, "runningtime");
			  }else{
				  String lasttime = (String)vtime.elementAt(0);
				  long lasttimes = Long.parseLong(lasttime);
				  runningtime +=lasttimes;
				  vtime = new Vector();
				  vtime.addElement(String.valueOf(runningtime));
				  RmsOpt.saveRMS(vtime, "runningtime");
			  }
			 
			 xmlURL1 = xmlURL1 +"&runningtime="+runningtime;
//			System.out.println("url:"+xmlURL1);
			HttpConnectionUtil hcu = new HttpConnectionUtil(xmlURL1);
			hcu.setHttpConnectionListener(new MyHttpConnectionListener(21));
			hcu.start();
//			do{
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}while(true);
		}else if(cmd == cmdNo){
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdDownload){
			DownloadedBookScreen.getInstance().switchTab(0);
			DownloadedBookScreen.getInstance().setEntry(1);
			MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
		}else if(cmd == cmdBookStore){
			showMyBookStore(0);
		}
		
	}
	
	/**我的书架*/
	public void showMyBookStore(int k){
		if(myBookScreen != null){
			myBookScreen.switchTab(k);
			myBookScreen.refreshTab(k);
			myBookScreen.setEntry(k);
			
			MainMidlet.getInstance().display.setCurrent(myBookScreen);
		}else{
			ItemControler ic = (ItemControler)vItem.elementAt(6);
			String titles[] = null;
			String channels[] = null;
			Vector items = (Vector)ic.getChannels();
			titles = new String[items.size()];
			channels = new String[items.size()];
			for(int i=0;i<items.size();i++){
				ChannelItem item = (ChannelItem)items.elementAt(i);
				titles[i] = item.getName();
				channels[i] = item.getLink();
			}
			
			myBookScreen = new BrowserScreen2(titles,channels);
			myBookScreen.setScreenType(1);
			myBookScreen.setTitle(" ");
			myBookScreen.switchTab(k);
			myBookScreen.setEntry(k);
			
			MainMidlet.getInstance().display.setCurrent(myBookScreen);
		}
	}
	public void preProcessIndex(int type){
		switch(type){
		case 0:{
			System.out.println("preProcess0");
			ItemControler ic = (ItemControler)vItem.elementAt(0);
			String titles[] = null;
			String channels[] = null;
			Vector items = (Vector)ic.getChannels();
			titles = new String[items.size()];
			channels = new String[items.size()];
			for(int i=0;i<items.size();i++){
				ChannelItem item = (ChannelItem)items.elementAt(i);
				titles[i] = item.getName();
				channels[i] = item.getLink();
			}
			
			bookMainScreen = new BrowserScreen2(titles,channels);
								
			
			bookMainScreen.setScreenType(0);
			bookMainScreen.setEntry(0);
			bookMainScreen.switchTbs(0);
			bookMainScreen.setTitle("图书-首页");
			bookMainScreen.setFocusedTab(false);
			break;
		}
		case 1:{
			
			ItemControler ic = (ItemControler)vItem.elementAt(6);
			String titles[] = null;
			String channels[] = null;
			Vector items = (Vector)ic.getChannels();
			titles = new String[items.size()];
			channels = new String[items.size()];
			for(int i=0;i<items.size();i++){
				ChannelItem item = (ChannelItem)items.elementAt(i);
				titles[i] = item.getName();
				channels[i] = item.getLink();
			}
			
			myBookScreen = new BrowserScreen2(titles,channels);
			myBookScreen.setEntry(0);
			
			myBookScreen.setScreenType(1);
			
			myBookScreen.switchTbs(0);
			myBookScreen.setTitle("书架-历史");
			myBookScreen.setFocusedTab(false);
			break;
		}
		}
	}
	/**检查是否弹出欢迎页界面*/
	public void run() {
		try {
			//启动构造首页进程
			new Thread(){
				public void run(){
					preProcessIndex(0);
				}
			}.start();
			Thread.sleep(1000);
			int menuHight = this.getScreenFullHeight() - this.getScreenContentHeight() - this.titleHeight;
			
			tempData.setMenuHight(menuHight);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Vector v = (Vector)RmsOpt.readRMS("welcome");
		if(v.isEmpty()){
			MainMidlet.getInstance().display.setCurrent(WelcomeScreen.getInstance());
		}else{
			String set = (String)v.elementAt(0);
			if(set.equals("yes")){
				MainMidlet.getInstance().display.setCurrent(WelcomeScreen.getInstance());
			}
		}
		
	}
	
	/**提示框设置*/
	public void showAlert(String content){
		alert.setTimeout(Alert.FOREVER);
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	

}
