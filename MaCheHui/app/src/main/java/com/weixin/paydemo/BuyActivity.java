package com.weixin.paydemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.activity.LoginActivity;
import com.cac.machehui.client.activity.WashDeatilActivity;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.OrderBean;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 购买后付款前的订单
 */

public class BuyActivity extends BaseActivity {
	AppClient app;// 全局变量
	private HttpUtils httpUtils;
	// @ViewInject(R.id.buy_shopgoodname)
	// 商品名字
	String shopgoodname;
	// 商铺编号
	String shopnum;
	String shopname;// 商品编号
	String howmany;// 商品有几次
	String shopgoodnum;// 商品编号
	@ViewInject(R.id.buy_shopgoodname)
	private TextView tv_shopgoodname;
	String shopgoodprice;
	// 商品价格
	@ViewInject(R.id.buy_shopgoodprice)
	private TextView tv_shopgoodprice;
	// 商品数目
	@ViewInject(R.id.buy_shopgoodsum)
	private TextView tv_shopgoodsum;

	// 商品数量增加的按钮
	@ViewInject(R.id.buynum_plus)
	private ImageButton bt_buynum_plus;
	// 商品数量增加的阴影按钮
	@ViewInject(R.id.buynum_plus_c)
	private ImageButton bt_buynum_plus_c;
	// 商品数量减少的按钮
	@ViewInject(R.id.buynum_minus)
	private ImageButton bt_buynum_minus;
	// 商品数量减少的阴影按钮
	@ViewInject(R.id.buynum_minus_c)
	private ImageButton bt_buynum_minus_c;
	// 价格小计
	@ViewInject(R.id.buy_shopgoodminortotal)
	private TextView buy_shopgoodminortotal;
	// 订单总价
	@ViewInject(R.id.buy_total)
	private TextView buy_total;

	String shopgoodprice_total;
	// 缓存的用户名
	String username;
	int payway = 1;// 支付方式0为未选择支付， 1为码车卡支付，2为微信支付，3为支付宝支付
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String userId;
	private String token;
	private String oldPrice;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		app = ((AppClient) getApplicationContext());
		sp = BuyActivity.this.getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);
		username = sp.getString("usernames", "");
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
		ViewUtils.inject(this);
		// 从上一页得到的 数据
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			shopgoodname = bundle.getString("goodName");
			shopgoodprice = bundle.getString("nowPrice");
			howmany = bundle.getString("howmany");
			shopnum = bundle.getString("shopNum");
			shopname = bundle.getString("shopName");
			shopgoodnum = bundle.getString("shopgoodnum");
			oldPrice = bundle.getString("oldPrice");
			shopgoodprice_total = shopgoodprice;// 初始化商品总价
		} else {
			getValueFromSp();
		}

		findview();
		initInformation();

	}

	private void getValueFromSp() {
		SharedPreferences sp = getSharedPreferences("currentPurchaseDetail",
				Context.MODE_PRIVATE);
		shopgoodname = sp.getString(WashCst.T_GOODNAME, "");
		shopgoodprice = sp.getString(WashCst.T_NOWPRICE, "");
		howmany = sp.getString("howmany", "");
		shopnum = sp.getString("shopNum", "");
		shopname = sp.getString(WashCst.T_SHOPNAME, "");
		shopgoodnum = sp.getString("shopgoodnum", "");
		oldPrice = sp.getString(WashCst.T_OLDPRICE, "");
		shopgoodprice_total = shopgoodprice;// 初始化商品总价

	}

	// 部署数据
	private void initInformation() {
		tv_shopgoodname.setText(shopgoodname);
		tv_shopgoodprice.setText(shopgoodprice + "元");
		buy_shopgoodminortotal.setText(shopgoodprice + "元");
		buy_total.setText(shopgoodprice + "元");

	}

	private void findview() {
		bt_buynum_plus_c.setVisibility(View.GONE);
		bt_buynum_minus.setVisibility(View.GONE);
		// 增加商品数量的按钮
		bt_buynum_plus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String shopgoodnum = tv_shopgoodsum.getText().toString();
				int i = Integer.parseInt(shopgoodnum);

				i++;
				shopgoodprice_total = Integer.parseInt(shopgoodprice) * i + "";
				buy_shopgoodminortotal.setText(shopgoodprice_total + "元");
				buy_total.setText(shopgoodprice_total + "元");// 订单总结 待修改
				tv_shopgoodsum.setText(i + "");
				if (i > 1) {
					bt_buynum_minus_c.setVisibility(View.GONE);
					bt_buynum_minus.setVisibility(View.VISIBLE);
				}
			}
		});
		// 减少商品数量的按钮
		bt_buynum_minus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String shopgoodnum = tv_shopgoodsum.getText().toString();
				int i = Integer.parseInt(shopgoodnum);
				if (i > 1) {
					i--;
					String shopgoodprice_total = Integer
							.parseInt(shopgoodprice) * i + "";
					buy_shopgoodminortotal.setText(shopgoodprice_total + "元");
					buy_total.setText(shopgoodprice_total + "元");// 订单总结 待修改
					tv_shopgoodsum.setText(i + "");
				}

				if (i == 1) {
					bt_buynum_minus_c.setVisibility(View.VISIBLE);
					bt_buynum_minus.setVisibility(View.GONE);
				}
			}
		});

		bt_buynum_minus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String shopgoodnum = tv_shopgoodsum.getText().toString();
				int i = Integer.parseInt(shopgoodnum);
				if (i > 1) {
					i--;
					String shopgoodprice_total = Integer
							.parseInt(shopgoodprice) * i + "";
					buy_shopgoodminortotal.setText(shopgoodprice_total + "元");
					buy_total.setText(shopgoodprice_total + "元");// 订单总结 待修改
					tv_shopgoodsum.setText(i + "");
				}

				if (i == 1) {
					bt_buynum_minus_c.setVisibility(View.VISIBLE);
					bt_buynum_minus.setVisibility(View.GONE);
				}
			}
		});

		// 返回按钮
		ImageButton buy_back_button = (ImageButton) findViewById(R.id.buy_back_button);
		buy_back_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// 去支付按钮

		Button button_buy = (Button) findViewById(R.id.button_buy_buy);
		button_buy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				userId = sp.getString("userid", "");
				token = sp.getString("token", "");
				if (TextUtils.isEmpty(userId)) {
					Intent intent = new Intent(BuyActivity.this,
							LoginActivity.class);
					startActivity(intent);
					return;
				}
				if (NetworkUtil.hasInternetConnected(BuyActivity.this)) {
					getDataFromServer();
				} else {
					Toast.makeText(BuyActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void getDataFromServer() {
		httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("shopid", shopgoodnum);// 商品编号
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("orderType", "0");// 0：团购 1:先消费后付款
		params.addBodyParameter("shopPrice", oldPrice);// 商品价格
		params.addBodyParameter("discountPrice", shopgoodprice);// 折扣价格
		params.addBodyParameter("shopgoodNum", shopgoodprice_total);// 商品数量
		params.addBodyParameter("shopgoodType", "1");// 商品类型0:实物商品 1：优惠券 2：抵价券
		params.addBodyParameter("shopNum", shopnum);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.CREATE_ORDER,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(BuyActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(BuyActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<OrderBean> type = new TypeToken<OrderBean>() {
								};
								OrderBean baseBean = gson.fromJson(result,
										type.getType());
								switch (baseBean.typecode) {
									case "-1":
										Toast.makeText(BuyActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										final Intent intent = new Intent(
												BuyActivity.this, PayActivity.class);
										intent.putExtra("shopgoodname",
												shopgoodname);
										intent.putExtra("shopgoodprice",
												shopgoodprice);
										intent.putExtra("goodnum", tv_shopgoodsum
												.getText().toString());// 商品
										intent.putExtra("orderid", baseBean.pubCode);
										Toast.makeText(BuyActivity.this, "订单提交成功",
												Toast.LENGTH_SHORT).show();
										startActivity(intent);
										finish();
										break;
									default:
										Toast.makeText(BuyActivity.this,
												"订单提交失败，请重试", Toast.LENGTH_SHORT)
												.show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(BuyActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}

					}

				});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(BuyActivity.this, WashDeatilActivity.class);
		startActivity(intent);
		BuyActivity.this.finish();
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
