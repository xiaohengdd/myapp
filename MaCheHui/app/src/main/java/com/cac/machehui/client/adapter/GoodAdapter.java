package com.cac.machehui.client.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.ShopGood;
import com.cac.machehui.client.utils.xUtilsImageLoader;

public class GoodAdapter extends BaseAdapter {

	private ArrayList<ShopGood> shopGoods;
	private Context context;
	private LayoutInflater inflater;

	private xUtilsImageLoader loader;

	public GoodAdapter(Context context, ArrayList<ShopGood> shopGoods) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		loader = new xUtilsImageLoader(context);
		this.shopGoods = shopGoods;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return shopGoods.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return shopGoods.get(position);
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
			convertView = inflater
					.inflate(R.layout.wash_detail_good_item, null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.wash_detail_good_photo);

			holder.imageHui = (ImageView) convertView
					.findViewById(R.id.wash_detail_good_diacant);

			holder.viewCard = (TextView) convertView
					.findViewById(R.id.wash_detail_good_info);
			holder.viewMoney = (TextView) convertView
					.findViewById(R.id.now_price);
			holder.viewOldMoney = (TextView) convertView
					.findViewById(R.id.old_money);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		loader.display(holder.imageView, shopGoods.get(position).getImgUrl());// 服务端全部拼接
		holder.viewCard.setText(shopGoods.get(position).getName());
		holder.viewMoney.setText(shopGoods.get(position).getPresentPrice());
		holder.viewOldMoney.setText(shopGoods.get(position).getOriginalPrice());

		return convertView;
	}

	private static class ViewHolder {
		ImageView imageView;

		TextView viewCard;

		TextView viewMoney;

		ImageView imageHui;

		TextView viewOldMoney;

	}

}
