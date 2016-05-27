package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.MyCar;

public class MyCarAdapter extends BaseAdapter {
	Context context;
	List<MyCar> list;

	public MyCarAdapter(Context context, List<MyCar> list) {
		this.context = context;
		this.list = list;
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
		MyCar car = list.get(position);
		ViewHoder viewHoder;
		if (convertView == null) {
			viewHoder = new ViewHoder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_mycar_list, null);
			viewHoder.tv_car_name = (TextView) convertView
					.findViewById(R.id.car_name_tv_item);
			viewHoder.tv_car_num = (TextView) convertView
					.findViewById(R.id.car_num_tv_item);
			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHoder) convertView.getTag();
		}
		viewHoder.tv_car_name.setText(car.cartype + "  " + car.carmodle + "  "
				+ car.carjutimodle);
		viewHoder.tv_car_num.setText(car.carnumber);
		return convertView;
	}

	class ViewHoder {
		private TextView tv_car_name;
		private TextView tv_car_num;
	}
}
