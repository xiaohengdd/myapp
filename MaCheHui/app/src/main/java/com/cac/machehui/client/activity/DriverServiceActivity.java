package com.cac.machehui.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cac.machehui.R;

/**
 * 代驾页面
 */
public class DriverServiceActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	private TextView tv_title;
	private ImageButton ib_return;
	private Button btn_call;
	private TextView tv_protocol;
	private CheckBox iv_choose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_service_activity);
		iniView();
	}

	private void iniView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("代驾");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		btn_call = (Button) findViewById(R.id.call_btn_driver);
		btn_call.setOnClickListener(this);
		tv_protocol = (TextView) findViewById(R.id.protocol_tv_driver);
		tv_protocol.setOnClickListener(this);
		iv_choose = (CheckBox) findViewById(R.id.iv_choose);
		iv_choose.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.call_btn_driver:
				callDriving();
				break;
			case R.id.protocol_tv_driver:
				Intent intent = new Intent(DriverServiceActivity.this,
						ProtocolActivity.class);
				intent.putExtra("type", "fromDriv");
				startActivity(intent);
				break;
		}
	}

	private void callDriving() {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + "4006850531"));
		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			btn_call.setClickable(true);
			btn_call.setBackground(getResources().getDrawable(
					R.drawable.login_btn_selector));
		} else {
			btn_call.setBackground(getResources().getDrawable(
					R.drawable.person_04));
			btn_call.setClickable(false);
		}
	}
}
