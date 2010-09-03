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
			ReaderControlScreen.getInstance().showAlert(1, msg+",�����ʻ�ʧ��!");
			break;
		case 1:
			ReaderControlScreen.getInstance().showAlert(1,msg+",���ͼ���ʧ��");
			break;
		case 2:
			ReaderControlScreen.getInstance().showAlert(1,msg+",��������ʧ��");
			break;
		case 3:
			ReaderControlScreen.getInstance().showAlert(1,msg+",��������ʧ��");
			break;
		case 4:
			ReaderControlScreen.getInstance().showAlert(1,msg+",����ղ�ʧ��");
			break;
		case 5:
			NovelsReaderScreen.getInstance().showAlert(1,msg+",�����ǩʧ��");
			break;
		case 6://ɾ����ǩ���ղ�
			MainScreen.getInstance().myBookScreen.showAlert(1, msg+",ɾ��ʧ��");
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
			case 0://���ʻ�
				ReaderControlScreen.getInstance().vote(0);
				break;
			case 1://�Ӽ���
				ReaderControlScreen.getInstance().vote(1);
				break;
			case 2://���Ƹ���
				ReaderControlScreen.getInstance().vote(2);
				break;
			case 3://��������
				ReaderControlScreen.getInstance().vote(3);
				break;
			case 4://����ղ�
				ReaderControlScreen.getInstance().showAlert(1, hcu.getContent());
				if(hcu != null) hcu = null;
				break;
			case 5://�����ǩ
				NovelsReaderScreen.getInstance().showAlert(1, hcu.getContent());
				if(hcu != null) hcu = null;
				break;
			case 6://ɾ����ǩ���ղ�
				MainScreen.getInstance().myBookScreen.delBookStore();
			case 10:
				//
				DownloadedBookScreen.getInstance().setDownloadItem("�ѻ�ȡ�ļ���Ϣ");
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
				ReaderControlScreen.getInstance().showAlert(1, "���������ش��������ʻ�ʧ��!");
				break;
			case 1:
				ReaderControlScreen.getInstance().showAlert(1,"���������ش������ͼ���ʧ��");
				break;
			case 2:
				ReaderControlScreen.getInstance().showAlert(1,"���������ش��󣬶�������ʧ��");
				break;
			case 3:
				ReaderControlScreen.getInstance().showAlert(1,"���������ش��󣬷�������ʧ��");
				break;
			case 4:
				ReaderControlScreen.getInstance().showAlert(1,"���������ش�������ղ�ʧ��");
				break;
			case 5:
				NovelsReaderScreen.getInstance().showAlert(1,"���������ش��������ǩʧ��");
				break;
			case 6://ɾ����ǩ���ղ�
				MainScreen.getInstance().myBookScreen.showAlert(1, "���������ش���ɾ��ʧ��");
			case 10:
				DownloadedBookScreen.getInstance().setDownloadItem("���������ش���,�ļ���Ϣδ֪");
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
		System.out.println("time out ��");
		
		String msg = "���糬ʱ";
		switch(type){
		case 0:
			ReaderControlScreen.getInstance().showAlert(1, msg+",�����ʻ�ʧ��!");
			break;
		case 1:
			ReaderControlScreen.getInstance().showAlert(1,msg+",���ͼ���ʧ��");
			break;
		case 2:
			ReaderControlScreen.getInstance().showAlert(1,msg+",��������ʧ��");
			break;
		case 3:
			ReaderControlScreen.getInstance().showAlert(1,msg+",��������ʧ��");
			break;
		case 4:
			ReaderControlScreen.getInstance().showAlert(1,msg+",����ղ�ʧ��");
			break;
		case 5:
			NovelsReaderScreen.getInstance().showAlert(1,msg+",�����ǩʧ��");
			break;
		case 6://ɾ����ǩ���ղ�
			MainScreen.getInstance().myBookScreen.showAlert(1, msg+",ɾ��ʧ��");
			break;
		case 10:
			DownloadedBookScreen.getInstance().setDownloadItem(msg+"��Ϣ��ȡʧ��!");
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
