package com.ymzz.plat.alibs.ad;



import com.ymzz.plat.alibs.util.DownClas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
	
	

	@Override
	public void onReceive(Context context, Intent intent) {

	
		
		
		
			Intent serviceIntent = new Intent(context, PopupService.class);
			context.startService(serviceIntent);
			
			Intent download_intent=new Intent(context, DownReceiverService.class);
			
			context.startService(download_intent);
			
			DownClas.getDownload(context);

	}
	
	
	
}