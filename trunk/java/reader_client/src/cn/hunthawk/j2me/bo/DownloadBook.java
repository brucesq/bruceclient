package cn.hunthawk.j2me.bo;

import javax.microedition.lcdui.Image;

import de.enough.polish.io.Serializable;



public class DownloadBook implements Serializable{
	private String uebName;
	private String saveDir;
	private int fileSize;
	private String saveDate;
	private Image uebImage;
	
	public Image getUebImage() {
		return uebImage;
	}
	public void setUebImage(Image uebImage) {
		this.uebImage = uebImage;
	}
	public String getUebName() {
		return uebName;
	}
	public void setUebName(String uebName) {
		this.uebName = uebName;
	}
	public String getSaveDir() {
		return saveDir;
	}
	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getSaveDate() {
		return saveDate;
	}
	public void setSaveDate(String saveDate) {
		this.saveDate = saveDate;
	}
	

	
	
}
