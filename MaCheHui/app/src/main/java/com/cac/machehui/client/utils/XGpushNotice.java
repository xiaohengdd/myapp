package com.cac.machehui.client.utils;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

public class XGpushNotice {

	/** 对于普通未注册用户的信鸽推送注册 **/
	public static void ordinaryPush(Context context) {
		XGPushManager.registerPush(context);
		// context 一般传入this即可
	}

	/** 对于普通未注册用户的信鸽推送注册 带注册成功返回信息的方法　一般不用 **/
	public static void ordinaryPushcallback(Context context) {
		XGPushManager.registerPush(context, new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				Log.d("TPush", "注册成功，设备token为：" + data);
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
		});
	}
	/**根据用户名进行信鸽推送  可以发送到指定的改用户名**/
	public static void useridPush(Context context,String userid){
		XGPushManager.registerPush(context,userid);
	}
	/**根据用户名进行信鸽推送  可以发送到指定的改用户名 带注册成功返回信息的方法**/
	public static void useridPushcallback(Context context,String userid){
		XGPushManager.registerPush(context, userid,
				new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						Log.d("TPush", "注册成功，设备token为：" + data);
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
					}
				});
	}
	/**账号解绑     只是userid与APP账号的关联，若使用全量/标签/token推送仍然能收到通知/消息**/
	public static void deleteUseridPush(Context context){
		XGPushManager.registerPush(context,"*");
	}
	/**删除信鸽推送    用在用户不想推送或者退出   个人观点是退出时不要让他删除 **/
	public static void deleteAllPush(Context context){
		XGPushManager.unregisterPush(context);
		//反注册操作切勿过于频繁，可能会造成后台同步延时。
		//切换别名无需反注册，多次注册自动会以最后一次为准。
	}

}
