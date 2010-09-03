package cn.hunthawk.j2me.ui.screen;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.html.BrowserListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TreeItem;
import de.enough.polish.util.Locale;

public class TopScreen extends TabbedForm 
implements CommandListener,ItemCommandListener{
	public static TopScreen topScreen = null;
	private Command cmdReturn = new Command(Locale.get("cmd.return"),Command.BACK,1);
	private Command cmdRefresh = new Command(Locale.get("cmd.refresh"),Command.ITEM,2);
	private Command cmdSet = new Command(Locale.get("cmd.set1"),Command.ITEM,7);
	private Command cmdHelp = new Command(Locale.get("cmd.help1"),Command.ITEM,8);
	private Command cmdIndex =new Command(Locale.get("cmd.index"),Command.ITEM,9);
	
	private HtmlBrowser getInfoBrowser = null;
	public static String tabNames[] = {"图书"};
	private int tabCounts = 0;
	private StringItem topButton = null;
	private String topNames[] = {"点击量排行","搜索量排行","收藏量排行","留言量排行"};
	private String subNames[] = {"总排行","月排行","周排行","日排行"};

	private TreeItem tree = null;
	private StringItem treeItem = null;
	private String urls;
	boolean isrun = false;
	String runInfo = null;
	int runValue = 0;
//	private Form frm = new Form(null);
	
	
	public TopScreen(){
		//#style contentsScreen
		super("排行榜",tabNames,null);
		this.addCommand(cmdHelp);
		this.addCommand(cmdIndex);
		this.addCommand(cmdRefresh);
		this.addCommand(cmdReturn);
		this.addCommand(cmdSet);
		this.setCommandListener(this);
		
		getInfoBrowser = new HtmlBrowser();
		
//		frm.append(getInfoBrowser);
		//#style bookBagTree
		tree = new TreeItem(null);
		this.append(0,tree);
		
	}
	public static TopScreen getInstance(){
		if(topScreen  == null){
			topScreen = new TopScreen();
		}
		return topScreen;
	}
	public void initTab(String url){
		this.urls = url;
		for(int i=0;i<4;i++){
			//#style bagRoot
			topButton = new StringItem(null,topNames[i],Item.HYPERLINK);
			topButton.setAttribute("key", "top");		
			tree.appendToRoot(topButton);
			for(int j=0;j<4;j++){
				//#style bagNode
				treeItem = new StringItem(null,subNames[j],Item.HYPERLINK);
				
				treeItem.setAttribute("key", "second");
				String od = null;
				String href = null;
				switch(i){
				case 0:
					od = "&od=6";
					break;
				case 1:
					od = "&od=2";
					break;
				case 2:
					od = "&od=3";
					break;
				case 3:
					od = "&od=5";
					break;
				}
				String ods = "&ods="+(j+1);
				href = urls+od+ods;
				System.out.println("href:"+href);
				treeItem.setAttribute("href", href);
				tree.appendToNode(topButton, treeItem);
			}
			
		}
		this.focus(0);
		System.gc();
	}
	public boolean handleKeyPressed(int keyCode,int gameAction){
		if(gameAction == Canvas.FIRE){
			if(getInfoBrowser.isWorking()){
				return false;
			}
			
			Item[] items = this.tree.getSelectedPath();
			int size = items.length;
			String key = (String)items[size-1].getAttribute("key");
			
			if(key != null){
				if(key.equals("second")){
					String href = (String)items[size-1].getAttribute("href");
					addTreeNote(items[size-1],href);
				}
				System.out.println("default");
				return super.handleKeyPressed(keyCode, gameAction);
			}else{
				String type = (String)items[size-1].getAttribute("type");
				if(type != null){
					
					String href = (String)items[size-1].getAttribute("href");
					href = this.makeAbsoluteURL(href);
//					return true;
					if(href.indexOf("pg=r")!=-1){
						
						ReaderControlScreen.getInstance().setEntry(1);
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
						ReaderControlScreen.getInstance().init(0, href, null);
					}else{
						BrowserScreen.getInstance().go(href);
						BrowserScreen.getInstance().setEntry(3);
						BrowserScreen.getInstance().setTitle("更多排行");
						MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
					}	
				}
			}

		}
		else if(gameAction == Canvas.LEFT){
			int index = this.getActiveTab();
			if(index == 0){
//				switchTab(tabCounts - 1);
			}else{
				index --;
//				switchTab(index);
			}
		
		}else if(gameAction == Canvas.RIGHT){
			int index = this.getActiveTab();
			if(index == (tabCounts-1)){
//				switchTab(0);
			}else{
				index ++;
//				switchTab(index);
			}
		}else{
			
			return super.handleKeyPressed(keyCode, gameAction);
		}
		
		return true;
		
	}
	public String makeAbsoluteURL(String url){
		return getInfoBrowser.makeAbsoluteURL(url);
	}
	public void addTreeNote(Item item,String href){
		setRunState(true,"请求服务器..",20);
		Tools.sleep(1000);

//		repaint();
		getInfoBrowser.go(href);
	
		do{
			Tools.sleep(100);
//			setRunState(true,"请求服务器..",45);
			repaint();
		}while(getInfoBrowser.isWorking());
		setRunState(true,"处理数据..",70);
		Item items[] = getInfoBrowser.getItems();
		int length = items.length;
		for(int j=0;j<length;j++){
			StringItem strItem = (StringItem)items[j];
			if(strItem.getText() != null){
				String label =strItem.getText();
				    int beginIndex = 0;
	    			int endIndex = 0;
	    			do{
	    				endIndex = label.indexOf((char)13);
	    				if(endIndex == -1) break;
	    				label = label.substring(beginIndex, endIndex)+label.substring(endIndex+1, label.length());
	    			}while(label.indexOf((char)13) != -1);
	    			beginIndex = 0;
	    			endIndex = 0;
	    			do{
	    				endIndex = label.indexOf((char)9);
	    				if(endIndex == -1) break;
	    				label = label.substring(beginIndex, endIndex)+label.substring(endIndex+1, label.length());
	    			}while(label.indexOf((char)9) != -1);
	    			strItem.setText(label);
				tree.appendToNode(item, items[j]);
			}
		}
		setRunState(false,"处理数据..",45);
		item.setAttribute("key", "second1");
	}
	public void commandAction(Command cmd,Displayable d){
		
		if(cmd == cmdReturn){
			System.gc();
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdSet){
			SystemSettingScreen.getInstance().setEntry(4);
			MainMidlet.getInstance().display.setCurrent(SystemSettingScreen.getInstance());
		}else if(cmd == cmdIndex){
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}else if(cmd == cmdRefresh){
			
		}else if(cmd == cmdHelp){
			BrowserScreen.getInstance().setTitle("帮助");
			String url = "resource://help.html";
			BrowserScreen.getInstance().setEntry(3);
			MainMidlet.getInstance().display.setCurrent(BrowserScreen.getInstance());
			//
			BrowserScreen.getInstance().go(url);
		}
	}
	
	public void commandAction(Command cmd, Item item) {
		// TODO Auto-generated method stub
		System.out.println("item action!");
		
	}
	
	public void setRunState(boolean run,String info,int value){
		this.isrun = run;
		this.runInfo = info;
		this.runValue = value;
		repaint();
	}
	public void paint(Graphics g){
		super.paint(g);
//		if(isrun){
//			System.out.println("paint");
//			tempData.DrawGauge(g,runValue,runInfo);
//		}
		if(getInfoBrowser.isOpenProc()){
			System.out.println("paint");
			tempData.DrawGauge(g,getInfoBrowser.getProcValue(),getInfoBrowser.getProcInfo());
		}
		
	}

}
