package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wkj.team.driver.adapter.DriverTypeAdapter;
import wkj.team.driver.view.SideBar;
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
import com.cac.machehui.client.entity.CarBrandBean;
import com.cac.machehui.client.entity.CarBrandResultBean;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 车品牌页面
 */
public class DriverListActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private List<CarBrandBean> carBrandBeanList;
	private DriverTypeAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_list);
		iniView();
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.GET, URLCst.GET_CAR_BRAND,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(DriverListActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(DriverListActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<CarBrandResultBean> type = new TypeToken<CarBrandResultBean>() {
								};
								CarBrandResultBean caResultBean = gson
										.fromJson(result, type.getType());
								List<CarBrandBean> list = caResultBean.listcarbr;
								carBrandBeanList.clear();
								if (list != null && list.size() > 0) {
									sideBar.setVisibility(View.VISIBLE);
									carBrandBeanList.addAll(list);
									mAdapter.notifyDataSetChanged();
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(DriverListActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("选择品牌");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 有bug
				if (carBrandBeanList.size() > 0) {
					for (int i = 0; i < carBrandBeanList.size(); i++) {
						CarBrandBean carBrandBean = carBrandBeanList.get(i);
						if (((carBrandBean.carbrletter).toUpperCase(Locale
								.getDefault())).equals(s)
								&& i < carBrandBeanList.size() - 1) {
							mAdapter.notifyDataSetChanged();
							sortListView.setSelection(i);
							break;
						}
					}
				}
			}
		});
		carBrandBeanList = new ArrayList<CarBrandBean>();
		mAdapter = new DriverTypeAdapter(DriverListActivity.this,
				carBrandBeanList);
		sortListView.setAdapter(mAdapter);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CarBrandBean carBrandBean = carBrandBeanList.get(position);
				Intent intent = new Intent();
				intent.putExtra("carBrId", carBrandBean.carbrid);
				intent.putExtra("carBrand", carBrandBean.carbrname);
				intent.setClass(DriverListActivity.this,
						DriverListDetailActivity.class);
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
