/**
 * Jesse Business Group
 * MUFileList.java, 2020年6月14日 上午10:48:13
 */
package org.jqss.m3u8p.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.jqss.m3u8p.exception.MUException;
import org.jqss.m3u8p.exception.MUExceptionType;
import org.jqss.m3u8p.protocol.MUIndex;
import org.jqss.m3u8p.protocol.MUProtocol;
import org.jqss.m3u8p.utils.MULogger;
import org.jqss.m3u8p.utils.MUSignal;

/**
 * @author jesse
 *
 */
public class MUFileList {
	public static MUSignal MUS = new MUSignal() {
		public void ts(int c, int a, String curline) {
			MULogger.TRACE("c:"+c+" a:"+a+" l:"+curline);
		}
		public void list(int c, int a, String curline) {
			MULogger.TRACE("c:"+c+" a:"+a+" l:"+curline);
		}
		public void complete() {
		}
		public void setShutdown(boolean shutdown) {
		}
		public boolean isShutdown() {
			return false;
		}
		public void loaded(String filename) {
		}
		public void setConvertMP4(boolean convert) {
		}
		public boolean isConvertMP4() {
			return false;
		}
		public void setCompleteFrame(JFrame cf) {
		}};
		
	public void runList(String list, String outdir) throws IOException, MUException {
		MULogger.TRACE("run list:"+list+" outdir:"+outdir); 
		
		BufferedReader in = new BufferedReader(new FileReader(list, Charset.forName("UTF-8")));

		LinkedList<String> ls = new LinkedList<String>();
		String l = in.readLine();
		while(l!=null) {
			ls.addLast(l);
			l = in.readLine();
		}
		
		runListEx(ls, outdir);
	}
	
	public void runTextList(String text, String filename, String splitC, int splitN, String outdir, int startNum) throws IOException, MUException {
		String rl;
		
		BufferedReader br = new BufferedReader(new StringReader(text));
		LinkedList<String> brls = new LinkedList<String>();
		LinkedList<String> ls = new LinkedList<String>();
		
		do {
			rl = br.readLine();
			brls.addLast(rl);
		} while(rl!=null);
		
		int it=startNum-1;

		String nft= "00";
		if((brls.size()+it)>=100) nft="000";
		
		DecimalFormat df = new DecimalFormat(nft);
		
		for(String l: brls) {
			if(l==null||l.split(splitC).length==0) break;
			
			MULogger.TRACE(l);	
			String url = l.split(splitC)[splitN];
			String file = df.format(++it);
			if(filename.indexOf("#")<0) filename = filename+"#";
			String fn = filename.replaceFirst("#", file);
			String al = fn + "," + url;
			MULogger.TRACE(al);
			ls.addLast(al);
		}
		
		runListEx(ls, outdir);
	}
	
	public void runListEx(LinkedList<String> ls, String outdir) throws IOException, MUException {
		int t=0;
		
		for(String s: ls) {
			int i = s.indexOf(",");
			
			if(i<0) throw new MUException(MUExceptionType.INV_LIST, s);
			
			String file = outdir+s.substring(0, i)+".ts";
			String url = s.substring(i+1);
			
			MUS.list(++t, ls.size(), s.substring(0, i));
			
			MULogger.TRACE("load "+file+" from "+url);
			
			MUProtocol mu = new MUProtocol();
			MUIndex mi = new MUIndex(mu);
			MUFile mf = new MUFile(mu, mi);

			mi.loadIndex(url);
			mf.tempTS();
			mf.mergeTS(file);
			mf.clearTemp();
		}
		
		MUS.complete();

	}
}
