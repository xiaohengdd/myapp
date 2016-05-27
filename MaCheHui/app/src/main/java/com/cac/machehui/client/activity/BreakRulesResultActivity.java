package com.cac.machehui.client.activity;

import java.net.URLEncoder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BreakRuleResultBean;
import com.cac.machehui.client.entity.CityItem;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * Υ�²�ѯ���
 */
public class BreakRulesResultActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private TextView tv_car_number;
	private ListView lv_result;
	private String cityId;
	private String carnumber;
	private CityItem cityItem;
	/******** ���ܺ� **********/
	private String classno;
	/******** �������� **********/
	private String engineno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_break_rules_result);
		initView();
		iniData();
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "����������ҳ����ˣ���������", Toast.LENGTH_SHORT).show();
		}
	}

	private void iniData() {
		Intent intent = getIntent();
		cityItem = (CityItem) intent.getSerializableExtra("cityItem");
		carnumber = intent.getStringExtra("carNumber");
		classno = intent.getStringExtra("classno");
		engineno = intent.getStringExtra("engineno");
		tv_car_number.setText(carnumber);
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("��ѯΥ�½��");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		tv_car_number = (TextView) findViewById(R.id.car_number_tv_break_rules);
		lv_result = (ListView) findViewById(R.id.result_lv_break_rules);
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		RequestParams params = new RequestParams("UTF-8");
		switch (cityItem.engineno) {
			case "0":

				break;
			case "99":
				params.addBodyParameter("engineno", engineno);
				break;
			default:
				int num = Integer.parseInt(cityItem.engineno);
				if (num < engineno.length()) {
					params.addBodyParameter("engineno", engineno.substring(num - 1));
				}
				break;
		}
		switch (cityItem.classno) {
			case "0":

				break;
			case "99":
				params.addBodyParameter("classno", classno);
				break;
			default:
				int num = Integer.parseInt(cityItem.classno);
				if (num < engineno.length()) {
					params.addBodyParameter("classno", engineno.substring(num - 1));
				}
				break;
		}
		params.addBodyParameter("key", MaCheHuiConstants.WZ_KEY);
		params.addBodyParameter("cityid", cityItem.cityId);
		params.addBodyParameter("carno", URLEncoder.encode(carnumber));// ���ƺ���
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.WEIZHANG, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(BreakRulesResultActivity.this,
								"���������ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						try {
							Gson gson = new Gson();
							BreakRuleResultBean resultBean = gson.fromJson(
									result, BreakRuleResultBean.class);
							if (resultBean.error_code == 0) {
								// ��ѯ�Ľ��

							} else {
								Toast.makeText(BreakRulesResultActivity.this,
										resultBean.reason, Toast.LENGTH_SHORT)
										.show();
							}
						} catch (Exception e) {
							Toast.makeText(BreakRulesResultActivity.this,
									"�����쳣", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
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

}
