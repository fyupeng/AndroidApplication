package com.fangyupeng.service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: fyp
 * @Date: 2022/5/23
 * @Description:
 * @Package: com.fangyupeng.service
 * @Version: 1.0
 */
public class GlobalSessionService {

   public final static String serverPrefix = "http://47.107.63.171:8083";
   public final static String localServerPrefix = "http://192.168.10.1:8083";
   public final static String webSitePrefix = "http://120.76.217.185:8080";
   public final static String developEmail = "fyp010311@163.com";

   private static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

   public static void set(String key, Object val) {
      map.put(key, val);
   }

   public static Object get(String key) {
      return map.get(key);
   }

   public static Object remove(String key) {
      return map.remove(key);
   }

}
