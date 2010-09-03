package cn.hunthawk.j2me.util;

public interface HttpConnectionListener {
	
	public void notifyHttpConnectionStart(String url);
	public void notifyHttpConnectionCreated(int code);
	public void notifyHttpConnectionError(int code ,String msg);
	public void notifyHttpConnectionTimeOut();
	
}
