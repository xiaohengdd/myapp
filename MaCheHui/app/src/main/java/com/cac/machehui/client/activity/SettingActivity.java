package com.cac.machehui.client.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.VersionBean;
import com.cac.machehui.client.task.CheckVersion;
import com.cac.machehui.client.task.CheckVersion.CheckVersionListener;
import com.cac.machehui.client.task.CheckVersion.IntentToHome;
import com.cac.machehui.client.utils.XGpushNotice;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	private SharedPreferences sp;
	private ImageButton ib_return;
	private RelativeLayout rl_feedback;
	private RelativeLayout rl_update;
	private RelativeLayout rl_about;
	private RelativeLayout rl_share;
	private TextView tv_new_version;
	private Button button_goout;
	private String userId;
	private HttpHandler<String> httpHandler;
	private String token;
	private boolean isNew;
	private VersionBean newVersionBean;
	private String savePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = this.getSharedPreferences("currentUser", MODE_PRIVATE);
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
		initView();
		checkVersion();
	}

	private void checkVersion() {
		CheckVersion checkVersion = new CheckVersion(SettingActivity.this,
				new CheckVersionListener() {

					@Override
					public void toUpdate(VersionBean newVersionBean,
										 String savePath) {
						isNew = false;
						tv_new_version.setTextColor(getResources().getColor(
								R.color.red_yello));
						tv_new_version.setText("发现新版本");
						SettingActivity.this.newVersionBean = newVersionBean;
						SettingActivity.this.savePath = savePath;
					}
				}, new IntentToHome() {

			@Override
			public void toHome() {
				isNew = true;
			}
		});
		checkVersion.checkUpdate();
	}

	private void initView() {
		ib_return = (ImageButton) findViewById(R.id.setting_back_button);
		ib_return.setOnClickListener(this);
		rl_feedback = (RelativeLayout) findViewById(R.id.feedback_rl_setting);
		rl_feedback.setOnClickListener(this);
		rl_about = (RelativeLayout) findViewById(R.id.about_rl_setting);
		rl_about.setOnClickListener(this);
		rl_share = (RelativeLayout) findViewById(R.id.share_rl_setting);
		rl_share.setOnClickListener(this);
		rl_update = (RelativeLayout) findViewById(R.id.update_rl_setting);
		rl_update.setOnClickListener(this);
		tv_new_version = (TextView) findViewById(R.id.update_tv_setting);
		button_goout = (Button) findViewById(R.id.goout);
		button_goout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.feedback_rl_setting:// 意见反馈

				break;
			case R.id.update_rl_setting:
				if (!isNew) {
					intent = new Intent(SettingActivity.this,
							VersionUpdateActivity.class);
					intent.putExtra("bean", newVersionBean);
					intent.putExtra("type", 0);
					intent.putExtra("savePath", savePath);
					startActivity(intent);
				}
				break;
			case R.id.about_rl_setting:
				intent = new Intent();
				intent.setClass(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			case R.id.share_rl_setting:
				intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_TEXT,
						"码车惠分享!http://www.macheka.cn:8080/aichequan/appdownload.html");
				intent.setType("text/*");
				// 设置分享列表的标题，并且每次都显示分享列表
				startActivity(Intent.createChooser(intent, "分享到"));
				break;
			case R.id.setting_back_button:
				finish();
				break;
			case R.id.goout:
				showExitDailog();
				break;
		}
	}

	/***
	 * 退出对话框
	 */
	private void showExitDailog() {
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		View view = LayoutInflater.from(this).inflate(
				R.layout.delete_car_dialog, null);
		dialog.setContentView(view);
		TextView tv_content = (TextView) view
				.findViewById(R.id.content_tv_delete);
		tv_content.setText("确定退出吗？");
		TextView tv_cancle = (TextView) view
				.findViewById(R.id.cancle_tv_delete);
		tv_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		final TextView tv_confirm = (TextView) view
				.findViewById(R.id.confirm_tv_delete);
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Editor editor = sp.edit();
				editor.clear();
				editor.commit();
				cancleToken();
				XGpushNotice.useridPush(getApplicationContext(), userId);
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, HomeActivity.class);
				SettingActivity.this.startActivity(intent);
				SettingActivity.this.finish();
			}
		});
		dialog.show();
	}

	/***
	 * 调用接口销毁token
	 */
	private void cancleToken() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("token", token);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.LOGOUT, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
	}
}
