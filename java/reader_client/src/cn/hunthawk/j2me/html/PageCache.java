package cn.hunthawk.j2me.html;

import java.util.Vector;

import de.enough.polish.util.HashMap;

public class PageCache {
	private HashMap pageCache = new HashMap();
	private Vector keys = new Vector();
	public static final int MAXSIZE = 10;
	public void put(Object key,Object value){
		if(pageCache.size() == MAXSIZE){
			pageCache.remove(keys.elementAt(0));
			pageCache.put(key, value);
			keys.removeElementAt(0);
			keys.addElement(key);
		}else{
			pageCache.put(key, value);
			keys.addElement(key);
		}
	}
	public void delete(Object key){
		int index = keys.indexOf(key);
		if(index !=-1){
			pageCache.remove(key);
			keys.removeElementAt(index);
		}
		
	}
	public Object get(Object key){
		return pageCache.get(key);
	}
	public int size(){
		return pageCache.size();
	}
	public void deleteAll(){
		pageCache.clear();
		keys.removeAllElements();
	}
}
