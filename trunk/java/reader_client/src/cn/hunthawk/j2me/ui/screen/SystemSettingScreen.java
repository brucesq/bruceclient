package cn.hunthawk.j2me.ui.screen;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.MusicPlayerThread;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.util.Locale;

public class SystemSettingScreen extends TabbedForm 
implements CommandListener{
	public static SystemSettingScreen setScreen = null;
	
	private Command cmdReturn = new Command("����",Command.BACK,1);
	private Command cmdSave = new Command("����",Command.ITEM,1);
	private Command cmdYes = new Command("��",Command.ITEM,1);
	private Command cmdNo = new Command("��",Command.BACK,1);
	private Command cmdReset = new Command("�ָ�Ĭ��",Command.ITEM,2);
	static String tabNames[] = {"����","����","����","�洢","��ӭ","����","��ɫ"};
	
	private int prevSettingValues[] = {1,0,0,0,0,0,0};
	private int currentSettingValue[] = {1,0,0,0,0,0,0};
	private int saveSettingValue[] = {1,0,0,0,0,0,0};
	private ChoiceGroup settings[] = new ChoiceGroup[7];
	//#ifdef polish.debugEnabled
	Command showLogCmd = new Command( Locale.get("cmd.showLog"), Command.ITEM, 9 );
	Command cmdExit = new Command(Locale.get("cmd.exit"), Command.EXIT, 10 );
	//#endif
	private Alert alert = null;
	private int entry = 0;
	private Image [] colorImage = new Image[8];

	public SystemSettingScreen(){
		//#style mainScreen
		super("����",tabNames,null);
		
		this.addCommand(cmdReturn);
		this.addCommand(cmdSave);
		this.addCommand(cmdReset);
		this.setCommandListener(this);
		//#ifdef polish.debugEnabled
		this.addCommand( this.showLogCmd );
		this.addCommand( this.cmdExit );
		this.setCommandListener(this);
		//#endif
		//#style messageAlert
		alert = new Alert(null,"",null,AlertType.INFO);
		alert.setCommandListener(this);
		
		for(int i=0;i<8;i++){
			try {
				colorImage[i] = Image.createImage("/color"+i+".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		initTabs();
		initSetting();
		
	}
	public void initSetting(){
		Vector v = null;
		v = (Vector)RmsOpt.readRMS("settings");
		
		if(!v.isEmpty()){
//			System.out.println("v != null");
			prevSettingValues = (int[])v.elementAt(0);
			
		}
		for(int i=0;i<7;i++){
//			System.out.println("prevSettingValues:"+prevSettingValues[i]);
			settings[i].setSelectedIndex(prevSettingValues[i], true);
		}
		
		v = (Vector)RmsOpt.readRMS("welcome");
		if(!v.isEmpty()){
			String set = (String)v.elementAt(0);
			if(set.equals("no")){
				settings[4].setSelectedIndex(1, true);
				currentSettingValue[4] = 1;
				prevSettingValues[4] = 1;
				saveSettingValue[4] = 1;
			}
			
		}
	}
	public void setEntry(int type){
		this.entry = type;
	}
	public void initTabs(){
		StringItem item = null;
		//#style SystemSettingChoiceGroup
		settings[0] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[0].append("500", null);
		//#style SystemSettingItem
		settings[0].append("1000", null);
		//#style SystemSettingItem
		settings[0].append("2000", null);
		settings[0].setSelectedIndex(1, true);
		//#style stringItembook
		item = new StringItem(null,"������ͼ������ÿҳ����");
		this.append(0,item);
		this.append(0,settings[0]);
		
		//#style SystemSettingChoiceGroup
		settings[1] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[1].append("С����", null);
		//#style SystemSettingItem
		settings[1].append("������", null);
		//#style SystemSettingItem
		settings[1].append("������", null);
		settings[1].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"������������ʾ��С");
		this.append(1,item);
		this.append(1,settings[1]);
		//#style stringItembook
		item = new StringItem(null,"˵���������С���ֻ�ϵͳ�Դ�Ϊ����");
		this.append(1,item);
		
		//#style SystemSettingChoiceGroup
		settings[2] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[2].append("�ر�", null);
		//#style SystemSettingItem
		settings[2].append("����", null);
		
		settings[2].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"�����ñ�������");
		this.append(2,item);
		this.append(2,settings[2]);
		
		//#style SystemSettingChoiceGroup
		settings[3] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[3].append("�洢��", null);
		//#style SystemSettingItem
		settings[3].append("�ֻ�", null);
//		System.out.println("okkk1");
		settings[3].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"���������ش洢λ��");
		this.append(3,item);
		this.append(3,settings[3]);
		
		//#style SystemSettingChoiceGroup
		settings[4] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[4].append("��", null);
		//#style SystemSettingItem
		settings[4].append("�ر�", null);
	
		settings[4].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"�����û�ӭ��ʾҳ��");
		this.append(4,item);
		this.append(4,settings[4]);
		
		//#style SystemSettingChoiceGroup
		settings[5] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[5].append("Ĭ��", null);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[0]);
		//#style SystemSettingItem
		settings[5].append("ǳ��", colorImage[1]);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[2]);
		//#style SystemSettingItem
		settings[5].append("ǳ��", colorImage[3]);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[4]);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[5]);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[6]);
		//#style SystemSettingItem
		settings[5].append("��ɫ", colorImage[7]);
	
		settings[5].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"�������Ķ�����ɫ");
		this.append(5,item);
		this.append(5,settings[5]);
		
		//#style SystemSettingChoiceGroup
		settings[6] = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		settings[6].append("Ĭ��", null);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[0]);
		//#style SystemSettingItem
		settings[6].append("ǳ��", colorImage[1]);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[2]);
		//#style SystemSettingItem
		settings[6].append("ǳ��", colorImage[3]);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[4]);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[5]);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[6]);
		//#style SystemSettingItem
		settings[6].append("��ɫ", colorImage[7]);
	
		settings[6].setSelectedIndex(0, true);
		//#style stringItembook
		item = new StringItem(null,"�������Ķ�������ɫ");
		this.append(6,item);
		this.append(6,settings[6]);
//		System.out.println("okkk!");
		
	}
	public static SystemSettingScreen getInstance(){
		if(setScreen == null){
			setScreen = new SystemSettingScreen();
			
		}
		return setScreen;
	}
	
//	public boolean handleKeyPressed(int keyCode,int gameAction){
//		this.removeCommand(ChoiceGroup.)
//	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdReturn){
			if(isSave()){
				showAlert(1,"���Ѿ��ı䵱ǰ����,�Ƿ񱣴�?");
				MainMidlet.getInstance().display.setCurrent(alert);
			}else{
				if(isChange()){
					if(currentSettingValue[4] != prevSettingValues[4]){
						Vector vv = new Vector();
						if(currentSettingValue[4] == 0){
							vv.addElement("yes");
						}else if(currentSettingValue[4] == 1){
							vv.addElement("no");
						}
						RmsOpt.saveRMS(vv, "welcome");
					}
					if(entry == 0){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}else if(entry == 1){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
					}else if(entry == 2){
						boolean isneedfresh = false;
						if(currentSettingValue[0] != prevSettingValues[0]){
							NovelsReaderScreen.getInstance().refreshWordSet();
							isneedfresh = true;
						}
						if(currentSettingValue[1] != prevSettingValues[1]){
							tempData.setFontChange(true);
							isneedfresh = true;
							
						}	
						if(currentSettingValue[5] != prevSettingValues[5]){
							NovelsReaderScreen.getInstance().refreshBgColor(currentSettingValue[5]);
						}
						if(currentSettingValue[6] != prevSettingValues[6]){
							tempData.setColorChange(true);
							isneedfresh = true;
							
						}
						if(isneedfresh){
							NovelsReaderScreen.getInstance().doRefresh();
						}
						MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
					}else if(entry == 3){
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
					}else if(entry == 4){
						MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
					}else if(entry == 5){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
					}
					if(currentSettingValue[2] == 1){
						MusicPlayerThread.getInstance().play();
					}else if(currentSettingValue[2] == 0){
						MusicPlayerThread.getInstance().stop();
					}
				}else{
					if(entry == 0){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
					}else if(entry == 1){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
					}else if(entry == 2){
						MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
					}else if(entry == 3){
						MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
					}else if(entry == 4){
						MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
					}else if(entry == 5){
						MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
					}
				}
				
			}
			
		}else if(cmd == cmdYes){
			Vector v = new Vector();
			v.addElement(currentSettingValue);
			RmsOpt.saveRMS(v, "settings");
			if(currentSettingValue[1] != prevSettingValues[1]){
				tempData.setFontChange(true);
			}
			if(entry == 0){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
			}else if(entry == 1){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
			}else if(entry == 2){
				boolean isneedfresh = false;
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());	
				
				if(currentSettingValue[0] != prevSettingValues[0]){
//					System.out.println("word set change!");
					NovelsReaderScreen.getInstance().refreshWordSet();
					isneedfresh = true;
				}
				if(currentSettingValue[1] != prevSettingValues[1]){
					tempData.setFontChange(true);
//					if(!isneedfresh){
//						NovelsReaderScreen.getInstance().doRefresh();
//						NovelsReaderScreen.getInstance().backTop();
						isneedfresh = true;
//					}
					
				}	
				if(currentSettingValue[5] != prevSettingValues[5]){
					NovelsReaderScreen.getInstance().refreshBgColor(currentSettingValue[5]);
				}
				if(currentSettingValue[6] != prevSettingValues[6]){
					tempData.setColorChange(true);
//					if(!isneedfresh){
//						NovelsReaderScreen.getInstance().doRefresh();
//					}
					
					isneedfresh = true;
				}
				
				if(isneedfresh){
					NovelsReaderScreen.getInstance().doRefresh();
				}
			}else if(entry == 3){
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}else if(entry == 4){
				MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
			}else if(entry == 5){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
			}
			
			if(currentSettingValue[2] == 1){
				MusicPlayerThread.getInstance().play();
			}else if(currentSettingValue[2] == 0){
				MusicPlayerThread.getInstance().stop();
			}
			
			if(currentSettingValue[4] != prevSettingValues[4]){
				Vector vv = new Vector();
				if(currentSettingValue[4] == 0){
					vv.addElement("yes");
				}else if(currentSettingValue[4] == 1){
					vv.addElement("no");
				}
				RmsOpt.saveRMS(vv, "welcome");
			}
			
			
			
		}
		else if(cmd == cmdNo){
			
			if(entry == 0){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
			}else if(entry == 1){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().bookMainScreen);
			}else if(entry == 2){
				MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
			}else if(entry == 3){
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}else if(entry == 4){
				MainMidlet.getInstance().display.setCurrent(TopScreen.getInstance());
			}else if(entry == 5){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance().myBookScreen);
			}
		}else if(cmd == cmdSave){
			
			Vector v = new Vector();
			for(int i =0;i<7;i++){
				currentSettingValue[i] = settings[i].getSelectedIndex();
			}
			
			Vector vv = new Vector();
			if(currentSettingValue[4] == 0){
				vv.addElement("yes");
			}else if(currentSettingValue[4] == 1){
				vv.addElement("no");
			}
			RmsOpt.saveRMS(vv, "welcome");
				

			v.addElement(currentSettingValue);
			RmsOpt.saveRMS(v, "settings");
			showAlert(0,"����ɹ�!");
		}else if(cmd == cmdReset){
			for(int i=0;i<7;i++){
				if(i == 0){
					settings[i].setSelectedIndex(1, true);
				}else{
					settings[i].setSelectedIndex(0, true);
				}
				
			}
		}
		
	}
	
	private boolean isSave(){
		boolean change = false;
		Vector v = null;
		v = (Vector)RmsOpt.readRMS("settings");
		
		if(!v.isEmpty()){
//			System.out.println("v != null");
			saveSettingValue = (int[])v.elementAt(0);
			
		}
		for(int i =0;i<7;i++){
			currentSettingValue[i] = settings[i].getSelectedIndex();
		}
		
		v = (Vector)RmsOpt.readRMS("welcome");
		if(!v.isEmpty()){
			String set = (String)v.elementAt(0);
			if(set.equals("no")){
				saveSettingValue[4] = 1;
			}
			
		}
		for(int i =0;i<7;i++){
			if(currentSettingValue[i] != saveSettingValue[i]){
				change = true;
				break;
			}
		}
		
		return change;
	}

	private boolean isChange(){
		boolean change = false;
		
		for(int i =0;i<7;i++){
			currentSettingValue[i] = settings[i].getSelectedIndex();
		}
		
		for(int i =0;i<7;i++){
			if(currentSettingValue[i] != prevSettingValues[i]){
				change = true;
				break;
			}
		}
		
		return change;
	}
	public void paint(Graphics g){
		super.paint(g);
//		g.drawString(tempData.getTime(), 235, 3, 24);
	}
	protected boolean handleKeyPressed(int keyCode, int gameAction){
		if(gameAction == Canvas.LEFT){
			int activeTab = this.getActiveTab();
			if(activeTab == 0){
				this.setActiveTab(this.getTabCount()-1);
			}else{
				activeTab--;
				this.setActiveTab(activeTab);
			}
		}else if(gameAction == Canvas.RIGHT){
			int activeTab = this.getActiveTab();
			if(activeTab == this.getTabCount()-1){
				this.setActiveTab(0);
			}else{
				activeTab++;
				this.setActiveTab(activeTab);
			}
		}else{
			super.handleKeyPressed(keyCode, gameAction);
		}
		return true;
	}
	/**��ʾ������*/
	public void showAlert(int type,String content){
		if(type == 0){
			alert.removeAllCommands();
			alert.setTimeout(1500);
		}else if(type == 1){
			alert.removeAllCommands();
			alert.addCommand(cmdYes);
			alert.addCommand(cmdNo);
			alert.setTimeout(Alert.FOREVER);
		}
		
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	
	
}
