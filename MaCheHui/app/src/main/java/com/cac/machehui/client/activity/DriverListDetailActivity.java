package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;

import wkj.team.driver.adapter.DriverLineAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.CarLineBean;
import com.cac.machehui.client.entity.CarModelResultBean;
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
 * 车系页面
 */
public class DriverListDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private ListView sortListView;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private List<CarLineBean> carBrandBeanList;
	private DriverLineAdapter mAdapter;
	private String id;
	private String carBrand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_list);
		iniView();
		iniData();
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	private void iniData() {
		Intent intent = getIntent();
		id = intent.getStringExtra("carBrId");
		carBrand = intent.getStringExtra("carBrand");
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("carbrid", id);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.GET, URLCst.GET_LINE_BRAND,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(DriverListDetailActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(DriverListDetailActivity.this,
									"返回数据异常", Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<CarModelResultBean> type = new TypeToken<CarModelResultBean>() {
								};
								CarModelResultBean carModelResultBean = gson
										.fromJson(result, type.getType());
								List<CarLineBean> list = carModelResultBean.carmodle;
								carBrandBeanList.clear();
								if (list != null && list.size() > 0) {
									carBrandBeanList.addAll(list);
									mAdapter.notifyDataSetChanged();
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(DriverListDetailActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("选择车系");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);

		carBrandBeanList = new ArrayList<CarLineBean>();
		mAdapter = new DriverLineAdapter(DriverListDetailActivity.this,
				carBrandBeanList);
		sortListView.setAdapter(mAdapter);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CarLineBean carLineBean = carBrandBeanList.get(position);
				Intent intent = new Intent();
				intent.putExtra("value", carLineBean.Value);
				intent.putExtra("line", carLineBean.Text);
				intent.putExtra("carBrand", carBrand);
				intent.setClass(DriverListDetailActivity.this,
						DriverDetailActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_return_ib_header:
			finish();
			break;

		default:
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
