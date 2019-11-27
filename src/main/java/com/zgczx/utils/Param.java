package com.zgczx.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Param {
	
	/**
	 * 获取请求的参数
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getParam(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration em = request.getParameterNames();
		 while (em.hasMoreElements()) {
		    String name = (String) em.nextElement();
		    String value = request.getParameter(name);

		    System.out.println("请求参数名： " + name);
		    System.out.println("请求参数值： " + value);

		    map.put(name, value);
		}
		return map;
	}
	
}
