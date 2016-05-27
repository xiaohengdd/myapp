package com.cac.machehui.client.cst;

public class URLCst {
	// 根路径
	// public static String CAR_HOST = "http://115.28.36.21:8080";
	// public static String CAR_HOST = "http://macheka.cn:8080";
	// public static String CAR_HOST = "http://xiaohengdd.vicp.cc";
	// 正式服务器
	public static String CAR_HOST = "http://123.236.49.231:8080";

	// public static String CAR_HOST = "http://192.168.31.236:8080";
	// /***** 检查手机号是否被注册 **/
	/***** 获取手机验证码 **/
	public static final String VERIFY_CODE = CAR_HOST
			+ "/appInterface/getcode/";
	/***** 直接获取手机验证码 **/
	public static final String DIRECT_VERIFY_CODE = CAR_HOST
			+ "/appInterface/verificationcode/";
	/***** 注册请求 **/
	public static final String REGISTER = CAR_HOST + "/appInterface/register";
	/***** 忘记密码 **/
	public static final String GET_BACK_PWD = CAR_HOST
			+ "/appInterface/forgetpassword";
	/***** 登录请求 **/
	public static final String LOGIN = CAR_HOST + "/appInterface/auth/";
	/***** 微信登录请求 **/
	public static final String LOGIN_WX = CAR_HOST + "/appInterface/isweixin/";
	/***** 微信注册请求 **/
	public static final String REGISTER_WX = CAR_HOST
			+ "/appInterface/weixinregister/";
	/***** 登录后绑定微信请求 **/
	public static final String BUND_AFTER_LOGIN_WX = CAR_HOST
			+ "/appInterface/weixinbinding/";
	/***** 解绑微信请求 **/
	public static final String REMOVE_WX = CAR_HOST
			+ "/appInterface/removeweixin/";
	/***** 获取首页四张图片 **/
	public static final String IMG_LIST = CAR_HOST
			+ "/appInterface/gethomeinglist/";
	/***** 获取ip **/
	public static final String IP = CAR_HOST + "/appInterface/ip";
	/***** 获取首页列表 **/
	public static final String HOME_LIST = CAR_HOST
			+ "/appInterface/carcommunityarticle?articleStatus=1&count=10&page=";
	/***** 获取发现页面的详情 **/
	public static final String FIND_DETAIL = CAR_HOST
			+ "/aichequan/ueditor/aichequanmobile.jsp?id=";
	/***** 修改头像 **/
	public static final String UPDATE_HEADER = CAR_HOST
			+ "/appInterface/UploadServlet";
	/***** 签到 **/
	public static final String QIANDAO = CAR_HOST
			+ "/appInterface/everyuserscore";
	/***** 更新个人资料 **/
	public static final String UPDATE_MESSAGE = CAR_HOST
			+ "/appInterface/changeuserinformation";
	/***** 更新用户密码 **/
	public static final String UPDATE_PWD = CAR_HOST
			+ "/appInterface/changepassword";
	/***** 验证手机号或者密码 **/
	public static final String VERIFY_IDENTIFY = CAR_HOST
			+ "/appInterface/ifpasswordorusername";
	/***** 获取积分以及商品列表 **/
	public static final String GET_SCORE_GOOD = CAR_HOST
			+ "/appInterface/getscoregood";
	/***** 兑换商品 **/
	public static final String BUY_SCORE_GOOD = CAR_HOST
			+ "/appInterface/buyscoregood";
	/***** 提交订单 **/
	public static final String CREATE_ORDER = CAR_HOST
			+ "/appInterface/createOrder";
	/***** 查询订单 **/
	public static final String GET_ORDERA = CAR_HOST
			+ "/appInterface/getMyOrder";
	/***** 取消订单 **/
	public static final String CANCLE_ORDERA = CAR_HOST
			+ "/appInterface/updateOrder";
	// /***** 去支付 **/
	// public static final String PAY = CAR_HOST
	// + "/appInterface/insertmyshopgoods";
	/***** 用户协议 **/
	public static final String USER_PROTOCAL = CAR_HOST
			+ "/aichequan/ueditor/useragreement.jsp";
	/***** 代驾使用协议 **/
	public static final String DRIVER_PROTOCAL = CAR_HOST
			+ "/aichequan/ueditor/drivingagreement.jsp";
	/***** 礼券 **/
	public static final String LI_QUAN = CAR_HOST
			+ "/appInterface/getscoreorder";
	/***** 会员积分兑换查询 **/
	public static final String SCORE_GOOD_LISTS = CAR_HOST
			+ "/appInterface/scoreGoodList";
	/***** 获取车牌 GET **/
	public static final String GET_CAR_BRAND = CAR_HOST
			+ "/appInterface/cartypejson";
	/***** 获取车系 GET **/
	public static final String GET_LINE_BRAND = CAR_HOST
			+ "/appInterface/carmodlejson";
	/***** 获取车系 GET **/
	public static final String GET_MODEL_BRAND = CAR_HOST
			+ "/appInterface/carjutimodlejson";
	/***** 添加车辆 post **/
	public static final String ADD_CAR = CAR_HOST + "/appInterface/insertmycar";
	/***** 查看车辆 post **/
	public static final String SELECT_CAR = CAR_HOST
			+ "/appInterface/selectmycar";
	/***** 删除车辆 post **/
	public static final String DELETE_CAR = CAR_HOST
			+ "/appInterface/updateMycar";
	/***** 检查版本更新post **/
	public static final String UPDATE_VERSION = CAR_HOST
			+ "/appInterface/appUpdate";
	/***** 违章查询 支持城市 **/
	public static final String WEIZHANG_CITIES = "http://v.juhe.cn/wzcxy/citys";
	/***** 违章查询 **/
	public static final String WEIZHANG = "http://v.juhe.cn/wzcxy/query";
	/***** 微信登录获取access_token GET **/
	public static final String WX_GET_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	/***** 微信登录获取个人信息 GET **/
	public static final String WX_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
	/***** 是否有新消息 **/
	public static final String HAS_MSG = CAR_HOST + "/appInterface/isHaveMsg";
	/***** 消息列表 **/
	public static final String MSG_LIST = CAR_HOST + "/appInterface/myMsg";
	/***** 读取消息 **/
	public static final String MSG_UPDATE = CAR_HOST
			+ "/appInterface/updateMyMsg";

	/***** 退出登录post **/
	public static final String LOGOUT = CAR_HOST + "/appInterface/userQuit";

}
