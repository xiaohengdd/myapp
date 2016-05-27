package com.cac.machehui.client.activity;

import android.content.Context;
import android.content.Intent;
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
 * 修改信息页面，包括修车辆信息
 */
public class UpdateInfoActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	private TextView tv_letf_hint;
	private ImageButton ib_return;
	private EditText et_name;
	private Button btn_save;
	private SharedPreferences sp;
	private HttpUtils httpUtils;
	private LodingDialog lodingDialog;
	private HttpHandler<String> httpHandler;
	private String title;
	private String content;
	/************ 区分类型，0车牌号，1车主姓名，2车架号，3发动机号，4行驶公里数 *********************/
	private int type;
	private String id;
	private String userId;
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
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
		Intent intent = getIntent();
		if (intent != null) {
			title = intent.getStringExtra("title");
			content = intent.getStringExtra("content");
			type = intent.getIntExtra("type", -1);
			id = intent.getStringExtra("id");
			tv_title.setText("修改" + title);
			tv_letf_hint.setText(title);
			et_name.setText(content);
		}

	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		et_name = (EditText) findViewById(R.id.nickname_et_update);
		btn_save = (Button) findViewById(R.id.save_btn_update);
		btn_save.setOnClickListener(this);
		tv_letf_hint = (TextView) findViewById(R.id.nick_left_tv_update);
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
					Toast.makeText(this, "请输入信息", Toast.LENGTH_SHORT).show();
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

	private void getDataFromServer(final String value) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("token", token);
		switch (type) {
			case 0:
				params.addBodyParameter("method", "carnumber");
				break;
			case 1:
				params.addBodyParameter("method", "carownername");
				break;
			case 2:
				params.addBodyParameter("method", "vin");
				break;
			case 3:
				params.addBodyParameter("method", "engineno");
				break;
			case 4:
				params.addBodyParameter("method", "kilometres");
				break;
		}
		params.addBodyParameter("value", value);
		params.addBodyParameter("id", id);
		httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.DELETE_CAR,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(UpdateInfoActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(UpdateInfoActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean baseBean = gson.fromJson(result,
										type.getType());
								switch (baseBean.typecode) {
									case "-1":
										Toast.makeText(UpdateInfoActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "0":
										Toast.makeText(UpdateInfoActivity.this,
												"修改失败", Toast.LENGTH_SHORT).show();
										break;
									case "1":
										Toast.makeText(UpdateInfoActivity.this,
												"修改成功", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent();
										intent.putExtra("data", value);
										UpdateInfoActivity.this.setResult(
												RESULT_OK, intent);
										finish();
										break;
									default:
										Toast.makeText(UpdateInfoActivity.this,
												"系统错误", Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(UpdateInfoActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
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
