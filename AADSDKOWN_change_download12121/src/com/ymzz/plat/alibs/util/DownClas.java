package com.ymzz.plat.alibs.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.feilu.download.AllDownList;
import com.feilu.download.DownloadService;
import com.feilu.download.MyIntents;
import com.feilu.utilmy.Contant;
import com.feilu.utilmy.RecommendItem;
import com.ymzz.plat.alibs.ad.PopupService;
import com.ymzz.plat.alibs.util.AddOtherAppShortcutUtil;
import com.ymzz.plat.alibs.util.UtilRong;

public class DownClas {

	private static AllDownList allDownList=AllDownList.getInstance();
	static List<RecommendItem> downloadinglist = new ArrayList<RecommendItem>();
	
	
	public static void getDownload(Context context){
		

		if (allDownList==null) {
		allDownList = AllDownList.getInstance();
		}
		
		if (UtilRong.isNetConnected(context)) {
			downloadinglist.clear();
			
			List<RecommendItem> downing=allDownList.downloadingItems;

			if (downing!=null&&downing.size()>0) {
				System.out.println("---woaini--"+downing.size());
				downloadinglist.addAll(downing);
				
				for (int i = 0; i < downloadinglist.size(); i++) {
					
					downloadApp(context, downloadinglist.get(i));
					
					
				}
				
				
			}
			
			
	}
		
		
	}
	
	public void openApp(Context mContext, String PackageName) {
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = new Intent();
		// 要启动引用的package名字
		intent = packageManager.getLaunchIntentForPackage(PackageName);
		if (intent != null) {
			mContext.startActivity(intent);
		}
	}

	public static String processData(int i, String[] value) {
		
		String key="";
		if (value != null && value.length > 0) {

			if (value.length >= PopupService.adurls.length) {
				key = value[i].replace("\"", "").replace("\\", "");
			} else {

				if (i < value.length) {
					key = value[i].replace("\"", "").replace("\\", "");
				} else {
					key = "";
				}
			}
		} else {

			key = "";
		}
		
		return key;
	}

	/**
	 * 判断应用是否安装
	 * 
	 * @param context
	 * @param pkgname
	 * @return
	 */
	public static boolean appIsInstall(Context context, String pkgname) {
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
	
	
public static void downloadApp(Context context,RecommendItem resolve){
	if (allDownList==null) {
		allDownList = AllDownList.getInstance();
		}
		
		//System.out.println("进来下载了"+resolve.status);
					switch (resolve.status) {
					case MyIntents.Types.ADD: {
						Intent downloadIntent = new Intent(context,
								DownloadService.class);
						downloadIntent.setAction(Contant.SERVICE_ACTION);
						downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
						downloadIntent.putExtra("resolve", resolve);
						context.startService(downloadIntent);
					}
						break;
					case MyIntents.Types.PROCESS: {
						// Intent downloadIntent = new Intent(instance,
						// DownloadService.class);
						// downloadIntent.setAction(Contant.SERVICE_ACTION);
						// downloadIntent.putExtra(MyIntents.TYPE,
						// MyIntents.Types.PAUSE);
						// downloadIntent.putExtra("resolve", resolve);
						// instance.startService(downloadIntent);

					}
						break;
					case MyIntents.Types.ERROR: {
						allDownList.start(resolve);
						Intent downloadIntent = new Intent(context,
								DownloadService.class);
						downloadIntent.setAction(Contant.SERVICE_ACTION);
						downloadIntent.putExtra(MyIntents.TYPE,
								MyIntents.Types.CONTINUE);
						downloadIntent.putExtra("resolve", resolve);
						context.startService(downloadIntent);

					}
						break;
					case MyIntents.Types.PAUSE: {
						allDownList.start(resolve);
						Intent downloadIntent = new Intent(context,
								DownloadService.class);
						downloadIntent.setAction(Contant.SERVICE_ACTION);
						downloadIntent.putExtra(MyIntents.TYPE,
								MyIntents.Types.CONTINUE);
						downloadIntent.putExtra("resolve", resolve);
						context.startService(downloadIntent);
					}
						break;
					case MyIntents.Types.DEFAULT: {
						resolve.status = MyIntents.Types.ADD;
						if (allDownList.downloadingItems.contains(resolve)) {
							Toast.makeText(context, "已经下载", Toast.LENGTH_SHORT).show();
						} else {
							allDownList.start(resolve);
							Intent downloadIntent = new Intent(context,
									DownloadService.class);
							downloadIntent.setAction(Contant.SERVICE_ACTION);
							downloadIntent
									.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
							downloadIntent.putExtra("resolve", resolve);
							context.startService(downloadIntent);

						}

						break;
					}
					case MyIntents.Types.COMPLETE: {
						AddOtherAppShortcutUtil.openFile(context,resolve);
					}
						break;
					case MyIntents.Types.OPEN: {
//						openApp(instance, resolve.PackageName);
					}
						break;
					case MyIntents.Types.DELETE:
						Intent downloadIntent = new Intent(context,
								DownloadService.class);
						downloadIntent.setAction(Contant.SERVICE_ACTION);
						downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
						downloadIntent.putExtra("resolve", resolve);
						context.startService(downloadIntent);
						break;
					}
				
			}
}
