package com.cac.machehui.client.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.LoginActivity;
import com.cac.machehui.client.activity.TuangouOrLiquanListActivity;

public class MoneyFragment extends BaseFragment implements OnClickListener {

	private Button tuangouquan_btn;
	private Button liquan_btn;

	private SharedPreferences sp;
	private String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("ll", "MoneyFragment onCreate");
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sp = MoneyFragment.this.getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);
		username = sp.getString("usernames", "登录");
		tuangouquan_btn = (Button) view.findViewById(R.id.tuangouquan_btn);
		tuangouquan_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.equals("登录")) {
					Intent intent = new Intent(
							MoneyFragment.this.getActivity(),
							LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(
							MoneyFragment.this.getActivity(),
							TuangouOrLiquanListActivity.class);
					intent.putExtra("username", username);
					intent.putExtra("tag", "tuangouquan");
					startActivity(intent);
				}

			}
		});

		liquan_btn = (Button) view.findViewById(R.id.liquan_btn);
		liquan_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (username.equals("登录")) {
					Intent intent = new Intent(
							MoneyFragment.this.getActivity(),
							LoginActivity.class);
					startActivity(intent);

				} else {
					Intent intent = new Intent(
							MoneyFragment.this.getActivity(),
							TuangouOrLiquanListActivity.class);
					intent.putExtra("username", username);
					intent.putExtra("tag", "liquan");
					startActivity(intent);
				}

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = MoneyFragment.this.getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);

		username = sp.getString("usernames", "");
	}

	@Override
	public void onClick(View v) {
		if (username.equals("登录")) {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);

		} else {
			Intent intent = new Intent(getActivity(),
					TuangouOrLiquanListActivity.class);
			intent.putExtra("username", username);
			switch (v.getId()) {
				case R.id.liquan_btn:
					intent.putExtra("tag", "liquan");
					break;
				case R.id.tuangouquan_btn:
					intent.putExtra("tag", "tuangouquan");
					break;
			}
			startActivity(intent);
		}

	}

	@Override
	public void initData() {

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.app_fragment_money, null);
	}
}
