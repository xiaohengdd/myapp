package com.dong.mamaxiqu;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dong.mamaxiqu.com.dong.mamaxiqu.bean.QuyiBean;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    int screen_width;
    int screen_height;
    LinearLayout layout_left;
    LinearLayout layout_right;
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.v("aaa", "接收到了msg");
                    createBtn(btn_num);
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }

    };
    int btn_num = 1;
    String quyiid;
    List<QuyiBean.QuyilistBean> quyilist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.Ext.init(this.getApplication());
        x.Ext.setDebug(true); // 是否输出debug日志
        setContentView(R.layout.activity_main);
        getScreen();
        findview();
        getQuyiList();
    }

    public void getScreen() {
            /*获得屏幕长宽高*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screen_width = dm.widthPixels;
        screen_height = dm.heightPixels;
    }

    public void findview() {
        layout_left = (LinearLayout) findViewById(R.id.layout_left);
        layout_right = (LinearLayout) findViewById(R.id.layout_right);
    }

    public void createBtn(int btn_num) {
        int button_num = btn_num;
        Button Btn[] = new Button[button_num];
        for (int i = 0; i < button_num; i++) {
            Btn[i] = new Button(this);
            Btn[i].setText(quyilist.get(i).getSortname());
            quyiid = quyilist.get(i).getQuyiid();
            Btn[i].setWidth(screen_width / 2);
            Btn[i].setHeight(screen_width / 2);
            Btn[i].setOnClickListener(new View.OnClickListener() {
               String id = quyiid;
                @Override
                public void onClick(View v) {
                    Log.v("bbb",id);
                    Intent intent = new Intent();
                    intent.putExtra("quyiid",id);
                    intent.setClass(MainActivity.this,XiqulistActivity.class);
                    startActivity(intent);
                }
            });
            if (i % 2 == 0) {
                layout_left.addView(Btn[i]);
            } else if (i % 2 == 1) {
                layout_right.addView(Btn[i]);
            }
        }
    }

    public void getQuyiList() {
        RequestParams params = new RequestParams("http://192.168.31.177:3001/mmdxq/quyilist");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                QuyiBean quyibean = gson.fromJson(result, QuyiBean.class);
                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);
                quyilist = quyibean.getQuyilist();
                btn_num = quyibean.getQuyilist().size();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                 Log.v("aaa,","连接失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    ;

}
