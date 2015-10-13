package com.ymzz.plat.alibs.util;

import java.io.File;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;

public class AddCurrentAppShutcut {

	/**
	 * 本应用的快捷
	 * 
	 * http://blog.csdn.net/liu149339750/article/details/8306419
	 */
	private static final String xmlName = "gamefirstfile";
	private static final String MapName = "gameisfirst";

	@SuppressWarnings("static-access")
	public static void firstTimeVisit(Context context) {

		SharedPreferences preferences = context.getSharedPreferences(xmlName,
				context.MODE_PRIVATE);
		boolean isFirst = preferences.getBoolean(MapName, true);
		if (isFirst) {
			 createDeskShortCut(context);
			
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(MapName, false);
		editor.commit();

	}

	public static String appName = "";

	/** 创建快捷方式 * */
	public static void createDeskShortCut(Context context) {
//		System.out.println("--aaaaa--"+appName);
		// 创建快捷方式的Intent
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建 ，如果重复的话就会有多个快捷方式了
		shortcutIntent.putExtra("duplicate", false);
		// 这个就是应用程序图标下面的名称

		/** ============================================================ **/
//
//		System.out.println("--1111--" + getAppName(context));
//		System.out.println("--2222--" + getAppName2(context));

		appName = getAppName(context);

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				appName != null ? appName : "");
//		 shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "aa");
		// shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
		// R.string.app_name);
		/** ============================================================ **/

		// 快捷图片

		Bitmap my_iconBitmap = drawableToBitmap(context);
		if (my_iconBitmap != null) {
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, my_iconBitmap);
		}

		/** ============================================================ **/

		Intent launcherIntent = getLauncherAct(context);
		if (launcherIntent != null) {
			// 点击快捷图片，运行的程序主入口
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
					launcherIntent);
		}

		context.sendBroadcast(shortcutIntent);
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

	public static String getAppName2(Context context) {

		String appName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			appName = info.applicationInfo.loadLabel(pm).toString();

		} catch (NameNotFoundException e) {
			appName = "";

		}
		return appName;
	}

	/***
	 * 获得icon的名字
	 * 
	 * @param context
	 * @param packname
	 * @return
	 */
	public static Drawable getIcon(Context context) {

		Drawable icon = null;
		try {

			PackageManager pm = context.getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(
					context.getPackageName(), 0);

			icon = info.loadIcon(pm);

		} catch (NameNotFoundException e) {
			icon = null;

		}
		return icon;
	}

	public static Bitmap drawableToBitmap(Context context) {
		Drawable drawable = getIcon(context);
		Bitmap bitmap = null;
		if (drawable != null) {

			bitmap = Bitmap
					.createBitmap(

							drawable.getIntrinsicWidth(),

							drawable.getIntrinsicHeight(),

							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

									: Bitmap.Config.RGB_565);

			Canvas canvas = new Canvas(bitmap);

			// canvas.setBitmap(bitmap);

			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			drawable.draw(canvas);
		}
		return bitmap;

	}

	public static Intent getLauncherAct(Context context) {

		// String LauncherClassName=null;
		Intent intent = null;
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			PackageManager pm = context.getPackageManager();

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;
				intent = new Intent();
				// intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return intent;

	}
	// private void openApp(String packageName) {
	// PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);
	//
	// Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
	// resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// resolveIntent.setPackage(pi.packageName);
	//
	// List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
	//
	// ResolveInfo ri = apps.iterator().next();
	// if (ri != null ) {
	// String packageName = ri.activityInfo.packageName;
	// String className = ri.activityInfo.name;
	//
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_LAUNCHER);
	//
	// ComponentName cn = new ComponentName(packageName, className);
	//
	// intent.setComponent(cn);
	// startActivity(intent);
	// }
	// }

}
