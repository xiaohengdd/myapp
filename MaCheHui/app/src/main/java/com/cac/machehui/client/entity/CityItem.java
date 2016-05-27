package com.cac.machehui.client.entity;

import java.io.Serializable;

/**
 * 城市
 */
public class CityItem implements Serializable {
	public static final long serialVersionUID = 1L;
	/********** 城市ID **************/
	public String cityId;
	/********** 城市名称 **************/
	public String cityName;
	/********** 车牌前缀 **************/
	public String carnoPrefix;
	/********** 是否需要车架号:0 代表不需要 99 代表完整 具体数字代表 后多少位 **************/
	public String classno;
	/********** 是否需要发动机号:0 代表不需要 99 代表完整 具体数字代表 后多少位 **************/
	public String engineno;

}
