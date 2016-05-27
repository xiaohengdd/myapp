package com.cac.machehui.client.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.ShopGood;
import com.cac.machehui.client.view.MyScrollView;
import com.cac.machehui.client.view.MyScrollView.OnScrollListener;
import com.lidroid.xutils.BitmapUtils;
import com.weixin.paydemo.BuyActivity;

/**
 * 鍥㈣喘璇︽儏椤甸潰
 */
public class PurchaseDetailActivity extends BaseActivity implements
		OnScrollListener {
	private RelativeLayout wash_purchase_buyc;// 鎮诞缁撶畻瀹瑰櫒
	// 鍟嗗搧璇︽儏椤甸潰

	private static final String TAG = "PurchaseActivity";
	private int width;
	private int height;

	// 鍥剧墖涓婇潰鐨勬枃瀛楃殑淇℃伅
	private TextView purchase_tv_name;
	private TextView purchase_tv_info;

	// 鏂版棫浠锋牸鏀粯
	private TextView now_price;
	private TextView now_pricec;
	private TextView old_money;
	private TextView old_moneyc;
	private Button btn_purchase;
	private Button btn_purchasec;

	// 棰勭害閫� 杩囨湡閫�
	private TextView Free_reservation;
	private TextView return_good;
	private TextView overdue;

	//
	private TextView group_address;
	private RatingBar gounp_shop_ratingBar;
	private TextView gounp_shop_hide_phone;
	private Button gounp_shop_btn_phone;
	private TextView grounp_address;
	private TextView group_distance;
	TextView wash_purchase_background;// 浣滀负鍟嗗搧璇︽儏閲岄潰鐨勭櫧鑹叉枃瀛楃殑鍗婇�鏄庤儗鏅�
	ImageView purchase_detail_pic;// 鍟嗗搧鍥剧墖
	// 閲嶅啓鐨刴yscroview
	private MyScrollView purchase_deatail_sc;
	// 璇存槑閲岄潰鐨勫晢鍝佺殑鍚嶇О
	private TextView purchase_detaile_name;
	private TextView purchase_detaile_txt;

	// 璐拱鎻愰啋鐨勫悕绉�
	private TextView purchase_remind_txt;

	// 鑾峰彇涓婁釜鐣岄潰浼犻�杩囨潵鐨勬暟鎹�
	private Intent intentT;

	// 淇濆瓨鍚戠涓変釜鐣岄潰浼犻�鐨勬暟鎹�
	private String shopName;
	private float barCount;
	private String distance;

	private String nowPrice;
	private String oldPrice;
	private String goodName;
	private String shopNum;// 鍟嗛摵鍚嶅瓧
	private String howmany;// 鍟嗗搧娆℃暟
	private String shopgoodnum;// 鍟嗗搧缂栧彿
	// 淇濆瓨褰撳墠鍟嗗搧鐨勬墍鏈夌殑淇℃伅
	private ShopGood good;
	String goodimg;// 鍟嗗搧鍥剧墖

	private String phoneOne;
	private String phoneTwo;
	// 鍟嗗簵鐨勮缁嗙殑鍦板潃
	private String addressDetail;

	// 杩斿洖鎸夐挳
	private ImageButton btnBack;
	private ImageButton btnShare;

	// 绾挎�鍙傛暟
	private LayoutParams layoutParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wash_purchase_detail);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 灞忓箷瀹藉害锛堝儚绱狅級
		height = metric.heightPixels; // 灞忓箷楂樺害锛堝儚绱狅級
		// 璁＄畻椤堕儴鍥剧墖鐨勫竷灞�枃浠�
		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				height * 2 / 10);

		// 鑾峰緱璇︾粏鏁版嵁 浠庝笂涓�〉寰楀埌鐨勪俊鎭�
		intentT = getIntent();
		Bundle bundle = intentT.getExtras();
		if (bundle != null) {
			shopNum = bundle.getString("shopNum");
			shopName = bundle.getString(WashCst.T_SHOPNAME);
			barCount = bundle.getFloat(WashCst.T_BATCOUNT, 0.0f);
			distance = bundle.getString(WashCst.T_DISTANCE);

			phoneOne = bundle.getString(WashCst.T_PHONEONE);
			phoneTwo = bundle.getString(WashCst.T_PHONETWO);
			addressDetail = bundle.getString(WashCst.T_ADDRESSdETAIL);

			nowPrice = bundle.getString(WashCst.T_NOWPRICE);
			oldPrice = bundle.getString(WashCst.T_OLDPRICE);
			goodName = bundle.getString(WashCst.T_GOODNAME);
			howmany = bundle.getString("howmany");
			goodimg = bundle.getString("goodimg");
			shopgoodnum = bundle.getString("shopgoodnum");
			good = (ShopGood) bundle.getSerializable(WashCst.T_SHOPGOOD);
			savePurchaseDetail();
		} else {
			getPurchaseDetail();
			// shopNum = KeyValues.shopNum;
			// shopName = KeyValues.shopName;
			// barCount = KeyValues.barCount;
			// distance = KeyValues.distance;
			//
			// phoneOne = KeyValues.phoneOne;
			// phoneTwo = KeyValues.phoneTwo;
			// addressDetail = KeyValues.addressDetail;
			//
			// oldPrice = KeyValues.oldPrice;
			// nowPrice = KeyValues.nowPrice;
			// goodName = KeyValues.goodName;
			// howmany = KeyValues.howmany;
			// goodimg = KeyValues.goodimg;
			// shopgoodnum = KeyValues.shopgoodnum;
			// good = KeyValues.good;
		}
		initRes();
	}

	/***
	 * 鑾峰彇瀛樺偍鐨勫�
	 */
	private void getPurchaseDetail() {
		SharedPreferences sp = getSharedPreferences("currentPurchaseDetail",
				Context.MODE_PRIVATE);
		shopNum = sp.getString("shopNum", "");
		shopName = sp.getString(WashCst.T_SHOPNAME, "");
		barCount = sp.getFloat(WashCst.T_BATCOUNT, 0.0f);
		distance = sp.getString(WashCst.T_DISTANCE, "");
		phoneOne = sp.getString(WashCst.T_PHONEONE, "");
		phoneTwo = sp.getString(WashCst.T_PHONETWO, "");
		addressDetail = sp.getString(WashCst.T_ADDRESSdETAIL, "");

		nowPrice = sp.getString(WashCst.T_NOWPRICE, "");
		oldPrice = sp.getString(WashCst.T_OLDPRICE, "");
		goodName = sp.getString(WashCst.T_GOODNAME, "");
		howmany = sp.getString("howmany", "");
		goodimg = sp.getString("goodimg", "");
		shopgoodnum = sp.getString("shopgoodnum", "");
		good = getShopGoodFromSp(sp.getString("shopgood", ""));
	}

	/**
	 * 瀛樺偍褰撳墠鐨勫彇鍊�
	 */
	private void savePurchaseDetail() {
		SharedPreferences sp = getSharedPreferences("currentPurchaseDetail",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("shopNum", shopNum);
		editor.putString(WashCst.T_SHOPNAME, shopName);
		editor.putFloat(WashCst.T_BATCOUNT, barCount);
		editor.putString(WashCst.T_DISTANCE, distance);
		editor.putString(WashCst.T_PHONEONE, phoneOne);
		editor.putString(WashCst.T_PHONETWO, phoneTwo);
		editor.putString(WashCst.T_ADDRESSdETAIL, addressDetail);

		editor.putString(WashCst.T_OLDPRICE, oldPrice);
		editor.putString(WashCst.T_NOWPRICE, nowPrice);
		editor.putString(WashCst.T_GOODNAME, goodName);
		editor.putString("howmany", howmany);
		editor.putString("goodimg", goodimg);
		editor.putString("shopgoodnum", shopgoodnum);
		editor.putString("shopgood", saveShopGoodToSp());
		editor.commit();
	}

	/**
	 * 灏嗗璞″瓨鍒皊p涓�
	 */
	private String saveShopGoodToSp() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		String serStr = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(good);
			serStr = byteArrayOutputStream.toString("ISO-8859-1");
			serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}
			} catch (IOException e2) {
			}
		}
		return serStr;
	}

	/**
	 * 浠巗p涓幏鍙栧璞�
	 * 
	 * @param str
	 * @return
	 */
	private ShopGood getShopGoodFromSp(String str) {
		String redStr;
		ShopGood shopGood = null;
		try {
			redStr = java.net.URLDecoder.decode(str, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					redStr.getBytes("ISO-8859-1"));
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			shopGood = (ShopGood) objectInputStream.readObject();
			objectInputStream.close();
			byteArrayInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shopGood;
	}

	// 鍒濆鍖栨帶浠讹紝骞朵笖缁戝畾鏁版嵁
	private void initRes() {
		wash_purchase_buyc = (RelativeLayout) findViewById(R.id.wash_purchase_buyc);// 鎮诞瀹瑰櫒
		purchase_deatail_sc = (MyScrollView) findViewById(R.id.purchase_deatail_sc);// 鑷畾涔塻croview
		purchase_deatail_sc.setOnScrollListener(this);
		wash_purchase_background = (TextView) findViewById(R.id.wash_purchase_background);// 鍗婇�鏄庤儗鏅�
		wash_purchase_background.setAlpha(0.3f);
		purchase_tv_name = (TextView) findViewById(R.id.purchase_tv_name);
		purchase_tv_name.setText(shopName);
		purchase_tv_info = (TextView) findViewById(R.id.purchase_tv_info);
		purchase_tv_info.setText(good.getName());
		purchase_detail_pic = (ImageView) findViewById(R.id.purchase_detail_pic);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(purchase_detail_pic, goodimg);

		// 鏂版棫浠锋牸鏀粯
		now_price = (TextView) findViewById(R.id.now_price);
		now_pricec = (TextView) findViewById(R.id.now_pricec);
		now_price.setText("楼" + good.getPresentPrice());
		now_pricec.setText("楼" + good.getPresentPrice());
		old_money = (TextView) findViewById(R.id.old_money);
		old_moneyc = (TextView) findViewById(R.id.old_moneyc);
		old_money.setText("楼" + good.getOriginalPrice());
		old_moneyc.setText("楼" + good.getOriginalPrice());
		btn_purchase = (Button) findViewById(R.id.btn_purchase);
		btn_purchasec = (Button) findViewById(R.id.btn_purchasec);
		// 璐拱鎸夐挳
		btn_purchase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 鍚戜笅涓�〉浼犲嚭鏁版嵁
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(PurchaseDetailActivity.this, BuyActivity.class);
				bundle.putString("goodName", goodName);
				bundle.putString("nowPrice", nowPrice);
				bundle.putString("shopNum", shopNum);
				bundle.putString("howmany", howmany);
				bundle.putString("shopName", shopName);
				bundle.putString("shopgoodnum", shopgoodnum);
				bundle.putString("oldPrice", oldPrice);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
		// 璐拱鎸夐挳
		btn_purchasec.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 鍚戜笅涓�〉浼犲嚭鏁版嵁
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(PurchaseDetailActivity.this, BuyActivity.class);
				bundle.putString("goodName", goodName);
				bundle.putString("nowPrice", nowPrice);
				bundle.putString("shopNum", shopNum);
				bundle.putString("howmany", howmany);
				bundle.putString("shopName", shopName);
				bundle.putString("shopgoodnum", shopgoodnum);
				bundle.putString("oldPrice", oldPrice);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
		Drawable dra;

		Resources res = getResources();
		dra = res.getDrawable(R.drawable.person_03);
		// 棰勭害閫� 杩囨湡閫�
		Free_reservation = (TextView) findViewById(R.id.Free_reservation);
		if (good.getIsAppoint().equals("1")) {
			Free_reservation.setCompoundDrawablesWithIntrinsicBounds(dra, null,
					null, null);
		}
		return_good = (TextView) findViewById(R.id.return_good);
		if (good.getBackAnytime().equals("1")) {
			return_good.setCompoundDrawablesWithIntrinsicBounds(dra, null,
					null, null);
		}
		overdue = (TextView) findViewById(R.id.overdue);
		if (good.getBackAnytime().equals("1")) {
			overdue.setCompoundDrawablesWithIntrinsicBounds(dra, null, null,
					null);
		}

		//
		group_address = (TextView) findViewById(R.id.group_address);
		group_address.setText(shopName);
		gounp_shop_ratingBar = (RatingBar) findViewById(R.id.gounp_shop_ratingBar);
		gounp_shop_ratingBar.setRating(barCount);
		gounp_shop_hide_phone = (TextView) findViewById(R.id.gounp_shop_hide_phone);
		gounp_shop_hide_phone.setText(phoneOne + "," + phoneTwo);
		gounp_shop_btn_phone = (Button) findViewById(R.id.gounp_shop_btn_phone);
		gounp_shop_btn_phone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String tel = gounp_shop_hide_phone.getText().toString();
				Call(tel);
			}
		});
		grounp_address = (TextView) findViewById(R.id.grounp_address);
		grounp_address.setText(addressDetail);
		group_distance = (TextView) findViewById(R.id.group_distance);
		group_distance.setText(distance);

		// 璇存槑閲岄潰鐨勫晢鍝佺殑鍚嶇О
		purchase_detaile_name = (TextView) findViewById(R.id.purchase_detaile_name);
		purchase_detaile_name.setText(good.getName());
		purchase_detaile_txt = (TextView) findViewById(R.id.purchase_detaile_txt);
		purchase_detaile_txt.setText(good.getKindlyReminder());

		// 璐拱鎻愰啋鐨勫悕绉�
		purchase_remind_txt = (TextView) findViewById(R.id.purchase_remind_txt);
		purchase_remind_txt.setText(good.getName());

		btnBack = (ImageButton) findViewById(R.id.purchase_deatail_btn_back);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PurchaseDetailActivity.this.finish();
			}
		});
		btnShare = (ImageButton) findViewById(R.id.purchase_deatail_share);
		btnShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						shopName + "\n" + good.getGoodDetail());// 鏆傛椂娌℃湁鏂囨锛屾殏鏃舵壘鐨勪俊鎭�
				shareIntent.setType("text/*");
				// 璁剧疆鍒嗕韩鍒楄〃鐨勬爣棰橈紝骞朵笖姣忔閮芥樉绀哄垎浜垪琛�
				startActivity(Intent.createChooser(shareIntent, "鍒嗕韩鍒�"));

			}
		});
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

	@Override
	public void onScroll(int scrollY) {
		int mBuyLayout2ParentTop = Math.max(scrollY,
				purchase_deatail_sc.getTop());
		Log.v("!!!", "杩欐槸璺濈鍧愭爣!!!" + mBuyLayout2ParentTop);
		if (mBuyLayout2ParentTop > 371) {

			wash_purchase_buyc.setVisibility(View.VISIBLE);

		} else {
			wash_purchase_buyc.setVisibility(View.GONE);
		}

	}
}
