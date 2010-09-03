package cn.hunthawk.j2me.control;

import cn.hunthawk.j2me.html.BrowserListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import de.enough.polish.ui.TextField;

public class ReaderControlListener implements BrowserListener{
	
	private HtmlBrowser htmlBrowser = null;
	public ReaderControlListener(HtmlBrowser htmlBrowser){
		this.htmlBrowser = htmlBrowser;
	}
	public void notifyDownloadEnd() {
		// TODO Auto-generated method stub
		
	}


	public void notifyDownloadStart(String url) {
		// TODO Auto-generated method stub
		
	}

	
	public void notifyPageEnd() {
		// TODO Auto-generated method stub
		System.out.println("htmlBrowser2 end");
		if(htmlBrowser.getItemSize() >2){
			if(htmlBrowser.get(1) instanceof TextField){
				System.out.println("gooo");
				htmlBrowser.focus(1);
			}
		}
		
	}

	
	public void notifyPageError(String url, Exception e) {
		// TODO Auto-generated method stub
		
	}

	
	public void notifyPageStart(String url) {
		// TODO Auto-generated method stub
		
	}
	
}
