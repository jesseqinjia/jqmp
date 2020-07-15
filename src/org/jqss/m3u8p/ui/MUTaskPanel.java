/**
 * Jesse Business Group
 * MUTaskPanel.java, 2020年6月14日 下午12:24:01
 */
package org.jqss.m3u8p.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jqss.m3u8p.media.ConvertTS;
import org.jqss.m3u8p.utils.MULogger;
import org.jqss.m3u8p.utils.MULoggerLevel;
import org.jqss.m3u8p.utils.MUSignal;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.TextArea;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;

/**
 * @author jesse
 *
 */
public class MUTaskPanel extends JFrame implements MUSignal {

	private static final long serialVersionUID = 1L;
	public static String DTITLE = "M3U8";
	public static String COMPLETE_MESSAGE = "load complete!";
	public static String MERGE_MESSAGE = "Merge ...";
	public static String MP4_STATUS = "CnvMP4";
	JProgressBar jpb = new JProgressBar();
	JLabel jtp = new JLabel();
	JLabel jtStatus = new JLabel("");
	TextArea logP = new TextArea();
	boolean shutdown = false;
	boolean convert = false;
	ConvertTS cts;
	JFrame completeFrame;

	public MUTaskPanel() {
		super(DTITLE);
		setResizable(false);
		getContentPane().setLayout(null);
		jtp.setBounds(0, 0, 272, 35);
		getContentPane().add(jtp);
		jpb.setBounds(0, 35, 516, 36);
		getContentPane().add(jpb);

		jtStatus.setBounds(275, 0, 272, 35);
		getContentPane().add(jtStatus);

		logP.setEditable(false);
		logP.setBounds(0, 72, 537, 222);
		getContentPane().add(logP);
		logP.setVisible(false);

		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (logP.isVisible()) {
					setSize(553, 108);
					logP.setVisible(false);
					logP.setText("");
				} else {
					logP.setVisible(true);
					setSize(553, 333);
				}
			}
		});
		btnNewButton.setBounds(515, 35, 22, 36);
		getContentPane().add(btnNewButton);

		this.setSize(553, 108);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MULogger mul = MULogger.getLogger();
		MULogger.setLogger(new MULogger() {
			public void log(MULoggerLevel t, String s, Exception e) {
				mul.log(t, s, e);
				if (s == null && e != null)
					s = e.toString();
				if (logP.isVisible())
					logP.append(t.name() + "|" + s + "\n");
			}
		});
	}

	public void init() {
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;

		this.setLocation(x, y);
		this.setVisible(true);
	}

	@Override
	public void ts(int c, int a, String curline) {
		if (!isVisible())
			init();

		jpb.setMaximum(a);
		jpb.setMinimum(0);
		jpb.setValue(c);
		jpb.setStringPainted(true);
		jpb.setString(curline + "(" + c + "/" + a + ")");

		if (c >= a) {
			jtStatus.setText(MERGE_MESSAGE);
		}
	}

	@Override
	public void list(int c, int a, String curline) {
		if (!isVisible())
			init();

		jtp.setText(curline + "(" + c + "/" + a + ")");
	}

	public void complete() {
		waitCTS();
		if (isShutdown()) {
			try {
				Runtime.getRuntime().exec("Shutdown.exe -s -t 30");
			} catch (IOException e) {
				MULogger.ERROR(e);
			}
		} else if (completeFrame != null) {
			this.setVisible(false);
			completeFrame.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, COMPLETE_MESSAGE, DTITLE, JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
	}

	@Override
	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	@Override
	public boolean isShutdown() {
		return this.shutdown;
	}

	protected void waitCTS() {
		if (cts == null)
			return;

		synchronized (cts.getVideoProcess()) {
			if (!cts.isComplete()) {
				try {
					cts.getVideoProcess().wait();
				} catch (InterruptedException e) {
				}
			}
		}
		MULogger.TRACE("waitCTS:cts.wait()");
	}

	public void loaded(String filename) {
		MULogger.TRACE("MUSigner.loadded(" + filename + ")");

		jtStatus.setText("");

		if (isConvertMP4() && filename.indexOf(".ts") > 0) {
			waitCTS();
			String out = filename.substring(0, filename.indexOf(".ts")) + ".mp4";
			cts = new ConvertTS(filename, out);
			cts.setWaitFor(false);
			cts.setDeleteSrc(true);
			cts.process();

			new Thread() {
				public void run() {
					InputStream is = cts.getVideoProcess().getInputStream();

					byte b[] = new byte[128];
					int r = 0;
					StringBuffer sb = new StringBuffer();
					do {
						try {
							r = is.read();
							if (r >= 0) {
								if (r == '\n' || r == '\r') {
									String s = sb.toString();
									String c1 = "";
									String c2 = "";
									int i1 = s.indexOf("time=");
									int i2 = s.indexOf("speed=");
									if (i1 > 0) {
										c1 = s.substring(i1 + 5, i1 + 13);
									}

									if (i2 > 0) {
										c2 = s.substring(i2 + 6);
									}

									jtStatus.setText(MP4_STATUS + " " + c1 + ":" + c2);
									sb = new StringBuffer();
								} else
									sb.append((char) r);
							}
						} catch (IOException e) {
						}
					} while (r >= 0);
				}
			}.start();
		}
	}

	@Override
	public void setConvertMP4(boolean convert) {
		this.convert = convert;
	}

	@Override
	public boolean isConvertMP4() {
		return this.convert;
	}

	@Override
	public void setCompleteFrame(JFrame cf) {
		this.completeFrame = cf;
	}
}
