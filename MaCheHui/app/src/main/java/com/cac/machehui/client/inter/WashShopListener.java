package com.cac.machehui.client.inter;

import java.util.ArrayList;

import com.cac.machehui.client.entity.ShopTable;
/**
 * 店铺列表接口
 * @author wkj
 *
 */
public interface WashShopListener {

	void getShopTableData(ArrayList<ShopTable> infoList,int flag);
}
