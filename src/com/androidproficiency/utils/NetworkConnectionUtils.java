package com.androidproficiency.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidproficiency.AndroidProficiency;
import com.androidproficiency.R;

/** NetworkConnection class used to check network availability**/  
public class NetworkConnectionUtils {

	private static NetworkConnectionUtils sNetworkConnectionUtils = null;
	
	private ConnectivityManager mConnectivityManager;
	
	private boolean isNetworkAvailable;
	
	public String mNetworkType;

	private NetworkConnectionUtils(Context mContext) {
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static NetworkConnectionUtils getInstance(Context mContext) {
		if (sNetworkConnectionUtils == null) {
			sNetworkConnectionUtils = new NetworkConnectionUtils(mContext);
		}
		return sNetworkConnectionUtils;
	}

	/** Checks Network status for application **/
	private boolean checkNetworkStatus() {
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		isNetworkAvailable = networkInfo == null ? false : true;
		return isNetworkAvailable;
	}
	
	/** Checks Network type available for application **/
	public String getNetworkType() {
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		mNetworkType = networkInfo == null ? AndroidProficiency.getAppContext().getString(R.string.offline) : networkInfo.getTypeName();
		if (mNetworkType == null) {
			
			return AndroidProficiency.getAppContext().getString(R.string.offline);
		}
		return mNetworkType;
	}

	/** Returns whether Network is Available or not **/
	public boolean isNetworkAvailable(Context mContext) {

		return NetworkConnectionUtils.getInstance(mContext).checkNetworkStatus();
	}

}
