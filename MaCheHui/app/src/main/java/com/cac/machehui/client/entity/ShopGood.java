package com.cac.machehui.client.entity;

import java.io.Serializable;

public class ShopGood implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int shopNum;
	private int shopGoodnum;
	private String imgUrl;
	private String Name;
	private String originalPrice;
	private String presentPrice;
	private String isAppoint;

	private String backAnytime;
	private String backOverdue;

	private String goodDetail;
	private String goodBegin;

	private String goodEnd;
	private String kindlyReminder;

	private String howmany;
	private String appointMsg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShopNum() {
		return shopNum;
	}

	public void setShopNum(int shopNum) {
		this.shopNum = shopNum;
	}

	public int getShopGoodnum() {
		return shopGoodnum;
	}

	public void setShopGoodnum(int shopGoodnum) {
		this.shopGoodnum = shopGoodnum;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}

	public String getPresentPrice() {
		return presentPrice;
	}

	public void setPresentPrice(String presentPrice) {
		this.presentPrice = presentPrice;
	}

	public String getIsAppoint() {
		return isAppoint;
	}

	public void setIsAppoint(String isAppoint) {
		this.isAppoint = isAppoint;
	}

	public String getBackAnytime() {
		return backAnytime;
	}

	public void setBackAnytime(String backAnytime) {
		this.backAnytime = backAnytime;
	}

	public String getBackOverdue() {
		return backOverdue;
	}

	public void setBackOverdue(String backOverdue) {
		this.backOverdue = backOverdue;
	}

	public String getGoodDetail() {
		return goodDetail;
	}

	public void setGoodDetail(String goodDetail) {
		this.goodDetail = goodDetail;
	}

	public String getGoodBegin() {
		return goodBegin;
	}

	public void setGoodBegin(String goodBegin) {
		this.goodBegin = goodBegin;
	}

	public String getGoodEnd() {
		return goodEnd;
	}

	public void setGoodEnd(String goodEnd) {
		this.goodEnd = goodEnd;
	}

	public String getKindlyReminder() {
		return kindlyReminder;
	}

	public void setKindlyReminder(String kindlyReminder) {
		this.kindlyReminder = kindlyReminder;
	}

	public String getHowmany() {
		return howmany;
	}

	public void setHowmany(String howmany) {
		this.howmany = howmany;
	}

	public String getAppointMsg() {
		return appointMsg;
	}

	public void setAppointMsg(String appointMsg) {
		this.appointMsg = appointMsg;
	}

}
