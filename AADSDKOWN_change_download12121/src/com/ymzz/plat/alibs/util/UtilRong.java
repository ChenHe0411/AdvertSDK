package com.ymzz.plat.alibs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import com.ymzz.plat.alibs.ad.ADSDK;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UtilRong {
	
	/*** 
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent, 
     * "java.lang.IllegalArgumentException: Service Intent must be explicit" 
     * 
     * If you are using an implicit intent, and know only 1 target would answer this intent, 
     * This method will help you turn the implicit intent into the explicit form. 
     * 
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466 
     * @param context 
     * @param implicitIntent - The original implicit intent 
     * @return Explicit Intent created from the implicit original intent 
     */  
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {  
        // Retrieve all services that can match the given intent  
        PackageManager pm = context.getPackageManager();  
        String packName = context.getPackageName();
        ResolveInfo serviceInfo = null;
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);  
        for (Iterator<ResolveInfo> iterator = resolveInfo.iterator(); iterator.hasNext();) {
			ResolveInfo resolveInfo2 = (ResolveInfo) iterator.next();
			if(resolveInfo2.serviceInfo.packageName.equals(packName)){
				serviceInfo = resolveInfo2;
			}
			
		}
        // Make sure only one match was found  
        if (serviceInfo == null) {  
            return null;  
        }  
   
        // Get component info and create ComponentName  
        
        String packageName = serviceInfo.serviceInfo.packageName;  
        String className = serviceInfo.serviceInfo.name;  
        ComponentName component = new ComponentName(packageName, className);  
   
        // Create a new intent. Use the old one for extras and such reuse  
        Intent explicitIntent = new Intent(implicitIntent);  
   
        // Set the component to be explicit  
        explicitIntent.setComponent(component);  
   
        return explicitIntent;  
    }  
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public static int getR(Context context,String path,String name){
		int rs = 0;
		try {
//			Class r = Class.forName(ADSDK.rpath + path);
			Class r = Class.forName(context.getPackageName() + path);
			rs = r.getField(name).getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void getPackNames(){
		PackageManager pm = ADSDK.context.getPackageManager();// 获取系统应用包管理

		List<ResolveInfo> resolveInfos = getAllApps(ADSDK.context, pm);
		if (resolveInfos != null && resolveInfos.size() != 0) {
			for (ResolveInfo info : resolveInfos) {
				//String packageName = info.activityInfo.packageName;
				String appName = info.loadLabel(pm).toString();
				ADSDK.packNameList.add(appName);
			}
		}
	}
	
	/**
	 * 获得手机里面安装软件的resolveInfo集合
	 * 
	 */
	public static List<ResolveInfo> getAllApps(Context context,
			PackageManager pManager) {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appList = pManager.queryIntentActivities(intent, 0);
		return appList;
	}
	
	
	
	public static String getCurrentTaskActivity(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager
				.getRunningTasks(Integer.MAX_VALUE);
		if (runningTaskInfos != null) {
			ComponentName str = runningTaskInfos.get(0).topActivity;
			Log.d("BaseActivity", str.getClassName());

			return str.getClassName();
		}
		return "";
	}
	

	public static boolean isNetConnected(Context context) {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
//			String name = info.getTypeName();
//			L.i("当前网络名称：" + name);
			isNetConnected = true;
		} else {
		
			isNetConnected = false;
		}
		return isNetConnected;
	}

	
}
