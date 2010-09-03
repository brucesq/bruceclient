package cn.hunthawk.j2me.ui.screen;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.gzip.GZIP;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Locale;

public class BrowserScreen extends Form 
implements CommandListener{
	private static BrowserScreen browserScreen = null;
	private Command cmdCancle = new Command("取消",Command.BACK,1);
	private Command cmdReturn = new Command(Locale.get("cmd.return"),Command.BACK,2);
	private Command cmdRefresh = new Command(Locale.get("cmd.refresh"),Command.ITEM,2);
	private Command cmdSet = new Command(Locale.get("cmd.set1"),Command.ITEM,7);
	private Command cmdHelp = new Command(Locale.get("cmd.help1"),Command.ITEM,8);
	private Command cmdIndex =new Command(Locale.get("cmd.index"),Command.ITEM,9);
	private HtmlBrowser htmlBrowser = null;
	
	private Command cmdSelect = new Command("进        入",Command.ITEM,1);
	
	private int entry = 0;
	private String screenType = null;
	public BrowserScreen(){
		//#style mainScreen
		super("主页");
		//#debug
		System.out.println("point4");
		
		//#style browser
		htmlBrowser = new HtmlBrowser();

		htmlBrowser.setCancleCommand(cmdCancle);

		this.append(htmlBrowser);
		this.addCommand(cmdReturn);

		this.addCommand(cmdSelect);
		this.addCommand(cmdHelp);
		this.addCommand(cmdIndex);
		this.addCommand(cmdRefresh);
		this.addCommand(cmdSet);
		this.setCommandListener(this);
		
	}
	
	public static BrowserScreen getInstance(){
		if(browserScreen == null){
			browserScreen = new BrowserScreen();
		}
		return browserScreen;
	}
	public void setEntry(int j){
		this.entry = j;
	}
	
	public void go(String url){
		htmlBrowser.clear();
		htmlBrowser.clearHistory();
		htmlBrowser.go(url);
	}
	public void goback(){
		htmlBrowser.goBack();
	}
	public void doFresh(){
		htmlBrowser.refresh(htmlBrowser.getCurrentUrl());
	}
	public void setScreenType(String type){
		this.screenType = type;
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdReturn){
			if(htmlBrowser.canGoBack()){
				htmlBrowser.goBack();
			}else{
				htmlBrowser.releaseResources();
				htmlBrowser.clearHistory();
				htmlBrowser.clear();
				if(entry == 0){
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
				}else if(entry == 1){
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				}else if(entry == 2){
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
				}else if(entry == 3){
					MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
				}else if(entry == 4){
					MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
				}
				
			}
		}else if(cmd == cmdIndex){
			htmlBrowser.releaseResources();
			htmlBrowser.clearHistory();
			htmlBrowser.clear();
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdRefresh){
			doFresh();
		}else if(cmd == cmdSet){
			SystemSettingScreen.getInstance().setEntry(1);
			SystemSettingScreen.getInstance().initSetting();
			MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
		}else if(cmd == cmdHelp){
			this.setTitle("帮助");
			String url = "resource://help.html";
			htmlBrowser.go(url);
		}else if(cmd == cmdCancle){
			htmlBrowser.cancelRequest();
		}
	}

	protected boolean handleKeyPressed(int keyCode, int action){
		if(action == Canvas.FIRE){
			if(htmlBrowser.getFocusedItem() instanceof ChoiceGroup){
//				System.out.println("getFocusedItem is ChoiceGroup return default keyPressed");
			      return htmlBrowser.handleKeyPressed(keyCode, action);
			  }
			StringItem anchor = (StringItem)htmlBrowser.getFocusedItem();
			String type = anchor.getText();
			String href = (String)anchor.getAttribute("href");

			if(href != null){
				if(href.indexOf("pg=r") != -1){
					href = htmlBrowser.makeAbsoluteURL(href);
					
					if(href.indexOf("&rt=1") != -1){
						href = href.substring(0, href.indexOf("&rt=1"));
					}
					ReaderControlScreen.getInstance().init(0, href, null);
					if(screenType != null){
						if(!this.screenType.equals("author")){
							ReaderControlScreen.getInstance().setEntry(4);
						}
					}else{
						ReaderControlScreen.getInstance().setEntry(4);
					}
					
					MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
				}
				else{
//					System.out.println("other!");
					return htmlBrowser.handleKeyPressed(keyCode, action);
				}
			}else{
				return htmlBrowser.handleKeyPressed(keyCode, action);
			}
		}
		else if(action == Canvas.DOWN){
			if(htmlBrowser.getFocusedItem() instanceof ChoiceGroup){
			      return htmlBrowser.handleKeyPressed(keyCode, action);
			  }
			htmlBrowser.Down();
		}
		else if(action == Canvas.UP){
			if(htmlBrowser.getFocusedItem() instanceof ChoiceGroup){
//				System.out.println("getFocusedItem is ChoiceGroup return default keyPressed");
			      return htmlBrowser.handleKeyPressed(keyCode, action);
			  }
			  boolean top = true;
			  int index = htmlBrowser.getFocusedIndex();
			  do{
				  index--;
				  if(index < 0){
					  htmlBrowser.setScrollYOffset(0, false);
					  top = false;
					  break;
				  }
			  }while(!htmlBrowser.focus(index));
			  if(top){
				  htmlBrowser.scroll(0, htmlBrowser.getFocusedItem());
			  }
		}
		else{
			return htmlBrowser.handleKeyPressed(keyCode, action);
		}
		return true;
	}
	protected boolean handleKeyRepeated(int keyCode,
	          int action){
		  
		  return this.handleKeyPressed(keyCode, action);

	 }
	public void paint(Graphics g){
		super.paint(g);
//		g.drawString(tempData.getTime(), 235, 3, 24);
		if(htmlBrowser.isOpenProc()){
			tempData.DrawGauge(g,htmlBrowser.getProcValue(),htmlBrowser.getProcInfo());
		}
	}
}
