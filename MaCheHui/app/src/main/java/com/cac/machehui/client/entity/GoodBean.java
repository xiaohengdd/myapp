package com.cac.machehui.client.entity;

import java.io.Serializable;

/****
 * 积分商城的商品
 */
public class GoodBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	/*************** 商品id **************/
	public String scoregoodid;
	public String scoregoodimg;
	/************** 商品名字 **************/
	public String scoregoodname;
	public String score;
	/*** 积分类型: 0 实物商品 ,1虚拟商品 ,2 抽奖 ****/
	public String type;
	/*** 商品状态: 0 为失效 1为正在进行 ****/
	public String state;
	public String scoredetai;
	public String time;
	public String scoreimg;
	/**** 商品价格 ******/
	public String scoregoodprice;
	/**** 商品数量 *******/
	public String scoregoodamount;
	/**** 开始时间 ***************/
	public String begintime;
	/**** 结束时间 **********/
	public String endtime;
	/**** 地址 *******/
	public String scoregoodaddress;
	/**** 中转地址 *****/
	public String convertaddress;
	/***** 兑换流程 *******/
	public String convertflow;//
	/***** 备注 *********/
	public String notes;//
	/***** 提示 **********/
	public String prompt;
	/***** 密码 **********/
	public String scorepassword;
	/***** 购买数量 **********/
	public String buynum;
	/***** 积分列表里面的有效时间 **********/
	public String expiretime;
}
