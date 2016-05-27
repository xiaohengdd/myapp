package com.weixin.paydemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.activity.PurchaseDetailActivity;
import com.cac.machehui.client.activity.TuangouOrLiquanListActivity;
import com.cac.machehui.client.activity.WashDeatilActivity;

public class PaySuccessActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private TextView tv_goog_name;
	private TextView tv_goog_id;
	private Button btn_browse;
	private Button btn_bug_again;
	private boolean fromPayment;
	/*********** 商品名 ************/
	private String goodName;
	/*********** 商品数量 ************/
	private String goodNum;
	/*********** 商品id ************/
	private String goodID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paysuccess);
		iniView();
		iniData();
	}

	private void iniData() {
		getValueSp();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			fromPayment = bundle.getBoolean("fromPayment");
		}
		String content;
		if (fromPayment) {
			content = goodName + " 线上付款" + "(" + goodNum + "份)";
			btn_browse.setVisibility(View.GONE);
			btn_bug_again.setVisibility(View.GONE);
		} else {
			content = goodName + "(" + goodNum + "份)";
			btn_browse.setVisibility(View.VISIBLE);
			btn_bug_again.setVisibility(View.VISIBLE);
		}
		tv_title.setText("购买成功");
		tv_goog_name.setText(content);
		tv_goog_id.setText(goodID);
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_goog_name = (TextView) findViewById(R.id.good_name_tv_success);
		tv_goog_id = (TextView) findViewById(R.id.good_id_tv_success);
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		btn_browse = (Button) findViewById(R.id.browse_btn_success);
		btn_browse.setOnClickListener(this);
		btn_bug_again = (Button) findViewById(R.id.bug_again_btn_success);
		btn_bug_again.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				intent = new Intent(this, PurchaseDetailActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.browse_btn_success:
				intent = new Intent(this, TuangouOrLiquanListActivity.class);
				intent.putExtra("tag", "tuangouquan");
				startActivity(intent);
				break;
			case R.id.bug_again_btn_success:// 再次购买
				intent = new Intent(this, BuyActivity.class);
				startActivity(intent);
				break;
		}

	}

	/**
	 * 获取提交订单存的值
	 */
	private void getValueSp() {
		SharedPreferences sp = getSharedPreferences("currentOrder",
				Context.MODE_PRIVATE);
		goodName = sp.getString("goodName", "");
		goodNum = sp.getString("goodNum", "");
		goodID = sp.getString("goodID", "");
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		if (fromPayment) {
			intent.setClass(this, WashDeatilActivity.class);
		} else {
			intent.setClass(this, PurchaseDetailActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
