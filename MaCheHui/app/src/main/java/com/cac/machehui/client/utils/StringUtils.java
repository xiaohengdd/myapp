package com.cac.machehui.client.utils;

/**
 * String的工具类
 *
 * @author wkj
 *
 */
public class StringUtils {
	/**
	 * 判断信息是否为空
	 *
	 * @param strs
	 * @return
	 */
	public static boolean isEmpty(String... strs) {
		int count = strs.length;
		for (int i = 0; i < count; i++) {

			if (strs[i] == null || strs[i].isEmpty())

				return true;

		}
		return false;
	}


}
