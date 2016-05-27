package com.cac.machehui.client.cst;

public class WashCst {

	// 传递数据的标志
	public static final String SHOPNUMBER = "SHOPNUMBER";

	// 首次进入的时候的标志
	public static final int FIRSTOPEN = 5;
	// 按照类型搜索标志
	public static final int QUERY_TYPE = 6;

	// 传递目标的经纬的信息
	public static final String MAP_LONGTITUDE = "MAP_LONGTITUDE";
	public static final String MAP_LATITUDE = "MAP_LATITUDE";
	/** 全部的标注位 */
	public static final int FULL_POSITION = 0;
	/** 设置空参数 */
	public static final String EMPTY = "";

	// 查询洗车
	public static final String WASH_GAS = "isGas";
	// 查询美容店
	public static final String WASH_CLEAN = "isCleanVehicle";
	// 维修保养
	public static final String WASH_REAPIR = "isRepair";
	// 查询加油
	public static final String WASH_COME = "isCosmetology";

	// 查询条件

	public static final String WASHCARTYPE="washcarType";
	public static final String WASH_ISDISCOUNT = "isDiscount";
	public static final String WASH_ISAPPOINT = "isAppoint";
	public static final String AEARBIG = "aearBig";
	public static final String AEARLITTLE = "aearLittle";
	public static final String WASH_SHOPTYPE = "shopType";
	public static final String WASH_PAGER = "page";

	public static final String WASH_ID = "shopNum";
	public static final String WASH_SHOPNAME = "wash_shopname";

	// 服务器请求ShopTable的位置
	public static final String SHOPTABLEURL = "cleancar";
	// 服务器请求shopDeatail的位置
	public static final String SHOPDEATAIL = "cleancardetail";

	// 服务器中优惠信息
	public static final String SHOPISDISCOUNT = "cleancarisappoint";

	// 服务器的商品获取的列表
	public static final String SHOPNUMSERVER = "shopgoods";

	// 进入的时候修改查询的类型
	public static final String MODIFYTYPE = "MODIFYTYPE";

	// json集合返回码
	public static final String SHOP_CLEANCARVOLIST = "cleancarvolist";

	// 首次进入这个Activity
	public static final String FIRSTINPUT = "FIRSTINPUT";
	public static final String FIRSTTYPE = "FIRSTTYPE";
	// 传递服务器的标志
	public static final String SHOPNUM = "shopNum";

	//传递数据的标志
	public static final String T_SHOPNAME="T_SHOPNAME";
	public static final String T_BATCOUNT="T_BATCOUNT";
	public static final String T_DISTANCE="T_DISTANCE";

	public static final String T_NOWPRICE="T_NOWPRICE";
	public static final String T_OLDPRICE="T_OLDPRICE";
	public static final String T_GOODNAME="T_GOODNAME";

	public static final String T_PHONEONE="T_PHONEONE";
	public static final String T_PHONETWO="T_PHONETWO";

	public static final String T_ADDRESSdETAIL="T_ADDRESSdETAIL";


	//商品列表向团购详情界面
	public static final String T_SHOPGOOD="T_SHOPGOOD";

}
