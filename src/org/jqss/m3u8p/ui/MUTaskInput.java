/**
 * Jesse Business Group
 * MUTaskInput.java, 2020��6��14�� ����4:41:03
 */
package org.jqss.m3u8p.ui;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.jqss.m3u8p.exception.MUException;
import org.jqss.m3u8p.io.MUFile;
import org.jqss.m3u8p.io.MUFileList;
import org.jqss.m3u8p.protocol.MUIndex;
import org.jqss.m3u8p.protocol.MUProtocol;
import org.jqss.m3u8p.utils.MULogger;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

/**
 * @author jesse
 *
 */
public class MUTaskInput extends JFrame {
	public static String DTITLE = "M3U8";
	public static String G1TEXT = "Video File Name";
	public static String G2TEXT = "M3U8 Index URL";
	public static String G3TEXT = "Load M3U8";
	public static String G4TEXT = "From M3U8 Index List File";
	public static String G5TEXT = "JQSS Homepage";
	public static String G6TEXT = "shudown computer";
	public static String G7TEXT = "Start Number:";
	public static String VIDEO_PATH = "./";
	public static String HOME_PAGE = "http://jesse.qinjia.date";

	JTextField jfilename = new JTextField();
	JTextField jurl = new JTextField();
	JButton jb1 = new JButton(G3TEXT);
	JButton jb2 = new JButton(G4TEXT);
	JButton jb3 = new JButton(G5TEXT);
	JCheckBox jc = new JCheckBox(G6TEXT);
	JFrame jf = this;
	private JTextField fileName;
	private JTextField splitC;
	private JTextField startNum;

	public MUTaskInput() {
		super(DTITLE);
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(MUTaskInput.class.getResource("/org/jqss/m3u8p/ui/m3u8.png")));
		getContentPane().setLayout(null);
		JLabel label = new JLabel(G1TEXT);
		label.setBounds(10, 13, 109, 21);
		getContentPane().add(label);
		jfilename.setBounds(119, 13, 314, 21);
		getContentPane().add(jfilename);
		JLabel label_1 = new JLabel(G2TEXT);
		label_1.setBounds(10, 42, 109, 21);
		getContentPane().add(label_1);
		jurl.setBounds(119, 43, 314, 20);
		getContentPane().add(jurl);
		jb1.setBounds(346, 317, 87, 21);
		getContentPane().add(jb1);
		jb2.setBounds(10, 317, 192, 21);
		getContentPane().add(jb2);
		jb3.setBounds(0, 348, 433, 28);
		getContentPane().add(jb3);
		jc.setBounds(140, 273, 131, 28);
		getContentPane().add(jc);

		JTextPane miList = new JTextPane();
		miList.setBounds(0, 98, 433, 138);
		getContentPane().add(miList);

		JLabel lblNewLabel = new JLabel("M3U8 Index List");
		lblNewLabel.setBounds(10, 73, 103, 15);
		getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Filename");
		lblNewLabel_1.setBounds(10, 251, 71, 15);
		getContentPane().add(lblNewLabel_1);

		fileName = new JTextField();
		fileName.setText("");
		fileName.setBounds(78, 246, 200, 21);
		getContentPane().add(fileName);
		fileName.setColumns(10);

		JLabel splistLabel = new JLabel("Split");
		splistLabel.setBounds(292, 252, 54, 15);
		getContentPane().add(splistLabel);

		splitC = new JTextField();
		splitC.setText("\\$");
		splitC.setBounds(346, 248, 44, 21);
		getContentPane().add(splitC);
		splitC.setColumns(10);

		JButton btnNewButton = new JButton("Frome M3U8 List");

		JComboBox<String> splitN = new JComboBox<String>();
		splitN.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4", "5", "6" }));
		splitN.setSelectedIndex(1);
		splitN.setBounds(392, 248, 34, 21);
		getContentPane().add(splitN);

		JCheckBox convertMP4 = new JCheckBox("Convert to MP4");
		convertMP4.setBounds(7, 276, 131, 23);
		getContentPane().add(convertMP4);

		startNum = new JTextField();
		startNum.setText("1");
		startNum.setBounds(356, 277, 66, 21);
		getContentPane().add(startNum);
		startNum.setColumns(10);

		// load m3u8 index list from text panel
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (miList.getText().length() == 0 || fileName.getText().length() == 0
						|| splitC.getText().length() == 0) {
					MULogger.ERROR("input error(multipie files from panel)", null);
					return;
				}

				jf.setVisible(false);

				MUFile.MUS.setShutdown(jc.isSelected());
				MUFile.MUS.setConvertMP4(convertMP4.isSelected());
				MUFile.MUS.setCompleteFrame(jf);

				int intStartNum = Integer.parseInt(startNum.getText());

				new Thread() {
					public void run() {
						MUFileList fl = new MUFileList();

						try {
							fl.runTextList(miList.getText(), fileName.getText(), splitC.getText(),
									splitN.getSelectedIndex(), VIDEO_PATH, intStartNum);
						} catch (IOException | MUException ex) {
							MULogger.ERROR(ex);
						}
					}
				}.start();
			}
		});
		btnNewButton.setBounds(213, 316, 123, 23);
		getContentPane().add(btnNewButton);

		JLabel lblNewLabel_2 = new JLabel(G7TEXT);
		lblNewLabel_2.setBounds(277, 280, 87, 15);
		getContentPane().add(lblNewLabel_2);

		// load a m3u8 index
		jb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfilename.getText().length() == 0 || jurl.getText().length() == 0) {
					MULogger.ERROR("input error (single file)", null);
					return;
				}

				jf.setVisible(false);

				MUProtocol mu = new MUProtocol();
				MUIndex mi = new MUIndex(mu);
				MUFile mf = new MUFile(mu, mi);

				MUFile.MUS.list(1, 1, jfilename.getText());
				MUFile.MUS.setShutdown(jc.isSelected());
				MUFile.MUS.setConvertMP4(convertMP4.isSelected());
				MUFile.MUS.setCompleteFrame(jf);

				new Thread() {
					public void run() {
						try {
							mi.loadIndex(jurl.getText());
							mf.tempTS();
							mf.mergeTS(VIDEO_PATH + jfilename.getText() + ".ts");
							mf.clearTemp();
						} catch (Exception e) {
							MULogger.ERROR(e);
						}

						mf.MUS.complete();
					}
				}.start();
			}
		});

		// load m3u8 index from list file
		jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jf.setVisible(false);

				JFileChooser jfc = new JFileChooser(new File("./"));
				jfc.showOpenDialog(jf);
				if (jfc.getSelectedFile() == null) {
					System.exit(-1);
				}
				String listfile = jfc.getSelectedFile().getAbsolutePath();

				MULogger.INFO("open list file " + listfile);
				MUFile.MUS.list(1, 1, listfile);
				MUFile.MUS.setShutdown(jc.isSelected());
				MUFile.MUS.setConvertMP4(convertMP4.isSelected());
				MUFile.MUS.setCompleteFrame(jf);

				new Thread() {
					public void run() {
						MUFileList fl = new MUFileList();

						try {
							fl.runList(listfile, VIDEO_PATH);
						} catch (IOException | MUException ex) {
							MULogger.ERROR(ex);
						}
					}
				}.start();
			}
		});

		jb3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = "rundll32 url.dll,FileProtocolHandler " + HOME_PAGE;
				String os = System.getProperty("os.name");
				if (os.startsWith("Windows")) {
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException ex) {
						MULogger.ERROR(ex);
					}
				} else {
					JOptionPane.showMessageDialog(jf, HOME_PAGE, G5TEXT, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		setSize(452, 425);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;

		this.setLocation(x, y);
		this.setVisible(true);
	}
}
