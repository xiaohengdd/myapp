package com.cac.machehui.client.entity;

import java.util.Date;

public class OrderpasswordVo {
	private String id; // int id
	private String orderid; // varchar 订单号
	private String orderpassword; // varchar 订单密码
	private String time; // time 创建或修改时间
	private String howmany;//查询数据传递参数的时候需要
	private String user_name; //用户名字
	private String orderpasswordend;
	private Date date;
	private String orderpasswordtype;


	public String getOrderpasswordtype() {
		return orderpasswordtype;
	}

	public void setOrderpasswordtype(String orderpasswordtype) {
		this.orderpasswordtype = orderpasswordtype;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOrderpasswordend() {
		return orderpasswordend;
	}

	public void setOrderpasswordend(String orderpasswordend) {
		this.orderpasswordend = orderpasswordend;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getHowmany() {
		return howmany;
	}

	public void setHowmany(String howmany) {
		this.howmany = howmany;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getOrderpassword() {
		return orderpassword;
	}

	public void setOrderpassword(String orderpassword) {
		this.orderpassword = orderpassword;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
