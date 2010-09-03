package cn.hunthawk.j2me.control;

import java.io.IOException;
import java.util.Vector;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.InitControler;
import cn.hunthawk.j2me.browser.conf.XMLBrowser;
import cn.hunthawk.j2me.html.BrowserListener;
import cn.hunthawk.j2me.ui.screen.MainScreen;
import cn.hunthawk.j2me.ui.screen.SplashScreen;
import cn.hunthawk.j2me.ui.screen.WelcomeScreen;
import cn.hunthawk.j2me.util.ControlerOpt;
import cn.hunthawk.j2me.util.RmsOpt;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.StringItem;

public class UserBrowserListener implements BrowserListener{

	public XMLBrowser xmlBrowser;
	public static int loadcontrol = 0;
	private InitControler control;
	private Gauge loadingIndicator;
	private StringItem msg;
	int value = 0;
	public UserBrowserListener(XMLBrowser wmlBrowser,Gauge loadingIndicator,StringItem msg)
	{
		this.xmlBrowser = wmlBrowser;
		this.loadingIndicator = loadingIndicator;
		this.msg = msg;
	}
	
	public void notifyDownloadEnd(){
		
	}

	public void notifyDownloadStart(String url){
		
	}

	public void notifyPageEnd()
	{
		
		value+=10;
		setValue(value);
		//#debug
		System.out.println("value4"+value);
		if(loadcontrol==1)
		{
			Vector vector = new Vector();
			int i=0;
			do
			{
				
				try
				{					
					RmsStorage storage = new RmsStorage();
					vector = (Vector) storage.read("homecontrol");
					Thread.sleep( 100 );
					i++;
					
				} 
				catch (IOException e){}
				catch (InterruptedException e){}
			}while(vector.size()< 1 && i<5);
			value+=10;
			setValue(value);
			//#debug
			System.out.println("value5"+value);
			Vector v = (Vector)RmsOpt.readRMS("welcome");
			if(v.isEmpty()){
//				MainMidlet.getInstance().display.setCurrent(WelcomeScreen.getInstance());
				WelcomeScreen.getInstance().init();
			}else{
				String set = (String)v.elementAt(0);
//				System.out.println("set:"+set);
				if(set.equals("yes")){
//					MainMidlet.getInstance().display.setCurrent(WelcomeScreen.getInstance());
					WelcomeScreen.getInstance().init();
				}
			}
			
				value+=20;
				setValue(value);
				//#debug
				System.out.println("value6"+value);
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				SplashScreen.splashscreen = null;
				System.gc();
				value+=10;
				setValue(value);
				//#debug
				System.out.println("value7"+value);
				
			
		}
	
	}

	public void notifyPageError(String url, Exception e)
	{
//		System.out.println("notifyPageError:"+url);
	}

	public void notifyPageStart(String url)
	{
		value+=10;
		setValue(value);
		//#debug
		System.out.println("value1"+value+"#url"+url);
		
			control = (InitControler) ControlerOpt.getInitControler(RmsOpt.readRMS("initcontrol"));;
			
			//#debug
			System.out.println("value2"+value);
			
			
			if(control!=null && control.getHomeURL().equals(url))
			{
				loadcontrol = 1;
				value+=10;
				setValue(value);
				//#debug
				System.out.println("value3"+value);
				
			}
		
	}
	
	public void setValue(int values){
		loadingIndicator.setValue(values);
		float tem = (float)values/(float)1.2;
		values = (int)tem;
		//#debug
		System.out.println("userBrowserlistener gauge values :" + values);
		if(loadcontrol == 1){
			msg.setText("初始化动态数据.. "+String.valueOf(values)+"%");
		}else{
			msg.setText("正在进入沃书城.. "+String.valueOf(values)+"%");
		}
		
		System.out.print(String.valueOf(values)+"%");
	}
	
	
}
