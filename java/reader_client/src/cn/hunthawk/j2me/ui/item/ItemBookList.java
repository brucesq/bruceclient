package cn.hunthawk.j2me.ui.item;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.ui.screen.LoadPageScreen;
import cn.hunthawk.j2me.util.RmsOpt;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;

public class ItemBookList extends Container{
	private StringItem item = null;
	private String Longname = null;
	private String Shortname = null;
	private ImageItem imageItem = null;
	public ItemBookList(String itemName,int indexValue,Image image){
		//#style contentsContainer
		super(false);
		this.Longname = itemName;
		this.Shortname = itemName.substring(0, itemName.indexOf("\n"));
		//#style contentsContainerItem
		item= new StringItem(null, itemName, Item.BUTTON);
		item.setAttribute("index", String.valueOf(indexValue));
		imageItem = new ImageItem(null,image,0,null);
		add(item);
		allowCycling = false;
	}
	
	public boolean handleKeyPressed(int keyCode, int action) {
		if(action == Canvas.FIRE){
			
		    MainMidlet.getInstance().display.setCurrent(LoadPageScreen.getInstance());

		    new Thread(){
		    	public void run(){
		    		int index = Integer.parseInt((String)item.getAttribute("index"));
					Vector v = (Vector)RmsOpt.readRMS("downloadbook");
					DownloadBook db = (DownloadBook)v.elementAt(index);
					String uebFile =db.getUebName();
					LocalBook lb = new LocalBook(uebFile);
					if(lb != null){
						ReaderControlScreen.getInstance().readerControlScreen = null;
						
						ReaderControlScreen.getInstance().setLocalBook(lb);
						ReaderControlScreen.getInstance().init(1,null,null);
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());

						System.gc();
						
					}else{
//						System.out.println("error content format");
					}
		    	}
		    }.start();
			
			
		}else if(action == Canvas.DOWN){
//			System.out.println("action contentsContainer");
		}
		return super.handleKeyPressed(keyCode, action);
	}
	public void setFocusedName(){
		item.setText(Longname);
		this.add(0, imageItem);
	}
	public void setOtherName(){
		item.setText(Shortname);
		this.remove(imageItem);
	}
	public int getItemIndex(){
		return Integer.parseInt((String)item.getAttribute("index"));
	}
	public String getShortName(){
		return this.Shortname;
	}
}
