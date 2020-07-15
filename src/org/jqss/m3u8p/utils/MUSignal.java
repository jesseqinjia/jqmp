/**
 * Jesse Business Group
 * MUSignal.java, 2020年6月14日 上午11:11:16
 */
package org.jqss.m3u8p.utils;

import javax.swing.JFrame;

/**
 * @author jesse
 *
 */
public interface MUSignal {
	public void setCompleteFrame(JFrame cf);
	public void ts(int c, int a, String curline);
	public void list(int c, int a, String curline);
	public void loaded(String filename);
	public void complete();
	public void setShutdown(boolean shutdown);
	public boolean isShutdown();
	public void setConvertMP4(boolean convert);
	public boolean isConvertMP4();
}
