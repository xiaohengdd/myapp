package com.cac.machehui.client.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BundleMachekaActivity extends BaseActivity {
	private EditText machekaNo_value;
	private Button bundleit;
	private LinearLayout number_linear;
	private LinearLayout password_linear;

	private EditText fangge1;
	private EditText fangge2;
	private EditText fangge3;
	private EditText fangge4;
	private EditText fangge5;
	private EditText fangge6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bundle_macheka_layout);
		init();

		// 返回按钮
		ImageButton shurukahao_btn_back = (ImageButton) findViewById(R.id.shurukahao_btn_back);
		shurukahao_btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

				// TODO Auto-generated method stub

			}
		});

		bundleit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bundleit.getText().toString().equals("下一步")) {

					password_linear.setVisibility(View.VISIBLE);

					number_linear.setVisibility(View.GONE);

					bundleit.setText("绑定码车卡");

				} else {
					HttpUtils httputils = new HttpUtils();

					String url = URLCst.CAR_HOST
							+ "/appInterface/insertmacheka?user_name="
							+ "13911271013&machekaid=8661087390000096756";

					httputils.send(HttpMethod.GET, url,
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
													  String arg1) {
									Log.v("!!!", "绑定玛车卡请求异常");

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									if (arg0.equals("1")) {
										Toast.makeText(
												BundleMachekaActivity.this,
												"绑定成功", Toast.LENGTH_SHORT)
												.show();
										BundleMachekaActivity.this.finish();
									} else {
										Toast.makeText(
												BundleMachekaActivity.this,
												"绑定失败", Toast.LENGTH_SHORT)
												.show();
										BundleMachekaActivity.this.finish();
									}

								}
							});

				}

			}
		});

	}

	private void init() {
		machekaNo_value = (EditText) findViewById(R.id.machekaNo_value);

		bundleit = (Button) findViewById(R.id.bundleit);
		number_linear = (LinearLayout) findViewById(R.id.number_linear);
		password_linear = (LinearLayout) findViewById(R.id.password_linear);

		fangge1 = (EditText) findViewById(R.id.fangge1);
		fangge2 = (EditText) findViewById(R.id.fangge2);
		fangge3 = (EditText) findViewById(R.id.fangge3);
		fangge4 = (EditText) findViewById(R.id.fangge4);
		fangge5 = (EditText) findViewById(R.id.fangge5);
		fangge6 = (EditText) findViewById(R.id.fangge6);
	}

}
