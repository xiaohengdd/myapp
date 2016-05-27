package com.cac.machehui.client.receiver;

import android.app.Notification;
import android.content.Context;

import com.cac.machehui.R;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class MessageReceiver extends XGPushBaseReceiver {

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {

	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
										   XGPushClickedResult arg1) {

	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {

	}

	@Override
	public void onRegisterResult(Context arg0, int arg1,
								 XGPushRegisterResult arg2) {

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {

	}

	// 接受信息
	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage arg1) {
		if (arg0 == null || arg1 == null) {
			return;
		}
		// 创建一个通知
		Notification mNotification = new Notification();

		mNotification.ledARGB = 0xff00ff00;
		mNotification.ledOnMS = 300;
		mNotification.ledOffMS = 1000;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;

		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置属性值
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = arg1.getContent();
		mNotification.when = System.currentTimeMillis(); // 立即发生此通知
		// 添加声音效果
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.notify();
	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {

	}

}
