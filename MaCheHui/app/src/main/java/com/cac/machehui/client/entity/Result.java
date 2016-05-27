package com.cac.machehui.client.entity;

import java.util.List;

public class Result {

	private String province;

	private String city;

	/**
	 * 车牌号
	 */
	private String carno;

	private String hpzl;

	private List<PeccancyItem> lists;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCarno() {
		return carno;
	}

	public void setCarno(String carno) {
		this.carno = carno;
	}

	public String getHpzl() {
		return hpzl;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}

	public List<PeccancyItem> getLists() {
		return lists;
	}

	public void setLists(List<PeccancyItem> lists) {
		this.lists = lists;
	}




}
