package com.ymzz.plat.alibs.util;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.ymzz.plat.alibs.ad.ADSDK;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Xml;

public class AppUtil {
	public static Integer getMeTAInt(Context paramContext, String paramString)
	  {
			ApplicationInfo localApplicationInfo = null;
			Integer integer = null;
			try
			{
				  localApplicationInfo = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128);
				  integer = localApplicationInfo.metaData.getInt(paramString);
			}
			catch (Exception localException)
			{
				
			}
			return integer;
	  }
	
	public static String getMetaString(Context paramContext, String paramString)
	  {
			ApplicationInfo localApplicationInfo = null;
			String str = null;
			try
			{
				  localApplicationInfo = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128);
				  str = localApplicationInfo.metaData.getString(paramString);
			}
			catch (Exception localException)
			{
				
			}
			return str;
	  }
	
	public static Object getMetaObject(Context paramContext, String paramString)
	  {
			ApplicationInfo localApplicationInfo = null;
			Object str = null;
			try
			{
				  localApplicationInfo = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128);
				  str = localApplicationInfo.metaData.get(paramString);
			}
			catch (Exception localException)
			{
				
			}
			return str;
	  }
	public static Long getMetaLong(Context paramContext, String paramString)
	  {
			ApplicationInfo localApplicationInfo = null;
			Long str = null;
			try
			{
				  localApplicationInfo = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128);
				  str = localApplicationInfo.metaData.getLong(paramString);
			}
			catch (Exception localException)
			{
				
			}
			return str;
	  }
		/**
		 * ���ص�ǰ����汾���
		 */
		public static String getAppVersionName(Context context) {
			String versionName = "";
			try {
				// Get the package info
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				versionName = pi.versionName;
				if (TextUtils.isEmpty(versionName)) {
					return "";
				}
			} catch (Exception e) {
				
			}
			return versionName;
		}
		
		/*
		 * 获取程序的名字
		 */
		public static String getAppName(Context context) {

			String appName = "";
			try {

				PackageManager pm = context.getPackageManager();
				ApplicationInfo info = pm.getApplicationInfo(
						context.getPackageName(), 0);

				appName = info.loadLabel(pm).toString();

			} catch (NameNotFoundException e) {
				appName = "";

			}
			return appName;
		}
		
		public static String getAppNameFromPackageName(Context context,String packageName) {
			String appName="";
			
			PackageInfo packageInfo=null;
	 		try {
	 			PackageManager packageManager=context.getPackageManager();
	 			packageInfo = packageManager.getPackageInfo(
	 					packageName, 0);
	 			
	 			if (packageInfo!=null) {
	 				appName=packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
				}
	 			
	 			
	 	
	 			
	 		} catch (Exception e) {
	 		}
	 		
			return appName;
	 	}
		public static ArrayList <SDKInfo>getSDKInfos(InputStream inputStream) throws Exception{
			   ArrayList<SDKInfo> infos =null;
			   SDKInfo info =null;
			   XmlPullParser parser = Xml.newPullParser();
			   parser.setInput(inputStream, "UTF-8");   
			   int event = parser.getEventType();//
			   while(event!=XmlPullParser.END_DOCUMENT){   
			    switch(event){   
			         case XmlPullParser.START_DOCUMENT://
			        	 infos = new ArrayList<SDKInfo>();//   
			                   break;   
			         case XmlPullParser.START_TAG:// 
			        	 if("adtollgate".equals(parser.getName())){
			        		 ADSDK.adtollgate = parser.nextText().split(",");
			        	 }else if("adtype".equals(parser.getName())){
			        		 ADSDK.adtype = Integer.parseInt(parser.nextText());
			        	 }else if("type".equals(parser.getName())){
			        		 ADSDK.type = Integer.parseInt(parser.nextText());
			        	 }else if("adsdkid".equals(parser.getName())){
			        		 ADSDK.adsdkid = Integer.parseInt(parser.nextText());
			        	 }else if("adchannelid".equals(parser.getName())){
			        		 ADSDK.channelId = parser.nextText();
			        	 }else if("adgameid".equals(parser.getName())){
			        		 ADSDK.gameId = parser.nextText();
			        	 }else if("vertical".equals(parser.getName())){
			        		 ADSDK.vertical = Integer.parseInt(parser.nextText());
			        	 }else if("rpath".equals(parser.getName())){
			        		 ADSDK.rpath = parser.nextText();
			        	 }else{
		                   if("sdk".equals(parser.getName())){//
		                	   info = new SDKInfo();   
		                	   info.setId(Integer.parseInt(parser.getAttributeValue(0)));//
		                    }   
		                    if(info!=null){   
		                        if("appId".equals(parser.getName())){//
		                        	info.setAppId(parser.nextText());   
		                        }else if("appKey".equals(parser.getName())){// 
		                        	info.setAppKey(parser.nextText());   
		                        }
		                    }  
			        	 }
		                    break;   
			         case XmlPullParser.END_TAG://
		                    if("sdk".equals(parser.getName())){//  
		                     infos.add(info);// 
		                     info = null;   
		                    }   
		                    break;   
			               }   
			               event = parser.next();//
			          }//end while  
			   return infos;
			   
			  }
		public static String getCurrentNetType(Context context) {  
		    String type = "";  
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		    NetworkInfo info = cm.getActiveNetworkInfo();  
		    if (info == null) {  
		        type = "null";  
		    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {  
		        type = "wifi";  
		    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {  
		        int subType = info.getSubtype();  
		        if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS  
		                || subType == TelephonyManager.NETWORK_TYPE_EDGE) {  
		            type = "2g";  
		        } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA  
		                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0  
		                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {  
		            type = "3g";  
		        } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准  
		            type = "4g";  
		        }  
		    }  
		    return type;  
		}


}
