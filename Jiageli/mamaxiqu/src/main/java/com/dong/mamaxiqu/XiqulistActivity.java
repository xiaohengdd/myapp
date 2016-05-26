package com.dong.mamaxiqu;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dong.mamaxiqu.com.dong.mamaxiqu.bean.QuyiBean;
import com.dong.mamaxiqu.com.dong.mamaxiqu.bean.XiquBean;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class XiqulistActivity extends AppCompatActivity {
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;

            }
            super.handleMessage(msg);
        }
    };
    String quyiid;
    XiquAdapter adapter;
    List<XiquBean.XiqulistBean> list_xiqu = new ArrayList<XiquBean.XiqulistBean>();
    List<XiquBean.XiqulistBean> list_xiqu_copy = new ArrayList<XiquBean.XiqulistBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.Ext.init(getApplication());
        setContentView(R.layout.activity_xiqulist);
        Intent intent = getIntent();
        quyiid = intent.getStringExtra("quyiid");

        adapter = new XiquAdapter(XiqulistActivity.this, list_xiqu);
        ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        getXiqulist();

    }

    public void getXiqulist() {
        RequestParams params = new RequestParams("http://192.168.31.177:3001/mmdxq/xiqusortlist");
        params.addBodyParameter("quyiid", quyiid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                XiquBean xiqubean = gson.fromJson(result, XiquBean.class);
                list_xiqu.addAll(xiqubean.getXiqulist());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public class XiquAdapter extends BaseAdapter {
        Context context;
        List<XiquBean.XiqulistBean> list;

        public XiquAdapter(Context context, List<XiquBean.XiqulistBean> list) {
            Log.v("!!!", list.size() + "");
            this.context = context;
            this.list = list;


        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v("aaa", "到这了");
            convertView = LayoutInflater.from(context).inflate(R.layout.xiqulist_adapter, null);
            TextView textview = (TextView) convertView.findViewById(R.id.xiqulist_textview);
            textview.setText(list.get(position).getName());
            return convertView;
        }
    }
}
