package com.cac.machehui.client.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

public class ChangeUIUtil {
	/**
	 * 字体加粗
	 *
	 * @param view
	 * @param fontSize
	 * @param start
	 * @param end
	 * @param color
	 */
	public static void setTextViewSpan(TextView view, int fontSize, int start,
									   int end, int color) {
		Spannable span = new SpannableString(view.getText());
		span.setSpan(new AbsoluteSizeSpan(fontSize), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(span);
	}

	/**
	 * 设置字体大小，自适应
	 *
	 * @param context
	 * @param textSize
	 * @return
	 */
	public static int getFontSize(Context context, int textSize) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		// screenWidth = screenWidth > screenHeight ? screenWidth :
		// screenHeight;
		int rate = (int) (textSize * (float) screenHeight / 1280);
		return rate;
	}
}
