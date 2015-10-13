package com.ymzz.plat.alibs.ad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.ymzz.plat.alibs.util.DownloadMission;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.util.UtilRong;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * 独立于应用，在应用关闭页面的时候，服务未死的情况下，推送应用
 * @author feilu-pc
 *
 */
public class PopupService extends Service {
	
	public static final String ad_type="0";
	
	public static final String showmore_type="1";
	public static final String h5ad_type="2";
	/**=========================**/
	public static int sendicon_showmore=0;
	
	public static int nextGet=3600;
	public static int timer = 0;
//	public static Context context;
	public static PopupService main;
	public static String adtitle;
	public static String[] adtollgate;
	
	public static String[] adevent_showmore;
	public static int showtimes=1;//开屏安装弹出次数
	
	public static String[] adids;
	public static String[] adnames;
	public static String[] adurls;
	public static String[] adsrcs;
	public static String[] pkgname;
	
	
	public static String[] clicktypes;
	public static  boolean isDebug = true;
	public static  boolean isBusy = false;
	public static  boolean isHid = false;
	public PopupService() {
		main = this;
	}
	
    @Override
    public IBinder onBind(Intent intent) {

		return null;
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate(); 
//        context = this;
//        if(isDebug)
//        Log.v("xgAD","Service is Created");
//        if(ADSDK.gameStatus == 0){
//        		ADSDK.init(context);
//        }
     
   }  
 
    
   
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
//    	if(isDebug)
//        Log.v("xgAD","Service is Started");
//    	if(ADSDK.gameStatus == 0){
//    		ADSDK.init(context);
//    	}
    	return START_STICKY;
    }

    @Override  
    public void onDestroy() {  
        super.onDestroy();  
    
    } 
    


    
}
