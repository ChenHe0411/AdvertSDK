package com.ymzz.plat.alibs.ad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.feilu.download.AllDownList;
import com.feilu.download.MyIntents;
import com.feilu.download.StorageUtils;
import com.feilu.utilmy.RecommendItem;
import com.guad.sdk.R;
import com.ymzz.plat.alibs.util.DownClas;
import com.ymzz.plat.alibs.util.GetResourseIdSelf;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.zwu.DownloadTask_zw;
import com.ymzz.plat.alibs.zwu.FileUtil;

@SuppressLint("NewApi")
public class ShowADActivity extends Activity {
	public static Activity instance;
	private WebView webview;
	private DownloadTask_zw doDownload;

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (ADSDK.vertical != 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		}

		instance = this;

		Intent intent = getIntent();
		String loadingmode = intent.getStringExtra("loadingmode");
		String src = intent.getStringExtra("src");
		String srctype = intent.getStringExtra("srctype");
		clicktype = intent.getStringExtra("clicktype");
		initView();

		if ("1".equals(loadingmode)) {
			loadPage();
		} else {

			if (src != null && (!"".equals(src))) {
				webview.loadUrl(src);
			}

		}

	
	}

	/**
	 * 初始化视图
	 */
	// private void initView() {
	// // 实例化WebView并设置相关属性
	// webview = new WebView(this);
	// webview.getSettings().setJavaScriptEnabled(true);
	// webview.setBackgroundColor(0);
	// webview.addJavascriptInterface(new JavaScriptInterface(),
	// "AndroidDownH5");
	// webview.setWebChromeClient(new WebChromeClient());
	//
	// }
	private RelativeLayout relativeLayout;

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		// 实例化WebView并设置相关属性

		relativeLayout = new RelativeLayout(instance);
		webview = new WebView(this);

		// 设置RelativeLayout的相关属性
		RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		relativeLayout.setLayoutParams(param1);
		relativeLayout.setBackgroundColor(Color.BLACK);
		
		
		Button btn = new Button(this);
		LayoutParams param2 = new LayoutParams(60,60);
		btn.setLayoutParams(param2);

		btn.setBackgroundResource(GetResourseIdSelf.getResourseIdByName(
				ShowADActivity.this.getPackageName(), "drawable", "close1"));
		// 设置webview的属性
	
		RelativeLayout.LayoutParams param3=new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);;
		
		if (ADSDK.picadwh != null && ADSDK.picadwh.length > 1) {

			int picwidth=Integer.parseInt(ADSDK.picadwh[0]);
			int picheight=Integer.parseInt(ADSDK.picadwh[1]);
		
			if (picwidth!=0&&picheight!=0) {
			param3 = new RelativeLayout.LayoutParams(
					picwidth, picheight);
			}
		}
		
		
		param3.addRule(RelativeLayout.CENTER_IN_PARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setBackgroundColor(0);
		webview.setLayoutParams(param3);
		webview.addJavascriptInterface(new JavaScriptInterface(),
				"AndroidDownH5");
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		webview.addView(btn);
		relativeLayout.addView(webview);
		setContentView(relativeLayout);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ADSDK.isBusy = false;
				finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		ADSDK.isBusy = false;
	}

	/**
	 * 加载页面
	 */
	private void loadPage() {
		if (webview != null) {
			webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
		}

		if (new File(FileUtil.FILE_ROOT + DownloadTask_zw.md5 + ".html")
				.exists()) {

			webview.loadUrl("file://" + FileUtil.FILE_ROOT
					+ DownloadTask_zw.md5 + ".html");
		}

		setFinishOnTouchOutside(false);
	}

	/**
	 * JavaScript调用Activity接口
	 * 
	 * @author Woody_PC
	 * 
	 */
	final class JavaScriptInterface {

		public JavaScriptInterface() {
		}

		@JavascriptInterface
		public void downloadH5App(int adlibid, String adUrl, String iconUrl,
				String pName, String packageName) {

			if (!iconUrl.startsWith("http")) {

				File dirFile = new File(StorageUtils.FILE_ROOT);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				dirFile = new File(StorageUtils.FILE_ROOT + "icon/");
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				// System.out.println(pName+"----"+iconUrl);

				if (new File(iconUrl).exists()) {
					FileUtil.copyFile(iconUrl, StorageUtils.FILE_ROOT + "icon/"
							+ pName + ".png");
				} else {
					// System.out.println("no");
				}

			} else {

				SettingUtil.downloadAdIcon(ShowADActivity.this, iconUrl, pName);
			}
			List<RecommendItem> list = getListData(adlibid, adUrl, iconUrl, pName,
					packageName);

			if (list != null && list.size() > 0) {
				ad_list.clear();
				ad_list.addAll(list);
			}

			if (ad_list != null && ad_list.size() > 0) {
				if (Integer.parseInt(clicktype) == 1) {

					Uri uri = Uri.parse(ad_list.get(0).Pid);
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					ShowADActivity.this.startActivity(it);
				} else {
					Toast.makeText(ShowADActivity.this, "正在下载...",
							Toast.LENGTH_SHORT).show();
					DownClas.downloadApp(ShowADActivity.this, ad_list.get(0));
				}

				if (ad_list.get(0).ItemId != null) {
					try {
						SettingUtil.show_H5page_log(ShowADActivity.this, 2,
								Integer.parseInt(ad_list.get(0).ItemId));
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			} else {
				Uri uri = Uri.parse(adUrl);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				ShowADActivity.this.startActivity(it);
				SettingUtil.show_H5page_log(ShowADActivity.this, 2, adlibid);

			}

		}
	}

	/*******/
	public static List<RecommendItem> ad_list = new ArrayList<RecommendItem>();
	private String clicktype;

	private List<RecommendItem> getListData(int adid, String adUrl,
			String iconUrl, String pName, String packageName) {
		AllDownList allDownList = AllDownList.getInstance();
		List<RecommendItem> list = new ArrayList<RecommendItem>();

		try {
			if (adid != 0 && (adUrl != null) && (!"".equals(adUrl))) {

				RecommendItem hi = new RecommendItem();
				hi.Pid = adUrl;

				if (iconUrl != null && (!"".equals(iconUrl))) {
					hi.Img = iconUrl;
				}
				if (pName != null && (!"".equals(pName))) {
					hi.PName = pName;
					hi.ItemName = pName;
				} else {
					hi.PName = "dayUp";
					hi.ItemName = "dayUp";

				}
				if (packageName != null && (!"".equals(packageName))) {
					hi.PackageName = packageName;
				}

				hi.ItemId = adid + "";
				hi.XingJi = PopupService.h5ad_type;

				// if (flag==1) {
				// hi.clicktype=ADSDK.clicktype+"";
				// hi.downloadPicPath=SettingUtil.downloadPicPath;
				// }
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

					if (DownClas.appIsInstall(instance, hi.PackageName)) {
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
