package cn.hunthawk.j2me.browser.conf;

import cn.hunthawk.j2me.bo.Fee;
import cn.hunthawk.j2me.bo.FeeResult;
import cn.hunthawk.j2me.html.Browser;
import cn.hunthawk.j2me.html.TagHandler;
import cn.hunthawk.j2me.html.html.HtmlBrowser;
import cn.hunthawk.j2me.ui.screen.FeeScreen;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.polish.xml.SimplePullParser;



public class FeeTagHandler 
extends TagHandler{
	public static final String TAG_HTML = "html";
	public static final String TAG_HEAD = "head";
	public static final String TAG_TITLE = "title";
	public static final String TAG_META = "meta";
	public static final String TAG_A = "a";
	public static final String TAG_DIV = "div";
	public static final String TAG_SPAN = "span";
	
	private HtmlBrowser htmlBrowser;
	private TagHandler parent;
	
	private Fee fee;
	private boolean feeList = false;
	private int feeCount = 0;
	private int feeIndex = 0;
	
	private boolean feeResultType = false;
	private FeeResult feeResult = null;
	public FeeTagHandler( cn.hunthawk.j2me.html.TagHandler tagHandler ) {
		this.parent = tagHandler;
	}
	public void register(Browser browser)
	{
		htmlBrowser = (HtmlBrowser)browser;
		browser.addTagHandler(TAG_HTML, this);
		browser.addTagHandler(TAG_HEAD, this);
		browser.addTagHandler(TAG_TITLE, this);
		browser.addTagHandler(TAG_A, this);
		browser.addTagHandler(TAG_DIV, this);
		browser.addTagHandler(TAG_SPAN, this);
		
		
	}
	
	public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style){
		if(!opening){
			if(TAG_HTML.equals(tagName)){
				
			}else if(TAG_HEAD.equals(tagName)){
				
			}else if(TAG_TITLE.equals(tagName)){
				
			}else if(TAG_META.equals(tagName)){
				
			}else if(TAG_DIV.equals(tagName)){
				if(this.feeList){
					if(this.feeIndex == this.feeCount){
						this.feeList = false;
						this.feeCount = 0;
						this.feeIndex = 0;
					}else{
						addFee(fee);
						this.feeIndex++;
						this.fee = new Fee();
						
					}
				}else if(this.feeResultType){
					addFeeResult(this.feeResult);
					this.feeResultType = false;
					
				}
			}else if(TAG_SPAN.equals(tagName)){
				
			}
			
		}else{
			if(TAG_HTML.equals(tagName)){
				System.out.println("计费解析开始");
			}else if(TAG_HEAD.equals(tagName)){
				
			}else if(TAG_TITLE.equals(tagName)){
				
			}else if(TAG_META.equals(tagName)){
				
			}else if(TAG_DIV.equals(tagName)){
				String type = (String)attributeMap.get("type");
				if("list".equals(type)){
					this.feeList = true;
					this.feeCount = Integer.parseInt((String)attributeMap.get("count"));
					fee = new Fee();
					parser.next();
				}else if("result".equals(type)){
					this.feeResultType = true;
					this.feeResult = new FeeResult();
				}
			}else if(TAG_SPAN.equals(tagName)){
				if(this.feeList){
					String type = (String)attributeMap.get("type");
					if("name".equals(type)){
						parser.next();
						fee.setName(parser.getText());
					}else if("unit".equals(type)){
						parser.next();
						fee.setUnit(parser.getText());
						System.out.println(parser.getText());
					}else if("amount".equals(type)){
						parser.next();
						fee.setAmount(parser.getText());
						System.out.println(parser.getText());
					}else if("info".equals(type)){
						parser.next();
						fee.setInfo(parser.getText());
						System.out.println(parser.getText());
					}else if("confirm".equals(type)){
						parser.next();
						String confrim = htmlBrowser.makeAbsoluteURL(parser.getText());
						fee.setConfirm(confrim);
						System.out.println(parser.getText());
					}
				}else if(this.feeResultType){
					String type = (String)attributeMap.get("type");
					if("info".equals(type)){
						parser.next();
						feeResult.setInfo(parser.getText());
					}else if("continue".equals(type)){
						parser.next();
						String continues = htmlBrowser.makeAbsoluteURL(parser.getText());
						feeResult.setContinues(continues);
					}
				}
			}
		}
		return false;
	}
	
	public void addFee(Fee fe1){
		StringItem str = null;
		StringItem br = new StringItem(null, null);
	    br.setLayout(Item.LAYOUT_NEWLINE_AFTER);
	    htmlBrowser.add(br);
		//#style browserText
		str = new StringItem(null,"业务名称:"+fe1.getName());
		htmlBrowser.add(str);
		htmlBrowser.add(br);
		
		//#style browserText
		str = new StringItem(null,"资费信息:"+fe1.getAmount()+"/"+fe1.getUnit());
		htmlBrowser.add(str);
		htmlBrowser.add(br);
		
		//#style browserText
		str = new StringItem(null,"业务详情:"+fe1.getInfo());
		htmlBrowser.add(str);
		htmlBrowser.add(br);
		
		
		FeeScreen.getInstance().setConfirmUrl(fe1.getConfirm());
	}
	
	public void addFeeResult(FeeResult feeResult1){
		StringItem str = null;
		StringItem br = new StringItem(null, null);
	    br.setLayout(Item.LAYOUT_NEWLINE_AFTER);
	    htmlBrowser.add(br);
	    
	  //#style browserText
		str = new StringItem(null,"订购结果:"+feeResult1.getInfo());
		htmlBrowser.add(str);
		
		FeeScreen.getInstance().setResult(feeResult1.getInfo(), feeResult1.getContinues());
	}
}
