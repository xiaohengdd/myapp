package com.cac.machehui.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cac.machehui.R;

public class FoundDetailActivity extends BaseActivity {
	private String url = "";
	private WebView webview;
	private TextView title_view;
	private String title;
	private ProgressBar bar;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.founddetail_layout);
		ImageButton backbutton = (ImageButton) findViewById(R.id.finddetailback_button);
		bar = (ProgressBar) findViewById(R.id.myProgressBar);
		// 返回按钮
		backbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title_view = (TextView) findViewById(R.id.wash_cur_deatail_tv_name);
		webview = (WebView) findViewById(R.id.founddetail_wv);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");
		type = intent.getStringExtra("type");
		title_view.setText(type);

		webview.getSettings().setJavaScriptEnabled(true);
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
		webview.setWebViewClient(new WebViewClient());

		// title_view.setText(title);
		webview.loadUrl(url);

	}

}
