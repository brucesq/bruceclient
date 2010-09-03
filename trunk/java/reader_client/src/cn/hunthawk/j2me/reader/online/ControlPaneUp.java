package cn.hunthawk.j2me.reader.online;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import de.enough.polish.ui.List;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.TextUtil;

public class ControlPaneUp extends List
implements CommandListener{
	
	public static ControlPaneUp cp = null;
	private int totalCommands = 0;
	public ControlPaneUp()
	{
		//#style controlpane	
		super(null,List.IMPLICIT);
		
		this.setCommandListener(this);
	}
	public void init(){
		int bookType = NovelsReaderScreen.getInstance().getBookCurType();
		totalCommands = 0;
		switch(bookType){
		case 0:{
			this.deleteAll();
			int currentPage = NovelsReaderScreen.getInstance().getCurrentPageID();
			int currentChapter = 0;
			Vector v = NovelsReaderScreen.getInstance().getBrowser().getMeta();
			if(v != null){
				int length = v.size();
				for(int i=0;i<length;i++){
					StringItem meta = (StringItem)v.elementAt(i);
					if(meta.getLabel().equals("currentCapterID")){
						currentChapter = Integer.parseInt(meta.getText());
						break;
					}
				}	
			}
			if(currentPage != 1){
				//#style mainMenu1
				this.append("上页", null);
				totalCommands++;
			}
			if(currentChapter != 1){
				if(totalCommands == 0){
					//#style mainMenu1
					this.append("上章", null);
					totalCommands++;
				}else{
					//#style mainMenu
					this.append("上章", null);
					totalCommands++;
				}
				
			}
			//#style mainMenu
			this.append("目录", null);
			totalCommands++;
			
			
			break;
		}
		case 1:
			this.deleteAll();
			int currentPage = NovelsReaderScreen.getInstance().getCurrentPageID();
			int currentChapter = NovelsReaderScreen.getInstance().getCurrentChapterID();
			if(currentPage != 1){
				//#style mainMenu1
				this.append("上页", null);
				totalCommands++;
			}
			if(currentChapter != 2){
				if(totalCommands == 0){
					//#style mainMenu1
					this.append("上章", null);
					totalCommands++;
				}else{
					//#style mainMenu
					this.append("上章", null);
					totalCommands++;
				}
			}
			//#style mainMenu
			this.append("目录", null);
			totalCommands++;
			break;
		}
		
		
	}
	public static ControlPaneUp getInstance(){
		if(cp == null){
			cp = new ControlPaneUp();
		}
		return cp;
	}
	public boolean handleKeyPressed(int keyCode,int gameAction){
		int index = this.getSelectedIndex();
		
		if(gameAction == Canvas.LEFT){
			if(index == 0){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().backTop();
			}else{
				index--;
				this.setSelectedIndex(index, true);
			}
		}else if(gameAction == Canvas.RIGHT){
			if(index == totalCommands-1){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().backTop();
			}else{
				index++;
				this.setSelectedIndex(index, true);
			}
		}else if(gameAction == Canvas.UP){
			
		}else if(gameAction == Canvas.DOWN){
			MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			NovelsReaderScreen.getInstance().backTop();
			
		}
		else{
			super.handleKeyPressed(keyCode, gameAction);
		}
		return true;
	}
	
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == List.SELECT_COMMAND){
			
			String type = this.getString(this.getSelectedIndex());
			if(type.equals("上页")){
				
				NovelsReaderScreen.getInstance().pageUp();
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				
			}else if(type.equals("上章")){
				
				NovelsReaderScreen.getInstance().ChapterUp();
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				
			}else if(type.equals("目录")){
				if(ReaderControlScreen.readerControlScreen != null){
					ReaderControlScreen.getInstance().switchTab(1);
					MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
				}else{
					String base = NovelsReaderScreen.getInstance().getBrowser().getCurrentUrl();
					String parent = handlePreUrl(base);
					System.out.println("parset:"+parent);
					ReaderControlScreen.getInstance().init(0, parent, null);
					ReaderControlScreen.getInstance().setBase(parent);
					ReaderControlScreen.getInstance().switchTab(1);
					MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
				}
				
			}
		}
		
	}
	
	public String handlePreUrl(String url){
		int pos = url.indexOf("zd=");
		String zd = null;
		String rd = null;
		String test  = url.substring(pos);
		if(test.indexOf("&") != -1){
			zd = test.substring(0,test.indexOf("&"));
//			System.out.println("page current1:"+url);
			rd = "rd=" + zd.substring(3, zd.length()-3);
			
			
		}else{
			zd = test.substring(3);
//			System.out.println("page current2:"+url);
			rd = "rd=" + zd.substring(3, zd.length()-3);
		}
		
		url = TextUtil.replaceFirst(url, zd, rd);
		url = TextUtil.replaceFirst(url, "pg=d", "pg=r");
		return url;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(0xff0000);
		g.drawString(NovelsReaderScreen.getInstance().getDisplayInfo(), this.getWidth()-10, 3, 24);
	}
}
