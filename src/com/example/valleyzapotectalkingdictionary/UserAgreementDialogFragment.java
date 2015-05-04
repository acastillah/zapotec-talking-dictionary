package com.example.valleyzapotectalkingdictionary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class UserAgreementDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_agreement_dialog, null);
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Terms of Use");
        builder.setView(dialogView);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
								SharedPreferences preferences = getActivity()
										.getSharedPreferences(Preferences.APP_SETTINGS,
												Activity.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putBoolean(Preferences.TERMS_ACCEPTED, true);
								editor.commit();
                   }
               })
               .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // exit the application
                	   getActivity().finish();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
