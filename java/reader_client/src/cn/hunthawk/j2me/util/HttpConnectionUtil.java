package cn.hunthawk.j2me.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.html.protocols.RedirectHttpConnection;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;

public class HttpConnectionUtil extends Thread implements HttpConnectionListener{
	
	private  String  USERAGENT = "";
	private  String  USERID    = "";
	
	private String requestUrl = null;
	private String realUrl = null;
	private String content = null;
	private int responseCode = 0;
	private HashMap requestProperties;
	private HttpConnection httpConn;
	private boolean isOver = false;
	HttpConnectionListener httpListener;
	private int percent = 0;
	private String percentInfo = "";
	private boolean isTimeOut = false;
	public HttpConnectionUtil(String url){
		getPlatform();
		isOver = false;
		percent = 10;
		percentInfo = "开始请求";
		if (requestProperties == null) {
			requestProperties = new HashMap();
		}
		this.requestUrl = url;
		requestUrl = requestUrl.trim();
		realUrl = requestUrl;
		
		
		
		this.requestProperties.put("User-Agent",USERAGENT);
		this.requestProperties.put("User-XID",USERAGENT);
		this.requestProperties.put("User-UID",USERID);
		
	    	
	    //#if polish.use.line
		//#if polish.http.mobile:defined
		String lineid=null;
		//#= lineid = "${polish.http.mobile}";
		requestProperties.put("x-up-calling-line-id", lineid );
		requestProperties.put("X-Source-ID", "3gwap" );
		//#endif
		//#endif   	
		
	    isOver = false;
		percent = 20;
		
	}
	
	public HttpConnectionUtil(String url,int start,int end){
		getPlatform();
		url = url.trim();
		if (requestProperties == null) {
			requestProperties = new HashMap();
		}
		this.requestUrl = url;
		requestUrl = requestUrl.trim();
		realUrl = requestUrl;
		
		
		this.requestProperties.put("User-Agent",USERAGENT);
		this.requestProperties.put("User-XID",USERAGENT);
		this.requestProperties.put("User-UID",USERID);
		
	    if(start >=0 && end >=0 && (end - start) > 0){
    		this.requestProperties.put("Range", "bytes="+start+"-"+end);
	    }
	    //#if polish.use.line
		//#if polish.http.mobile:defined
		String lineid=null;
		//#= lineid = "${polish.http.mobile}";
		requestProperties.put("x-up-calling-line-id", lineid );
		requestProperties.put("X-Source-ID", "3gwap" );
		//#endif
		//#endif
	}
	public void run(){
		TimeOutCheck toc = null;
		try {
			isOver = false;
			percent = 25;
			percentInfo = "准备网络连接";
			if(realUrl.indexOf("runningtime") != -1){
				//#debug
				System.out.println("runningtime request start!");
				this.httpConn = (HttpConnection) Connector.open(realUrl, Connector.READ_WRITE, true);
				//#debug
				System.out.println("runningtime request end!");
				if (this.requestProperties != null)
				{
					Object[] keys = this.requestProperties.keys();

					if (keys != null)
					{
						for (int i = 0; i < keys.length; i++)
						{
							this.httpConn.setRequestProperty((String) keys[i],
									(String) this.requestProperties.get(keys[i]));
							
						}
						
					}
				}
			}else{
				this.httpConn = new RedirectHttpConnection(realUrl, this.requestProperties);
			}
			
			isOver = false;
			percent = 35;
			percentInfo = "正在网络连接";
			toc = new TimeOutCheck(10000);
			toc.start();
			isOver = false;
			percent = 60;
			percentInfo = "正在处理数据";
			responseCode = this.httpConn.getResponseCode();
			isOver = true;
			percent = 90;
			percentInfo = "操作完成";
			if(!isTimeOut){
				notifyHttpConnectionCreated(responseCode);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(this.responseCode != 999){
				isOver = true;
				percent = 90;
				percentInfo = "操作完成";
				notifyHttpConnectionError(-1,"网络连接异常");
			}else{
				isOver = true;
			}
			
			
		} finally{
			
			if(toc != null){
				toc = null;
			}
			System.gc();
		}
	}
	public boolean isConnOver(){
		return this.isOver;
	}
	public int getPercent(){
		return this.percent;
	}
	public String getPercentInfo(){
		return this.percentInfo;
	}
	public HttpConnection getCurrentConnection(){

		return this.httpConn;
		
	}
	/**
	 * get the text content of this connection
	 * */
	public String getContent(){
		
		content = "网络请求失败";
		if(this.httpConn != null){
			if(responseCode == 200){
				ByteArrayOutputStream bStream = null;
				InputStream is =null;
				try{
					bStream = new ByteArrayOutputStream();
					is = this.httpConn.openInputStream();
					int ch;
					while((ch = is.read()) != -1){
						bStream.write(ch);
					}
					byte[] data = bStream.toByteArray();
					content = new String(data,"UTF-8");
					content = content.substring(0, content.indexOf("<br/>"));
				}catch(IOException e){}
				finally{
					if(is != null){
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(bStream != null){
						try {
							bStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					System.gc();
				}
			}
					
		}
		return content;
	}
	
	public int getResponseCodes(){
		return responseCode;
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
		
		public TimeOutCheck(int length) { 
			
			m_length = length; 
			m_elapsed = 0; 
			isTimeOut = false;
			//#debug
			System.out.println("checking 0");
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
				//#debug
				System.out.println("checking 1");
				synchronized (this) { 
	
					m_elapsed += m_rate; 
					//#debug
					System.out.println("checking 1:"+m_elapsed+" m_length:"+m_length);
					if (m_elapsed > m_length ){ //isConnActive 为全局变量 
						//#debug
						System.out.println("out!");
						timeout(); 
						break; 
					}else if(isOver){
						break;
					}
				} 
	
			} 
	    } 

		public void timeout() { 
			//#debug
			System.out.println("timeout");
			try {
				if(realUrl.indexOf("runningtime") == -1){
					httpConn.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//#debug
			System.out.println("timeout2");
			httpConn = null;
			isOver = true;
			responseCode = 999;
			isTimeOut = true;
			notifyHttpConnectionTimeOut();
		} 
		
	}

	
	public void notifyHttpConnectionError(int code, String msg) {
		// TODO Auto-generated method stub
		if(httpListener != null)
			httpListener.notifyHttpConnectionError(code, msg);
	}

	
	public void notifyHttpConnectionCreated(int code){
		if(httpListener != null)
			httpListener.notifyHttpConnectionCreated(code);
	}
	
	public void notifyHttpConnectionStart(String url) {
		// TODO Auto-generated method stub
		if(httpListener != null)
			httpListener.notifyHttpConnectionStart(url);
	}
	
	public void notifyHttpConnectionTimeOut(){
		if(httpListener != null){
			httpListener.notifyHttpConnectionTimeOut();
		}
	}
	public void setHttpConnectionListener(HttpConnectionListener httpListener){
		this.httpListener = httpListener;
	}
	
	private void getPlatform(){
		USERAGENT = System.getProperty("microedition.platform");
		
		USERID = this.getMobileID();
		
		if(USERAGENT.equals("j2me")){
			USERAGENT = " ";
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
}
