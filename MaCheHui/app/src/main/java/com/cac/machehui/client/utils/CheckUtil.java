package com.cac.machehui.client.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.cac.machehui.client.entity.GoodBean;

public class CheckUtil {
	private Context context;

	public CheckUtil(Context context) {
		this.context = context;
	}

	public boolean checkEmpty(String number, String verifyCode) {
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
			return true;
		} else if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	public boolean checkEmptyThree(String number, String verifyCode, String pwd) {
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
			return true;
		} else if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
			return true;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	public boolean isEnough(GoodBean goodBean, String scoreNum) {
		int scoreAll = Integer.parseInt(scoreNum);
		int goodScore = Integer.parseInt(goodBean.score);
		if (scoreAll >= goodScore) {
			return true;
		} else {
			Toast.makeText(context, "积分不足", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	public boolean isEnough(String goodScores, String scoreNum) {
		int scoreAll = Integer.parseInt(scoreNum);
		int goodScore = Integer.parseInt(goodScores);
		if (scoreAll >= goodScore) {
			return true;
		} else {
			Toast.makeText(context, "积分不足", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
