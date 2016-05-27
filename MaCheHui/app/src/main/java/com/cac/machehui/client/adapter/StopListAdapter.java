package com.cac.machehui.client.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cac.machehui.R;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.StopListItem;
import com.cac.machehui.client.map.MapPlanActivity;

public class StopListAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<StopListItem> listItems;

	private LayoutInflater inflater;

	public StopListAdapter(Context context, ArrayList<StopListItem> listItems) {
		this.context = context;
		this.listItems = listItems;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.stop_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvAddress = (TextView) convertView
					.findViewById(R.id.stop_list_item_tv_address);
			viewHolder.tvGo = (TextView) convertView
					.findViewById(R.id.go_tv_stop);
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.stop_list_item_tv_city);
			viewHolder.tvDistance = (TextView) convertView
					.findViewById(R.id.stop_list_item_tv_distance);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvAddress.setText(listItems.get(position).getStopAddress());
		viewHolder.tvGo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MapPlanActivity.class);
				intent.putExtra(WashCst.MAP_LATITUDE, listItems.get(position)
						.getLatitude());
				intent.putExtra(WashCst.MAP_LONGTITUDE, listItems.get(position)
						.getLongitude());
				context.startActivity(intent);
			}
		});
		LatLng latLng = new LatLng(listItems.get(position).getLatitude(),
				listItems.get(position).getLongitude());
		LatLng center = new LatLng(AppClient.getInstance().getLocation()
				.getLatitude(), AppClient.getInstance().getLocation()
				.getLongitude());
		double dis = DistanceUtil.getDistance(center, latLng);
		viewHolder.tvDistance.setText((int) dis + "米");
		viewHolder.tvName.setText((position + 1) + "."
				+ listItems.get(position).getStopName());
		return convertView;
	}

	private class ViewHolder {
		TextView tvAddress;
		TextView tvName;
		TextView tvDistance;
		TextView tvGo;
	}
}
