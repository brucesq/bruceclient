package cn.hunthawk.j2me.html.protocols;

import cn.hunthawk.j2me.html.ProtocolHandler;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;

//#if polish.usePolishGui
	import de.enough.polish.ui.StyleSheet;
//#endif

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.StreamConnection;

/**
 * Protocol handler class to handle HTTP.
 * 
 * The sent user agent is normally the string defined in the
 * <code>microedition.platform</code> system property. You can overwrite
 * this value by defining the <code>polish.Browser.UserAgent</code> variable
 * in your <code>build.xml</code>.
 */
public class HttpProtocolHandler extends ProtocolHandler
{
	private static final String USER_AGENT = 
	//#if polish.Browser.UserAgent:defined
		//#= 	"${polish.Browser.UserAgent}";
	//#else
				System.getProperty("microedition.platform");
	//#endif

	private HashMap requestProperties;
	private RmsStorage storage;

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 */
	public HttpProtocolHandler()
	{
		this("http",new HashMap() );
	}

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 * 
	 * @param requestProperties the request properties to use for each request
	 */
	public HttpProtocolHandler(HashMap requestProperties)
	{
		this("http", requestProperties );
	}

	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 */
	public HttpProtocolHandler(String protocolName)
	{
		this(protocolName,new HashMap() );
	}
	
	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 * @param requestProperties the request properties to use for each request
	 */
	public HttpProtocolHandler(String protocolName, HashMap requestProperties)
	{
		super( protocolName );	
		String strChannel = null;
		
		if (requestProperties == null) {
			requestProperties = new HashMap();
		}
		this.requestProperties = requestProperties;
		if ( requestProperties.get("User-Agent") == null )
		{
			requestProperties.put("User-Agent", USER_AGENT );
		}
		if ( requestProperties.get("User-XID") == null )
		{
			requestProperties.put("User-XID", USER_AGENT );
		}
		if ( requestProperties.get("User-UID") == null )
		{
			requestProperties.put("User-UID", getMobileID() );
		}
		if ( requestProperties.get("tenfen-reader-client") == null){
			String version = null;
			//#if polish.soft.version:defined
			//#= version = "${polish.soft.version}";
			//#endif
					
			requestProperties.put("tenfen-reader-client", "JavaClient("+USER_AGENT+")/"+version);
			System.out.println("tenfen-reader-client:"+"JavaClient("+USER_AGENT+")/"+version);
		}
    	//#if ChannelID
    	//#ifdef ChannelID:defined
    	//#= strChannel = "${ChannelID}";
		requestProperties.put("User-Channel",strChannel);
        //#endif
		//#endif
    
		if ( requestProperties.get("Accept") == null ) {
			requestProperties.put("Accept", "text/html, text/xml, text/*, image/png, image/*, application/xhtml+xml, */*" );
		}
		if (requestProperties.get("Accept-Language") == null) {
			//#ifdef polish.i18n.useDynamicTranslations
				requestProperties.put("Accept-Language", Locale.LANGUAGE );
			//#else
				String meLocale = System.getProperty("microedition.locale");
				//#if polish.language:defined
					if (meLocale == null) { 
						//#= meLocale = "${polish.language}";
					}
				//#endif
				if (meLocale != null) {
					requestProperties.put("Accept-Language", meLocale );
				} 
			//#endif
		}
		//#if polish.usePolishGui
		if ( requestProperties.get("UA-pixels")  == null && StyleSheet.currentScreen != null)  {
			requestProperties.put("UA-pixels", StyleSheet.currentScreen.getWidth() + "x" + StyleSheet.currentScreen.getHeight() );
		}
		//#endif
		//#if polish.use.line
		//#if polish.http.mobile:defined
		String lineid=null;
		//#= lineid = "${polish.http.mobile}";
		requestProperties.put("x-up-calling-line-id", lineid );
		requestProperties.put("X-Source-ID", "3gwap" );
		//#endif
		//#endif
		
	}

	public static String getMobileID(){
		String imei="";
		imei = System.getProperty("phone.IMEI");
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
			imei = System.getProperty("com.nokia.mid.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.nokia.mid.IMEI");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.sonyericsson.imei");
		}
		if(imei == null || imei.equals("")){
			imei = System.getProperty("com.sonyericsson.IMEI");
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
		return imei;
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.browser.ProtocolHandler#getConnection(java.lang.String)
	 */
	public StreamConnection getConnection(String url) throws IOException
	{
//		String msisdn = SortMemCache(url);
//		if(msisdn!=null)
//		{
//			requestProperties.put("User-UID", msisdn );
//		}
		String requestUrl = url, realHost = null,proxyUrl=null;
		//#if polish.Browser.cmwap
			//#ifdef polish.Browser.proxy:defined
			//#= proxyUrl = "${polish.Browser.proxy}";
				try{Thread.sleep(200);}catch(Exception e){}
				int ind = url.indexOf('/',7);
				if(ind >= 0)
				{
					requestUrl = url.substring(0, 7) +proxyUrl+ url.substring(ind);
					realHost = url.substring(7, ind);
					url = requestUrl;
					if(realHost != null)
					{
						this.requestProperties.put("X-Online-Host",realHost);
					}
				}
            //#endif
        //#endif
        
		return new RedirectHttpConnection(url, this.requestProperties);
	}
}
