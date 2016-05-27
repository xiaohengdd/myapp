package com.cac.machehui.client.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.FoundDetailActivity;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.FindTop;
import com.cac.machehui.client.entity.Found;
import com.cac.machehui.client.entity.FoundListItem;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.cac.machehui.client.view.XListView;
import com.cac.machehui.client.view.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class FindFragment extends BaseFragment implements IXListViewListener {
	private TextView tv_title;
	private ImageButton ib_return;

	private String back_result = "";

	private XListView mListView;

	FoundAdapter adapter;// foundadpter

	private int page = 1;
	int flag = 1;

	private ViewHolder viewHolder;
	private TopViewHolder topViewHolder;
	private TextView tv_empty;

	private List<Found> list = new ArrayList<Found>();
	private List<Found> list_children = new ArrayList<Found>();// 获得的数据里面的中间交换list
	private FoundListItem foundlistitem;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("ll", "FindFragment onCreate");
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.e("ll", "FindFragment onViewCreated");
		tv_title = (TextView) view.findViewById(R.id.title_tv_header);
		tv_title.setText("爱车圈");
		ib_return = (ImageButton) view.findViewById(R.id.left_return_ib_header);
		ib_return.setVisibility(View.GONE);
		adapter = new FoundAdapter(FindFragment.this.getActivity(), list);
		mListView = (XListView) view.findViewById(R.id.pub_ui_find_xlistview);
		mListView.setPullLoadEnable(true);

		mListView.setAdapter(adapter);
		mListView.setXListViewListener(FindFragment.this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Found found = list.get(position - 1);
				Intent intent = new Intent(FindFragment.this.getActivity(),
						FoundDetailActivity.class);
				intent.putExtra("title", found.getArticleTitle());
				intent.putExtra("type", found.getType());
				intent.putExtra("url", URLCst.FIND_DETAIL + found.getId());

				startActivity(intent);
			}
		});
		mListView.setPullLoadEnable(false);
		tv_empty = (TextView) view.findViewById(R.id.empty_tv);
		mListView.setEmptyView(tv_empty);
	}

	private void onStopLoad() {
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime(DateUtil.date2Str(new Date(), "kk:mm:ss"));
	}

	@Override
	public void onPause() {
		super.onPause();
		page = 1;
	}

	/**
	 * 获取服务器数据
	 *
	 * @param url
	 * @return
	 */
	public void GetFoundInfo(String url) {
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("UTF-8");
		lodingDialog = LodingDialog.createDialog(getActivity());
		httpHandler = http.send(HttpRequest.HttpMethod.GET, url,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						lodingDialog.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						lodingDialog.dismiss();
						back_result = "" + arg0.result;
						try {
							Gson gson = new Gson();
							foundlistitem = gson.fromJson(back_result,
									FoundListItem.class);
							Found found = new Found();
							if (foundlistitem != null) {
								list_children = foundlistitem
										.getCarcommunityarticlelist();
								if (page == 1) {
									FindTop findTop = foundlistitem
											.getCarheadvo();
									if (findTop != null) {
										found.setId(findTop.id);
										found.setArticleImg_url(findTop.headimgurl);
										found.setType(findTop.headtype);
										found.setArticleTitle(findTop.headtitle);
										list.add(found);
									}
								}
								list.addAll(list_children);
								adapter.notifyDataSetChanged();
							}
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
							Toast.makeText(getActivity(), "解析失败",
									Toast.LENGTH_SHORT).show();
						}
						onStopLoad();
						if (list_children != null && list_children.size() >= 10) {// 如果不足，表示没有下一页
							mListView.setPullLoadEnable(true);
						} else {
							mListView.setPullLoadEnable(false);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						lodingDialog.dismiss();
						Toast.makeText(getActivity(), "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}
				});

	}

	// 刷新
	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				list.clear();
				page = 1;
				GetFoundInfo(URLCst.HOME_LIST + page);
			}
		}, 2000);
	}

	// 加载更多
	@Override
	public void onLoadMore() {
		page++;
		GetFoundInfo(URLCst.HOME_LIST + page);
	}

	public class FoundAdapter extends BaseAdapter {

		Context context;
		List<Found> list;

		public FoundAdapter(Context context, List<Found> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {
				return 1;
			}
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
			BitmapUtils bitmapUtils = new BitmapUtils(
					FindFragment.this.getActivity());
			viewHolder = null;
			topViewHolder = null;
			switch (getItemViewType(position)) {
				case 0:
					if (null == convertView) {
						topViewHolder = new TopViewHolder();
						convertView = LayoutInflater.from(context).inflate(
								R.layout.find_listview_top_item, null);
						topViewHolder.imageViewTop = (ImageView) convertView
								.findViewById(R.id.pub_ui_find_head_iv);
						topViewHolder.tag_iv = (ImageView) convertView
								.findViewById(R.id.pub_ui_find_head_icon);
						topViewHolder.title_top_tv = (TextView) convertView
								.findViewById(R.id.title_tv_find);

						convertView.setTag(topViewHolder);
					} else {
						topViewHolder = (TopViewHolder) convertView.getTag();
					}
					FindTop top = foundlistitem.getCarheadvo();
					if (top != null) {
						topViewHolder.title_top_tv.setText(top.headtitle);
						if (top.headtype.equals("资讯")) {
							topViewHolder.tag_iv
									.setBackgroundResource(R.drawable.pub_ui_find_info);
						} else if (top.headtype.equals("百科")) {
							topViewHolder.tag_iv
									.setBackgroundResource(R.drawable.pub_ui_find_ask);
						} else if (top.headtype.equals("新闻")) {
							topViewHolder.tag_iv
									.setBackgroundResource(R.drawable.pub_ui_find_store);
						} else {
							topViewHolder.tag_iv
									.setBackgroundResource(R.drawable.pub_ui_find_active);
						}
						BitmapDisplayConfig config = new BitmapDisplayConfig();
						config.setLoadFailedDrawable(getResources().getDrawable(
								R.drawable.default2));
						config.setLoadingDrawable(getResources().getDrawable(
								R.drawable.image_default));// 图片加载中
						bitmapUtils.display(topViewHolder.imageViewTop,
								top.headimgurl, config);
					}
					break;
				case 1:
					if (null == convertView) {
						viewHolder = new ViewHolder();
						convertView = LayoutInflater.from(context).inflate(
								R.layout.item_found, null);
						viewHolder.imageView = (ImageView) convertView
								.findViewById(R.id.news_img);
						viewHolder.title_tv = (TextView) convertView
								.findViewById(R.id.news_title);
						viewHolder.content_tv = (TextView) convertView
								.findViewById(R.id.news_pro);
						viewHolder.tag_iv = (ImageView) convertView
								.findViewById(R.id.newstype_img);
						convertView.setTag(viewHolder);

					} else {
						viewHolder = (ViewHolder) convertView.getTag();
					}

					String source = "<strong><p style='font-weight:bold;'>"
							+ list.get(position).getArticleTitle() + "</p><stong>";
					viewHolder.title_tv.setText(Html.fromHtml(source));

					viewHolder.content_tv.setText(list.get(position)
							.getArticleAbstract());

					if (list.get(position).getType().equals("资讯")) {
						viewHolder.tag_iv
								.setBackgroundResource(R.drawable.pub_ui_find_info);
					} else if (list.get(position).getType().equals("百科")) {
						viewHolder.tag_iv
								.setBackgroundResource(R.drawable.pub_ui_find_ask);
					} else if (list.get(position).getType().equals("新闻")) {
						viewHolder.tag_iv
								.setBackgroundResource(R.drawable.pub_ui_find_store);
					} else {
						viewHolder.tag_iv
								.setBackgroundResource(R.drawable.pub_ui_find_active);
					}
					BitmapDisplayConfig config = new BitmapDisplayConfig();
					config.setLoadFailedDrawable(getResources().getDrawable(
							R.drawable.default2));
					config.setLoadingDrawable(getResources().getDrawable(
							R.drawable.image_default));// 图片加载中
					bitmapUtils.display(viewHolder.imageView, list.get(position)
							.getArticleImg_url(), config);
					break;
			}

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView imageView;
		TextView title_tv;
		TextView content_tv;
		ImageView tag_iv;
	}

	private class TopViewHolder {
		ImageView imageViewTop;
		TextView title_top_tv;
		ImageView tag_iv;
	}

	@Override
	public void initData() {
		onRefresh();
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.app_fragment_find, null);
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
