package com.cac.machehui.client.adapter;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.MyOrderBean;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

public class MyOrderAdapter extends BaseAdapter {
	private Context context;
	private List<MyOrderBean> list;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;

	public MyOrderAdapter(Context context, List<MyOrderBean> list) {
		this.context = context;
		this.list = list;
		bitmapUtils = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		config.setLoadFailedDrawable(context.getResources().getDrawable(
				R.drawable.default2));
		config.setLoadingDrawable(context.getResources().getDrawable(
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
		MyOrderBean myOrderBean = list.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_my_order_lv, null);
			viewHolder.iv_order_shop = (ImageView) convertView
					.findViewById(R.id.order_iv_item);
			viewHolder.tv_order_name = (TextView) convertView
					.findViewById(R.id.order_name_tv_item);
			viewHolder.tv_order_detail = (TextView) convertView
					.findViewById(R.id.order_detail_tv_item);
			viewHolder.tv_order_state = (TextView) convertView
					.findViewById(R.id.state_tv_item);
			viewHolder.tv_order_action = (TextView) convertView
					.findViewById(R.id.action_tv_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bitmapUtils.display(viewHolder.iv_order_shop, myOrderBean.shopUrl,
				config);
		viewHolder.tv_order_name.setText(myOrderBean.shopsName);
		if (TextUtils.isEmpty(myOrderBean.name)) {
			viewHolder.tv_order_detail.setText("线上付款： "
					+ myOrderBean.discountPrice);
		} else {
			viewHolder.tv_order_detail.setText(myOrderBean.name);
		}
		switch (myOrderBean.shopgoodStatus) {// 8是已取消
			case "0":
				switch (myOrderBean.orderStatus) {
					case "0":
						viewHolder.tv_order_state.setTextColor(context.getResources()
								.getColor(R.color.text_red));
						viewHolder.tv_order_state.setText("未付款");
						viewHolder.tv_order_action.setVisibility(View.INVISIBLE);
						break;
					case "2":
						viewHolder.tv_order_state.setTextColor(context.getResources()
								.getColor(R.color.text_gray));
						viewHolder.tv_order_state.setText("已取消");
						viewHolder.tv_order_action.setVisibility(View.INVISIBLE);
						break;
				}
				break;
			case "1":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("未消费");
				viewHolder.tv_order_action.setTextColor(context.getResources()
						.getColor(R.color.text_blue));
				viewHolder.tv_order_action.setText("申请退款");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 申请退款
								showPhoneDailog();
							}
						});
				break;
			case "2":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_gray));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("已消费");
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setText("去点评");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 去点评
							}
						});
				break;
			case "3":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_gray));
				viewHolder.tv_order_action.setVisibility(View.INVISIBLE);
				viewHolder.tv_order_state.setText("已完成");
				viewHolder.tv_order_action.setText("查看点评");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 查看点评
							}
						});
				break;
			case "4":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("未消费");
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.red_yello));
				viewHolder.tv_order_action.setText("退款申请中");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				break;
			case "5":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("未消费");
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.red_yello));
				viewHolder.tv_order_action.setText("审核通过退款中");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				break;
			case "6":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("未消费");
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.red_yello));
				viewHolder.tv_order_action.setText("退款中");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				break;
			case "7":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_red));
				viewHolder.tv_order_action.setVisibility(View.VISIBLE);
				viewHolder.tv_order_state.setText("未消费");
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.red_yello));
				viewHolder.tv_order_action.setText("退款成功");
				viewHolder.tv_order_action
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
				break;
			case "8":
				viewHolder.tv_order_state.setTextColor(context.getResources()
						.getColor(R.color.text_gray));
				viewHolder.tv_order_state.setText("已取消");
				viewHolder.tv_order_action.setVisibility(View.INVISIBLE);
				break;
		}
		return convertView;
	}

	class ViewHolder {
		private ImageView iv_order_shop;
		private TextView tv_order_name;
		private TextView tv_order_detail;
		private TextView tv_order_state;
		private TextView tv_order_action;
	}

	private void showPhoneDailog() {
		final Dialog dialog = new Dialog(context,
				R.style.transparentFrameWindowStyle);
		View view = LayoutInflater.from(context).inflate(R.layout.phone_dialog,
				null);
		dialog.setContentView(view);
		final TextView tv_content = (TextView) view
				.findViewById(R.id.phone_tv_dialog);
		tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"
						+ tv_content.getText().toString()));
				context.startActivity(intent);
			}
		});
		dialog.show();
	}
}
