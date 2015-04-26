package com.example.valleyzapotectalkingdictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AboutFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_about, container, false);

        ImageView[] logos = {
    		(ImageView) view.findViewById(R.id.haverfordLogo),
            (ImageView) view.findViewById(R.id.livingtonguesLogo),
            (ImageView) view.findViewById(R.id.ngLogo),
            (ImageView) view.findViewById(R.id.swarthmoreLogo),
            (ImageView) view.findViewById(R.id.alfredoharpLogo),
            (ImageView) view.findViewById(R.id.juanLogo)
        };
        
        int resID = view.getResources().getIdentifier("haverford_logo.gif", "drawable", "my.app.package.name");
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), resID);
        
        Bitmap[] bitmaps = {
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.haverford_logo)),
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.livingtongueslogo_2012)),
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.nglogo_2012)),
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.swarthmorelogo_2012)),
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.alfredo_harp_helu_logo)),
        		BitmapFactory.decodeStream(view.getResources().openRawResource(R.raw.juan_de_cordova_logo))
        };
        
//        Log.i("LOGOS", "about to set image");
//        for (int i=0; i<6; i++)
//        	logos[i].setImageBitmap(bitmaps[i]);
        
        for (int i=0; i<6; i++)
        	Log.i("LOGOS", i + " width=" + logos[i].getWidth() + " height=" + logos[i].getHeight());
        
        
        
        return view;
	}
	
}
