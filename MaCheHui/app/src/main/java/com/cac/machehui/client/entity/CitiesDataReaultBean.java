package com.cac.machehui.client.entity;

import java.util.List;

public class CitiesDataReaultBean {
	/******* 返回码，0为成功，其余请参照错误码表 *******/
	public int error_code;
	/******* 返回说明 ********/
	public String reason;
	/******* 结果集 ********/
	public List<CitiesDataBean> result;
}
