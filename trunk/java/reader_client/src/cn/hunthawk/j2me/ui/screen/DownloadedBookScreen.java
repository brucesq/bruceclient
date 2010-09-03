package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.bo.DownloadingBook;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.ui.item.ContainerDownloadedBookList;
import cn.hunthawk.j2me.ui.item.ContainerDownloadingBookList;
import cn.hunthawk.j2me.ui.item.ItemBookList;
import cn.hunthawk.j2me.ui.item.ItemDownLoadingGauge;
import cn.hunthawk.j2me.ui.item.SearchAlert;
import cn.hunthawk.j2me.util.DownloadThread2;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.Tools;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;

public class DownloadedBookScreen extends TabbedForm 
implements CommandListener{
	
	public static DownloadedBookScreen dbs = null;
	private ItemBookList item_book= null;
	private ContainerDownloadedBookList conDownloadedBookList = null;
	
	/**准备下载*/
	public static final int TYPE_READY = 0;
	/**下载中*/
	public static final int TYPE_DOWNLOADING = 1;
	/**已下载完成*/
	public static final int TYPE_DOWNLOADED = 2;
	
	private Command cmdOk = new Command("是",Command.ITEM,1);
	private Command cmdOk1 = new Command("是",Command.ITEM,1);
	private Command cmdOk2 = new Command("是",Command.ITEM,1);
	private Command cmdNo = new Command("否",Command.BACK,1);
	private Command cmdOpen = new Command("进        入",Command.ITEM,1);
	private Command cmdDelete = new Command("删        除",Command.ITEM,3);
	private Command cmdDeleteAll = new Command("全部删除",Command.ITEM,4);
	private Command cmdFresh = new Command("刷         新",Command.ITEM,5);
	private Command cmdSearch = new Command("搜索下载",Command.ITEM,6);
	private Command cmdTestWeb = new Command("测试网络",Command.ITEM,7);
	private Command cmdReturn = new Command("退出下载",Command.BACK,8);
	
	private Command cmdStart = new Command("开        始",Command.ITEM,1);
	private Command cmdPause = new Command("暂        停",Command.ITEM,1);
	private Command cmdDelete1 = new Command("删        除",Command.ITEM,3);
	private Command cmdDeleteAll2 = new Command("全部删除",Command.ITEM,4);
	private Command cmdStartAll = new Command("全部启动",Command.ITEM,5);
	private Command cmdPauseAll = new Command("全部暂停",Command.ITEM,6);
	
	private static String tabName[] = {"已下载","下载中"};
	private Alert alert;
	private Alert alertQuit;
	private boolean deleteAll = false;
	private int entry = 0;
	
	private String fileName = null;
	private String downloadUrl = null;
	private Image imgCover = null;
	private ContainerDownloadingBookList conDownloadingBookList = null;
	
	private Vector currentDownloadThread = new Vector();
	private int loadingIndex = 0;
	public boolean isWeb = true;
	private boolean isConnecting = false;

	public DownloadedBookScreen() {
		//#style mainScreen
		super("下载管理",tabName,null);
		
		conDownloadedBookList = new ContainerDownloadedBookList();
		
		conDownloadingBookList = new ContainerDownloadingBookList();
		//#style messageAlert
		alert = new Alert( null);
		alert.addCommand(cmdNo);
		alert.addCommand(cmdOk);
		alert.setCommandListener(this);
		
		this.append(0,conDownloadedBookList);
		this.append(1,conDownloadingBookList);
		this.setCommandListener(this);
		this.setActiveTab(0);
	}
	
	public static DownloadedBookScreen getInstance(){
		if(dbs == null){
			dbs = new DownloadedBookScreen();
		}
		return dbs;
	}
	
	public void switchTab(int i){
		this.setActiveTab(i);
		
		if(i == 0){
			initDownloaded();
			this.removeAllCommands();
			this.addCommand(cmdOpen);
			this.addCommand(cmdDeleteAll);
			this.addCommand(cmdDelete);
			this.addCommand(cmdFresh);
			this.addCommand(cmdSearch);
			this.focus(conDownloadedBookList);
			if(!isWeb){
				this.addCommand(cmdTestWeb);
			}
			this.addCommand(cmdReturn);
		}else if(i == 1){
			this.removeAllCommands();
			this.addCommand(cmdStart);
			this.addCommand(cmdPause);
			this.addCommand(cmdDelete1);
			this.addCommand(cmdDeleteAll2);
			this.addCommand(cmdStartAll);
			this.addCommand(cmdPauseAll);
			this.addCommand(cmdReturn);
			this.focus(conDownloadingBookList);
			if(conDownloadingBookList.size() == 0){
				initDownloadingBookList();
				if(conDownloadingBookList.size() != 0){
					conDownloadingBookList.focus(0);
				}
			}
		}
	}
	public void initDownloaded(){
		entry = 0;
		conDownloadedBookList.clear();
		Vector v = (Vector)RmsOpt.readRMS("downloadbook");
		if(!v.isEmpty()){
			int length = v.size();
			for(int i=length-1;i>-1;i--){
				DownloadBook db = (DownloadBook)v.elementAt(i);
				String uebName = db.getUebName();
				item_book = new ItemBookList(uebName+"\n大小:"+db.getFileSize()+"K"+"\n"+db.getSaveDate(),i,db.getUebImage());
				conDownloadedBookList.add(item_book);
			}
		}else{
			//#style browserText
			StringItem stringItem = new StringItem(null,"暂无下载记录");
			conDownloadedBookList.add(stringItem);
		}
		System.gc();
	}
	public void initDownloading(String fileName,String url,Image cover){
		
		initDownloadID();
		this.fileName = fileName;
		this.downloadUrl = url;
		this.imgCover = cover;
		conDownloadingBookList.clear();
		ItemDownLoadingGauge item = new ItemDownLoadingGauge(fileName,"正在读取中...",TYPE_READY);
		item.setType(0);
		loadingIndex ++;
		item.setAttribute("downloadID", String.valueOf(loadingIndex));
		conDownloadingBookList.add(item);
		initDownloadingBookList();
		switchTab(1);
		new StartDownloadThread().start();
	}
	private void initDownloadID(){
		String rmsName[] = RecordStore.listRecordStores();
		int length = rmsName.length;
		for(int i=0;i<length;i++){
			if(rmsName[i].indexOf("downloading") != -1){
				int value = Integer.parseInt(rmsName[i].substring(12, rmsName[i].length()));
//				System.out.println("initDownloadID:"+value);
				if(value > loadingIndex){
					loadingIndex = value;
				}else if(value == loadingIndex){
					loadingIndex ++;
				}
			}
		}
	}
	private void initDownloadingBookList() {
		String rmsName[] = RecordStore.listRecordStores();
		System.out.println("size:"+rmsName.length);
//		conDownloadingBookList.clear();
		int length = rmsName.length;
		for(int i=0;i<length;i++){
//			System.out.println("rms name:"+rmsName[i]);
			if(rmsName[i].indexOf("downloading") != -1){
				Vector v = (Vector)RmsOpt.readRMS(rmsName[i]);
				DownloadingBook db = (DownloadingBook)v.elementAt(0);
				int total = db.getTotal();
				int progress = db.getCompleted();
				
				ItemDownLoadingGauge item = new ItemDownLoadingGauge("","0% 0K/0K 0K/s",TYPE_DOWNLOADING);
				item.setMaxValue(total);
				item.setIndicatorValue(progress);
				item.setFileName(db.getFileName());
				int percent = (int)(((float)progress/(float)total)*(float)100);
				String info = percent+"%"+String.valueOf(progress/1000)+"K/"+String.valueOf(total/1000)+"K"+" "+String.valueOf(0)+"K/S";
				item.setDownloadInfo(info);
				item.setType(1);
				item.setPause();
				item.setAttribute("downloadID", String.valueOf(db.getDownloadID()));
				conDownloadingBookList.add(item);
				
			}
		}
	}
	public void setEntry(int type){
		this.entry = type;
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdReturn){
			if(isWeb){
				if(entry == 0){
					if(!ReaderControlScreen.getInstance().canGoBackHere()){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}else{
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
					}
				}else if(entry == 1){
					MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
				}
			}else{
				//#style messageAlert
				alertQuit = new Alert( null);
				alertQuit.addCommand(cmdNo);
				alertQuit.addCommand(cmdOk1);
				alertQuit.setCommandListener(this);
				alertQuit.setString("您要真得退出?");
				MainMidlet.getInstance().display.setCurrent(alertQuit);
			}
			
		}else if(cmd == cmdOpen){
			int index = conDownloadedBookList.getFocusedIndex();
//			System.out.println("book index id:"+index);
			Vector v = (Vector)RmsOpt.readRMS("downloadbook");
			int size = v.size();
			DownloadBook db = (DownloadBook)v.elementAt(size-index-1);
			String uebFile =db.getUebName();
//			System.out.println("file :"+uebFile);
			LocalBook lb = new LocalBook(uebFile);
			if(lb != null){
				ReaderControlScreen.getInstance().readerControlScreen = null;
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
				ReaderControlScreen.getInstance().setLocalBook(lb);
				ReaderControlScreen.getInstance().init(1,null,null);
				
			}else{
//				System.out.println("error content format");
			}
		}else if(cmd == cmdDelete){
			showAlert("您确认要删除该条记录?");
			
		}else if(cmd == cmdOk){
			if(deleteAll){
				if(this.getActiveTab() == 0){
					Vector v = (Vector)RmsOpt.readRMS("downloadbook");
					int size = v.size();
					for(int i=0;i<size;i++){
						DownloadBook db = (DownloadBook)v.elementAt(i);
						RmsOpt.deleteRMS(db.getUebName());
						RmsOpt.deleteRMS("localbookinfo"+db.getUebName());
						System.out.println("localbookinfo"+db.getUebName());
					}
					RmsOpt.deleteRMS("downloadbook");
					initDownloaded();
					MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
				}else if(this.getActiveTab() == 1){
					String rmsName[] = RecordStore.listRecordStores();
					int length = rmsName.length;
					for(int i=0;i<length;i++){
						if(rmsName[i].indexOf("downloading") != -1){
							Vector v = (Vector)RmsOpt.readRMS(rmsName[i]);
							DownloadingBook db = (DownloadingBook)v.elementAt(0);
							String name = rmsName[i];
							//#debug
							RmsOpt.deleteRMS(name);
							//#debug
							RmsOpt.deleteRMS(db.getFileName());
							System.out.println("downloading_id:"+rmsName[i]);
							System.out.println("filename:"+db.getFileName());
							Tools.sleep(500);
						}
					}
					conDownloadingBookList.clear();
					if(!currentDownloadThread.isEmpty()){
						for(int i=0;i<currentDownloadThread.size();i++){
							DownloadThread2 dlt = (DownloadThread2)currentDownloadThread.elementAt(i);
							dlt.pause();
							dlt = null;
							currentDownloadThread.removeElementAt(i);
							break;	
							
						}
					}
					MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
				}
				
			}else{
				if(this.getActiveTab() == 0){
					int index = conDownloadedBookList.getFocusedIndex();
					Vector v = (Vector)RmsOpt.readRMS("downloadbook");
					int size = v.size();
					DownloadBook db = (DownloadBook)v.elementAt(size-index-1);
					v.removeElementAt(size-index-1);
					RmsOpt.saveRMS(v, "downloadbook");
					initDownloaded();
					MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
					RmsOpt.deleteRMS(db.getUebName());
					RmsOpt.deleteRMS("localbookinfo"+db.getUebName());
					System.out.println("localbookinfo"+db.getUebName());
					db = null;
					System.gc();
				}else if(this.getActiveTab() == 1){
					ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
					int downloadID = Integer.parseInt(gg.getAttribute("downloadID"));
					RmsOpt.deleteRMS("downloading_"+downloadID);
					RmsOpt.deleteRMS(gg.getFileName());
					conDownloadingBookList.remove(gg);
					if(conDownloadingBookList.size() != 0){
						conDownloadingBookList.focus(0);
					}
					if(!currentDownloadThread.isEmpty()){
						for(int i=0;i<currentDownloadThread.size();i++){
							DownloadThread2 dlt = (DownloadThread2)currentDownloadThread.elementAt(i);
//							System.out.println("continue check download dlt id:"+dlt.getDownloadID());
							if(downloadID == dlt.getDownloadID()){
								dlt.pause();
								dlt = null;
								currentDownloadThread.removeElementAt(i);
								break;
							}
						}
					}
					MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
					
				}
				
			}
			
			this.isConnecting = false;
		}else if(cmd == cmdNo){
			MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
		}else if(cmd == cmdDeleteAll){
			deleteAll = true;
			showAlert("您要全部删除?");
		}else if(cmd == cmdStart){
			int index = this.getActiveTab();
			if(index == 1){
				ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
				Start(gg);
				
			}
		}else if(cmd == cmdPause){
			ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
			Pause(gg);
			
		}else if(cmd == cmdDelete1){
			showAlert("确定要删除该条未完成的下载?");
			
		}else if(cmd == cmdDeleteAll2){
			deleteAll = true;
			showAlert("您要全部删除未完成的下载?");
		}else if(cmd == cmdStartAll){
			StartAll();
		}else if(cmd == cmdPauseAll){
			PauseAll();
		}else if(cmd == cmdSearch){
			MainMidlet.getInstance().display.setCurrent(SearchAlert.getInstance());
		}else if(cmd == cmdOk1){
			MainMidlet.getInstance().quit();
		}else if(cmd == cmdTestWeb){
			String testURL = null;
			//#= testURL = "${ homeurl }";
			HttpConnectionUtil hcu = new HttpConnectionUtil(testURL);
			
//			int r = hcu.getResponseCode();
			int r = 200;
			try{

					if(r == 200){
						//#style messageAlert
						alertQuit = new Alert( null);
						alertQuit.addCommand(cmdNo);
						alertQuit.addCommand(cmdOk2);
						alertQuit.setCommandListener(this);
						alertQuit.setString("网络已链接正常,是否启动在线模式?");
						MainMidlet.getInstance().display.setCurrent(alertQuit);
					}else{
						//#style messageAlert
						alertQuit = new Alert( null);
						alertQuit.setString("网络测试失败");
						alertQuit.setTimeout(1500);
						MainMidlet.getInstance().display.setCurrent(alertQuit);
					}
				
				
			}catch(Exception e){}
		}else if(cmd == cmdOk2){
			dbs = null;
			System.gc();
			SplashScreen.getInstance().start();
			MainMidlet.getInstance().display.setCurrent(SplashScreen.getInstance());
		}
		
	}
	public void setWeb(boolean isweb){
		this.isWeb = isweb;
	}
	public void StartAll(){
		int length = conDownloadingBookList.size();
		for(int i = 0;i<length;i++){
			ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.get(i);
			Start(gg);
		}
	}
	public void PauseAll(){
		int length = conDownloadingBookList.size();
		for(int i=0;i<length;i++){
			ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.get(i);
			Pause(gg);
		}
	}
	public void Pause(ItemDownLoadingGauge gg){
		int ID = Integer.parseInt(gg.getAttribute("downloadID"));
		gg.setPause();
		for(int i=0;i<currentDownloadThread.size();i++){
			DownloadThread2 dlt = (DownloadThread2)currentDownloadThread.elementAt(i);
			if(ID == dlt.getDownloadID()){
				dlt.pause();
				gg.setType(1);
				currentDownloadThread.removeElement(dlt);
				break;
			}
		}
	}
	public void Start(ItemDownLoadingGauge gg){
			gg.setRun();
			System.out.println("start gg");
			if(gg.getType() == 0){
				DownloadThread2 dlt = new DownloadThread2(Integer.parseInt(gg.getAttribute("downloadID")),downloadUrl,fileName,gg,imgCover);
				dlt.start();
				currentDownloadThread.addElement(dlt);
			}else if(gg.getType() == 1){
				System.out.println("type:load downloading info");
				int ID = Integer.parseInt(gg.getAttribute("downloadID"));
				Vector v = (Vector)RmsOpt.readRMS("downloading_"+ID);
				DownloadingBook db = (DownloadingBook)v.elementAt(0);
				DownloadThread2 dlt = new DownloadThread2(ID,db.getServiceUrl(),db.getFileName(),gg,db.getImgCover());
				dlt.initDownloadingInfo(db);
				dlt.start();
				currentDownloadThread.addElement(dlt);
			}
	}
	/**提示框设置*/
	public void showAlert(String content){
		alert.setTimeout(Alert.FOREVER);
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	public void deleteItem(ItemDownLoadingGauge gg){
		conDownloadingBookList.remove(gg);
	}
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		int index = this.getActiveTab();
		System.out.println("keyCode:"+keyCode);
		if(gameAction == Canvas.LEFT){
			if(index == 1){
				switchTab(0);
			}
		}else if(gameAction == Canvas.RIGHT){
			if(index == 0){
				switchTab(1);
			}
		}
		else if(gameAction == Canvas.DOWN){
			if(index == 0){
				int tt = conDownloadedBookList.getFocusedIndex();
				if(tt != -1){
					tt++;
					System.out.println("tt:"+tt);
					if(tt < conDownloadedBookList.size()){
						conDownloadedBookList.focus(tt);
					}
				}
				
			}else if(index == 1){
				int tt = conDownloadingBookList.getFocusedIndex();
				if(tt != -1){
					tt++;
					System.out.println("tt:"+tt);
					if(tt < conDownloadingBookList.size()){
						conDownloadingBookList.focus(tt);
					}
				}
			}
		}else if(gameAction == Canvas.UP){
			if(index == 0){
				int tt = conDownloadedBookList.getFocusedIndex();
				if(tt != -1){
					tt--;
					System.out.println("tt1:"+tt);
					if(tt >= 0){
						conDownloadedBookList.focus(tt);
					}
				}
			}else if(index == 1){
				int tt = conDownloadingBookList.getFocusedIndex();
				if(tt != -1){
					tt--;
					System.out.println("tt1:"+tt);
					if(tt >= 0){
						conDownloadingBookList.focus(tt);
					}
				}
			}
		}else if(gameAction == Canvas.FIRE){
			if(index == 0){
				conDownloadedBookList.handleKeyPressed(keyCode, gameAction);
			}
			
		}else if(keyCode == -6){
			System.out.println("left soft key");
			if(index == 1){
				ItemDownLoadingGauge idg = (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
				if(idg != null){
					if(idg.getPauseState()){
						this.removeCommand(cmdStart);
						this.removeCommand(cmdPause);
						this.addCommand(cmdStart);
					}else{
						this.removeCommand(cmdStart);
						this.removeCommand(cmdPause);
						this.addCommand(cmdPause);
					}
				}
				
			}
			super.handleKeyPressed(keyCode, gameAction);
		}
		else{
			super.handleKeyPressed(keyCode, gameAction);
		}
		return true;
	}
	public void setConnecting(boolean isConnecting){
		this.isConnecting = isConnecting;
		repaint();
	}
	public void paint(Graphics g){
		super.paint(g);
		if(isConnecting && this.getActiveTab() == 1){
			tempData.DrawGauge(g, 20, "正在请求中...");
		}
		
	}
	public ItemDownLoadingGauge getFocusedItem(){
		return (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
	}
	public Vector getDownloadThreadVector(){
		return currentDownloadThread;
	}
	public void setDownloadItem(String text){
		getFocusedItem().setDownloadInfo(text);
	}
	public class StartDownloadThread extends Thread{
		public void run(){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ItemDownLoadingGauge gg = (ItemDownLoadingGauge)conDownloadingBookList.getFocusedItem();
			Start(gg);
		}
	}
	
	
}
