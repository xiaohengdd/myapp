package com.cac.machehui.client.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * 短信接收者
 *
 * @author shiran
 *
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static MessageListener mMessageListener;

	public static final String SMS_RECEIVER_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	public SMSBroadcastReceiver() {

		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(SMS_RECEIVER_ACTION)) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");

			for (Object pdu : pdus) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);

				String sender = message.getDisplayOriginatingAddress();

				String content = message.getDisplayMessageBody();

				long date = message.getTimestampMillis();

				Date timeDate = new Date(date);

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

				String time = sdf.format(timeDate);

				if ("".equals(sender)) {
					mMessageListener.onReceived(content);

					abortBroadcast();
				}
			}

		}

	}

	/**
	 * 回调接口
	 *
	 * @author shiran
	 *
	 */
	public interface MessageListener {

		public void onReceived(String message);
	}

	public void setOnReceivedMessageListener(MessageListener messageListener) {
		this.mMessageListener = messageListener;
	}

}
