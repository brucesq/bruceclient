package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class LocalBookContents implements Serializable{
	
	private String chapterPlayOrder;
	private String chapterSrc;
	private String chapterName;
	
	public String getChapterPlayOrder() {
		return chapterPlayOrder;
	}
	public void setChapterPlayOrder(String chapterPlayOrder) {
		this.chapterPlayOrder = chapterPlayOrder;
	}
	public String getChapterSrc() {
		return chapterSrc;
	}
	public void setChapterSrc(String chapterSrc) {
		this.chapterSrc = chapterSrc;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	
	
}
