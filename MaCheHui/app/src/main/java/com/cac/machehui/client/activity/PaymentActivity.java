package com.cac.machehui.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.OrderBean;
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
import com.weixin.paydemo.PayActivity;

/**
 * 提交订单
 */
public class PaymentActivity extends BaseActivity implements OnClickListener {
	/********** 标题 ***********/
	private TextView tv_title;
	/********** 左上角返回 ***********/
	private ImageButton ib_return;
	/********** 携带数据 ***********/
	private Intent intent;
	/********** 输入金额 ***********/
	private EditText et_input_pay;
	/********** 优惠券，暂时没有 ***********/
	private TextView tv_youhuiquan;
	/********** 是否使用优惠券 ***********/
	private CheckBox cb_use_youhuiquan;
	/********** 实际 的付款金额 ***********/
	private TextView tv_actually_pay;
	/********** 提交订单 ***********/
	private Button btn_confrim;
	/********** 支付金额 ***********/
	private String pay;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String userId;
	private String token;
	/********** 商品编号 ***********/
	private String shopNum;
	/********** 商品名称 ***********/
	private String shopGoodName;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_activity);
		initView();
		initData();
	}

	private void initData() {
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (intent != null) {
			String title = intent.getStringExtra("title");
			tv_title.setText(title);
			if (bundle != null) {
				shopGoodName = bundle.getString("shopName");
				shopNum = bundle.getString("shopid");
			}
		}
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		et_input_pay = (EditText) findViewById(R.id.input_pay_et_payment);
		et_input_pay.addTextChangedListener(new MyTextWatcher());
		tv_youhuiquan = (TextView) findViewById(R.id.youhuiquan_tv_payment);
		cb_use_youhuiquan = (CheckBox) findViewById(R.id.youhuiquan_use_cb_payment);// 后期有优惠券的时候显示
		tv_actually_pay = (TextView) findViewById(R.id.actually_pay_tv_payment);
		btn_confrim = (Button) findViewById(R.id.confirm_btn_payment);
		btn_confrim.setOnClickListener(this);
		setPricePoint(et_input_pay);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.confirm_btn_payment:
				userId = sp.getString("userid", "");
				if (TextUtils.isEmpty(userId)) {
					Intent intent = new Intent(PaymentActivity.this,
							LoginActivity.class);
					startActivity(intent);
					break;
				}
				pay = et_input_pay.getText().toString();
				if (TextUtils.isEmpty(pay)) {
					Toast.makeText(this, "请输入付款金额", Toast.LENGTH_SHORT).show();
					break;
				} else if (!NetworkUtil.hasInternetConnected(this)) {
					Toast.makeText(PaymentActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_SHORT).show();
					break;
				}
				getDataFromServer();
				break;
		}
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("shopid", shopNum);// 商品编号
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("orderType", "1");// 0：团购 1:先消费后付款
		params.addBodyParameter("shopPrice", pay);// 商品价格
		params.addBodyParameter("discountPrice", pay);// 商品价格
		params.addBodyParameter("shopgoodNum", "1");// 商品数量
		params.addBodyParameter("shopgoodType", "1");// 商品类型0:实物商品 1：优惠券 2：抵价券
		// params.addBodyParameter("shopgoodNum", shopNum);
		params.addBodyParameter("shopNum", shopNum);
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
						Toast.makeText(PaymentActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PaymentActivity.this, "返回数据异常",
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
										Toast.makeText(PaymentActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										final Intent intent = new Intent(
												PaymentActivity.this,
												PayActivity.class);
										intent.putExtra("shopgoodname",
												shopGoodName);
										intent.putExtra("shopgoodprice", pay);
										intent.putExtra("goodnum", "1");// 商品
										intent.putExtra("orderid", baseBean.pubCode);
										intent.putExtra("fromPayment", true);
										Toast.makeText(PaymentActivity.this,
												"订单提交成功", Toast.LENGTH_SHORT)
												.show();
										startActivity(intent);
										finish();
										break;
									default:
										Toast.makeText(PaymentActivity.this,
												"订单提交失败，请重试", Toast.LENGTH_SHORT)
												.show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PaymentActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
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

	/**
	 * 设置两位小数
	 *
	 * @param editText
	 *            文本编辑框
	 */
	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});

	}

	class MyTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
								  int arg3) {
			tv_actually_pay.setText(arg0);// 当前没有优惠券，如果有的话要减去优惠券的价格
		}
	}
}
