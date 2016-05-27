package com.cac.machehui.client.entity;

import java.io.Serializable;

public class ShopListItem implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int shopNum;
	// 商铺的名称
	private String shopName;
	// 商铺的地址
	private String shopAddress;
	// 星的个数
	private double star;
	// 距离
	private double distance;

	// 商品的折扣
	private double rate;
	// 图片的位图
	private String imageBitmap;



	public int getShopNum() {
		return shopNum;
	}

	public void setShopNum(int shopNum) {
		this.shopNum = shopNum;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public double getStar() {
		return star;
	}

	public void setStar(double star) {
		this.star = star;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(String imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}
