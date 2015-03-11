package com.androidproficiency.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.androidproficiency.AndroidProficiency;
import com.androidproficiency.R;
import com.androidproficiency.constants.ParserConstants;
import com.androidproficiency.model.CountryDetails;
import com.androidproficiency.model.CountryInfo;

/** Parser class for get data from JSON **/
public class ParserUtils {

	private static ParserUtils parserUtils = null;

	public static ParserUtils getInstance(Context appContext) {
		if (parserUtils != null) {
			return parserUtils;
		}
		parserUtils = new ParserUtils();
		return parserUtils;
	}


	/**Get Necessary data about Countrydetails from JSON String 
	 * @throws JSONException **/
	public CountryInfo getCountryDetails(String jsonString) throws JSONException {

		CountryInfo resultSet = new CountryInfo();

		if (jsonString == null && !TextUtils.isEmpty(jsonString)) {

			return null;
		}

		JSONObject root = new JSONObject(jsonString);

		resultSet.title = root.getString(ParserConstants.TITLE);

		JSONArray countryDetailsArray = root.getJSONArray(ParserConstants.COUNTRY_DETAILS);

		for(int i = 0; i < countryDetailsArray.length(); i++){
			JSONObject countryDetailsObj = countryDetailsArray.getJSONObject(i);
			CountryDetails countryDetails = new CountryDetails();
			if(!countryDetailsObj.getString(ParserConstants.TITLE).equalsIgnoreCase(AndroidProficiency.getAppContext().getString(R.string.null_value)))
				countryDetails.title = countryDetailsObj.getString(ParserConstants.TITLE);
			if(!countryDetailsObj.getString(ParserConstants.DESCRIPTION).equalsIgnoreCase(AndroidProficiency.getAppContext().getString(R.string.null_value)))
				countryDetails.description = countryDetailsObj.getString(ParserConstants.DESCRIPTION);
			if(!countryDetailsObj.getString(ParserConstants.IMAGE_DETAILS).equalsIgnoreCase(AndroidProficiency.getAppContext().getString(R.string.null_value)))
				countryDetails.imageHref = countryDetailsObj.getString(ParserConstants.IMAGE_DETAILS);
			resultSet.countryDetails.add(countryDetails); 
		}
		return resultSet;
	}
}
