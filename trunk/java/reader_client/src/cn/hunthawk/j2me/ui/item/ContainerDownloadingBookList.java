package cn.hunthawk.j2me.ui.item;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;

public class ContainerDownloadingBookList extends Container{
	
	private Vector v = new Vector();
	private int focusedIndex = -1;
	private int prevFocusedIndex = -2;
	int index = -1;
	public ContainerDownloadingBookList(){
		//#style bookListScreen
		super(false);
		
		this.allowCycling = false;
	}
	public void add(Item item){
		super.add(item);
		if(item instanceof ItemDownLoadingGauge){
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
			
		}
		return true;
	}
	public void paint(int x, int y, int width, int height, Graphics g){
		focusedIndex = this.getFocusedIndex();
		if(focusedIndex != prevFocusedIndex){
			if(!v.isEmpty()){
				for(int i=0;i<v.size();i++){
					ItemDownLoadingGauge book = (ItemDownLoadingGauge)v.elementAt(i);
					if(this.getFocusedItem() == book){
						book.setFocusedName();
					}else{
						book.setOtherName();
					}
				}
				prevFocusedIndex = focusedIndex;
			}
		}
		
		super.paint(x, y, width, height, g);
	}
}
