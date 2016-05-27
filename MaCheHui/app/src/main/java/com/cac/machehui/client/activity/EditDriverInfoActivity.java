package com.cac.machehui.client.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.city.GetAddressInfoActivity;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.entity.MyCar;
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
 * 编辑车辆信息
 */
public class EditDriverInfoActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_title;
	/*********** 查询违章 *************/
	private TextView tv_wei_zhang;
	private ImageButton ib_return;
	private RelativeLayout rl_car_num;
	private RelativeLayout rl_user_name;
	private RelativeLayout rl_jia_num;
	private RelativeLayout rl_engine;
	private RelativeLayout rl_km;
	private TextView tv_brand;
	private TextView tv_modle;
	private TextView tv_car_num;
	private TextView tv_user_name;
	private TextView tv_jia_num;
	private TextView tv_engine;
	private TextView tv_km;
	private Button btn_delete;
	private MyCar myCar;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String userId;
	private String token;
	private SharedPreferences sp;
	/************ 区分类型，0车牌号，1车主姓名，2车架号，3发动机号，4行驶公里数 *********************/
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_driver_info);
		iniView();
		iniData();
	}

	private void iniData() {
		Intent intent = getIntent();
		myCar = (MyCar) intent.getSerializableExtra("myCar");
		if (myCar != null) {
			tv_brand.setText(myCar.cartype + "  " + myCar.carmodle);
			tv_modle.setText(myCar.carjutimodle);
			tv_car_num.setText(myCar.carnumber);
			tv_user_name.setText(myCar.carownername);
			tv_engine.setText(myCar.engineno);
			tv_jia_num.setText(myCar.vin);
			tv_km.setText(myCar.kilometres);
		}
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
	}

	private void iniView() {
		rl_car_num = (RelativeLayout) findViewById(R.id.car_num_rl_edit);
		rl_car_num.setOnClickListener(this);
		rl_user_name = (RelativeLayout) findViewById(R.id.car_name_rl_edit);
		rl_user_name.setOnClickListener(this);
		rl_jia_num = (RelativeLayout) findViewById(R.id.jia_num_rl_edit);
		rl_jia_num.setOnClickListener(this);
		rl_engine = (RelativeLayout) findViewById(R.id.engine_num_rl_edit);
		rl_engine.setOnClickListener(this);
		rl_km = (RelativeLayout) findViewById(R.id.km_rl_edit);
		rl_km.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_wei_zhang = (TextView) findViewById(R.id.wei_zhang_tv_edit_driver);
		tv_wei_zhang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditDriverInfoActivity.this,
						GetAddressInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("fromEdit", 1);
				bundle.putString("carNumber", tv_car_num.getText().toString());
				bundle.putString("engineno", tv_engine.getText().toString());
				bundle.putString("classno", tv_jia_num.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		tv_title.setText("车辆信息");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_brand = (TextView) findViewById(R.id.driver_type_tv_edit);
		tv_modle = (TextView) findViewById(R.id.driver_modle_tv_edit);
		tv_car_num = (TextView) findViewById(R.id.driver_num_tv_edit);
		tv_user_name = (TextView) findViewById(R.id.driver_name_tv_edit);
		tv_engine = (TextView) findViewById(R.id.driver_engine_tv_edit);
		tv_km = (TextView) findViewById(R.id.km_et_driver_tv_edit);
		tv_jia_num = (TextView) findViewById(R.id.driver_jia_tv_edit);
		btn_delete = (Button) findViewById(R.id.delete_btn_edit);
		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDeleteCarDialog();
			}
		});

	}

	/***
	 * 显示删除车辆dialog
	 */
	private void showDeleteCarDialog() {
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		View view = LayoutInflater.from(this).inflate(
				R.layout.delete_car_dialog, null);
		dialog.setContentView(view);
		TextView tv_cancle = (TextView) view
				.findViewById(R.id.cancle_tv_delete);
		tv_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		final TextView tv_confirm = (TextView) view
				.findViewById(R.id.confirm_tv_delete);
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (NetworkUtil
						.hasInternetConnected(EditDriverInfoActivity.this)) {
					deleteCar();
				} else {
					Toast.makeText(EditDriverInfoActivity.this,
							"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(EditDriverInfoActivity.this, UpdateInfoActivity.class);
		String title = null;
		String content = null;
		switch (v.getId()) {
			case R.id.car_num_rl_edit:
				type = 0;
				content = tv_car_num.getText().toString();
				title = "车牌号";
				break;
			case R.id.car_name_rl_edit:
				type = 1;
				content = tv_user_name.getText().toString();
				title = "车主姓名";
				break;
			case R.id.jia_num_rl_edit:
				content = tv_jia_num.getText().toString();
				type = 2;
				title = "车架号";
				break;
			case R.id.engine_num_rl_edit:
				type = 3;
				content = tv_engine.getText().toString();
				title = "发动机号";
				break;
			case R.id.km_rl_edit:
				type = 4;
				content = tv_km.getText().toString();
				title = "行驶公里数";
				break;
		}
		intent.putExtra("title", title);
		intent.putExtra("content", content);
		intent.putExtra("type", type);
		intent.putExtra("id", myCar.id);
		startActivityForResult(intent, type);
	}

	/**
	 * 调用接口，删除车辆
	 */
	private void deleteCar() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("method", "isdelete");
		params.addBodyParameter("token", token);
		params.addBodyParameter("value", "1");
		params.addBodyParameter("id", myCar.id);
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.DELETE_CAR,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(EditDriverInfoActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(EditDriverInfoActivity.this,
									"返回数据异常", Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean baseBean = gson.fromJson(result,
										type.getType());
								switch (baseBean.typecode) {
									case "-1":
										Toast.makeText(EditDriverInfoActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "0":
										Toast.makeText(EditDriverInfoActivity.this,
												"删除失败", Toast.LENGTH_SHORT).show();
										break;
									case "1":
										Toast.makeText(EditDriverInfoActivity.this,
												"删除成功", Toast.LENGTH_SHORT).show();
										EditDriverInfoActivity.this.finish();
										break;
									default:
										Toast.makeText(EditDriverInfoActivity.this,
												"系统错误", Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(EditDriverInfoActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}

					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String value = data.getStringExtra("data");
			switch (requestCode) {
				case 0:
					tv_car_num.setText(value);
					break;
				case 1:
					tv_user_name.setText(value);
					break;
				case 2:
					tv_jia_num.setText(value);
					break;
				case 3:
					tv_engine.setText(value);
					break;
				case 4:
					tv_km.setText(value);
					break;
			}
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
