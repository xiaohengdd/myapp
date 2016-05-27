package com.cac.machehui.client.activity.city;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.PeccancyQueryActivity;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.CitiesDataBean;
import com.cac.machehui.client.entity.CitiesDataReaultBean;
import com.cac.machehui.client.entity.CityItem;
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


public class GetAddressInfoActivity extends Activity {
	private List<CityItem> addressList = null;
	private String province = null;
	private String city = null;
	private int type = 0;
	private Intent intent;
	private String carnumber;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private ProvinceAdapter adapter;

	private String classno;

	private String engineno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_info);
		initData();
		iniView();
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "？？？？", Toast.LENGTH_SHORT).show();
		}
	}

	private void initData() {
		addressList = new ArrayList<CityItem>();
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			type = bundle.getInt("fromEdit");
			carnumber = bundle.getString("carNumber");
			engineno = bundle.getString("engineno");
			classno = bundle.getString("classno");
		}
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		lodingDialog = LodingDialog.createDialog(this);
		params.addQueryStringParameter("key", MaCheHuiConstants.WZ_KEY);
		httpHandler = httpUtils.send(HttpMethod.GET, URLCst.WEIZHANG_CITIES,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(GetAddressInfoActivity.this,
								"？？？？？？", Toast.LENGTH_SHORT).show();
						GetAddressInfoActivity.this.finish();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						try {
							Gson gson = new Gson();
							TypeToken<CitiesDataReaultBean> type = new TypeToken<CitiesDataReaultBean>() {
							};
							CitiesDataReaultBean reaultBean = gson.fromJson(
									result, type.getType());
							switch (reaultBean.error_code) {
							case 0:
								getShanDongCities(reaultBean.result);
								break;
							default:
								Toast.makeText(GetAddressInfoActivity.this,
										reaultBean.reason, Toast.LENGTH_SHORT)
										.show();
								GetAddressInfoActivity.this.finish();
								break;
							}
						} catch (JsonSyntaxException e) {
							Toast.makeText(GetAddressInfoActivity.this, "？？？",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}


	protected void getShanDongCities(List<CitiesDataBean> result) {
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				CitiesDataBean citiesDataBean = result.get(i);
				if ("灞变笢".equals(citiesDataBean.provinceName)) {
					addressList.clear();
					addressList.addAll(citiesDataBean.citys);
					adapter.notifyDataSetChanged();
					return;
				}
			}
		}
	}

	private void iniView() {
		ListView listView = (ListView) findViewById(R.id.listview);
		adapter = new ProvinceAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (type) {
				case 0:
					intent.putExtra("city", addressList.get(arg2).cityName);
					intent.putExtra("cityId", addressList.get(arg2).cityId);
					intent.putExtra("cityItem", addressList.get(arg2));
					setResult(Activity.RESULT_OK, intent);
					break;
				case 1:
					// intent = new Intent(GetAddressInfoActivity.this,
					// BreakRulesResultActivity.class);
					// intent.putExtra("carNumber", carnumber);
					// intent.putExtra("cityItem", addressList.get(arg2));
					// intent.putExtra("engineno", engineno);
					// intent.putExtra("classno", classno);
					// startActivity(intent);
					intent = new Intent(GetAddressInfoActivity.this,
							PeccancyQueryActivity.class);
					intent.putExtra("carno", carnumber);
					intent.putExtra("cityid", addressList.get(arg2).cityId);
					intent.putExtra("fadongji", engineno);
					intent.putExtra("chejiahao", classno);
					startActivity(intent);
					break;
				}
				finish();
			}
		});
	}

	class ProvinceAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return addressList != null ? addressList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return addressList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = GetAddressInfoActivity.this.getLayoutInflater().inflate(
						R.layout.item_addressinfo, null);
				convertView = view;
				convertView.setTag(view);
			} else {
				view = (View) convertView.getTag();
			}
			TextView text = (TextView) view
					.findViewById(R.id.item_address_city);
			text.setText(addressList.get(position).cityName);
			return convertView;
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
