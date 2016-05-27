package wkj.team.driver.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import wkj.team.driver.entity.DriverItem;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cac.machehui.R;

public class DriverItemAdapter extends BaseAdapter implements SectionIndexer {

	private List<DriverItem> List;

	private Context mContext;

	private LayoutInflater inflater;

	public DriverItemAdapter(Context mContext, List<DriverItem> List) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		this.List = List;
	}

	/**
	 * 褰揕istView鏁版嵁鍙戠敓鍙樺寲鏃�,璋冪敤姝ゆ柟娉曟潵鏇存柊ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<DriverItem> list) {
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
		final DriverItem mContent = List.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.fragment_phone_constacts_item, null);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) convertView
					.findViewById(R.id.catalog);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 鏍规嵁position鑾峰彇鍒嗙被鐨勯瀛楁瘝鐨凜har ascii鍊�
		int section = getSectionForPosition(position);

		// 濡傛灉褰撳墠浣嶇疆绛変簬璇ュ垎绫婚瀛楁瘝鐨凜har鐨勪綅缃� 锛屽垯璁や负鏄涓�娆″嚭鐜�
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetter());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		viewHolder.tvTitle.setText(this.List.get(position).getN());
		// String imageUrl =
		// "http://115.28.36.21:8080/appInterface/img/carlogo/8.jpg";
		// Bitmap bmImg;
		// viewHolder.icon.setImageBitmap(returnBitMap(imageUrl));
		// BitmapUtils bitmapUtils = new BitmapUtils(mContext);

		// bitmapUtils.display(viewHolder.icon ,
		// "http://115.28.36.21:8080/appInterface/img/carlogo/"+this.List.get(position).getI()+".jpg");

		return convertView;
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView icon;
	}

	/**
	 * 鎻愬彇鑻辨枃鐨勯瀛楁瘝锛岄潪鑻辨枃瀛楁瘝鐢�#浠ｆ浛銆�
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		for (int i = 0; i < getCount(); i++) {
			String sortStr = List.get(i).getSortLetter();
			char firstChar = sortStr.toUpperCase(Locale.getDefault()).charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return List.get(position).getSortLetter().charAt(0);
	}

	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
