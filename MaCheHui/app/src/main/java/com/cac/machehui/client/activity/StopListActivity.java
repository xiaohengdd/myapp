package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.cac.machehui.R;
import com.cac.machehui.client.adapter.StopListAdapter;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.entity.StopListItem;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;

public class StopListActivity extends BaseActivity implements
		IXListViewListener {
	private TextView tv_title;
	private ImageButton ib_return;
	// 保存传递过来的经纬度数据

	// 通过文字搜索的地理位置坐标类
	private PoiSearch mPoiSearch = null;
	private ArrayAdapter<String> sugAdapter = null;

	// 记录当前搜索的页码数
	private int load_Index = 0;

	// 显示查询结果的列表
	private XListView xListView;

	// 返回的按钮
	private Button btnback;

	// 文字坐标转换类
	private GeoCoder mSearch = null;

	// 保存搜索的列表中的信息
	private ArrayList<StopListItem> listItems;
	// 数据的适配器
	private StopListAdapter adapter;
	/*
	 * // 当前编辑框中的信息,默认输入关键字数，停车场 private String inpuyMsg = "停车场";
	 */

	// 保持ListView显示的位置
	private int currtPosition;
	private boolean fromStop;
	private PoiNearbySearchOption nearbySearchOption;
	private Handler xHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// switch (msg.what) {
			// case FLAG:
			// listItems = (ArrayList<StopListItem>) msg.obj;
			// adapter = new StopListAdapter(StopListActivity.this, listItems);
			// xListView.setAdapter(adapter);
			// xListView.setSelection(currtPosition);
			// // 停止加载的刷新
			// onStopLoad();
			// break;
			// }
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_list_activity);
		initData();
		initView();
		initRes();
		nearbySearchOption = new PoiNearbySearchOption();
		BDLocation location = AppClient.getInstance().getLocation();
		double latitude = 36.654476;// 纬度
		double longitude = 117.055424;// 经度
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
		}
		nearbySearchOption.location(new LatLng(latitude, longitude));
		if (fromStop) {
			nearbySearchOption.keyword("停车场");
		} else {
			nearbySearchOption.keyword("加油站");
		}
		nearbySearchOption.radius(2000);// 检索半径，单位是米
		nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);
		nearbySearchOption.pageNum(load_Index);
		mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
	}

	private void initData() {
		Intent intent = getIntent();
		fromStop = intent.getBooleanExtra("fromStop", false);
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		if (fromStop) {
			tv_title.setText("附近的停车场");
		} else {
			tv_title.setText("附近的加油站");
		}
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StopListActivity.this.finish();
			}
		});
	}

	// 初始化控件

	private void initRes() {
		mPoiSearch = PoiSearch.newInstance();// 实例化PoiSearch对象
		OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
			@Override
			public void onGetPoiDetailResult(PoiDetailResult arg0) {
				// Log.i("!!!", "搜索结果detail:" + arg0.getTelephone());
			}

			@Override
			public void onGetPoiResult(PoiResult arg0) {
				if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有获取到数据
					CustomToast.showToast(StopListActivity.this,
							"没有获取到数据奥，请重新进入页面试试吧", Toast.LENGTH_SHORT);
					return;
				}
				if (listPoiInfo != null) {
					listPoiInfo.clear();
				}
				listPoiInfo = arg0.getAllPoi();
				int rCount = 0;
				if (listPoiInfo != null) {
					rCount = listPoiInfo.size();
				}
				if (load_Index == 0) {
					listItems.clear();
				}
				StopListItem item = null;
				if (rCount != 0) {
					for (int i = 0; i < rCount; i++) {
						item = new StopListItem();
						item.setStopAddress(listPoiInfo.get(i).address);
						item.setLatitude(listPoiInfo.get(i).location.latitude);
						item.setLongitude(listPoiInfo.get(i).location.longitude);
						item.setStopName(listPoiInfo.get(i).name);
						listItems.add(item);
					}
				}
				adapter.notifyDataSetChanged();
				if (listPoiInfo != null && listPoiInfo.size() >= 10) {
					xListView.setPullLoadEnable(true);
				} else {
					xListView.setPullLoadEnable(false);
				}
			}
		};
		mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

		listItems = new ArrayList<StopListItem>();
		xListView = (XListView) findViewById(R.id.stop_list_xlistview);
		TextView tv_empty = (TextView) findViewById(R.id.empty_tv);
		xListView.setEmptyView(tv_empty);
		// 设置刷新和加载的监听
		xListView.setXListViewListener(this);
		adapter = new StopListAdapter(StopListActivity.this, listItems);
		xListView.setAdapter(adapter);
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TextView tv = (TextView) view.findViewById(R.id.go_tv_stop);
				// String lat = tv.getText().toString();
				// Intent intent = new Intent(StopListActivity.this,
				// StopMapDetailActivity.class);
				// intent.putExtra(StopCst.ITEM_LATLNG, lat);
				// StopListActivity.this.startActivity(intent);
			}
		});
	}

	List<PoiInfo> listPoiInfo = null;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// if (mPoiSearch != null) {
		// mPoiSearch.destroy();
		// }
	}

	/** 停止刷新， */
	private void onStopLoad() {
		xListView.stopLoadMore();
		xListView.stopRefresh();
		xListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	// 刷新
	@Override
	public void onRefresh() {
		currtPosition = xListView.getFirstVisiblePosition();
		load_Index = 0;
		onStopLoad();
		xHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				nearbySearchOption.pageNum(load_Index);
				mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
			}
		}, 2000);
	}

	// 加载更多
	@Override
	public void onLoadMore() {
		load_Index++;
		nearbySearchOption.pageNum(load_Index);
		mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPoiSearch.destroy();
	}
}
