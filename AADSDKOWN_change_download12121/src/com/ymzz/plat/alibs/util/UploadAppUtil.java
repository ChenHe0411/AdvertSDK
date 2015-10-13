package com.ymzz.plat.alibs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.ymzz.plat.alibs.ad.ADSDK;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;

public class UploadAppUtil {
	private static final String uploadUrl = "http://www.baopiqi.com/api/postpkg.php?uid=";
	
	
	/**
	 * 提交调用的方法
	 * @param context
	 */
	public static void uploadPackage(final Context context){
		

		new Thread() {
			public void run() {

				try {
					String IMEI = UploadAppUtil.getIMEI(context);

					String info = UploadAppUtil
							.getAppData(context);
					String result = PostUpdata.readContentFromPost(
							uploadUrl + IMEI, info);
//System.out.println("result--"+result);
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();

	
		
		
	}
	
	
	
	
	/***
	 * 
	 * @param string
	 * @return
	 */

	public static String stringToBase64(String string) {

		String aa = Base64.encodeToString(string.getBytes(), Base64.DEFAULT)
				.replace("\n", "").replace("\r", "").replace("\t", "");

		return aa;

	}

	public static String Base64Tostring(String string) {
		return new String(Base64.decode(string, Base64.DEFAULT));
	}

	public static String getIMEI(Context context) {

		String IMEI = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		IMEI = tm.getDeviceId();
		if (IMEI == null) {
			IMEI = "";
		}
		return IMEI;
	}

	

	public static String getAppData(Context mContext) {
		PackageManager pm = mContext.getPackageManager();// 获取系统应用包管理

		List<ResolveInfo> resolveInfos = UtilRong.getAllApps(mContext, pm);

//		System.out.println("resolveInfos============" + resolveInfos.size());

		// 遍历每个应用包信息
		StringBuffer sb = new StringBuffer();
		String appInf = "";
		String ifo = "";
		sb.append("spamprob=0&packagename=");
		StringBuffer sbb = new StringBuffer();

		if (resolveInfos != null && resolveInfos.size() != 0) {

			for (ResolveInfo info : resolveInfos) {

				String packageName = info.activityInfo.packageName;
				String appName = info.loadLabel(pm).toString();
				appInf = packageName + "," + appName + ";";
				ADSDK.packNameList.add(appName);
				try {
					ifo = URLEncoder.encode(appInf, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				sbb.append(ifo);

			}
		}
		return sb.append(stringToBase64(sbb.toString())).toString();
	}

	
}
