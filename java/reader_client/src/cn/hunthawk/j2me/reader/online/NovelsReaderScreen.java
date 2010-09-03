package cn.hunthawk.j2me.reader.online;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.bo.LocalBookContents;
import cn.hunthawk.j2me.bo.LocalBookHeaderInfo;
import cn.hunthawk.j2me.bo.LocalBookMark;
import cn.hunthawk.j2me.browser.conf.HtmlImageTagHandler;
import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.control.NovelsReaderListener;
import cn.hunthawk.j2me.html.BrowserListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.ui.screen.FeeScreen;
import cn.hunthawk.j2me.ui.screen.MainScreen;
import cn.hunthawk.j2me.ui.screen.SystemSettingScreen;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.util.TextUtil;


public class NovelsReaderScreen extends Form
implements CommandListener,Runnable{
	
	public static NovelsReaderScreen novelsReaderScreen = null;
	public static final int BOOK_TYPE_ONLINE = 0;
	public static final int BOOK_TYPE_LOCAL = 1;
	private int bookType = 0;
	private int lastestPos = -1;
	private HtmlBrowser htmlBrowser = null;
	private int entry = 0;
	
	/*命令定义*/
	private Command cmdOk = new Command("确定",Command.ITEM,1);
	private Command cmdCancle = new Command("取消",Command.BACK,1);
	private Command cmdPageUp = new Command("上一章",Command.ITEM,2);
	private Command cmdPageDown = new Command("下一章",Command.ITEM,3);
	private Command cmdAddBookMark = new Command("添加书签",Command.ITEM,4);
	private Command cmdLocalBookMark = new Command("本书书签",Command.ITEM,4);
	private Command cmdBookMark = new Command("我的书签",Command.ITEM,4);
	private Command cmdAddNote = new Command("添加笔记",Command.ITEM,5);
	private Command cmdRefresh = new Command("刷新",Command.ITEM,6);
	private Command cmdSet = new Command("设      置",Command.ITEM,7);
	private Command cmdIndex = new Command("返回主页",Command.ITEM,8);
	private Command cmdExit = new Command("返回",Command.BACK,2);
	LocalBookContents lbcs = null;
	//滚动设置变量
	private boolean isRolling = false;
	private boolean skillArc = false;
	private boolean isBackTop = false;	
	private int sleepTime = 100;
	//字体设置变量
	private int fontSizeState = 0;/*0-不改变;1-小号字体(默认);2-中号字体;3-大号字体*/
	private int fontColorState = 0;/*0-不改变;1-白色(默认);2-红色;3-绿色*/
	//alert变量
	private Alert alert;
	
	private int pageState = 0;//0-简介 ，1，2....代表当前页数
	private int subPage = 0;

	private Vector items_local = null;
	private LocalBook lb = null;
	
	private int screenContentHeight;
	boolean controlPage = false;
	boolean isFullScreen = false;
	long timeOut = 0L;
	private AutoRollingControl arc = null;
	private String addMarkbook = null;
	private Thread fsc = null;
	private boolean initCMD = true;
	
	private HttpConnectionUtil hcu = null;
	public NovelsReaderScreen() {
		//#style novelsReaderScreen
		super("正文");
		//#style browser
		htmlBrowser = new HtmlBrowser();
		new HtmlImageTagHandler(this.htmlBrowser.getTagHandler("html")).register(this.htmlBrowser);
		htmlBrowser.setCancleCommand(cmdCancle);
		htmlBrowser.setShowTitle(true);
		htmlBrowser.setBrowserListener((BrowserListener) new NovelsReaderListener(htmlBrowser));
		this.append(htmlBrowser);
		
		
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);	
		alert.addCommand(cmdOk);
		Vector v = (Vector)RmsOpt.readRMS("homecontrol");
		if(!v.isEmpty()){
			HomeControler home = (HomeControler)v.elementAt(0);
			if(home != null){
				addMarkbook = home.getAddbookmarktd();
			}
		}
		
		fsc = new Thread(this);
		fsc.start();
	}
	
	public static NovelsReaderScreen getInstance(){
		if(novelsReaderScreen == null){
			novelsReaderScreen = new NovelsReaderScreen();
		}
//		System.out.println("new novelsReaderScreen");
		return novelsReaderScreen;
	}
	public void initCommand(){
//		System.out.println("booooooooooooooooktype:"+bookType);
		setUseDefinitionBgColor();
		if(bookType == 0){
			this.removeAllCommands();
			this.addCommand(cmdPageUp);
			this.addCommand(cmdPageDown);
			this.addCommand(cmdAddBookMark);
			this.addCommand(cmdBookMark);
			this.addCommand(cmdRefresh);
			this.addCommand(cmdSet);
			this.addCommand(cmdIndex);
			this.addCommand(cmdExit);
		}else if(bookType == 1){
			this.removeAllCommands();
			this.addCommand(cmdPageUp);
			this.addCommand(cmdPageDown);
			this.addCommand(cmdAddBookMark);
			this.addCommand(cmdLocalBookMark);
			this.addCommand(cmdAddNote);
			this.addCommand(cmdRefresh);

			this.addCommand(cmdSet);
			this.addCommand(cmdIndex);
			this.addCommand(cmdExit);
		}
		
		this.setCommandListener(this);
		
	}
	public void setUseDefinitionBgColor(){
		Vector v = (Vector)RmsOpt.readRMS("settings");
		if(v.isEmpty()){
			htmlBrowser.setDifintionBgColor(false);
		}else{
			int[] settings = (int[])v.elementAt(0);
			if(settings[5] == 0){
				htmlBrowser.setDifintionBgColor(false);
			}else{
				htmlBrowser.setDifintionBgColor(true);
				this.setCustomStyle(settings[5]-1);
			}
		}
	}
	public void show(){
		MainMidlet.getInstance().display.setCurrent(this);
	}
	public int getBookCurType(){
		return bookType;
	}
	/**字节转流*/
	public InputStreamReader getReader(String fileName){
		InputStreamReader isr = null;
		InputStream is = null;
		//#debug
		System.out.println("creat stream...");
		is = new ByteArrayInputStream(getBytes(this.lb,fileName));
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
	public void go(String url){
		htmlBrowser.go(url);
	}
	/**在线阅读入口设置*/
	public void setAddress(String Url,int index){
		htmlBrowser.refresh(Url);
		bookType = BOOK_TYPE_ONLINE;
		pageState = index;
//		initCommand();
		this.setFullScreen(false);
		htmlBrowser.setLocalBrowserType(false);
		
	}
	public void setEntry(int entry){
		this.entry = entry;		
		
	}
	/**离线阅读入口设置*/
	public void setLocalBook(LocalBook lb){
		this.lb = lb;
		htmlBrowser.setLocalBrowserType(true);
		htmlBrowser.initLocalChapter();
		items_local = lb.getBookInfo().getContents();
		
		for(int i=0;i<items_local.size();i++){
		
			lbcs = (LocalBookContents)items_local.elementAt(i);
			
			if(lbcs.getChapterPlayOrder().equals("2")){
				
					new Thread(){
						public void run(){
							try {
								htmlBrowser.loadPage(getReader(lbcs.getChapterSrc()));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();	
				
				break;
				
			}
		}
		
		bookType = BOOK_TYPE_LOCAL;
		htmlBrowser.setCurrentDocumentBase(null);
		pageState = 2;
		subPage = 0;
		this.backTop();
		this.setFullScreen(false);
		initCommand();	
	}
	public void ContentsToReader(LocalBook lb, int order){
		this.lb = lb;
		htmlBrowser.setLocalBrowserType(true);
		htmlBrowser.setCurrentDocumentBase(null);
		htmlBrowser.initLocalChapter();
		items_local = lb.getBookInfo().getContents();
		for(int i=0;i<items_local.size();i++){
			lbcs = (LocalBookContents)items_local.elementAt(i);
			if(lbcs.getChapterPlayOrder().equals(String.valueOf(order))){
				//#debug
				System.out.println("into order:"+order+"src:"+lbcs.getChapterSrc());
				new Thread(){
					public void run(){
						try {
							htmlBrowser.loadPage(getReader(lbcs.getChapterSrc()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();	
				break;
			}
		}
		bookType = BOOK_TYPE_LOCAL;
		pageState = order;
		subPage = 0;
		entry = 1;
		this.backTop();
		if(this.getCommandItem(cmdAddNote) == null){
			this.setFullScreen(false);
			this.initCommand();
		}
		
		
	}
	public LocalBook getCurrentLocalBook(){
		return this.lb;
	}
	public void setInputReade(String fileName){
		try {
			htmlBrowser.loadPage(this.getReader(fileName));
			do{
				Thread.sleep(100);
			}while(htmlBrowser.isWorking());
			htmlBrowser.setScrollYOffset(0, false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == this.cmdExit){
			Exit();
		}else if(cmd == this.cmdPageUp){
			ChapterUp();
		}else if(cmd == this.cmdPageDown){
			ChapterDown();
		}else if(cmd == this.cmdAddBookMark){
			addBookMark(0);
		}else if(cmd == this.cmdRefresh){
			doRefresh();
		}else if(cmd == this.cmdSet){
			showSet();
		}else if(cmd == this.cmdIndex){
			clear();
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == this.cmdAddNote){
			MainMidlet.getInstance().display.setCurrent(UserBookNoteScreen.getInstance());
		}else if(cmd == this.cmdLocalBookMark){
			LocalBookMarkScreen.getInstance().init();
			LocalBookMarkScreen.getInstance().setEntry(0);
			MainMidlet.getInstance().display.setCurrent(LocalBookMarkScreen.getInstance());
		}else if(cmd == cmdCancle){
			htmlBrowser.cancelRequest();
		}else if(cmd == cmdBookMark){
			showBookMark();
		}else if(cmd == cmdOk){
			MainMidlet.getInstance().display.setCurrent(this);
			this.setScrollYOffset(lastestPos, false);
		}

	}
	/**显示书签*/
	public void showBookMark(){
		if(bookType == 0){
			MainScreen.getInstance().showMyBookStore(1);
		}
	}
	/**加入书签*/
	public void addBookMark(int fun){
//		htmlBrowser.go(htmlBrowser.getCurrentUrl()+"&fn=ma&td=353268");
		
		switch(bookType){
		case BOOK_TYPE_ONLINE:
			hcu = new HttpConnectionUtil(htmlBrowser.getCurrentUrl()+"&fn=ma&td="+addMarkbook);
			hcu.setHttpConnectionListener(new MyHttpConnectionListener(5,hcu));
			hcu.start();
			
			break;
		case BOOK_TYPE_LOCAL:
			System.out.println("book type local book mark1");
			int nowwordset = htmlBrowser.getUserSetWord();
			int nowpage = subPage+1;
			int nowscrollY = -htmlBrowser.getScrollYOffset();
			int rate1 = nowwordset / 500;
			
			int index = htmlBrowser.binarySearchFristItem(htmlBrowser.getItems(),nowscrollY);
			StringItem stringItem = null;
			
			System.out.println("book type local book mark2:"+index);
			if(index == 0){
				do{
					stringItem = (StringItem)htmlBrowser.getItems()[index];
					if(stringItem.getText() == null){
						index++;
					}else{
						break;
					}
				}while(true);
			}else{
				while(true){
					stringItem = (StringItem)htmlBrowser.getItems()[index];
					if(stringItem.getText() == null){
						index--;
						
					}else{
						break;
					}
				};
			}
			
			System.out.println("index:"+index);
			String match = stringItem.getText();
			if(match.length() >= 9) match = match.substring(0, 8);
			System.out.println("match:"+match);
			int distance = nowscrollY - htmlBrowser.getItems()[index].relativeY;
			int savepage = (nowpage - 1)*rate1 + 1;
			
			int halfHeight = htmlBrowser.itemHeight / rate1;
			int realHight = htmlBrowser.getItems()[index].relativeY + distance;
			int rate2 = realHight / halfHeight;
			savepage += rate2;
			LocalBookHeaderInfo lbhi = lb.getBookInfo().getHeader();
			if(lbhi != null){
				
				String packageID = String.valueOf(lbhi.getPackageID());
				LocalBookMark lbm = new LocalBookMark();
				lbm.setPackageID(packageID);
				lbm.setChapter(pageState);
				lbm.setTitle(this.getTitle());
				lbm.setPage(savepage);
				lbm.setText(match);
				lbm.setDistance(distance);
				Vector marks = (Vector)RmsOpt.readRMS("userbookmark");
				int length = marks.size();
				boolean isFrist = true;
				for(int i=0;i<length;i++){
					Vector vitem = (Vector)marks.elementAt(i);
					String head = (String)vitem.elementAt(0);
					if(head.equals(packageID)){
						switch(fun){
						case 0:{
//							Vector mark = (Vector)vitem.elementAt(1);
//							mark.addElement(lbm);
							marks.removeElementAt(i);
							Vector vv = new Vector();
							vv.addElement(packageID);
							vv.addElement(lbm);
							
							vv.addElement(vitem.elementAt(2));
							marks.addElement(vv);
							RmsOpt.saveRMS(marks, "userbookmark");
							isFrist = false;
							break;
						}
						case 1:
							vitem.removeElementAt(2);
							vitem.addElement(lbm);
							marks.removeElementAt(i);
							marks.addElement(vitem);
							RmsOpt.saveRMS(marks, "userbookmark");
							isFrist = false;
							break;
						}
						
						break;
					}
				}
					
				if(isFrist){
					switch(fun){
					case 0:{
						Vector head = new Vector();
						head.addElement(packageID);
//						Vector mark = new Vector();
//						mark.addElement(lbm);
						head.addElement(lbm);
						head.addElement(null);
						marks.addElement(head);
						System.out.println("fun0");
						RmsOpt.saveRMS(marks, "userbookmark");
						break;
					}
					case 1:{
						Vector head = new Vector();
						head.addElement(packageID);
						Vector mark = new Vector();
						head.addElement(mark);
						head.addElement(lbm);
						System.out.println("fun1");
						marks.addElement(head);
						RmsOpt.saveRMS(marks, "userbookmark");
					}
						break;
					}
					
				}
				
				showAlert(0,"保存成功");
			}
			break;
		}
		
		
		
	}
	/**上一章*/
	public void ChapterUp(){
		if(bookType == this.BOOK_TYPE_ONLINE){
			Vector v = htmlBrowser.getMeta();
			int currentChapterID = 1;
			if(v != null){
				int length = v.size();
				for(int j=0;j<length;j++){
					StringItem meta = (StringItem)v.elementAt(j);
					if(meta.getLabel().equals("currentCapterID")){
						currentChapterID = Integer.parseInt(meta.getText());
						break;
					}
				}
				if(currentChapterID != 1){
					for(int i=0;i<length;i++){
						StringItem meta = (StringItem)v.elementAt(i);
						if(meta.getLabel().equals("prevCapterUrl")){
							String url = meta.getText().trim();
//							System.out.println("ChapterUp:"+url);
							htmlBrowser.clear();
							url = htmlBrowser.makeAbsoluteURL(url);
							url = TextUtil.replace(url,"amp;", "");
							if(isFeeUrl(url)){
								FeeScreen.getInstance().setEntry(2);
								FeeScreen.getInstance().go(url);
								MainMidlet.getInstance().display.setCurrent(FeeScreen.getInstance());
								
							}else{
								htmlBrowser.go(url);
							}
							
							break;
						}
					}
				}
				
			}
		}else if(bookType == this.BOOK_TYPE_LOCAL){
			
			if(pageState >1){
				pageState--;
				htmlBrowser.initLocalChapter();
				ContentsToReader(lb,pageState);
			}
			
		}
		System.gc();
	}
	/**下一章*/
	public void ChapterDown(){
		if(bookType == this.BOOK_TYPE_ONLINE){
			Vector v = htmlBrowser.getMeta();
			int currentChapterID = 1;
			int totalChapter = 0;
			if(v != null){
				int length = v.size();
				for(int j=0;j<length;j++){
					StringItem meta = (StringItem)v.elementAt(j);
					if(meta.getLabel().equals("currentCapterID")){
						currentChapterID = Integer.parseInt(meta.getText());
					}
					if(meta.getLabel().equals("totalCapter")){
						totalChapter = Integer.parseInt(meta.getText());
					}
					
				}
				if(currentChapterID < totalChapter){
					for(int i=0;i<length;i++){
						StringItem meta = (StringItem)v.elementAt(i);
						if(meta.getLabel().equals("nextCapterUrl")){
							String url = meta.getText().trim();
//							System.out.println("ChapterDown:"+url);
							htmlBrowser.clear();
							url = htmlBrowser.makeAbsoluteURL(url);
							url = TextUtil.replace(url,"amp;", "");
							if(isFeeUrl(url)){
								FeeScreen.getInstance().setEntry(2);
								FeeScreen.getInstance().go(url);
								MainMidlet.getInstance().display.setCurrent(FeeScreen.getInstance());
								
							}else{
								htmlBrowser.go(url);
							}
							
							break;
						}
					}
				}
				
			}
		}else if(bookType == this.BOOK_TYPE_LOCAL){
			pageState++;
			htmlBrowser.initLocalChapter();
			ContentsToReader(lb,pageState);
		}
		System.gc();
	}
	/**下一页*/
	public void pageDown(){
		if(bookType == this.BOOK_TYPE_ONLINE){
			Vector v = htmlBrowser.getMeta();
			int currentPageID = getCurrentPageID();
			int totalPage = 0;
			if(v != null){
				int length = v.size();
				for(int j=0;j<length;j++){
					StringItem meta = (StringItem)v.elementAt(j);
					
					if(meta.getLabel().equals("totalPage")){
						totalPage = Integer.parseInt(meta.getText());
					}
					
				}
				if(currentPageID < totalPage){
					for(int i=0;i<length;i++){
						StringItem meta = (StringItem)v.elementAt(i);
						if(meta.getLabel().equals("nextContentUrl")){
							String url = meta.getText().trim();
//							System.out.println("pageDown:"+url);
							htmlBrowser.clear();
							url = htmlBrowser.makeAbsoluteURL(url);
							//#debug
							System.out.println("pageDown:"+url);
							//#debug
							System.out.println("cururet:"+htmlBrowser.getCurrentUrl());
							url = TextUtil.replace(url,"amp;", "");
							if(isFeeUrl(url)){
								FeeScreen.getInstance().setEntry(2);
								FeeScreen.getInstance().go(url);
								MainMidlet.getInstance().display.setCurrent(FeeScreen.getInstance());
								
							}else{
								htmlBrowser.go(url);
							}
							
							break;
						}
					}
				}else if(currentPageID == totalPage){//每章最后一页连接至下一章
					ChapterDown();
				}
				
			}
		}else if(bookType == this.BOOK_TYPE_LOCAL){
			subPage++;
//			System.out.println("subPage:"+subPage);
			
			Vector v = htmlBrowser.getLocalCurrentChapterContainer();
//			System.out.println("v size:"+(v.size()));
			StringItem stringItem = null;
			if(subPage < v.size()){
				Vector vv = (Vector)v.elementAt(subPage);
				int length = vv.size();
				
				htmlBrowser.clear();
				for(int i=0;i<length;i++){
					stringItem = (StringItem)vv.elementAt(i);
//					System.out.println("stringitem:"+stringItem.getText());
					htmlBrowser.add(stringItem);
				}
				this.backTop();
			}else{
				ChapterDown();
			}
		}
		System.gc();
	}
	/**跳转指定页*/
	public void loadPageOfNumber(int pageNum){
		subPage = pageNum;
		Vector v = htmlBrowser.getLocalCurrentChapterContainer();
//		System.out.println("v size:"+(v.size()));
		StringItem stringItem = null;
		if(subPage < v.size()){
			Vector vv = (Vector)v.elementAt(subPage);
			int length = vv.size();
			
			htmlBrowser.clear();
			for(int i=0;i<length;i++){
				stringItem = (StringItem)vv.elementAt(i);
				Style localStyle = StyleSheet.getStyle("browserTextReader");
    			Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, tempData.getFontSize());
    			localStyle.font = font;
    			stringItem.setStyle(localStyle);
//    			System.out.println("content hight:"+htmlBrowser.itemHeight);
				htmlBrowser.add(stringItem);
			}
//			this.backTop();
		}
	}
	/**上一页*/
	public void pageUp(){
		
		if(bookType == this.BOOK_TYPE_ONLINE){
			Vector v = htmlBrowser.getMeta();
			int currentChapterID = this.getCurrentPageID();
			if(v != null){
				int length = v.size();
				
				if(currentChapterID != 1){
					for(int i=0;i<length;i++){
						StringItem meta = (StringItem)v.elementAt(i);
						if(meta.getLabel().equals("prevContentUrl")){
							String url = meta.getText().trim();
//							System.out.println("pageUp:"+url);
							htmlBrowser.clear();
							url = htmlBrowser.makeAbsoluteURL(url);
							url = TextUtil.replace(url,"amp;", "");
							if(isFeeUrl(url)){
								FeeScreen.getInstance().setEntry(2);
								FeeScreen.getInstance().go(url);
								MainMidlet.getInstance().display.setCurrent(FeeScreen.getInstance());
								
							}else{
								htmlBrowser.go(url);
							}
							break;
						}
					}
				}
				
			}
		}else if(bookType == this.BOOK_TYPE_LOCAL){
			subPage--;
			Vector v = htmlBrowser.getLocalCurrentChapterContainer();
			StringItem stringItem = null;
			if(subPage > -1){
				Vector vv = (Vector)v.elementAt(subPage);
				int length = vv.size();
				
				htmlBrowser.clear();
				for(int i=0;i<length;i++){
					stringItem = (StringItem)vv.elementAt(i);
//					System.out.println("stringitem:"+stringItem.getText());
					htmlBrowser.add(stringItem);
				}
				this.backTop();
			}
		}
		System.gc();
	}
	public void loadBookMark(int chapter,int page,String text,int distance){
		htmlBrowser.initLocalChapter();
		ContentsToReader(tempData.currentLocalBook,chapter);
//		System.out.println("isWorking:"+htmlBrowser.isWorking());
		Tools.sleep(1000);
		loadPageOfNumber(page);
		Tools.sleep(500);
		MainMidlet.getInstance().display.setCurrent(this);
		Tools.sleep(500);
		int scroollBar = 0;
		int length = htmlBrowser.getItems().length;
		for(int i=0;i<length;i++){
			StringItem stringItem = (StringItem)htmlBrowser.get(i);
			if(stringItem.getText().indexOf(text) != -1){
				scroollBar = htmlBrowser.getItems()[i].relativeY;
				this.setScrollYOffset(-(scroollBar+distance), false);
				break;
			}
		}
		System.gc();
	}
	public int getCurrentPageID(){
		int pageID = -1;
		switch(bookType){
		case 0:
			String url = htmlBrowser.getCurrentUrl();
			int pos = url.indexOf("pn=");
			url  = url.substring(pos);
			if(url.indexOf("&") != -1){
				url = url.substring(3,url.indexOf("&"));
//				System.out.println("page current1:"+url);
				pageID = Integer.parseInt(url);
				
			}else{
				url = url.substring(3);
//				System.out.println("page current2:"+url);
				pageID = Integer.parseInt(url);
			}
			break;
		case 1:
			pageID = subPage + 1;
			break;
		}
		return pageID;
	}
	public int getCurrentChapterID(){
		int chapterID = -1;
		switch(bookType){
		case 0:
			Vector v = htmlBrowser.getMeta();
			if(v != null){
				int length = v.size();
				for(int j=0;j<length;j++){
					StringItem meta = (StringItem)v.elementAt(j);
					if(meta.getLabel().equals("currentCapterID")){
						chapterID = Integer.parseInt(meta.getText());
						break;
					}
				}
			}
			break;
		case 1:
			chapterID = pageState;
			break;
		}
		return chapterID;
	}
	/**
	 * 返回字体状态
	 * @return 字体状态ID
	 * */
	public int getFontState(){
		return this.fontSizeState;
	}
	
	/**刷新*/
	public void doRefresh(){
		if(bookType == this.BOOK_TYPE_ONLINE){

			htmlBrowser.go(htmlBrowser.getCurrentUrl());
		}else if(bookType == this.BOOK_TYPE_LOCAL){
			ContentsToReader(this.lb,pageState);
		}
		
	}
	/**回封面*/
	public void backLocalCover(){
		setLocalBook(lb);
	}
	public void Exit(){
//		addBookMark(1);
		clear();
		if(entry == 1){
			MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
		}else if(entry == 2){
			if(MainScreen.getInstance().myBookScreen != null){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
			}else{
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
			}
			
		}else if(entry == 3){
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}
	}
	public void clear(){
		
		htmlBrowser.releaseResources();
		htmlBrowser.clearHistory();
		htmlBrowser.clear();
		htmlBrowser.clearPageCache();
		this.lb = null;
		isRolling = false;
		skillArc = true;
		arc = null;
		
		System.gc();
	}
	/**显示设置*/
	public void showSet(){
		SystemSettingScreen.getInstance().initSetting();
		SystemSettingScreen.getInstance().setEntry(2);
		MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
	}
	
	
	/**
	 * 字体设置
	 * @param fontSize 字体字号状态标记
	 * @param fontColor 字体颜色状态标记
	 * 
	 * */
	public void setCustomFont(int fontSize,int fontColor){
		if(fontSize != 0){
			this.fontSizeState = fontSize;
		}
		if(fontColor != 0){
			this.fontColorState = fontColor;
		}
		
	
		Style style = new Style();
		Style styleLink = new Style();
		Style styleLinkFoucsed = new Style();
		Font font = null;
		Color color = null;
		Color colorFoucsed = null;
		
		Item[] items = this.htmlBrowser.getItems();
		
		if(fontSizeState == 1){
			font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		}else if(fontSizeState == 2){
			font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		}else if(fontSizeState == 3){
			font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
		}
		style.font = font;
		styleLink.font = font;
		styleLinkFoucsed.font = font;
		
		
		
		if(fontColorState == 1){
			color = new Color(0xffffff);
			colorFoucsed = new Color(0xff0000);
		}else if(fontColorState == 2){
			color = new Color(0xff0000);
			colorFoucsed = new Color(0x00ff00);
		}else if(fontColorState == 3){
			color = new Color(0x00ff00);
			colorFoucsed = new Color(0xff0000);
		}else{
			color = new Color(0x000000);
			colorFoucsed = new Color(0xff0000);
		}
		style.fontColorObj = color;
		styleLink.fontColorObj = color;
		styleLinkFoucsed.fontColorObj = colorFoucsed;
		
		
		htmlBrowser.clear();
		for(int i=0;i<items.length;i++){
			if(items[i] instanceof StringItem){
				String href = (String) items[i].getAttribute("href");
				if(href == null){
					items[i].setStyle(style);
				}else{
					items[i].setStyle(styleLink);
				}
				
				if(items[i].isFocused){
					items[i].setStyle(styleLinkFoucsed);
				}
			}
			htmlBrowser.add(items[i]);
		}
	}
	
	/**
	 * 风格设置
	 * @param styleID 风格ID
	 * */
	public void setCustomStyle(int styleID){
		int [] colorValues = {0xFF0000
				,0xFFC000
				,0xFFFF00
				,0x92D050
				,0x00B050
				,0x00B0F0
				,0x000000
				,0xffffff};
		Style style = StyleSheet.getStyle("novelsReaderScreen");
//		if(style == null) System.out.println("style null");
		style.background = new SimpleBackground(colorValues[styleID]);
		this.setStyle(style);
//		System.out.println("setCustomStyle!");
		htmlBrowser.setDifintionBgColor(true);
	}
	public void setHomeStyle(){
		Style style = StyleSheet.getStyle("novelsReaderScreen");
		style.background = StyleSheet.getStyle("mainScreen").background;
		this.setStyle(style);
		htmlBrowser.setDifintionBgColor(false);
	}
	/**
	 * 自动滚屏设置
	 * @param isRolling 是否滚动
	 * @param sleepTime 每向下偏移1像素等待的时间，快速-25ms;中速-50ms;慢速-100ms;
	 * */
	public void setRolling(boolean isRolling , int sleepTime){
		this.isRolling = isRolling;
		this.sleepTime = sleepTime;
	}
	public HtmlBrowser getBrowser(){
		return this.htmlBrowser;
	}
	/**
	 * 回到起始位置
	 * */
	public void backTop(){
		System.out.println("back to top!");
		Tools.sleep(250);
		this.setScrollYOffset(0, false);
		repaint();
		
	}
	public void setAutoRolling(){
		if(arc == null){
			arc = new AutoRollingControl();
			arc.start();
			skillArc = false;
		}
		
			
		if(isRolling){
			isRolling = false;
		}else{
			isRolling = true;
		}
	}
	/**控制自动滚屏*/
	class AutoRollingControl extends Thread{
		
		public void run(){
			
			while(true){
				if(skillArc){
					break;
				}
				if(isRolling){
					
						try {
							Thread.sleep(110);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int height = htmlBrowser.getScrollYOffset()-1;
						
						htmlBrowser.setScrollYOffset(height, false);
						if(novelsReaderScreen.getScrollYOffset() <= -(htmlBrowser.itemHeight-novelsReaderScreen.getAvailableHeight()-2)){
							isRolling = false;
							if(!skillArc){
								ControlPaneDown.getInstance().init();
								MainMidlet.getInstance().display.setCurrent(ControlPaneDown.getInstance());
							}
						}
						repaint();
					
				}
				
			}
		
		}
	}
	/**控制隐藏title,menuBar线程*/
	public void run(){
		while(true){
//			System.out.println("thread running...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!isFullScreen){
				if(novelsReaderScreen.isMenuOpened()){
//					System.out.println("get timeOut");
//					timeOut = System.currentTimeMillis();
					
				}else{
					
					if(System.currentTimeMillis() - timeOut >3000){
						setFullScreen(true);
					}
				}
			}else{
				synchronized( this.fsc ) {
		        	try {
		        		
						fsc.wait();
//						System.out.println("thread waiting...");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
		}
	}

	/**
	 * 按键处理函数
	 * */
	protected boolean handleKeyPressed(int keyCode,
            int action){
		System.out.println("key:"+keyCode+"action:"+action);
		if(isFullScreen){
			
			if(action != Canvas.UP && action != Canvas.DOWN &&
				action != Canvas.LEFT && action != Canvas.RIGHT){
				setFullScreen(false);
			}
		}

			
		
		if(keyCode == Canvas.KEY_NUM1){
			addBookMark(0);
		}else if(keyCode == Canvas.KEY_NUM3){
			setAutoRolling();
		}else if(keyCode == Canvas.KEY_NUM7){
			pageUp();
		}else if(keyCode == Canvas.KEY_NUM9){
			pageDown();
		}else if(keyCode == Canvas.KEY_STAR){
//			showBookMark();
		}else if(keyCode == Canvas.KEY_NUM0){
			backTop();
		}else if(action == Canvas.UP || keyCode == Canvas.KEY_NUM2){
			if(this.getScrollYOffset() >= 0){
				if(!htmlBrowser.isOpenProc()){
					ControlPaneUp.getInstance().init();
					MainMidlet.getInstance().display.setCurrent(ControlPaneUp.getInstance());
				}
				
			}else{
				int height = this.getScrollYOffset()+25;					
				  
				this.setScrollYOffset(height,false);
			}
		}else if(action == Canvas.DOWN || keyCode == Canvas.KEY_NUM8){
			if(htmlBrowser.itemHeight < this.getAvailableHeight()){
				if(!htmlBrowser.isOpenProc()){
					ControlPaneDown.getInstance().init();
					MainMidlet.getInstance().display.setCurrent(ControlPaneDown.getInstance());
				}
				
			}else{
				if(this.getScrollYOffset() <= -(htmlBrowser.itemHeight-this.getAvailableHeight()-2)){
					if(!htmlBrowser.isOpenProc()){
						ControlPaneDown.getInstance().init();
						MainMidlet.getInstance().display.setCurrent(ControlPaneDown.getInstance());
					}
					
				}else{
					  this.scrollRelative(-25);
				}
			}
			
		}else if(action == Canvas.LEFT || keyCode == Canvas.KEY_NUM4){
			  if(this.getScrollYOffset() == 0){
				  if(!htmlBrowser.isOpenProc()){
					  ControlPaneUp.getInstance().init();
					  MainMidlet.getInstance().display.setCurrent(ControlPaneUp.getInstance());
					  return true;
				  }
				  
			  }
			  screenContentHeight = this.getAvailableHeight();
			  int height = this.getScrollYOffset()+this.screenContentHeight-20;
			  if(height > 0) 
				  height = 0;
			  this.setScrollYOffset(height,false);
			
		}else if(action == Canvas.RIGHT || keyCode == Canvas.KEY_NUM6){
			  
			  if(this.getScrollYOffset() == -htmlBrowser.getScrollBarMaxOffset()){
				  if(!htmlBrowser.isOpenProc()){
					  ControlPaneDown.getInstance().init();
					  MainMidlet.getInstance().display.setCurrent(ControlPaneDown.getInstance());
					  return true;
				  }
				  
			  }
			  screenContentHeight = this.getAvailableHeight();
			  int height = this.getScrollYOffset()-screenContentHeight +20;
			  if(Math.abs(height) > htmlBrowser.getScrollBarMaxOffset()) 
				  height = -htmlBrowser.getScrollBarMaxOffset();
			  this.setScrollYOffset(height,false);
			
			  
		}
//		System.gc();
		return true;
		
	}
	protected boolean handleKeyRepeated(int keyCode,int action){
		  return handleKeyPressed(keyCode, action);
	}
	public void refreshWordSet(){
		switch(bookType){
		case BOOK_TYPE_ONLINE:
			String currentUrl = htmlBrowser.getCurrentUrl();
			int currentPage = getCurrentPageID();
			String url = TextUtil.replaceFirst(currentUrl, "pn="+currentPage, "pn=1");
//			System.out.println("url word set:"+url);
			htmlBrowser.go(url);
			break;
		case BOOK_TYPE_LOCAL:
//			System.out.println("refreshwordset");
			htmlBrowser.initLocalChapter();
			ContentsToReader(lb,pageState);
			break;
		}
		
	}
	public void refreshBgColor(int i){
//		System.out.println("refreshBgColor:"+i);
		switch(bookType){
		case BOOK_TYPE_ONLINE:
			if(i == 0){
				htmlBrowser.setDifintionBgColor(false);
				this.setHomeStyle();
				htmlBrowser.go(htmlBrowser.getCurrentUrl());
			}else{
//				System.out.println("refreshBgColors:");
				setCustomStyle(i-1);
			}
			break;
		case BOOK_TYPE_LOCAL:
			if(i == 0){
				this.setHomeStyle();
			}else{
//				System.out.println("refreshBgColors:");
				setCustomStyle(i-1);
			}
			break;
		}
	}
	/**提示框设置*/
	public void showAlert(int type,String content){
		lastestPos = this.getScrollYOffset();
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	/**设置全屏*/
	public void setFullScreen(boolean isFull){
		System.out.println("setFullScreen:"+isFull);
		int offset = this.getScrollYOffset();
		if(isFull){
			this.setFullScreenMode(true);
			isFullScreen = true;

			Style titleStyle  = StyleSheet.getStyle("title2");
			titleStyle.marginTop = -20;
			this.setTitle(this.getTitle(), titleStyle);
		}else{
			this.setFullScreenMode(false);
			isFullScreen = false;

			Style titleStyle  = StyleSheet.getStyle("title2");
			titleStyle.marginTop = 0;
			this.setTitle(this.getTitle(), titleStyle);
			timeOut = System.currentTimeMillis();
			synchronized( this.fsc ) {
					fsc.notify();
	        }
			
		}
		this.setScrollYOffset(offset, false);
		repaint();
	}
	public void paint(Graphics g){
		super.paint(g);
//		g.drawString(tempData.getTime(), 235, 3, 24);
		if(htmlBrowser.isOpenProc()){
			tempData.DrawGauge(g,htmlBrowser.getProcValue(),htmlBrowser.getProcInfo());
		}
		if(hcu != null){
			if(!hcu.isConnOver()){
				tempData.DrawGauge(g,hcu.getPercent(),hcu.getPercentInfo());
			}
		}
	}
	//判断是否是计费URL
	public boolean isFeeUrl(String url){
		if(url == null) return false;
		if(url.indexOf("fn=fee") != -1 ){
			return true;
		}
		return false;
	}
	/**get current page and total pages*/
	public String getDisplayInfo(){
		int current =0;
		String total = null;
		switch(bookType){
		case BOOK_TYPE_ONLINE:
			Vector v = htmlBrowser.getMeta();
			current  = this.getCurrentPageID();;
			
			if(v != null){
				int length = v.size();
				for(int j=0;j<length;j++){
					StringItem meta = (StringItem)v.elementAt(j);
					if(meta.getLabel().equals("totalPage")){
						total = meta.getText();
						break;
					}
				}
			}
			break;
		case BOOK_TYPE_LOCAL:
			Vector vv = htmlBrowser.getLocalCurrentChapterContainer();
			total = String.valueOf(vv.size());
			current = subPage + 1;
			break;
		}
		
		return current+"/"+total;
	}
}
