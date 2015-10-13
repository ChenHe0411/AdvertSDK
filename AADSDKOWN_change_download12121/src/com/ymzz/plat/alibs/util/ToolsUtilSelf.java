package com.ymzz.plat.alibs.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class ToolsUtilSelf {
	
	
	public static int setPicWidth(int deviceWidth,int deviceHeight,int bitmapWidth,int bitmapHeight,int flag){
		
		
		if (bitmapWidth==0||bitmapHeight==0) {
			return 0;
		}
		int setwidth=0;
		int setHeight=0;
		
		
		
		
		
		float picWidthScare=deviceWidth/(float)bitmapWidth;
		float picHeightScare=deviceHeight/(float)bitmapHeight;
//		System.out.println("picWidthScare--"+picWidthScare+"picHeightScare---"+picHeightScare);
		if (picWidthScare*bitmapHeight<=deviceHeight) {
			setwidth=deviceWidth;
			setHeight=(int)(picWidthScare*bitmapHeight);
			
		}else{
			setHeight=deviceHeight;
			setwidth=(int)(picHeightScare*bitmapWidth);
			
		}
		
		int newHeight=(int)(setHeight*0.7);
		int newWidth=(int)(setwidth*0.7);
		
		if (flag==1) {

			return newWidth;
		}else {
			

			return newHeight;
		}
	
	}

	/**
	 * 图片部分
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getDiskCacheDirPath(Context context) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return cachePath;
	}

	/**
	 * 视频部分
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	@SuppressLint("NewApi")
	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/** * 返回当前屏幕是否为竖屏。 * @param context * @return 当且仅当当前屏幕为竖屏时返回true,否则返回false。 */
	public static boolean isScreenOriatationLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static String urlEncodeToString(String value) {

		String en = "";
		try {
			en = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}

		return en;
	}

	public static boolean isNetConnected(Context context) {
		boolean isNetConnected;
		// ����������ӷ���
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// String name = info.getTypeName();
			// L.i("��ǰ������ƣ�" + name);
			isNetConnected = true;
		} else {

			isNetConnected = false;
		}
		return isNetConnected;
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

}
