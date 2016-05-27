package com.cac.machehui.client.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.receiver.SMSBroadcastReceiver;
import com.cac.machehui.client.utils.CustomToast;
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

public class RegisterActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private String mobilephone_number = "";
	private String password_num = "";
	private long num;

	private SMSBroadcastReceiver receiver;

	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVER";
	private ImageView iv_return;
	private SharedPreferences sp;
	private HttpHandler<String> httpHandlerCode;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;

	private EditText yanzhengma;
	private EditText rigest_number;
	private EditText password;
	private CheckBox iv_choose;
	/*
	 * 用户协议
	 */
	private LinearLayout ly_protocol;
	private TextView rebind_sms_btn;
	/**
	 * 注册
	 */
	private Button register_btn;
	private HttpUtils httpUtils;
	private Timer timer;
	private MyTimerTask myTimerTask = null;
	private long starttime;
	private String url;

	@Override
	protected void onStart() {
		super.onStart();
		receiver = new SMSBroadcastReceiver();

		IntentFilter intentFilter = new IntentFilter(ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);

		this.registerReceiver(receiver, intentFilter);

		receiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {

			@Override
			public void onReceived(String message) {
				yanzhengma.setText(message);

				RegisterActivity.this.unregisterReceiver(receiver);

			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_rigister);
		init();
		rebind_sms_btn = (TextView) findViewById(R.id.rebind_sms_btn);

		rebind_sms_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mobilephone_number = rigest_number.getText().toString();
				sendSms();
			}
		});

		/**
		 * 注册
		 */
		register_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(yanzhengma.getText().toString())
						|| TextUtils
						.isEmpty(rigest_number.getText().toString())
						|| TextUtils.isEmpty(password.getText().toString())) {
					Toast.makeText(RegisterActivity.this, "请将信息填写完整",
							Toast.LENGTH_SHORT).show();
				} else if (!(num + "").equals(yanzhengma.getText().toString())) {
					Toast.makeText(RegisterActivity.this, "验证码不正确",
							Toast.LENGTH_SHORT).show();
				} else {
					password_num = password.getText().toString();
					if (password_num.length() < 6 || password_num.length() > 20) {
						CustomToast.showToast(RegisterActivity.this,
								"密码应在6-20位之间", Toast.LENGTH_SHORT);
						return;
					}
					String md5Pwd = PasswordEncoder.encode(password_num);
					if (NetworkUtil.hasInternetConnected(RegisterActivity.this)) {
						toRegister(md5Pwd);
					} else {
						Toast.makeText(RegisterActivity.this,
								"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
	}

	private void toRegister(String md5Pwd) {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("utf-8");
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", mobilephone_number);// 接口改动已经修改
		params.addBodyParameter("password", md5Pwd);
		lodingDialog = LodingDialog.createDialog(this);
		httpUtils.send(HttpMethod.POST, URLCst.REGISTER, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(RegisterActivity.this, "请求服务器失败，请重试",
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
									sp = RegisterActivity.this
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
									editor.putString("password", password_num);
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

									Intent intent = new Intent();
									intent.setAction("currentusername");
									RegisterActivity.this.sendBroadcast(intent);

									Toast.makeText(RegisterActivity.this,
											"注册成功", Toast.LENGTH_SHORT).show();
									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

									if (imm != null) {
										imm.hideSoftInputFromWindow(getWindow()
												.getDecorView()
												.getWindowToken(), 0);
									}
									Intent intent1 = new Intent();
									intent1.setClass(RegisterActivity.this,
											HomeActivity.class);
									startActivity(intent1);
									RegisterActivity.this.finish();
								}
							} else {
								Toast.makeText(RegisterActivity.this, "注册失败",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(RegisterActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}

				});

	}

	// @Override
	// public boolean onTouchEvent(android.view.MotionEvent event) {
	// InputMethodManager imm = (InputMethodManager)
	// getSystemService(INPUT_METHOD_SERVICE);
	// return imm.hideSoftInputFromWindow(this.getCurrentFocus()
	// .getWindowToken(), 0);
	// }

	private void init() {
		rigest_number = (EditText) findViewById(R.id.rigest_number);

		yanzhengma = (EditText) findViewById(R.id.yanzhengma);
		password = (EditText) findViewById(R.id.password);

		iv_choose = (CheckBox) findViewById(R.id.iv_choose);
		iv_choose.setOnCheckedChangeListener(this);

		ly_protocol = (LinearLayout) findViewById(R.id.ly_protocol);
		ly_protocol.setOnClickListener(this);

		register_btn = (Button) findViewById(R.id.register_btn);
		iv_return = (ImageView) findViewById(R.id.return_iv_register);
		iv_return.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.return_iv_register:
				finish();
				break;
			case R.id.ly_protocol:// 点击进入用户协议页面
				Intent intent = new Intent(RegisterActivity.this,
						ProtocolActivity.class);
				intent.putExtra("type", "fromReg");
				startActivity(intent);
				break;
		}

	}

	private void sendSms() {
		if (mobilephone_number == null || mobilephone_number.length() == 0) {
			Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT)
					.show();
		} else if (mobilephone_number.length() != 11) {
			Toast.makeText(RegisterActivity.this, "请输入正确的手机号",
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
	 * 网络请求获取验证码
	 */
	private void getDataFormService() {
		httpUtils = new HttpUtils();
		double codenumber = Math.random() * 9000 + 1000;
		num = Math.round(codenumber);
		// Toast.makeText(RegisterActivity.this, num + "", Toast.LENGTH_SHORT)
		// .show();
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", mobilephone_number);
		params.addBodyParameter("code", num + "");
		params.addBodyParameter("method", "reg");
		lodingDialog = LodingDialog.createDialog(this);
		httpHandlerCode = httpUtils.send(HttpMethod.POST, URLCst.VERIFY_CODE,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						super.onStart();
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(RegisterActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							if ("0".equals(typeCode)) {
								Toast.makeText(RegisterActivity.this, "手机号已注册",
										Toast.LENGTH_SHORT).show();
							} else {
								// 倒计时开始
								starttime = System.currentTimeMillis() / 1000;
								timer = new Timer();
								myTimerTask = new MyTimerTask();
								timer.schedule(myTimerTask, 0, 1000);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(RegisterActivity.this, "解析失败",
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			register_btn.setClickable(true);
			register_btn.setBackground(getResources().getDrawable(
					R.drawable.login_btn_selector));
		} else {
			register_btn.setBackground(getResources().getDrawable(
					R.drawable.person_04));
			register_btn.setClickable(false);
		}
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
						rebind_sms_btn.setBackground(getResources()
								.getDrawable(R.drawable.person_04));
						rebind_sms_btn.setText(havetime + "秒后重新获取");
						rebind_sms_btn.setClickable(false);
					} else {
						if (timer != null) {
							timer.cancel();
						}
						if (myTimerTask != null) {
							myTimerTask.cancel();
						}
						timer = null;
						myTimerTask = null;
						rebind_sms_btn.setBackground(getResources()
								.getDrawable(R.drawable.yzm_selector));
						rebind_sms_btn.setClickable(true);
						rebind_sms_btn.setText("获取验证码");
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
}
