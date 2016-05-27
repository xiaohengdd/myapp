package com.cac.machehui.client.entity;


 
import java.util.ArrayList;
import java.util.List;

public class ShopTableGSON   {

	private ArrayList<ShopTable> cleancarvolist;
	
	private String errorCode;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ArrayList<ShopTable> getCleancarvolist() {
		return cleancarvolist;
	}

	public void setCleancarvolist(ArrayList<ShopTable> cleancarvolist) {
		this.cleancarvolist = cleancarvolist;
	}
	 
}
