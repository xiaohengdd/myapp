package com.cac.machehui.client.entity;

import java.io.Serializable;

import com.baidu.mapapi.model.LatLng;

/**
 * 停车列表对应的每一项
 *
 * @author wkj
 *
 */
public class StopListItem implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String stopName;
	private String stopAddress;
	private String phone;
	private double distance;
	private int seatCount;

	private String imageUrl;
	// 经度
	private double longitude;
	// 维度
	private double latitude;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getStopAddress() {
		return stopAddress;
	}

	public void setStopAddress(String stopAddress) {
		this.stopAddress = stopAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
