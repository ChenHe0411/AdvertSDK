package com.ymzz.plat.alibs.util;

import android.content.Context;

import com.feilu.download.AllDownList;
import com.feilu.download.MyIntents;
import com.feilu.utilmy.RecommendItem;
import com.ymzz.plat.alibs.ad.ADSDK;
import com.ymzz.plat.alibs.ad.PopupService;

import java.util.ArrayList;
import java.util.List;

public class GetPicList {

	

	public static List<RecommendItem> getListData(Context context,int flag) {
		AllDownList allDownList = AllDownList.getInstance();
		List<RecommendItem> list = new ArrayList<RecommendItem>();

		try {
			if (ADSDK.adid != 0 && (ADSDK.adurl != null)
					&& (!"".equals(ADSDK.adurl))) {

				RecommendItem hi = new RecommendItem();
				 hi.Pid = ADSDK.adurl;
			

				if (ADSDK.icon != null && (!"".equals(ADSDK.icon))) {
					hi.Img = ADSDK.icon;
				}
				if (ADSDK.pname != null && (!"".equals(ADSDK.pname))) {
					hi.PName = ADSDK.pname;
					hi.ItemName = ADSDK.pname;
				} else {
					hi.PName = "daydayUp";
					hi.ItemName = "daydayUp";

				}
				if (ADSDK.packagename != null
						&& (!"".equals(ADSDK.packagename))) {
					hi.PackageName = ADSDK.packagename;
				}

				hi.ItemId = ADSDK.adid + "";
				hi.XingJi = PopupService.ad_type;
				
				if (flag==1) {
					hi.clicktype=ADSDK.clicktype+"";
					hi.downloadPicPath=SettingUtil.downloadPicPath;
				}
				hi.Downcount = "";

				hi.FileSize = "";
				hi.Version = "";
				hi.VersionCode = "";

				hi.Description = "";

				for (int j = 0; j < allDownList.downloadingItems.size(); j++) {
					RecommendItem rein = allDownList.downloadingItems.get(j);
					if (rein.Pid.equals(hi.Pid)) {
						hi = null;
						hi = rein;
					}
				}

				if (hi.PackageName != null && (!"".equals(hi.PackageName))) {

					if (DownClas.appIsInstall(context, hi.PackageName)) {
						hi.status = MyIntents.Types.OPEN;
					}
				}

				list.add(hi);

			}
		} catch (Exception e) {
		}
		return list;
	}

}
