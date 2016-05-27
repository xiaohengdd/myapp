package com.cac.machehui.client.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.GoodAdapter;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.ShopDeatailTable;
import com.cac.machehui.client.entity.ShopGood;
import com.cac.machehui.client.inter.WashShopDetailListener;
import com.cac.machehui.client.map.MapPlanActivity;
import com.cac.machehui.client.service.WashSearchService;
import com.cac.machehui.client.utils.xUtilsImageLoader;
import com.cac.machehui.client.view.MyScrollView;
import com.cac.machehui.client.view.MyScrollView.OnScrollListener;
import com.cac.machehui.client.view.ScrollViewListView;

/**
 * 商铺详情信息的显示
 *
 * @author wkj
 *
 */
public class WashDeatilActivity extends BaseActivity implements
		OnClickListener, WashShopDetailListener, OnScrollListener {

	// private static final String TAG = "WashDeatilActivity";
	private ImageView imageShow;
	private RelativeLayout layout;
	private TextView tv_title;
	private ImageButton ib_return;
	private int width;
	private int height;
	// 返回的按钮
	private ImageButton IB_Back;

	// 顶部商店的名字
	// private TextView tvShopName;
	// 显示地址
	private TextView tvShopAddress;
	// 电话号码
	private TextView tvShopPhone;
	// 优惠的信息
	private TextView tvShopMsg;
	// 点击后是进入的付款的界面
	private Button btnOrder;
	// 点击后进入地图的界面
	private RadioButton RBMap;

	// 显示商品信息
	private ScrollViewListView goodsList;

	// 商铺的具体的信息

	private ArrayList<ShopDeatailTable> shopDeatail;
	// 访问数据部分
	private WashSearchService service;
	// 商铺的图片
	private ImageView shopPic;
	/******** 商铺的编号 ***********/
	private String shopNum;
	/******** 商品的编号 ***********/
	private String shopGoodNum;
	private ArrayList<ShopGood> shopGoods;

	private GoodAdapter goodAdapter;

	private MyScrollView scrollView;

	// 图片加载器
	private xUtilsImageLoader loader;

	// 获取上个界面传递过来的数据
	private Intent intentT;

	// 保存向第三个界面传递的数据
	private String shopName;
	private float barCount;
	private String distance;

	private String nowPrice;
	private String oldPrice;
	private String goodName;

	private String phoneOne;
	private String phoneTwo;
	private String addressDetail;
	// 商品的次数
	String howmany;
	// private RelativeLayout mBuyLayout;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MaCheHuiConstants.GETDATA:
					shopDeatail = (ArrayList<ShopDeatailTable>) msg.obj;
					if (shopDeatail != null) {
						// Log.i(TAG, "详情表中的数据:" + shopDeatail.toString());
						// 将数据部署在控件上
						setViewData();

						// 数据加载完成后，根据店铺编号查询店铺的商品的列表信息
						service.getShopGoods(Integer.parseInt(shopNum));
					}
					break;
				case MaCheHuiConstants.GETGOODS:
					// 商品列表
					shopGoods = (ArrayList<ShopGood>) msg.obj;

					if (shopGoods != null) {
						// Log.i(TAG, "商品列表：" + shopGoods.toString());
						goodAdapter = new GoodAdapter(WashDeatilActivity.this,
								shopGoods);
						goodsList.setAdapter(goodAdapter);

					}
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wash_list_deatail);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		scrollView = (MyScrollView) findViewById(R.id.scrollView);

		scrollView.smoothScrollTo(0, 0);
		scrollView.setOnScrollListener(this);
		// 当布局的状态或者控件的可见性发生改变回调的接口
		findViewById(R.id.wash_deatil_parent).getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// 这一步很重要，使得上面的购买布局和下面的购买布局重合
						onScroll(scrollView.getScrollY());
						Log.v("!!!", "移动Y轴的坐标为" + scrollView.getScrollY());
						System.out.println(scrollView.getScrollY());
					}
				});

		// 初始化控件
		initView();

		service = new WashSearchService(this, this);
		intentT = getIntent();
		Bundle bundle = intentT.getExtras();
		if (bundle != null) {
			shopName = bundle.getString(WashCst.T_SHOPNAME);
			barCount = bundle.getFloat(WashCst.T_BATCOUNT, 0.0f);
			distance = bundle.getString(WashCst.T_DISTANCE);
			shopNum = bundle.getString(WashCst.SHOPNUMBER);
			saveWashValue();
		} else {
			getWashValue();
		}
		service.shopDeatailTableInfo(Integer.parseInt(shopNum));
	}

	/****
	 * 获取洗车列表页面传过来的值
	 */
	private void getWashValue() {
		SharedPreferences sp = getSharedPreferences("currentWashDetail",
				Context.MODE_PRIVATE);
		shopName = sp.getString(WashCst.T_SHOPNAME, "");
		barCount = sp.getFloat(WashCst.T_BATCOUNT, 0.0f);
		distance = sp.getString(WashCst.T_DISTANCE, "");
		shopNum = sp.getString(WashCst.SHOPNUMBER, "");
	}

	/****
	 * 保存洗车列表页面传过来的值
	 */
	private void saveWashValue() {
		SharedPreferences sp = getSharedPreferences("currentWashDetail",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(WashCst.T_SHOPNAME, shopName);
		editor.putFloat(WashCst.T_BATCOUNT, barCount);
		editor.putString(WashCst.T_DISTANCE, distance);
		editor.putString(WashCst.SHOPNUMBER, shopNum);
		editor.commit();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		loader = new xUtilsImageLoader(this);
		imageShow = (ImageView) findViewById(R.id.wash_deatail_pic);
		imageShow.setBackground(getResources().getDrawable(
				R.drawable.detail_default));

		tvShopMsg = (TextView) findViewById(R.id.wrap_deatail_discount);

		tvShopAddress = (TextView) findViewById(R.id.wrap_deatail_content);

		tvShopPhone = (TextView) findViewById(R.id.wrap_deatail_tel);
		tvShopPhone.setOnClickListener(this);

		btnOrder = (Button) findViewById(R.id.wash_deatail_btn_pay);
		btnOrder.setOnClickListener(this);
		RBMap = (RadioButton) findViewById(R.id.wash_deatail_rb_go);
		RBMap.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);

		// 商品列表
		goodsList = (ScrollViewListView) findViewById(R.id.goods_list);
		// 点击商品列表跳转
		goodsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(WashDeatilActivity.this,
						PurchaseDetailActivity.class);

				TextView viewCard = (TextView) view
						.findViewById(R.id.wash_detail_good_info);
				/******************* 现在价格 ******************/
				TextView viewMoney = (TextView) view
						.findViewById(R.id.now_price);
				/******************* 旧金额 ******************/
				TextView viewOldMoney = (TextView) view
						.findViewById(R.id.old_money);

				ShopGood good = shopGoods.get(position);
				String goodimg = good.getImgUrl();
				goodName = viewCard.getText().toString();
				nowPrice = viewMoney.getText().toString();
				oldPrice = viewOldMoney.getText().toString();
				// 像下一页传递数据
				Bundle bundle = new Bundle();
				bundle.putString(WashCst.T_SHOPNAME, shopName);
				bundle.putFloat(WashCst.T_BATCOUNT, barCount);
				bundle.putString(WashCst.T_DISTANCE, distance);
				bundle.putString(WashCst.T_NOWPRICE, nowPrice);
				bundle.putString(WashCst.T_OLDPRICE, oldPrice);
				bundle.putString(WashCst.T_GOODNAME, goodName);
				bundle.putString(WashCst.T_ADDRESSdETAIL, addressDetail);
				bundle.putString("goodimg", goodimg);
				bundle.putString("howmany", shopGoods.get(position)
						.getHowmany());
				bundle.putString("goodId", shopGoods.get(position).getId() + "");
				// intent.putExtra("shopgoodnum",
				// shopGoods.get(position).getShopGoodnum());
				bundle.putString("shopgoodnum", shopGoods.get(position)
						.getShopGoodnum() + "");// 一定得注意字符的转换
				bundle.putString("shopNum", shopNum);
				// Toast.makeText(WashDeatilActivity.this,
				// "商品名字是"+shopGoods.get(position).getShopGoodnum(),
				// Toast.LENGTH_SHORT).show();

				// 手机号码
				bundle.putString(WashCst.T_PHONEONE, phoneOne);
				bundle.putString(WashCst.T_PHONETWO, phoneTwo);
				// 传递一个实体过去
				bundle.putSerializable(WashCst.T_SHOPGOOD, good);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intentGo = null;
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.wash_deatail_btn_pay:
				// 去付款页面
				Intent intent = new Intent(WashDeatilActivity.this,
						PaymentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("shopid", shopNum);
				bundle.putString("shopName", shopName);
				intent.putExtras(bundle);
				intent.putExtra("title", shopName);
				startActivity(intent);
				break;
			case R.id.wash_deatail_rb_go:
				// 进入百度梯度的界面
				if (shopDeatail != null && shopDeatail.size() > 0
						&& shopDeatail.get(0) != null) {
					intentGo = new Intent(WashDeatilActivity.this,
							MapPlanActivity.class);
					intentGo.putExtra(WashCst.MAP_LATITUDE, shopDeatail.get(0)
							.getLatitude());
					// Log.i(TAG, "维度:" + shopDeatail.get(0).getLatitude());
					intentGo.putExtra(WashCst.MAP_LONGTITUDE, shopDeatail.get(0)
							.getLongtitude());
					// Log.i(TAG, "经度:" + shopDeatail.get(0).getLongtitude());
				}
				break;

			case R.id.wrap_deatail_tel:
				// 点击文字的时候弹出号码的列表，然后，可以拨打
				if (tvShopPhone.getText().toString() != null) {
					Call(tvShopPhone.getText().toString());
				}

				break;
		}
		if (intentGo != null) {
			startActivity(intentGo);
		}
	}

	public void Call(String item) {
		final String items[] = item.split(",");
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		View view = LayoutInflater.from(this).inflate(
				R.layout.phone_two_dialog, null);
		dialog.setContentView(view);
		final TextView tv_content_one = (TextView) view
				.findViewById(R.id.phone_one_tv_dialog);
		tv_content_one.setText(items[0]);
		final TextView tv_content_two = (TextView) view
				.findViewById(R.id.phone_two_tv_dialog);
		if (items.length == 2) {
			tv_content_two.setVisibility(View.VISIBLE);
			tv_content_two.setText(items[1]);
		} else {
			tv_content_two.setVisibility(View.GONE);
		}
		final Intent intent = new Intent(Intent.ACTION_DIAL);
		tv_content_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				intent.setData(Uri.parse("tel:" + items[0]));
				startActivity(intent);
			}
		});
		tv_content_two.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				intent.setData(Uri.parse("tel:" + items[1]));
				startActivity(intent);
			}
		});
		dialog.show();
	}

	/**
	 * 给View设置设置显示的数据
	 */
	private void setViewData() {
		phoneOne = shopDeatail.get(0).getPhoneOne();
		phoneTwo = shopDeatail.get(0).getPhoneTwo();
		// 商铺商品列表的数据 从shopDeatail中得到
		addressDetail = shopDeatail.get(0).getAddressDeatail();
		tvShopAddress.setText(shopDeatail.get(0).getAddressDeatail());
		String disMsg = shopDeatail.get(0).getDiscountMsg();
		if (TextUtils.isEmpty(disMsg)) {
			disMsg = "暂无";
		}
		tvShopMsg.setText("优惠信息：" + disMsg);
		// tvShopName.setText(shopDeatail.get(0).getShopsName());
		tvShopPhone.setText(shopDeatail.get(0).getPhoneOne() + ","
				+ shopDeatail.get(0).getPhoneTwo());
		/* howmany = shopDeatail.get(0) */
		tv_title.setText(shopName);
		loader.display(imageShow, shopDeatail.get(0).getShopDeatailUrl());

	}

	/**
	 * 获取Service中的数据
	 */
	@Override
	public void getShopDetailData(ArrayList<ShopDeatailTable> deatailTables) {

		if (deatailTables != null) {
			// Log.i(TAG, "回调的数据:" + deatailTables.toString());
			Message message = Message.obtain();
			message.obj = deatailTables;
			message.what = MaCheHuiConstants.GETDATA;
			handler.sendMessage(message);
		} else {
			// Log.i(TAG, "回调的数据为空");
		}

		// Message message = Message.obtain();
		// message.obj = deatailTables;
		// message.what = MaCheHuiConstants.GETDATA;
		// handler.sendMessage(message);
	}

	/**
	 * 获取商品列表里面的信息
	 */
	@Override
	public void getShopGoosList(ArrayList<ShopGood> shopGoodList) {

		Message message = Message.obtain();
		message.obj = shopGoodList;
		message.what = MaCheHuiConstants.GETGOODS;
		handler.sendMessage(message);
	}

	@Override
	public void onScroll(int scrollY) {
		int mBuyLayout2ParentTop = Math.max(scrollY, scrollView.getTop());
		// mTopBuyLayout.layout(0, mBuyLayout2ParentTop,
		// mTopBuyLayout.getWidth(), mBuyLayout2ParentTop +
		// mTopBuyLayout.getHeight());

		if (mBuyLayout2ParentTop < 371) {

			// float i = float(mBuyLayout2ParentTop)%281;

			float t = mBuyLayout2ParentTop;
			float j = 281;
			float i = mBuyLayout2ParentTop / j;
			Log.v("!!!", i + "这是坐标");
			// wash_detail_title_background.setAlpha(i);
			// wash_detail_textview.setAlpha(i);
			/* IB_Back.setVisibility(View.GONE); */
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}
}
