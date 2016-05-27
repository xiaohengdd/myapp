package com.cac.machehui.client.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.cac.machehui.R;
import com.cac.machehui.client.cst.Environment;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 首页加载的时候数据Service
 *
 * @author wkj
 *
 */
public class FragmentHomeService {

	private Context context;

	private HttpUtils httpUtils = null;

	private String picUrls[] = null;

	public FragmentHomeService(Context context) {
		this.context = context;
	}

	public String[] getHomePicUrl(String picUrl) {

		httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, picUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(context, R.string.toast_netfail,
						Toast.LENGTH_LONG).show();
			}
			@Override
			public void onSuccess(ResponseInfo<String> result) {
				try {
					JSONObject object=new JSONObject(result.result);


					int code=object.getInt(Environment.EROROCODESTRING);
					if (code== Environment.EROROCODEINT) {

						JSONArray array=object.getJSONArray("cleancarvolist");
						int count=array.length();
						picUrls=new String[count];
						for (int i = 0; i < count; i++) {
							picUrls[i]=array.getString(i);
						}
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}
		});
		return picUrls;
	}
}
