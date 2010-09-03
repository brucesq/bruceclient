package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.ui.item.ContainerDownloadedBookList;
import cn.hunthawk.j2me.ui.item.ItemBookList;
import cn.hunthawk.j2me.util.RmsOpt;
import de.enough.polish.ui.Form;

public class SearchResultScreen extends Form 
implements CommandListener{
	
	public static SearchResultScreen resultScreen = null;

	private ItemBookList item_book= null;
	private Vector vBook = null;
	
	public Command cmdReturn = new Command("返回",Command.BACK,1);
	private ContainerDownloadedBookList conDownloadedBookList = null;
	private String name = null;
	public SearchResultScreen(){
		//#style mainScreen
		super("搜索结果");
		this.addCommand(cmdReturn);
		this.setCommandListener(this);
		
		conDownloadedBookList = new ContainerDownloadedBookList();
		this.append(conDownloadedBookList);
	}
	public static SearchResultScreen getInstance(){
		if(resultScreen == null){
			resultScreen = new SearchResultScreen();
		}
	
		return resultScreen;
	}
	public  void init(Vector result){
		this.vBook = result;
		conDownloadedBookList.clear();
		int length = result.size();
		System.out.println("read downloadbook:"+length);
		for(int i=0;i<length;i++){
			System.out.println("read downloadbook");
			DownloadBook db = (DownloadBook)result.elementAt(i);
			
			item_book = new ItemBookList(db.getUebName()+"\n"+"大小:"+db.getFileSize()+"K"+"\n"+db.getSaveDate(),i,db.getUebImage());
			
			conDownloadedBookList.add(item_book);
		}
	}
	
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdReturn){
			MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
		}
		
	}
	
	public boolean handleKeyPressed(int keyCode,int action){
		if(action == Canvas.DOWN){
			int tt = conDownloadedBookList.getFocusedIndex();
			if(tt != -1){
				tt++;
				System.out.println("tt:"+tt);
				if(tt < conDownloadedBookList.size()){
					conDownloadedBookList.focus(tt);
				}
			}
		}else if(action == Canvas.UP){
			int tt = conDownloadedBookList.getFocusedIndex();
			if(tt != -1){
				tt--;
				System.out.println("tt1:"+tt);
				if(tt >= 0){
					conDownloadedBookList.focus(tt);
				}
			}
		}else if(action == Canvas.FIRE){
			MainMidlet.getInstance().display.setCurrent(LoadPageScreen.getInstance());
			DownloadBook db = (DownloadBook)vBook.elementAt(conDownloadedBookList.getFocusedIndex());
			 name = db.getUebName();
		    new Thread(){
		    	public void run(){
		    		
					LocalBook lb = new LocalBook(name);
					if(lb != null){
						
						ReaderControlScreen.getInstance().setLocalBook(lb);
						ReaderControlScreen.getInstance().init(1,null,null);
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());

						System.gc();
						
					}else{
//						System.out.println("error content format");
					}
		    	}
		    }.start();
		}
		return true;
	}
	
}
