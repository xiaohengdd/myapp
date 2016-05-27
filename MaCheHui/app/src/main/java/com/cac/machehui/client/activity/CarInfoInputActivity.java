package com.cac.machehui.client.activity;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.city.GetAddressInfoActivity;
import com.cac.machehui.client.entity.CityItem;
import com.cac.machehui.client.view.LodingDialog;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 违章查询
 */
public class CarInfoInputActivity extends BaseActivity implements
		OnClickListener {
	private Button submit;

	private EditText carno_et;
	private EditText fadongji_et;
	private EditText chejiahao_et;
	private TextView queryCity;
	/*********** 车牌号 *****************/
	private String carnumber = "";
	/*********** 发动机号 *****************/
	private String fadongji = "";
	/*********** 车架号 *****************/
	private String chejiahao = "";
	/*********** 城市名称 *****************/
	private String cityName = "";
	/*********** 城市编号 *****************/
	private String cityID = "";
	private Button carinfo_input_back;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private CityItem cityItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carinfo_input_layout);
		initView();
	}

	private void initView() {
		carinfo_input_back = (Button) findViewById(R.id.carinfo_input_back);
		carno_et = (EditText) findViewById(R.id.carno_value);
		fadongji_et = (EditText) findViewById(R.id.fadongji_value);
		chejiahao_et = (EditText) findViewById(R.id.chejiahao_value);
		queryCity = (TextView) findViewById(R.id.city_value);
		submit = (Button) findViewById(R.id.submit);
		carinfo_input_back.setOnClickListener(this);
		queryCity.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			cityName = data.getStringExtra("city");
			cityID = data.getStringExtra("cityId");
			cityItem = (CityItem) data.getSerializableExtra("cityItem");
			queryCity.setText(cityName);
		} else {
			queryCity.setText("请选择");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.carinfo_input_back:
				finish();
				break;
			case R.id.city_value:
				startActivityForResult(new Intent(CarInfoInputActivity.this,
						GetAddressInfoActivity.class), 10000);
				break;
			case R.id.submit:
				carnumber = carno_et.getText().toString();
				fadongji = fadongji_et.getText().toString();
				chejiahao = chejiahao_et.getText().toString();
				cityName = queryCity.getText().toString();
				if (carnumber.equals("") || chejiahao.equals("")
						|| fadongji.equals("") || cityName.equals("请选择")) {
					Toast.makeText(CarInfoInputActivity.this, "请将信息填写完整",
							Toast.LENGTH_SHORT).show();
					break;
				}
				carnumber = carnumber.toUpperCase(Locale.getDefault());
				fadongji = fadongji.toUpperCase(Locale.getDefault());
				chejiahao = chejiahao.toUpperCase(Locale.getDefault());
				// Intent intent = new Intent(this, BreakRulesResultActivity.class);
				// intent.putExtra("carNumber", carnumber);
				// intent.putExtra("cityItem", cityItem);
				// intent.putExtra("engineno", fadongji);
				// intent.putExtra("classno", chejiahao);
				// startActivity(intent);
				Intent intent = new Intent(this, PeccancyQueryActivity.class);
				intent.putExtra("carno", carnumber);
				intent.putExtra("cityid", cityID);
				intent.putExtra("fadongji", fadongji);
				intent.putExtra("chejiahao", chejiahao);
				startActivity(intent);
				// carnumber = carnumber.toUpperCase(Locale.getDefault());
				// fadongji = fadongji.toUpperCase(Locale.getDefault());
				// chejiahao = chejiahao.toUpperCase(Locale.getDefault());
				// if (NetworkUtil.hasInternetConnected(this)) {
				// // getDataFromServer();
				// } else {
				// Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
				// .show();
				// }
				break;
		}
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
