package cn.hunthawk.j2me.html;

import de.enough.polish.io.Serializable;

public class ImageCache implements Serializable{
	private int recordID;
	private String url;
	public int getRecordID() {
		return recordID;
	}
	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
