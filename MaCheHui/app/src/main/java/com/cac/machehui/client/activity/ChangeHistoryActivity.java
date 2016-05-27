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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.MyScoreGoodListAdapter;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.GoodBean;
import com.cac.machehui.client.entity.MyScoreGoodListBean;
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
 * 兑换记录
 */
public class ChangeHistoryActivity extends BaseActivity implements
		OnClickListener, IXListViewListener, OnItemClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	/****** 兑换列表 ******/
	private XListView mListView;
	private HttpUtils httpUtils;
	private LodingDialog lodingDialog;
	private SharedPreferences sp;
	private String userId;
	private HttpHandler<String> httpHandler;
	private MyScoreGoodListAdapter mAdapter;
	private List<GoodBean> goodBeanList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_history);
		iniView();
		iniData();
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("兑换记录");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		mListView = (XListView) findViewById(R.id.history_xlv_points);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
		TextView tv_empty = (TextView) findViewById(R.id.empty_tv);
		mListView.setEmptyView(tv_empty);
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		KeyValues.userid = userId;
		goodBeanList = new ArrayList<GoodBean>();
		mAdapter = new MyScoreGoodListAdapter(this, goodBeanList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onRefresh();
	}

	private void getDataFromServer() {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.SCORE_GOOD_LISTS,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(ChangeHistoryActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						onStopLoad();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(ChangeHistoryActivity.this,
									"返回数据异常", Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<MyScoreGoodListBean> type = new TypeToken<MyScoreGoodListBean>() {
								};
								MyScoreGoodListBean pointsBean = gson.fromJson(
										result, type.getType());
								switch (pointsBean.typecode) {
									case "-1":
										Toast.makeText(ChangeHistoryActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										goodBeanList.clear();
										goodBeanList
												.addAll(pointsBean.myScoreGoodList);
										mAdapter.notifyDataSetChanged();
										break;
									default:
										Toast.makeText(ChangeHistoryActivity.this,
												"系统错误", Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(ChangeHistoryActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
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

	/** 停止刷新， */
	private void onStopLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		GoodBean goodBean = goodBeanList.get(position - 1);
		switch (goodBean.type) {
			case "0":// 跳转至详情页面 实物
			case "1":// 券
				Intent intent = new Intent(ChangeHistoryActivity.this,
						PointsDetailActivity.class);// 是否需要传值
				Bundle bundle = new Bundle();
				bundle.putString("wbUrl", goodBean.scoredetai);
				bundle.putString("goodId", goodBean.scoregoodid);
				bundle.putString("goodScore", goodBean.score);
				bundle.putString("state", goodBean.state);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case "2":// 抽奖

				break;
		}
	}
}
