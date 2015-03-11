package com.androidproficiency.utils;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.androidproficiency.R;
import com.androidproficiency.constants.DialogConstants;

public class DialogUtils extends DialogFragment{

	private int id = -1;

	private String mDialogtitle;

	private String mDialogMessage;
	
	/** AlertDialog shown to User Provided with DailogMessages **/ 
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		mDialogtitle = getArguments().getString(getString(R.string.title));

		mDialogMessage = getArguments().getString(getString(R.string.message));

		id = getArguments().getInt(getString(R.string.dialogid));
		
		switch (id) {
		
		case DialogConstants.NO_NETWORK_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(mDialogtitle);
			builder.setMessage(mDialogMessage)
			.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dismiss();
				}
			});
			return builder.create();
			
		default:
			return null;
		}
	}
}
