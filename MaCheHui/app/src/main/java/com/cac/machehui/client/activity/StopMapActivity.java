package com.cac.machehui.client.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cac.machehui.R;
import com.cac.machehui.client.cst.StopCst;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class StopMapActivity extends BaseActivity implements
		OnGetGeoCoderResultListener {

	private static final String TAG = "StopMapActivity";

	// 百度地图对应的视图
	private MapView mMapView;
	// 百度地图的操作的对象
	private BaiduMap mBaiduMap;

	// 定位的Client
	private LocationClient locationClient;
	// 定位的的监听事件
	private BDLocationListener locationListener;

	// 提醒
	private BDNotifyListener notifyListener;

	// 百度地图UI显示的界面
	private UiSettings mUiSettings;

	// 搜索模块，也可去掉地图模块独立使用
	private GeoCoder mSearch = null;

	// 振动器设备
	private Vibrator mVibrator;
	// 经度
	private double longitude;
	// 维度
	private double latitude;
	// 定位精度半径，单位是米
	private float radius;
	// 反地理编码
	private String addrStr;
	// 省份信息
	private String province;
	// 城市信息
	private String city;
	// 区县信息
	private String district;
	// 手机方向信息
	private float direction;
	// 街道
	private String street;
	// 街道号码
	private String streetNumber;
	// 经纬度转换卓坐标
	private LatLng location;

	// 搜索指定的周围的车辆
	private Button stopMapBtnCurrt;
	// 返回按钮
	private ImageView BtnBack;

	// 底部俩个控制的显示的层
	private RadioButton stopMapBtnP;
	private RadioButton stopMapBtnF;

	// 底部被隐藏的搜索的框
	private Button stopMapTvBottom;
	// 显示当前的位置的信息
	private TextView currtPosition;

	// 底部隐藏的搜索的框
	private LinearLayout stopMapBottomLL;

	// 保存从服务器返回过来的经纬度坐标
	private ArrayList<LatLng> listLatlng;

	// 保存当前界面的Bitmap的对象
	private ArrayList<BitmapDescriptor> bitmapDescriptor;

	// 使用xUtils中HttpsxUtlis访问网络
	private HttpUtils httpUtils;

	// 保存当前页面存在的Marker
	private List<Marker> listMarker;

	// 点击的时候出现的提示框
	private InfoWindow mInfoWindow;

	// 子线程返回主线程的标示
	private static final int GETlATLNGJSON = 1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				// 返回json数据成功
				case GETlATLNGJSON:

					break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stop_map_activity);

		// 初始化控件
		initRes();
	}

	/**
	 * 初始化百度地图的控件
	 */
	private void initRes() {
		// 屏幕中的Marker的集合
		listMarker = new ArrayList<Marker>();
		// 屏幕中Bitmap的集合
		bitmapDescriptor = new ArrayList<BitmapDescriptor>();
		// 初始化地图的View
		mMapView = (MapView) findViewById(R.id.bmapView);
		// 得到地图的操作对象
		mBaiduMap = mMapView.getMap();

		mBaiduMap.setMyLocationEnabled(true);
		// 1. 初始化LocationClient类
		locationClient = new LocationClient(getApplicationContext());
		// 2. 声明LocationListener类
		locationListener = new mBDLocationListener();
		// 3. 注册监听函数
		locationClient.registerLocationListener(locationListener);
		// 4. 设置参数
		LocationClientOption locOption = new LocationClientOption();
		locOption.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		locOption.setCoorType("bd09ll");// 设置定位结果类型
		locOption.setScanSpan(5000);// 设置发起定位请求的间隔时间,ms
		locOption.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		locOption.setNeedDeviceDirect(true);// 设置返回结果包含手机的方向

		locationClient.setLocOption(locOption);
		// 5. 注册位置提醒监听事件
		notifyListener = new mNotifyListener();
		notifyListener.SetNotifyLocation(longitude, latitude, 3000, "bd09ll");// 精度，维度，范围，坐标类型
		locationClient.registerNotify(notifyListener);
		// 6. 开启/关闭 定位SDK
		locationClient.start();

		// 文字和坐标转换搜索的类
		mSearch = GeoCoder.newInstance();
		// 注册回调中的监听
		mSearch.setOnGetGeoCodeResultListener(this);

		// 去掉底部的缩放的按钮
		mMapView.showZoomControls(false);

		// 获得百度地图操作的对象
		mBaiduMap = mMapView.getMap();

		mUiSettings = mBaiduMap.getUiSettings();
		// 设使用缩放的功能
		mUiSettings.setZoomGesturesEnabled(true);
		// 使用平移的功能
		mUiSettings.setScrollGesturesEnabled(true);
		// 设使用仰视俯视的功能
		mUiSettings.setOverlookingGesturesEnabled(true);
		// 使用旋转的功能
		mUiSettings.setRotateGesturesEnabled(true);
		// 使用指南针的功能
		mUiSettings.setCompassEnabled(true);

		// 点击Marker的时候显示InfoWindows
		mBaiduMap
				.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						// 创建一个弹出来的Windows窗口
						Button button = new Button(getApplicationContext());
						button.setBackgroundResource(R.drawable.popup);

						OnInfoWindowClickListener listener = null;
						int g = listMarker.size();
						for (int i = 0; i < g; i++) {
							if (marker == listMarker.get(i)) {
								// 进入回调中 然后去的根据经纬度显示的文字坐标
								getTextArs(marker.getPosition());
								// Toast.makeText(StopMapActivity.this,
								// "Market的位置:" + city, Toast.LENGTH_LONG)
								// .show();

								listener = new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick() {

										Toast.makeText(StopMapActivity.this,
												"infoWindows被点击了" + city,
												Toast.LENGTH_LONG).show();

										mBaiduMap.hideInfoWindow();
									}
								};
								LatLng ll = marker.getPosition();
								mInfoWindow = new InfoWindow(
										BitmapDescriptorFactory
												.fromView(button), ll, -47,
										listener);
								mBaiduMap.showInfoWindow(mInfoWindow);
							}
						}

						return false;
					}
				});
		// 初始化页面中的控件
		initView();

	}

	// 根据当前的位置经纬度显示当前的文字坐标
	private void getTextArs(LatLng ptCenter) {

		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}

	/**
	 * 初始化页面的控件
	 */
	private void initView() {

		currtPosition = (TextView) findViewById(R.id.stop_map_currt_pos);

		// 底部的搜索框的部分
		stopMapBottomLL = (LinearLayout) findViewById(R.id.stop_map_ll_bottom);
		// 周围的听的按钮
		stopMapBtnP = (RadioButton) findViewById(R.id.stop_map_btn_p);
		stopMapBtnP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopMapBottomLL.setVisibility(View.GONE);
				stopMapBtnCurrt.setVisibility(View.VISIBLE);

			}
		});
		// 底部的搜索的框
		stopMapTvBottom = (Button) findViewById(R.id.stop_map_tv_bottom);
		stopMapTvBottom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(StopMapActivity.this,
						StopListActivity.class);
				// Bundle bundle = new Bundle();
				// if (listLatlng != null) {
				// bundle.putSerializable(NetCst.BLISTLATLNG, listLatlng);
				// }
				// intent.putExtra(NetCst.LISTLATLNG, bundle);
				intent.putExtra(StopCst.CITY, city);
				startActivity(intent);
			}
		});

		// 搜索的控件的按钮
		stopMapBtnF = (RadioButton) findViewById(R.id.stop_map_btn_f);
		stopMapBtnF.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				stopMapBtnCurrt.setVisibility(View.GONE);
				stopMapBottomLL.setVisibility(View.VISIBLE);
			}
		});
		BtnBack = (ImageView) findViewById(R.id.stop_map_btn_back);
		BtnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭当前的Activity
				StopMapActivity.this.finish();

			}
		});
		// 查看附近的车场的时候，将附近的车场显示出来
		stopMapBtnCurrt = (Button) findViewById(R.id.stop_map_btn_currt);
		stopMapBtnCurrt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listLatlng = new ArrayList<LatLng>();
				httpUtils = new HttpUtils();
				httpUtils.send(HttpMethod.GET, StopCst.URL,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
												  String arg1) {

								Toast.makeText(StopMapActivity.this,
										R.string.stop_map_accress_net_fail,
										Toast.LENGTH_LONG).show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								Log.i(TAG, "返回码:" + arg0.statusCode + "结果:"
										+ arg0.result + "编码格式:"
										+ arg0.contentEncoding);
								if (arg0.statusCode == 200) {
									String jsonStr = arg0.result;

									Gson gson = new Gson();
									Type listType = new TypeToken<ArrayList<LatLng>>() {
									}.getType();
									listLatlng = gson.fromJson(jsonStr,
											listType);
									Log.i(TAG,
											"解析后的结果:" + listLatlng.toString());
								}
								int count = listLatlng.size();

								for (int i = 0; i < count; i++) {
									Marker mk = null;
									BitmapDescriptor A = BitmapDescriptorFactory
											.fromResource(R.drawable.map_icon_marka);
									bitmapDescriptor.add(A);
									OverlayOptions ooA = new MarkerOptions()
											.position(listLatlng.get(i))
											.icon(A).zIndex(9).draggable(true);

									mk = (Marker) mBaiduMap.addOverlay(ooA);
									listMarker.add(mk);
								}
							}
						});
			}
		});
	}

	/**
	 * 接口回调来监听定位的数据
	 *
	 * @author wkj
	 *
	 */
	private class mBDLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {

			// 根据定位的信息来显示周围洗车场或者停车场
			longitude = bdLocation.getLongitude();
			latitude = bdLocation.getLatitude();
			radius = bdLocation.getRadius();
			direction = bdLocation.getDirection();

			// 将经纬度转换成坐标位置
			location = new LatLng(latitude, longitude);
			Log.i(TAG, "定位的数据：" + location.toString());

			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.latitude(latitude).accuracy(radius).direction(direction)
					.longitude(longitude).build();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);

			// 设置地图显示定位的中心点
			MapStatus ms = new MapStatus.Builder().target(location).build();
			MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
			mBaiduMap.animateMapStatus(u, 1000);

			if (location != null) {
				getTextArs(location);
				Log.i(TAG, "设置经纬度转文字中");
			}

			// 定位以后获取了当前的位置后将当前的位置更新为屏幕中的中心的位置
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(location);
			mBaiduMap.animateMapStatus(update);
			// 记录画在上面的图标
			Marker localMarker = null;
			BitmapDescriptor A = BitmapDescriptorFactory
					.fromResource(R.drawable.map_icon_marka);
			bitmapDescriptor.add(A);
			OverlayOptions ooA = new MarkerOptions().position(location).icon(A)
					.zIndex(9).draggable(true);
			localMarker = (Marker) mBaiduMap.addOverlay(ooA);
			listMarker.add(localMarker);

		}
	}

	/**
	 * 到达指定的位置震动提醒
	 *
	 * @author wkj
	 *
	 */
	private class mNotifyListener extends BDNotifyListener {
		@Override
		public void onNotify(BDLocation bdLocation, float distance) {
			super.onNotify(bdLocation, distance);
			mVibrator.vibrate(1000);
			// 振动提醒已到设定位置附近
			Toast.makeText(StopMapActivity.this, "震动提醒", Toast.LENGTH_SHORT)
					.show();
			// DistanceUtil.getDistance(arg0, arg1)
		}
	}

	/** -------------------坐标的文字信息之间的转换-------↓----------- */

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(StopMapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		city = result.getAddressDetail().city;
		street = result.getAddressDetail().street;
		district = result.getAddressDetail().district;
		streetNumber = result.getAddressDetail().streetNumber;
		// 设置当前的位置
		currtPosition.setText(city + " " + district + " " + street);
		Log.i(TAG, "当前的城市:" + city);

	}

	/** -------------------坐标的文字信息之间的转换-------↑----------- */

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 回收Bitmap的数据
		recyleBitmap();
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		locationClient.unRegisterLocationListener(locationListener);
		// 取消位置提醒
		locationClient.removeNotifyEvent(notifyListener);
		locationClient.stop();
	}

	// 将Bitmap的数据进行回收
	private void recyleBitmap() {
		int c = bitmapDescriptor.size();
		if (c > 0) {

			for (int i = 0; i < c; i++) {
				bitmapDescriptor.get(i).recycle();
			}
		}
	}
}
