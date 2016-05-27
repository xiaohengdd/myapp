package com.cac.machehui.client.entity;

import java.io.Serializable;

public class ShopDeatailTable implements Serializable {

	 
	private static final long serialVersionUID = 1L;

	private int id;
	private String shopsName;
	private String addressDeatail;
	private double longtitude;
	private double latitude;
	private String shopDeatailUrl;
	private String phoneOne;
	private String phoneTwo;
	private String discountMsg	;
	

	public String getDiscountMsg() {
		return discountMsg;
	}

	public void setDiscountMsg(String discountMsg) {
		this.discountMsg = discountMsg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShopsName() {
		return shopsName;
	}

	public void setShopsName(String shopsName) {
		this.shopsName = shopsName;
	}

	public String getAddressDeatail() {
		return addressDeatail;
	}

	public void setAddressDeatail(String addressDeatail) {
		this.addressDeatail = addressDeatail;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getShopDeatailUrl() {
		return shopDeatailUrl;
	}

	public void setShopDeatailUrl(String shopDeatailUrl) {
		this.shopDeatailUrl = shopDeatailUrl;
	}

	public String getPhoneOne() {
		return phoneOne;
	}

	public void setPhoneOne(String phoneOne) {
		this.phoneOne = phoneOne;
	}

	public String getPhoneTwo() {
		return phoneTwo;
	}

	public void setPhoneTwo(String phoneTwo) {
		this.phoneTwo = phoneTwo;
	}

}
