package com.ymzz.plat.alibs.ad;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ymzz.plat.alibs.util.AddOtherAppShortcutUtil;
import com.ymzz.plat.alibs.util.DownClas;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.feilu.download.AllDownList;
import com.feilu.download.DownloadService;
import com.feilu.download.MyIntents;
import com.feilu.download.StorageUtils;
import com.feilu.utilmy.RecommendItem;
import com.feilu.utilmy.Contant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ShowMoreActivity extends Activity {
	public static Activity instance;
	private WebView webview;
	public static List<RecommendItem> items = new ArrayList<RecommendItem>();
	private AllDownList allDownList;

	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (ADSDK.vertical != 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		instance = this;
		this.allDownList = AllDownList.getInstance();

		/*
		 * sdkDraw = new SDKDraw(this); sdkDraw.setKeepScreenOn(true);
		 * setContentView(sdkDraw);
		 */
		webview = new WebView(this);

		webview.getSettings().setJavaScriptEnabled(true);
		// webview.getBackground().setAlpha(0);

		webview.setBackgroundColor(0);

		webview.addJavascriptInterface(this, "AndroidDo");
		webview.loadUrl("file:///android_asset/3guAD/admore.html");

		setFinishOnTouchOutside(false);
		setContentView(webview);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/**
		 * 在这里把得到的数据转成 list,（就直接用原来的实体，有值则赋值，没有值的赋值 "",）
		 * 在底下点击不同的index，把对应的实体传给service下载，把pid用本应用中的url赋值， 原来市场中的pid之前的值改为空字符串；
		 */

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (items != null) {
			items.clear();

			List<RecommendItem> list = getListData();
			if (list != null && list.size() > 0) {
				items.addAll(list);
			}
		}
//		int size = items.size();
//		System.out.println("size----" + size);
		if (items != null && items.size() > 0) {

			for (int i = 0; i < items.size(); i++) {
				RecommendItem recommendItem = items.get(i);
				if (recommendItem.status == MyIntents.Types.PROCESS) {

					xiaZai(recommendItem);
				}
			}
		}else{
			
			finish();
		}

	}

	@JavascriptInterface
	public void close() {
		PopupService.isBusy = false;
		instance.finish();
	}

	@JavascriptInterface
	public void cancel() {
		if (PopupService.isDebug)
			Log.v("xgAD", "cancel");
		close();
	}

	@Override
	public void onBackPressed() {
		close();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PopupService.isBusy = false;
	}

	@JavascriptInterface
	public String getNames() {
		return Arrays.toString(PopupService.adnames);
	}

	@JavascriptInterface
	public String getSrcs() {
		return Arrays.toString(PopupService.adsrcs);
	}

	@JavascriptInterface
	public String getUrls() {
		return Arrays.toString(PopupService.adurls);
	}

	@JavascriptInterface
	public String getIds() {
		return Arrays.toString(PopupService.adids);
	}

	@JavascriptInterface
	public String getVertical() {
		return ADSDK.vertical + "";
	}

	@JavascriptInterface
	public String getADTitle() {
		return PopupService.adtitle;
	}

	
	@JavascriptInterface
	public void installAll() {
		for (int i = 0; i < PopupService.adids.length; i++) {
			install(i);
		}
	}

	@JavascriptInterface
	public void install(int index) {
		
		System.out.println("index--"+index);
		if (PopupService.clicktypes[index].equals("0")) {
			if (index < items.size()) {

				if (items != null && items.size() > 0) {
					downloadApp(items.get(index));
					
					
					
				}

			}
	
			
		} else {
			Uri uri = Uri.parse(PopupService.adurls[index].replace("\"", "")
					.replace("\\", ""));
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
		}

		SettingUtil.show_more_log(ShowMoreActivity.this, 2,
				PopupService.adids[index]);
		close();
	}

	/*** ===================================================== **/
	private void xiaZai(RecommendItem baseItem) {
		allDownList.start(baseItem);
		Intent downloadIntent = new Intent(instance, DownloadService.class);
		downloadIntent.setAction(Contant.SERVICE_ACTION);
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.CONTINUE);
		downloadIntent.putExtra("resolve", baseItem);
		instance.startService(downloadIntent);

	}

	public void downloadApp(RecommendItem resolve){
	
//System.out.println("进来下载了"+resolve.status);
			switch (resolve.status) {
			case MyIntents.Types.ADD: {
				Intent downloadIntent = new Intent(instance,
						DownloadService.class);
				downloadIntent.setAction(Contant.SERVICE_ACTION);
				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
				downloadIntent.putExtra("resolve", resolve);
				instance.startService(downloadIntent);
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
				Intent downloadIntent = new Intent(instance,
						DownloadService.class);
				downloadIntent.setAction(Contant.SERVICE_ACTION);
				downloadIntent.putExtra(MyIntents.TYPE,
						MyIntents.Types.CONTINUE);
				downloadIntent.putExtra("resolve", resolve);
				instance.startService(downloadIntent);

			}
				break;
			case MyIntents.Types.PAUSE: {
				allDownList.start(resolve);
				Intent downloadIntent = new Intent(instance,
						DownloadService.class);
				downloadIntent.setAction(Contant.SERVICE_ACTION);
				downloadIntent.putExtra(MyIntents.TYPE,
						MyIntents.Types.CONTINUE);
				downloadIntent.putExtra("resolve", resolve);
				instance.startService(downloadIntent);
			}
				break;
			case MyIntents.Types.DEFAULT: {
				resolve.status = MyIntents.Types.ADD;
				if (allDownList.downloadingItems.contains(resolve)) {
					Toast.makeText(instance, "已经下载", Toast.LENGTH_SHORT).show();
				} else {
					allDownList.start(resolve);
					Intent downloadIntent = new Intent(instance,
							DownloadService.class);
					downloadIntent.setAction(Contant.SERVICE_ACTION);
					downloadIntent
							.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
					downloadIntent.putExtra("resolve", resolve);
					instance.startService(downloadIntent);

				}

				break;
			}
			case MyIntents.Types.COMPLETE: {
				AddOtherAppShortcutUtil.openFile(ShowMoreActivity.this,resolve);
			}
				break;
			case MyIntents.Types.OPEN: {
//				openApp(instance, resolve.PackageName);
			}
				break;
			case MyIntents.Types.DELETE:
				Intent downloadIntent = new Intent(instance,
						DownloadService.class);
				downloadIntent.setAction(Contant.SERVICE_ACTION);
				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
				downloadIntent.putExtra("resolve", resolve);
				instance.startService(downloadIntent);
				break;
			}
		
	}
	
	
	/**
	 *  misson.setId(PopupService.adids[index].replace("\"", "").replace("\\", ""));
			 misson.setName(PopupService.adnames[index].replace("\"", "").replace("\\", ""));
			 misson.setIcon(PopupService.adsrcs[index].replace("\"", "").replace("\\", ""));
			 misson.setUrl(PopupService.adurls[index].replace("\"", "").replace("\\", ""));
	 * @return
	 */

	public List<RecommendItem> getListData() {
		AllDownList allDownList = AllDownList.getInstance();
		List<RecommendItem> list = new ArrayList<RecommendItem>();

		try {

			if (PopupService.adurls != null && PopupService.adurls.length > 0) {

				for (int i = 0; i < PopupService.adurls.length; i++) {

					RecommendItem hi = new RecommendItem();
					hi.Pid = DownClas.processData(i, PopupService.adurls);

					hi.Img = DownClas.processData(i, PopupService.adsrcs);


					hi.PName = DownClas.processData(i, PopupService.adnames);
					hi.PackageName = DownClas.processData(i, PopupService.pkgname);
					hi.ItemName = DownClas.processData(i, PopupService.adnames);
					hi.ItemId = DownClas.processData(i, PopupService.adids);
					hi.XingJi = PopupService.showmore_type;
					hi.Downcount = "";

					

					hi.FileSize = "";
					hi.Version = "";
					hi.VersionCode = "";
				
					hi.Description = "";

					for (int j = 0; j < allDownList.downloadingItems.size(); j++) {
						RecommendItem rein = allDownList.downloadingItems
								.get(j);
						if (rein.Pid.equals(hi.Pid)) {
							hi = null;
							hi = rein;
						}
					}
					if (hi.PackageName != null
							&& (!"".equals(hi.PackageName))) {
					if (DownClas.appIsInstall(ShowMoreActivity.this, hi.PackageName)) {
						hi.status = MyIntents.Types.OPEN;
					}
					}

					list.add(hi);

				}
			}
		} catch (Exception e) {
		}
		return list;
	}



	

}
