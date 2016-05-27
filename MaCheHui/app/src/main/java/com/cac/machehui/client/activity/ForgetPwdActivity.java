package com.cac.machehui.client.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.utils.PasswordEncoder;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 忘记密码页面
 */
public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
	private EditText et_number;
	private EditText et_verify_code;
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	private TextView tv_get_verify_code;
	private Button btn_submit;
	private ImageButton iv_return;
	private String number;
	private String verifyCode;
	private String newPwd;
	private String confirmPwd;
	private HttpUtils httpUtils;
	private Timer timer;
	private MyTimerTask myTimerTask = null;
	private long starttime;
	private HttpHandler<String> httpHandler;
	private HttpHandler<String> httpHandlerCode;
	private LodingDialog lodingDialog;
	private SharedPreferences sp;
	private long num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_activity_forget);
		initView();
		initData();
	}

	private void initData() {
		httpUtils = new HttpUtils();
	}

	private void initView() {
		et_number = (EditText) findViewById(R.id.number_et_forget);
		et_verify_code = (EditText) findViewById(R.id.verify_code_et_forget);
		et_new_pwd = (EditText) findViewById(R.id.new_pwd_et_forget);
		et_confirm_pwd = (EditText) findViewById(R.id.confirm_pwd_et_forget);
		initTimeButton();
		btn_submit = (Button) findViewById(R.id.submit_btn_forget);
		btn_submit.setOnClickListener(this);
		iv_return = (ImageButton) findViewById(R.id.return_iv_forget);
		iv_return.setOnClickListener(this);
	}

	private void initTimeButton() {
		tv_get_verify_code = (TextView) findViewById(R.id.rebind_sms_btn);
		tv_get_verify_code.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		number = et_number.getText().toString();
		verifyCode = et_verify_code.getText().toString();
		newPwd = et_new_pwd.getText().toString();
		confirmPwd = et_confirm_pwd.getText().toString();
		switch (v.getId()) {
			case R.id.rebind_sms_btn:
				sendSms();
				break;
			case R.id.submit_btn_forget:
				if (checkLegal()) {
					getBackPwd();
				}
				break;
			case R.id.return_iv_forget:
				finish();
				break;
		}
	}

	private void sendSms() {
		if (number == null || number.length() == 0) {
			Toast.makeText(ForgetPwdActivity.this, "请输入手机号", Toast.LENGTH_SHORT)
					.show();
		} else if (number.length() != 11) {
			Toast.makeText(ForgetPwdActivity.this, "请输入正确的手机号",
					Toast.LENGTH_SHORT).show();
		} else {
			if (NetworkUtil.hasInternetConnected(this)) {
				getDataFormService();
			} else {
				Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 请求服务器找回密码
	 */
	private void getBackPwd() {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("utf-8");
		String md5Pwd = PasswordEncoder.encode(newPwd);
		RequestParams params = new RequestParams();
		params.addBodyParameter("method", "password");
		params.addBodyParameter("username", number);
		params.addBodyParameter("value", md5Pwd);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.GET_BACK_PWD,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(ForgetPwdActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							if ("1".equals(typeCode)) {

								Gson gson = new Gson();
								TypeToken<User> type = new TypeToken<User>() {
								};
								User user = gson.fromJson(jsonObject
										.getJSONObject("user").toString(), type
										.getType());
								if (user != null) {
									sp = ForgetPwdActivity.this
											.getSharedPreferences(
													"currentUser",
													Context.MODE_PRIVATE);
									// 登录后存储信息
									SharedPreferences.Editor editor = sp.edit();

									editor.putString("userid", user.getUserid());
									editor.putString("usernames",
											"" + user.getUsername());
									editor.putString("headUrl",
											user.getUserheadurl());
									editor.putString("password", newPwd);
									editor.putString("nickname",
											user.getNickname());
									editor.putString("vipIdentify",
											user.getVip());
									editor.putString("sex", user.getUsersex());
									editor.putString("bundWx",
											user.getWeixintype());
									editor.putString("userId", user.getUserid());
									editor.putString("token", user.getToken());
									// AppClient.token = user.getToken();
									editor.commit();

									Toast.makeText(ForgetPwdActivity.this,
											"成功找回密码", Toast.LENGTH_SHORT)
											.show();
									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

									if (imm != null) {
										imm.hideSoftInputFromWindow(getWindow()
												.getDecorView()
												.getWindowToken(), 0);
									}
									Intent intent1 = new Intent();
									intent1.setClass(ForgetPwdActivity.this,
											HomeActivity.class);
									startActivity(intent1);
									ForgetPwdActivity.this.finish();
								}
							} else {
								Toast.makeText(ForgetPwdActivity.this,
										"密码找回失败", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(ForgetPwdActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}

				});

	}

	/**
	 *
	 * @return
	 */
	private boolean checkLegal() {
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(newPwd)) {
			Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(confirmPwd)) {
			Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!confirmPwd.equals(newPwd)) {
			Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!(num + "").equals(verifyCode)) {
			Toast.makeText(ForgetPwdActivity.this, "请输入正确的验证码",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 获取验证码
	 */
	private void getDataFormService() {
		// 进行网络判断，如果有网就进行网络请求
		double codenumber = Math.random() * 9000 + 1000;
		num = Math.round(codenumber);

		RequestParams params = new RequestParams();
		params.addBodyParameter("username", number);
		params.addBodyParameter("method", "changepassword");
		params.addBodyParameter("code", num + "");
		lodingDialog = LodingDialog.createDialog(this);
		httpHandlerCode = httpUtils.send(HttpMethod.POST, URLCst.VERIFY_CODE,
				params,

				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(ForgetPwdActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							if ("0".equals(typeCode)) {
								Toast.makeText(ForgetPwdActivity.this,
										"手机号未注册", Toast.LENGTH_SHORT).show();
							} else {
								// 倒计时开始
								starttime = System.currentTimeMillis() / 1000;
								timer = new Timer();
								myTimerTask = new MyTimerTask();
								timer.schedule(myTimerTask, 0, 1000);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(ForgetPwdActivity.this, "解析失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	// 定时器任务
	class MyTimerTask extends TimerTask {
		// 每隔一秒刷新按钮
		@Override
		public void run() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					long havetime = starttime + 60 - System.currentTimeMillis()
							/ 1000;
					if (havetime > 0) {
						tv_get_verify_code.setBackground(getResources()
								.getDrawable(R.drawable.person_04));
						tv_get_verify_code.setText(havetime + "秒后重新获取");
						tv_get_verify_code.setClickable(false);
					} else {
						if (timer != null) {
							timer.cancel();
						}
						if (myTimerTask != null) {
							myTimerTask.cancel();
						}
						timer = null;
						myTimerTask = null;
						tv_get_verify_code.setBackground(getResources()
								.getDrawable(R.drawable.yzm_selector));
						tv_get_verify_code.setClickable(true);
						tv_get_verify_code.setText("获取验证码");
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (myTimerTask != null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
		if (httpHandlerCode != null) {
			httpHandlerCode.cancel();
			httpHandlerCode = null;
		}
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}

	}

	// @Override
	// public boolean onTouchEvent(android.view.MotionEvent event) {
	// InputMethodManager imm = (InputMethodManager)
	// getSystemService(INPUT_METHOD_SERVICE);
	// return imm.hideSoftInputFromWindow(this.getCurrentFocus()
	// .getWindowToken(), 0);
	// }
}
