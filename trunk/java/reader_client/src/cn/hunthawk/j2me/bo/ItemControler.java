package cn.hunthawk.j2me.bo;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import de.enough.polish.io.Serializable;

public class ItemControler implements Serializable{
	
	private String itemName;
	private String link;
	private String icon;
	private String focusedIcon;
	private String vip;
	private Vector channels;
	
	public Vector getChannels(){
		return channels;
	}
	public void setChannels(Vector channels){
		this.channels = channels;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getFocusedIcon() {
		return focusedIcon;
	}
	public void setFocusedIcon(String focusedIcon) {
		this.focusedIcon = focusedIcon;
	}
	
	
	
	
}
