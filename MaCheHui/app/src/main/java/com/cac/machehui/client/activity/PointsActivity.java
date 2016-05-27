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
import com.cac.machehui.client.adapter.PointsAdapter;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.entity.GoodBean;
import com.cac.machehui.client.entity.PointsBean;
import com.cac.machehui.client.utils.CustomToast;
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
 * 积分商城
 */
public class PointsActivity extends BaseActivity implements OnClickListener,
		IXListViewListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private XListView mListView;
	private HttpUtils httpUtils;
	private SharedPreferences sp;
	private String userId;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private PointsAdapter mAdapter;
	private PointsBean pointsBean;
	private List<GoodBean> goodBeans;
	private int page = 1;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points);
		iniView();
		iniData();
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

	@Override
	protected void onPause() {
		super.onPause();
		page = 1;
	}

	/**
	 * 服务器获取积分以及商品列表数据
	 */
	public void getDataFromServer() {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("page", page + "");
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.GET_SCORE_GOOD,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						CustomToast.showToast(PointsActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						onStopLoad();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PointsActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<PointsBean> type = new TypeToken<PointsBean>() {
								};
								pointsBean = gson.fromJson(result,
										type.getType());
								switch (pointsBean.typecode) {
									case "-1":
										Toast.makeText(PointsActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										String score;
										if (TextUtils.isEmpty(pointsBean.scoreNum)) {
											score = "当前积分0";
										} else {
											score = "当前积分" + pointsBean.scoreNum;
										}
										if (pointsBean.scoreNum == null) {
											KeyValues.scoreNum = 0 + "";
										}
										KeyValues.scoreNum = pointsBean.scoreNum;
										mAdapter.currentScore = score;
										if (page == 1) {
											goodBeans.clear();
										}
										goodBeans.addAll(pointsBean.scoreGoodList);
										mAdapter.notifyDataSetChanged();
										if (pointsBean.scoreGoodList.size() >= 10) {
											mListView.setPullLoadEnable(true);
										} else {
											mListView.setPullLoadEnable(false);
										}
										break;
									default:
										Toast.makeText(PointsActivity.this, "系统错误",
												Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PointsActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}

					}
				});
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
		KeyValues.userid = userId;
		goodBeans = new ArrayList<GoodBean>();
		mAdapter = new PointsAdapter(this, goodBeans, "当前积分0");
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);
		mListView.setPullRefreshEnable(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				int position = arg2 - 1;
				if (goodBeans != null && goodBeans.size() > 0 && position > -1
						&& position != 0) {
					GoodBean goodBean = goodBeans.get(position - 1);
					switch (goodBean.type) {
						case "0":// 跳转至详情页面 实物
						case "1":// 券
							Intent intent = new Intent(PointsActivity.this,
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
		});
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("积分商城");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		mListView = (XListView) findViewById(R.id.quan_xlv_points);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.history_tv_points:
				// 跳转兑换记录页面
				intent = new Intent(this, ChangeHistoryActivity.class);
				startActivity(intent);
				break;
			case R.id.jifen_turntable_iv_points:

				break;
		}
	}

	/**
	 * 抽奖
	 */
	private void getDraw(GoodBean goodBean) {
		HttpUtils httpUtils = new HttpUtils();
		/** 设置超时时间 */
		httpUtils.configTimeout(10 * 1000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("scoregoodid", goodBean.scoregoodid);
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("scoreNum", KeyValues.scoreNum);
		params.addBodyParameter("score", goodBean.score);
		params.addBodyParameter("buyNum", "1");
		lodingDialog = LodingDialog.createDialog(this);
		lodingDialog.setCancelable(false);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.BUY_SCORE_GOOD,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(PointsActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
						lodingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PointsActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean baseBean = gson.fromJson(result,
										type.getType());
								switch (baseBean.typecode) {
									case "-1":
										Toast.makeText(PointsActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										Toast.makeText(PointsActivity.this,
												"抱歉，您没有中奖，请再试一次吧",
												Toast.LENGTH_SHORT).show();
										onRefresh();
										break;
									case "201":
										Toast.makeText(PointsActivity.this, "兑换失败",
												Toast.LENGTH_SHORT).show();
										break;
									default:
										Toast.makeText(PointsActivity.this, "系统错误",
												Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PointsActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	@Override
	public void onRefresh() {
		page = 1;
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoadMore() {
		page++;
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	/** 停止刷新， */
	private void onStopLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
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
