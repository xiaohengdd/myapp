package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.MyOrderBean;

public class TuanGouListAdapter extends BaseAdapter {
	Context context;
	List<MyOrderBean> list;

	public TuanGouListAdapter(Context context, List<MyOrderBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list.size();
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
					R.layout.tuangouorliquan_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name_tv = (TextView) convertView
					.findViewById(R.id.quanName);
			viewHolder.discribe_tv = (TextView) convertView
					.findViewById(R.id.quanDiscribe);
			viewHolder.count_tv = (TextView) convertView
					.findViewById(R.id.quan_count);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name_tv.setText(list.get(position).name);
		// 由于需求关于有效期还没有定，暂时还没有时间，暂时写死
		// viewHolder.discribe_tv.setText("有效期至  " +
		// list.get(position).endTime);
		viewHolder.discribe_tv.setText("有效期至  " + "2016.1.1");
		viewHolder.count_tv.setText(list.get(position).shopgoodNum);
		return convertView;
	}

	class ViewHolder {
		TextView name_tv;
		TextView discribe_tv;
		TextView count_tv;
	}
}
