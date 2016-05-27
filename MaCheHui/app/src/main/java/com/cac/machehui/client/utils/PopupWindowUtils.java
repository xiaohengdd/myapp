package com.cac.machehui.client.utils;

import com.cac.machehui.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

public class PopupWindowUtils {

	private Context context;

	private int screenWidth;

	private int screenHeigh;

	private PopupWindow popupWindow;

	View view;
	View downview;

	public PopupWindowUtils(Context context, int screenWidth, int screenHeigh,
							View view, View downview) {
		super();
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeigh = screenHeigh;
		this.view = view;
		this.downview = downview;
		init();
	}

	public void init() {

		popupWindow = new PopupWindow(view, screenWidth, screenHeigh);
		// view.setBackgroundResource(R.drawable.popupwindow);
		/*** 这2句很重要 ***/
		ColorDrawable cd = new ColorDrawable(-0000);
		popupWindow.setBackgroundDrawable(cd);

		// popupWindow.showAsDropDown(down);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);// 设置外部能点击
		popupWindow.showAtLocation(downview, Gravity.BOTTOM, 0, 0);
		popupWindow.setAnimationStyle(R.style.popupAnimation);
		popupWindow.update();

		// setting popupWindow d点击消失
		popupWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/**** 如果点击了popupwindow的外部，popupwindow也会消失 ****/
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

	}

}
