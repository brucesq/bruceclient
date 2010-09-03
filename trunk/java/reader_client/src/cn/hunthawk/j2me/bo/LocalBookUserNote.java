package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class LocalBookUserNote implements Serializable{
	private String packageID;
	
	public String getPackageID() {
		return packageID;
	}
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}
	private String note;
	private String date;
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
