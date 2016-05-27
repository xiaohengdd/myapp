package com.cac.machehui.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtil {
	public static String CONFIG =  "config";
	private static SharedPreferences sharedPreferences;
	
	public static void saveBooleanData(Context context,String key,boolean value){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBooleanData(Context context,String key,boolean defValue){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getBoolean(key, defValue);
	}
}
