package com.androidproficiency.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;

import android.content.Context;

import com.androidproficiency.AndroidProficiency;
import com.androidproficiency.constants.AppConstants;
import com.androidproficiency.model.CountryInfo;

/** Class used to Define the interaction with the server **/
public class ServerUtils {

	private static ServerUtils serverUtils = null;

	public static ServerUtils getInstance(Context appContext) {
		if (serverUtils != null) {
			return serverUtils;
		}
		serverUtils = new ServerUtils();
		return serverUtils;
	}

	/** Makes HttpURLConnection and returns Details of URL 
	 * @throws IOException 
	 * @throws JSONException **/
	public CountryInfo countryInfo(String urlString) throws IOException, JSONException{
		CountryInfo countrInfo = new CountryInfo();
		String countryInfo = null;

		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		httpConnection.setRequestMethod(AppConstants.HTTP_GET);
		httpConnection.setConnectTimeout(AppConstants.CONNECTION_TIMEOUT);
		httpConnection.connect();

		boolean redirect = false;

		int status = httpConnection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		if (redirect) {

			// get redirect url from "location" header field
			String newUrl = httpConnection.getHeaderField(AppConstants.LOCATION);

			// get the cookie if need, for login
			String cookies = httpConnection.getHeaderField(AppConstants.SETCOOKIE);

			// open the new connnection again
			httpConnection = (HttpURLConnection) new URL(newUrl).openConnection();
			httpConnection.setRequestProperty(AppConstants.COOKIE, cookies);
		}

		countryInfo = retrieveInputStream(httpConnection.getInputStream());

		countrInfo = ParserUtils.getInstance(AndroidProficiency.getAppContext()).getCountryDetails(countryInfo);
		return countrInfo;
	}

	/** Reading InputStream from Connection Established**/
	private String retrieveInputStream(InputStream is) throws IOException {
		if (is != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is), (8 * 1024));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			return sb.toString();
		}
		return "";
	}
}
