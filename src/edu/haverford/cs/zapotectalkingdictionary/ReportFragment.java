package edu.haverford.cs.zapotectalkingdictionary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ReportFragment extends Fragment {

	public static final String REPORT_EMAIL = "ZapotecTalkingDictionary@gmail.com";
	Button reportButton = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_report, container, false);
		
		reportButton = (Button) view.findViewById(R.id.report_button);
		reportButton.setOnClickListener(new ReportButtonOnClickListener());
		
		return view;
	}
	
	public class ReportButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", REPORT_EMAIL, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.reportEmail_subject));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.reportEmail_body));
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
		}
		
	}
}
