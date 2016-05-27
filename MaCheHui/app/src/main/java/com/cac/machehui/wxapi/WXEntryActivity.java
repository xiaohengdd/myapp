package com.cac.machehui.wxapi;

import net.sourceforge.simcpux.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.BaseActivity;
import com.cac.machehui.client.activity.BundMobileActivity;
import com.cac.machehui.client.activity.HomeActivity;
import com.cac.machehui.client.cst.AppClient;
import com.cac.machehui.client.cst.KeyValues;
import com.cac.machehui.client.cst.MaCheHuiConstants;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.User;
import com.cac.machehui.client.utils.CustomToast;
import com.cac.machehui.client.utils.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	private IWXAPI api;
	private HttpUtils httpUtils;
	private SharedPreferences sp;
	private Intent intent;
	private String userId;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.user_wx_wxentry_activity);
		initView();
		initData();
	}

	private void initView() {
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	private void initData() {
		httpUtils = new HttpUtils();
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		api.handleIntent(getIntent(), this);
		sp = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		userId = sp.getString("userId", "");
		token = sp.getString("token", "");
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_AUTH_DENIED:// 用户拒绝授权,如果微信客户端被挤掉
				if (api.registerApp(Constants.APP_ID)) {
					Toast.makeText(this, "拒绝授权", Toast.LENGTH_SHORT).show();
					api.unregisterApp();
					api.detach();
					finish();
				}
				break;
			case BaseResp.ErrCode.ERR_OK:
				String code = ((SendAuth.Resp) resp).code;
				if (NetworkUtil.hasInternetConnected(WXEntryActivity.this)) {
					getAccessTokenFromWx(code);
				}
				break;
			default:
				finish();
				break;
		}
	}

	private void getAccessTokenFromWx(String code) {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("appid", Constants.APP_ID);
		params.addQueryStringParameter("code", code);
		params.addQueryStringParameter("secret", MaCheHuiConstants.WX_SECRET);
		params.addQueryStringParameter("grant_type",
				MaCheHuiConstants.GRANT_TYPE);
		httpUtils.send(HttpMethod.GET, URLCst.WX_GET_TOKEN_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						handlerSuccess(responseInfo);
						if (NetworkUtil
								.hasInternetConnected(WXEntryActivity.this)) {
							getUserInfo();
						}
					}

				});
	}

	protected void getUserInfo() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("access_token",
				KeyValues.wx_access_token);
		params.addQueryStringParameter("openid", KeyValues.wx_open_id);
		httpUtils.send(HttpMethod.GET, URLCst.WX_GET_USER_INFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(WXEntryActivity.this, "连接微信服务器失败",
								Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						handlerSuccessWxInfo(responseInfo);
						if (NetworkUtil
								.hasInternetConnected(WXEntryActivity.this)) {
							switch (AppClient.getInstance().getWeixinFrom()) {
								case 0:// 登录页面
									getDataFromServer();
									break;
								case 1:// 个人资料页面
									bundWeiXin();
									break;
							}
						} else {
							Toast.makeText(WXEntryActivity.this,
									"您的网络离家出走了，请检查重试", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

	}

	/**
	 * 个人信息微信绑定
	 */
	protected void bundWeiXin() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("openid", KeyValues.wx_open_id);
		params.addBodyParameter("token", token);
		params.addBodyParameter("userid", userId);
		params.addBodyParameter("unionid", KeyValues.wx_union_id);
		httpUtils.send(HttpMethod.POST, URLCst.BUND_AFTER_LOGIN_WX, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(WXEntryActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							switch (typeCode) {
								case "-1":
									Toast.makeText(WXEntryActivity.this,
											"登录失效，请重新登录", Toast.LENGTH_SHORT)
											.show();
									finish();
									break;
								case "1":
									sp = getSharedPreferences("currentUser",
											Context.MODE_PRIVATE);
									// 登录后存储信息 数据要存储完全，个人资料里面要用
									SharedPreferences.Editor editor = sp.edit();
									editor.putString("bundWx", "1");
									editor.commit();
									Toast.makeText(WXEntryActivity.this, "绑定成功",
											Toast.LENGTH_SHORT).show();
									finish();
									break;
								case "0":
									Toast.makeText(WXEntryActivity.this, "绑定失败",
											Toast.LENGTH_SHORT).show();
									finish();
									break;
								default:
									CustomToast.showToast(WXEntryActivity.this,
											"系统错误,请重试", Toast.LENGTH_SHORT);
									finish();
									break;

							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(WXEntryActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();
							WXEntryActivity.this.finish();
						}
					}
				});

	}

	/**
	 * 微信登录
	 */
	protected void getDataFromServer() {
		RequestParams params = new RequestParams();
		httpUtils.configResponseTextCharset("UTF-8");
		params.addBodyParameter("openid", KeyValues.wx_open_id);
		params.addBodyParameter("unionid", KeyValues.wx_union_id);
		httpUtils.send(HttpMethod.POST, URLCst.LOGIN_WX, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(WXEntryActivity.this, "请求服务器失败，请重试",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String typeCode = jsonObject.getString("typecode");
							switch (typeCode) {
								case "1":
									Gson gson = new Gson();
									TypeToken<User> type = new TypeToken<User>() {
									};
									User user = gson.fromJson(jsonObject
											.getJSONObject("user").toString(), type
											.getType());
									if (user != null) {
										sp = getSharedPreferences("currentUser",
												Context.MODE_PRIVATE);
										// 登录后存储信息 数据要存储完全，个人资料里面要用
										SharedPreferences.Editor editor = sp.edit();
										editor.putString("id", user.getId());
										editor.putString("usernames",
												"" + user.getUsername());
										editor.putString("headUrl",
												user.getUserheadurl());
										editor.putString("password",
												user.getPassword());
										editor.putString("nickname",
												user.getNickname());
										editor.putString("vipIdentify",
												user.getVip());
										editor.putString("sex", user.getUsersex());
										editor.putString("bundWx",
												user.getWeixintype());
										editor.putString("userId", user.getUserid());
										editor.putString("userid", user.getUserid());
										editor.putString("token", user.getToken());
										// AppClient.token = user.getToken();
										editor.commit();
										Toast.makeText(WXEntryActivity.this,
												"登录成功", Toast.LENGTH_SHORT).show();
										intent = new Intent(WXEntryActivity.this,
												HomeActivity.class);
										startActivity(intent);
										finish();
									}
									break;
								case "0":
									intent = new Intent(WXEntryActivity.this,
											BundMobileActivity.class);
									startActivity(intent);
									finish();
									break;
								default:
									Toast.makeText(WXEntryActivity.this,
											jsonObject.getString("msg"),
											Toast.LENGTH_SHORT).show();
									WXEntryActivity.this.finish();
									break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(WXEntryActivity.this, "解析异常",
									Toast.LENGTH_SHORT).show();
							WXEntryActivity.this.finish();
						}
					}
				});

	}

	protected void handlerSuccessWxInfo(ResponseInfo<String> responseInfo) {
		JSONObject result;
		try {
			result = new JSONObject(responseInfo.result.toString());
			if (result.has("unionid")) {
				KeyValues.wx_union_id = result.getString("unionid");
				KeyValues.userName = result.getString("nickname");
				if (result.has("headimgurl")) {
					KeyValues.headimgurl = result.getString("headimgurl");
				}
			} else {
				KeyValues.wx_union_id = "";
				KeyValues.userName = "";
				KeyValues.headimgurl = "";
				Toast.makeText(WXEntryActivity.this, "获取微信信息失败",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void handlerSuccess(ResponseInfo<String> responseInfo) {
		try {
			JSONObject result = new JSONObject(responseInfo.result.toString());
			if (result.has("access_token")) {
				// 获取access_token
				KeyValues.wx_access_token = result.getString("access_token");
				KeyValues.wx_open_id = result.getString("openid");
				KeyValues.wx_refresh_token = result.getString("refresh_token");
			} else {
				Toast.makeText(WXEntryActivity.this, "微信登录失败,请重新操作",
						Toast.LENGTH_SHORT).show();
				finish();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(WXEntryActivity.this, "解析异常", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

	}
}
