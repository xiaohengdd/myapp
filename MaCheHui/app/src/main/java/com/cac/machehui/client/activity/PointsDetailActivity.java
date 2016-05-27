package com.cac.machehui.client.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.utils.CheckUtil;
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
 * 积分商品详情
 */
public class PointsDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private ImageButton ib_share;
	/****** 商品详情 ******/
	private WebView wv_detail;
	/****** 马上兑换 ******/
	private Button btn_change;
	private ProgressBar bar;
	private String goodId;
	private String userId;
	private String token;
	private String state;
	private SharedPreferences sp;
	private HttpHandler<String> httpHandler;
	private String urlString;
	private String goodScore;
	private LodingDialog lodingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points_detail);
		iniData();
		iniView();
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			urlString = bundle.getString("wbUrl");
			goodId = bundle.getString("goodId");
			goodScore = bundle.getString("goodScore");
			state = bundle.getString("state");
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void iniView() {
		bar = (ProgressBar) findViewById(R.id.myProgressBar);
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("商品详情");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		ib_share = (ImageButton) findViewById(R.id.right_share_ib_header);
		ib_share.setOnClickListener(this);
		wv_detail = (WebView) findViewById(R.id.detail_wv_point_detail);
		wv_detail.getSettings().setJavaScriptEnabled(true);
		if (!TextUtils.isEmpty(urlString)) {
			wv_detail.loadUrl(urlString);
		}
		wv_detail.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					bar.setVisibility(View.GONE);
				} else {
					if (View.INVISIBLE == bar.getVisibility()) {
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		btn_change = (Button) findViewById(R.id.change_btn_point_detail);
		switch (state) {
			case "0":
				btn_change.setBackgroundColor(getResources().getColor(
						R.color.bg_gray));
				btn_change.setText("已过期");
				break;
			case "1":// 正在进行
				btn_change.setOnClickListener(this);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.right_share_ib_header:
				// 分享
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT, "积分商品分享!");
				shareIntent.setType("text/plain");
				// 设置分享列表的标题，并且每次都显示分享列表
				startActivity(Intent.createChooser(shareIntent, "分享到"));
				break;
			case R.id.change_btn_point_detail:
				// 请求服务器兑换，兑换成功与否发个通知，兑换的结果显示在通知栏上
				CheckUtil checkUtil = new CheckUtil(PointsDetailActivity.this);
				if (!checkUtil.isEnough(goodScore, KeyValues.scoreNum)) {
					break;
				}
				if (NetworkUtil.hasInternetConnected(PointsDetailActivity.this)) {
					getDataFromServer();
				} else {
					Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
							.show();
				}
				break;
		}
	}

	/**
	 * 兑换商品
	 */
	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configTimeout(10 * 1000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("scoregoodid", goodId);
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("scoreNum", KeyValues.scoreNum);
		params.addBodyParameter("score", goodScore);
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
						Toast.makeText(PointsDetailActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
						lodingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PointsDetailActivity.this, "返回数据异常",
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
										Toast.makeText(PointsDetailActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										Toast.makeText(PointsDetailActivity.this,
												"兑换成功", Toast.LENGTH_SHORT).show();
										PointsDetailActivity.this.finish();
										break;
									case "201":
										Toast.makeText(PointsDetailActivity.this,
												"兑换失败", Toast.LENGTH_SHORT).show();
										break;
									default:
										Toast.makeText(PointsDetailActivity.this,
												"系统错误", Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PointsDetailActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}

					}
				});
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
