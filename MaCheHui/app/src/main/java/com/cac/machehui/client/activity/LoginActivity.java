package com.cac.machehui.client.activity;

import net.sourceforge.simcpux.Constants;

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
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.utils.PasswordEncoder;
import com.cac.machehui.client.utils.XGpushNotice;
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
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private EditText ed_number;
	private EditText ed_password;
	private Button button1;
	private Button bt_register;
	private SharedPreferences sp;
	private String number;
	private String password;
	private Button btn_forget;// 忘记密码
	private TextView tv_weixin;// 微信第三方登录
	/***** 第三方app与微信通信的openapi接口 *******/
	private IWXAPI api;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_activity_login);
		initview();
	}

	private void initview() {
		ed_number = (EditText) findViewById(R.id.ed_username);
		ed_password = (EditText) findViewById(R.id.ed_password);
		bt_register = (Button) findViewById(R.id.tv_register);
		bt_register.setOnClickListener(this);

		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);

		btn_forget = (Button) findViewById(R.id.btn_forget_login);
		btn_forget.setOnClickListener(this);

		tv_weixin = (TextView) findViewById(R.id.weixin_login_tv);
		tv_weixin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.button1:
				number = ed_number.getText().toString();
				password = ed_password.getText().toString();
				if (NetworkUtil.hasInternetConnected(this)) {
					Login(number, password);
				} else {
					Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case R.id.tv_register:
				intent = new Intent(this, RegisterActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_forget_login:
				intent = new Intent(this, ForgetPwdActivity.class);
				startActivity(intent);
				break;
			case R.id.weixin_login_tv:
				AppClient.getInstance().setWeixinFrom(0);
				regToWx();
				loginInWx();
				break;
		}
	}

	/**
	 * 启动微信登录
	 */
	private void loginInWx() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		if (NetworkUtil.hasInternetConnected(this)) {
			// send oauth request
			final SendAuth.Req req = new SendAuth.Req();
			req.scope = MaCheHuiConstants.WX_SCOPE;
			req.state = MaCheHuiConstants.WX_STATE;
			api.sendReq(req);
		}
	}

	/**
	 * 将app注册到微信
	 */
	private void regToWx() {
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
		// 将应用的AppId注册到微信
		api.registerApp(Constants.APP_ID);
	}

	private void Login(String number, final String password) {
		if (TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
			Toast.makeText(LoginActivity.this, "请输入账号密码", Toast.LENGTH_SHORT)
					.show();
		} else {
			if (password.length() < 6 || password.length() > 20) {
				Toast.makeText(LoginActivity.this, "密码长度应在6-20位之间",
						Toast.LENGTH_SHORT).show();
			} else {
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.configResponseTextCharset("UTF-8");
				RequestParams params = new RequestParams();
				params.addBodyParameter("username", number);
				String md5Pwd = PasswordEncoder.encode(password);
				params.addBodyParameter("password", md5Pwd);
				lodingDialog = LodingDialog.createDialog(this);
				httpHandler = httpUtils.send(HttpMethod.POST, URLCst.LOGIN,
						params, new RequestCallBack<String>() {
							@Override
							public void onStart() {
								lodingDialog.show();
							}

							@Override
							public void onFailure(HttpException arg0,
												  String arg1) {
								lodingDialog.dismiss();
								Toast.makeText(LoginActivity.this,
										"请求服务器失败，请重试", Toast.LENGTH_SHORT)
										.show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								lodingDialog.dismiss();
								try {
									JSONObject jsonObject = new JSONObject(
											arg0.result);
									String typeCode = jsonObject
											.getString("typecode");
									switch (typeCode) {
										case "1":
											Gson gson = new Gson();
											TypeToken<User> type = new TypeToken<User>() {
											};
											User user = gson.fromJson(jsonObject
													.getJSONObject("user")
													.toString(), type.getType());
											if (user != null) {
												XGpushNotice.useridPush(
														getApplicationContext(),
														user.getUserid());
												sp = LoginActivity.this
														.getSharedPreferences(
																"currentUser",
																Context.MODE_PRIVATE);
												// 登录后存储信息 数据要存储完全，个人资料里面要用
												SharedPreferences.Editor editor = sp
														.edit();

												editor.putString("userid",
														user.getUserid());
												editor.putString("usernames", ""
														+ user.getUsername());
												editor.putString("headUrl",
														user.getUserheadurl());
												editor.putString("password",
														password);
												editor.putString("nickname",
														user.getNickname());
												editor.putString("vipIdentify",
														user.getVip());
												editor.putString("sex",
														user.getUsersex());
												editor.putString("bundWx",
														user.getWeixintype());
												editor.putString("userId",
														user.getUserid());
												editor.putString("token",
														user.getToken());
												editor.commit();

												InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

												if (imm != null) {

													imm.hideSoftInputFromWindow(
															getWindow()
																	.getDecorView()
																	.getWindowToken(),
															0);
												}
												LoginActivity.this.finish();
											}
											break;
										case "2":
											Toast.makeText(LoginActivity.this,
													"密码错误", Toast.LENGTH_SHORT)
													.show();
											break;
										default:
											Toast.makeText(LoginActivity.this,
													jsonObject.getString("msg"),
													Toast.LENGTH_SHORT).show();
											break;
									}
								} catch (JSONException e) {
									e.printStackTrace();
									Toast.makeText(LoginActivity.this, "解析异常",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
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
