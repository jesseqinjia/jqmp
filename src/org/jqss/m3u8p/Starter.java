/**
 * Jesse Business Group
 * Starter.java
 */
package org.jqss.m3u8p;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jqss.m3u8p.exception.MUException;
import org.jqss.m3u8p.io.MUFile;
import org.jqss.m3u8p.io.MUFileList;
import org.jqss.m3u8p.ui.MUTaskInput;
import org.jqss.m3u8p.ui.MUTaskPanel;
import org.jqss.m3u8p.utils.MULogger;
import org.jqss.m3u8p.utils.MULoggerLevel;

/**
 * @author jesse
 *
 */
public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Properties p = new Properties();
		try {
			p.load(Starter.class.getClassLoader().getResourceAsStream("org/jqss/m3u8p/m3u8p.properties"));
		} catch (IOException e) {
			MULogger.ERROR(e);
		}
		
		String feel=p.getProperty("lookfeel");
		try {
			UIManager.setLookAndFeel(feel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		if(p.getProperty("log.file")!=null) {
			File logfile = new File(p.getProperty("log.file"));
			try {
				PrintStream ps = new PrintStream(new FileOutputStream(logfile));
				System.setOut(ps);
				System.setErr(ps);
			} catch (FileNotFoundException e) {
				MULogger.ERROR(e);
			}
		}
		
		
		File tempF = new File("temp");
		File videoF = new File("videos");
		
		if(!tempF.exists()) tempF.mkdir();
		if(!videoF.exists()) videoF.mkdir();
		
		
		MUFile.TEMP_DIR = tempF.getAbsolutePath()+"/";
		MUFile.MAX_THREAD = 10;	
		MULogger.MESSAGE_LEVEL = MULoggerLevel.TRACE;
		MUTaskPanel.DTITLE = "JQSS m3u8 (v"+p.getProperty("ver")+", by jesse)";
		MUTaskInput.DTITLE = MUTaskPanel.DTITLE;
		MUTaskInput.VIDEO_PATH = videoF.getAbsolutePath()+"/";
		MUTaskInput.HOME_PAGE = p.getProperty("homepage");
		
		MUTaskPanel mtp = new MUTaskPanel();
		MUTaskInput mti = new MUTaskInput();
		MUFile.MUS = mtp;
		MUFileList.MUS = mtp;
		MULogger.PARENT_FRAME = mtp;
		
		Image image=Toolkit.getDefaultToolkit().getImage(MUTaskPanel.class.getClassLoader().getResource("org/jqss/m3u8p/ui/m3u8.png"));
		mtp.setIconImage(image);
		mti.setIconImage(image);
		
		MULogger.INFO("m3u8 started");

		mti.init();
	}

}
