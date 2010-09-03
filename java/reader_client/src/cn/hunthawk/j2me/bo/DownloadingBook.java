package cn.hunthawk.j2me.bo;

import javax.microedition.lcdui.Image;

import de.enough.polish.io.Serializable;

public class DownloadingBook implements Serializable{
	
	private int downloadID;
	
	private String serviceUrl;
	private String dir;
	private String fileName;
	private Image imgCover;
	private int total;
	private int completed;
	
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	private int parts[] = new int[3];
	private int completeParts[] = new int[4];
	
	public int getDownloadID() {
		return downloadID;
	}
	public Image getImgCover() {
		return imgCover;
	}
	public void setImgCover(Image imgCover) {
		this.imgCover = imgCover;
	}
	public void setDownloadID(int downloadID) {
		this.downloadID = downloadID;
	}
	public String getServiceUrl() {
		return serviceUrl;
	}
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int[] getParts() {
		return parts;
	}
	public void setParts(int[] parts) {
		this.parts = parts;
	}
	public int[] getCompleteParts() {
		return completeParts;
	}
	public void setCompleteParts(int[] completeParts) {
		this.completeParts = completeParts;
	}
	
	
}
