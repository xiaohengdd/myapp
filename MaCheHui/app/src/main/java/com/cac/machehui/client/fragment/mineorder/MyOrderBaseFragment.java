package com.cac.machehui.client.fragment.mineorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.cac.machehui.client.adapter.MyOrderAdapter;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.MyOrderBean;
import com.cac.machehui.client.entity.MyOrderResult;
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
import com.weixin.paydemo.PayActivity;

/**
 * 订单模块的基类
 */
public abstract class MyOrderBaseFragment extends Fragment implements
		IXListViewListener {
	public XListView mListView;
	private int page = 1;
	private String userId;//
	private String token;//
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private List<MyOrderBean> list;
	private MyOrderAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflateLayout(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(getActivity());
		onRefresh();
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configResponseTextCharset("UTF-8");
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("token", token);
		addOderStateToRParams(params);
		lodingDialog = LodingDialog.createDialog(getActivity());
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.GET_ORDERA,
				params, new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(getActivity(), "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						onStopLoad();
						lodingDialog.dismiss();
						try {
							Gson gson = new Gson();
							TypeToken<MyOrderResult> typeToken = new TypeToken<MyOrderResult>() {

							};
							MyOrderResult myOrderResult = gson.fromJson(
									arg0.result, typeToken.getType());
							switch (myOrderResult.typecode) {
								case "-1":
									Toast.makeText(getActivity(), "登录失效，请重新登录",
											Toast.LENGTH_SHORT).show();
									break;
								case "200":
									List<MyOrderBean> tempList = myOrderResult.orderList;
									list.clear();
									if (tempList != null) {
										list.addAll(tempList);
										mAdapter.notifyDataSetChanged();
									}
									break;
								default:
									Toast.makeText(getActivity(), "系统错误",
											Toast.LENGTH_SHORT).show();
									break;
							}

						} catch (JsonSyntaxException e) {
							e.printStackTrace();
							Toast.makeText(getActivity(), "解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(getActivity());
		page = 1;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		SharedPreferences sp = getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(MyOrderBaseFragment.this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				MyOrderBean myOrderBean = list.get(position - 1);
				Intent intent = new Intent();
				switch (myOrderBean.shopgoodStatus) {
					case "0":
						switch (myOrderBean.orderStatus) {
							case "0":
								intent.setClass(getActivity(), PayActivity.class);
								if (TextUtils.isEmpty(myOrderBean.name)) {
									intent.putExtra("shopgoodname",
											myOrderBean.shopsName);
								} else {
									intent.putExtra("shopgoodname", myOrderBean.name);
								}
								intent.putExtra("shopgoodprice",
										myOrderBean.discountPrice);
								intent.putExtra("orderid", myOrderBean.orderid);
								intent.putExtra("goodnum", myOrderBean.shopgoodNum);
								startActivity(intent);
								break;
						}
						break;
					case "1":

						break;
					case "2":

						break;
					case "3":

						break;
					case "4":

						break;
					case "5":

						break;
					case "6":

						break;
					case "7":

						break;
				}
			}
		});
		list = new ArrayList<MyOrderBean>();
		mAdapter = new MyOrderAdapter(getActivity(), list);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onRefresh() {
		if (NetworkUtil.hasInternetConnected(getActivity())) {
			getDataFromServer();
		} else {
			Toast.makeText(getActivity(), "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onLoadMore() {

	}

	public abstract void addOderStateToRParams(RequestParams params);

	public abstract View inflateLayout(LayoutInflater inflater,
									   @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

	private void onStopLoad() {
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	public XListView getmListView() {
		return mListView;
	}

	public void setmListView(XListView mListView) {
		this.mListView = mListView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (httpHandler != null) {
			httpHandler.cancel();
			httpHandler = null;
		}
		if (lodingDialog != null) {
			lodingDialog.cancel();
			lodingDialog = null;
		}
	}
}
