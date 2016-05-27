package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.cac.machehui.R;
import com.cac.machehui.client.adapter.FragmentTabHostAdapter;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.fragment.FindFragment;
import com.cac.machehui.client.fragment.HomeFragment;
import com.cac.machehui.client.fragment.MineFragment;
import com.cac.machehui.client.fragment.MoneyFragment;

/**
 * 程序的主界面
 *
 * @author wkj
 *
 */
public class HomeActivity extends BaseActivity {

	private static final String TAG = "HomeActivity";

	// 定位的对象
	private LocationClient client;

	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 将要填充的内容部分的Fragment
	private List<Fragment> listFragment;

	// Tab选项卡的文字
	private int tabTxtArray[] = { R.string.home, R.string.find, R.string.money,
			R.string.mine };

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = { HomeFragment.class, FindFragment.class,
			MoneyFragment.class, MineFragment.class };

	// Fragment的容器
	private ViewPager viewPager;

	// 显示底部图标
	private int s_image[];
	// 隐藏底部图标
	private int h_image[];

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_activity_home);
		intData();
		initRes();
		initViewPager();
	}

	private void intData() {
		s_image = new int[] { R.drawable.pub_ui_home_s,
				R.drawable.pub_ui_find_s, R.drawable.pub_ui_money_s,
				R.drawable.pub_ui_mine_s };
		h_image = new int[] { R.drawable.pub_ui_home_h,
				R.drawable.pub_ui_find_h, R.drawable.pub_ui_money_h,
				R.drawable.pub_ui_mine_h };
	}

	/**
	 * 初始化TabHost控件
	 */
	private void initRes() {
		listFragment = new ArrayList<Fragment>();

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);

		layoutInflater = LayoutInflater.from(this);

		for (int i = 0; i < s_image.length; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec("" + i).setIndicator(
					getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}

		listFragment.add(new HomeFragment());
		listFragment.add(new FindFragment());
		listFragment.add(new MoneyFragment());
		listFragment.add(new MineFragment());

		/**
		 * Tabhost实现的底部的按钮的监听
		 */
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onTabChanged(String tabId) {
				// mTabHost.getDisplay()
				// mTabHost.getCurrentTabView()
				Log.i(TAG, "tabId:" + tabId);

				changeColorAndBg(mTabHost);

				viewPager.setCurrentItem(mTabHost.getCurrentTab());

			}
		});
	}

	/**
	 * 改变字体的颜色和图片的背景
	 */
	@SuppressLint("ResourceAsColor")
	private void changeColorAndBg(FragmentTabHost tabHost) {

		int tab = mTabHost.getCurrentTab();

		Log.i(TAG, "mTabHost中tab:" + tab);
		ImageView image;
		TextView text;
		for (int i = 0; i < s_image.length; i++) {

			image = (ImageView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(R.id.pub_ui_item_iv);
			text = (TextView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(R.id.pub_ui_item_tv);

			Log.i(TAG, "真假:" + (tab == i));
			if (tab == i) {
				image.setBackgroundResource(s_image[i]);
				text.setTextColor(getApplication().getResources().getColor(
						R.color.pub_ui_home_s));
				Log.i(TAG, "单次点击的For循环true: " + tab);
			} else {
				image.setBackgroundResource(h_image[i]);
				text.setTextColor(R.color.pub_ui_home_h);
				Log.i(TAG, "单次点击的For循环false :" + tab);
			}

		}
	}

	/**
	 * ViewPager的初始化
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.itemViewPager);
		viewPager.setAdapter(new FragmentTabHostAdapter(
				getSupportFragmentManager(), listFragment));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mTabHost.setCurrentTab(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	@SuppressLint("ResourceAsColor")
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.app_item_tab_view, null);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.pub_ui_item_iv);
		TextView textView = (TextView) view.findViewById(R.id.pub_ui_item_tv);

		textView.setText(tabTxtArray[index]);
		if (index == 0) {
			imageView.setBackgroundResource(s_image[index]);

			textView.setTextColor(getApplication().getResources().getColor(
					R.color.pub_ui_home_s));
		} else {
			imageView.setBackgroundResource(h_image[index]);
			textView.setTextColor(R.color.pub_ui_home_h);
		}
		return view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(HomeActivity.this, "再按一次推出码车惠", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			HomeActivity.this.finish();
			AppClient.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
}
