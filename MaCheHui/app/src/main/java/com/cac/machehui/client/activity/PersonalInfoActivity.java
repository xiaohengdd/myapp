package com.cac.machehui.client.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.simcpux.Constants;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.BaseBean;
import com.cac.machehui.client.entity.UploadHeadBean;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.NetworkUtil;
import com.cac.machehui.client.view.LodingDialog;
import com.cac.machehui.client.view.RoundRectImage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 个人信息页面
 *
 * @author
 *
 */
public class PersonalInfoActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton ib_return;
	private RelativeLayout rl_header;
	private RelativeLayout rl_nick_name;
	private RelativeLayout rl_sex;
	private RelativeLayout rl_identity;
	private RelativeLayout rl_mobile;
	private RelativeLayout rl_pwd;
	private RelativeLayout rl_bund;
	private RoundRectImage iv_header;
	private SharedPreferences sp;
	private String nickName;
	private String imgUrl;
	private String mobile;
	private String sex;

	private String vipIdentify;
	private TextView tv_nick_name;
	private TextView tv_sex;
	private TextView tv_identify;
	private TextView tv_mobile;
	private TextView tv_bund_state;
	private Button btnPIC;
	private Button btnCamera;
	private Button btnCancel;
	private File tempFile;
	private File file;
	private String URL_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private HttpUtils httpUtils;
	private Bitmap bitmap;
	private Intent intent;
	private String url;
	private String imgName;
	private String headUrl;
	private String userid;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private String bundState;
	private String bundOrNo;

	/***** 第三方app与微信通信的openapi接口 *******/
	private IWXAPI api;
	private HttpHandler<String> httpHandler;
	private LodingDialog lodingDialog;
	private String token;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.person_infor_activity);
		initView();
		iniData();
		// 创建一个以当前时间为名称的文件
		tempFile = new File(Environment.getExternalStorageDirectory(),
				getPhotoFileName());
		httpUtils = new HttpUtils();
	}

	// 使用系统当前日期加以调整作为照片的名称
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
		return dateFormat.format(date) + ".jpg";
	}

	private void iniData() {
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		bitmapUtils = new BitmapUtils(this);
		// 设定绑定状态
		String state = sp.getString("bundWx", "0");
		switch (state) {
			case "0":
				tv_bund_state.setTextColor(getResources().getColor(
						R.color.red_yello));
				tv_bund_state.setText("未绑定");
				break;
			case "1":
				tv_bund_state.setTextColor(getResources().getColor(
						R.color.text_black_gray));
				tv_bund_state.setText("已绑定");
				break;
		}
		userid = sp.getString("userId", "");

		nickName = sp.getString("nickname", "");
		token = sp.getString("token", "");
		mobile = sp.getString("usernames", "");
		headUrl = sp.getString("headUrl", "");
		sex = sp.getString("sex", "男");
		vipIdentify = sp.getString("vipIdentify", "");
		tv_nick_name.setText(nickName);
		if (!TextUtils.isEmpty(sex)) {
			tv_sex.setText(sex);
		} else {
			tv_sex.setText("男");
		}
		tv_identify.setText("vip" + vipIdentify);
		tv_mobile.setText(mobile.substring(0, 3) + "*****"
				+ mobile.substring(8, mobile.length()));
		config = new BitmapDisplayConfig();
		config.setLoadingDrawable(getResources().getDrawable(
				R.drawable.person_07));
		config.setLoadFailedDrawable(getResources().getDrawable(
				R.drawable.person_07));
		bitmapUtils.display(iv_header, headUrl, config);
	}

	private void initView() {
		ib_return = (ImageButton) findViewById(R.id.return_ib_person_info);
		ib_return.setOnClickListener(this);
		rl_header = (RelativeLayout) findViewById(R.id.header_rl_person_info);
		rl_header.setOnClickListener(this);
		rl_nick_name = (RelativeLayout) findViewById(R.id.nick_name_rl_person_info);
		rl_nick_name.setOnClickListener(this);
		rl_sex = (RelativeLayout) findViewById(R.id.sex_rl_person_info);
		rl_sex.setOnClickListener(this);
		rl_identity = (RelativeLayout) findViewById(R.id.identify_rl_person_info);
		rl_identity.setOnClickListener(this);
		rl_mobile = (RelativeLayout) findViewById(R.id.mobile_rl_person_info);
		rl_mobile.setOnClickListener(this);
		rl_pwd = (RelativeLayout) findViewById(R.id.pwd_rl_person_info);
		rl_pwd.setOnClickListener(this);
		rl_bund = (RelativeLayout) findViewById(R.id.bund_rl_person_info);
		rl_bund.setOnClickListener(this);
		iv_header = (RoundRectImage) findViewById(R.id.header_iv_person_info);
		iv_header.setCircle(true);
		tv_nick_name = (TextView) findViewById(R.id.nick_name_tv_peron_info);
		tv_sex = (TextView) findViewById(R.id.sex_tv_peron_info);
		tv_identify = (TextView) findViewById(R.id.identify_tv_peron_info);
		tv_mobile = (TextView) findViewById(R.id.mobile_tv_peron_info);
		tv_bund_state = (TextView) findViewById(R.id.bund_state_tv_peron_info);
	}

	/**
	 * 显示选择图片来源Dialog
	 */
	private void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog,
				null, true);

		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		btnPIC = (Button) view.findViewById(R.id.btnPIC);
		btnPIC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						MaCheHuiConstants.PHOTO_REQUEST_GALLERY);
			}
		});
		btnCamera = (Button) view.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("TAG", "照相");
				dialog.dismiss();
				// 调用系统的拍照功能
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				startActivityForResult(intent,
						MaCheHuiConstants.PHOTO_REQUEST_TAKEPHOTO);
			}
		});
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();

		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		bundState = tv_bund_state.getText().toString();
		intent = new Intent();
		switch (v.getId()) {
			case R.id.return_ib_person_info:
				finish();
				break;
			case R.id.header_rl_person_info:
				showDialog();
				break;
			case R.id.nick_name_rl_person_info:
				intent.setClass(PersonalInfoActivity.this,
						UpdateNicknameActivity.class);
				startActivity(intent);
				break;
			case R.id.sex_rl_person_info:
				showSelectSexDialog();
				break;
			case R.id.identify_rl_person_info:// 会员身份当前不能修改，没有下一级页面

				break;
			case R.id.mobile_rl_person_info:
				intent.setClass(PersonalInfoActivity.this,
						UpdateMobileActivity.class);
				startActivity(intent);
				break;
			case R.id.pwd_rl_person_info:
				intent.setClass(PersonalInfoActivity.this, UpdatePwdActivity.class);
				startActivity(intent);
				break;
			case R.id.bund_rl_person_info:
				switch (bundState) {
					case "已绑定":
						showUnbundDialog();
						break;
					case "未绑定":
						AppClient.getInstance().setWeixinFrom(1);
						regToWx();
						loginInWx();
						break;
				}
				break;
		}
	}

	private void showUnbundDialog() {
		View view = getLayoutInflater().inflate(R.layout.person_unbund_dialog,
				null, false);
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view);
		dialog.show();
		Button btnCancle = (Button) view
				.findViewById(R.id.cancle_btn_unbund_dialog);
		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button confirmCancle = (Button) view
				.findViewById(R.id.confirm_btn_unbund_dialog);
		confirmCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (NetworkUtil.hasInternetConnected(PersonalInfoActivity.this)) {
					toUnbund();
				} else {
					Toast.makeText(PersonalInfoActivity.this,
							"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	protected void toUnbund() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("userid", userid);
		httpUtils.send(HttpMethod.POST, URLCst.REMOVE_WX, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(PersonalInfoActivity.this,
								"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						if (TextUtils.isEmpty(result)) {
							Toast.makeText(PersonalInfoActivity.this, "返回数据异常",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean scoreBean = gson.fromJson(result,
										type.getType());
								switch (scoreBean.typecode) {
									case "-1":
										Toast.makeText(PersonalInfoActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "1":
										SharedPreferences.Editor editor = sp.edit();
										editor.putString("bundWx", "0");
										editor.commit();
										Toast.makeText(PersonalInfoActivity.this,
												"解绑成功", Toast.LENGTH_SHORT).show();
										tv_bund_state.setTextColor(getResources()
												.getColor(R.color.red_yello));
										tv_bund_state.setText("未绑定");
										break;
									default:
										CustomToast.showToast(
												PersonalInfoActivity.this,
												"系统错误，解绑失败", Toast.LENGTH_SHORT);
										break;
								}
							} catch (JsonSyntaxException e) {
								Toast.makeText(PersonalInfoActivity.this,
										"解析异常", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});

	}

	/**
	 * 启动微信登录
	 */
	private void loginInWx() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		if (NetworkUtil.hasInternetConnected(this)) {
			// send oauth request
			final SendAuth.Req req = new SendAuth.Req();
			req.scope = MaCheHuiConstants.WX_SCOPE;
			req.state = MaCheHuiConstants.WX_STATE;
			api.sendReq(req);
		}
	}

	/**
	 * 将app注册到微信
	 */
	private void regToWx() {
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
		// 将应用的AppId注册到微信
		api.registerApp(Constants.APP_ID);
	}

	@Override
	protected void onResume() {
		super.onResume();
		iniData();
	}

	/**
	 * 选择性别
	 */
	private void showSelectSexDialog() {
		View view = getLayoutInflater().inflate(
				R.layout.person_info_select_sex_dialog, null, true);
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view);
		Button btn_man = (Button) view.findViewById(R.id.man_btn_dialog);
		btn_man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				getDataFromServer("男");
			}
		});
		Button btn_woman = (Button) view.findViewById(R.id.woman_btn_dialog);
		btn_woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				getDataFromServer("女");
			}
		});
		dialog.show();
	}

	private void getDataFromServer(final String sex) {
		if (NetworkUtil.hasInternetConnected(this)) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("token", token);
			params.addBodyParameter("method", "usersex");
			params.addBodyParameter("value", sex);
			params.addBodyParameter("userid", userid);
			lodingDialog = LodingDialog.createDialog(this);
			httpUtils.send(HttpMethod.POST, URLCst.UPDATE_MESSAGE, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							lodingDialog.show();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							lodingDialog.dismiss();
							Toast.makeText(PersonalInfoActivity.this,
									"请求服务器失败，请重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							lodingDialog.dismiss();
							String result = arg0.result;
							if (TextUtils.isEmpty(result)) {
								Toast.makeText(PersonalInfoActivity.this,
										"返回数据异常", Toast.LENGTH_SHORT).show();
							} else {
								Gson gson = new Gson();
								TypeToken<BaseBean> type = new TypeToken<BaseBean>() {
								};
								BaseBean scoreBean = gson.fromJson(result,
										type.getType());
								switch (scoreBean.typecode) {
									case "-1":
										Toast.makeText(PersonalInfoActivity.this,
												"登录失效，请重新登录", Toast.LENGTH_SHORT)
												.show();
										break;
									case "1":
										SharedPreferences.Editor editor = sp.edit();
										editor.putString("sex", sex);
										editor.commit();
										tv_sex.setText(sex);
										Toast.makeText(PersonalInfoActivity.this,
												"修改成功", Toast.LENGTH_SHORT).show();
										break;
									default:
										Toast.makeText(PersonalInfoActivity.this,
												"修改失败", Toast.LENGTH_SHORT).show();
										break;
								}
							}
						}
					});
		} else {
			Toast.makeText(this, "您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case MaCheHuiConstants.PHOTO_REQUEST_TAKEPHOTO:
					startPhotoZoom(Uri.fromFile(tempFile), 150);
					break;
				case MaCheHuiConstants.PHOTO_REQUEST_GALLERY:
					if (data != null)
						startPhotoZoom(data.getData(), 150);
					break;
				case MaCheHuiConstants.PHOTO_REQUEST_CUT:
					if (data != null)
						setPicToView(data);
					break;
			}
		}
	}

	/***
	 * 调用系统的截图
	 *
	 * @param uri
	 * @param size
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);// 取消人脸识别功能

		startActivityForResult(intent, MaCheHuiConstants.PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	/**
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			bitmap = bundle.getParcelable("data");
			// 将Bitmap装换成jpg格式的文件然后进行保存
			if (bitmap.getByteCount() != 0) {
				bitmapToSdFile(bitmap);
				// 将头像上传到服务器
				if (NetworkUtil.hasInternetConnected(this)) {
					uploadHead();
				} else {
					Toast.makeText(PersonalInfoActivity.this,
							"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(PersonalInfoActivity.this, "图片裁切失败",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private void uploadHead() {
		try {
			if (file != null && file.length() != 0) {
				url = URLCst.UPDATE_HEADER;
				RequestParams params = new RequestParams("utf-8");
				if (TextUtils.isEmpty(token)) {
					CustomToast.showToast(PersonalInfoActivity.this,
							"登录失效，请重新登录", Toast.LENGTH_SHORT);
				} else {
					params.addBodyParameter("token", token);
					params.addBodyParameter("userid", userid);
					params.addBodyParameter("method", "favicon");
					params.addBodyParameter("imgname", imgName);
					params.addBodyParameter(url.replace("/", ""), file);
					lodingDialog = LodingDialog.createDialog(this);
					httpHandler = httpUtils.send(HttpMethod.POST,
							URLCst.UPDATE_HEADER, params,
							new RequestCallBack<String>() {
								@Override
								public void onStart() {
									lodingDialog.show();
								}

								@Override
								public void onFailure(HttpException arg0,
													  String arg1) {
									deleteFile();
									lodingDialog.dismiss();
									Toast.makeText(PersonalInfoActivity.this,
											"请求服务器失败", Toast.LENGTH_LONG)
											.show();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									deleteFile();
									lodingDialog.dismiss();
									String result = arg0.result;
									if (TextUtils.isEmpty(result)) {
										Toast.makeText(
												PersonalInfoActivity.this,
												"返回数据异常", Toast.LENGTH_SHORT)
												.show();
									} else {
										try {
											Gson gson = new Gson();
											TypeToken<UploadHeadBean> type = new TypeToken<UploadHeadBean>() {
											};
											UploadHeadBean uploadBean = gson
													.fromJson(result,
															type.getType());
											switch (uploadBean.typecode) {
												case "-1":
													Toast.makeText(
															PersonalInfoActivity.this,
															"登录失效，请重新登录",
															Toast.LENGTH_SHORT)
															.show();
													break;
												case "1":
													SharedPreferences.Editor editor = sp
															.edit();
													editor.putString("headUrl",
															uploadBean.headurl);
													editor.commit();
													Toast.makeText(
															PersonalInfoActivity.this,
															"修改成功",
															Toast.LENGTH_SHORT)
															.show();
													bitmapUtils.display(iv_header,
															uploadBean.headurl,
															config);
													break;
											}
										} catch (JsonSyntaxException e) {
											Toast.makeText(
													PersonalInfoActivity.this,
													"解析异常", Toast.LENGTH_SHORT)
													.show();
										}
									}

								}
							});
				}
			} else {
				Toast.makeText(PersonalInfoActivity.this, "头像文件不存在",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(PersonalInfoActivity.this,
					"异常，头像修改失败" + file.length(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 将Bitmap存到成本地的文件
	 *
	 */
	@SuppressLint("SimpleDateFormat")
	synchronized private boolean bitmapToSdFile(Bitmap photo) {
		try {
			Date date = new Date(System.currentTimeMillis());// 时间要改成用时间加上用户名
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"'IMG'_yyyyMMdd_HHmmss");
			imgName = dateFormat.format(date) + ".jpg";
			file = new File(URL_PATH + "/" + imgName);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除临时文件
	 */
	private void deleteFile() {
		if (file != null && file.exists()) {
			file.delete();
		}
		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

	/**
	 * 释放空间
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
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
