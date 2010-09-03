package cn.hunthawk.j2me.util;

import de.enough.polish.io.Serializable;

public class UebRecordNode implements Serializable{
	
	private int recordID;
	private int start;
	private int end;
	
	public int getRecordID() {
		return recordID;
	}
	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
//	public byte[] serialize(){
//		byte[] ret = null;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(baos);
//        try {
//            dos.writeInt(recordID);
//            dos.writeInt(start);
//            dos.writeInt(end);
//
//            ret = baos.toByteArray();
//            dos.close();
//            baos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret;
//	}
//	
//	 public void deserialize(byte[] data) {
//         ByteArrayInputStream bais = new ByteArrayInputStream(data);
//         DataInputStream dis = new DataInputStream(bais);
//         try {
//        	 recordID = dis.readInt();
//        	 start = dis.readInt();
//             end = dis.readInt();
//
//             dis.close();
//             bais.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
}
