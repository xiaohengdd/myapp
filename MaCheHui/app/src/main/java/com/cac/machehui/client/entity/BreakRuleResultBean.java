package com.cac.machehui.client.entity;

import java.util.List;

/**
 * 违章的结果
 */
public class BreakRuleResultBean {
	/********* 返回码，0为成功，其余请参照错误码表 ***********/
	public int error_code;
	public String reason;
	/********** 结果集 ************/
	public List<CityResult> result;
}
