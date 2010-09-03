package cn.hunthawk.j2me.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import cn.hunthawk.j2me.reader.local.LocalBook;

public class tempData {
	public static long startTime = 0L;
	public static LocalBook currentLocalBook = null;
	public static Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public static int dot = 1;
	public static int recent = -1;
	
	public static int screenWidth = -1;
	public static int screenHeight = -1;
	public static int menuHeight = 0;
	public static boolean getScreenSize = true;
	public static float rate = 0f;
	public static Image percent = null;
	public static Image percentBg = null;
	public static  String getNowTime(){
		TimeZone tz = TimeZone.getTimeZone("GMT+08:00");
    	Calendar ca = Calendar.getInstance(tz);
    	String year = String.valueOf(ca.get(Calendar.YEAR));
    	String month = String.valueOf(ca.get(Calendar.MONTH)+1);
    	String day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
    	String hour = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
    	String minute = String.valueOf(ca.get(Calendar.MINUTE));
    	String seconds = String.valueOf(ca.get(Calendar.SECOND));
    	if(ca.get(Calendar.HOUR_OF_DAY) < 10) hour = "0"+hour;
    	if(ca.get(Calendar.MINUTE) < 10) minute = "0"+minute;
    	if(ca.get(Calendar.SECOND) < 10) seconds = "0"+seconds;
    	String date = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds;
    	
    	
    	return date;
	}
	public static void setScreen(int width,int height){
		//
		screenWidth = width;
		screenHeight = height;
		try {
			percent = Image.createImage("/procenter1.png");
			percentBg = Image.createImage("/probg.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void setMenuHight(int menu){
		menuHeight = menu;
	}
	public static void DrawGauge(Graphics g,int value,String info){
		if(getScreenSize){
			rate = (screenWidth - 100)/(100.0f);
			
		}
		float values = value * rate -6;
		value = (int)values;
		for(int i=0;i<screenWidth-100;i++){
			g.drawImage(percentBg, 50+i, screenHeight-menuHeight+4, 20);
		}
		for(int i=0;i<value;i++){
			g.drawImage(percent, 50+i, screenHeight-menuHeight+4, 20);
		}
		g.setColor(0xFFFFFF);
		g.setFont(font);
		if(dot == 1){
			g.drawString(info+".", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else if(dot == 2){
			g.drawString(info+"..", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else if(dot == 3){
			g.drawString(info+"...", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else if(dot == 4){
			g.drawString(info+"....", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else if(dot == 5){
			g.drawString(info+".....", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else if(dot == 6){
			g.drawString(info+"......", 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot++;
		}else{
			g.drawString(info, 70, screenHeight-menuHeight+3,Graphics.TOP | Graphics.LEFT);
			dot = 1;
		}
		
	}
	public static String getTime(){
	//+8时区代表北京时间为准
	TimeZone tz = TimeZone.getTimeZone("GMT+08:00");
	Calendar ca = Calendar.getInstance(tz);
	String  strHour=String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
	String  strMinite=String.valueOf(ca.get(Calendar.MINUTE));
	String  strTime = null;
	if(ca.get(Calendar.MINUTE)<10){
		strTime = strHour+":0"+strMinite;
	}else
		strTime = strHour+":"+strMinite;
	
	return strTime;
}
	public static boolean isChangeSize = true;
	public static boolean isChangeColor = true;
	public static int fontSize = 8;
	public static int fontColor = 0xA89B83;
	public static void setFontChange(boolean change){
		isChangeSize = change;
	}
	public static void setColorChange(boolean change){
		isChangeColor = change;
	}
	public static int getFontColor(){
		if(isChangeColor){
//			System.out.println("check seeting->");
			Vector v = (Vector)RmsOpt.readRMS("settings");
			if(!v.isEmpty()){
				int settings[] = (int[])v.elementAt(0);
				switch(settings[6]){
				case 0:
					fontColor = 0xA89B83;
					break;
				case 1:
					fontColor = 0xFF0000;
					break;
				case 2:
					fontColor = 0xFFC000;
					break;
				case 3:
					fontColor = 0xFFFF00;
					break;
				case 4:
					fontColor = 0x92D050;
					break;
				case 5:
					fontColor = 0x00B050;
					break;
				case 6:
					fontColor = 0x00B0F0;
					break;
				case 7:
					fontColor = 0x000000;
					break;
				case 8:
					fontColor = 0xffffff;
					break;
				
				}
			}
			isChangeColor = false;
		}
		
		return fontColor;
	}
	
	public static int getFontSize(){
		if(isChangeSize){
//			System.out.println("check seeting->");
			Vector v = (Vector)RmsOpt.readRMS("settings");
			if(!v.isEmpty()){
				int settings[] = (int[])v.elementAt(0);
				switch(settings[1]){
				case 0:
					fontSize = 8;
					break;
				case 1:
					fontSize = 0;
					break;
				case 2:
					fontSize = 16;
					break;
				}
			}
			isChangeSize = false;
		}
		
		return fontSize;
	}
	
	
	
}
