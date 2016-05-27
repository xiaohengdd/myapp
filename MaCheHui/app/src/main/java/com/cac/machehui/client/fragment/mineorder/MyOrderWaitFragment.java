package com.cac.machehui.client.fragment.mineorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.view.XListView;
import com.lidroid.xutils.http.RequestParams;

/**
 * 待付款
 */
public class MyOrderWaitFragment extends MyOrderBaseFragment {

	// @Override
	// public void initListView(XListView mListView, View view) {
	// mListView = (XListView) view
	// .findViewById(R.id.fragment_minewaitoder_listview);
	// }

	@Override
	public void addOderStateToRParams(RequestParams params) {
		params.addBodyParameter("orderStatus", "0");
	}

	@Override
	public View inflateLayout(LayoutInflater inflater,
							  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_wait_order, null);
		setmListView((XListView) view
				.findViewById(R.id.fragment_minewaitoder_listview));
		TextView tv_empty = (TextView) view.findViewById(R.id.empty_tv);
		getmListView().setEmptyView(tv_empty);
		return view;
	}
}
