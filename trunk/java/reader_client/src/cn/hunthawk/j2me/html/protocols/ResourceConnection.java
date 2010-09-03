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
package cn.hunthawk.j2me.html.protocols;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

/**
 * A StreamConnection for resources from inside the application jar.
 * The urls of resources always start with <code>resource://</code>. 
 */
public class ResourceConnection
  implements StreamConnection
{
  private String path;
  private InputStream inputStream;

  public ResourceConnection(String url)
  {
    // Strip off 'resource:/' part of url.
    this.path = url.substring(10);
    
    // Resource paths are always absolute.
    if (this.path.charAt(0) != '/')
    {
      this.path = '/' + this.path;
    }
  }
  
  /* (non-Javadoc)
   * @see javax.microedition.io.Connection#close()
   */
  public void close() throws IOException
  {
    if (this.inputStream != null)
    {
    	try {
    		this.inputStream.close();
    	} catch (Exception e) {
    		// ignore
    	} finally{
    		this.inputStream = null;
    	}
    }
  }
  
  /* (non-Javadoc)
   * @see javax.microedition.io.InputConnection#openDataInputStream()
   */
  public DataInputStream openDataInputStream() throws IOException
  {
    return new DataInputStream(openInputStream());
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.OutputConnection#openDataOutputStream()
   */
  public DataOutputStream openDataOutputStream() throws IOException
  {
    // Resource connections don't support output streams.
    return null;
  }
  
  /* (non-Javadoc)
   * @see javax.microedition.io.InputConnection#openInputStream()
   */
  public synchronized InputStream openInputStream() throws IOException
  {
    if (this.inputStream == null)
    {
      this.inputStream = getClass().getResourceAsStream(this.path);

      if (this.inputStream == null)
      {
    	  throw new IOException("resource not found: " + this.path );
      }
    }
    
    return this.inputStream;
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.OutputConnection#openOutputStream()
   */
  public OutputStream openOutputStream() throws IOException
  {
    // Resource connections don't support output streams.
    return null;
  }
}
