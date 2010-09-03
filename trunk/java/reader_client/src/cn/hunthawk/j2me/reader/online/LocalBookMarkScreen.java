package cn.hunthawk.j2me.reader.online;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.LocalBookHeaderInfo;
import cn.hunthawk.j2me.bo.LocalBookMark;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.List;

public class LocalBookMarkScreen extends List
implements CommandListener{

	public static LocalBookMarkScreen markScreen = null;
	private Command cmdReturn = new Command("返回",Command.BACK,1);
	private Command cmdRead = new Command("读取",Command.ITEM,1);
	private Command cmdDelete = new Command("删除",Command.ITEM,2);
	String packageID = null;
	int entry = -1;
	
	public LocalBookMarkScreen(){
		//#style markScreen
		super("本书书签",List.IMPLICIT);
		
		this.addCommand(cmdDelete);
		this.addCommand(cmdRead);
		this.addCommand(cmdReturn);
		this.setCommandListener(this);
		
		init();
	}
	public void setEntry(int entry){
		this.entry = entry;
	}
	public static LocalBookMarkScreen getInstance(){
		if(markScreen == null){
			markScreen = new LocalBookMarkScreen();
		}
		return markScreen;
	}
	public void init(){
		this.deleteAll();
		boolean isnull = true;
		LocalBookHeaderInfo lbhi = tempData.currentLocalBook.getBookInfo().getHeader();
		if(lbhi != null){
			
			packageID = String.valueOf(lbhi.getPackageID());
		}
		Vector marks = (Vector)RmsOpt.readRMS("userbookmark");
		if(!marks.isEmpty()){
			int length = marks.size();
			for(int i=0;i<length;i++){
				Vector vitem = (Vector)marks.elementAt(i);
				String head = (String)vitem.elementAt(0);
				if(head.equals(packageID)){
//					Vector mark = (Vector)vitem.elementAt(1);
					
						LocalBookMark lbm = (LocalBookMark)vitem.elementAt(1);
						if(lbm != null){
							isnull = false;
							System.out.println("lbm:"+lbm.getText());
							if(lbm.getText().length() >8){
								//#style browserLink
								this.append(lbm.getTitle()+"--"+lbm.getText().substring(0, 7).trim()+"...", null);
							}else{
								//#style browserLink
								this.append(lbm.getTitle()+"--"+lbm.getText().trim(), null);
							}
						}
						
						
					
					break;
				}
				
			}
		}
		
		if(isnull){
			//#style browserLink
			this.append("您还未对本书添加书签!", null);
			
		}
	}
	public void commandAction(Command cmd, Displayable d) {
	    if(cmd == List.SELECT_COMMAND || cmd == cmdRead){
	    	int index = this.getSelectedIndex();
	    	Vector marks = (Vector)RmsOpt.readRMS("userbookmark");
	    	if(!marks.isEmpty()){
	    		int length = marks.size();
				for(int i=0;i<length;i++){
					Vector vitem = (Vector)marks.elementAt(i);
					String head = (String)vitem.elementAt(0);
					if(head.equals(packageID)){
						
						LocalBookMark lbm = (LocalBookMark)vitem.elementAt(1);
						loadBookMark(lbm);
						break;
						
					}
				}
	    	}
	    	
	    }
		else if(cmd == cmdReturn){
			if(entry == 0){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			}else if(entry == 1){
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}
			
		}else if(cmd == cmdDelete){
			int index = this.getSelectedIndex();
			Vector marks = (Vector)RmsOpt.readRMS("userbookmark");
	    	if(!marks.isEmpty()){
	    		int length = marks.size();
				for(int i=0;i<length;i++){
					Vector vitem = (Vector)marks.elementAt(i);
					String head = (String)vitem.elementAt(0);
					if(head.equals(packageID)){
						
						marks.removeElementAt(i);
						
						RmsOpt.saveRMS(marks, "userbookmark");
						break;
					}
				}
	    	}
			
			init();
		}
		
	}
	
	public void loadBookMark(LocalBookMark mark){
		
		int chapter = mark.getChapter();
		int wordset = NovelsReaderScreen.getInstance().getBrowser().getUserSetWord();	
		int rate1 = wordset / 500 ;
		int page = (mark.getPage() -1)/rate1 + 1;
		String text = mark.getText();
		NovelsReaderScreen.getInstance().loadBookMark(chapter, page-1, text,mark.getDistance());
		
		
	}
}
