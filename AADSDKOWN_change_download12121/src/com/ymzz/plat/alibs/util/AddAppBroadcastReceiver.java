package com.ymzz.plat.alibs.util;

import java.util.List;

import com.feilu.download.StorageUtils;
import com.feilu.utilmy.RecommendItem;
import com.ymzz.plat.alibs.ad.ADSDK;
import com.ymzz.plat.alibs.ad.ShowADActivity;
import com.ymzz.plat.alibs.ad.ShowMoreActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;

public class AddAppBroadcastReceiver extends BroadcastReceiver {
	private final String ADD_APP = "android.intent.action.PACKAGE_ADDED";
	private final String REMOVE_APP = "android.intent.action.PACKAGE_REMOVED";

	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();

		if (ADD_APP.equals(action)) {
			String packageName = intent.getDataString();
			//
			//
			// System.out.println("安装了------" + packageName);

			try {

				appNameAdded(mContext, intent);

				upload(mContext, intent);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		if (REMOVE_APP.equals(action)) {

		}

		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getDataString();
			// System.out.println("替换了:" + packageName);

			try {

				appNameAdded(mContext, intent);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private void appNameAdded(Context mContext, Intent intent) {
		String packageName2 = intent.getDataString().substring(8);

		System.out.println("截取了-----" + packageName2);

		String appName_add = AddOtherAppShortcutUtil.getAppNameFromPackageName(
				mContext, packageName2);
		if (appName_add != null && !"".equals(appName_add)) {

			if (AddOtherAppShortcutUtil.showAppNameStoreData(mContext) != null
					&& AddOtherAppShortcutUtil.showAppNameStoreData(mContext)
							.contains(appName_add)) {
				String apkPath = StorageUtils.FILE_ROOT + "/" + appName_add
						+ ".apk";

				AddOtherAppShortcutUtil.delShortcut(mContext, appName_add,
						apkPath);

			}

		}
	}

	/**
	 * 统计
	 */
	public void upload(Context mContext, Intent intent) {

		String packageName2 = intent.getDataString().substring(8);

		String appName_add = AddOtherAppShortcutUtil.getAppNameFromPackageName(
				mContext, packageName2);

		if (appName_add != null && !"".equals(appName_add)) {

			if (AddOtherAppShortcutUtil.showAppNameStoreData(mContext) != null
					&& AddOtherAppShortcutUtil.showAppNameStoreData(mContext)
							.contains(appName_add)) {
				boolean flag1 = (ShowMoreActivity.items != null && ShowMoreActivity.items
						.size() > 0);
				boolean flag2 = (ShowADActivity.ad_list != null && ShowADActivity.ad_list
						.size() > 0);
				boolean flag3 = (ADSDK.ad_list != null && ADSDK.ad_list
						.size() > 0);

				if ((!flag1) && (!flag2)&&(!flag3)) {
					SettingUtil.show_more_log(mContext, 4, "0");
				} else {

					if (flag1) {
						for (int i = 0; i < ShowMoreActivity.items.size(); i++) {
							RecommendItem item = ShowMoreActivity.items.get(i);
							if (item.PName != null) {

								if (appName_add.equals(item.PName)) {

									if (item.ItemId != null) {
										SettingUtil.show_more_log(mContext, 4,
												item.ItemId);
									}

								}

							}

						}

					}
					
					if (flag2) {
						for (int i = 0; i < ShowADActivity.ad_list.size(); i++) {
							RecommendItem item = ShowADActivity.ad_list.get(i);
							if (item.PName != null) {

								if (appName_add.equals(item.PName)) {
									
									if (item.ItemId!=null) {
										SettingUtil.show_more_log(mContext, 4,
												item.ItemId);
									}

									

								}

							}

						}

					}
					if (flag3) {
						for (int i = 0; i < ADSDK.ad_list.size(); i++) {
							RecommendItem item = ADSDK.ad_list.get(i);
							if (item.PName != null) {
								
								if (appName_add.equals(item.PName)) {
									
									if (item.ItemId!=null) {
										SettingUtil.show_more_log(mContext, 4,
												item.ItemId);
									}
									
									
									
								}
								
							}
							
						}
						
					}

				}
			}

		}

	}
	// public void upload(Context mContext, Intent intent) {
	//
	// String packageName2 = intent.getDataString().substring(8);
	//
	// String appName_add = AddOtherAppShortcutUtil.getAppNameFromPackageName(
	// mContext, packageName2);
	//
	// if (appName_add != null && !"".equals(appName_add)) {
	//
	// if (AddOtherAppShortcutUtil.showAppNameStoreData(mContext) != null
	// && AddOtherAppShortcutUtil.showAppNameStoreData(mContext)
	// .contains(appName_add)) {
	// if ((ShowMoreActivity.items != null
	// && ShowMoreActivity.items.size() > 0)||) {
	// for (int i = 0; i < ShowMoreActivity.items.size(); i++) {
	// RecommendItem item = ShowMoreActivity.items.get(i);
	// if (item.PName != null) {
	//
	// if (appName_add.equals(item.PName)) {
	//
	// if (item.ItemId!=null) {
	// SettingUtil.show_more_log(mContext, 4,
	// item.ItemId);
	// }
	//
	//
	//
	// }else{
	// SettingUtil.show_more_log(mContext, 4,
	// "0");
	// }
	//
	// }else{
	//
	// SettingUtil.show_more_log(mContext, 4,
	// "0");
	// }
	//
	// }
	//
	// }
	//
	// else{
	// SettingUtil.show_more_log(mContext, 4,
	// "0");
	//
	// }
	//
	// }
	//
	// }
	//
	// }

	// public void openApp(Context mContext, String PackageName) {
	// PackageManager packageManager = mContext.getPackageManager();
	// Intent intent = new Intent();
	// // 要启动引用的package名字
	// intent = packageManager.getLaunchIntentForPackage(PackageName);
	// if (intent != null) {
	// mContext.startActivity(intent);
	// }
	// }

}
