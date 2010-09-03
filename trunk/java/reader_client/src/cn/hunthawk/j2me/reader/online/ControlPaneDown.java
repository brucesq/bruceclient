package cn.hunthawk.j2me.reader.online;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.LocalBookContents;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.List;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.TextUtil;

public class ControlPaneDown extends List
implements CommandListener{
	
	public static ControlPaneDown cp = null;
	private int totalCommands = 0;
	public ControlPaneDown()
	{
		//#style controlpanedown	
		super(null,List.IMPLICIT);
		this.setCommandListener(this);
	}
	
	public static ControlPaneDown getInstance(){
		if(cp == null){
			cp = new ControlPaneDown();
		}
		return cp;
	}
	public void init(){
		int bookType = NovelsReaderScreen.getInstance().getBookCurType();
		totalCommands = 0;
		switch(bookType){
		case 0:{
			this.deleteAll();
			int currentPage = NovelsReaderScreen.getInstance().getCurrentPageID();
			int currentChapter = 0;
			int totalPage = 0;
			int totalChapter =0;
			Vector v = NovelsReaderScreen.getInstance().getBrowser().getMeta();
			if(v != null){
				int length = v.size();
				for(int i=0;i<length;i++){
					StringItem meta = (StringItem)v.elementAt(i);
					if(meta.getLabel().equals("currentCapterID")){
						currentChapter = Integer.parseInt(meta.getText());
					}else if(meta.getLabel().equals("totalCapter")){
						totalChapter = Integer.parseInt(meta.getText());
					}else if(meta.getLabel().equals("totalPage")){
						totalPage = Integer.parseInt(meta.getText());
					}
				}	
			}
			
			if(currentPage != totalPage){
				//#style mainMenu1
				this.append("下页", null);
				totalCommands++;
			}
			if(currentChapter != totalChapter){
				if(totalCommands == 0){
					//#style mainMenu1
					this.append("下章", null);
					totalCommands++;
				}else{
					//#style mainMenu
					this.append("下章", null);
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
			Vector page = NovelsReaderScreen.getInstance().getBrowser().getLocalCurrentChapterContainer();
			Vector chapters = tempData.currentLocalBook.getBookInfo().getContents();
			int currentPage = NovelsReaderScreen.getInstance().getCurrentPageID();
			int currentChapter = NovelsReaderScreen.getInstance().getCurrentChapterID();
			int totalPage = page.size();
			int totalChapter =chapters.size();
			if(currentPage != totalPage){
				//#style mainMenu1
				this.append("下页", null);
				totalCommands++;
			}
			System.out.println("current:"+currentChapter+"totalChapter:"+totalChapter);
			if(currentChapter != totalChapter){
				if(totalCommands == 0){
					//#style mainMenu1
					this.append("下章", null);
					totalCommands++;
				}else{
					//#style mainMenu
					this.append("下章", null);
					totalCommands++;
				}
			}
			//#style mainMenu
			this.append("目录", null);
			totalCommands++;
			break;
		}
		
	}
	public boolean handleKeyPressed(int keyCode,int gameAction){
		int index = this.getSelectedIndex();
		String type = this.getString(this.getSelectedIndex());
//		System.out.println("type:"+type);
		if(gameAction == Canvas.LEFT){
			if(index == 0){
				int offset = NovelsReaderScreen.getInstance().getScrollYOffset();
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().setScrollYOffset(offset, true);
			}else{
				index--;
				this.setSelectedIndex(index, true);
			}
		}else if(gameAction == Canvas.RIGHT){
			if(index == totalCommands-1){
				int offset = NovelsReaderScreen.getInstance().getScrollYOffset();
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().setScrollYOffset(offset, true);
			}else{
				index++;
				this.setSelectedIndex(index, true);
			}
		}else if(gameAction == Canvas.UP){
			int offset = NovelsReaderScreen.getInstance().getScrollYOffset();
			MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			NovelsReaderScreen.getInstance().setScrollYOffset(offset, true);
		}else if(gameAction == Canvas.DOWN){
			
		}
		else{
			super.handleKeyPressed(keyCode, gameAction);
		}
		return true;
	}

	
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == List.SELECT_COMMAND){
			String type = this.getString(this.getSelectedIndex());
			if(type.equals("下页")){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().pageDown();
				
			}else if(type.equals("下章")){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
				NovelsReaderScreen.getInstance().ChapterDown();
				
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
			rd = "rd=" + zd.substring(3, zd.length()-3);
		}
		
		url = TextUtil.replaceFirst(url, zd, rd);
		url = TextUtil.replaceFirst(url, "pg=d", "pg=r");
		return url;
	}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(0xff0000);
		g.drawString(NovelsReaderScreen.getInstance().getDisplayInfo(), this.getWidth()-10, this.getScreenFullHeight()-3, 40);
	}
}
