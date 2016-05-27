package com.cac.machehui.client.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.Peccancy;
import com.cac.machehui.client.entity.PeccancyItem;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 违章查询功能的展示页面
 */
public class PeccancyQueryActivity extends BaseActivity {

	private ListView peccancy_lv;
	private LinearLayout happy_linear;

	private TextView weizhang_value;
	private TextView fakuan_value;
	private TextView jifen_value;

	private TextView chepaihao;

	private Intent intent;

	private String carno = "";
	private String chejiahao = "";
	private String cityid = "";
	private String fadongji = "";
	private LodingDialog lodingDialog;
	private HttpHandler<String> httpHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.peccancy_layout);
		initView();
		iniData();
		if (NetworkUtil.hasInternetConnected(this)) {
			getPeccancyQuery(carno, chejiahao, cityid);
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	private void iniData() {
		intent = PeccancyQueryActivity.this.getIntent();
		carno = intent.getStringExtra("carno");
		fadongji = intent.getStringExtra("fadongji");
		chejiahao = intent.getStringExtra("chejiahao");
		cityid = intent.getStringExtra("cityid");
	}

	private void initView() {
		peccancy_lv = (ListView) findViewById(R.id.prccancy_lv);
		happy_linear = (LinearLayout) findViewById(R.id.biaoqing_linear);
		weizhang_value = (TextView) findViewById(R.id.weizhang_value);
		fakuan_value = (TextView) findViewById(R.id.fakuan_value);
		jifen_value = (TextView) findViewById(R.id.jifen_value);
		chepaihao = (TextView) findViewById(R.id.chepaihao);
	}

	private void getPeccancyQuery(String carno, String chejiahao, String cityid) {

		// String carno = "鲁AS930L";

		// String province = "SD";
		// String cityid = "134";
		// String classno = "LBERCACB7BX094631";
		String key = "110f0e31175a7fe24a42abb392bfbb4d";

		HttpUtils httputils = new HttpUtils();
		httputils.configResponseTextCharset("utf-8");
		// RequestParams params = new RequestParams("UTF-8");
		// params.addQueryStringParameter("key", MaCheHuiConstants.WZ_KEY);
		// params.addQueryStringParameter("cityid", cityid);
		// params.addQueryStringParameter("carno", carno);
		// params.addQueryStringParameter("engineno", fadongji);
		// params.addQueryStringParameter("classno", chejiahao);
		String url = "http://v.juhe.cn/wzcxy/query?key=" + key + "&cityid="
				+ cityid + "&carno=" + carno + "&engineno=" + fadongji
				+ "&classno=" + chejiahao;
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httputils.send(HttpMethod.GET, url,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(PeccancyQueryActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String back_json = arg0.result;

						Log.v("!!!", "车辆违章查询返回数据" + back_json);

						Gson gson = new Gson();

						Peccancy peccancy = gson.fromJson(back_json,
								Peccancy.class);
						if (!peccancy.getError_code().equals("0")) {
							CustomToast.showToast(PeccancyQueryActivity.this,
									peccancy.getReason(), Toast.LENGTH_SHORT);
							PeccancyQueryActivity.this.finish();
						} else {
							List<PeccancyItem> list = peccancy.getResult()
									.getLists();
							chepaihao.setText(peccancy.getResult().getCarno());
							if (list == null || list.size() == 0) {
								happy_linear.setVisibility(View.VISIBLE);
								peccancy_lv.setVisibility(View.GONE);
							} else {
								int moneyCount = 0;
								int numCount = 0;
								for (int i = 0; i < list.size(); i++) {
									String num1 = list.get(i).getMoney();
									String num2 = list.get(i).getFen();

									moneyCount += Integer.parseInt(num1);
									numCount += Integer.parseInt(num2);

								}
								fakuan_value.setText("" + moneyCount);
								jifen_value.setText("" + numCount);
								weizhang_value.setText("" + list.size());
								PeccancyQueryAdapter adapter = new PeccancyQueryAdapter(
										PeccancyQueryActivity.this, list);
								peccancy_lv.setAdapter(adapter);
							}
						}
					}
				});
	}

	/**
	 *
	 * 违章适配器
	 *
	 */
	private class PeccancyQueryAdapter extends BaseAdapter {

		private Context context;
		private List<PeccancyItem> list;

		public PeccancyQueryAdapter(Context context, List<PeccancyItem> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = LayoutInflater.from(context).inflate(
						R.layout.prccancy_query_item, null);

				holder.tiem_tv = (TextView) convertView.findViewById(R.id.time);
				holder.location_tv = (TextView) convertView
						.findViewById(R.id.location);
				holder.reason_tv = (TextView) convertView
						.findViewById(R.id.reason);

				holder.fakuan = (TextView) convertView
						.findViewById(R.id.fakuan_value);
				holder.jifen = (TextView) convertView
						.findViewById(R.id.jifen_value);

				holder.tiem_tv.setText(list.get(position).getDate());
				holder.location_tv.setText(list.get(position).getArea());
				holder.reason_tv.setText(list.get(position).getAct());

				holder.fakuan.setText(list.get(position).getMoney());
				holder.jifen.setText(list.get(position).getFen());

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

				holder.tiem_tv.setText(list.get(position).getDate());
				holder.location_tv.setText(list.get(position).getArea());
				holder.reason_tv.setText(list.get(position).getAct());

				holder.fakuan.setText(list.get(position).getMoney());
				holder.jifen.setText(list.get(position).getFen());
			}

			return convertView;
		}

	}

	private class ViewHolder {
		TextView tiem_tv;
		TextView location_tv;
		TextView reason_tv;

		TextView fakuan;
		TextView jifen;

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
