package com.cac.machehui.client.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.LoginActivity;
import com.cac.machehui.client.activity.MineDriverActivity;
import com.cac.machehui.client.activity.MineMachekaListActivity;
import com.cac.machehui.client.activity.MineorderActivity;
import com.cac.machehui.client.activity.PersonalInfoActivity;
import com.cac.machehui.client.activity.SettingActivity;
import com.cac.machehui.client.view.RoundRectImage;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

public class MineFragment extends BaseFragment implements OnClickListener {
	private RelativeLayout mineMacheka;
	private RelativeLayout mineDriver;
	private RelativeLayout minesetting;
	private RelativeLayout mimeorder;
	private RoundRectImage rImageView;
	private String userId;
	private String nickname;
	private String img_url;

	private TextView pub_ui_mine_state;
	private BitmapUtils bitmapUtils;

	private SharedPreferences sp;
	private LinearLayout ll_header;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.app_fragment_mine, null);
		rImageView = (RoundRectImage) view.findViewById(R.id.pub_ui_mine_head);
		rImageView.setCircle(true);
		pub_ui_mine_state = (TextView) view
				.findViewById(R.id.pub_ui_mine_state);
		ll_header = (LinearLayout) view
				.findViewById(R.id.head_ll_mine_fragment);
		ll_header.setOnClickListener(this);
		initDatas();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		initDatas();
	}

	private void initDatas() {
		sp = MineFragment.this.getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		// nickname = sp.getString("nickname", "登录");
		nickname = sp.getString("usernames", "登录");
		img_url = sp.getString("headUrl", "");
		pub_ui_mine_state.setText(nickname);
		bitmapUtils = new BitmapUtils(getActivity());
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingDrawable(getResources().getDrawable(
				R.drawable.person_07));
		config.setLoadFailedDrawable(getResources().getDrawable(
				R.drawable.person_07));
		if (!"登录".equals(pub_ui_mine_state.getText())) {
			bitmapUtils.display(rImageView, img_url, config);
		} else {
			rImageView.setImageDrawable(getActivity().getResources()
					.getDrawable(R.drawable.person_09));
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sp = MineFragment.this.getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		nickname = sp.getString("nickname", "登录");
		img_url = sp.getString("img_url", "");

		/**
		 * 我的玛车卡
		 */
		mineMacheka = (RelativeLayout) view.findViewById(R.id.mineMacheka);
		minesetting = (RelativeLayout) view.findViewById(R.id.mainsetting);
		mimeorder = (RelativeLayout) view.findViewById(R.id.mimeorder);

		/**
		 * 我的 订单
		 */
		mimeorder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if (pub_ui_mine_state.getText().equals("登录")) {
					intent.setClass(MineFragment.this.getActivity(),
							LoginActivity.class);
				} else {
					intent.setClass(MineFragment.this.getActivity(),
							MineorderActivity.class);
				}
				startActivity(intent);
			}
		});
		/**
		 * 设置
		 */
		minesetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MineFragment.this.getActivity(),
						SettingActivity.class);
				startActivity(intent);
			}
		});
		/**
		 * 我的玛车卡
		 */
		mineMacheka.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (pub_ui_mine_state.getText().equals("登录")) {
					intent = new Intent(MineFragment.this.getActivity(),
							LoginActivity.class);
				} else {
					intent = new Intent(MineFragment.this.getActivity(),
							MineMachekaListActivity.class);
					startActivity(intent);
				}

			}
		});

		pub_ui_mine_state = (TextView) view
				.findViewById(R.id.pub_ui_mine_state);
		rImageView = (RoundRectImage) view.findViewById(R.id.pub_ui_mine_head);

		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingDrawable(getResources().getDrawable(
				R.drawable.person_07));
		config.setLoadFailedDrawable(getResources().getDrawable(
				R.drawable.person_07));
		bitmapUtils.display(rImageView, img_url, config);
		pub_ui_mine_state.setText(nickname);

		mineDriver = (RelativeLayout) view.findViewById(R.id.mine_driver);
		rImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (pub_ui_mine_state.getText().toString().equals("登录")) {
					intent = new Intent(getActivity(), LoginActivity.class);
				} else {
					intent = new Intent(getActivity(),
							PersonalInfoActivity.class);
				}
				startActivity(intent);
			}
		});
		mineDriver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (pub_ui_mine_state.getText().toString().equals("登录")) {
					intent = new Intent(getActivity(), LoginActivity.class);
				} else {
					intent = new Intent(getActivity(), MineDriverActivity.class);
				}
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.head_ll_mine_fragment:
				if (pub_ui_mine_state.getText().toString().equals("登录")) {
					intent = new Intent(getActivity(), LoginActivity.class);
				} else {
					intent = new Intent(getActivity(), PersonalInfoActivity.class);
				}
				startActivity(intent);
				break;
		}
	}

	@Override
	public void initData() {

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.app_fragment_mine, null);
	}
}
