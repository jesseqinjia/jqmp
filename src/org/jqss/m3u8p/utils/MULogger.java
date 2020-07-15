/**
 * Jesse Business Group
 * Logger.java, 2020年6月13日 下午10:21:24
 */
package org.jqss.m3u8p.utils;

import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author jesse
 *
 */
public class MULogger {
	private static MULogger LOG = new MULogger();
	public static MULoggerLevel MESSAGE_LEVEL = MULoggerLevel.INFO;
	public static JFrame PARENT_FRAME;
	public static String ERROR_BOX = "Exception";
	
	public static void setLogger(MULogger log) {
		LOG = log;
	}
	
	public static MULogger getLogger() {
		return LOG;
	}

	public static void ERROR(String s, Exception e) {
		LOG.log(MULoggerLevel.ERROR, s, e);
	}

	public static void ERROR(Exception e) {
		LOG.log(MULoggerLevel.ERROR, e.toString(), e);
	}

	public static void INFO(String s) {
		LOG.log(MULoggerLevel.INFO, s, null);
	}

	public static void TRACE(String s) {
		LOG.log(MULoggerLevel.TRACE, s, null);
	}

	public static void DEBUG(String s) {
		LOG.log(MULoggerLevel.DEBUG, s, null);
	}
	
	public void log(MULoggerLevel t, String s, Exception e) {
		if (t.compareTo(MESSAGE_LEVEL)>=0) {
			System.out.println(t.name() + "|" + s);
		}
		
		if (t.compareTo(MULoggerLevel.ERROR)>=0&&PARENT_FRAME!=null) {
			if(e!=null) e.printStackTrace();
			JOptionPane.showMessageDialog(PARENT_FRAME, s, ERROR_BOX, JOptionPane.ERROR_MESSAGE);
		}
	}
}
