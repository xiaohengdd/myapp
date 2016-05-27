package com.cac.machehui.client.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.machehui.R;

/**
 * 请在此处简要描述此类所实现的功能。因为这项注释主要是为了在IDE环境中生成tip帮助，务必简明扼要
 *
 * 请在此处详细描述类的功能、调用方法、注意事项、以及与其它类的关系.
 **/
public class LodingDialog extends Dialog {

	private Context context = null;
	private static LodingDialog customProgressDialog = null;

	private LodingDialog(Context context) {
		super(context);
		this.context = context;
	}

	private LodingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LodingDialog createDialog(Context context) {
		customProgressDialog = new LodingDialog(context, R.style.LodingDialog);
		customProgressDialog.setContentView(R.layout.dialog_loding);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		// customProgressDialog.setCancelable(true);// 设置为不可取消
		return customProgressDialog;
	}

	public static LodingDialog createUploadPicDialog(Context context) {
		customProgressDialog = new LodingDialog(context, R.style.LodingDialog);
		customProgressDialog.setContentView(R.layout.dialog_loding);
		TextView id_tv_loadingmsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);
		id_tv_loadingmsg.setText("正在上传...");
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}
		ImageView imageView = (ImageView) customProgressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	/**
	 *
	 * setMessage 提示内容
	 *
	 * @param strMessage
	 *
	 * @return
	 */
	public LodingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customProgressDialog;
	}

	@Override
	public void show() {
		super.show();
		ImageView imageView = (ImageView) customProgressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

}
