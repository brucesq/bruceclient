//#condition polish.usePolishGui
/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2007 - 2008 Michael Koch / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package cn.hunthawk.j2me.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import cn.hunthawk.j2me.html.protocols.HttpProtocolHandler;
import cn.hunthawk.j2me.html.protocols.ResourceProtocolHandler;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.Serializer;
import de.enough.polish.io.StringReader;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
//#if polish.LibraryBuild
import de.enough.polish.ui.FakeContainerCustomItem;
//#endif
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.zip.GZipInputStream;
import de.enough.polish.xml.SimplePullParser;
import de.enough.polish.xml.XmlPullParser;

/**
 * TODO: Write good docs.
 * 
 * polish.Browser.UserAgent
 * polish.Browser.MaxRedirects
 * polish.Browser.Gzip
 * polish.Browser.POISupport
 * polish.Browser.PaintDownloadIndicator
 * 
 * @see HttpProtocolHandler
 * @see ResourceProtocolHandler
 * @see RedirectHttpConnection
 * 
 * @author Michael Koch
 */
public abstract class Browser
//#if polish.LibraryBuild
	extends FakeContainerCustomItem
//#else
  //# extends Container
//#endif
implements Runnable
 {
  private static final String BROKEN_IMAGE = "resource://broken.png";

  private HashMap imageCache = new HashMap();
  private PageCache pageCache = new PageCache();
  protected String currentDocumentBase = null;
  protected HashMap protocolHandlersByProtocol = new HashMap();
  protected HashMap tagHandlersByTag = new HashMap();
  protected ArrayList tagHandlers = new ArrayList();
  
  protected Stack history = new Stack();
  private History vHistory= new History();
  //#if polish.Browser.PaintDownloadIndicator
	  protected Gauge loadingIndicator;
	  private boolean isStoppedWorking;
  //#endif
	  private boolean isChecking;
	    
	    private boolean isDirecting;
  private Thread loadingThread;
  private boolean isRunning;
  private boolean isWorking;
  private boolean isFirst;
  private boolean isCancelRequested;

  private String nextUrl;
  private String nextPostData;
  protected ArrayList imageList;
  protected BrowserListener browserListener;
  
  StringItem stringItme1;
  StringItem stringItme2;
  private boolean isFreshWordSet = false;
  private String procInfo = null;
  private int procValue = 0;
  private boolean isOpenProc = false;
  /**
   * Currently used container for storing parsing results.
   */
  protected Container currentContainer;

  private Command cmdBack;
  private Command cmdCancle;

  private boolean pageCancle = false;
  
private HistoryEntry scheduledHistoryEntry;
	private boolean isLoadSuccessful = true;
	private int loadingTimes = 0;
  /**
   * Creates a new Browser without any protocol handlers, tag handlers or style.
   */
  public Browser()
  {
	  //#style browser?
    this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null );
  }
  
  /**
   * Creates a new Browser without any protocol handler or tag handlers.
   * 
   * @param style the style of this browser
   */
  public Browser( Style style )
  {
    this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null, style );
  }

  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param protocolHandlers the tag handlers
   */
  public Browser( ProtocolHandler[] protocolHandlers )
  {
	  //#if polish.css.style.browser && !polish.LibraryBuild
	  	//#style browser
	  	//# this(protocolHandlers);
	  //#else
	  	this(protocolHandlers, null);
	  //#endif
  }
  
  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param protocolHandlers the tag handlers
   * @param style the style to use for the browser item
   */
  public Browser( ProtocolHandler[] protocolHandlers, Style style )
  {
	  this( (String[])null, (TagHandler[])null, protocolHandlers, style);
  }

  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagNames the names of the tags that the taghandler should handle (this allows to use a single taghandler for several tags)
   * @param tagHandlers the tag handlers
   * @param protocolHandlers the protocol handlers
   */
  public Browser( String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers )
  {
	  //#if polish.css.style.browser && !polish.LibraryBuild
	  	//#style browser
	  	//# this(tagNames, tagHandlers, protocolHandlers);
	  //#else
	  	this(tagNames,tagHandlers, protocolHandlers, null);
	  //#endif
  }

  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagNames the names of the tags that the taghandler should handle (this allows to use a single taghandler for several tags)
   * @param tagHandlers the tag handlers
   * @param protocolHandlers the protocol handlers
   * @param style the style of this browser
   */
  public Browser( String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers, Style style )
  {
    super( true, style );
    if (tagHandlers != null && tagNames != null && tagNames.length == tagHandlers.length) {
	    for (int i = 0; i < tagHandlers.length; i++) {
	    	TagHandler handler = tagHandlers[i];
			addTagHandler(tagNames[i], handler);
		}
    }
    if (protocolHandlers != null) {
    	for (int i = 0; i < protocolHandlers.length; i++) {
			ProtocolHandler handler = protocolHandlers[i];
			addProtocolHandler( handler );
			
		}
    }
    //#if polish.Browser.PaintDownloadIndicator
	    //#style browserDownloadIndicator
	    this.loadingIndicator = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
	//#endif
	//#style browserSplash1
	stringItme1 = new StringItem(null,"精彩内容马上呈现");
	//#style browserSplash1
	stringItme2 = new StringItem(null,"精彩阅读，精彩在沃!");

	this.imageList = new ArrayList();
	this.loadingThread = new Thread( this );
	this.loadingThread.start();
	
  }
  
  /**
   * Instantiates and returns the default tag handlers for "http", "https" and "resource" URLs.
   * 
   * @return new default tag handlers
   * @see HttpProtocolHandler
   * @see ResourceProtocolHandler
   */
  protected static ProtocolHandler[] getDefaultProtocolHandlers() {
	  HashMap httpRequestProperties = new HashMap();

  	//#if polish.Browser.Gzip
 		httpRequestProperties.put("Accept-Encoding", "gzip");
  	//#endif

  	return new ProtocolHandler[] {
  		new HttpProtocolHandler("http", httpRequestProperties),
		new HttpProtocolHandler("https", httpRequestProperties),
		new ResourceProtocolHandler("resource")
  	};  
  }

  public void addTagCommand(String tagName, Command command)
  {
	  TagHandler handler = getTagHandler(tagName);
	  
    if (handler != null )
    {
		  handler.addTagCommand( tagName, command );
	  }
  }
  
  public void addAttributeCommand(String attributeName, String attributeValue, Command command)
  {
    addAttributeCommand(null, attributeName, attributeValue, command);
  }
  
  public void addAttributeCommand(String tagName, String attributeName, String attributeValue, Command command)
  {
	  TagHandler handler = getTagHandler(tagName);
	  
    if (handler != null )
    {
		  handler.addAttributeCommand( tagName, attributeName, attributeValue, command );
	  }
  }

  public void addProtocolHandler(ProtocolHandler handler)
  {
    this.protocolHandlersByProtocol.put(handler.getProtocolName(), handler);
  }
  
  public void addProtocolHandler(String protocolName, ProtocolHandler handler)
  {
    this.protocolHandlersByProtocol.put(protocolName, handler);
  }
  
  protected ProtocolHandler getProtocolHandler(String protocolName)
  {
    return (ProtocolHandler) this.protocolHandlersByProtocol.get(protocolName);
  }

  protected ProtocolHandler getProtocolHandlerForURL(String url)
  throws IOException
  {
	  if (url.length() > 1 && url.charAt(0) == '/') {
		  url = protocolAndHostOf(this.currentDocumentBase) + url;
	  }
	  int pos = url.indexOf(':');
	  if (pos == -1) {
		  throw new IOException("malformed url");
	  }
	  String protocol = url.substring(0, pos);
	  ProtocolHandler handler = (ProtocolHandler) this.protocolHandlersByProtocol.get(protocol);
	  if (handler == null) {
		  throw new IOException("protocol handler not found");
	  }
	  return handler;
  }
  
  public void addTagHandler(String tagName, TagHandler handler)
  {
    this.tagHandlersByTag.put(new TagHandlerKey(tagName), handler);
    if (!this.tagHandlers.contains(handler)) {
    	this.tagHandlers.add(handler);
    }    
  }
  
  public void addTagHandler(String tagName, String attributeName, String attributeValue, TagHandler handler)
  {
    TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);
    
    //#debug
    System.out.println("Browser.addTagHandler: adding key: " + key);
    
    this.tagHandlersByTag.put(key, handler);
    if (!this.tagHandlers.contains(handler)) {
    	this.tagHandlers.add(handler);
    }
  }
  
  public TagHandler getTagHandler(String tagName)
  {
    TagHandlerKey key = new TagHandlerKey(tagName);
    return (TagHandler) this.tagHandlersByTag.get(key);
  }
  
  public TagHandler getTagHandler(String tagName, String attributeName, String attributeValue)
  {
    TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);
    return (TagHandler) this.tagHandlersByTag.get(key);
  }
  
	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param containerStyle the style of the container
	 */
	public void openContainer(Style containerStyle)
	{
		//#debug
		System.out.println("Opening nested container");
		Container previousContainer = this.currentContainer;
		if (containerStyle == null) {
			if (previousContainer != null) {
				containerStyle = previousContainer.getStyle();
			} else {
				containerStyle = getStyle();
			}
		}
		openContainer( new Container( false, containerStyle ) );
	}
	
	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param container the new the container
	 */
	public void openContainer(Container container)
	{
//		Container cont = this.currentContainer;
//		while (cont != null) {
//			System.out.print(" ");
//			if (cont.getParent() instanceof Container) {
//				cont = (Container) cont.getParent();
//			} else {
//				cont = null;
//			}
//		}
		//#debug
		System.out.println("Opening nested container " + container);
		
		Container previousContainer = this.currentContainer;
		if (previousContainer != null) {
			container.setParent( previousContainer );
		} else {
			container.setParent( this );
		}
		//add(container);
		this.currentContainer = container;
	}
	
	//#if polish.LibraryBuild
	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param container the new the container
	 */
	public void openContainer(FakeContainerCustomItem container)
	{
		// ignore
	}
	//#endif
	
	/**
	 * Closes the current container
	 * 
	 * If the current container only contains a single item, that item will be extracted and directly appended using the current container's style.
	 * @return the previous container, if any is known
	 */
	public Container closeContainer() {
		if (this.currentContainer == null) {
			return null;
		}
//		Container cont = this.currentContainer;
//		while (cont != null) {
//			System.out.print(" ");
//			if (cont.getParent() instanceof Container) {
//				cont = (Container) cont.getParent();
//			} else {
//				cont = null;
//			}
//		}
//		System.out.println("closing container " + this.currentContainer + " with size " + this.currentContainer.size() );
		//#debug
		System.out.println("closing container with " + this.currentContainer.size() + " items, previous=" + this.currentContainer.getParent());
		Container current = this.currentContainer;
		Container previousContainer = (Container) current.getParent();
		if (previousContainer == UiAccess.cast(this)) {
			this.currentContainer = null;
		} else {
			this.currentContainer = previousContainer;
		}
		
		//System.out.println("closing container with size " + current.size() + ", 0=" + current.get(0));
		if (current.size() == 1) {
			Item item = current.get(0);
			if (item != null) {
				if (current.getStyle() != null) {
//					System.out.println("cuurent style is not null");
					item.setStyle( current.getStyle() );
				}
				//previousContainer.remove(current);
				add( item );
			}
		} else {
//			System.out.println("add tableItem");
			add(current);
		}
		return previousContainer;
	}
	
	  /**
		 * 
		 */
		private void closeContainers()
		{
			while (this.currentContainer != null) {
				closeContainer();
			}
		}

  
  /**
   * @param parser the parser to read the page from
   */
  private void parsePage(SimplePullParser parser)
  {
    // Clear out all items in the browser.
    clear();
    
    // Clear image cache when visiting a new page.
    this.imageCache.clear();

    // Really free memory.
    System.gc();
    
    parsePartialPage(parser);
    
    Object o = this.currentContainer;
    while ( o != null && o != this ) {
    	//System.out.println("closing container with " + this.currentContainer.size() );
    	closeContainer();
    	o = this.currentContainer;
    }

  }

  /**
   * @param parser the parser to read the page from
   */
  private void parsePartialPage(SimplePullParser parser)
  {
	  HashMap attributeMap = new HashMap();
	while (parser.next() != SimplePullParser.END_DOCUMENT)
    {
      if (parser.getType() == SimplePullParser.START_TAG
          || parser.getType() == SimplePullParser.END_TAG)
      {
        boolean openingTag = parser.getType() == SimplePullParser.START_TAG;

        // #debug
        //System.out.println( "looking for handler for " + parser.getName()  + ", openingTag=" + openingTag );
        attributeMap.clear();
        TagHandler handler = getTagHandler(parser, attributeMap);
        
        if (handler != null)
        {
          // #debug
          //System.out.println("Calling handler: " + parser.getName() + " " + attributeMap);
    	  String styleName = (String) attributeMap.get("class");
    	  Style tagStyle = null;
    	  if (styleName != null) {
    		  tagStyle = StyleSheet.getStyle(styleName);
    	  }
    	  if (tagStyle == null || styleName == null) {
    		  styleName = (String) attributeMap.get("id");
    	  }
    	  if (styleName != null) {
    		  tagStyle = StyleSheet.getStyle(styleName);
    	  }
    	  Container container =  this.currentContainer;
    	  if (container == null) {
    		  container = (Container) ((Object)this);
    	  }
    	  procInfo = "打开页面";
          if(procValue <80){
        	  procValue++;
          }
          if(pageCancle){
        	  break;
          }
          handler.handleTag(container, parser, parser.getName(), openingTag, attributeMap, tagStyle);
        }
        else
        {
        	//#debug
        	System.out.println( "no handler for " + parser.getName() );
        }
      }
      else if (parser.getType() == SimplePullParser.TEXT)
      {
        handleText(parser.getText().trim());
      }
      else
      {
    	  //#debug error
    	  System.out.println("unknown type: " + parser.getType() + ", name=" + parser.getName());
      }
    } // end while (parser.next() != PullParser.END_DOCUMENT)
	
    //#debug
    System.out.println("end of document...");
    System.gc();
  
  }

  private TagHandler getTagHandler(SimplePullParser parser, HashMap attributeMap)
  {
    TagHandlerKey key;
    TagHandler handler = null;
    String name = parser.getName().toLowerCase();
    
    for (int i = 0; i < parser.getAttributeCount(); i++)
    {
      String attributeName = parser.getAttributeName(i).toLowerCase();
      String attributeValue = parser.getAttributeValue(i);
      attributeMap.put(attributeName, attributeValue);
      
      key = new TagHandlerKey(name,
                              attributeName,
                              attributeValue);
      handler = (TagHandler) this.tagHandlersByTag.get(key);
      
      if (handler != null)
      {
        break;
      }
    }
    
    if (handler == null)
    {
      key = new TagHandlerKey(name);
      handler = (TagHandler) this.tagHandlersByTag.get(key);
    }
    return handler;
  }
  
  /**
   * Handles normal text.
   *  
   * @param text the text
   */
  protected abstract void handleText(String text);
  
  

	 /* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#add(de.enough.polish.ui.Item)
	 */
	public void add(Item item)
	{
		if (this.currentContainer != null) {
			this.currentContainer.add(item);
		} else {
			super.add(item);
			
		}
	}
	
	
	//#if polish.LibraryBuild
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#add(de.enough.polish.ui.Item)
	 */
	public void add(javax.microedition.lcdui.Item item)
	{
		// ignore
	}
	//#endif


/**
   * Loads a page from a given <code>Reader</code>.
   * 
   * @param reader the reader to load the page from
   * @throws IOException of an error occurs
   */
  public void loadPage(Reader reader)
    throws IOException
  {
    XmlPullParser xmlReader = new XmlPullParser(reader, false);
    xmlReader.relaxed = true;
    parsePage(xmlReader);
  }

  /**
   * Loads a page from a given <code>Reader</code>.
   * 
   * @param reader the reader to load the page from
   * @throws IOException of an error occurs
   */
  public void loadPartialPage(Reader reader)
    throws IOException
  {
    XmlPullParser xmlReader = new XmlPullParser(reader, false);
    xmlReader.relaxed = true;
    parsePartialPage(xmlReader);
  }

  /**
   * "http://foo.bar.com/baz/blah.html" => "http://foo.bar.com"
   * <p>
   * "resource://baz/blah.html" => "resource://"
   * 
   * @param url the URL to the get protocol and host part from
   * @return the protocol and host part from the given URL
   */
  protected String protocolAndHostOf(String url)
  {
    if ("resource://".regionMatches(true, 0, url, 0, 11))
    {
      return "resource://";
    }
    else if ("http://".regionMatches(true, 0, url, 0, 7))
    {
      int hostStart = url.indexOf("//");
      // figure out what error checking to do here
      hostStart+=2;
      // look for next '/'. If none, assume rest of string is hostname
      int hostEnd = url.indexOf("/", hostStart);
    
      if (hostEnd != -1)
      {
        return url.substring(0, hostEnd);
      }
      else
      {
        return url;
      }
    }
    else
    {
      // unsupported protocol
      return url;
    }
  }

  /**
   * Takes a possibly relative URL, and generate an absolute URL, merging with
   * the current documentbase if needed.
   * 
   * <ol>
   * <li> If URL starts with http:// or resource:// leave it alone
   * <li> If URL starts with '/', prepend document base protocol and host name.
   * <li> Otherwise, it's a relative URL, so prepend current document base and
   * directory path.
   * </ol>
   * 
   * @param url the (possibly relative) URL
   * @return absolute URL
   */
  public String makeAbsoluteURL(String url)
  {
    //#debug debug
    System.out.println("makeAbsoluteURL: currentDocumentBase = " + this.currentDocumentBase);
    // If currentDocumentBase is null (when Local book is loading)  
    if(this.currentDocumentBase == null){
    	return "HtmlImageTagHandler";
    }
    // If no ":", assume it's a relative link, (no protocol),
    // and append current page
    if (url.indexOf("://") != -1)
    {
      return url;
    }
    else if (url.startsWith("/"))
    {
      if ("resource://".regionMatches(true, 0, this.currentDocumentBase, 0, 11))
      {
        // we need to strip a leading slash if it's a local resource, i.e.,
        // "resource://" + "/foo.png" => "resource://foo.png"
        return protocolAndHostOf(this.currentDocumentBase) + url.substring(1);
      }
      else
      {
        // for HTTP, we don't need to strip the leading slash, i.e.,
        // "http://foo.bar.com" + "/foo.png" => "http://foo.bar.com/foo.png"
        return protocolAndHostOf(this.currentDocumentBase) + url;
      }
    } 
    else
    {
      // It's a relative url, so merge it with the current document path:
    	String baseUrl = this.currentDocumentBase;
    	if (baseUrl == null) {
    		return url;
    	} else {
	      String prefix = protocolAndPathOf(baseUrl);
	      
	      if (prefix.endsWith("/"))
	      {
	        return prefix + url;
	      }
	      else
	      {
	        return prefix + "/" + url;
	      }
    	}
    }
  }
  public String getCurrentUrl(){
	  return this.currentDocumentBase;
  }
  public void setCurrentDocumentBase(String documentBase){
	  this.currentDocumentBase = documentBase;
  }
  public void loadPage(String document)
  {
    try
    {
      loadPage(new StringReader(document));
    }
    catch (IOException e)
    {
      // StringReader never throws an IOException.
    }
  }

	/** 
	 * Loads a new HTML page with the specified input stream
	 * @param in the input stream
	 * @throws IOException when the page could not be read or when the input stream is null
	 */
	public void loadPage(InputStream in)
	throws IOException
	{
		loadPage(in, null);
	}

	/** 
	 * Loads a new HTML page with the specified input stream
	 * @param in the input stream
	 * @param encoding the encoding, is ignored when null
	 * @throws IOException when the page could not be read or when the input stream is null or when the specified encoding is not supported
	 */
	public void loadPage(InputStream in, String encoding)
	throws IOException
	{
		if (in == null)
		{
			throw new IOException("no input stream");
		}
		InputStreamReader reader;
		if (encoding == null) {
			reader = new InputStreamReader(in);
		} else {
			reader = new InputStreamReader(in, encoding);
		}
		loadPage(reader);
	}

  private Image loadImageInternal(String url)
  {
	  Image image = (Image) this.imageCache.get(url);

	  if (image == null)
	  {
		  StreamConnection connection = null;
		  InputStream is  = null;
		  if(!pageCancle){
		  try
		  {
			  notifyDownloadStart(url);
			  OpenStreamThread openInputThread = null;
			  for(int i=0;i<3;i++){
				  ProtocolHandler handler = getProtocolHandlerForURL(url);
			        
				  connection = handler.getConnection(url);
				  openInputThread = new OpenStreamThread(connection);
		        	openInputThread.start();
		        	long start = System.currentTimeMillis();
		        	do{
		        		  Thread.sleep(500);
		        		  if(pageCancle){
		        			  break;
		        		  }
		        		  if((System.currentTimeMillis() - start) >10000){
		        			  System.out.println("链接图片,超出设定的时间，丢弃该链接");
		        			  break;
		        		  }
		        	  }while(!openInputThread.isOk());
		        	is = openInputThread.getInputStream();
//				  is = connection.openInputStream();
				  if (is == null) {
					  if(this.pageCancle){//如果是用户主动取消的
						  System.out.println("用户主动取消");
						  break;
					  }else{
						  System.out.println("链接图片第"+(i+1)+"次超时，重新开始");
						  continue;
					  }
					 
				  }else{
					  System.out.println("打开成功");
					  break;
				  }
			  }
			  if(openInputThread != null) openInputThread = null;
			  if (is == null){
				  System.out.println("图片打开失败");
				  return null;
			  }
			  ByteArrayOutputStream bos = new ByteArrayOutputStream();
			  byte[] buf = new byte[1024];
			  int bytesRead;
			  boolean cancle = false;
			  do
			  {
				  bytesRead = is.read(buf);
				  if (bytesRead > 0)
				  {
					  if(pageCancle){
						  cancle = true;
						  break;
					  }
					  bos.write(buf, 0, bytesRead);
				  }
			  }
        while (bytesRead >= 0);
        
        notifyDownloadEnd();
        if(cancle){
        	return null;
        }
        buf = bos.toByteArray();
        
        //#debug
        System.out.println("Image requested: " + url);

        image = Image.createImage(buf, 0, buf.length);
        this.imageCache.put(url, image);
        return image;
      }
      catch (Exception e)
      {
        // TODO: Implement proper error handling.
        
        //#debug error
        System.out.println("Unable to load image " + url + e);
        
        return null;
      } finally {
    	  if (is != null) {
    		  try {
    			  is.close();
    		  } catch (Exception e) {
    			  //#debug error
    			  System.out.println("unable to close inputstream " + e );
    		  }
    	  }
    	  if (connection != null) {
    		  try {
					connection.close();
					// on some Series 40 devices we need to set the connection to null,
					// which is weird, to say the least.  Nokia, anyone?
					connection = null;
    		  } catch (Exception e) {
    			  //#debug error
    			  System.out.println("unable to close connection " + e );
    		  }
    	  }
      }
	}
    }
    
    return image;
  }

  public Image loadImage(String url)
  {
    Image image = loadImageInternal(url);
    
    if (image == null)
    {
      image = loadImageInternal(BROKEN_IMAGE);
    }
    
    if (image == null)
    {
      image = Image.createImage(10, 10);
      Graphics g = image.getGraphics();
      g.setColor(0xFFFFFF);
      g.fillRect(0, 0, 10, 10);
      g.setColor(0xFF0000);
      g.drawLine(0, 0, 10, 10);
      g.drawLine(0, 10, 10, 0);
    }
    
    return image;
  }
  
  public void addImageList(String url)
	{
  	//#debug
  	System.out.println("idx:"+size());
  	imageList.add(new CustomImage(size()-1,url));
	}
  /**
   * "http://foo.bar.com/baz/boo/blah.html" => "http://foo.bar.com/baz/boo"
   * <br>
   " "http://foo.bar.com" => "http://foo.bar.com"
   * <br>
   * "resource://baz/blah.html" => "resource://baz"
   * <br>
   * "resource://blah.html" => "resource://"
   * 
   * @param url the URL to the get protocol and path part from
   * @return the protocol and path part from the given URL
   */
  protected String protocolAndPathOf (String url)
  {
    // 1. Look for query args, or end of string.
    // 2. from there, scan backward for first '/', 
    // 3. cut the string there.
    // figure out what error checking to do here
  
    int end = url.indexOf('?');
    
    if (end == -1)
    {
      end = url.length()-1;
    }
        
    int hostStart = url.indexOf("//");
    // figure out what error checking to do here
    hostStart += 2;
  
    int lastSlash = url.lastIndexOf('/', end);
  
    // RESOURCE urls have no host portion, so return everything between
    // the "resource://" and last slash, if it exists.
    if ("resource://".regionMatches(true, 0, url, 0, 11))
    {
      if ((lastSlash == -1) || (lastSlash <= hostStart))
      {
        return "resource://";
      }

      return url.substring(0, lastSlash);
    }
    else
    {
      if ((lastSlash == -1) || (lastSlash <= hostStart))
      {
        return url;
      }
      
      return url.substring(0, lastSlash);
    }
  }
  
  public boolean handleCommand(Command command)
  {
	  Object[] handlers = this.tagHandlers.getInternalArray();
	  for (int i = 0; i < handlers.length; i++)
	{
		TagHandler handler = (TagHandler) handlers[i];
		if (handler == null) {
			break;
		}
		if (handler.handleCommand(command)) {
			return true;
		}
		
	}
    return super.handleCommand(command);
  }
  
  protected void goImpl(String url, String postData)
  {
	 System.out.println("goImpl");
	 String previousDocumentBase = this.currentDocumentBase;
	 StreamConnection connection = null;
	 InputStream is = null;
//	 notifyPageStart(url);
	 byte [] content = (byte[])pageCache.get(url);
	 if(content != null){
		 procInfo = "缓存页面";
   	     procValue = 50;
		 this.currentDocumentBase = url;
		 InputStreamReader isr = null;
		 is = new ByteArrayInputStream(content);
	     try {
//	    	 System.out.println("get pageCache:"+new String(content,"UTF-8"));
	    	 isr = new InputStreamReader(is,"UTF-8");
	    	 procInfo = "打开页面";
	   	     procValue = 70;
			loadPage(isr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    notifyPageEnd();
	    procInfo = "打开完毕";
  	     procValue = 100;
  	     isOpenProc = false;
  	   if(this.cmdCancle != null && getScreen() != null){
       	getScreen().removeCommand(cmdCancle);
       }
	    if(this.isDirecting)
	    {
	    	this.isChecking = true;
	    }
	    else
	    {
	    	this.isChecking = false;
	    }
	 }
	 else{
		 try
		    {
		      // Throws an exception if no handler found.
		      ProtocolHandler handler = getProtocolHandlerForURL(url);
		      System.out.println("point 1");
		      this.currentDocumentBase = url;
		      procInfo = "正在联网";
        	  procValue = 15;
        	  connection = handler.getConnection(url);
        	  procInfo = "联网成功";
        	  procValue = 24;
		      if (connection != null)
		      {
			    notifyPageStart(url);

			    if (postData != null && connection instanceof HttpConnection) {
			    	HttpConnection httpConnection = (HttpConnection) connection;
			    	httpConnection.setRequestMethod(HttpConnection.POST);
			    	httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    	
				    OutputStream os = connection.openOutputStream();
				    os.write(postData.getBytes());
				    os.close();
				    
			    }
			    procInfo = "准备接收";
	        	procValue = 26;
	        	OpenStreamThread openInputThread = new OpenStreamThread(connection);
	        	openInputThread.start();
	        	long start = System.currentTimeMillis();
	        	do{
	        		  Thread.sleep(500);
	        		  if(pageCancle){
	        			  break;
	        		  }
	        		  if((System.currentTimeMillis() - start) >10000){
	        			  System.out.println("超出设定的时间，丢弃该链接");
	        			  break;
	        		  }
	        	  }while(!openInputThread.isOk());
	        	is = openInputThread.getInputStream();
//	        	openInputThread = null;

	        	if(is == null){
	        		  if(this.pageCancle){//如果是用户主动取消的
	        			  procValue = 100;
					      isOpenProc = false;
					      if(this.cmdCancle != null && getScreen() != null){
					       	getScreen().removeCommand(cmdCancle);
					      }
					      loadPage("获取信息失败,请稍候手动刷新");
					      
					      //将检测打开流的线程，让它安息
//					      synchronized( openInputThread ) {
//				            	openInputThread.;
//				            }
					      System.out.println("线程状态:"+openInputThread.isAlive());
					      this.isLoadSuccessful = true;
					      System.gc();
	        		  }else{//如果是由于程序检测超时而打开失败的话，重新链接
	        			  if(this.loadingTimes < 3){
	        				  System.out.println("第"+(loadingTimes+1)+"次打开流超时，开始启动新的");
	        				  procValue = 100;
						      isOpenProc = false;
						      
						      
//						      synchronized( openInputThread ) {
//					            	openInputThread.wait();
//					          }
						      
						      this.loadingTimes++;
						      
						      this.isLoadSuccessful = false;
						      
//						      if(openInputThread != null){
//						    	  openInputThread.
//						      }
	        			  }else{
	        				  procValue = 100;
						      isOpenProc = false;
						      if(this.cmdCancle != null && getScreen() != null){
						       	getScreen().removeCommand(cmdCancle);
						      }
						      loadPage("获取信息失败,请稍候手动刷新");
						      
						      //将检测打开流的线程，让它安息
//						      synchronized( openInputThread ) {
//					            	openInputThread.wait();
//					            }
//						      
						      this.isLoadSuccessful = true;
	        			  }
	        			  
	        		  }
	        		  openInputThread = null;    
	        	}else{
	        		openInputThread = null;
	        		procInfo = "连接成功";
		        	procValue = 30;
				    String contentEncoding = null;
			    	if (connection instanceof HttpConnection) {
			    		HttpConnection httpConnection = (HttpConnection) connection; 
				    	contentEncoding = httpConnection.getEncoding();
				    	if (contentEncoding == null) {
				    		contentEncoding = httpConnection.getHeaderField("Content-Encoding");
				    	}
			    	}
			    	if (contentEncoding == null) {
			    		contentEncoding = "UTF-8";
			    	}
			    	//#if polish.Browser.Gzip
				  	    try {
			    	    	if (contentEncoding != null && contentEncoding.indexOf("gzip") != -1) {
			    	    		is = new GZipInputStream(is, GZipInputStream.TYPE_GZIP, false);
			    	    		contentEncoding = null;
			    	    	}
				  		}
				  	    catch (IOException e) {
				  	    	//#debug error
				  	    	System.out.println("Unable to use GzipInputStream" + e);
				  		}
			  	    //#endif
				  
				   int ch;
				   ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
				   procInfo = "接收数据";
		           procValue = 35;
				   while((ch = is.read()) != -1) 
				     {
				         bStrm.write(ch);
				     } 
				     content = bStrm.toByteArray();
				     
				     pageCache.put(this.currentDocumentBase,content);     
			  		is = new ByteArrayInputStream(content);
			  		procInfo = "打开页面";
			        procValue = 50;
			        
			    	loadPage(new InputStreamReader(is,contentEncoding));
			    	this.isLoadSuccessful = true;
			    	notifyPageEnd();
			    	if(imageList.size() > 0){
			    		procInfo = "打开图片";
				        procValue = 90;
			    	}else{
			    		procInfo = "打开完毕";
				        procValue = 100;
				        isOpenProc = false;
				        if(this.cmdCancle != null && getScreen() != null){
				        	getScreen().removeCommand(cmdCancle);
				        }
			    	}
	        	}
			    
		    	
		      }else{
		    	  procValue = 100;
			      isOpenProc = false;
			      if(this.cmdCancle != null && getScreen() != null){
			       	getScreen().removeCommand(cmdCancle);
			      }
		      }
		    }
		    catch (Exception e)
		    {
		      //#debug
		      System.out.println("Unable to load page " + url + e );
		      this.currentDocumentBase = previousDocumentBase;
		      notifyPageError(url, e);
		      closeContainers();
		    } finally { 
			  	  if (is != null) {
					  try {
						  is.close();
					  } catch (Exception e) {
						  //#debug error
						  System.out.println("unable to close inputstream " + e );
					  }
				  }
		    	if (connection != null) {
		    		try {
		    			connection.close();
		    		} catch (Exception e) {
		    			//#debug error
		    			System.out.println("Unable to close connection " + e);
		    		}
		    	}
		      	HistoryEntry entry = this.scheduledHistoryEntry;
		      	if (entry != null) {
		      		focus( entry.getFocusedIndex() );
		      		setScrollYOffset( entry.getScrollOffset(), false );
		      		this.scheduledHistoryEntry = null;
		      	}

		    }
		    if(this.isDirecting)
		    {
		    	this.isChecking = true;
		    }
		    else
		    {
		    	this.isChecking = false;
		    }
		 
	 }
    
  }

///////////////////////openInputStream handling thread//////////////
  class OpenStreamThread extends Thread{
	  StreamConnection connection = null;
	  InputStream is = null;;

	  boolean isOk = false;
	  public OpenStreamThread(StreamConnection connection){
		  this.connection = connection;

	  }
	  public void run(){
		  try {
			  
			is = connection.openInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("open error");
//			e.printStackTrace(s;
		} 
		isOk = true;
	  }
	  public boolean isOk(){
		  return this.isOk;
	  }
	  public InputStream getInputStream(){
		  return this.is;
	  }
  }
//////////////// download indicator handling /////////////
  
  
  //#if polish.Browser.PaintDownloadIndicator
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#initContent(int, int)
   */
  protected void initContent(int firstLineWidth, int lineWidth) {
  	super.initContent(firstLineWidth, lineWidth);
	  	// when there is a loading indicator, we need to specify the minmum size:
	  	int width = this.loadingIndicator.getItemWidth( lineWidth, lineWidth );
	  	if (width > this.contentWidth) {
	  		this.contentWidth = width;
	  	}
	  	int height = this.loadingIndicator.itemHeight;
	  	if (height > this.contentHeight) {
	  		this.contentHeight = height;
	  	}
  }
  
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
   */
  protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
  {
    super.paintContent(x, y, leftBorder, rightBorder, g);
    
    if (this.isWorking)
    {
    	int originalY = y;
    	if (this.parent instanceof Container) {
    		y += ((Container)this.parent).getScrollYOffset();
    	}
    	y += this.yOffset;
    	int lineWidth = rightBorder - leftBorder;
      	int liHeight = this.loadingIndicator.getItemHeight( lineWidth, lineWidth );
      	int liLayout = this.loadingIndicator.getLayout();
      	if ( (liLayout & LAYOUT_VCENTER) == LAYOUT_VCENTER ) {
      		y += this.contentHeight>>1 + liHeight>>1;
      	} else if ( (liLayout & LAYOUT_BOTTOM ) == LAYOUT_BOTTOM ) {
      		y += this.contentHeight - liHeight;
      	}
      	this.loadingIndicator.relativeX = x;
      	this.loadingIndicator.relativeY = y - originalY;
      	//System.out.println(">>>>>> download indicator at " + x + ", " + y + ", originalY=" + originalY +", yOffset=" + this.yOffset);
      	this.loadingIndicator.paint(x, y, leftBorder, rightBorder, g);
    }
  }
  
  
  	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
	    if (this.isWorking) {
	    	this.loadingIndicator.animate(currentTime, repaintRegion);
	    } else if (!this.isStoppedWorking) {
	    	this.isStoppedWorking = true;
	    }
	}

  
  //#endif
 
  
	 
////////////////  downloading thread /////////////////
  public void run()
  {
	  // ensure that the user is able to specify the first location before this thread is going to sleep/wait.
	  try {
		Thread.sleep( 100 );
	} catch (InterruptedException e) {
		// ignore
	}
    this.isRunning = true;
    this.isDirecting = false;
    this.isFirst = true;
   
    while (this.isRunning)
    {
    	
    	try {
      if (this.isRunning && this.nextUrl != null)
      {
        this.isWorking = true;
        this.isChecking = true;
        //#if polish.Browser.PaintDownloadIndicator
        	this.isStoppedWorking = false;
        //#endif
        String url = this.nextUrl;
        String postData = this.nextPostData;
        this.nextUrl = null;
        this.nextPostData = null;
          
        if (this.isCancelRequested != true)
        {
            //#if polish.Browser.MemorySaver
        		int size = 50 * 1024;
        		//#if polish.Browser.MemorySaver.Amount:defined
        			//#= size = ${polish.Browser.MemorySaver.Amount};
        		//#endif
        		byte[] memorySaver = new byte[size];
        	//#endif

            try {
            	while(this.isChecking)
            	{
//            		System.out.println("isChecking:isDirecting:"+isDirecting+"isFrist:"+isFirst);
              		if(this.isDirecting && this.isFirst)
              		{
              			this.isDirecting = true;
              			this.isFirst = false;
              			continue;
              		}
              		else if(this.isDirecting)
              		{
              			url = this.nextUrl;
              			postData = this.nextPostData;
              			this.isDirecting = false;
              			
              		}
              		procInfo = "准备联网";
              		procValue = 10;
              		goImpl(url, postData);

              		
              		if(imageList.size()>0)
                    {
                    	LoadImageList();
                    }
            	}
        	}
            catch (OutOfMemoryError e) {
            	//#if polish.Browser.MemorySaver
            		memorySaver = null;
            		System.gc();
            	//#endif

            	// Signal stopped parsing.
            	//#style browserText?
            	StringItem item = new StringItem(null, "parsing stopped");
            	add(item);
            } finally {
            	//#if polish.Browser.MemorySaver
	            	if (memorySaver != null) {
	        			memorySaver = null;
	            	}
            	//#endif
	            
            }
        }
        if(this.isLoadSuccessful){
        	this.isWorking = false;
            this.isDirecting = false;
            this.isChecking = false;
        }
        
        repaint();
      }
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
      if (this.isCancelRequested == true)
      {
        this.isWorking = false;
        
        repaint();
        this.isCancelRequested = false;
        this.nextUrl = null;
        this.nextPostData = null;
        loadPage("Request canceled");
      }
        
      try
      {
        
//    	  System.out.println("isWorking:"+this.isWorking);
        if(this.isLoadSuccessful){//如果页面获取正常线程暂停
//        	System.out.println("线程执行完毕，进入休息室");
        	this.isWorking = false;
        	synchronized( this.loadingThread ) {
            	this.loadingThread.wait();
            }
        }else{
        	//重新启动下载线程
//        	System.out.println("重新开始请求！");
        	Thread.sleep(500);//休息一会，马上开始
        	
        	this.nextUrl = this.currentDocumentBase;
        	this.isDirecting = false;
  		    procInfo = "准备请求";
  		    procValue = 5;
  		    isOpenProc = true;
  		    pageCancle = false;
  		    repaint();
        }
        
//        System.out.println("loadingThread over!");
        
         
      }
      catch (InterruptedException ie)
      {
//        interrupt();
      }
    } // end while(isRunning)
  } // end run()
  
  protected void schedule(String url, String postData)
  {
    this.nextUrl = url;
    this.nextPostData = postData;
    if(this.currentDocumentBase != null){
    	vHistory.push(this.currentDocumentBase);
    }
    synchronized( this.loadingThread ) {
    	this.loadingThread.notify();
    }
  }
      
  public void cancel()
  {
    this.isCancelRequested = true;
  }
  public void redirect(String url)
  {
	  this.imageCache.clear();
	  
	  System.gc();
	  //#debug
	  System.out.println("Browser: going to [" + url + "]" );
//	  if (this.currentDocumentBase != null)
//	  {
//		  this.history.push( new HistoryEntry( this.currentDocumentBase, getFocusedIndex(), getScrollYOffset() ) );
//		  if (this.cmdBack != null && this.history.size() == 1 && getScreen() != null) {
//			  getScreen().addCommand(this.cmdBack);
//		  }
//	  }
	  this.isDirecting = true;
	  
	  schedule(url, null);
  }
  public synchronized void requestStop()
  {
    this.isRunning = false;
    synchronized( this.loadingThread ) {
    	this.loadingThread.notify();
    }
  }

  public boolean isRunning()
  {
    return this.isRunning;
  }
  
  public boolean isCanceled()
  {
    return this.isCancelRequested;
  }
  
  public boolean isWorking()
  {
    return this.isWorking;
  }
  
  
  //////////////////////////// History //////////////////////////////
  
  /**
   * Schedules the given URL for loading.
   * @param url the URL that should be loaded
   */
  public void go(String url)
  {

	  clear();

	 
	  //#debug
	  System.out.println("Browser: going to [" + url + "]" );
	  if(url.indexOf("pg=d") != -1){
		  
//	  		System.out.println("check system set,word set---------------->");
	  		Vector v = (Vector)RmsOpt.readRMS("settings");
	  		if(!v.isEmpty()){
	  			int [] setting = (int[])v.elementAt(0);
	  			int value = setting[0];
	  			
	  			String wordset = "ps=1000";
	  			switch(value){
	  			case 0:
	  				wordset = "ps=500";
	  				break;
	  			case 1:
	  				wordset = "ps=1000";
	  				break;
	  			case 2:
	  				wordset = "ps=2000";
	  				break;
	  			}

	  			if(url.indexOf("ps=") != -1){
	  				if(url.indexOf("ps=1000") != -1){
		  				url = TextUtil.replace(url, "ps=1000", wordset);
		  			}else if(url.indexOf("ps=500") != -1){
		  				url = TextUtil.replace(url, "ps=500", wordset);
		  			}else if(url.indexOf("ps=2000") != -1){
		  				url = TextUtil.replace(url, "ps=2000", wordset);
		  			}
	  			}else{
	  				url = url+"&"+wordset;
	  			}

	  		}
	  	}
	  //保存客户端运行时间
	  long runningtime = System.currentTimeMillis() - tempData.startTime;
	  runningtime = runningtime/1000;
	  Vector vtime = (Vector)RmsOpt.readRMS("runningtime");
	  if(vtime.isEmpty()){
		  vtime = new Vector();
		  vtime.addElement(String.valueOf(runningtime));
		  RmsOpt.saveRMS(vtime, "runningtime");
		  tempData.startTime = System.currentTimeMillis();
	  }else{
		  String lasttime = (String)vtime.elementAt(0);
		  long lasttimes = Long.parseLong(lasttime);
		  runningtime +=lasttimes;
		  vtime = new Vector();
		  vtime.addElement(String.valueOf(runningtime));
		  RmsOpt.saveRMS(vtime, "runningtime");
		  tempData.startTime = System.currentTimeMillis();
	  }
		  if (this.currentDocumentBase != null)
		  {
			  this.history.push( new HistoryEntry( this.currentDocumentBase, getFocusedIndex(), getScrollYOffset() ) );
			  if (this.cmdBack != null && this.history.size() == 1 && getScreen() != null) {
				  getScreen().addCommand(this.cmdBack);
			  }
		  }
		  this.isDirecting = false;
		  procInfo = "准备请求";
		  procValue = 5;
		  isOpenProc = true;
		  this.loadingTimes = 0;
		  pageCancle = false;
		  
		  if(this.cmdCancle != null && getScreen() != null){
			  getScreen().addCommand(cmdCancle);
		  }
		  if(this.getScreen() != null)
	    		this.getScreen().focus(this);
		  
		  this.add(stringItme1);
			 
		  this.add(stringItme2);
		  schedule(url, null);
		  
	  
	  
  }
  public void setFreshWordSet(boolean i){
	  this.isFreshWordSet = i;
  }
  private String getCurrentPageID(){
		String url = this.getCurrentUrl();
		int pos = url.indexOf("pn=");
		url  = url.substring(pos);
		if(url.indexOf("&") != -1){
			url = url.substring(0,url.indexOf("&"));
//			System.out.println("page current1:"+url);
			return url;
			
		}else{
			url = url.substring(0);
//			System.out.println("page current2:"+url);
			return url;
		}
	}
  /**
   * Schedules the given URL for loading with HTTP POST data.
   * @param url the URL that should be loaded
   * @param postData the data to be sent via HTTP POST
   */
  public void go(String url, String postData)
  {
	  //#debug
	  System.out.println("Browser: going to [" + url + "]" );
      if (this.currentDocumentBase != null)
      {
    	  this.history.push(this.currentDocumentBase);
    	  if (this.cmdBack != null && this.history.size() == 1 && getScreen() != null) {
    		  getScreen().addCommand(this.cmdBack);
    	  }
      }
      this.isDirecting = false;
      schedule(url, postData);
  }
  
  /**
   * Schedules the given history document for loading.
   * 
   * @param historySteps the steps that should go back, e.g. 1 for the last page that has been shown
   */
  public void go(int historySteps)
  {
    HistoryEntry entry = null;
    
    while (historySteps > 0 && this.history.size() > 0)
    {
      entry = (HistoryEntry) this.history.pop();
      historySteps--;
    }
    
    if (entry != null)
    {
    	this.scheduledHistoryEntry = entry;
        schedule(entry.getUrl(), null);
        if (this.history.size() == 0 && this.cmdBack != null && getScreen() != null) {
        	getScreen().removeCommand(this.cmdBack);
        }
    }
  }
  /**
   * get browser history url
   */
  public String getHistoryUrl(int historySteps){
	  return vHistory.getUrl(historySteps);
  }
  public int getHistorySize(){
	  return vHistory.getSize();
  }
  public void followLink()
  {
    Item item = getFocusedItem();
    String href = (String) item.getAttribute("href");
    
    if (href != null)
    {
      go(makeAbsoluteURL(href));
    }
  }
  
  /**
   * Sets the back command for this browser.
   * The back command will be appended to the parent screen when the browser can go back and it will be removed when the browser cannot got back anymore.
   * @param cmdBack the back command - set to null to remove it completely
   */
  public void setBackCommand(Command cmdBack)
  {
	  if (this.cmdBack != null && getScreen() != null) {
		  getScreen().removeCommand(this.cmdBack);
	  }
	  this.cmdBack = cmdBack;
  }
  
  public void setCancleCommand(Command cmdCancle){
	  if(this.cmdCancle != null && getScreen() != null){
		  getScreen().removeCommand(cmdCancle);
	  }
	  this.cmdCancle = cmdCancle;
  }
  
  /**
   * Goes back one history step.
   * 
   * @return true when the browser has a previous document in its history
   * @see #go(int)
   */
  public boolean goBack()
  {
	  if (this.history.size() > 0) {
		  go(1);
		  return true;
	  } else {
		  return false;
	  }
  }
  
  /**
   * Checks if the browser can go back
   * @return true when there is a known previous document
   * @see #goBack()
   */
  public boolean canGoBack()
  {
    return this.history.size() > 0;
  }

  /**
   * Clears the history
   * @see #goBack()
   * @see #go(int)
   */
  public void clearHistory()
  {
    this.history.removeAllElements();
    this.imageCache.clear();
    //clear();
    this.currentDocumentBase = null;
    if (this.cmdBack != null && getScreen() != null) {
    	getScreen().removeCommand(this.cmdBack);
    }
  }
  /**
   * 
   * @param index
   * @return the position y in the browser
   */
  public int getItemPositionY(int index){
	  int height = -1;
	  Item [] items = this.getItems();
	  if(index < items.length){
		  height = items[index].relativeY;
	  }
	  return height;
  }
  /**
   * 
   * @param item the item
   * @return height the position y in the browser
   */
  public int getItemPositionY(Item item){

	  int index = this.getPosition(item);
	  return getItemPositionY(index);
  }
  /**
   * 判断当前焦点item是否在屏幕显示
   */
  public boolean isInScreenContent(Item focusedItem){
	  if(focusedItem == null) return false;
	  int startY = Math.abs(this.getScrollYOffset());
	  int endY = Math.abs(this.getScrollYOffset() - this.getScreen().getAvailableHeight());
	  int itemY = focusedItem.relativeY;
	  if(itemY < startY || itemY > endY){
		  return false;
	  }else{
		  return true; 
	  }
  }
  /**
   * 获得当前屏幕显示的最后一个item
   */
  public int getLastItemInScreenIndex(){
	  if(focusedItem == null){
		  return -1;
	  }
	  int endY = Math.abs(this.getScrollYOffset() - this.getScreen().getAvailableHeight()+this.focusedItem.itemHeight);
	  boolean skip = false;
	  int index = binarySearchLastItem(this.getItems(),endY);
	  index--;
	
	  if(index != -1){
		  Item items[] = this.getItems();
		  String href = null;
		  do{
			 href= (String)items[index].getAttribute("href");
			if(href != null){
				break;
			}
			index--;
		
			if(index == 0){
				skip = true;
				break;
			}
		  }while(href == null);
		  if(skip){
			  return -1;
		  }else{
			  return index;
		  }
		  
	  }else{
		  return -1;
	  }
  }
  /**
   * 获得当前屏幕显示的第一个item
   */
  public int getFristItemInScreenIndex(){
	  int startY = Math.abs(this.getScrollYOffset());
	  boolean skip = false;
	  int index = binarySearchFristItem(this.getItems(),startY);

	  if(index != -1){
		  Item items[] = this.getItems();
		  String href = null;
		  do{
			 href= (String)items[index].getAttribute("href");
			if(href != null){
				break;
			}
			index++;
			if(index == items.length){
//				System.out.println("can't find focused item util last item");
				skip = true;
				break;
			}
		  }while(href == null);
		  
		  if(skip){
			  return -1;
		  }else{
			  return index;
		  }
		  
	  }else{
		  return -1;
	  }
	  
  }
  /**
   * 查找最近的连接item
   */
  public int getClosestHrefItemIndex(int direction,int startIndex){
	  Item items[] = this.getItems();
	  String href = null;
	  do{
		  if(direction == Canvas.DOWN){
			  
			  startIndex++;
			
		  }else if(direction == Canvas.UP){
			  startIndex--;
			  
		  }
		  if(startIndex >= items.length){
//			  System.out.println("out of index");
			  startIndex--;
			  break;
		  }
		  if(startIndex < 0){
//			  System.out.println("out of index");
			  startIndex++;
			  break;
		  }
		  href = (String)items[startIndex].getAttribute("href");
		  if(href != null){
			  break;
		  }
	  }while(href == null);
	  
	  return startIndex;
  }
  /**
   * 二分查找最后一个算法
   */
   public int binarySearchLastItem(Item[]a, int start) {
	   if (a.length==0) return -1;

 	   int startPos = 0;
 	   int endPos = a.length-1;
 	   int m = (startPos + endPos) / 2;
 	   while(startPos <= endPos){
// 		 System.out.println("search m:"+m);
 	     if(a[m-1].relativeY<=start && a[m].relativeY>=start) return m;
 	     else if(start > a[m].relativeY) {
 	     	startPos = m + 1;
 	     }
 	     else if(start < a[m].relativeY) {
 	     	endPos = m -1;
 	     }
 	     m = (startPos + endPos) / 2;
 	   }
 	   return -1;
	 }
   /**
    * 二分查找第一个焦点位置算法
    */
    public int binarySearchFristItem(Item[]a, int start) {
 	   if (a.length==0) return -1;

 	   int startPos = 0;
 	   int endPos = a.length-1;
 	   int m = (startPos + endPos) / 2;
 	   int j=0;
 	   while(startPos <= endPos){
// 		 System.out.println("while:"+j);
 		 j++;
 	     if(a[m].relativeY<=start && a[m+1].relativeY>=start) return m;
 	     else if(start > a[m].relativeY) {
 	     	startPos = m + 1;
 	     }
 	     else if(start < a[m].relativeY) {
 	     	endPos = m -1;
 	     }
 	     m = (startPos + endPos) / 2;
 	   }
 	   return -1;
 	 }
    /**
     * refresh 删除保存的页面，重新请求
     */
    public void refresh(String url){
    	pageCache.delete(url);
    	System.out.println("isWorking:"+isWorking);
    	if(!isWorking)
    		go(url);
    	
    }
    public void clearPageCache(){
    	pageCache.deleteAll();
    }
    public boolean isFristFocusedItem(){
    	int index = this.getFocusedIndex();
    	int frist = getClosestHrefItemIndex(Canvas.DOWN,-1);
    	if(index == frist){
//    		System.out.println("isFrist:true");
    		return true;
    	}else{
//    		System.out.println("isFrist:false");
    		return false;
    	}
    	
    }
  /**
   * notifyPageError
   * @param url 
   * @param e
   */
  protected void notifyPageError(String url, Exception e)
  {
	  if (this.browserListener != null) {
		  this.browserListener.notifyPageError(url, e);
	  }
  }
  /**
   * 
   * @param url
   */
  protected void notifyPageStart(String url)
  {
	  if (this.browserListener != null) {
		  this.browserListener.notifyPageStart(url);
	  }
  }
  
  protected void notifyPageEnd()
  {
	  if (this.browserListener != null) {
		  this.browserListener.notifyPageEnd();
	  }
  }

  protected void notifyDownloadStart(String url)
  {
	  if (this.browserListener != null) {
		  this.browserListener.notifyDownloadStart(url);
	  }
  }
  
  protected void notifyDownloadEnd()
  {
	  if (this.browserListener != null) {
		  this.browserListener.notifyDownloadEnd();
	  }
  }

  public BrowserListener getBrowserListener()
  {
	return this.browserListener;
  }

  public void setBrowserListener(BrowserListener browserListener)
  {
	this.browserListener = browserListener;
  }
 
  public void LoadImageList()
  {
	  
	for(int i=0;i<imageList.size();i++)
	{
		CustomImage customimage = (CustomImage)imageList.get(i);
		Image image = loadImageInternal(customimage.getUrl());
		if (image != null) 
		{
			
			ImageItem item = new ImageItem(null, image, Item.LAYOUT_DEFAULT, "");
			if (style != null)
			{
				item.setStyle(style);
			}
			this.set(customimage.getIndex(), item);
			try {
				RecordStore rs = RecordStore.openRecordStore("imageCache", true);
				if(rs.getNumRecords() == 0){
					ImageCache cache  = new ImageCache();
					cache.setRecordID(2);
					cache.setUrl(customimage.getUrl());
					Vector vCache = new Vector();
					vCache.addElement(cache);
				    byte [] map = RmsOpt.Object2Byte(vCache);
				    rs.addRecord(map, 0, map.length);
				    byte [] imageCache = RmsOpt.Object2Byte(image);
				    rs.addRecord(imageCache, 0, imageCache.length);
					
				}else{
					byte [] imageCache = RmsOpt.Object2Byte(image);
					rs.addRecord(imageCache, 0, imageCache.length);
					byte []p = rs.getRecord(1);
					int num  = rs.getNumRecords();
					DataInputStream in = new DataInputStream( new ByteArrayInputStream( p ));
					Vector vCache = (Vector)Serializer.deserialize(in);
					ImageCache cache = new ImageCache();
					cache.setRecordID(num);
					cache.setUrl(customimage.getUrl());
					vCache.addElement(cache);
					byte [] map = RmsOpt.Object2Byte(vCache);
					rs.setRecord(1, map, 0, map.length);
					
				}
				rs.closeRecordStore();
			} catch (RecordStoreFullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordStoreNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    if(this.isDirecting)
    {
    	this.isChecking = true;
    }
    else
    {
    	this.isChecking = false;
    }
    procInfo = "打开完毕";
    procValue = 100;
    isOpenProc = false;
    if(this.cmdCancle != null && getScreen() != null){
    	getScreen().removeCommand(cmdCancle);
    }
	imageList.clear();
  }

  public int getProcValue(){
	  return this.procValue;
  }
  public String getProcInfo(){
	  return this.procInfo;
  }
  public boolean isOpenProc(){
	  return this.isOpenProc;
  }
  public void cancelRequest(){
	  pageCancle = true;
	  
  }
}
