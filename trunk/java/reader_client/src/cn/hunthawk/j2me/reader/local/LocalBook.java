package cn.hunthawk.j2me.reader.local;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import cn.hunthawk.j2me.bo.LocalBookContents;
import cn.hunthawk.j2me.bo.LocalBookControler;
import cn.hunthawk.j2me.bo.LocalBookHeaderInfo;
import cn.hunthawk.j2me.bo.LocalBookIndex;
import cn.hunthawk.j2me.bo.LocalBookInfo;
import cn.hunthawk.j2me.gzip.GZIP;
import cn.hunthawk.j2me.util.RmsOpt;
import cn.hunthawk.j2me.util.UebRecordNode;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.base64.Base64;
import de.enough.polish.xml.SimplePullParser;

public class LocalBook {
	private byte [] temp = null;
	private int index = 0;
	
	public static final String FILE_CONTENT_OPF = "content.opf";
	public static final String FILE_CONTENT_NCX = "content.ncx";
	
	
	
	private LocalBookControler lbControler = null;
	private LocalBookInfo bookInfo = null;
	private boolean isBase64 = true;
	byte []gzipData = null;
	public LocalBook(String bookUrl) {
		Vector v = (Vector)RmsOpt.readRMS("localbookinfo"+bookUrl);
		if(!v.isEmpty()){
			System.out.println("nod");
			bookInfo = (LocalBookInfo)v.elementAt(0);
			isBase64Decoder();
		}else{
			System.out.println("nod1");
			Runtime.getRuntime().gc();
			bookInfo = new LocalBookInfo();
			bookInfo.setUebName(bookUrl);
			gzipData = getByteFromRMS(bookUrl,0,50000);//预取20k头信息
			//#debug
			System.out.println("gzipData:"+gzipData.length);
			handleHeaderInfo();//处理头信息块（暂时为提高效率只获取PackageID）
			handleFileIndex();//处理ueb文件索引区段
			byte [] contentncx = searchFileBytes(FILE_CONTENT_NCX);
			handleContentNcx(contentncx);//处理ueb图书的目录索引区段
			byte [] contentopf = searchFileBytes(FILE_CONTENT_OPF);
			handleContentOpf(contentopf);
			Vector vBook = new Vector();
			vBook.addElement(bookInfo);
			isBase64Decoder();
			RmsOpt.saveRMS(vBook, "localbookinfo"+bookUrl);
			Runtime.getRuntime().gc();
		}
		
	}
	public void isBase64Decoder(){
		Hashtable vIndex = bookInfo.getFileIndex();
		LocalBookIndex lbi = (LocalBookIndex)vIndex.get("encryption.xml");
		System.out.println("ibi:"+lbi);
		if(lbi != null) isBase64 = true;
		else isBase64 = false;
	}
	public boolean getIsBase64(){
		return this.isBase64;
	}
	public LocalBookInfo getBookInfo(){
		return this.bookInfo;
	}
	/**从ueb文件中取得数据*/
	public byte[] searchFileBytes(String fileName){
		
		Hashtable vIndex = bookInfo.getFileIndex();
		byte[] fileBytes = null;
		LocalBookIndex lbi = (LocalBookIndex)vIndex.get(fileName);
		fileBytes = new byte[(int)lbi.getFileSize()];
		fileBytes = getByteFromRMS(bookInfo.getUebName(),(int)lbi.getStartPos(),(int)lbi.getFileSize());
		
		try {
			fileBytes = GZIP.inflate(fileBytes);

			if(isBase64 && fileName.indexOf(".html") != -1){
				if(fileName.indexOf("cover.html") != -1){
					
				}else{
					
					String utf8 = new String(fileBytes);
					
					fileBytes = Base64.decode(utf8.getBytes("utf-8"),0,fileBytes.length,Base64.NO_OPTIONS);
					
//					System.out.println("base64:"+new String(fileBytes,"UTF-8"));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Runtime.getRuntime().gc();
		return fileBytes;
	}
	/**从ueb中取指定字节数*/
	public byte[] getByteFromRMS(String fileDir,int off,int length){
		//#debug
		System.out.println("off:"+off+"  length:"+length);
		byte[]data = null;
		data = new byte[length];
		int end = off + length;
		int datasize = 0;
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(fileDir, false);
			if(rs.getNumRecords()!=0){
				byte id[] = rs.getRecord(1);
				DataInputStream in = new DataInputStream( new ByteArrayInputStream( id ));
				
				Vector v = (Vector)Serializer.deserialize( in );
				int size = v.size();
				for(int i=0;i<size;i++){
					UebRecordNode node = (UebRecordNode)v.elementAt(i);
					if(off >= node.getStart() && off <= node.getEnd()){
						if(end <= node.getEnd()){
							byte record[] = rs.getRecord(node.getRecordID());
							System.arraycopy(record, off - node.getStart(), data, datasize, end - off);
							datasize += node.getEnd() - off;
							break;
						}else{
							byte record[] = rs.getRecord(node.getRecordID());
							System.arraycopy(record, off - node.getStart(), data, datasize, node.getEnd() - off);
							datasize += node.getEnd() - off;
							off  = node.getEnd();
							continue;
						}
					}
				}
			
			}
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Runtime.getRuntime().gc();
		return data;
	}

	/**处理ueb头信息*/
	public void handleHeaderInfo(){
		LocalBookHeaderInfo lbhi = new LocalBookHeaderInfo();
		//为提高解析UEB的效率问题，暂将不用的信息不予存储
//		temp = new byte[16];
//		System.arraycopy(gzipData, index, temp, 0, 16);
//		lbhi.setFileType(new String(temp).trim());
//		System.out.println("fileType:"+new String(temp));
//		index +=16;
//		
//		temp = new byte[4];
//		System.arraycopy(gzipData, index, temp, 0, 4);
//		lbhi.setVersion(byteToInt(temp));
//		System.out.println("version:"+byteToInt(temp));
//		index +=4;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setContentID(byteToLong(temp));
//		System.out.println("content ID:"+byteToLong(temp));
//		index +=8;
//		
//		temp = new byte[4];
//		System.arraycopy(gzipData, index, temp, 0, 4);
//		lbhi.setShowType(byteToInt(temp));
//		System.out.println("Typesetting:"+byteToInt(temp));
//		index +=4;
//		
//		temp = new byte[4];
//		System.arraycopy(gzipData, index, temp, 0, 4);
//		lbhi.setFeeType(byteToInt(temp));
//		System.out.println("fee type:"+byteToInt(temp));
//		index +=4;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setChannelID(byteToLong(temp));
//		System.out.println("channel ID:"+byteToLong(temp));
//		index +=8;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setCpid(byteToLong(temp));
//		System.out.println("CPID:"+byteToLong(temp));
//		index +=8;
//		
		index +=52;
		temp = new byte[8];
		System.arraycopy(gzipData, index, temp, 0, 8);
		lbhi.setPackageID(byteToLong(temp));
		//#debug
		System.out.println("bookID:"+byteToLong(temp));
		index +=42;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setModifyDate(new String(temp));
//		System.out.println("Modified time:"+new String(temp));
//		index +=8;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setCreatDate(new String(temp));
//		System.out.println("Creat time:"+new String(temp));
//		index +=8;
//		
//		temp = new byte[8];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setFileSize(byteToLong(temp));
//		System.out.println("File size:"+byteToLong(temp));
//		index +=8;
//		
//		temp = new byte[10];
//		System.arraycopy(gzipData, index, temp, 0, 8);
//		lbhi.setReserveFiled(new String(temp));
//		System.out.println("Reserved field:"+new String(temp));
//		index +=10;
		
		bookInfo.setHeader(lbhi);
	}
	/**处理ueb文件索引区段*/
	public void handleFileIndex(){
		int fileID = 0;
		int fileNum;
//		Vector vIndex = new Vector();
		LocalBookIndex lbi = new LocalBookIndex();
		Hashtable hash = new Hashtable();
		String name = null;
		while(true){
			fileID++;
			lbi = new LocalBookIndex();
			
			temp = new byte[4];
			System.arraycopy(gzipData, index, temp, 0, 4);
			fileNum = byteToInt(temp);
			if(fileNum != fileID){
				break;
			}
			lbi.setFileID(byteToInt(temp));
			System.out.println("file id:"+byteToInt(temp));
			index +=4;
		
			temp = new byte[35];
			System.arraycopy(gzipData, index, temp, 0, 35);
			name = new String(temp).trim();
			lbi.setFileName(name);

			System.out.println("file name:"+new String(temp));
			index +=35;
		
			temp = new byte[8];
			System.arraycopy(gzipData, index, temp, 0, 8);
			lbi.setStartPos(byteToLong(temp));
			System.out.println("start position:"+byteToLong(temp));
			index +=8;
		
			temp = new byte[8];
			System.arraycopy(gzipData, index, temp, 0, 8);
			lbi.setFileSize(byteToLong(temp));
			System.out.println("file size:"+byteToLong(temp));
			index +=8;
			
			hash.put(name, lbi);
//			vIndex.addElement(lbi);
		}
		bookInfo.setFileIndex(hash);
		
	}
	/**byte To int*/
	public int byteToInt(byte[] b) {

        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i=0;i<4;i++){
           n<<=8;
           temp=b[i]&mask;
           n|=temp;
      	}
        return n;
	}
	/**byte To long*/
	public long byteToLong(byte[] bb) { 
        return ((((long) bb[0] & 0xff) << 56) 
                | (((long) bb[1] & 0xff) << 48) 
                | (((long) bb[2] & 0xff) << 40) 
                | (((long) bb[3] & 0xff) << 32) 
                | (((long) bb[4] & 0xff) << 24) 
                | (((long) bb[5] & 0xff) << 16) 
                | (((long) bb[6] & 0xff) << 8) 
                | (((long) bb[7] & 0xff) << 0)); 
    } 
	/**处理ueb图书目录区段*/
	public void handleContentNcx(byte[] data){
		
		LocalBookContents lbContents = new LocalBookContents();
		Vector vContents = new Vector();
		InputStream is = new ByteArrayInputStream(data);
		InputStreamReader reader = null;
		try{
			reader = new InputStreamReader(is,"UTF-8");
			XmlPullParser parser = new XmlPullParser(reader, false);
			parser.relaxed = true;
//			System.out.println("content.ncx start tag-------------------------");
			while(parser.next()!= SimplePullParser.END_DOCUMENT){
				parser.next();
				if(parser.getType() == SimplePullParser.START_TAG){
					String name = parser.getName();
					if(name.equals("head")){
//						System.out.println("head:");
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("meta")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										for(int i=0;i<parser.getAttributeCount();i++){
//											System.out.println("meta: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
										}
//										System.out.println("meta over");
										parser.next();
										
									}
								}
							}
						}
					}
					else if(name.equals("docTitle")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("docAuthor")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("docType")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("docSort")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("docSecondsort")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("keyword")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("text")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(name+" :"+subName+": "+parser.getText());
										}
									}
								}
							}
						}
					}
					else if(name.equals("navMap")){
						
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
//								System.out.println("subName:"+subName);
								if(subName.equals("navPoint")){
//									System.out.println("init vitem and lbContents");
									lbContents = new LocalBookContents();
									
									lbContents.setChapterPlayOrder(parser.getAttributeValue("playOrder"));
									//#debug
									System.out.println("lbContents value play :"+lbContents.getChapterPlayOrder());
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.START_TAG){
											String nextSubName = parser.getName();
											if(nextSubName.equals("navLabel")){
												while(!(parser.getType() == SimplePullParser.END_TAG && parser
														.getName().equals(nextSubName))){
													parser.next();
													if(parser.getType() == SimplePullParser.START_TAG){
														String next2SubName = parser.getName();
														if(next2SubName.equals("text")){
															while(!(parser.getType() == SimplePullParser.END_TAG && parser
																	.getName().equals(next2SubName))){
																parser.next();
																if(parser.getType() == SimplePullParser.TEXT){
//																	System.out.println(name+": "+subName+": "+nextSubName+": "
//																			+next2SubName+": "+parser.getText());
																	lbContents.setChapterName(parser.getText());
																}
															}
														}
													}
												}
											}
											else if(nextSubName.equals("content")){
//												for(int i=0;i<parser.getAttributeCount();i++){
//													System.out.println("content: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
//												}
//												System.out.println("src:"+parser.getAttributeValue("src"));
												lbContents.setChapterSrc(parser.getAttributeValue("src"));
												
												
												vContents.addElement(lbContents);
//												System.out.println("add vContents");
												parser.next();
												
											}
										}
									}
								}							
							}
						}
					}
				}
			}
			bookInfo.setContents(vContents);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**解析content.opf文件*/
	public void handleContentOpf(byte[] data){
		lbControler = new LocalBookControler();
		InputStream is = new ByteArrayInputStream(data);
		InputStreamReader reader = null;
		try {
			reader= new InputStreamReader(is,"UTF-8");
			XmlPullParser parser = new XmlPullParser(reader, false);
			parser.relaxed = true;
//			System.out.println("content.opf start tag");
			while(parser.next()!= SimplePullParser.END_DOCUMENT){
				parser.next();
//				System.out.println("tag:"+parser.getName()+" type:"+parser.getType());
				if(parser.getType() == SimplePullParser.START_TAG){
					String name = parser.getName();
					if(name.equals("metadata")){
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))) {
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("dc:title")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcTitle(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:bookid")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcBookid(parser.getText().trim());
										}
											
									}
								}
								else if(subName.equals("dc:author")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcAuthor(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:creator")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcCreator(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:type")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcType(parser.getText().trim());
										}
											
									}
								}
								else if(subName.equals("dc:sort")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcSort(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:secondsort")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcSencondSort(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:keyword")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcKeyWord(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:description")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcDescription(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:longdescription")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcLongDescription(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:publisher")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcPublisher(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:publisherdate")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcPublisherDate(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:isbn")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcISBN(parser.getText().trim());
										}
											
									}
								}
								else if(subName.equals("dc:finishflag")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcFinishFlag(parser.getText().trim());
										}
											
									}
								}
								else if(subName.equals("dc:language")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcLanguage(parser.getText());
										}
											
									}
								}
								else if(subName.equals("dc:chargetype")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcChargeType(parser.getText().trim());
										}
											
									}
								}
								else if(subName.equals("dc:price")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										parser.next();
										if(parser.getType() == SimplePullParser.TEXT){
//											System.out.println(subName+" :"+parser.getText());
											lbControler.setDcPrice(parser.getText());
										}
											
									}
								}
								else if(subName.equals("meta")){
									
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										for(int i=0;i<parser.getAttributeCount();i++){
//											System.out.println("meta: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
										}
										parser.next();
										
									}
								}
							}
						}
					}
					else if(name.equals("manifest1")){//暂不解析，如解析改为“manifest”
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("item")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										for(int i=0;i<parser.getAttributeCount();i++){
											System.out.println("item: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
											if(parser.getAttributeValue("id").equals("ncx")){
												lbControler.setItemNcx(parser.getAttributeValue("href"));
											}
											else if(parser.getAttributeValue("id").equals("css")){
												lbControler.setItemCss(parser.getAttributeValue("href"));
											}
											else if(parser.getAttributeValue("id").equals("cover")){
												lbControler.setItemCover(parser.getAttributeValue("href"));
											}
											else if(parser.getAttributeValue("id").equals("cover-image")){
												lbControler.setCoverImage(parser.getAttributeValue("href"));
											}
										}
										System.out.println("item over");
										parser.next();
										
									}
								}
							}
						}
					}
					else if(name.equals("spine1")){//暂不解析，如解析改为“spine”
						for(int i=0;i<parser.getAttributeCount();i++){
							System.out.println("spin: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
						}
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("itemref")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										for(int i=0;i<parser.getAttributeCount();i++){
											System.out.println("itemref: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
										}
										System.out.println("itemref over");
										parser.next();
									}
								}
							}
						}
					}
					else if(name.equals("guide1")){//暂不解析，如解析改为“guide”
						while(!(parser.getType() == SimplePullParser.END_TAG && parser
								.getName().equals(name))){
							parser.next();
							if(parser.getType() == SimplePullParser.START_TAG){
								String subName = parser.getName();
								if(subName.equals("reference")){
									while(!(parser.getType() == SimplePullParser.END_TAG && parser
											.getName().equals(subName))){
										for(int i=0;i<parser.getAttributeCount();i++){
											System.out.println("reference: "+parser.getAttributeName(i)+": "+parser.getAttributeValue(i));
										}
										System.out.println("reference over");
										parser.next();
										
									}
								}
							}
						}
					}
				}
			}
			System.out.println("end content.opf");
//			Vector vector = new Vector();
//			vector.addElement(lbControler);
//			RmsOpt.saveRMS(vector, "localbookcontroler");
			bookInfo.setBookOpfInfo(lbControler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
