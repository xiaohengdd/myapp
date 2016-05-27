package com.cac.machehui.client.utils;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *
 * 网络的帮助类
 *
 * @author 史明松
 */
public class NetworkUtil {

	private static String LOG_TAG = "NetWorkHelper";
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 0;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 0;
	/** 3G */
	public static final int NETWORKTYPE_3G = 1;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 2;
	/** 4G网络 */
	public static final int NETWORKTYPE_4G = 3;
	private static int mNetWorkType;

	public static int getNetType(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				mNetWorkType = getMobileType(context);
			}
		} else {
			mNetWorkType = -1;
		}

		return mNetWorkType;
	}

	private static int getMobileType(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephonyManager.getNetworkType();
		if (type == 1 || type == 2 || type == 4) {
			return NETWORKTYPE_2G;
		} else if (type == 13) {
			return NETWORKTYPE_4G;
		} else {
			return NETWORKTYPE_3G;
		}
	}

	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_1xRTT: // 7
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA: // 4
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE: // 2
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0: // 5
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A: // 6
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS: // 1
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA: // 8
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA: // 10
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA: // 9
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS: // 3
				return true; // ~ 400-7000 kbps
			case TelephonyManager.NETWORK_TYPE_EHRPD: // 14
				return true; // ~ 1-2 Mbps
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // 12
				return true; // ~ 5 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPAP: // 15
				return true; // ~ 10-20 Mbps
			case TelephonyManager.NETWORK_TYPE_IDEN: // 11
				return false; // ~25 kbps
			case TelephonyManager.NETWORK_TYPE_LTE: // 13
				return true; // ~ 10+ Mbps
			case TelephonyManager.NETWORK_TYPE_UNKNOWN: // 0
				return false;
			default:
				return false;
		}
	}

	/**
	 *
	 * 判断是否有网络连接
	 *
	 * @param context
	 * @return
	 * @author 史明松
	 * @update 2014-5-25 下午4:12:12
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {
			Log.e(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable()) {
						Log.d(LOG_TAG, "network is available");
						return true;
					}
				}
			}
		}
		return false;
	}

	/***
	 *
	 * 获取当前网络类型
	 *
	 * @param context
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 * @author 史明松
	 * @update 2014-5-25 下午4:07:45
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/***
	 *
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 *
	 * @param input
	 * @return
	 * @author 史明松
	 * @update 2014-5-25 下午4:07:32
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/***
	 *
	 * 检查当前网络的状态
	 *
	 * @param context
	 * @return
	 * @author 史明松
	 * @update 2014-5-25 下午4:07:10
	 */
	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	/***
	 *
	 * 判断网络是否为漫游
	 *
	 * @param context
	 * @return
	 * @author 史明松
	 * @update 2014-5-25 下午4:06:53
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.isNetworkRoaming()) {
					Log.d(LOG_TAG, "network is roaming");
					return true;
				} else {
					Log.d(LOG_TAG, "network is not roaming");
				}
			} else {
				Log.d(LOG_TAG, "not using mobile network");
			}
		}
		return false;
	}

	/***
	 *
	 * 判断MOBILE网络是否可用
	 *
	 * @param context
	 * @return
	 * @throws Exception
	 * @author 史明松
	 * @update 2014-5-25 下午4:06:43
	 */
	public static boolean isMobileDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isMobileDataEnable = false;

		isMobileDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		return isMobileDataEnable;
	}

	/***
	 *
	 * 判断wifi 是否可用
	 *
	 * @param context
	 * @return
	 * @throws Exception
	 * @author 史明松
	 * @update 2014-5-25 下午4:06:27
	 */
	public static boolean isWifiDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiDataEnable = false;
		isWifiDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		return isWifiDataEnable;
	}

	/***
	 *
	 * 这个方法作用：是否有网可用
	 *
	 * @param context
	 * @return
	 * @author 史明松
	 * @update 2014-7-7 上午10:55:23
	 */
	public static boolean hasInternetConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}
}