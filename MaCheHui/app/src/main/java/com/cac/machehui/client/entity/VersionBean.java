package com.cac.machehui.client.entity;

import java.io.Serializable;

/**
 *
 * 版本信息
 *
 */
public class VersionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	public String appName;
	public String versionCode;
	public String versionName;
	public String appUrl;
	public String appType;
	public String os;
	public String isForcedUpdate;
	public String updateTime;
}
