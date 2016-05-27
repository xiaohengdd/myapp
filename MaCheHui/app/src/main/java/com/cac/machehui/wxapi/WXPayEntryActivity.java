package com.cac.machehui.wxapi;

import net.sourceforge.simcpux.Constants;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.cst.AppClient;
import com.lidroid.xutils.HttpUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weixin.paydemo.PaySuccessActivity;

public class WXPayEntryActivity extends BaseActivity implements
		IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;
	AppClient app;// 全局变量
	private HttpUtils httpUtils;// 访问网路工具

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wx_wxentry_activity);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		api.handleIntent(getIntent(), this);
		initView();
	}

	private void initView() {
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		int code = resp.errCode;
		switch (code) {
			case 0:
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putBoolean("fromPayment", AppClient.fromPayment);
				intent.putExtras(bundle);
				intent.setClass(WXPayEntryActivity.this, PaySuccessActivity.class);
				startActivity(intent);
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:// 用户拒绝授权,如果微信客户端被挤掉
				if (api.registerApp(Constants.APP_ID)) {
					Toast.makeText(this, "拒绝授权", Toast.LENGTH_SHORT).show();
					api.unregisterApp();
					api.detach();
				}
				break;
			default:
				if (api.registerApp(Constants.APP_ID)) {
					api.unregisterApp();
					api.detach();
				}
				Toast.makeText(this, "微信支付失败，请重试", Toast.LENGTH_SHORT).show();
				break;
		}
		finish();
	}
}