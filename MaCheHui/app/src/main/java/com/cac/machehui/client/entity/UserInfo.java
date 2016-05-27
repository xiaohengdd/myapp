package com.cac.machehui.client.entity;

public class UserInfo extends BaseEntity {

	private User user;
	private String typecode;

	public String getTypecode() {
		return typecode;
	}

	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}

	public User getUser() {
		return user;
	}

	public void setUserRef(User user) {
		this.user = user;
	}

}
