/**
 * Jesse Business Group
 * MUIndex.java, 2020年6月14日 上午8:15:23
 */
package org.jqss.m3u8p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jqss.m3u8p.exception.MUException;
import org.jqss.m3u8p.utils.MULogger;

/**
 * @author jesse
 *
 */
public class MUIndex {
	MUProtocol mu;
	String url;
	URL u;
	
	public MUIndex(MUProtocol mu) {
		this.mu = mu;
	}
	
	public void loadIndex(String url) throws MUException {
		MULogger.DEBUG("load url:"+url);
	
		this.url = url;
		try {
			u = new URL(url);
			String n = mu.parse(u.openStream());
			
			if(n!=null) {				
				String nu="";
				if(n.startsWith("/")) {					
					nu=u.getProtocol()+"://"+u.getHost()+n;							
				} else {
					int l = url.lastIndexOf("/");
					nu = url.substring(0, l+1) + n;
				}
				loadIndex(nu);
			}
		} catch (MalformedURLException e) {
			throw new MUException(e);
		} catch (IOException e) {
			throw new MUException(e);
		}
	}
	
	public InputStream loadTS(String ts) throws MalformedURLException, IOException {		
		String nu = "";
		
		if(ts.startsWith("/")) {					
			nu=u.getProtocol()+"://"+u.getHost()+ts;							
		} else {
			int l = url.lastIndexOf("/");
			nu = url.substring(0, l+1) + ts;
		}
		
		MULogger.TRACE("load ts:"+u);
		
		return new URL(nu).openStream();
	}
}
