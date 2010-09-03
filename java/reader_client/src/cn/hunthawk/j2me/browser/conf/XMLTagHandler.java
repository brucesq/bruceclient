package cn.hunthawk.j2me.browser.conf;

import java.util.Vector;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.ChannelItem;
import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.bo.InitControler;
import cn.hunthawk.j2me.bo.ItemControler;
import cn.hunthawk.j2me.html.Browser;
import cn.hunthawk.j2me.html.TagHandler;
import cn.hunthawk.j2me.ui.screen.SplashScreen;
import cn.hunthawk.j2me.util.RmsOpt;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.SimplePullParser;


public class XMLTagHandler extends TagHandler
{

  protected XMLBrowser browser;

  private Vector vchannel;
  private Vector channels;
  
  public static InitControler initcontrol;
  public static HomeControler homecontrol;
  public static ItemControler itemcontroler;
  public static ChannelItem channelItem;
  public static int v = 0;

  //版本控制文件
  public static final String TAG_INIT = "init";
  public static final String TAG_NAME= "name";
  public static final String TAG_SERVICE = "service";
  public static final String TAG_DESC = "description";
  public static final String TAG_MIME = "mime";
  public static final String TAG_SOFT = "soft";
  public static final String TAG_SOFT_URL = "softurl";
  public static final String TAG_INVITATION = "invitation";
  public static final String TAG_PROP = "prop";
  public static final String TAG_PROP_URL = "propurl";
  public static final String TAG_HOME = "home";
  public static final String TAG_HOME_URL = "homeurl";
  
  //桌面控制文件
  public static final String TAG_RSS = "rss";
  public static final String TAG_CHANNEL = "channel";
  public static final String TAG_TITLE = "title";
  public static final String TAG_LINK = "link";
  public static final String TAG_WELCOMEPAGE = "welcomepage";
  public static final String TAG_HELP = "help";
  public static final String TAG_ADDFAVORITETD = "addfavoritetd";
  public static final String TAG_ADDBOOKMARKTD = "addbookmarktd";
  public static final String TAG_ITEM = "item";
  public static final String TAG_ICON = "icon";
  public static final String TAG_FOCUSEDICON = "focusedicon";
  public static final String TAG_VIP = "vip";
  public static final String TAG_CONTENTSTD = "contentstd";
  public static final String TAG_COMMENTTD = "commenttd";
  public static final String TAG_ITEMS = "items";
  public static final String TAG_TAB = "tab";

  

  //版本升级检测
  public static final String TAG_SOFTUPDATE = "softupdate";
  public static final String TAG_CLIENTINFO = "clientinfo";
  public static final String TAG_URL = "url";
  public static final String TAG_VERSION = "version";
  public static final String TAG_MUSTUPDATE = "mustupdate";
  public static final String TAG_MSG = "msg";
  
  private String downloadUrl = null;
  private String version = null;
  private String mustUpdate = null;
  private String msg = null;
  public XMLTagHandler() {

  }
  
  public void register(Browser parent)
  {
    this.browser = (XMLBrowser) parent;


    parent.addTagHandler(TAG_INIT, this);
    parent.addTagHandler(TAG_NAME, this);
    parent.addTagHandler(TAG_SERVICE, this);
    parent.addTagHandler(TAG_DESC, this);
    parent.addTagHandler(TAG_MIME, this);
    parent.addTagHandler(TAG_PROP_URL, this);
    parent.addTagHandler(TAG_SOFT, this);
    parent.addTagHandler(TAG_PROP, this);
    parent.addTagHandler(TAG_SOFT_URL, this);
    parent.addTagHandler(TAG_HOME, this);
    parent.addTagHandler(TAG_HOME_URL, this);
    parent.addTagHandler(TAG_INVITATION, this);
    
    parent.addTagHandler(TAG_RSS, this);
    parent.addTagHandler(TAG_CHANNEL, this);
    parent.addTagHandler(TAG_ITEM, this);
    parent.addTagHandler(TAG_TITLE, this);
    parent.addTagHandler(TAG_LINK, this);
    parent.addTagHandler(TAG_WELCOMEPAGE, this);
    parent.addTagHandler(TAG_HELP, this);
    parent.addTagHandler(TAG_ADDFAVORITETD, this);
    parent.addTagHandler(TAG_ADDBOOKMARKTD, this);
    parent.addTagHandler(TAG_ICON, this);
    parent.addTagHandler(TAG_FOCUSEDICON, this);
    parent.addTagHandler(TAG_VIP, this);

    parent.addTagHandler(TAG_CONTENTSTD, this);
    parent.addTagHandler(TAG_COMMENTTD, this);
    parent.addTagHandler(TAG_ITEMS, this);
    parent.addTagHandler(TAG_TAB, this);
    
    parent.addTagHandler(TAG_SOFTUPDATE, this);
    parent.addTagHandler(TAG_CLIENTINFO, this);
    parent.addTagHandler(TAG_URL, this);
    parent.addTagHandler(TAG_VERSION, this);
    parent.addTagHandler(TAG_MUSTUPDATE, this);
    parent.addTagHandler(TAG_MSG, this);
	



   
  }

  public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style)
  {
	

	if(!opening)
	{
		if(TAG_ITEM.equals(tagName)){
			if(itemcontroler!=null){
				vchannel.addElement(itemcontroler);
			}
		}
		else if(TAG_TAB.equals(tagName)){
			if(channelItem != null){
				channels.addElement(channelItem);
			}
		}
		else if(TAG_ITEMS.equals(tagName)){
			if(channels!=null){
				itemcontroler.setChannels(channels);
			}
		}
		else if(TAG_CHANNEL.equals(tagName))
		{
			if(!vchannel.isEmpty()){
				homecontrol.setVItem(vchannel);
			}
		}
		else if(TAG_INIT.equals(tagName))
		{
			Vector v = new Vector();
			v.addElement(initcontrol);
			//#debug
			System.out.println("save initcontrol");
			RmsOpt.saveRMS(v, "initcontrol");
		}
		else if(TAG_RSS.equals(tagName)){
			Vector v = new Vector();
			v.addElement(homecontrol);
			RmsOpt.saveRMS(v, "homecontrol");
		}else if(TAG_SOFTUPDATE.equals(tagName)){
			if(version != null && mustUpdate != null && downloadUrl != null){
				//#debug
				System.out.println("check version:"+version);
				//#debug
				System.out.println("check update:"+mustUpdate);
				String curVersion = null;
				//#= curVersion = "${app.version}";
				//#debug
				System.out.println("check curVersion:"+curVersion);
				if(Integer.parseInt(version) > Integer.parseInt(curVersion)){
					System.out.println("dd");
					if(mustUpdate.equals("1")){
						System.out.println("dd1");
						SplashScreen.getInstance().showUpdateAlert(msg,downloadUrl,1);
					}else if(mustUpdate.equals("0")){
						SplashScreen.getInstance().showUpdateAlert(msg,downloadUrl, 0);
					}
				}else{
					SplashScreen.getInstance().setCheckVersionTag(false);
				}
			}
		
		}
		
	}
	else
	{
		if(TAG_INIT.equals(tagName))
		{
			//#debug
			System.out.println("init start");
			v = 1;
			initcontrol = new InitControler();
			
		}
		else if(TAG_RSS.equals(tagName)){
			//#debug
			System.out.println("file type: rss,version:"+parser.getAttributeValue("version"));
			
		}
		else if(TAG_NAME.equals(tagName))
		{
			parser.next();
			if(v == 1){
				initcontrol.setName(parser.getText().trim());
			}
		}
		
		else if(TAG_SERVICE.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			
		}
		else if(TAG_DESC.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			
		}
		else if(TAG_MIME.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			
		}
		
		else if(TAG_SOFT.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setSoftVersion(Integer.parseInt(parser.getText().trim()));
			}
		}
		else if(TAG_SOFT_URL.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setSoftURL(parser.getText().trim());
			}
		}
		else if(TAG_INVITATION.equals(tagName)){
			parser.next();
			//System.out.println(parser.getText().trim());
			if(v == 1){
				initcontrol.setInvitation(parser.getText().trim());
			}
		}
		else if(TAG_PROP.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setPropVersion(Integer.parseInt(parser.getText().trim()));
			}
		}
		else if(TAG_PROP_URL.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setPropURL(parser.getText().trim());
			}
		}
		else if(TAG_HOME.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setHomeVersion(Integer.parseInt(parser.getText().trim()));
			}
		}
		else if(TAG_HOME_URL.equals(tagName))
		{
			parser.next();
			//System.out.println(parser.getText().trim());
			if (v == 1)
			{
				initcontrol.setHomeURL(parser.getText().trim());
			}
		}
		
		 //桌面配置
		else if(TAG_CHANNEL.equals(tagName)){
			homecontrol = new HomeControler();
			vchannel = new Vector();
//			System.out.println("config start");
			v = 2;
		}
		else if(TAG_ITEMS.equals(tagName)){
			channels = new Vector();
		}
		else if(TAG_TITLE.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setTitle(parser.getText());
			}else if(v == 3){
				itemcontroler.setItemName(parser.getText());
				
//				System.out.println("tag:"+tagName+" open:"+opening+"title:"+parser.getText());
			}else if(v == 4){
				channelItem.setName(parser.getText());
			}
		}
		else if(TAG_LINK.equals(tagName)){
			parser.next();
			if(v == 3){
				itemcontroler.setLink(parser.getText().trim());
			}else if(v == 4){
				channelItem.setLink(parser.getText());
			}
		}
		else if(TAG_WELCOMEPAGE.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setLink(parser.getText().trim());
			}
		}
		else if(TAG_ADDFAVORITETD.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setAddfavoritetd(parser.getText());
			}
		}
		else if(TAG_ADDBOOKMARKTD.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setAddbookmarktd(parser.getText());
			}
		}
		else if(TAG_HELP.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setHelp(parser.getText().trim());
			}
		}
		
		else if(TAG_COMMENTTD.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setCommenttd(parser.getText().trim());
			}
		}
		else if(TAG_CONTENTSTD.equals(tagName)){
			parser.next();
			if(v == 2){
				homecontrol.setContentstd(parser.getText().trim());
			}
		}
		else if(TAG_ITEM.equals(tagName)){
			itemcontroler = new ItemControler();
			v = 3;
		}
		else if(TAG_ICON.equals(tagName)){
			parser.next();
			if(v == 3){
//				Image image =null;
				String url = this.browser.makeAbsoluteURL(parser.getText().trim());
//				System.out.println("url:"+url);
//				if(url.startsWith("Resources://"))
//				{
//					url = url.substring(("Resources:/").length());
//					try
//					{
//						image = Image.createImage(url);
//					} catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//				else
//				{
//					//从网络上下载item图标
//					image = this.browser.loadImage(url);
//				}
				
				itemcontroler.setIcon(url);
			}
		}
		else if(TAG_FOCUSEDICON.equals(tagName)){
			parser.next();
			if(v == 3){
//				Image image =null;
				String url = this.browser.makeAbsoluteURL(parser.getText().trim());
//				if(url.startsWith("Resources://"))
//				{
//					url = url.substring(("Resources:/").length());
//					try
//					{
//						image = Image.createImage(url);
//					} catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//				else
//				{
//					//从网络上下载item图标
//					image = this.browser.loadImage(url);
//				}
				
				itemcontroler.setFocusedIcon(url);
			}
		}
		else if(TAG_VIP.equals(tagName)){
			parser.next();
			if(v == 3){
				itemcontroler.setVip(parser.getText());
			}
		}else if(TAG_TAB.equals(tagName)){
			channelItem = new ChannelItem();
			v = 4;
		}
		else if(TAG_SOFTUPDATE.equals(tagName)){
				//#debug
				System.out.println("start soft update");
			}
			else if(TAG_NAME.equals(tagName)){
				parser.next();
				//#debug
				System.out.println("jar-Name:"+parser.getText());
			}
			else if(TAG_URL.equals(tagName)){
				parser.next();
				//#debug
				System.out.println("url:"+parser.getText());
				downloadUrl = parser.getText();
			}else if(TAG_MUSTUPDATE.equals(tagName)){
				parser.next();
				mustUpdate = parser.getText();
				//#debug
				System.out.println("mustupdate:"+mustUpdate);
			}else if(TAG_VERSION.equals(tagName)){
				parser.next();
				version = parser.getText();
				//#debug
				System.out.println("version:"+version);
			}else if(TAG_MSG.equals(tagName)){
				parser.next();
				msg = parser.getText();
				//#debug
				System.out.println("msg:"+msg);
			}		 
		 
	}
    return false;
  }

  public void add(Item item)
  {
	this.browser.add(item);
  }

}
