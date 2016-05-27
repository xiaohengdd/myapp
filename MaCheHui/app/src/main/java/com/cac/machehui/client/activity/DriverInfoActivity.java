package com.cac.machehui.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.city.GetAddressInfoActivity;
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

//车辆信息
public class DriverInfoActivity extends BaseActivity {

	private TextView tv_type;
	private TextView tv_modle;
	/********** 品牌车型 ********/
	private String cartype;
	/********** 车系 ********/
	private String carLine;
	/********** 车型 ********/
	private String cartModel;
	/********** 行驶公里数 ********/
	private String carkm;// 车型
	private HttpUtils httpUtils;
	private RelativeLayout rl_car_style;
	private ImageView iv_return;

	// 车型的View
	private View tv_typeView;
	//
	private View iv_driverView;
	Button imagebutton;// 选择车型的图片按钮
	AppClient app;// 全局变量
	TextView textview_city;
	String cityname;// 城市
	String userId;
	/******************* 车牌号 ***************/
	@ViewInject(R.id.mine_driver_card)
	private EditText edittext_carnumber;
	String carnumber;
	/******************* 车主姓名 ***************/
	@ViewInject(R.id.mine_driver_user)
	private EditText edittext_carownername;
	/******************* 行驶公里数 ***************/
	@ViewInject(R.id.km_et_driver_info)
	private EditText et_km;
	String carownername;
	/******************* 车架号 ***************/
	@ViewInject(R.id.mine_driver_card_num)
	private EditText edittext_vin;
	String vin;
	/******************* 发动机号 ***************/
	@ViewInject(R.id.mine_driver_engine_num)
	private EditText edittext_engineno;
	String engineno;
	/******************* 添加车辆的按钮 ***************/
	@ViewInject(R.id.addmycar)
	private Button addmycar;

	private SharedPreferences sp;
	private LodingDialog lodingDialog;
	private HttpHandler<String> httpHandler;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_info_activity);
		ViewUtils.inject(this);
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
		// 初始化控件
		initRes();
		addmycar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getData();
				if (!TextUtils.isEmpty(cartModel)
						&& !TextUtils.isEmpty(carnumber)
						&& !TextUtils.isEmpty(carownername)
						&& !TextUtils.isEmpty(vin)
						&& !TextUtils.isEmpty(engineno)
						&& !TextUtils.isEmpty(carkm)) {
					if (NetworkUtil
							.hasInternetConnected(DriverInfoActivity.this)) {
						insertCar();
					} else {
						Toast.makeText(DriverInfoActivity.this,
								"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(DriverInfoActivity.this, "请填写完整信息",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	// 获得输入车型的数据
	private void getData() {
		String carT = tv_type.getText().toString();
		if (!TextUtils.isEmpty(carT) && carT.contains("  ")) {
			String[] datas = carT.split("  ");
			if (datas.length >= 2) {
				cartype = datas[0];
				carLine = datas[1];
			}
		}
		cartModel = tv_modle.getText().toString();
		carnumber = edittext_carnumber.getText().toString();
		carownername = edittext_carownername.getText().toString();
		vin = edittext_vin.getText().toString();
		engineno = edittext_engineno.getText().toString();
		carkm = et_km.getText().toString();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String carModle = bundle.getString("carBrand") + "  "
						+ bundle.getString("line");
				tv_type.setText(carModle);
				tv_modle.setText(bundle.getString("modle"));
			}
		}
	}

	private void initRes() {
		iv_return = (ImageView) findViewById(R.id.mine_driver_add_deatail_btn_back);
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				returnMineDriverActivity();
			}
		});
		rl_car_style = (RelativeLayout) findViewById(R.id.style_rl_driver_info);
		rl_car_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(DriverInfoActivity.this,
						DriverListActivity.class), 10000);
			}
		});
		tv_type = (TextView) findViewById(R.id.mine_driver_type);
		tv_modle = (TextView) findViewById(R.id.driver_modle_tv_info);
		tv_type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		textview_city = (TextView) findViewById(R.id.mine_driver_carcityselect);
		textview_city.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DriverInfoActivity.this,
						GetAddressInfoActivity.class), 10000);

			}
		});
	}

	// 插入车型的网络访问
	public void insertCar() {
		httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("cartype", cartype);
		params.addBodyParameter("carmodle", carLine);
		params.addBodyParameter("carjutimodle", cartModel);
		params.addBodyParameter("cityname", "济南");
		params.addBodyParameter("vin", vin);
		params.addBodyParameter("engineno", engineno);
		params.addBodyParameter("token", token);
		params.addBodyParameter("kilometres", carkm);
		params.addBodyParameter("carownername", carownername);
		params.addBodyParameter("carnumber", carnumber);
		params.addBodyParameter("creater", "androidapp");
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.ADD_CAR, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(DriverInfoActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(DriverInfoActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean scoreBean = gson.fromJson(result,
										type.getType());
								switch (scoreBean.typecode) {
									case "200":
										Toast.makeText(DriverInfoActivity.this,
												"车辆添加成功", Toast.LENGTH_SHORT)
												.show();
										returnMineDriverActivity();
										break;
									case "-1":
										Toast.makeText(DriverInfoActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "199":
										Toast.makeText(DriverInfoActivity.this,
												"首次添加车辆成功，已获得50积分",
												Toast.LENGTH_SHORT).show();
										returnMineDriverActivity();
										break;
									default:
										Toast.makeText(DriverInfoActivity.this,
												"系统错误,车辆添加失败", Toast.LENGTH_SHORT)
												.show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(DriverInfoActivity.this, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		returnMineDriverActivity();
	}

	private void returnMineDriverActivity() {
		Intent intent = new Intent();
		intent.setClass(DriverInfoActivity.this, MineDriverActivity.class);
		startActivity(intent);
		finish();
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
