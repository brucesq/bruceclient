package cn.hunthawk.j2me.bo;

import java.util.Hashtable;
import java.util.Vector;

import de.enough.polish.io.Serializable;

public class LocalBookInfo implements Serializable{
	
	private String UebName;
	private LocalBookHeaderInfo header;
	private Vector contents;
	private Hashtable fileIndex;
	private Vector bookMark;
	private Vector bookNote;
	private LocalBookControler bookOpfInfo;
	
	public LocalBookControler getBookOpfInfo() {
		return bookOpfInfo;
	}
	public void setBookOpfInfo(LocalBookControler bookOpfInfo) {
		this.bookOpfInfo = bookOpfInfo;
	}
	public String getUebName() {
		return UebName;
	}
	public void setUebName(String uebName) {
		UebName = uebName;
	}
	public LocalBookHeaderInfo getHeader() {
		return header;
	}
	public void setHeader(LocalBookHeaderInfo header) {
		this.header = header;
	}
	public Vector getContents() {
		return contents;
	}
	public void setContents(Vector contents) {
		this.contents = contents;
	}
	public Hashtable getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(Hashtable fileIndex) {
		this.fileIndex = fileIndex;
	}
	public Vector getBookMark() {
		return bookMark;
	}
	public void setBookMark(Vector bookMark) {
		this.bookMark = bookMark;
	}
	public Vector getBookNote() {
		return bookNote;
	}
	public void setBookNote(Vector bookNote) {
		this.bookNote = bookNote;
	}
	
	
}
