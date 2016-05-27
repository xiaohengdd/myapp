package com.cac.machehui.client.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.machehui.R;


public class AboutActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	/************* 用户使用条款 *****************/

	private TextView tv_clause;
	private TextView tv_version_name;
	private ImageView iv_return;
	private RelativeLayout rl_company;
	private RelativeLayout rl_connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initView();
		iniData();
	}

	private void iniData() {
		tv_version_name.setText(getApkVersion());
	}

	private void initView() {
		rl_connection = (RelativeLayout) findViewById(R.id.connection_rl_about);
		rl_connection.setOnClickListener(this);
		rl_company = (RelativeLayout) findViewById(R.id.company_rl_about);
		rl_company.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("关于我们");
		tv_version_name = (TextView) findViewById(R.id.version_name_tv_about);
		iv_return = (ImageView) findViewById(R.id.left_return_ib_header);
		iv_return.setOnClickListener(this);
		tv_clause = (TextView) findViewById(R.id.clause_tv_about);
		tv_clause.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
			case R.id.clause_tv_about:// 进入用户使用条款页面
				Intent intent = new Intent(AboutActivity.this,
						ProtocolActivity.class);
				intent.putExtra("type", "fromReg");
				startActivity(intent);
				break;
			case R.id.connection_rl_about:// 联系我们
				showPhoneDailog();
				break;
			case R.id.company_rl_about:// 进入商户合作页面

				break;
		}

	}

	private void showPhoneDailog() {
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		View view = LayoutInflater.from(this).inflate(R.layout.phone_dialog,
				null);
		dialog.setContentView(view);
		final TextView tv_content = (TextView) view
				.findViewById(R.id.phone_tv_dialog);
		tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:4000930770"));
				AboutActivity.this.startActivity(intent);
			}
		});
		dialog.show();
	}

	/**
	 * 获取版本号
	 *
	 * @return
	 */
	public String getApkVersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}
