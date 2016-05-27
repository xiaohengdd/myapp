package com.cac.machehui.client.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.WashCst;

public class MapPlanActivity extends BaseActivity implements
		BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {

	// private static final String TAG = "MapPlanActivity";

	MapView mMapView = null; // 地图View
	BaiduMap mBaidumap = null;

	// 百度地图UI显示的界面
	private UiSettings mUiSettings;

	OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;

	private String curtAddress = "";

	// 搜索相关
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	GeoCoder mGeoSearch = null; // 搜索模块，也可去掉地图模块独立使用

	// 目标地点的经纬度
	private LatLng position;
	// 当前的经纬度
	private LatLng center;

	// 返回按钮
	private ImageButton btnBack;

	String startCityAddress = "";
	String endCityAddress = "";

	private boolean mIsEngineInitSuccess = false;

	private Button btnNavi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_plan_rounte);
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.map);
		mBaidumap = mMapView.getMap();
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra(WashCst.MAP_LATITUDE, 0.0);
		double longtitude = intent.getDoubleExtra(WashCst.MAP_LONGTITUDE, 0.0);
		position = new LatLng(latitude, longtitude);
		curtAddress = AppClient.getInstance().getLocation().getCity();

		btnBack = (ImageButton) findViewById(R.id.map_btn_back);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MapPlanActivity.this.finish();
			}
		});
		btnNavi = (Button) findViewById(R.id.btn_navi);
		btnNavi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startNavi(position, center);
			}
		});
		// 去掉底部的缩放的按钮
		mMapView.showZoomControls(false);
		center = new LatLng(
				AppClient.getInstance().getLocation().getLatitude(), AppClient
				.getInstance().getLocation().getLongitude());
		// 移动节点至中心
		mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(center));
		// 地图点击事件处理
		mBaidumap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		startCityAddress = AppClient.getInstance().getLocation().getStreet();
		// 目标的位置
		PlanNode stNode = PlanNode.withLocation(position);
		// 当前的位置
		PlanNode enNode = PlanNode.withLocation(center);

		// 设置驾车行驶的起始点
		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(enNode).to(
				stNode));

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MapPlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
			routeOverlay = overlay;
			mBaidumap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	/**
	 * 开始导航
	 *
	 * @param view
	 */
	public void startNavi(LatLng position, LatLng center) {

		// 构建 导航参数
		NaviPara para = new NaviPara();
		para.startPoint = center;
		para.startName = "从这里开始";
		para.endPoint = position;
		para.endName = "到这里结束";

		try {

			BaiduMapNavigation.openBaiduMapNavi(para, this);

		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					BaiduMapNavigation
							.getLatestBaiduMapApp(MapPlanActivity.this);
				}
			});

			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		mBaidumap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}

}
