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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UserAgreementDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_agreement_dialog, null);
    	
    	Button englishButton = (Button) dialogView.findViewById(R.id.userAgreement_English_Button);
    	Button spanishButton = (Button) dialogView.findViewById(R.id.userAgreement_Spanish_Button);
    	TextView englishText = (TextView) dialogView.findViewById(R.id.userAgreement_English);
    	TextView spanishText = (TextView) dialogView.findViewById(R.id.userAgreement_Spanish);
    	
		englishButton.setOnClickListener(new UserAgreementLanguageButtonOnClickListener(englishText, spanishText, englishButton, spanishButton));
    	spanishButton.setOnClickListener(new UserAgreementLanguageButtonOnClickListener(spanishText, englishText, spanishButton, englishButton));
    	
    	// show English by default
    	englishButton.setEnabled(false);
    	spanishText.setVisibility(View.GONE);
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.termsOfUse);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
								SharedPreferences preferences = getActivity()
										.getSharedPreferences(Preferences.APP_SETTINGS,
												Activity.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putBoolean(Preferences.TERMS_ACCEPTED, true);
								editor.commit();
                   }
               })
               .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // exit the application
                	   getActivity().finish();
                   }
               });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    public class UserAgreementLanguageButtonOnClickListener implements OnClickListener {

    	TextView show = null;
    	TextView hide = null;
    	Button enable = null;
    	Button disable = null;
    	
    	public UserAgreementLanguageButtonOnClickListener(TextView show, TextView hide, Button disable, Button enable) {
    		this.show = show;
    		this.hide = hide;
    		this.disable = disable;
    		this.enable = enable;
    	}
    	
		@Override
		public void onClick(View arg0) {
			show.setVisibility(View.VISIBLE);
			hide.setVisibility(View.GONE);
			enable.setEnabled(true);
			disable.setEnabled(false);
		}
    	
    }
}
