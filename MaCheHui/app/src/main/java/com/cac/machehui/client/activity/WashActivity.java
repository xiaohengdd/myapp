package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.cac.machehui.R;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.Environment;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.ShopDeatailTableGSON;
import com.cac.machehui.client.entity.ShopListItem;
import com.cac.machehui.client.entity.ShopTable;
import com.cac.machehui.client.entity.ShopTableGSON;
import com.cac.machehui.client.service.WashSearchService;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

//洗车功能
@SuppressLint("NewApi")
public class WashActivity extends BaseActivity implements OnClickListener,
		IXListViewListener {

	private ViewHolder viewHolder;
	WashListAdapter washListAdapter;
	// 洗车界面的是适配器

	// RadioButton
	private RadioButton RB_Near;
	private RadioButton RB_Item;

	private RadioButton RB_Select;
	// 返回按钮
	private ImageButton btnBack;
	// 编辑按钮
	private Button btnEdit;
	// 选择区域的view
	private View layoutType;
	private View layoutSort;
	private View layoutSreening;
	// Dialog对应的视图
	private View CustomView;
	String URL;
	private LinearLayout layout;

	private ListView menulistType;
	private ListView menulistSort;
	private ListView menulistSreening;
	// 列表
	private XListView listView;
	// 标题
	private TextView textview_searchtitle;

	// 菜单数据项
	private List<HashMap<String, String>> listType;
	private List<HashMap<String, String>> listSort;
	private List<HashMap<String, String>> listSreening;
	private List<HashMap<String, String>> listAear;

	// 除了洗车之外的类型的筛选
	private PopupWindow popType;
	// 根据不同的条件进行排序
	private PopupWindow popSort;
	// 根据条件进行筛选
	private PopupWindow popSreening;
	// 显示当当前的位置
	private TextView localTv;
	// 搜索的条件
	private String searchCondition;

	// 当前的所在的位置
	private String city;
	// 当前显示数据的页码
	int pager = 1;
	// 坐标
	private LatLng latPosition;
	private int width;
	private int height;

	// 区域的查询的条件
	private String QueryAear = "";
	// 车型的查询的条件 根据车型的标号来惊醒查询
	private String shopType = "";
	// 筛选
	private String shopSreening = "";
	private HttpUtils httpUtils = null;

	// ShopTable实体的集合
	List<ShopTable> infoList = new ArrayList<ShopTable>();
	// private ArrayList<ShopTable> infoList = null;
	List<ShopTable> infoLista = new ArrayList<ShopTable>();// 这个问题又来了

	// ShopTableGSON的实体
	private ShopTableGSON shopTableGOSN;
	// ShopDeatailGSON的实体
	private ShopDeatailTableGSON shopDeatailTableGSON;
	// 填充列表的实体
	private ArrayList<ShopListItem> shopListitem;
	private ArrayList<ShopListItem> shopListitem_copy = new ArrayList<ShopListItem>();

	// 新版的访问数据库的Service
	private WashSearchService service;

	// 定位对象
	private BDLocation location;
	// 经纬度
	double longitude;
	double latitude;

	// 类型
	private String BRITEM = "";
	private Handler handler;
	private TextView tv_empty;

	// AnimationDrawable animation;// 加载数据是的动画
	// ImageView img;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wash_activity);
		// 通过百度SDK获得百度定位对象
		location = AppClient.getInstance().getLocation();
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			QueryAear = location.getDistrict();// 地域
			// 坐标信息
			latPosition = new LatLng(location.getLatitude(),
					location.getLongitude());
		}
		// 上个页面传递过的数据
		Intent intent = getIntent();

		shopType = intent.getStringExtra(WashCst.FIRSTTYPE);// 店铺类型
		BRITEM = intent.getStringExtra(WashCst.MODIFYTYPE);// 不知所云
		// 设置类型的文字

		// 获取手机屏幕的大小
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {

				}
				super.handleMessage(msg);
			}

		};
		// 初始化控件
		initView();
		RB_Item.setText(BRITEM);// 更改选项里面的名字
		// 根据条件获取数据
		getData();
	}

	// 获取数据
	private void getData() {
		if (!"".equals(AppClient.fuzzyshop)) {
			if (NetworkUtil.hasInternetConnected(this)) {
				shopListInfo(AppClient.fuzzyshop, "", "", "", pager,
						WashCst.FIRSTOPEN, longitude, latitude);
				AppClient.fuzzyshop = "";
				textview_searchtitle.setText("搜索结果");
			} else {
				Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			// 触发回调时间，然后加载数据
			if (NetworkUtil.hasInternetConnected(this)) {
				shopListInfo("", "", QueryAear, shopType, pager,
						WashCst.FIRSTOPEN, longitude, latitude);
				textview_searchtitle.setText(BRITEM);
			} else {
				Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
						.show();
			}

		}

	}

	/**
	 * 监听接口回调
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			// 附近按钮
			case R.id.wash_head_rb1:

				initAear(v);
				break;
			// 洗车 等功能按钮
			case R.id.wash_head_rb2:
				initMoreFunction(v);
				break;
			// 筛选按钮
			case R.id.wash_head_rb4:
				initSreening(v);
				break;
			// 后退按钮
			case R.id.wash_btn_back:
				this.finish();
				break;
			// 位置变化按钮
			case R.id.wash_bottom_btn_edit:
				// showDialog();
				break;
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		textview_searchtitle = (TextView) findViewById(R.id.tv_activity_shoptitle);// 商铺title
		layout = (LinearLayout) findViewById(R.id.wash_line);// 选择地域等条件的 下拉列表
		btnBack = (ImageButton) findViewById(R.id.wash_btn_back);// 返回按钮
		btnBack.setOnClickListener(this);

		// 新版本的数据库的Service
		// service = new WashSearchService(this, this);

		localTv = (TextView) findViewById(R.id.wash_bottom_tv);// 当前位置
		android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
				width / 3, height * 1 / 10 - 5);// 根据屏幕设置选择地址按钮的大小

		btnEdit = (Button) findViewById(R.id.wash_bottom_btn_edit);// 地址如果不对更换地址的button
		btnEdit.setOnClickListener(this);

		// 附近的单选的按钮
		RB_Near = (RadioButton) findViewById(R.id.wash_head_rb1);
		RB_Near.setOnClickListener(this);
		RB_Near.setLayoutParams(param);

		// 根据类型筛选
		RB_Item = (RadioButton) findViewById(R.id.wash_head_rb2);
		RB_Item.setOnClickListener(this);

		RB_Item.setLayoutParams(param);

		RB_Select = (RadioButton) findViewById(R.id.wash_head_rb4);
		RB_Select.setOnClickListener(this);
		RB_Select.setLayoutParams(param);

		listView = (XListView) findViewById(R.id.wash_list_xlistview);// 列表框

		// washListAdapter= new WashListAdapter(WashActivity.this,infoList);
		// washListAdapter= new WashListAdapter(WashActivity.this,infoList);
		infoList.clear();
		washListAdapter = new WashListAdapter(WashActivity.this, infoList);
		listView.setAdapter(washListAdapter);
		// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		listView.setPullLoadEnable(false);
		// 设置刷新和加载的监听
		listView.setXListViewListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Intent intent = new Intent(WashActivity.this,
						WashDeatilActivity.class);
				// 商品的编号
				TextView tv = (TextView) view
						.findViewById(R.id.wash_item_shopnum);
				String shopNum = tv.getText().toString();

				// 店铺的名称
				TextView tvName = (TextView) view
						.findViewById(R.id.wash_item_shop_name);
				String shopName = tvName.getText().toString();
				// 评价的星指数
				RatingBar bar = (RatingBar) view
						.findViewById(R.id.wash_item_shop_ratingBar);
				float barCount = bar.getRating();
				// 距离
				TextView tv_distance = (TextView) view
						.findViewById(R.id.wash_item_shop_distance);

				String distance = tv_distance.getText().toString();
				// 电话号码
				Bundle bundle = new Bundle();
				bundle.putString(WashCst.SHOPNUMBER, shopNum);
				bundle.putString(WashCst.T_SHOPNAME, shopName);
				bundle.putFloat(WashCst.T_BATCOUNT, barCount);
				bundle.putString(WashCst.T_DISTANCE, distance);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		tv_empty = (TextView) findViewById(R.id.empty_tv);
		listView.setEmptyView(tv_empty);

		// 根据定位得到具体位置信息
		if (location != null) {
			city = location.getDistrict() + "  " + location.getStreet();
		}
		// Log.i(TAG, "定位当前的位置:" + city);
		if (TextUtils.isEmpty(city)) {
			localTv.setText("网络不存在！");
		} else {
			localTv.setText(city);
		}
	}

	/**
	 * 地区的POP
	 */
	private void initAear(View viewSort) {
		// 初始化数据项
		listAear = new ArrayList<HashMap<String, String>>();

		String[] strAear = getResources().getStringArray(R.array.WashNear);
		int count = strAear.length;
		for (int i = 0; i < count; i++) {
			HashMap<String, String> mapTemp = new HashMap<String, String>();
			mapTemp.put("item", strAear[i]);
			listAear.add(mapTemp);
		}

		if (popSort != null && popSort.isShowing()) {
			popSort.dismiss();
		} else {
			layoutSort = getLayoutInflater().inflate(R.layout.wash_meau_list,
					null);
			menulistSort = (ListView) layoutSort
					.findViewById(R.id.wash_meau_list);
			menulistSort.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					// 恢复Pager的初始的页码
					pager = 1;
					// 改变顶部对应TextView值
					// searchCondition = listType.get(position).get("item");

					switch (position) {
						case 0:
							QueryAear = "市中区";
							BRITEM = "市中区";
							break;

						case 1:
							QueryAear = "历下区";
							BRITEM = "历下区";
							break;
						case 2:
							QueryAear = "历城区";
							BRITEM = "历城区";
							break;
						case 3:
							QueryAear = "槐荫区";
							BRITEM = "槐荫区";
							break;
						case 4:
							QueryAear = "天桥区";
							BRITEM = "天桥区";
							break;
					}
					// 设置选项卡的文字
					RB_Near.setText(BRITEM);
					// 触发回调时间，然后加载数据
					infoLista.clear();// 清除原有adapter数据
					shopListInfo("", "", QueryAear, shopType, pager,
							WashCst.QUERY_TYPE, longitude, latitude);
					// 隐藏弹出窗口
					if (popSort != null && popSort.isShowing()) {
						popSort.dismiss();
					}
				}
			});
			SimpleAdapter listAdapter = new SimpleAdapter(WashActivity.this,
					listAear, R.layout.wash_meau_item, new String[] { "item" },
					new int[] { R.id.wash_item_tv1 });
			menulistSort.setAdapter(listAdapter);

			int topWidth = layout.getRight();
			// 创建弹出窗口
			// 窗口内容为layoutLeft，里面包含一个ListView
			// 窗口宽度跟tvLeft一样
			popSort = new PopupWindow(layoutSort, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			ColorDrawable cd = new ColorDrawable(-0000);
			popSort.setBackgroundDrawable(cd);
			// popType.setAnimationStyle(R.style.PopupAnimation);
			popSort.update();
			popSort.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popSort.setTouchable(true); // 设置popupwindow可点击
			popSort.setOutsideTouchable(true); // 设置popupwindow外部可点击
			popSort.setFocusable(true); // 获取焦点

			// 设置popupwindow的位置（相对tvLeft的位置）

			popSort.showAsDropDown(layout, 0, 0);

			popSort.setTouchInterceptor(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// 如果点击了popupwindow的外部，popupwindow也会消失
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popSort.dismiss();
						return true;
					}
					return false;
				}
			});
		}
	}

	/**
	 * 类型POP
	 */
	private void initMoreFunction(View typeView) {
		// 初始化数据项
		listType = new ArrayList<HashMap<String, String>>();
		String[] strType = getResources().getStringArray(R.array.WashType);
		int count = strType.length;
		for (int i = 0; i < count; i++) {
			HashMap<String, String> mapTemp = new HashMap<String, String>();
			mapTemp.put("item", strType[i]);
			listType.add(mapTemp);
		}

		if (popType != null && popType.isShowing()) {
			popType.dismiss();
		} else {
			layoutType = getLayoutInflater().inflate(R.layout.wash_meau_list,
					null);
			menulistType = (ListView) layoutType
					.findViewById(R.id.wash_meau_list);
			menulistType.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					// 恢复Pager的初始的页码
					pager = 1;
					// 改变顶部对应TextView值
					searchCondition = listType.get(position).get("item");

					switch (position) {
						case 0:
							shopType = WashCst.WASH_CLEAN;
							BRITEM = "洗车";
							break;
						case 1:
							shopType = WashCst.WASH_COME;
							BRITEM = "美容";
							break;
						case 2:
							shopType = WashCst.WASH_REAPIR;
							BRITEM = "维修保养";
							break;
						case 3:
							shopType = WashCst.WASH_GAS;
							BRITEM = "加油";
							break;
					}
					// 设置类型的文字
					RB_Item.setText(BRITEM);
					// 触发回调时间，然后加载数据
					infoLista.clear();// 清除原有adapter数据
					shopListInfo("", "", QueryAear, shopType, pager,
							WashCst.QUERY_TYPE, longitude, latitude);
					// 隐藏弹出窗口
					if (popType != null && popType.isShowing()) {
						popType.dismiss();
					}
				}
			});
			SimpleAdapter listAdapter = new SimpleAdapter(WashActivity.this,
					listType, R.layout.wash_meau_item, new String[] { "item" },
					new int[] { R.id.wash_item_tv1 });
			menulistType.setAdapter(listAdapter);

			int topWidth = layout.getRight();
			// 创建弹出窗口
			// 窗口内容为layoutLeft，里面包含一个ListView
			// 窗口宽度跟tvLeft一样
			popType = new PopupWindow(layoutType, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			ColorDrawable cd = new ColorDrawable(-0000);
			popType.setBackgroundDrawable(cd);
			// popType.setAnimationStyle(R.style.PopupAnimation);
			popType.update();
			popType.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popType.setTouchable(true); // 设置popupwindow可点击
			popType.setOutsideTouchable(true); // 设置popupwindow外部可点击
			popType.setFocusable(true); // 获取焦点

			// 设置popupwindow的位置（相对tvLeft的位置）

			popType.showAsDropDown(layout, 0, 0);

			popType.setTouchInterceptor(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// 如果点击了popupwindow的外部，popupwindow也会消失
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popType.dismiss();
						return true;
					}
					return false;
				}
			});
		}
	}

	/**
	 * 筛选的POP
	 */
	private void initSreening(View viewSreening) {
		// 初始化数据项
		listSreening = new ArrayList<HashMap<String, String>>();

		String[] strType = getResources().getStringArray(R.array.WashSreening);
		int count = strType.length;
		for (int i = 0; i < count; i++) {
			HashMap<String, String> mapTemp = new HashMap<String, String>();
			mapTemp.put("item", strType[i]);
			listSreening.add(mapTemp);
		}

		if (popSreening != null && popSreening.isShowing()) {
			popSreening.dismiss();
		} else {
			layoutSreening = getLayoutInflater().inflate(
					R.layout.wash_meau_list, null);
			menulistSreening = (ListView) layoutSreening
					.findViewById(R.id.wash_meau_list);
			menulistSreening.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					// 改变顶部对应TextView值

					String Sreening = "";
					switch (position) {
						case 0:
							Sreening = WashCst.WASH_ISDISCOUNT;
							break;

						case 1:
							Sreening = WashCst.WASH_ISAPPOINT;
							break;
					}

					pager = 1;
					// 触发回调时间，然后加载数据
					infoLista.clear();// 清除原有adapter数据
					shopListInfo("", Sreening, "", "", pager,
							WashCst.QUERY_TYPE, longitude, latitude);

					// 隐藏弹出窗口
					if (popSreening != null && popSreening.isShowing()) {
						popSreening.dismiss();
					}
				}
			});
			SimpleAdapter listAdapter = new SimpleAdapter(WashActivity.this,
					listSreening, R.layout.wash_meau_item,
					new String[] { "item" }, new int[] { R.id.wash_item_tv1 });
			menulistSreening.setAdapter(listAdapter);

			int topWidth = layout.getRight();
			// 创建弹出窗口
			// 窗口内容为layoutLeft，里面包含一个ListView
			// 窗口宽度跟tvLeft一样
			popSreening = new PopupWindow(layoutSreening,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			ColorDrawable cd = new ColorDrawable(-0000);
			popSreening.setBackgroundDrawable(cd);
			// popType.setAnimationStyle(R.style.PopupAnimation);
			popSreening.update();
			popSreening.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popSreening.setTouchable(true); // 设置popupwindow可点击
			popSreening.setOutsideTouchable(true); // 设置popupwindow外部可点击
			popSreening.setFocusable(true); // 获取焦点

			// 设置popupwindow的位置（相对tvLeft的位置）

			popSreening.showAsDropDown(layout, 0, 0);

			popSreening.setTouchInterceptor(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// 如果点击了popupwindow的外部，popupwindow也会消失
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popSreening.dismiss();
						return true;
					}
					return false;
				}
			});
		}
	}

	/** 停止刷新， */
	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	// 下拉刷新

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pager = 1;
				infoList.clear();
				handler.sendEmptyMessage(WashCst.QUERY_TYPE);

				// 触发回调时间，然后加载数据
				shopListInfo("", "", QueryAear, shopType, pager,
						WashCst.FIRSTOPEN, longitude, latitude);
				onLoad();
			}
		}, 2000);

	}

	// 上拉加载
	@Override
	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pager++;
				shopListInfo("", "", QueryAear, shopType, pager,
						WashCst.FIRSTOPEN, longitude, latitude);
			}
		}, 2000);

		// handler.sendEmptyMessage(WashCst.QUERY_TYPE);

	}

	// 得到数据的网络操作

	public void shopListInfo(String fuzzyshop, String Sreening, String aearBig,
							 String shopType, final int pager, final int flag, double longitude,
							 double latitude) {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		if (!Sreening.equals("")) {
			// 优惠信息的网址
			URL = URLCst.CAR_HOST + "/appInterface/" + WashCst.SHOPISDISCOUNT;
		} else if (!fuzzyshop.equals("")) {
			URL = URLCst.CAR_HOST + "/appInterface/" + "selectshop";
		} else {
			// 区域类型的网址
			URL = URLCst.CAR_HOST + "/appInterface/" + WashCst.SHOPTABLEURL;
		}
		RequestParams params = null;
		if (!Sreening.equals("")) {
			params = new RequestParams("UTF-8");
			// 优惠的商铺
			if (Sreening.equals(WashCst.WASH_ISDISCOUNT)) {
				params.addQueryStringParameter(new BasicNameValuePair(
						WashCst.WASHCARTYPE, Sreening));
			}
			// 免预约的商铺
			if (Sreening.equals(WashCst.WASH_ISAPPOINT)) {
				params.addQueryStringParameter(new BasicNameValuePair(
						WashCst.WASHCARTYPE, Sreening));
			}
			params.addQueryStringParameter(new BasicNameValuePair(
					WashCst.WASH_PAGER, String.valueOf(pager)));
			params.addQueryStringParameter("pager", String.valueOf(pager));
		} else if (!"".equals(aearBig) && !"".equals(shopType)) {

			params = new RequestParams("UTF-8");

			params.addBodyParameter(new BasicNameValuePair(WashCst.AEARBIG,
					aearBig));
			params.addBodyParameter(new BasicNameValuePair(
					WashCst.WASH_SHOPTYPE, shopType));
			params.addBodyParameter(new BasicNameValuePair(WashCst.WASH_PAGER,
					String.valueOf(pager)));
			params.addBodyParameter(new BasicNameValuePair("longitude", String
					.valueOf(longitude)));
			params.addBodyParameter(new BasicNameValuePair("latitude", String
					.valueOf(latitude)));

		} else if (!fuzzyshop.equals("")) {
			params = new RequestParams("UTF-8");

			params.addBodyParameter(new BasicNameValuePair("fuzzyshop",
					fuzzyshop));
		}
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpRequest.HttpMethod.POST, URL, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(WashActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onSuccess(ResponseInfo<String> result) {
						onLoad();
						lodingDialog.dismiss();
						String resultStr = result.result;
						try {
							Gson gson = new Gson();
							shopTableGOSN = gson.fromJson(resultStr,
									ShopTableGSON.class);
							if (shopTableGOSN != null) {
								int errorCode = Integer.parseInt(shopTableGOSN
										.getErrorCode());
								if (errorCode == Environment.EROROCODEINT) {
									infoLista.clear();
									if (pager == 1) {
										infoList.clear();
									}
									infoLista = shopTableGOSN
											.getCleancarvolist();// 此处为得到的数据要布置到adapter中
									if (infoList != null) {
										infoList.addAll(infoLista);
										washListAdapter.notifyDataSetChanged();// 此处为刷新
										if (infoLista.size() >= 10) {
											listView.setPullLoadEnable(true);
										} else {
											listView.setPullLoadEnable(false);
										}

									} else {
										listView.setPullLoadEnable(false);
									}
								}
							} else {
								listView.setPullLoadEnable(false);
							}
						} catch (Exception e) {
							Toast.makeText(WashActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();

						}
					}
				});
	}

	// 适配器的类
	public class WashListAdapter extends BaseAdapter {

		Context context;
		List<ShopTable> washList; // 注意private 可烦人
		BitmapUtils bitmapUtils;
		BitmapDisplayConfig config;

		public WashListAdapter(Context context, List<ShopTable> infoList) {

			this.context = context;
			this.washList = infoList;

			bitmapUtils = new BitmapUtils(context);
			config = new BitmapDisplayConfig();
			config.setLoadFailedDrawable(getResources().getDrawable(
					R.drawable.default2));
			config.setLoadingDrawable(getResources().getDrawable(
					R.drawable.image_default));
		}

		@Override
		public int getCount() {
			if (washList == null) {
				return 0;
			}

			return washList.size();
		}

		@Override
		public Object getItem(int position) {
			return washList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.wash_list_item, null);
				viewHolder.iv_shop = (ImageView) convertView
						.findViewById(R.id.wash_item_pic);
				viewHolder.tv_address = (TextView) convertView
						.findViewById(R.id.wash_item_shop_address);
				viewHolder.tv_name = (TextView) convertView
						.findViewById(R.id.wash_item_shop_name);
				viewHolder.bar = (RatingBar) convertView
						.findViewById(R.id.wash_item_shop_ratingBar);
				viewHolder.tv_distance = (TextView) convertView
						.findViewById(R.id.wash_item_shop_distance);
				viewHolder.tv_rate = (TextView) convertView
						.findViewById(R.id.wash_item_rate);
				// 下一步再让它出来 先隐藏 打折数
				viewHolder.tv_rate.setVisibility(View.GONE);
				convertView.setTag(viewHolder);
				viewHolder.tv_shopNum = (TextView) convertView
						.findViewById(R.id.wash_item_shopnum);
				Log.v("!!!", "这里也到哦了");
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tv_address.setText(washList.get(position).getAearBig());

			bitmapUtils.display(viewHolder.iv_shop, washList.get(position)
					.getShopUrl());
			viewHolder.tv_name.setText(washList.get(position).getShopsName());
			viewHolder.bar.setRating(Float.valueOf(""
					+ washList.get(position).getStarCount()) / 2f);
			viewHolder.tv_distance.setText((washList.get(position)
					.getDistance()) + "米");
			viewHolder.tv_rate.setText(washList.get(position).getRate() + "折起");
			viewHolder.tv_shopNum.setText(washList.get(position).getShopNum()
					+ "");
			return convertView;
		}

	}

	// 适配器中view的类
	private class ViewHolder {
		ImageView iv_shop;
		RatingBar bar;
		TextView tv_name;
		TextView tv_address;
		TextView tv_distance;
		TextView tv_rate;
		TextView tv_shopNum;
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
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}

	}
}
