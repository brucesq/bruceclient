package cn.hunthawk.j2me.ui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.List;
import de.enough.polish.ui.StringItem;

public class WelcomePageSetScreen extends Form 
implements CommandListener{
	public static WelcomePageSetScreen setScreen = null;
	private ChoiceGroup set = null;
	
	private Command cmdClose = new Command("关闭",Command.BACK,1);
	private Command cmdSave = new Command("保存",Command.ITEM,6);
	public WelcomePageSetScreen(){
		//#style mainScreen
		super("欢迎页设置");
		
		this.addCommand(cmdClose);
		this.addCommand(cmdSave);
		this.setCommandListener(this);
		
		//#style SystemSettingChoiceGroup
		set = new ChoiceGroup("",ChoiceGroup.EXCLUSIVE);
		//#style SystemSettingItem
		set.append("登录后继续使用欢迎页", null);
		//#style SystemSettingItem
		set.append("登录后不再使用欢迎页", null);
		set.setSelectedIndex(0, true);
		
		this.append(set);
	}
	public static WelcomePageSetScreen getInstance(){
		if(setScreen == null){
			setScreen = new WelcomePageSetScreen();
		}
		return setScreen;
	}
	
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == this.cmdClose){
			MainMidlet.getInstance().display.setCurrent(WelcomeScreen.getInstance());
		}else if(cmd == this.cmdSave){
			int index = set.getSelectedIndex();
			Vector v = new Vector();
			if(index == 0){
				v.addElement("yes");
			}else if(index == 1){
				v.addElement("no");
			}
			RmsOpt.saveRMS(v, "welcome");
			MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
		}
		
	}
	
}
