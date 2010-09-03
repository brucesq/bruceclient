package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.browser.conf.FeeTagHandler;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.TextUtil;

public class FeeScreen extends Form implements CommandListener{
	
	public static FeeScreen feeScreen = null;
	
	private Command cmdFee = new Command("订购",Command.ITEM,1);
	private Command cmdReturn = new Command("返回",Command.BACK,1);
	private Command cmdContinue = new Command("继续",Command.BACK,1);
	private HtmlBrowser browser = null;
	private int entry = 0;
	HttpConnectionUtil hcu = null;
	private String realUrl = null;
	private String feeResultInfo = null;
	private String feeResultContinue = null;
	private String feeConfrimUrl = null;
	private boolean isSucess = false;
	public FeeScreen(){
		//#style mainScreen
		super("订制信息");
		this.addCommand(cmdFee);
		this.addCommand(cmdReturn);
		this.setCommandListener(this);
		
		//#style browser
		browser = new HtmlBrowser();
		new FeeTagHandler(this.browser.getTagHandler("html")).register(this.browser);
		this.append(browser);
		
	}
	
	public static FeeScreen getInstance(){
		if(feeScreen == null){
			feeScreen = new FeeScreen();
		}
		return feeScreen;
	}
	
	public void setEntry(int i){
		this.entry = i;
	}
	
	public void go(String url){
		this.realUrl = url;
		System.out.println("urlll:"+url);
		this.browser.refresh(url);
	}
	
	
	public void clear(){
		this.removeAllCommands();
		this.addCommand(cmdFee);
		this.addCommand(cmdReturn);
		this.browser.clear();
		this.browser.clearMeta();
		this.browser.clearHistory();
		this.browser.clearPageCache();
		realUrl = null;
		feeResultInfo = null;
		feeResultContinue = null;
		feeConfrimUrl = null;
		
	}
	
	public void setResult(String info,String continues){
		this.feeResultInfo = info;
		this.feeResultContinue = continues;
		if(this.feeResultInfo!= null){
			if(this.feeResultInfo.indexOf("对不起") != -1){
				this.isSucess = false;
			}else{
				this.isSucess = true;
			}
		}else{
			this.isSucess = false;
		}
	}
	
	public void setHttpUtil(HttpConnectionUtil hcu){
		this.hcu = hcu;
	}
	
	public void setConfirmUrl(String confrim){
		this.feeConfrimUrl = confrim;
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == this.cmdFee){
			this.removeCommand(cmdFee);
			feeConfrimUrl =  TextUtil.replace(feeConfrimUrl,"amp;","");
			
			browser.go(feeConfrimUrl);	

			
			
		}else if(cmd == this.cmdReturn){
			clear();
			if(entry == 1){
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}else if(entry == 2){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			}
			
			
			
		}else if(cmd == this.cmdContinue){
			clear();
			if(entry == 1){
				if(this.isSucess){
					ReaderControlScreen.getInstance().doFresh();
				}
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}else if(entry == 2){
				if(this.isSucess){
					NovelsReaderScreen.getInstance().setAddress(realUrl, 1);
				}
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			}
		}
		
	}
	
	public void paint(Graphics g){
		super.paint(g);
		if(browser.isOpenProc()){
			tempData.DrawGauge(g,browser.getProcValue(),browser.getProcInfo());

		}
		
		if(hcu != null){
			if(!hcu.isConnOver()){
				tempData.DrawGauge(g,hcu.getPercent(),hcu.getPercentInfo());
			}
		}

	}
	
	
	
}
