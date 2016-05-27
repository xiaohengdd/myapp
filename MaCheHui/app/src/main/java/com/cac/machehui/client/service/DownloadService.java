package com.cac.machehui.client.service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.activity.VersionUpdateActivity.ICallbackResult;
import com.cac.machehui.client.activity.WelcomeActivity;
import com.cac.machehui.client.utils.CustomToast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class DownloadService extends Service {
	private static final int NOTIFY_ID = 01201;
	/** 下载进度条 **/
	private int progress;
	private NotificationManager mNotificationManager;
	/** 是否取消下载 true表示取消下载 **/
	private boolean canceled;
	/** 安装包url **/
	private String apkUrl = "";
	/** 安装包md5 **/
	// private String md5 = "";
	/** 下载包安装路径 **/
	private String savePath = "";
	/** 下载包安装文件路径 **/
	private String saveFileName = "";
	/** 回调结果 **/
	private ICallbackResult callback;
	/** 下载的Binder **/
	private DownloadBinder binder;
	/** 下载服务是否挂掉 true表示已经挂了，默认false **/
	private boolean serviceIsDestroy = false;
	private Context context;
	/** 下载完成后的apk文件 **/
	private File apkfile;
	private HttpHandler<File> downHandler;
	private Context mContext = this;
	/** 下载失败重试的次数 **/
	private int reDownCount = 0;

	@Override
	public IBinder onBind(Intent intent) {
		apkUrl = intent.getStringExtra("apkUrl");
		saveFileName = intent.getStringExtra("saveFileName");
		savePath = intent.getStringExtra("savePath");
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		binder = new DownloadBinder();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		stopForeground(true);// 这个不确定是否有作用
	}

	public class DownloadBinder extends Binder {
		public void start() {
			if (downHandler == null || downHandler.isCancelled()) {
				progress = 0;
				setUpNotification();
				// 下载
				canceled = false;
				downloadApk();
			}
		}

		public void cancel() {
			canceled = true;
		}

		public int getProgress() {
			return progress;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public boolean serviceIsDestroy() {
			return serviceIsDestroy;
		}

		public void cancelNotification() {
			// 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
			// 取消通知
			mNotificationManager.cancel(NOTIFY_ID);
		}

		public void addCallback(ICallbackResult callback) {
			DownloadService.this.callback = callback;
		}

		public void retryDownload() {
			downloadApk();
		}
	}

	// 通知栏
	Notification mNotification;

	/**
	 * 创建通知
	 */
	private void setUpNotification() {
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.name, "新版本正在下载...");
		// 指定个性化视图
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, WelcomeActivity.class);
		// 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
		// 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 指定内容意图
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}

	/**
	 *
	 * 下载完成.
	 *
	 * @author 史明松
	 * @update 2014年6月26日 下午1:36:18
	 */
	private void downloadComplete() {
		// 下载完毕后变换通知形式
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.contentView = null;
		Intent intent = new Intent(mContext, WelcomeActivity.class);
		// 告知已完成
		intent.putExtra("completed", "yes");
		// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕,请安装！",
				contentIntent);
		serviceIsDestroy = true;
		stopSelf();// 停掉服务自身
		mNotificationManager.notify(NOTIFY_ID, mNotification);
		// 下载完毕 取消通知
		mNotificationManager.cancel(NOTIFY_ID);
		// 下载完了，cancelled也要设置
		canceled = true;
	}

	/**
	 * 下载apk.
	 *
	 * @author 史明松
	 * @update 2014年6月20日 下午7:21:38
	 */
	private void downloadApk() {
		// 弹出下载界面的对话框，下载完之后替换安装
		HttpUtils http = new HttpUtils();
		http.configUserAgent("Android");
		downHandler = http.download(apkUrl, saveFileName, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onLoading(long total, long current,
										  boolean isUploading) {
						int progress = (int) (((float) current / total) * 100);
						String message = "";
						if (progress < 100) {
							message = "已下载" + progress + "%";
						} else {
							message = "恭喜，已下载完成,请安装！";
						}
						callback.OnBackResult(progress, message);
						RemoteViews contentview = mNotification.contentView;
						contentview.setTextViewText(R.id.tv_progress, message);
						contentview.setProgressBar(R.id.progressbar, 100,
								progress, false);
						mNotificationManager.notify(NOTIFY_ID, mNotification);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// maybe the file has downloaded completely
						if (error.getExceptionCode() == 416) {
							apkfile = new File(saveFileName);
							// if (md5.equals(getFileMD5(apkfile))) {

							downloadComplete();
							installApk();
							// return;
							// }
							apkfile.delete();
						}
						/** 如果次数耗尽就终止下载服务，清除通知栏任务 **/
						if (reDownCount == 2) {
							// 下载完毕 取消通知
							mNotificationManager.cancel(NOTIFY_ID);
							CustomToast.showToast(context,
									"网络或服务器发生未知经常，下载失败！", Toast.LENGTH_LONG);
							stopSelf();// 停掉服务自身
							callback.OnBackResult(null,
									ICallbackResult.BACK_RESULT_FINISH);
							return;
						}
						/** 如果下载失败程序就不断递归重试直到下载完成 **/
						reDownCount = reDownCount + 1;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
						downloadApk();
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						apkfile = responseInfo.result;
						// // if (!md5.equals(getFileMD5(apkfile))) {
						// apkfile.delete();
						// mNotificationManager.cancel(NOTIFY_ID);
						// callback.OnBackResult(null,
						// ICallbackResult.BACK_RESULT_FAILED);
						// // return;
						// // }
						downloadComplete();
						installApk();
						CustomToast.showToast(context, "下载完成！",
								Toast.LENGTH_LONG);
					}
				});

	}

	/**
	 *
	 * 安装apk .
	 *
	 * @author 史明松
	 * @update 2014年6月20日 下午7:20:40
	 */
	private void installApk() {
		/** 如果文件为空或者不存在就重新开启下载 **/
		if (apkfile == null || !apkfile.exists()) {
			downloadApk();
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
		callback.OnBackResult(null, ICallbackResult.BACK_RESULT_FINISH);
	}

	public String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
}
