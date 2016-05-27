package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mobstat.StatService;
import com.cac.machehui.R;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.entity.VersionBean;
import com.cac.machehui.client.task.CheckVersion;
import com.cac.machehui.client.task.CheckVersion.CheckVersionListener;
import com.cac.machehui.client.task.CheckVersion.IntentToHome;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.utils.PasswordEncoder;
import com.cac.machehui.client.utils.ShareUtil;
import com.cac.machehui.client.utils.XGpushNotice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class WelcomeActivity extends BaseActivity implements BDLocationListener {
	private int UpdateReqCode = 2015;
	// 定位的对象
	private LocationClient client;
	private static final String tag = "MainActivity";
	private SharedPreferences sp;
	int flag = 1;// 判断是否跳转到首页
	private HttpUtils httputils;
	// 获取首次进入时候的数据
	private final int FIRSTCODE = 0;
	AlphaAnimation alphaAnimation;
	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (FIRSTCODE == msg.what) {
				ArrayList<String> homeView = (ArrayList<String>) msg.obj;
				if (homeView != null) {
					AppClient.homeView = homeView;
					bdlocation();
				}
			} else if (1 == msg.what) {
				if (NetworkUtil.hasInternetConnected(WelcomeActivity.this)) {
					isTime();
				} else {
					Toast.makeText(WelcomeActivity.this, "您的网络离家出走了，请检查重试",
							Toast.LENGTH_LONG).show();
				}
			}
		};
	};

	private ImageView view;
	private CheckVersion checkVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_activity_splash);
		bdlocation();
		StatService.setAppChannel(this, "RepleceWithYourChannel", true);
		view = (ImageView) findViewById(R.id.splash_iv);
		alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this,
				R.anim.splash_anim_fade);
		view.setAnimation(alphaAnimation);
		httputils = new HttpUtils();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetworkUtil.hasInternetConnected(this)) {
			LoadNetData();
			autoLogin();
		} else {
			Toast.makeText(WelcomeActivity.this, "您的网络离家出走了，请检查重试",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void autoLogin() {
		// 获得sp信息
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);

		String username = sp.getString("usernames", "");
		String password = sp.getString("password", "");
		String auto = sp.getString("auto", "true");

		if ((!username.equals("") && !password.equals(""))
				&& auto.equals("true")) {
			Login(username, password);
		} else {
			XGpushNotice.ordinaryPush(getApplicationContext());
		}
	}

	private void Login(String number, final String password) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", number);
		String md5Pwd = PasswordEncoder.encode(password);
		params.addBodyParameter("password", md5Pwd);
		httpUtils.configResponseTextCharset("UTF-8");
		httpUtils.send(HttpMethod.POST, URLCst.LOGIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							switch (typeCode) {
								case "1":
									Gson gson = new Gson();
									TypeToken<User> type = new TypeToken<User>() {
									};
									User user = gson.fromJson(jsonObject
											.getJSONObject("user").toString(), type
											.getType());
									if (user != null) {
										XGpushNotice.useridPush(
												getApplicationContext(),
												user.getUserid());
										sp = WelcomeActivity.this
												.getSharedPreferences(
														"currentUser",
														Context.MODE_PRIVATE);
										// 登录后存储信息 数据要存储完全，个人资料里面要用
										SharedPreferences.Editor editor = sp.edit();

										editor.putString("id", user.getId());
										editor.putString("usernames",
												"" + user.getUsername());
										editor.putString("headUrl",
												user.getUserheadurl());
										editor.putString("password", password);
										editor.putString("nickname",
												user.getNickname());
										editor.putString("vipIdentify",
												user.getVip());
										editor.putString("sex", user.getUsersex());
										editor.putString("bundWx",
												user.getWeixintype());
										editor.putString("userId", user.getUserid());
										editor.putString("userid", user.getUserid());
										editor.putString("token", user.getToken());
										// AppClient.token = user.getToken();
										editor.commit();
									}
									break;
								case "2":
									Toast.makeText(WelcomeActivity.this,
											"密码错误，自动登录失败", Toast.LENGTH_SHORT)
											.show();
									break;
								case "3":
									Toast.makeText(WelcomeActivity.this,
											"用户名错误，自动登录失败", Toast.LENGTH_SHORT)
											.show();
									break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(WelcomeActivity.this, "解析异常，自动登录失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void bdlocation() {
		// 定位信息
		// 1. 初始化LocationClient类
		client = new LocationClient(getApplicationContext());

		// 3. 注册监听函数
		client.registerLocationListener(this);
		// 4. 设置参数
		LocationClientOption locOption = new LocationClientOption();
		locOption.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		locOption.setCoorType("bd09ll");// 设置定位结果类型
		locOption.setScanSpan(5000);// 设置发起定位请求的间隔时间,ms
		locOption.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		locOption.setNeedDeviceDirect(true);// 设置返回结果包含手机的方向
		client.setLocOption(locOption);
		// 5. 开启/关闭 定位SDK
		client.start();

	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// 将定位信息保存到全局变量中 需要保存好
		AppClient.getInstance().setLocation(arg0);

		if (1 == flag) {
			flag = 2;
			Message message = Message.obtain();
			message.what = 1;
			handler.sendMessage(message);
		}
	}

	public void isTime() {
		boolean b = ShareUtil.getBooleanData(getApplicationContext(),
				"is_first", true);
		if (b) {
			// 第一次进入，进入导航页，is_first改成false
			Log.i(tag, "这是第一次进入页面");
			Intent intent = new Intent(WelcomeActivity.this,
					GuideActivity.class);
			startActivity(intent);
			finish();
			ShareUtil.saveBooleanData(getApplicationContext(), "is_first",
					false);
		} else {
			// 第二次进入，进入详情页
			Log.i(tag, "这是第二次进入页面");
			checkVersion();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
								 final Intent data) {
		finish();
		// if (requestCode == UpdateReqCode) {
		// Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
		// startActivity(intent);
		// finish();
		// }
	}

	/**
	 * 检查版本更新
	 */
	private void checkVersion() {
		checkVersion = new CheckVersion(this, new CheckVersionListener() {

			@Override
			public void toUpdate(VersionBean newVersionBean, String savePath) {
				if (!isActivityRunning(WelcomeActivity.this,
						VersionUpdateActivity.class.getName())) {
					Intent intent = new Intent(WelcomeActivity.this,
							VersionUpdateActivity.class);
					intent.putExtra("type", 1);
					intent.putExtra("savePath", savePath);
					intent.putExtra("bean", newVersionBean);
					startActivityForResult(intent, UpdateReqCode);
				}
			}
		}, new IntentToHome() {

			@Override
			public void toHome() {
				Intent intent = new Intent(WelcomeActivity.this,
						HomeActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		});
		checkVersion.checkUpdate();
	}

	// 加载界面加载网络数据
	private void LoadNetData() {
		httputils.send(HttpMethod.GET, URLCst.IMG_LIST,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Message message = Message.obtain();
						message.what = FIRSTCODE;
						handler.sendMessage(message);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ArrayList<String> homeView = new ArrayList<String>();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONArray array = jsonObject
									.getJSONArray("homeimglist");
							int count = array.length();
							for (int i = 0; i < count; i++) {
								JSONObject obj = (JSONObject) array.get(i);
								homeView.add(obj.getString("homeimg"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Message message = Message.obtain();
						message.obj = homeView;
						message.what = FIRSTCODE;
						handler.sendMessage(message);

					}
				});
	}

	/**
	 * 判断某个activity 时候后台运行
	 *
	 * @param mContext
	 * @param activityClassName
	 *            要判断的activity 的class
	 * @return
	 */
	public static boolean isActivityRunning(Context mContext,
											String activityClassName) {
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
		if (info != null && info.size() > 0) {
			ComponentName component = info.get(0).topActivity;
			if (activityClassName.equals(component.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
		if (checkVersion != null) {
			checkVersion.cancle();
		}
	}
}
