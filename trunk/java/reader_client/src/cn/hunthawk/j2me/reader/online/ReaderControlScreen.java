package cn.hunthawk.j2me.reader.online;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.bo.LocalBookContents;
import cn.hunthawk.j2me.bo.LocalBookHeaderInfo;
import cn.hunthawk.j2me.bo.LocalBookMark;
import cn.hunthawk.j2me.bo.LocalBookUserNote;
import cn.hunthawk.j2me.browser.conf.HtmlImageTagHandler;
import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.control.ReaderControlListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.ui.item.ItemContents;
import cn.hunthawk.j2me.ui.screen.BrowserScreen;
import cn.hunthawk.j2me.ui.screen.DownloadedBookScreen;
import cn.hunthawk.j2me.ui.screen.FeeScreen;
import cn.hunthawk.j2me.ui.screen.InviteScreen1;
import cn.hunthawk.j2me.ui.screen.MainScreen;
import cn.hunthawk.j2me.ui.screen.SystemSettingScreen;
import cn.hunthawk.j2me.ui.screen.TopScreen;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;

public class ReaderControlScreen extends TabbedForm 
implements CommandListener{
	public static ReaderControlScreen readerControlScreen = null;
	
	public static final int BOOK_ONLINE = 0;
	public static final int BOOK_LOCAL = 1;
	private int bookType = 0;
	private Command cmdCancle = new Command("取消",Command.BACK,1);
	private Command cmdReturn = new Command(Locale.get("cmd.return"),Command.BACK,2);

	private Command cmdRefresh = new Command(Locale.get("cmd.refresh"),Command.ITEM,2);
	private Command cmdSet = new Command(Locale.get("cmd.set1"),Command.ITEM,7);
	private Command cmdHelp = new Command(Locale.get("cmd.help1"),Command.ITEM,8);
	private Command cmdIndex =new Command(Locale.get("cmd.index"),Command.ITEM,9);
	
	private Command cmdContinue = new Command("接上次阅读",Command.ITEM,2);
	private Command cmdBookMark = new Command("本书书签",Command.ITEM,3);
	private Command cmdStartRead = new Command("开始阅读",Command.ITEM,1);
	private Command cmdSelect = new Command("进        入",Command.ITEM,1);
	
	private Command cmdAddFa = new Command("添加收藏",Command.ITEM,3);
	private Command cmdComment = new Command("发表评论",Command.ITEM,3);
	private Command cmdInvite = new Command("推       荐",Command.ITEM,4);
	private Command cmdOk = new Command("确定",Command.ITEM,1);
	private Command cmdNo = new Command("否",Command.BACK,1);
	private Alert alert;
	
	private Vector items_online = null;
	private Vector items_local = null;
	
	private LocalBookContents lbc = null;
	
	private LocalBook lb = null;
	private ItemContents chapterItem= null;
	private HtmlBrowser htmlBrowser[] = new HtmlBrowser[3];
	private static String tabName[] = {"简介","目录","评论"};
	
	private int tabCount = 3;
	private boolean localContents = true;
	
	private boolean initCMD = true;
	private String bookIntroUrl = null;
	private String bookContentsUrl = null;
	private String bookCommentUrl = null;
	private String readUrl = null;
	
	private String addFavoriteTD = null;
	private String commentTD = null;
	private String contentsTD = null;
	private int scrollBarHeight = 0;
	//#ifdef polish.debugEnabled
	Command showLogCmd = new Command( Locale.get("cmd.showLog"), Command.ITEM, 9 );
	Command cmdExit1 = new Command(Locale.get("cmd.exit"), Command.EXIT, 10 );
	//#endif
	private int entry = -1;
	
	private HttpConnectionUtil hcu = null;
	public ReaderControlScreen(){
		//#style contentsScreen
		super("图书封面-简介",tabName,null);
		//#ifdef polish.debugEnabled
		this.addCommand( this.showLogCmd );
		this.addCommand( this.cmdExit1 );
		this.setCommandListener(this);
		//#endif
		for(int i=0;i<3;i++){
			//#style browser
			htmlBrowser[i] = new HtmlBrowser();
			htmlBrowser[i].setCancleCommand(cmdCancle);
			
			this.append(i,htmlBrowser[i]);
		}
		new HtmlImageTagHandler(this.htmlBrowser[0].getTagHandler("html")).register(this.htmlBrowser[0]);

		htmlBrowser[2].setSupressTextStyle();
		htmlBrowser[2].setBrowserListener(new ReaderControlListener(htmlBrowser[2]));
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);	
		this.setCommandListener(this);
		
		Vector v = (Vector)RmsOpt.readRMS("homecontrol");
		if(!v.isEmpty()){
			HomeControler home = (HomeControler)v.elementAt(0);
			if(home != null){
				commentTD = home.getCommenttd();
				contentsTD = home.getContentstd();
				addFavoriteTD = home.getAddfavoritetd();
			}
		}
		
		this.setActiveTab(0, true);
	}
	public static ReaderControlScreen getInstance(){
		if(readerControlScreen == null){
			readerControlScreen = new ReaderControlScreen();
		}
		return readerControlScreen;
	}

	public void switchTab(int i){
		setActiveTab(i);
		switch(i){
		case 0:
			this.setTitle("图书封面-简介");
			if(isUpdate(i)){
				initIntroduction();
			}
			break;
		case 1:
			this.setTitle("图书封面-目录");
			if(isUpdate(i)){
				initContents();
				
			}
			break;
		case 2:
			switch(bookType){
			case 0:
				this.setTitle("图书封面-评论");
				break;
			case 1:
				this.setTitle("图书封面-笔记");
				break;
			}
			if(isUpdate(i)){
				initComments();
				
			}
			break;
		}
	}
	
	public boolean isUpdate(int i){
		boolean isupdate = true;
		switch(i){
		case 0:
			if(htmlBrowser[0].getItems().length != 0){
				isupdate = false;
			}else{
				isupdate = true;
			}
			break;
		case 1:
			switch(bookType){
			case 0:
				if(htmlBrowser[1].getCurrentUrl() == null){
					isupdate = true;
				}else{
					String contents = htmlBrowser[0].getCurrentUrl()+"&td="+contentsTD;
					if(contents.equals(htmlBrowser[1].getCurrentUrl())){
						isupdate = false;
					}else{
						if(contents.indexOf("pg=r") != -1){
							isupdate = true;
						}else{
							isupdate = false;
						}
						
					}
				}
				break;
			case 1:
				isupdate = localContents;
				break;
			}
			
			break;
		case 2:
			if(htmlBrowser[2].getCurrentUrl() == null){
				isupdate = true;
			}else{
				String contents = htmlBrowser[0].getCurrentUrl()+"&td="+commentTD;
				if(contents.equals(htmlBrowser[2].getCurrentUrl())){
					isupdate = false;
				}else{
					if(contents.indexOf("pg=r") != -1){
						isupdate = true;
					}else{
						isupdate = false;
					}
				}
			}
			break;
		}
		
		return isupdate;
	}
	public void initComments(){
		switch(bookType){
		case BOOK_LOCAL:
			this.deleteAll(2);
//			System.out.println("userNotes");
			
			LocalBookHeaderInfo lbhi = lb.getBookInfo().getHeader();
			String packageID = String.valueOf(lbhi.getPackageID());
			Vector notes = (Vector)RmsOpt.readRMS("usernote");

			if(!notes.isEmpty()){
				int length = notes.size();
				StringItem item = null;
				int index = 0;
				for(int i=0;i<length;i++){
					LocalBookUserNote lbun = (LocalBookUserNote)notes.elementAt(i);
					if(lbun.getPackageID().equals(packageID)){
						//#style stringItembook
						item = new StringItem(null,++index+":"+lbun.getNote()+"\n"+lbun.getDate()+"\n"+"----------------");
						this.append(2, item);
					}
				}
			}
			break;
		case BOOK_ONLINE:
			bookCommentUrl = htmlBrowser[0].getCurrentUrl()+"&td="+commentTD;
			htmlBrowser[2].go(bookCommentUrl);
			break;
		}
		
	}
	public void initIntroduction(){
		switch(bookType){
		case BOOK_ONLINE:
			htmlBrowser[0].go(bookIntroUrl);
//			htmlBrowser[0].go("resource://search.html");
			break;
		case BOOK_LOCAL:
			
			items_local = lb.getBookInfo().getContents();
			
			for(int i=0;i<items_local.size();i++){
				lbc = (LocalBookContents)items_local.elementAt(i);
				if(lbc.getChapterPlayOrder().equals("1")){
					try {
//						System.out.println("html file name :"+lbc.getChapterName());
						htmlBrowser[0].loadPage(getReader(lb,lbc.getChapterSrc()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			break;
			
		}
		
	}
	public void initContents(){
		switch(bookType){
		case BOOK_ONLINE:
			bookContentsUrl = htmlBrowser[0].getCurrentUrl()+"&td="+contentsTD;
			htmlBrowser[1].go(bookContentsUrl);
			break;
		case BOOK_LOCAL:
			
			items_local = lb.getBookInfo().getContents();
			System.out.println("Book local!");
			int size = items_local.size();
			for(int i=0;i<size;i++){
				lbc = (LocalBookContents)items_local.elementAt(i);
				chapterItem = new ItemContents(lbc.getChapterName(),i,BOOK_LOCAL);
				this.append(1,chapterItem);
			}
			this.focus(0);
			localContents = false;
			break;
		}
		
	}
	public void setBase(String url){
		htmlBrowser[0].setCurrentDocumentBase(url);
	}
	public void init(int type,String parent,String start){
		clear();
		
		this.bookType = type;
		if(type == 0){
			bookIntroUrl = parent;
			//
			this.removeAllCommands();
			this.addCommand(cmdRefresh);
			this.addCommand(cmdReturn);
			this.addCommand(cmdSet);
			this.addCommand(cmdHelp);
			this.addCommand(cmdIndex);
			this.addCommand(cmdSelect);
			this.addCommand(cmdAddFa);
			this.addCommand(cmdComment);
			this.addCommand(cmdInvite);
			this.setText(2, "评论");
		}else if(type == 1){
//			System.out.println("delete 1");
			this.deleteAll(1);
			this.removeAllCommands();
			this.addCommand(cmdStartRead);
//			this.addCommand(cmdContinue);
			this.addCommand(cmdBookMark);
			this.addCommand(cmdReturn);
			this.setText(2, "笔记");
		}
		switchTab(0);
		
	}
	public void setEntry(int entry){
		this.entry = entry;
	}
	public void setLocalBook(LocalBook lb){
		this.lb = lb;
		tempData.currentLocalBook = lb;
		if(htmlBrowser[0] != null){
			htmlBrowser[0].setCurrentDocumentBase(null);
		}
	}
	protected boolean handleKeyPressed(int keyCode, int gameAction) 
	{
		int i = this.getActiveTab();
//		System.out.println("ReaderControler");
		if(gameAction == Canvas.RIGHT)
		{
			if(!htmlBrowser[i].isOpenProc()){
				if(i == (tabCount-1))
				{
					switchTab(0);
				}
				else
				{
					switchTab(i+1);
				}
			}
			
			
		}
		else if(gameAction == Canvas.LEFT)
		{
			if(!htmlBrowser[i].isOpenProc()){
				if(i == 0 )
				{
					switchTab(tabCount-1);
				}
				else
				{
					switchTab(i-1);
				}
			}
			
				
		}
		else if(gameAction == Canvas.DOWN){
			switch(bookType){
			case 0:
				if(i == 2){
					htmlBrowser[2].Down();
					
				}else{
					boolean isDown = true;
					
					  int index = htmlBrowser[i].getFocusedIndex();
					  if(index == -1){
						  htmlBrowser[i].focusClosestItem(0);
						  return false;
					  }
					  do{
						  index++;
						  if(index > htmlBrowser[i].getItemSize()){
//							  int height =-(htmlBrowser[i].getContentHeight() - this.getAvailableHeight()+21);
//							  this.setScrollYOffset(height, false);
							  isDown = false;
							  break;
						  }
					  }while(!htmlBrowser[i].focus(index));
					  if(isDown){
						  htmlBrowser[i].scroll(0, htmlBrowser[i].getFocusedItem());
					  }
				}
				
				break;
			case 1:
				if(i == 0){
					if(htmlBrowser[0].itemHeight > this.getAvailableHeight()){
						if(htmlBrowser[0].getScrollYOffset() > -(htmlBrowser[0].getScrollBarMaxOffset())){
							System.out.println("down。。。。");
							int height = this.getScrollYOffset()-30;
							System.out.println("height:"+height);
							System.out.println("scrooll:"+htmlBrowser[0].getScrollBarMaxOffset());
							if(height < -htmlBrowser[0].getScrollBarMaxOffset()){
								height = -htmlBrowser[0].getScrollBarMaxOffset()-10;
							}
							htmlBrowser[0].setScrollYOffset(height,false);
						}
					}
				}else{
					super.handleKeyPressed(keyCode, gameAction);
				}
				
				break;
			}
			 
		}
		else if(gameAction == Canvas.UP){
			switch(bookType){
			case 0:
				if(i == 2){
					htmlBrowser[2].up();
					
				}else{
					 boolean top = true;
					  int index = htmlBrowser[i].getFocusedIndex();
					  if(index == -1) return false;
					  do{
						  index--;
						  if(index < 0){
							  htmlBrowser[i].setScrollYOffset(0, false);
							  top = false;
							  break;
						  }
					  }while(!htmlBrowser[i].focus(index));
					  if(top){
						  htmlBrowser[i].scroll(0, htmlBrowser[i].getFocusedItem());
					  }
				}
				
				break;
			case 1:
				if(i == 0){
					if(htmlBrowser[0].getScrollYOffset() < 0){
						int height = this.getScrollYOffset()+30;					
						  
						  this.setScrollYOffset(height,false);
					}
				}else{
					super.handleKeyPressed(keyCode, gameAction);
				}
				
				break;
			}	 
		}
		
		else if(gameAction == Canvas.FIRE){
			int index = this.getActiveTab();
			switch(bookType){
			case BOOK_ONLINE:
				StringItem anchor = (StringItem)htmlBrowser[index].getFocusedItem();
				String href = (String)anchor.getAttribute("href");
				
				String type = (String)anchor.getAttribute("type");
				String name = anchor.getText();
//				System.out.println("type:"+type+"href:"+href);
				if(type == null){
					return htmlBrowser[i].handleKeyPressed(keyCode, gameAction);
				}
				if(type.equals("link")){
					href = htmlBrowser[index].makeAbsoluteURL(href);
					if(href.endsWith(".ueb")){
						Vector v = htmlBrowser[index].getMeta();
						String fileName = null;
						int j= v.size();
						for(int k=0;k<j;k++){
							StringItem meta = (StringItem)v.elementAt(k);
							if(meta.getLabel().equals("uebName")){
								fileName = meta.getText();
							}
						}
						Vector vv = (Vector)RmsOpt.readRMS("downloadbook");
						int k=vv.size();
						boolean exist = false;
						for(int l=0;l<k;l++){
							DownloadBook db = (DownloadBook)vv.elementAt(l);
							if(fileName.equals(db.getUebName())){
								DownloadedBookScreen.getInstance().switchTab(0);
								DownloadedBookScreen.getInstance().setEntry(0);
								MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
								exist = true;
								break;
								
							}
						}
						if(!exist){
//							
							ImageItem imageItem = (ImageItem)this.htmlBrowser[index].get(0);
							Image image = null;
							if(imageItem != null){
								image = imageItem.getImage();
								image = Tools.zoomImage(true, image, 46, 60);
							}else{
								try {//默认
									image = Image.createImage("/cover75.png");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							DownloadedBookScreen.getInstance().initDownloading(fileName,href,image);
							DownloadedBookScreen.getInstance().setEntry(0);
							MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
						}
						
						
					}else if(name.indexOf("鲜花(") != -1){
						href = htmlBrowser[0].makeAbsoluteURL(href);
						hcu = new HttpConnectionUtil(href);
						hcu.setHttpConnectionListener(new MyHttpConnectionListener(0));
						hcu.start();
					}else if(name.indexOf("鸡蛋(") != -1){
						href = htmlBrowser[0].makeAbsoluteURL(href);
						hcu = new HttpConnectionUtil(href);
						hcu.setHttpConnectionListener(new MyHttpConnectionListener(1));
						hcu.start();

					}else if(name.indexOf("订制") != -1 || name.indexOf("取消") != -1){
						href = htmlBrowser[0].makeAbsoluteURL(href);
						hcu = new HttpConnectionUtil(href);
						hcu.setHttpConnectionListener(new MyHttpConnectionListener(2));
						hcu.start();
					}
					else if(name.indexOf("我要评论") != -1){
						href = htmlBrowser[0].makeAbsoluteURL(href);
						this.setActiveTab(2);
//						this.deleteAll(0);
						System.out.println("我要评论:"+href);
						htmlBrowser[2].go(href);
					}
					else if(name.indexOf("全部评论") != -1){
						this.setActiveTab(2);
//						this.deleteAll(0);
						htmlBrowser[2].go(htmlBrowser[0].getCurrentUrl()+"&td="+commentTD);
					}
					else if(isFeeUrl(href)){
						href = htmlBrowser[0].makeAbsoluteURL(href);
						FeeScreen.getInstance().setEntry(1);
						FeeScreen.getInstance().go(href);
						MainMidlet.getInstance().display.setCurrent(FeeScreen.getInstance());
						
					}
					else if(href.indexOf("pg=d") != -1){
						
						NovelsReaderScreen.getInstance().setEntry(1);
						NovelsReaderScreen.getInstance().initCommand();
						MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
						NovelsReaderScreen.getInstance().setAddress(href,1);
					}else if(href.indexOf("au=") != -1){
						System.out.println("dddd:"+href);
						BrowserScreen.getInstance().setTitle(name);
						BrowserScreen.getInstance().setEntry(4);
						BrowserScreen.getInstance().setScreenType("author");
						MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
						BrowserScreen.getInstance().go(href);
					}
					else{
//						System.out.println("other href!"+href);
						return htmlBrowser[i].handleKeyPressed(keyCode, gameAction);
					}
				}else if(type.equals("submit")){
					   String submit = htmlBrowser[i].getSubmitOfGetMethodUrl();
					   hcu = new HttpConnectionUtil(submit);
					   hcu.setHttpConnectionListener(new MyHttpConnectionListener(3));
					   hcu.start();
					
				}else{
//					System.out.println("textfield");
					return htmlBrowser[i].handleKeyPressed(keyCode, gameAction);
				}
				
				break;
			case BOOK_LOCAL:
				if(this.getActiveTab() == 0){
					NovelsReaderScreen.getInstance().setLocalBook(lb);
					
					NovelsReaderScreen.getInstance().setEntry(1);
					MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				}else{
					super.handleKeyPressed(keyCode, gameAction);
				}
				
				break;
			}
			
		}
		else
		{
			switch(bookType){
			case BOOK_ONLINE:
				return htmlBrowser[i].handleKeyPressed(keyCode, gameAction);
			case BOOK_LOCAL:
				return super.handleKeyPressed(keyCode, gameAction);
			default:
				return htmlBrowser[i].handleKeyPressed(keyCode, gameAction);
			}
		}
		return true;
	}

	protected boolean handleKeyRepeated(int keyCode,int action){
		int i = this.getActiveTab();
		switch(bookType){
		case BOOK_ONLINE:
			return this.handleKeyPressed(keyCode, action);
			
		case BOOK_LOCAL:
			return this.handleKeyPressed(keyCode, action);
		default:
			return htmlBrowser[i].handleKeyPressed(keyCode, action);
		}
		

	}
	
	public void commandAction(Command cmd, Displayable d) {
		//#ifdef polish.debugEnabled
		if (cmd == this.showLogCmd ) 
		{
			//#debug
			System.out.println("freeMemory:"+Runtime.getRuntime().freeMemory());
			//#debug
			System.out.println("totalMemory:"+Runtime.getRuntime().totalMemory());
			Debug.showLog(MainMidlet.getInstance().display);
			return;
		}
		if(cmd == this.cmdExit1)
		{
			MainMidlet.getInstance().quit();
		}
		//#endif
		if(cmd == cmdStartRead){
			switch(bookType){
			case BOOK_ONLINE:
				NovelsReaderScreen.getInstance().setAddress(readUrl,1);
				
				NovelsReaderScreen.getInstance().setEntry(0);
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
//				NovelsReaderScreen.getInstance().initCommand();
				break;
			case BOOK_LOCAL:
//				System.out.println("TIME IS 1:"+System.currentTimeMillis());
				NovelsReaderScreen.getInstance().setLocalBook(lb);
				
				NovelsReaderScreen.getInstance().setEntry(1);
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
//				NovelsReaderScreen.getInstance().initCommand();
//				System.out.println("TIME IS 2:"+System.currentTimeMillis());
				break;
			}
			
			
		}else if(cmd == cmdReturn){
			switch(bookType){
			case BOOK_ONLINE:
//				if(htmlBrowser[this.getActiveTab()].canGoBack()){
//					htmlBrowser[this.getActiveTab()].goBack();
//				}else{
					clear();
					if(entry == 0){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
					}else if(entry == 1){
						MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
					}else if(entry == 3){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
					}else if(entry == 4){
						MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
					}
					else{
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}
//				}
				this.lb = null;
				System.gc();
			
				break;
			case BOOK_LOCAL:
				lb = null;
			    clear();
				tempData.currentLocalBook = null;
				System.gc();
				MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
				break;
			}
			
		}else if(cmd == cmdIndex){
			clear();
			System.gc();
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdRefresh){
			doFresh();
		}else if(cmd == cmdSet){
			SystemSettingScreen.getInstance().setEntry(3);
			SystemSettingScreen.getInstance().initSetting();
			MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
		}else if(cmd == cmdHelp){
			BrowserScreen.getInstance().setTitle("帮助");
			String url = "resource://help.html";
			BrowserScreen.getInstance().setEntry(4);
			MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
			BrowserScreen.getInstance().go(url);
		}else if(cmd == cmdAddFa){
			addFa();
		}else if(cmd == cmdOk){
			MainMidlet.getInstance().display.setCurrent(this);
			this.setScrollYOffset(scrollBarHeight, false);
		}else if(cmd == cmdCancle){
			htmlBrowser[this.getActiveTab()].cancelRequest();
		}else if(cmd == cmdComment){
			this.setActiveTab(2);
			htmlBrowser[2].go(htmlBrowser[0].getCurrentUrl()+"&fn=cl&td=353415");
		}else if(cmd == cmdBookMark){
			LocalBookMarkScreen.getInstance().init();
			LocalBookMarkScreen.getInstance().setEntry(1);
			MainMidlet.getInstance().display.setCurrent(LocalBookMarkScreen.getInstance());
		}else if(cmd == cmdContinue){
			String packageID = null;
	    	Vector marks = (Vector)RmsOpt.readRMS("userbookmark");
	    	LocalBookHeaderInfo lbhi = tempData.currentLocalBook.getBookInfo().getHeader();
			if(lbhi != null){
				
				packageID = String.valueOf(lbhi.getPackageID());
			}
	    	if(!marks.isEmpty()){
	    		int length = marks.size();
				for(int i=0;i<length;i++){
					Vector vitem = (Vector)marks.elementAt(i);
					String head = (String)vitem.elementAt(0);
					if(head.equals(packageID)){
						LocalBookMark mark = (LocalBookMark)vitem.elementAt(2);
						if(mark != null){
							int chapter = mark.getChapter();
							int wordset = NovelsReaderScreen.getInstance().getBrowser().getUserSetWord();	
							int rate1 = wordset / 500 ;
							int page = (mark.getPage() -1)/rate1 + 1;
							String text = mark.getText();
							NovelsReaderScreen.getInstance().loadBookMark(chapter, page-1, text,mark.getDistance());
						}else{
							MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
							NovelsReaderScreen.getInstance().setEntry(1);
							NovelsReaderScreen.getInstance().setLocalBook(lb);
						}
						break;
						
					}
				}
	    	}
		}else if(cmd == cmdInvite){
			InviteScreen1.getInstance().setEntry(1);
			MainMidlet.getInstance().display.setCurrent(InviteScreen1.getInstance());
		}else if(cmd == cmdSelect){
			if(htmlBrowser[this.getActiveTab()].getFocusedItem() instanceof TextField){
				
				UiAccess.handleKeyPressed(htmlBrowser[this.getActiveTab()].getFocusedItem(), 0, Canvas.FIRE);
			}else{
				handleKeyPressed(-1,Canvas.FIRE);
			}
			
		}
		
	}
	/**提示框设置*/
	public void showAlert(int type,String content){
		scrollBarHeight = this.getScrollYOffset();
		if(type == 0){
			alert.removeAllCommands();
			alert.setTimeout(1500);
		}else if(type == 1){
			alert.removeAllCommands();
			alert.addCommand(cmdOk);
			alert.setTimeout(Alert.FOREVER);
		}
		
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	public void clear(){
		for(int i=0;i<3;i++){
			htmlBrowser[i].clear();
			htmlBrowser[i].clearPageCache();
			htmlBrowser[i].clearHistory();
			htmlBrowser[i].releaseResources();
			htmlBrowser[i].clearMeta();
			this.deleteAll(i);
			this.append(i,htmlBrowser[i]);
			Runtime.getRuntime().gc();
		}
		localContents = true;
	}
	public boolean canGoBackHere(){
		if(htmlBrowser[0].getItems().length != 0){
			System.out.println("can back");
			return true;
		}
		return false;
	}
	public void doFresh(){
		int focusedTab = this.getActiveTab();
		//#debug
		System.out.println("come to:"+htmlBrowser[focusedTab].getCurrentUrl());
		htmlBrowser[focusedTab].refresh(htmlBrowser[focusedTab].getCurrentUrl());
	}
	public void go(String url){
		int focusedTab = this.getActiveTab();
		htmlBrowser[focusedTab].refresh(url);
	}
	public void addFa(){
		hcu = new HttpConnectionUtil(htmlBrowser[0].getCurrentUrl()+"&fn=fa&td="+addFavoriteTD);	
		hcu.setHttpConnectionListener(new MyHttpConnectionListener(4,hcu));
		hcu.start();

	}
	/**字节转流*/
	public InputStreamReader getReader(LocalBook lb,String fileName){
		InputStreamReader isr = null;
		InputStream is = null;
		
		is = new ByteArrayInputStream(getBytes(lb,fileName));
		try {
			isr = new InputStreamReader(is,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isr;
	}
	/**从Local Book 中取字节文件*/
	public byte[] getBytes(LocalBook lb,String fileName){
		
		return lb.searchFileBytes(fileName);
		
	}
	//判断是否是计费URL
	public boolean isFeeUrl(String url){
		if(url == null) return false;
		if(url.indexOf("fn=fee") != -1 ){
			return true;
		}
		return false;
	}
	public void vote(int vote){
		StringItem anchor = (StringItem)htmlBrowser[0].getFocusedItem();
		String name = anchor.getText();
		switch(vote){
		case 0:{
				showAlert(1,"赠送鲜花成功!");
				int start = name.indexOf('(');
				String num = name.substring(start+1, name.length()-1);
				int number = Integer.parseInt(num)+1;
				anchor.setText("送鲜花("+number+")");
			}	
			break;
		case 1:{
				showAlert(1,"赠送鸡蛋成功!");
				int start = name.indexOf('(');
				String num = name.substring(start+1, name.length()-1);
				int number = Integer.parseInt(num)+1;
				anchor.setText("扔鸡蛋("+number+")");
			}
			break;
		case 2:{
				if(name.indexOf("订制") != -1){
					anchor.setText("取消");
				}else if(name.indexOf("取消") != -1){
					anchor.setText("订制");
				}
			}
			break;
		case 3:
			showAlert(1,"发表留言成功!");
			break;
		}
	}
	public void paint(Graphics g){
		super.paint(g);

		if(htmlBrowser[this.getActiveTab()].isOpenProc()){
			tempData.DrawGauge(g,htmlBrowser[this.getActiveTab()].getProcValue(),htmlBrowser[this.getActiveTab()].getProcInfo());
		}
		if(hcu != null){
			if(!hcu.isConnOver()){
				tempData.DrawGauge(g,hcu.getPercent(),hcu.getPercentInfo());
			}
		}
		if(bookType == this.BOOK_LOCAL){
			if(this.getActiveTab() == 0){
				g.drawString("开始阅读", this.getWidth()>>1, this.getScreenFullHeight()-4, 33);
			}
		}
	}
}
