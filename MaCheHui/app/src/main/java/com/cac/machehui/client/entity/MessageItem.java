package com.cac.machehui.client.entity;

public class MessageItem {
	public String id;
	/******* 消息id ********/
	public String msgid;
	/****** 消息类型：0：群发 暂无其他 *********/
	public String msgType;
	/******* 消息属性：0：私人信息 1：群发信息 ********/
	public String msgAttribute;
	/********* 发送时间 ******/
	public String sendTime;
	/******* 读取时间 ********/
	public String readTime;
	/******* 消息标题 ********/
	public String title;
	/******* 消息内容 ********/
	public String msgContent;
}
