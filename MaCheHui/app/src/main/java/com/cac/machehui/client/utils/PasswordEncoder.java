package com.cac.machehui.client.utils;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * 密码处理工具类
 *
 * @author zhugl
 *
 */
public class PasswordEncoder {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 验证输入的密码是否正确
	 *
	 * @param password
	 *            真正的密码（加密后的真密码）
	 * @param inputString
	 *            输入的字符串
	 * @return 验证结果，boolean类型
	 */
	/*
	 * public static boolean authenticatePassword(String password, String
	 * inputString) { if(password.equals(encodeByMD5(inputString))) { return
	 * true; } else { return false; } }
	 */

	/**
	 * 对字符串进行MD5加密
	 *
	 * @param originString
	 *            原始密码
	 * @return 加密后的密码
	 */
	private static String encodeByMD5(String originString) {
		if (originString != null) {
			try {
				// 创建具有指定算法名称的信息摘要
				MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = md.digest(originString.getBytes());
				// 将得到的字节数组变成字符串返回
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase(Locale.getDefault());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 *
	 * @param b
	 *            字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 将一个字节转化成十六进制形式的字符串
	 *
	 * @param b
	 *            字节符
	 * @return 十六进制字符
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 对密码进行加密
	 *
	 * @param password
	 *            密码值
	 * @return 加密后的值
	 */
	public static String encode(String password) {

		return encodeByMD5(password);
	}

	public static void main(String args[]) {
		System.out.print(encode("111111"));
	}
	/**
	 * 返回length位随机生成字母或数字的密码
	 *
	 * @param length
	 *            密码长度
	 * @return
	 */
	/*
	 * public static String generateRandomPassword(int length) { String password
	 * = ""; for (int i = 0; i < length; i++) { password = password +
	 * randomChar(); } return password; }
	 */

	/**
	 * 随机生成一个字母或数字
	 *
	 * @return
	 */
	/*
	 * private static char randomChar() { Random r = new Random(); String s =
	 * "0123456789ABCDEFGHJKLMNPRSTUVWXYZ0123456789abcdefghjklmnprstuvwxyz0123456789"
	 * ; return s.charAt(r.nextInt(s.length())); }
	 */
}
