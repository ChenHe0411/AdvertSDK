package com.ymzz.plat.alibs.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class StoreLocalDataUtil {

	
	public static String fileName="domainNamefile";
	public static String domainName="domainName";
	
	
	
	public static void storeAppNameData(Context context, String domainname_obj) {
		SharedPreferences timepref = context.getSharedPreferences(
				fileName, Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();

		timeditor.putString(domainName, domainname_obj);
		timeditor.commit();
	}

	public static String showAppNameStoreData(Context context) {
		SharedPreferences timepref = context.getSharedPreferences(
				fileName, Activity.MODE_PRIVATE);
		String fullScreen = timepref.getString(domainName, "www.baopiqi.com");
		return fullScreen;
	}
}
