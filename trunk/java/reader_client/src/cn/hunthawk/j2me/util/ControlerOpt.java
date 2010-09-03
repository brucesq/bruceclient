package cn.hunthawk.j2me.util;

import java.util.Vector;

import cn.hunthawk.j2me.bo.HomeControler;
import cn.hunthawk.j2me.bo.InitControler;
import cn.hunthawk.j2me.bo.LocalBookControler;

public class ControlerOpt {
	
	/**获得HomeControler*/
	public static HomeControler getHomeControler(Object o){
		HomeControler hc = null;
		Vector vector = (Vector)o;
		if(vector.size()>0)
			hc = (HomeControler)vector.elementAt(vector.size()-1);
		return hc;
	}
	
	/**获得InitControler*/
	public static InitControler getInitControler(Object o){
		InitControler ic = null;
		Vector vector = (Vector)o;
		if(vector.size()>0)
			ic = (InitControler)vector.elementAt(vector.size()-1);
		return ic;
	}
	
	
	
	/**获得LocalBookControler*/
	public static LocalBookControler getLocalBookControler(Object o){
		LocalBookControler lbc = null;
		Vector vector = (Vector)o;
		if(vector.size()>0)
			lbc = (LocalBookControler)vector.elementAt(vector.size()-1);
		return lbc;
	}
}
