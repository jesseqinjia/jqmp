/**
 * Jesse Business Group
 * M3U8ProtocolException.java, 2020年6月13日 下午4:44:46
 */
package org.jqss.m3u8p.exception;

import java.io.IOException;

import org.jqss.m3u8p.utils.MULogger;

/**
 * @author jesse
 *
 */
public class MUException extends Exception {

	private static final long serialVersionUID = 1L;
	MUExceptionType type;
	String exceptionMsg;
	Exception exception;

	public MUException(MUException e) {
		type = e.getExceptionType();
		exceptionMsg = e.getExceptionMsg();
		exception = e.getBackException();
	}

	public MUException(MUExceptionType t, String msg, Exception e) {
		this.type = t;
		this.exceptionMsg = msg;
		this.exception = e;
	}

	public MUException(MUExceptionType t) {
		this(t, null, null);
	}

	public MUException(MUExceptionType t, String msg) {
		this(t, msg, null);
	}

	public MUException(String msg) {
		this(msg, null);
	}

	public MUException(String msg, Exception e) {
		this(MUExceptionType.UNKNOWN, msg, e);
	}

	public MUException(Exception e) {
		this(null, e);
	}

	@Override
	public String toString() {
//		return type.toString() + (exceptionMsg == null ? "" : ":" + exceptionMsg);
		return type.toString();
	}

	/**
	 * @return the exceptionMsg
	 */
	public String getExceptionMsg() {
		return exceptionMsg;
	}

	/**
	 * @return the exception
	 */
	public Exception getBackException() {
		return exception;
	}

	/**
	 * @return the type
	 */
	public MUExceptionType getExceptionType() {
		return type;
	}
}
