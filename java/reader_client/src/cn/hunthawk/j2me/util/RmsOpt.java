package cn.hunthawk.j2me.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import de.enough.polish.io.RmsStorage;
import de.enough.polish.io.Serializer;

public class RmsOpt {
	
	/**¶ÁÈ¡RMS*/
	public static Object readRMS(String rs){
		Vector o = new Vector();
	    RmsStorage storage= new RmsStorage();
	    try{
	    	o = (Vector)storage.read(rs);
	    }catch(IOException e){
	    	
	    }
	    return o;
	}
	/**É¾³ýRMS*/
	public static boolean deleteRMS(String rs){
		
	    RmsStorage storage= new RmsStorage();
	    try{
	    	storage.delete(rs);
	    }catch(IOException e){
	    	
	    	return false;
	    }
	    return true;
	}
	/**´æ´¢RMS*/
       public static boolean saveRMS(Object o,String rs){
		
	    RmsStorage storage= new RmsStorage();
	    try{
	    	storage.save(o, rs);
	    }catch(IOException e){
	    	
	    	return false;
	    }
	    return true;
	}
     
    public static byte[] Object2Byte(Object o){
    	ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		try {
			Serializer.serialize(o, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteOut.toByteArray();
    }
}
