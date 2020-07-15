/**
 * Jesse Business Group
 * MUFile.java, 2020年6月14日 上午9:21:52
 */
package org.jqss.m3u8p.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import org.jqss.m3u8p.protocol.MUIndex;
import org.jqss.m3u8p.protocol.MUProtocol;
import org.jqss.m3u8p.utils.MULogger;
import org.jqss.m3u8p.utils.MUSignal;

/**
 * @author jesse
 *
 */
public class MUFile {
	public static String TEMP_DIR = "C:\\Users\\jesse\\eclipse-workspace\\jqmp\\temp\\";
	public static int MAX_THREAD = 10;
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

	MUProtocol mu;
	MUIndex mi;
	long fid;
	String videofile;
	int running = 0;
	Object syo = new Object();
	int t, ta;

	public MUFile(MUProtocol mu, MUIndex mi) {
		this.mu = mu;
		this.mi = mi;
		this.fid = System.currentTimeMillis();
	}
	
	protected String buildTsName(String fn) {
		String rn;
		if(fn.indexOf("/")<0) {
			rn = fn;
		} else {
			rn = fn.replaceAll("/", "_");
		}
		
		return TEMP_DIR + fid + "_" + rn;
	}

	protected void aTS(String fn, InputStream is) throws IOException {
		FileOutputStream fos = new FileOutputStream(fn);
		fos.write(is.readAllBytes());
		fos.close();
	}

	public void tempTS() throws MalformedURLException, IOException {
		String fn;

		t=0;
		ta=mu.getTsfile().size();
		
		for (String ts : mu.getTsfile()) {
			fn = buildTsName(ts);
			MULogger.TRACE("read " + fn);
			
			if (MAX_THREAD > 1) {
				MULogger.TRACE("Thread mode " + MAX_THREAD);
				synchronized (syo) {
					MULogger.TRACE("MT " + running);
					while (running >= MAX_THREAD)
						try {
							syo.wait();
						} catch (InterruptedException e1) {
							MULogger.ERROR(e1);
						}
					MULogger.TRACE("MT " + running + " got");
					running++;
					new Thread() {
						String fn;

						public void start(String fn) {
							this.fn = fn;
							this.start();
						}

						public void run() {
							InputStream is;
							try {
								is = mi.loadTS(ts);
								aTS(fn, is);
								is.close();
							} catch (IOException e) {
								MULogger.ERROR(e);
							}
							synchronized (syo) {
								MUS.ts(++t, ta, ts);
								MULogger.TRACE("RT " + running + " - 1");
								running--;
								syo.notify();
							}
						}
					}.start(fn);
				}
			} else {
				InputStream is = mi.loadTS(ts);
				aTS(fn, is);
				is.close();
				MUS.ts(++t, ta, ts);
			}

		}

		synchronized (syo) {
			while (running > 0) {
				try {
					syo.wait();
				} catch (InterruptedException e1) {
					MULogger.ERROR(e1);
				}
			}
		}

		MULogger.TRACE("TS files temped.");
	}

	public void mergeTS(String videofile) throws IOException {
		String fn;
		this.videofile = videofile;
		MULogger.TRACE("write video file " + videofile);
		FileOutputStream vf = new FileOutputStream(videofile);

		for (String ts : mu.getTsfile()) {
			fn = buildTsName(ts);

			FileInputStream fis = new FileInputStream(fn);
			vf.write(fis.readAllBytes());
			
			fis.close();
		}

		vf.close();
		MUS.loaded(videofile);
	}

	public void clearTemp() {
		String fn;

		for (String ts : mu.getTsfile()) {
			fn = buildTsName(ts);
			MULogger.TRACE("clear " + fn);
			new File(fn).delete();
		}
	}
}
