package com.cac.machehui.client.adapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.sort.SortModel;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;

	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * 褰揕istView鏁版嵁鍙戠敓鍙樺寲锟�?,璋冪敤姝ゆ柟娉曟潵鏇存柊ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 鏍规嵁position鑾峰彇鍒嗙被鐨勯瀛楁瘝鐨凜har ascii锟�?
		int section = getSectionForPosition(position);

		// 濡傛灉褰撳墠浣嶇疆绛変簬璇ュ垎绫婚瀛楁瘝鐨凜har鐨勪綅锟�? 锛屽垯璁や负鏄锟�?娆″嚭锟�?
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		viewHolder.tvTitle.setText(this.list.get(position).getName());

		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * 鏍规嵁ListView鐨勫綋鍓嶄綅缃幏鍙栧垎绫荤殑棣栧瓧姣嶇殑Char ascii锟�?
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 鏍规嵁鍒嗙被鐨勯瀛楁瘝鐨凜har ascii鍊艰幏鍙栧叾绗竴娆″嚭鐜拌棣栧瓧姣嶇殑浣嶇疆
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase(Locale.getDefault()).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 鎻愬彇鑻辨枃鐨勯瀛楁瘝锛岄潪鑻辨枃瀛楁瘝锟�?#浠ｆ浛锟�?
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1)
				.toUpperCase(Locale.getDefault());
		// 姝ｅ垯琛ㄨ揪寮忥紝鍒ゆ柇棣栧瓧姣嶆槸鍚︽槸鑻辨枃瀛楁瘝
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}