package com.cac.machehui.client.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.adapter.MessageAdapter;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.MessageItem;
import com.cac.machehui.client.entity.MsgListReaultBean;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/***
 * 消息列表
 */
public class MessageListActivity extends BaseActivity implements
		OnClickListener, IXListViewListener {
	private XListView mListView;
	private MessageAdapter mAdapter;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private TextView tv_title;
	private ImageButton ib_return;
	private SharedPreferences sp;
	private String userId;
	private String token;
	private List<MessageItem> list;
	private int page = 1;
	protected boolean isRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);
		initView();
		iniData();
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userid", "");
		token = sp.getString("token", "");
	}

	@Override
	protected void onResume() {
		super.onResume();
		onRefresh();
	}

	private void getDataFromServer() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("page", page + "");
		params.addBodyParameter("userid", userId);
		HttpUtils httpUtils = new HttpUtils();
		lodingDialog = LodingDialog.createDialog(this);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.MSG_LIST, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						onStopLoad();
						lodingDialog.dismiss();
						Toast.makeText(MessageListActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						onStopLoad();
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(MessageListActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<MsgListReaultBean> type = new TypeToken<MsgListReaultBean>() {
								};
								MsgListReaultBean bean = gson.fromJson(result,
										type.getType());
								switch (bean.typecode) {
									case "-1":
										Toast.makeText(MessageListActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "200":
										List<MessageItem> childList = bean.myMsgList;
										mAdapter.toRefresh(isRefresh, childList);
										if (childList != null
												&& childList.size() >= 10) {
											mListView.setPullLoadEnable(true);
										} else {
											mListView.setPullLoadEnable(false);
										}
										break;
									default:
										Toast.makeText(MessageListActivity.this,
												"获取消息列表失败，请重试", Toast.LENGTH_SHORT)
												.show();
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(MessageListActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.title_tv_header);
		tv_title.setText("消息");
		ib_return = (ImageButton) findViewById(R.id.left_return_ib_header);
		ib_return.setOnClickListener(this);
		list = new ArrayList<MessageItem>();
		mAdapter = new MessageAdapter(this, list);
		mListView = (XListView) findViewById(R.id.message_xlv);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				position--;
				if (position < list.size()) {
					changeMsgState(list.get(position));
				}
			}
		});
		TextView tv_empty = (TextView) findViewById(R.id.empty_tv);
		mListView.setEmptyView(tv_empty);
	}

	/**
	 * 改变消息状态
	 */
	protected void changeMsgState(MessageItem message) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("msgid", message.msgid);
		params.addBodyParameter("userid", userId);
		HttpUtils httpUtils = new HttpUtils();
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.MSG_UPDATE,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_return_ib_header:
				finish();
				break;
		}
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		page = 1;
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoadMore() {
		isRefresh = false;
		page++;
		if (NetworkUtil.hasInternetConnected(this)) {
			getDataFromServer();
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	private void onStopLoad() {
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
	}
}
