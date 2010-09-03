package cn.hunthawk.j2me.ui.item;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;

/**
 * 
 * 通用的链接StringItem,当链接本身的Label长度超过了Item的长度，
 * 链接名称会自动滚动
 * @author zhangjf
 *
 */
public class StringItemTicker extends StringItem{
	
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_TICKER = 1;
	
	private int type = TYPE_NORMAL;
	private int tickerXOffset;
	private int step = 2;
	private int tickerWidth;
	
	private int maxItemHeight = 0;
	
	private String fullString = null;
	private boolean frist = true;
	
	public StringItemTicker(String label ,String str){
		this(label , str , null);
		
	}
	
	public StringItemTicker(String label ,String str ,Style style){
		super(label, str.substring(0, 1) ,style);
		this.fullString = str;
	}
	
	public String getString(){
		return this.text;
	}
	public void setString(String str){
		super.setText(str);
	}
	
	protected void initContent(int firstLineWidth, int lineWidth) {
		super.initContent( Integer.MAX_VALUE, Integer.MAX_VALUE );
		System.out.println("text---"+this.text+"-------");
		System.out.println("firstLineWidth:"+firstLineWidth+"lineWidth:"+lineWidth);
		System.out.println("tickerWidht:"+this.tickerWidth+"contentWidth:"+this.contentWidth+"itemWidth:"+this.itemWidth);
		System.out.println("getBackgroundWidth:"+this.getBackgroundWidth()+
				"getContentWidth:"+this.getContentWidth());
		System.out.println("1this.getAbsoluteX():"+this.getAbsoluteX()+"width:"+this.getScreen().getScreenContentWidth());
//		System.out.println(t);
		if(this.maxItemHeight == 0){
//			this.maxItemHeight = this.get
//			this.getScreen().focus(this);
		}
		if(this.getContentWidth() < lineWidth - 10){
			type = this.TYPE_NORMAL;
			this.tickerWidth = 0;
//			AnimationThread.removeAnimationItem( this );
		}else{
//			this.maximumWidth = lineWidth;
//			this.setString(this.getString().substring(0, 8));
		}
//		if(maxItemHeight == 0){
//			this.tickerWidth = this.contentWidth;
//			lineWidth = this.contentWidth;
//			this.tickerXOffset = - this.contentWidth;
//			this.maxItemHeight = this.contentWidth;
//
//		}else{
//			this.maximumWidth = this.maxItemHeight;
//			this.tickerWidth = this.maxItemHeight;
//			lineWidth = this.maxItemHeight ;
//			this.tickerXOffset = this.maxItemHeight+5;
//		}
//		
//		if(this.tickerWidth < this.contentWidth){
//		AnimationThread.removeAnimationItem( this );
//			type = this.TYPE_TICKER;
//		}else{
//			type = this.TYPE_NORMAL;
//		}
		
		
	}
	
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		System.out.println("x:"+x+"y:"+y+"leftBorder:"+leftBorder+"rightBorder:"+rightBorder);
		if(type == this.TYPE_NORMAL){
			super.paintContent(x, y, leftBorder, rightBorder, g);
		}else{
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipHeight = g.getClipHeight();
			int clipWidth = g.getClipWidth();
			int width = (rightBorder - leftBorder);
			if(frist){
				this.setString(fullString);
				initContent(0, 0);
				frist = false;
			}	
			g.clipRect( x, clipY, width, clipHeight);
			x -= this.tickerXOffset;
//			System.out.println("tickerWidht:"+this.tickerWidth+"contentWidth:"+this.contentWidth+"itemWidth:"+this.itemWidth);
			super.paintContent(x, y, leftBorder, rightBorder, g);
//			System.out.println("tickerWidht:"+this.tickerWidth+"contentWidth:"+this.contentWidth);
			g.setClip(clipX, clipY, clipWidth, clipHeight);
		}
		
		
	}
	
	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "stringitemticker";
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		this.font = style.font;
		this.textColor = style.getFontColor();
		//#ifdef polish.css.ticker-step
			Integer stepInt = style.getIntProperty("ticker-step");
			if (stepInt != null) {
				this.step = stepInt.intValue();
			}
		//#endif
		super.setStyle(style);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		if (this.tickerWidth == 0) {
			return false;
		}
		if (this.tickerXOffset < this.tickerWidth) {
			this.tickerXOffset += this.step;
		} else {
			if (this.tickerWidth > this.contentWidth) {
				this.tickerXOffset -= (this.tickerWidth + this.paddingHorizontal) - this.step;
			} else {
				this.tickerXOffset = (this.tickerXOffset - this.contentWidth) + this.step;
			}
		}
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#hideNotify()
	 */
	protected void hideNotify() {
		super.hideNotify();
		AnimationThread.removeAnimationItem( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeStringCustomItem#showNotify()
	 */
	protected void showNotify() {
		super.showNotify();
		AnimationThread.addAnimationItem( this );
	}
}
