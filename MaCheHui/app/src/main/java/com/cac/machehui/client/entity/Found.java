package com.cac.machehui.client.entity;

public class Found {
	
	private String id;
	private String articleImg_url;
	private String articleTitle;
	private String articleAbstract;
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArticleImg_url() {
		return articleImg_url;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public void setArticleImg_url(String articleImg_url) {
		this.articleImg_url = articleImg_url;
	}

	public String getArticleAbstract() {
		return articleAbstract;
	}

	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
