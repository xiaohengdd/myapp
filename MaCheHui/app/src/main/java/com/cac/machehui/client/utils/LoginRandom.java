package com.cac.machehui.client.utils;

import java.util.Random;

public class LoginRandom   {
	public static int getRandom(){
		int yanzhengma = (new Random().nextInt(9000))+1000;
		
		return yanzhengma;
	}
}
