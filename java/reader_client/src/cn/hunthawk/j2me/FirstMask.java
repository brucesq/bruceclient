package cn.hunthawk.j2me;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import cn.hunthawk.j2me.ui.screen.SplashScreen;

import de.enough.polish.util.TextUtil;
/**
 * 这个类的目的是封装最开始的网络连接。
 * 由于采用cmwap和cmnet两种连接方式 第一次连接的处理不同，
 * 而且容易产生混乱，所以利用这个类来封装第一次。
 * 这样后面的连接就可以采用相似的方式，连接设置的细节问题可以是透明的
 * 封装在一个方法里即可。
 * 
 * @author zhou mingming
 * @version 1.0.01 
 */
public class FirstMask extends Thread{


	private  String  xmlUrl    = null;
	private  boolean isOk      = false;
	private  boolean isWeb     = true;
	private  String  USERAGENT = "";
	private  String  USERID    = "";
	private  String  proxyUrl  = "10.0.0.172:80";
	private HttpConnection htc = null;
	private TimeOutCheck toc = null;
	private int ok = 999;
	public FirstMask(String url){
		this.xmlUrl = url;
		getPlatform();

	}
	
	
	public void run() {
		isOk = false;
		//#debug
		System.out.println("firstmask url: "+ this.xmlUrl);
		
		if(this.xmlUrl == null
				|| this.xmlUrl.trim().equals("") 
				|| ( !this.xmlUrl.startsWith("http://") && this.xmlUrl.length() < 11 ) )
		{
			//#debug 
			System.out.println("first url format err");
			return;
		}
		SplashScreen.getInstance().setShowInfo("测试网络中...");
		htc = this.getConnection(this.xmlUrl);
		//#debug 
		System.out.println("conn ok");
		if(htc != null){
			try {
				toc = new TimeOutCheck(60000);
				toc.start();
				ok = htc.getResponseCode();	
				
				//#debug 
				System.out.println("ok:"+ok);
				if(ok >= 500 ){
					//#debug 
					System.out.println("server error");
					isWeb = false;
				}else if(ok >= 400){
					//#debug
					System.out.println("page err");
					isWeb = false;
				}else if(ok >= 300){
					//#debug
					System.out.println("location redirected");
				}else if(ok >= 200 ){
					//#debug
					System.out.println("connect ok");
					//#debug
					System.out.println("isWeb:"+isWeb);
				}else{
					//#debug
					System.out.println(" this responndcode is : " + ok);
				}
			} catch (IOException e) {
				//#debug
				System.out.println("find exception: "+e.getMessage());
				//#debug
				e.printStackTrace();
				isWeb = false;
			}finally{
				try {
					htc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				htc = null;
			}
			//#debug
			System.out.println("## first connection over" );
			isOk = true;
		}else{
			//#debug
			System.out.println("first httpconn is null. ");
			isWeb = false;
			
		}
		
	}
	
	private HttpConnection getConnection(String url){
		url = url.trim();
		String requestUrl = url, realHost = null ;		
		
		//#ifdef polish.Browser.proxy:defined
		//#= proxyUrl = "${polish.Browser.proxy}";
	    	int ind = url.indexOf('/',7);
	    	if(ind >= 0)
	    	{
	    		requestUrl = url.substring(0, 7) +proxyUrl+ url.substring(ind);
	    		realHost = url.substring(7, ind);
	    	}
	    //#endif
		
	    HttpConnection conn = null;
	    try{
	    	//#debug
	    	System.out.println("requestUrl:"+requestUrl);
	    	conn = (HttpConnection)Connector.open(requestUrl);

	    	//#debug
	    	System.out.println("conn overed!");
	    	if(realHost != null)
	    	{
	    		conn.setRequestProperty("X-Online-Host", realHost);
	    	}
	    	conn.setRequestProperty("User-Agent",USERAGENT);
	    	conn.setRequestProperty("User-XID",USERAGENT);
	    	conn.setRequestProperty("User-UID",USERID);
			//#if polish.use.line
			//#if polish.http.mobile:defined
			String lineid=null;
			//#= lineid = "${polish.http.mobile}";
			conn.setRequestProperty("x-up-calling-line-id", lineid );
//			conn.setRequestProperty("X-Source-ID", "3gwap" );
			//#endif
			//#endif
	    }
	    catch(Exception e)
	    {
	    	//#debug
	    	System.out.println("connect error " + e.getMessage());
	    	handleResult(0);
	    	conn = null;
	    	
	    }
	    return conn;
	}
	

	private void getPlatform(){
		USERAGENT = System.getProperty("microedition.platform");
		
		USERID = this.getMobileID();
		
		if(USERAGENT.equals("j2me")){
			USERAGENT = " ";
		}
		
		if(proxyUrl == null){
			USERAGENT = "wtk";
		}
	}
	
	private String getMobileID(){
		String imei="";
		if(imei == null || imei.equals("")){
			imei = System.getProperty("IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("phone.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("phone.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.nokia.mid.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.nokia.mid.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.nokia.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.nokia.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.sonyericsson.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.sonyericsson.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.samsung.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.samsung.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.motorola.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.motorola.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.siemens.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.siemens.IMEI");
		}
		if(imei == null){
			imei="000000000000000";
		}
		if(imei != null)
		{
			if(imei.startsWith("IMEI"))
			{
				imei = imei.substring(4);
			}
			imei = (TextUtil.replace(imei,"-","")).trim();
		}
		System.out.println("IMEI:"+imei);
		return imei;
	}
	
	public void closeConnection(){
		try {
			if(htc != null)
				htc.close();
			htc = null;
			System.gc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * check httpconnection time out
	 * 
	 * */
	class TimeOutCheck extends Thread{
		
		/** 每个多少毫秒检测一次 */ 
		protected int m_rate = 500; 

		/** 超时时间长度毫秒计算 */ 
		private int m_length; 

		/** 已经运行的时间 */ 
		private int m_elapsed; 
		
		private boolean isTimeOut = false;
		public TimeOutCheck(int length) { 
			
			m_length = length; 
			m_elapsed = 0; 
			
		} 

		public synchronized void reset() { 
			m_elapsed = 0; 
		} 
		
		public synchronized void setTimeOut() 
		{ 
			m_elapsed = m_length+1; 
		} 
		
		public void run() { 
			for (;;) { 
				
				try { 
					Thread.sleep(m_rate); 
				} catch (InterruptedException ioe) { 
					continue; 
				} 
	
				synchronized (this) { 
	
					m_elapsed += m_rate; 
					//#debug
					System.out.println("isOk:"+isOk+"m_elapsed:"+m_elapsed);
					
					
					if(isOk){
//						processResponse();
						isTimeOut = false;
						break;
					}
					if (m_elapsed > m_length ) { //isConnActive 为全局变量 
//						timeout(); 
						isTimeOut = true;
						break; 
					}
				} 
	
			} 
			
			if(isTimeOut){
				timeout();
			}else{
				processResponse();
			}
			
	    } 

		public void timeout() { 
			//#debug
			System.out.println("timeout");
//			closeConnection();
			handleResult(1);
		} 
		public void processResponse() { 
			//#debug
			System.out.println("processResponse");
//			closeConnection();	
			handleResult(2);
		} 
		
	}
	
	public void handleResult(int state){
		//#debug
		System.out.println("processResponse2");
		SplashScreen.getInstance().switchWebState(state,ok);
	}
}
