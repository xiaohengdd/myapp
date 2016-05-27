package wkj.team.driver.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.entity.CarLineBean;

public class DriverLineAdapter extends BaseAdapter {

	private List<CarLineBean> List;

	private Context mContext;

	private LayoutInflater inflater;

	private HashMap<String, Integer> topHashMap;

	public DriverLineAdapter(Context mContext, List<CarLineBean> List) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		this.List = List;
		topHashMap = new HashMap<String, Integer>();
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 *
	 * @param list
	 */
	public void updateListView(List<CarLineBean> list) {
		this.List = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.List.size();
	}

	public Object getItem(int position) {
		return List.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CarLineBean mContent = List.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.fragment_phone_constacts_item, null);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) convertView
					.findViewById(R.id.catalog);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (hasTop(position)) {
			// topHashMap.put(mContent.carbrletter.toUpperCase(), position);
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.GroupName);
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(mContent.Text);
		return convertView;
	}

	class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		// ImageView icon;
	}

	private boolean hasTop(int position) {
		if (position == 0) {
			return true;
		} else if (!(List.get(position).GroupName).equals((List
				.get(position - 1).GroupName))) {
			return true;
		}
		return false;
	}

	public int getPositionForSec(String s) {
		Integer position = topHashMap.get(s);
		if (position != null) {
			return position;
		}
		return -1;
	}

}
