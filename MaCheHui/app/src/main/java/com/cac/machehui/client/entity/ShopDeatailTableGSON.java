package com.cac.machehui.client.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopDeatailTableGSON implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private ArrayList<ShopDeatailTable> cleancarvolist;
	
	private String errorCode;

	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

 

	public ArrayList<ShopDeatailTable> getCleancarvolist() {
		return cleancarvolist;
	}

	public void setCleancarvolist(ArrayList<ShopDeatailTable> cleancarvolist) {
		this.cleancarvolist = cleancarvolist;
	}

	@Override
	public String toString() {
		return "ShopDeatailTableGSON [cleancarvolist=" + cleancarvolist
				+ ", errorCode=" + errorCode + ", getErrorCode()="
				+ getErrorCode() + ", getCleancarvolist()="
				+ getCleancarvolist() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	 
	
}
