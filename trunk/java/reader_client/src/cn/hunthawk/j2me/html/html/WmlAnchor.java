package cn.hunthawk.j2me.html.html;

import java.util.Hashtable;

public class WmlAnchor
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	private String formName;
	private String actionUrl;
	private String method;
	
	private Hashtable postfieldElememts;
	
	public WmlAnchor()
	{
		postfieldElememts = new Hashtable();
//		this.formName = name;
//		this.actionUrl = actionUrl;
//		this.method = method.toUpperCase();
	}

	public void setFormName(String formname)
	{
		this.formName = formname;
	}

	public void setActionUrl(String url)
	{
		this.actionUrl = url;
	}

	public void setMethod(String m)
	{
		this.method = m.toUpperCase();
	}

	public String getAction()
	{
		return this.actionUrl;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public boolean isGet() {
		return GET.equals(this.method);
	}
	
	public boolean isPost() {
		return POST.equals(this.method);
	}

	public String getName() {
		return this.formName;
	}
	public void addPostField(String name,String Value){
		
		postfieldElememts.put(name, Value);
	}
	public boolean isPostFieldEmpty(){
		return postfieldElememts.isEmpty();
	}
	public Hashtable getPostFieldElemets(){
		return postfieldElememts;
	}
}
