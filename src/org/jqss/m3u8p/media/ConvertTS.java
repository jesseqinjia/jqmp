/**
 * Jesse Business Group
 * ProcessVT.java, 2020年6月20日 下午5:06:15
 */
package org.jqss.m3u8p.media;

/**
 * @author jesse
 *
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jqss.m3u8p.utils.MULogger;

public class ConvertTS {
	private String inputPath = "";
	private String outputPath = "";
	private Process videoProcess;
	private boolean waitFor=true;
	private boolean complete=true;
	private boolean deleteSrc=false;
	public static String ffmpegPath = "./ffmpeg/bin/";

	public static void main(String args[]) throws IOException {
		
		String in = "C:\\Users\\jesse\\eclipse-workspace\\jqmp\\videos/FILES01.ts";
		String out = "C:\\Users\\jesse\\eclipse-workspace\\jqmp\\videos/FILES01.mp4";

		ConvertTS ctts = new ConvertTS(in, out);
		if (ctts.process()) {
			MULogger.TRACE("convert mp4 is ok");
		}
	}

	public boolean isDeleteSrc() {
		return deleteSrc;
	}

	public void setDeleteSrc(boolean deleteSrc) {
		this.deleteSrc = deleteSrc;
	}

	public ConvertTS(String src, String des) {
		MULogger.TRACE("ConvertTS("+src+","+des+")");
		this.inputPath = src;
		this.outputPath=des;
	}

	public boolean process() {
		if (!checkfile(inputPath)) {
			MULogger.INFO(inputPath + " is not file");
			return false;
		}

		int type = checkContentType();
		boolean status = false;
		status = processMp4(inputPath);// 直接转成mp4格式
		return status;
	}

	private int checkContentType() {
		String type = inputPath.substring(inputPath.lastIndexOf(".") + 1, inputPath.length()).toLowerCase();
		// ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
		if (type.equals("avi")) {
			return 0;
		} else if (type.equals("mpg")) {
			return 0;
		} else if (type.equals("wmv")) {
			return 0;
		} else if (type.equals("3gp")) {
			return 0;
		} else if (type.equals("mov")) {
			return 0;
		} else if (type.equals("mp4")) {
			return 0;
		} else if (type.equals("asf")) {
			return 0;
		} else if (type.equals("asx")) {
			return 0;
		} else if (type.equals("flv")) {
			return 0;
		}
		// 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
		// 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
		else if (type.equals("wmv9")) {
			return 1;
		} else if (type.equals("rm")) {
			return 1;
		} else if (type.equals("rmvb")) {
			return 1;
		}
		return 9;
	}

	private boolean checkfile(String path) {
		File file = new File(path);
		if (!file.isFile()) {
			return false;
		}
		return true;
	}

	// 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
	private String processAVI(int type) {
		List<String> commend = new ArrayList<String>();
		commend.add(ffmpegPath + "mencoder");
		commend.add(inputPath);
		commend.add("-oac");
		commend.add("lavc");
		commend.add("-lavcopts");
		commend.add("acodec=mp3:abitrate=64");
		commend.add("-ovc");
		commend.add("xvid");
		commend.add("-xvidencopts");
		commend.add("bitrate=600");
		commend.add("-of");
		commend.add("mp4");
		commend.add("-o");
		commend.add(outputPath + "a.AVI");
		try {
			ProcessBuilder builder = new ProcessBuilder();
			Process process = builder.command(commend).redirectErrorStream(true).start();
			new PrintStream(process.getInputStream());
			new PrintStream(process.getErrorStream());
			process.waitFor();
			return outputPath + "a.AVI";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	private boolean processFlv(String oldfilepath) {

		if (!checkfile(inputPath)) {
			System.out.println(oldfilepath + " is not file");
			return false;
		}
		List<String> command = new ArrayList<String>();
		command.add(ffmpegPath + "ffmpeg");
		command.add("-i");
		command.add(oldfilepath);
		command.add("-ab");
		command.add("56");
		command.add("-ar");
		command.add("22050");
		command.add("-qscale");
		command.add("8");
		command.add("-r");
		command.add("15");
		command.add("-s");
		command.add("600x500");
		command.add(outputPath + "a.flv");
		try {

			// 方案1
//            Process videoProcess = Runtime.getRuntime().exec(ffmpegPath + "ffmpeg -i " + oldfilepath 
//                    + " -ab 56 -ar 22050 -qscale 8 -r 15 -s 600x500 "
//                    + outputPath + "a.flv");

			// 方案2
			videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();

			new PrintStream(videoProcess.getErrorStream()).start();

			new PrintStream(videoProcess.getInputStream()).start();

			if(isWaitFor()) {
				setComplete(false);
				videoProcess.waitFor();
				setComplete(true);
			}
			else {
				setComplete(false);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	private boolean processMp4(String oldfilepath) {

		if (!checkfile(inputPath)) {
			System.out.println(oldfilepath + " is not file");
			return false;
		}
		List<String> command = new ArrayList<String>();
		command.add(ffmpegPath + "ffmpeg");
		command.add("-i");
		command.add(oldfilepath);
		command.add("-c:v");
		command.add("libx264");
		command.add("-mbd");
		command.add("0");
		command.add("-c:a");
		command.add("aac");
		command.add("-strict");
		command.add("-2");
		command.add("-pix_fmt");
		command.add("yuv420p");
		command.add("-movflags");
		command.add("faststart");
		command.add(outputPath);
		try {

			// 方案1
			// Process videoProcess = Runtime.getRuntime().exec(ffmpegPath + "ffmpeg -i " +
			// oldfilepath
			// + " -ab 56 -ar 22050 -qscale 8 -r 15 -s 600x500 "
			// + outputPath + "a.flv");

			// 方案2
			setComplete(false);
			
			videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();

//			new PrintStream(videoProcess.getErrorStream()).start();

//			new PrintStream(videoProcess.getInputStream()).start();
			
			if(isWaitFor()) {
				videoProcess.waitFor();
				setComplete(true);
			}
			else {
				new Thread() {
					public void run() {
						synchronized(videoProcess)  {
							try {
								videoProcess.waitFor();
							} catch (InterruptedException e) {
							}
							if(isDeleteSrc()) {
								new File(oldfilepath).delete();
							}
							setComplete(true);
							videoProcess.notifyAll();
						}
					}
				}.start();
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isWaitFor() {
		return waitFor;
	}

	public void setWaitFor(boolean waitFor) {
		this.waitFor = waitFor;
	}

	public Process getVideoProcess() {
		return videoProcess;
	}
}

class PrintStream extends Thread {
	java.io.InputStream __is = null;

	public PrintStream(java.io.InputStream is) {
		__is = is;
	}

	public void run() {
		try {
			while (this != null) {
				int _ch = __is.read();
				if (_ch != -1)
					System.out.print((char) _ch);
				else
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
