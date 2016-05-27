package com.cac.machehui.client.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.URLCst;
import com.cac.machehui.client.entity.VersionBean;
import com.cac.machehui.client.entity.VersionResultBean;
import com.cac.machehui.client.service.DownloadService.DownloadBinder;
import com.cac.machehui.client.view.ConfirmDialog;
import com.cac.machehui.client.view.ConfirmDialog.OnButton1ClickListener;
import com.cac.machehui.client.view.ConfirmDialog.OnButton2ClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 检查版本更新
 */
public class CheckVersion {
	private Context context;
	/** 下载Service是否绑定 **/
	protected boolean downServiceIsBinded;
	/** 下载DownloadBinder **/
	protected DownloadBinder downloadBinder;
	private String savePath;
	private VersionBean newVersionBean;
	private CheckVersionListener checkVersionListener;
	private IntentToHome intentToHome;
	private boolean isCancle;

	public CheckVersion(Context context,
						CheckVersionListener checkVersionListener, IntentToHome intentToHome) {
		this.context = context;
		this.checkVersionListener = checkVersionListener;
		this.intentToHome = intentToHome;
		checkStoragePathAndSetBaseApp();

	}

	public void checkUpdate() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("os", "0");
		params.addBodyParameter("versionCode", getApkVersionCode() + "");
		params.addBodyParameter("appType", "1");// appType : 0:商户端 1: 客户端
		httpUtils.send(HttpMethod.POST, URLCst.UPDATE_VERSION, params,// 检查版本更新url
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						intentToHome.toHome();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							Gson gson = new Gson();
							TypeToken<VersionResultBean> type = new TypeToken<VersionResultBean>() {
							};
							VersionResultBean vResultBean = gson.fromJson(
									arg0.result, type.getType());
							switch (vResultBean.typecode) {
								case "000":// 没有更新
									intentToHome.toHome();
									break;
								case "200":
									if (!isCancle) {
										List<VersionBean> versionBeans = vResultBean.appUpdateList;
										if (versionBeans != null) {
											newVersionBean = versionBeans.get(0);
											checkVersionListener.toUpdate(
													newVersionBean, savePath);
										}
									}
									break;
								case "201":
									intentToHome.toHome();
									Toast.makeText(context, "检查更新失败",
											Toast.LENGTH_SHORT).show();
									break;
								default:
									intentToHome.toHome();
									break;
							}
						} catch (JsonSyntaxException e) {
							intentToHome.toHome();
							Toast.makeText(context, "解析异常", Toast.LENGTH_SHORT)
									.show();
						}
					}

				});
	}

	/**
	 *
	 * 功能: 获取应用的版本Code
	 *
	 * @return
	 */
	private int getApkVersionCode() {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (Exception e) {

		}
		return 0;
	}

	/**
	 * 验证手机最大可用存储，并设置成当前应用根路径.
	 */
	public boolean checkStoragePathAndSetBaseApp() {
		String storagePath = null;
		List<Long> memorySize = null;
		Map<Long, String> storageMap = new HashMap<Long, String>();
		// LongSparseArray<String> storageMap = new LongSparseArray<String>();

		// 如果可以检测到SD卡返回SD卡路径，否则就反射得到最大可以用的机身存储
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) == true) {
			storagePath = android.os.Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			StorageManager sm = (StorageManager) context
					.getSystemService(Context.STORAGE_SERVICE);
			String[] paths;
			try {
				paths = (String[]) sm.getClass()
						.getMethod("getVolumePaths", null).invoke(sm, null);
				for (String path : paths) {
					StatFs stat = new StatFs(path);
					long blockSize = stat.getBlockSize();
					long availableBlocks = stat.getAvailableBlocks();
					long storageSize = availableBlocks * blockSize;
					if (storageSize > 0) {
						memorySize = new ArrayList<Long>();
						memorySize.add(storageSize);
						storageMap.put(storageSize, path);
					}
				}
				if (memorySize != null)
					storagePath = storageMap.get(Collections.max(memorySize));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (storagePath == null) {
			storagePath = context.getFilesDir().getAbsolutePath();
		}

		if (storagePath != null) {
			savePath = storagePath + "/download/";
			return true;
		} else {
			ConfirmDialog dialog = ConfirmDialog.createDialog(context);
			dialog.setDialogTitle("提示");
			dialog.setCancelable(false);
			dialog.setDialogMessage("请检查有无可用存储卡");
			dialog.setOnButton1ClickListener("确定", null,
					new OnButton1ClickListener() {
						@Override
						public void onClick(View view, DialogInterface dialog) {
							dialog.cancel();
							Intent intent = new Intent(Settings.ACTION_SETTINGS);
							context.startActivity(intent);
							((Activity) context).finish();
						}
					});
			dialog.setOnButton2ClickListener("退出", R.color.text_gray,
					new OnButton2ClickListener() {
						@Override
						public void onClick(View view, DialogInterface dialog) {
							dialog.cancel();
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(1);
						}
					});
			dialog.show();
			return false;
		}
	}

	public boolean cancle() {
		isCancle = true;
		return isCancle;
	}

	public interface CheckVersionListener {
		public void toUpdate(VersionBean newVersionBean, String savePath);
	}

	public interface IntentToHome {
		public void toHome();
	}
}
