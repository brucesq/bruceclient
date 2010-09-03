package cn.hunthawk.j2me.html;

public class CustomImage
{
	final int index;
	final String url;
	public CustomImage(int idx,String RequestUrl)
	{
		this.index = idx;
		this.url = RequestUrl;
	}
	public int getIndex()
	{
		return this.index;
	}
	public String getUrl()
	{
		return this.url;
	}
}