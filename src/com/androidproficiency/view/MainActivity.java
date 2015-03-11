package com.androidproficiency.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONException;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidproficiency.R;
import com.androidproficiency.constants.DialogConstants;
import com.androidproficiency.model.CountryDetails;
import com.androidproficiency.model.CountryInfo;
import com.androidproficiency.utils.DialogUtils;
import com.androidproficiency.utils.ImageLoader;
import com.androidproficiency.utils.NetworkConnectionUtils;
import com.androidproficiency.utils.ServerUtils;

public class MainActivity extends Activity {

	private CountryDetailsCall  countryDetailsCall;

	private ProgressBar mProgress;

	private CountryInfo countryInfo = new CountryInfo();

	private ArrayList<CountryDetails> countryDetails = new ArrayList<CountryDetails>();

	private CountryListAdapter countryListAdapter;

	private ListView countryList;

	public ImageLoader imageLoader; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.country_info);

		getActionBar().setTitle(getString(R.string.app_name));

		mProgress = (ProgressBar) findViewById(R.id.progressList);

		countryList = (ListView) findViewById(R.id.countyList);

		imageLoader=new ImageLoader(this);
	}


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		UpdateView();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			countryDetailsCall.cancel(true);
			UpdateView();
			break;

		default:
			break;
		}
		return true;
	}

	/** View updation with data provided from server **/ 
	private void UpdateView(){

		if (!NetworkConnectionUtils.getInstance(getApplicationContext()).isNetworkAvailable(this)) {
			showDialog(DialogConstants.NO_NETWORK_DIALOG, getString(R.string.app_name),
					getString(R.string.network_unavailable));
		}else{
			countryDetailsCall = new CountryDetailsCall();
			countryDetailsCall.execute(getString(R.string.country_info));
		}

		countryListAdapter = new CountryListAdapter();
		countryList.setAdapter(countryListAdapter);
	}

	/** Download Data about CountryDetails from provided url using AsyncTask **/
	private class CountryDetailsCall extends AsyncTask<String, ArrayList<?>, Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... arg0) {

			try {
				countryInfo = ServerUtils.getInstance(getApplicationContext()).countryInfo(arg0[0]);
				if(countryInfo.countryDetails.size() > 0){
					publishProgress(countryInfo.countryDetails);
				}
			} catch (IOException e) {
				Logger.getLogger(e.getMessage());
				return null;
			} catch (JSONException e) {
				Logger.getLogger(e.getMessage());
				return null;
			}

			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onProgressUpdate(ArrayList<?>... values) {
			super.onProgressUpdate(values);
			if(values != null){
				getActionBar().setTitle(countryInfo.title);
				countryDetails = (ArrayList<CountryDetails>) values[0];
				countryListAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgress.setVisibility(View.GONE);
		}
	}


	public class CountryListAdapter extends BaseAdapter {

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.country_support_list, null);

				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.title);
				holder.details = (TextView)convertView.findViewById(R.id.details);
				holder.detailsImage = (ImageView)convertView.findViewById(R.id.content_image);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder)convertView.getTag();
			}

			if (countryDetails.get(position).title != null) {
				holder.title.setText(countryDetails.get(position).title);
			}else{
				holder.title.setText("");
			}

			if(countryDetails.get(position).description != null){
				holder.details.setText(Html.fromHtml(countryDetails.get(position).description));
			}else{
				holder.details.setText("");
			}

			if(countryDetails.get(position).imageHref != null){
				imageLoader.DisplayImage(countryDetails.get(position).imageHref, holder.detailsImage);
			}else{
				imageLoader.DisplayImage(countryDetails.get(position).imageHref, holder.detailsImage);
			}

			return (convertView);
		}

		@Override
		public int getCount() {
			if (countryDetails != null)
				return countryDetails.size();
			return 0;
		}

		@Override
		public CountryDetails getItem(int position) {
			return countryDetails.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	static class ViewHolder {
		TextView title, details;
		ImageView detailsImage;
	}

	/**Dialog instance created for the Show dialog fragment**/
	private void showDialog(int id, String title, String message){

		FragmentManager fragmentManager = getFragmentManager();
		DialogUtils confirmationDialog = new DialogUtils();
		Bundle bundle = new Bundle();
		bundle.putString(getString(R.string.message), message);
		bundle.putString(getString(R.string.title), title);
		bundle.putInt(getString(R.string.dialogid), id);
		confirmationDialog.setArguments(bundle);
		confirmationDialog.show(fragmentManager, null);
	}

}
