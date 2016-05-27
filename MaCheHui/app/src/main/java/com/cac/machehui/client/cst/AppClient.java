package com.cac.machehui.client.cst;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wkj.team.driver.entity.DriverTypes;
import android.app.Activity;
import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;

public class AppClient extends Application {
	private List<DriverTypes> driverlist;// 车型数据库专用
	/** 所有当前打开的Activity列表 **/
	public List<WeakReference<Activity>> activityList = new LinkedList<WeakReference<Activity>>();

	private static AppClient appClient = null;
	private static Map<String, Long> map = null;
	public static Map<String, Long> maps;

	public static ArrayList<String> homeView;// 首页轮滑图片

	public static String fuzzyshop = "";
	private static String orderid_cache;// 单个订单
	private static String cartype_cache;// 由于跳转页面不成功，申请一个车型中转到 进行传递
	// public static String token;
	public static boolean fromPayment;
	/**
	 * 授权来源 0-登录页面授权 1-个人中心授权
	 */
	public Integer weixinFrom = 0;

	public Integer getWeixinFrom() {
		return weixinFrom;
	}

	public void setWeixinFrom(Integer weixinFrom) {
		this.weixinFrom = weixinFrom;
	}

	public static String getCartype_cache() {
		return cartype_cache;
	}

	public static void setCartype_cache(String cartype_cache) {
		AppClient.cartype_cache = cartype_cache;
	}

	public static String getOrderid_cache() {
		return orderid_cache;
	}

	public static void setOrderid_cache(String orderid_cache) {
		AppClient.orderid_cache = orderid_cache;
	}

	// 保存全局的定位的信息
	private BDLocation location;

	/**
	 * 使用单例模式创建Applcation的对象
	 *
	 * @return
	 */
	public static AppClient getInstance() {
		if (appClient == null) {
			appClient = new AppClient();
		}
		return appClient;
	}

	/**
	 * 把Activity从容器中移除.
	 */
	public void removeActivity(Activity activity) {
		for (WeakReference<Activity> weak : activityList) {
			if (activity == weak.get()) {
				activityList.remove(weak);
				break;
			}
		}
	}

	/**
	 *
	 * 退出APP.
	 *
	 */
	public void exit() {
		clearActivities();
		System.exit(0);
	}

	/**
	 * 遍历所有Activity并finish.
	 *
	 */
	public void clearActivities() {
		for (WeakReference<Activity> weak : activityList) {
			if (weak != null) {
				weak.get().finish();
			}
		}
	}

	/**
	 * 添加Activity到容器中.
	 *
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(new WeakReference<Activity>(activity));
	}

	@Override
	public void onCreate() {

		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());

	}

	public BDLocation getLocation() {
		return location;
	}

	public void setLocation(BDLocation location) {
		this.location = location;
	}

	public List<DriverTypes> getDriverlist() {
		return driverlist;
	}

	public void setDriverlist(List<DriverTypes> driverlist) {
		this.driverlist = driverlist;
	}

}
