package com.ymzz.plat.alibs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import com.ymzz.plat.alibs.ad.ADSDK;
import com.ymzz.plat.alibs.ad.BehindDownLoadService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownUtils {
	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
	public static final int MB_2_BYTE = 1024 * 1024;
	public static final int KB_2_BYTE = 1024;
	static final int DOWN_PROGRESS = 1, DOWN_COMPLETE = 2, DOWN_ERROR = 3;
	static final int SDCARD_NO = 4;
	private Timer timer;
	static public int msgargs1;
	static public int msgargs2;

	public static final String DOWNLOAD_FOLDER_NAME = "Trina";
	private File path;
	private ThreadPoolExecutor executor;

	private List<String> urls = new ArrayList<String>();
	static public String url = ADSDK.updateUrl;
	private static String DOWNLOAD_FILE_NAME = "game.apk";

	private TextView download_precent, download_size;
	private ProgressBar downProgressBar;
	private AlertDialog dialog;
	private Activity activity;

	/**
	 * 2015.0909 后期如果给首页Activity里显示，记得把失败和下载完时候，Activity的finish去掉
	 * 
	 * @param activity
	 */
	public  DownUtils(Activity activity) {
		this.activity = activity;
	}

	public void showDialog() {
		path = new File(Environment.getExternalStorageDirectory(),
				DOWNLOAD_FOLDER_NAME);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

		dialog = new AlertDialog.Builder(activity).show();
		dialog.setCancelable(false);

		Window window = dialog.getWindow();
		window.setContentView(UtilRong.getR(activity, ".R$layout",
				"dialog_layout"));
		ImageView titleImg = (ImageView) window.findViewById(UtilRong.getR(
				activity, ".R$id", "title_img"));
		titleImg.setImageResource(android.R.drawable.ic_dialog_info);

		final TextView contentTv = (TextView) window.findViewById(UtilRong
				.getR(activity, ".R$id", "content_tv"));

		downProgressBar = (ProgressBar) window.findViewById(UtilRong.getR(
				activity, ".R$id", "load_progress_bar"));

		download_precent = (TextView) window.findViewById(UtilRong.getR(
				activity, ".R$id", "current_tv"));

		download_size = (TextView) window.findViewById(UtilRong.getR(activity,
				".R$id", "percent_tv"));

		final View line = (View) window.findViewById(UtilRong.getR(activity,
				".R$id", "bottom_line"));

		final TextView confirmTv = (TextView) window.findViewById(UtilRong
				.getR(activity, ".R$id", "confirm_dialog_but"));

		TextView cancelTv = (TextView) window.findViewById(UtilRong.getR(
				activity, ".R$id", "cancel_dialog_but"));

		ADSDK.content = ADSDK.content.trim();
		if (ADSDK.content != null && ADSDK.content.length() > 4) {
			contentTv.setText(ADSDK.content);
		}
		if (ADSDK.noticetype == 3) {
			cancelTv.setVisibility(View.VISIBLE);
			cancelTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();

				}
			});
		} else {
			cancelTv.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
		confirmTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				line.setVisibility(View.GONE);
				confirmTv.setClickable(false);
				if (ADSDK.mode == 1) {
					Intent intent = new Intent(activity,
							BehindDownLoadService.class);
					intent.putExtra("url", ADSDK.updateUrl);
					intent.putExtra("type", "update");

					intent.putExtra(
							"name",
							DownUtils.getAppName(activity) != null ? DownUtils
									.getAppName(activity) : "下载中");
					activity.startService(intent);
					Toast.makeText(ADSDK.activity, "开始更新...", Toast.LENGTH_LONG)
							.show();
					dialog.cancel();
				} else {
					downProgressBar.setVisibility(View.VISIBLE);
					download_precent.setVisibility(View.VISIBLE);
					download_size.setVisibility(View.VISIBLE);
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (timer == null) {
								timer = new Timer();
							}
							try {
								if (!urls.contains(url)) {
									urls.add(url);

									executor.submit(new Down(
											url,
											DownUtils.getAppName(activity) != null ? DownUtils
													.getAppName(activity)
													: DOWNLOAD_FILE_NAME));
									confirmTv.setText("正在下载...");
								}
							} catch (Exception e) {
							}
						}
					});
				}
			}
		});
	}

	class Downhandler extends Handler {
		WeakReference<Down> reference;

		Downhandler(Down d) {
			reference = new WeakReference<Down>(d);
		}

		@Override
		public void handleMessage(Message msg) {
			Down d = reference.get();
			if (d != null) {
				d.handleMessage(msg);
			}
		}
	}

	class Down implements Runnable {
		String urlStr;
		Downhandler downhandler = new Downhandler(this);
		int count = 0, contentLength;
		File apkFile;

		Down(String url, String name) {
			this.urlStr = url;
			if (!path.exists()) {
				path.mkdirs();
			}
			apkFile = new File(path, name);
			Log.e("", "下载中" + apkFile.toString());
			if (apkFile.exists()) {
				apkFile.delete();
			}
		}

		public void handleMessage(Message msg) {
			msgargs1 = msg.arg1;
			msgargs2 = msg.arg2;
			switch (msg.what) {
			case DOWN_PROGRESS:
				if (msgargs2 < 0) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							downProgressBar.setIndeterminate(true);
						}
					});

				} else {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							downProgressBar.setIndeterminate(false);
							downProgressBar.setMax(msgargs2);
							downProgressBar.setProgress(msgargs1);
							download_precent.setText(DownUtils.getNotiPercent(
									msgargs1, msgargs2));
							download_size.setText(DownUtils
									.getAppSize(msgargs1)
									+ "/"
									+ DownUtils.getAppSize(msgargs2));
						}
					});

				}
				break;
			case DOWN_ERROR:
				urls.remove(urlStr);
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				Toast.makeText(activity, "下载失败", Toast.LENGTH_LONG).show();
				if (dialog != null && dialog.isShowing())
					dialog.cancel();
				activity.finish();
				break;
			case DOWN_COMPLETE:
				urls.remove(urlStr);
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				DownUtils.openFile(activity, apkFile);
				if (dialog != null && dialog.isShowing())
					dialog.cancel();
				activity.finish();
				break;
			case SDCARD_NO:
				break;
			}
		}

		private void updateData() {
			if (timer == null) {
				timer = new Timer();
			}
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = DOWN_PROGRESS;
					message.arg1 = count;
					message.arg2 = contentLength;
					downhandler.sendMessage(message);
				}
			}, 1, 500);
		}

		@Override
		public void run() {
			try {
				contentLength = DownUtils.getContentLength(urlStr);
				if (contentLength < 0) {
					downhandler.sendEmptyMessage(DOWN_ERROR);
					return;
				}
				InputStream stream = DownUtils.getStreamFromNetwork(urlStr);
				if (stream == null) {
					downhandler.sendEmptyMessage(DOWN_ERROR);
					return;
				}
				byte[] bs = new byte[12 * 1024];
				FileOutputStream outputStream = new FileOutputStream(apkFile);
				int length = -1;
				count = 0;
				int downloadCount = 0;
				while ((length = stream.read(bs)) != -1) {
					outputStream.write(bs, 0, length);
					count += length;
					int tmp = (int) (count * 100 / contentLength);
					if (downloadCount == 0 || tmp - 3 > downloadCount) {
						downloadCount += 3;
						updateData();
					}
				}
				outputStream.close();
				stream.close();
				updateData();
				downhandler.sendEmptyMessage(DOWN_COMPLETE);
			} catch (Exception e1) {
				downhandler.sendEmptyMessage(DOWN_ERROR);
			}
		}
	}

	public static void openFile(Context context, File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		/* 调用getMIMEType()来取得MimeType */
		String type = "application/vnd.android.package-archive";
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);
	}

	public static InputStream getStreamFromNetwork(String imageUri) {
		try {
			HttpURLConnection conn = createConnection(imageUri);
			InputStream imageStream;
			imageStream = conn.getInputStream();
			return imageStream;
		} catch (IOException e) {
		} finally {

		}
		return null;
	}

	public static HttpURLConnection createConnection(String path)
			throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		return conn;
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

	public static String getNotiPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress > max) {
			rate = 100;
		} else {
			rate = (int) ((double) progress / max * 100);
		}
		return new StringBuilder(16).append(rate).append("%").toString();
	}

	public static CharSequence getAppSize(long size) {
		if (size <= 0) {
			return "0M";
		}
		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE))
					.append("K");
		} else {
			return size + "B";
		}
	}

	public static int getContentLength(String url)
			throws MalformedURLException, IOException {
		int totalSize;
		HttpURLConnection conn;
		if (url.contains("?")) {
			conn = (HttpURLConnection) new URL(url + "&do=getfilesize")
					.openConnection();
		} else {
			conn = (HttpURLConnection) new URL(url).openConnection();
		}
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(30 * 1000);
		conn.connect();
		totalSize = conn.getContentLength();
		conn.disconnect();
		return totalSize;
	}

}