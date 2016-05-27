package com.cac.machehui.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtils {
	public static String sendHttpClientPost(String path,
											Map<String, String> map, String encode) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}

		}
		try {
			// 将请求的参数封装到表单中，请求提体之中
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			// 使用Post的方式提交
			HttpPost httpPost = new HttpPost(path);
			// 设置请求中的请求体的参数
			httpPost.setEntity(entity);
			// 指定post的请求的方式
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return changeInputStream(httpResponse.getEntity().getContent(),
						encode);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将文本的输入流转换成文本的形式
	 *
	 * @param inputStream
	 *            服务器返回的输入流
	 * @param encode
	 *            输出文本的文件流
	 * @return
	 */
	private static String changeInputStream(InputStream inputStream,
											String encode) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		String result = "";
		int len = 0;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(buffer)) != -1) {
					output.write(buffer, 0, len);

				}
				result = new String(output.toByteArray(), encode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		String path = "http://127.0.0.1:8080/transit.do?action=send";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "658e64568e10483eb1cdb4bf987ce107");
		params.put("message", "新订单！");
		params.put("title", "新订单！");
		sendHttpClientPost(path, params, "utf-8");
	}
}
