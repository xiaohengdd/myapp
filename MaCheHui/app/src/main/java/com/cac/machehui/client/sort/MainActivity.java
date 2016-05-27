package com.cac.machehui.client.sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import wkj.team.driver.view.SideBar;
import wkj.team.driver.view.SideBar.OnTouchingLetterChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.SortAdapter;
import com.cac.machehui.client.entity.Citys;

public class MainActivity extends Activity {

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private ImageView imageView = null;
	private String[] cityName = null;
	public static String cityId = "12";
	/**
	 * 姹夊瓧杞崲鎴愭嫾闊崇殑锟�?
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList = new ArrayList<SortModel>();
	private HashMap<String, String> map = null;
	private List<Citys> cityList = new ArrayList<Citys>();
	/**
	 * 鏍规嵁鎷奸煶鏉ユ帓鍒桳istView閲岄潰鐨勬暟鎹被
	 */
	private PinyinComparator pinyinComparator;
	private static String str3 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_sort);

		initViews();

		imageView = (ImageView) findViewById(R.id.sort_main_img_back);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			MainActivity.this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	public HashMap<String, String> parserXml() {
		// String[] cityName=null;
		InputStream inputStream = null;
		try {
			inputStream = getAssets().open("city.xml");

			GetData city = new GetData();
			map = city.getCity(inputStream);

		} catch (IOException e1) {

			e1.printStackTrace();
		}
		return map;
	}

	private void initViews() {

		// System.out.println(getResources().getStringArray(R.array.area)+"xml------------------------");
		HashMap<String, String> map = parserXml();
		// String[] ids=new String[map.size()];
		String[] names = new String[map.size()];

		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			Citys city = new Citys();
			// city.setcId(entry.getKey());
			city.setcName(entry.getValue());
			cityList.add(city);

		}
		for (int i = 0; i < cityList.size(); i++) {

			// ids[i]=cityList.get(i).getcId();
			names[i] = cityList.get(i).getcName();
			// System.out.println(names[i]);
		}

		// 瀹炰緥鍖栨眽瀛楄浆鎷奸煶锟�?
		characterParser = CharacterParser.getInstance();
		System.out.println(characterParser + "characterParser");
		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 璁剧疆鍙充晶瑙︽懜鐩戝惉
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 璇ュ瓧姣嶉娆″嚭鐜扮殑浣嶇疆
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 杩欓噷瑕佸埄鐢╝dapter.getItem(position)鏉ヨ幏鍙栧綋鍓峱osition锟�?瀵瑰簲鐨勫锟�?
				String addressname = ((SortModel) adapter.getItem(position))
						.getName();
				// String cid="";
				/*
				 * for (int i = 0; i < cityList.size(); i++) {
				 * if(cityList.get(i).getcName().equals(addressname)) {
				 * cid=cityList.get(i).getcId(); cityId=cid;
				 * str3=cityList.get(i).getcId().toString();
				 * 
				 * str3=str3.trim();
				 * 
				 * PublicNum.CITYID=str3; System.out.println("cityId="+cityId);
				 * getCityId(); } }
				 */

				Intent intent = new Intent();
				intent.putExtra("city", addressname);

				MainActivity.this.setResult(200, intent);

				com.cac.machehui.client.sort.MainActivity.this.finish();

			}
		});

		// SourceDateList =
		// filledData(getResources().getStringArray(R.array.area));
		SourceDateList = filledData(names);

		// 鏍规嵁a-z杩涜鎺掑簭婧愭暟锟�?
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 鏍规嵁杈撳叆妗嗚緭鍏ワ拷?锟界殑鏀瑰彉鏉ヨ繃婊ゆ悳锟�?
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 褰撹緭鍏ユ閲岄潰鐨勶拷?锟戒负绌猴紝鏇存柊涓哄師鏉ョ殑鍒楄〃锛屽惁鍒欎负杩囨护鏁版嵁鍒楄〃
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public String getCityId() {
		return cityId;
	}

	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 姹夊瓧杞崲鎴愭嫾锟�?
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase(
					Locale.getDefault());

			// 姝ｅ垯琛ㄨ揪寮忥紝鍒ゆ柇棣栧瓧姣嶆槸鍚︽槸鑻辨枃瀛楁瘝
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase(Locale
						.getDefault()));
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 鏍规嵁杈撳叆妗嗕腑鐨勶拷?锟芥潵杩囨护鏁版嵁骞舵洿鏂癓istView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 鏍规嵁a-z杩涜鎺掑簭
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

}
