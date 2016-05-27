package com.alipay.sdk.pay.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.cst.URLCst;

public class PayDemoActivity extends BaseActivity {
	String orderid;// 自己服务器生成的订单�?
	String shopgoodprice; // �?要付钱数
	String shopgoodname; // 商品名字

	// 商户PID
	public static final String PARTNER = "2088021170393774";
	// 商户收款账号
	public static final String SELLER = "13156444404@qq.com";
	// 商户私钥，pkcs8格式
	// 自己生存的私钥public static final String RSA_PRIVATE =
	// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOPOofcRGVSwroBGI1ZgnVwN0CbIM27YaWKlxzI5qiJ2f9LiuDEmRPKCj98ZLWqNJ5sVzlzYp6DAB4S+pAyiTbKmCnZ2Aj++J0FO54f1rrK7E4zzdANnxKfqYug7Vdy80ROR2qMFTpM9m1K1OhYvC7J0YcdfffHWvSmMY+EAT2eBAgMBAAECgYEAjRg7SrpFdSXlPr+yPVqjvBM2G/8dlAbn5tiEsr/yd1vruaMAsDhitc1V7Lk8XuVEZZKw+OKbmD23qmklVEnmUpfJ0oYASVbsrmteMhGttCxQGu43EGD5aO7EeiziQR8SPdij+TesodmAOIutJQYilFVMkVm7/hKmjW+EvukOHkECQQDzxGy+IyC1DI7wEpp3CflgEHVb1Lh09A7pIelNhJ4yzbnnKEsdkIR0KgWm2VNa/x5ww68tXrOtBtbeGON1LBK5AkEA7z0sx2mqz+0yfJ8LuvnURD+/5NsFWKInpSJJzR0iWhXWCBoMGXqaYZGhI6i8gDV0vUjY9Jcv5JKrhKkV3SU3CQJASCbNoQG4vp32+OwouC9Flr+IXSP7bPKIL00QRxeH07W83sS4ZwnNiqyde6M73uVAV1Q0V5N+TyqFErv/J0FiUQJBANv6r0MJaXPZUhP/FoUtlT3QNH2V2rueFsAj4CKHiH+3Fl/Ku5duAah3hOjKDdIB1T77TerRXctaBfZsl1sKDikCQQCu4tvLgvFAjOOkoQ5bHtXxqGuDkAHXYwCBxSpyrp3liBTgIwi6f+CN66VUfWeskux3WLQwTn9ubpoIIa/PNI6y";
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALYsM84+Y+D/MeyNaIOJUXdg797fCN7mSdinXasikbkZ/PDMy9SQsn4QYtcfiWw6Xlel7HXpAu/FegI1xE1j1hGtkN5TC3NMl8qss4U6yc93LgDG3NuDgtag6RKubbvoapGepEMdYRqe36p4viRl+x/D1KUGYwHmoqAZRuAxzCXFAgMBAAECgYAlsgXSQnS2ZPf2o9ghh7OUyTk5W9thDDPxocgvFksjCy6cwTPYO0zD6y4Yp4zvGTDWYB4K0/loletGM8v325W63siUhjHiehdNSy/sw4dBRHlKNuKq+g7F/5BTgO9TIKF2gsyJ9HAuJJQk3H6dGhjPlatyU5femQt57oQVah3jYQJBAPGfw4Jfdc8N8iDFbHQrsq658kx57osyUtTPGcDNQabyPvFzvFEWMFfii1bvRKAnLii9n+gFXkc/s1EmrzrkJv0CQQDBAuthOrFRyMIELTmATEgX78lh7Fa6UzHEeAAw8qaefO0ql8rCDvLfBifcQPpzzlyrvUtmP4tneCmpkAFGlUhpAkBGjhFoA3oCMR9P5HZL4eq2TMPtAvneGZ7IV4U8oHMYTcqFdPc7clBBiXyIsSuH3IkFCoIkHVUvVvGEs9gdSTv9AkBL50cDxJ4pKIdMNzDOyLDtYOpUbSoqOj9vITYKCdMGruBiCLG/ITvYA0a3soIL+tKs41N8kW7UMQhJW4104Y5JAkAnhgHHVRcJ3GcuYZIMuGN4VMGEV7NJbJkvoJ817TrApvBrErbj1AMoy14noq/bywIXkEZmy0XEtgufK72pofkO";
	/* 私钥是对�? */
	// 支付宝公�?
	// public static final String RSA_PUBLIC =
	// "pvukqf41mvucgu5s416gexta7c1ttfiz";
	// 自己生存的弓腰public static final String RSA_PUBLIC =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjzqH3ERlUsK6ARiNWYJ1cDdAmyDNu2GlipccyOaoidn/S4rgxJkTygo/fGS1qjSebFc5c2KegwAeEvqQMok2ypgp2dgI/vidBTueH9a6yuxOM83QDZ8Sn6mLoO1XcvNETkdqjBU6TPZtStToWLwuydGHHX33x1r0pjGPhAE9ngQIDAQAB";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus
				// 为�??9000”则代表支付成功，具体状态码代表含义可参考接口文�?
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayDemoActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					PayDemoActivity.this.finish();
				} else {
					// 判断resultStatus 为非�?9000”则代表可能支付失败
					// �?8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，�?终交易是否成功以服务端异步�?�知为准（小概率状�?�）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayDemoActivity.this, "支付结果确认等待",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayDemoActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
						finish();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				/*
				 * Toast.makeText(PayDemoActivity.this, "�?查结果为�?" + msg.obj,
				 * 
				 * 
				 * Toast.LENGTH_SHORT).show();
				 */
				paypay();
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		paygoon();// �?测有无支付宝，然后�?�过handler进行支付
		Intent intent = this.getIntent();
		orderid = intent.getStringExtra("orderid");// 得到上一页的订单�?
		shopgoodprice = intent.getStringExtra("shopgoodprice");
		shopgoodname = intent.getStringExtra("shopgoodname");
	}

	private void paygoon() {
		Log.v("!!!", "到检测支付宝�?");
		// 构�?�PayTask 对象
		PayTask payTask = new PayTask(PayDemoActivity.this);
		// 调用查询接口，获取查询结�?
		// 查询终端是否存在支付�?
		boolean isExist = payTask.checkAccountIfExist();

		Message msg = new Message();
		msg.what = SDK_CHECK_FLAG;
		msg.obj = isExist;
		mHandler.sendMessage(msg);
		// TODO Auto-generated method stub

	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	// public void pay(View v) {Log.v("!!!", "到这�?1"); 原来的先没有删除
	public void paypay() {
		Log.v("!!!", "到这�?1");// 进行支付操作
		// 订单
		String orderInfo = getOrderInfo(shopgoodname, shopgoodname,
				shopgoodprice);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		Log.v("!!!", "到这�?2");
		try {
			Log.v("!!!", "到这�?3");
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
			Log.v("!!!", "到这�?4");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信�?
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();
		Log.v("!!!", "到这�?4");
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {

				// PayTask 对象主要为商户提供订单支付功能，查询该设备终端是
				// 否存在认证过�? 支付宝账户，及获取当前开发包版本号�??
				// 构�?�PayTask 对象
				PayTask alipay = new PayTask(PayDemoActivity.this);
				// 调用支付接口，获取支付结�?
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				Log.v("!!!", "这是结果" + result);
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账�?
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				Log.v("!!!", "到检测支付宝�?");
				// 构�?�PayTask 对象
				PayTask payTask = new PayTask(PayDemoActivity.this);
				// 调用查询接口，获取查询结�?
				// 查询终端是否存在支付�?
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本�?
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账�?
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单�?
		orderInfo += "&out_trade_no=" + "\"" + orderid + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步�?�知页面路径

		// orderInfo += "&notify_url=" + "\"" +
		// "http://notify.msp.hk/notify.htm"
		// + "\"";
		orderInfo += "&notify_url=" + "\"" + URLCst.CAR_HOST
				+ "/appInterface/getzhifubao" + "\"";

		// 服务接口名称�? 固定�?
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型�? 固定�?
		orderInfo += "&payment_type=\"1\"";

		// 参数编码�? 固定�?
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭�??
		// 取�?�范围：1m�?15d�?
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）�?
		// 该参数数值不接受小数点，�?1.5h，可转换�?90m�?
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支�?
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可�?
		orderInfo += "&return_url=\"http://www.macheka.com\"";

		// 调用银行卡支付，�?配置此参数，参与签名�? 固定�?
		// （需要签约�?�无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		/*
		 * String xmlstring =toXml(orderInfo); return new
		 * String(xmlstring.toString().getBytes(), "ISO8859-1");
		 */
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该�?�在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签�?
	 * 
	 * @param content
	 *            待签名订单信�?
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
