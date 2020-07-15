/**
 * Jesse Business Group
 * M3U8ExceptionType.java, 2020年6月14日 上午7:31:05
 */
package org.jqss.m3u8p.exception;

/**
 * @author jesse
 *
 */
public class MUExceptionType {

	public static MUExceptionType UNKNOWN = new MUExceptionType(1, "unknown error");
	public static MUExceptionType INV_TAG = new MUExceptionType(2, "invaild tag");
	public static MUExceptionType INV_LINE = new MUExceptionType(3, "invaild line");
	public static MUExceptionType INV_FILE = new MUExceptionType(4, "not m3u8 index file");
	public static MUExceptionType INV_LIST = new MUExceptionType(5, "invaild m3u8 list");

	protected int exCode;
	protected String exMsg;

	public MUExceptionType(int c, String m) {
		exCode = c;
		exMsg = m;
	}

	/**
	 * @return the exCode
	 */
	public int getExCode() {
		return exCode;
	}

	/**
	 * @return the exMsg
	 */
	public String getExMsg() {
		return exMsg;
	}

	@Override
	public String toString() {
//		return exMsg + "(" + exCode + ")";
		return exMsg;
	}
}
