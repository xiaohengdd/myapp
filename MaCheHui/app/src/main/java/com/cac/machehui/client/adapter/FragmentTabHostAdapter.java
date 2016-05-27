package com.cac.machehui.client.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
/**
 * 主界面Fragment的数据的适配器
 * @author wkj
 *
 */
public class FragmentTabHostAdapter extends FragmentPagerAdapter {

	private List<Fragment> list;

	public FragmentTabHostAdapter(FragmentManager fm,List<Fragment> list) {
		super(fm);
		this.list=list;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
}
