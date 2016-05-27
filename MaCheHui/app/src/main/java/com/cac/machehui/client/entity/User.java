package com.cac.machehui.client.entity;

public class User {
	private String id;
	private String userid;
	private String username;
	private String nickname;

	private String mobile;

	private String usersex;

	private String password;

	private String token;
	private String type;

	private String wtime;

	private String vip;
	private String time;
	private String userheadurl;
	private String weixintype;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getWeixintype() {
		return weixintype;
	}

	public void setWeixintype(String weixintype) {
		this.weixintype = weixintype;
	}

	public String getUserheadurl() {
		return userheadurl;
	}

	public void setUserheadurl(String userheadurl) {
		this.userheadurl = userheadurl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUsersex() {
		return usersex;
	}

	public void setUsersex(String usersex) {
		this.usersex = usersex;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", nickname="
				+ nickname + ", mobile=" + mobile + ", usersex=" + usersex
				+ ", password=" + password + ", token=" + token + ", type="
				+ type + ", wtime=" + wtime + ", vip=" + vip + ", time=" + time
				+ "]";
	}
}
