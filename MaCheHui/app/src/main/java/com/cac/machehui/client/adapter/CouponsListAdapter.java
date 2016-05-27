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
import com.cac.machehui.client.entity.GoodBean;

public class CouponsListAdapter extends BaseAdapter {
	Context context;
	List<GoodBean> list;

	public CouponsListAdapter(Context context, List<GoodBean> list) {
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
			viewHolder.iv_next = (ImageView) convertView
					.findViewById(R.id.right_arrow);
			viewHolder.iv_next.setVisibility(View.INVISIBLE);
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
		viewHolder.name_tv.setText(list.get(position).scoregoodname);
		viewHolder.discribe_tv
				.setText("有效期至  " + list.get(position).expiretime);
		viewHolder.count_tv.setText(list.get(position).buynum);
		return convertView;
	}
}

class ViewHolder {
	TextView name_tv;
	TextView discribe_tv;
	TextView count_tv;
	ImageView iv_next;

}
