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

/**
 * 修改用户昵称
 */
public class UpdateNicknameActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private EditText et_name;
	private Button btn_save;
	private SharedPreferences sp;
	private HttpUtils httpUtils;
	private LodingDialog lodingDialog;
	private HttpHandler<String> httpHandler;
	private String userid;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_update_nickname_activity);
		iniView();
		iniData();
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		et_name.setText(sp.getString("nickname", ""));
		userid = sp.getString("userid", "");
		token = sp.getString("token", "");
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("修改昵称");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		et_name = (EditText) findViewById(R.id.nickname_et_update);
		btn_save = (Button) findViewById(R.id.save_btn_update);
		btn_save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String nickName = et_name.getText().toString();
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.save_btn_update:
				if (TextUtils.isEmpty(nickName)) {
					Toast.makeText(this, "请输入一个昵称", Toast.LENGTH_SHORT).show();
					break;
				}
				if (NetworkUtil.hasInternetConnected(this)) {
					getDataFromServer(nickName);
				} else {
					Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
							.show();
				}
				break;
		}
	}

	private void getDataFromServer(final String nickName) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("method", "nickname");
		params.addBodyParameter("value", nickName);
		params.addBodyParameter("userid", userid);
		httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.UPDATE_MESSAGE,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(UpdateNicknameActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(UpdateNicknameActivity.this,
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
										Toast.makeText(UpdateNicknameActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									default:
										SharedPreferences.Editor editor = sp.edit();
										editor.putString("nickname", nickName);
										editor.commit();
										Toast.makeText(UpdateNicknameActivity.this,
												"修改成功", Toast.LENGTH_SHORT).show();
										UpdateNicknameActivity.this.finish();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(UpdateNicknameActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}
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
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}
	}
}
