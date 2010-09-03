package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class ChannelItem implements Serializable{
	private String name ;
	private String link;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
}
