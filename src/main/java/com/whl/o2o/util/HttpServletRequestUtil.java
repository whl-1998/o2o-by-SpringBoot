package com.whl.o2o.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class HttpServletRequestUtil {
    /**
     * 通过request中的key值获取value并转换为Integer
     * @param request
     * @param key
     * @return
     */
    public static int getInt(HttpServletRequest request,String key){
        try {
            return Integer.decode(request.getParameter(key));
        }catch (Exception e){
            return -1;
        }
    }

    /**
     * 通过request中的key值获取value并转换为Long
     * @param request
     * @param key
     * @return
     */
    public static long getLong(HttpServletRequest request,String key){
        try {
            return Long.valueOf(request.getParameter(key));
        }catch (Exception e){
            return -1;
        }
    }

    /**
     * 通过request中的key值获取value并转换为Double
     * @param request
     * @param key
     * @return
     */
    public static double getDouble(HttpServletRequest request,String key){
        try {
            return Double.valueOf(request.getParameter(key));
        }catch (Exception e){
            return -1d;
        }
    }

    /**
     * 通过request中的key值获取value并转换为Boolean
     * @param request
     * @param key
     * @return
     */
    public static boolean getBoolean(HttpServletRequest request,String key){
        try {
            return Boolean.valueOf(request.getParameter(key));
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 通过request中的key值获取value并转换为String
     * @param request
     * @param key
     * @return
     */
    public static String getString(HttpServletRequest request,String key){
        try {
            String result = request.getParameter(key);
            if(result != null){
                result = result.trim();
            }
            if("".equals(result)){
                result = null;
            }
            return result;
        }catch (Exception e){
            return null;
        }
    }
}
