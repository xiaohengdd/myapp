package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.CouponsListAdapter;
import com.cac.machehui.client.adapter.TuanGouListAdapter;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.GoodBean;
import com.cac.machehui.client.entity.MyOrderBean;
import com.cac.machehui.client.entity.MyOrderResult;
import com.cac.machehui.client.entity.PointScoreGoodListBean;
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

/**
 * 团购券以及礼券的详细页面
 */
public class TuangouOrLiquanListActivity extends BaseActivity implements
		IXListViewListener {
	private Button back_btn;
	private TextView title_tv;
	private List<GoodBean> list = new ArrayList<GoodBean>();
	private List<MyOrderBean> tuanList = new ArrayList<MyOrderBean>();
	private XListView liquan_lv;
	private SharedPreferences sp;
	private String userId = "";
	private String token = "";
	private CouponsListAdapter liQuanAdapter;
	private TuanGouListAdapter tuanAdapter;
	private TextView tv_empty;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuangouorliquan_list_layout);
		iniView();
		iniData();
	}

	private void iniData() {
		sp = TuangouOrLiquanListActivity.this.getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
		if (TextUtils.isEmpty(userId)) {
			Intent intent = new Intent(TuangouOrLiquanListActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			tag = TuangouOrLiquanListActivity.this.getIntent().getStringExtra(
					"tag");
			if (tag.equals("tuangouquan")) {
				title_tv.setText("团购券");
				tuanAdapter = new TuanGouListAdapter(
						TuangouOrLiquanListActivity.this, tuanList);
				liquan_lv.setAdapter(tuanAdapter);
			} else {
				title_tv.setText("礼券");
				liQuanAdapter = new CouponsListAdapter(
						TuangouOrLiquanListActivity.this, list);
				liquan_lv.setAdapter(liQuanAdapter);
			}
			onRefresh();
		}
	}

	private void iniView() {
		tv_empty = (TextView) findViewById(R.id.empty_tv);
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TuangouOrLiquanListActivity.this.finish();
			}
		});
		title_tv = (TextView) findViewById(R.id.app_title_tv);
		liquan_lv = (XListView) findViewById(R.id.tuangouOrLiquan_lv);
		liquan_lv.setPullRefreshEnable(true);
		liquan_lv.setPullLoadEnable(false);
		// 设置刷新和加载的监听
		liquan_lv.setXListViewListener(this);
		liquan_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				int index = position - 1;
				if (tag.equals("tuangouquan") && index > -1) {
					Intent intent = new Intent(
							TuangouOrLiquanListActivity.this,
							TuangoupasswordActivcity.class);
					intent.putExtra("myOrderBean", tuanList.get(index));
					startActivity(intent);
				}
			}
		});
		liquan_lv.setEmptyView(tv_empty);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent();
		intent.setClass(TuangouOrLiquanListActivity.this, HomeActivity.class);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		if (!NetworkUtil.hasInternetConnected(this)) {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
			return;
		}
		if (tag.equals("tuangouquan")) {
			getList(initTuanGouParams(), URLCst.GET_ORDERA,
					initTuanGouHttpUtils());
		} else {
			getList(initLiQuanParams(), URLCst.LI_QUAN, initLiQuanHttpUtils());
		}
	}

	private HttpUtils initLiQuanHttpUtils() {
		HttpUtils httpUtils = new HttpUtils();
		return httpUtils;
	}

	private HttpUtils initTuanGouHttpUtils() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		return httpUtils;
	}

	private RequestParams initLiQuanParams() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("token", token);
		return params;
	}

	private RequestParams initTuanGouParams() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("orderType", "0");
		params.addBodyParameter("shopgoodStatus", "1");// 去未消费的
		params.addBodyParameter("token", token);
		return params;
	}

	private void getList(RequestParams params, String url, HttpUtils httpUtils) {
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(TuangouOrLiquanListActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						onStopLoad();
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(TuangouOrLiquanListActivity.this,
									"返回数据异常", Toast.LENGTH_SHORT).show();
						} else {
							jsonData(result);
						}
					}
				});

	}

	private void jsonData(String result) {
		try {
			Gson gson = new Gson();
			if (tag.equals("tuangouquan")) {
				TypeToken<MyOrderResult> typeToken = new TypeToken<MyOrderResult>() {

				};
				MyOrderResult myOrderResult = gson.fromJson(result,
						typeToken.getType());
				switch (myOrderResult.typecode) {
					case "-1":
						Toast.makeText(TuangouOrLiquanListActivity.this,
								"登录失效，请重新登录", Toast.LENGTH_SHORT).show();
						break;
					case "200":
						List<MyOrderBean> tempList = myOrderResult.orderList;
						tuanList.clear();
						if (tempList != null) {
							tuanList.addAll(tempList);
							tuanAdapter.notifyDataSetChanged();
						}
						break;
					default:
						Toast.makeText(TuangouOrLiquanListActivity.this,
								"获取列表失败，请重试", Toast.LENGTH_SHORT).show();
						break;
				}
			} else {
				TypeToken<PointScoreGoodListBean> type = new TypeToken<PointScoreGoodListBean>() {
				};
				PointScoreGoodListBean scoreBean = gson.fromJson(result,
						type.getType());
				switch (scoreBean.typecode) {
					case "-1":
						Toast.makeText(TuangouOrLiquanListActivity.this,
								"登录失效，请重新登录", Toast.LENGTH_SHORT).show();
						break;
					case "200":
						list.clear();
						list.addAll(scoreBean.myscoregoodlistcopy);
						liQuanAdapter.notifyDataSetChanged();
						break;
					default:
						Toast.makeText(TuangouOrLiquanListActivity.this,
								"获取列表失败，请重试", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		} catch (JsonSyntaxException e) {
			Toast.makeText(TuangouOrLiquanListActivity.this, "解析异常",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onLoadMore() {

	}

	/** 停止刷新， */
	private void onStopLoad() {
		liquan_lv.stopRefresh();
		liquan_lv.stopLoadMore();
		liquan_lv.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
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
