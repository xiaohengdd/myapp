package com.cac.machehui.client.service;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.Environment;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.ShopDeatailTable;
import com.cac.machehui.client.entity.ShopDeatailTableGSON;
import com.cac.machehui.client.entity.ShopGood;
import com.cac.machehui.client.entity.ShopGoodGSON;
import com.cac.machehui.client.entity.ShopListItem;
import com.cac.machehui.client.entity.ShopTable;
import com.cac.machehui.client.entity.ShopTableGSON;
import com.cac.machehui.client.inter.WashShopDetailListener;
import com.cac.machehui.client.inter.WashShopListener;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 *
 * @author wkj
 *
 */
public class WashSearchService {

	private static final String TAG = "WashSearchService";

	private Context context;

	private HttpUtils httpUtils = null;

	private static String URL = "";

	private ArrayList<ShopTable> infoList;

	// ShopTableGSON的实体
	private ShopTableGSON shopTableGOSN;
	// ShopDeatailGSON的实体
	private ShopDeatailTableGSON shopDeatailTableGSON;

	// 返回商品集合的信息
	private ShopGoodGSON goodGSON;

	private ArrayList<ShopListItem> shopListitem;

	private ArrayList<ShopDeatailTable> shopDeatail;
	// 商品列表集合
	private ArrayList<ShopGood> shopGoodList;

	// 商铺详情回调函数
	private WashShopDetailListener shopListener;
	// 商铺列表的回调的函数
	private WashShopListener listener;

	// 店铺列表查询的标志
	private static final int SHOPTABLEFLAG = 0;
	// 店铺详情的标志
	private static final int SHOPDETAILFALG = 1;
	// 商品列表信息
	private static final int SHOPGOOD = 2;

	public WashSearchService(Context context) {
		this.context = context;
	}

	public WashSearchService(Context context,
							 WashShopDetailListener shopListener) {
		this.context = context;
		this.shopListener = shopListener;
	}

	public WashSearchService(Context context, WashShopListener listener) {
		this.context = context;
		this.listener = listener;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case SHOPTABLEFLAG:
					infoList = (ArrayList<ShopTable>) msg.obj;
					Log.i(TAG, "mHandler中shoptable的中的数据:" + infoList.toString());
					// 设置接口中的数据
					if (infoList != null) {
						listener.getShopTableData(infoList, WashCst.QUERY_TYPE);
					}

					break;

				case SHOPDETAILFALG:
					shopDeatail = (ArrayList<ShopDeatailTable>) msg.obj;
					if (shopDeatail != null) {
						shopListener.getShopDetailData(shopDeatail);
					}

					break;
				case SHOPGOOD:
					shopGoodList = (ArrayList<ShopGood>) msg.obj;
					if (shopGoodList != null) {
						shopListener.getShopGoosList(shopGoodList);
					}
					break;
			}
		};
	}

			;

	/**
	 *
	 * @param longtitude
	 *            根据经纬度查询
	 * @param latitude
	 *            根据经纬度查询
	 * @param aearBig
	 *            根据大区域查询
	 * @param aearLittle
	 *            根据小区域查询
	 * @param shopType
	 *            根据店铺类型查询
	 * @return
	 */
	// Screening 是筛选的意思
	/*
	 * public void shopListInfo(String fuzzyshop, String Sreening, String
	 * aearBig, String shopType, int pager, final int flag,double
	 * longitude,double latitude) { //哦，明白了昨天是根据市中区 没有很好的根据坐标计算距离 httpUtils =
	 * new HttpUtils(); httpUtils.configResponseTextCharset("UTF-8"); if
	 * (!Sreening.equals("")) { // 优惠信息的网址 URL = Envirment.SERVER_PATH_URL +
	 * WashCst.SHOPISDISCOUNT; } else if (!fuzzyshop.equals("")) { URL =
	 * Envirment.SERVER_PATH_URL + "selectshop"; } else { // 区域类型的网址 URL =
	 * Envirment.SERVER_PATH_URL + WashCst.SHOPTABLEURL; } Log.i(TAG, "使用的URL=："
	 * + URL); Log.i(TAG, "ShopTable参数-->" + "aearBig:" + aearBig +
	 * "-->shopType:" + shopType); RequestParams params = null; if
	 * (!Sreening.equals("")) { params = new RequestParams("UTF-8"); // 优惠的商铺 if
	 * (Sreening.equals(WashCst.WASH_ISDISCOUNT)) {
	 * params.addQueryStringParameter(new BasicNameValuePair(
	 * WashCst.WASHCARTYPE, Sreening)); Log.i(TAG, "进入优惠"); } // 免预约的商铺 if
	 * (Sreening.equals(WashCst.WASH_ISAPPOINT)) {
	 * params.addQueryStringParameter(new BasicNameValuePair(
	 * WashCst.WASHCARTYPE, Sreening)); Log.i(TAG, "进入预约 ！"); } Log.i(TAG,
	 * "Sreening的值:" + Sreening); params.addQueryStringParameter(new
	 * BasicNameValuePair( WashCst.WASH_PAGER, String.valueOf(pager))); } else
	 * if (!"".equals(aearBig) && !"".equals(shopType)) {
	 *
	 * params = new RequestParams("UTF-8");
	 *
	 * params.addBodyParameter(new BasicNameValuePair(WashCst.AEARBIG,
	 * aearBig)); params.addBodyParameter(new BasicNameValuePair(
	 * WashCst.WASH_SHOPTYPE, shopType)); params.addBodyParameter(new
	 * BasicNameValuePair(WashCst.WASH_PAGER, String.valueOf(pager)));
	 * params.addBodyParameter(new BasicNameValuePair("longitude",
	 * String.valueOf(longitude))); params.addBodyParameter(new
	 * BasicNameValuePair("latitude", String.valueOf(latitude)));
	 *
	 *
	 * } else if (!fuzzyshop.equals("")) { params = new RequestParams("UTF-8");
	 *
	 * params.addBodyParameter(new BasicNameValuePair("fuzzyshop", fuzzyshop));
	 * Log.i(TAG, "进入设置首页搜索查询的参数:" + fuzzyshop); } Log.i(TAG, "fuzzyshop:" +
	 * fuzzyshop); httpUtils.send(HttpRequest.HttpMethod.POST, URL, params, new
	 * RequestCallBack<String>() {
	 *
	 * @Override public void onFailure(HttpException arg0, String arg1) {
	 *
	 * }
	 *
	 * @Override public void onSuccess(ResponseInfo<String> result) {
	 *
	 * String resultStr = result.result; Log.i(TAG, "服务器数据:" + resultStr);
	 *
	 * Gson gson = new Gson(); shopTableGOSN = gson.fromJson(resultStr,
	 * ShopTableGSON.class); if (shopTableGOSN != null) { infoList = new
	 * ArrayList<ShopTable>(); int errorCode = Integer.parseInt(shopTableGOSN
	 * .getErrorCode()); if (errorCode == Envirment.EROROCODEINT) { Log.i(TAG,
	 * "拿数据中。。。"); infoList = shopTableGOSN.getCleancarvolist(); if (infoList !=
	 * null) { Log.i(TAG, "数据解析后:" + shopTableGOSN .getCleancarvolist());
	 * Message msg = Message.obtain(); msg.obj = infoList; msg.what =
	 * SHOPTABLEFLAG;
	 *
	 * mHandler.sendMessage(msg);
	 *
	 * } else { Log.i(TAG, "数据为空！"); } } } } }); }
	 */

	/**
	 * 转换信息为Item中需要的形式
	 *
	 * @param shopInfo
	 * @param lng
	 * @return
	 */
	/*
	 * public ArrayList<ShopListItem> getShopListItem( ArrayList<ShopTable>
	 * shopInfo, LatLng lng) {
	 *
	 * int count = shopInfo.size(); if (shopListitem == null) { shopListitem =
	 * new ArrayList<ShopListItem>(); } else { shopListitem = null; shopListitem
	 * = new ArrayList<ShopListItem>(); } for (int i = 0; i < count; i++) {
	 * ShopListItem shop = new ShopListItem(); LatLng latLng = new
	 * LatLng(shopInfo.get(i).getLatitude(), shopInfo .get(i).getLongtitude());
	 * // 计算经纬之间的距离 double dis = DistanceUtil.getDistance(latLng, lng);
	 * shop.setDistance(dis); shop.setShopNum(shopInfo.get(i).getShopNum());
	 * shop.setImageBitmap(shopInfo.get(i).getShopUrl());
	 * shop.setStar(shopInfo.get(i).getStarCount());
	 * shop.setShopAddress(shopInfo.get(i).getAearBig());
	 * shop.setShopName(shopInfo.get(i).getShopsName());
	 *
	 * shop.setRate(shopInfo.get(i).getRate()); shopListitem.add(shop); }
	 *
	 * return shopListitem; }
	 */

	/**
	 * 根据商店的编号搜索详情店铺信息
	 *
	 * @param shopNumber
	 * @return
	 */
	public void shopDeatailTableInfo(int shopNumber) {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		RequestParams params = new RequestParams("UTF-8");
		params.addQueryStringParameter(new BasicNameValuePair(WashCst.WASH_ID,
				String.valueOf(shopNumber)));
		Log.v("!!!", shopNumber + "商铺编号是");
		URL = URLCst.CAR_HOST + "/appInterface/" + WashCst.SHOPDEATAIL;
		httpUtils.send(HttpMethod.GET, URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						Toast.makeText(context, R.string.toast_netfail,
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> result) {

						String resultStr = result.result;

						Log.i(TAG, "shopDetail的数据:" + resultStr);
						// 得到数据类型
						// Type listType = new
						// TypeToken<LinkedList<ShopDeatailTableGSON>>() {
						// }.getType();
						Gson gson = new Gson();

						shopDeatailTableGSON = gson.fromJson(resultStr,
								ShopDeatailTableGSON.class);
						Log.i(TAG, "shopDeatailTableGSON数据:"
								+ shopDeatailTableGSON.toString());
						if (Integer.parseInt(shopDeatailTableGSON
								.getErrorCode()) == Environment.EROROCODEINT) {

							shopDeatail = shopDeatailTableGSON
									.getCleancarvolist();
							if (shopDeatail != null) {
								Log.i(TAG,
										"解析getInfoList():"
												+ shopDeatailTableGSON
												.getCleancarvolist());
								Message msg = Message.obtain();
								msg.obj = shopDeatail;
								msg.what = SHOPDETAILFALG;
								mHandler.sendMessage(msg);

							} else {
								Log.i(TAG, "shopDeatail数据为空!");
							}

						} else {
							Toast.makeText(context, R.string.toast_jsonerror,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	/**
	 * 获取服务器的商品信息的列表
	 *
	 * @shopNum 店铺的编号
	 */
	public void getShopGoods(int shopNum) {
		httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		RequestParams params = new RequestParams("UTF-8");
		Log.i(TAG, "商铺的编号:" + shopNum);
		params.addQueryStringParameter(WashCst.SHOPNUM, String.valueOf(shopNum));
		URL = URLCst.CAR_HOST + "/appInterface/" + WashCst.SHOPNUMSERVER;
		httpUtils.send(HttpMethod.GET, URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.i(TAG, "商品列表返回数据失败！");

					}

					@Override
					public void onSuccess(ResponseInfo<String> result) {

						Log.i(TAG, "商品信息结果:" + result.result);
						Gson gson = new Gson();

						goodGSON = gson.fromJson(result.result,
								ShopGoodGSON.class);
						if (goodGSON.getErrorCode() == Environment.EROROCODEINT) {
							shopGoodList = goodGSON.getShopgoodslist();
							Message msg = Message.obtain();
							msg.obj = shopGoodList;
							msg.what = SHOPGOOD;
							mHandler.sendMessage(msg);
						} else {
							Log.i(TAG, "商品信息出现错误");
						}

					}
				});
	}
}
