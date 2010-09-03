package cn.hunthawk.j2me.ui.item;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;

public class ItemDownLoadingGauge extends Container{
	
	StringItem item1 = null;
	StringItem item2 = null;
	private Gauge loadingIndicator = null;
	Image imgStart = null;
	Image imgPause = null;
	boolean isPause = false;
	public ItemDownLoadingGauge(String itemName,String info,int indexValue){
		//#style contentsContainer
		super(false);
		
		//#style contentsContainerItem
		item1= new StringItem(null, itemName, Item.BUTTON);
		item1.setAttribute("index", String.valueOf(indexValue));
		
		add(item1);
		
		//#style downloadgauge1
	    this.loadingIndicator = new Gauge(null, false, 100,1);
	    add(loadingIndicator);
	    
	    //#style contentsContainerItem
	    item2= new StringItem(null, info);
		
		
		add(item2);
		allowCycling = false;
		try {
			imgStart = Image.createImage("/start.png");
			imgPause = Image.createImage("/pause.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected boolean handleKeyPressed(int keyCode, int action) {
		
		return super.handleKeyPressed(keyCode, action);
	}
	
	public void setFileName(String name){
		item1.setText(name);
	}
	public String getFileName(){
		return item1.getText();
	}
	public void setDownloadInfo(String info){
		item2.setText(info);
	}
	public void setIndicatorValue(int value){
		this.loadingIndicator.setValue(value);
	}
	public int getIndex(){
		return Integer.parseInt((String) item1.getAttribute("index"));
	}
	public void setMaxValue(int maxValue){
		this.loadingIndicator.setMaxValue(maxValue);
	}
	public int getIndicatorValue(){
		return this.loadingIndicator.getValue();
	}
	public int getType(){
		return Integer.parseInt((String)item1.getAttribute("type"));
	}
	public void setType(int type){
		item1.setAttribute("type", String.valueOf(type));
	}
	public void setAttribute(String key,String value){
		item1.setAttribute(key, value);
	}
	public String getAttribute(String key){
		return (String)item1.getAttribute(key);
	}
	
	public void setFocusedName(){
		System.out.println("set focused");
		loadingIndicator.setVisible(true);
		item2.setVisible(true);
	}
	public void setOtherName(){
		System.out.println("others");
		loadingIndicator.setVisible(false);
		item2.setVisible(false);
		System.out.println("others:"+this.itemWidth);
		
	}
	public void setPause(){
		this.isPause = true;
	}
	public void setRun(){
		this.isPause = false;
	}
	public boolean getPauseState(){
		return this.isPause;
	}
	public void paint(int x, int y, int width, int height, Graphics g){
		super.paint(x, y, width, height, g);
		if(item2.isVisible()){
			if(isPause){
				g.drawImage(imgPause, x+5, y+5, Graphics.LEFT | Graphics.TOP);
			}else{
				g.drawImage(imgStart, x+5, y+5, Graphics.LEFT | Graphics.TOP);
			}
			
		}
		
	}
}
