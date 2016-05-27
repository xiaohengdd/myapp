package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.ChangeHistoryActivity;
import com.cac.machehui.client.activity.LucyPanActivity;
import com.cac.machehui.client.activity.PointsDetailActivity;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.entity.GoodBean;
import com.cac.machehui.client.utils.ChangeUIUtil;
import com.cac.machehui.client.utils.CheckUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class PointsAdapter extends BaseAdapter {
	private Context context;
	private List<GoodBean> goodBeans;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	public String currentScore;
	private String token;

	public PointsAdapter(Context context, List<GoodBean> goodBeans,
						 String currentScore) {
		this.context = context;
		this.goodBeans = goodBeans;
		this.currentScore = currentScore;
		SharedPreferences sp = context.getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);
		token = sp.getString("token", "");
		bitmapUtils = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		config.setLoadFailedDrawable(context.getResources().getDrawable(
				R.drawable.default2));
		config.setLoadingDrawable(context.getResources().getDrawable(
				R.drawable.image_default));
	}

	@Override
	public int getCount() {
		if (goodBeans == null) {
			return 0;
		}
		return goodBeans.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return goodBeans.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ViewHolderTop viewHolderTop;
		switch (getItemViewType(position)) {
			case 0:
				if (convertView == null) {
					viewHolderTop = new ViewHolderTop();
					convertView = LayoutInflater.from(context).inflate(
							R.layout.item_listview_points_top, null);
					viewHolderTop.iv_turntable = (ImageView) convertView
							.findViewById(R.id.jifen_turntable_iv_points);
					viewHolderTop.tv_jifen_num = (TextView) convertView
							.findViewById(R.id.jifen_num_tv_points);
					viewHolderTop.tv_history = (TextView) convertView
							.findViewById(R.id.history_tv_points);
					convertView.setTag(viewHolderTop);
				} else {
					viewHolderTop = (ViewHolderTop) convertView.getTag();
				}
				viewHolderTop.iv_turntable
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 跳转大转盘页面
								Intent intent = new Intent(context,
										LucyPanActivity.class);
								context.startActivity(intent);
							}
						});
				viewHolderTop.tv_history.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转兑换记录页面
						Intent intent = new Intent(context,
								ChangeHistoryActivity.class);
						context.startActivity(intent);
					}
				});
				viewHolderTop.tv_jifen_num.setText(currentScore);
				ChangeUIUtil.setTextViewSpan(viewHolderTop.tv_jifen_num,
						ChangeUIUtil.getFontSize(context, 38), 4,
						currentScore.length(),
						context.getResources().getColor(R.color.red_yello));
				break;
			case 1:
				if (convertView == null) {
					viewHolder = new ViewHolder();
					convertView = LayoutInflater.from(context).inflate(
							R.layout.item_listview_points, null);
					viewHolder.iv_good = (ImageView) convertView
							.findViewById(R.id.good_iv_item_points);
					viewHolder.tv_name = (TextView) convertView
							.findViewById(R.id.name_tv_item_points);
					viewHolder.tv_price = (TextView) convertView
							.findViewById(R.id.price_tv_item_points);
					viewHolder.tv_change = (TextView) convertView
							.findViewById(R.id.change_tv_item_points);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				final GoodBean goodBean = goodBeans.get(position - 1);
				bitmapUtils.display(viewHolder.iv_good, goodBean.scoreimg, config);
				viewHolder.tv_name.setText(goodBean.scoregoodname);
				viewHolder.tv_price.setText(goodBean.score + "积分");
				final String goodsType = goodBean.type;
				switch (goodsType) {
					case "0":// 积分商品
					case "1":// 积分商品
						viewHolder.tv_change.setText("兑换");
						break;
					case "2":// 积分抽奖
						viewHolder.tv_change.setText("抽奖");
						break;
				}
				viewHolder.tv_change.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (goodsType) {
							case "0":// 跳转至详情页面
							case "1":
								Intent intent = new Intent(context,
										PointsDetailActivity.class);// 是否需要传值
								Bundle bundle = new Bundle();
								bundle.putString("wbUrl", goodBean.scoredetai);
								bundle.putString("goodId", goodBean.scoregoodid);
								bundle.putString("goodScore", goodBean.score);
								bundle.putString("state", goodBean.state);
								intent.putExtras(bundle);
								context.startActivity(intent);
								break;
							case "2":
								CheckUtil checkUtil = new CheckUtil(context);
								if (!checkUtil.isEnough(goodBean, KeyValues.scoreNum)) {
									break;
								}
								if (NetworkUtil.hasInternetConnected(context)) {
									getDraw(goodBean);
								} else {
									Toast.makeText(context, "您的网络离家出走了，请检查重试",
											Toast.LENGTH_SHORT).show();
								}
								break;
						}
					}
				});

				break;
		}

		return convertView;
	}

	class ViewHolder {
		ImageView iv_good;
		TextView tv_name;
		TextView tv_price;
		TextView tv_change;
	}

	class ViewHolderTop {
		ImageView iv_turntable;// 转盘
		TextView tv_jifen_num;// 当前积分
		TextView tv_history;// 兑换记录
	}

	/**
	 * 抽奖
	 */
	private void getDraw(final GoodBean goodBean) {
		HttpUtils httpUtils = new HttpUtils();
		/** 设置超时时间 */
		httpUtils.configTimeout(10 * 1000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("scoregoodid", goodBean.scoregoodid);
		params.addBodyParameter("userid", KeyValues.userid);
		params.addBodyParameter("scoreNum", KeyValues.scoreNum);
		params.addBodyParameter("score", goodBean.score);
		params.addBodyParameter("buyNum", "1");
		final LodingDialog lodingDialog = LodingDialog.createDialog(context);
		lodingDialog.setCancelable(false);
		httpUtils.send(HttpMethod.POST, URLCst.BUY_SCORE_GOOD, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(context, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
						lodingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(context, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean baseBean = gson.fromJson(result,
										type.getType());
								switch (baseBean.typecode) {
									case "-1":
										Toast.makeText(context, "登录失效，请重新登录",
												Toast.LENGTH_SHORT).show();
										break;
									case "200":
										String scoreNum = currentScore.substring(4);
										int scoreInt = Integer.parseInt(scoreNum);
										int goodScore = Integer
												.parseInt(goodBean.score);
										if (scoreInt >= goodScore) {
											scoreNum = "当前积分"
													+ (scoreInt - goodScore);
										} else {
											scoreNum = "当前积分0";
										}
										currentScore = scoreNum;
										notifyDataSetChanged();
										Toast.makeText(context, "抱歉，您没有中奖，请再试一次吧",
												Toast.LENGTH_SHORT).show();
										break;
									case "201":
										Toast.makeText(context, "兑换失败",
												Toast.LENGTH_SHORT).show();
										break;
									default:
										Toast.makeText(context, "系统错误",
												Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(context, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}
}
