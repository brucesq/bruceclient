package cn.hunthawk.j2me.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.bo.DownloadingBook;
import cn.hunthawk.j2me.control.MyHttpConnectionListener;
import cn.hunthawk.j2me.ui.item.ItemDownLoadingGauge;
import cn.hunthawk.j2me.ui.screen.DownloadedBookScreen;
import de.enough.polish.io.Serializer;

public class DownloadThread2 extends Thread{
	private int downloadID = 0;
	private String url = null;
	private String fileName = null;
	private Image imgCover = null;
	private long total = 0;
	private int totalcount = 0;
	private ItemDownLoadingGauge item = null;
	
	private ByteArrayOutputStream bStrm = null;
	private SaveRMSThread saveRms = null;
	private DisplayInfoThread displayInfo = null;
	public byte [] result = null;
	public boolean downning;
	private int datacount = 0;
	
	private boolean isConnected = false;
	private boolean isPause = false;
	private int lastSize = 0;
	private boolean isOk = false;
	DownloadingBook downloadingBook = null;
	boolean downloading = false;
	private Queue q = new Queue();

	private static final String USER_AGENT = 
		//#if polish.Browser.UserAgent:defined
			//#= 	"${polish.Browser.UserAgent}";
		//#else
			System.getProperty("microedition.platform");
		//#endif

		private static final String USER_ID = Tools.getID();
	public DownloadThread2(int downloadID,String url,String fileName,ItemDownLoadingGauge item,Image cover){
		this.downloadID = downloadID;
		this.url = url;
		this.fileName = fileName;
		this.item = item;
		this.imgCover = cover;
		downloadingBook = new DownloadingBook();
		downloadingBook.setDownloadID(downloadID);
		downloadingBook.setServiceUrl(url);
		downloadingBook.setFileName(fileName);
		downloadingBook.setImgCover(cover);
	}
	public void initDownloadingInfo(DownloadingBook db){
		this.downloadID = db.getDownloadID();
		this.url = db.getServiceUrl();
		this.fileName = db.getFileName();
		this.total = db.getTotal();
	    this.totalcount = db.getCompleted();
	    this.imgCover = db.getImgCover();
		downloadingBook = new DownloadingBook();
		downloadingBook.setDownloadID(downloadID);
		downloadingBook.setServiceUrl(url);
		downloadingBook.setFileName(fileName);
		downloadingBook.setCompleted(db.getCompleted());
		downloadingBook.setTotal((int)total);
		downloadingBook.setImgCover(db.getImgCover());
		downloading = true;
		
	}
	public void run(){
		HttpConnection conn = null;
		HttpConnectionUtil hcu = null;
		isPause = false;
		DownloadedBookScreen.getInstance().setConnecting(true);
		if(downloading){
			hcu = new HttpConnectionUtil(url,downloadingBook.getCompleted()-1,downloadingBook.getTotal());
			hcu.setHttpConnectionListener(new MyHttpConnectionListener(10,hcu));
			hcu.start();
		}else{
			 hcu = new HttpConnectionUtil(url);
			 hcu.setHttpConnectionListener(new MyHttpConnectionListener(10,hcu));
			 hcu.start();
			 
		}
		
		do{
			Tools.sleep(500);
			System.out.println("wait conn....");
		}while(!hcu.isConnOver());
		
		conn = hcu.getCurrentConnection();
		System.out.println("conn:"+conn);
//		if(conn != null){
			downning = true;
			System.out.println("conn:"+conn);
			
			try{

				if (hcu.getResponseCodes() == HttpConnection.HTTP_OK || hcu.getResponseCodes() == HttpConnection.HTTP_PARTIAL){
					
					System.out.println("okkk!");
					if(downloading){
						total = downloadingBook.getTotal();
					}else{
						total = (int) conn.getLength();
						downloadingBook.setTotal((int)total);
					}
					DownloadedBookScreen.getInstance().setConnecting(false);
					isConnected = true;
					getData(conn);	
				
				conn.close();
				conn = null;
				}
			}catch(Exception e){
			}
			downning = false;
			isOk = true;
//		}
		
		System.gc();
		
	}
	
	private void getData(HttpConnection conn){
		try{

			System.out.println("getData....");
			int receive_length = 0;
			byte[] data = null;
			byte[] buffer = new byte[52224];//缓存10K
			int progress = 0;
//			totalcount = 0;
			InputStream is = conn.openInputStream();
			saveRms = new SaveRMSThread();
			saveRms.start();
			displayInfo = new DisplayInfoThread();
			displayInfo.start();
			do{
//				System.out.println("while");
				data = null;
				data = new byte[512];
				//每次从is读取读取512字节
				receive_length = is.read(data, 0, 512);
				
				if (receive_length != -1) {
					System.arraycopy(data, 0, buffer, progress,receive_length);
					progress += receive_length;
					totalcount += receive_length;
					if(progress > 51200){
						byte[] b = new byte[progress];
						System.arraycopy(buffer, 0, b, 0, progress);
						//放入队列存储rms
						q.put(b);
                        b = null;
                        buffer = null;
                        buffer = new byte[52224];
                        progress = 0;
					}
				}
				if(isPause){
					
//					do{
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}while(isPause);
					break;
					
				}

			}while(receive_length != -1);
			if(!isPause){
				//最后一段内容
				byte[] b = new byte[progress];
				System.arraycopy(buffer, 0, b, 0, progress);
				q.put(b);
	            b = null;
	            buffer = null;
	            totalcount += progress;
	            progress = 0;
	            if (conn != null) {
					try{
						conn.close();
					}catch(Exception e){}
				}
				if (is != null) {
					try{
						is.close();
					}catch(Exception e){}
				}
				q.put(new Object());
			}else{
				if (conn != null) {
					try{
						conn.close();
					}catch(Exception e){}
				}
				if (is != null) {
					try{
						is.close();
					}catch(Exception e){}
				}
				q.put(new Object());
			}
			
		}catch(Exception e){
		}finally{
			
			
		}
		}
	public ByteArrayOutputStream getByteArrayOutputStream(){
		ByteArrayOutputStream  bStreamTemp = null;
		if(downloading){
			byte[] saved = null;
			RecordStore rs = null;
			
			try {
				rs = RecordStore.openRecordStore(fileName, false);
				saved = rs.getRecord(1);
			} catch (RecordStoreFullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordStoreNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RecordStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(saved != null){
				
				bStreamTemp = new ByteArrayOutputStream();
				bStreamTemp.write(saved, 0, saved.length);
			}
		}else{
			bStreamTemp = new ByteArrayOutputStream();
		}
		return bStreamTemp;
	}
	public int getDownloadID(){
		return this.downloadID;
	}
	public void pause(){
		
		isPause = true;

	}
	public void play(){
		this.isPause = false;
	}
	public void setItem(ItemDownLoadingGauge item ){
		this.item = item;
	}
	/**
	 * 
	 * 设置下载信息线程
	 */
	class DisplayInfoThread extends Thread{
		public void run(){
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println("totalcount:"+totalcount);
				setDownloadItemInfo(totalcount);
				if(isOk){
					break;
				}
				if(isPause){
					break;
				}
			}
		}
	}
	/**
	 * RMS存储线程
	 * 
	 * */
	class SaveRMSThread extends Thread{
		public void run(){
			RecordStore rs = null;
			try {
				rs = RecordStore.openRecordStore(fileName, true);
				
			} catch (RecordStoreFullException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RecordStoreNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RecordStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int currentLength = 0;
			if(downloadingBook != null){
				currentLength = downloadingBook.getCompleted();
			}
			
			while(true){
				   Object ob = q.get();
				   byte [] temp = null;
				   if(ob instanceof byte[]){
					   temp = (byte[])ob;
				   }else{
					   ob = null;
					   System.gc();
					break;
				   }
					
					if(temp.length>0){
						
						try {
							if(rs.getNumRecords() == 0){
								UebRecordNode node = new UebRecordNode();
								node.setRecordID(2);
								node.setStart(0);
								node.setEnd(temp.length);
								Vector v = new Vector();
								v.addElement(node);
								ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
								DataOutputStream out = new DataOutputStream( byteOut );
								try {
									Serializer.serialize(v, out);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								byte[] p = byteOut.toByteArray();
								rs.addRecord(p, 0, p.length);
								rs.addRecord(temp, 0, temp.length);	
								
							}else{
								rs.addRecord(temp, 0, temp.length);
								byte []p = rs.getRecord(1);
								int num  = rs.getNumRecords();
								DataInputStream in = new DataInputStream( new ByteArrayInputStream( p ));
								try {
									Vector v = (Vector)Serializer.deserialize( in );
									UebRecordNode node = new UebRecordNode();
									node.setRecordID(num);
									node.setStart(currentLength);
									node.setEnd(currentLength+temp.length);
									v.addElement(node);
									
									ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
									DataOutputStream out = new DataOutputStream( byteOut );
									try {
										Serializer.serialize(v, out);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									byte[] b = byteOut.toByteArray();
									rs.setRecord(1, b, 0, b.length);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}				
							
						} catch (RecordStoreNotOpenException e) {
							e.printStackTrace();
						} catch (InvalidRecordIDException e) {
							e.printStackTrace();
						} catch (RecordStoreFullException e) {
							e.printStackTrace();
						} catch (RecordStoreException e) {
							e.printStackTrace();
						}
						currentLength += temp.length;
						
						temp = null;
					
					}
				setDownloadingInfo(currentLength);
				
				
				System.gc();
			}
			
			System.gc();
			if(!isPause)
				addDownloadedBookList();
		}
	}
	public boolean getSize(){
		return this.isPause;
	}
	public void setDownloadingInfo(int current){
		downloadingBook.setCompleted(current);
		Vector v = new Vector();
		v.addElement(downloadingBook);
		RmsOpt.saveRMS(v, "downloading_"+downloadID);
	}
	public void setDownloadItemInfo(int current){
		System.out.println("current:"+current);
		item.setMaxValue((int)this.total);
		int rate = current - lastSize;
		int progress = (int)(((float)current/(float)(int)total)*(float)100);
		
		String info = progress+"%"+String.valueOf(current/1000)+"K/"+String.valueOf(total/1000)+"K"+" "+String.valueOf(rate/1000)+"K/S";
		lastSize = current;
		item.setDownloadInfo(info);
//		System.out.println("downloadID:"+this.downloadID+" info:"+info);
		item.setIndicatorValue(current);
		
	}
	public void addDownloadedBookList(){
		RmsOpt.deleteRMS("downloading_"+String.valueOf(downloadID));
		DownloadBook db = new DownloadBook();
		
		db.setUebName(this.fileName);
		db.setSaveDate(tempData.getNowTime());
		int size = (int)this.total/1000;
		db.setFileSize(size);
		db.setUebImage(imgCover);
		Vector v = (Vector)RmsOpt.readRMS("downloadbook");
		v.addElement(db);
		RmsOpt.saveRMS(v, "downloadbook");
//		System.out.println("add downloaded book rms info");
		ShowDownloadedBookScreen();
	}
	public void ShowDownloadedBookScreen(){
		DownloadedBookScreen.getInstance().deleteItem(item);
		DownloadedBookScreen.getInstance().switchTab(0);
		System.gc();
//		MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
	}
	
	
	
}
