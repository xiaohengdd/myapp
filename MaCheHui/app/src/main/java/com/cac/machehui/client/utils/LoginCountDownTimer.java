package com.cac.machehui.client.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.cac.machehui.R;

public class LoginCountDownTimer extends CountDownTimer {

	public static final int TIME_COUNT =121000;//时间防止从119s开始显示（以倒计时120s为例子）
	private TextView btn;
	private int endStrRid;
	private int normalColor, timingColor;

	public LoginCountDownTimer (long millisInFuture, long countDownInterval, TextView btn, int endStrRid) {
		super(millisInFuture, countDownInterval);
		this.btn = btn;
		this.endStrRid = endStrRid;
	}

	public  LoginCountDownTimer (TextView btn, int endStrRid) {
		super(TIME_COUNT, 1000);
		this.btn = btn;
		this.endStrRid = endStrRid;
	}

	public LoginCountDownTimer (TextView btn) {
		super(TIME_COUNT, 1000);
		this.btn = btn;
		this.endStrRid = R.string.stop_yanzhengma;
	}


	public LoginCountDownTimer (TextView tv_varify, int normalColor, int timingColor) {
		this(tv_varify);
		this.normalColor = normalColor;
		this.timingColor = timingColor;
	}


	@Override
	public void onTick(long millisUntilFinished) {
		if(timingColor > 0){
			btn.setTextColor(timingColor);
		}
		btn.setEnabled(false);
		btn.setText(millisUntilFinished / 1000 + "s");

	}

	@Override
	public void onFinish() {
		if(normalColor > 0){
			btn.setTextColor(normalColor);
		}
		btn.setText(endStrRid);
		btn.setEnabled(true);

	}

}
