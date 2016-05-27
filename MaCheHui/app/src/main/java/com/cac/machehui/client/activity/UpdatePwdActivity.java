package com.cac.machehui.client.activity;

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
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.utils.PasswordEncoder;
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

public class UpdatePwdActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private EditText et_old_pwd;
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	private Button btn_confirm;
	private String oldPwd;
	private String newPwd;
	private String confirmPwd;
	private String userid;
	private String token;
	private HttpUtils httpUtils;
	private SharedPreferences sp;
	private LodingDialog lodingDialog;
	private HttpHandler<String> httpHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_update_pwd_activity);
		iniView();
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);

	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("修改密码");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		et_confirm_pwd = (EditText) findViewById(R.id.confirm_pwd_et_update);
		et_old_pwd = (EditText) findViewById(R.id.old_pwd_et_update);
		et_new_pwd = (EditText) findViewById(R.id.new_pwd_et_update);
		btn_confirm = (Button) findViewById(R.id.confirm_btn_update_pwd);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		oldPwd = et_old_pwd.getText().toString();
		newPwd = et_new_pwd.getText().toString();
		confirmPwd = et_confirm_pwd.getText().toString();
		userid = sp.getString("userid", "");
		token = sp.getString("token", "");

		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.confirm_btn_update_pwd:
				if (checkEmpty()) {
					break;
				}
				if (!confirmPwd.equals(newPwd)) {
					Toast.makeText(this, "两次输入密码不一致，请确认后重新输入", Toast.LENGTH_SHORT)
							.show();
					break;
				}
				if (confirmPwd.length() < 6 || confirmPwd.length() > 20) {
					CustomToast.showToast(UpdatePwdActivity.this, "密码应在6-20位之间",
							Toast.LENGTH_SHORT);
					break;
				}
				if (NetworkUtil.hasInternetConnected(this)) {
					getDataFromServer();
				} else {
					Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
							.show();
				}
				break;
		}
	}

	private void getDataFromServer() {
		RequestParams params = new RequestParams();
		String md5OldPwd = PasswordEncoder.encode(oldPwd);
		String md5NewPwd = PasswordEncoder.encode(newPwd);
		params.addBodyParameter("token", token);
		params.addBodyParameter("password", md5OldPwd);
		params.addBodyParameter("newpassword", md5NewPwd);
		params.addBodyParameter("userid", userid);
		httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.UPDATE_PWD,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(UpdatePwdActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(UpdatePwdActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean scoreBean = gson.fromJson(result,
										type.getType());
								switch (scoreBean.typecode) {
									case "-1":
										Toast.makeText(UpdatePwdActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "0":
										Toast.makeText(UpdatePwdActivity.this,
												"旧密码错误，修改失败", Toast.LENGTH_SHORT)
												.show();
										break;
									case "1":
										SharedPreferences.Editor editor = sp.edit();
										editor.putString("password", newPwd);
										editor.commit();
										Toast.makeText(UpdatePwdActivity.this,
												"修改成功", Toast.LENGTH_SHORT).show();
										UpdatePwdActivity.this.finish();
										break;

								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(UpdatePwdActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	private boolean checkEmpty() {
		if (TextUtils.isEmpty(oldPwd)) {
			Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
			return true;
		} else if (TextUtils.isEmpty(newPwd)) {
			Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
			return true;
		} else if (TextUtils.isEmpty(confirmPwd)) {
			Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;

		}

	}
}
