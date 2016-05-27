package com.cac.machehui.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.StopCst;

public class StopMapDetailActivity extends BaseActivity {
	private static final String TAG = "StopMapDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_list_deatail);
		Intent intent = getIntent();
		String latlng = intent.getStringExtra(StopCst.ITEM_LATLNG);
		Log.i(TAG, "经纬度为:" + latlng);
	}
}
