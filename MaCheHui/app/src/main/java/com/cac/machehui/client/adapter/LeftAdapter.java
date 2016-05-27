package com.cac.machehui.client.adapter;

import com.cac.machehui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LeftAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private String leftString[];
	private int pos;

	public LeftAdapter(Context context, String leftString[]) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.leftString = leftString;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return leftString.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return leftString[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.wash_choose_leftitem, null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv.setText(leftString[position]);

		if (pos == position) {
			holder.tv.setTextColor(context.getResources().getColor(
					R.color.list_text_select_color));
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.zu_choose_right_item_bg));
		} else {
			holder.tv.setTextColor(context.getResources().getColor(
					android.R.color.black));
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.zu_choose_left_item_bg));
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tv;
	}

	public void setSelectedPosition(int pos) {
		this.pos = pos;
	}
}
