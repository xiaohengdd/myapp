package com.cac.machehui.client.entity;

import java.io.Serializable;
/**
 * 商铺信息的实体类
 * @author wkj
 *
 */
public class ShopTable  implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	private int shopNum;
	private String shopsName;
	private String aearBig;
	private String aearLittle;
	private String addressDeatail;
	private String city;
	private double longtitude;
	private double latitude;
	private int rate;
	private String shopUrl;
	private int starCount;
	private boolean isAppoint;
	private boolean isDiscount;
	private String discountMsg;
	private String distance;

	public int getShopNum() {
		return shopNum;
	}
	public void setShopNum(int shopNum) {
		this.shopNum = shopNum;
	}
	public String getShopsName() {
		return shopsName;
	}
	public void setShopsName(String shopsName) {
		this.shopsName = shopsName;
	}
	public String getAearBig() {
		return aearBig;
	}
	public void setAearBig(String aearBig) {
		this.aearBig = aearBig;
	}
	public String getAearLittle() {
		return aearLittle;
	}
	public void setAearLittle(String aearLittle) {
		this.aearLittle = aearLittle;
	}
	public String getAddressDeatail() {
		return addressDeatail;
	}
	public void setAddressDeatail(String addressDeatail) {
		this.addressDeatail = addressDeatail;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getShopUrl() {
		return shopUrl;
	}
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	public int getStarCount() {
		return starCount;
	}
	public void setStarCount(int starCount) {
		this.starCount = starCount;
	}

	public boolean isAppoint() {
		return isAppoint;
	}
	public void setAppoint(boolean isAppoint) {
		this.isAppoint = isAppoint;
	}
	public boolean isDiscount() {
		return isDiscount;
	}
	public void setDiscount(boolean isDiscount) {
		this.isDiscount = isDiscount;
	}
	public String getDiscountMsg() {
		return discountMsg;
	}
	public void setDiscountMsg(String discountMsg) {
		this.discountMsg = discountMsg;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}


}
