package com.ymzz.plat.alibs.util;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ymzz.plat.alibs.ad.ADSDK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class LauncherOtherApp {

	private static String packagefile = "packageLaunchxml";
	private static String datekey = "datekey";
	public static int showTotalTime = 1;

	private static Timer tasktimer = null;

	@SuppressWarnings("unused")
	public static void delaylaunchAp(final Context context) {

		final Handler laucherOtherApp = new Handler() {
			public void handleMessage(Message msg) {

				if (ADSDK.openapp != null && (!"".equals(ADSDK.openapp))) {

					if (ADSDK.openapp.contains("/")) {
						String[] pkglaunch = ADSDK.openapp.split("/");

						if (pkglaunch != null && pkglaunch.length > 1) {
							LauncherOtherApp.launchApp(context, pkglaunch[0],
									pkglaunch[1]);
						}

					} else {

						LauncherOtherApp.launchApp(context, ADSDK.openapp, "");
					}
				}

			}

		};

		if (tasktimer != null) {
			tasktimer.cancel();
			tasktimer = null;

		}
		tasktimer = new Timer();
		tasktimer.schedule(new TimerTask() {

			@Override
			public void run() {
				laucherOtherApp.sendEmptyMessage(0);

			}
		}, 10000);

	}

	/*** ==================================================== **/

	private static void storeAppNameData(Context context, String pkgName,
			int time) {
		SharedPreferences timepref = context.getSharedPreferences(packagefile,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();

		timeditor.putInt(pkgName, time);
		timeditor.commit();

	}

	private static int showAppNameStoreData(Context context, String pkgName) {
		SharedPreferences timepref = context.getSharedPreferences(packagefile,
				Activity.MODE_PRIVATE);
		int showTime = timepref.getInt(pkgName, 0);
		return showTime;
	}

	@SuppressLint("SimpleDateFormat")
	private static String getCurrentDate() {
		String date = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = sdf.format(new java.util.Date());
		return date;

	}

	private static void storeDateData(Context context, String datevalue) {
		SharedPreferences timepref = context.getSharedPreferences(packagefile,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();

		timeditor.putString(datekey, datevalue);
		timeditor.commit();

	}

	private static void clearShareDate(Context context) {
		SharedPreferences timepref = context.getSharedPreferences(packagefile,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();
		timeditor.clear();

		timeditor.commit();

	}

	public static void launchApp(Context context, String pkgName,
			String launcherAct) {

		if (appIsInstall(context, pkgName)) {

			SharedPreferences timepref = context.getSharedPreferences(
					packagefile, Activity.MODE_PRIVATE);

			String date = timepref.getString(datekey, "");

			if (!date.equals(getCurrentDate())) {

				clearShareDate(context);
				storeDateData(context, getCurrentDate());
			}
			
			if (launcherAct != null && (!"".equals(launcherAct))) {


				
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(pkgName,
							launcherAct));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);

				
			}else{
			

			if (timepref.contains(pkgName)) {

				if (showAppNameStoreData(context, pkgName) < showTotalTime) {

					storeAppNameData(context, pkgName,
							showAppNameStoreData(context, pkgName) + 1);
			
						openApp(context, pkgName);
					

				}

			} else {
				storeAppNameData(context, pkgName, 1);

			
					openApp(context, pkgName);
				

			}
			}
		}

	}

	public static boolean biaomeiRun(Context context, String pkgname) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = pkgname;
		
		if (list!=null&&list.size()!=0) {
			
		
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME)
					|| info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				break;
			}
		}
		}
		return isAppRunning;

	}

	/***
	 * =============================================================
	 * 
	 * @param context
	 * @return
	 */
	/**
	 * 判断应用是否安装
	 * 
	 * @param context
	 * @param pkgname
	 * @return
	 */

	private static boolean appIsInstall(Context context, String pkgname) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(pkgname, 0);
		} catch (Exception e) {

		}

		if (packageInfo != null) {
			return true;
		} else {

			return false;

		}

	}

	private static void openApp(Context mContext, String PackageName) {
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = new Intent();
		// 要启动引用的package名字
		intent = packageManager.getLaunchIntentForPackage(PackageName);
		if (intent != null) {
			mContext.startActivity(intent);
		}
	}

}
