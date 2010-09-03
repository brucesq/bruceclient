package cn.hunthawk.j2me.ui.screen;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.InitControler;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.ControlerOpt;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;

public class InviteScreen1 extends Form 
implements CommandListener{
	public static InviteScreen1 inviteScreen = null;
	
	private Command cmdExit = new Command("返回",Command.EXIT,1);
	private Command cmdSend = new Command("发送",Command.OK,1);

	
	private TextField tfNumber = null;
	private TextField tfYourName = null;
	private TextField tfContent = null;
	
	private String phoneNum = null;
	private String yourName = null;
	private String content = null;
	private Command cmdOk = new Command("是",Command.ITEM,1);
	private Command cmdNo = new Command("否",Command.BACK,1);
	private Alert alert;
	//#ifdef polish.debugEnabled
	Command showLogCmd = new Command( Locale.get("cmd.showLog"), Command.ITEM, 9 );
	Command cmdExit1 = new Command(Locale.get("cmd.exit"), Command.EXIT, 10 );
	//#endif
	private int entry = 0;
	public InviteScreen1(){
		//#style mainScreen
		super("推荐好友");
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);	
		this.addCommand(cmdExit);

		this.addCommand(cmdSend);
		this.setCommandListener(this);
		//#ifdef polish.debugEnabled
		this.addCommand( this.showLogCmd );
		this.addCommand( this.cmdExit );
		this.setCommandListener(this);
		//#endif
		//#style inputPhoneNum
		tfNumber = new TextField(null,"",11,TextField.PHONENUMBER);
		
		//#style inputPhoneNum
		tfYourName = new TextField(null,"",6,TextField.ANY);
		//#style inputArea
		tfContent = new TextField(null,"",255,TextField.ANY);
		InitControler control =  ControlerOpt.getInitControler(RmsOpt.readRMS("initcontrol"));
		tfContent.setText(control.getInvitation());
		//#style labelAppend
		this.append("输入手机号");
		this.append(tfNumber);
		//#style labelAppend
		this.append("输入您的名字");
		this.append(tfYourName);
		//#style labelAppend
		this.append("留言(255字符)");
		this.append(tfContent);
		
	}
	public static InviteScreen1 getInstance(){
		if(inviteScreen == null){
			inviteScreen = new InviteScreen1();
		}
		return inviteScreen;
	}
	public void setEntry(int i){
		this.entry = i;
	}
	public void commandAction(Command cmd, Displayable d) {
		//#ifdef polish.debugEnabled
		if (cmd == this.showLogCmd ) 
		{
			//#debug
			System.out.println("freeMemory:"+Runtime.getRuntime().freeMemory());
			//#debug
			System.out.println("totalMemory:"+Runtime.getRuntime().totalMemory());
			Debug.showLog(MainMidlet.getInstance().display);
			return;
		}
		if(cmd == this.cmdExit1)
		{
			MainMidlet.getInstance().quit();
		}
		//#endif
		if(cmd == cmdExit){
			tfNumber.setString("");
			tfYourName.setString("");
			tfContent.setString("");
			if(entry == 0){
				MainMidlet.getInstance().display.setCurrent(MainScreen.getInstance());
			}else if(entry == 1){
				MainMidlet.getInstance().display.setCurrent(ReaderControlScreen.getInstance());
			}
			
		}else if(cmd == cmdSend){
			phoneNum = tfNumber.getString();
			yourName = tfYourName.getString();
			content = tfContent.getString();
			
			int isNull = checkInput();
			switch(isNull){
			case 0:
				showAlert("号码不能为空！");
				break;
			case 1:
				showAlert("名字不能为空！");
				break;
			case 2:
				showAlert("内容不能为空！");
				break;
			case 3:
				
				if(!isValidPhoneNumber(phoneNum)){
					showAlert("号码错误");
				}else{
					//#debug
					System.out.println("ok!go on");
					String finalContent = content+"--"+yourName;
					new SendSMS(finalContent,"sms://"+phoneNum).start();
				}
				break;
			}
			
			
		}
		
	}
	
	private int checkInput(){
		int temp = 3;
		if(tfNumber.getString() == null || tfNumber.getString().equals("")){
			temp = 0;
		}
		if(tfYourName.getString() == null || tfYourName.getString().equals("")){
			temp = 1;
		}
		if(tfContent.getString() == null || tfContent.getString().equals("")){
			temp = 2;
		}
		return temp;
	}
	private boolean isValidPhoneNumber(String number) {
        char[] chars = number.toCharArray();

        if (chars.length <10) {
            return false;
        }

        int startPos = 0;

        if (chars[0] == '+') {
            startPos = 1;
        }

        for (int i = startPos; i < chars.length; ++i) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }
	
	/**提示框设置*/
	public void showAlert(String content){
		alert.setString(content);
		alert.setTimeout(1500);
		MainMidlet.getInstance().display.setCurrent(alert);
	}
	
	class SendSMS extends Thread{
		private String content = null;
		private String address = null;
		public SendSMS(String content,String address){
			this.content = content;
			this.address = address;
		}
		public void run(){
			//#debug
			System.out.println("send sms: send"+content+"to"+address);
			MessageConnection smsconn = null;
			
			try{
				
				smsconn = (MessageConnection)Connector.open(address);
				
				TextMessage message = (TextMessage)smsconn.newMessage(MessageConnection.TEXT_MESSAGE);
				
				message.setAddress(address);
				
				message.setPayloadText(content);
				
				smsconn.send(message);
				
				showAlert("发送成功");
			}
			catch(Exception e){
				e.printStackTrace();

				showAlert("发送失败");
			}
			finally{
				//#debug
				System.out.println("err1");
				if(smsconn != null){
					try{
						smsconn.close();
					}
					catch(Exception e){
						//#debug
						System.out.println("err");
					}
				}
			}
		}
	}
}
