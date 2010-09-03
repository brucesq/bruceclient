package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.FirstMask;
import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.InitControler;
import cn.hunthawk.j2me.browser.conf.XMLBrowser;
import cn.hunthawk.j2me.control.UserBrowserListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.util.ControlerOpt;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;

public class SplashScreen extends Form implements CommandListener,Runnable{
	
	public static SplashScreen splashscreen = null;
	private String message="正在进入沃书城...";
	private StringItem msg2 = null;
	private StringItem msg = null;
	private String xmlURL = null;
	private InitControler control = null;
	protected Gauge loadingIndicator = null;
	public XMLBrowser xmlBrowser;

	//#ifdef polish.debugEnabled
	Command showLogCmd = new Command( Locale.get("cmd.showLog"), Command.ITEM, 9 );
	Command cmdExit = new Command(Locale.get("cmd.exit"), Command.EXIT, 10 );
	//#endif
	
	//alert变量
	private Alert alert;
	private Command cmdOk = new Command(Locale.get("cmd.ok"),Command.ITEM,1);
	private Command cmdNo = new Command(Locale.get("cmd.no"),Command.BACK,1);
	private Command cmdQiut = new Command(Locale.get("cmd.quit"),Command.BACK,2);
	
	private Command cmdUpdate = new Command("更新",Command.BACK,1);
	private Command cmdUpdateNot = new Command("暂时不",Command.ITEM,1);
	

	private int alertState = 0;
	private int homeVersionOld = 0;
	private boolean checkVersion = true;
	
	private String updateUrl = null;
	FirstMask fm = null;
	public SplashScreen() {
		//#style splashScreen
		super(null);
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
		//#style loadIndicator
	    this.loadingIndicator = new Gauge(null, false, 120,1);
	    //#style progessItem2
		msg = new StringItem(null,message);
		this.append(msg);
	    this.append(loadingIndicator);
	    
		//#style progessItem2
		msg2 = new StringItem(null," ");
		this.append(msg2);
		this.addCommand(cmdQiut);
		this.setCommandListener(this);
		InitControler init =  ControlerOpt.getInitControler(RmsOpt.readRMS("initcontrol"));
		if(init != null){
			homeVersionOld = init.getHomeVersion();
		}
		xmlBrowser = new XMLBrowser();
		
		this.xmlBrowser.setBrowserListener(new UserBrowserListener(this.xmlBrowser,loadingIndicator,msg));
	}
	
	public static SplashScreen getInstance(){
		if(splashscreen==null){
			splashscreen = new SplashScreen();
		}
		return splashscreen;
	}
	
	
	public void start()
	{
		new Thread(this).start();
	}
	public void run() {
		RmsOpt.deleteRMS("initcontrol");
		//#= xmlURL = "${ homeurl }";
		
		//#if app.use.firstmask
		fm = new FirstMask(xmlURL);
		fm.start();
		//#else
		this.xmlBrowser.go(xmlURL);
		waits();
		//#endif
    		
	}
	public void switchWebState(int state,int ok){
		switch(state){
		case 0:
			showAlert("请您设置沃书城允许使用网络链接,是否启动离线模式?",0);
			//#debug
			System.out.println("请您设置沃书城允许使用网络链接");
			break;
		case 1:
			showAlert("网络链接超时,是否启动离线模式?",0);
			//#debug
			System.out.println("网络链接超时");
			break;
		case 2:
			if(ok == 200 || ok==302){
				this.xmlBrowser.go(xmlURL);
				waits();
				//#debug
				System.out.println("ok:"+ok);
			}else{
				showAlert("服务器返回错误,是否启动离线模式?",0);
				//#debug
				System.out.println("服务器返回错误,是否启动离线模式");
				//#debug
				System.out.println("服务器返回错误,是否启动离线模式:"+ok);
			}
			break;
		}
		
	}
	public void waits()
	{
		fm = null;
		System.gc();
		do
		{
			try
			{					
				Thread.sleep( 100 );
				control =  ControlerOpt.getInitControler(RmsOpt.readRMS("initcontrol"));
			} 
			catch (InterruptedException e){}
		}while(control == null );
		
		do{
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}while(xmlBrowser.isWorking());
		//#debug
		System.out.println("check version1:"+msg.getText());
		msg.setText("检测版本.. "+"25%");
//		UpdateClient();
		//检查版本文件，服务器端根据UA适配，只有广东联通才能正常使用，别的匹配至其他
		String url = control.getSoftURL();
		xmlBrowser.go(url);
		do{
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}while(checkVersion);
		
		Vector homeItem = (Vector)RmsOpt.readRMS("homecontrol");
		if(homeItem.isEmpty()){
			this.xmlBrowser.go(control.getHomeURL());
		}else{
			int homeVersionNew = control.getHomeVersion();
			
			if(homeVersionNew != homeVersionOld){
				this.xmlBrowser.go(control.getHomeURL());
			}else{
				loadingIndicator.setValue(90);
				msg.setText("获取最新信息.. "+"81%");
				Vector v = (Vector)RmsOpt.readRMS("welcome");
				if(v.isEmpty()){
					WelcomeScreen.getInstance().init();
				}else{
					String set = (String)v.elementAt(0);
					System.out.println("set:"+set);
					if(set.equals("yes")){
						WelcomeScreen.getInstance().init();
					}
				}
				
				loadingIndicator.setValue(110);
				msg.setText("进入沃书城成功.. "+"95%");
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				
			}
		}
		
		
	}
	public void setShowInfo(String text){
		msg.setText(text);
	}
	public void setCheckVersionTag(boolean check){
		this.checkVersion = check;
	}
	public void commandAction(Command cmd, Displayable d) {
		//#ifdef polish.debugEnabled
		if (cmd == this.showLogCmd ) 
		{
			Debug.showLog(MainMidlet.getInstance().display);
			return;
		}
		if(cmd == this.cmdExit)
		{
			MainMidlet.getInstance().quit();
		}
		//#endif
		
		if(cmd == cmdOk){
			if(alertState == 0){
				DownloadedBookScreen.getInstance().setWeb(false);
				DownloadedBookScreen.getInstance().switchTab(0);
				MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
			}
			
		}
		else if(cmd == cmdNo){
			MainMidlet.getInstance().quit();
		}else if(cmd == cmdQiut){
			MainMidlet.getInstance().quit();
		}else if(cmd == cmdUpdate){
			update();
		}else if(cmd == cmdUpdateNot){
			this.checkVersion = false;
			MainMidlet.getInstance().display.setCurrent(this);
		}
		
	}
	/**提示框设置*/
	public void showAlert(String content,int alertID){
		alert.setTimeout(Alert.FOREVER);
		alert.setString(content);
		alertState = alertID;
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	public void showUpdateAlert(String content,String updateUrl,int alertID){
		alert.removeAllCommands();
		this.updateUrl = updateUrl;
		switch(alertID){
		case 0:
			alert.addCommand(cmdUpdate);
			alert.addCommand(cmdUpdateNot);
			alert.setTimeout(Alert.FOREVER);
			alert.setString(content);
			break;
		case 1:
			alert.addCommand(cmdUpdate);
			alert.addCommand(cmdNo);
			alert.setTimeout(Alert.FOREVER);
			alert.setString(content);
			break;
		}
		MainMidlet.getInstance().display.setCurrent(alert);
	}
 
	public void update(){
		if(updateUrl.indexOf(".jar") != -1 || updateUrl.indexOf(".jad") != -1){
		try
		{
			
			System.out.println("downloadUrl:"+updateUrl);
    		//调用系统浏览器下载更新
			MainMidlet.getInstance().platformRequest(updateUrl);
		} catch (ConnectionNotFoundException e)
		{
			e.printStackTrace();
		}
		Tools.sleep(1000);
		MainMidlet.getInstance().quit();
	}
	}
}
