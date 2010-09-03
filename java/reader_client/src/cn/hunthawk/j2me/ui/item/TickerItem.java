package cn.hunthawk.j2me.ui.item;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
/**
 * 
 * 图文并排当书名字数超过最大宽度时，书名自动滚动
 * 
 * @author zhangjf
 *
 */
public class TickerItem extends StringItem
{
	private int tickerXOffset;
	private int step = 1;
	private int tickerWidth;
	private String fristLine = null;
	private boolean frist = true;
	private int fristContent = 0;
	
	public TickerItem( String str,String line)
	{
		this( null, str ,null);
	}
	
	public TickerItem( String str,String line,Style style)
	{
		this( null, str ,style,line);
	}

	
	public TickerItem( String label ,String str, Style style ,String line)
	{
	
		super( label, str,style);
		
		this.fristLine = line;

		setAppearanceMode( Item.PLAIN );
	}
	
	
	public String getString() {
		return this.text;
	}
	
	
	public void setString( String text ) {
		
		super.setText(text);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		super.initContent( Integer.MAX_VALUE, Integer.MAX_VALUE );
		if(this.fristContent == 0){
			this.tickerWidth = this.contentWidth;
			lineWidth = this.contentWidth ;
			this.tickerXOffset = - this.contentWidth;
			this.fristContent = this.contentWidth;
		}else{
			this.maximumWidth = this.fristContent;
			this.tickerWidth = this.fristContent;
			lineWidth = this.fristContent ;
			this.tickerXOffset = this.fristContent+5;
		}		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
//		System.out.println("painting ticker at " + x + " width itemWidth=" + this.itemWidth);
//		System.out.println("rightBorder=" + rightBorder + ", screenWidth=" + this.screen.getScreenFullWidth());
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipHeight = g.getClipHeight();
		int clipWidth = g.getClipWidth();
		int width = (rightBorder - leftBorder);
		if(frist){
			this.setString(fristLine);
			initContent(0, 0);
			frist = false;
		}	
		g.clipRect( x, clipY, width, clipHeight);

		x -= this.tickerXOffset;

		super.paintContent(x, y, leftBorder, rightBorder, g);

		
		g.setClip(clipX, clipY, clipWidth, clipHeight);
		
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "tickeritem";
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
		if (this.tickerXOffset < (this.contentWidth-this.tickerWidth +3)) {
			this.tickerXOffset += this.step;
			
		} else {
			if (this.tickerWidth > this.contentWidth) {
				this.tickerXOffset -= (this.tickerWidth + this.paddingHorizontal) - this.step;
			
			} else {
				this.tickerXOffset = (this.tickerXOffset - this.contentWidth + this.tickerWidth) + this.step;
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
