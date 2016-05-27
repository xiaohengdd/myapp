package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.TuanQuanDetailAdapter;
import com.cac.machehui.client.entity.MyOrderBean;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;

/***
 * 团购券详情页面
 */
public class TuangoupasswordActivcity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	/********** 标题 ***********/
	private TextView tv_title;
	/********** 商品名字 ***********/
	private TextView tv_name;
	/********** 套餐详情 ***********/
	private TextView tv_detail;
	/********** 有效时间 ***********/
	private TextView tv_valid_time;
	/********** 套餐数量 ***********/
	private TextView tv_num;
	/********** 返回 ***********/
	private ImageButton ib_return;
	/********** 列表 ***********/
	private XListView mListView;
	private TuanQuanDetailAdapter mAdapter;
	private MyOrderBean myOrderBean;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuangoupassword);
		initView();
		initData();
	}

	private void initData() {
		list = new ArrayList<String>();
		Intent intent = getIntent();
		myOrderBean = (MyOrderBean) intent.getSerializableExtra("myOrderBean");
		if (myOrderBean != null) {
			tv_name.setText(myOrderBean.shopsName);
			tv_detail.setText(myOrderBean.name);
			tv_valid_time.setText("有效期至：2016.12.31");// 暂时没有需求的确定
			tv_num.setText(myOrderBean.shopgoodNum);
			list.add(myOrderBean.goodPassword);
			mAdapter = new TuanQuanDetailAdapter(this, list);
			mListView.setAdapter(mAdapter);
			mListView.setPullLoadEnable(false);
			mListView.setPullRefreshEnable(false);
		}
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("团购券详情");
		tv_name = (TextView) findViewById(R.id.good_name_tv_tuan_detail);
		tv_detail = (TextView) findViewById(R.id.detail_tv_tuan_detail);
		tv_valid_time = (TextView) findViewById(R.id.valid_time_tv_tuan_detail);
		tv_num = (TextView) findViewById(R.id.num_tv_tuan_detail);
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		mListView = (XListView) findViewById(R.id.quan_xlv_tuan_detail);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
		}
	}
}
