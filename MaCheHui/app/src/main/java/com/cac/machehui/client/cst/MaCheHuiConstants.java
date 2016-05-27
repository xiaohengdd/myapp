package com.cac.machehui.client.cst;

/**
 * 定义UI界面中需要的常量类
 *
 * @author wkj
 *
 */
public class MaCheHuiConstants {
	/************ 微信登录的secret *****************/
	public static final String WX_SECRET = "36741e16da5bfa468bbe6f63825d1085";
	/************ 微信登录的grant_type *****************/
	public static final String GRANT_TYPE = "authorization_code";
	/************ 微信登录的scope,权限,获取用户信息 *****************/
	public static final String WX_SCOPE = "snsapi_userinfo";
	/************ 微信登录的state *****************/
	public static final String WX_STATE = "MaCheHui";
	/************ 拍照 *****************/
	public static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	/************ 从相册中选择 *****************/
	public static final int PHOTO_REQUEST_GALLERY = 2;
	/************ 裁切结果 *****************/
	public static final int PHOTO_REQUEST_CUT = 3;
	/************ 修改手机号，获取验证码，旧手机号 *****************/
	public static final int GET_CODE_OLD = 1;
	/************ 修改手机号，获取验证码，新手机号 *****************/
	public static final int GET_CODE_NEW = 2;
	/************ 获取商品数据的信息 *****************/
	public static final int GETDATA = 0;
	/************ 获取商品的信息 *****************/
	public static final int GETGOODS = 1;
	/************ 查询违章 *****************/
	public static final String WZ_KEY = "110f0e31175a7fe24a42abb392bfbb4d";

}
