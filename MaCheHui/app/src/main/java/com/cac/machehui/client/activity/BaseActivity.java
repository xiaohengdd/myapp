package com.cac.machehui.client.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mobstat.StatService;
import com.cac.machehui.client.cst.AppClient;

public class BaseActivity extends FragmentActivity implements
		BDLocationListener {
	// 定位的对象
	private LocationClient client;
	private AppClient appClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bdlocation();
		appClient = AppClient.getInstance();
		appClient.addActivity(this);
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
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		appClient.removeActivity(this);
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// 将定位信息保存到全局变量中 需要保存好
		AppClient.getInstance().setLocation(arg0);
	}

}
