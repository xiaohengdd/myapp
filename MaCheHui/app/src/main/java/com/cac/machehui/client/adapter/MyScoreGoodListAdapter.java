package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.PointsDetailActivity;
import com.cac.machehui.client.activity.TuangouOrLiquanListActivity;
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

public class MyScoreGoodListAdapter extends BaseAdapter {
	private Context mContext;
	private List<GoodBean> list;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private String token;

	public MyScoreGoodListAdapter(Context mContext, List<GoodBean> list) {
		this.mContext = mContext;
		this.list = list;
		bitmapUtils = new BitmapUtils(mContext);
		SharedPreferences sp = mContext.getSharedPreferences("currentUser",
				Context.MODE_PRIVATE);
		token = sp.getString("token", "");
		config = new BitmapDisplayConfig();
		config.setLoadFailedDrawable(mContext.getResources().getDrawable(
				R.drawable.default2));
		config.setLoadingDrawable(mContext.getResources().getDrawable(
				R.drawable.image_default));
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GoodBean goodBean = list.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_my_score_good_list, null);
			viewHolder.iv_img = (ImageView) convertView
					.findViewById(R.id.img_iv_item_my_score);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.score_good_name_tv_item_score);
			viewHolder.tv_score_num = (TextView) convertView
					.findViewById(R.id.score_num_tv_item_score);
			viewHolder.tv_pwd = (TextView) convertView
					.findViewById(R.id.pwd_tv_item_score);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.time_tv_item_score);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bitmapUtils.display(viewHolder.iv_img, goodBean.scoregoodimg, config);
		viewHolder.tv_name.setText(goodBean.scoregoodname);
		viewHolder.tv_score_num.setText(goodBean.score + "积分");
		String text;
		switch (goodBean.type) {
			case "0":
				viewHolder.tv_pwd.setVisibility(View.VISIBLE);
				text = "密码券：" + goodBean.scorepassword;
				viewHolder.tv_pwd.setText(text);
				setTextViewSpan(viewHolder.tv_pwd,
						ChangeUIUtil.getFontSize(mContext, 26), 4, text.length(),
						mContext.getResources().getColor(R.color.red_yello));
				viewHolder.tv_pwd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								PointsDetailActivity.class);// 是否需要传值
						Bundle bundle = new Bundle();
						bundle.putString("wbUrl", goodBean.scoredetai);
						bundle.putString("goodId", goodBean.scoregoodid);
						bundle.putString("goodScore", goodBean.score);
						bundle.putString("state", goodBean.state);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					}
				});
				viewHolder.tv_time.setVisibility(View.VISIBLE);
				viewHolder.tv_time.setText(goodBean.endtime);
				break;
			case "1":
				viewHolder.tv_pwd.setVisibility(View.VISIBLE);
				text = "已放入我的礼券，点击查看";
				viewHolder.tv_pwd.setText(text);
				viewHolder.tv_pwd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								TuangouOrLiquanListActivity.class);
						intent.putExtra("tag", "liquan");
						mContext.startActivity(intent);
					}
				});
				setTextViewSpan(viewHolder.tv_pwd,
						ChangeUIUtil.getFontSize(mContext, 26), 8, text.length(),
						mContext.getResources().getColor(R.color.red_yello));
				viewHolder.tv_time.setVisibility(View.INVISIBLE);
				break;
			case "2":
				viewHolder.tv_pwd.setVisibility(View.VISIBLE);
				text = "很遗憾，这次没抽中，再来一次试试吧~";
				viewHolder.tv_pwd.setText(text);
				viewHolder.tv_pwd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckUtil checkUtil = new CheckUtil(mContext);
						if (!checkUtil.isEnough(goodBean, KeyValues.scoreNum)) {
							return;
						}
						if (NetworkUtil.hasInternetConnected(mContext)) {
							getDraw(goodBean);
						} else {
							Toast.makeText(mContext, "您的网络离家出走了，请检查重试",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
				setTextViewSpan(viewHolder.tv_pwd,
						ChangeUIUtil.getFontSize(mContext, 26), 0, text.length(),
						mContext.getResources().getColor(R.color.red_yello));
				viewHolder.tv_time.setVisibility(View.INVISIBLE);
				break;
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_img;
		TextView tv_name;
		TextView tv_score_num;
		TextView tv_pwd;
		TextView tv_time;
	}

	public void setTextViewSpan(TextView view, int fontSize, int start,
								int end, int color) {
		Spannable span = new SpannableString(view.getText());
		span.setSpan(new AbsoluteSizeSpan(fontSize), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(span);
	}

	/**
	 * 抽奖
	 */
	private void getDraw(GoodBean goodBean) {
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
		final LodingDialog lodingDialog = LodingDialog.createDialog(mContext);
		lodingDialog.setCancelable(false);
		httpUtils.send(HttpMethod.POST, URLCst.BUY_SCORE_GOOD, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(mContext, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
						lodingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(mContext, "返回数据异常",
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
										Toast.makeText(mContext, "登录失效，请重新登录",
												Toast.LENGTH_SHORT).show();
										break;
									case "200":
										Toast.makeText(mContext, "抱歉，您没有中奖，请再试一次吧",
												Toast.LENGTH_SHORT).show();
										break;
									case "201":
										Toast.makeText(mContext, "兑换失败",
												Toast.LENGTH_SHORT).show();
										break;
									default:
										Toast.makeText(mContext, "系统错误",
												Toast.LENGTH_SHORT).show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(mContext, "解析异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}
}
