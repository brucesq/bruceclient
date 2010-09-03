package cn.hunthawk.j2me.browser.conf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.bo.LocalBookControler;
import cn.hunthawk.j2me.html.Browser;
import cn.hunthawk.j2me.html.TagHandler;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.reader.local.LocalBook;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.polish.xml.SimplePullParser;

public class HtmlImageTagHandler 
extends TagHandler{
	public static final String TAG_HTML = "html";
	public static final String TAG_IMG = "img";
	
	private HtmlBrowser htmlBrowser;
	private TagHandler parent;
	public HtmlImageTagHandler( cn.hunthawk.j2me.html.TagHandler tagHandler ) {
		this.parent = tagHandler;
	}
	public void register(Browser browser)
	{
		htmlBrowser = (HtmlBrowser)browser;
		browser.addTagHandler(TAG_HTML, this);
		browser.addTagHandler(TAG_IMG, this);
		
	}
	
	public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style){
		
		if(!opening){
			if(TAG_HTML.equals(tagName)){
//				System.out.println("html image end");
			}else if(TAG_IMG.equals(tagName)){
//				System.out.println("local book html image end");
			}
		}else{
			if(TAG_HTML.equals(tagName)){
//				System.out.println("html image start");
			}else if(TAG_IMG.equals(tagName)){
//				System.out.println("local book html image start");
				String src = (String) attributeMap.get("src");
		        System.out.println("loacl src:"+src);
		        boolean cover = false;
		        if(src.indexOf("cover") != -1){
		        	cover = true;
		        }
		        String url = this.htmlBrowser.makeAbsoluteURL(src);
		        if(url.equals("HtmlImageTagHandler")){
//		        	System.out.println("handle local book images");
		        	LocalBook lb = tempData.currentLocalBook;
		        	byte[] img = lb.searchFileBytes(src);
		        	InputStream is = new ByteArrayInputStream(img);
		        	Image image = null;
		        	try {
						 image = Image.createImage(is);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (image != null) {
			        	ImageItem item = new ImageItem(null, image, Item.LAYOUT_DEFAULT, "");
			    		if (style != null) {
			    			item.setStyle(style);
			    		}
			    		htmlBrowser.add(item);
			    		addBr();
			        }
					if(cover){
						StringItem stringItem = null;
						LocalBookControler lbook = lb.getBookInfo().getBookOpfInfo();
						//#style browserText
	                    stringItem = new StringItem(null, "书名:"+lbook.getDcTitle());
	                    htmlBrowser.add(stringItem);
	                    addBr();
	                    //#style browserText
	                    stringItem = new StringItem(null, "作者:"+lbook.getDcAuthor());
	                    htmlBrowser.add(stringItem);
	                    addBr();
	                    //#style browserText
	                    stringItem = new StringItem(null, "简介:"+lbook.getDcLongDescription());
	                    htmlBrowser.add(stringItem);
					}
					
		        }
			}
		}
		
		if (this.parent == null) {
			return false;
		} else {
			return this.parent.handleTag(parentItem, parser, tagName, opening, attributeMap, style);
		}
	}
	
	public void addBr(){
		 StringItem stringItem = new StringItem(null, null);
	     stringItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
	     htmlBrowser.add(stringItem);
	}
}
