package cn.hunthawk.j2me.reader.online;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.LocalBookHeaderInfo;
import cn.hunthawk.j2me.bo.LocalBookUserNote;

import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;

public class UserBookNoteScreen extends Form 
implements CommandListener{

	public static UserBookNoteScreen noteScreen = null;
	
	private Command cmdAdd = new Command("����",Command.ITEM,1);
	private Command cmdReturn = new Command("����",Command.BACK,1);
	private Command cmdReStart = new Command("����",Command.ITEM,2);
	private Command cmdOk = new Command("��",Command.ITEM,1);
	private Command cmdNo = new Command("��",Command.BACK,1);
	private TextField tfContent = null;
	private Alert alert = null;
	public UserBookNoteScreen(){
		//#style mainScreen
		super("����ʼ�");
		this.addCommand(cmdAdd);
		this.addCommand(cmdReStart);
		this.addCommand(cmdReturn);
		this.setCommandListener(this);
		//#style inputArea
		tfContent = new TextField(null,"",1000,TextField.ANY);
		this.append(tfContent);
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);
	}
	public static UserBookNoteScreen getInstance(){
		if(noteScreen == null){
			noteScreen = new UserBookNoteScreen();
		}
		return noteScreen;
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == this.cmdReturn){
			MainMidlet.getInstance().display.setCurrent(NovelsReaderScreen.getInstance());
		}else if(cmd == this.cmdReStart){
			tfContent.setText("");
		}else if(cmd == this.cmdAdd){
			saveNote();
		}
		
	}
	public void saveNote(){
		String note = tfContent.getText();
		if(note == null || note.equals("")){
			showAlert(0,"���ݲ���Ϊ��");
		}else{
			LocalBookHeaderInfo lbhi = tempData.currentLocalBook.getBookInfo().getHeader();
			if(lbhi != null){
				String packageID = String.valueOf(lbhi.getPackageID());
//				System.out.println("packageID:"+packageID);
//				System.out.println("note:"+note);
				String time = tempData.getNowTime();
				LocalBookUserNote lbun = new LocalBookUserNote();
				lbun.setNote(note);
				lbun.setDate(time);
				lbun.setPackageID(packageID);
				Vector notes = (Vector)RmsOpt.readRMS("usernote");
				notes.addElement(lbun);
				RmsOpt.saveRMS(notes, "usernote");
				showAlert(0,"����ɹ�");
				tfContent.setText("");
			}else{
				showAlert(0,"�޷���ø�����Ϣ");
			}
		}
	}
	
	/**��ʾ������*/
	public void showAlert(int type,String content){
		if(type == 0){
			alert.removeAllCommands();
			alert.setTimeout(1500);
		}else if(type == 1){
			alert.removeAllCommands();
			alert.addCommand(cmdNo);
			alert.addCommand(cmdOk);
			alert.setTimeout(Alert.FOREVER);
		}
		
		alert.setString(content);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.drawString(tempData.getTime(), 235, 3, 24);
	}
}
