package com.androidproficiency;

import android.app.Application;
import android.content.Context;

public class AndroidProficiency extends Application {

	private static Context sAppContext;

	@Override
	public void onCreate() {

		sAppContext = getApplicationContext();
		super.onCreate();

	}
	
	/** Returns Context of Application **/
	public static Context getAppContext() {

		return sAppContext;
	}

	@Override
	public void onTerminate() {

		super.onTerminate();
	}

	@Override
	public void onLowMemory() {

		super.onLowMemory();
	}

}
