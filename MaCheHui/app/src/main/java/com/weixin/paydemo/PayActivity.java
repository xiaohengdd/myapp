package com.weixin.paydemo;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.demo.PayDemoActivity;
import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
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
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 支付页面
 */
public class PayActivity extends BaseActivity implements
		OnCheckedChangeListener {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	private Handler myhandler;
	private PayReq req;
	private Map<String, String> resultunifiedorder;
	private StringBuffer sb;
	private final int prepay_id = 0;
	private String shopgoodname;// 商品名字
	private String shopgoodprice;// 商品价格
	private String orderid;// 商品订单号
	private String goodNum;// 商品的数量
	private int total;
	@ViewInject(R.id.pay_shopgoodname)
	private TextView pay_shopgoodname;
	@ViewInject(R.id.pay_shopgoodprice)
	private TextView pay_shopgoodprice;
	@ViewInject(R.id.pay_total)
	private TextView pay_total;
	/******** 取消订单 *********/
	@ViewInject(R.id.cancle_order_tv_pay)
	private TextView tv_cancle_order;
	/******** 微信支付 *********/
	private CheckBox cb_weixin;
	/******** 支付宝支付 *********/
	private CheckBox cb_zhifubao;
	private Button payBtn;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private IWXAPI msgApi;
	private String token;
	/********* 是否来自线上付款 ************/
	private boolean fromPayment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		ViewUtils.inject(this);
		msgApi = WXAPIFactory.createWXAPI(this, null);
		SharedPreferences sp = getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);
		token = sp.getString("token", "");
		Intent intent = this.getIntent();
		shopgoodname = intent.getStringExtra("shopgoodname");
		shopgoodprice = intent.getStringExtra("shopgoodprice");
		orderid = intent.getStringExtra("orderid");
		goodNum = intent.getStringExtra("goodnum");
		fromPayment = intent.getBooleanExtra("fromPayment", false);
		AppClient.fromPayment = fromPayment;
		saveToSp();
		if (shopgoodprice.contains(".")) {
			float floatTotal = Float.parseFloat(shopgoodprice);
			total = (int) (floatTotal * 100);
		} else {
			total = Integer.parseInt(shopgoodprice);
			total *= 100;
		}
		pay_shopgoodname.setText(shopgoodname);
		pay_shopgoodprice.setText(shopgoodprice + "元");
		pay_total.setText(shopgoodprice + "元");
		req = new PayReq();
		sb = new StringBuffer();
		findview();

		// Handler管理
		myhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					// 得到prepay_id
					case prepay_id:
						genPayReq();
						break;

				}
				super.handleMessage(msg);
			}

		};

		msgApi.registerApp(Constants.APP_ID);
		// 生成prepay_id
		payBtn = (Button) findViewById(R.id.unifiedorder_btn);
		payBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cb_weixin.isChecked()
						&& NetworkUtil.hasInternetConnected(PayActivity.this)) {
					GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
					getPrepayId.execute();
				} else if (cb_zhifubao.isChecked()
						&& NetworkUtil.hasInternetConnected(PayActivity.this)) {
					Intent intent = new Intent();
					intent.setClass(PayActivity.this, PayDemoActivity.class);
					intent.putExtra("orderid", orderid);
					intent.putExtra("shopgoodprice", shopgoodprice);
					intent.putExtra("shopgoodname", shopgoodname);
					startActivity(intent);
				} else {
					Toast.makeText(PayActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		tv_cancle_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkUtil.hasInternetConnected(PayActivity.this)) {
					cancleOrderFromServer();
				} else {
					Toast.makeText(PayActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 将支付成功后需要使用的值保存
	 */
	private void saveToSp() {
		SharedPreferences sp = getSharedPreferences("currentOrder",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("goodName", shopgoodname);
		editor.putString("goodNum", goodNum);
		editor.putString("goodID", orderid);
		editor.commit();
	}

	/**
	 * 取消订单
	 */
	protected void cancleOrderFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid", orderid);
		params.addBodyParameter("token", token);
		params.addBodyParameter("shopgoodStatus", "8");// 已取消
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.CANCLE_ORDERA,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(PayActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PayActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean scoreBean = gson.fromJson(result,
										type.getType());
								switch (scoreBean.typecode) {
									case "-1":
										Toast.makeText(PayActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										Toast.makeText(PayActivity.this, "订单取消成功",
												Toast.LENGTH_SHORT).show();
										PayActivity.this.finish();
										break;
									default:
										Toast.makeText(PayActivity.this,
												"订单取消失败，请重试", Toast.LENGTH_SHORT)
												.show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PayActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	private void findview() {
		cb_weixin = (CheckBox) findViewById(R.id.weixin_pay_cb_payment);
		cb_weixin.setOnCheckedChangeListener(this);
		cb_zhifubao = (CheckBox) findViewById(R.id.zhifubao_pay_cb_payment);
		cb_zhifubao.setOnCheckedChangeListener(this);
		// 返回按钮
		ImageButton pay_back_button = (ImageButton) findViewById(R.id.pay_back_button);
		pay_back_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				onBackPressed();
			}
		});
	}

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase(Locale.getDefault());
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase(Locale.getDefault());
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(PayActivity.this,
					getString(R.string.app_tip),
					getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (result == null) {
				Toast.makeText(PayActivity.this, "未知错误", Toast.LENGTH_SHORT)
						.show();
				PayActivity.this.finish();
			} else if (TextUtils.isEmpty(result.get("prepay_id"))) {
				Toast.makeText(PayActivity.this, "微信支付失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
				myhandler.sendEmptyMessage(prepay_id);
				resultunifiedorder = result;
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);
			Map<String, String> xml = null;
			if (buf != null) {
				String content = new String(buf);
				Log.e("orion", content);
				xml = decodeXml(content);
			}
			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:

						if ("xml".equals(nodeName) == false) {
							// 实例化student对象
							xml.put(nodeName, parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams
					.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", shopgoodname));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					URLCst.CAR_HOST + " /appInterface/getweixin"));
			packageParams.add(new BasicNameValuePair("out_trade_no", orderid));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", total + ""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);
			return new String(xmlstring.toString().getBytes(), "ISO8859-1");
		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	private void genPayReq() {
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		sendPayReq();

		Log.e("orion", signParams.toString());
		PayActivity.this.finish();
	}

	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);

	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
		switch (cb.getId()) {
			case R.id.weixin_pay_cb_payment:
				selectPayStyle(cb_zhifubao, isChecked);
				break;
			case R.id.zhifubao_pay_cb_payment:
				selectPayStyle(cb_weixin, isChecked);
				break;
		}
	}

	private void selectPayStyle(CheckBox cb, boolean isChecked) {
		if (isChecked) {
			payBtn.setClickable(true);
			payBtn.setBackground(getResources().getDrawable(
					R.drawable.login_btn_selector));
			cb.setChecked(false);
		} else if (!cb.isChecked()) {
			payBtn.setBackground(getResources().getDrawable(
					R.drawable.person_04));
			payBtn.setClickable(false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
	}
}
