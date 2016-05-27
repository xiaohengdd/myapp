package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

public class TuanQuanDetailAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;

	public TuanQuanDetailAdapter(Context context, List<String> list) {
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_orderpassword, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_item_orderpassword = (TextView) convertView
					.findViewById(R.id.tv_item_orderpassword);
			viewHolder.iv_item_orderpassword = (ImageView) convertView
					.findViewById(R.id.iv_item_orderpassword);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// viewHolder.tv_item_orderpassword.setText(list.get(position)
		// .getOrderpassword());
		// BitmapUtils bitmapUtils = new BitmapUtils(context);
		// bitmapUtils.display(viewHolder.iv_item_orderpassword, list
		// .get(position).getOrderpassword());
		viewHolder.tv_item_orderpassword.setText(list.get(position));
		String urlString = URLCst.CAR_HOST
				+ "/appInterface/CodeServlet?orderpassword="
				+ list.get(position);
		bitmapUtils
				.display(viewHolder.iv_item_orderpassword, urlString, config);
		return convertView;
	}

	private class ViewHolder {
		TextView tv_item_orderpassword;
		ImageView iv_item_orderpassword;
	}
}
