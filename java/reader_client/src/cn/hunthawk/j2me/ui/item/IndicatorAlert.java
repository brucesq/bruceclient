package cn.hunthawk.j2me.ui.item;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.StringItem;

public class IndicatorAlert extends Form{
	
	private Gauge indicator = null;
	private StringItem info = null;
	public IndicatorAlert(){
		//#style IndicatorAlert
		super(null);
		//#style progessItem3
		info = new StringItem(null,null);
		//#style loadIndicator
	    this.indicator = new Gauge(null, false, 100,1);
	    this.append(indicator);
	}
	
	public void setValue(int value){
		indicator.setValue(value);
	}
	public void setMaxValue(int maxValue){
		indicator.setMaxValue(maxValue);
	}
	public void setInfo(String text){
		info.setText(text);
	}
	
}
