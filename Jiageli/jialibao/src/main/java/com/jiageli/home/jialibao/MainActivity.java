package com.jiageli.home.jialibao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.waps.AdInfo;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsListener;

public class MainActivity extends Activity implements UpdatePointsListener{
    WebView webview;
    AppConnect ac;
    int score=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("http://www.onlyjiali.com");
        Button button = (Button) findViewById(R.id.button);
        AppConnect.getInstance("497851c5002e3479af13761a34f2bfad", "default", this);

        Log.v("a","成功aa");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConnect.getInstance(MainActivity.this).showOffers(MainActivity.this,"xiaoheng");
            }
        });
       /* AppConnect.getInstance(this).showAppOffers(this);*/
/*        LinearLayout layout = (LinearLayout)findViewById(R.id.AdLinearLayout);
        AppConnect.getInstance(this).showBannerAd(this,layout);*/

/*        AppConnect.getInstance(this).initAdInfo();
        ac = AppConnect.getInstance(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdInfo adInfo = ac.getAdInfo();
                String aa = adInfo.getAdName();
                textview.setText(aa);
            }
        });*/




}

    @Override
    protected void onResume() {
        AppConnect.getInstance(this).getPoints(this);
      /*  textview.setText(score+"");*/
        super.onResume();
    }

    public void getUpdatePoints(String currencyName, int pointTotal) {
        Log.v("a",currencyName+'成'+pointTotal);
        score = pointTotal;

    }

    public void getUpdatePointsFailed(String error) {
     /*   textview.setText("失败");*/
        Log.v("a","是吧");
    }

}
