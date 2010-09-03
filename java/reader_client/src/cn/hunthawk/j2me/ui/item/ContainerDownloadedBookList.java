package cn.hunthawk.j2me.ui.item;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.ui.screen.LoadPageScreen;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;

public class ContainerDownloadedBookList extends Container{
	
	private Vector v = new Vector();
	private int focusedIndex = -1;
	private int prevFocusedIndex = -2;
	int index = -1;
	String name = null;
	public ContainerDownloadedBookList(){
		//#style bookListScreen
		super(false);
		
		this.allowCycling = false;
	}
	public void add(Item item){
		super.add(item);
		if(item instanceof ItemBookList){
			v.addElement(item);
			prevFocusedIndex = -2;
//			System.out.println("add..");
		}
		
	}
	public void clear(){
		super.clear();
		v.removeAllElements();
	}
	public boolean handleKeyPressed(int keyCode, int action){
		if(action == Canvas.FIRE){
			MainMidlet.getInstance().display.setCurrent(LoadPageScreen.getInstance());
			ItemBookList book = (ItemBookList)this.getFocusedItem();
			name = book.getShortName();
		    new Thread(){
		    	public void run(){
					LocalBook lb = new LocalBook(name);
					if(lb != null){
						ReaderControlScreen.getInstance().setLocalBook(lb);
						ReaderControlScreen.getInstance().init(1,null,null);
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
						System.gc();
						
					}else{
					}
		    	}
		    }.start();
		}
		return true;
	}
	public void paint(int x, int y, int width, int height, Graphics g){
		focusedIndex = this.getFocusedIndex();
		if(focusedIndex != prevFocusedIndex){
			if(!v.isEmpty()){
				for(int i=0;i<v.size();i++){
					ItemBookList book = (ItemBookList)v.elementAt(i);
					if(this.getFocusedItem() == book){
						book.setFocusedName();
						System.out.println("focused book:"+book.itemHeight);
					}else{
						book.setOtherName();
						System.out.println("unfocused book:"+book.itemHeight);
					}
				}
				prevFocusedIndex = focusedIndex;
			}
		}
		
		super.paint(x, y, width, height, g);
	}
}
