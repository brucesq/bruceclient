package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class LocalBookHeaderInfo implements Serializable{
	
	private String fileType;
	private int version;
	private long contentID;
	private int showType;
	private int feeType;
	private long channelID;
	private long cpid;
	private long packageID;
	private String creatDate;
	private String modifyDate;
	private long fileSize;
	private String reserveFiled;
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public long getContentID() {
		return contentID;
	}
	public void setContentID(long contentID) {
		this.contentID = contentID;
	}
	public int getShowType() {
		return showType;
	}
	public void setShowType(int showType) {
		this.showType = showType;
	}
	public int getFeeType() {
		return feeType;
	}
	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}
	public long getChannelID() {
		return channelID;
	}
	public void setChannelID(long channelID) {
		this.channelID = channelID;
	}
	public long getCpid() {
		return cpid;
	}
	public void setCpid(long cpid) {
		this.cpid = cpid;
	}
	public long getPackageID() {
		return packageID;
	}
	public void setPackageID(long packageID) {
		this.packageID = packageID;
	}
	public String getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(String creatDate) {
		this.creatDate = creatDate;
	}
	public String getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getReserveFiled() {
		return reserveFiled;
	}
	public void setReserveFiled(String reserveFiled) {
		this.reserveFiled = reserveFiled;
	}
	
	
}
