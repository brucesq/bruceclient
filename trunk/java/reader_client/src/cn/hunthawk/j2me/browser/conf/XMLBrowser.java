package cn.hunthawk.j2me.browser.conf;



import cn.hunthawk.j2me.html.Browser;
import cn.hunthawk.j2me.html.ProtocolHandler;
import de.enough.polish.ui.Style;


public class XMLBrowser extends Browser
{

  private XMLTagHandler xmlTagHandler;

  public XMLBrowser()
  {
	this( (Style) null );
  }
  public XMLBrowser( Style style )
  {
	  this( new XMLTagHandler(), getDefaultProtocolHandlers(), style );
  }
  
  public XMLBrowser( XMLTagHandler tagHandler, ProtocolHandler[] protocolHandlers )
  {
	 this( tagHandler, protocolHandlers, (Style) null );
  }
  
  public XMLBrowser( XMLTagHandler tagHandler, ProtocolHandler[] protocolHandlers,  Style style )
  {
	  super( protocolHandlers, style );
	  tagHandler.register(this);
	  this.xmlTagHandler = tagHandler;
  }

  protected void handleText(String text){}
}
