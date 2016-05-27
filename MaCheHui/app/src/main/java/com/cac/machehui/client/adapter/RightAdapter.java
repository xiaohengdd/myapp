package com.cac.machehui.client.adapter;



import com.cac.machehui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RightAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private String rightString[];

	public RightAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(rightString == null){
			return 0;
		}

		return rightString.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rightString[position];
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
			convertView = inflater.inflate(
					R.layout.wash_choose_rightitem, null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv.setText(rightString[position]);

		return convertView;
	}

	static class ViewHolder {
		TextView tv;
	}

	public void setDatas(String rightString[]) {
		this.rightString = rightString;
	}
}
