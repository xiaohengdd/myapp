package com.cac.machehui.client.entity;

import java.sql.Date;

import android.widget.ImageView;

public class XListViewItem {

	private int id;

	private ImageView image;

	private String itemName;

	private String itemContent;

	private ImageView imageIcon;

	private Date curTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ImageView getImage() {
		return image;
	}

	public void setImage(ImageView image) {
		this.image = image;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemContent() {
		return itemContent;
	}

	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}

	public ImageView getImageIcon() {
		return imageIcon;
	}

	public void setImageIcon(ImageView imageIcon) {
		this.imageIcon = imageIcon;
	}

	public Date getCurTime() {
		return curTime;
	}

	public void setCurTime(Date curTime) {
		this.curTime = curTime;
	}

	
	
}
