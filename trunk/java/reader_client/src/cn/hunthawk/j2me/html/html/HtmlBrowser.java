//#condition polish.usePolishGui

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2007 - 2008 Michael Koch / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package cn.hunthawk.j2me.html.html;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.html.Browser;
import cn.hunthawk.j2me.html.ProtocolHandler;
import cn.hunthawk.j2me.html.protocols.HttpProtocolHandler;
import cn.hunthawk.j2me.html.protocols.ResourceProtocolHandler;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.tempData;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;

/**
 * TODO: Write good docs.
 * 
 * polish.Browser.UserAgent
 * polish.Browser.MaxRedirects
 * polish.Browser.Gzip
 * polish.Browser.POISupport
 * polish.Browser.PaintDownloadIndicator
 * 
 * @see HttpProtocolHandler
 * @see ResourceProtocolHandler
 * @see RedirectHttpConnection
 */
public class HtmlBrowser
extends Browser
{

	private boolean BROWSER_DEFINITION_KEY = true;
	private boolean localBrowser = false;
	private boolean BROWSER_DEFINITION_BGCOLOR = false;
	private HtmlTagHandler htmlTagHandler;
	private Vector vMeta = null;
	private int scrollBarMaxOffset =0;
	private int chapterWordNum = 0;
	private Vector contentContainer = new Vector();
	private Vector subContainer = new Vector();
	private boolean door = false;
	private boolean isFristOver = false;
	private String temp = null;
	private int setWordNum = 0;
	private boolean isShowTitle = false;
	private boolean useDefinedTextStyle = true;
	int height = 0;
  	/**
  	 * Creates a new browser using the default ".browser" style and default tag- and protocol handlers.
  	 */
  public HtmlBrowser()
  {
	  //#if polish.css.style.browser && !polish.LibraryBuild
	  	//#style browser
	  	//# this();	
	  //#else
	  	this( (Style) null );
	  //#endif
	  	
	  this.allowCycling = true;
	 
  }
  
  /**
   * Creates a new browser with the given style, the default tag handler and default protocol handlers (http, https, resource)
   * 
   * @param style the style
   * @see #getDefaultProtocolHandlers()
   * @see HtmlTagHandler
   */
  public HtmlBrowser( Style style )
  {
	  this( new HtmlTagHandler(), getDefaultProtocolHandlers(), style );
  }
  
  /**
   * Creates a new browser with the specified html tag handler
   * 
   * @param tagHandler the HtmlTagHandler used for this browser
   * @param protocolHandlers the protocol handlers
   * 
   * @throws NullPointerException when the tagHandler is null
   */
  public HtmlBrowser( HtmlTagHandler tagHandler, ProtocolHandler[] protocolHandlers )
  {
	  //#if polish.css.style.browser && !polish.LibraryBuild
	  	//#style browser
	  	//# this( tagHandler, protocolHandlers );	
	  //#else
	  	this( tagHandler, protocolHandlers, (Style) null );
	  //#endif	  
  }
  
  /**
   * Creates a new browser with the specified html tag handler
   * 
   * @param tagHandler the HtmlTagHandler used for this browser
   * @param protocolHandlers the protocol handlers
   * @param style the style of this browser
   * 
   * @throws NullPointerException when the tagHandler is null
   */
  public HtmlBrowser( HtmlTagHandler tagHandler, ProtocolHandler[] protocolHandlers,  Style style )
  {
	  super( protocolHandlers, style );
	  tagHandler.register(this);
	  this.htmlTagHandler = tagHandler;
	 
  }
  
  /**
   * Sets the form listener that is notified about form creation and submission events
   * 
   * @param listener the listener, use null for de-registering a previous listener
   */
  public void setFormListener( FormListener listener ) {
	  this.htmlTagHandler.setFormListener( listener );
  }
  
  
  protected void handleText(String text)
  {
	
    if (text.length() > 0)
    {
      StringTokenizer st = new StringTokenizer(text, " \n\t");
      
      while (st.hasMoreTokens())
      {
        String str = st.nextToken();
        str = TextUtil.replace(str, "&nbsp;", " ");
        StringItem stringItem = null;
        if (this.htmlTagHandler.textStyle != null) {
        	stringItem = new StringItem(null, str, this.htmlTagHandler.textStyle);
        } 
        else if (this.htmlTagHandler.textBold && this.htmlTagHandler.textItalic)
        {
          //#style browserTextBoldItalic
          stringItem = new StringItem(null, str);
        }
        else if (this.htmlTagHandler.textBold)
        {
          //#style browserTextBold
          stringItem = new StringItem(null, str);
        }
        else if (this.htmlTagHandler.textItalic)
        {
          //#style browserTextItalic
          stringItem = new StringItem(null, str);
        }
        else
        {
        	if(localBrowser){
        		if(setWordNum == 0){
        			setWordNum = getUserSetWord();
        		}
        		chapterWordNum += str.length();
        		if(temp != null){
        			str = temp + str;
        			temp = null;
        		}
        		
        		if(chapterWordNum > setWordNum){
        			chapterWordNum -= str.length();
        			int distance = setWordNum - chapterWordNum;
        			door = true;
        			chapterWordNum = str.length() - distance;
        			temp = str.substring(distance,str.length());
        			str = str.substring(0, distance);
        		}
        	}
        	if(localBrowser){
    			Style localStyle = StyleSheet.getStyle("browserTextReader");
    			Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, tempData.getFontSize());
    			localStyle.font = font;
    			localStyle.fontColorObj = new Color(tempData.getFontColor());
            	stringItem = new StringItem(null, str,localStyle);
    		}else{
    			
    			
    			if(this.getCurrentUrl().indexOf("pg=d") != -1){
        			Style style = StyleSheet.getStyle("browserTextReader");
	
                	Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, tempData.getFontSize());
                	style.font = font;
                	style.fontColorObj = new Color(tempData.getFontColor());
                	
                	stringItem = new StringItem(null, str,style);
                	
        		}else{
        			if(str.indexOf("【") != -1){
        				if(useDefinedTextStyle){
        					//#style browserText2
                            stringItem = new StringItem(null, str);
        				}else{
        					//#style browserText
                            stringItem = new StringItem(null, str);
        				}
        				
        			}else{
        				//#style browserText
                        stringItem = new StringItem(null, str);
        			}
        			
        		}
    		}

        }
       if(htmlTagHandler.getAItemText() != null){
    	   htmlTagHandler.setAItemText(stringItem.getText());
       }else{
    	   if(localBrowser){
    		   if(!isFristOver){
    			   add(stringItem);    
    		   }
    		   subContainer.addElement(stringItem);
    		   if(door){
    			   isFristOver = true;
    			   contentContainer.addElement(subContainer);
//    			   System.out.println("完成分割第"+contentContainer.size()+"页");
    			   
    			   subContainer = new Vector();
    			   
    			   door = false;
    		   }
    		  
    	   }else{
    		   add(stringItem); 
    	   }
    	   
       }     
        
      }
    }
  }
  public int getUserSetWord(){
	  int wordset = 1000;
	  Vector v = (Vector)RmsOpt.readRMS("settings");
	  if(!v.isEmpty()){
			int [] setting = (int[])v.elementAt(0);
			int value = setting[0];
			switch(value){
			case 0:
				wordset = 500;
				break;
			case 1:
				wordset = 1000;
				break;
			case 2:
				wordset = 2000;
				break;
			}
	  }
	  return wordset;
	  
  }
  public boolean isLocalBrowserType(){
	  return localBrowser;
  }
  public void initLocalChapter(){
	  if(localBrowser){
		  chapterWordNum = 0;
		  contentContainer.removeAllElements();
		  subContainer = new Vector();
		  temp = null;
		  isFristOver = false;
		  setWordNum = 0;
		  door = false;
	  }
  }
  
  public Vector getLocalCurrentChapterContainer(){
	  return this.contentContainer;
  }
  public void addLastChapterContainer(){
	  if(localBrowser){
		  contentContainer.addElement(subContainer); 
	  }
	  
  }

  
  public void addMetaInfo(StringItem meta){
	  if(vMeta == null) vMeta = new Vector();
	  vMeta.addElement(meta);
  }
  public Vector getMeta(){
	  return this.vMeta;
  }
  public void clearMeta(){
	  if(vMeta != null){
		  vMeta.removeAllElements();
	  }
  }
  public void setSupressTextStyle(){
	  this.useDefinedTextStyle = false;
  }
  public void setShowTitle(boolean isShow){
	  this.isShowTitle = isShow;
  }
  public boolean getShowTitle(){
	  return this.isShowTitle;
  }
  
  public boolean handleKeyPressed(int keyCode, int action){

	  if(BROWSER_DEFINITION_KEY){
		  if(this.getFocusedItem() instanceof ChoiceGroup){
			  System.out.println("here is handle ChoiceGroup Code");
			  
			  ChoiceGroup cg = (ChoiceGroup)this.getFocusedItem();
			  
			  boolean close = cg.isPopupClosed();
			  
			  System.out.println("close："+close);
			  if(!close){
				  return super.handleKeyPressed(keyCode, action);
			  }else{
				  if(action == Canvas.FIRE){
//					  System.out.println("close1："+close);
					  
					  return super.handleKeyPressed(keyCode, action);
				  }
			  }
		  }
		  if(action == Canvas.FIRE){
			  String type = (String)this.getFocusedItem().getAttribute("type");
//			  System.out.println("typeee:"+type);
			  if(type != null){
				  if(type.equals("link")){
//					  System.out.println("link:");
					  this.handleLinkCommand();
				  }else if(type.equals("submit")){
//					  System.out.println("submit:");
					  this.handleSubmitCommand();
				  }
			  }else{
//				  System.out.println("ddff");
				  return super.handleKeyPressed(keyCode, action);
			  }
			  
		  }else if(action == Canvas.DOWN){

			  int index = this.getFocusedIndex();
			  do{
				  index++;
				  if(index > this.getItemSize()){
					  break;
				  }
			  }while(!this.focus(index));

			  this.scroll(0, this.getFocusedItem());

		  }
		  else if(action == Canvas.UP){
			  boolean top = true;
			  int index = this.getFocusedIndex();
			  do{
				  index--;
				  if(index < 0){
					  this.setScrollYOffset(0, false);
					  top = false;
					  break;
				  }
			  }while(!this.focus(index));

			  if(top){
				  this.scroll(0, this.getFocusedItem());
				
			  }
		  }
		  else if(action == Canvas.LEFT){
			  if(this.getFocusedIndex() == -1) return false;
			  int height = this.getScrollYOffset()+this.getScreen().getAvailableHeight();
			  if(height > 0){
				  height = 0;
				  this.setScrollYOffset(height,false);
				  this.focus(-1);
				  Down();
			  }else{
				  this.setScrollYOffset(height,false);
				  this.focus(-1);
				  Down();
			  }
			  
		  }else if(action == Canvas.RIGHT){
			  if(this.getFocusedIndex() == -1) return false;
			  if(this.itemHeight <= this.getScreen().getAvailableHeight()) return false;
			  int height = this.getScrollYOffset()-this.getScreen().getAvailableHeight();
			  if(Math.abs(height) > this.getScrollBarMaxOffset()){
				  height = -this.getScrollBarMaxOffset()-20;
				  this.setScrollYOffset(height,false);
//				  this.focus(-1);
				  Down();
			  }
			  else{
				  this.setScrollYOffset(height,false);
//				  this.focus(-1);
				  Down();
			  }
		  }
		  else{
			  return super.handleKeyPressed(keyCode, action);  
		  }
	  }else{
		  return super.handleKeyPressed(keyCode, action);
//		  this.scroll(0, this.getFocusedItem());  
	  }
	  
	  System.gc();
	  return true;
	}
  protected boolean handleKeyRepeated(int keyCode,
          int action){
	  
	  return this.handleKeyPressed(keyCode, action);

  }
  public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g){
	  super.paintContent(x, y, leftBorder, rightBorder, g);
	  if(this.getFocusedItem() instanceof ChoiceGroup){
		  this.getFocusedItem().paint(this.getFocusedItem().getAbsoluteX(), this.getFocusedItem().getAbsoluteY(),
				  this.getFocusedItem().getAbsoluteX()-1, this.getFocusedItem().getBackgroundWidth()+this.getFocusedItem().getAbsoluteX()+1,g);
	  }
  }
  public void setDifintionKey(boolean key){
	  this.BROWSER_DEFINITION_KEY = key;
  }
  public void setLocalBrowserType(boolean local){
	  this.localBrowser = local;
  }
  public void setDifintionBgColor(boolean color){
	  this.BROWSER_DEFINITION_BGCOLOR = color;
  }
  public boolean getDifintionBgColor(){
	  return this.BROWSER_DEFINITION_BGCOLOR;
  }
  public void handleLinkCommand(){
	  this.htmlTagHandler.handleLinkCommand();
  }
  public void handleSubmitCommand(){
	  this.htmlTagHandler.handleSubmitCommand();
  }
  public int getItemSize(){
	  return this.getItems().length-1;
  }
  public int getScrollBarMaxOffset(){
	  if(this.itemHeight < this.getScreen().getAvailableHeight()){
		  return 0;
	  }else{
		  return this.itemHeight-this.getScreen().getAvailableHeight()+21;
	  }
	  
  }
  
  public String getSubmitOfGetMethodUrl(){
	  return this.htmlTagHandler.getSubmitOfGetUrl();
  }
  
  public void Down(){
	  if(this.isInScreenContent(this.getFocusedItem())){
		  int index = this.getFocusedIndex();
		  int closestIndex = this.getClosestHrefItemIndex(Canvas.DOWN,index);
//		  int closestIndex = this.get.focusClosestItem(index);
		  System.out.println("closest Index:"+closestIndex);
		  if(this.isInScreenContent(this.getItems()[closestIndex])){
			  System.out.println("into here1:");
			  boolean isBottom = true;
			  do{
				  index++;
				  if(index > this.getItemSize()){
//					  System.out.println("-this.getScrollBarMaxOffset():"+(-this.getScrollBarMaxOffset()));
					  isBottom = false;
					  this.setScrollYOffset((-this.getScrollBarMaxOffset()), false);
					  break;
				  }
			  }while(!this.focus(index));
			  if(isBottom){
				  this.scroll(0, this.getFocusedItem());  
			  }
			  
		  }else{
			  System.out.println("into here2:");
			  int height = this.getScrollYOffset()-20;					
			  if(Math.abs(height) > this.getScrollBarMaxOffset()) height = -this.getScrollBarMaxOffset();				 
			  this.setScrollYOffset(height,false);
		  }
		  
	  }else{			
		  int index = this.getFristItemInScreenIndex();
		  if(index == -1){				
			  int height = this.getScrollYOffset()-20;					
			  if(Math.abs(height) > this.getScrollBarMaxOffset()) height = -this.getScrollBarMaxOffset();				 
			  this.setScrollYOffset(height,false);
		  }else{
			  if(this.isInScreenContent(this.getItems()[index])){
				  this.focus(index);
				  this.scroll(0, this.getFocusedItem());  
			  }else{
				  int height = this.getScrollYOffset()-20;
				  if(Math.abs(height) > this.getScrollBarMaxOffset()) height = -this.getScrollBarMaxOffset();
				  this.setScrollYOffset(height,false);
			  }
		  }
		  
	  }
  }
  public void up(){
	  if(this.isInScreenContent(this.getFocusedItem())){
		  int index = this.getFocusedIndex();
		  int closestIndex = this.getClosestHrefItemIndex(Canvas.UP,index);
		 
		  if(this.isInScreenContent(this.getItems()[closestIndex])){
			  do{
				  index--;
//				  System.out.println("UP index:"+index);
				  if(index < 0) break;
			  }while(!this.focus(index));
			  this.scroll(0, this.getFocusedItem());  
		  }else{
//			  System.out.println("scrollbar start control!1");
			  int height = this.getScrollYOffset()+20;					
			  if(height > 0) height = 0;				 
			  this.setScrollYOffset(height,false);
		  }
	  }else{
		  int index = this.getLastItemInScreenIndex();
		  
		  if(index == -1){
			  int height = this.getScrollYOffset()+20;			
			  if(height > 0) height = 0; 
			  this.setScrollYOffset(height,false);
		  }else{
			  if(this.isInScreenContent(this.getItems()[index])){
				  this.focus(index);
				  this.scroll(0, this.getFocusedItem());  
			  }else{
				  int height = this.getScrollYOffset()+20;		
				  if(height > 0) height = 0;
				  this.setScrollYOffset(height,false);
			  }
		  }
		  
	  }
  }


}
