/**
 * Jesse Business Group
 * M3U8Protocol.java, 2020年6月13日 下午4:30:01
 */
package org.jqss.m3u8p.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jqss.m3u8p.exception.MUException;
import org.jqss.m3u8p.exception.MUExceptionType;
import org.jqss.m3u8p.utils.MULogger;

/**
 * @author jesse
 *
 */
public class MUProtocol {
	private String lastProperty;
	private String lastValue;
	private Properties properties = new Properties();
	private LinkedList<String> tsfile = new LinkedList<String>();
	private LinkedList<String> tsindex = new LinkedList<String>();

	/**
	 * @return the tsfile
	 */
	public LinkedList<String> getTsfile() {
		return tsfile;
	}


	/**
	 * @return the tsindex
	 */
	public LinkedList<String> getTsindex() {
		return tsindex;
	}


	public String parse(InputStream is) throws MUException {
		BufferedReader br;
		
		br=new BufferedReader(new InputStreamReader(is));
		
		try {
			String flag = br.readLine();
			if(!isM3U8(flag)) {
				throw new MUException(MUExceptionType.INV_FILE);
			};		
			
			String ld = br.readLine();
			
			while(ld!=null) {
				MULogger.TRACE("read:"+ld);
				MULogger.TRACE("last:"+getLastProperty());
				if(ld.startsWith("#")) {
					parseLine(ld);
				} else if(getLastProperty().equals("EXT-X-ENDLIST")) {
					break;					
				} else if(getLastProperty().equals("EXT-X-STREAM-INF")) {
					return ld;					
				} else if(getLastProperty().equals("EXTINF")) {
					tsindex.addLast(getLastValue());
					tsfile.addLast(ld);
					MULogger.TRACE("add file:"+ld+", toal "+tsfile.size());
				} else {
					throw new MUException(MUExceptionType.INV_LINE, ld);
				}
				
				
				ld = br.readLine();
			} 
			
		} catch (IOException e) {
			throw new MUException(e);
		} 
		
		return null;
	}
	

	public String getLastValue() {
		return lastValue;
	}

	public String getLastProperty() {
		if(lastProperty!=null) return lastProperty;
		else return "";
	}
	
	public void setProperty(String key, String val) {
		lastProperty = key;
		lastValue = val;
		properties.setProperty(key, val);
	} 
	
	public String getProperty(String key) {
		return properties.getProperty(key, null);
	}
	
	protected void parseLine(String l) {
		String c, v;
		
		int i = l.indexOf(":");
		if(i<=0) {
			c=l.substring(1);
			v="";
		} else {
			c = l.substring(1, i);
			v = l.substring(i+1);
		}
		setProperty(c, v);		
		MULogger.TRACE("c="+c+",v="+v);
	}
	
	protected boolean isM3U8(String flag) {
		return flag.equals("#EXTM3U");
	}
}
