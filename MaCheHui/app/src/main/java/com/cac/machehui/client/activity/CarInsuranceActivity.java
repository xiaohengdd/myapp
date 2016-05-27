package com.cac.machehui.client.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cac.machehui.R;

/**
 * 车险
 */
public class CarInsuranceActivity extends BaseActivity {

	private TextView title_tv;
	private WebView webview;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carinsurance);
		ImageButton backbutton = (ImageButton) findViewById(R.id.carinsurance_back_button);

		// 返回按钮
		backbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
		bar = (ProgressBar) findViewById(R.id.myProgressBar);
		webview = (WebView) findViewById(R.id.carinsurance_wv);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient());

		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					bar.setVisibility(View.GONE);
				} else {
					if (View.INVISIBLE == bar.getVisibility()) {
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		// webview.loadUrl("http://chedai.m.yiche.com/baoxian/");
		webview.loadUrl("http://chedai.m.yiche.com/baoxian/?from=WMH0001");

	}

}
