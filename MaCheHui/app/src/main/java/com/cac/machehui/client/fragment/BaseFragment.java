package com.cac.machehui.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;

public abstract class BaseFragment extends Fragment {
	private View rootView;
	protected Context context;
	private Boolean hasInitData = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = initView(inflater);
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!hasInitData) {
			initData();
			hasInitData = true;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (rootView != null) {
			ViewGroup viewGroup = (ViewGroup) rootView.getParent();
			if (viewGroup != null) {
				viewGroup.removeView(rootView);
			}
		}
	}

	/**
	 * 子类实现初始化数据操作(子类自己调用)
	 */
	public abstract void initData();

	/**
	 * 子类实现初始化View操作
	 */
	protected abstract View initView(LayoutInflater inflater);
}
