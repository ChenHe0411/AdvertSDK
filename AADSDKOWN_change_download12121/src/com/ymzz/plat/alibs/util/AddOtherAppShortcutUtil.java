package com.ymzz.plat.alibs.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.feilu.download.StorageUtils;
import com.feilu.utilmy.RecommendItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class AddOtherAppShortcutUtil {
	
	/**
	 * 下载完成打开
	 * @param context
	 * @param baseItem
	 */
	public static void openFile(Context context,RecommendItem baseItem) {
		File file = new File(StorageUtils.FILE_ROOT + "/" + baseItem.PName
				 + ".apk");
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
		
	}
	/**
	 * 根据apk路径打开
	 * @param apkpath
	 * @param mContext
	 */
	 public static void installApk(String apkpath, Context mContext) {
	 File file = new File(apkpath);
	 Intent intent = new Intent();
	 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 intent.setAction(Intent.ACTION_VIEW);
	 String type = "application/vnd.android.package-archive";
	 intent.setDataAndType(Uri.fromFile(file), type);
	 mContext.startActivity(intent);
	 SettingUtil.show_more_log(mContext,3,"0");
	 }

	public static  void addShortCut(Context context,String appName){
		
		
		if (appName!=null) {
			String apkPath=StorageUtils.FILE_ROOT+appName+".apk";
			String iconPath=StorageUtils.FILE_ROOT+"icon/"+appName+".png";
			
			if (apkPath!=null) {
//				AddOtherAppShortcutUtil.addShortcut_actual(context, apkPath, iconPath, appName);
//				System.out.println("ffffff");
				

			}
			
		}
		
		
	}
	static private void addShortcut_actual(Context context, String apkPath,
			String iconPath, String apkName) {

		try {
			
		
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		
		Bitmap my_iconBitmap = iconBitmap(iconPath, context, apkPath);
		// 快捷方式的名�?
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, apkName);
		shortcut.putExtra("duplicate", false); // 不允许重复创�?
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(apkPath);
		if (file.exists() && file.isAbsolute()) {
			String type = "application/vnd.android.package-archive";
			intent.setDataAndType(Uri.fromFile(file), type);
		}
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

	

		if (my_iconBitmap != null) {
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, my_iconBitmap);

		} else {

			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(
							context,
							context.getResources().getIdentifier("ic_launcher",
									"drawable", context.getPackageName()));
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		}
		context.sendBroadcast(shortcut);
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/***
	 * =====================================
	 */
	public static void delShortcut(Context context, String apkName,
			String apkpath) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, apkName);

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File file = new File(apkpath);
		if (file.exists() && file.isAbsolute()) {
			String type = "application/vnd.android.package-archive";
			intent.setDataAndType(Uri.fromFile(file), type);
		}
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		context.sendBroadcast(shortcut);

	}

	private static Bitmap iconBitmap(String iconPath, Context mContext,
			String apkPath) {
		Bitmap my_mBitmap = null;
		if (new File(iconPath).exists()) {
			// my_mBitmap =
			// BitmapFactory.decodeFile(iconPath,getBitmapOption(2));
			my_mBitmap = BitmapFactory.decodeFile(iconPath);

		}
		if (my_mBitmap == null) {
			my_mBitmap = apkInfo(mContext, apkPath);
		}
		if (my_mBitmap == null) {
			my_mBitmap = showUninstallAPKIcon(mContext, apkPath);
		}
		
		return my_mBitmap;
	}

	/***
	 * =====================================
	 */

	public static Bitmap showUninstallAPKIcon(Context mContext, String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";

		Bitmap mBitmap = null;
		try {
			// apk包的文件路径
			// 这是�?��Package 解释�? 是隐藏的
			// 构�?函数的参数只有一�? apk文件的路�?
			// PackageParser packageParser = new PackageParser(apkPath);
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			Class<?>[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			Object pkgParser = pkgParserCt.newInstance(valueArgs);
			// 这个是与显示有关�? 里面涉及到一些像素显示等�? 我们使用默认的情�?
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			typeArgs = new Class[4];
			typeArgs[0] = File.class;
			typeArgs[1] = String.class;
			typeArgs[2] = DisplayMetrics.class;
			typeArgs[3] = Integer.TYPE;
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);
			valueArgs = new Object[4];
			valueArgs[0] = new File(apkPath);
			valueArgs[1] = apkPath;
			valueArgs[2] = metrics;
			valueArgs[3] = 0;
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);
			// 应用程序信息�? 这个公开�? 不过有些函数, 变量没公�?
			// ApplicationInfo info = mPkgInfo.applicationInfo;
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			Constructor<?> assetMagCt = assetMagCls
					.getConstructor((Class[]) null);
			Object assetMag = assetMagCt.newInstance((Object[]) null);
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			Resources res = mContext.getResources();
			typeArgs = new Class[3];
			typeArgs[0] = assetMag.getClass();
			typeArgs[1] = res.getDisplayMetrics().getClass();
			typeArgs[2] = res.getConfiguration().getClass();

			Constructor<?> resCt = Resources.class.getConstructor(typeArgs);
			valueArgs = new Object[3];
			valueArgs[0] = assetMag;
			valueArgs[1] = res.getDisplayMetrics();
			valueArgs[2] = res.getConfiguration();
			res = (Resources) resCt.newInstance(valueArgs);

			// if (info.labelRes != 0) {
			// // 把名字存起来 好在广播这边去取�?
			// name = res.getText(info.labelRes).toString();
			// Log.e("===========", "==========" + name);
			// SharedPreferences sp =
			// mContext.getSharedPreferences("PackageName",
			// Context.MODE_PRIVATE);
			// sp.edit().putString("name", name).commit();
			//
			// }

			// 这里就是读取�?��apk程序的图�?
			if (info.icon != 0) {
				Drawable icon = res.getDrawable(info.icon);
				BitmapDrawable bd = (BitmapDrawable) icon;
				mBitmap = bd.getBitmap();
				// ImageView image = (ImageView) findViewById(R.id.imag);
				// image.setVisibility(View.VISIBLE);
				// image.setImageDrawable(icon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mBitmap;
	}
	public static Bitmap apkInfo(Context context,String absPath) {  
		Bitmap bitmap=null;
	    PackageManager pm = context.getPackageManager();  
	    PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES);  
	    if (pkgInfo != null) {  
	        ApplicationInfo appInfo = pkgInfo.applicationInfo;  
	        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */  
	        appInfo.sourceDir = absPath;  
	        appInfo.publicSourceDir = absPath;  
	        String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名  
	        String packageName = appInfo.packageName; // 得到包名  
	        String version = pkgInfo.versionName; // 得到版本信息  
	        /* icon1和icon2其实是一样的 */  
	        Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息  
	        Drawable icon2 = appInfo.loadIcon(pm);  
	        String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);  
	        Log.i("TAG", String.format("PkgInfo: %s", pkgInfoStr));  
	        
	        bitmap= drawableToBitmap(context,icon1);
	        
	        
	    }  
	    return bitmap;
	} 
	public static Bitmap drawableToBitmap(Context context,Drawable  drawable) {
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
	/***=========================================================**/
	
//	public static String packageNameS="packageNameS";
//	public static String packageNameKey="packageNameKey";
//	
//	public static void storeData(Context context, String packageNameSS) {
//		SharedPreferences timepref = context.getSharedPreferences(
//				packageNameS, Activity.MODE_PRIVATE);
//		SharedPreferences.Editor timeditor = timepref.edit();
//
//		timeditor.putString(packageNameKey, packageNameSS);
//		timeditor.commit();
//	}
//
//	public static String showStoreData(Context context) {
//		SharedPreferences timepref = context.getSharedPreferences(
//				packageNameS, Activity.MODE_PRIVATE);
//		String fullScreen = timepref.getString(packageNameKey, "");
//		return fullScreen;
//	}
/***=========================================================**/
	
	public static String appNameS="appNameS";
	public static String appNameKey="appNameKey";
	
	public static void storeAppNameData(Context context, String packageNameSS) {
		SharedPreferences timepref = context.getSharedPreferences(
				appNameS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();
	
		timeditor.putString(appNameKey, packageNameSS);
		timeditor.commit();
		
	
	}

	public static String showAppNameStoreData(Context context) {
		SharedPreferences timepref = context.getSharedPreferences(
				appNameS, Activity.MODE_PRIVATE);
		String fullScreen = timepref.getString(appNameKey, "");
		return fullScreen;
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

}
