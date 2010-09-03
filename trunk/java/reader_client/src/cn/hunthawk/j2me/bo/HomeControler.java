package cn.hunthawk.j2me.bo;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import de.enough.polish.io.Serializable;

public class HomeControler implements Serializable
{
	private String title;
	private String link;
	private String search;
	private String help;
	private String addfavoritetd;
	private String addbookmarktd;
	
	private Vector vItem;
	
	private String contentstd;
	private String commenttd;
	
	
	public String getContentstd() {
		return contentstd;
	}
	public void setContentstd(String contentstd) {
		this.contentstd = contentstd;
	}
	public String getCommenttd() {
		return commenttd;
	}
	public void setCommenttd(String commenttd) {
		this.commenttd = commenttd;
	}
	public String getAddfavoritetd() {
		return addfavoritetd;
	}
	public void setAddfavoritetd(String addfavoritetd) {
		this.addfavoritetd = addfavoritetd;
	}
	public String getAddbookmarktd() {
		return addbookmarktd;
	}
	public void setAddbookmarktd(String addbookmarktd) {
		this.addbookmarktd = addbookmarktd;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Vector getVItem() {
		return vItem;
	}
	public void setVItem(Vector item) {
		vItem = item;
	}
	
	

}
