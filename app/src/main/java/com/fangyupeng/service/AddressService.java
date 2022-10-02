package com.fangyupeng.service;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Auther: fyp
 * @Date: 2022/5/23
 * @Description:
 * @Package: com.fangyupeng.utils
 * @Version: 1.0
 */
public class AddressService {

   public static String getIP(Context context) {
      WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (!wifiManager.isWifiEnabled()) {
         try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
               NetworkInterface intf = (NetworkInterface) en.nextElement();
               for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                  InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                  if (!inetAddress.isLoopbackAddress()) {
                     return inetAddress.getHostAddress().toString();
                  }
               }
            }
         } catch (SocketException e) {
            e.printStackTrace();
         }

      } else {
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         int ipAddress = wifiInfo.getIpAddress();
         String ip = intToIp(ipAddress);
         return ip;
      }
      return null;
   }

   private static String intToIp(int ipAddress) {
      return (ipAddress & 0xFF) + "." +
              ((ipAddress >> 8) & 0xFF) + "." +
              ((ipAddress >> 16) & 0xFF) + "." +
              (ipAddress >> 24 & 0xFF);
   }
}
