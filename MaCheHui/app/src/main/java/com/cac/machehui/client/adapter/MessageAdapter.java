package com.cac.machehui.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.MessageItem;

public class MessageAdapter extends BaseAdapter {
	Context context;
	List<MessageItem> list;

	public MessageAdapter(Context context, List<MessageItem> list) {
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
		MessageItem message = list.get(position);
		ViewHoder viewHoder;
		if (convertView == null) {
			viewHoder = new ViewHoder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_message_list, null);
			viewHoder.tv_time = (TextView) convertView
					.findViewById(R.id.time_msg_tv_item);
			viewHoder.tv_content = (TextView) convertView
					.findViewById(R.id.content_msg_tv_item);
			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHoder) convertView.getTag();
		}
		viewHoder.tv_time.setText(message.sendTime.split(" ")[0]);
		viewHoder.tv_content.setText(message.msgContent);
		return convertView;
	}

	class ViewHoder {
		private TextView tv_time;
		private TextView tv_content;
	}

	/**
	 * 刷新列表
	 *
	 * @param isRefresh
	 *            是否是下拉刷新
	 * @param childList
	 *            新数据
	 */
	public void toRefresh(boolean isRefresh, List<MessageItem> childList) {
		if (isRefresh) {
			list.clear();
		}
		if (childList != null && childList.size() > 0) {
			list.addAll(childList);
		}
		notifyDataSetChanged();
	}
}
