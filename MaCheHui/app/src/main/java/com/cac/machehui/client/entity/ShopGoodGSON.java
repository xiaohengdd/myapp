package com.cac.machehui.client.entity;

import java.util.ArrayList;

public class ShopGoodGSON {

	private ArrayList<ShopGood> shopgoodslist;

	private int errorCode;

	public ArrayList<ShopGood> getShopgoodslist() {
		return shopgoodslist;
	}

	public void setShopgoodslist(ArrayList<ShopGood> shopgoodslist) {
		this.shopgoodslist = shopgoodslist;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
