package com.cac.machehui.client.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.Macheka;
import com.cac.machehui.client.entity.MyMachekaResponse;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MineMachekaListActivity extends BaseActivity {

	private SharedPreferences sp;
	private LinearLayout biaoqing_linear;
	private Button bundle_btn;

	private List<Macheka> list;

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.macheka_list_layout);

		lv = (ListView) findViewById(R.id.macheka_lv);

		bundle_btn = (Button) findViewById(R.id.bundle_macheka);
		// 返回按钮
		ImageButton mymacheka_btn_back = (ImageButton) findViewById(R.id.mymacheka_btn_back);
		mymacheka_btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

				// TODO Auto-generated method stub

			}
		});
		bundle_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MineMachekaListActivity.this,
						BundleMachekaActivity.class);
				startActivity(intent);

			}
		});

		biaoqing_linear = (LinearLayout) findViewById(R.id.biaoqing_linear);

		sp = MineMachekaListActivity.this.getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);

		String username = sp.getString("usernames", "");

		String url = URLCst.CAR_HOST + "/appInterface/selectmacheka?user_name="
				+ username;

		HttpUtils httputils = new HttpUtils();

		httputils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.v("!!!", "返回json异常");

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.v("!!!", "返回玛车卡json=" + arg0.result);

				String back_json = arg0.result;

				Gson gson = new Gson();

				MyMachekaResponse response = gson.fromJson(back_json,
						MyMachekaResponse.class);

				list = response.getMachekalist();

				if (list.size() == 0) {
					biaoqing_linear.setVisibility(View.VISIBLE);
				} else {

					MachekaAdapter adapter = new MachekaAdapter(
							MineMachekaListActivity.this, list);

					lv.setAdapter(adapter);

				}
			}
		});

	}

	private class MachekaAdapter extends BaseAdapter {
		Context context;
		List<Macheka> list;

		public MachekaAdapter(Context context, List<Macheka> list) {
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

			View view = LayoutInflater.from(context).inflate(
					R.layout.macheka_list_item, null);

			TextView number = (TextView) view.findViewById(R.id.macheka_number);

			String num = list.get(position).getMachekaid();

			number.setText(num);

			return view;
		}

	}

}
