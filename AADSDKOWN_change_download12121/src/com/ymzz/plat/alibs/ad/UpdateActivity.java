package com.ymzz.plat.alibs.ad;

import android.app.Activity;
import android.os.Bundle;
import com.ymzz.plat.alibs.util.DownUtils;

public class UpdateActivity extends Activity {

	private DownUtils downUtils;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		downUtils = new DownUtils(this);
		downUtils.showDialog();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}