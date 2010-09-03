package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class FeeResult implements Serializable{
	private String info;
	private String continues;
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getContinues() {
		return continues;
	}
	public void setContinues(String continues) {
		this.continues = continues;
	}
	
	
}
