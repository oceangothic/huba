/**
 * 
 */
package com.huba.common.utils;

import com.alibaba.fastjson.JSON;


public class StringUtils {
    
	
	/**
	 * 将对象以json格式打印出来
	 * @param obj
	 * @return
	 */
	public static String printObject(Object obj) {
		return JSON.toJSONString(obj);
	}
}
