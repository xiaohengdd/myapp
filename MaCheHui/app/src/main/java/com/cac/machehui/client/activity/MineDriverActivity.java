package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.MyCarAdapter;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.MyCar;
import com.cac.machehui.client.entity.MyCarListBean;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;
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

public class MineDriverActivity extends BaseActivity implements
		IXListViewListener, OnItemClickListener {
	private XListView driverList;

	private Button btnAdd;
	private SharedPreferences sp;
	private List<MyCar> list;// 我的汽车list
	private LinearLayout mine_driver_nocar;
	private HttpUtils httpUtils;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String userid;
	private MyCarAdapter mAdapter;
	private LinearLayout ll_add_car;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine_driver_activity);
		driverList = (XListView) findViewById(R.id.mine_driver_list_ll);
		// 没有汽车时的布局
		mine_driver_nocar = (LinearLayout) findViewById(R.id.mine_driver_nocar);
		// 返回按钮
		ImageButton mine_driver_deatail_btn_back_n = (ImageButton) findViewById(R.id.mine_driver_deatail_btn_back_n);
		mine_driver_deatail_btn_back_n
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						onBackPressed();
					}
				});
		// 添加车辆的按钮
		btnAdd = (Button) findViewById(R.id.btn_add);
		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MineDriverActivity.this,
						DriverInfoActivity.class);
				startActivity(intent);
			}
		});
		sp = MineDriverActivity.this.getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);
		userid = sp.getString("userId", "");
		list = new ArrayList<MyCar>();
		mAdapter = new MyCarAdapter(this, list);
		driverList.setAdapter(mAdapter);
		driverList.setPullLoadEnable(false);
		driverList.setXListViewListener(this);
		driverList.setOnItemClickListener(this);
		ll_add_car = (LinearLayout) findViewById(R.id.add_car_mine_driver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	private void getDataFromServer() {
		httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userid);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.SELECT_CAR,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(MineDriverActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						onLoad();
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(MineDriverActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<MyCarListBean> type = new TypeToken<MyCarListBean>() {
								};
								MyCarListBean myCarListBean = gson.fromJson(
										result, type.getType());
								list.clear();
								if (myCarListBean.mycarlist != null
										&& myCarListBean.mycarlist.size() > 0) {
									mine_driver_nocar.setVisibility(View.GONE);
									list.addAll(myCarListBean.mycarlist);
									mAdapter.notifyDataSetChanged();
								} else {
									mine_driver_nocar
											.setVisibility(View.VISIBLE);
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(MineDriverActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	/** 停止刷新， */
	private void onLoad() {
		driverList.stopRefresh();
		driverList.stopLoadMore();
		driverList.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	@Override
	public void onRefresh() {
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		MyCar myCar = list.get(position - 1);
		Intent intent = new Intent(MineDriverActivity.this,
				EditDriverInfoActivity.class);
		intent.putExtra("myCar", myCar);
		startActivity(intent);

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
