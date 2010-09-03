package cn.hunthawk.j2me.control;

import cn.hunthawk.j2me.html.BrowserListener;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.online.NovelsReaderScreen;




public class NovelsReaderListener 
implements BrowserListener{

	private boolean isShow = false;
	private HtmlBrowser htmlBrowser = null;
	public NovelsReaderListener(HtmlBrowser htmlBrowser){
		this.htmlBrowser = htmlBrowser;
	}
	public void notifyDownloadEnd() {
		
		
	}

	
	public void notifyDownloadStart(String arg0) {
		
		
	}

	
	public void notifyPageEnd() {
//		NovelsReaderScreen.getInstance().setScrollYOffset(0, false);
//		System.out.println("notifyPageEnd");
		if(htmlBrowser.isLocalBrowserType()){
			
			htmlBrowser.addLastChapterContainer();
			
		}
		
	}


	public void notifyPageError(String arg0, Exception arg1) {	
		
	}

	
	public void notifyPageStart(String url) {
		
		System.out.println("page:"+url);
		
		NovelsReaderScreen.getInstance().setScrollYOffset(0, false);
		
	}
	
	public void AnalysisWordNum(){
		
	}

}
