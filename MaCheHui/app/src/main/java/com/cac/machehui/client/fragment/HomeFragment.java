package com.cac.machehui.client.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.CarInfoInputActivity;
import com.cac.machehui.client.activity.CarInsuranceActivity;
import com.cac.machehui.client.activity.DriverServiceActivity;
import com.cac.machehui.client.activity.LoginActivity;
import com.cac.machehui.client.activity.MessageListActivity;
import com.cac.machehui.client.activity.PointsActivity;
import com.cac.machehui.client.activity.StopListActivity;
import com.cac.machehui.client.activity.WashActivity;
import com.cac.machehui.client.adapter.FragmentHomeAdapter;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.cst.WashCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.utils.DateUtil;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HomeFragment extends BaseFragment {
	// 轮番图的容器
	private ViewPager viewPager;
	// 填充的内容
	private List<ImageView> listImages;
	// 底部焦点
	private List<ImageView> dots;
	// 底部的焦点部分
	private int dosId[];
	// // 假数据轮番图的ID
	// private int[] images;
	// ViewPager的数据适配器
	private FragmentHomeAdapter adapter;

	// 记录底部当前焦点的位置
	private int curPosition;
	// 上一次焦点所在的位置
	private int oldPosition = 0;

	private GridView gridView;

	private int imagesId[];

	private int textId[];

	private InternalHandler internalHandler;

	private int currPage = 0;

	private int width;
	private int height;

	// /private xUtilsImageLoader loader;

	private SharedPreferences sp;

	private String city = "";

	private ArrayList<String> homeView;// 主页图片的名字list

	private EditText editText;
	private HttpUtils httpUtils;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private ImageView iv_message;
	private TextView tv_qiandao;
	private String username;
	private String userid;
	private String token;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获得sp信息
		sp = HomeFragment.this.getActivity().getSharedPreferences(
				"currentUser", Context.MODE_PRIVATE);

		username = sp.getString("usernames", "");
		bitmapUtils = new BitmapUtils(getActivity());
		config = new BitmapDisplayConfig();
		config.setLoadFailedDrawable(getActivity().getResources().getDrawable(
				R.drawable.default2));
		config.setLoadingDrawable(getActivity().getResources().getDrawable(
				R.drawable.detail_default));
		httpUtils = new HttpUtils();
		homeView = AppClient.homeView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (homeView == null) {
			LoadImageUrl();
		}
		currentTime = sp.getString("qianDaoTime", "");
		if (currentTime.equals(DateUtil.getCurDateStr("yyyy-MM-dd"))) {
			tv_qiandao.setTextColor(getActivity().getResources().getColor(
					R.color.text_gray));
			tv_qiandao.setText("已签到");
			tv_qiandao.setClickable(false);
		} else {
			tv_qiandao.setText("签到");
			tv_qiandao.setClickable(true);
		}
		userid = sp.getString("userid", "");
		token = sp.getString("token", "");
		if (!TextUtils.isEmpty(userid)) {
			hasMsg();
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.e("ll", "HomeFragment:onViewCreated");
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		// 搜索框

		editText = (EditText) view.findViewById(R.id.edit_search);
		editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					AppClient.fuzzyshop = v.getText().toString();
					Intent intent = new Intent(getActivity(),
							WashActivity.class);
					intent.putExtra(WashCst.FIRSTINPUT, city);
					intent.putExtra(WashCst.FIRSTTYPE, WashCst.WASH_CLEAN);
					intent.putExtra(WashCst.MODIFYTYPE, "全部");
					getActivity().startActivity(intent);
				}
				return false;
			}
		});
		iv_message = (ImageView) view.findViewById(R.id.pub_ui_home_iv);
		iv_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转至消息推送页面
				Intent intent = new Intent(getActivity(),
						MessageListActivity.class);
				startActivity(intent);

			}
		});
		tv_qiandao = (TextView) view
				.findViewById(R.id.qiandao_tv_home_fragment);
		tv_qiandao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				username = sp.getString("usernames", "");
				userid = sp.getString("userid", "");
				token = sp.getString("token", "");
				if (TextUtils.isEmpty(userid)) {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				} else {
					if (NetworkUtil.hasInternetConnected(getActivity())) {
						getDataFromServer();
					} else {
						Toast.makeText(getActivity(), "您的网络离家出走了，请检查重试",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

		});
		// 初始化控件
		initRes(view);

		// 初始化9个按钮的图标
		initGirdView(view);

		if (internalHandler == null) {
			internalHandler = new InternalHandler();
		} else {
			// 把队列中的所有消息和任务全部清除出队列
			internalHandler.removeCallbacksAndMessages(null);
		}
		// 延时3秒钟, 执行InternalRunnable任务类中的run方法
		internalHandler.postDelayed(new InternalRunnable(), 3000);
	}

	/**
	 * 签到 从服务器获取数据
	 */
	protected void getDataFromServer() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("creater", "androidapp");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("activity", "everydayscore");
		lodingDialog = LodingDialog.createDialog(getActivity());
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.QIANDAO, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						super.onStart();
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
						lodingDialog.dismiss();
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(getActivity(), "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							Gson gson = new Gson();
							TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
							};
							BaseBean scoreBean = gson.fromJson(result,
									type.getType());
							switch (scoreBean.typecode) {
								case "-1":
									Toast.makeText(getActivity(), "登录失效，请重新登录",
											Toast.LENGTH_SHORT).show();
									break;
								case "200":
								case "202":
									changeQianDaoState(scoreBean);
									break;
								default:
									Toast.makeText(getActivity(), "服务器错误",
											Toast.LENGTH_SHORT).show();
									break;
							}
						}
					}
				});
	}

	/***
	 * 改变签到状态
	 */
	private void changeQianDaoState(BaseBean scoreBean) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("qianDaoTime", DateUtil.getCurDateStr("yyyy-MM-dd"));
		editor.commit();
		tv_qiandao.setTextColor(getActivity().getResources().getColor(
				R.color.text_gray));
		tv_qiandao.setText("已签到");
		tv_qiandao.setClickable(false);
		showQiandaoDialog(scoreBean.msg);
	}

	/**
	 * 是否有未读消息
	 */
	private void hasMsg() {
		httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("userid", userid);
		httpHandler = httpUtils.send(HttpMethod.POST, URLCst.HAS_MSG, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(getActivity(), "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(getActivity(), "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							Gson gson = new Gson();
							TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
							};
							BaseBean scoreBean = gson.fromJson(result,
									type.getType());
							switch (scoreBean.typecode) {
								case "-1":
									Toast.makeText(getActivity(), "登录失效，请重新登录",
											Toast.LENGTH_SHORT).show();
									break;
								case "200":
									if (getActivity() != null) {
										iv_message
												.setImageDrawable(getActivity()
														.getResources()
														.getDrawable(
																R.drawable.pub_ui_home_letter_s));
									}
									break;
								case "198":
									if (getActivity() != null) {
										iv_message
												.setImageDrawable(getActivity()
														.getResources()
														.getDrawable(
																R.drawable.pub_ui_home_letter_h));
									}
									break;
								default:
									Toast.makeText(getActivity(), "服务器错误",
											Toast.LENGTH_SHORT).show();
									break;
							}
						}
					}
				});
	}

	/**
	 * 显示签到的对话框
	 */
	@SuppressLint("InflateParams")
	protected void showQiandaoDialog(String msg) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.home_qiandoa_dialog, null, true);
		final Dialog dialog = new Dialog(getActivity(),
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view);
		TextView tv_msg = (TextView) view.findViewById(R.id.msg_tv_qiandao);
		tv_msg.setText(msg);
		Button btn_confirm = (Button) view
				.findViewById(R.id.confirm_btn_qiandao_dialog);
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void initRes(View view) {
		viewPager = (ViewPager) view
				.findViewById(R.id.pub_ui_frag_itemViewPager);
		android.widget.RelativeLayout.LayoutParams param = new android.widget.RelativeLayout.LayoutParams(
				width, height * 3 / 10);
		viewPager.setLayoutParams(param);
		listImages = new ArrayList<ImageView>();

		// images = new int[] { R.drawable.pub_ui_turn_01,
		// R.drawable.pub_ui_turn_01, R.drawable.pub_ui_turn_01,
		// R.drawable.pub_ui_turn_01 };
		dosId = new int[] { R.id.pub_ui_iv_dot1, R.id.pub_ui_iv_dot2,
				R.id.pub_ui_iv_dot3, R.id.pub_ui_iv_dot4

		};
		android.view.ViewGroup.LayoutParams ivP = new android.view.ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 初始化ViewPager的元素 填充数据
		listImages = new ArrayList<ImageView>();
		for (int i = 0; i < 4; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(1000, height));
			imageView.setLayoutParams(ivP);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			// 部署图片 拼接url
			if (homeView != null && homeView.size() == 4) {// 后期四张图片数目改变后要处理的
				bitmapUtils.display(imageView, homeView.get(i), config);
				Log.v("!!!", homeView.get(i));
			}
			listImages.add(imageView);
		}

		// 初始化底部的焦点
		dots = new ArrayList<ImageView>();
		for (int i = 0; i < dosId.length; i++) {

			ImageView iv = (ImageView) view.findViewById(dosId[i]);
			dots.add(iv);
		}

		adapter = new FragmentHomeAdapter(listImages);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(onPageChangeListener);
	}

	// 设置底部的焦点和当前的界面一致
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			dots.get(position).setBackgroundResource(R.drawable.pub_ui_dot_s);
			dots.get(oldPosition)
					.setBackgroundResource(R.drawable.pub_ui_dot_h);

			oldPosition = position;

			currPage = position;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	// ------------------------轮播图适配器-----------------------↑

	class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// 当前是在主线程中, 把轮播图切换到下一页面
			currPage = (currPage + 1) % dosId.length;
			viewPager.setCurrentItem(currPage);

			// 递归: postDelayed -> InternalRunnable.run -> sendEmptyMessage ->
			// handleMessage
			internalHandler.postDelayed(new InternalRunnable(), 3000);
		}
	}

	class InternalRunnable implements Runnable {

		@Override
		public void run() {
			internalHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initGirdView(View view) {
		imagesId = new int[] { R.drawable.pub_ui_clean,
				R.drawable.pub_ui_driver, R.drawable.pub_ui_stop,
				R.drawable.pub_ui_repair, R.drawable.pub_ui_casoline,
				R.drawable.pub_ui_comse, R.drawable.pub_ui_qlllegal,
				R.drawable.pub_ui_autoinsur, R.drawable.pub_ui_activite };
		textId = new int[] { R.string.pub_ui_fragment_home_clear,
				R.string.pub_ui_fragment_home_driver,
				R.string.pub_ui_fragment_home_stop,
				R.string.pub_ui_fragment_home_repair,
				R.string.pub_ui_fragment_home_comseline,
				R.string.pub_ui_fragment_home_comse,
				R.string.pub_ui_fragment_home_qlleag,
				R.string.pub_ui_fragment_home_autoinsure,
				R.string.pub_ui_fragment_home_active };
		gridView = (GridView) view.findViewById(R.id.gridview);
		GirdViewAdapter adapter = new GirdViewAdapter(getActivity());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				switch (position) {
					case 0:
						Intent intentWASH = new Intent(getActivity(),
								WashActivity.class);

						intentWASH.putExtra(WashCst.FIRSTINPUT, city);
						intentWASH.putExtra(WashCst.FIRSTTYPE, WashCst.WASH_CLEAN);
						intentWASH.putExtra(WashCst.MODIFYTYPE, "洗车");
						startActivity(intentWASH);
						break;
					case 1:
						// 代驾
						Intent intentDriver = new Intent(getActivity(),
								DriverServiceActivity.class);
						startActivity(intentDriver);
						break;

					case 2:
						Intent intentSTOP = new Intent(getActivity(),
								StopListActivity.class);
						intentSTOP.putExtra("fromStop", true);
						startActivity(intentSTOP);
						break;
					case 3:
						Intent intentRepair = new Intent(getActivity(),
								WashActivity.class);
						intentRepair.putExtra(WashCst.FIRSTINPUT, city);
						intentRepair.putExtra(WashCst.FIRSTTYPE,
								WashCst.WASH_REAPIR);
						intentRepair.putExtra(WashCst.MODIFYTYPE, "维修保养");
						startActivity(intentRepair);
						break;
					case 4:
						Intent intentGas = new Intent(getActivity(),
								StopListActivity.class);
						intentGas.putExtra("fromStop", false);
						startActivity(intentGas);
						break;
					case 5:
						Intent intentCose = new Intent(getActivity(),
								WashActivity.class);
						intentCose.putExtra(WashCst.FIRSTINPUT, city);
						intentCose.putExtra(WashCst.FIRSTTYPE, WashCst.WASH_COME);
						intentCose.putExtra(WashCst.MODIFYTYPE, "美容");
						startActivity(intentCose);
						break;
					case 6:
						Intent intent = new Intent(getActivity(),
								CarInfoInputActivity.class);
						startActivity(intent);

						break;
					case 7:
						Intent intentCarInsurance = new Intent(getActivity(),
								CarInsuranceActivity.class);
						startActivity(intentCarInsurance);
						break;
					case 8:
						username = sp.getString("usernames", "");
						Intent intentPoints;
						if (TextUtils.isEmpty(username)) {
							intentPoints = new Intent(getActivity(),
									LoginActivity.class);
						} else {
							intentPoints = new Intent(getActivity(),
									PointsActivity.class);
						}
						startActivity(intentPoints);
						break;
				}
			}
		});

	}

	// 表格的适配器
	private class GirdViewAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;

		public GirdViewAdapter(Context context) {
			this.context = context;
			inflater = inflater.from(context);
		}

		@Override
		public int getCount() {
			return imagesId.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			android.widget.AbsListView.LayoutParams param = new android.widget.AbsListView.LayoutParams(
					width / 3, height * 5 / 10 / 3);
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.app_fragment_home_gridview_item, null);
				convertView.setLayoutParams(param);
				holder = new ViewHolder();
				holder.view = (ImageView) convertView
						.findViewById(R.id.function_view);

				holder.tv = (TextView) convertView
						.findViewById(R.id.function_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.view.setImageResource(imagesId[position]);
			holder.tv.setText(textId[position]);
			return convertView;
		}

		private class ViewHolder {
			ImageView view;
			TextView tv;
		}
	}

	// 加载界面加载网络数据
	private void LoadImageUrl() {
		Log.v("!!!", "访问图片了");
		httpUtils.send(HttpMethod.GET, URLCst.IMG_LIST,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(getActivity(), "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						homeView = new ArrayList<String>();
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							if (jsonObject.getInt("errorCode") == 200) {
								JSONArray array = jsonObject
										.getJSONArray("homeimglist");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = (JSONObject) array.get(i);
									homeView.add(obj.getString("homeimg"));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getActivity(), "解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
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
		// 把队列中的所有消息和任务全部清除出队列
		internalHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void initData() {

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.app_fragment_home, null);
	}
}
