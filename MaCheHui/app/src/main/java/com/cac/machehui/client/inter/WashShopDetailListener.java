package com.cac.machehui.client.inter;

import java.util.ArrayList;

import com.cac.machehui.client.entity.ShopDeatailTable;
import com.cac.machehui.client.entity.ShopGood;
import com.cac.machehui.client.entity.ShopTable;

/**
 * 店铺详情接口
 *
 * @author wkj
 *
 */
public interface WashShopDetailListener {

	void getShopDetailData(ArrayList<ShopDeatailTable> deatailTables);

	void getShopGoosList(ArrayList<ShopGood> shopGoodList);
}
