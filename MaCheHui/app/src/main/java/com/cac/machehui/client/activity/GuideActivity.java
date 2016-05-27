package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cac.machehui.R;

public class GuideActivity extends BaseActivity {
	private Button button;
	private ViewPager pager;
	private List<ImageView> imageList;
	private List<ImageView> dotList;
	private LinearLayout rl_dots;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_guide);

		button = (Button) findViewById(R.id.button);
		pager = (ViewPager) findViewById(R.id.view_pager);

		ImageView imageView1 = new ImageView(getApplicationContext());
		imageView1.setBackgroundResource(R.drawable.app_guide_1);

		ImageView imageView2 = new ImageView(getApplicationContext());
		imageView2.setBackgroundResource(R.drawable.app_guide_2);

		ImageView imageView3 = new ImageView(getApplicationContext());
		imageView3.setBackgroundResource(R.drawable.app_guide_3);

		imageList = new ArrayList<ImageView>();

		imageList.add(imageView1);
		imageList.add(imageView2);
		imageList.add(imageView3);
		rl_dots = (LinearLayout) findViewById(R.id.point_rl_guide);
		initDotList();
		pager.setAdapter(new MyAdatper());

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == imageList.size() - 1) {
					rl_dots.setVisibility(View.GONE);
					// 选中某页
					button.setVisibility(View.VISIBLE);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							Intent intent = new Intent(GuideActivity.this,
									HomeActivity.class);
							startActivity(intent);

							finish();
						}
					});
				} else {
					button.setVisibility(View.GONE);
					rl_dots.setVisibility(View.VISIBLE);
					oprDotsImageview(arg0);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// 滚动完成

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// 滚动状态发生改变

			}
		});
	}

	private void initDotList() {
		dotList = new ArrayList<ImageView>();
		ImageView imageView1 = (ImageView) findViewById(R.id.pub_ui_iv_dot1);
		ImageView imageView2 = (ImageView) findViewById(R.id.pub_ui_iv_dot2);
		ImageView imageView3 = (ImageView) findViewById(R.id.pub_ui_iv_dot3);
		dotList.add(imageView1);
		dotList.add(imageView2);
		dotList.add(imageView3);
	}

	private void oprDotsImageview(int position) {
		for (int i = 0; i < dotList.size(); i++) {
			ImageView imageView = dotList.get(i);
			imageView.setBackground(getResources().getDrawable(
					R.drawable.pub_ui_dot_h));
		}
		ImageView imageView = dotList.get(position);
		imageView.setBackground(getResources().getDrawable(
				R.drawable.pub_ui_dot_s));
	}

	class MyAdatper extends PagerAdapter {
		// 返回页面个数
		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		// 在当前的viewpager中去添加一个item
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(imageList.get(position));
			return imageList.get(position);
		}

		// 销毁一个view
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}
}
