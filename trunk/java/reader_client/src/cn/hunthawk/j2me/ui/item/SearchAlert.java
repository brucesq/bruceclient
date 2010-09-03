package cn.hunthawk.j2me.ui.item;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.bo.DownloadBook;
import cn.hunthawk.j2me.ui.screen.DownloadedBookScreen;
import cn.hunthawk.j2me.ui.screen.SearchResultScreen;
import cn.hunthawk.j2me.util.RmsOpt;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;

public class SearchAlert extends Form 
implements CommandListener {
	public static SearchAlert search = null;
	private TextField tfSearch = null;
	private Command cmdSearch = new Command("搜索",Command.ITEM,1);
	private Command cmdReturn = new Command("返回",Command.BACK,1);
	private Command cmdOk = new Command("是",Command.ITEM,1);
	private Command cmdNo = new Command("否",Command.BACK,1);
	private Alert alert;
	public SearchAlert(){
		//#style searchAlert
		super(null);
		//#style inputSearchBook
		tfSearch = new TextField(null,"输入书名",6,TextField.ANY);
		//#style messageAlert
		alert = new Alert( null);
		alert.setCommandListener(this);
		this.append(tfSearch);
		this.addCommand(cmdReturn);
		this.addCommand(cmdSearch);
		this.setCommandListener(this);
		
	}
	public static SearchAlert getInstance(){
		if(search == null){
			search = new SearchAlert();
			
		}
		return search;
	}
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdReturn){
			MainMidlet.getInstance().display.setCurrent(DownloadedBookScreen.getInstance());
		}else if(cmd == cmdSearch){
			String searchWord = tfSearch.getText();
			System.out.println("searchWord:"+searchWord);
			if(searchWord == null || searchWord.equals("")){
				showAlert(0,"输入内容不能为空!");
			}else{
				Vector result = searchDownloadedBook(searchWord);
				if(result.isEmpty()){
					showAlert(0,"搜索结果为空");
				}else{
					System.out.println("search result:");
					for(int i=0;i<result.size();i++){
						DownloadBook db = (DownloadBook)result.elementAt(i);
						System.out.println("book "+i+":"+db.getUebName());
					}
					SearchResultScreen.getInstance().init(result);
					MainMidlet.getInstance().display.setCurrent(SearchResultScreen.getInstance());
				}
			}
			
		}
		
	}
	
	protected boolean handleKeyPressed(int keyCode, int action){
		if(action == Canvas.FIRE){
			System.out.println("fire");
			if(tfSearch.getText().equals("输入书名")){
				System.out.println("fire1");
				tfSearch.setText("");
			}else{
				tfSearch.setCaretPosition(tfSearch.getString().length());
			}
		}
		return super.handleKeyPressed(keyCode, action);
	}
	public Vector searchDownloadedBook(String searchWord){
		
		Vector vResult = new Vector();
		Vector v = (Vector)RmsOpt.readRMS("downloadbook");
		if(!v.isEmpty()){
			int length = v.size();
			
			for(int i=length-1;i>-1;i--){
				
				DownloadBook db = (DownloadBook)v.elementAt(i);
				if(db.getUebName().indexOf(searchWord) != -1){
					vResult.addElement(db);
				}
				
			}
		}
		
		return vResult;
	}
	/**提示框设置*/
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
}
