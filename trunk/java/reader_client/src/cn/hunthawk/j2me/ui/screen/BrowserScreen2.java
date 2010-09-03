package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

public class BrowserScreen2 extends TabbedForm 
implements CommandListener{

	private Command cmdCancle = new Command("取消",Command.BACK,1);
	private Command cmdReturn = new Command(Locale.get("cmd.return"),Command.BACK,2);
	private Command cmdRefresh = new Command(Locale.get("cmd.screen2refresh"),Command.ITEM,2);
	private Command cmdSet = new Command(Locale.get("cmd.screen2set"),Command.ITEM,7);
	private Command cmdHelp = new Command(Locale.get("cmd.screen2help"),Command.ITEM,8);
	private Command cmdIndex =new Command(Locale.get("cmd.screen2index"),Command.ITEM,9);
	
	private Command cmdOk = new Command(Locale.get("cmd.ok"),Command.ITEM,1);
	private Command cmdNo = new Command(Locale.get("cmd.no"),Command.BACK,1);
	private Alert alert = null;
	private int delType = 0;
	private String delUrl = null;
	private HtmlBrowser htmlBrowser[] = null;
	private int tabCounts = 0;
	private String channels[] = null;
	private boolean isTabBarFocuse = false;
	private int scrollBarHeight = 0;
	String titles[] = null;
	private int entry = -1;
	private int screenType = -1;
	
	public BrowserScreen2(String[] titles,String[] channels){
		//#style contentsScreen
		super("图书-首页",titles,null);
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);	
		this.addCommand(cmdReturn);
		this.addCommand(cmdHelp);
		this.addCommand(cmdIndex);
		this.addCommand(cmdRefresh);
		this.addCommand(cmdSet);
		this.setCommandListener(this);
		
		
		this.titles = titles;
		screenWidth = this.getScreenFullWidth();
		screenHeight = this.getScreenFullHeight();
		init(channels);
		
	}
	public void setFocusedTab(boolean lock){
		this.isTabBarFocuse = lock;
	}
	public void setScreenType(int type){
		this.screenType = type;
	}
	public void setEntry(int entry){
		this.entry = entry;
	}
	public void init(String[] channels){
		
		
		this.channels = channels;
		tabCounts = channels.length;
		htmlBrowser = new HtmlBrowser[tabCounts];
		for(int i=0;i<tabCounts;i++){
			//#style browser
			htmlBrowser[i] = new HtmlBrowser();
			
			htmlBrowser[i].setCancleCommand(cmdCancle);

			this.append(i,htmlBrowser[i]);
		}
		this.setActiveTab(2,false);
		
	}
	public void switchTbs(int i){
		Runtime.getRuntime().gc();
		switch(this.screenType){
		case 0:
			this.setTitle("图书-"+titles[i]);
			break;
		case 1:
			this.setTitle("书架-"+titles[i]);
			break;
		}
		this.setActiveTab(i,false);
//		this.focus(htmlBrowser[i]);
	}
	public void switchTab(int i){
		Runtime.getRuntime().gc();
		switch(this.screenType){
		case 0:
			this.setTitle("图书-"+titles[i]);
			break;
		case 1:
			this.setTitle("书架-"+titles[i]);
			break;
		}
		System.out.println();
		this.setActiveTab(i,false);
		
		if(htmlBrowser[i].getItems().length == 0){
			/**计费测试*/
//			htmlBrowser[i].go("http://iread.wo.com.cn/pps/s.do?pg=c&gd=4051&ad=001&cd=3271&pd=250000000");
			htmlBrowser[i].go(channels[i]);
		
		}else{
//			System.out.println("bookMark...");
			htmlBrowser[i].scroll(Canvas.DOWN, htmlBrowser[i].getFocusedItem());
		}
	}
	
	public void refreshTab(int i){
		switch(this.screenType){
		case 0:
			this.setTitle("图书-"+titles[i]);
			break;
		case 1:
			this.setTitle("书架-"+titles[i]);
			break;
		}
		if(!htmlBrowser[i].isWorking())
			htmlBrowser[i].refresh(channels[i]);
	}
	public boolean handleKeyPressed(int keyCode,int gameAction){
		int focusedTab = this.getActiveTab();
		if(isTabBarFocuse || htmlBrowser[focusedTab].getFocusedIndex() == -1){
			if(gameAction == Canvas.LEFT){
				int index = this.getActiveTab();
				if(!htmlBrowser[index].isOpenProc()){
					if(index == 0){
						switchTab(tabCounts - 1);
					}else{
						index --;
						switchTab(index);
					}
				}
				
			}else if(gameAction == Canvas.RIGHT){
				int index = this.getActiveTab();
				if(!htmlBrowser[index].isOpenProc()){
					if(index == (tabCounts-1)){
						switchTab(0);
					}else{
						index ++;
						switchTab(index);
					}
				}
				
			}
			else if(gameAction == Canvas.DOWN){
				htmlBrowser[focusedTab].focusClosestItem(0);
				if(htmlBrowser[focusedTab].getFocusedIndex() != -1){
					isTabBarFocuse = false;
				}
			}
		}else{
			if(keyCode == Canvas.KEY_NUM0){
				htmlBrowser[focusedTab].setScrollYOffset(0, false);
				htmlBrowser[focusedTab].focusClosestItem(0);
			}
		    else if(gameAction == Canvas.UP || keyCode == Canvas.KEY_NUM2){
				if(htmlBrowser[focusedTab].getScrollYOffset() == 0){
					if(htmlBrowser[focusedTab].isFristFocusedItem()){
						htmlBrowser[focusedTab].focus(-1);
						
						isTabBarFocuse = true;
					}else{
						if(htmlBrowser[focusedTab].getFocusedItem() instanceof ChoiceGroup){
						      return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
						  }
						  boolean top = true;
						  int index = htmlBrowser[focusedTab].getFocusedIndex();
						  if(index == -1) return false;
						  do{
							  index--;
							  if(index < 0){
								  htmlBrowser[focusedTab].setScrollYOffset(0, false);
								  top = false;
								  break;
							  }
						  }while(!htmlBrowser[focusedTab].focus(index));
						  if(top){
							  htmlBrowser[focusedTab].scroll(0, htmlBrowser[focusedTab].getFocusedItem());
						  }
					}
					
				}else{	
					if(htmlBrowser[focusedTab].getFocusedItem() instanceof ChoiceGroup){
					      return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
					  }
					  boolean top = true;
					  int index = htmlBrowser[focusedTab].getFocusedIndex();
					  if(index == -1) return false;
					  do{
						  index--;
						  if(index < 0){
							  htmlBrowser[focusedTab].setScrollYOffset(0, false);
							  top = false;
							  break;
						  }
					  }while(!htmlBrowser[focusedTab].focus(index));
					  if(top){
						  htmlBrowser[focusedTab].scroll(0, htmlBrowser[focusedTab].getFocusedItem());
					  }
				}
			}else if(gameAction == Canvas.FIRE || keyCode == Canvas.KEY_NUM5){
				if(htmlBrowser[focusedTab].getFocusedItem() instanceof ChoiceGroup){			
				      return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
				  }
				StringItem anchor = (StringItem)htmlBrowser[focusedTab].getFocusedItem();
				String type = (String)anchor.getAttribute("type");
				String href = (String)anchor.getAttribute("href");
				String name = anchor.getText();
				String alertContent = "是否删除";
				if(type == null){
					return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
				}
				if(type.equals("link")){
					href = htmlBrowser[focusedTab].makeAbsoluteURL(href);
					if(name.equals("[删]")){
						Vector v = htmlBrowser[focusedTab].getMeta();
						int length = v.size();
						for(int i=0;i<length;i++){
							StringItem meta = (StringItem)v.elementAt(i);
							if(meta.getLabel().equals("bookmark")){
								alertContent = meta.getText();
								delUrl = htmlBrowser[focusedTab].makeAbsoluteURL(href);
								delType = 0;
								break;
							}else if(meta.getLabel().equals("favorite")){
								alertContent = meta.getText();
								delUrl = htmlBrowser[focusedTab].makeAbsoluteURL(href);
								delType = 1;
								break;
							}
						}
						showAlert(1,alertContent);
					}else if(href.indexOf("pg=r") != -1){
						href = htmlBrowser[focusedTab].makeAbsoluteURL(href);
						
						switch(screenType){
						case 0:
							ReaderControlScreen.getInstance().setEntry(0);
							break;
						case 1:
							ReaderControlScreen.getInstance().setEntry(3);
							break;
						}
					
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
						ReaderControlScreen.getInstance().init(0, href, null);
					}else if(href.indexOf("pg=d") !=-1){
						href = htmlBrowser[focusedTab].makeAbsoluteURL(href);
						
						NovelsReaderScreen.getInstance().setEntry(2);
						NovelsReaderScreen.getInstance().initCommand();
						MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
						NovelsReaderScreen.getInstance().setAddress(href,1);
					}else if(name.equals("名家:")){
						switchTab(4);
					}else if(name.equals("新书:")){
						switchTab(2);
					}
					else{
						return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
					}
				}else if(type.equals("submit")){
					if(name.equals("搜索")){
						String submit = htmlBrowser[focusedTab].getSubmitOfGetMethodUrl();
						BrowserScreen.getInstance().setTitle("搜索结果");
						
						BrowserScreen.getInstance().setEntry(2);
						MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
						BrowserScreen.getInstance().go(submit);
						
					}else{
						String submit = htmlBrowser[focusedTab].getSubmitOfGetMethodUrl();
						if(submit != null){
							submit = TextUtil.replace(submit,"td=353404","td=353423");
							htmlBrowser[focusedTab].go(submit);
						}
//						return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
					}
					
				}else{
					return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
				}
				
			}else if(gameAction == Canvas.LEFT || keyCode == Canvas.KEY_NUM4){
				return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
			}else if(gameAction == Canvas.RIGHT || keyCode == Canvas.KEY_NUM6){
				return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
			}else if(gameAction == Canvas.DOWN){
				if(htmlBrowser[focusedTab].getFocusedItem() instanceof ChoiceGroup){
				      return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
				  }
				 boolean isDown = true;
				  int index = htmlBrowser[focusedTab].getFocusedIndex();
//				  System.out.println("index:"+index);
				  if(index == -1) return false;
				  do{
					  index++;
					  if(index > htmlBrowser[focusedTab].getItemSize()){
//						  if(htmlBrowser[focusedTab].getContentHeight() > this.getAvailableHeight()){
//							  int height =-(htmlBrowser[focusedTab].getContentHeight() - this.getAvailableHeight()+25);
//							  this.setScrollYOffset(height, false);
							  isDown = false;
//						  }
						  break;
					  }
				  }while(!htmlBrowser[focusedTab].focus(index));
				  if(isDown){
					  htmlBrowser[focusedTab].scroll(0, htmlBrowser[focusedTab].getFocusedItem());
				  }
			}
			else{
				return htmlBrowser[focusedTab].handleKeyPressed(keyCode, gameAction);
			}
		}
		
		return true;
	}
	protected boolean handleKeyRepeated(int keyCode,
	          int action){
		  
		  return this.handleKeyPressed(keyCode, action);

	 }
	public void clear(){
		for(int i=0;i<tabCounts;i++){
			htmlBrowser[i].clear();
			htmlBrowser[i].releaseResources();
			htmlBrowser[i].clearMeta();
			
		}
	}
	public void doFresh(){
		int focusedTab = this.getActiveTab();
		htmlBrowser[focusedTab].refresh(htmlBrowser[focusedTab].getCurrentUrl());
	}
	public void commandAction(Command cmd, Displayable d) {
		int focusedTab = this.getActiveTab();
		if(cmd == cmdReturn){
			if(this.screenType == 0){
				if(htmlBrowser[focusedTab].canGoBack()){
					htmlBrowser[focusedTab].goBack();
				}else{
					if(entry == 0){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}else if(entry == 1){
						MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
					}else{
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}
					
				}
			}else if(this.screenType == 1){
				System.out.println("screenType:"+screenType+entry);
				if(entry == 0){
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				}else if(entry == 1){
					MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				}else{
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				}
			}
			
		
		}else if(cmd == cmdIndex){
//			clear();
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdRefresh){
			doFresh();
//			System.out.println("getfocused:"+this.f);
		}else if(cmd == cmdSet){
			switch(screenType){
			case 0:
				SystemSettingScreen.getInstance().setEntry(1);
				break;
			case 1:
				SystemSettingScreen.getInstance().setEntry(5);
				break;
			}
			
			SystemSettingScreen.getInstance().initSetting();
			MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
		}else if(cmd == cmdHelp){
			BrowserScreen.getInstance().setTitle("帮助");
			String url = "resource://help.html";
			MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
			switch(this.screenType){
			case 0:
				BrowserScreen.getInstance().setEntry(2);
				break;
			case 1:
				BrowserScreen.getInstance().setEntry(0);
				break;
			}
			BrowserScreen.getInstance().go(url);
		}else if(cmd == cmdOk){
			HttpConnectionUtil hcu = new HttpConnectionUtil(delUrl);
			hcu.setHttpConnectionListener(new MyHttpConnectionListener(6));
			hcu.start();
			
//			htmlBrowser[focusedTab].refresh(htmlBrowser[focusedTab].getCurrentUrl());
			
		}else if(cmd == cmdNo){
			MainMidlet.getInstance().display.setCurrent(this);
			this.setScrollYOffset(scrollBarHeight, false);
		}else if(cmd == cmdCancle){
			htmlBrowser[focusedTab].cancelRequest();
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
			alert.addCommand(cmdNo);
			alert.addCommand(cmdOk);
			alert.setTimeout(Alert.FOREVER);
		}
		
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	public void delBookStore(){
		int focusedTab = this.getActiveTab();
		if(delType == 0){
			showAlert(0,"书签删除成功!");
			int index = htmlBrowser[focusedTab].getFocusedIndex();
			htmlBrowser[focusedTab].remove(index);
			index--;
			htmlBrowser[focusedTab].remove(index);
			index--;
			htmlBrowser[focusedTab].focus(index);
		}else if(delType == 1){
			showAlert(0,"收藏删除成功!");
			int index = htmlBrowser[focusedTab].getFocusedIndex();
			htmlBrowser[focusedTab].remove(index);
			index--;
			htmlBrowser[focusedTab].remove(index);
			index--;
			htmlBrowser[focusedTab].focus(index);
		}
	}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(0xFFFFFF);
		if(htmlBrowser[this.getActiveTab()].isOpenProc()){
			tempData.DrawGauge(g,htmlBrowser[this.getActiveTab()].getProcValue(),htmlBrowser[this.getActiveTab()].getProcInfo());
		}
	}
}
