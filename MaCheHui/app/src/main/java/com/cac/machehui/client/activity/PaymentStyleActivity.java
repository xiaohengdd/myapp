package com.cac.machehui.client.activity;

import android.content.Intent;
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

/**
 * 提交订单
 */
public class PaymentStyleActivity extends BaseActivity implements
		OnClickListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_activity);
		initView();
		initData();
	}

	private void initData() {
		intent = getIntent();
		if (intent != null) {
			String title = intent.getStringExtra("title");
			tv_title.setText(title);
		}
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.confirm_btn_payment:
				pay = et_input_pay.getText().toString();
				if (TextUtils.isEmpty(pay)) {
					Toast.makeText(this, "请输入付款金额", Toast.LENGTH_SHORT).show();
				} else {
					// // 提交服务器页面跳转
					// Intent intent = new Intent();
					// intent.setClass(PaymentStyleActivity.this, cls);
					// startActivity(intent);

				}
				break;
		}
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
