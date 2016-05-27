package com.cac.machehui.client.entity;

import java.io.Serializable;

public class MyOrderBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	/*********** 订单id *******************/
	public String orderid;
	public String userid;
	/*********** 订单创建时间 *******************/
	public String createTime;
	/*********** 订单过期时间 *******************/
	public String endTime;
	/*********** 订单有效时间 *******************/
	public String orderTime;
	/********** 订单类型 0：团购 1：线下消费线上付款 *******************/
	public String orderType;
	/*********** 订单状态 0:未付款 1;已付款 2:已取消 3:申请订单 *******************/
	public String orderStatus;
	/*********** 商品密码 *******************/
	public String goodPassword;
	/*********** 商品id *******************/
	public String shopid;
	/*********** 商品价格 *******************/
	public String shopPrice;
	/*********** 折扣价格 *******************/
	public String discountPrice;
	/*********** 折扣率 *******************/
	public String discount;
	/*********** 购买数量 *******************/
	public String shopgoodNum;
	/*********** 商品状态：0：未付款 1：已付款未使用 2；已使用，未评价 3：已使用已评价 4：退款申请中 5：退款给深刻已通过 6：退款中 7：已退款 *******************/
	public String shopgoodStatus;
	/*********** 商品类型 *******************/
	public String shopgoodType;
	/*********** 店铺名称 *******************/
	public String shopsName;
	/*********** 店铺图片 *******************/
	public String shopUrl;
	/*********** 商品名称 *******************/
	public String name;
	/*********** 付款方式:0:微信 1：支付宝 *******************/
	public String payType;
	/*********** 付款时间 *******************/
	public String payTime;
	/*********** 付款金额 *******************/
	public String payMoney;

}
