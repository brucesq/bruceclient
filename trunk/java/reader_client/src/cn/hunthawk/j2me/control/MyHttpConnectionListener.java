package cn.hunthawk.j2me.control;

import cn.hunthawk.j2me.MainMidlet;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;
import cn.hunthawk.j2me.reader.online.ReaderControlScreen;
import cn.hunthawk.j2me.ui.item.ItemDownLoadingGauge;
import cn.hunthawk.j2me.ui.screen.DownloadedBookScreen;
import cn.hunthawk.j2me.ui.screen.FeeScreen;
import cn.hunthawk.j2me.ui.screen.MainScreen;
import cn.hunthawk.j2me.util.DownloadThread2;
import cn.hunthawk.j2me.util.HttpConnectionListener;
import cn.hunthawk.j2me.util.HttpConnectionUtil;
import cn.hunthawk.j2me.util.RmsOpt;

public class MyHttpConnectionListener implements HttpConnectionListener{

	private int type = -1;
	HttpConnectionUtil hcu = null;
	public MyHttpConnectionListener(int type){
		this.type = type;
	}
	public MyHttpConnectionListener(int type,HttpConnectionUtil hcu){
		this.type = type;
		this.hcu = hcu;
	}
	


	public void notifyHttpConnectionError(int code, String msg) {
		// TODO Auto-generated method stub
		System.out.println("error:"+msg);
		switch(type){
		case 0:
			ReaderControlScreen.getInstance().showAlert(1, msg+",赠送鲜花失败!");
			break;
		case 1:
			ReaderControlScreen.getInstance().showAlert(1,msg+",赠送鸡蛋失败");
			break;
		case 2:
			ReaderControlScreen.getInstance().showAlert(1,msg+",订制提醒失败");
			break;
		case 3:
			ReaderControlScreen.getInstance().showAlert(1,msg+",发表留言失败");
			break;
		case 4:
			ReaderControlScreen.getInstance().showAlert(1,msg+",添加收藏失败");
			break;
		case 5:
			NovelsReaderScreen.getInstance().showAlert(1,msg+",添加书签失败");
			break;
		case 6://删除书签或收藏
			MainScreen.getInstance().myBookScreen.showAlert(1, msg+",删除失败");
		case 10:
			DownloadedBookScreen.getInstance().setDownloadItem(msg);
			DownloadedBookScreen.getInstance().setConnecting(false);
			break;
		case 20:
			
			break;
		case 21:
			MainMidlet.getInstance().quit();
			break;
		}
	}


	public void notifyHttpConnectionStart(String url) {
		// TODO Auto-generated method stub
		System.out.println("notifyHttpConnectionStart:"+url);
	}


	
	public void notifyHttpConnectionCreated(int code) {
		// TODO Auto-generated method stub
		//#debug
		System.out.println("notifyHttpConnectionCreated:"+code);
		if(code == 200 || code == 206 || code==302){
			switch(type){
			case 0://送鲜花
				ReaderControlScreen.getInstance().vote(0);
				break;
			case 1://扔鸡蛋
				ReaderControlScreen.getInstance().vote(1);
				break;
			case 2://订制更新
				ReaderControlScreen.getInstance().vote(2);
				break;
			case 3://发表评论
				ReaderControlScreen.getInstance().vote(3);
				break;
			case 4://添加收藏
				ReaderControlScreen.getInstance().showAlert(1, hcu.getContent());
				if(hcu != null) hcu = null;
				break;
			case 5://添加书签
				NovelsReaderScreen.getInstance().showAlert(1, hcu.getContent());
				if(hcu != null) hcu = null;
				break;
			case 6://删除书签或收藏
				MainScreen.getInstance().myBookScreen.delBookStore();
			case 10:
				//
				DownloadedBookScreen.getInstance().setDownloadItem("已获取文件信息");
				break;
			case 20:
				
				break;
			case 21:
				RmsOpt.deleteRMS("runningtime");
				MainMidlet.getInstance().quit();
				break;
			}
			
		}else{
			switch(type){
			case 0:
				ReaderControlScreen.getInstance().showAlert(1, "服务器返回错误，赠送鲜花失败!");
				break;
			case 1:
				ReaderControlScreen.getInstance().showAlert(1,"服务器返回错误，赠送鸡蛋失败");
				break;
			case 2:
				ReaderControlScreen.getInstance().showAlert(1,"服务器返回错误，订制提醒失败");
				break;
			case 3:
				ReaderControlScreen.getInstance().showAlert(1,"服务器返回错误，发表留言失败");
				break;
			case 4:
				ReaderControlScreen.getInstance().showAlert(1,"服务器返回错误，添加收藏失败");
				break;
			case 5:
				NovelsReaderScreen.getInstance().showAlert(1,"服务器返回错误，添加书签失败");
				break;
			case 6://删除书签或收藏
				MainScreen.getInstance().myBookScreen.showAlert(1, "服务器返回错误，删除失败");
			case 10:
				DownloadedBookScreen.getInstance().setDownloadItem("服务器返回错误,文件信息未知");
				DownloadedBookScreen.getInstance().setConnecting(false);
				break;
			case 20:
				
				break;
			case 21:
				MainMidlet.getInstance().quit();
				break;
			}
		}
		System.gc();
	}
	
	public void notifyHttpConnectionTimeOut(){
		//#debug
		System.out.println("time out 啦");
		
		String msg = "网络超时";
		switch(type){
		case 0:
			ReaderControlScreen.getInstance().showAlert(1, msg+",赠送鲜花失败!");
			break;
		case 1:
			ReaderControlScreen.getInstance().showAlert(1,msg+",赠送鸡蛋失败");
			break;
		case 2:
			ReaderControlScreen.getInstance().showAlert(1,msg+",订制提醒失败");
			break;
		case 3:
			ReaderControlScreen.getInstance().showAlert(1,msg+",发表留言失败");
			break;
		case 4:
			ReaderControlScreen.getInstance().showAlert(1,msg+",添加收藏失败");
			break;
		case 5:
			NovelsReaderScreen.getInstance().showAlert(1,msg+",添加书签失败");
			break;
		case 6://删除书签或收藏
			MainScreen.getInstance().myBookScreen.showAlert(1, msg+",删除失败");
			break;
		case 10:
			DownloadedBookScreen.getInstance().setDownloadItem(msg+"信息读取失败!");
			DownloadedBookScreen.getInstance().setConnecting(false);
			break;
		case 20:
			
			break;
		case 21:
			MainMidlet.getInstance().quit();
			break;
		}
	}

}
