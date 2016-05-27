package com.cac.machehui.client.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.utils.CheckUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.utils.PasswordEncoder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 微信绑定手机号
 */
public class BundMobileActivity extends BaseActivity implements OnClickListener {
	private EditText et_number;
	private EditText et_verify_code;
	private TextView tv_get_verify_code;
	private Button btn_submit;
	private String number;
	private String pwd;
	private String verifyCode;
	private HttpUtils httpUtils;
	private long num;
	private EditText et_pwd;
	private SharedPreferences sp;
	private Timer timer;
	private MyTimerTask myTimerTask = null;
	private long starttime;
	private TextView tv_title;
	private ImageButton ib_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bund_mobile);
		initView();
		initData();
	}

	private void initData() {
		httpUtils = new HttpUtils();
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("绑定手机号");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setVisibility(View.GONE);
		et_number = (EditText) findViewById(R.id.number_et_forget);
		et_verify_code = (EditText) findViewById(R.id.verify_code_et_forget);
		initTimeButton();
		btn_submit = (Button) findViewById(R.id.submit_btn_forget);
		btn_submit.setOnClickListener(this);
		et_pwd = (EditText) findViewById(R.id.new_pwd_et_forget);
	}

	private void initTimeButton() {
		tv_get_verify_code = (TextView) findViewById(R.id.rebind_sms_btn);
		tv_get_verify_code.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		number = et_number.getText().toString();
		verifyCode = et_verify_code.getText().toString();
		pwd = et_pwd.getText().toString();
		switch (v.getId()) {
			case R.id.rebind_sms_btn:
				sendSms();
				break;
			case R.id.submit_btn_forget:
				CheckUtil checkUtil = new CheckUtil(BundMobileActivity.this);
				if (checkUtil.checkEmptyThree(number, verifyCode, pwd)) {
					break;
				}
				if (!NetworkUtil.hasInternetConnected(this)) {
					Toast.makeText(BundMobileActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
					break;
				}
				bundMobile();
				break;
		}
	}

	private void bundMobile() {
		if (!(num + "").equals(verifyCode)) {
			Toast.makeText(BundMobileActivity.this, "验证码不正确",
					Toast.LENGTH_SHORT).show();
		}
		String md5Pwd = PasswordEncoder.encode(pwd);
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", number);
		params.addBodyParameter("unionid", KeyValues.wx_union_id);
		params.addBodyParameter("openid", KeyValues.wx_open_id);
		params.addBodyParameter("nickname", KeyValues.userName);
		params.addBodyParameter("userheadurl", KeyValues.headimgurl);
		params.addBodyParameter("password", md5Pwd);
		httpUtils.configResponseTextCharset("UTF-8");
		httpUtils.send(HttpMethod.POST, URLCst.REGISTER_WX, params,

				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(BundMobileActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							switch (typeCode) {
								case "0":
									Toast.makeText(BundMobileActivity.this,
											"该手机号已被绑定，绑定失败", Toast.LENGTH_SHORT).show();
									break;
								case "1":
								case "2":
									Toast.makeText(BundMobileActivity.this, "绑定成功",
											Toast.LENGTH_SHORT).show();
									Gson gson = new Gson();
									TypeToken<User> type = new TypeToken<User>() {
									};
									User user = gson.fromJson(
											jsonObject.getJSONObject("user").toString(),
											type.getType());
									if (user != null) {
										sp = getSharedPreferences("currentUser",
												Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = sp.edit();

										editor.putString("id", user.getId());
										editor.putString("usernames",
												"" + user.getUsername());
										editor.putString("headUrl", user.getUserheadurl());
										editor.putString("password", pwd);
										editor.putString("nickname", user.getNickname());
										editor.putString("vipIdentify", user.getVip());
										editor.putString("sex", user.getUsersex());
										editor.putString("bundWx", user.getWeixintype());
										editor.putString("userId", user.getUserid());
										editor.putString("userid", user.getUserid());
										editor.putString("token", user.getToken());
										// AppClient.token = user.getToken();
										editor.commit();
										Intent intent = new Intent(BundMobileActivity.this,
												HomeActivity.class);
										startActivity(intent);
										BundMobileActivity.this.finish();
									}
									break;
								case "3":
									Toast.makeText(BundMobileActivity.this, "密码错误，绑定失败",
											Toast.LENGTH_SHORT).show();
									break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(BundMobileActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();
							Log.e("ll", arg0.result);
						}
					}
				});

	}

	private void sendSms() {
		if (number == null || number.length() == 0) {
			Toast.makeText(BundMobileActivity.this, "请输入手机号",
					Toast.LENGTH_SHORT).show();
		} else if (number.length() != 11) {
			Toast.makeText(BundMobileActivity.this, "请输入正确的手机号",
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
	 * 请求网络获取验证码
	 */
	private void getDataFormService() {
		double codenumber = Math.random() * 9000 + 1000;
		num = Math.round(codenumber);

		RequestParams params = new RequestParams();
		params.addBodyParameter("username", number);
		params.addBodyParameter("code", num + "");
		// Toast.makeText(BundMobileActivity.this, num + "", Toast.LENGTH_SHORT)
		// .show();
		httpUtils.send(HttpMethod.POST, URLCst.DIRECT_VERIFY_CODE, params,

				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(BundMobileActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 倒计时开始
						starttime = System.currentTimeMillis() / 1000;
						timer = new Timer();
						myTimerTask = new MyTimerTask();
						timer.schedule(myTimerTask, 0, 1000);
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

	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}
}
