package cn.hunthawk.j2me.html;

import java.util.Vector;

public class History {
	private Vector history = new Vector();
	private int size = 10;
	public void push(String url){
		if(history.size() == size){
			history.removeElementAt(0);
			history.addElement(url);
		}else{
			history.addElement(url);
		}
	}
	public String getUrl(int historySteps){
		int length = history.size();
		int location = length - historySteps;
		return (String)history.elementAt(location - 1);
	}
	public void setSize(int size){
		this.size = size;
	}
	public int getSize(){
		return history.size();
	}
}
