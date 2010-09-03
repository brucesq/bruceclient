package cn.hunthawk.j2me.ui.item;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.LocalBookContents;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;

public class ItemContents extends Container{
	
	StringItem item = null;
	Vector items = new Vector();
	LocalBookContents lbc = null;
	public ItemContents(String itemName,int indexValue,int type){
		//#style contentsContainer
		super(false);
		
		//#style contentsContainerItem
		item= new StringItem(null, itemName, Item.BUTTON);
		item.setAttribute("index", String.valueOf(indexValue));
		item.setAttribute("type", String.valueOf(type));
		add(item);
		allowCycling = false;
		
	}
	
	protected boolean handleKeyPressed(int keyCode, int action) {
		if(action == Canvas.FIRE){
//			System.out.println("item:"+item.getText()+"index:"+item.getAttribute("index"));
			int type = Integer.parseInt((String)item.getAttribute("type"));
			if(type == 1){
				items = tempData.currentLocalBook.getBookInfo().getContents();
				int index = Integer.parseInt((String)item.getAttribute("index"));
				lbc = (LocalBookContents)items.elementAt(index);
				//#debug
				System.out.println("local index:"+index);
				NovelsReaderScreen.getInstance().ContentsToReader(tempData.currentLocalBook,Integer.valueOf(lbc.getChapterPlayOrder()).intValue());
				NovelsReaderScreen.getInstance().setEntry(1);
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			}
			
			
		}else{
			super.handleKeyPressed(keyCode, action);
		}
		
		return super.handleKeyPressed(keyCode, action);
	}
}
