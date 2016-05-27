package com.cac.machehui.client.entity;

public class Coupons {

	private String id;
	private String user_name;

	/**
	 * 商品编号
	 */
	private String shop_goodnum;

	/**
	 * 数量
	 */
	private String howmany;

	/**
	 * 店铺名
	 */
	private String shopname;

	/**
	 * 商品名
	 */
	private String shopgoodname;

	/**
	 * 商铺编号
	 */
	private String shopnum;


	/**
	 * 商品属性
	 */
	private String goodtype;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getShop_goodnum() {
		return shop_goodnum;
	}


	public void setShop_goodnum(String shop_goodnum) {
		this.shop_goodnum = shop_goodnum;
	}


	public String getHowmany() {
		return howmany;
	}


	public void setHowmany(String howmany) {
		this.howmany = howmany;
	}


	public String getShopname() {
		return shopname;
	}


	public void setShopname(String shopname) {
		this.shopname = shopname;
	}


	public String getShopgoodname() {
		return shopgoodname;
	}


	public void setShopgoodname(String shopgoodname) {
		this.shopgoodname = shopgoodname;
	}


	public String getShopnum() {
		return shopnum;
	}


	public void setShopnum(String shopnum) {
		this.shopnum = shopnum;
	}


	public String getGoodtype() {
		return goodtype;
	}


	public void setGoodtype(String goodtype) {
		this.goodtype = goodtype;
	}






}
