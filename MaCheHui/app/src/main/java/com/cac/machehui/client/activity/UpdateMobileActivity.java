package com.cac.machehui.client.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.utils.CheckUtil;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class UpdateMobileActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private EditText et_mobile;
	private EditText et_verify_code;
	private Button btn_save;
	private TextView tv_get_verify_code;
	private String number;
	private String verifyCode;
	private SharedPreferences sp;
	private String userId;
	private String token;
	private HttpUtils httpUtils;
	private long num;
	private int type;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private Timer timer;
	private MyTimerTask myTimerTask = null;
	private long starttime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_update_mobile_activity);
		iniView();
		iniData();
	}

	private void iniData() {
		httpUtils = new HttpUtils();
		type = 1;
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
	}

	private void iniView() {
		tv_get_verify_code = (TextView) findViewById(R.id.rebind_sms_btn_update);
		tv_get_verify_code.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("修改手机");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		et_mobile = (EditText) findViewById(R.id.mobile_et_update);
		et_verify_code = (EditText) findViewById(R.id.verify_code_et_update);
		btn_save = (Button) findViewById(R.id.save_btn_update_mobile);
		btn_save.setOnClickListener(this);
		refreshTextViewGetCode();
	}

	private void refreshTextViewGetCode() {

	}

	@Override
	public void onClick(View v) {
		number = et_mobile.getText().toString();
		verifyCode = et_verify_code.getText().toString();
		switch (v.getId()) {
			case R.id.rebind_sms_btn_update:
				sendSms();
				break;
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.save_btn_update_mobile:
				CheckUtil checkUtil = new CheckUtil(UpdateMobileActivity.this);
				if (checkUtil.checkEmpty(number, verifyCode)) {
					break;
				}
				if (!NetworkUtil.hasInternetConnected(this)) {
					Toast.makeText(UpdateMobileActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
					break;
				}
				if ("验证".equals(btn_save.getText().toString())) {
					if (!verifyCode.equals(num + "")) {
						CustomToast.showToast(UpdateMobileActivity.this, "验证码不正确",
								Toast.LENGTH_SHORT);
						break;
					}
					verifiedMobile(number, verifyCode);
				} else {
					if (!verifyCode.equals(num + "")) {
						CustomToast.showToast(UpdateMobileActivity.this, "验证码不正确",
								Toast.LENGTH_SHORT);
						break;
					}
					updateMobile(verifyCode, number);
				}
				break;
		}

	}

	private void verifiedMobile(String mobile, String code) {
		if (code.equals(et_verify_code.getText().toString())) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("token", token);
			params.addBodyParameter("userid", userId);
			params.addBodyParameter("method", "username");
			params.addBodyParameter("value", mobile);
			lodingDialog = LodingDialog.createDialog(this);
			httpUtils.configResponseTextCharset("utf-8");
			httpHandler = httpUtils.send(HttpMethod.POST,
					URLCst.VERIFY_IDENTIFY, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							lodingDialog.show();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							lodingDialog.dismiss();
							Toast.makeText(UpdateMobileActivity.this,
									"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							lodingDialog.dismiss();
							String result = arg0.result;
							if (TextUtils.isEmpty(result)) {
								Toast.makeText(UpdateMobileActivity.this,
										"返回数据异常", Toast.LENGTH_SHORT).show();
							} else {
								try {
									Gson gson = new Gson();
									TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
									};
									BaseBean scoreBean = gson.fromJson(result,
											type.getType());
									switch (scoreBean.typecode) {
										case "-1":
											Toast.makeText(
													UpdateMobileActivity.this,
													"登录失效，请重新登录",
													Toast.LENGTH_SHORT).show();
											break;
										case "1":
											refreshUI();
											break;
										default:
											Toast.makeText(
													UpdateMobileActivity.this,
													scoreBean.msg,
													Toast.LENGTH_SHORT).show();
											break;
									}
								} catch (JsonSyntaxException e) {
									Toast.makeText(UpdateMobileActivity.this,
											"解析异常", Toast.LENGTH_SHORT).show();
								}
							}

						}
					});

		} else {
			Toast.makeText(UpdateMobileActivity.this, "验证码不正确",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void refreshUI() {
		et_mobile.setText("");
		et_verify_code.setText("");
		et_mobile.setHint("请输入新手机号");
		et_verify_code.setHint("请输入验证码");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (myTimerTask != null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}
		tv_get_verify_code.setBackground(getResources().getDrawable(
				R.drawable.yzm_selector));
		tv_get_verify_code.setClickable(true);
		tv_get_verify_code.setText("获取验证码");
		btn_save.setText("确认修改");
		et_mobile.setFocusable(true);
		type = 2;
	}

	private void sendSms() {
		if (number == null || number.length() == 0) {
			Toast.makeText(UpdateMobileActivity.this, "请输入手机号",
					Toast.LENGTH_SHORT).show();
		} else if (number.length() != 11) {
			Toast.makeText(UpdateMobileActivity.this, "请输入正确的手机号",
					Toast.LENGTH_SHORT).show();
		} else {
			switch (type) {
				case MaCheHuiConstants.GET_CODE_OLD:
					getDataFormService();
					break;
				case MaCheHuiConstants.GET_CODE_NEW:
					getDataFormServiceGetCode();
					break;
			}

		}
	}

	private void getDataFormServiceGetCode() {
		if (NetworkUtil.hasInternetConnected(this)) {
			double codenumber = Math.random() * 9000 + 1000;
			num = Math.round(codenumber);
			RequestParams params = new RequestParams();
			params.addBodyParameter("username", number);
			params.addBodyParameter("code", num + "");
			params.addBodyParameter("method", "reg");
			httpUtils.send(HttpMethod.POST, URLCst.VERIFY_CODE, params,

					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(UpdateMobileActivity.this, "请求服务器失败，请重试",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							try {
								JSONObject jsonObject = new JSONObject(arg0.result);
								String typeCode = jsonObject.getString("typecode");
								if ("0".equals(typeCode)) {
									Toast.makeText(UpdateMobileActivity.this,
											"手机号已经注册", Toast.LENGTH_SHORT).show();
								} else {
									startTime();
									// Toast.makeText(UpdateMobileActivity.this, num +
									// "",
									// Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(UpdateMobileActivity.this, "解析失败",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
		} else {
			Toast.makeText(UpdateMobileActivity.this, "您的网络离家出走了，请检查重试",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 修改手机号
	 *
	 * @param code
	 * @param mobile
	 */
	private void updateMobile(String code, String mobile) {
		if (code.equals(et_verify_code.getText().toString())) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("token", token);
			params.addBodyParameter("userid", userId);
			params.addBodyParameter("method", "username");
			params.addBodyParameter("value", mobile);
			lodingDialog = LodingDialog.createDialog(this);
			httpHandler = httpUtils.send(HttpMethod.POST,
					URLCst.UPDATE_MESSAGE, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							lodingDialog.show();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							lodingDialog.dismiss();
							Toast.makeText(UpdateMobileActivity.this,
									"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							lodingDialog.dismiss();
							String result = arg0.result;
							if (TextUtils.isEmpty(result)) {
								Toast.makeText(UpdateMobileActivity.this,
										"返回数据异常", Toast.LENGTH_SHORT).show();
							} else {
								try {
									Gson gson = new Gson();
									TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
									};
									BaseBean scoreBean = gson.fromJson(result,
											type.getType());
									switch (scoreBean.typecode) {
										case "-1":
											Toast.makeText(
													UpdateMobileActivity.this,
													"登录失效，请重新登录",
													Toast.LENGTH_SHORT).show();
											break;
										case "1":
											Toast.makeText(
													UpdateMobileActivity.this,
													"手机号修改成功", Toast.LENGTH_SHORT)
													.show();
											SharedPreferences.Editor editor = sp
													.edit();
											editor.putString("usernames", number);
											editor.commit();
											UpdateMobileActivity.this.finish();
											break;
										default:
											Toast.makeText(
													UpdateMobileActivity.this,
													"手机号修改失败", Toast.LENGTH_SHORT)
													.show();
											break;
									}
								} catch (JsonSyntaxException e) {
									Toast.makeText(UpdateMobileActivity.this,
											"解析异常", Toast.LENGTH_SHORT).show();
								}
							}

						}
					});

		} else {
			Toast.makeText(UpdateMobileActivity.this, "验证码不正确",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void getDataFormService() {
		if (NetworkUtil.hasInternetConnected(this)) {
			double codenumber = Math.random() * 9000 + 1000;
			num = Math.round(codenumber);

			RequestParams params = new RequestParams();
			params.addBodyParameter("username", number);
			params.addBodyParameter("code", num + "");
			params.addBodyParameter("method", "changepassword");
			lodingDialog = LodingDialog.createDialog(this);
			httpHandler = httpUtils.send(HttpMethod.POST, URLCst.VERIFY_CODE,
					params, new RequestCallBack<String>() {
						@Override
						public void onStart() {
							lodingDialog.show();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							lodingDialog.dismiss();
							Toast.makeText(UpdateMobileActivity.this,
									"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							lodingDialog.dismiss();
							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								String typeCode = jsonObject
										.getString("typecode");
								if ("0".equals(typeCode)) {
									Toast.makeText(UpdateMobileActivity.this,
											"手机号未注册", Toast.LENGTH_SHORT)
											.show();
								} else {
									startTime();
								}
								// Toast.makeText(UpdateMobileActivity.this,
								// num + "", Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(UpdateMobileActivity.this,
										"解析失败", Toast.LENGTH_SHORT).show();
							}
						}
					});
		} else {
			Toast.makeText(UpdateMobileActivity.this, "您的网络离家出走了，请检查重试",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void startTime() {
		// 倒计时开始
		starttime = System.currentTimeMillis() / 1000;
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 0, 1000);
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
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (myTimerTask != null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}
	}
}
